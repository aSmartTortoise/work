package com.voyah.ai.device.voyah.common.H56Car.dc;

import android.provider.Settings;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.example.filter_annotation.CarDevices;
import com.example.filter_annotation.CarType;
import com.mega.ecu.MegaProperties;
import com.mega.nexus.os.MegaSystemProperties;
import com.voice.sdk.device.carservice.signal.AtmosphereSignal;
import com.voice.sdk.device.carservice.vcar.IDeviceRegister;
import com.voyah.ai.basecar.carservice.CarPropUtils;
import com.voyah.ai.basecar.manager.DialogueManager;
import com.voyah.ai.device.voyah.common.H56Car.AtmosphereHelper;
import com.voyah.ai.sdk.bean.LifeState;
import com.voyah.ai.voice.platform.agent.api.flowchart.library.bean.FunctionalPositionBean;
import com.voyah.ai.voice.platform.agent.api.functionMatrix.FunctionDeviceMappingManager;

import mega.car.config.Lighting;
import mega.car.config.ParamsCommon;
import mega.car.config.Signal;

/**
 * @Date 2024/9/6 11:04
 * @Author 8327821
 * @Email *
 * @Description .
 **/
@CarDevices(carType = CarType.H37_CAR)
public class AtmospherePropertyOperator extends Base56Operator implements IDeviceRegister {

    private static final String TAG = "AtmosphereControlImpl";
    private CarPropUtils carPropHelper;
    @Override
    void init() {
        map.put(AtmosphereSignal.ATMO_SWITCH_STATE, Lighting.ID_INTERIOR_LIGHT_AMBIENT_LIGHT);
        //初始化氛围灯SDK，并监听语音状态
        initAtmosphere();
    }

    private void initAtmosphere() {
        carPropHelper = CarPropUtils.getInstance();
        DialogueManager.get().registerStateCallback(state -> {
            LogUtils.i(TAG, "state :" + state);
            if (LifeState.AWAKE.equals(state)) {
                carPropHelper.setIntProp(Lighting.ID_AMB_LIGHT_ON_OFF_VR, ParamsCommon.OnOff.ON);
            } else if (LifeState.ASLEEP.equals(state)) {
                carPropHelper.setIntProp(Lighting.ID_AMB_LIGHT_ON_OFF_VR, ParamsCommon.OnOff.OFF);
            } else if (LifeState.SPEAKING.equals(state)) {
                carPropHelper.setIntProp(Lighting.ID_AMB_LIGHT_VOICESTATUS, ParamsCommon.Active.ACTIVE);
            } else if (LifeState.LISTENING.equals(state)) {
                carPropHelper.setIntProp(Lighting.ID_AMB_LIGHT_VOICESTATUS, ParamsCommon.Active.INACTIVE);
            }
        });
    }

    @Override
    public int getBaseIntProp(String key, int area) {
        switch (key) {
            case AtmosphereSignal.ATMO_CONFIG:
                return MegaSystemProperties.getInt(MegaProperties.CONFIG_AMB_TYPE, 0);
            case AtmosphereSignal.ATMO_ACTION_MODE:
                return carPropHelper.getIntProp(Lighting.ID_AMB_LIGHT_MODE);
            case AtmosphereSignal.ATMO_LAST_STATIC_MODE: //只读
                try {
                    return Settings.System.getInt(Utils.getApp().getContentResolver(), "com.ts.ambientlightsapplication.amb.static");
                } catch (Settings.SettingNotFoundException e) {
                    throw new RuntimeException(e);
                }
            case AtmosphereSignal.ATMO_LAST_DYNAMIC_MODE: //只读
                try {
                    return Settings.System.getInt(Utils.getApp().getContentResolver(), "com.ts.ambientlightsapplication.amb.dynamic");
                } catch (Settings.SettingNotFoundException e) {
                    throw new RuntimeException(e);
                }
            case AtmosphereSignal.ATMO_STATIC_SINGLE_COLOR:
                return carPropHelper.getIntProp(Lighting.ID_AMB_LIGHT_COLOR);
            case AtmosphereSignal.ATMO_DYNAMIC_MULTI_COLOR:
                return carPropHelper.getIntProp(Lighting.ID_AMB_LIGHT_GAMUT);
            //亮度
            case AtmosphereSignal.ATMO_STATIC_BRIGHTNESS:
                return carPropHelper.getIntProp(Lighting.ID_AMB_LIGHT_BRIGHTNESS);
            case AtmosphereSignal.ATMO_ATMOSPHERE_THEME_COLOR:
                return carPropHelper.getIntProp(Signal.ID_THEME_SETTING_QNX);
            default:
                return super.getBaseIntProp(key, area);
        }
    }

