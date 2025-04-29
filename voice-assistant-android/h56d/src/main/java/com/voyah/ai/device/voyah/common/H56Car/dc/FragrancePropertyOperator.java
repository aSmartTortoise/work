package com.voyah.ai.device.voyah.common.H56Car.dc;

import android.util.Pair;

import com.blankj.utilcode.util.MapUtils;
import com.blankj.utilcode.util.Utils;
import com.example.filter_annotation.CarDevices;
import com.example.filter_annotation.CarType;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.carservice.signal.FragranceSignal;
import com.voice.sdk.device.carservice.signal.SysControlSignal;
import com.voice.sdk.device.carservice.vcar.IDeviceRegister;
import com.voyah.ai.basecar.carservice.CarPropUtils;
import com.voyah.ai.basecar.carservice.CarServicePropUtils;
import com.voyah.ai.common.utils.LogUtils;

import com.voyah.ai.voice.platform.agent.api.flowchart.library.bean.FunctionalPositionBean;
import com.voyah.ai.voice.platform.agent.api.functionMatrix.FunctionDeviceMappingManager;
import com.voyah.cockpit.airconditioner.HvacServiceImpl;

import java.util.Arrays;
import java.util.Map;


import mega.car.config.H56D;
import mega.car.config.ParamsCommon;

/**
 * 统一
 * 1是true，0是false，不符合要求的再特殊处理。
 */
@CarDevices(carType = CarType.H37_CAR)
public class FragrancePropertyOperator extends Base56Operator implements IDeviceRegister {
    private static final String TAG = "FragrancePropertyOperator";
    private CarPropUtils carPropHelper;
    private HvacServiceImpl hvacService;
    private static final String DISPLAY_ID = "_displayId";
    private static final String DISPLAY_ID2 = "_displayId2";


    @Override
    void init() {
        HvacServiceImpl.getInstance(Utils.getApp()).startService(() ->
                LogUtils.d(TAG, "AC onServiceConnected() called"));
        carPropHelper = CarPropUtils.getInstance();
        hvacService = HvacServiceImpl.getInstance(Utils.getApp());
        //香氛开关
        map.put(FragranceSignal.FRAGRANCE_SWITCH_STATE, H56D.BCM_FCM_BCM_FCM_FCMSWSET);
        //香氛等级
        map.put(FragranceSignal.FRAGRANCE_LEVEL, H56D.BCM_FCM_BCM_FCM_FRAGCONCENTRATIONRESP);
        //当前香氛是什么
        map.put(FragranceSignal.FRAGRANCE_MODE, H56D.BCM_FCM_BCM_FCM_FRAGTASTE);
        //香氛时长
//        map.put(FragranceSignal.FRAGRANCE_TIME, -1);
        //香氛槽位数据
        map.put(FragranceSignal.FRAGRANCE_SLOT_TYPE, 11);
    }

    private static final Map<Integer, String> AIR_FRAGRANCE_INTEGER_STRING_MAP = MapUtils.newLinkedHashMap(
            new Pair<>(0, "无香氛"),
            new Pair<>(1, "海静"),
            new Pair<>(2, "木影"),
            new Pair<>(3, "竹境"),
            new Pair<>(4, "风煦"),
            new Pair<>(5, "花韵"),
            new Pair<>(6, "茶悠")
    );

    private static final Map<String, Integer> AIR_FRAGRANCE_STRING_INTEGER_MAP = MapUtils.newLinkedHashMap(
            new Pair<>("无香氛", 0),
            new Pair<>("海静", 1),
            new Pair<>("木影", 2),
            new Pair<>("竹境", 3),
            new Pair<>("风煦", 4),
            new Pair<>("花韵", 5),
            new Pair<>("茶悠", 6)
    );

    private static final Map<Integer, String> AIR_FRAGRANCE_CONCENTRATION_INTEGER_STRING_MAP = MapUtils.newLinkedHashMap(
            new Pair<>(1, "low"),
            new Pair<>(2, "mid"),
            new Pair<>(3, "high")
    );

    private static final Map<String, Integer> AIR_FRAGRANCE_CONCENTRATION_STRING_INTEGER_MAP = MapUtils.newLinkedHashMap(
            new Pair<>("min", 1),
            new Pair<>("low", 1),
            new Pair<>("mid", 2),
            new Pair<>("high", 3),
            new Pair<>("max", 3)
    );

