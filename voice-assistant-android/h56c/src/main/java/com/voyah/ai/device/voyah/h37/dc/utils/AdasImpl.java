package com.voyah.ai.device.voyah.h37.dc.utils;


import com.voice.sdk.context.DeviceContextUtils;
import com.voice.sdk.context.DeviceInfo;
import com.voice.sdk.context.ReportConstant;
import com.voice.sdk.device.carservice.dc.AdasInterface;
import com.voice.sdk.device.carservice.dc.NoaActivateInterface;
import com.voice.sdk.device.carservice.dc.WardInterface;
import com.voyah.ai.basecar.carservice.CarServicePropUtils;
import com.voyah.ai.common.utils.LogUtils;

import java.util.HashMap;
import java.util.HashSet;

import mega.car.CarPropertyManager;
import mega.car.config.H56C;
import mega.car.hardware.CarPropertyValue;

public class AdasImpl implements AdasInterface {

    private static final String TAG = "AdasImpl";

    private static final AdasImpl adasImpl = new AdasImpl();

    public static AdasImpl getInstance() {
        return adasImpl;
    }

    @Override
    public void registerAVMStateCallback() {
        HashSet<Integer> integerHashSet = new HashSet<>();
        integerHashSet.add(H56C.APA_avmState_APA_AVMSTS);
        CarServicePropUtils.getInstance().registerCallback(new CarPropertyManager.CarPropertyEventCallback() {
            @Override
            public void onChangeEvent(CarPropertyValue carPropertyValue) {
                int intProp = (int) carPropertyValue.getValue();
                switch (carPropertyValue.getPropertyId()) {
                    case H56C.APA_avmState_APA_AVMSTS:
                        LogUtils.i(TAG, "ID_APA_PAS_FUNCMODE value:" + intProp);
                        DeviceContextUtils.getInstance().updateDeviceInfo(
                                DeviceInfo.build(ReportConstant.KEY_360_IN_FRONT, intProp == 1 || intProp == 6 || intProp == 7));
                        break;
                }
            }

            @Override
            public void onErrorEvent(int i, int i1) {

            }
        }, integerHashSet);
    }

    @Override
    public boolean isNoaOrLccOpened() {
        //  56C动态都是兜底，所以都是空实现
        return false;
    }

    @Override
    public boolean isNeedDetermineLaneChangeSwitch() {
        //  56C动态都是兜底，所以都是空实现
        return false;
    }

    @Override
    public void setChangeLanes(HashMap<String, Object> map) {
        //  56C动态都是兜底，所以都是空实现
    }

    @Override
    public void setActivateNOA() {
        //  56C动态都是兜底，所以都是空实现
    }

    @Override
    public boolean isAccOrLccOpened() {
        //  56C动态都是兜底，所以都是空实现
        return false;
    }

    @Override
    public boolean isWorkshopDoesNotQualify(HashMap<String, Object> map) {
        //  56C动态都是兜底，所以都是空实现
        return false;
    }

    @Override
    public void setCurWorkshopTimeInterval(HashMap<String, Object> map) {
        //  56C动态都是兜底，所以都是空实现
    }

    @Override
    public boolean isLessThanLowest(HashMap<String, Object> map) {
        //  56C动态都是兜底，所以都是空实现
        return false;
    }

    @Override
    public boolean isBeyondHighest(HashMap<String, Object> map) {
        //  56C动态都是兜底，所以都是空实现
        return false;
    }

    @Override
    public boolean isMaxEqualTo4() {
        //  56C动态都是兜底，所以都是空实现
        return false;
    }
}
