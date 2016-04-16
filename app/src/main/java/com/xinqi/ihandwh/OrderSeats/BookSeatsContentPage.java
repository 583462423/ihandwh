package com.xinqi.ihandwh.OrderSeats;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.xinqi.ihandwh.ConfigCenter.LoginActivity;
import com.xinqi.ihandwh.HttpService.OrderSeatService.OrderSeatService;
import com.xinqi.ihandwh.Local_Utils.UserinfoUtils;
import com.xinqi.ihandwh.Model.FloorInfo;
import com.umeng.analytics.MobclickAgent;
import com.xinqi.ihandwh.R;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

//import com.xinqi.ihandwh.R;

/**
 * Created by Presisco on 2015/9/28.
 */
public class BookSeatsContentPage extends Fragment{
    private boolean seatLoader = false;
    public static boolean isfirstin=true;
    private static final String LOG_TAG = BookSeatsContentPage.class.getSimpleName();
    private static final Integer COLUMN_COUNT=3;
    private static  List<FloorInfo> floorInfos;
    private TextView tvrefresh;
    private SharedPreferences sharedPreferences;
    private boolean hasknow;//用户是否知道下拉刷新
    int times=0;
    /* private SharedPreferences floorinfosshar;
    private SharedPreferences.Editor flooreditor;*/
    private SharedPreferences.Editor editor;
    /**
     * The {@link SwipeRefreshLayout} that detects swipe gestures and
     * triggers callbacks in the app.
     */
    private SwipeRefreshLayout mSwipeRefreshLayout;

    /**
     * The {@link RecyclerView} that displays the content that should be refreshed.
     */
    private TextView textView;
    private RecyclerView mFloorInfoRecyclerView;
    private FloorInfoAdapter mFloorInfoAdapter;
    private RecyclerView.LayoutManager mRecyclerViewLayoutManager;
    private FloorInfo[] mDataSet;
    private FloorInfoHelper floorInfoHelper;
    private int floorSize;

    private DummyBackgroundTask taskHandle;
    private WifiManager wifiManager;
    @Override
    public void onDestroy() {
        super.onDestroy();
        //isfirstin=true;
    }


