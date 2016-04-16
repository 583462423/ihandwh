package com.xinqi.ihandwh;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.RippleDrawable;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.message.UmengRegistrar;
import com.xinqi.ihandwh.Local_Utils.UserinfoUtils;
import com.xinqi.ihandwh.ui_components.SlidingTabsIconsFragment;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.update.UmengUpdateAgent;

import java.util.Date;

//import com.xinqi.ihandwh.R;

/**
 * s
 * Created by syd on 2015/11/12.
 */

public class HomeActivity extends AppCompatActivity {
    long pre_click_time;
    android.app.ActionBar actionBar;
    private SlidingTabsIconsFragment mSlidingTabsHost;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    WifiManager wifiManager;
    private FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

       // TODO 2016/3/27  低版本实现沉浸式状态栏
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        // checkNetworkState();
        sharedPreferences = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE);
        editor = sharedPreferences.edit();
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        //getActionBar().setTitle(getResources().getString(R.string.app_name));
        if (savedInstanceState != null)
            return;
        mSlidingTabsHost = new SlidingTabsIconsFragment();
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.slidingtab_fragment, mSlidingTabsHost);
        transaction.commit();
        //登录帐号统计
        UserinfoUtils usr = new UserinfoUtils(this);
        MobclickAgent.onProfileSignIn(usr.get_LastId());
        PushAgent.getInstance(this).onAppStart();


        // TODO 2016/3/29 修改actionbar实现方式为Toolbar
        Toolbar mToolBar = (Toolbar) findViewById(R.id.myToolBar);
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);

       /* ActionBar mActionBar = getActionBar();
        if (mActionBar == null)
            return;

        // TODO: 2016/3/27 修改自定义Actionbar在5.0版本以下出现最上边白线问题
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setCustomView(R.layout.action_bar_main);*/

        Log.d("Umeng Push Info", "Device Token:" + UmengRegistrar.getRegistrationId(this));
    }

    //实现按两次退出程序
    @Override
    public void onBackPressed() {
        long click_time = new Date().getTime();
        if (click_time - pre_click_time > 2000) {
            Toast.makeText(HomeActivity.this, "再按一次退出程序！", Toast.LENGTH_SHORT).show();
            //更新时间
            pre_click_time = click_time;
            return;
        }
        //BookSeatsContentPage.isfirstin=true;
//        Log.i("bac", "离开" + BookSeatsContentPage.isfirstin);
        editor.putBoolean("isfirstin", true);
        editor.commit();
        finish();
//        super.onBackPressed();
    }

    // TODO: 2016/2/27 修复无网络也加载座位信息 
    // TODO: 2016/2/27 删除搜索历史提醒，按钮效果 
    private boolean checkNetworkState() {
        boolean flag = false;
        // 得到网络连接信息
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // 去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null) {
            flag = manager.getActiveNetworkInfo().isAvailable();
        }
        if (!flag) {
            setNetwork();
        }

        return flag;
    }

    int nettype = 0;
    private void setNetwork() {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("网络不可用！");
        builder.setSingleChoiceItems(R.array.nettype, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Log.i("bac","singlechoice which:"+which);
                /**
                 * 1代表WLAN
                 * 2代表移动数据
                 * */
                nettype = which;

            }
        });
        builder.setPositiveButton("打开", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.i("bac", "open which:" + nettype);
                if (nettype == 0) {//打开WlAN
                    wifiManager.setWifiEnabled(true);
                } else {//打开移动数据
//                    toggleMobileData(HomeActivity.this,true);
//                    setMobileDataStatus(HomeActivity.this,true);
                    Intent intent = new Intent("android.settings.WIRELESS_SETTINGS");
                    startActivity(intent);
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // return;
                dialog.dismiss();
            }
        });
        builder.create();
        builder.show();
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
