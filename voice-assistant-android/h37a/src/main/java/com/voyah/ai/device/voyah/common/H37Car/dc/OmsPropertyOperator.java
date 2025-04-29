package com.voyah.ai.device.voyah.common.H37Car.dc;

import com.example.filter_annotation.CarDevices;
import com.example.filter_annotation.CarType;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.signal.OmsSignal;

import org.apache.commons.lang3.StringUtils;

import mega.car.SignalH53B;
import mega.car.config.Dms;

/**
 * @Date 2024/7/31 14:46
 * @Author 8327821
 * @Email *
 * @Description .
 **/
@CarDevices(carType = CarType.H37_CAR)
public class OmsPropertyOperator extends Base37Operator {
    @Override
    void init() {
        String carType;
        carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();
        if (StringUtils.equals(carType,"H37B")) {
            map.put(OmsSignal.OMS_FORGET_REMIND, SignalH53B.ID_OMS_LEAVE_OVER_SWITCH);
        }else {
            map.put(OmsSignal.OMS_FORGET_REMIND, Dms.ID_LIFEFORGREMIND_ONOFF);
        }
    }

    @Override
    public int getBaseIntProp(String key, int area) {
        return super.getBaseIntProp(key, area);
    }

    @Override
    public void setBaseIntProp(String key, int area, int value) {
        super.setBaseIntProp(key, area, value);
    }

}
