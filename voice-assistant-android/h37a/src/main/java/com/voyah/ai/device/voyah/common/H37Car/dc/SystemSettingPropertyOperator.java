package com.voyah.ai.device.voyah.common.H37Car.dc;

import android.os.RemoteException;
import android.provider.Settings;

import com.blankj.utilcode.util.Utils;
import com.example.filter_annotation.CarDevices;
import com.example.filter_annotation.CarType;
import com.example.upload_log_manager.UploadLogManager;
import com.voice.sdk.device.carservice.constants.ISysSetting;
import com.voice.sdk.device.carservice.dc.carsetting.SettingConstants;
import com.voice.sdk.device.carservice.signal.CarSettingSignal;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.carservice.signal.SysSettingSignal;
import com.voice.sdk.device.carservice.vcar.IDeviceRegister;
import com.voyah.ai.basecar.CommonSystemUtils;
import com.voyah.ai.basecar.carservice.CarPropUtils;
import com.voyah.ai.basecar.carservice.CarServicePropUtils;
import com.voyah.ai.basecar.utils.SettingUtils;
import com.voyah.ai.basecar.utils.VolumeUtils;
import com.voyah.ai.device.voyah.common.H37Car.SysSettingHelper;
import com.voyah.ai.device.voyah.h37.dc.utils.ShareUtils;
import com.voyah.ai.voice.platform.agent.api.flowchart.library.bean.FunctionalPositionBean;
import com.voyah.ai.voice.platform.agent.api.functionMatrix.FunctionDeviceMappingManager;
import com.voyah.cockpit.appadapter.aidlimpl.IFunctionManagerImpl;

import mega.car.Signal;
import mega.car.config.Comforts;
import mega.car.config.Infotainment;
import mega.car.config.ParamsCommon;
import mega.car.config.VehicleMotion;

/**
 * @Date 2024/7/23 17:14
 * @Author 8327821
 * @Email *
 * @Description .
 **/
@CarDevices(carType = CarType.H37_CAR)
public class SystemSettingPropertyOperator extends Base37Operator implements IDeviceRegister {
    @Override
    void init() {
        map.put(SysSettingSignal.SYS_WIRELESS_CHARGE, Comforts.ID_WIRELESS_CHARGING_SWITCH); //映射只用来写，读有特殊处理
        map.put(SysSettingSignal.SYS_VOLUME_FOLLOW_SPEED, Infotainment.ID_AMP_VEHICLESPEEDCOMP_MODEENABLE); //只用来读，写要走设置接口，否则设置不刷新UI
        map.put(SysSettingSignal.SYS_ACOUSTIC_VEH_ALERT_SWITCH, VehicleMotion.ID_ACOUSTIC_VEH_ALERT); //低速行人报警音
        map.put(SysSettingSignal.SYS_IMITATE_POS, Signal.ID_AMP_ESE_OUTDOOR_SOUND_ON_OFF); //模拟声浪发声位置  get用信号，set用接口
        map.put(SysSettingSignal.SYS_MEDIA_OUTPLAY, Signal.ID_AMP_OUTDOOR_MODE);
        map.put(SysSettingSignal.SYS_5GP_SWITCH, Signal.ID_TBOX5GONOFFSET);
        map.put(SysSettingSignal.SYS_REAR_BELT_REMINDER_SWITCH, Signal.ID_REAR_SEAT_BELT_REMINDER_SWITCH);
        map.put(SysSettingSignal.SYS_INSTUMENT_TONE, Signal.ID_SOUND_FIELD_INSTUMENT_TONE);
        UploadLogManager.getInstance().init(Utils.getApp());
    }

