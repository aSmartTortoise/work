package com.voyah.h37z;

import android.app.Application;
import android.content.Context;
import android.media.AudioAttributes;
import android.os.RemoteException;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.interfaces.SimpleCallback;
import com.microsoft.assistant.AssistantClient;
import com.microsoft.assistant.IAssistantBinder;
import com.microsoft.assistant.IAssistantBinderCallback;
import com.microsoft.assistant.model.AssistantClientConfiguration;
import com.microsoft.assistant.model.AssistantMode;
import com.microsoft.assistant.model.DialogMode;
import com.microsoft.assistant.model.SpeechLocation;
import com.microsoft.assistant.model.TtsCategory;
import com.microsoft.assistant.response.ResponseOutput;
import com.microsoft.assistant.response.ResultOutput;
import com.voyah.h37z.fragment.dialog.CarControlDialog;
import com.voyah.h37z.fragment.dialog.ContinueDialogueDialog;
import com.voyah.h37z.fragment.dialog.CustomGreetDialog;
import com.voyah.h37z.fragment.dialog.GlobalWakeupDialog;
import com.voyah.h37z.fragment.dialog.MainWakeupDialog;
import com.voyah.h37z.fragment.dialog.MicZoneLockDialog;
import com.voyah.h37z.fragment.dialog.ViewCmdDialog;
import com.voyah.h37z.fragment.dialog.VoiceTouchDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class VpaViewModel extends AndroidViewModel {
    private static final String CLIENT_APP_ID = "h37z_vpa_settings";
    private static final String TAG = VpaViewModel.class.getSimpleName();
    // LiveData
    private final MutableLiveData<Boolean> isConnectedLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> dataChangedLiveData = new MutableLiveData<>();
    private final Context context;
    private List<CommonTipBean> viewCmdTipList, voiceTouchTipList, carControlTipList;
    private IAssistantBinder assistantBinder;
    private AssistantClient client;
    private final Map<String, TTSPlayCallback> ttsPlayCallbackMap = new ArrayMap<>();

    /**
     * 发声人列表映射(key为microsoft name, value为UI name)，有序
     */
    public final Map<String, String> sortedVoiceNameMap = new TreeMap<>();
    // todo 发声人名字待确定
    public static final List<String> MS_VOICE_NAMES = Arrays.asList(
            "microsoft_voice_1",
            "microsoft_voice_2"
    );
    // todo UI名字待确定
    public static final List<String> UI_VOICE_NAMES = Arrays.asList(
            MainApplication.getApplication().getString(R.string.personal_voice_soft_female),
            MainApplication.getApplication().getString(R.string.personal_voice_cool_female)
    );

    /**
     * 默认问候语，key为SpeechLocation取值
     */
    private final Map<Integer, String> DEFAULT_GREETINGS = new ArrayMap<>();

    public VpaViewModel(@NonNull Application application) {
        super(application);
        this.context = application;
        // 初始化数据
        for (int i = 0; i < MS_VOICE_NAMES.size(); i++) {
            sortedVoiceNameMap.put(MS_VOICE_NAMES.get(i), UI_VOICE_NAMES.get(i));
        }

        DEFAULT_GREETINGS.put(1, context.getString(R.string.settings_item_custom_greet_0_tips));
        DEFAULT_GREETINGS.put(2, context.getString(R.string.settings_item_custom_greet_1_tips));
        DEFAULT_GREETINGS.put(4, context.getString(R.string.settings_item_custom_greet_2_tips));
        DEFAULT_GREETINGS.put(8, context.getString(R.string.settings_item_custom_greet_3_tips));
    }

    /**
     * 获取MS-SDK是否已连接livedata
     *
     * @return
     */
    public MutableLiveData<Boolean> getConnectLiveData() {
        return isConnectedLiveData;
    }

    /**
     * 获取数据发生改变livedata
     *
     * @return
     */
    public MutableLiveData<Boolean> getDataChangedLiveData() {
        return dataChangedLiveData;
    }

    /**
     * 连接语音服务
     */
    public void connect() {
        Log.d(TAG, "connect() called");
        client = AssistantClient.Create(context, AssistantMode.FUll, s -> {
            try {
                if ("Connected".equalsIgnoreCase(s)) {
                    Log.d(TAG, "服务已连接");
                    assistantBinder = client.getAssistantBinderClient();
                    assistantBinder.registerServiceCallback(CLIENT_APP_ID, callback);
                    isConnectedLiveData.postValue(true);
                } else {
                    assistantBinder = null;
                    Log.d(TAG, "服务已断开");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
        client.connect();
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        if (assistantBinder != null) {
            try {
                assistantBinder.unRegisterServiceCallback(CLIENT_APP_ID, callback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            assistantBinder = null;
        }
        if (client != null) {
            client.disconnect();
            client = null;
        }
    }

    /**
     * 获取可见即可说提示列表
     *
     * @return
     */
    public List<CommonTipBean> getViewCmdTipList() {
        if (viewCmdTipList == null) {
            viewCmdTipList = getCommonTipBeans("vpa/viewcmd_tips.json");
        }
        return viewCmdTipList;
    }

    /**
     * 获取一语即达提示列表
     *
     * @return
     */
    public List<CommonTipBean> getVoiceTouchTipList() {
        if (voiceTouchTipList == null) {
            voiceTouchTipList = getCommonTipBeans("vpa/voice_touch_tips.json");
        }
        return voiceTouchTipList;
    }

    /**
     * 获取语音车控提示列表
     *
     * @return
     */
    public List<CommonTipBean> getCarControlTipList() {
        if (carControlTipList == null) {
            carControlTipList = getCommonTipBeans("vpa/car_control_tips.json");
        }
        return carControlTipList;
    }

    /**
     * 从assert中获取CommonTipBean列表
     *
     * @param assetFileName
     * @return
     */
    private List<CommonTipBean> getCommonTipBeans(String assetFileName) {
        List<CommonTipBean> list = new ArrayList<>();
        try {
            InputStream inputStream = context.getAssets().open(assetFileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            JSONObject object = new JSONObject(stringBuilder.toString());
            JSONArray jsonArray = object.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String image = jsonObject.getString("image");
                String title = jsonObject.getString("title");
                JSONArray tipsArray = jsonObject.getJSONArray("tips");
                List<String> tips = new ArrayList<>();
                for (int j = 0; j < tipsArray.length(); j++) {
                    tips.add(tipsArray.getString(j));
                }
                CommonTipBean commonTipBean = new CommonTipBean(image, tips, title);
                list.add(commonTipBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 显示免唤醒提示弹窗
     */
    public void showGlobalWakeupDialog(View view) {
        Log.d(TAG, "showGlobalWakeupDialog");
        new XPopup.Builder(view.getContext())
                .dismissOnBackPressed(false)
                .dismissOnTouchOutside(true)
                .setPopupCallback(new SimpleCallback() {
                    @Override
                    public void onDismiss(BasePopupView basePopupView) {
                    }
                })
                .asCustom(new GlobalWakeupDialog(view.getContext()))
                .show();
    }

    /**
     * 显示主唤醒关闭提示弹窗
     */
    public void showMainWakeupDialog(View view) {
        Log.d(TAG, "showMainWakeupDialog");
        new XPopup.Builder(view.getContext())
                .dismissOnBackPressed(false)
                .dismissOnTouchOutside(true)
                .setPopupCallback(new SimpleCallback() {
                    @Override
                    public void onDismiss(BasePopupView basePopupView) {
                        dataChangedLiveData.setValue(true);
                    }
                })
                .asCustom(new MainWakeupDialog(view.getContext(), this))
                .show();
    }

    /**
     * 显示自定义欢迎语、弹窗
     *
     * @param context  上下文
     * @param location 该值为SpeechLocation中定义
     */
    public void showCustomGreetDialog(Context context, int location) {
        Log.d(TAG, "showCustomGreetDialog() called, location = [" + location + "]");
        new XPopup.Builder(context)
                .dismissOnBackPressed(false)
                .dismissOnTouchOutside(true)
                .setPopupCallback(new SimpleCallback() {
                    @Override
                    public void onShow(BasePopupView popupView) {
                        EditText editText = popupView.findViewById(R.id.et_custom_greet);
                        if (editText != null) {
                            editText.requestFocus();
                            KeyBoardUtil.openKeybord(editText, editText.getContext());
                        }
                    }

                    @Override
                    public void onDismiss(BasePopupView basePopupView) {
                        dataChangedLiveData.setValue(true);
                    }
                })
                .asCustom(new CustomGreetDialog(context, location, this))
                .show();
    }

    /**
     * 显示音区锁定设置弹窗
     */
    public void showMicZoneLockDialog(View view) {
        Log.d(TAG, "showMicZoneLockDialog");
        new XPopup.Builder(view.getContext())
                .dismissOnBackPressed(false)
                .dismissOnTouchOutside(true)
                .setPopupCallback(new SimpleCallback() {
                    @Override
                    public void onDismiss(BasePopupView basePopupView) {
                    }
                })
                .asCustom(new MicZoneLockDialog(view.getContext(), this))
                .show();
    }


    /**
     * 显示连续对话弹窗
     */
    public void showContinueDialogueDialog(View view) {
        Log.d(TAG, "showContinueDialogueDialog");
        new XPopup.Builder(view.getContext())
                .dismissOnBackPressed(false)
                .dismissOnTouchOutside(true)
                .setPopupCallback(new SimpleCallback() {
                    @Override
                    public void onDismiss(BasePopupView basePopupView) {
                    }
                })
                .asCustom(new ContinueDialogueDialog(view.getContext()))
                .show();
    }

    /**
     * 显示可见即可说弹窗
     */
    public void showViewCmdDialog(View view) {
        Log.d(TAG, "showViewCmdDialog");
        new XPopup.Builder(view.getContext())
                .dismissOnBackPressed(false)
                .dismissOnTouchOutside(true)
                .setPopupCallback(new SimpleCallback() {
                    @Override
                    public void onDismiss(BasePopupView basePopupView) {
                    }
                })
                .asCustom(new ViewCmdDialog(view.getContext(), this))
                .show();
    }

    /**
     * 显示一语即达弹窗
     *
     * @param view
     */
    public void showVoiceTouchDialog(View view) {
        Log.d(TAG, "showVoiceTouchDialog");
        new XPopup.Builder(view.getContext())
                .dismissOnBackPressed(false)
                .dismissOnTouchOutside(true)
                .setPopupCallback(new SimpleCallback() {
                    @Override
                    public void onDismiss(BasePopupView basePopupView) {
                    }
                })
                .asCustom(new VoiceTouchDialog(view.getContext(), this))
                .show();
    }

    /**
     * 显示语音车控弹窗
     *
     * @param view
     */
    public void showCarControlDialog(View view) {
        Log.d(TAG, "showCarControlDialog");
        new XPopup.Builder(view.getContext())
                .dismissOnBackPressed(false)
                .dismissOnTouchOutside(true)
                .setPopupCallback(new SimpleCallback() {
                    @Override
                    public void onDismiss(BasePopupView basePopupView) {
                    }
                })
                .asCustom(new CarControlDialog(view.getContext(), this))
                .show();
    }

    /**
     * 是否启用语音唤醒
     *
     * @return
     */
    public boolean isMainWakeupEnable() {
        if (assistantBinder != null) {
            try {
                AssistantClientConfiguration config = assistantBinder.getCurrentClientConfiguretion(CLIENT_APP_ID);
                if (config != null) {
                    return config.EnabledDialogMode != DialogMode.None;
                } else {
                    Log.d(TAG, "AssistantClientConfiguration is null");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 是否启用免唤醒指令
     *
     * @return
     */
    public boolean isGlobalWakeupEnable() {
        if (assistantBinder != null) {
            try {
                AssistantClientConfiguration config = assistantBinder.getCurrentClientConfiguretion(CLIENT_APP_ID);
                if (config != null) {
                    return config.EnableDirectCommand;
                } else {
                    Log.d(TAG, "AssistantClientConfiguration is null");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 是否开启连续对话开关
     *
     * @return
     */
    public boolean isContinuousDialogueEnable() {
        if (assistantBinder != null) {
            try {
                AssistantClientConfiguration config = assistantBinder.getCurrentClientConfiguretion(CLIENT_APP_ID);
                if (config != null) {
                    return config.EnableContinuousDialog;
                } else {
                    Log.d(TAG, "AssistantClientConfiguration is null");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 启用语音唤醒
     */
    public void enableMainWakeup(boolean enable) {
        Log.d(TAG, "enableMainWakeup() called with: enable = [" + enable + "]");
        if (assistantBinder != null) {
            try {
                AssistantClientConfiguration config = assistantBinder.getCurrentClientConfiguretion(CLIENT_APP_ID);
                if (config != null) {
                    if (enable) {
                        config.EnabledDialogMode = DialogMode.SingleLocation;
                    } else {
                        config.EnabledDialogMode = DialogMode.None;
                    }
                    assistantBinder.updateConfiguration(CLIENT_APP_ID, config);
                } else {
                    Log.d(TAG, "AssistantClientConfiguration is null");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "assistantBinder == null");
            connect();
        }
    }

    /**
     * 启用免唤醒
     */
    public void enableGlobalWakeup(boolean enable) {
        Log.d(TAG, "enableGlobalWakeup() called with: enable = [" + enable + "]");
        if (assistantBinder != null) {
            try {
                AssistantClientConfiguration config = assistantBinder.getCurrentClientConfiguretion(CLIENT_APP_ID);
                if (config != null) {
                    config.EnableDirectCommand = enable;
                    assistantBinder.updateConfiguration(CLIENT_APP_ID, config);
                } else {
                    Log.d(TAG, "AssistantClientConfiguration is null");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "assistantBinder == null");
            connect();
        }
    }

    /**
     * 启用连续对话
     */
    public void enableContinuousDialog(boolean enable) {
        Log.d(TAG, "enableContinuousDialog() called with: enable = [" + enable + "]");
        if (assistantBinder != null) {
            try {
                AssistantClientConfiguration config = assistantBinder.getCurrentClientConfiguretion(CLIENT_APP_ID);
                if (config != null) {
                    config.EnableContinuousDialog = enable;
                    assistantBinder.updateConfiguration(CLIENT_APP_ID, config);
                } else {
                    Log.d(TAG, "AssistantClientConfiguration is null");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "assistantBinder == null");
            connect();
        }
    }

    /**
     * 获取对应Location 音区是否可用
     *
     * @param location
     * @return
     */
    public boolean isMicZoneEnable(SpeechLocation location) {
        if (assistantBinder != null) {
            try {
                AssistantClientConfiguration config = assistantBinder.getCurrentClientConfiguretion(CLIENT_APP_ID);
                if (config != null) {
                    SpeechLocation speechLocation = config.EnabledMicrophoneLocation;
                    if (speechLocation != null) {
                        return (speechLocation.getState() & location.getState()) != 0;
                    }
                } else {
                    Log.d(TAG, "AssistantClientConfiguration is null");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 设置对应location音区是否启用
     *
     * @param location
     * @param enable
     */
    public void setMicZoneEnable(@NonNull SpeechLocation location, boolean enable) {
        Log.d(TAG, "setMicZoneEnable() called with: location = [" + location + "], enable = [" + enable + "]");
        if (assistantBinder != null) {
            try {
                AssistantClientConfiguration config = assistantBinder.getCurrentClientConfiguretion(CLIENT_APP_ID);
                if (config != null) {
                    int curState = config.EnabledMicrophoneLocation.getState();
                    if (enable) {
                        config.EnabledMicrophoneLocation = SpeechLocation.fromIntValue(curState | location.getState());
                    } else {
                        config.EnabledMicrophoneLocation = SpeechLocation.fromIntValue(curState & ~location.getState());
                    }
                    assistantBinder.updateConfiguration(CLIENT_APP_ID, config);
                } else {
                    Log.d(TAG, "AssistantClientConfiguration is null");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "assistantBinder == null");
            connect();
        }
    }

    /**
     * 获取自定义问候语
     *
     * @param location 该值为SpeechLocation中定义
     * @return
     */
    public String getCustomGreet(int location) {
        if (assistantBinder != null) {
            try {
                AssistantClientConfiguration config = assistantBinder.getCurrentClientConfiguretion(CLIENT_APP_ID);
                if (config != null) {
                    Map<Integer, String> customGreetingWord = config.CustomGreetingWord;
                    if (customGreetingWord != null && customGreetingWord.get(location) != null) {
                        return customGreetingWord.get(location);
                    }
                } else {
                    Log.d(TAG, "AssistantClientConfiguration is null");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return DEFAULT_GREETINGS.get(location);
    }

    /**
     * 设置自定义问候语
     *
     * @param location 该值为SpeechLocation中定义
     * @param custom   自定义问候语
     * @return
     */
    public void setCustomGreet(int location, String custom) {
        Log.d(TAG, "setCustomGreet() called with: location = [" + location + "], custom = [" + custom + "]");
        if (assistantBinder != null) {
            try {
                AssistantClientConfiguration config = assistantBinder.getCurrentClientConfiguretion(CLIENT_APP_ID);
                if (config != null) {
                    Map<Integer, String> customGreetingWord = config.CustomGreetingWord;
                    customGreetingWord.put(location, custom);
                    assistantBinder.updateConfiguration(CLIENT_APP_ID, config);
                } else {
                    Log.d(TAG, "AssistantClientConfiguration is null");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "assistantBinder == null");
            connect();
        }
    }

    /**
     * 获取当前的发声人
     */
    public String getVoiceName() {
        if (assistantBinder != null) {
            try {
                AssistantClientConfiguration config = assistantBinder.getCurrentClientConfiguretion(CLIENT_APP_ID);
                if (config != null) {
                    return config.TtsVoiceName;
                } else {
                    Log.d(TAG, "AssistantClientConfiguration is null");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return MS_VOICE_NAMES.get(0);
    }

    /**
     * 设置发声人
     *
     * @param voiceName
     */
    public void setVoiceName(String voiceName) {
        Log.d(TAG, "setVoiceName() called with: voiceName = [" + voiceName + "]");
        if (assistantBinder != null) {
            try {
                AssistantClientConfiguration config = assistantBinder.getCurrentClientConfiguretion(CLIENT_APP_ID);
                if (config != null) {
                    config.TtsVoiceName = voiceName;
                    assistantBinder.updateConfiguration(CLIENT_APP_ID, config);
                } else {
                    Log.d(TAG, "AssistantClientConfiguration is null");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "assistantBinder == null");
            connect();
        }
    }

    /**
     * 播报
     *
     * @param text
     */
    public String speak(String text, @NonNull TTSPlayCallback callback) {
        Log.d(TAG, "speak() called with: text = [" + text + "]");
        if (assistantBinder != null) {
            try {
                AudioAttributes attributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ASSISTANT)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).build();
                String ttsId = assistantBinder.ttsTextInput(CLIENT_APP_ID, text, TtsCategory.VA.getState(), attributes, true);
                ttsPlayCallbackMap.put(ttsId, callback);
                Log.d(TAG, "speak, text:" + text + ",ttsId:" + ttsId);
                return ttsId;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "assistantBinder == null");
            connect();
            callback.onTTSPlayEnd("");
        }
        return null;
    }

    /**
     * 停止播报
     *
     * @param ttsId
     */
    public void stopSpeak(String ttsId) {
        Log.d(TAG, "stopSpeak() called with: ttsId = [" + ttsId + "]");
        if (assistantBinder != null) {
            try {
                assistantBinder.stopTts(CLIENT_APP_ID, ttsId);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "assistantBinder == null");
            connect();
        }
    }

    /**
     * 注册/反注册回调
     */
    private final IAssistantBinderCallback callback = new IAssistantBinderCallback.Stub() {
        @Override
        public void onInitialized(int i) throws RemoteException {
            Log.d(TAG, "onInitialized() called with: i = [" + i + "]");
        }

        @Override
        public void onSrRecognizing(ResultOutput resultOutput) throws RemoteException {
        }

        @Override
        public void onSrRecognized(ResultOutput resultOutput) throws RemoteException {
        }

        @Override
        public boolean beforeLanguageTriage(ResultOutput resultOutput) throws RemoteException {
            return false;
        }

        @Override
        public void afterLanguageTriage(ResultOutput resultOutput) throws RemoteException {

        }

        @Override
        public boolean beforeDialogTriage(ResultOutput resultOutput) throws RemoteException {
            return false;
        }

        @Override
        public void afterDialogTriage(ResultOutput resultOutput) throws RemoteException {

        }

        @Override
        public void onResponse(ResponseOutput responseOutput) throws RemoteException {

        }

        @Override
        public void onConfigurationUpdated(int i) throws RemoteException {

        }

        @Override
        public void onError(int i, String s) throws RemoteException {

        }

        @Override
        public void onStatusChange(int i) throws RemoteException {

        }

        @Override
        public void onTtsSynthesisStarted(String s) throws RemoteException {

        }

        @Override
        public void onTtsSynthesisWordBoundary(String s, long l, long l1, long l2) throws RemoteException {

        }

        @Override
        public void onTtsSynthesisCanceled(String s, int i, String s1) throws RemoteException {

        }

        @Override
        public void onTtsSynthesisCompleted(String s, String s1, String s2, long l) throws RemoteException {

        }

        @Override
        public void onTtsSynthesizing(String s, String s1, long l, byte[] bytes) throws RemoteException {

        }

        @Override
        public void onTtsPlayStarted(String s) throws RemoteException {
            TTSPlayCallback callback = ttsPlayCallbackMap.get(s);
            if (callback != null) {
                callback.onTTSPlayBegin(s);
            }
        }

        @Override
        public void onTtsPlayPaused(String s) throws RemoteException {
            TTSPlayCallback callback = ttsPlayCallbackMap.get(s);
            if (callback != null) {
                callback.onTTSPlayEnd(s);
                ttsPlayCallbackMap.remove(s);
            }
        }

        @Override
        public void onTtsPlayResumed(String s) throws RemoteException {

        }

        @Override
        public void onTtsPlayStopped(String s) throws RemoteException {
            TTSPlayCallback callback = ttsPlayCallbackMap.get(s);
            if (callback != null) {
                callback.onTTSPlayEnd(s);
                ttsPlayCallbackMap.remove(s);
            }
        }
    };
}