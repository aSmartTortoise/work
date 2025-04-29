package com.voice.sdk.device.carservice.constants;

/**
 * @Date 2024/7/24 16:24
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public interface ISysSetting {


    //android原生方法,获取system存储值
    interface SystemSettingType {
        String SETTING_SYSTEM = "setting_system";

        String SETTING_GLOBAL = "setting_global";
    }

    interface ThemeModeType {
        int MODE_NIGHT_AUTO = 0;
        int MODE_NIGHT_NO = 1;
        int MODE_NIGHT_YES = 2;
    }

    //模拟声浪
    interface VolumeImitate {
        int OFF = 1;
        int TRADITION = 2;
        int TECHNOLOGY = 3;
    }

    interface VolumeImitatePos {
        int IN_CAR = 0;
        int OUT_CAR = 1;
        int ALL = 2;
    }

    //驾驶辅助播报
    interface DrvAssistBroadcast {
        int OFF = 0;
        int BRIEF = 1;
        int DETAIL = 2;
    }

    interface IVolume {
        int STREAM_MUSIC = 3; //媒体
        int STREAM_VOICE_CALL = 0; //通话
        int STREAM_NOTIFICATION = 5; //系统
        int STREAM_ASSISTANT = 11; //语音
        int STREAM_NVI = 9; //导航
        int STREAM_BLUETOOTH = 12; //蓝牙
        int STEP_VOLUME = 3; //音量设置步长
        int VOLUME_MAX = 30; //音量最大
        int VOLUME_MIN = 0; //音量最小
        int VOLUME_VOL_MIN = 1; //通话音量最小
    }

    interface IWirelessCharge {
        int WC_UNACTIVATED = 0;
        int WC_OFF = 1;
        int WC_ON = 2;
    }
}
