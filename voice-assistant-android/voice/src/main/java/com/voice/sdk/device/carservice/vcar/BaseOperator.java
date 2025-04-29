package com.voice.sdk.device.carservice.vcar;

import com.voice.sdk.device.carservice.signal.VCarSeatSignal;
import com.voyah.ai.common.helper.LogTool;
import com.voyah.ai.common.helper.ParamsKey;
import com.voyah.ai.common.utils.LogUtils;

import java.util.HashMap;

/**
 * 这块是各个模块的Operator的基类，不要做任何修改。修改也是所有模块的通用处理
 */
public abstract class BaseOperator implements IPropertyOperator {
    private static final String TAG = "BaseOperator";

    private IVirtualDevice mIVirtualDevice;

    public void setIVirtualDevice(IVirtualDevice IVirtualDevice){
        mIVirtualDevice = IVirtualDevice;
    }

    protected abstract int getBaseIntProp(String key, int area);

    protected abstract void setBaseIntProp(String key, int area, int value);

    protected abstract float getBaseFloatProp(String key, int area);

    protected abstract void setBaseFloatProp(String key, int area, float value);

    public String getBaseStringProp(String key, int area){
        String virtualKey = getVirtualKey(key, area);
        return (String) getValue(virtualKey);
    }

    public void setBaseStringProp(String key, int area, String value){
        String virtualKey = getVirtualKey(key, area);
        setValue(virtualKey, value);
    }

    public abstract boolean getBaseBooleanProp(String key, int area);

    public abstract void setBaseBooleanProp(String key, int area, boolean value);

    @Override
    public final int getIntProp(String key, int area) {
        return getBaseIntProp(key, area);
    }


    @Override
    public final void setIntProp(String key, int area, int value) {
        setBaseIntProp(key,area,value);
        printLog(getVirtualKey(key, area), value);
    }

    @Override
    public final float getFloatProp(String key, int area) {
        return getBaseFloatProp(key,area);
    }

    @Override
    public final void setFloatProp(String key, int area, float value) {
        setBaseFloatProp(key,area,value);
        printLog(getVirtualKey(key, area), value);
    }

    @Override
    public final String getStringProp(String key, int area) {
        return getBaseStringProp(key,area);
    }

    @Override
    public final void setStringProp(String key, int area, String value) {
        setBaseStringProp(key, area, value);
        printLog(getVirtualKey(key, area), value);
    }

    @Override
    public final boolean getBooleanProp(String key, int area) {
        return getBaseBooleanProp(key, area);
    }


    public void setBooleanProp(String key, int area, boolean value) {
        setBaseBooleanProp(key, area, value);
        printLog(getVirtualKey(key, area), value);
    }

    /**
     * 需要多位置的信号是少数，因此只对这些这些做拼接
     * @param key 原始key
     * @see VCarSeatSignal
     * @param area 多位置， 0，1，2，3
     */
    protected String getVirtualKey(String key, int area) {
        if (area != 0 && area != IPropertyOperator.AREA_NONE) {
            return key + "_" + area;
        } else {
            return key;
        }
    }

    protected Object getValue(String VKey) {
        Object value = mIVirtualDevice.getData(VKey);
        LogUtils.d(TAG, "VirtualCar get value with key:" + VKey + " is " + value);
        return value;
    }
    protected void setValue(String VKey, Object value) {
        LogUtils.d(TAG, "VirtualCar set value with key:" + VKey + " & value :" + value);
        mIVirtualDevice.setData(VKey, value);
    }
    public void printLog(String key,Object value){
        HashMap<String, String> hashMap = new HashMap<>();
        //json拼接工具类
        LogTool.JsonStructure jsonStructure = new LogTool.JsonStructure();
        //key = module_type  测试用来识别当前log是哪个阶段的log的key。
        //value = excute     测试用来识别当前log是哪个阶段的log的value。
        jsonStructure.addJsonParam(ParamsKey.TestKey.TEST_KEY, ParamsKey.TestKey.VALUE.EXCUTE);
        //方法名method_name:airSwitch
        jsonStructure.addJsonParam(key, value + "");
        jsonStructure.addJsonParam("graph_operation", "set");
        //放入map里。
        hashMap.put(ParamsKey.ModuleKey.TEST_MODULE, jsonStructure.toString());
        //log打印，传入tag和map
        LogUtils.i(TAG, hashMap);
    }

    public String isSupport(String key){
        return "-";
    }
}
