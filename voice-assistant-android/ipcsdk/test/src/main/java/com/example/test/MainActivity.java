package com.example.test;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.voyah.ai.sdk.DhSpeechSDK;
import com.voyah.ai.sdk.bean.DhDirection;
import com.voyah.ai.sdk.bean.UserVprInfo;
import com.voyah.ai.sdk.bean.VprResult;
import com.voyah.ai.sdk.listener.IVAReadyListener;
import com.voyah.ai.sdk.listener.IVAStateListener;
import com.voyah.ai.sdk.listener.IVprResultListener;
import com.voyah.ai.sdk.manager.DialogueManager;
import com.voyah.ai.sdk.manager.VprManager;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "xyj_test";
    String vprId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DhSpeechSDK.initialize(getApplicationContext(), new IVAReadyListener() {
            @Override
            public void onSpeechReady() {
                Log.d(TAG, "onSpeechReady() called");
                // 添加状态监听
                DialogueManager.setVAStateListener(new IVAStateListener() {

                    @Override
                    public void onState(String state) {
                        Log.d(TAG, "onState() called with: state = [" + state + "]");
                    }
                });
            }
        });

        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 开始注册声纹（如果是修改需要使用带vprId的接口）
                vprId = VprManager.startRegisterVpr(DhDirection.FRONT_LEFT, new IVprResultListener() {

                    // 声纹注册过程中结果回调
                    @Override
                    public void onVprResult(VprResult vprResult) {
                        Log.d(TAG, "onVprResult() called with: vprResult = [" + vprResult + "]");
                    }
                });
                Toast.makeText(getApplicationContext(), "startRegisterVpr", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 录制1条声纹 （重复调用3次）
                VprManager.startRecordingVpr(vprId, "打开车窗");
                Toast.makeText(getApplicationContext(), "startRecordingVpr", Toast.LENGTH_SHORT).show();
            }
        });
        //测试播报
        findViewById(R.id.btn3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 停止录制1条声纹
                VprManager.stopRecordingVpr(vprId, "打开车窗", 0);
                Toast.makeText(getApplicationContext(), "stopRecordingVpr", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.btn4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 保存用户信息
                UserVprInfo userVprInfo = VprManager.getUserVprInfo(vprId);
                if (userVprInfo != null) {
                    userVprInfo.name = "xyj";
                    userVprInfo.age = 40;
                    userVprInfo.isFemale = false;
                }
                VprManager.saveUserVprInfo(userVprInfo);
                Toast.makeText(getApplicationContext(), "saveUserVprInfo", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btn5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 查询用户已注册的所有声纹
                List<UserVprInfo> registeredVprList = VprManager.getRegisteredVprList();
                if (registeredVprList != null) {
                    for (UserVprInfo info : registeredVprList) {
                        Log.d(TAG, "info:" + info);
                    }
                }
                Toast.makeText(getApplicationContext(), "getRegisteredVprList", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btn6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 结束声纹注册
                VprManager.stopRegisterVpr(vprId, 0);
                Toast.makeText(getApplicationContext(), "stopRegisterVpr", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean isAllNetworkEnabled() {
        boolean enable = false;
        try {
            Uri uri = Uri.parse("content://com.voyah.ai.voice.export/settings/all_network");
            Cursor cursor = getApplicationContext().getContentResolver().query(uri, null, null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        enable = cursor.getInt(0) == 1;
                        Log.d(TAG, "isAllNetworkEnabled enable = [" + enable + "]");
                        break;
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return enable;
    }
}