package com.voyah.ai.device.voyah.common.H56Car;

import com.voice.sdk.device.carservice.constants.ICarSetting;
import com.voyah.ai.basecar.carservice.CarPropUtils;
import com.voyah.ai.basecar.carservice.CarServicePropUtils;

import mega.car.config.Driving;
import mega.car.config.ElecPower;
import mega.car.config.Qnx;


public class VehicleConditionHelper {

    private static final String TAG = "VehicleConditionHelper";

    public static int getRemainMileageMode() {
        return CarPropUtils.getInstance().getIntProp(Driving.ID_REMAINING_MILEAGESWITCH);
    }

    public static int getRemainMileage() {
        int mode = CarPropUtils.getInstance().getIntProp(Driving.ID_REMAINING_MILEAGESWITCH);
        if (mode == 1) {
            return Math.round(CarPropUtils.getInstance().getFloatProp(ElecPower.ID_REMAINING_MILEAGE_REAL));
        } else {
            return Math.round(CarPropUtils.getInstance().getFloatProp(ElecPower.ID_REMAINING_MILEAGE_STANDARD));
        }
    }

    public static float getAllRange() {
        return CarPropUtils.getInstance().getFloatProp(Qnx.ID_TOTAL_DRIVE_DISTANCE);
    }
}
