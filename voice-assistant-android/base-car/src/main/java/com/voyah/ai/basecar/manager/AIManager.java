package com.voyah.ai.basecar.manager;


import com.voice.sdk.device.base.AIInterface;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.sdk.listener.IImageRecognizeListener;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class AIManager implements AIInterface {

    private static final String TAG = "AIManager";

    /**
     * -1：bind绑定错误
     * -2：当前未开始识别
     * -3：网络错误、服务器异常等
     * 0：开始识图
     * 1：图片上传识别中
     * 2：流式卡片展示中
     * 3：流式加载完成
     * 4：识图结束，卡片消失
     */
    private final List<IImageRecognizeListener> irListeners = new CopyOnWriteArrayList<>();

    @Override
    public String startImageRecognize(int screenType) {
        LogUtils.d(TAG, "startImageRecognize() called with: screenType = [" + screenType + "]");
        String sessionId = UUID.randomUUID().toString();
        //int displayId = DeviceHolder.INS().getDevices().getSystem().getScreen().getDisplayId(screenType);
        //onImageRecognizeCallback(sessionId, screenType, -3, "网络错误、服务器异常等");
        // todo
        return sessionId;
    }

    @Override
    public void registerIRListener(IImageRecognizeListener listener) {
        if (!irListeners.contains(listener)) {
            this.irListeners.add(listener);
        }
    }

    @Override
    public void unregisterIRListener(IImageRecognizeListener listener) {
        irListeners.remove(listener);
    }

    public void onImageRecognizeCallback(String sessionId, int screenType, int state, String msg) {
        LogUtils.d(TAG, "onImageRecognizeCallback() called with: sessionId = [" + sessionId + "], screenType = [" + screenType + "], state = [" + state + "], msg = [" + msg + "]");
        for (IImageRecognizeListener callback : irListeners) {
            callback.onIRResult(sessionId, screenType, state, msg);
        }
    }

    @Override
    public int getImageRecognizeState() {
        return -2;
    }


    private static class Holder {
        private static final AIManager _INSTANCE = new AIManager();
    }

    private AIManager() {
        super();
    }

    public static AIManager get() {
        return AIManager.Holder._INSTANCE;
    }
}
