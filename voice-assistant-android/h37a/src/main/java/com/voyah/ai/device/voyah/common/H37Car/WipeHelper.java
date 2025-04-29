package com.voyah.ai.device.voyah.common.H37Car;

import com.voyah.ai.basecar.carservice.CarPropUtils;

import mega.car.Signal;
import mega.car.hardware.CarPropertyValue;

/**
 * @Date 2024/7/17 16:24
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class WipeHelper {

    public static int getWipeActionState() {
        CarPropertyValue wipeWorkState = CarPropUtils.getInstance().getPropertyRaw(Signal.ID_FRONT_WIPER_WORK_STATUS);
        if (wipeWorkState != null && wipeWorkState.getValue() instanceof Integer[]) {
            Integer[] integers = (Integer[]) wipeWorkState.getValue();
            return integers[0];
        }
        return Signal.ParamsWiperaction.WIPERACTSTS_ON;
    }
}
