package com.voyah.ai.virtual.dc;

import com.voice.sdk.device.carservice.constants.ICarSetting;
import com.voice.sdk.device.carservice.constants.ICommon;
import com.voice.sdk.device.carservice.signal.CarSettingSignal;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.carservice.vcar.IDeviceRegister;
import com.voice.sdk.device.carservice.vcar.IPropertyOperator;
import com.voice.sdk.device.carservice.vcar.IVirtualDevice;
import com.voice.sdk.vcar.LogCallBack;
import com.voice.sdk.vcar.VirtualDeviceManager;
import com.voyah.ai.common.helper.LogTool;
import com.voyah.ai.common.helper.ParamsKey;
import com.voyah.ai.common.utils.BiDirectionalMap;
import com.voyah.ai.common.utils.LogUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Date 2024/7/17 14:23
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class BaseVirtualPropertyOperator implements IPropertyOperator , IDeviceRegister {
    private static final String TAG = "BaseVirtualPropertyOper";
    protected IVirtualDevice iVirtualDevice = VirtualDeviceManager.getInstance().getVirtualDevice();

    private BiDirectionalMap<Integer, String> gearMap;
    private BiDirectionalMap<Integer, String> drvModeMap;

    public BaseVirtualPropertyOperator() {
        initGearMap();
        initDrivingModeMap();
    }

    private void initGearMap() {
        gearMap = new BiDirectionalMap<>();
        gearMap.put(0 , "P");
        gearMap.put(1, "R");
        gearMap.put(2, "N");
        gearMap.put(3, "D");
        gearMap.put(4, "S");
    }

    private void initDrivingModeMap() {
        drvModeMap = new BiDirectionalMap<>();
        drvModeMap.put(ICarSetting.DrivingMode.COMFORTABLE, "comfortable");
        drvModeMap.put(ICarSetting.DrivingMode.ECONOMY, "economy");
        drvModeMap.put(ICarSetting.DrivingMode.SPORT, "high_energy");
        drvModeMap.put(ICarSetting.DrivingMode.PICNIC, "picnic");
        drvModeMap.put(ICarSetting.DrivingMode.SNOW, "snowfield");
        drvModeMap.put(ICarSetting.DrivingMode.SUPER_POWER, "super_power_saving");
        drvModeMap.put(ICarSetting.DrivingMode.CUSTOM, "custom");
    }

    public int getBaseIntProp(String key, int area){
        switch (key) {
            case CarSettingSignal.CARSET_DRIVING_MODE:
                String mode = (String) getValue(key);
                return drvModeMap.getReverse(mode.toLowerCase());
            case CommonSignal.COMMON_GEAR_INFO:
                String info = (String) getValue(key);
                return gearMap.getReverse(info.toLowerCase());
            case CommonSignal.COMMON_PRIVACY_PROTECTION:
                boolean value = (Boolean) getValue(key);
                return value ? ICommon.Switch.ON : ICommon.Switch.OFF;
            default:
                String virtualKey = getVirtualKey(key, area);
                Object dbValue = getValue(virtualKey);
                if (dbValue == null) {
                    return 0;
                } else {
                    return (Integer) dbValue;
                }
        }
    };

    public void setBaseIntProp(String key, int area, int value){
        switch (key) {
            case CarSettingSignal.CARSET_DRIVING_MODE:
                String mode = drvModeMap.getForward(value);
                setValue(key, mode);
                break;
            case CommonSignal.COMMON_GEAR_INFO:
                String info = gearMap.getForward(value);
                setValue(key, info);
                break;
            case CommonSignal.COMMON_PRIVACY_PROTECTION:
                setValue(key, ICommon.Switch.ON == value);
                break;
            default:
                String virtualKey = getVirtualKey(key, area);
                setValue(virtualKey, value);
        }
    };
    public  float getBaseFloatProp(String key, int area){
        String virtualKey = getVirtualKey(key, area);
        return (Float)  getValue(virtualKey);
    };

    public void setBaseFloatProp(String key, int area, float value){
        String virtualKey = getVirtualKey(key, area);
         setValue(virtualKey, value);
    };

    public String getBaseStringProp(String key, int area){
        String virtualKey = getVirtualKey(key, area);
        return (String)  getValue(virtualKey);
    };

    public void setBaseStringProp(String key, int area, String value){
        String virtualKey = getVirtualKey(key, area);
        setValue(virtualKey, value);
    };

    public boolean getBaseBooleanProp(String key, int area){
        String virtualKey = getVirtualKey(key, area);
        return (Boolean)  getValue(virtualKey);
    };

    public void setBaseBooleanProp(String key, int area, boolean value){
        String virtualKey = getVirtualKey(key, area);
        setValue(virtualKey, value);
    };

    @Override
    public final int getIntProp(String key, int area) {
        return getBaseIntProp(key, area);
    }


    @Override
    public final void setIntProp(String key, int area, int value) {
        setBaseIntProp(key,area,value);
    }

    @Override
    public final float getFloatProp(String key, int area) {
        return getBaseFloatProp(key,area);
    }

    @Override
    public final void setFloatProp(String key, int area, float value) {
        setBaseFloatProp(key,area,value);
    }

    @Override
    public final String getStringProp(String key, int area) {
        return getBaseStringProp(key,area);
    }

    @Override
    public final void setStringProp(String key, int area, String value) {
        setBaseStringProp(key, area, value);
    }

    @Override
    public final boolean getBooleanProp(String key, int area) {
        return getBaseBooleanProp(key, area);
    }


    public void setBooleanProp(String key, int area, boolean value) {
        setBaseBooleanProp(key, area, value);
    }

    @Override
    public String isSupport(String key) {
        return null;
    }

    /**
     * 需要多位置的信号是少数，因此只对这些这些做拼接
     * @param key 原始key
     * @param area 多位置， 0，1，2，3
     * @return
     */
    String getVirtualKey(String key, int area) {
        if (area != 0 && area != IPropertyOperator.AREA_NONE) {
            return key + "_" + area;
        } else {
            return key;
        }
    }
    public void printLog(String key,Object value){
        HashMap<String, String> hashMap = new HashMap<>();
        //json拼接工具类
        LogTool.JsonStructure jsonStructure = new LogTool.JsonStructure();
        //key = module_type  测试用来识别当前log是哪个阶段的log的key。
        //value = excute     测试用来识别当前log是哪个阶段的log的value。
        jsonStructure.addJsonParam(ParamsKey.TestKey.TEST_KEY, ParamsKey.TestKey.VALUE.EXCUTE);
        //方法名method_name:airSwitch
        jsonStructure.addJsonParam(key, value);
        jsonStructure.addJsonParam("graph_operation", "set");
        //放入map里。
        hashMap.put(ParamsKey.ModuleKey.TEST_MODULE, jsonStructure.toString());
        //log打印，传入tag和map
        LogTool logTool = new LogTool(TAG);
        for (Map.Entry<String, String> entry : hashMap.entrySet()) {
            logTool.add(entry.getKey(), entry.getValue());
        }
        //自动化测试老师关注
        LogCallBack callBack = VirtualDeviceManager.getInstance().getLogCallBack();
        if (callBack != null) {
            callBack.log(logTool.toString());
        }
    }

    protected Object getValue(String VKey) {
        Object value = iVirtualDevice.getData(VKey);
        LogUtils.d(TAG, "VirtualCar get value with key:" + VKey + " is " + value);
        return value;
    }
    protected void setValue(String VKey, Object value) {
        LogUtils.d(TAG, "VirtualCar set value with key:" + VKey + " & value :" + value);
        printLog(VKey, value);
        iVirtualDevice.setData(VKey, value);
    }

    @Override
    public void registerDevice() {

    }
}
