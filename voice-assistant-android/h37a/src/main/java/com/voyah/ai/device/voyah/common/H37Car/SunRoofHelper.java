package com.voyah.ai.device.voyah.common.H37Car;

import com.voyah.ai.basecar.carservice.CarPropUtils;

import mega.car.Signal;
import mega.car.VehicleArea;
import mega.car.config.Windows;

/**
 * @Date 2024/8/16 14:33
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class SunRoofHelper {

    public static int getSunRoofState() {
        return CarPropUtils.getInstance().getIntProp(Windows.ID_WINDOW, VehicleArea.OUTSIDE_TOP);
    }

    public static void setSunRoofState(int value) {
        if (value == 0) {
            CarPropUtils.getInstance().setIntProp(Signal.ID_SUNROOFCMDREQ, Signal.SunRoofCmdReq.SUNROOFCMDREQ_CLOSE);
        } else if (value == 100) {
            CarPropUtils.getInstance().setIntProp(Signal.ID_SUNROOFCMDREQ, Signal.SunRoofCmdReq.SUNROOFCMDREQ_OPEN);
        } else {
            CarPropUtils.getInstance().setIntProp(Windows.ID_WINDOW, VehicleArea.OUTSIDE_TOP, value);
        }
    }

    /**
     * 获取天窗通风状态
     * @return true : 天窗通风
     */
    public static boolean getSunRoofVentilation() {
        return CarPropUtils.getInstance().getIntProp(Signal.ID_SUNROOFMOTIONSTATE)
                == Signal.SunRoofMotionState.SUNROOFMOTIONSTATE_VENTILATION;
    }


    /**
     * 天窗设置成翘起状态
     * @param value ignored
     */
    public static void setSunRoofVentilation(boolean value) {
        CarPropUtils.getInstance().setIntProp(Signal.ID_SUNROOFCMDREQ, Signal.SunRoofCmdReq.SUNROOFCMDREQ_VENTILATION);
    }


}
