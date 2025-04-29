package com.voyah.ai.device.base.recorder.forbidden;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.Settings;
import android.view.WindowManager;

import com.blankj.utilcode.util.Utils;
import com.vcos.common.widgets.vcostoast.VcosToastManager;
import com.voice.sdk.VoiceConfigManager;
import com.voice.sdk.VoiceImpl;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.forbidden.VoiceCarSignalInterface;
import com.voice.sdk.device.func.FuncConstants;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voice.sdk.device.ui.UIMgr;
import com.voice.sdk.record.VoiceStateRecordManager;
import com.voice.sdk.record.VoiceStatus;
import com.voyah.ai.basecar.carservice.CarServicePropUtils;
import com.voyah.ai.basecar.manager.SettingsManager;
import com.voyah.ai.basecar.media.vedio.ThunderKtvImpl;
import com.voyah.ai.basecar.utils.BeanDumpManager;
import com.voyah.ai.basecar.utils.VolumeUtils;
import com.voyah.ai.common.dump.IBeanDump;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.device.voyah.h37.dc.utils.MegaDataStorageConfigUtils;
import com.voyah.ai.sdk.bean.DhSwitch;
import com.voyah.ai.sdk.bean.LifeState;
import com.voyah.ai.sdk.manager.TTSManager;
import com.voyah.ai.voice.sdk.api.component.parameter.command.WakeupCommandParameters;
import com.voyah.cockpit.window.model.VoiceMode;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import mega.car.CarPropertyManager;
import mega.car.Signal;
import mega.car.config.Dms;
import mega.car.config.Driving;
import mega.car.config.Infotainment;
import mega.car.config.ParamsCommon;
import mega.car.config.SosRemote;
import mega.car.config.VehicleBody;
import mega.car.hardware.CarPropertyValue;

/**
 * @author:lcy 语音车辆信号管理(语音禁用管理 + 方控唤醒 。 。)
 * @data:2024/7/1
 **/
public class VoiceCarSignalManager implements IBeanDump, VoiceCarSignalInterface {
    private static final String TAG = VoiceCarSignalManager.class.getSimpleName();

    public static final int UNABLE_RECORD = 0;
    public static final int ENABLE_RECORD = 1;

    private static final int TYPE_DO_PTT = 0; //方控唤醒
    private static final int TYPE_WAKE_UP = 1; //主唤醒词唤醒

    private static final Object petModeLock = new Object(); //宠物模式锁
    private static final Object strModeLock = new Object(); //STR模式锁
    private static final Object carActivateStatusLock = new Object(); //车辆是否激活锁

    public static final String LONG_ASR_SOURCE = "longAsrSource";

    private final AtomicBoolean isShow = new AtomicBoolean(false);
    private static boolean isFrontLeftForbidden; //主驾是否禁止录音


    private long currentDoPttTime; //方控按键按压计时
    private boolean isCanDoPtt = true; //方控按键是否可用

    private boolean isPetMode; //宠物模式
    private boolean isReverseGear; //倒挡 视线盲区 (视线盲区的时候，肯定在R档)
    private boolean isSuperPowerSaving; //超级省电
    private boolean isSos; //紧急呼叫
    private boolean isSysUpdate; //系统升级
    private boolean isPhone; //通话中
    private boolean isSTR; //STR
    private boolean isCarNotActivate; //车辆是否激活

    private boolean isLsPlaying; //雷石KTV是否正在播放中

    private boolean isLongAsr; //语音输入法是否处于输入状态

//    private String carType; //车型

    private HandlerThread mHandlerThread;

    private Context mContext;


    //倒档、超级省电、SOS、系统升级中
    private static final Set<Integer> registerId = new HashSet<>(Arrays.asList(
            Driving.ID_DRV_INFO_GEAR_POSITION,
            Driving.ID_DRV_MODE,
            SosRemote.ID_ECALL_STATE,
            VehicleBody.ID_VEHICLE_USAGEMODE,
            Infotainment.ID_INFO_KEY_VOICE)); //,Signal.ID_DANGER_ACTION_EVENT 删除主驾电话行为，底层无法支持

