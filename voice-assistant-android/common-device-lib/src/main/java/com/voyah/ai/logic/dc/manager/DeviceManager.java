package com.voyah.ai.logic.dc.manager;


import android.content.Context;

import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.dc.Devices;
import com.voice.sdk.device.carservice.dc.context.DCContext;
import com.voice.sdk.device.carservice.vcar.IDeviceRegister;
import com.voice.sdk.device.carservice.vcar.IOperatorDispatcher;
import com.voice.sdk.device.carservice.vcar.IPropertyOperator;
import com.voice.sdk.util.LogUtils;
import com.voyah.ai.voice.platform.agent.api.functionMatrix.FunctionDeviceMappingManager;

import java.util.HashMap;
import java.util.Map;

public class DeviceManager {
    private static final String TAG = "DeviceManager";
    private DevicesIntentManager devicesIntentManager;

    private DeviceManager() {

    }

    public static DeviceManager getInstance() {
        return Holder.holder;
    }

    private static class Holder {
        private static final DeviceManager holder = new DeviceManager();
    }

    HashMap<String, Object> proxyMap = new HashMap<>();

    public void init() {
        devicesIntentManager = new DevicesIntentManager();
        Map<String, IPropertyOperator>  iOperatorDispatcherMap= DeviceHolder.INS().getDevices().getCarService().getOperatorDispatcher().getPropertyOperatorMap();

        LogUtils.d(TAG, "准备注册设备能力");
        FunctionDeviceMappingManager.getInstance().registerCallBack(new FunctionDeviceMappingManager.FuncGuidanceCallBack() {
            @Override
            public void register() {
                try {
                    for (String key : iOperatorDispatcherMap.keySet()) {
                        IPropertyOperator devices = iOperatorDispatcherMap.get(key);
                        if (devices instanceof IDeviceRegister) {
                            LogUtils.d(TAG, "当前进行注册的设备为：" + key);
                            try {
                                ((IDeviceRegister) devices).registerDevice();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    //根据设备能力设置矩阵能力映射表。
                    FunctionDeviceMappingManager functionDeviceMappingManager = FunctionDeviceMappingManager.getInstance();
                    functionDeviceMappingManager.refreshFunctionDevicesMapping();
                    //可生成0101的能力矩阵映射表。
                    functionDeviceMappingManager.generateFuncMatrix();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    /**
     * 通过intent获取对应的设备方法对象。
     *
     * @return
     */
    public Object getProxy(Map<String, Object> map) {
        String domain = (String) map.get(DCContext.INTENT);
        return devicesIntentManager.getDevices(domain);
    }

    public DevicesIntentManager getDevicesIntentManager() {
        return devicesIntentManager;
    }
}