    /**
     * return a new instance of the fragment
     */
    public static Fragment newInstance() {

        BookSeatsContentPage fragment = new BookSeatsContentPage();
        return fragment;
    }
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//
//    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        wifiManager= (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        floorInfoHelper=new FloorInfoHelper(getActivity());
        Log.i("bac","onCreateView");
        sharedPreferences =getActivity().getSharedPreferences(getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        editor= sharedPreferences.edit();
        isfirstin=sharedPreferences.getBoolean("isfirstin",true);
        hasknow= sharedPreferences.getBoolean("knowpull",false);
        View view=inflater.inflate(R.layout.book_seats_content_page, container, false);
        textView = (TextView) view.findViewById(R.id.seatInfoLoader);
//        if (!hasknow)tvrefresh.setVisibility(View.VISIBLE);
        Log.d("BookSeatsCP", "onCreateView()");
        //Test Data Gen
        Log.i("bac",isfirstin+"1111111111");
        //isfirstin=sharedPreferences.getBoolean("isfirstin",true);
        if (isfirstin) {
            Log.i("bac", "firstin");
//            genTestData();
            genTestData01();
            if (!checkNetworkState()){//网络不可用
                setNetwork();

            }else {
                seatLoading(textView);
                seatLoader = false;
                initiateRefresh();
            }
        }else {
            genTestData01();
        }
        /*Log.d("BookSeatsCP", "mSeatsInfoDataSet.length:" + mSeatsInfoDataSet.length);*/
        // Retrieve the SwipeRefreshLayout and ListView instances
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.curFloorsSwipeRefresh);
        // BEGIN_INCLUDE (change_colors)
        // Set the color scheme of the SwipeRefreshLayout by providing 4 color resource ids
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.swipe_color_1, R.color.swipe_color_2,
                R.color.swipe_color_3, R.color.swipe_color_4);
        // END_INCLUDE (change_colors)

        mFloorInfoRecyclerView = (RecyclerView) view.findViewById(R.id.curFloorsRecyclerView);
        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mRecyclerViewLayoutManager = new GridLayoutManager(getActivity(),COLUMN_COUNT);
        mFloorInfoRecyclerView.setLayoutManager(mRecyclerViewLayoutManager);
        mFloorInfoAdapter = new FloorInfoAdapter(mDataSet,getContext());
        // Set CustomAdapter as the adapter for RecyclerView.
        mFloorInfoRecyclerView.setAdapter(mFloorInfoAdapter);
        // END_INCLUDE(initializeRecyclerView)
        //设置预定按钮监听
        Button randomBook=(Button)view.findViewById(R.id.bookRandomSeatButton);
        randomBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //先判断是否已经登陆
                UserinfoUtils userinfoUtils=new UserinfoUtils(getActivity());
                if (userinfoUtils.get_Login_Status()){
                Intent newIntent=new Intent(getContext(),SelectSeatActivity.class);
                newIntent.putExtra(SelectSeatActivity.ORDERMODE,0);
                startActivity(newIntent);
                isfirstin=false;
                }else {
                    Intent intent=new Intent(new Intent(getActivity(), LoginActivity.class));
                    Toast.makeText(getActivity(),"请先登录！",Toast.LENGTH_SHORT).show();
                    intent.putExtra("from",-1);
                    startActivity(intent);
                    isfirstin=false;
                }
            }
        });
        return view;
    }

    private void genTestData01() {
        List<FloorInfo> floorInfos=floorInfoHelper.quryFloorinfo();
        floorSize = floorInfos.size();
        Log.w("-----------------",""+floorSize);
        if (floorSize==0){
            genTestData();
            Log.i("bac","000000");
        }else {
            Log.i("bac","000000");
            this.mDataSet = new FloorInfo[12];
            genTestData();
                for (int i = 0; i < floorSize; i++) {
                    mDataSet[i] = new FloorInfo();
                    mDataSet[i].layer = floorInfos.get(floorSize-1 - i).layer;
                    mDataSet[i].total = floorInfos.get(floorSize-1 - i).total;
                    mDataSet[i].rest = floorInfos.get(floorSize-1 - i).rest;
               /* mSeatsInfoDataSet[i].total = 100;
                mSeatsInfoDataSet[i].rest = 100;*/
                }
            }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("BookSeatsCP", "onViewCreated()");
        // BEGIN_INCLUDE (setup_refreshlistener)
        /**
         * Implement {@link SwipeRefreshLayout.OnRefreshListener}. When users do the "swipe to
         * refresh" gesture, SwipeRefreshLayout invokes
         * {@link SwipeRefreshLayout.OnRefreshListener#onRefresh onRefresh()}. In
         * {@link SwipeRefreshLayout.OnRefreshListener#onRefresh onRefresh()}, call a method that
         * refreshes the content. Call the same method in response to the Refresh action from the
         * action bar.
         */

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i("bac", "onRefresh called from SwipeRefreshLayout");
               /* editor.putBoolean("knowpull", true);
                editor.commit();*/
                /*if (tvrefresh.getVisibility() == View.VISIBLE)
                    tvrefresh.setVisibility(View.INVISIBLE);*/
//                initiateRefresh();
                if (!checkNetworkState()){//网络不可用
                    setNetwork();
                }else {
                   initiateRefresh();
                }
            }
        });
        // END_INCLUDE (setup_refreshlistener)
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("BookSeat"); //统计页面
        if(seatLoader)seatLoaderDone(textView);
        else seatLoading(textView);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("BookSeat");
