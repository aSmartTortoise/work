package com.voyah.ai.device.voyah.h37.dc.utils;


import static com.voyah.ai.device.voyah.h37.utils.LauncherViewUtils.getValueInContext;

import com.voice.sdk.context.DeviceContextUtils;
import com.voice.sdk.context.DeviceInfo;
import com.voice.sdk.context.ReportConstant;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.dc.AdasInterface;
import com.voice.sdk.device.carservice.dc.NoaActivateInterface;
import com.voice.sdk.device.carservice.dc.WardInterface;
import com.voice.sdk.device.carservice.signal.AdasSignal;
import com.voice.sdk.util.ThreadPoolUtils;
import com.voyah.ai.basecar.carservice.CarPropUtils;
import com.voyah.ai.basecar.carservice.CarServicePropUtils;
import com.voyah.ai.common.utils.LogUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import mega.car.CarPropertyManager;
import mega.car.config.H56D;
import mega.car.hardware.CarPropertyValue;

public class AdasImpl implements AdasInterface {

    private static final String TAG = "AdasImpl";

    public HashSet<Integer> integerHashSet = new HashSet<>();

    String mDirection;

    private static final AdasImpl adasImpl = new AdasImpl();

    public static AdasImpl getInstance() {
        return adasImpl;
    }

    public void registerNoaActivateCallback(NoaActivateInterface noaActivateInterface) {
        // 56D激活NOA不执行，兜底就行，所以空实现
    }

    public void registerWardCallback(String ward) {
        integerHashSet.clear();
        integerHashSet.add(H56D.ADS_FunctionStatus_ADS_TLCFUNCTIONSTATUS);
        mDirection = ward;
        CarServicePropUtils.getInstance().registerCallback(changeCallback, integerHashSet);
    }

