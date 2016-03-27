package com.xinqi.ihandwh.OrderSeats;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.xinqi.ihandwh.ConfigCenter.LoginActivity;
import com.xinqi.ihandwh.HttpService.OrderSeatService.OrderSeatService;
import com.xinqi.ihandwh.Local_Utils.FloorName2ID;
import com.xinqi.ihandwh.Local_Utils.UserinfoUtils;
import com.xinqi.ihandwh.Model.SeatInfo;
//import com.xinqi.ihandwh.R;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.xinqi.ihandwh.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by syd on 2015/11/13.
 */
public class SelectSeatActivity extends AppCompatActivity implements View.OnClickListener{
    private ActionBar actionBar;
    /*//楼层数量
    public static final int FLOORS=12;*/
    //座位显示列数
    private static final Integer COLUMN_COUNT=5;
    //预约日期格式
    private static final String DATE_FORMAT="yyyy/MM/dd";
    public static final String ORDERMODE ="ordermode" ;
    public static final String ORDER_SITNUM ="order_sitNum";
    public static final String ORDER_ROOM ="order_room";
    public static final String ORDER_DATE ="order_date";
    public static  String room;
    public static  String sitNum;
    //存储日期
    String mBookDate;


    TextView mBookSeatDate;
    TextView nofloorselectedtv;
    int selectfloor_pos;
    String mSelectedFloorName;

    //Spinner下拉选项
    TextView spinnerTitle;
    //自定义Spinner下拉列表
    boolean isLoadFloorsComplete=false;
    boolean isListOpened=false;
    CardView mCustomSpinnerList;
    GridView mSelectFloorGrid;
    TextView mSelectRandomSeatText;
    CustomSpinnerListAdaper mCustomSpinnerListAdapter;
    //楼层信息数组
    private List<String> floor_item=new ArrayList<>();

    private RecyclerView mSeatRecyclerView;
    private SeatInfoAdapter mSeatInfoAdapter;
    private RecyclerView.LayoutManager mRecyclerViewLayoutManager;
    SeatInfo[] mSeatsInfoDataSet;

