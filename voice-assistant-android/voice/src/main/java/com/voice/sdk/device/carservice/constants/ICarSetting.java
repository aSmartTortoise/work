package com.voice.sdk.device.carservice.constants;

import androidx.annotation.IntDef;

/**
 * @Date 2024/7/18 11:30
 * @Author 8327821
 * @Email *
 * @Description 车辆设置模块使用到的枚举属性
 **/
public interface ICarSetting {

    /**
     * 车辆驾驶模式定义
     */
    @IntDef(value = {DrivingMode.COMFORTABLE,
            DrivingMode.ECONOMY, DrivingMode.SPORT, DrivingMode.PICNIC,
            DrivingMode.SNOW, DrivingMode.SUPER_POWER, DrivingMode.CUSTOM, DrivingMode.INVALID})
    public @interface DrivingMode {
        int COMFORTABLE = 2;
        int ECONOMY = 1;
        int SPORT = 3;
        int PICNIC = 11;
        int SNOW = 12;
        int SUPER_POWER = 4;
        int CUSTOM = 10;
        int INVALID = 99;
    }

    /**
     * 车辆动力模式定义
     */
    @IntDef(value = {PowerMode.ECO, PowerMode.STANDARD, PowerMode.SPORT, PowerMode.SNOW})
    public @interface PowerMode {
        int ECO = 1;
        int STANDARD = 2;
        int SPORT = 3;
        int SNOW = 4;

    }

    /**
     * 自动驻车
     */
    interface AvhMode {
        int DEPP_STEP = 1; //深踩激活
        int AUTO = 0; //自动激活

    }


    //续航里程
    interface RemainMileage {
        int STANDARD = 0;
        int REAL = 1;
        int PERCENT = 2;
    }

    //解锁模式 全车解锁或者主驾解锁
    interface UnlockMode {
        int ALL_CAR = 1; //全车解锁
        int DRIVER_ONLY = 2; //仅主驾解锁
    }

    /**
     * 伴我回家
     */
    interface FollowHomeMode {
        int OFF = 1;
        int MODE_15S = 2;
        int MODE_30S = 3;
        int MODE_60S = 4;
    }

    interface WelcomeLights {
        int OFF = 0;
        int MODE1 = 1;
        int MODE2 = 2;
        int MODE3 = 3;
        int MODE4 = 4;
        int MODE5 = 5;
        int MODE6 = 6;
        int MODE7 = 7;
        int MODE8 = 8;
        int MODE9 = 9;
        int MODE10 = 10;
    }

}
