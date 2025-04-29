package com.voyah.plugin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.voyah.aiwindow.aidlbean.AIMessage;
import com.voyah.aiwindow.sdk.AIWindowSDK;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AIWindowSDK.initialize(getApplicationContext(), () -> Log.d("xyj_test", "onConnected"));

        findViewById(R.id.btn_send_message).setOnClickListener(v -> {
            Log.d("xyj_test", "pkgName:" + getPackageName() + ",clazz:" + CustomView.class.getName());
            AIMessage aiMessage = new AIMessage(getPackageName(), CustomView.class.getName(), "哈哈，我是从插件传送过来的数据");
            aiMessage.width = 1000;
            aiMessage.height = 800;
            AIWindowSDK.sendAIMessage(aiMessage, (state, msg) -> Log.d("xyj_test", "onResult() called with: state = [" + state + "], msg = [" + msg + "]"));
        });
    }
}