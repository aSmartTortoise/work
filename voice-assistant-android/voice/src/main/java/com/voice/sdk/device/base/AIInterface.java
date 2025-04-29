package com.voice.sdk.device.base;

import com.voyah.ai.sdk.bean.DhScreenType;
import com.voyah.ai.sdk.listener.IImageRecognizeListener;

public interface AIInterface {

    String startImageRecognize(@DhScreenType int screenType);

    void registerIRListener(IImageRecognizeListener listener);

    void unregisterIRListener(IImageRecognizeListener listener);

    int getImageRecognizeState();
}