    @Override
    public void setBaseIntProp(String key, int area, int value) {
        switch (key) {
            case AtmosphereSignal.ATMO_ACTION_MODE:
                carPropHelper.setIntProp(Lighting.ID_AMB_LIGHT_MODE,value);
                break;
            case AtmosphereSignal.ATMO_STATIC_SINGLE_COLOR:
                carPropHelper.setIntProp(Lighting.ID_AMB_LIGHT_COLOR,value);
                break;
//            case AtmosphereSignal.ATMO_STATIC_MULTI_COLOR:
//                carPropHelper.setIntProp(Signal.ID_AMB_STATIC_MULTIPLE_COLOR,value);
//                break;
//            case AtmosphereSignal.ATMO_DYNAMIC_SINGLE_COLOR:
//                carPropHelper.setIntProp(Signal.ID_SINGLE_COLOR_MUSIC_RHYTHM,value);
//                break;
            case AtmosphereSignal.ATMO_DYNAMIC_MULTI_COLOR:
                carPropHelper.setIntProp(Lighting.ID_AMB_LIGHT_GAMUT,value);
                break;
            //亮度
            case AtmosphereSignal.ATMO_STATIC_BRIGHTNESS:
                carPropHelper.setIntProp(Lighting.ID_AMB_LIGHT_BRIGHTNESS,value);
                break;
//            case AtmosphereSignal.ATMO_DYNAMIC_BRIGHTNESS:
//                carPropHelper.setIntProp(Signal.ID_AMB_BRIGHT_DYNAMIC,value);
//                break;
            case AtmosphereSignal.ATMO_ATMOSPHERE_THEME_COLOR:
                carPropHelper.setIntProp(Signal.ID_THEME_SETTING_QNX,value);
                break;
            default:
                super.setBaseIntProp(key, area, value);
                break;
        }
    }

    @Override
    public boolean getBaseBooleanProp(String key, int area) {
        if (AtmosphereSignal.ATMO_IS_STATIC.equals(key)) {
            int value = carPropHelper.getIntProp(Lighting.ID_AMB_LIGHT_MODE);
            return value == 0 || value == 9;
        } else if (AtmosphereSignal.ATMO_EFFECT_MODE.equals(key)) {
            return AtmosphereHelper.getEffectModeState(area);
        } else if (AtmosphereSignal.ATMO_FOLLOW_MUSIC_SWITCH_STATE.equals(key)) {
            return AtmosphereHelper.getFollowMusicSwitchState();
        } else if (AtmosphereSignal.ATMO_ATMOSPHERE_PAGE_STATE.equalsIgnoreCase(key)) {
            return AtmosphereHelper.getAtmospherePageState();
        } else {
            return super.getBaseBooleanProp(key, area);
        }
    }

    @Override
    public void setBaseBooleanProp(String key, int area, boolean value) {
        if (AtmosphereSignal.ATMO_EFFECT_MODE.equals(key)) {
            AtmosphereHelper.setEffectModeState(area, value);
        } else if (AtmosphereSignal.ATMO_FOLLOW_MUSIC_SWITCH_STATE.equals(key)) {
            AtmosphereHelper.setFollowMusicSwitchState(value);
        } else if (AtmosphereSignal.ATMO_ATMOSPHERE_PAGE_STATE.equalsIgnoreCase(key)) {
            AtmosphereHelper.setAtmospherePageState(value);
        } else {
            super.setBaseBooleanProp(key, area, value);
        }
    }

    @Override
    public void registerDevice() {
        // 音乐律动是否是开关，37A是模式切换 37B是开关 56C没有音乐律动 56D是模式切换
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AtmosphereSignal.ATMO_IS_FOLLOW_MUSIC_SWITCH, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "-1");
    }
}