package com.voice.sdk.device;

public class DeviceHolder {

    private DeviceHolder() {

    }

    public DevicesInterface getDevices() {
        return devicesInterface;
    }

    public void setDevicesInterface(DevicesInterface devicesInterface) {
        this.devicesInterface = devicesInterface;
    }

    private DevicesInterface devicesInterface;
    private final static DeviceHolder deviceHolder = new DeviceHolder();

    public static DeviceHolder INS() {
        return deviceHolder;
    }


}
