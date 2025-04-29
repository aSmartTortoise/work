package com.voyah.ai.device.voyah.common.H37Car.dc;

import com.example.filter_annotation.CarDevices;
import com.example.filter_annotation.CarType;
import com.voice.sdk.device.carservice.signal.SunroofSignal;
import com.voyah.ai.device.voyah.common.H37Car.SunRoofHelper;

/**
 * @Date 2024/7/25 14:01
 * @Author 8327821
 * @Email *
 * @Description .
 **/
@CarDevices(carType = CarType.H37_CAR)

public class SunroofPropertyOperator extends Base37Operator {

    @Override
    void init() {

    }

    @Override
    public int getBaseIntProp(String key, int area) {
        if (key.equals(SunroofSignal.SUNROOF_WINDOW)) {
            return SunRoofHelper.getSunRoofState();
        } else {
            return super.getBaseIntProp(key, area);
        }
    }

    @Override
    public void setBaseIntProp(String key, int area, int value) {
        if (key.equals(SunroofSignal.SUNROOF_WINDOW)) {
            SunRoofHelper.setSunRoofState(value);
        } else {
            super.setBaseIntProp(key, area, value);
        }
    }

    @Override
    public boolean getBaseBooleanProp(String key, int area) {
        if (SunroofSignal.SUNROOF_VENTILATION.equals(key)) {
            return SunRoofHelper.getSunRoofVentilation();
        } else {
            return super.getBaseBooleanProp(key, area);
        }
    }

    @Override
    public void setBaseBooleanProp(String key, int area, boolean value) {
        if (SunroofSignal.SUNROOF_VENTILATION.equals(key)) {
            SunRoofHelper.setSunRoofVentilation(value);
        } else {
            super.setBaseBooleanProp(key, area, value);
        }
    }
}
