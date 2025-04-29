package com.voyah.ai.device.voyah.common.media.impl;

import com.voice.sdk.device.media.bean.ObserverResponse;
import com.voyah.ai.basecar.media.bean.Code;
import com.voyah.ipcsdk.exception.IpcTimeOutException;
import com.voyah.ipcsdk.exception.ResponseException;


public abstract class IpcObserverResponse<T> extends ObserverResponse<T> {

    @Override
    public void onError(Throwable e) {
        if (e instanceof ResponseException) {
            ResponseException responseException = ((ResponseException) e);
            onFailed(responseException.getCode(), responseException.getMsg());
        } else if (e instanceof IpcTimeOutException) {
            onFailed(Code.ICP_TIMEOUT.code(), Code.ICP_TIMEOUT.msg());
        } else {
            onException(e);
        }
    }
}
