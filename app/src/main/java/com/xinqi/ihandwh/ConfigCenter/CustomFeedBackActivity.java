package com.xinqi.ihandwh.ConfigCenter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.SyncListener;
import com.umeng.fb.model.Conversation;
import com.umeng.fb.model.Reply;
import com.xinqi.ihandwh.R;

import java.util.List;

/**
 * Created by presisco on 2015/12/1.
 */
public class CustomFeedBackActivity extends AppCompatActivity {
    protected EditText mContactMethod;
    protected EditText mFeedBackContent;
    private android.support.v7.app.ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar=getSupportActionBar();
        setContentView(R.layout.see_collected_book_route);
        actionBar.setTitle("进行反馈");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.psdback1);
        setContentView(R.layout.activity_custom_feedback);
        mContactMethod=(EditText)findViewById(R.id.contactMethodEditText);
        mFeedBackContent=(EditText)findViewById(R.id.feedBackContentEditText);
    }

    public void onSubmit(View v){
        if (TextUtils.isEmpty(mFeedBackContent.getText())){
            Toast.makeText(this,"请填写您的宝贵意见",Toast.LENGTH_SHORT).show();
        }else {
            String contactMethod=mContactMethod.getText().toString().trim();
            String feedBackContent=mFeedBackContent.getText().toString().trim();

            FeedbackAgent agent=new FeedbackAgent(this);
            Conversation conversation=agent.getDefaultConversation();
            conversation.addUserReply("联系方式:"+contactMethod+
                    " 反馈内容:"+feedBackContent+
                    " 设备型号:"+android.os.Build.MODEL+
                    " 系统版本:"+android.os.Build.VERSION.RELEASE);
            conversation.sync(new SyncListener() {
            @Override
            public void onReceiveDevReply(List<Reply> list) {
            }
            @Override
            public void onSendUserReply(List<Reply> list) {
                Toast.makeText(CustomFeedBackActivity.this,"已发送反馈",Toast.LENGTH_SHORT).show();
            }
        });
    }
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
