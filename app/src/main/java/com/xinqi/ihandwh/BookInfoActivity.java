package com.xinqi.ihandwh;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.xinqi.ihandwh.Fragment.RenewBookInfoFrag;
import com.xinqi.ihandwh.HttpService.CheckWork;

public class BookInfoActivity extends AppCompatActivity {

    Toolbar toolbar;
    Intent intent;
    CheckWork checkWork;

    public final static int RENEW = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);
        checkWork = new CheckWork(this);
        checkWork.checkNetWork();

        intent = getIntent();
        int which = intent.getIntExtra("which",1);
        switch(which)
        {
            case RENEW:
                toolbar = (Toolbar) findViewById(R.id.bookinfo_toolbar);
                toolbar.setTitle("当前借阅");
                setSupportActionBar(toolbar);
                checkWork.checkNetWork();
                if(checkWork.getFlag())
                {
                    RenewBookInfoFrag renewBookInfoFrag = new RenewBookInfoFrag();
                    FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.book_info_frame,renewBookInfoFrag);
                    transaction.commit();
                }
                break;
            default:break;
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
