package com.voyah.ai.device.voyah.common.H56Car;

import com.voyah.ai.basecar.carservice.CarPropUtils;

import mega.car.config.Driving;

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
     * 将37车型定义的驾驶模式转换为语音工程定义的
     * @return
     */
    public static int getVDrivingMode() {
        return CarPropUtils.getInstance().getIntProp(Driving.ID_DRV_MODE);
    }

    public static void setDrivingMode(int virtualMode) {
        CarPropUtils.getInstance().setIntProp(Driving.ID_DRV_MODE, virtualMode);
    }
}
