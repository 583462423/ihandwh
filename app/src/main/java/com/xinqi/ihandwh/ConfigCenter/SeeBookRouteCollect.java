package com.xinqi.ihandwh.ConfigCenter;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.xinqi.ihandwh.Adapters.MyBookRootListViewAdapter;
//import com.xinqi.ihandwh.R;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.xinqi.ihandwh.R;

/**
 * Created by syd on 2015/11/20.
 */
public class SeeBookRouteCollect extends AppCompatActivity {
    private ActionBar actionBar;
    private MyBookRootListViewAdapter myBookRootListViewAdapter;
    private ListView listView;
    private TextView nonroute;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar=getSupportActionBar();
        setContentView(R.layout.see_collected_book_route);
        actionBar.setTitle("借书收藏路线");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.psdback1);
        nonroute= (TextView) findViewById(R.id.nonroute);
        listView= (ListView) findViewById(R.id.seebookroutelistview);
        myBookRootListViewAdapter=new MyBookRootListViewAdapter(this,nonroute);
        listView.setAdapter(myBookRootListViewAdapter);
        PushAgent.getInstance(this).onAppStart();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            onBackPressed();
            finish();
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
