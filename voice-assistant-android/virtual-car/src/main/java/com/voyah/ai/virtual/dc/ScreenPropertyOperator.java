package com.voyah.ai.virtual.dc;

import com.voice.sdk.device.carservice.constants.IScreen;
import com.voice.sdk.device.carservice.signal.ScreenSignal;
import com.voyah.ai.common.utils.BiDirectionalMap;

/**
 * @Date 2024/8/6 13:45
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class ScreenPropertyOperator extends BaseVirtualPropertyOperator {

    private final BiDirectionalMap<Integer, String> screenPosMap = new BiDirectionalMap<>();

    public ScreenPropertyOperator() {
        screenPosMap.put(IScreen.IScreenPos.DRIVER, "leftward");
        screenPosMap.put(IScreen.IScreenPos.MIDDLE, "mid_side");
        screenPosMap.put(IScreen.IScreenPos.PASSENGER, "rightward");
    }

    @Override
    public int getBaseIntProp(String key, int area) {
        switch (key) {
            case ScreenSignal.SCREEN_POS:
                String pos = (String) getValue(key);
                return screenPosMap.getReverse(pos.toLowerCase());
            default:
                return super.getBaseIntProp(key, area);
        }
    }

    @Override
    public void setBaseIntProp(String key, int area, int value) {
        switch (key) {
            case ScreenSignal.SCREEN_POS:
                String pos = screenPosMap.getForward(value);
                setValue(key, pos);
                break;
            default:
                super.setBaseIntProp(key, area, value);
        }
    }
}
