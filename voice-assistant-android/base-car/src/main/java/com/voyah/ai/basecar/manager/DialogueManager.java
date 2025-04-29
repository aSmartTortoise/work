package com.voyah.ai.basecar.manager;


import static com.voice.sdk.constant.ConfigsConstant.WAKEUP_LOCATION_MAP;

import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.voice.sdk.VoiceImpl;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.base.DialogueInterface;
import com.voice.sdk.device.system.WakeupDirection;
import com.voice.sdk.device.ui.UIMgr;
import com.voyah.ai.sdk.SceneIntent;
import com.voyah.ai.sdk.bean.DhDirection;
import com.voyah.ai.sdk.bean.DhSwitch;
import com.voyah.ai.sdk.bean.LifeState;
import com.voyah.ai.sdk.bean.NluResult;
import com.voyah.ai.sdk.listener.IRmsDbListener;
import com.voyah.ai.sdk.listener.IVAReadyListener;
import com.voyah.ai.sdk.listener.IVAResultListener;
import com.voyah.ai.sdk.listener.IVAStateListener;
import com.voyah.ai.voice.sdk.api.component.parameter.command.WakeupCommandParameters;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class DialogueManager implements DialogueInterface {

    private final List<IVAResultListener> resultListeners = new CopyOnWriteArrayList<>();
    private final List<IVAStateListener> stateListeners = new CopyOnWriteArrayList<>();
    private final List<IVAReadyListener> readyListeners = new CopyOnWriteArrayList<>();
    private final List<IRmsDbListener> rmsDbListeners = new CopyOnWriteArrayList<>();
    private final Handler threadHandler;

    private @LifeState String speechState;

    private int direction = -1;

    public void registerResultListener(IVAResultListener listener) {
        if (!resultListeners.contains(listener)) {
            this.resultListeners.add(listener);
        }
    }

    public void unregisterResultListener(IVAResultListener listener) {
        this.resultListeners.remove(listener);
    }

    public void registerStateCallback(IVAStateListener listener) {
        if (!stateListeners.contains(listener)) {
            this.stateListeners.add(listener);
        }
    }

    public void unregisterStateListener(IVAStateListener listener) {
        this.stateListeners.remove(listener);
    }

    public void registerReadyListener(IVAReadyListener listener) {
        LogUtils.v("registerReadyListener() called with: listener = [" + listener + "]");
        if (!readyListeners.contains(listener)) {
            this.readyListeners.add(listener);
        }
    }

    public void unregisterReadyListener(IVAReadyListener listener) {
        LogUtils.v("unregisterReadyListener() called with: listener = [" + listener + "]");
        this.readyListeners.remove(listener);
    }

    public void onViewCommandCallback(String text, NluResult result) {
        LogUtils.v("onViewCommandCallback() called with: text = [" + text + "], result = [" + result + "]");
        for (IVAResultListener callback : resultListeners) {
            callback.onViewCommand(text, result);
        }
    }

    public void onAsrResultCallback(boolean isOnline, String text, boolean isEnd) {
        LogUtils.d("onAsrResultCallback() called with: text = [" + text + "], isEnd = [" + isEnd + "], isOnline = [" + isOnline + "]");
        for (IVAResultListener callback : resultListeners) {
            if (callback instanceof ExtVAResultListener) {
                if (isEnd) {
                    ((ExtVAResultListener) callback).onAsr(isOnline, text);
                }
            } else {
                callback.onAsr(text, isEnd);
            }
        }
    }

    public void onTtsCallback(String text) {
        LogUtils.v("onTtsCallback() called with: text = [" + text + "]");
        for (IVAResultListener callback : resultListeners) {
            if (callback instanceof ExtVAResultListener) {
                ((ExtVAResultListener) callback).onTts(text);
            }
        }
    }

    public void onNluResultCallback(NluResult result) {
        LogUtils.d("onNluResultCallback() called with: result = [" + result + "]");
        for (IVAResultListener callback : resultListeners) {
            callback.onNluResult(result);
        }
    }

    public void onShortCommandCallback(String command, NluResult result) {
        LogUtils.d("onShortCommandCallback() called with: command = [" + command + "], result = [" + result + "]");
        for (IVAResultListener callback : resultListeners) {
            callback.onShortCommand(command, result);
        }
    }

    public void onVoiceStateCallback(@LifeState String state, String... awakenLocation) {
        if (LifeState.ASLEEP.equals(speechState) && !LifeState.AWAKE.equals(state) && !LifeState.READY.equals(state)) {
            LogUtils.d("ignore state change, last state:" + speechState + ",new state:" + state);
            return;
        }
        if (!TextUtils.equals(speechState, state)) {
            this.speechState = state;
            if (LifeState.AWAKE.equals(state) && awakenLocation != null && awakenLocation.length > 0) {
                WakeupDirection wakeupDirection = WAKEUP_LOCATION_MAP.get(awakenLocation[0]);
                direction = wakeupDirection == null ? DhDirection.FRONT_LEFT : wakeupDirection.getDirection();
            }
            threadHandler.post(() -> {
                for (IVAStateListener callback : stateListeners) {
                    callback.onState(state);
                }
            });
        }
        if (LifeState.READY.equals(state)) {
            onVoiceReadyCallback();
            checkAccessibilityService();
        } else if (LifeState.AWAKE.equals(state)) {
            checkAccessibilityService();
            DeviceHolder.INS().getDevices().getViewCmd().triggerViewCmdUpload(DeviceHolder.INS().getDevices().getSystem().getScreen().getCurVpaDisplayId());
        }
        LogUtils.d("onVoiceStateCallback() called with: state = [" + state + "]");
    }

    private void onVoiceReadyCallback() {
        LogUtils.d("onVoiceReadyCallback() called");
        for (IVAReadyListener callback : readyListeners) {
            callback.onSpeechReady();
        }
    }

    private void checkAccessibilityService() {
        boolean isRunning = DeviceHolder.INS().getDevices().getViewCmd().isAccessibilityServiceRunning();
        if (!isRunning) {
            boolean isCar = DeviceHolder.INS().getDevices().getCarServiceProp().vehicleSimulatorJudgment();
            if (isCar) {
                DeviceHolder.INS().getDevices().getViewCmd().enableAccessibilityService(true);
            }
        }
    }


    /**
     * 当前语音是否处于交互状态
     */
    public boolean isInteractionState() {
        String state = getSpeechState();
        return LifeState.AWAKE.equals(state) || LifeState.SPEAKING.equals(state)
                || LifeState.LISTENING.equals(state) || LifeState.INPUTTING.equals(state)
                || LifeState.RECOGNIZING.equals(state) || LifeState.HOLDING.equals(state);
    }

    public boolean isReady() {
        return !LifeState.UNKNOWN.equals(getSpeechState());
    }

    public String getSpeechState() {
        return TextUtils.isEmpty(speechState) ? LifeState.UNKNOWN : speechState;
    }

    public int getDirection() {
        return direction;
    }

    public String getDirectionNature() {
        WakeupDirection wakeupDirection = WakeupDirection.fromKey(direction);
        if (wakeupDirection != null) {
            return wakeupDirection.getChName();
        } else {
            return "";
        }
    }

    public void stopDialogue() {
        LogUtils.d("stopDialogue() called");
        VoiceImpl.getInstance().exDialog();
        UIMgr.INSTANCE.forceExitAll("dialogueManager");
    }

    public void startDialogue(int direction) {
        VoiceImpl.getInstance().wakeUp(direction, WakeupCommandParameters.WAKEUP_DEFAULT);
    }

    public void registerRmsDbListener(IRmsDbListener listener) {
        if (!rmsDbListeners.contains(listener)) {
            this.rmsDbListeners.add(listener);
        }
    }

    public void unregisterRmsDbListener(IRmsDbListener listener) {
        this.rmsDbListeners.remove(listener);
    }

    public void onRmsChangeCallback(float rms) {
        for (IRmsDbListener listener : rmsDbListeners) {
            listener.onRmsChanged(rms);
        }
    }

    public int triggerSceneIntent(SceneIntent intent) {
        if (null == intent) {
            LogUtils.d("triggerSceneIntent intent is null");
            return -1;
        }

        boolean isEnableSwitch = SettingsManager.get().isEnableSwitch(DhSwitch.MainWakeup);
        boolean isVoiceForbidden = DeviceHolder.INS().getDevices().getVoiceCarSignal().isVoiceForbidden();
        if (!isEnableSwitch) {
            LogUtils.d("triggerSceneIntent isEnableSwitch is false");
            return -2;
        } else if (isVoiceForbidden) {
            LogUtils.d("triggerSceneIntent isVoiceForbidden is true");
            return -3;
        }
        Map<String, Object> params = null;
        if (intent.params != null) {
            params = new HashMap<>(intent.params);
        }
        if (!StringUtils.isBlank(intent.tts))
            DeviceHolder.INS().getDevices().getTts().speak(intent.tts);
        VoiceImpl.getInstance().thirdSceneReport(intent.state, intent.tts, params, 0, true);
        return 0;
    }

    private static class Holder {
        private static final DialogueManager _INSTANCE = new DialogueManager();
    }

    private DialogueManager() {
        super();
        HandlerThread thread = new HandlerThread("t_dm_cb");
        thread.start();
        threadHandler = new Handler(thread.getLooper());
    }

    public static DialogueManager get() {
        return Holder._INSTANCE;
    }
}
