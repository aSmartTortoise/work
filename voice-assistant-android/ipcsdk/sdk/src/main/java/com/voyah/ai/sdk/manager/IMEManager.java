package com.voyah.ai.sdk.manager;

import static com.voyah.ai.sdk.DhSpeechSDK.assistantConnector;

import android.util.Log;

import androidx.annotation.NonNull;

import com.voyah.ai.sdk.bean.DhScreenType;
import com.voyah.ai.sdk.listener.ILongASRListener;

/**
 * 输入法管理类
 */
public class IMEManager {

    private static final String TAG = IMEManager.class.getSimpleName();

    /**
     * 开启长识别
     */
    public static void startLongASR(@DhScreenType int screenType, @NonNull ILongASRListener listener) {
        Log.d(TAG, "startLongASR() called with: screenType = [" + screenType + "], listener = [" + listener + "]");
        if (assistantConnector != null) {
            assistantConnector.startLongASR(screenType, listener);
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 结束长识别
     */
    public static void stopLongASR(@DhScreenType int screenType) {
        Log.d(TAG, "stopLongASR() called with: screenType = [" + screenType + "]");
        if (assistantConnector != null) {
            assistantConnector.stopLongASR(screenType);
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 是否正在进行长识别
     *
     * @return
     */
    public static boolean isLongAsrGoing(@DhScreenType int screenType) {
        if (assistantConnector != null) {
            return assistantConnector.isLongAsrGoing(screenType);
        } else {
            Log.e(TAG, "please call initialize first");
        }
        return false;
    }
}
