package com.xinqi.ihandwh.HttpService;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Qking on 2016/4/14.
 */
public class OkHttpTools {
    private static OkHttpClient okHttpClient;

    /**
     *
     * @param okHttpClient 客户端
     * @param number    帐号
     * @param passwd    密码
     * @param captcha   验证码
     * @param cookie     cookie
     * @return 返回一个需要访问请求的Call对象
     */
    public static Call login(OkHttpClient okHttpClient,String number,String passwd,String captcha,String cookie){

        Headers headers = new Headers.Builder()
                .add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .add("Accept-Encoding", "gzip, deflate")
                .add("Accept-Language", "zh-CN,zh;q=0.8")
                .add("Connection", "keep-alive")
                .add("Host", "202.194.40.71:8080")
                .add("Upgrade-Insecure-Requests", "1")
                .add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.108 Safari/537.36")
                .add("Cookie",cookie)
                .build();

        RequestBody requestBody = new FormBody.Builder().add("number",number)
                .add("passwd",passwd)
                .add("captcha",captcha)
                .add("select","cert_no")
                .add("returnUrl","")
                .build();

        return okHttpClient.newCall(new Request.Builder().url("http://202.194.40.71:8080/reader/redr_verify.php").headers(headers).post(requestBody).build());
    }

    public static Call getBookCall(OkHttpClient okHttpClient,String cookie)
    {
        Headers headers = new Headers.Builder()
                .add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .add("Accept-Encoding", "gzip, deflate")
                .add("Accept-Language", "zh-CN,zh;q=0.8")
                .add("Connection", "keep-alive")
                .add("Host", "202.194.40.71:8080")
                .add("Upgrade-Insecure-Requests", "1")
                .add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.108 Safari/537.36")
                .add("Cookie",cookie)
                .build();
        Request request = new Request.Builder().url("http://202.194.40.71:8080/reader/book_lst.php").headers(headers).build();
        return okHttpClient.newCall(request);
    }

    public static Call getRenewCall(OkHttpClient okHttpClient,String cookie,String barcode,String check,String capthca)
    {
        Headers headers = new Headers.Builder()
                .add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .add("Accept-Encoding", "gzip, deflate")
                .add("Accept-Language", "zh-CN,zh;q=0.8")
                .add("Connection", "keep-alive")
                .add("Host", "202.194.40.71:8080")
                .add("Upgrade-Insecure-Requests", "1")
                .add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.108 Safari/537.36")
                .add("Cookie",cookie)
                .build();
        Request request = new Request.Builder().url("http://202.194.40.71:8080/reader/ajax_renew.php?bar_code="
                +barcode
                +"&check="+check
                +"&captcha="+capthca
                +"&time="+new Date().getTime()).headers(headers).build();
        return okHttpClient.newCall(request);
    }



}