    private static final String PRIVACY_SECURITY_KEY = "privacySecurity"; //隐私保护开关
    //    private static final String PET_MODE_KEY = "system_life_support_mode"; //宠物模式
    private static final String PET_MODE_KEY = "com.voyah.cockpit.scenemode.state"; //情景模式-宠物模式

    private static final String STR = "mega_android_power_status";//下电

    private static final String CAR_ACTIVATE_STATUS = "key_activate_status"; // 车辆激活

    private volatile boolean enableAudioRecord = true;

    private static class InnerHolder {
        private static final VoiceCarSignalManager instance = new VoiceCarSignalManager();
    }

    public static VoiceCarSignalManager getInstance() {
        return InnerHolder.instance;
    }

    public void init() {
        mContext = Utils.getApp();
        mHandlerThread = new HandlerThread(TAG);
        mHandlerThread.start();
//        carType = CarServicePropUtils.getInstance().getCarType();
        BeanDumpManager.getInstance().addDump(this);
        CarServicePropUtils.getInstance().registerCallback(carPropertyEventCallback, registerId);
        registerPetModeCallback();
//        registerLeiKtvPlayState();
        updateStatus();
    }

    @Override
    public void showVolumeToast(String screenName) {
        int volume = VolumeUtils.getInstance().getVolume(VolumeUtils.STREAM_ASSISTANT);
        boolean isMute = VolumeUtils.getInstance().getMuteSound("voice");
        LogUtils.d(TAG, "showVolumeToast volume:" + volume + " ,isMute:" + isMute);
        if (0 != volume && !isMute)
            return;

        if (isShow.getAndSet(true)) {
            return;
        }
        DeviceHolder.INS().getDevices().getSystem().getUi().showSystemToast(DeviceScreenType.fromValue(screenName), "语音音量处于静音状态");
    }

    private void updateStatus() {
        isReverseGear = getReverseGear() == Driving.ParamsDrvInfoGearPosition.REVERSE;
        isSuperPowerSaving = getSuperPowerStatus() == Driving.ParamsDrvMode.ECO_PLUS;
        isSos = getSosStatus() != SosRemote.EcallState.NOREQ;
        isSysUpdate = getSystemUpdateStatus() == VehicleBody.UsageMode.SYSTEMUPDATE;
        isPhone = getPhoneCallStatus() == 4;
        isLsPlaying = ThunderKtvImpl.INSTANCE.isPlaying() && ThunderKtvImpl.INSTANCE.isFront();
        isLongAsr = DeviceHolder.INS().getDevices().getLongAsr().isLongAsrGoing(1);
        LogUtils.d(TAG, "isPetMode:" + isPetMode + " ,isReverseGear:" + isReverseGear + " ,isSuperPowerSaving:"
                + isSuperPowerSaving + " ,isSos:" + isSos + " ,isSysUpdate:" + isSysUpdate + " ,isPhone:" + isPhone
                + " ,isSTR:" + isSTR + " ,isLsPlaying:" + isLsPlaying + " ,isLongAsr:" + isLongAsr + " ,isCarNotActivate:" + isCarNotActivate);
        enableAudioRecord = !isPetMode && !isReverseGear && !isSuperPowerSaving && !isSos && !isSysUpdate && !isPhone && !isSTR && !isLsPlaying && !isLongAsr && !isCarNotActivate;
        //语音禁用时，方控按键不可唤醒，仅Toast提示
        if (!enableAudioRecord) {
            isCanDoPtt = false;
            if (isSTR)
                DeviceHolder.INS().getDevices().getAudioRecorder().stopAudioRecorder();
            else
                VoiceImpl.getInstance().enableVoice(false);
        }
        LogUtils.i(TAG, "updateStatus enableAudioRecord is " + enableAudioRecord);
    }

