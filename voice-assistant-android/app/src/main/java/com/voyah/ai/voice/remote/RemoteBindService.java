package com.voyah.ai.voice.remote;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.LogUtils;
import com.voice.sdk.VoiceImpl;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.constant.ApplicationConstant;
import com.voyah.ai.sdk.IAgentCallback;
import com.voyah.ai.sdk.IAsrListener;
import com.voyah.ai.sdk.IHintResultCallback;
import com.voyah.ai.sdk.IImageRecognizeCallback;
import com.voyah.ai.sdk.ILongAsrCallback;
import com.voyah.ai.sdk.IPvcCallback;
import com.voyah.ai.sdk.IRmsDbCallback;
import com.voyah.ai.sdk.ISpeechService;
import com.voyah.ai.sdk.ITtsCallback;
import com.voyah.ai.sdk.IVAReadyCallback;
import com.voyah.ai.sdk.IVAResultCallback;
import com.voyah.ai.sdk.IVAStateCallback;
import com.voyah.ai.sdk.IVprCallback;
import com.voyah.ai.sdk.SceneIntent;
import com.voyah.ai.sdk.bean.DhDialect;
import com.voyah.ai.sdk.bean.UserVprInfo;
import com.voyah.ai.voice.agent.generic.SampleClientAgent;

import java.util.List;

/**
 * 响应远程连接的Service
 */
public class RemoteBindService extends Service {
    private final RemoteCallbackList<SpeechCallbackWrapper<IVAReadyCallback>> readyCallbacks = new RemoteCallbackList<>();
    private final RemoteCallbackList<SpeechCallbackWrapper<IVAStateCallback>> stateCallbacks = new RemoteCallbackList<>();
    private final RemoteCallbackList<SpeechCallbackWrapper<IVAResultCallback>> resultCallbacks = new RemoteCallbackList<>();
    private final RemoteCallbackList<SpeechCallbackWrapper<IRmsDbCallback>> rmsDbCallbacks = new RemoteCallbackList<>();
    private final RemoteCallbackList<SpeechCallbackWrapper<IImageRecognizeCallback>> imageRecognizeCallbacks = new RemoteCallbackList<>();

