package com.xinqi.ihandwh.HttpService.OrderSeatService;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.xinqi.ihandwh.HttpService.HttpTools;
import com.xinqi.ihandwh.Model.FloorInfo;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by syd on 2015/11/17.
 */
public class OrderSeatService extends Service{
    public static HttpClient coreClient;
    /**
     * Called by the system when the service is first created.  Do not call this method directly.
     */
    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Called by the system every time a client explicitly starts the service by calling
     * {@link Context#startService}, providing the arguments it supplied and a
     * unique integer token representing the start request.  Do not call this method directly.
     * <p>
     * <p>For backwards compatibility, the default implementation calls
     * {@link #onStart} and returns either {@link #START_STICKY}
     * or {@link #START_STICKY_COMPATIBILITY}.
     * <p>
     * <p>If you need your application to run on platform versions prior to API
     * level 5, you can use the following model to handle the older {@link #onStart}
     * callback in that case.  The <code>handleCommand</code> method is implemented by
     * you as appropriate:
     * <p>
     * {@sample development/samples/ApiDemos/src/com/example/android/apis/app/ForegroundService.java
     * start_compatibility}
     * <p>
     * <p class="caution">Note that the system calls this on your
     * service's main thread.  A service's main thread is the same
     * thread where UI operations take place for Activities running in the
     * same process.  You should always avoid stalling the main
     * thread's event loop.  When doing long-running operations,
     * network calls, or heavy disk I/O, you should kick off a new
     * thread, or use {@link AsyncTask}.</p>
     *
     * @param intent  The Intent supplied to {@link Context#startService},
     *                as given.  This may be null if the service is being restarted after
     *                its process has gone away, and it had previously returned anything
     *                except {@link #START_STICKY_COMPATIBILITY}.
     * @param flags   Additional data about this start request.  Currently either
     *                0, {@link #START_FLAG_REDELIVERY}, or {@link #START_FLAG_RETRY}.
     * @param startId A unique integer representing this specific request to
     *                start.  Use with {@link #stopSelfResult(int)}.
     * @return The return value indicates what semantics the system should
     * use for the service's current started state.  It may be one of the
     * constants associated with the {@link #START_CONTINUATION_MASK} bits.
     * @see #stopSelfResult(int)
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Return the communication channel to the service.  May return null if
     * clients can not bind to the service.  The returned
     * {@link IBinder} is usually for a complex interface
     * that has been <a href="{@docRoot}guide/components/aidl.html">described using
     * aidl</a>.
     * <p>
     * <p><em>Note that unlike other application components, calls on to the
     * IBinder interface returned here may not happen on the main thread
     * of the process</em>.  More information about the main thread can be found in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html">Processes and
     * Threads</a>.</p>
     *
     * @param intent The Intent that was used to bind to this service,
     *               as given to {@link Context#bindService
     *               Context.bindService}.  Note that any extras that were included with
     *               the Intent at that point will <em>not</em> be seen here.
     * @return Return an IBinder through which clients can call on to the
     * service.
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public static boolean testUserInfoIsTrue(String userId, String passWd)
            throws Exception {

        HttpClient client = new DefaultHttpClient(); // 链接

        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();

        // 构建实体数据
        postParameters.add(new BasicNameValuePair("subCmd", "Login"));
        postParameters.add(new BasicNameValuePair("txt_LoginID", userId));
        postParameters.add(new BasicNameValuePair("txt_Password", passWd));
        postParameters.add(new BasicNameValuePair("selSchool", "15"));

        String reqUrl = "http://yuyue.juneberry.cn/Login.aspx";
        String varString = HttpTools.GetHTTPRequest(reqUrl, client);
        String []strs = parseEandVAttri(varString).split(",");
        postParameters.add(new BasicNameValuePair("__EVENTVALIDATION", strs[1]));
        postParameters.add(new BasicNameValuePair("__VIEWSTATE", strs[0]));

        // 发送post请求
        String s = HttpTools.PostHTTPRequest(
                "http://yuyue.juneberry.cn/Login.aspx", client, postParameters);

        // 登录失败
        if (s.contains("登录失败:用户名或密码错误")) {
            return false;
        } else {
            OrderSeatService.coreClient = client;
        }

        return true;
    }

    /**
     * 一键抢座业务实现
     */
    public static String OneKeySit(HttpClient client) throws Exception {

        // 获取当前日期+1
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date beginDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(beginDate);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
        String date = format.format(calendar.getTime());

        // 发送get请求
        HttpTools.GetHTTPRequest(
                "http://yuyue.juneberry.cn/BookSeat/BookSeatListForm.aspx",
                client);

        // 随机生成room号
        String[] rooms = { "103", "104", "105", "106", "107", "108", "109",
                "110", "111", "112", "203", "204" };
        Random random = new Random();
        int index = random.nextInt(rooms.length); // 随机生成一个
        String room = rooms[index]; // 获取房间No
        // System.out.println("room：" + room);

        // 测试，他妈的座位都被抢光了。
        // room = "103";
        List<String> list =getYuYueInfo("000" + room, date); // 获取该房间的列表
        if (0 == list.size()) {
            // 没有可用座位
            return "empty";
        } else {
            // 获取座位
            String sitNo = list.get(random.nextInt(list.size()));
            // sitNo = "25";
            String res = subYuYueInfo(client, "000" + room, sitNo,
                    date);
            // System.out.println(res);
            if (res.contains("已经存在有效的预约记录")) {
                return "fail";
            }

           /* String info = "<infos>";

            info += "<info><lblReadingRoomName>"
                    + room.charAt(room.length() - 1) + "楼</lblReadingRoomName>"
                    + "<lblSeatNo>" + sitNo + "</lblSeatNo>" + "<lblBookDate>"
                    + date + "</lblBookDate>" + "<lbComformTime>7:50至8:35"
                    + "</lbComformTime></info>";

            info += "</infos>";*/
            String s;
            if (index==10){
                s="图东环楼03"+","+sitNo+","+date;
            }else if (index==11){
                s="图东环楼04"+","+sitNo+","+date;

            }else {

            Log.i("bacc","sssssss room: :"+room);
            Log.i("bacc","sssssss 1:"+room.substring(1));
            s=room.substring(1)+","+sitNo+","+date;
            }
            Log.i("bacc","sssssss: :"+s);
            return s;
        }
    }

    /**
     * 取消功能
     * 成功返回success
     * empty代表在别处已经取消
     * error代表网络错误
     */

    public static String Cancel(HttpClient client) {

        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        // 构建实体数据
        postParameters.add(new BasicNameValuePair("subCmd", ""));
        postParameters.add(new BasicNameValuePair("subBookNo", ""));
        postParameters.add(new BasicNameValuePair("chooseDate", "选择日期"));
        postParameters.add(new BasicNameValuePair("ddlDate", "7"));
        postParameters.add(new BasicNameValuePair("ddlRoom", "-1"));
        String reqUrl = "http://yuyue.juneberry.cn/UserInfos/QueryLogs.aspx";
        String varString = HttpTools.GetHTTPRequest(reqUrl, client);
        String []strs = parseEandVAttri(varString).split(",");
        postParameters.add(new BasicNameValuePair("__EVENTVALIDATION", strs[1]));
        postParameters.add(new BasicNameValuePair("__VIEWSTATE", strs[0]));

        // 1.提交到http://yuyue.juneberry.cn/UserInfos/QueryLogs.aspx表单
        String s = HttpTools.PostHTTPRequest(
                "http://yuyue.juneberry.cn/UserInfos/QueryLogs.aspx", client,
                postParameters);
        // System.out.println(s);
        // 获取subBookNo
        if(s==null||s.equals("")){
            return "error";
        }
        int start = s.indexOf("(&apos;") + 7;
        int end = s.indexOf("&apos;)");
        if (start <= 6 || end <= 0 || start > s.length() || end > s.length()) {
            return "empty";
        }
        String subBookNo = s.substring(start, end);
        //System.out.print("----" + subBookNo + "----");
        postParameters.set(0, new BasicNameValuePair("subCmd", "cancel"));
        postParameters.set(1, new BasicNameValuePair("subBookNo", subBookNo));

        String reqUrl2 = "http://yuyue.juneberry.cn/UserInfos/QueryLogs.aspx";
        String varString2 = HttpTools.GetHTTPRequest(reqUrl2, client);
        String []strs2 = parseEandVAttri(varString2).split(",");
        postParameters.set(5,new BasicNameValuePair("__EVENTVALIDATION", strs2[1]));
        postParameters.set(6,new BasicNameValuePair("__VIEWSTATE", strs2[0]));

        // 2.提交到http://yuyue.juneberry.cn/UserInfos/QueryLogs.aspx,带详细信息
        s = HttpTools.PostHTTPRequest(
                "http://yuyue.juneberry.cn/UserInfos/QueryLogs.aspx", client,
                postParameters);
        if(s==null||s.equals("")){
            return "error";
        }
        if (s.contains("alert('成功取消预约！')"))
            return "success";
        else
            return "empty";
    }
    /**
     * 解析指定楼层的座位信息获得座位
     */
    public static List<String> parseSitsInfo(String s)
    {
        String tag1 = "text-align: center; margin-left: 0; margin-top: 0\">";

        List<String> lists = new ArrayList<String>();

        String subStr = s; // 原串

        int index = subStr.indexOf(tag1);

        while(index != -1)
        {
            String val = subStr.substring(index + tag1.length() + 30, index + tag1.length() + 33);

            lists.add(val);

            subStr = subStr.substring(index + tag1.length() + 33);
            index = subStr.indexOf(tag1);
        }

        return lists;
    }

    /**
     * 得到指定房间预约信息
     * @return
     * @throws Exception
     */
    public static List<String> getYuYueInfo(String room, String date) throws Exception
    {
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        // 构建实体数据
        Log.d("getYuYueInfo", "room:" + room + "|date:" + date);
        postParameters.add(new BasicNameValuePair("subCmd", "query"));
        postParameters.add(new BasicNameValuePair("selReadingRoom", room));
        postParameters.add(new BasicNameValuePair("txtBookDate", date));
        postParameters.add(new BasicNameValuePair("hidBookDate", ""));
        postParameters.add(new BasicNameValuePair("hidRrId", ""));

        String reqUrl = "http://yuyue.juneberry.cn/BookSeat/BookSeatListForm.aspx";
        String varString = HttpTools.GetHTTPRequest(reqUrl, coreClient);
        String []strs = parseEandVAttri(varString).split(",");
        postParameters.add(new BasicNameValuePair("__EVENTVALIDATION", strs[1]));
        postParameters.add(new BasicNameValuePair("__VIEWSTATE", strs[0]));

        // 发送post请求

        String yuyueInfo = HttpTools.PostHTTPRequest("http://yuyue.juneberry.cn/BookSeat/BookSeatListForm.aspx", coreClient, postParameters);
        Log.i("bac","yuyueInfo；"+yuyueInfo);
        // 座号list
        List<String> lists =parseSitsInfo(yuyueInfo);
        //System.out.println("得到指定房间预约信息返回结果;原始数据:"+yuyueInfo);
        return lists;
    }
    /**
     * 根据s获取属性值
     * @param s
     * @param beginStr
     * @param endStr
     * @return
     */
    public static String getEandVAttri(String s, String beginStr, String endStr)
    {
        String sub_s = s;
        int b_index = sub_s.indexOf(beginStr) + beginStr.length();
        sub_s = sub_s.substring(b_index);
        int e_index = sub_s.indexOf(endStr);
        return sub_s.substring(0, e_index);
    }

    /**
     * 解析属性
     * @param s
     */
    public static String parseEandVAttri(String s) {

        String __VIEWSTATE = getEandVAttri(s, "id=\"__VIEWSTATE\" value=\"", "\" />");
        String __EVENTVALIDATION = getEandVAttri(s, "id=\"__EVENTVALIDATION\" value=\"", "\" />");

        System.out.println("__VIEWSTATE: " + __VIEWSTATE);
        System.out.println("__EVENTVALIDATION: " + __EVENTVALIDATION);

        String val = __VIEWSTATE + "," + __EVENTVALIDATION;

        return val;
    }

    /**
     * 提交预约信息,精准预约
     * @param client
     * @return
     * @throws Exception
     */
    public static String subYuYueInfo(HttpClient client, String room, String sitNo, String date) throws Exception
    {

        // 发送get请求
        String reqUrl = "http://yuyue.juneberry.cn/BookSeat/BookSeatMessage.aspx?seatNo=" + room + sitNo + "&seatShortNo=" + sitNo + "&roomNo=" + room + "&date=" + date;
        String s = HttpTools.GetHTTPRequest(reqUrl, client);
        //System.out.println("s:" + s);
        // 需要解析出__EVENTVALIDATION和__VIEWSTATE字段
        String []strs = parseEandVAttri(s).split(",");
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        // 构建实体数据
        postParameters.add(new BasicNameValuePair("subCmd", "query"));
        postParameters.add(new BasicNameValuePair("__EVENTVALIDATION", strs[1]));
        postParameters.add(new BasicNameValuePair("__VIEWSTATE", strs[0]));
        //Log.i("bac",strs.toString()+"---------------");
        // 发送post请求
        String res = HttpTools.PostHTTPRequest(reqUrl, client, postParameters);
        return res;
    }
    /**
     * 从网上获取座位信息
     *
     * @return
     *
     * @throws Exception
     */
    public static List<FloorInfo> getFloorInfo(String id,String password) throws Exception {
        HttpClient client = new DefaultHttpClient();
        // 登录
        HttpPost request = new HttpPost("http://yuyue.juneberry.cn/");
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("subCmd", "Login"));
        postParameters
                .add(new BasicNameValuePair("txt_LoginID", id));
        postParameters.add(new BasicNameValuePair("txt_Password", password));
        postParameters.add(new BasicNameValuePair("selSchool", "15"));

        String reqUrl = "http://yuyue.juneberry.cn/";
        String varString = HttpTools.GetHTTPRequest(reqUrl, client);
        String []strs = parseEandVAttri(varString).split(",");
        postParameters.add(new BasicNameValuePair("__EVENTVALIDATION", strs[1]));
        postParameters.add(new BasicNameValuePair("__VIEWSTATE", strs[0]));

        UrlEncodedFormEntity formEntity = null;
        formEntity = new UrlEncodedFormEntity(postParameters);
        request.setEntity(formEntity);
        client.execute(request);
        // 访问座位页面
        HttpGet sitRequest = new HttpGet(
                new URI(
                        "http://yuyue.juneberry.cn/ReadingRoomInfos/ReadingRoomState.aspx"));
        HttpResponse response = client.execute(sitRequest);
        List<FloorInfo> floorInfos =parseInfo(EntityUtils.toString(response.getEntity(), "UTF-8"));
        return floorInfos;
    }
    /**
     * 解析HTML信息
     *
     * @return
     */
    public static List<FloorInfo> parseInfo(String content) {
        List<FloorInfo> result = new ArrayList<FloorInfo>();
        Document doc = Jsoup.parse(content);
        Elements items = doc.getElementsByAttributeValue("data-theme", "c");
        //Log.d("#####", items.toString());
        for (Element item : items) {
            FloorInfo info = new FloorInfo();
            info.layer = item.text().substring(0, item.text().indexOf(':'));
            Elements itemlis = item.getElementsByTag("ul").get(0)
                    .getElementsByTag("li");
            for (Element li : itemlis) {
                if (li.text().startsWith("总座位")) {
                    info.total = Integer
                            .parseInt(li.text().replace("总座位：", ""));
                } else if (li.text().startsWith("空闲")) {
                    info.rest = Integer.parseInt(li.text().replace("空闲：", ""));
                }
            }
            result.add(info);
        }
        return result;
    }
}
