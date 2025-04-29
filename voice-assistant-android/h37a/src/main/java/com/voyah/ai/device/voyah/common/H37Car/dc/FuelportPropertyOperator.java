package com.voyah.ai.device.voyah.common.H37Car.dc;

import com.example.filter_annotation.CarDevices;
import com.example.filter_annotation.CarType;
import com.voice.sdk.device.carservice.signal.FuelPortSignal;
import com.voyah.ai.basecar.carservice.CarPropUtils;

import mega.car.config.EntryLocks;
import mega.car.config.ParamsCommon;

/**
 * @Date 2024/7/31 14:07
 * @Author 8327821
 * @Email *
 * @Description .
 **/
@CarDevices(carType = CarType.H37_CAR)
public class FuelportPropertyOperator extends Base37Operator {
    @Override
    void init() {
        map.put(FuelPortSignal.FUEL_PORT_SWITCH, EntryLocks.ID_FUEL_PORT);
    }

    @Override
    public int getBaseIntProp(String key, int area) {
        return super.getBaseIntProp(key, area);
    }

    @Override
    public boolean getBaseBooleanProp(String key, int area) {
        if (key.equalsIgnoreCase(FuelPortSignal.FUEL_PORT_SWITCH)) {
            return CarPropUtils.getInstance().getIntProp(EntryLocks.ID_FUEL_PORT) == ParamsCommon.OpenClose.OPENED;
        } else {
            return super.getBaseBooleanProp(key, area);
        }
    }

    @Override
    public void setBaseBooleanProp(String key, int area, boolean value) {
        if (key.equalsIgnoreCase(FuelPortSignal.FUEL_PORT_SWITCH)) {
            CarPropUtils.getInstance().setIntProp(EntryLocks.ID_FUEL_PORT, value ? ParamsCommon.OpenClose.OPENED : ParamsCommon.OpenClose.CLOSED);
        } else {
            super.setBaseBooleanProp(key, area, value);
        }
    }
}
