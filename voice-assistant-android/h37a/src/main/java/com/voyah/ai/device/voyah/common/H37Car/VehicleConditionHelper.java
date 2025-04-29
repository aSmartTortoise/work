package com.voyah.ai.device.voyah.common.H37Car;

import android.content.ComponentName;
import android.content.Intent;

import com.blankj.utilcode.util.Utils;
import com.mega.ecu.MegaProperties;
import com.mega.nexus.os.MegaSystemProperties;
import com.voice.sdk.device.carservice.constants.ICarSetting;
import com.voyah.ai.basecar.carservice.CarPropUtils;
import com.voyah.ai.basecar.carservice.CarServicePropUtils;

import mega.car.Signal;
import mega.car.config.Driving;
import mega.car.config.ElecPower;

public class VehicleConditionHelper {

    private static final String TAG = "VehicleConditionHelper";

    public static int getRemainMileage() {
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            int mileageMode = ICarSetting.RemainMileage.STANDARD;
            Integer[] prop = (Integer[]) CarPropUtils.getInstance().getPropertyRaw(Signal.ID_REMAINGMILEAGE_MODE_ARRAY).getValue();
            if (prop != null && prop.length == 2) {
                int cltcOrStandard = prop[1] == null ? 0 : prop[1];
                if (0 == cltcOrStandard) {
                    //CLTC工况
                    mileageMode =  ICarSetting.RemainMileage.STANDARD;
                } else {
                    //综合工况
                    mileageMode =  ICarSetting.RemainMileage.REAL;
                }
            }
            if (mileageMode == 1) {
                return Math.round(CarPropUtils.getInstance().getFloatProp(ElecPower.ID_REMAINING_MILEAGE_REAL));
            } else {
                return Math.round(CarPropUtils.getInstance().getFloatProp(ElecPower.ID_REMAINING_MILEAGE_STANDARD));
            }
        } else {
            int mode = CarPropUtils.getInstance().getIntProp(Driving.ID_REMAINGMILEAGE_MODE);
            if (mode == 1) {
                return Math.round(CarPropUtils.getInstance().getFloatProp(ElecPower.ID_REMAINING_MILEAGE_REAL));
            } else {
                return Math.round(CarPropUtils.getInstance().getFloatProp(ElecPower.ID_REMAINING_MILEAGE_STANDARD));
            }
        }
    }

    public static int getMaximumRange() {
        int number;
        int vehicleLevel = MegaSystemProperties.getInt(MegaProperties.CONFIG_EQUIPMENT_LEVEL, -1);
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equalsIgnoreCase("H37A")) {
            if (vehicleLevel == 2) {
                number = 570;
            } else if (vehicleLevel == 3) {
                number = 650;
            } else if (vehicleLevel == 4) {
                number = 901;
            } else {
                number = 625;
            }
        } else {
            if (vehicleLevel == 2) {
                number = 600;
            } else if (vehicleLevel == 3) {
                number = 900;
            } else {
                number = 650;
            }
        }
        return number;
    }

    public static void openCarHealthPage(int value) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.voyah.vehiclehealth", "com.voyah.vehiclehealth.feature.main.MainActivity"));
        intent.putExtra("route_path", value);
        Utils.getApp().startActivity(intent);
    }

}
