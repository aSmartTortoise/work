package com.voyah.ai.device.voyah.common.H56Car.dc;

import com.example.filter_annotation.CarDevices;
import com.example.filter_annotation.CarType;
import com.voice.sdk.device.carservice.signal.TailGateSignal;
import com.voyah.ai.device.voyah.common.H56Car.TailGateHelper;

import mega.car.config.H56D;

/**
 * @Date 2024/8/12 15:00
 * @Author 8327821
 * @Email *
 * @Description .
 **/
@CarDevices(carType = CarType.H37_CAR)
public class TailGatePropertyOperator extends Base56Operator {
    @Override
    void init() {
        map.put(TailGateSignal.TAILGATE_UNOPERABLE_STATE, H56D.POT_state_POT_ANTIPLAYWARNING);
    }

    @Override
    public int getBaseIntProp(String key, int area) {
        if (TailGateSignal.TAILGATE_STATE.equalsIgnoreCase(key)) {
            return TailGateHelper.getTailGateState();
        }
        return super.getBaseIntProp(key, area);
    }

    @Override
    public void setBaseIntProp(String key, int area, int value) {
        if (TailGateSignal.TAILGATE_STATE.equalsIgnoreCase(key)) {
            TailGateHelper.setTailGateState(value);
        }
    }

    @Override
    public void setBaseBooleanProp(String key, int area, boolean value) {
        if (TailGateSignal.TAILGATE_SHOW_UNOPERABLE.equalsIgnoreCase(key)) {
            TailGateHelper.showUnoperableDiglog();
        } else {
            super.setBaseBooleanProp(key, area, value);
        }
    }
}
