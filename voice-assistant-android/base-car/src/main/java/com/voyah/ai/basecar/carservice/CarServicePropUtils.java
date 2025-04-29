package com.voyah.ai.basecar.carservice;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.mega.ecu.MegaProperties;
import com.mega.nexus.os.MegaSystemProperties;
import com.voice.sdk.device.DeviceHolder;
import com.voyah.ai.common.ParamsGather;
import com.voyah.ai.common.utils.ContextUtils;
import com.voyah.ai.common.utils.DeviceUtils;
import com.voyah.ai.common.utils.LogUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mega.car.CarPropertyManager;
import mega.car.MegaCarPropHelper;
import mega.car.SourceID;
import mega.car.config.Driving;
import mega.car.config.Ecu;
import mega.car.hardware.CarPropertyValue;

public class CarServicePropUtils {
    private static final String TAG = "CarServicePropUtils";
    public static final String H97 = "H97";
    public static final String H56 = "H56";
    public static final String H53 = "H53";
    public static final String H37A = "H37A";
    public static final String H37B = "H37B";
    public static final String H77 = "H77";
    public static final String H97c = "H97c";
    public static final String H53B = "H53B";
    public static final String H56C = "H56C";
    public static final String H56D = "H56D";

    //todo:暂时按照已知车型区分
    public static final List<String> singleScreen = Arrays.asList(H37A, H37B);
    public static final List<String> multiScreen = Arrays.asList(H56, H56C, H56D);
    private static CarServicePropUtils mInstance;
    private MegaCarPropHelper carPropHelper;
    private static Context mContext;

    private static final String defaultVin = "vin-0123456789ABC";
    private static String sharedUserId;

    private static String carType;

    public static CarServicePropUtils getInstance() {
        if (null == mInstance) {
            synchronized (CarPropUtils.class) {
                if (null == mInstance) {
                    mInstance = new CarServicePropUtils();
                    mInstance.init(ContextUtils.getAppContext());
                }
            }
        }
        return mInstance;
    }

    public void init(Context context) {
        mContext = context;
        //获取sharedUserId,用来判断代码
        if (vehicleSimulatorJudgment()) {
            LogUtils.i(TAG, "进行初始化了");
            carPropHelper = MegaCarPropHelper.getInstance(mContext, null);
        } else {
            LogUtils.i(TAG, "没有进行初始化");
        }
        initDate();
    }

    private void initDate() {
        ParamsGather.vin = getVinCode();
        ParamsGather.type = getCarType();
    }

    private CarServicePropUtils() {

    }


