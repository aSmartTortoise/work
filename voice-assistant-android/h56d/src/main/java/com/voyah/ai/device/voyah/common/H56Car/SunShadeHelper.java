package com.voyah.ai.device.voyah.common.H56Car;


import com.voyah.ai.basecar.carservice.CarPropUtils;

import mega.car.VehicleArea;
import mega.car.config.H56D;
import mega.car.config.Signal;

/**
 * @Date 2024/8/16 14:15
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class SunShadeHelper {

    public static int getSunShadeState() {
        return CarPropUtils.getInstance().getIntProp(H56D.GW_LIN_INFO_SSM_SSPOSITION, VehicleArea.OUTSIDE_TOP);
    }

    public static void setSunShadeState(int value) {
//        CarPropUtils.getInstance().setIntProp(H56D.IVI_BodySet1_IVI_SUNROOFROLLSHADECTRLSELECT, VehicleArea.OUTSIDE_TOP, 2);//1是天窗，2是遮阳帘
//        CarPropUtils.getInstance().setIntProp(H56D.IVI_BodySet1_IVI_SUNROOF_HORIZONTALCONTROL, VehicleArea.OUTSIDE_TOP, value);

        if (value == 0) {
            CarPropUtils.getInstance().setIntProp(Signal.ID_SUNROOFCMDREQ, 6);
        } else if (value == 100) {
            CarPropUtils.getInstance().setIntProp(Signal.ID_SUNROOFCMDREQ, 5);
        }
    }
}
