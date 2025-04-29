package com.voyah.ai.device.voyah.common.H37Car.dc;

import com.example.filter_annotation.CarDevices;
import com.example.filter_annotation.CarType;
import com.voice.sdk.device.carservice.signal.SunShadeSignal;
import com.voyah.ai.device.voyah.common.H37Car.SunShadeHelper;

/**
 * @Date 2024/7/25 14:01
 * @Author 8327821
 * @Email *
 * @Description .
 **/
@CarDevices(carType = CarType.H37_CAR)

public class SunShadePropertyOperator extends Base37Operator {

    @Override
    void init() {

    }

    @Override
    public int getBaseIntProp(String key, int area) {
        if (SunShadeSignal.SUNSHADE_STATE.equals(key)) {
            return SunShadeHelper.getSunShadeState();
        } else {
            return super.getBaseIntProp(key, area);
        }
    }

    @Override
    public void setBaseIntProp(String key, int area, int value) {
        if (SunShadeSignal.SUNSHADE_STATE.equals(key)) {
            SunShadeHelper.setSunShadeState(value);
        } else {
            super.setBaseIntProp(key, area, value);
        }
    }
}
