package com.xinqi.ihandwh.ConfigCenter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.umeng.message.UmengRegistrar;
import com.xinqi.ihandwh.R;

/**
 * Created by presisco on 2015/11/28.
 */
public class DeviceTokenActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_token_dialog);
        ((EditText)findViewById(R.id.deviceTokenEditText)).setText(UmengRegistrar.getRegistrationId(this));
        findViewById(R.id.deviceTokenReturnBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
