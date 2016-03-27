package com.xinqi.ihandwh.HttpService;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * Created by syd on 2015/11/16.
 */
public class HttpTools {
    public static String PostHTTPRequest(String path, HttpClient client, List<NameValuePair> postParameters)
    {
        try {
            HttpPost request = new HttpPost(path); // 构建post路径
            //链接超时
            //client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 000);
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters); // 传递post实体数据
            request.setEntity(formEntity); // 设置实体
            HttpResponse res = client.execute(request); // 执行请求
            String str = EntityUtils.toString(res.getEntity(), "UTF-8"); // 获取请求数据
            //EntityUtils.consume(res.getEntity()); // clear实体数据
            return str;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取预约信息get提交
     * @param path
     * @param client
     * @return
     */
    public static String GetHTTPRequest(String path, HttpClient client)
    {
        try {
            HttpGet sitRequest = new HttpGet(new URI(path)); // 构建get路径
            HttpResponse res = client.execute(sitRequest);// 执行请求
            String str = EntityUtils.toString(res.getEntity(), "UTF-8"); // 获取请求数据
            //client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,10000);
            //EntityUtils.consume(res.getEntity()); // clear实体数据

            return str;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    public static String getContent(String url, Map<String, String> param) {
        String result = "";
        String paramStr = "";
        for (String key : param.keySet()) {
            try {
                paramStr += key + "="
                        + URLEncoder.encode(param.get(key), "UTF-8") + "&";
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
        }
        HttpGet getMethod = new HttpGet(url + "?" + paramStr);
        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpResponse response = httpClient.execute(getMethod);
            if (response.getStatusLine().getStatusCode() == 200)
                result = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
