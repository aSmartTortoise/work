package com.voyah.ai.device.base.recorder.forbidden;

import static com.voyah.ai.basecar.manager.LongAsrManager.LONG_ASR_SOURCE;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings;
import android.view.WindowManager;

import com.blankj.utilcode.util.Utils;
import com.voice.sdk.VoiceConfigManager;
import com.voice.sdk.VoiceImpl;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.vcar.CarPropertyEventCallback;
import com.voice.sdk.device.carservice.vcar.CarPropertyValue;
import com.voice.sdk.device.forbidden.VoiceCarSignalInterface;
import com.voice.sdk.device.func.FuncConstants;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voice.sdk.device.ui.UIMgr;
import com.voyah.ai.basecar.carservice.CarServicePropUtils;
import com.voyah.ai.basecar.manager.LongAsrManager;
import com.voyah.ai.basecar.manager.SettingsManager;
import com.voice.sdk.record.VoiceStateRecordManager;
import com.voice.sdk.record.VoiceStatus;
import com.voyah.ai.basecar.media.vedio.ThunderKtvImpl;
import com.voyah.ai.basecar.utils.VolumeUtils;
import com.voyah.ai.basecar.utils.BeanDumpManager;
import com.voyah.ai.common.dump.IBeanDump;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.device.voyah.h37.dc.utils.MegaDataStorageConfigUtils;
import com.voyah.ai.sdk.bean.DhSwitch;
import com.voyah.ai.sdk.bean.LifeState;
import com.voyah.ai.sdk.manager.TTSManager;
import com.voyah.ai.voice.sdk.api.component.parameter.command.WakeupCommandParameters;
import com.voyah.cockpit.window.model.VoiceMode;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import mega.car.config.Driving;
import mega.car.config.H56D;
import mega.car.config.ParamsCommon;
import mega.car.config.Qnx;
import mega.car.config.SomeIp;

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

    private static final Object LeaveCarKeepPowerLock = new Object(); //离车不下电
    private static final Object strModeLock = new Object(); //STR模式锁
    private static final Object carActivateStatusLock = new Object(); //车辆是否激活锁

    private static final String STATE_PET_IN_ACTIVATION = "state_pet_in_activation"; //离车不下电模式返回值
    private static final String STATE_MANUAL_CAR_WASH_ACTIVE = "state_manual_car_wash_active"; //手动洗车模式中
    private static final String STATE_AUTO_CAR_WASH_ACTIVE = "state_auto_car_wash_active"; //自动洗车模式中

    private final AtomicBoolean isShow = new AtomicBoolean(false);
    private static boolean isFrontLeftForbidden; //主驾是否禁止录音

    private long currentDoPttTime; //方控按键按压计时
    private boolean isCanDoPtt = true; //方控按键是否可用

    private boolean isLeaveCarKeepPower; //56C离车不下电
    private boolean isCarWash; //是否处于洗车模式中
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
//    SosRemote.ID_ECALL_STATE 37SOS
    private static final Set<Integer> registerId = new HashSet<>(Arrays.asList(
            Driving.ID_DRV_INFO_GEAR_POSITION,
            Driving.ID_DRV_MODE,
            SomeIp.ID_ECALL_MSG,
            H56D.TBOX_VehicleMode_TBOX_VEHICLEMODE,
            H56D.SWS_Set_SWS_VOICE,
            Qnx.ID_DANGER_ACTION_EVENT));

    private static final String PRIVACY_SECURITY_KEY = "privacySecurity"; //隐私保护开关
    //    private static final String PET_MODE_KEY = "system_life_support_mode"; //宠物模式
    private static final String SCENE_MODE_KEY = "com.voyah.cockpit.scenemode.state"; //56C离车不下电 洗车模式

    private static final String STR = "mega_android_power_status";//下电

    private static final String CAR_ACTIVATE_STATUS = "key_activate_status"; // 车辆激活

    private static final String KEY_PARKING_APP_STATUS = "persist.sys.parking.app_status"; //泊车状态key

    private volatile boolean enableAudioRecord = true;

    private static class InnerHolder {
        private static final VoiceCarSignalManager instance = new VoiceCarSignalManager();
    }

    public static VoiceCarSignalManager getInstance() {
        return InnerHolder.instance;
    }

    @Override
    public void init() {
        mContext = Utils.getApp();
        mHandlerThread = new HandlerThread(TAG);
        mHandlerThread.start();
//        carType = CarServicePropUtils.getInstance().getCarType();
        BeanDumpManager.getInstance().addDump(this);
//        CarServicePropUtils.getInstance().registerCallback(carPropertyEventCallback, registerId);
        DeviceHolder.INS().getDevices().getCarServiceProp().registerCallback(carPropertyEventCallback, registerId);
        registerModeCallback();
//        registerLeiKtvPlayState();
        updateStatus();
    }

    private void updateStatus() {
//        isReverseGear = getReverseGear() == 1;
        isReverseGear = false;
        //56C 没有超级省电模式
        isSuperPowerSaving = false;
        isSos = getSosStatus() != 0;
        isSysUpdate = getSystemUpdateStatus() == 5;
        isPhone = getPhoneCallStatus() == 4;
        isLsPlaying = ThunderKtvImpl.INSTANCE.isPlaying() && ThunderKtvImpl.INSTANCE.isFront();
        isLongAsr = LongAsrManager.get().isLongAsrGoing(1);
        LogUtils.d(TAG, "isLeaveCarKeepPower:" + isLeaveCarKeepPower + " ,isCarWash:" + isCarWash + " ,isReverseGear:" + isReverseGear + " ,isSuperPowerSaving:"
                + isSuperPowerSaving + " ,isSos:" + isSos + " ,isSysUpdate:" + isSysUpdate + " ,isPhone:" + isPhone
                + " ,isSTR:" + isSTR + " ,isLsPlaying:" + isLsPlaying + " ,isLongAsr:" + isLongAsr + " ,isCarNotActivate:" + isCarNotActivate);
        enableAudioRecord = !isCarWash && !isLeaveCarKeepPower && !isReverseGear && !isSuperPowerSaving && !isSos && !isSysUpdate && !isPhone && !isSTR && !isLsPlaying && !isLongAsr && !isCarNotActivate;
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

    private void registerModeCallback() {
        //隐私保护开关切换监听
        mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(PRIVACY_SECURITY_KEY),
                false, new ContentObserver(new Handler(mHandlerThread.getLooper())) {
                    @Override
                    public void onChange(boolean selfChange) {
                        super.onChange(selfChange);
                        //获取隐私保护开关状态 0关闭 1开启
                        int privacySecurityStatus = DeviceHolder.INS().getDevices().getLauncher().getPrivacySecurity();
                        DeviceHolder.INS().getDevices().getUiCardInterface().setVoiceMode(privacySecurityStatus == 0 ? VoiceMode.VOICE_MODE_ONLINE : VoiceMode.VOICE_MODE_OFFLINE);
                    }
                });


        mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(SCENE_MODE_KEY),
                false, new ContentObserver(new Handler(mHandlerThread.getLooper())) {
                    @Override
                    public void onChange(boolean selfChange) {
                        super.onChange(selfChange);
                        String currentSceneMode = getSceneMode();
                        LogUtils.d(TAG, "registerModeCallback currentSceneMode:" + currentSceneMode);
                        if (StringUtils.equals(currentSceneMode, STATE_PET_IN_ACTIVATION)) {
                            //离车不下电
                            isLeaveCarKeepPower = true;
                            deviceForbiddenStatus("pet", 0);
                        } else if (StringUtils.equals(currentSceneMode, STATE_MANUAL_CAR_WASH_ACTIVE) || StringUtils.equals(currentSceneMode, STATE_AUTO_CAR_WASH_ACTIVE)) {
                            //洗车模式
                            isCarWash = true;
                            deviceForbiddenStatus("carWash", 0);
                        } else if (StringUtils.isBlank(currentSceneMode)) {
                            //不处于任何禁用(离车不下电和洗车模式互斥，不会同时存在)
                            isLeaveCarKeepPower = false;
                            isCarWash = false;
                            deviceForbiddenStatus("pet", 1);
                            deviceForbiddenStatus("carWash", 1);
                        }

                    }
                });
        String currentSceneMode = getSceneMode();
        isLeaveCarKeepPower = StringUtils.equals(currentSceneMode, STATE_PET_IN_ACTIVATION);
        isCarWash = StringUtils.equals(currentSceneMode, STATE_MANUAL_CAR_WASH_ACTIVE) || StringUtils.equals(currentSceneMode, STATE_AUTO_CAR_WASH_ACTIVE);

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
//            MegaDataStorageConfig.getContentResolver().registerContentObserver(Settings.System.getUriFor(CAR_ACTIVATE_STATUS),
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

    private CarPropertyEventCallback carPropertyEventCallback = new CarPropertyEventCallback() {
        @Override
        public void onChangeEvent(CarPropertyValue carPropertyValue) {
            if (null == carPropertyValue) {
                LogUtils.d(TAG, "carPropertyValue is null");
                return;
            }
            int propertyId = carPropertyValue.getPropertyId();
            Object value = carPropertyValue.getValue();
            switch (propertyId) {
                //H56D移除
//                case Driving.ID_DRV_INFO_GEAR_POSITION: //倒挡 视线盲区 (视线盲区的时候，肯定在R档)
//                    if (value instanceof Integer && 1 == (int) value) {
//                        isReverseGear = true;
//                        deviceForbiddenStatus("Driving.ID_DRV_INFO_GEAR_POSITION", 0);
//                    } else {
//                        isReverseGear = false;
//                        deviceForbiddenStatus("Driving.ID_DRV_INFO_GEAR_POSITION", 1);
//                    }
//                    break;
                /*case Driving.ID_DRV_MODE: //超级省电
                    if (value instanceof Integer && 4 == (int) value) {
                        isSuperPowerSaving = true;
                        deviceForbiddenStatus("Driving.ID_DRV_MODE", 0);
                    } else {
                        isSuperPowerSaving = false;
                        deviceForbiddenStatus("Driving.ID_DRV_MODE", 1);
                    }
                    break;*/
                case SomeIp.ID_ECALL_MSG: //SOS
//                {
//                    "CallDisplayReq": 1, //呼叫显示请求
//                        "MuteOut": 1, //静音
//                        "EcallSystem_Status":1//Ecall触发状态
//                }
//                    CallDisplayReq:
//                    0x0:无请求
//                    0x1:确认拨号
//                    0x2:取消拨号
//                    0x3:呼叫连接
//                    0x4:通话中
//                    0x5:通话结束
//                    0x6:呼叫转移
//                    0x7:回拨
//                    0x8:重拨
//                    0x9:呼叫失败重拨
                    try {
                        if (value instanceof String && !StringUtils.isBlank((String) value)) {
                            JSONObject jsonObject = new JSONObject((String) value);
                            LogUtils.d(TAG, "SomeIp.ID_ECALL_MSG value:" + value);
                            int CallDisplayReq = jsonObject.getInt("CallDisplayReq");
                            LogUtils.d(TAG, "SomeIp.ID_ECALL_MSG CallDisplayReq:" + CallDisplayReq);
                            if (CallDisplayReq == 1 || CallDisplayReq == 3 || CallDisplayReq == 4 || CallDisplayReq == 7 || CallDisplayReq == 8 || CallDisplayReq == 9) {
                                isSos = true;
                                deviceForbiddenStatus("SomeIp.ID_ECALL_MSG", 0);
                            } else {
                                isSos = false;
                                deviceForbiddenStatus("SomeIp.ID_ECALL_MSG", 1);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case H56D.TBOX_VehicleMode_TBOX_VEHICLEMODE: //系统升级中禁用语音
                    if (value instanceof Integer && 5 == (int) value) {
                        isSysUpdate = true;
                        deviceForbiddenStatus("Driving.ID_VEHICLE_USAGEMODE", 0);
                    } else {
                        isSysUpdate = false;
                        deviceForbiddenStatus("Driving.ID_VEHICLE_USAGEMODE", 1);
                    }
                    break;
                //方控按键唤醒
                case H56D.SWS_Set_SWS_VOICE:
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
                case Qnx.ID_DANGER_ACTION_EVENT: //行为状态-监听主驾是否有打电话行为
//                    LogUtils.d(TAG, "Dms.ID_FEATURE_FL value is " + value);
//                    if (value instanceof String && !StringUtils.isBlank((String) value)) {
//                        try {
//                            int voiceMicMask = SettingsManager.get().getUserVoiceMicMask();
//                            JSONObject jsonObject = new JSONObject((String) value);
//                            int behaviorSts = jsonObject.getInt("BehaviorSts");
////                            LogUtils.d(TAG, "behaviorSts:" + behaviorSts + " ,voiceMicMask:" + voiceMicMask + " ,isFrontLeftForbidden:" + isFrontLeftForbidden);
//                            //0x0:初始值  0x1:正常  0x2:打电话
//                            if ((behaviorSts == 0 || behaviorSts == 1) && isFrontLeftForbidden) {
//                                LogUtils.d(TAG, "0-1 behaviorSts:2 ,voiceMicMask:" + voiceMicMask + " ,isFrontLeftForbidden:false");
//                                VoiceImpl.getInstance().setRegionConfig(voiceMicMask);
//                                isFrontLeftForbidden = false;
//                                if (!isVoiceForbidden())
//                                    isCanDoPtt = true;
//                            } else if (behaviorSts == 2 && !isFrontLeftForbidden) {
//                                LogUtils.d(TAG, "2 behaviorSts:" + behaviorSts + " ,voiceMicMask:" + voiceMicMask + " ,isFrontLeftForbidden:false");
//                                int voiceStatus = VoiceStateRecordManager.getInstance().getVoiceState();
//                                String voiceLocation = VoiceStateRecordManager.getInstance().getVoiceLocation();
//                                LogUtils.d(TAG, "2 voiceStatus:" + voiceStatus + " ,voiceLocation:" + voiceLocation);
//                                if (voiceStatus == VoiceStatus.status.VOICE_STATE_AWAKE && StringUtils.equals(voiceLocation, VoiceStatus.wakeUpLocation.FIRST_ROW_LEFT)) {
//                                    //如果当前是主驾唤醒态
//                                    VoiceImpl.getInstance().exDialog();
//                                    DeviceHolder.INS().getDevices().getTts().shutUpOneSelf();
//                                    UIMgr.INSTANCE.forceExitAll("ID_FEATURE_FL");
//                                    showSystemToast("识别到主驾在通话,一会再用语音吧");
//                                }
//                                VoiceImpl.getInstance().setRegionConfig(voiceMicMask - 1);
//                                isFrontLeftForbidden = true;
//                                isCanDoPtt = false;
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
                    break;
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
    @Override
    public void onLeiKtvReceiveStatus(boolean isLsPlay) {
        isLsPlaying = isLsPlay;
        deviceForbiddenStatus("LEI_SHI_KTV", isLsPlay ? 0 : 1);
    }

//    private int getLeaveCarKeepPower() {
//        String pet = "";
//        synchronized (LeaveCarKeepPowerLock) {
//            pet = Settings.System.getString(Utils.getApp().getContentResolver(),
//                    SCENE_MODE_KEY);
//        }
//        LogUtils.d(TAG, "getLeaveCarKeepPower:" + pet);
//        return StringUtils.equals(pet, "state_pet_in_activation") ? 1 : 0;
//    }

    private String getSceneMode() {
        String currentSceneMode = Settings.System.getString(Utils.getApp().getContentResolver(),
                SCENE_MODE_KEY);
        LogUtils.d(TAG, "getSceneMode currentSceneMode:" + currentSceneMode);
        return currentSceneMode;
    }

    private int getReverseGear() {
        return CarServicePropUtils.getInstance().getIntProp(Driving.ID_DRV_INFO_GEAR_POSITION);
    }

    private int getSuperPowerStatus() {
        return CarServicePropUtils.getInstance().getIntProp(Driving.ID_DRV_MODE);
    }

    private int getSosStatus() {
        int status = 0;
        CarPropertyValue carPropertyValue = DeviceHolder.INS().getDevices().getCarServiceProp().getPropertyRaw(SomeIp.ID_ECALL_MSG);
        if (null != carPropertyValue && null != carPropertyValue.getValue()) {
            try {
                if (carPropertyValue.getValue() instanceof String && !StringUtils.isBlank((String) carPropertyValue.getValue())) {
                    JSONObject jsonObject = new JSONObject((String) carPropertyValue.getValue());
                    LogUtils.d(TAG, "getSosStatus:" + carPropertyValue.getValue());
                    int CallDisplayReq = jsonObject.getInt("CallDisplayReq");
                    LogUtils.d(TAG, "getSosStatus:" + CallDisplayReq);
                    if (CallDisplayReq == 1 || CallDisplayReq == 3 || CallDisplayReq == 4 || CallDisplayReq == 7 || CallDisplayReq == 8 || CallDisplayReq == 9)
                        status = 1;

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return status;
    }

    private int getSystemUpdateStatus() {
//        return CarServicePropUtils.getInstance().getIntProp(150995007);
        return CarServicePropUtils.getInstance().getIntProp(H56D.TBOX_VehicleMode_TBOX_VEHICLEMODE);
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
    @Override
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

    @Override
    public boolean getEnableAudioRecord() {
        LogUtils.i(TAG, "getEnableAudioRecord isSTR:" + isSTR);
        return !isSTR;
    }

    public boolean isEnableVoice() {
        LogUtils.d(TAG, "isEnableVoice isLeaveCarKeepPower:" + isLeaveCarKeepPower + " ,isCarWash:" + isCarWash + " ,isReverseGear:" + isReverseGear + " ,isSuperPowerSaving:"
                + isSuperPowerSaving + " ,isSos:" + isSos + " ,isSysUpdate:" + isSysUpdate + " ,isPhone:" + isPhone + " ,isLsPlaying:" + isLsPlaying + " ,isCarNotActivate:" + isCarNotActivate);
        boolean isEnableVoice = !isLeaveCarKeepPower && !isCarWash && !isReverseGear && !isSuperPowerSaving && !isSos && !isSysUpdate && !isPhone && !isLsPlaying && !isCarNotActivate;
        LogUtils.d(TAG, "isEnableVoice isEnable is " + isEnableVoice);
        return !isEnableVoice;
    }

    @Override
    public boolean isVoiceForbidden() {
        LogUtils.d(TAG, "getEnableAudioRecord isLeaveCarKeepPower:" + isLeaveCarKeepPower + " ,isCarWash:" + isCarWash + " ,isReverseGear:" + isReverseGear + " ,isSuperPowerSaving:"
                + isSuperPowerSaving + " ,isSos:" + isSos + " ,isSysUpdate:" + isSysUpdate + " ,isPhone:" + isPhone + " ,isSTR:" + isSTR + " ,isLsPlaying:" + isLsPlaying + " ,isCarNotActivate:" + isCarNotActivate);
        boolean isEnable = !isLeaveCarKeepPower && !isCarWash && !isReverseGear && !isSuperPowerSaving && !isSos && !isSysUpdate && !isPhone && !isSTR && !isLsPlaying && !isCarNotActivate;
        LogUtils.d(TAG, "getEnableAudioRecord isEnable is " + isEnable);
        return !isEnable;
    }

    @Override
    public void showForbiddenToast(int type, int location) {
        showForbiddenToast(type, getScreenName(location));
    }

    //部分垂域功能禁用条件由单独的R挡改为R挡、蟹行模式、离车泊入、自动泊车控车状态和遥控泊车状态。
    @Override
    public boolean isParkingLimitation() {
        //0后台, 1前台, 2霸屏
        int parkingStatus = Settings.System.getInt(Utils.getApp().getContentResolver(), KEY_PARKING_APP_STATUS, 0);
        LogUtils.d(TAG, "getParkingStatus parkingStatus:" + parkingStatus);
        return parkingStatus == 2;
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
        else if (isLeaveCarKeepPower)
            showSystemToast("离车不下电模式开启中，语音暂不可用", screenName);
        else if (isCarWash)
            showSystemToast("洗车模式中，暂时无法使用语音", screenName);
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
    public String getDumpInfo() {
        return "VoiceCarSignalManager:{" + '\n' +
                "\"isLeaveCarKeepPower:\"" + isLeaveCarKeepPower + "," + '\n'
                + "\"isCarWash:\"" + isCarWash + "," + '\n'
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

    private String getScreenName(int location) {
        String screenName = FuncConstants.VALUE_SCREEN_CENTRAL;
        switch (location) {
            case 0:
                screenName = FuncConstants.VALUE_SCREEN_CENTRAL;
                break;
            case 1:
                screenName = FuncConstants.VALUE_SCREEN_PASSENGER;
                break;
            case 2:
            case 3:
            case 4:
            case 5:
                screenName = FuncConstants.VALUE_SCREEN_CEIL;
                break;
        }
        return screenName;
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