    private boolean randomOrder=false;
    Intent intent;
    private TextView mNoSeatWarn;
    private ProgressBar progressBar;
    private TextView refreshtip;
    private String mRefreshTipLoadingSeats;
    private String mRefreshTipLoadingFloors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_seat);

        setupActionBar();
        setupStatusIndicator();
        setupBookDateTip();
        setupSpinner();
        setupSeatsUI();
        setDisplayRandomHind();

        intent=new Intent(SelectSeatActivity.this, Order_Seat_Process.class);

        findViewById(R.id.btn_order_seats).setOnClickListener(this);

        //有待修改，不确定目前的作用
        if (getIntent().getBooleanExtra("random",true)) {
            Log.d("SelectSeatActivity","LaunchRandom");
            //spinner选择不限
        } else {
            Log.d("SelectSeatActivity", "LaunchSelected");
            //spinner选择mSelectedFloorName
            mSelectedFloorName=getIntent().getStringExtra("floorname");
            spinnerTitle.setText(mSelectedFloorName);
            Log.d("SelectSeatActivity","PreSelected:"+ mSelectedFloorName);
        }

        //友盟信息
        PushAgent.getInstance(this).onAppStart();
    }

    /**
     * 设置ActionBar内容
     */
    public void setupActionBar(){
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("查座预约");
        actionBar.setHomeAsUpIndicator(R.drawable.psdback1);
        actionBar.show();
    }

    /**
     * 设置选择楼层Spinner
     */
    public void setupSpinner(){
        spinnerTitle = (TextView) findViewById(R.id.floorSpinnerTitle);
        mCustomSpinnerList=(CardView)findViewById(R.id.customSpinnerListContainer);
        mSelectFloorGrid=(GridView)findViewById(R.id.gridView);
        mSelectRandomSeatText=(TextView)findViewById(R.id.textViewBottom);

        findViewById(R.id.floorSpinner).setOnClickListener(this);
        spinnerTitle.setText(getResources().getText(R.string.text_floor_spinner_unavailable));

        mCustomSpinnerListAdapter=new CustomSpinnerListAdaper(this,floor_item);
        mSelectFloorGrid.setAdapter(mCustomSpinnerListAdapter);
        mSelectFloorGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedFloorName = floor_item.get(position + 1);
                spinnerTitle.setText(mSelectedFloorName);
                mCustomSpinnerList.setVisibility(View.GONE);
                selectfloor_pos = position + 1;
                mSeatInfoAdapter.clearSelection();
                randomOrder = false;
                isListOpened = false;
                new GetAvailableSeatsTask().execute();
            }
        });
        mSelectRandomSeatText.setText(getResources().getText(R.string.text_random_seat));
        mSelectRandomSeatText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectedFloorName = floor_item.get(0);
                spinnerTitle.setText(mSelectedFloorName);
                selectfloor_pos = 0;
                mCustomSpinnerList.setVisibility(View.GONE);
                isListOpened = false;
                hideStatusIndicator();
                nofloorselectedtv.setVisibility(View.VISIBLE);
                randomOrder = true;
            }
        });
        mCustomSpinnerList.setVisibility(View.GONE);
        new GetAvailableFloorsTask().execute();
    }

    /**
     * 选择楼层Spinner响应
     */
    public void onSpinner(){
        if(isLoadFloorsComplete){
            mNoSeatWarn.setVisibility(View.GONE);
            mCustomSpinnerList.setVisibility(View.VISIBLE);
            mSeatRecyclerView.setVisibility(View.INVISIBLE);
            isListOpened=true;
        }else{
            Toast.makeText(SelectSeatActivity.this,mRefreshTipLoadingFloors,Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 设置滚动条与提示文字
     */
    public void setupStatusIndicator(){
        nofloorselectedtv = (TextView) findViewById(R.id.noselectFloortv);
        progressBar= (ProgressBar) findViewById(R.id.refreshperroompro);
        refreshtip = (TextView) findViewById(R.id.roomrefreshtips);
        refreshtip.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        mRefreshTipLoadingSeats =getResources().getString(R.string.loading_available_seats_tip);
        mRefreshTipLoadingFloors=getResources().getString(R.string.loading_available_floors_tip);
        refreshtip.setText(mRefreshTipLoadingFloors);
        mNoSeatWarn=(TextView)findViewById(R.id.noSeatTextView);
        mNoSeatWarn.setVisibility(View.INVISIBLE);
    }

    /**
     * 显示状态提示
     * @param hint 提示的内容
     */
    public void showStatusIndicator(String hint){
            refreshtip.setText(hint);
            refreshtip.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏状态提示
     */
    public void hideStatusIndicator(){
        refreshtip.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    /**
     * 获取预约时间并设置提示信息
     */
    private void setupBookDateTip() {
        SimpleDateFormat converter=new SimpleDateFormat(DATE_FORMAT);
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        mBookDate=converter.format(calendar.getTime());
        Log.d("setupBookDateTip()", mBookDate);
        mBookSeatDate = (TextView) findViewById(R.id.bookDateTextView);
        mBookSeatDate.setText(mBookDate);
    }

    /**
     * 设置选择座位界面
     */
    private void setupSeatsUI(){
        mSeatRecyclerView = (RecyclerView) findViewById(R.id.seatInfoRecyclerView);
        mRecyclerViewLayoutManager = new GridLayoutManager(this,COLUMN_COUNT);
        mSeatRecyclerView.setLayoutManager(mRecyclerViewLayoutManager);
        mSeatInfoAdapter = new SeatInfoAdapter(new SeatInfo[0],this);
        mSeatInfoAdapter.setPickColors(getResources().getColor(R.color.default_background_color),
                getResources().getColor(R.color.selected_background_color));
        mSeatRecyclerView.setAdapter(mSeatInfoAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 完成刷新座位信息附加调用
     */
    private void onRefreshComplete() {
        if (mSeatsInfoDataSet ==null|| mSeatsInfoDataSet.length < 1) {
            mNoSeatWarn.setVisibility(View.VISIBLE);
            mSeatRecyclerView.setVisibility(View.INVISIBLE);
        } else {
            mNoSeatWarn.setVisibility(View.INVISIBLE);
            mSeatRecyclerView.setVisibility(View.VISIBLE);
            mSeatInfoAdapter.updateDataSet(mSeatsInfoDataSet);
            mSeatInfoAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 点击预约按钮后的具体响应操作
     */
    public void startBookProcess(){
        if(isListOpened)
            return;
        UserinfoUtils userinfoUtils = new UserinfoUtils(SelectSeatActivity.this);
        //判断是否登陆
        if (userinfoUtils.get_Login_Status()) {
            if (randomOrder) {
                // TODO: 2015/11/18 一键预约
                Log.i("bacground", "on key order");
                //0代表一键预约
                intent.putExtra(SelectSeatActivity.ORDERMODE, 0);
                startActivity(intent);

            } else {
                if (mSeatInfoAdapter.getSelectedSeat() == null) {
                    Toast.makeText(SelectSeatActivity.this, "请选择座位！", Toast.LENGTH_SHORT).show();
                } else {
                    intent.putExtra(SelectSeatActivity.ORDERMODE, 1);
                    intent.putExtra(SelectSeatActivity.ORDER_ROOM, FloorName2ID.getID(mSelectedFloorName));
                    intent.putExtra(SelectSeatActivity.ORDER_SITNUM, mSeatInfoAdapter.getSelectedSeat().id);
                    intent.putExtra(SelectSeatActivity.ORDER_DATE, mBookDate);
                    startActivity(intent);
                    Log.d("Start new seat order", "specific order" + FloorName2ID.getID(mSelectedFloorName) + "=="
                            + mSeatInfoAdapter.getSelectedSeat().id + "--"
                            + mBookDate);
                }
            }
        } else {
            Toast.makeText(SelectSeatActivity.this, "请先登录！", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SelectSeatActivity.this, LoginActivity.class));
        }
    }

    /**
     * 设置是否显示随即预约提醒
     */
    public void setDisplayRandomHind(){
        if(randomOrder) {
            nofloorselectedtv.setVisibility(View.VISIBLE);
        }else{
            nofloorselectedtv.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_order_seats:startBookProcess();break;
            case R.id.floorSpinner:onSpinner();break;
        }
    }

    /**
     * 从服务器获取指定楼层的座位预约信息
     */
    private class GetAvailableSeatsTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
            try {
                //判断是否登陆
                List<String> seatRaw;
                UserinfoUtils userinfoUtils=new UserinfoUtils(SelectSeatActivity.this);
                if (userinfoUtils.get_Login_Status()){
                OrderSeatService.testUserInfoIsTrue(userinfoUtils.get_LastId(), userinfoUtils.get_LastPassword());
                seatRaw=OrderSeatService.getYuYueInfo(FloorName2ID.getID(mSelectedFloorName), mBookDate);
                    Log.i("bac","获取房间信息："+seatRaw);
                }else {
                OrderSeatService.testUserInfoIsTrue("201100800169","011796");
                seatRaw=OrderSeatService.getYuYueInfo(FloorName2ID.getID(mSelectedFloorName),mBookDate);
                }
                //System.out.println(userinfoUtils.get_LastId()+"==="+userinfoUtils.get_LastPassword());
                mSeatsInfoDataSet =new SeatInfo[seatRaw.size()];
                for (int j=0;j<seatRaw.size();j++){
                    mSeatsInfoDataSet[j]=new SeatInfo();
                    mSeatsInfoDataSet[j].id=seatRaw.get(j);
                }
                Log.d("SelectSeatActivity","SeatInfoFetched!Total Count:"+seatRaw.size());
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Return a new random list of cheeses
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            // Tell the Fragment that the refresh has completed
            hideStatusIndicator();
            onRefreshComplete();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setDisplayRandomHind();
            showStatusIndicator(mRefreshTipLoadingSeats);
        }
    }

    /**
     * 从服务器获取可预约楼层信息
     */
    private class GetAvailableFloorsTask extends AsyncTask<Void,Void,List<String>>{
        @Override
        protected void onPostExecute(List<String> s) {
            super.onPostExecute(s);
            floor_item=s;
            isLoadFloorsComplete=true;
            mSelectedFloorName=floor_item.get(0);
            spinnerTitle.setText(mSelectedFloorName);
            nofloorselectedtv.setVisibility(View.VISIBLE);
            hideStatusIndicator();
            mCustomSpinnerListAdapter.updateDataset(floor_item.subList(1,floor_item.size()));
            mCustomSpinnerListAdapter.notifyDataSetChanged();
        }

        @Override
        protected List<String> doInBackground(Void... params) {
            List<String> result=FloorName2ID.getAllFloors();
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mNoSeatWarn.setVisibility(View.GONE);
            showStatusIndicator(mRefreshTipLoadingFloors);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
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