//        taskHandle.cancel(true);
    }

    private void seatLoaderDone(TextView textView)
    {
        textView.setText("座位信息加载完成");
        textView.setTextColor(getResources().getColor(R.color.black));
    }

    private void seatLoading(TextView textView)
    {
        textView.setTextColor(getResources().getColor(R.color.colorAccent));
        textView.setText("正在加载座位信息...");
    }
    private boolean checkNetworkState() {
        boolean flag = false;
        // 得到网络连接信息
        ConnectivityManager manager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        // 去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null) {
            flag = manager.getActiveNetworkInfo().isAvailable();
        }
        return flag;
    }
    private int nettype=0;
    private void setNetwork() {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("网络不可用！");
//        builder.setMessage("网络不可用，如果继续，请先设置网络！");
        builder.setSingleChoiceItems(R.array.nettype, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                nettype=which;
            }
        });
        builder.setPositiveButton("打开", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (nettype==0){
                    wifiManager.setWifiEnabled(true);
                }else {
                    Intent intent = new Intent("android.settings.WIRELESS_SETTINGS");
                    startActivity(intent);
                }
                final Timer timer=new Timer();
                TimerTask timerTask=new TimerTask() {
                    @Override
                    public void run() {
                        times++;
                        if (checkNetworkState()){//开启网络成功
                            initiateRefresh();
                            Log.i("bac","等待成功");
                            timer.cancel();
                        }
                        if (times>=20){//最多等待10秒
                            timer.cancel();
                            Log.i("bac","等待失败");
                        }
                    }
                };
                timer.schedule(timerTask, 0, 500);//没500毫秒执行一次
//                initiateRefresh();
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // return;
                mSwipeRefreshLayout.setRefreshing(false);
                dialog.dismiss();
            }
        });
        builder.create();
        builder.show();
    }

    private void initiateRefresh() {

        /**
         * Execute the background task, which uses {@link AsyncTask} to load the data.
         */
        //***************************************************************************************
        //***************************************************************************************
        //提示开始加载
        taskHandle=new DummyBackgroundTask();
        taskHandle.execute();

        Log.i("bac", "initiateRefreshsssssssssssssssss");
    }
    // END_INCLUDE (initiate_refresh)

    // BEGIN_INCLUDE (refresh_complete)
    /**
     * When the AsyncTask finishes, it calls onRefreshComplete(), which updates the data in the
     * ListAdapter and turns off the progress bar.
     */
    private void onRefreshComplete() {
       // Log.i(LOG_TAG, "onRefreshComplete");
        //提示加载完成
        //***************************************************************************************
        //***************************************************************************************
        // Remove all items from the ListAdapter, and then replace them with the new items
       // mSeatsInfoDataSet=null;
        mFloorInfoAdapter.updateDataSet(mDataSet);
        mFloorInfoAdapter.notifyDataSetChanged();
        // Stop the refreshing indicator
        seatLoaderDone(textView);
        seatLoader = true;
        mSwipeRefreshLayout.setRefreshing(false);

    }
    // END_INCLUDE (refresh_complete)

    /**
     * Dummy {@link AsyncTask} which simulates a long running task to fetch new cheeses.
     */
    private class DummyBackgroundTask extends AsyncTask<Void, Void, Integer> {

        static final int TASK_DURATION = 3 * 1000; // 3 seconds

        @Override
        protected Integer doInBackground(Void... params) {
            OrderSeatService yuyueService=new OrderSeatService();
            // 获取当前日期+1
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
            Date beginDate = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(beginDate);
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
            String date = format.format(calendar.getTime());
            try {
                UserinfoUtils userinfoUtils=new UserinfoUtils(getActivity());
                if (userinfoUtils.get_Login_Status()){
                floorInfos=OrderSeatService.getFloorInfo(userinfoUtils.get_LastId(),userinfoUtils.get_LastPassword());
                }else {
                floorInfos=OrderSeatService.getFloorInfo("201100800169","011796");
                }
                Log.i("bac","isFirstin set false");
                editor.putBoolean("isfirstin",false);
                editor.commit();
                floorInfoHelper.deleteAllFloorInfo();
                mDataSet=new FloorInfo[floorInfos.size()];
               for (int j=0;j<floorInfos.size();j++){
                    mDataSet[j]=new FloorInfo();
                    mDataSet[j].total=floorInfos.get(j).total;
                    mDataSet[j].rest=floorInfos.get(j).rest;
                    mDataSet[j].layer=floorInfos.get(j).layer;
                    floorInfoHelper.insertFloorInfo(mDataSet[j].layer, mDataSet[j].total,mDataSet[j].rest);
                    //System.out.println(mSeatsInfoDataSet[j].total+"=="+mSeatsInfoDataSet[j].layer+"=="+mSeatsInfoDataSet[j].rest);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("bac","网络还未设置好！"+e.toString());
            }
            // Return a new random list of cheeses
            return 0;
        }
        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            // Tell the Fragment that the refresh has completed
            onRefreshComplete();
        }
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {

        }
    };
    //private static int i=0;

    private void genTestData() {
        this.mDataSet =new FloorInfo[12];
        if (isfirstin) {
            for (int i = 0; i < 12;i++) {
                mDataSet[i] = new FloorInfo();
                mDataSet[i].layer = i + " Fl";
                mDataSet[i].total = 100;
                mDataSet[i].rest = 100;
            }
        }
    }
}
