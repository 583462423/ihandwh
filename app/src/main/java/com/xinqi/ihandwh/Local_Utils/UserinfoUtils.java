package com.xinqi.ihandwh.Local_Utils;

import android.content.Context;
import android.content.SharedPreferences;
//import com.xinqi.ihandwh.R;
import com.xinqi.ihandwh.R;

import java.util.HashMap;
import java.util.Map;
/**
 * Created by syd on 2015/11/15.
 */
public class UserinfoUtils {
    public static final String BE_ON="be_on";
    public static final String USERID="userid";
    public static final String USERPASSWORD="userpassword";
    public static final String HAS_ORDER_SEAT_HISTORY ="hasorderhistory";
    public static final String ORDER_SEAT_HISTORY_FLOOR ="orderhistoryfloor";
    public static final String ORDER_SEAT_HISTORY_ROOM ="orderhistoryroom";
    public static final String ORDER_SEAT_HISTORY_SEAT_NUM ="orderhistoryseatnumber";
    public static final String ORDER_SEAT_HISTORY_DATE="orderhistorydate";
    //本地信息存储
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public UserinfoUtils(Context context){
        sharedPreferences=context.getSharedPreferences(context.getResources().getString(R.string.app_name_eng), Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
    }
    //更新登陆状态
    public void refresh_Login_Status(boolean be_on){
        editor.putBoolean(BE_ON,be_on);
        editor.commit();
    }
    //获取登录状态
    public boolean get_Login_Status(){

        return sharedPreferences.getBoolean(BE_ON,false);

    }
    //存储当前用户名和密码
    public void save_CureentLogin_Info(String Id,String password){
        editor.putString(USERID,Id);
        editor.putString(USERPASSWORD,password);
        editor.commit();
    }
    //存储当前用户名和密码
    public void unsave_CureentLogin_Info(){
        editor.putString(USERID,null);
        editor.putString(USERPASSWORD,null);
        editor.commit();
    }
    //获取上一次登陆的用户账号
    public String get_LastId(){
        return sharedPreferences.getString(USERID,null);
    }
    //获取上次登录的用户密码
    public String get_LastPassword(){
        return sharedPreferences.getString(USERPASSWORD,null);
    }
    //更新预约历史
    public void refresh_Order_Hisrory(String floor,String room,String seatNum,String data){
        editor.putString(ORDER_SEAT_HISTORY_FLOOR,floor);
        editor.putString(ORDER_SEAT_HISTORY_ROOM,room);
        editor.putString(ORDER_SEAT_HISTORY_SEAT_NUM,seatNum);
        editor.putString(ORDER_SEAT_HISTORY_DATE,data);
        editor.putBoolean(HAS_ORDER_SEAT_HISTORY,true);
        editor.commit();
    }
    //判断是否有预约历史
    public boolean has_Order_History(){
        boolean b=sharedPreferences.getBoolean(HAS_ORDER_SEAT_HISTORY,false);
        return b;
    }
    //获取预约历史
    public Map<String,String> get_Order_History(){
            Map<String,String> OrderHistory=new HashMap<>();
            String floor=sharedPreferences.getString(ORDER_SEAT_HISTORY_FLOOR,null);
            OrderHistory.put(ORDER_SEAT_HISTORY_FLOOR,floor);
            String room=sharedPreferences.getString(ORDER_SEAT_HISTORY_ROOM,null);
            OrderHistory.put(ORDER_SEAT_HISTORY_ROOM,room);
            String sitNum=sharedPreferences.getString(ORDER_SEAT_HISTORY_SEAT_NUM,null);
            OrderHistory.put(ORDER_SEAT_HISTORY_SEAT_NUM,sitNum);
            return OrderHistory;
    }
}
