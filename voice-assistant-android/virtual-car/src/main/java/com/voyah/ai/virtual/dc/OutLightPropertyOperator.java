package com.voyah.ai.virtual.dc;

import com.voice.sdk.device.carservice.constants.ILamp;
import com.voice.sdk.device.carservice.signal.LightSignal;
import com.voyah.ai.common.utils.BiDirectionalMap;

/**
 * @Date 2024/8/12 19:02
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class OutLightPropertyOperator extends BaseVirtualPropertyOperator {

    private final BiDirectionalMap<Integer, String> map = new BiDirectionalMap<>();
    public OutLightPropertyOperator() {
        map.put(ILamp.IMode.DEFAULT, "default");
        map.put(ILamp.IMode.OFF, "off");
        map.put(ILamp.IMode.POSITION, "position");
        map.put(ILamp.IMode.LOW, "low");
        map.put(ILamp.IMode.AUTO, "auto");
    }

    @Override
    public int getBaseIntProp(String key, int area) {
        if (LightSignal.LAMP_MODE.equals(key)) {
            String mode = (String) getValue(key);
            return map.getReverse(mode.toLowerCase());
        } else if (LightSignal.LAMP_FOG.equals(key)) {
            return (Integer) getValue(key);
        } else {
            return super.getBaseIntProp(key, area);
        }
    }

    @Override
    public void setBaseIntProp(String key, int area, int value) {
        if (LightSignal.LAMP_MODE.equals(key)) {
            String mode = map.getForward(value);
            setValue(key, mode);
        } else if (LightSignal.LAMP_FOG.equals(key)) {
            setValue(key, value);
        } else {
            super.setBaseIntProp(key, area, value);
        }
    }
}