    private void registerPetModeCallback() {
        //隐私保护开关切换监听
        mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(PRIVACY_SECURITY_KEY),
                false, new ContentObserver(new Handler(mHandlerThread.getLooper())) {
                    @Override
                    public void onChange(boolean selfChange) {
                        super.onChange(selfChange);
                        //获取隐私保护开关状态 0关闭 1开启
                        int privacySecurityStatus = Settings.System.getInt(Utils.getApp().getContentResolver(), "privacySecurity", 0);
                        DeviceHolder.INS().getDevices().getUiCardInterface().setVoiceMode(privacySecurityStatus == 0 ? VoiceMode.VOICE_MODE_ONLINE : VoiceMode.VOICE_MODE_OFFLINE);
                    }
                });


        mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(PET_MODE_KEY),
                false, new ContentObserver(new Handler(mHandlerThread.getLooper())) {
                    @Override
                    public void onChange(boolean selfChange) {
                        super.onChange(selfChange);
                        // 1:打开 0：关闭 // 宠物模式已打开
                        int petModeStatus = getPetMode();
                        isPetMode = petModeStatus == 1;
                        LogUtils.d(TAG, "petModeCallback  petModeStatus:" + petModeStatus);
                        if (petModeStatus == 1) {
                            deviceForbiddenStatus("pet", 0);
                        } else {
                            deviceForbiddenStatus("pet", 1);
                        }
                    }
                });
        isPetMode = getPetMode() == 1;

        //STR 参考连接 https://hav4xarv6k.feishu.cn/file/ULPabUIuooqFNOxH2GocXHfKnqc
        mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(STR),
                false, new ContentObserver(new Handler(mHandlerThread.getLooper())) {
                    @Override
                    public void onChange(boolean selfChange) {
                        super.onChange(selfChange);
                        int powerStatus = getPowerStatus();
                        isSTR = powerStatus == 21;
                        LogUtils.d(TAG, "powerStatusCallback  powerStatus:" + powerStatus);
                        //STATUS_ABANDONED 21 Android处于Abandoned状态(岚图项目独有，其表现和STANDBY基本一致)。
                        if (powerStatus == 21)
                            deviceForbiddenStatus("STR", 0);
                        else if (powerStatus == 6) //STATUS_ON 6 Android处于工作的active活跃状态，此状态机下屏幕处于点亮状态。
                            deviceForbiddenStatus("STR", 1);
                    }
                });
        int powerStatus = getPowerStatus();
        isSTR = (powerStatus == 21);

