package com.voice.sdk.device.system;

public enum DeviceScreenType {

    CENTRAL_SCREEN("central_screen", "中控屏"),
    PASSENGER_SCREEN("passenger_screen", "副驾屏"),
    CEIL_SCREEN("ceil_screen", "吸顶屏");

    private final String name;

    private final String chName;

    DeviceScreenType(String name, String chName) {

        this.name = name;
        this.chName = chName;
    }

    public static DeviceScreenType fromValue(String value) {
        for (DeviceScreenType e : DeviceScreenType.values()) {
            if (value != null && value.equalsIgnoreCase(e.name)) {
                return e;
            }
        }
        return CENTRAL_SCREEN;
    }

    public String getChName() {
        return chName;
    }
}
