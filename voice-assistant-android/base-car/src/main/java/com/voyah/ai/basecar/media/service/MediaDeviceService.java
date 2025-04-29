package com.voyah.ai.basecar.media.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.ktcp.aiagent.device.aidl.IDeviceAudioEventAidl;
import com.ktcp.aiagent.device.aidl.IDeviceCallbackAidl;
import com.ktcp.aiagent.device.aidl.IDeviceVoiceServiceAidl;
import com.voice.sdk.tts.TtsReplyUtils;
import com.voyah.ai.basecar.media.utils.MediaHelper;
import com.voyah.ai.basecar.media.vedio.TencentVideoImpl;
import com.voyah.ai.common.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class MediaDeviceService extends Service {

    private static final String TAG = "MediaDeviceService";

    // 分配给接入方的AppId和SecretKey，请联系腾讯侧正式申请，设备方需要妥善保存
    private static final String APP_ID = "40072"; // H37A id key
    private static final String SECRET_KEY = "62c701b705df7f34aafd771a3775b537";

    // 通知给中间件的事件
    private static final int EVENT_DATA_READABLE = 1;   //音频数据可读的事件
    private static final int EVENT_DATA_END = 2;        //音频数据读取结束事件
    private static final int EVENT_ASR_TEXT_RESULT = 3;
    //语音ASR后的文本结果
    public static String VIDEO_NAME;
    public static boolean IS_PLAYING = false;
    public static String IS_TENCENT_PLAY = "is_tencent_play";
    private String currentAsrText;
    private static final RemoteCallbackList<SpeechCallbackWrapper<IDeviceCallbackAidl>> stateCallbacks = new RemoteCallbackList<>();
    private static final RemoteCallbackList<SpeechCallbackWrapper<IDeviceAudioEventAidl>> eventCallbacks = new RemoteCallbackList<>();

    /**
     * 1、使用Intent拉起腾讯视频（可携带命令）
     * 2、在腾讯视频下可以用adb命令继续输入文本进行测试
     * 3、am startservice -a com.ktcp.voicedevicedemo.ACTION_TEST --es type command --es text Hi
     */
    private static final String ACTION_DEVICE_DEMO_TEST = "com.voyah.ai.voice.ACTION_BIND_SERVICE";

    private static final String KEY_TYPE = "type";
    private static final String TYPE_COMMAND = "command";
    private static final String TYPE_CALLBACK = "callback";

    // Service提供的Binder接口实现
    private DeviceVoiceServiceStub mDeviceVoiceServiceStub = new DeviceVoiceServiceStub();

    // 中间件注册过来的回调接口
    private IDeviceAudioEventAidl mAudioEventInterface;

    @Override
    public void onCreate() {
        LogUtils.i(TAG, "onCreate");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        LogUtils.i(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.i(TAG, "onStartCommand: " + intent + ", flags = " + flags + ", startId = " + startId);
        handleIntent(intent);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.i(TAG, "onBind: " + intent);
        return mDeviceVoiceServiceStub;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        LogUtils.i(TAG, "onRebind: " + intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtils.i(TAG, "onUnbind: " + intent);
        // 置空中间件注册过来回调接口
//        eventCallbacks.kill();
//        stateCallbacks.kill();
        return super.onUnbind(intent);
    }

    private class DeviceVoiceServiceStub extends IDeviceVoiceServiceAidl.Stub {

        @Override
        public void requestAudio(int sampleRate, int channel, int encoding, IDeviceAudioEventAidl event) throws RemoteException {
            LogUtils.i(TAG, "call requestAudio sampleRate: " + sampleRate + ", channel: " + channel + ", encoding: " + encoding+"---"+Binder.getCallingUserHandle().hashCode());
            eventCallbacks.register(new SpeechCallbackWrapper<>(String.valueOf(Binder.getCallingUserHandle().hashCode()),event));
        }

        @Override
        public void abandonAudio() throws RemoteException {
            LogUtils.i(TAG, "call abandonAudio");
        }

        @Override
        public int readAudioData(byte[] data) throws RemoteException {
            LogUtils.i(TAG, "call readAudioData");
            return 0;
        }

        @Override
        public void onAsrResult(String result) throws RemoteException {
            LogUtils.i(TAG, "call onAsrResult result" + result);
            try {
                JSONObject jb = new JSONObject(result);
                int resultCode = jb.optInt("resultCode");
                String asrText = jb.optString("asrText");
                LogUtils.d(TAG, "resultCode: " + resultCode);
                LogUtils.d(TAG, "asrText: " + asrText);
                if (asrText.startsWith("播放第")) {
                    if (resultCode == 0) {
                        MediaHelper.speakTts("1100005");
                    } else if (resultCode == 3) {
                        MediaHelper.speak(TtsReplyUtils.getTtsBean("4025003",
                                "@{media_num}", String.valueOf(TencentVideoImpl.EPISODE)).getSelectTTs());
                    } else {
                        //TODO 当前在第一集，继续执行播放第一集，code返回的仍是0
                        MediaHelper.speak(TtsReplyUtils.getTtsBean("4025002",
                                "@{media_num}", String.valueOf(TencentVideoImpl.EPISODE)).getSelectTTs());
                    }
                } else if ("下一集".equals(asrText)) {
                    if (resultCode == 0) {
                        MediaHelper.speakTts("1100005");
                    } else {
                        MediaHelper.speakTts("4023502");
                    }
                } else if ("上一集".equals(asrText)) {
                    if (resultCode == 0) {
                        MediaHelper.speakTts("1100005");
                    } else {
                        MediaHelper.speakTts("4023402");
                    }
                } else if ("降低清晰度".equals(asrText)) {
                    if (resultCode == 0) {
                        MediaHelper.speakTts("4017303");
                    } else {
                        MediaHelper.speakTts("4024806");
                    }
                } else if ("提高清晰度".equals(asrText)) {
                    if (resultCode == 0) {
                        MediaHelper.speakTts("4017302");
                    } else {
                        MediaHelper.speakTts("4024704");
                    }
                } else if (asrText.startsWith("调到最")) {
                    if (resultCode == 0) {
                        MediaHelper.speakTts("4017302");
                    }
                } else if (asrText.contains("清晰度")||asrText.contains("切换到")) {
                    if (resultCode == 0) {
                        MediaHelper.speakTts("1100005");
                    } else if (resultCode == 3) {
                        MediaHelper.speakTts("4024704");
                    } else {
                        MediaHelper.speakTts("4024703");
                    }
                } else if ("增加倍速播放".equals(asrText)) {
                    if (resultCode == 0) {
                        MediaHelper.speakTts("4017302");
                    }
                } else if ("降低倍速播放".equals(asrText)) {
                    if (resultCode == 0) {
                        MediaHelper.speakTts("4017303");
                    }
                } else if ("最快倍速播放".equals(asrText) || "最慢倍速播放".equals(asrText)) {
                    if (resultCode == 0) {
                        MediaHelper.speakTts("1100005");
                    }
                } else if (asrText.startsWith("播放速度调整为")) {
                    if (resultCode == 0) {
                        if (TencentVideoImpl.isOutRangeMaxSpeed) {
                            TencentVideoImpl.isOutRangeMaxSpeed = false;
                            MediaHelper.speak(TtsReplyUtils.getTtsBean("4024503", "@{media_speed_max}", "1.5倍速").getSelectTTs());
                        } else if (TencentVideoImpl.isOutRangeMinSpeed) {
                            TencentVideoImpl.isOutRangeMinSpeed = false;
                            MediaHelper.speak(TtsReplyUtils.getTtsBean("4024504", "@{media_speed_min}", "0.5倍速").getSelectTTs());
                        } else {
                            MediaHelper.speakTts("1100005");
                        }
                    }
                } else if (asrText.startsWith("快进")) {
                    if (resultCode == 0) {
                        MediaHelper.speakTts("1100005");
                    }
                } else if (asrText.startsWith("快退")) {
                    if (resultCode == 0) {
                        MediaHelper.speakTts("1100005");
                    } else {
                        MediaHelper.speakTts("4024103");
                    }
                } else if ("收藏视频".equals(asrText)) {
                    if (resultCode == 0) {
                        MediaHelper.speakTts("4004204");
                    } else {
                        MediaHelper.speakTts("4004203");
                    }
                } else if ("取消收藏视频".equals(asrText)) {
                    if (resultCode == 0) {
                        MediaHelper.speakTts("4004303");
                    } else {
                        MediaHelper.speakTts("4004304");
                    }
                } else if ("暂停播放".equals(asrText)) {
                    MediaHelper.speakTts("1100005");
                } else if ("继续播放".equals(asrText)) {
                    MediaHelper.speakTts("1100005");
                } else if ("播放历史记录".equals(asrText)) {
                    if (resultCode == 0) {
//                        currentAsrText = asrText;
//                    } else {
                        MediaHelper.speakTts("1100005");
                    }
                } else if ("播放我的在追".equals(asrText)) {
                    if (resultCode == 0) {
                        MediaHelper.speakTts("1100005");
                    }
                } else if ("打开播放历史".equals(asrText)) {
                    if (resultCode == 0) {
                        MediaHelper.speakTts("4019702");
                    }
                } else if ("换到负集".equals(asrText)) {
                    MediaHelper.speakTts("4003100");
                } else if ("从头播放".equals(asrText)) {
                    if (resultCode == 0) {
                        MediaHelper.speakTts("1100005");
                    }
                } else if ("打开弹幕".equals(asrText)) {
                    if (resultCode == 0) {
                        MediaHelper.speakTts("4024303");
                    }
                } else if ("关闭弹幕".equals(asrText)) {
                    if (resultCode == 0) {
                        MediaHelper.speakTts("4024403");
                    }
                }
            } catch (JSONException e) {
                LogUtils.d(TAG, "onAsrResult e: " + e.getMessage());
            }
        }

        @Override
        public void playTTS(String json) throws RemoteException {
            LogUtils.i(TAG, "call playTTS json" + json);
        }

        @Override
        public void openLight() throws RemoteException {
            LogUtils.i(TAG, "call openLight");
        }

        @Override
        public void closeLight() throws RemoteException {
            LogUtils.i(TAG, "call closeLight");
        }

        @Override
        public void setCallback(IDeviceCallbackAidl callback) throws RemoteException {
            LogUtils.i(TAG, "call setCallback");
            stateCallbacks.register(new SpeechCallbackWrapper<>(String.valueOf(Binder.getCallingUserHandle().hashCode()),callback));
        }

        @Override
        public String getAuthConfig() throws RemoteException {
            LogUtils.i(TAG, "call getAuthConfig");
            // 提供appId和secretKey给中间件校验身份，否则无法正常对接
            JSONObject auth = new JSONObject();
            try {
                auth.put("appId", APP_ID);
                auth.put("secretKey", SECRET_KEY);
            } catch (JSONException e) {
                LogUtils.d(TAG, "getAuthConfig e: " + e.getMessage());
            }
            return auth.toString();
        }

        @Override
        public String call(String method, String params) throws RemoteException {
            LogUtils.i(TAG, "call method: " + method + ", params: " + params+"---"+Binder.getCallingUserHandle().hashCode());
            // 此处调用callback仅为测试从设备方语音中间件到腾讯视频的泛化接口回调测试
            callback(method, params,String.valueOf(Binder.getCallingUserHandle().hashCode()));
            return "success";
        }
    }

    private void callAudioEvent(int event, String jsonParams,String userId) {
        if (eventCallbacks.getRegisteredCallbackCount() > 0) {
            for (int i = 0; i < eventCallbacks.getRegisteredCallbackCount(); i++) {
                try {
                    SpeechCallbackWrapper<IDeviceAudioEventAidl> registeredCallbackItem = eventCallbacks.getRegisteredCallbackItem(i);
                    LogUtils.i(TAG, "onAudioEvent event=" + event + " jsonParams=" + jsonParams + "----" + registeredCallbackItem.getPackageName() + "---" + userId);
                    if (registeredCallbackItem.getPackageName().equals(userId)) {
                        registeredCallbackItem.getCallback().onAudioEvent(event, jsonParams);
                        break;
                    }
                } catch (Exception e) {
                    LogUtils.d(TAG, "callAudioEvent e: " + e.getMessage());
                }
            }
        }
    }

    private void callback(String method, String jsonParams,String userId) {
        if (stateCallbacks.getRegisteredCallbackCount() > 0) {
            for (int i = 0; i < stateCallbacks.getRegisteredCallbackCount(); i++) {
                try {
                    SpeechCallbackWrapper<IDeviceCallbackAidl> registeredCallbackItem = stateCallbacks.getRegisteredCallbackItem(i);
                    LogUtils.d(TAG, "userId = " + userId + "----" + registeredCallbackItem.getPackageName());
                    if (registeredCallbackItem.getPackageName().equals(userId)) {
                        String result = registeredCallbackItem.getCallback().onCallback(method, jsonParams);
                        LogUtils.i(TAG, "onCallback method=" + method + " jsonParams=" + jsonParams + " result=" + result);
                        if ("onPlayerStateChanged".equals(method)) {
                            JSONObject jo = new JSONObject(jsonParams);
                            String newState = jo.optString("newState");
                            VIDEO_NAME = jo.optString("videoName");
                            if ("state_playing".equals(newState)) {
                                IS_PLAYING = true;
                                if ("播放历史记录".equals(currentAsrText)) {
                                    currentAsrText = "";
                                    MediaHelper.speak(TtsReplyUtils.getTtsBean("4010101", "@{media_name}", VIDEO_NAME).getSelectTTs());
                                }
                            } else {
                                IS_PLAYING = false;
                            }
                        }
                        break;
                    }
                } catch (Exception e) {
                    LogUtils.d(TAG, "callback e: " + e.getMessage());
                }
            }
        }
    }

    private void handleIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        String action = intent.getAction();

        // 处理用于测试Intent
        if (ACTION_DEVICE_DEMO_TEST.equals(action)) {
            String type = intent.getStringExtra(KEY_TYPE);

            // 发送命令
            if (TYPE_COMMAND.equals(type)) {
                String text = intent.getStringExtra("text");
                if (!TextUtils.isEmpty(text)) {
                    callAudioEvent(EVENT_ASR_TEXT_RESULT, String.format("{\"asrText\":\"%s\"}", text),intent.getStringExtra("userId"));
                }
            }
            // 调用回调接口
            else if (TYPE_CALLBACK.equals(type)) {
                String method = intent.getStringExtra("method");
                String params = intent.getStringExtra("jsonParams");
                if (!TextUtils.isEmpty(method)) {
                    callback(method, params,intent.getStringExtra("userId"));
                }
            }
        }
    }
}
