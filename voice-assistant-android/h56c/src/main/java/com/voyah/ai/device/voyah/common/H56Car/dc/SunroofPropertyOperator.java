package com.voyah.ai.device.voyah.common.H56Car.dc;

import com.example.filter_annotation.CarDevices;
import com.example.filter_annotation.CarType;
import com.voice.sdk.device.carservice.signal.SunroofSignal;

import com.voyah.ai.device.voyah.common.H56Car.SunRoofHelper;


import mega.car.config.H56C;
import mega.car.config.Signal;

/**
 * @Date 2024/7/25 14:01
 * @Author 8327821
 * @Email *
 * @Description . 天窗
 **/
@CarDevices(carType = CarType.H37_CAR)

public class SunroofPropertyOperator extends Base56Operator {

    @Override
    void init() {
        map.put(SunroofSignal.SUNROOF_SETSUNROOFAIR, Signal.ID_SUNROOFCMDREQ);//设置成天窗翘起
        map.put(SunroofSignal.SUNROOF_ISSUNROOFAIR, H56C.GW_LIN_BODY_SRM_SRPOSITION);//天窗目前的状态
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
            setCommonInt(key, area, value);
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

}
