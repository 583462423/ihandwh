package com.xinqi.ihandwh;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.xinqi.ihandwh.Fragment.LoginFragment;
import com.xinqi.ihandwh.Fragment.NoticeFragment;
import com.xinqi.ihandwh.Fragment.RenewBookInfoFrag;
import com.xinqi.ihandwh.HttpService.CheckWork;
import com.xinqi.ihandwh.HttpService.OkHttpTools;
import com.xinqi.ihandwh.Model.BookInfo;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;

public class LibraryActivity extends AppCompatActivity implements View.OnClickListener {

    FloatingActionButton login;
    FloatingActionButton quit;
    FloatingActionsMenu menu;
    FloatingActionButton cancel;
    DialogFragment loginDialog;
    NoticeFragment noticeDialog;
    ProgressBar loading;
    LinearLayout menuLayout;
    Button renewBtn;
    CheckWork checkWork;
    TextView loginNumber;
    String number;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        checkWork = new CheckWork(this);
        checkWork.checkNetWork();

        Toolbar toolbar = (Toolbar) findViewById(R.id.library_toolbar);
        loading = (ProgressBar) findViewById(R.id.loading);
        menu = (FloatingActionsMenu) findViewById(R.id.main_menu);
        login = (FloatingActionButton) findViewById(R.id.login);
        quit = (FloatingActionButton) findViewById(R.id.quit);
        cancel = (FloatingActionButton) findViewById(R.id.cancel);
        menuLayout = (LinearLayout) findViewById(R.id.menu_layout);
        renewBtn = (Button) findViewById(R.id.renew);
        loginNumber = (TextView) findViewById(R.id.login_number);

        renewBtn.setOnClickListener(this);


        toolbar.setTitle("我的图书馆");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //TODO 如果没登录过，就进行判断，如果登录过，再次用该号登录失败的时候，就重新登录，如果返回值是密码错误，则重新登录


        //TODO 选择本地中存储的提示信息的变量看是否是不再提示
        SharedPreferences sharedPreferences = getSharedPreferences("isremember", Context.MODE_PRIVATE);
        if(!sharedPreferences.getBoolean("is_remember",false))
        {
            noticeDialog = new NoticeFragment();
            noticeDialog.show(getSupportFragmentManager(),"notice");
        }else{
            if(checkWork.getFlag())displayLogin();
        }

        cancel.setTitle("注销");
        cancel.setOnClickListener(this);
        login.setTitle("登录");
        login.setOnClickListener(this);
        quit.setTitle("退出");
        quit.setOnClickListener(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        this.finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                menu.collapse();
                if(checkWork.checkNetWork())
                    new LoginFragment().show(getSupportFragmentManager(), "login");
                break;
            case R.id.quit:
                finish();
                break;
            case R.id.cancel:
                //清空登录的数据，并且将图标恢复
                SharedPreferences sharedPreferences = getSharedPreferences("is_login",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                menuLayout.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
                login.setEnabled(true);
                login.setTitle("登录");
                new LoginFragment().show(getSupportFragmentManager(),"login");
                break;
            case R.id.renew:
                Intent intent = new Intent(LibraryActivity.this,BookInfoActivity.class);
                intent.putExtra("which", BookInfoActivity.RENEW);
                startActivity(intent);
            default:
                break;
        }
    }

    public void onConfirm()
    {
        displayLogin();
    }

    //显示登录界面
    public void displayLogin()
    {
        //在显示之前要选取本地数据，看是否之前已经登录了，如果登录了，就使用该功能后台登录一遍，如果没能登录成功，则重新登录
        SharedPreferences sharedPreferences = getSharedPreferences("is_login", Context.MODE_PRIVATE);
        if(getSharedPreferences("is_login", Context.MODE_PRIVATE).getBoolean("is_login", false))
        {
            //如果保存了密码则先使用该密码登录一遍试试
            OkHttpClient okHttpClient = new OkHttpClient();
            String number = sharedPreferences.getString("number",null);
            String passwd = sharedPreferences.getString("passwd",null);
            String cookie = sharedPreferences.getString("cookie",null);
            String captcha = sharedPreferences.getString("captcha",null);
            Call call = OkHttpTools.login(okHttpClient,number,passwd,captcha,cookie);
            new TestLogin().execute(call);
        }else
        {
            menuLayout.setVisibility(View.GONE);
            loginDialog = new LoginFragment();
            loginDialog.show(getSupportFragmentManager(), "login");
        }
    }

    //如果已经登录就更新fab显示的内容
    public void hasLogin()
    {
        number = getSharedPreferences("is_login",Context.MODE_PRIVATE).getString("number",null);
        loginNumber.setText(""+number);
        loading.setVisibility(View.GONE);
        cancel.setVisibility(View.VISIBLE);
        login.setEnabled(false);
        login.setTitle(getSharedPreferences("is_login",Context.MODE_PRIVATE).getString("number",null));
        menuLayout.setVisibility(View.VISIBLE);
        //设置欢迎界面
    }

    class TestLogin extends AsyncTask<Call,Void,Void>
    {
        String responce;
        @Override
        protected void onPreExecute() {
            loading.setVisibility(View.VISIBLE);
            menu.setEnabled(false);
            menuLayout.setVisibility(View.GONE);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Call... params) {
            try {
                if(checkWork.checkNetWork())
                {
                    responce = params[0].execute().body().string();
                }else
                {
                    responce = "";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            loading.setVisibility(View.GONE);
            menu.setEnabled(true);
            if(responce!=null && responce.contains("注销"))
            {
                //表示直接登录，直接开始显示菜单选项，待写
                hasLogin();
            }else
            {
                if(checkWork.checkNetWork())
                {
                    //没登录成功就显示login重新登录
                    loginDialog = new LoginFragment();
                    loginDialog.show(getSupportFragmentManager(),"login");
                }else
                    checkWork.setNetWork();

            }

            super.onPostExecute(aVoid);
        }
    }
}
