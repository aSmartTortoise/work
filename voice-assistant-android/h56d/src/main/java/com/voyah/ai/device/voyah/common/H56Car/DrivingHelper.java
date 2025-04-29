package com.voyah.ai.device.voyah.common.H56Car;


import com.voice.sdk.device.carservice.constants.ICarSetting;
import com.voyah.ai.basecar.carservice.CarPropUtils;

import mega.car.config.Driving;
import mega.car.config.H56D;

/**
 * @Date 2024/7/2 14:26
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class DrivingHelper {


    public static boolean isDriving() {
        //获取当前挡位
        int curDriveGear = CarPropUtils.getInstance().getIntProp(Driving.ID_DRV_INFO_GEAR_POSITION);
        if (curDriveGear == 0 || curDriveGear == 2) {
            //获取当前车速
            float curDriveSpeed = CarPropUtils.getInstance().getFloatProp(Driving.ID_DRV_INFO_SPEED_INFO);
            if (curDriveSpeed > 3F) {
                return true;
            }
            return false;
        }
        return true;
    }

    /**
     * 将车型定义的驾驶模式转换为语音工程定义的
     * @return
     */
    public static int getDrivingMode() {
        int carServiceValue = CarPropUtils.getInstance().getIntProp(H56D.PDCM10_PDCM_DRIVEMODE_HW);
        switch (carServiceValue) {
            case 0:
                return ICarSetting.DrivingMode.ECONOMY;
            case 1:
                return ICarSetting.DrivingMode.COMFORTABLE;
            case 2:
                return ICarSetting.DrivingMode.SPORT;
            case 3:
                return ICarSetting.DrivingMode.SNOW;
            case 4:
                return ICarSetting.DrivingMode.CUSTOM;
            case 5:
                return ICarSetting.DrivingMode.PICNIC;
        }
        return ICarSetting.DrivingMode.INVALID;
    }

    public static void setDrivingMode(int virtualMode) {
        int carServiceValue = 0;
        switch (virtualMode) {
            case ICarSetting.DrivingMode.ECONOMY:
                carServiceValue = 1;
                break;
            case ICarSetting.DrivingMode.COMFORTABLE:
                carServiceValue = 2;
                break;
            case ICarSetting.DrivingMode.SPORT:
                carServiceValue = 3;
                break;
            case ICarSetting.DrivingMode.CUSTOM:
                carServiceValue = 4;
                break;
            case ICarSetting.DrivingMode.PICNIC:
                carServiceValue = 5;
                break;
            case ICarSetting.DrivingMode.SNOW:
                carServiceValue = 6;
                break;
        }
        CarPropUtils.getInstance().setIntProp(H56D.IVI_pwrSet_IVI_DRIVEMODESET, carServiceValue);
    }
}