    /**
     * @return vin 车机vin码
     */
    public String getVinCode() {
        String vin = MegaSystemProperties.get("ro.build.voice_vin", "");
        LogUtils.i(TAG, "getVinCode ro.build.voice_vin vin:" + vin);
        if (!StringUtils.isBlank(vin)) {
            return vin;
        }
        CarPropertyValue carPropertyValue = null;
        if (carPropHelper != null) {
            carPropertyValue = carPropHelper.getPropertyRaw(Ecu.ID_VIN);
            LogUtils.i(TAG, "carPropertyValue " + carPropertyValue);
            if (carPropertyValue != null && carPropertyValue.getValue() instanceof String) {
                vin = ((String) carPropertyValue.getValue()).trim();
            }
        }
        if (StringUtils.isBlank(vin) || StringUtils.equals(defaultVin, vin)) {
            LogUtils.d(TAG, "vin is null or vin is default, load androidId");
            vin = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return vin;
    }

    /**
     * 获取车的类型
     *
     * @return
     */
    public String getCarType() {
        if (carType != null && !carType.isEmpty()) {
            return carType;
        }
        String str = System.getProperty("vehicle_model");

        int type = MegaSystemProperties.getInt(MegaProperties.CONFIG_VEHICLE_MODEL, -1);
        switch (type) {
            case 0:
                str = "H97";
                break;
            case 1:
                // 目前通过车型配置看，56D的配置字是1
                str = "H56D";
                break;
            case 2:
                str = "H53";
                break;
            case 3:
                str = "H37A";
                break;
            case 4:
                str = "H77";
                break;
            case 5:
                str = "H97c";
                break;
            case 6:
                str = "H53B";
                break;
            case 7:
                str = "H56C";
                break;
            case 8:
                str = "H97E";
                break;
            case 9:
                str = "H37B";
                break;
        }
        if (DeviceHolder.INS().getDevices().getSystem().getApp().getAppVersionName().contains("h56d")) {
            str = "H56D";
        }
        carType = StringUtils.isBlank(str) ? "H37A" : str;
        return carType;
    }

    public boolean isH37A() {
        return "H37A".equalsIgnoreCase(getCarType());
    }

    public boolean isH56C() {
        return "H56C".equalsIgnoreCase(getCarType());
    }

    public boolean isH56D() {
        return "H56D".equalsIgnoreCase(getCarType());
    }

    public boolean isH37B() {
        return "H37B".equalsIgnoreCase(getCarType());
    }

    /**
     * set carserver prop is the int value
     *
     * @param propid
     * @param status
     */
    public void setIntProp(int propid, int status) {
        this.setIntProp(propid, 0, status);
    }

    /**
     * set carserver prop is the int value
     *
     * @param propid carserver signal id
     * @param area   carserver area id
     * @param status carser
     */
    public void setIntProp(int propid, int area, int status) {
        Log.i(TAG, "setIntProp  propid:" + propid + "   area:" + area + "   status:" + status);
        if (carPropHelper != null) {
            CarPropertyValue value = new CarPropertyValue(propid, area, status);
            value.setExtension(new SourceID(SourceID.SOURCEID_VOICE));
            setRawProp(value);
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
        Log.i(TAG, "setFloatProp  propid:" + propid + "   area:" + area + "   status:" + status);
        if (carPropHelper != null) {
            CarPropertyValue value = new CarPropertyValue(propid, area, status);
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
            CarPropertyValue value = new CarPropertyValue(propid, area, status);
            value.setExtension(sourceId);
            setRawProp(value);
        }
    }

    public void setRawProp(CarPropertyValue value) {
        Log.i(TAG, "setRawProp  value=" + value);
        if (carPropHelper != null) {
            carPropHelper.setRawProp(value);
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
            propertyRaw = carPropHelper.getPropertyRaw(propid, areaId);
        }
        if (855638018 != propid) {
            Log.i(TAG, "getPropertyRaw  propid:" + propid + "   areaId:" + areaId + ",propertyRaw=" + propertyRaw);
        }
        return propertyRaw;
    }

    /**
     * 通过sharedUserId来判断当前是车机还是模拟器
     *
     * @return true是车机，false是模拟器
     */
    public static boolean vehicleSimulatorJudgment() {
        return DeviceUtils.isVoyahDevice();
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

//    public String getVehicleType() {
//        String vehicleType = System.getProperty("vehicle_model");
//        return StringUtils.isBlank(vehicleType) ? "H37A" : vehicleType;
//    }

    /**
     * 车型配置
     * 0001	装备等级-N1
     * 0010	装备等级-N2
     * 0011	装备等级-N3
     * 0100	装备等级-N4
     * 0101	N1大客户版
     * 0110	N2大客户版
     * 0111	N3大客户版
     * 1000	N1对公版
     * 1001	N2对公版对公版
     * 1010	N3对公版对公版
     * 1011	装备等级-N5
     * 1100	装备等级-G1
     * 1101	装备等级N3+
     * 1110	装备等级N4+
     * 1111	行政版
     *
     * @return
     */
    public int getCarModeConfig() {
        return MegaSystemProperties.getInt(MegaProperties.CONFIG_EQUIPMENT_LEVEL, -1); //获取当前配置字
    }

    /**
     * 获取档位
     *
     * @return 档位  0：P档, 1：R档, 2：N档, 3：D档, 9：无效
     */
    public int getDrvInfoGearPosition() {
        int pst = -1;
        try {
            if (carPropHelper != null) {
                pst = carPropHelper.getIntProp(Driving.ID_DRV_INFO_GEAR_POSITION);
            }
        } catch (Exception e) {
            Log.i(TAG, "getDrvInfoGearPosition Exception:" + e.getMessage());
        }
        return pst;
    }
}
