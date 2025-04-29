package com.voyah.api;

import com.voice.sdk.IVoAIVoiceSDKInitCallback;
import com.voice.sdk.VoiceEnvPlugin;
import com.voice.sdk.VoiceImpl;
import com.voice.sdk.context.ParamsGather;
import com.voice.sdk.device.carservice.vcar.IVirtualDevice;
import com.voice.sdk.util.LogUtils;
import com.voice.sdk.vcar.LogCallBack;
import com.voice.sdk.vcar.TTSCallBack;
import com.voice.sdk.vcar.VirtualDeviceManager;
import com.voyah.ai.logic.agent.flowchart.FlowChartAgent;
import com.voyah.ai.logic.agent.generic.AsrTextRecStreamAgent;
import com.voyah.ai.logic.agent.generic.DefaultAgent;
import com.voyah.ai.logic.agent.generic.IgnoreAgent;
import com.voyah.ai.logic.agent.generic.InstanceTtsAgent;
import com.voyah.ai.logic.agent.voice.H37RealDevice;
import com.voyah.ai.logic.dc.manager.DeviceManager;
import com.voyah.ai.voice.platform.agent.api.VehicleControlToolApi;
import com.voyah.ai.voice.platform.agent.api.functionMatrix.FunctionDeviceMappingManager;
import com.voyah.ai.voice.platform.agent.api.tts.TTSIDConvertHelper;
import com.voyah.ai.voice.platform.soa.api.plugin.IEnvPlugin;
import com.voyah.ai.voice.sdk.api.task.AgentX;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClientApi {

    private static final String TAG = "ClientApi";
    public static String graphPath = "";

    //初始化接口，
    public void initForService(String path, IEnvPlugin iEnvPlugin, String vin, String carType, int configurationType, IVoAIVoiceSDKInitCallback iVoAIVoiceSDKInitCallback) {
        LogUtils.d(TAG, "传进SDK的path是：" + path);
        initVCar();
        ParamsGather.vin = vin;
        TTSIDConvertHelper.getInstance().init(path);
        graphPath = path + "/flow_chart/";
        VehicleControlToolApi.getInstance().init(path + "/flow_chart/", new H37RealDevice(), null);
        VoiceImpl.getInstance().setVoiceEnvPlugin(iEnvPlugin == null ? new VoiceEnvPlugin() : iEnvPlugin);
        DeviceManager.getInstance().init();
        FunctionDeviceMappingManager.getInstance().init(path); //依赖上面先初始化
        initToApp(vin, false, carType, configurationType, new IVoAIVoiceSDKInitCallback() {
            @Override
            public void onSUCCESS() {
                if (iVoAIVoiceSDKInitCallback != null) {
                    iVoAIVoiceSDKInitCallback.onSUCCESS();
                }
                List<AgentX> list = new ArrayList<>();
                list.add(new FlowChartAgent());
                list.add(new InstanceTtsAgent());
                list.add(new IgnoreAgent());
                list.add(new DefaultAgent());
                list.add(new AsrTextRecStreamAgent());
                VoiceImpl.getInstance().addAgents(list);

            }

            @Override
            public void onFAILED() {
                if (iVoAIVoiceSDKInitCallback != null) {
                    iVoAIVoiceSDKInitCallback.onFAILED();
                }
            }

            @Override
            public void onExit() {
                if (iVoAIVoiceSDKInitCallback != null) {
                    iVoAIVoiceSDKInitCallback.onExit();
                }
            }

            @Override
            public void onVoiceStatus(String detail, Map<String, Object> msgMap) {
                if (iVoAIVoiceSDKInitCallback != null) {
                    iVoAIVoiceSDKInitCallback.onVoiceStatus(detail, msgMap);
                }
            }
        });
    }

    /**
     * @param vinCode
     * @param isCar
     * @param vehicleType
     * @param configurationType
     * @param initCallback
     */
    public void initToApp(String vinCode, boolean isCar, String vehicleType, int configurationType, IVoAIVoiceSDKInitCallback initCallback) {
        VoiceImpl.getInstance().init(vinCode, isCar, vehicleType, configurationType, initCallback);
    }

    //销毁接口
    public void destory() {
        VoiceImpl.getInstance().exDialog();
    }

    //发送数据接口
    public void sendMessage(String queryText, int location) {
        System.out.println("发送的asr数据是：" + queryText + "声源位置是：" + location);
        VoiceImpl.getInstance().queryTest(queryText, location, true);
    }

    //唤醒
    public void wakeUp(int location) {
        VoiceImpl.getInstance().wakeUp(location, 0);
    }

    public void registerCallBack(TTSCallBack ttsCallBack) {
        VirtualDeviceManager.getInstance().setTtsCallBack(ttsCallBack);
    }

    public void registerLogCallBack(LogCallBack logCallBack) {
        VirtualDeviceManager.getInstance().setLogCallBack(logCallBack);
    }

    public void registerVirtualDevice(IVirtualDevice iVirtualDevice) {
        LogUtils.d(TAG, "注册虚拟车：" + (((iVirtualDevice == null) ? "虚拟车注册进来是null的" : "虚拟车不是null的")));
        VirtualDeviceManager.getInstance().setVirtualDevice(iVirtualDevice);
    }

    /**
     * 初始化虚拟车
     * VirtualCar 在virtual-car 模块中，由buildClientAar 控制是否打包
     */
    public void initVCar() {
        try {
            Class<?> realCarClass = Class.forName("com.voyah.ai.device.VirtualCar");
            Method initMethod = realCarClass.getMethod("init");
            initMethod.invoke(null);
        } catch (Exception e) {
            LogUtils.e(TAG, "initVCar error: " + e.getMessage());
        }
    }

}
