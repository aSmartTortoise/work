package com.voyah.ai.basecar.media.bean;

public enum ServiceConfigure {
    UI("com.voyah.media.app", "UIService", "ui"),
    MEDIAMANAGER("com.voyah.media.service_mediamanager", "MediaManagerService", "media_manager"),
    BTMUSIC("com.voyah.media.service_btmusic", "BtMusicService", "bt_music"),
    QQMUSIC("com.voyah.media.service_qqmusic", "QQMusicService", "qq_music"),
    USBMUSIC("com.voyah.media.service_usbmusic", "UsbMusicService", "usb_music"),
    WYMUSIC("com.voyah.media.service_wymusic", "WyMusicService", "wy_music"),
    YTMUSIC("com.voyah.media.service_ytmusic", "YtMusicService", "yt_music"),
    XMLYMUSIC("com.voyah.media.service_xmlymusic", "XmlyMusicService", "xmly_music");

    private final String packageName;
    private final String serviceName;
    private final String sourceType;

    ServiceConfigure(String packageName, String serviceName, String sourceType) {
        this.packageName = packageName;
        this.serviceName = serviceName;
        this.sourceType = sourceType;
    }

    public String pkgName() {
        return packageName;
    }

    public String serviceName() {
        return serviceName;
    }

    public String sourceType() {
        return sourceType;
    }

    public static ServiceConfigure getConfigureByPkg(String pkgName) {
        ServiceConfigure configure = null;
        for (ServiceConfigure service : ServiceConfigure.values()) {
            if (pkgName.equals(service.pkgName())) {
                configure = service;
                break;
            }
        }
        return configure;
    }

    public static ServiceConfigure getConfigureBySource(String sourceType) {
        ServiceConfigure configure = null;
        for (ServiceConfigure service : ServiceConfigure.values()) {
            if (sourceType.equals(service.sourceType())) {
                configure = service;
                break;
            }
        }
        return configure;
    }

    public static boolean sourceExist(String sourceType) {
        for (ServiceConfigure service : ServiceConfigure.values()) {
            if (sourceType.equals(service.sourceType())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "ServiceConfigure{" +
                "packageName='" + packageName + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", sourceType='" + sourceType + '\'' +
                '}';
    }
}
