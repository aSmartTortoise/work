package com.voyah.ai.device.voyah.common.H37Car.dc;

import com.example.filter_annotation.CarDevices;
import com.example.filter_annotation.CarType;
import com.voice.sdk.device.carservice.signal.RefrigeratorSignal;
import com.voyah.ai.basecar.carservice.CarPropUtils;

import mega.car.Signal;

/**
 * @Date 2024/7/29 16:39
 * @Author 8327821
 * @Email *
 * @Description 冰箱
 **/
@CarDevices(carType = CarType.H37_CAR)
public class RefrigeratorPropertyOperator extends Base37Operator {
    @Override
    void init() {

    }

    @Override
    public int getBaseIntProp(String key, int area) {
        switch (key) {
            case RefrigeratorSignal.REFRIGERATOR_POWER:
                return CarPropUtils.getInstance().getIntProp(Signal.ID_CRF_ONOFF);
            case RefrigeratorSignal.REFRIGERATOR_MODE:
                return CarPropUtils.getInstance().getIntProp(Signal.ID_CRF_MODE);
            case RefrigeratorSignal.REFRIGERATOR_TEMP:
                return CarPropUtils.getInstance().getIntProp(Signal.ID_CRF_TEMP);
            case RefrigeratorSignal.REFRIGERATOR_WORK:
                return CarPropUtils.getInstance().getIntProp(Signal.ID_CRF_OFF_LINE_TIME);
            case RefrigeratorSignal.REFRIGERATOR_ENERGY:
                return CarPropUtils.getInstance().getIntProp(Signal.ID_CRF_PWR_MODE);
            case RefrigeratorSignal.REFRIGERATOR_KILL:
                return CarPropUtils.getInstance().getIntProp(Signal.ID_CRF_STZN);
            default:
                return super.getBaseIntProp(key, area);
        }
    }

    @Override
    public void setBaseIntProp(String key, int area, int value) {
        switch (key) {
            case RefrigeratorSignal.REFRIGERATOR_POWER:
                CarPropUtils.getInstance().setIntProp(Signal.ID_CRF_ONOFF, value);
                return;
            case RefrigeratorSignal.REFRIGERATOR_MODE:
                CarPropUtils.getInstance().setIntProp(Signal.ID_CRF_MODE, value);
                return;
            case RefrigeratorSignal.REFRIGERATOR_TEMP:
                CarPropUtils.getInstance().setIntProp(Signal.ID_CRF_TEMP,  value);
                return;
            case RefrigeratorSignal.REFRIGERATOR_WORK:
                CarPropUtils.getInstance().setIntProp(Signal.ID_CRF_OFF_LINE_TIME, value);
                return;
            case RefrigeratorSignal.REFRIGERATOR_ENERGY:
                CarPropUtils.getInstance().setIntProp(Signal.ID_CRF_PWR_MODE, value);
                return;
            case RefrigeratorSignal.REFRIGERATOR_KILL:
                CarPropUtils.getInstance().setIntProp(Signal.ID_CRF_STZN, value);
                return;
            default:
                super.setBaseIntProp(key, area, value);
        }
    }

}
