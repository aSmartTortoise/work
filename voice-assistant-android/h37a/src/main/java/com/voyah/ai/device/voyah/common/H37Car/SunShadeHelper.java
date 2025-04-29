package com.voyah.ai.device.voyah.common.H37Car;

import com.voyah.ai.basecar.carservice.CarPropUtils;

import mega.car.VehicleArea;
import mega.car.config.Windows;

/**
 * @Date 2024/8/16 14:15
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class SunShadeHelper {

    public static int getSunShadeState() {
        return CarPropUtils.getInstance().getIntProp(Windows.ID_SUNSHADE, VehicleArea.FRONT_ROW);
    }

    public static void setSunShadeState(int value) {
        CarPropUtils.getInstance().setIntProp(Windows.ID_SUNSHADE, VehicleArea.FRONT_ROW, value);
    }
}
