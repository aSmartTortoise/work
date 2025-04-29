package com.bytedance.variants.platform;

import android.os.Bundle;
import com.bytedance.variants.platform.IMediaSeesionCallback;

interface IMediaSeesionOnCommandCB {

    void onICommand(String command,inout Bundle data);

    // 注册回调接口
    void registerCallback(IMediaSeesionCallback callback);

    // 取消注册回调接口
    void unregisterCallback(IMediaSeesionCallback callback);
}