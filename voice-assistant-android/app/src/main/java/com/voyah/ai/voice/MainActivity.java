package com.voyah.ai.voice;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.voyah.ai.common.utils.LogUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        LogUtils.i("MainActivity", "onCreate");
        startVRService();
    }

    private void startVRService() {
        //todo:语音服务启动临时放在这里
        VRService.startService(this);
        finish();
    }
}