    @Override
    public int getBaseIntProp(String key, int area) {
        Integer curKey = map.get(key);
        int curValue;
        if (curKey == null) {
            switch (key) {
                default:
                    curValue = -1;
                    LogUtils.e(TAG, "空调里getBaseIntProp方法缺少对应key的处理" + key);
                    break;
            }
        } else {
            curValue = carPropHelper.getIntProp(curKey,
                    getRealArea(area));
        }

        return curValue;
    }

    @Override
    public void setBaseIntProp(String key, int area, int value) {
        int resValue = value;
        switch (key) {
            case FragranceSignal.FRAGRANCE_TIME:
                if (value == 60000) {
                    //无限
                    HvacServiceImpl.getInstance(Utils.getApp()).exec("ACTION_IS_CHANGE_FRAG_TIME" + "_displayId3");
                } else if(value == 30){
                    //30分钟

                    HvacServiceImpl.getInstance(Utils.getApp()).exec("ACTION_IS_CHANGE_FRAG_TIME" + "_displayId1");
                }else {
                    //60分钟
                    HvacServiceImpl.getInstance(Utils.getApp()).exec("ACTION_IS_CHANGE_FRAG_TIME" + "_displayId2");
                }
                return;
            default:
                break;
        }
        LogUtils.d(TAG, "resvalue:" + resValue + " value:" + value + " key:" + key);
        carPropHelper.setIntProp(map.get(key),
                getRealArea(area), resValue);
    }

    @Override
    public float getBaseFloatProp(String key, int area) {
        Integer curKey = map.get(key);
        float curValue;

        curValue = carPropHelper.getFloatProp(curKey,
                getRealArea(area));

        return curValue;
    }

    @Override
    public void setBaseFloatProp(String key, int area, float value) {

        carPropHelper.setFloatProp(map.get(key), getRealArea(area),
                value);
    }

    @Override
    public String getBaseStringProp(String key, int area) {

        int realKey = map.get(key);
        String res = "";
        switch (key) {
            case FragranceSignal.FRAGRANCE_SLOT_TYPE:
                int slot1 = carPropHelper.getIntProp(H56D.BCM_FCM_BCM_FCM_CH1_TYPE);
                int slot2 = carPropHelper.getIntProp(H56D.BCM_FCM_BCM_FCM_CH2_TYPE);
                int slot3 = carPropHelper.getIntProp(H56D.BCM_FCM_BCM_FCM_CH3_TYPE);
                LogUtils.d(TAG, "获取香氛槽位信息：1." + slot1 + " 2." + slot2 + " 3." + slot3);

                String[] frangeArray = new String[]{AIR_FRAGRANCE_INTEGER_STRING_MAP.get(slot1), AIR_FRAGRANCE_INTEGER_STRING_MAP.get(slot2), AIR_FRAGRANCE_INTEGER_STRING_MAP.get(slot3)};
                LogUtils.d(TAG, "当前香氛类型是：" + Arrays.toString(frangeArray));
                res = "[" + frangeArray[0] + "," + frangeArray[1] + "," + frangeArray[2] + "]";
                LogUtils.d(TAG, "当前香氛类型是打印结果：" + res);
                break;
            case FragranceSignal.FRAGRANCE_MODE:
                //获取到的是当前槽位的香氛，1，2，3对应的当前香氛
                int curValue = carPropHelper.getIntProp(realKey);

                LogUtils.i(TAG, "当前香氛获取到的index数：" + curValue);
                String[] slotFrance = stringToArray(getBaseStringProp(FragranceSignal.FRAGRANCE_SLOT_TYPE, 0));
//                for (int i = 0; i < slotFrance.length; i++) {
//                    if (slotFrance[i].equals(AIR_FRAGRANCE_INTEGER_STRING_MAP.get(curValue))) {
//                        res = slotFrance[i];
//                    }
//                }
                res = slotFrance[curValue - 1];
                LogUtils.i(TAG, "当前香氛模式为：" + res);
                break;
            case FragranceSignal.FRAGRANCE_LEVEL:
                curValue = (carPropHelper.getIntProp(realKey) == 0) ? 1 : carPropHelper.getIntProp(realKey);
                LogUtils.i(TAG, "当前香氛浓度为：" + curValue + " " + AIR_FRAGRANCE_CONCENTRATION_INTEGER_STRING_MAP.get(curValue));
                res = AIR_FRAGRANCE_CONCENTRATION_INTEGER_STRING_MAP.get(curValue);
                break;

            default:
                res = "";
                LogUtils.e(TAG, "香氛当前方法getBaseStringProp存在没处理的情况：" + key);
                break;
        }
        return res;
    }


