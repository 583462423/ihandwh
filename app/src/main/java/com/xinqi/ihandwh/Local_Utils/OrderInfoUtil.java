package com.xinqi.ihandwh.Local_Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.xinqi.ihandwh.Model.OrderInfo;
//import com.xinqi.ihandwh.R;
import com.xinqi.ihandwh.R;

/**
 * Created by syd on 2015/11/18.
 * 处理历史预约信息工具
 */
public class OrderInfoUtil {
    private static final String ROOM="room";
    private static final String SEATNUM="seatnum";
    private static final String DATE="date";
    private Context context;
    private  SharedPreferences sharedPreferences;
    public  OrderInfoUtil(Context context){
        this.context=context;
        sharedPreferences=context.getSharedPreferences(context.getResources().getString(R.string.app_name),Context.MODE_PRIVATE);
    }
    /**
     * 更新预约座位数据
     * @param room
     * @param seatNum
     * @param date
     * */
    public void refreshOrderInfo(String room,String seatNum,String date){
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(ROOM,room);
        editor.putString(SEATNUM,seatNum);
        editor.putString(DATE,date);
        editor.commit();
    }
    /**
     * 获取预约信息,每次打开程序，都要联网更新预约历史
     *
     * */
    public OrderInfo getOrderInfo(){
        OrderInfo orderInfo=new OrderInfo();
        orderInfo.setDate(sharedPreferences.getString(ROOM,"00"));
        orderInfo.setSeatNum(sharedPreferences.getString(SEATNUM,"000"));
        orderInfo.setDate(sharedPreferences.getString(DATE,"2008/08/08"));
        return orderInfo;

    }
}
