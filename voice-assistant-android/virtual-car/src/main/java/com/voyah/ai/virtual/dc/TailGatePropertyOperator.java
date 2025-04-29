package com.voyah.ai.virtual.dc;

import com.voice.sdk.device.carservice.constants.ITailGate;
import com.voice.sdk.device.carservice.signal.TailGateSignal;
import com.voyah.ai.common.utils.BiDirectionalMap;

/**
 * @Date 2024/8/12 16:00
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class TailGatePropertyOperator extends BaseVirtualPropertyOperator {
    public final BiDirectionalMap<Integer, String> map = new BiDirectionalMap<>();

    public TailGatePropertyOperator() {
        map.put(ITailGate.TAILGATE_FULLYCLOSED, "fully_closed");
        map.put(ITailGate.TAILGATE_FULLYOPENED, "fully_opened");
        map.put(ITailGate.TAILGATE_OPENING, "opening");
        map.put(ITailGate.TAILGATE_CLOSING, "closing");
        map.put(ITailGate.TAILGATE_STOPPED, "stopped");
        map.put(ITailGate.TAILGATE_LATCHRELEASING, "releasing");
        map.put(ITailGate.TAILGATE_LATCHCINCHING, "locking");
    }

    @Override
    public int getBaseIntProp(String key, int area) {
        if (TailGateSignal.TAILGATE_STATE.equalsIgnoreCase(key)) {
            String state = (String) getValue(key);
            return map.getReverse(state.toLowerCase());
        }
        return super.getBaseIntProp(key, area);
    }

    @Override
    public void setBaseIntProp(String key, int area, int value) {
        if (TailGateSignal.TAILGATE_STATE.equalsIgnoreCase(key)) {
            String state = map.getForward(value);
            setValue(key, state);
        } else {
            super.setBaseIntProp(key, area, value);
        }
    }
}
