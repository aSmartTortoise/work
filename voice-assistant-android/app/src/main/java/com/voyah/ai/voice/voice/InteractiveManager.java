package com.voyah.ai.voice.voice;

import static com.voyah.ai.voice.sdk.kernel.api.callback.StateCallbackCode.VOICE_EVENT_RECOGNIZE_BEGIN;
import static com.voyah.ai.voice.sdk.kernel.api.callback.StateCallbackCode.VOICE_EVENT_RECOGNIZE_END;
import static com.voyah.ai.voice.sdk.kernel.api.callback.StateCallbackCode.VOICE_EVENT_WAKEUP;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.provider.Settings;

import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;
import com.vcos.vehicle.env.EnvApi;
import com.voice.sdk.IVoAIVoiceSDKInitCallback;
import com.voice.sdk.PathUtil;
import com.voice.sdk.VoiceImpl;
import com.voice.sdk.context.DeviceContextUtils;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.base.SettingsInterface;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.ui.UIMgr;
import com.voice.sdk.record.VoiceStateRecordManager;
import com.voice.sdk.record.VoiceStatus;
import com.voyah.ai.common.helper.ResMgr;
import com.voyah.ai.common.utils.FileUtils;
import com.voyah.ai.common.utils.IflytekUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.engineer.window.RecordTipFloatWindow;
import com.voyah.ai.logic.buriedpoint.BuriedPointHelper;
import com.voyah.ai.sdk.bean.DhSwitch;
import com.voyah.ai.sdk.bean.LifeState;
import com.voyah.ai.voice.agent.AgentIntentManager;
import com.voyah.ai.voice.platform.agent.api.VehicleControlToolApi;
import com.voyah.ai.voice.platform.agent.api.datasyn.DataSynHelper;
import com.voyah.ai.voice.platform.agent.api.function.mapping.OriginalFunctionMapping;
import com.voyah.ai.voice.platform.agent.api.functionMatrix.FunctionDeviceMappingManager;
import com.voyah.ai.voice.platform.agent.api.tts.TTSIDConvertHelper;
import com.voyah.ai.voice.platform.soa.api.parameter.FeatureConfig;
import com.voyah.ai.voice.platform.soa.api.parameter.SpeechConfig;
import com.voyah.ai.voice.receiver.voice.LeiKtvReceiver;
import com.voyah.ai.voice.receiver.voice.PackageRemovedReceiver;
import com.voyah.api.ClientApi;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author:lcy 语音流程使用初始化项
 * @data:2024/6/25
 **/
public class InteractiveManager {
    private static final String TAG = InteractiveManager.class.getSimpleName();

    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    private static class InnerHolder {
        private static final InteractiveManager instance = new InteractiveManager();
    }

    public static InteractiveManager getInstance() {
        return InnerHolder.instance;
    }

    public void init() {
        LogUtils.d(TAG, "InteractiveManager init");
        IflytekUtils.copyResIfNeed(new IflytekUtils.Callback() {
            @Override
            public void onSuccess() {
                LogUtils.d(TAG, "copyResIfNeed, onSuccess() called");
                ThreadUtils.getMainHandler().post(() -> initSdk());
            }

            @Override
            public void onFail(String errMsg) {
                LogUtils.d(TAG, "onFail() called with: errMsg = [" + errMsg + "]");
                DeviceHolder.INS().getDevices().getSystem().getUi().showSystemToast(null, errMsg);
            }
        });

    }

