package com.voyah.ai.device.voyah.common.H37Car.dc;

import com.example.filter_annotation.CarDevices;
import com.example.filter_annotation.CarType;
import com.voice.sdk.device.carservice.signal.OtherSignal;
import com.voice.sdk.device.carservice.signal.SysSettingSignal;
import com.voice.sdk.device.carservice.vcar.IDeviceRegister;
import com.voyah.ai.basecar.manager.SettingsManager;
import com.voyah.ai.sdk.bean.DhSwitch;
import com.voyah.ai.voice.platform.agent.api.flowchart.library.bean.FunctionalPositionBean;
import com.voyah.ai.voice.platform.agent.api.functionMatrix.FunctionDeviceMappingManager;

import mega.car.Signal;
import mega.car.config.Dms;

/**
 * @Date 2024/7/25 14:01
 * @Author 8327821
 * @Email *
 * @Description .
 **/
@CarDevices(carType = CarType.H37_CAR)
public class OtherPropertyOperator extends Base37Operator implements IDeviceRegister {

    @Override
    void init() {
        map.put(OtherSignal.OTHER_STATIC_GESTURE_SWITCH, Signal.ID_OMS_STATIC_GESTURE_SWITCH);
        map.put(OtherSignal.OTHER_DYNAMIC_GESTURE_SWITCH, Signal.ID_OMS_DYNAMIC_GESTURE_SWITCH);

    }

    @Override
    public int getBaseIntProp(String key, int area) {
        switch (key) {
            case OtherSignal.OTHER_VOICE_AREA:
                return SettingsManager.get().getUserVoiceMicMask();
            case OtherSignal.OTHER_MUSIC_PREFERENCE:
                return SettingsManager.get().getMusicPreference();
            case OtherSignal.OTHER_VIDEO_PREFERENCE:
                return SettingsManager.get().getVideoPreference();
            default:
                return super.getBaseIntProp(key, area);
        }
    }

    @Override
    public void setBaseIntProp(String key, int area, int value) {
        switch (key) {
            case OtherSignal.OTHER_VOICE_AREA:
                SettingsManager.get().setUserVoiceMicMask(value);
                break;
            case OtherSignal.OTHER_MUSIC_PREFERENCE:
                SettingsManager.get().setMusicPreference(value);
                break;
            case OtherSignal.OTHER_VIDEO_PREFERENCE:
                SettingsManager.get().setVideoPreference(value);
                break;
            default:
                super.setBaseIntProp(key, area, value);
                break;
        }
    }

    @Override
    public boolean getBaseBooleanProp(String key, int area) {
        switch (key) {
            case OtherSignal.OTHER_WAKEUP_SWITCH:
                return SettingsManager.get().isEnableSwitch(DhSwitch.MainWakeup);
            case OtherSignal.OTHER_CONTINUOUS_DIALOGUE:
                return SettingsManager.get().isEnableSwitch(DhSwitch.ContinuousDialogue);
            case OtherSignal.OTHER_MULTI_ZONE_DIALOGUE:
                return SettingsManager.get().isEnableSwitch(DhSwitch.MultiZoneDialogue);
            default:
                return super.getBaseBooleanProp(key, area);

        }
    }

    @Override
    public void setBaseBooleanProp(String key, int area, boolean value) {
        switch (key) {
            case OtherSignal.OTHER_WAKEUP_SWITCH:
                SettingsManager.get().enableSwitch(DhSwitch.MainWakeup, value);
                break;
            case OtherSignal.OTHER_CONTINUOUS_DIALOGUE:
                SettingsManager.get().enableSwitch(DhSwitch.ContinuousDialogue, value);
                break;
            case OtherSignal.OTHER_MULTI_ZONE_DIALOGUE:
                SettingsManager.get().enableSwitch(DhSwitch.MultiZoneDialogue, value);
                break;
            default:
                super.setBaseBooleanProp(key, area, value);
        }
    }

    @Override
    public void registerDevice() {
        // 静态手势 37A 37B支持   56C 56D不支持
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(OtherSignal.OTHER_STATIC_GESTURE_CONFIG, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "1");
        // 动态手势 37A 37B支持   56C 56D不支持
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(OtherSignal.OTHER_DYNAMIC_GESTURE_CONFIG, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "1");
    }
}
