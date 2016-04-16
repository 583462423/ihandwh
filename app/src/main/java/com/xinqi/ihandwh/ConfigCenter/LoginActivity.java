package com.xinqi.ihandwh.ConfigCenter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.xinqi.ihandwh.HomeActivity;
import com.xinqi.ihandwh.HttpService.OrderSeatService.OrderSeatService;
import com.xinqi.ihandwh.Local_Utils.UserinfoUtils;
import com.xinqi.ihandwh.OrderSeats.SelectSeatActivity;
import com.xinqi.ihandwh.R;

//import com.xinqi.ihandwh.R;

/**
 * Created by syd on 2015/11/15.
 */
public class LoginActivity extends AppCompatActivity {
    android.support.v7.app.ActionBar actionBar;
    EditText etid,etps;
    TextView errortv;
    String id,ps;
    Handler handler;
    private Button mbtnlogin;
    //获取从哪里启动,0代表个人中心点击登陆，-1代表预约未登录跳转，默认为0
    int from=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = getSupportActionBar();
        setContentView(R.layout.activity_login);
        actionBar.setTitle("用户登录");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.psdback1);
        from = getIntent().getIntExtra("from", 0);
        etid = (EditText) findViewById(R.id.etid);
        etps = (EditText) findViewById(R.id.etps);
        errortv = (TextView) findViewById(R.id.errortv);
        mbtnlogin= (Button) findViewById(R.id.btn_login);
        etid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(etid.getText()) && !TextUtils.isEmpty(etps.getText())) {
                    errortv.setText("");
                }

            }
        });
        etps.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(etid.getText()) && !TextUtils.isEmpty(etps.getText())) {
                    errortv.setText("");
                }
            }
        });
        mbtnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = etid.getText().toString();
                ps = etps.getText().toString();
                if (TextUtils.isEmpty(etid.getText()) || TextUtils.isEmpty(etps.getText())) {
                    errortv.setText("请输入账号及密码");
                } else {
                    mbtnlogin.setEnabled(false);
                    login(id, ps, from);
                    Log.i("bac", "id:" + id + "ps:" + ps);
                }
            }
        });
        PushAgent.getInstance(this).onAppStart();
    }
    public void login(final String id, final String password, final int from){
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.obj.toString().contains("fail")) {
                    Toast.makeText(LoginActivity.this, "用户名或密码错误！", Toast.LENGTH_SHORT).show();
                    mbtnlogin.setEnabled(true);
                } else if(msg.obj.toString().contains("success")) {
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    //更新登录信息
                    UserinfoUtils userinfoUtils = new UserinfoUtils(LoginActivity.this);
                    userinfoUtils.refresh_Login_Status(true);
                    userinfoUtils.save_CureentLogin_Info(id,password);
                    if (from == -1) {
                       //startActivity(new Intent(LoginActivity.this, SelectSeatActivity.class));
                        finish();
                    } /*else if (from == 0) {
                        Intent intent=new Intent(new Intent(LoginActivity.this,HomeActivity.class));
                        intent.putExtra("pos",2);
//                            HomeActivity homeActivity = new HomeActivity();
//                            homeActivity.viewPager.setCurrentItem(2);
                      startActivity(intent);
                        finish();
                    }*/
//                    Intent resultintent=new Intent();
//                    resultintent.putExtra("back",1);
//                    setResult(1);
                    //Intent intent=new Intent(new Intent(LoginActivity.this,HomeActivity.class));
                    //intent.putExtra("pos",2);
//                            HomeActivity homeActivity = new HomeActivity();
//                            homeActivity.viewPager.setCurrentItem(2);
                    //startActivity(intent);
                    setResult(1);
                    finish();

//                    context.startActivity(new Intent(context, HomeActivity.class));
                }else {
                    Toast.makeText(LoginActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                    mbtnlogin.setEnabled(true);

                }
            }

        };
        //验证登陆信息是否符合,验证成功则发送handler消息
        new Thread(){
            @Override
            public void run() {
                Message message=new Message();
                message.what=123;
                //调用服务器验证信息接口
                try {
                    Log.i("bac","id"+id+"pa:"+ps);
                    boolean b= OrderSeatService.testUserInfoIsTrue(id, password);
                    Log.i("bac",b+"");
                    if (b){
                        message.obj="success";

                    }else {
                        message.obj="fail";
                    }


                } catch (Exception e) {
                    message.obj="error";
                    e.printStackTrace();

                }
                handler.sendMessage(message);
            }
        }.start();

    }
/*
    @Override
    public void onBackPressed() {
        if (from==0){//从个人中心来
            Intent intent=new Intent()

        }*/
//    }

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

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     * <p/>
     * <p>Derived classes should call through to the base class for it to
     * perform the default menu handling.</p>
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            setResult(1);
            //onBackPressed();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