//        LogUtils.d(TAG, "registerPetModeCallback carType:" + carType);
//        if (StringUtils.equals(carType, "H77A")) {
//            mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(CAR_ACTIVATE_STATUS),
//                    false, new ContentObserver(new Handler(mHandlerThread.getLooper())) {
//                        @Override
//                        public void onChange(boolean selfChange) {
//                            super.onChange(selfChange);
//                            //车辆未激活时语音不可用
//                            if (!BuildConfig.DEBUG) {
//                                int carActivateStatus = getCarActivateStatus();
//                                isCarNotActivate = carActivateStatus <= 0;
//                                LogUtils.d(TAG, "isCarNotActivate  isCarNotActivate:" + isCarNotActivate);
//                                if (carActivateStatus > 0)
//                                    deviceForbiddenStatus("carActivateStatus", 1);
//                                else
//                                    deviceForbiddenStatus("carActivateStatus", 0);
//                            }
//                        }
//                    });
//            if (!BuildConfig.DEBUG)
//                isCarNotActivate = getCarActivateStatus() <= 0; //车辆未激活时语音不可用,大于0为激活
//        }
    }

    /**
     * carService 禁用语音服务信号监听
     */
    private CarPropertyManager.CarPropertyEventCallback carPropertyEventCallback = new CarPropertyManager.CarPropertyEventCallback() {
        @Override
        public void onChangeEvent(CarPropertyValue carPropertyValue) {
            if (null == carPropertyValue)
                return;
            int propertyId = carPropertyValue.getPropertyId();
            Object value = carPropertyValue.getValue();
            switch (propertyId) {
                case Driving.ID_DRV_INFO_GEAR_POSITION: //倒挡 视线盲区 (视线盲区的时候，肯定在R档)
                    if (value instanceof Integer && Driving.ParamsDrvInfoGearPosition.REVERSE == (int) value) {
                        isReverseGear = true;
                        deviceForbiddenStatus("Driving.ID_DRV_INFO_GEAR_POSITION", 0);
                    } else {
                        isReverseGear = false;
                        deviceForbiddenStatus("Driving.ID_DRV_INFO_GEAR_POSITION", 1);
                    }
                    break;
                case Driving.ID_DRV_MODE: //超级省电
                    if (value instanceof Integer && Driving.ParamsDrvMode.ECO_PLUS == (int) value) {
                        isSuperPowerSaving = true;
                        deviceForbiddenStatus("Driving.ID_DRV_MODE", 0);
                    } else {
                        isSuperPowerSaving = false;
                        deviceForbiddenStatus("Driving.ID_DRV_MODE", 1);
                    }
                    break;
                case SosRemote.ID_ECALL_STATE: //SOS
                    if (value instanceof Integer && ((int) value == SosRemote.EcallState.NOREQ || (int) value == SosRemote.EcallState.CANCELDIAL)) {
                        isSos = false;
                        deviceForbiddenStatus("SosRemote.ID_ECALL_STATE", 1);
                    } else {
                        isSos = true;
                        deviceForbiddenStatus("SosRemote.ID_ECALL_STATE", 0);
                    }
                    break;
                case VehicleBody.ID_VEHICLE_USAGEMODE: //系统升级中禁用语音
                    if (value instanceof Integer && VehicleBody.UsageMode.SYSTEMUPDATE == (int) value) {
                        isSysUpdate = true;
                        deviceForbiddenStatus("Driving.ID_VEHICLE_USAGEMODE", 0);
                    } else {
                        isSysUpdate = false;
                        deviceForbiddenStatus("Driving.ID_VEHICLE_USAGEMODE", 1);
                    }
                    break;
                //方控按键唤醒
                case Infotainment.ID_INFO_KEY_VOICE:
                    if (value instanceof Integer && ParamsCommon.Press.PRESSED == (int) value) {
                        LogUtils.i(TAG, "Infotainment.ID_INFO_KEY_VOICE value:" + value + " ,isCanDoPtt:" + isCanDoPtt);
                        if (System.currentTimeMillis() - currentDoPttTime >= 500) {
                            int voiceStatus = VoiceStateRecordManager.getInstance().getVoiceState();
                            String voiceLocation = VoiceStateRecordManager.getInstance().getVoiceLocation();
                            LogUtils.d(TAG, "voiceStatus:" + voiceStatus + " ,voiceLocation:" + voiceLocation);
                            //触发方控唤醒禁用场景提示
                            if (!isCanDoPtt) {
                                showForbiddenToast(0, FuncConstants.VALUE_SCREEN_CENTRAL);
                            } else {
                                if (voiceStatus != VoiceStatus.status.VOICE_STATE_EXIT) {
                                    if (StringUtils.equals(voiceLocation, VoiceStatus.wakeUpLocation.FIRST_ROW_LEFT)) {
                                        DeviceHolder.INS().getDevices().getTts().shutUpOneSelf();
                                        VoiceImpl.getInstance().exDialog();
                                        UIMgr.INSTANCE.forceExitAll("ID_INFO_KEY_VOICE");
                                        DeviceHolder.INS().getDevices().getDialogue().onVoiceStateCallback(LifeState.ASLEEP);
                                    } else {
                                        VoiceImpl.getInstance().wakeUp(0, WakeupCommandParameters.WAKEUP_DEFAULT);
                                    }
                                } else {
                                    VoiceImpl.getInstance().wakeUp(0, WakeupCommandParameters.WAKEUP_DEFAULT);
                                }
                            }

                            currentDoPttTime = System.currentTimeMillis();
                        }
                    }
                    break;
//                case Signal.ID_DANGER_ACTION_EVENT: //行为状态-监听主驾是否有打电话行为
//                    //  { "extension":null ,"relative":false, "time":0, "valid":true, "value":0}   // 0 无信息 1 提示打电话
//                    if (value instanceof String && !StringUtils.isBlank((String) value)) {
//                        try {
//                            int voiceMicMask = SettingsManager.get().getUserVoiceMicMask();
//                            JSONObject jsonObject = new JSONObject((String) value);
//                            int behaviorSts = jsonObject.getInt("BehaviorSts");
////                            LogUtils.d(TAG, "behaviorSts:" + behaviorSts + " ,voiceMicMask:" + voiceMicMask + " ,isFrontLeftForbidden:" + isFrontLeftForbidden);
//                            //0x0:初始值  0x1:正常  0x2:打电话
//                            if ((behaviorSts == 0) && isFrontLeftForbidden) {
//                                LogUtils.d(TAG, "0-1 behaviorSts:2 ,voiceMicMask:" + voiceMicMask + " ,isFrontLeftForbidden:false");
//                                VoiceImpl.getInstance().setRegionConfig(voiceMicMask);
//                                isFrontLeftForbidden = false;
//                                if (!isVoiceForbidden())
//                                    isCanDoPtt = true;
//                            } else if (behaviorSts == 1 && !isFrontLeftForbidden) {
//                                LogUtils.d(TAG, "2 behaviorSts:" + behaviorSts + " ,voiceMicMask:" + voiceMicMask + " ,isFrontLeftForbidden:false");
//                                int voiceStatus = VoiceStateRecordManager.getInstance().getVoiceState();
//                                String voiceLocation = VoiceStateRecordManager.getInstance().getVoiceLocation();
//                                LogUtils.d(TAG, "2 voiceStatus:" + voiceStatus + " ,voiceLocation:" + voiceLocation);
//                                if (voiceStatus == VoiceStatus.status.VOICE_STATE_AWAKE && StringUtils.equals(voiceLocation, VoiceStatus.wakeUpLocation.FIRST_ROW_LEFT)) {
//                                    //如果当前是主驾唤醒态
//                                    VoiceImpl.getInstance().exDialog();
//                                    DeviceHolder.INS().getDevices().getTts().shutUpOneSelf();
//                                    UIMgr.INSTANCE.forceExitAll("ID_FEATURE_FL");
//                                    showSystemToast("识别到主驾在通话,一会再用语音吧", FuncConstants.VALUE_SCREEN_CENTRAL);
//                                }
//                                VoiceImpl.getInstance().setRegionConfig(voiceMicMask - 1);
//                                isFrontLeftForbidden = true;
//                                isCanDoPtt = false;
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    break;
            }
        }

        @Override
        public void onErrorEvent(int i, int i1) {
            LogUtils.i(TAG, "i:" + i + " ,i1:" + i1);
        }
    };

    /**
     * 雷石KTV状态监听
     * 弃用
     *
     * @return
     */
    private void registerLeiKtvPlayState() {
        //onPlay   禁用
        //onCompletion onPause onStop onError 启用
        ThunderKtvImpl.INSTANCE.registerPlayStateListener(new ThunderKtvImpl.PlayStateListener() {
            @Override
            public void onPlay() {
                isLsPlaying = true;
                LogUtils.d(TAG, "onPlay");
                //禁用
                deviceForbiddenStatus("LEI_SHI_KTV", 0);
            }

            @Override
            public void onStop() {
                isLsPlaying = false;
                LogUtils.d(TAG, "onStop");
                deviceForbiddenStatus("LEI_SHI_KTV", 1);
            }
        });
        //获取雷石KTV播放状态
        isLsPlaying = ThunderKtvImpl.INSTANCE.isPlaying() && ThunderKtvImpl.INSTANCE.isFront();
    }

    /**
     * 雷石KTV状态监听
     * 广播方式监听结果
     */
    public void onLeiKtvReceiveStatus(boolean isLsPlay) {
        isLsPlaying = isLsPlay;
        deviceForbiddenStatus("LEI_SHI_KTV", isLsPlay ? 0 : 1);
    }

    @Override
    public void showForbiddenToast(int type, String screenName) {
        boolean enableWakeup = SettingsManager.get().isEnableSwitch(DhSwitch.MainWakeup);
        LogUtils.i(TAG, "showForbiddenToast type:" + type + " ,enableWakeup:" + enableWakeup);
        if (TYPE_DO_PTT != type && !enableWakeup) {
            return;
        }
        //主驾通话中
        if (isFrontLeftForbidden) {
            int voiceStatus = VoiceStateRecordManager.getInstance().getVoiceState();
            String voiceLocation = VoiceStateRecordManager.getInstance().getVoiceLocation();
            boolean isFirstRowLeft = voiceStatus == VoiceStatus.status.VOICE_STATE_AWAKE && StringUtils.equals(voiceLocation, VoiceStatus.wakeUpLocation.FIRST_ROW_LEFT);
            if (isFirstRowLeft || TYPE_DO_PTT == type)
                showSystemToast("识别到主驾在通话,一会再用语音吧", screenName);
            //通话
        } else if (TYPE_DO_PTT == type && isPhone)
            showSystemToast("蓝牙电话使用中，暂时无法使用语音", screenName);
            //SOS
        else if (TYPE_DO_PTT == type && isSos)
            showSystemToast("SOS使用中，暂时无法使用语音", screenName);
            //离车不下电
        else if (isPetMode)
            showSystemToast("宠物模式下，语音暂不可用", screenName);
            //STR
        else if (TYPE_DO_PTT == type && isSTR)
            showSystemToast("下电休眠中，暂时无法使用语音", screenName);
            //语音输入法识别中
        else if (TYPE_DO_PTT == type && isLongAsr)
            showSystemToast("输入法录音中，语音暂不可用", screenName);
            //R档
        else if (isReverseGear)
            showSystemToast("为了你的行车安全，暂时无法使用语音", screenName);
            //超级省电
        else if (isSuperPowerSaving)
            showSystemToast("超级省电模式下，语音暂不可用", screenName);
            //未激活
        else if (isCarNotActivate)
            showSystemToast("车辆未激活，语音暂不可用", screenName);
            //KTV
        else if (isLsPlaying)
            showSystemToast("K歌模式下，语音暂不可用", screenName);
            //升级
        else if (isSysUpdate)
            showSystemToast("系统升级过程中，暂时无法使用语音", screenName);
    }

    @Override
    public void showForbiddenToast(int type, int location) {
        showForbiddenToast(type, FuncConstants.VALUE_SCREEN_CENTRAL);
    }

    @Override
    public boolean isParkingLimitation() {
        return false;
    }

    private int getPetMode() {
        String pet = "";
        synchronized (petModeLock) {
            pet = Settings.System.getString(Utils.getApp().getContentResolver(),
                    PET_MODE_KEY);
        }
        LogUtils.d(TAG, "getPetMode pet:" + pet);
        return StringUtils.equals(pet, "state_pet_in_activation") ? 1 : 0;
    }

    private int getReverseGear() {
        return CarServicePropUtils.getInstance().getIntProp(Driving.ID_DRV_INFO_GEAR_POSITION);
    }

    private int getSuperPowerStatus() {
        return CarServicePropUtils.getInstance().getIntProp(Driving.ID_DRV_MODE);
    }

    private int getSosStatus() {
        return CarServicePropUtils.getInstance().getIntProp(SosRemote.ID_ECALL_STATE);
    }

    private int getSystemUpdateStatus() {
        return CarServicePropUtils.getInstance().getIntProp(VehicleBody.ID_VEHICLE_USAGEMODE);
    }

    private int getPhoneCallStatus() {
        return DeviceHolder.INS().getDevices().getPhone().getBluetoothCallState();
    }

    private int getPowerStatus() {
        synchronized (strModeLock) {
            return Settings.System.getInt(Utils.getApp().getContentResolver(), STR, 0);
        }
    }

    private int getCarActivateStatus() {
        synchronized (carActivateStatusLock) {
            return MegaDataStorageConfigUtils.getInt("key_activate_status", 0);
        }
    }


    /**
     * @param source 业务方
     * @param code   状态值 0:禁用 1:可用
     *               STR禁止录音
     *               其他场景不传音频
     */
    public void deviceForbiddenStatus(String source, int code) {
        LogUtils.d(TAG, "source:" + source + " ,code:" + code + " ,enableAudioRecord:" + enableAudioRecord);
        if (code == UNABLE_RECORD) {
            isCanDoPtt = false;
            if (StringUtils.equals(source, "STR"))
                DeviceHolder.INS().getDevices().getAudioRecorder().stopAudioRecorder();
            else
                VoiceImpl.getInstance().enableVoice(false);
//                AudioRecorderManager.getInstance().enableSendAudio(false);

            if (StringUtils.equals(source, "PhoneInterfaceImpl"))
                isPhone = true;
            if (StringUtils.equals(source, LONG_ASR_SOURCE))
                isLongAsr = true;
            if (enableAudioRecord) {
                VoiceImpl.getInstance().exDialog();
                UIMgr.INSTANCE.forceExitAll("deviceForbiddenStatus");
                DeviceHolder.INS().getDevices().getTts().shutUpOneSelf();
                DeviceHolder.INS().getDevices().getDialogue().onVoiceStateCallback(LifeState.ASLEEP);
                enableAudioRecord = false;
            }
        } else if (code == ENABLE_RECORD) {
            if (StringUtils.equals(source, "PhoneInterfaceImpl"))
                isPhone = false;
            if (StringUtils.equals(source, LONG_ASR_SOURCE))
                isLongAsr = false;
            if (StringUtils.equals(source, "STR")) {
                DeviceHolder.INS().getDevices().getAudioRecorder().startAudioRecorder();
            }
            if (!isFrontLeftForbidden && !isVoiceForbidden())
                isCanDoPtt = true;

            if (isEnableVoice())
                return;

            if (!enableAudioRecord) {
                VoiceImpl.getInstance().enableVoice(true);
                enableAudioRecord = true;
            }
//                AudioRecorderManager.getInstance().enableSendAudio(true);
            LogUtils.d(TAG, "deviceForbiddenStatus isSTR:" + isSTR);
            if (!DeviceHolder.INS().getDevices().getAudioRecorder().isAudioRecorderStart() && !isSTR)
                DeviceHolder.INS().getDevices().getAudioRecorder().startAudioRecorder();

        }
    }


    public void nearbyTtsStatusChange(boolean enable) {
        LogUtils.d(TAG, "nearbyTtsStatusChange enable:" + enable);
//        //就近交互开关关闭后修改为全车播报
        TTSManager.setNearByTtsStatus(enable);
    }


    public boolean getEnableAudioRecord() {
        LogUtils.i(TAG, "getEnableAudioRecord isSTR:" + isSTR);
        return !isSTR;
    }

    public boolean isEnableVoice() {
        LogUtils.d(TAG, "isEnableVoice isPetMode:" + isPetMode + " ,isReverseGear:" + isReverseGear + " ,isSuperPowerSaving:"
                + isSuperPowerSaving + " ,isSos:" + isSos + " ,isSysUpdate:" + isSysUpdate + " ,isPhone:" + isPhone + " ,isLsPlaying:" + isLsPlaying + " ,isCarNotActivate:" + isCarNotActivate);
        boolean isEnableVoice = !isPetMode && !isReverseGear && !isSuperPowerSaving && !isSos && !isSysUpdate && !isPhone && !isLsPlaying && !isCarNotActivate;
        LogUtils.d(TAG, "isEnableVoice isEnable is " + isEnableVoice);
        return !isEnableVoice;
    }

    public boolean isVoiceForbidden() {
        LogUtils.d(TAG, "getEnableAudioRecord isPetMode:" + isPetMode + " ,isReverseGear:" + isReverseGear + " ,isSuperPowerSaving:"
                + isSuperPowerSaving + " ,isSos:" + isSos + " ,isSysUpdate:" + isSysUpdate + " ,isPhone:" + isPhone + " ,isSTR:" + isSTR + " ,isLsPlaying:" + isLsPlaying + " ,isCarNotActivate:" + isCarNotActivate);
        boolean isEnable = !isPetMode && !isReverseGear && !isSuperPowerSaving && !isSos && !isSysUpdate && !isPhone && !isSTR && !isLsPlaying && !isCarNotActivate;
        LogUtils.d(TAG, "getEnableAudioRecord isEnable is " + isEnable);
        return !isEnable;
    }

    @Override
    public String getDumpInfo() {
        return "VoiceCarSignalManager:{" + '\n' +
                "\"isPetMode:\"" + isPetMode + "," + '\n'
                + "\"isReverseGear:\"" + isReverseGear + "," + '\n'
                + "\"isSuperPowerSaving:\"" + isSuperPowerSaving + "," + '\n'
                + "\"isSos:\"" + isSos + "," + '\n'
                + "\"isSysUpdate:\"" + isSysUpdate + "," + '\n'
                + "\"isPhone:\"" + isPhone + "," + '\n'
                + "\"isSTR:\"" + isSTR + "," + '\n'
                + "\"isLsPlaying:\"" + isLsPlaying + '\n'
                + "\"isCarNotActivate:\"" + isCarNotActivate + '\n'
                + "\"isLongAsr:\"" + isLongAsr + '\n'
                + "\"env:\"" + VoiceConfigManager.getInstance().getVoiceEnv() + '\n'
                + "\"appVersion:\"" + DeviceHolder.INS().getDevices().getSystem().getApp().getAppVersionName() + '\n'
                + "}"
                ;
    }

    public void showSystemToast(String toastText, String screenName) {
        DeviceHolder.INS().getDevices().getSystem().getUi().showSystemToast(DeviceScreenType.fromValue(screenName), toastText, WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW + 21);

    }


