package com.voyah.ai.device.voyah.common.H37Car.dc;

import com.example.filter_annotation.CarDevices;
import com.example.filter_annotation.CarType;
import com.voice.sdk.device.carservice.signal.LightSignal;
import com.voyah.ai.basecar.carservice.CarPropUtils;

import mega.car.VehicleArea;
import mega.car.config.Lighting;

/**
 * @Date 2024/8/1 10:29
 * @Author 8327821
 * @Email *
 * @Description .
 **/
@CarDevices(carType = CarType.H37_CAR)
public class OutLightPropertyOperator extends Base37Operator {

    @Override
    void init() {
        map.put(LightSignal.LAMP_MODE, Lighting.ID_EXT_LIGHT_MODE);
        map.put(LightSignal.LAMP_FOG, Lighting.ID_TELLTALE_FOG_LAMP);
        map.put(LightSignal.LAMP_SWITCH, Lighting.ID_TELLTALE_LOW_BEAM);
        map.put(LightSignal.LAMP_HEIGHT, Lighting.ID_EXT_LIGHT_HEIGHT_AUTO);
        map.put(LightSignal.LAMP_POSITION, Lighting.ID_EXT_LIGHT_HEIGHT_AUTO);
    }

    @Override
    public int getBaseIntProp(String key, int area) {
        if (key.equals(LightSignal.LAMP_SWITCH)) {
            return CarPropUtils.getInstance().getIntProp(Lighting.ID_TELLTALE_LOW_BEAM, VehicleArea.FRONT_LEFT);
        } else if (key.equals(LightSignal.LAMP_POSITION)) {
            return CarPropUtils.getInstance().getIntProp(Lighting.ID_TELLTALE_STS_POSITION_LIGHT_ON_OFF_LAMP,VehicleArea.OUTSIDE_REAR);
        }else {
            return super.getBaseIntProp(key, area);
        }
    }

    @Override
    public void setBaseIntProp(String key, int area, int value) {
        super.setBaseIntProp(key, area, value);
    }
}
