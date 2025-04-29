package com.voyah.ai.device.voyah.common.H56Car.dc;

import com.example.filter_annotation.CarDevices;
import com.example.filter_annotation.CarType;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.carservice.signal.OmsSignal;

import mega.car.config.Dms;

/**
 * @Date 2024/7/31 14:46
 * @Author 8327821
 * @Email *
 * @Description .
 **/
@CarDevices(carType = CarType.H37_CAR)
public class OmsPropertyOperator extends Base56Operator {
    @Override
    void init() {
        map.put(OmsSignal.OMS_FORGET_REMIND, Dms.ID_LIFEFORGREMIND_ONOFF);
    }


    @Override
    public void setBaseIntProp(String key, int area, int value) {
        setCommonInt(key, area, value);
    }

    @Override
    public boolean getBaseBooleanProp(String key, int area) {
        if (OmsSignal.OMS_CONFIG.equalsIgnoreCase(key)) {
            return getIntProp(CommonSignal.COMMON_EQUIPMENT_LEVEL) >= 2; //N2以上支持
        } else {
            return super.getBaseBooleanProp(key, area);
        }
    }
}