//    public void aaa(int behaviorSts) {
//
//        int voiceMicMask = SettingsManager.get().getUserVoiceMicMask();
////                JSONObject jsonObject = new JSONObject((String) value);
////                int behaviorSts = jsonObject.getInt("BehaviorSts");
////                            LogUtils.d(TAG, "behaviorSts:" + behaviorSts + " ,voiceMicMask:" + voiceMicMask + " ,isFrontLeftForbidden:" + isFrontLeftForbidden);
//        //0x0:初始值  0x1:正常  0x2:打电话
//        if ((behaviorSts == 0 || behaviorSts == 1) && isFrontLeftForbidden) {
//            LogUtils.d(TAG, "0-1 behaviorSts:2 ,voiceMicMask:" + voiceMicMask + " ,isFrontLeftForbidden:false");
//            VoiceImpl.getInstance().setRegionConfig(voiceMicMask);
//            isFrontLeftForbidden = false;
//            if (!isVoiceForbidden())
//                isCanDoPtt = true;
//        } else if (behaviorSts == 2 && !isFrontLeftForbidden) {
//            LogUtils.d(TAG, "2 behaviorSts:" + behaviorSts + " ,voiceMicMask:" + voiceMicMask + " ,isFrontLeftForbidden:false");
//            int voiceStatus = VoiceStateRecordManager.getInstance().getVoiceState();
//            String voiceLocation = VoiceStateRecordManager.getInstance().getVoiceLocation();
//            LogUtils.d(TAG, "2 voiceStatus:" + voiceStatus + " ,voiceLocation:" + voiceLocation);
//            if (voiceStatus == VoiceStatus.status.VOICE_STATE_AWAKE && StringUtils.equals(voiceLocation, VoiceStatus.wakeUpLocation.FIRST_ROW_LEFT)) {
//                //如果当前是主驾唤醒态
//                VoiceImpl.getInstance().exDialog();
//                DeviceHolder.INS().getDevices().getTts().shutUpOneSelf();
//                showSystemToast("识别到主驾在通话,一会再用语音吧");
//            }
//            VoiceImpl.getInstance().setRegionConfig(voiceMicMask - 1);
//            isFrontLeftForbidden = true;
//            isCanDoPtt = false;
//        }
//    }

//    public void bb(int strState) {
//        isSTR = strState == 21;
//        LogUtils.d(TAG, "powerStatusCallback  strState:" + strState);
//        //STATUS_ABANDONED 21 Android处于Abandoned状态(岚图项目独有，其表现和STANDBY基本一致)。
//        if (strState == 21)
//            deviceForbiddenStatus("STR", 0);
//        else if (strState == 6) //STATUS_ON 6 Android处于工作的active活跃状态，此状态机下屏幕处于点亮状态。
//            deviceForbiddenStatus("STR", 1);
//    }
}
