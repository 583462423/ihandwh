package com.xinqi.ihandwh.ConfigCenter;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

//import com.xinqi.ihandwh.R;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.xinqi.ihandwh.R;

/**
 * Created by syd on 2015/11/20.
 */
public class SeeAboutOur extends AppCompatActivity {
    private ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar=getSupportActionBar();
        setContentView(R.layout.about_our_layout);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.psdback1);

        actionBar.setTitle("关于我们");
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
