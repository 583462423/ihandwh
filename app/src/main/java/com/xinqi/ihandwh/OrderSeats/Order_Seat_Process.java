package com.xinqi.ihandwh.OrderSeats;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.xinqi.ihandwh.ConfigCenter.SeeOrderSeatHistory;
import com.xinqi.ihandwh.HttpService.HttpTools;
import com.xinqi.ihandwh.HttpService.OrderSeatService.OrderSeatService;
import com.xinqi.ihandwh.R;

import org.apache.http.client.HttpClient;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//import com.xinqi.ihandwh.R;

/**
 * Created by syd on 2015/11/16.
 */
public class Order_Seat_Process extends AppCompatActivity {
    private SharedPreferences sharedPreferences;//存储最近一次预约是否在本机取消
    private SharedPreferences.Editor editor;
    private TextView order_seat_result_tv, order_info_warn_tv,order_info_time_tv,order_info_pos_tv,your_his_words;
    private View middleView;
    private View line1,line2;
    Button button;
    public static final int ORDERSTATUS_TIMEOUT =-1;//链接失败
    //public static final int ORDERSTATUS_CONECT_SUCCESS =1;//链接成功
    public static final int ORDERSTATUS_HASORDERED =2;//已经存在有效预约
    public static final int ORDERSTATUS_ONEKEY_FAIL =-11;//一键预约失败
    public static final int ORDERSTATUS_ONE_KEY_SUCCESS =11;//一键预约成功
    public static final int ORDERSTATUS_SPECIFIC_SUCCESS =21;//精确预约成功
    public static final int ORDERSTATUS_SPECIFIC_FAIl =-21;//精确预约失败
    public static final int ORDERSTATUS_SPECIFIC_SEAT_ORDERED =-22;//精确预约此座位已有预约
     String resultspe="";
    int orderstatus;
    ProgressBar progressBar;
    ActionBar actionBar;
    int orderMode=-1;
    String room,sitNum,date;
//    private Calendar currenttime,confirmtime;
    private android.text.format.Time currenttime,confirmtime;
    private int currentYear,currentMonth,currentDay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar=getSupportActionBar();
        setContentView(R.layout.order_seat_result_layout);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("预约座位");
        actionBar.setHomeAsUpIndicator(R.drawable.psdback1);
        currenttime=new android.text.format.Time("GMT+8");
        //获取当前系统时间
        currenttime.setToNow();
        currentYear=currenttime.year;
        currentMonth=currenttime.month+1;
        currentDay=currenttime.monthDay;
        Log.i("bac","年"+currentYear+"月"+currentMonth+"日"+currentDay);
        sharedPreferences=getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE);
        editor=sharedPreferences.edit();
        Intent intent=getIntent();
        orderMode=intent.getIntExtra(SelectSeatActivity.ORDERMODE,-1);
        room=intent.getStringExtra(SelectSeatActivity.ORDER_ROOM);
        sitNum=intent.getStringExtra(SelectSeatActivity.ORDER_SITNUM);
        date=intent.getStringExtra(SelectSeatActivity.ORDER_DATE);
        progressBar= (ProgressBar) findViewById(R.id.orderprogressbar);
        order_info_warn_tv = (TextView) findViewById(R.id.order_info_warn_tv);
        order_info_time_tv = (TextView) findViewById(R.id.order_info_time_tv);
        order_info_pos_tv = (TextView) findViewById(R.id.order_info_position_tvtv);
        order_seat_result_tv= (TextView) findViewById(R.id.order_seat_result_tv);
        your_his_words= (TextView) findViewById(R.id.your_his_words);
        middleView=findViewById(R.id.middleView);
        line1=findViewById(R.id.order_info_line1);
        line2=findViewById(R.id.order_info_line2);
        button= (Button) findViewById(R.id.btnafter_order_result);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orderstatus==ORDERSTATUS_ONEKEY_FAIL
                        || orderstatus==ORDERSTATUS_TIMEOUT
                        ||orderstatus==ORDERSTATUS_SPECIFIC_FAIl) {
                    if (orderMode==0)
                    OneKey_Order();
                    else if (orderMode==1){
//                        specific_Order(OrderSeatService.coreClient, room, sitNum, date);
                        finish();
                    }
                    button.setEnabled(false);
                    order_seat_result_tv.setText("预约中");
                    middleView.setVisibility(View.INVISIBLE);
                    order_seat_result_tv.setTextColor(getResources().getColor(R.color.black));
                } else if (orderstatus==ORDERSTATUS_SPECIFIC_SEAT_ORDERED){
                    //重新选择座位

                }else {
//                    onBackPressed();
                    startActivity(new Intent(Order_Seat_Process.this, SeeOrderSeatHistory.class));
                    finish();
                }
            }
        });
        if (orderMode==0) {
            OneKey_Order();
            middleView.setVisibility(View.INVISIBLE);

        }else if (orderMode==1){
            specific_Order(OrderSeatService.coreClient, room, sitNum, date);
            middleView.setVisibility(View.INVISIBLE);

        }
        button.setEnabled(false);
        PushAgent.getInstance(this).onAppStart();
    }
    private void specific_Order(final HttpClient client, final String room, final String sitNo, final String date) {
        progressBar.setVisibility(View.VISIBLE);
        new Thread() {
            @Override
            public void run() {
                Log.d("bac"," speci thread start");
                Message message = new Message();
                    // 获取当前日期+1
                    SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                    Date beginDate = new Date();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(beginDate);
                    calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
                    String date1 = format.format(calendar.getTime());
                    //Log.i("bacground","精确预约11111111111111");
//                    UserinfoUtils userinfoUtils = new UserinfoUtils(Order_Seat_Process.this);
                try {
//                    OrderSeatService.testUserInfoIsTrue(userinfoUtils.get_LastId(),userinfoUtils.get_LastPassword());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                HttpTools.GetHTTPRequest(
                            "http://yuyue.juneberry.cn/BookSeat/BookSeatListForm.aspx",
                            OrderSeatService.coreClient);
//                    List<String> list = null; // 获取该房间的列表
//                    try {
//
////                        list = OrderSeatService.getYuYueInfo("000" + room, date1);
//                        Log.i("bac","获取该房间的列表"+list);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Log.i("bac", "精准预约：list为空");
//
//                    }
//                    if (0 == list.size()) {
//                        // 没有可用座位
//                        resultspe = "empty";
//                        Log.i("bac","精准预约：list为空");
//                        message.what = ORDERSTATUS_SPECIFIC_FAIl;
//                    } else {
                        try {
                            Log.i("bac","精准预约返回值1111："+resultspe);
                            resultspe = OrderSeatService.subYuYueInfo(OrderSeatService.coreClient,room, sitNo, date);
                            Log.i("bac","精准预约返回值1111："+resultspe);
                        } catch (Exception e) {
                            //链接错误，包括超时
                            Log.i("bacground", "精确预约超时");
                            message.what = ORDERSTATUS_TIMEOUT;
                            e.printStackTrace();
                        }
                        // System.out.println(res);
                // TODO: 2016/3/4  
                        if (resultspe.contains("已经存在有效的预约记录")) {
                            resultspe = "fail";
                            message.what = ORDERSTATUS_HASORDERED;
                        }else {
                            Log.i("sxj",resultspe);
                            Log.i("sxj","result"+resultspe);
                            message.what = ORDERSTATUS_SPECIFIC_SUCCESS;
                        }
                        //Log.i("bacground","精确预444444444444444");
                        Log.i("bacground", "精确预约返回结果：" + resultspe);
                        message.obj = resultspe;
//                    }
                     handler.sendMessage(message);

                }

        }.start();
    }

    private void OneKey_Order() {
        progressBar.setVisibility(View.VISIBLE);
        new Thread() {
            @Override
            public void run() {
                Message message = new Message();
                try {
//                    UserinfoUtils userinfoUtils = new UserinfoUtils(Order_Seat_Process.this);
//                    boolean b = OrderSeatService.testUserInfoIsTrue(userinfoUtils.get_LastId(), userinfoUtils.get_LastPassword());
                    String result = OrderSeatService.OneKeySit(OrderSeatService.coreClient);
                    if (result.contains("empty")){//一键预约失败
                        message.what=ORDERSTATUS_ONEKEY_FAIL;
                        orderstatus=ORDERSTATUS_ONEKEY_FAIL;
                        // TODO: 2016/3/4
                    }else if (result.contains("fail")){//已经有过预约
                        Log.i("bac","One key has orderHis");
                        message.what=ORDERSTATUS_HASORDERED;
                        orderstatus=ORDERSTATUS_HASORDERED;
                    }else {//一键预约成功
                        message.what=ORDERSTATUS_ONE_KEY_SUCCESS;
                        message.obj=result;
                        Log.i("bacc","One key rsult:"+result);
                        orderstatus=ORDERSTATUS_ONE_KEY_SUCCESS;
                    }
//                    System.out.println(result + "=====");
                } catch (Exception e) {
                    //链接错误，包括超时
                    message.what = ORDERSTATUS_TIMEOUT;
                    orderstatus = ORDERSTATUS_TIMEOUT;
                    e.printStackTrace();

                }
                handler.sendMessage(message);

            }
        }.start();
    }


    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case ORDERSTATUS_TIMEOUT://链接错误
                    middleView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    order_seat_result_tv.setTextColor(getResources().getColor(R.color.red));
                    order_seat_result_tv.setText(getResources().getText(R.string.order_fail));
                    order_info_time_tv.setText(getResources().getText(R.string.order_connection_out_tip));
                    // TODO: 2016/3/3
                    your_his_words.setVisibility(View.GONE);
                    line1.setVisibility(View.GONE);
                    line2.setVisibility(View.GONE);
                    order_info_warn_tv.setVisibility(View.VISIBLE);
                    order_info_pos_tv.setVisibility(View.GONE);
                    order_info_warn_tv.setVisibility(View.GONE);
                    button.setVisibility(View.VISIBLE);
                    orderstatus = ORDERSTATUS_TIMEOUT;
                    button.setText("再试一次");
                    button.setEnabled(true);
                    break;
                case ORDERSTATUS_ONE_KEY_SUCCESS://一键预成功
                    middleView.setVisibility(View.VISIBLE);
                    order_seat_result_tv.setTextColor(getResources().getColor(R.color.black));
                    editor.putBoolean("leastcanceled", false);//将当前预约设置为没有在本机取消
                    editor.commit();
                    order_info_warn_tv.setVisibility(View.VISIBLE);
