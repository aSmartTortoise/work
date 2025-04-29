package com.example.test;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import com.voyah.ai.sdk.DhSpeechSDK;
import com.voyah.ai.sdk.bean.DhDialect;
import com.voyah.ai.sdk.bean.DhDirection;
import com.voyah.ai.sdk.bean.DhSpeaker;
import com.voyah.ai.sdk.bean.DhSwitch;
import com.voyah.ai.sdk.listener.ITtsPlayListener;
import com.voyah.ai.sdk.listener.IVAReadyListener;
import com.voyah.ai.sdk.listener.SimpleTtsPlayListener;
import com.voyah.ai.sdk.manager.SettingManager;
import com.voyah.ai.sdk.manager.TTSManager;


public class SettingActivity extends AppCompatActivity {

    private static final String TAG = "xyj_test";

    private ITtsPlayListener ttsListener = new SimpleTtsPlayListener() {
        @Override
        public void onPlayBeginning(String text) {
            Log.d(TAG, "onPlayBeginning() called with: text = [" + text + "]");
        }

        @Override
        public void onPlayEnd(String text, int reason) {
            Log.d(TAG, "onPlayEnd() called with: text = [" + text + "], reason = [" + reason + "]");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main_system);

        DhSpeechSDK.initialize(getApplicationContext(), new IVAReadyListener() {
            @Override
            public void onSpeechReady() {
                Log.d(TAG, "onSpeechReady() called");
            }
        });

        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //测试预置音频播报(带发音人)
        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TTSManager.speak("您好，我是岚图。", ttsListener, DhSpeaker.SPEAKER_SICHUAN);
            }
        });
        //测试开始播报(使用默认发声人)
        findViewById(R.id.btn3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TTSManager.speak("今天的天气怎么样, 我准备去深圳大学玩一天，如果下雨就不想去了", ttsListener);
            }
        });
        //测试停止所有播报
        findViewById(R.id.btn4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TTSManager.shutUp();
            }
        });
        //测试音区设定
        findViewById(R.id.btn5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开启前右
                SettingManager.setMicZoneEnable(DhDirection.FRONT_RIGHT, true);
            }
        });
        //测试取消音区设定
        findViewById(R.id.btn6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingManager.setMicZoneEnable(DhDirection.FRONT_RIGHT, false);
            }
        });

        //开启语音唤醒开关
        findViewById(R.id.btn7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingManager.enableSwitch(DhSwitch.MainWakeup, true);
            }
        });

        //关闭语音唤醒助手
        findViewById(R.id.btn8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingManager.enableSwitch(DhSwitch.MainWakeup, false);
            }
        });
        //开启全局唤醒词
        findViewById(R.id.btn9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingManager.enableSwitch(DhSwitch.FreeWakeup, true);
            }
        });

        //关闭全局唤醒词
        findViewById(R.id.btn10).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingManager.enableSwitch(DhSwitch.FreeWakeup, false);
            }
        });

        // 切换方言
        findViewById(R.id.btn11).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DhDialect currentDialect = SettingManager.getCurrentDialect();
                Log.d(TAG, "currentDialect:" + currentDialect);
                SettingManager.setDialect(DhDialect.CANTONESE);
            }
        });

        //切换回普通话
        findViewById(R.id.btn12).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingManager.setDialect(DhDialect.OFFICIAL_1);
            }
        });

        Handler handler = new Handler(Looper.getMainLooper());
        // 注册数据库的监听，对应的是特定的Uri
        getContentResolver().registerContentObserver(Uri.parse("content://com.voyah.ai.voice.export/settings/dialect"),
                true, new ContentObserver(handler) {
                    @Override
                    public void onChange(boolean selfChange) {
                        super.onChange(selfChange);
                        Log.d(TAG, "onChange() called with: selfChange = [" + selfChange + "]");
                        DhDialect dialect = SettingManager.getCurrentDialect();
                        Log.d(TAG, "dialect:" + dialect);
                    }
                });
    }

}