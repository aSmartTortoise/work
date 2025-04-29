package com.voyah.ai.device.voyah.h37.utils;

import android.util.Log;

import com.blankj.utilcode.util.Utils;
import com.voyah.ai.basecar.carservice.CarServicePropUtils;

import java.util.HashSet;
import java.util.Set;

import mega.car.CarPropertyManager;
import mega.car.MegaCarPropHelper;
import mega.car.SourceID;
import mega.car.hardware.CarPropertyValue;


public class CarPropUtils {


    private static final String TAG = "CarPropUtils";
    private static CarPropUtils mInstance;
    private MegaCarPropHelper carPropHelper;
//    private static final boolean isVirtualDevice = BuildConfig.VIRTUAL_DEVICE;



    public static CarPropUtils getInstance() {
        if (null == mInstance) {
            synchronized (CarPropUtils.class) {
                if (null == mInstance) {
                    mInstance = new CarPropUtils();
                }
            }
        }
        return mInstance;
    }

    private CarPropUtils() {
        if (CarServicePropUtils.vehicleSimulatorJudgment()) {
            Log.i(TAG, "carPropHelper进行初始化了");
            carPropHelper = MegaCarPropHelper.getInstance(Utils.getApp(), null);
        } else {
            Log.i(TAG, "carPropHelper没有进行初始化");
        }
    }

    /**
     * set carserver prop is the int value
     *
     * @param propid
     * @param value
     */
    public void setIntProp(int propid, int value) {
        this.setIntProp(propid, 0, value);
    }

    /**
     * set carserver prop is the int value
     *
     * @param propid carserver signal id
     * @param area   carserver area id
     * @param value carser
     */
    public void setIntProp(int propid, int area, int value) {
        Log.i(TAG, "setIntProp  propid:" + propid + "   area:" + area + "   value:" + value);
        if (carPropHelper != null) {
            CarPropertyValue<Integer> propertyValue = new CarPropertyValue<>(propid, area, value);
            propertyValue.setExtension(new SourceID(SourceID.SOURCEID_VOICE));
            setRawProp(propertyValue);
        }
    }

    /**
     * get carserver prop the return int value
     *
     * @param propid
     * @return
     */
    public int getIntProp(int propid) {
        return this.getIntProp(propid, 0);
    }

    /**
     * get carserver prop the return int value
     *
     * @param propId
     * @param area
     * @return
     */
    public int getIntProp(int propId, int area) {
        int intProp = -1;
        if (carPropHelper != null) {
            intProp = carPropHelper.getIntProp(propId, area);
        }
        Log.i(TAG, "getIntProp  propid:" + propId + "   area:" + area + ",intProp=" + intProp);
        return intProp;
    }

    /**
     * set carserver prop is the float value
     *
     * @param propid
     * @param status
     */
    public void setFloatProp(int propid, float status) {
        Log.i(TAG, "setIntProp  propid:" + propid + "   area:" + 0 + "   status:" + status);
        this.setFloatProp(propid, 0, status);
    }

    /**
     * set carserver prop is the float value
     *
     * @param propid
     * @param area
     * @param status
     */
    public void setFloatProp(int propid, int area, float status) {
        Log.i(TAG, "setIntProp  propid:" + propid + "   area:" + area + "   status:" + status);
//        Map<String,String> map = new HashMap<>();
//        map.put("propid",propid+"");
//        map.put("area",area+"");
//        map.put("status",status+"");
//        LogUtils.i(TAG,map);
        if (carPropHelper != null) {
            CarPropertyValue<Float> value = new CarPropertyValue<>(propid, area, status);
            value.setExtension(new SourceID(SourceID.SOURCEID_VOICE));
            setRawProp(value);
        }

    }

    /**
     * get carserver prop the return float value
     *
     * @param propid
     * @return
     */
    public float getFloatProp(int propid) {
        return this.getFloatProp(propid, 0);
    }

    /**
     * get carserver prop the return float value
     *
     * @param propid
     * @param area
     * @return
     */
    public float getFloatProp(int propid, int area) {
        Float value = -1.0F;

        if (carPropHelper != null) {
            value = carPropHelper.getFloatProp(propid, area);
        }
        Log.i(TAG, "getFloatProp  propid:" + propid + "   area:" + area + ",value=" + value);
        return value;
    }

    /**
     * set carserver prop is the raw value
     *
     * @param propid
     * @param area
     * @param status
     * @param sourceId
     */
    public void setRawProp(int propid, int area, int status, int sourceId) {
        Log.i(TAG, "setRawProp  propid:" + propid + "  area:" + area + "   status:" + status + " " +
                "  sourceId:" + sourceId);
        if (carPropHelper != null) {
            CarPropertyValue<Integer> value = new CarPropertyValue<>(propid, area, status);
            value.setExtension(sourceId);
            setRawProp(value);
        }
    }

    public <T> void setRawProp(CarPropertyValue<T> carPropertyValue) {
        Log.i(TAG, "setRawProp"
                + "   propId:" + carPropertyValue.getPropertyId()
                + "   area:" + carPropertyValue.getAreaId()
                + "   status:" + carPropertyValue.getStatus()
                + "   value:" + carPropertyValue.getValue());
        //真车，走car_service 接口
        if (carPropHelper != null) {
            carPropHelper.setRawProp(carPropertyValue);
        }
    }

    /**
     * get carserver prop the return raw value
     *
     * @param propid
     * @return
     */
    public CarPropertyValue getPropertyRaw(int propid) {
        return this.getPropertyRaw(propid, 0);
    }

    /**
     * get carserver prop the return raw value
     *
     * @param propid
     * @param areaId
     * @return
     */
    public CarPropertyValue getPropertyRaw(int propid, int areaId) {
        CarPropertyValue propertyRaw = null;
        if (carPropHelper != null) {
            Log.i(TAG,"carPropHelper不为null");
            propertyRaw = carPropHelper.getPropertyRaw(propid, areaId);
        }
        if (855638018 != propid) {
            Log.i(TAG, "getPropertyRaw  propid:" + propid + "   areaId:" + areaId + ",propertyRaw=" + propertyRaw);
        }
        return propertyRaw;
    }

    public void registerCallback(CarPropertyManager.CarPropertyEventCallback cb, Set<Integer> ids) {
        Log.i(TAG, "registerCallback  cb:" + cb.getClass());
        if (carPropHelper != null) {
            carPropHelper.registerCallback(cb, ids);
        }
    }

    public void unregisterCallback(CarPropertyManager.CarPropertyEventCallback changeCallback, HashSet<Integer> integerHashSet) {
        if (carPropHelper != null) {
            carPropHelper.unregisterCallback(changeCallback, integerHashSet);
        }
    }
}