//                    order_seat_result_tv.setTextColor(getResources().getColor(R.color.green));
                    order_seat_result_tv.setText(getResources().getText(R.string.order_success));
                    progressBar.setVisibility(View.INVISIBLE);
                    line1.setVisibility(View.VISIBLE);
                    line2.setVisibility(View.VISIBLE);
                    your_his_words.setVisibility(View.VISIBLE);
                    order_info_warn_tv.setVisibility(View.VISIBLE);
                    order_info_time_tv.setVisibility(View.VISIBLE);
                    order_info_pos_tv.setVisibility(View.VISIBLE);
                    Log.i("bac", "一键预约1：" + msg.obj.toString());
                    String[] strings=msg.obj.toString().split(",");
                    Log.i("bac", "一键预约2：" + strings);
                    order_info_pos_tv.setText("位置：" + strings[0] + "楼" + strings[1] + "座");
                    order_info_time_tv.setText("时间：" + strings[2]);
                    order_info_warn_tv.setText("请您于"+strings[2]+",\n"+"7.50am-8.35am准时刷卡确认");
                    //设置最晚刷卡确定时间
                    confirmtime=new android.text.format.Time("GMT+8");
                    confirmtime.set(0, 35, 8, currentDay, currentMonth, currentYear);
//                    Log.i("bac","刷卡最晚时间"+confirmtime.toMillis(true)+"");
//                    editor.putLong("confirmtime",confirmtime.toMillis(true));

                   /* confirmtime=Calendar.getInstance();
                    confirmtime.set(currentYear,currentMonth,currentDay);*/
