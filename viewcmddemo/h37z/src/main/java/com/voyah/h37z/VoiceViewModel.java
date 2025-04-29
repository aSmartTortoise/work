package com.voyah.h37z;


import android.app.ActivityTaskManager;
import android.app.TaskStackListener;
import android.content.ComponentName;
import android.os.RemoteException;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.voyah.ai.sdk.DhSpeechSDK;
import com.voyah.ai.sdk.bean.LifeState;
import com.voyah.ai.sdk.manager.DialogueManager;
import com.voyah.viewcmd.aspect.VoiceAutoRefresh;


/**
 * 处理语音可见即可说的viewModel
 */
public class VoiceViewModel extends ViewModel {
    private final String TAG = this.getClass().getSimpleName();

    public MutableLiveData<Boolean> voiceStateChangeData = new MutableLiveData<>();

    public VoiceViewModel() {
        Log.d(TAG, "VoiceViewModel onCreate!");
        registerVAStateListener();
        try {
            registerTaskStack();
        } catch (NoSuchMethodError error) {
            // error.printStackTrace();
        }

    }

    private void registerVAStateListener() {
        voiceStateChangeData.setValue(false);
        DhSpeechSDK.setVAReadyListener(() -> {
            Log.d(TAG, "onSpeechReady() called");
            DialogueManager.setVAStateListener(state -> {
                switch (state) {
                    case LifeState.AWAKE:
                        onVoiceAwakened();
                        break;
                    case LifeState.ASLEEP:
                        onVoiceEnd();
                        break;
                }
            });
            if (DhSpeechSDK.isInteractionState()) {
                onVoiceAwakened();
            }
        });

    }

    /**
     * 当语音可见即可说被唤醒时下发通知
     */
    @VoiceAutoRefresh
    public void onVoiceAwakened() {
        Log.d(TAG, "onVoiceAwakened");
        //通知页面切换布局
        voiceStateChangeData.postValue(true);
    }

    /**
     * 当语音可见即可说结束时下发通知
     */
    public void onVoiceEnd() {
        Log.d(TAG, "onVoiceEnd");
        //通知页面切换布局
        voiceStateChangeData.postValue(false);
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        DhSpeechSDK.release();
    }

    public void registerTaskStack() {
        Log.d(TAG, "registerTaskStack: ");
        ActivityTaskManager.getInstance().registerTaskStackListener(new TaskStackListener() {

            @Override
            public void onTaskCreated(int taskId, ComponentName componentName) throws RemoteException {
                Log.d(TAG, "onTaskCreated() called with: taskId = [" + taskId + "], componentName = [" + componentName + "]");
            }

            @Override
            public void onTaskDisplayChanged(int taskId, int newDisplayId) throws RemoteException {
                Log.d(TAG, "onTaskDisplayChanged() called with: taskId = [" + taskId + "], newDisplayId = [" + newDisplayId + "]");
                voiceStateChangeData.postValue(DialogueManager.isInteractionState());
            }
        });
    }
}
