package com.voice.sdk.device.carservice.constants;

/**
 * @Date 2024/9/6 14:08
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public interface IAdas {

    interface IChassisStyle {
        int COMFORT = 1;
        int STANDARD = 2;
        int SPORT = 3;
    }

    interface ILDA {
        int OFF= 0;
        int WARNING_ONLY = 1;
        int WARNING_ASSIST = 2;
    }

    //车道偏离辅助报警方式
    interface ILDAWarningStyle {
        int VOICE = 0;
        int VIBRATION = 1;
    }

    //智能行车风格
    interface AssidDrvgStyle {
        int STANDARD= 0;
        int COMFORT = 1; //舒适优先
        int EFFICIENCY =2; //效率优先
    }

    interface TdaState {
        int UNPAID_UNSUPPORTED = 0; //未拥有，功能未上线
        int UNPAID_SUPPORTED = 1; //未拥有，功能已上线
        int PAID_UNSUPPORTED = 2; //已付费，功能未上线
        int PAID_SUPPORTED = 3; //已付费，功能已上线
    }
}
