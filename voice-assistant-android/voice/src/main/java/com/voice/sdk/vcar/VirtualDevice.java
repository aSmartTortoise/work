package com.voice.sdk.vcar;


import android.util.Log;

import com.voyah.ai.voice.platform.agent.api.flowchart.device.IDevice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 *
 */
public class VirtualDevice implements IDevice {
    private static final String TAG = "VirtualDevice";
    Logger mLog = LoggerFactory.getLogger(VirtualDevice.class);

    public VirtualDevice() {
    }

    @Override
    public Object get(Object object1, String methodName, Object value) {
        Class clazz = object1.getClass();
        Method method = null;
        Object result = null;
        try {
            methodName = methodName.trim();
            Log.i(TAG,"当前的类名是(get)：" + clazz.getName() + " 反射的方法名是：" + methodName);
            method = clazz.getMethod(methodName, HashMap.class);
            // 调用 myMethod 方法
            result = method.invoke(object1, value);
        } catch (Exception e) {
            mLog.error("反射报错",e);
        }
        return result;
    }

    @Override
    public void set(Object object1, String methodName, Object value) {
        Class clazz = object1.getClass();
        Method method = null;
        try {
            methodName = methodName.trim();
            Log.i(TAG,"当前的类名是(set)：" + clazz.getName() + " 反射的方法名是：" + methodName);
            method = clazz.getMethod(methodName, HashMap.class);
            // 调用 myMethod 方法
            method.invoke(object1, value);
        } catch (Exception e) {
            mLog.error("反射报错",e);
        }
    }

}