    private void startInit() {
        LogUtils.d(TAG, "InteractiveManager startInit");
        boolean isCar = DeviceHolder.INS().getDevices().getCarServiceProp().vehicleSimulatorJudgment();
        if (isCar)
            Settings.Global.putString(Utils.getApp().getContentResolver(), "version.software.speech", DeviceHolder.INS().getDevices().getSystem().getApp().getAppVersionName());
        setVoiceCreateConfig();

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                AgentIntentManager agentIntentManager = new AgentIntentManager();
                agentIntentManager.initAgent();
                VoiceImpl.getInstance().addAgents(agentIntentManager.getAgentList());
                registerPackageRemoveReceiver(Utils.getApp());
                registerLeiKtvReceiver(Utils.getApp());
            }
        });
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                if (isCar && DeviceHolder.INS().getDevices().getVoiceCarSignal().getEnableAudioRecord())
                    DeviceHolder.INS().getDevices().getAudioRecorder().startAudioRecorder();

                ResMgr.INSTANCE.tryUpdateRes(Utils.getApp());
                DeviceHolder.INS().getDevices().getDialogue().onVoiceStateCallback(LifeState.READY);

                VehicleControlToolApi.getInstance().init(PathUtil.getGraphPath(), new H37RealDevice(), null);
                //初始化ttsid处理类
                TTSIDConvertHelper.getInstance().init(PathUtil.getConfigurationPath());
                //触发资源同步的回调注册
                VehicleControlToolApi.getInstance();
                //初始化功能引导的类
                FunctionDeviceMappingManager.getInstance().init(PathUtil.getConfigurationPath());
                //初始化埋点类
                try {
                    LogUtils.d(TAG, "埋点数据初始化开始");
                    BuriedPointHelper.getInstance().init(PathUtil.getConfigurationPath());
                    DeviceHolder.INS().getDevices().getBuriedPointManager().init(PathUtil.getConfigurationPath());
                    LogUtils.d(TAG, "埋点数据初始化结束");
                } catch (Exception e) {
                    LogUtils.d(TAG, "埋点数据源开始初始化报错：" + e);
                }
                //初始化函数映射类
                String souce = FileUtils.readFile(new File(PathUtil.SOURCE_PATH + "function_mapping_new_old.json"));
                OriginalFunctionMapping.getInstance().init(souce);

                //初始化数据同步处理类
                DataSynHelper.getInstance().init(PathUtil.CONF_FILE, DeviceHolder.INS().getDevices().getCarServiceProp().getCarType(),
                        DeviceHolder.INS().getDevices().getSystem().getApp().getAppVersionCode(), EnvApi.getEnv(Utils.getApp()) == 1);

                // 设置是否dump实时音频
                setDumpAudioIfNeed();
            }
        });
    }

    private void registerPackageRemoveReceiver(Context context) {
        LogUtils.d(TAG, "registerPackageRemoveReceiver");
        // 初始化广播接收器
        PackageRemovedReceiver receiver = new PackageRemovedReceiver();
        // 创建 IntentFilter，监听 PACKAGE_REMOVED 广播
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        // 注册广播接收器
        context.registerReceiver(receiver, filter);
    }

    private void registerLeiKtvReceiver(Context context) {
        LogUtils.d(TAG, "registerLeiKtvReceiver");
        // 初始化广播接收器
        LeiKtvReceiver receiver = new LeiKtvReceiver();
        // 创建 IntentFilter，监听 PACKAGE_REMOVED 广播
        IntentFilter filter = new IntentFilter("com.thunder.carplay.broadcast.NOTIFY_FEATURE");
        // 注册广播接收器
        context.registerReceiver(receiver, filter);
    }


    private void initSdk() {
        LogUtils.i(TAG, "initSdk");
        String vinCode = DeviceHolder.INS().getDevices().getCarServiceProp().getVinCode();
        boolean isCar = DeviceHolder.INS().getDevices().getCarServiceProp().vehicleSimulatorJudgment();
        String vehicleType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();
        DeviceContextUtils.getInstance().startWork(Utils.getApp().getApplicationContext());
        int configurationType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarModeConfig();
        if (configurationType == 3 || configurationType == 4) {
            configurationType = SpeechConfig.DEVICE_CONFIGURATION_HIGH;
        } else {
            configurationType = SpeechConfig.DEVICE_CONFIGURATION_LOW;
        }
        LogUtils.d(TAG, "device configuration is:" + configurationType);
        ClientApi clientApi = new ClientApi();
        clientApi.initToApp(vinCode, isCar, vehicleType, configurationType, new IVoAIVoiceSDKInitCallback() {
            @Override
            public void onSUCCESS() {
                LogUtils.i(TAG, "sdk init success");
                startInit();
            }

            @Override
            public void onFAILED() {
                LogUtils.i(TAG, "sdk init failed");
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                initSdk();
            }

            @Override
            public void onExit() {
                LogUtils.i(TAG, "onExit");
                UIMgr.INSTANCE.exitWeakBusiness();
                VoiceStateRecordManager.getInstance().updateWakeStatus(VoiceStatus.status.VOICE_STATE_EXIT, "");
                delayToAsleepState();
                DeviceHolder.INS().getDevices().getTts().releaseAudioFocusByUsage();
            }

            @Override
            public void onVoiceStatus(String detail, Map<String, Object> msgMap) {
                if (StringUtils.equals(detail, VOICE_EVENT_WAKEUP))
                    DeviceHolder.INS().getDevices().getVoiceCarSignal().showForbiddenToast(1, msgMap.containsKey("location") ? (int) msgMap.get("location") : 0);
                else if (StringUtils.equals(detail, VOICE_EVENT_RECOGNIZE_BEGIN)) {
                    LogUtils.d(TAG, "===============埋点，vad开始..==============");
                    DeviceHolder.INS().getDevices().getBuriedPointManager().saveVadStartTime(System.currentTimeMillis(), msgMap.get("location") + "");
                    if (!LifeState.SPEAKING.equals(DeviceHolder.INS().getDevices().getDialogue().getSpeechState())) {
                        DeviceHolder.INS().getDevices().getDialogue().onVoiceStateCallback(LifeState.INPUTTING);
                    }
                } else if (StringUtils.equals(detail, VOICE_EVENT_RECOGNIZE_END)) {
                    LogUtils.d(TAG, "===============埋点，vad结束..==============");
                    DeviceHolder.INS().getDevices().getBuriedPointManager().saveVadEndTime(System.currentTimeMillis(), msgMap.get("location") + "");
                    if (!LifeState.SPEAKING.equals(DeviceHolder.INS().getDevices().getDialogue().getSpeechState())) {
                        DeviceHolder.INS().getDevices().getDialogue().onVoiceStateCallback(LifeState.RECOGNIZING);
                    }
                }
            }


        });
    }

    private void delayToAsleepState() {
        int delay = DeviceHolder.INS().getDevices().getVoiceSettings().isEnableSwitch(DhSwitch.ContinuousDialogue) ? 0 : 350;
        ThreadUtils.getMainHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int voiceStatus = VoiceStateRecordManager.getInstance().getVoiceState();
                if (VoiceStatus.status.VOICE_STATE_EXIT == voiceStatus) {
                    DeviceHolder.INS().getDevices().getDialogue().onVoiceStateCallback(LifeState.ASLEEP);
                }
            }
        }, delay);
    }

    //语音初始化设置开关状态
    private void setVoiceCreateConfig() {
        //20250113 多音区自由对话与全时合并，多音区自由对话无需独立的开关
        SettingsInterface settingsManager = DeviceHolder.INS().getDevices().getVoiceSettings();
        boolean enableWakeup = settingsManager.isEnableSwitch(DhSwitch.MainWakeup);
        boolean enableAllTime = settingsManager.isEnableSwitch(DhSwitch.FreeWakeup);
        boolean enableNatureWakeUp = settingsManager.isEnableSwitch(DhSwitch.Oneshot);
        //boolean enableMultiVPA = settingsManager.isEnableSwitch(DhSwitch.MultiZoneDialogue);
        boolean enableContinueSession = settingsManager.isEnableSwitch(DhSwitch.ContinuousDialogue);
        boolean enablePrivacyMode = DeviceHolder.INS().getDevices().getCarService().getOperatorDispatcher()
                .getOperatorByDomain("SysSetting").getIntProp(CommonSignal.COMMON_PRIVACY_PROTECTION) == 1;
        int llMode = settingsManager.getAiModelPreference();
        VoiceImpl.getInstance().setLlmModel(llMode == 0 ? FeatureConfig.LLM_MODE_VOYAH : FeatureConfig.LLM_MODE_DEEPSEEK);
        int regionConfig = settingsManager.getUserVoiceMicMask();
        String asr = settingsManager.getCurrentDialect().asr;
        VoiceImpl.getInstance().setVoiceCreateConfig(enableWakeup, enableContinueSession, enableAllTime, enableNatureWakeUp, enableAllTime, enablePrivacyMode, regionConfig, asr);
        LogUtils.i(TAG, "setVoiceCreateConfig enableWakeup is " + enableWakeup + " ,enableContinueSession is " + enableContinueSession +
                " ,enableAllTime is " + enableAllTime + " ,enableNatureWakeUp is " + enableNatureWakeUp + " ,enableMultiVPA is " + enableAllTime + " ,enablePrivacyMode is " + enablePrivacyMode + ", regionConfig is" + regionConfig);
    }

    private void setDumpAudioIfNeed() {
        SettingsInterface settingsManager = DeviceHolder.INS().getDevices().getVoiceSettings();
        long elapsedRealtime = SystemClock.elapsedRealtime();
        int THRESHOLD = 10 * 60 * 1000;
        LogUtils.d(TAG, "elapsedRealtime:" + elapsedRealtime + ", startup threshold：" + THRESHOLD);
        settingsManager.enableFadeWakeupDump(settingsManager.isFadeWakeupDumpEnabled());
        if (elapsedRealtime < THRESHOLD) {
            settingsManager.enableDebugAudioDump(false);
        } else {
            boolean isAudioDumpEnable = settingsManager.isDebugAudioDumpEnabled();
            settingsManager.enableDebugAudioDump(isAudioDumpEnable);
            if (isAudioDumpEnable) {
                ThreadUtils.runOnUiThread(() -> RecordTipFloatWindow.get().show());
            }
        }
    }
}