    private SpeechCallbackAccessor callbackAccessor;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.i("onCreate()");
        callbackAccessor = new SpeechCallbackAccessor();
        SpeechCallbackAccessor.SpeechCallbacks callbacks = new SpeechCallbackAccessor.SpeechCallbacks();
        callbacks.readyCallbacks = readyCallbacks;
        callbacks.stateCallbacks = stateCallbacks;
        callbacks.resultCallbacks = resultCallbacks;
        callbacks.rmsDbCallbacks = rmsDbCallbacks;
        callbacks.imageRecognizeCallbacks = imageRecognizeCallbacks;
        callbackAccessor.init(callbacks);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.v("onDestroy() called");
        readyCallbacks.kill();
        stateCallbacks.kill();
        resultCallbacks.kill();
        rmsDbCallbacks.kill();
        imageRecognizeCallbacks.kill();
        callbackAccessor.unInit();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new RemoteBinder();
    }


    /**
     * 提供其它应用接入能力
     */
    public class RemoteBinder extends ISpeechService.Stub {

        @Override
        public void registerReadyCallback(String packageName, IVAReadyCallback callback) throws RemoteException {
            LogUtils.d("registerReadyCallback() called with: packageName = [" + packageName + "], callback = [" + callback + "]");
            readyCallbacks.register(new SpeechCallbackWrapper<>(packageName, callback));

            SpeechCallbackRecipient recipient = new SpeechCallbackRecipient(packageName);
            callback.asBinder().linkToDeath(recipient, 0);
        }

        @Override
        public void unregisterReadyCallback(String packageName, IVAReadyCallback callback) throws RemoteException {
            LogUtils.d("unregisterReadyCallback() called with: packageName = [" + packageName + "], callback = [" + callback + "]");
            readyCallbacks.unregister(new SpeechCallbackWrapper<>(packageName, callback));
        }

        @Override
        public boolean isRemoteReady() throws RemoteException {
            return DeviceHolder.INS().getDevices().getDialogue().isReady();
        }

        @Override
        public void registerStateCallback(String packageName, IVAStateCallback cb) throws RemoteException {
            LogUtils.d("registerStateCallback() called with: packageName = [" + packageName + "], cb = [" + cb + "]");
            stateCallbacks.register(new SpeechCallbackWrapper<>(packageName, cb));
        }

        @Override
        public void unregisterStateCallback(String packageName, IVAStateCallback cb) throws RemoteException {
            LogUtils.d("unregisterStateCallback() called with: packageName = [" + packageName + "], cb = [" + cb + "]");
            stateCallbacks.unregister(new SpeechCallbackWrapper<>(packageName, cb));
        }

        @Override
        public void registerResultCallback(String pkgName, IVAResultCallback cb) throws RemoteException {
            LogUtils.d("registerResultCallback() called with: pkgName = [" + pkgName + "], cb = [" + cb + "]");
            resultCallbacks.register(new SpeechCallbackWrapper<>(pkgName, cb));
        }

        @Override
        public void unregisterResultCallback(String pkgName, IVAResultCallback cb) throws RemoteException {
            LogUtils.d("unregisterResultCallback() called with: pkgName = [" + pkgName + "], cb = [" + cb + "]");
            resultCallbacks.unregister(new SpeechCallbackWrapper<>(pkgName, cb));
        }

        @Override
        public void speak(String pkgName, String text, ITtsCallback callback, String speaker, boolean highPriority) throws RemoteException {
            LogUtils.d("speak() called with: pkgName = [" + pkgName + "], text = [" + text + "], speaker = [" + speaker + "], highPriority = [" + highPriority + "]");
            // 无须实现，走TTSService
        }

        @Override
        public void stopCurTts(String pkgName) throws RemoteException {
            LogUtils.d("stopCurTts() called with: pkgName = [" + pkgName + "]");
            // 无须实现，走TTSService
        }

        @Override
        public void shutUp(String pkgName) throws RemoteException {
            LogUtils.d("shutUp() called with: pkgName = [" + pkgName + "]");
            // 无须实现，走TTSService
        }

        @Override
        public void startDialogue(String pkgName, int direction) throws RemoteException {
            LogUtils.d("startDialogue() called with: pkgName = [" + pkgName + "], direction = [" + direction + "]");
            DeviceHolder.INS().getDevices().getDialogue().startDialogue(direction);
        }

        @Override
        public void stopDialogue(String pkgName) throws RemoteException {
            LogUtils.d("stopDialogue() called, token=" + pkgName);
            DeviceHolder.INS().getDevices().getDialogue().stopDialogue();
        }

        @Override
        public int getWakeupDirection() throws RemoteException {
            return DeviceHolder.INS().getDevices().getDialogue().getDirection();
        }

        @Override
        public String getLifeState() throws RemoteException {
            return DeviceHolder.INS().getDevices().getDialogue().getSpeechState();
        }

        @Override
        public void getHintResult(String pkgName, int num, @NonNull IHintResultCallback callback) throws RemoteException {
            LogUtils.d("getHintResult() called with: pkgName = [" + pkgName + "], num = [" + num + "], callback = [" + callback + "]");
        }

        @Override
        public void trackAsrEvent(String pkg, String skill, String scene, String intent, String tts) throws RemoteException {
            LogUtils.d("trackAsrEvent() called with: skill = [" + skill + "], scene = [" + scene + "], intent = [" + intent + "], tts = [" + tts + "]");
        }

        @Override
        public void subscribeDomains(String pkg, List<String> domainList) throws RemoteException {
            LogUtils.d("subscribe() called with: pkg = [" + pkg + "], domainList = [" + domainList + "]");
        }

        @Override
        public void setShortCommands(String pkg, String jsonStr) throws RemoteException {
            LogUtils.d("setShortCommands() called with: pkgName = [" + pkg + "], jsonStr = [" + jsonStr + "]");
        }

        @Override
        public void onViewContentChange(String pkg, String json) throws RemoteException {
            LogUtils.d("onViewContentChange() called with: pkg = [" + pkg + "], json = [" + json + "]");
            DeviceHolder.INS().getDevices().getFeedback().onViewContentChange(pkg, json);
        }

        @Override
        public String startRegisterVprWithId(String pkg, String vprId, int direction, IVprCallback callback) throws RemoteException {
            LogUtils.d("startRegisterVprWithId() called with: pkg = [" + pkg + "], vprId = [" + vprId + "], direction = [" + direction + "], callback = [" + callback + "]");
            return DeviceHolder.INS().getDevices().getVpr().startRegisterVpr(vprId, direction, callback);
        }

        @Override
        public String startRegisterVpr(String pkg, int direction, IVprCallback callback) throws RemoteException {
            LogUtils.d("startRegisterVpr() called with: pkg = [" + pkg + "], direction = [" + direction + "], callback = [" + callback + "]");
            return DeviceHolder.INS().getDevices().getVpr().startRegisterVpr(null, direction, callback);
        }

        @Override
        public void stopRegisterVpr(String pkg, String id, int errCode) throws RemoteException {
            LogUtils.d("stopRegisterVpr() called with: pkg = [" + pkg + "], id = [" + id + "], errCode = [" + errCode + "]");
            DeviceHolder.INS().getDevices().getVpr().stopRegisterVpr(id, errCode);
        }

        @Override
        public int deleteUserVpr(String pkg, String id) throws RemoteException {
            LogUtils.d("deleteUserVpr() called with: pkg = [" + pkg + "], id = [" + id + "]");
            return DeviceHolder.INS().getDevices().getVpr().deleteUserVpr(id);
        }

        @Override
        public void startRecordingVpr(String pkg, String id, String text) throws RemoteException {
            LogUtils.d("startRecordingVpr() called with: pkg = [" + pkg + "], id = [" + id + "], text = [" + text + "]");
            DeviceHolder.INS().getDevices().getVpr().startRecordingVpr(id, text);
        }

        @Override
        public void stopRecordingVpr(String pkg, String id, String text, int errCode) throws RemoteException {
            LogUtils.d("stopRecordingVpr() called with: pkg = [" + pkg + "], id = [" + id + "], text = [" + text + "], errCode = [" + errCode + "]");
            DeviceHolder.INS().getDevices().getVpr().stopRecognizeVpr(id, text, errCode);
        }

        @Override
        public int getMaxSupportedVprNum() throws RemoteException {
            return DeviceHolder.INS().getDevices().getVpr().getMaxSupportedVprNum();
        }

        @Override
        public String getUserVprInfo(String vprId) throws RemoteException {
            UserVprInfo userVpr = DeviceHolder.INS().getDevices().getVpr().getUserVprInfo(vprId);
            if (userVpr != null) {
                return JSON.toJSONString(userVpr);
            } else {
                return null;
            }
        }

        @Override
        public int saveUserVprInfo(String pkg, String json) throws RemoteException {
            LogUtils.d("saveUserVprInfo() called with: pkg = [" + pkg + "], json = [" + json + "]");
            UserVprInfo userVp = JSON.parseObject(json, UserVprInfo.class);
            return DeviceHolder.INS().getDevices().getVpr().saveUserVprInfo(userVp);
        }

        @Override
        public String getRegisteredVprList() throws RemoteException {
            List<UserVprInfo> list = DeviceHolder.INS().getDevices().getVpr().getRegisteredVprList();
            if (list != null) {
                return JSON.toJSONString(list);
            } else {
                return null;
            }
        }

        @Override
        public void enableSwitch(String pkgName, String switchName, boolean enable) throws RemoteException {
            LogUtils.d("enableSwitch() called with: pkgName = [" + pkgName + "], switchName = [" + switchName + "], enable = [" + enable + "]");
            if (TextUtils.equals(pkgName, ApplicationConstant.PKG_SETTINGS)) {
                DeviceHolder.INS().getDevices().getVoiceSettings().enableSwitch(switchName, enable);
            }
        }

        @Override
        public boolean isEnableSwitch(String switchName) throws RemoteException {
            return DeviceHolder.INS().getDevices().getVoiceSettings().isEnableSwitch(switchName);
        }

        @Override
        public void changeDialect(String pkg, String json) throws RemoteException {
            LogUtils.d("changeDialect() called with: pkg = [" + pkg + "], json = [" + json + "]");
            DhDialect dialect = JSON.parseObject(json, DhDialect.class);
            DeviceHolder.INS().getDevices().getVoiceSettings().setDialect(dialect);
        }

        @Override
        public String getCurrentDialect() throws RemoteException {
            DhDialect dhDialect = DeviceHolder.INS().getDevices().getVoiceSettings().getCurrentDialect();
            return JSON.toJSONString(dhDialect);
        }

        @Override
        public void getPvcList(String pkgName, @NonNull IPvcCallback callback) throws RemoteException {
            LogUtils.d("getPvcList() called with: pkgName = [" + pkgName + "], callback = [" + callback + "]");
            DeviceHolder.INS().getDevices().getVoiceSettings().getPvcList(callback);
        }

        @Override
        public void setNickname(String pkg, String nickname) throws RemoteException {
            LogUtils.d("setNickname() called with: pkg = [" + pkg + "], nickname = [" + nickname + "]");
        }

        @Override
        public void restoreNickname(String pkg) throws RemoteException {
            LogUtils.d("restoreNickname() called with: pkg = [" + pkg + "]");
        }

        @Override
        public String getNickname() throws RemoteException {
            LogUtils.d("getNickname() called");
            return null;
        }

        @Override
        public void setDiyGreeting(String pkgName, int direction, String greet) throws RemoteException {
            LogUtils.d("setDiyGreeting() called with: pkgName = [" + pkgName + "], direction = [" + direction + "], greet = [" + greet + "]");
        }

        @Override
        public String getDiyGreeting(int direction) throws RemoteException {
            LogUtils.d("getDiyGreeting() called with: direction = [" + direction + "]");
            return null;
        }

        @Override
        public void setPickupMode(String pkg, int mode) throws RemoteException {
            LogUtils.d("setPickupMode() called with: pkg = [" + pkg + "], mode = [" + mode + "]");
        }

        @Override
        public int getPickupMode() throws RemoteException {
            LogUtils.d("getPickupMode() called");
            return 0;
        }

        @Override
        public void setContinuousWaitTime(String pkg, int time) throws RemoteException {
            LogUtils.d("setContinuousWaitTime() called with: pkg = [" + pkg + "], time = [" + time + "]");
        }

        @Override
        public int getContinuousWaitTime() throws RemoteException {
            return 0;
        }

        @Override
        public void setUserVoiceMicMask(String pkg, int mask) throws RemoteException {
            LogUtils.d("setUserVoiceMicMask() called with: pkg = [" + pkg + "], mask = [" + mask + "]");
            if (TextUtils.equals(pkg, ApplicationConstant.PKG_SETTINGS)) {
                DeviceHolder.INS().getDevices().getVoiceSettings().setUserVoiceMicMask(mask);
            }
        }

        @Override
        public int getUserVoiceMicMask() throws RemoteException {
            return DeviceHolder.INS().getDevices().getVoiceSettings().getUserVoiceMicMask();
        }

        @Override
        public void startLongAsr(String pkg, int screenType, @NonNull ILongAsrCallback callback) throws RemoteException {
            LogUtils.d("startLongAsr() called with: pkg = [" + pkg + "], screenType = [" + screenType + "], callback = [" + callback + "]");
            DeviceHolder.INS().getDevices().getLongAsr().startLongASR(screenType, callback);
        }

        @Override
        public void stopLongAsr(String pkg, int screenType) throws RemoteException {
            LogUtils.d("stopLongAsr() called with: pkg = [" + pkg + "], screenType = [" + screenType + "]");
            DeviceHolder.INS().getDevices().getLongAsr().stopLongASR(screenType);
        }

        @Override
        public boolean isLongAsrGoing(int screenType) throws RemoteException {
            return DeviceHolder.INS().getDevices().getLongAsr().isLongAsrGoing(screenType);
        }

        @Override
        public int getMusicPreference() throws RemoteException {
            return DeviceHolder.INS().getDevices().getVoiceSettings().getMusicPreference();
        }

        @Override
        public int setMusicPreference(String pkgName, int preference) throws RemoteException {
            LogUtils.d("setMusicPreference() called with: pkgName = [" + pkgName + "], preference = [" + preference + "]");
            DeviceHolder.INS().getDevices().getVoiceSettings().setMusicPreference(preference);
            return 0;
        }

        @Override
        public int getVideoPreference() throws RemoteException {
            return DeviceHolder.INS().getDevices().getVoiceSettings().getVideoPreference();
        }

        @Override
        public int setVideoPreference(String pkgName, int preference) throws RemoteException {
            LogUtils.d("setVideoPreference() called with: pkgName = [" + pkgName + "], preference = [" + preference + "]");
            DeviceHolder.INS().getDevices().getVoiceSettings().setVideoPreference(preference);
            return 0;
        }

        @Override
        public void uploadPersonalEntity(String pkgName, String json) throws RemoteException {
            LogUtils.d("uploadPersonalEntity() called with: pkgName = [" + pkgName + "], json = [" + json + "]");
            DeviceHolder.INS().getDevices().getFeedback().uploadPersonalEntity(pkgName, json);
        }

        @Override
        public void setRmsDbListener(String pkgName, IRmsDbCallback callback) throws RemoteException {
            LogUtils.d("setRmsDbListener() called with: pkgName = [" + pkgName + "], callback = [" + callback + "]");
            if (callback != null) {
                rmsDbCallbacks.register(new SpeechCallbackWrapper<>(pkgName, callback));
            }
        }

        @Override
        public int registerAgentX(String pkgName, IAgentCallback iAgentCallback) throws RemoteException {
            if (null == iAgentCallback) {
                LogUtils.e("iAgentCallback is null");
                return -1;
            }
            if (!VoiceImpl.getInstance().getVoiceSdkInitStatus()) {
                LogUtils.e("voice sdk not init success");
                return -1;
            }

            LogUtils.i("registerAgentX pkgName:" + pkgName + ", agentName:" + iAgentCallback.getAgentName());
            SampleClientAgent sampleClientAgent = new SampleClientAgent(iAgentCallback);
            VoiceImpl.getInstance().registerAgent(sampleClientAgent);
            return 0;
        }

        @Override
        public void registerAsrListener(IAsrListener iAsrListener) throws RemoteException {
            LogUtils.i("registerAsrListener");
            RemoteAsrListenerManager.getInstance().registerAsrListener(iAsrListener);
        }

        @Override
        public void unregisterAsrListener(IAsrListener iAsrListener) throws RemoteException {
            LogUtils.i("unregisterAsrListener");
            RemoteAsrListenerManager.getInstance().unregisterAsrListener(iAsrListener);
        }

        @Override
        public int getCurVpaDisplayId() throws RemoteException {
            return DeviceHolder.INS().getDevices().getSystem().getScreen().getCurVpaDisplayId();
        }

        @Override
        public int triggerSceneIntent(String pkgName, SceneIntent intent) throws RemoteException {
            LogUtils.d("triggerSceneIntent() called with: pkgName = [" + pkgName + "], intent = [" + intent + "]");
            return DeviceHolder.INS().getDevices().getDialogue().triggerSceneIntent(intent);
        }

        @Override
        public void setTopCoverViewShowing(String pkgName, int displayId, boolean isShow) throws RemoteException {
            DeviceHolder.INS().getDevices().getViewCmd().setTopCoverViewShowing(pkgName, displayId, isShow);
        }

        @Override
        public boolean isShowingTopCoverView(String callPkg) throws RemoteException {
            return DeviceHolder.INS().getDevices().getViewCmd().isShowingTopCoverView(callPkg, DeviceHolder.INS().getDevices().getSystem().getScreen().getMainScreenDisplayId());
        }

        @Override
        public boolean isShowingTopCoverViewWithDisplayId(String callPkg, int displayId) throws RemoteException {
            return DeviceHolder.INS().getDevices().getViewCmd().isShowingTopCoverView(callPkg, displayId);
        }

        @Override
        public int getDisplayIdForScreenType(int screenType) throws RemoteException {
            return DeviceHolder.INS().getDevices().getSystem().getScreen().getDisplayId(screenType);
        }

        @Override
        public int getLeftSplitScreenWidth() throws RemoteException {
            return ApplicationConstant.VCOS_LEFT_SPLIT_SCREEN_WIDTH;
        }

        @Override
        public void setNewsPushConfigTime(String pkg, String time) throws RemoteException {
            LogUtils.d("setNewsPushConfigTime() called with: pkg = [" + pkg + "], time = [" + time + "]");
            DeviceHolder.INS().getDevices().getVoiceSettings().setNewsPushConfigTime(time);
        }

        @Override
        public String getNewsPushConfigTime() throws RemoteException {
            return DeviceHolder.INS().getDevices().getVoiceSettings().getNewsPushConfigTime();
        }

        @Override
        public int getAiModelPreference() throws RemoteException {
            return DeviceHolder.INS().getDevices().getVoiceSettings().getAiModelPreference();
        }

        @Override
        public int setAiModelPreference(String pkgName, int preference) throws RemoteException {
            LogUtils.d("setAiModelPreference() called with: pkgName = [" + pkgName + "], preference = [" + preference + "]");
            DeviceHolder.INS().getDevices().getVoiceSettings().setAiModelPreference(preference);
            return 0;
        }

        @Override
        public String startImageRecognize(String pkgName, int screenType) throws RemoteException {
            return DeviceHolder.INS().getDevices().getAI().startImageRecognize(screenType);
        }

        @Override
        public void registerImageRecognizeCallback(String pkgName, IImageRecognizeCallback callback) throws RemoteException {
            imageRecognizeCallbacks.register(new SpeechCallbackWrapper<>(pkgName, callback));
        }

        @Override
        public void unregisterImageRecognizeCallback(String pkgName, IImageRecognizeCallback callback) throws RemoteException {
            imageRecognizeCallbacks.unregister(new SpeechCallbackWrapper<>(pkgName, callback));
        }

        @Override
        public int getImageRecognizeState() throws RemoteException {
            return DeviceHolder.INS().getDevices().getAI().getImageRecognizeState();
        }
    }

}
