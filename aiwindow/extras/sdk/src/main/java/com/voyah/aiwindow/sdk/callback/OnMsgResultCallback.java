package com.voyah.aiwindow.sdk.callback;

import com.voyah.aiwindow.aidlbean.State;

public interface OnMsgResultCallback {
    /**
     * 执行结果
     */
    void onResult(State state, String msg);
}
