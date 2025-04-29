package com.voice.sdk.device.base;

import com.voyah.ai.sdk.SceneIntent;
import com.voyah.ai.sdk.bean.LifeState;
import com.voyah.ai.sdk.bean.NluResult;
import com.voyah.ai.sdk.listener.IRmsDbListener;
import com.voyah.ai.sdk.listener.IVAReadyListener;
import com.voyah.ai.sdk.listener.IVAResultListener;
import com.voyah.ai.sdk.listener.IVAStateListener;

public interface DialogueInterface {

    void registerResultListener(IVAResultListener listener);

    void unregisterResultListener(IVAResultListener listener);

    void registerStateCallback(IVAStateListener listener);

    void unregisterStateListener(IVAStateListener listener);

    void registerReadyListener(IVAReadyListener listener);

    void unregisterReadyListener(IVAReadyListener listener);

    void onViewCommandCallback(String text, NluResult result);

    void onAsrResultCallback(boolean isOnline, String text, boolean isEnd);

    void onTtsCallback(String text);

    void onNluResultCallback(NluResult result);

    void onShortCommandCallback(String command, NluResult result);

    void onVoiceStateCallback(@LifeState String state, String... awakenLocation);

    boolean isInteractionState();

    boolean isReady();

    String getSpeechState();

    int getDirection();

    String getDirectionNature();

    void stopDialogue();

    void startDialogue(int direction);

    void registerRmsDbListener(IRmsDbListener listener);

    void unregisterRmsDbListener(IRmsDbListener listener);

    void onRmsChangeCallback(float rms);

    int triggerSceneIntent(SceneIntent intent);
}
