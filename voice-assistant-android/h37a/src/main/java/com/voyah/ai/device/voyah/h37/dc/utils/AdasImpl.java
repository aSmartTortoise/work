package com.voyah.ai.device.voyah.h37.dc.utils;


import static com.voyah.ai.device.voyah.h37.utils.LauncherViewUtils.getValueInContext;

import com.blankj.utilcode.util.Utils;
import com.voice.sdk.context.DeviceContextUtils;
import com.voice.sdk.context.DeviceInfo;
import com.voice.sdk.context.ReportConstant;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.dc.AdasInterface;
import com.voice.sdk.util.ThreadPoolUtils;
import com.voyah.ai.basecar.carservice.CarPropUtils;
import com.voyah.ai.basecar.carservice.CarServicePropUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.cockpit.appadapter.aidlimpl.IFunctionManagerImpl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import mega.car.CarPropertyManager;
import mega.car.Signal;
import mega.car.config.Adas;
import mega.car.config.Apa;
import mega.car.hardware.CarPropertyValue;

public class AdasImpl implements AdasInterface {

    private static final String TAG = "AdasImpl";

    public HashSet<Integer> integerHashSet = new HashSet<>();

    String mDirection;

    private static final AdasImpl adasImpl = new AdasImpl();

    public static AdasImpl getInstance() {
        return adasImpl;
    }

    public void registerNoaActivateCallback() {
        integerHashSet.clear();
        integerHashSet.add(Signal.ID_NOA_ACTIVATION_FAILURE_INFO);
        CarServicePropUtils.getInstance().registerCallback(changeCallback, integerHashSet);
    }

    public void registerWardCallback(String ward) {
        integerHashSet.clear();
        integerHashSet.add(Signal.ID_TLCFUNSTS);
        mDirection = ward;
        CarServicePropUtils.getInstance().registerCallback(changeCallback, integerHashSet);
    }

