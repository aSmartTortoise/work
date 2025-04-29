package com.voyah.ai.device.voyah.common.H56Car.dc;

//import static mega.car.Signal.ID_TOTAL_MILEAGE;

import static com.blankj.utilcode.util.ActivityUtils.startActivity;

import android.content.ComponentName;
import android.content.Intent;

import com.example.filter_annotation.CarDevices;
import com.example.filter_annotation.CarType;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.carservice.signal.VehicleConditionSignal;
import com.voyah.ai.basecar.carservice.CarPropUtils;
import com.voyah.ai.device.voyah.common.H56Car.VehicleConditionHelper;


import mega.car.config.ElecPower;

/**
 * @Date 2024/9/13 17:28
 * @Author 8327821
 * @Email *
 * @Description .
 **/
@CarDevices(carType = CarType.H37_CAR)
public class VehicleConditionPropertyOperator extends Base56Operator {
    @Override
    void init() {
        map.put(VehicleConditionSignal.CONDITION_REMAIN_MILEAGE, ElecPower.ID_REMAINING_MILEAGE_STANDARD);
        map.put(VehicleConditionSignal.CONDITION_REAL_REMAIN_MILEAGE, ElecPower.ID_REMAINING_MILEAGE_REAL);
    }

    @Override
    public int getBaseIntProp(String key, int area) {
        if (VehicleConditionSignal.CONDITION_REMAIN_MILEAGE_MODE.equals(key)) {
            return VehicleConditionHelper.getRemainMileageMode();
        } else if (VehicleConditionSignal.CONDITION_REMAIN_MILEAGE.equalsIgnoreCase(key)) {
            return VehicleConditionHelper.getRemainMileage();
        } else if (VehicleConditionSignal.CONDITION_MAX_MILEAGE.equalsIgnoreCase(key)) {
            if (getIntProp(CommonSignal.COMMON_POWER_MODER) == 0) {
                return 650;
            } else {
                return 1411;
            }
        }
        return super.getBaseIntProp(key, area);
    }

    @Override
    public float getBaseFloatProp(String key, int area) {
        if (VehicleConditionSignal.CONDITION_TRAVELED_MILEAGE.equalsIgnoreCase(key)) {
            return VehicleConditionHelper.getAllRange();
        } else {
            return super.getBaseFloatProp(key, area);
        }
    }

    @Override
    public void setBaseFloatProp(String key, int area, float value) {
        super.setBaseFloatProp(key, area, value);
    }

    @Override
    public void setBaseIntProp(String key, int area, int value) {
        if (VehicleConditionSignal.CONDITION_OPEN_VEHICLE_HEALTH.equalsIgnoreCase(key)) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.voyah.vehiclehealth", "com.voyah.vehiclehealth.feature.main.MainActivity"));
            intent.putExtra("route_path", value);
            startActivity(intent);
        } else {
            super.setBaseIntProp(key, area, value);
        }
    }

    @Override
    public boolean getBaseBooleanProp(String key, int area) {
        if (VehicleConditionSignal.CONDITION_IS_CHARGING.equals(key)) {
            int intProp = CarPropUtils.getInstance().getIntProp(ElecPower.ID_CHARGE_CONNECT_LAMP);
            return intProp > 1 && intProp < 7;
        } else {
            return super.getBaseBooleanProp(key, area);
        }
    }

}
