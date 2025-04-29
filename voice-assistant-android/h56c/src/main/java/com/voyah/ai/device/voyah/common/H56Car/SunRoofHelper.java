package com.voyah.ai.device.voyah.common.H56Car;

import com.voyah.ai.basecar.carservice.CarPropUtils;
import com.voyah.ai.common.utils.LogUtils;


import mega.car.VehicleArea;
import mega.car.config.H56C;
import mega.car.config.Signal;

/**
 * @Date 2024/8/16 14:33
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class SunRoofHelper {

    private static final String TAG = "SunRoofHelper";

    public static int getSunRoofState() {
        LogUtils.e(TAG, "获取当前天窗的状态百分比：" + CarPropUtils.getInstance().getIntProp(H56C.GW_LIN_BODY_SRM_SRPOSITION, VehicleArea.OUTSIDE_TOP));
        return CarPropUtils.getInstance().getIntProp(H56C.GW_LIN_BODY_SRM_SRPOSITION, VehicleArea.OUTSIDE_TOP);
    }

    public static void setSunRoofState(int value) {
        if (value == 0) {
            CarPropUtils.getInstance().setIntProp(Signal.ID_SUNROOFCMDREQ, 2);
        } else if (value == 100) {
            CarPropUtils.getInstance().setIntProp(Signal.ID_SUNROOFCMDREQ, 1);
        } else {
            CarPropUtils.getInstance().setIntProp(H56C.IVI_BodySet1_IVI_SUNROOF_HORIZONTALCONTROL, VehicleArea.OUTSIDE_TOP, value);
        }
    }

    /**
     * 获取天窗通风状态
     * @return true : 天窗通风
     */
    public static boolean getSunRoofVentilation() {
        return CarPropUtils.getInstance().getIntProp(Signal.ID_SUNROOFMOTIONSTATE)
                == 4;
    }


    /**
     * 天窗设置成翘起状态
     * @param value ignored
     */
    public static void setSunRoofVentilation(boolean value) {
        CarPropUtils.getInstance().setIntProp(Signal.ID_SUNROOFCMDREQ,2);
    }


}