    @Override
    public void registerAVMStateCallback() {
        HashSet<Integer> integerHashSet = new HashSet<>();
        integerHashSet.add(Apa.ID_APA_PAS_FUNCMODE);
        CarServicePropUtils.getInstance().registerCallback(new CarPropertyManager.CarPropertyEventCallback() {
            @Override
            public void onChangeEvent(CarPropertyValue carPropertyValue) {
                int intProp = (int) carPropertyValue.getValue();
                switch (carPropertyValue.getPropertyId()) {
                    case Apa.ID_APA_PAS_FUNCMODE:
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
        int noaStart = CarServicePropUtils.getInstance().getIntProp(Adas.ID_ADAS_NOA_DISPSTS);
        int cadStart = CarServicePropUtils.getInstance().getIntProp(Adas.ID_ADAS_ICA_DISPSTS);
        return cadStart == 1 || noaStart == 2;
    }

    @Override
    public boolean isNeedDetermineLaneChangeSwitch() {
        return true;
    }

    @Override
    public void setChangeLanes(HashMap<String, Object> map) {
        String direction = (String) getValueInContext(map, "direction");
        registerWardCallback(direction);
        CarPropUtils.getInstance().setIntProp(Signal.ID_TLCACTIVEREQ, direction.equals("leftward") ? 1 : 2);
        ThreadPoolUtils.INSTANCE.getScheduledPool("adas").schedule(() -> {
            unRegisterAdasCallback();
        }, 5000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void setActivateNOA() {
        registerNoaActivateCallback();
        CarPropUtils.getInstance().setIntProp(Signal.ID_NOAACTIVEREQ, 1);
        ThreadPoolUtils.INSTANCE.getScheduledPool("Adas").schedule(() -> {
            int noaState = CarPropUtils.getInstance().getIntProp(Adas.ID_ADAS_NOA_DISPSTS);
            if (noaState == 0) {
                //需要判断智驾播报开关，开关是开的，智驾播报，否则语音播报
                setTtsText("5005304", true, true);
            } else if (noaState == 2) {
                //需要判断智驾播报开关，开关是开的，智驾播报，否则语音播报
                setTtsText("5005303", true, true);
            }
            unRegisterAdasCallback();
        }, 3000, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean isAccOrLccOpened() {
        boolean isNoaOpen = (CarPropUtils.getInstance().getIntProp(Adas.ID_ADAS_NOA_DISPSTS)) == Adas.NoaDispSts.NOAL_ACTIVE;
        boolean isIcaOpen = (CarPropUtils.getInstance().getIntProp(Adas.ID_ADAS_ICA_DISPSTS)) == Adas.IcaDispSts.ICADISPSTS_ACTIVE;
        int accState = CarPropUtils.getInstance().getIntProp(Adas.ID_ADAS_ACC_DISPSTS);
        boolean isAccOpen = !(Signal.ACCDispSts.ACC_DISP_STS_ACTIVE > accState || Signal.ACCDispSts.ACC_DISP_STS_STANDWAITE < accState);
        return isAccOpen || isIcaOpen || isNoaOpen;
    }

    @Override
    public boolean isWorkshopDoesNotQualify(HashMap<String, Object> map) {
        String adjustType = (String) getValueInContext(map, "adjust_type");
        int curWorkshopTimeInterval = CarPropUtils.getInstance().getIntProp(Signal.ID_TIMEGAPADJUSTREQ);
        if (adjustType.equals("increase")) {
            return curWorkshopTimeInterval == 4;
        } else if (adjustType.equals("decrease")) {
            return curWorkshopTimeInterval == 1;
        } else if (adjustType.equals("set")) {
            if (map.containsKey("level")) {
                String level = (String) getValueInContext(map, "level");
                if (level.equals("max")) {
                    return curWorkshopTimeInterval == 4;
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
        int curWorkshopTimeInterval = CarPropUtils.getInstance().getIntProp(Signal.ID_TIMEGAPADJUSTREQ);
        if (adjustType.equals("increase")) {
            if (map.containsKey("number_level")) {
                int setNumber = Integer.parseInt((String) getValueInContext(map, "number_level"));
                CarPropUtils.getInstance().setIntProp(Signal.ID_TIMEGAPADJUSTREQ, Math.min(4, curWorkshopTimeInterval + setNumber));
            } else {
                CarPropUtils.getInstance().setIntProp(Signal.ID_TIMEGAPADJUSTREQ, Math.min(4, curWorkshopTimeInterval + 1));
            }
        } else if (adjustType.equals("decrease")) {
            if (map.containsKey("number_level")) {
                int setNumber = Integer.parseInt((String) getValueInContext(map, "number_level"));
                CarPropUtils.getInstance().setIntProp(Signal.ID_TIMEGAPADJUSTREQ, Math.min(1, curWorkshopTimeInterval - setNumber));
            } else {
                CarPropUtils.getInstance().setIntProp(Signal.ID_TIMEGAPADJUSTREQ, Math.min(1, curWorkshopTimeInterval - 1));
            }
        } else if (adjustType.equals("set")) {
            if (map.containsKey("level")) {
                String level = (String) getValueInContext(map, "level");
                if (level.equals("max")) {
                    CarPropUtils.getInstance().setIntProp(Signal.ID_TIMEGAPADJUSTREQ, 4);
                } else if (level.equals("min")) {
                    CarPropUtils.getInstance().setIntProp(Signal.ID_TIMEGAPADJUSTREQ, 1);
                }
            } else if (map.containsKey("number_level")) {
                int setNumber = Integer.parseInt((String) getValueInContext(map, "number_level"));
                if (setNumber > 4) {
                    CarPropUtils.getInstance().setIntProp(Signal.ID_TIMEGAPADJUSTREQ, 4);
                } else if (setNumber < 1) {
                    CarPropUtils.getInstance().setIntProp(Signal.ID_TIMEGAPADJUSTREQ, 1);
                } else {
                    CarPropUtils.getInstance().setIntProp(Signal.ID_TIMEGAPADJUSTREQ, setNumber);
                }
            }
        }
    }

    @Override
    public boolean isLessThanLowest(HashMap<String, Object> map) {
        String adjustType = (String) getValueInContext(map, "adjust_type");
        int curWorkshopTimeInterval = CarPropUtils.getInstance().getIntProp(Signal.ID_TIMEGAPADJUSTREQ);
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
        int curWorkshopTimeInterval = CarPropUtils.getInstance().getIntProp(Signal.ID_TIMEGAPADJUSTREQ);
        int setNumber = Integer.parseInt((String) getValueInContext(map, "number_level"));
        if (adjustType.equals("increase")) {
            return curWorkshopTimeInterval + setNumber > 4;
        } else if (adjustType.equals("decrease")) {
            return curWorkshopTimeInterval - setNumber > 4;
        } else {
            return setNumber > 4;
        }
    }

    @Override
    public boolean isMaxEqualTo4() {
        return true;
    }

    private CarPropertyManager.CarPropertyEventCallback changeCallback =
            new CarPropertyManager.CarPropertyEventCallback() {
                @Override
                public void onChangeEvent(CarPropertyValue carPropertyValue) {
                    int value = (int) carPropertyValue.getValue();
                    switch (carPropertyValue.getPropertyId()) {
                        case Signal.ID_NOA_ACTIVATION_FAILURE_INFO:
//                            0x0：无提示
//                            0x1：未满足激活条件_NOA激活条件未满足
//                            0x2：未满足激活条件_请系好安全带
//                            0x3：未满足激活条件_请关好车门及引擎盖
//                            0x4：未满足激活条件_请挂前进挡
//                            0x5：未满足激活条件_请释放电子驻车
//                            0x6：未满足激活条件_请开启车身稳定系统
//                            0x7：未满足激活条件_陡坡缓降工作中，系统无法激活
//                            0x8：未满足激活条件_系统故障，NOA无法激活
                            LogUtils.i(TAG, "onChangeEvent Signal.ID_NOA_ACTIVATION_FAILURE_INFO value:" + value);
                            if (value > Signal.ParamsNOAActivationFailureInfo.NOAFAIL_NOREMIND &&
                                    value <= Signal.ParamsNOAActivationFailureInfo.NOAFAIL_SYSTEMFAILURE) {
                                String ttsString = "";
                                switch (value) {
                                    case Signal.ParamsNOAActivationFailureInfo.NOAFAIL_CONDITIONNOTMET:
                                        ttsString = "5005305";
                                        break;
                                    case Signal.ParamsNOAActivationFailureInfo.NOAFAIL_FASTENSEATBELT:
                                        ttsString = "5005306";
                                        break;
                                    case Signal.ParamsNOAActivationFailureInfo.NOAFAIL_CLOSEDOORHOOD:
                                        ttsString = "5005307";
                                        break;
                                    case Signal.ParamsNOAActivationFailureInfo.NOAFAIL_HANGINFORWARDGEAR:
                                        ttsString = "5005308";
                                        break;
                                    case Signal.ParamsNOAActivationFailureInfo.NOAFAIL_RELEASEEPB:
                                        ttsString = "5005309";
                                        break;
                                    case Signal.ParamsNOAActivationFailureInfo.NOAFAIL_OPENESP:
                                        ttsString = "5005310";
                                        break;
                                    case Signal.ParamsNOAActivationFailureInfo.NOAFAIL_DURINGSTEEPDESCENT:
                                        ttsString = "5005311";
                                        break;
                                    case Signal.ParamsNOAActivationFailureInfo.NOAFAIL_SYSTEMFAILURE:
                                        ttsString = "5005312";
                                        break;
                                }
                                //不需要判断智驾播报开发，直接语音播报
                                setTtsText(ttsString, false, true);
                            }
                            break;
                        case Signal.ID_TLCFUNSTS:
                            LogUtils.i(TAG, "onChangeEvent Signal.ID_TLCFUNSTS value:" + value);
                            switch (value) {
                                case Signal.TLCFunStsParams.TLCTFS_OFF:
                                    break;
                                case Signal.TLCFunStsParams.TLCTFS_PASSIVE:
                                case Signal.TLCFunStsParams.TLCTFS_ERROR:
                                    //不需要判断智驾播报开发，直接语音播报
                                    setTtsText("5005407", false, false);
                                    break;
                                case Signal.TLCFunStsParams.TLCTFS_STANDBY:
                                    break;
                                case Signal.TLCFunStsParams.TLCTFS_WAIT:
                                    break;
                                case Signal.TLCFunStsParams.TLCTFS_REQDELAY:
                                    //需要判断智驾播报开发，开关是开的，智驾播报，否则语音播报
                                    setTtsText("5005403", true, false);
                                    break;
                                case Signal.TLCFunStsParams.TLCTFS_LC_ING:
                                    int adasSts = CarPropUtils.getInstance().getIntProp(Adas.ID_ADAS_LC_DISP_STS);
                                    if (mDirection.equals("leftward")) {
                                        if (Adas.LCDispSts.LCDS_LCING_RIGHT == adasSts) {
                                            //不需要判断智驾播报开发，直接语音播报
                                            setTtsText("5005406", false, false);
                                        } else if (Adas.LCDispSts.LCDS_LCING_LEFT == adasSts) {
                                            //需要判断智驾播报开发，开关是开的，智驾播报，否则语音播报
                                            setTtsText("5005402", true, false);
                                        }
                                    } else if (mDirection.equals("rightward")) {
                                        if (Adas.LCDispSts.LCDS_LCING_RIGHT == adasSts) {
                                            //需要判断智驾播报开发，开关是开的，智驾播报，否则语音播报
                                            setTtsText("5005502", true, false);
                                        } else if (Adas.LCDispSts.LCDS_LCING_LEFT == adasSts) {
                                            //不需要判断智驾播报开发，直接语音播报
                                            setTtsText("5005506", false, false);
                                        }
                                    }
                                    break;
                                case Signal.TLCFunStsParams.TLCTFS_LC_RETURN:
                                    break;
                                case Signal.TLCFunStsParams.TLCTFS_LC_FAIL:
                                    break;
                            }
                    }
                }

                @Override
                public void onErrorEvent(int i, int i1) {
                }
            };

    public void setTtsText(String text, boolean isDrivingAssistBroadcast, boolean isNOA) {
        try {
            if (isNOA) {
                //如果是NOA，大部分都直接播报，限制部分只需要判断智驾播报开关状态
                if (isDrivingAssistBroadcast) {
                    if (IFunctionManagerImpl.getInstance(Utils.getApp()).getDriveAidBroadcastMode() != 0) {
                        return;
                    }
                }
                //todo: 异步发起需要传任务触发位置location+1,可参考 VoyahMusicImpl
                DeviceHolder.INS().getDevices().getTts().speak(text);
            } else {
                //如果变道，ICA开着NOA没开，需要全部由语音播报
                int noaStart = CarServicePropUtils.getInstance().getIntProp(Adas.ID_ADAS_NOA_DISPSTS);
                int cadStart = CarServicePropUtils.getInstance().getIntProp(Adas.ID_ADAS_ICA_DISPSTS);
                if (1 == cadStart && 2 != noaStart) {
                    //如果ICA是开着的，所有播报都由语音播报
                    DeviceHolder.INS().getDevices().getTts().speak(text);
                } else if (1 == cadStart && 2 == noaStart) {
                    if (isDrivingAssistBroadcast) {
                        if (IFunctionManagerImpl.getInstance(Utils.getApp()).getDriveAidBroadcastMode() != 0) {
                            return;
                        }
                    }
                    DeviceHolder.INS().getDevices().getTts().speak(text);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
