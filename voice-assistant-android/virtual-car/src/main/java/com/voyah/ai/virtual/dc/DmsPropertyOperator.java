package com.voyah.ai.virtual.dc;

import com.voice.sdk.device.carservice.constants.IDms;
import com.voice.sdk.device.carservice.signal.DmsSignal;
import com.voyah.ai.common.utils.BiDirectionalMap;

/**
 * @Date 2024/8/7 10:32
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class DmsPropertyOperator extends BaseVirtualPropertyOperator {

    private final BiDirectionalMap<Integer, String> fatigueMap = new BiDirectionalMap<>();

    public DmsPropertyOperator() {
        fatigueMap.put(IDms.IFatigueMonitor.OFF, "off");
        fatigueMap.put(IDms.IFatigueMonitor.NORMAL, "standard");
        fatigueMap.put(IDms.IFatigueMonitor.SENSITIVE, "sensitive");
    }

    @Override
    public int getBaseIntProp(String key, int area) {
        switch (key) {
            case DmsSignal.DMS_FATIGUE_MONITOR:
                String mode = (String) getValue(key);
                return fatigueMap.getReverse(mode.toLowerCase());
            default:
                return super.getBaseIntProp(key, area);
        }
    }

    @Override
    public void setBaseIntProp(String key, int area, int value) {
        switch (key) {
            case DmsSignal.DMS_FATIGUE_MONITOR:
                String mode = fatigueMap.getForward(value);
                setValue(key, mode);
                break;
            default:
                super.setBaseIntProp(key, area, value);
        }
    }
}