    @Override
    public int getBaseIntProp(String key, int area) {
        switch (key) {
            case SysSettingSignal.SYS_WIRELESS_CHARGE:
                return SysSettingHelper.getChargeState();
            case SysSettingSignal.SYS_VOLUME_IMITATE:
                return SysSettingHelper.getVolumeImitateState();
            case SysSettingSignal.SYS_DRV_ASSIST_CAST:
                return SysSettingHelper.getDrvAssistBroadcastState();
            case SysSettingSignal.SYS_VOLUME_STREAM_TYPE:
                return VolumeUtils.getInstance().getStreamType();
            case SysSettingSignal.SYS_VOLUME_PHONE:
                return VolumeUtils.getInstance().getVolume(ISysSetting.IVolume.STREAM_VOICE_CALL);
            case SysSettingSignal.SYS_VOLUME_NAVI:
                return VolumeUtils.getInstance().getVolume(ISysSetting.IVolume.STREAM_NVI);
            case SysSettingSignal.SYS_VOLUME_SYSTEM:
                return VolumeUtils.getInstance().getVolume(ISysSetting.IVolume.STREAM_NOTIFICATION);
            case SysSettingSignal.SYS_VOLUME_BLUETOOTH:
                return VolumeUtils.getInstance().getVolume(ISysSetting.IVolume.STREAM_BLUETOOTH);
            case SysSettingSignal.SYS_VOLUME_ASSISTANT:
                return VolumeUtils.getInstance().getVolume(ISysSetting.IVolume.STREAM_ASSISTANT);
            case SysSettingSignal.SYS_VOLUME_MEDIA:
                return VolumeUtils.getInstance().getVolume(ISysSetting.IVolume.STREAM_MUSIC);
            case SysSettingSignal.SYS_KEY_TONE:
                return VolumeUtils.getInstance().getKeyTone();
            case SysSettingSignal.SYS_MEDIA_OUTPLAY_CONFIRM:
                //和设置保持一致，默认值设置为1
                return Settings.System.getInt(Utils.getApp().getContentResolver(), "show_open_media_out_tip", 1);
            case SysSettingSignal.SYS_IMITATE_POS:
                return SysSettingHelper.getSoundWaveInOut();
            case SysSettingSignal.SYS_GET_VOLUME_TYPE:
                return SysSettingHelper.setVolumeType();
            case SysSettingSignal.SYS_THEME_MODE:
                if (CarServicePropUtils.getInstance().isH37A()) {
                    try {
                        return IFunctionManagerImpl.getInstance(Utils.getApp()).getTheme();
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    return Integer.parseInt(SettingUtils.getInstance().getCurrentState(SettingConstants.NEW_ACTION_GETTHEMEMODE));
                }
            case SysSettingSignal.SYS_HEADREST_SOUND_STATE:
                return SysSettingHelper.getHeadrestSoundState();
            case SysSettingSignal.SYS_SOUND_EFFECTS_MODE:
                return SysSettingHelper.getSoundEffectsMode();
            case SysSettingSignal.SYS_VOLUME_FOCUS_MODE:
                return SysSettingHelper.getVolumeFocusMode();
            case SysSettingSignal.SYS_ACOUSTIC_VEH_ALERT_MODE:
                return SysSettingHelper.getLowSpeedPedesWarningMode();
            default:
                return super.getBaseIntProp(key, area);
        }
    }

    @Override
    public void setBaseIntProp(String key, int area, int value) {
        switch (key) {
            case SysSettingSignal.SYS_VOLUME_IMITATE:
                SysSettingHelper.setVolumeImitate(value);
                break;
            case SysSettingSignal.SYS_DRV_ASSIST_CAST:
                SysSettingHelper.setDrvAssistBroadcastState(value);
                break;
            case SysSettingSignal.SYS_VOLUME_PHONE:
                VolumeUtils.getInstance().setVolume(ISysSetting.IVolume.STREAM_VOICE_CALL, value);
                break;
            case SysSettingSignal.SYS_VOLUME_NAVI:
                VolumeUtils.getInstance().setVolume(ISysSetting.IVolume.STREAM_NVI, value);
                break;
            case SysSettingSignal.SYS_VOLUME_SYSTEM:
                VolumeUtils.getInstance().setVolume(ISysSetting.IVolume.STREAM_NOTIFICATION, value);
                break;
            case SysSettingSignal.SYS_VOLUME_BLUETOOTH:
                VolumeUtils.getInstance().setVolume(ISysSetting.IVolume.STREAM_BLUETOOTH, value);
                break;
            case SysSettingSignal.SYS_VOLUME_ASSISTANT:
                VolumeUtils.getInstance().setVolume(ISysSetting.IVolume.STREAM_ASSISTANT, value);
                break;
            case SysSettingSignal.SYS_VOLUME_MEDIA:
                VolumeUtils.getInstance().setVolume(ISysSetting.IVolume.STREAM_MUSIC, value);
                break;
            case SysSettingSignal.SYS_KEY_TONE:
                VolumeUtils.getInstance().setKeyTone(value);
                break;
            case SysSettingSignal.SYS_MEDIA_OUTPLAY_CONFIRM:
                Settings.System.putInt(Utils.getApp().getContentResolver(), "show_open_media_out_tip", value);
                break;
            case SysSettingSignal.SYS_IMITATE_POS:
                SysSettingHelper.setSoundWaveInOut(value);
                break;
            case SysSettingSignal.SYS_THEME_MODE:
                if (CarServicePropUtils.getInstance().isH37A()) {
                    try {
                        IFunctionManagerImpl.getInstance(Utils.getApp()).setTheme(value);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    String mode = SettingConstants.NEW_ACTION_SETTHEMEAUTO;
                    switch (value) {
                        case 0:
                            mode = SettingConstants.NEW_ACTION_SETTHEMEAUTO;
                            break;
                        case 1:
                            mode = SettingConstants.NEW_ACTION_SETTHEMELIGHT;
                            break;
                        case 2:
                            mode = SettingConstants.NEW_ACTION_SETTHEMEDARK;
                            break;
                    }
                    SettingUtils.getInstance().exec(mode);
                }
                break;
            case SysSettingSignal.SYS_INSTUMENT_TONE:
                SysSettingHelper.setInstumentTone(value);
                break;
            case SysSettingSignal.SYS_HEADREST_SOUND_STATE:
                SysSettingHelper.setHeadrestSoundState(value);
                break;
            case SysSettingSignal.SYS_SOUND_EFFECTS_MODE:
                SysSettingHelper.setSoundEffectsMode(value);
                break;
            case SysSettingSignal.SYS_VOLUME_FOCUS_MODE:
                SysSettingHelper.setVolumeFocusMode(value);
                break;
            case SysSettingSignal.SYS_ACOUSTIC_VEH_ALERT_MODE:
                SysSettingHelper.setLowSpeedPedesWarningMode(value);
                break;
            default:
                super.setBaseIntProp(key, area, value);
        }
    }


    @Override
    public boolean getBaseBooleanProp(String key, int area) {
        switch (key) {
            case SysSettingSignal.SYS_BLUETOOTH_SWITCH:
                return SysSettingHelper.getBlueToothSwitch();
            case SysSettingSignal.SYS_HOTSPOT_SWITCH:
                return SysSettingHelper.getHotSpotSwitch();
            case SysSettingSignal.SYS_WIFI_SWITCH:
                return SysSettingHelper.getWifiSwitch();
            case SysSettingSignal.SYS_MUTE_PHONE:
                return VolumeUtils.getInstance().getMuteSound("phone");
            case SysSettingSignal.SYS_MUTE_NAVI:
                return VolumeUtils.getInstance().getMuteSound("navi");
            case SysSettingSignal.SYS_MUTE_SYSTEM:
                return VolumeUtils.getInstance().getMuteSound("system_sound");
            case SysSettingSignal.SYS_MUTE_BLUETOOTH:
                return VolumeUtils.getInstance().getMuteSound("bluetooth_headset");
            case SysSettingSignal.SYS_MUTE_ASSISTANT:
                return VolumeUtils.getInstance().getMuteSound("voice");
            case SysSettingSignal.SYS_MUTE_MEDIA:
                return VolumeUtils.getInstance().getMuteSound("media");
            case SysSettingSignal.SYS_MUTE_INSTRUMENT:
                return SysSettingHelper.getInstrumentMuteState();
            case SysSettingSignal.SYS_5GP_SWITCH:
                return SysSettingHelper.get5GSwitch();
            case SysSettingSignal.SYS_WIFI_SHARING:
                return ShareUtils.getInstance().get37AVsWifiConnect();
            case SysSettingSignal.SYS_HOTSPOT_SHARING:
                return ShareUtils.getInstance().get37AVsApConnect();
            case SysSettingSignal.SYS_VOLUME_MUTE_STATE:
                return VolumeUtils.getInstance().isAllMuteSound(getBooleanProp(CommonSignal.COMMON_HAS_BT_CONNECT), area == 1);
            case SysSettingSignal.SYS_REAR_BELT_REMINDER_SWITCH:
                try {
                    int rearSeatBeltSwitchState = IFunctionManagerImpl.getInstance(Utils.getApp()).getRearSeatBeltSwitchState();
                    return rearSeatBeltSwitchState == -1 || rearSeatBeltSwitchState == 1;
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            case SysSettingSignal.SYS_NIGHTTIME_MUTE_SWITCH:
                return SysSettingHelper.getNighttimeMuteSwitch();
            case SysSettingSignal.SYS_ACOUSTIC_VEH_ALERT_SWITCH:
                return SysSettingHelper.getLowSpeedPedesWarningSwitchState();
            case SysSettingSignal.SYS_REMOTE_KEY_BROADCAST_SWITCH:
                return SysSettingHelper.getRemoteKeyBroadcastState();
            case SysSettingSignal.SYS_DIAPASON_ASSESS_VEHICLE_CONFIGURATION:
                return false;
            case SysSettingSignal.SYS_VOLUME_FOLLOW_SPEED:
                return SysSettingHelper.getVolumeWithSpeedStatus();
            case SysSettingSignal.SYS_INTELLIGENT_VOLUME_SWITCH:
                return SysSettingHelper.getIntelligentVolume();
            case SysSettingSignal.SYS_MEDIA_OUTPLAY:
                return SysSettingHelper.getMediaOutsideSoundStatus();
            default:
                return super.getBaseBooleanProp(key, area);
        }
    }

    @Override
    public void setBaseBooleanProp(String key, int area, boolean value) {
        switch (key) {
            case SysSettingSignal.SYS_BLUETOOTH_SWITCH:
                SysSettingHelper.setBlueToothSwitch(value);
                break;
            case SysSettingSignal.SYS_HOTSPOT_SWITCH:
                SysSettingHelper.setHotSpotSwitch(value);
                break;
            case SysSettingSignal.SYS_WIFI_SWITCH:
                SysSettingHelper.setWifiSwitch(value);
                break;
            case SysSettingSignal.SYS_VOLUME_FOLLOW_SPEED:
                SysSettingHelper.setVolumeFollowSpeed(value);
                break;
            case SysSettingSignal.SYS_MUTE_PHONE:
                VolumeUtils.getInstance().setMuteSound("phone", value);
                break;
            case SysSettingSignal.SYS_MUTE_NAVI:
                VolumeUtils.getInstance().setMuteSound("navi", value);
                break;
            case SysSettingSignal.SYS_MUTE_SYSTEM:
                VolumeUtils.getInstance().setMuteSound("system_sound", value);
                break;
            case SysSettingSignal.SYS_MUTE_BLUETOOTH:
                VolumeUtils.getInstance().setMuteSound("bluetooth_headset", value);
                break;
            case SysSettingSignal.SYS_MUTE_ASSISTANT:
                VolumeUtils.getInstance().setMuteSound("voice", value);
                break;
            case SysSettingSignal.SYS_MUTE_MEDIA:
                VolumeUtils.getInstance().setMuteSound("media", value);
                break;
            case SysSettingSignal.SYS_MUTE_INSTRUMENT:
                SysSettingHelper.setInstrumentMuteState(value);
                break;
            case SysSettingSignal.SYS_5GP_SWITCH:
                SysSettingHelper.set5GSwitch(value);
                break;
            case SysSettingSignal.SYS_NIGHTTIME_MUTE_SWITCH:
                SysSettingHelper.setNighttimeMuteSwitch(value);
                break;
            case SysSettingSignal.SYS_ACOUSTIC_VEH_ALERT_SWITCH:
                SysSettingHelper.setLowSpeedPedesWarningState(value);
                break;
            case SysSettingSignal.SYS_SHOW_MEDIA_OUTSIDE_DIALOG:
                String carType = CarServicePropUtils.getInstance().getCarType();
                if (carType.equalsIgnoreCase("H37A")) {
                    try {
                        IFunctionManagerImpl.getInstance(Utils.getApp()).turnOnMediaOutside();
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    // 37B 走设置action接口
                    SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_MEDIAOUTSIDE);
                }
                break;
            case SysSettingSignal.SYS_REMOTE_KEY_BROADCAST_SWITCH:
                SysSettingHelper.setRemoteKeyBroadcastState(value);
                break;
            case SysSettingSignal.SYS_INTELLIGENT_VOLUME_SWITCH:
                SysSettingHelper.setIntelligentVolume(value);
                break;
            case SysSettingSignal.SYS_REAR_BELT_REMINDER_SWITCH:
            default:
                super.setBaseBooleanProp(key, area, value);
        }
    }

    @Override
    public String getBaseStringProp(String key, int area) {
        switch (key) {
            case SysSettingSignal.SYS_TIME_TYPE:
                return CommonSystemUtils.getTimeType();
            case SysSettingSignal.SYS_LANGUAGE:
                // TODO: 2024/5/23 当前语音接口暂未实现，暂时todo
                //String language = SystemUtils.getLanguage();
                return "中文";
            default:
                return super.getBaseStringProp(key, area);
        }
    }

    @Override
    public void setBaseStringProp(String key, int area, String value) {
        switch (key) {
            case SysSettingSignal.SYS_TIME_TYPE:
                CommonSystemUtils.setTimeType(value);
                break;
            case SysSettingSignal.SYS_LANGUAGE:
                CommonSystemUtils.setLanguage(CommonSystemUtils.getLanguageType().get(value));
                break;
            default:
                super.setBaseStringProp(key, area, value);
        }
    }

    @Override
    public void registerDevice() {
        String sysVolumeImitateConfig;
        String sysAcousticVehAlertModeConfig;
        String sysDiapasonConfig;
        String sysHasSoundEffectsMode;
        String sysSoundEffectsModeAllConfig;
        String sysVolumeFocusJustTts;
        String sysAcousticVehAlertModeALLConfig;
        String sysVolumeFeatureJustTts;
        String sysVolumeFeatureJustOpenPage;
        String sysGainConfig;
        String carType = CarServicePropUtils.getInstance().getCarType();
        String sysRearBeltReminderConfig;
        String sysHasAiMusicStyle;
        if (carType.equalsIgnoreCase("H37A")) {
            sysVolumeImitateConfig = "1";
            sysAcousticVehAlertModeConfig = "-1";
            sysDiapasonConfig = "1";
            sysHasSoundEffectsMode = "-1";
            sysSoundEffectsModeAllConfig = "-1";
            sysVolumeFocusJustTts = "1";
            sysAcousticVehAlertModeALLConfig = "-1";
            sysVolumeFeatureJustTts = "1";
            sysVolumeFeatureJustOpenPage = "1";
            sysGainConfig = "-1";
            sysRearBeltReminderConfig = "1";
            sysHasAiMusicStyle = "-1";
        } else {
            sysVolumeImitateConfig = "-1";
            sysAcousticVehAlertModeConfig = "1";
            sysDiapasonConfig = "-1";
            sysHasSoundEffectsMode = "1";
            sysSoundEffectsModeAllConfig = "1";
            sysVolumeFocusJustTts = "-1";
            sysAcousticVehAlertModeALLConfig = "1";
            sysVolumeFeatureJustTts = "-1";
            sysVolumeFeatureJustOpenPage = "-1";
            sysGainConfig = "1";
            sysRearBeltReminderConfig = "-1";
            sysHasAiMusicStyle = "1";
        }
        // 暂时只有37A支持，但是37B还不确定支不支持，之后做兼容
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(SysSettingSignal.SYS_5GP_CONFIG, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "1");
        // 夜间静音功能位
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(SysSettingSignal.SYS_NIGHTTIME_MUTE_CONFIG, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "1");
        // 模拟声浪功能位 37A支持 37B 56C 56D都不支持
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(SysSettingSignal.SYS_VOLUME_IMITATE_CONFIG, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, sysVolumeImitateConfig);
        // 低速行人警示模式切换 现在只有37A没有模式切换 37B有 和 56C和56D都是 N3 N4车型有
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(SysSettingSignal.SYS_ACOUSTIC_VEH_ALERT_MODE_CONFIG, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, sysAcousticVehAlertModeConfig);
        // 重低音只有56C 和 56D有
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(SysSettingSignal.SYS_DEEP_BASS_SUPPORT, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "-1");
        // 智能声场37A直接操作开关 56C需要前置条件判断，有前置条件=1，没有=-1
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(SysSettingSignal.SYS_INTELLIGENT_VOLUME_CONFIG, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "-1");
        // 是否有低音，中音，高音的调节，37A支持 37B不支持 56C支持 56D支持
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(SysSettingSignal.SYS_DIAPASON_CONFIG, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, sysDiapasonConfig);
        // 是否有音效模式功能 37A没有 37B有 56C有 56D有
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(SysSettingSignal.SYS_HAS_SOUND_EFFECTS_MODE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, sysHasSoundEffectsMode);
        // 音效模式功能是否是标配 37A没有这个功能 37B是标配 56C和56D是高配车型有（N3&N4有 N2没有）
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(SysSettingSignal.SYS_SOUND_EFFECTS_MODE_ALL_CONFIG, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, sysSoundEffectsModeAllConfig);
        // 声场聚焦是否是直接兜底回复 37A是直接兜底手动操作 37B 56C 56D是需要执行
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(SysSettingSignal.SYS_VOLUME_FOCUS_JUST_TTS, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, sysVolumeFocusJustTts);
        // 是否是标配有低速行人警示音模式调节 只有37B标配有，56C和56D高配车型有
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(SysSettingSignal.SYS_ACOUSTIC_VEH_ALERT_MODE_ALL_CONFIG, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, sysAcousticVehAlertModeALLConfig);
        // 声场音色是否直接tts回复手动操作 37A是直接tts兜底 37B 56C 56D都是执行
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(SysSettingSignal.SYS_VOLUME_FEATURE_JUST_TTS, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, sysVolumeFeatureJustTts);
        // 声场音色页面是否直接打开 37A是直接打开 37B 56C 56D需要判断条件
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(SysSettingSignal.SYS_VOLUME_FEATURE_JUST_OPEN_PAGE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, sysVolumeFeatureJustOpenPage);
        // 是否支持频段，增益，延时的调节，37A不支持 37B支持 56C支持 56D支持
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(SysSettingSignal.SYS_GAIN_CONFIG, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, sysGainConfig);
        // 是否有后排乘客安全带未系提醒 37A有 56C有 37B没有 56D没有
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(SysSettingSignal.SYS_REAR_BELT_REMINDER_CONFIG, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, sysRearBeltReminderConfig);
        // 是否有媒体外放功能 37A 37B 支持  56C 56D 不支持
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(SysSettingSignal.SYS_MEDIA_OUTPLAY_CONFIG, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "1");
        // 是否有AI音乐曲风开关功能 37A没有 37B 56C 56D有（56C和56D只有N3 N4车型才有）
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(SysSettingSignal.SYS_HAS_AI_MUSIC_STYLE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, sysHasAiMusicStyle);
        // AI音乐曲风开关功能是否是标配 37B是标配 56C和56D只有N3 N4车型才有
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(SysSettingSignal.SYS_AI_MUSIC_STYLE_ALL_CONFIG, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "1");
        // 是否有自动驻车激活提示音 37A 37B 56C 都没有，只有56D有
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(SysSettingSignal.SYS_HAS_AUTO_PARKING_HINT, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "-1");
    }
}
