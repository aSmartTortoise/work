package com.voyah.h37z;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.voyah.viewcmd.VoiceViewCmdUtils;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        // 告知可见SDK，该activity层级同弹窗
        VoiceViewCmdUtils.setDialogActivity(this);

        findViewById(R.id.iv_close).setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("xyj", "TestActivity onResume() called, hashCode:" + hashCode());
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("xyj", "TestActivity onStop() called, hashCode:" + hashCode());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("xyj", "TestActivity onPause() called, hashCode:" + hashCode());
    }
}