    @Override
    public void registerAVMStateCallback() {
        HashSet<Integer> integerHashSet = new HashSet<>();
        integerHashSet.add(H56D.APA_avmState_APA_AVMSTS);
        CarServicePropUtils.getInstance().registerCallback(new CarPropertyManager.CarPropertyEventCallback() {
            @Override
            public void onChangeEvent(CarPropertyValue carPropertyValue) {
                int intProp = (int) carPropertyValue.getValue();
                switch (carPropertyValue.getPropertyId()) {
                    case H56D.APA_avmState_APA_AVMSTS:
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

    public void unRegisterAdasCallback() {
        CarServicePropUtils.getInstance().unregisterCallback(changeCallback, integerHashSet);
    }

    @Override
    public boolean isNoaOrLccOpened() {
        int noaState = CarPropUtils.getInstance().getIntProp(H56D.ADS_FunctionStatus_ADS_NOAFUNCTIONSTATUS);
        int lccState = CarPropUtils.getInstance().getIntProp(H56D.ADS_FunctionStatus_ADS_LCCFUNCTIONSTATUS);
        if (noaState > 2 && noaState < 7) {
            return true;
        }
        if (lccState > 3 && lccState < 10) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isNeedDetermineLaneChangeSwitch() {
        // 56D不需要判断触发式变道辅助开关状态
        return false;
    }

    @Override
    public void setChangeLanes(HashMap<String, Object> map) {
        String direction = (String) getValueInContext(map, "direction");
        // NOA的状态码，如果状态是 5和6 不响应变道
        int noaState = CarPropUtils.getInstance().getIntProp(H56D.ADS_FunctionStatus_ADS_NOAFUNCTIONSTATUS);
        if (noaState == 5 || noaState == 6) {
            DeviceHolder.INS().getDevices().getTts().speak("变道条件不满足，请稍后再试。");
            return;
        }
        // LCC的状态码，如果状态是 5和6 不响应变道
        int lccState = CarPropUtils.getInstance().getIntProp(H56D.ADS_FunctionStatus_ADS_LCCFUNCTIONSTATUS);
        if (lccState > 9 && lccState < 16) {
            DeviceHolder.INS().getDevices().getTts().speak("变道条件不满足，请稍后再试。");
            return;
        }
        int tlcState = CarPropUtils.getInstance().getIntProp(H56D.ADS_FunctionStatus_ADS_TLCFUNCTIONSTATUS);
        if (tlcState == 3 || tlcState == 4 || tlcState == 5 || tlcState == 6) {
            DeviceHolder.INS().getDevices().getTts().speak("正在变道中，请稍后再试。");
            return;
        }
        registerWardCallback(direction);
        // 设置变道 1=左变道 2=右变道
        CarPropUtils.getInstance().setIntProp(H56D.IVI_ADSSet_IVI_VOICETLCCHAREQ, direction.equals("leftward") ? 1 : 2);
        ThreadPoolUtils.INSTANCE.getScheduledPool("adas").schedule(this::unRegisterAdasCallback, 5000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void setActivateNOA() {
        // 56D激活NOA不执行，兜底就行，所以空实现
    }

    @Override
    public boolean isAccOrLccOpened() {
        int accState = CarPropUtils.getInstance().getIntProp(H56D.ADS_FunctionStatus_ADS_ACCFUNCTIONSTATUS);
        int lccState = CarPropUtils.getInstance().getIntProp(H56D.ADS_FunctionStatus_ADS_LCCFUNCTIONSTATUS);
        return (accState > 2 && accState < 8) || (lccState > 3 && lccState < 16);
    }

    @Override
    public boolean isWorkshopDoesNotQualify(HashMap<String, Object> map) {
        String adjustType = (String) getValueInContext(map, "adjust_type");
        int curWorkshopTimeInterval = CarPropUtils.getInstance().getIntProp(H56D.ADS_DisplayReq2_ADS_TIMEGAPADJUSTMENT);
        if (adjustType.equals("increase")) {
            return curWorkshopTimeInterval == 5;
        } else if (adjustType.equals("decrease")) {
            return curWorkshopTimeInterval == 1;
        } else if (adjustType.equals("set")) {
            if (map.containsKey("level")) {
                String level = (String) getValueInContext(map, "level");
                if (level.equals("max")) {
                    return curWorkshopTimeInterval == 5;
                } else if (level.equals("min")) {
                    return curWorkshopTimeInterval == 1;
                }
            } else if (map.containsKey("number_level")) {
                int setNumber = Integer.parseInt((String) getValueInContext(map, "number_level"));
                return setNumber == curWorkshopTimeInterval;
            }
        }
        return true;
    }

    @Override
    public void setCurWorkshopTimeInterval(HashMap<String, Object> map) {
        String adjustType = (String) getValueInContext(map, "adjust_type");
        int curWorkshopTimeInterval = CarPropUtils.getInstance().getIntProp(H56D.ADS_DisplayReq2_ADS_TIMEGAPADJUSTMENT);
        if (adjustType.equals("increase")) {
            if (map.containsKey("number_level")) {
                int setNumber = Integer.parseInt((String) getValueInContext(map, "number_level"));
                CarPropUtils.getInstance().setIntProp(H56D.IVI_ADSSet_IVI_VOICETIMEGAPREQ, Math.min(5, curWorkshopTimeInterval + setNumber));
            } else {
                CarPropUtils.getInstance().setIntProp(H56D.IVI_ADSSet_IVI_VOICETIMEGAPREQ, Math.min(5, curWorkshopTimeInterval + 1));
            }
        } else if (adjustType.equals("decrease")) {
            if (map.containsKey("number_level")) {
                int setNumber = Integer.parseInt((String) getValueInContext(map, "number_level"));
                CarPropUtils.getInstance().setIntProp(H56D.IVI_ADSSet_IVI_VOICETIMEGAPREQ, Math.max(1, curWorkshopTimeInterval - setNumber));
            } else {
                CarPropUtils.getInstance().setIntProp(H56D.IVI_ADSSet_IVI_VOICETIMEGAPREQ, Math.max(1, curWorkshopTimeInterval - 1));
            }
        } else if (adjustType.equals("set")) {
            if (map.containsKey("level")) {
                String level = (String) getValueInContext(map, "level");
                if (level.equals("max")) {
                    CarPropUtils.getInstance().setIntProp(H56D.IVI_ADSSet_IVI_VOICETIMEGAPREQ, 5);
                } else if (level.equals("min")) {
                    CarPropUtils.getInstance().setIntProp(H56D.IVI_ADSSet_IVI_VOICETIMEGAPREQ, 1);
                }
            } else if (map.containsKey("number_level")) {
                int setNumber = Integer.parseInt((String) getValueInContext(map, "number_level"));
                if (setNumber > 5) {
                    CarPropUtils.getInstance().setIntProp(H56D.IVI_ADSSet_IVI_VOICETIMEGAPREQ, 5);
                } else if (setNumber < 1) {
                    CarPropUtils.getInstance().setIntProp(H56D.IVI_ADSSet_IVI_VOICETIMEGAPREQ, 1);
                } else {
                    CarPropUtils.getInstance().setIntProp(H56D.IVI_ADSSet_IVI_VOICETIMEGAPREQ, setNumber);
                }
            }
        }
    }

    @Override
    public boolean isLessThanLowest(HashMap<String, Object> map) {
        String adjustType = (String) getValueInContext(map, "adjust_type");
        int curWorkshopTimeInterval = CarPropUtils.getInstance().getIntProp(H56D.ADS_DisplayReq2_ADS_TIMEGAPADJUSTMENT);
        int setNumber = Integer.parseInt((String) getValueInContext(map, "number_level"));
        if (adjustType.equals("increase")) {
            return curWorkshopTimeInterval + setNumber < 1;
        } else if (adjustType.equals("decrease")) {
            return curWorkshopTimeInterval - setNumber < 1;
        } else {
            return setNumber < 1;
        }
    }

    @Override
    public boolean isBeyondHighest(HashMap<String, Object> map) {
        String adjustType = (String) getValueInContext(map, "adjust_type");
        int curWorkshopTimeInterval = CarPropUtils.getInstance().getIntProp(H56D.ADS_DisplayReq2_ADS_TIMEGAPADJUSTMENT);
        int setNumber = Integer.parseInt((String) getValueInContext(map, "number_level"));
        if (adjustType.equals("increase")) {
            return curWorkshopTimeInterval + setNumber > 5;
        } else if (adjustType.equals("decrease")) {
            return curWorkshopTimeInterval - setNumber > 5;
        } else {
            return setNumber > 5;
        }
    }

    @Override
    public boolean isMaxEqualTo4() {
        return false;
    }

    private CarPropertyManager.CarPropertyEventCallback changeCallback =
            new CarPropertyManager.CarPropertyEventCallback() {
                @Override
                public void onChangeEvent(CarPropertyValue carPropertyValue) {
                    String ttsString = "";
                    int value = (int) carPropertyValue.getValue();
                    switch (carPropertyValue.getPropertyId()) {
                        case H56D.ADS_FunctionStatus_ADS_TLCFUNCTIONSTATUS:
                            switch (value) {
                                case 3:
                                case 4:
                                case 5:
                                    DeviceHolder.INS().getDevices().getTts().speak("好的，变道等待。");
                                    break;
                                case 6:
                                    if (mDirection.equals("leftward")) {
                                        DeviceHolder.INS().getDevices().getTts().speak("正在向左变道。");
                                    } else {
                                        DeviceHolder.INS().getDevices().getTts().speak("正在向右变道。");
                                    }
                                    break;
                            }
                            break;

                    }
                }

                @Override
                public void onErrorEvent(int i, int i1) {
                }
            };

}
