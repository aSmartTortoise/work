package com.voice.sdk.device.base;

import com.voyah.ai.sdk.ILongAsrCallback;

public interface LongAsrInterface {

    void startLongASR(int screenType, ILongAsrCallback callback);

    void stopLongASR(int screenType);

    boolean isLongAsrGoing(int screenType);
}
