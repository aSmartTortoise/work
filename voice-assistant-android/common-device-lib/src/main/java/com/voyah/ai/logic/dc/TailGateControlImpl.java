package com.voyah.ai.logic.dc;

import com.voice.sdk.device.carservice.constants.ITailGate;
import com.voice.sdk.device.carservice.dc.context.DCContext;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.carservice.signal.TailGateSignal;
import com.voyah.ai.logic.util.ReflectUtils;

import java.util.HashMap;

public class TailGateControlImpl extends AbsDevices {

    private static final String TAG = TailGateControlImpl.class.getSimpleName();

    public TailGateControlImpl() {
        super();
    }

    @Override
    public String getDomain() {
        return "TailGate";
    }

    @Override
    public String replacePlaceHolderInner(HashMap<String, Object> map, String str) {
        //尾门没有占位符处理
        return str;
    }

    public boolean isSpeedRange(HashMap<String, Object> map) {
        //获取当前车速
        float curDriveSpeed = operator.getFloatProp(CommonSignal.COMMON_SPEED_INFO);
        return curDriveSpeed > 5f;
    }

    public boolean isStatusEqual(HashMap<String, Object> map) {
        String curMethodName = (String) getValueInContext(map, DCContext.GRAPH_PARAMS_KEY_METHOD_NAME_CUR);
        int curResult = (Integer) ReflectUtils.executeMethod(this, curMethodName, map);
        int eqMethodName = Integer.valueOf((String) getValueInContext(map, DCContext.GRAPH_PARAMS_KEY_METHOD_NAME_EQ));
        return curResult == eqMethodName;
    }

    public int curStatus(HashMap<String, Object> map) {
        return operator.getIntProp(TailGateSignal.TAILGATE_STATE);
    }

    public void openTailGate(HashMap<String, Object> map) {
        operator.setIntProp(TailGateSignal.TAILGATE_STATE, ITailGate.TAILGATE_FULLYOPENED);
    }

    public void closeTailGate(HashMap<String, Object> map) {
        operator.setIntProp(TailGateSignal.TAILGATE_STATE, ITailGate.TAILGATE_FULLYCLOSED);
    }

    public void stopTailGate(HashMap<String, Object> map) {
        operator.setIntProp(TailGateSignal.TAILGATE_STATE, ITailGate.TAILGATE_STOPPED);
    }

    public boolean isUnoperableTailGate(HashMap<String, Object> map) {
        //后备箱防玩提醒
        return operator.getIntProp(TailGateSignal.TAILGATE_UNOPERABLE_STATE) == 1;
    }

    public void showUnoperableDialog(HashMap<String, Object> map) {
        operator.setBooleanProp(TailGateSignal.TAILGATE_SHOW_UNOPERABLE, true);
    }
}
