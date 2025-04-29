package com.voyah.ai.device.voyah.common.H37Car.dc;

import com.example.filter_annotation.CarDevices;
import com.example.filter_annotation.CarType;
import com.mega.ecu.MegaProperties;
import com.mega.nexus.os.MegaSystemProperties;
import com.voice.sdk.device.carservice.signal.VehicleConditionSignal;
import com.voyah.ai.device.voyah.common.H37Car.VehicleConditionHelper;
import com.voyah.ai.basecar.carservice.CarPropUtils;

import mega.car.Signal;
import mega.car.config.ElecPower;

/**
 * @Date 2024/9/13 17:28
 * @Author 8327821
 * @Email *
 * @Description .
 **/
@CarDevices(carType = CarType.H37_CAR)
public class VehicleConditionPropertyOperator extends Base37Operator {
    @Override
    void init() {
        map.put(VehicleConditionSignal.CONDITION_REMAIN_MILEAGE, ElecPower.ID_REMAINING_MILEAGE_STANDARD);
        map.put(VehicleConditionSignal.CONDITION_REAL_REMAIN_MILEAGE, ElecPower.ID_REMAINING_MILEAGE_REAL);
        map.put(VehicleConditionSignal.CONDITION_REMAIN_BATTERY, ElecPower.ID_HV_PERCENT);
    }

    @Override
    public int getBaseIntProp(String key, int area) {
        if (VehicleConditionSignal.CONDITION_MAX_MILEAGE.equals(key)) {
            return VehicleConditionHelper.getMaximumRange();
        } if (VehicleConditionSignal.CONDITION_CONFIG_EQUIPMENT_LEVEL.equals(key)) {
            return MegaSystemProperties.getInt(MegaProperties.CONFIG_EQUIPMENT_LEVEL, -1);
        } else if (VehicleConditionSignal.CONDITION_REMAIN_MILEAGE.equalsIgnoreCase(key)) {
            return VehicleConditionHelper.getRemainMileage();
        }
        return super.getBaseIntProp(key, area);
    }

    @Override
    public float getBaseFloatProp(String key, int area) {
        if (VehicleConditionSignal.CONDITION_TRAVELED_MILEAGE.equalsIgnoreCase(key)) {
            return CarPropUtils.getInstance().getFloatProp(Signal.ID_TOTAL_MILEAGE);
        } else if (VehicleConditionSignal.CONDITION_REMAIN_BATTERY.equalsIgnoreCase(key)) {
            return CarPropUtils.getInstance().getFloatProp(ElecPower.ID_HV_PERCENT);
        } else {
            return super.getBaseFloatProp(key, area);
        }
    }

    @Override
    public void setBaseIntProp(String key, int area, int value) {
        if (VehicleConditionSignal.CONDITION_OPEN_VEHICLE_HEALTH.equalsIgnoreCase(key)) {
            VehicleConditionHelper.openCarHealthPage(value);
        } else {
            super.setBaseIntProp(key, area, value);
        }
    }

    @Override
    public void setBaseFloatProp(String key, int area, float value) {
        if (VehicleConditionSignal.CONDITION_TRAVELED_MILEAGE.equalsIgnoreCase(key)) {
            CarPropUtils.getInstance().setFloatProp(Signal.ID_TOTAL_MILEAGE, getRealArea(area), value);
        } else {
            super.setBaseFloatProp(key, area, value);
        }
    }

    @Override
    public boolean getBaseBooleanProp(String key, int area) {
        if (VehicleConditionSignal.CONDITION_IS_CHARGING.equals(key)) {
            int intProp = CarPropUtils.getInstance().getIntProp(ElecPower.ID_CHARGE_STATE);
            return intProp == Signal.ParamsChargStateDisp.CSD_CHARGING || intProp == Signal.ParamsChargStateDisp.CSD_CHARGE_FINISH;
        } else {
            return super.getBaseBooleanProp(key, area);
        }
    }

}
