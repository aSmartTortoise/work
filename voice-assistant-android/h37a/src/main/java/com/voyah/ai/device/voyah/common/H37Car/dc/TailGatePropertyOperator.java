package com.voyah.ai.device.voyah.common.H37Car.dc;

import com.example.filter_annotation.CarDevices;
import com.example.filter_annotation.CarType;
import com.voice.sdk.device.carservice.signal.TailGateSignal;
import com.voyah.ai.device.voyah.common.H37Car.TailGateHelper;

/**
 * @Date 2024/8/12 15:00
 * @Author 8327821
 * @Email *
 * @Description .
 **/
@CarDevices(carType = CarType.H37_CAR)
public class TailGatePropertyOperator extends Base37Operator{
    @Override
    void init() {
        //
    }

    @Override
    public int getBaseIntProp(String key, int area) {
        if (TailGateSignal.TAILGATE_STATE.equals(key)) {
            return TailGateHelper.getTailGateState();
        }
        return super.getBaseIntProp(key, area);
    }

    @Override
    public void setBaseIntProp(String key, int area, int value) {
        if (TailGateSignal.TAILGATE_STATE.equals(key)) {
            TailGateHelper.setTailGateState(value);
        } else {
            super.setBaseIntProp(key, area, value);
        }
    }
}
