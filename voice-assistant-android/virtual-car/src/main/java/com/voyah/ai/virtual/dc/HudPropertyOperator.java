package com.voyah.ai.virtual.dc;

import com.voice.sdk.device.carservice.constants.IHud;
import com.voice.sdk.device.carservice.signal.HudSignal;
import com.voyah.ai.common.utils.BiDirectionalMap;

/**
 * @Date 2024/8/6 14:45
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class HudPropertyOperator extends BaseVirtualPropertyOperator {

    private final BiDirectionalMap<Integer, String> hudModeMap = new BiDirectionalMap<>();

    public HudPropertyOperator() {
        hudModeMap.put(IHud.HudMode.SIMPLEST, "simplest");
        hudModeMap.put(IHud.HudMode.NAVI, "navi");
        hudModeMap.put(IHud.HudMode.INTELLIGENT_DRIVING, "intelligent_driving");
        hudModeMap.put(IHud.HudMode.STANDARD, "standard");
        hudModeMap.put(IHud.HudMode.AR, "ar");
    }

    @Override
    public int getBaseIntProp(String key, int area) {
        switch (key) {
            case HudSignal.HUD_MODE:
                String mode = (String) getValue(key);
                return hudModeMap.getReverse(mode.toLowerCase());
            default:
                return super.getBaseIntProp(key, area);
        }
    }

    @Override
    public void setBaseIntProp(String key, int area, int value) {
        switch (key) {
            case HudSignal.HUD_MODE:
                String mode = hudModeMap.getForward(value);
                setValue(key, mode);
                break;
            default:
                super.setBaseIntProp(key, area, value);
        }
    }
}
