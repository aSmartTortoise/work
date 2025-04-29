package com.voice.sdk.vcar;


import com.voice.sdk.device.carservice.vcar.IVirtualDevice;

/**
 * 存储外部虚拟车传入的实现类
 */
public class VirtualDeviceManager {
    //虚拟车的实现接口
    IVirtualDevice virtualDevice;
    TTSCallBack ttsCallBack;
    LogCallBack logCallBack;

    private VirtualDeviceManager() {
    }

    public static VirtualDeviceManager getInstance() {
        return Holder.Instance;
    }

    private static class Holder {
        public static final VirtualDeviceManager Instance = new VirtualDeviceManager();
    }

    public void setVirtualDevice(IVirtualDevice virtualDevice) {
        this.virtualDevice = virtualDevice;
    }

    public IVirtualDevice getVirtualDevice() {
        return virtualDevice;
    }

    public TTSCallBack getTtsCallBack() {
        return ttsCallBack;
    }

    public void setTtsCallBack(TTSCallBack ttsCallBack) {
        this.ttsCallBack = ttsCallBack;
    }

    public LogCallBack getLogCallBack() {
        return logCallBack;
    }

    public void setLogCallBack(LogCallBack logCallBack) {
        this.logCallBack = logCallBack;
    }
}
