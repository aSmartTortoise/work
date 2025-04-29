// IDeviceCallbackAidl.aidl
package com.ktcp.aiagent.device.aidl;

// Declare any non-default types here with import statements

interface IDeviceCallbackAidl {
    /**
     * 设备语音服务调用腾讯视频的泛化回调接口
     *
     * @param method
     * @param params
     * @return
     */
    String onCallback(String method, String params);
}