    /**
     * 因为涉及到String和int相互转换
     *
     * @param key
     * @param area
     * @param value
     */
    @Override
    public void setBaseStringProp(String key, int area, String value) {
        int curKey = map.get(key);
        int curArea = getRealArea(area);
        int curValue;
        switch (key) {
            case FragranceSignal.FRAGRANCE_MODE:
                curKey = H56D.IVI_BodySet3_IVI_FRAGTASTE;
                //当前香氛槽位index+1
                //[0,1,2]原始的
                //[1,2,3]传入的
                //获取原来香氛的数组
                String slotString = getStringProp(FragranceSignal.FRAGRANCE_SLOT_TYPE);
                String[] slotArray = stringToArray(slotString);
                int res = 1;
                for (int i = 0; i < slotArray.length; i++) {
                    if (slotArray[i].equals(value)) {
                        res = i + 1;
                    }
                }
                curValue = res;
                break;
            case FragranceSignal.FRAGRANCE_LEVEL:
                curKey = H56D.IVI_BodySet3_IVI_FRAGCONCENTRATIONREQ;
                curValue = AIR_FRAGRANCE_CONCENTRATION_STRING_INTEGER_MAP.get(value);
                break;
            default:
                curValue = -1;
                LogUtils.e(TAG, "香氛当前方法setBaseStringProp存在没处理的情况：" + key);
                break;
        }

        carPropHelper.setIntProp(curKey, curArea
                , curValue);
    }

    @Override
    public boolean getBaseBooleanProp(String key, int area) {
        LogUtils.d(TAG, "key是：" + key + " value:" + map.get(key) + " area:" + area);
        if(key.equalsIgnoreCase(CommonSignal.COMMON_CEIL_CONFIG)){
            return super.getBaseBooleanProp(key,-1);
        }

        int curAirSwitchInt = 1;
        switch (key) {
            case FragranceSignal.FRAGRANCE_UI:

                break;
            default:
                curAirSwitchInt = carPropHelper.getIntProp(map.get(key),
                        getRealArea(area));
                break;

        }

        boolean realRes;
        switch (key) {
            case FragranceSignal.FRAGRANCE_SWITCH_STATE:
                realRes = (curAirSwitchInt == ParamsCommon.OnOffInvalid.ON);
                break;
            case FragranceSignal.FRAGRANCE_UI:
                if (area == 0) {
                    realRes = hvacService.isCurrentState("ACTION_IS_FRAG_DIALOG_SHOWING" + DISPLAY_ID);
                } else {
                    realRes = hvacService.isCurrentState("ACTION_IS_FRAG_DIALOG_SHOWING" + DISPLAY_ID2);
                }
                break;
            default:
                realRes = (curAirSwitchInt == ParamsCommon.OnOff.ON);
                break;
        }
        LogUtils.d(TAG, "curAirSwitchInt:" + curAirSwitchInt + " ParamsCommon.OnOffInvalid.ON:" + ParamsCommon.OnOffInvalid.ON);
        LogUtils.d(TAG, "curAirSwitchInt == ParamsCommon.OnOffInvalid.ON:" + (curAirSwitchInt == ParamsCommon.OnOffInvalid.ON));
        //默认0是false，1是true
        return realRes;
    }

    @Override
    public void setBaseBooleanProp(String key, int area, boolean value) {

        if (key.equals(FragranceSignal.FRAGRANCE_UI)) {
            if (value) {
                hvacService.exec("ACTION_OPEN_FRAG_DIALOG" + (area == 0 ? DISPLAY_ID : DISPLAY_ID2));
            } else {
                hvacService.exec("ACTION_DISMISS_FRAG_DIALOG" + (area == 0 ? DISPLAY_ID : DISPLAY_ID2));
            }
            return;
        }
        int realBoolean = -1;
        int resKey = map.get(key);
        switch (key) {
            case FragranceSignal.FRAGRANCE_SWITCH_STATE:
                resKey = H56D.IVI_BodySet3_IVI_FCMSWIRESP;
                realBoolean = getRealBoolean(value);
                break;
            default:
                LogUtils.d(TAG, "香氛的调节：key是" + key + "value:" + value);
                realBoolean = getRealBoolean(value);
                break;
        }
        LogUtils.d(TAG, "香氛的调节：所有的参数，key" + resKey + "value:" + value);
        carPropHelper.setIntProp(resKey,
                getRealArea(area), realBoolean);
    }


