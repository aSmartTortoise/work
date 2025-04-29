package com.voyah.ai.device.voyah.common.H56Car.dc;

import com.example.filter_annotation.CarDevices;
import com.example.filter_annotation.CarType;
import com.voice.sdk.device.carservice.signal.RefrigeratorSignal;
import com.voyah.ai.basecar.carservice.CarPropUtils;

import mega.car.config.H56D;

/**
 * @Date 2024/7/29 16:39
 * @Author 8327821
 * @Email *
 * @Description 冰箱
 **/
@CarDevices(carType = CarType.H37_CAR)
public class RefrigeratorPropertyOperator extends Base56Operator {
    @Override
    void init() {

    }

    @Override
    public int getBaseIntProp(String key, int area) {
        switch (key) {
            case RefrigeratorSignal.REFRIGERATOR_DOOR:
                return CarPropUtils.getInstance().getIntProp(H56D.CRF_Fridge_St_CRFDOORONOFFST);
            case RefrigeratorSignal.REFRIGERATOR_POWER:
                return CarPropUtils.getInstance().getIntProp(H56D.CRF_Fridge_St_CRFONOFFST);
            case RefrigeratorSignal.REFRIGERATOR_MODE:
                return CarPropUtils.getInstance().getIntProp(H56D.CRF_Fridge_St_CRF_FRIDGEMODEST);
            case RefrigeratorSignal.REFRIGERATOR_TEMP:
                return CarPropUtils.getInstance().getIntProp(H56D.CRF_Fridge_St_CRFTEMPADJST);
            case RefrigeratorSignal.REFRIGERATOR_LOCK:
                return CarPropUtils.getInstance().getIntProp(H56D.CRF_Fridge_St_CRFCHILDLOCKST);
            case RefrigeratorSignal.REFRIGERATOR_WORK:
                return CarPropUtils.getInstance().getIntProp(H56D.CRF_Fridge_St_CRFOFFLINEWORKST);
            case RefrigeratorSignal.REFRIGERATOR_ENERGY:
                return CarPropUtils.getInstance().getIntProp(H56D.CRF_Fridge_St_CRFPWRMODST);
            default:
                return super.getBaseIntProp(key, area);
        }
    }

    @Override
    public void setBaseIntProp(String key, int area, int value) {
        switch (key) {
            case RefrigeratorSignal.REFRIGERATOR_DOOR:
                CarPropUtils.getInstance().setIntProp(H56D.IVI_Fridge_Req_IVI_CRFDOORONOFFREQ, value);
                return;
            case RefrigeratorSignal.REFRIGERATOR_POWER:
                CarPropUtils.getInstance().setIntProp(H56D.IVI_Fridge_Req_IVI_CRFONOFFREQ, value);
                return;
            case RefrigeratorSignal.REFRIGERATOR_MODE:
                CarPropUtils.getInstance().setIntProp(H56D.IVI_Fridge_Req_IVI_CRFMODADJREQ, value);
                return;
            case RefrigeratorSignal.REFRIGERATOR_TEMP:
                CarPropUtils.getInstance().setIntProp(H56D.IVI_Fridge_Req_IVI_CRFTEMPADJREQ,  value);
                return;
            case RefrigeratorSignal.REFRIGERATOR_LOCK:
                CarPropUtils.getInstance().setIntProp(H56D.IVI_Fridge_Req_IVI_CRFCHILDLOCKREQ, value);
                return;
            case RefrigeratorSignal.REFRIGERATOR_WORK:
                CarPropUtils.getInstance().setIntProp(H56D.IVI_Fridge_Req_IVI_CRFOFFLINEMODREQ, value);
                return;
            case RefrigeratorSignal.REFRIGERATOR_ENERGY:
                CarPropUtils.getInstance().setIntProp(H56D.IVI_Fridge_Req_IVI_CRFPWRMODTREQ, value);
            default:
                super.setBaseIntProp(key, area, value);
        }
    }

}
