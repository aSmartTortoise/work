package com.voyah.ai.device.voyah.common.H56Car.dc;

import com.example.filter_annotation.CarDevices;
import com.example.filter_annotation.CarType;
import com.mega.ecu.MegaProperties;
import com.mega.nexus.os.MegaSystemProperties;
import com.voice.sdk.device.carservice.signal.SteeringWheelSignal;
import com.voyah.ai.basecar.utils.SteeringWheelUtils;

import mega.car.config.Comforts;
import mega.car.config.Driving;


/**
 * @Date 2024/7/25 14:01
 * @Author 8327821
 * @Email *
 * @Description .
 **/
@CarDevices(carType = CarType.H37_CAR)
public class SteeringWheelPropertyOperator extends Base56Operator {

    @Override
    void init() {
        map.put(SteeringWheelSignal.STEERING_WHEEL_DRV_EPS_MODESET, Driving.ID_DRV_EPS_MODESET);
        map.put(SteeringWheelSignal.STEERING_WHEEL_HEAT, Comforts.ID_STEERING_WHEEL_HEATING);
    }

    @Override
    public int getBaseIntProp(String key, int area) {
        if (key.equals(SteeringWheelSignal.STEERING_WHEEL_CUSTOM_TYPE)) {
            return SteeringWheelUtils.getCustomType();
        }
        return super.getBaseIntProp(key, area);
    }

    @Override
    public void setBaseIntProp(String key, int area, int value) {
        if (key.equals(SteeringWheelSignal.STEERING_WHEEL_CUSTOM_TYPE)) {
            SteeringWheelUtils.setCustomType(value);
        } else {
            super.setBaseIntProp(key, area, value);
        }
    }

    @Override
    public boolean getBaseBooleanProp(String key, int area) {
        if (key.equals(SteeringWheelSignal.STEERING_WHEEL_CONFIG)) {
            return MegaSystemProperties.getInt(MegaProperties.CONFIG_STEERING_HEAT, 0) == 1;
        }
        return super.getBaseBooleanProp(key, area);
    }
}