    /**
     * int OFF = 1;
     * int ON = 2;
     *
     * @param value
     * @return
     */
    private int getRealBoolean2(boolean value) {
        int realBoolean;
        if (value) {
            realBoolean = 2;
        } else {
            realBoolean = 1;
        }
        return realBoolean;
    }

    /**
     * int OFF = 0;
     * int ON = 1;
     *
     * @param value
     * @return
     */
    private int getRealBoolean(boolean value) {
        int realBoolean;
        if (value) {
            realBoolean = ParamsCommon.OnOffInvalid.ON;
        } else {
            realBoolean = ParamsCommon.OnOffInvalid.OFF;
        }
        return realBoolean;
    }

//    protected int getRealArea(int virtualPosition) {
//        int realPosition = VehicleArea.FRONT_ROW;
//        switch (virtualPosition) {
//            case PositionSignal.AREA_NONE:
//                realPosition = VehicleArea.FRONT_ROW;
//                break;
//            case 0:
//                realPosition = VehicleArea.FRONT_LEFT;
//                break;
//            case 1:
//                realPosition = VehicleArea.FRONT_RIGHT;
//                break;
//            case 2:
//                realPosition = VehicleArea.FRONT_LEFT;
//                break;
//            case 3:
//                realPosition = VehicleArea.FRONT_RIGHT;
//                break;
//            default:
//                break;
//        }
//        return realPosition;
//    }


//    //香氛开关
//        map.put(FragranceSignal.FRAGRANCE_SWITCH_STATE, Comforts.ID_FRAGRANCE_MODE);
//    //香氛等级
//        map.put(FragranceSignal.FRAGRANCE_LEVEL, Comforts.ID_FRAGRANCE_CCT);
//    //香氛模式
//        map.put(FragranceSignal.FRAGRANCE_MODE, Comforts.ID_FRAGRANCE_TAT);
//    //香氛时长
//        map.put(FragranceSignal.FRAGRANCE_TIME, Signal.ID_FCMRUNTIMEREQ);
//    //香氛槽位数据
//        map.put(FragranceSignal.FRAGRANCE_SLOT_TYPE, Signal.ID_FCM_CH_TYPE);

    /**
     * 1代表支持。0代表不支持。-代表没处理。（当前没处理表示支持）
     *
     * @param key
     * @return
     */
    @Override
    public String isSupport(String key) {
        switch (key) {
            case FragranceSignal.FRAGRANCE_SWITCH_STATE:
            case FragranceSignal.FRAGRANCE_LEVEL:
            case FragranceSignal.FRAGRANCE_MODE:
            case FragranceSignal.FRAGRANCE_TIME:
            case FragranceSignal.FRAGRANCE_SLOT_TYPE:
                return "1";
            default:
                return "-";
        }
    }


    private String[] stringToArray(String str) {
        String st2 = str.substring(1, str.length() - 1);
        System.out.println(st2);
        String[] array = st2.split(",");
        return array;
    }

    @Override
    public void registerDevice() {
        LogUtils.i(TAG, "香氛注册");

        String fragranceUIValue;
        //车型
        String str = CarServicePropUtils.getInstance().getCarType();
        //车型里的类型划分
        int configurationType = CarServicePropUtils.getInstance().getCarModeConfig();
        if (str.equals("H37A")) {
            fragranceUIValue = "1";
        } else if (configurationType == 2 || configurationType == 3) {
            fragranceUIValue = "-1";
        } else {
            //56的其他车型 "1-1-0-0-0-0"
            fragranceUIValue = "1-1-0-0-0-0";
        }
        //注册当前车的屏幕类型。
        boolean hasCeilScreen;
        try {
            hasCeilScreen = getBooleanProp(CommonSignal.COMMON_CEIL_CONFIG);
        } catch (Exception e) {
            hasCeilScreen = false;
            e.printStackTrace();
        }
        String screenValue;
        if (!fragranceUIValue.equals("-1")) {
            if (hasCeilScreen) {
                screenValue = "central_screen-passenger_screen-ceil_screen-armrest_screen";
            } else {
                screenValue = "central_screen-passenger_screen-armrest_screen";
            }
        }else{
            screenValue = "-1";
        }
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(FragranceSignal.FRAGRANCE_UI, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, screenValue);

        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(FragranceSignal.FRAGRANCE_SWITCH_STATE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, fragranceUIValue);

    }
}
