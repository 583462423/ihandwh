package com.xinqi.ihandwh;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;

//import com.xinqi.ihandwh.R;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.message.PushAgent;
import com.umeng.update.UmengUpdateAgent;

/**
 * Created by syd on 2015/11/12.
 */
public class WelcomeActivity extends Activity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    //欢迎界面停留时间
    private static final int TIME_DELAY=2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        sharedPreferences=getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        editor=sharedPreferences.edit();
        editor.putBoolean("isfirstin", true);
        editor.commit();
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);


        //到时间后进入主页
        handler.sendEmptyMessageDelayed(0x123, TIME_DELAY);
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.updateOnlineConfig(this);
        AnalyticsConfig.enableEncrypt(true);
        PushAgent.getInstance(this).enable();
        FeedbackAgent agent=new FeedbackAgent(this);
        agent.sync();
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            UmengUpdateAgent.setUpdateOnlyWifi(false);
            UmengUpdateAgent.update(WelcomeActivity.this);
            //判断是否已经登陆，如果没有登陆，则进入登陆界面
            goHome();
        }


    };

    private void goHome() {
        Intent intent=new Intent(WelcomeActivity.this,HomeActivity.class);
        startActivity(intent);
        finish();//结束当前的activity
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
