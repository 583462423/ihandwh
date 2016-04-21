package com.xinqi.ihandwh.HttpService;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.xinqi.ihandwh.R;

/**
 * Created by Qking on 2016/4/15.
 * 每次调用网络任务都要检查网络，所以将检查网络的任务封装成类，简化书写
 */
public class CheckWork {
    Context mContext;


    boolean flag;   //表示当前网络是否可用
    int nettype;
    public CheckWork(Context context){
        mContext = context;
    }


    public boolean getFlag()
    {
        return flag;
    }


    public boolean checkNetWork()
    {
        ConnectivityManager manager = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if(networkInfo == null || !networkInfo.isAvailable())
        {
            setNetWork();
            return flag = false;
        }
        return flag = true;
    }

    public void setNetWork()
    {

        final WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
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
                    mContext.startActivity(intent);
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

}
