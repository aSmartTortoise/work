package com.voyah.ai.voice.voice;


import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.ui.ScreenStateHelper;
import com.voice.sdk.tts.TtsServiceInspection;
import com.voyah.ai.common.utils.LogUtils;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author:lcy 优先与语音初始化项
 * @data:2024/6/25
 **/
public class VrInfrastructureManager {
    private static final String TAG = VrInfrastructureManager.class.getSimpleName();

    private static final ExecutorService executorService = Executors.newCachedThreadPool();


    private static class InnerHolder {
        private static final VrInfrastructureManager instance = new VrInfrastructureManager();
    }

    public static VrInfrastructureManager getInstance() {
        return InnerHolder.instance;
    }

    public void init() {
        startInit();
    }

    private void startInit() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                String vehicleType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();
                LogUtils.d(TAG, "VrInfrastructureManager startInit start vehicleType:" + vehicleType);
                if (DeviceHolder.INS().getDevices().getCarServiceProp().vehicleSimulatorJudgment()) {
                    DeviceHolder.INS().getDevices().getAudioRecorder().init(vehicleType);
                }
                TtsServiceInspection.getInstance().startTtsService();
                DeviceHolder.INS().getDevices().getTts().initialize();
//                MultiModeFunction.getInstance().init();
                ScreenStateHelper.INSTANCE.bindVoiceWithScreen();
                LogUtils.d(TAG, "VrInfrastructureManager startInit finish");
            }
        });
    }

    public void onDestroy() {
        DeviceHolder.INS().getDevices().getTts().onDestroy();
    }
}
