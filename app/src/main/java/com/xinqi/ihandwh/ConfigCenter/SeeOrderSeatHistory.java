package com.xinqi.ihandwh.ConfigCenter;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.xinqi.ihandwh.Adapters.MyOrderHistoryListViewAdapter;
//import com.xinqi.ihandwh.R;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.xinqi.ihandwh.R;

/**
 * Created by syd on 2015/11/20.
 */
public class SeeOrderSeatHistory extends AppCompatActivity {
    private Button cancelOrder;
    private ActionBar actionBar;
    private ListView listView;
    private TextView nonorderHistorytv;
//    String cancelresult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar=getSupportActionBar();
        setContentView(R.layout.orderhistorylayout);
        actionBar.setTitle("预约记录");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.psdback1);

        View v= LayoutInflater.from(this).inflate(R.layout.orderhistorylistviewitemview,null);
        nonorderHistorytv= (TextView) findViewById(R.id.nonorderhistory);
        /*cancelOrder= (Button) v.findViewById(R.id.cancel_order);
        //取消预约
        cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("bac","cancel");
              cancelOrder();

            }
        });
*/
        PushAgent.getInstance(this).onAppStart();
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
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        listView= (ListView) findViewById(R.id.orderhistorylistview);
        MyOrderHistoryListViewAdapter m=new MyOrderHistoryListViewAdapter(this,nonorderHistorytv);
        listView.setAdapter(m);
    }



}
