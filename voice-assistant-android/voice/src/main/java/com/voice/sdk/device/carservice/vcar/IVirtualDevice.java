package com.voice.sdk.device.carservice.vcar;

/**
 * 集成方提供数据来源，实现该接口，如本地数据库，KV等
 */
public interface IVirtualDevice {
    Object getData(String methodName);
    void setData(String methodName, Object value);
}