//                    Long s=calendar.getTimeInMillis()
//                    editor.putInt("")
                    /*for (int i=0;i<strings.length;++i){
                        Log.i("bac","一键："+strings[i]);
                    }*/
                    //将预约信息添加到数据库
                    OrderSeatHistoryHelper orderSeatHistoryHelper=new OrderSeatHistoryHelper(Order_Seat_Process.this);
                    orderSeatHistoryHelper.insertOrderInfo(strings[0],strings[1],strings[2],confirmtime.toMillis(true),"no");
                    orderstatus =ORDERSTATUS_ONE_KEY_SUCCESS;
                    button.setText("查看预约记录");
                    button.setEnabled(true);
                   break;
                case ORDERSTATUS_ONEKEY_FAIL://一键预约失败
                    middleView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    order_seat_result_tv.setTextColor(getResources().getColor(R.color.red));
                    order_seat_result_tv.setText(getResources().getText(R.string.order_fail));
                    order_info_time_tv.setText(getResources().getText(R.string.order_empty_tip));
                    orderstatus= ORDERSTATUS_ONEKEY_FAIL;
                    button.setText("再试一次");
                    button.setEnabled(true);
                    // TODO: 2016/3/3
                    your_his_words.setVisibility(View.GONE);
                    line1.setVisibility(View.GONE);
                    line2.setVisibility(View.GONE);
                    order_info_warn_tv.setVisibility(View.VISIBLE);
                    order_info_pos_tv.setVisibility(View.GONE);
                    order_info_warn_tv.setVisibility(View.GONE);
                    break;
                case ORDERSTATUS_HASORDERED://已经存在预约记录
                    middleView.setVisibility(View.VISIBLE);

                    //Log.i("9999999999999999","13");
                        progressBar.setVisibility(View.INVISIBLE);
                        order_seat_result_tv.setTextColor(getResources().getColor(R.color.red));
                        order_seat_result_tv.setText(getResources().getText(R.string.order_fail));
                        order_info_time_tv.setText(getResources().getText(R.string.order_hashistory_tip));
                        orderstatus = ORDERSTATUS_HASORDERED;
                        button.setText("查看预约记录");
                        button.setEnabled(true);
                    // TODO: 2016/3/3
                    your_his_words.setVisibility(View.GONE);
                    line1.setVisibility(View.GONE);
                    line2.setVisibility(View.GONE);
                    order_info_warn_tv.setVisibility(View.VISIBLE);
                    order_info_pos_tv.setVisibility(View.GONE);
                    order_info_warn_tv.setVisibility(View.GONE);
                    break;
                // TODO: 2015/11/18
                case ORDERSTATUS_SPECIFIC_FAIl://精确预约失败
                    middleView.setVisibility(View.VISIBLE);

                    progressBar.setVisibility(View.INVISIBLE);
                    Log.i("bac", "bac message :精确预约失败");
                    order_seat_result_tv.setTextColor(getResources().getColor(R.color.red));
                    order_seat_result_tv.setText(getResources().getText(R.string.order_fail));
                    order_info_time_tv.setText(getResources().getText(R.string.order_empty_tip));
                    orderstatus=ORDERSTATUS_SPECIFIC_FAIl;
                    button.setText("重新选择座位");
                    button.setEnabled(true);
                    // TODO: 2016/3/3
                    your_his_words.setVisibility(View.GONE);
                    line1.setVisibility(View.GONE);
                    line2.setVisibility(View.GONE);
                    order_info_warn_tv.setVisibility(View.VISIBLE);
                    order_info_pos_tv.setVisibility(View.GONE);
                    order_info_warn_tv.setVisibility(View.GONE);
                    break;
                case ORDERSTATUS_SPECIFIC_SUCCESS://精确预约成功
                    middleView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    order_info_time_tv.setVisibility(View.VISIBLE);
                    order_info_warn_tv.setVisibility(View.VISIBLE);
                    order_info_pos_tv.setVisibility(View.VISIBLE);
                    order_seat_result_tv.setText(getResources().getText(R.string.order_success));
                    room=roomid_totext(room);
                    Log.i("bac","speci show room:"+room);
                    order_seat_result_tv.setTextColor(getResources().getColor(R.color.black));
                    confirmtime=new android.text.format.Time("GMT+8");
                    confirmtime.set(0, 35, 8, currentDay, currentMonth, currentYear);
                    order_info_pos_tv.setText("位置：" + room + "楼" + sitNum + "座");
                    order_info_time_tv.setText("时间："+date);
                    order_info_warn_tv.setText("请您于"+date+",\n7.50am-8.35am准时刷卡确认");
                    orderstatus=ORDERSTATUS_SPECIFIC_SUCCESS;
                    button.setText("查看预约记录");
                    //保存预约信息到数据库
                    OrderSeatHistoryHelper orderSeatHistoryHelper1=new OrderSeatHistoryHelper(Order_Seat_Process.this);
                    orderSeatHistoryHelper1.insertOrderInfo(room,sitNum, date,confirmtime.toMillis(true),"no");
                    button.setEnabled(true);
                    editor.putBoolean("leastcanceled",false);//将当前预约设置为没有在本机取消
                    editor.commit();
                    Log.i("bacground", "精确预约成功,预约信息："+room+"楼"+sitNum+"号时间"+date+"\n请在7:50至8:35到图书馆刷卡确认");
                    break;
               /* case ORDERSTATUS_SPECIFIC_SEAT_ORDERED://精确预约此座位已有人预约
                    progressBar.setVisibility(View.INVISIBLE);
                    orderstatus=ORDERSTATUS_SPECIFIC_SEAT_ORDERED;
                    button.setText("重新选择座位");
                    button.setEnabled(true);
                    Log.i("bacground","精确预约此座位已有人预约");
                    break;*/
            }
        }
    };

    private String roomid_totext(String id) {
        String room=null;
        switch (id){
            case "000103":
                room="3";
                break;
            case "000104":
                room="4";
                break;
            case "000105":
                room="5";
                break;
            case "000106":
                room="6";
                break;
            case "000107":
                room="7";
                break;
            case "000108":
                room="8";
                break;
            case "000109":
                room="9";
                break;
            case "000110":
                room="10";
                break;
            case "000111":
                room="11";
                break;
            case "000112":
                room="12";
                break;
            case "000203":
                room="图东环";
                break;
            case "000204":
                room="图西环";
                break;
        }
        return room;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
