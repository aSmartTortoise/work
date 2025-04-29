package com.voyah.ai.sdk.manager;

import static com.voyah.ai.sdk.DhSpeechSDK.assistantConnector;

import android.util.Log;

import com.voyah.ai.sdk.bean.DhScreenType;
import com.voyah.ai.sdk.listener.IImageRecognizeListener;

public class AIManager {

    private static final String TAG = AIManager.class.getSimpleName();

    /**
     * 开始图片识别
     *
     * @param screenType 屏幕类型
     */
    public static String startImageRecognize(@DhScreenType int screenType) {
        Log.d(TAG, "startImageRecognize() called with: screenType = [" + screenType + "]");
        if (assistantConnector != null) {
            return assistantConnector.startImageRecognize(screenType);
        } else {
            Log.e(TAG, "please call initialize first");
        }
        return null;
    }

    /**
     * 设置图片识别监听器
     *
     * @param listener 监听器
     */
    public static void setImageRecognizeListener(IImageRecognizeListener listener) {
        Log.d(TAG, "setImageRecognizeListener() called with: listener = [" + listener + "]");
        if (assistantConnector != null) {
            assistantConnector.setImageRecognizeListener(listener);
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 获取图像识别状态
     *
     * @return * -1：bind绑定错误
     * * -2：当前未开始识别
     * * -3：网络错误、服务器异常等
     * * 0：开始识图
     * * 1：图片上传识别中
     * * 2：流式卡片展示中
     * * 3：流式加载完成
     * * 4：识图结束，卡片消失
     */
    public static int getImageRecognizeState() {
        if (assistantConnector != null) {
            return assistantConnector.getImageRecognizeState();
        } else {
            Log.e(TAG, "please call initialize first");
        }
        return -1;
    }
}
