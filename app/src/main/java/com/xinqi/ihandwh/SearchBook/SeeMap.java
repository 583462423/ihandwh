package com.xinqi.ihandwh.SearchBook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.xinqi.ihandwh.R;

/**
 * Created by syd on 2015/11/22.
 */
public class SeeMap extends AppCompatActivity {
    private ImageView map;
    private ActionBar actionBar;
    private TextView tvbookmapinfo,nonomaptv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar=getSupportActionBar();
        setContentView(R.layout.see_map_layout);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("地图");
        actionBar.setHomeAsUpIndicator(R.drawable.psdback1);
        map= (ImageView) findViewById(R.id.imgshowmap);
        tvbookmapinfo= (TextView) findViewById(R.id.tvmapbookinfo);
        nonomaptv= (TextView) findViewById(R.id.nonemap);
        Intent intent=getIntent();
        String floor=intent.getStringExtra("floor");
        Log.i("sxj","ufg"+floor);
        String bookname=intent.getStringExtra("bookname");
        String boomcode=intent.getStringExtra("bookcode");
        tvbookmapinfo.setText(bookname+"\n"+boomcode);
        showMap(floor);
        Log.i("bac", floor);
        PushAgent.getInstance(this).onAppStart();
    }
    private void showMap(String floor) {
        switch (floor){
            case "3楼":
                map.setImageResource(R.drawable._3floor);
                break;
            case "4楼":
                map.setImageResource(R.drawable._4floor);
                break;
            case "5楼":
                map.setImageResource(R.drawable._5floor);
                break;
            case "6楼":
                map.setImageResource(R.drawable._6floor);
                break;
            case "7楼":
                map.setImageResource(R.drawable._7floor);
                break;
            case "8楼":
                map.setImageResource(R.drawable._8floor);
                break;
            case "9楼":
                map.setImageResource(R.drawable._9floor);
                break;
            case "10楼":
                map.setImageResource(R.drawable._10floor);
                break;
            case "11楼":
                map.setImageResource(R.drawable._11floor);
                break;
            case "12楼":
                map.setImageResource(R.drawable._12floor);
                break;
            default:
                map.setVisibility(View.GONE);
                nonomaptv.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            onBackPressed();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
