package com.xinqi.ihandwh.Adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.xinqi.ihandwh.HttpService.OrderSeatService.OrderSeatService;
import com.xinqi.ihandwh.Local_Utils.UserinfoUtils;
import com.xinqi.ihandwh.Model.OrderInfo;
import com.xinqi.ihandwh.OrderSeats.OrderSeatHistoryHelper;
import com.xinqi.ihandwh.R;


import java.util.ArrayList;

/**
 * Created by syd on 2015/11/20.
 */
public class MyOrderHistoryListViewAdapter extends BaseAdapter {
    private Time currenttime;
    private String res="";
    private OrderSeatHistoryHelper orderSeatHistoryHelper;
    private Context context;
    private ArrayList<OrderInfo> orderInfos;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean leatestordercanceled;
    private ProgressDialog progressDialog;
    private TextView nonOrderhistorytv;
    public MyOrderHistoryListViewAdapter(Context context,TextView nonOrderhistorytv){
        this.context=context;
        this.nonOrderhistorytv=nonOrderhistorytv;
        Log.i("bac", "adapter");
        initItems();
        sharedPreferences=context.getSharedPreferences(context.getResources().getString(R.string.app_name),Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        Log.i("bac", "adapter");
        currenttime=new android.text.format.Time("GMT+8");
        //获取当前系统时间
        currenttime.setToNow();
    }

    /**
     * 从数据库读取预约历史数据
     * */
    private void initItems() {
       orderSeatHistoryHelper=new OrderSeatHistoryHelper(context);
        orderInfos=orderSeatHistoryHelper.quryOrderInfo();
        if (orderInfos.size()==0){
            nonOrderhistorytv.setVisibility(View.VISIBLE);
        }else {

            nonOrderhistorytv.setVisibility(View.GONE);
        }
        Log.i("bac","adapter");
        Log.i("bac",orderInfos.size()+"预约次数");
        notifyDataSetChanged();//通知更新listview显示
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return orderInfos.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return orderInfos.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //对加载listview item 作优化处理
        ViewHolder viewHolder;
        if (convertView==null){//第一次获取item布局
            convertView=LayoutInflater.from(context).inflate(R.layout.orderhistorylistviewitemview,null);
            viewHolder=new ViewHolder();
            viewHolder.setCancel((Button) convertView.findViewById(R.id.cancel_order));
            viewHolder.setTv_order_position((TextView) convertView.findViewById(R.id.tvorderposition));
            viewHolder.setTv_ordertime((TextView) convertView.findViewById(R.id.tvordertime));
            convertView.setTag(viewHolder);
        }else {//非第一次加载，直接利用tag获取布局
            viewHolder= (ViewHolder) convertView.getTag();
        }
        //按时间倒序显示
        String time=orderInfos.get(position).getDate();
        String pos=orderInfos.get(position).getRoom()+"楼"+orderInfos.get(position).getSeatNum()+"座";
        viewHolder.getTv_order_position().setText(pos);
        viewHolder.getTv_ordertime().setText(time);
        if (position==0){
            leatestordercanceled=sharedPreferences.getBoolean("leastcanceled",false);
            if (leatestordercanceled){//正常取消
            viewHolder.getCancel().setEnabled(false);
            viewHolder.getCancel().setText("已取消");
            }else if (currenttime.toMillis(true)>= orderInfos.get(position).getConfirmTime()){
                //当前时间大于最晚预约时间
                viewHolder.getCancel().setText("已确认");
                viewHolder.getCancel().setEnabled(false);
            }else {//可以取消
                viewHolder.getCancel().setText("取消");
                viewHolder.getCancel().setEnabled(true);
            }
            
            /*else
            viewHolder.getCancel().setEnabled(true);*/
            viewHolder.getCancel().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(context).setTitle("取消预约").setMessage("真的要取消预约吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                           /* builder=new AlertDialog.Builder(context).setTitle("取消预约").setMessage("正在取消预约...").setView(R.layout.alertcancelingview);
                            alertDialog=builder.create();
                           alertDialog.show();*/
                            progressDialog=new ProgressDialog(context);
                            progressDialog.setTitle("正在取消预约...");
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressDialog.show();
                            cancelOrder();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }
            });
        }else {
            viewHolder.getCancel().setEnabled(false);
            Log.i("bac",orderInfos.get(position).getRoom()+orderInfos.get(position).getHasCanceled());
            if (orderInfos.get(position).getHasCanceled().contains("canceled")){//之前正常取消
                viewHolder.getCancel().setText("已取消");
                Log.i("bac","已经取消");
            }else if (currenttime.toMillis(true)>orderInfos.get(position).getConfirmTime()){
                viewHolder.getCancel().setText("已确认");
            }
        }
        return convertView;
    }
private  class ViewHolder {
    private TextView tv_ordertime = null;
    private TextView tv_order_position = null;
    private Button cancel;

    public void setCancel(Button cancel) {
        this.cancel = cancel;
    }

    public Button getCancel() {
        return cancel;
    }

    public void setTv_order_position(TextView tv_order_position) {
        this.tv_order_position = tv_order_position;
    }

    public void setTv_ordertime(TextView tv_ordertime) {
        this.tv_ordertime = tv_ordertime;
    }

    public TextView getTv_order_position() {
        return tv_order_position;
    }

    public TextView getTv_ordertime() {
        return tv_ordertime;
    }
}
    private void cancelOrder() {

        new Thread(){
            @Override
            public void run() {
                UserinfoUtils userinfoUtils=new UserinfoUtils(context);
                try {
                    OrderSeatService.testUserInfoIsTrue(userinfoUtils.get_LastId(), userinfoUtils.get_LastPassword());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                res= OrderSeatService.Cancel(OrderSeatService.coreClient);
                Log.i("sxj","cancel返回"+res);
                Message message=new Message();
                message.what=1;
                message.obj=res;
                handler.sendMessage(message);
            }
        }.start();
    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.obj.toString().contains("success")){//取消成功
                res="成功取消预约!";
                editor.putBoolean("leastcanceled", true);
                editor.commit();
                //// TODO: 2015/11/24 数据库更新
                String date=orderInfos.get(0).getDate();
                String room=orderInfos.get(0).getRoom();
                String seatNum=orderInfos.get(0).getSeatNum();
                long  confirmTime=orderInfos.get(0).getConfirmTime();
                boolean b=orderSeatHistoryHelper.deleteLastOrderinfo();
                Log.i("bac","删除最新一个"+b);
                orderSeatHistoryHelper.insertOrderInfo(room,seatNum,date,confirmTime,"canceled");
                notifyDataSetInvalidated();
                showtip();
            }else if (msg.obj.toString().contains("empty")){
                res="您已经在别处取消！";
                editor.putBoolean("leastcanceled",true);
                editor.commit();
                String date=orderInfos.get(0).getDate();
                String room=orderInfos.get(0).getRoom();
                String seatNum=orderInfos.get(0).getSeatNum();
                long  confirmTime=orderInfos.get(0).getConfirmTime();
                //orderInfos.get(0).setHascanceled("canceled");
                boolean b=orderSeatHistoryHelper.deleteLastOrderinfo();
                Log.i("bac", "删除最新一个" + b);
                orderSeatHistoryHelper.insertOrderInfo(room, seatNum, date, confirmTime, "canceled");
                notifyDataSetInvalidated();
                showtip();
            }else {
                res="网络错误,请稍后重试！";
                showtip();
            }

        }
    };

    private void showtip() {
        progressDialog.dismiss();
        new AlertDialog.Builder(context).setTitle("取消预约").setMessage(res).setNegativeButton("返回", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }
}
