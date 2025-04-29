package com.voice.sdk.context;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DeviceInfo implements Serializable {

    private String dm;

    private String value;


    public static DeviceInfo build(String dm, Object value) {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDm(dm);
        deviceInfo.setValue(value != null ? value.toString() : null);
        return deviceInfo;
    }

    public String getDm() {
        return dm;
    }

    public void setDm(String dm) {
        this.dm = dm;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        map.put(dm, value);
        return map;
    }


}
