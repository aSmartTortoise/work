package com.voyah.ai.voice.voice;

import android.util.Log2;

import com.voice.sdk.device.carservice.dc.context.DCContext;
import com.voyah.ai.voice.platform.agent.api.flowchart.device.IDevice;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class H37RealDevice implements IDevice {
    private static final String TAG = "H37RealDevice";
    public H37RealDevice(){

    }

    @Override
    public Object get(Object object1, String methodName, Object value) {
        //todo 后续处理。当前会发现，两波反射调用的是不通的接口实例对象。
//        Devices device = (Devices) DeviceManager.getInstance().getProxy((Map<String, Object>) value);
        //数据库获取数据
//        String virtualValue = methodName.substring(3);
//        return mContentProviderHelper.getData(virtualValue);
        //反射执行方法。
        Class clazz = object1.getClass();
        Method method = null;
        Object result = null;
        try {
            methodName = methodName.trim();
            Log2.i(TAG,"当前的类名是：" + clazz.getName() + " 反射的方法名是：" + methodName);
            method = clazz.getMethod(methodName, HashMap.class);
            // 调用 myMethod 方法
            result = method.invoke(object1, value);
        } catch (Exception e) {
            e.printStackTrace();
            Log2.e(TAG,e.getCause().toString());
        }
        return result;
    }

    @Override
    public void set(Object object1, String methodName, Object value) {
//        if(value instanceof Map){
//            //是map，则需要获取一下数据。
//            HashMap<String,Object> hashMap = (HashMap<String, Object>) value;
//            String resValue = null;
//            String virtualValue = methodName.substring(3);
//            if(keyContextInMap(hashMap,"switch_type")){
//                resValue = (String) getValueInContext(hashMap,"switch_type");
//                mContentProviderHelper.setData(virtualValue,resValue.equals("open"));
//            }
//            //数据库获取数据
//
//
//        }else{
//            //不是map则直接把数据设置进去。
//            //数据库获取数据
//            String virtualValue = methodName.substring(3);
//            mContentProviderHelper.setData(virtualValue,value);
//        }

//        Devices device = (Devices) DeviceManager.getInstance().getProxy((Map<String, Object>) value);
        //反射执行方法。
        Class clazz = object1.getClass();
        Method method = null;
        try {
            methodName = methodName.trim();
            Log2.i(TAG,"当前的类名是：" + clazz.getName() + " 反射的方法名是：" + methodName);
            method = clazz.getMethod(methodName, HashMap.class);
            // 调用 myMethod 方法
            method.invoke(object1, value);
        } catch (Exception e) {
            e.printStackTrace();
            Log2.e(TAG,e.getCause().toString());
        }
    }


    /**
     * 获取图中方法的key对应的值，或者ds里数据对应的值。
     * 图的方法参数放到了，上下文中图的上下文里。
     * ds数据放在了上下文里。
     * 先从图的上下文里找，再从全局上下文找
     * @param map
     * @param key
     * @return
     */
    public Object getValueInContext(Map<String,Object> map, String key){
        HashMap<String,Object> graphMap = (HashMap<String, Object>) map.get(DCContext.MAP_KEY_GRAPH_CONTEXT);
        if(graphMap.containsKey(key)){
            return graphMap.get(key);
        }
        return map.get(key);
    }

    /**
     * 判断key是否在图上下文，或者ds的上下文里。
     * 现在图的上下文里判断，再从全局上下文里判断。
     * @param map
     * @param key
     * @return
     */
    public boolean keyContextInMap(Map<String,Object> map, String key){
        HashMap<String,Object> graphMap = (HashMap<String, Object>) map.get(DCContext.MAP_KEY_GRAPH_CONTEXT);
        if(graphMap.containsKey(key)){
            return true;
        }
        return map.containsKey(key);
    }

}