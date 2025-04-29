package com.voyah.ai.logic.util;

import java.lang.reflect.Method;
import java.util.HashMap;

public class ReflectUtils {
    public static Object executeMethod(Object obj,String methodName,HashMap<String, Object> map) {
//        devices = new AirControlProxy();
        Class clazz = obj.getClass();
        Method method = null;
        Object result = null;
        try {
            method = clazz.getDeclaredMethod(methodName, HashMap.class);
            method.setAccessible(true);
            // 调用 myMethod 方法
            result = method.invoke(obj,map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


}
