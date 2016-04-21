package com.xinqi.ihandwh.Fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xinqi.ihandwh.HttpService.OkHttpTools;
import com.xinqi.ihandwh.LibraryActivity;
import com.xinqi.ihandwh.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.SocketHandler;

import okhttp3.Call;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends DialogFragment implements View.OnClickListener {

    OkHttpClient okHttpClient;
    private HashMap<HttpUrl,List<Cookie>> cookieStore = new HashMap<HttpUrl, List<Cookie>>();
    List<Cookie> cookies;
    Bitmap captchaBitmap; // 保存验证码的图片
    ImageView captchaView;
    ProgressBar captchaProBar,loginingProBar;
    EditText numberEdit,passwdEdit,captchaEdit;
    String cookie,number,passwd,captcha;
    Button loginBtn,quitBtn;
    Dialog dialog;
    TextView responceText;
    CheckBox isRemember;

    public final int LOGIN_PASSWD_WRONG = -1;
    public final int LOGIN_CAPTCHA_WRONG = -2;
    public final int LOGIN_SUCCESS = 1;
    public final int UNKNOW_WRONG = -3;
    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i("----------","onCreate");
        //先进行判断之前是否登录过

        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        okHttpClient = new OkHttpClient.Builder().cookieJar(new CookieJar() {
            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                cookieStore.put(url, cookies);
                Log.i("-----", "11111111111111111");
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                cookies = cookieStore.get(url);
                return cookies != null ? cookies : new ArrayList<Cookie>();
            }
        }).build();

        Log.i("----------","onCreateDialog");
        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View v = mInflater.inflate(R.layout.fragment_login,null);
        captchaProBar = (ProgressBar) v.findViewById(R.id.captchaProBar);
        loginingProBar = (ProgressBar) v.findViewById(R.id.loginingProBar);
        captchaView = (ImageView) v.findViewById(R.id.captchaImage);
        numberEdit = (EditText) v.findViewById(R.id.number);
        passwdEdit = (EditText) v.findViewById(R.id.passwd);
        captchaEdit = (EditText) v.findViewById(R.id.captcha);
        loginBtn = (Button) v.findViewById(R.id.login);
        quitBtn = (Button) v.findViewById(R.id.quit);
        responceText = (TextView) v.findViewById(R.id.responce_info);
        isRemember = (CheckBox) v.findViewById(R.id.is_remember);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        captchaProBar.setVisibility(View.VISIBLE);
        loginingProBar.setVisibility(View.GONE);
        loginBtn.setOnClickListener(this);
        quitBtn.setOnClickListener(this);
        if(getActivity().getSharedPreferences("is_login",Context.MODE_PRIVATE).getBoolean("is_remember",false))
        {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("is_login",Context.MODE_PRIVATE);
            ((EditText) v.findViewById(R.id.number)).setText(sharedPreferences.getString("number",null));
            ((EditText)v.findViewById(R.id.passwd)).setText(sharedPreferences.getString("passwd",null));
        }else
        {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("is_login",Context.MODE_PRIVATE);
            ((EditText) v.findViewById(R.id.number)).setText(sharedPreferences.getString("number",null));
        }

        new UpdateCaptcha().execute();

        captchaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captchaView.setVisibility(View.VISIBLE);
                new UpdateCaptcha().execute();
            }
        });
        dialog = builder.setTitle("登录").setView(v).create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }


    /**
     * 判断之前是否登录，只需要从本地获取数据
     */
    private boolean isLogin() {
        return getActivity().getSharedPreferences("is_login", Context.MODE_PRIVATE).getBoolean("is_login", false);
    }

    /**
     * 取得数据并且写入传入的参数中
     *
     * @param number
     * @param passwd
     * @param cookie
     */
    private void getInfo(String number, String passwd, String cookie) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("is_login", Context.MODE_PRIVATE);
        number = sharedPreferences.getString("number", null);
        passwd = sharedPreferences.getString("passwd", null);
        cookie = sharedPreferences.getString("cookie", null);
    }

    //向本地写数据
    private  void alterInfo(String number, String passwd, String cookie,String captcha, boolean is_login, boolean is_remember)
    {
        getActivity().getSharedPreferences("is_login",Context.MODE_PRIVATE).edit()
            .putString("number",number)
            .putString("passwd",passwd)
            .putString("cookie",cookie)
                .putString("captcha",captcha)
            .putBoolean("is_login",is_login)
            .putBoolean("is_remember",is_remember)
            .commit();
    }

    //点击登录后开始登录函数
    private void loging()
    {
        number = numberEdit.getText().toString();
        passwd = passwdEdit.getText().toString();
        captcha = captchaEdit.getText().toString();

        //登录之前开始判断帐号密码验证码cookie是否是空
        if(isEmpty())
        {
            responceText.setVisibility(View.VISIBLE);
            responceText.setText("请填入有效数据");
        }else
        {
            //新的线程开始登录,并在该线程中将信息保存
            new StartLogin().execute();
        }
    }

    private boolean isEmpty()
    {
        if(number.isEmpty() || passwd.isEmpty() || captcha.isEmpty()
                ||"".equals(number) || "".equals(passwd)||"".equals(captcha))return true;
        return false;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.login:
                loging();
                break;
            case R.id.quit:
                dialog.cancel();
                break;
            default:break;
        }
    }


    //开始登录线程，其中的帐号密码验证码cookie都已经存在
    class StartLogin extends AsyncTask<Void,Void,Void>
    {
        String responce;
        //运行前
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loginingProBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                responce = OkHttpTools.login(okHttpClient,number,passwd,captcha,cookie).execute().body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            loginingProBar.setVisibility(View.GONE);
            responceText.setVisibility(View.VISIBLE);
            switch(isLoginSuc(responce))
            {
                case UNKNOW_WRONG:
                    //提示框未知错误
                    responceText.setText("未知错误");
                    break;
                case LOGIN_CAPTCHA_WRONG:
                    //提示框验证码错误，请重新输入
                    responceText.setText("验证码错误，请重新输入");
                    break;
                case LOGIN_PASSWD_WRONG:
                    //提示框密码错误
                    responceText.setText("密码错误，请重新登录");
                    new UpdateCaptcha().execute();
                    break;
                case LOGIN_SUCCESS:
                    //提示框登录成功,将登录信息放如本地存储中，交给主ACTIVITY处理跳转任务
                    alterInfo(number,passwd,cookie,captcha,true,isRemember.isChecked());
                    ((LibraryActivity)getActivity()).hasLogin();
                    responceText.setVisibility(View.GONE);
                    dialog.cancel();
                    break;
                default:break;
            }
        }

        //判断返回的字符串中是否包含了用户名错误等等

        private int isLoginSuc(String responce)
        {
            if(responce.contains("注销"))return LOGIN_SUCCESS;
            else if(responce.contains("对不起，密码错误，请查实"))return LOGIN_PASSWD_WRONG;
            else if(responce.contains("验证码错误"))return LOGIN_CAPTCHA_WRONG;
            return UNKNOW_WRONG;
        }
    }

    //更新验证码线程
    class UpdateCaptcha extends AsyncTask<Void,Void,Void>
    {
        //运行前
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Request request = new Request.Builder().url("http://202.194.40.71:8080/reader/captcha.php").build();
            Call call = okHttpClient.newCall(request);
            Response response = null;
            InputStream inputStream = null;

            try {
                response = call.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputStream = response.body().byteStream();
            captchaBitmap = BitmapFactory.decodeStream(inputStream);
            cookie = cookieStore.get(request.url()).get(0).toString();
            Log.i("cookie是",cookie);
            return null;
        }

        //完成时候调用
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            captchaView.setImageBitmap(captchaBitmap);
            captchaProBar.setVisibility(View.GONE);
        }
    }
}
