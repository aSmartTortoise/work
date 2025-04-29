package com.voyah.ai.device.voyah.common.H37Car.dc;

import com.example.filter_annotation.CarDevices;
import com.example.filter_annotation.CarType;
import com.voice.sdk.device.carservice.constants.ICommon;
import com.voice.sdk.device.carservice.signal.ReadingLightSignal;

import mega.car.config.Lighting;

/**
 * @Date 2024/8/1 14:14
 * @Author 8327821
 * @Email *
 * @Description .
 **/
@CarDevices(carType = CarType.H37_CAR)
public class ReadingLightPropertyOperator extends Base37Operator {
    @Override
    void init() {
        map.put(ReadingLightSignal.READING_LIGHT_SWITCH, Lighting.ID_CABIN_LIGHT_ON_OFF);
    }


    @Override
    public void setBaseIntProp(String key, int area, int value) {
        if (ReadingLightSignal.READING_LIGHT_SWITCH.equals(key)
                && ICommon.Switch.ON == value) {
            //打开阅读灯 ReadingLampLevel.HIGHLEVEL = 2   需要+1
            setCommonInt(key, area,value + 1);
        } else {
            super.setBaseIntProp(key, area, value);
        }
    }
}
