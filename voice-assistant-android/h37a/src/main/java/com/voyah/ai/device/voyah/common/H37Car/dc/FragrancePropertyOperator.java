package com.voyah.ai.device.voyah.common.H37Car.dc;

import android.util.Pair;

import com.blankj.utilcode.util.MapUtils;
import com.blankj.utilcode.util.Utils;
import com.example.filter_annotation.CarDevices;
import com.example.filter_annotation.CarType;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.carservice.signal.FragranceSignal;
import com.voice.sdk.device.carservice.signal.SysControlSignal;
import com.voice.sdk.device.carservice.vcar.IDeviceRegister;
import com.voyah.ai.basecar.carservice.CarServicePropUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.basecar.carservice.CarPropUtils;
import com.voyah.ai.voice.platform.agent.api.flowchart.library.bean.FunctionalPositionBean;
import com.voyah.ai.voice.platform.agent.api.functionMatrix.FunctionDeviceMappingManager;
import com.voyah.cockpit.airconditioner.HvacServiceImpl;

import java.util.Arrays;
import java.util.Map;

import mega.car.Signal;
import mega.car.config.Comforts;
import mega.car.config.ParamsCommon;
import mega.car.hardware.CarPropertyValue;

/**
 * 统一
 * 1是true，0是false，不符合要求的再特殊处理。
 */
@CarDevices(carType = CarType.H37_CAR)
public class FragrancePropertyOperator extends Base37Operator implements IDeviceRegister {
    private static final String TAG = "FragrancePropertyOperator";
    private CarPropUtils carPropHelper;


    @Override
    void init() {
        carPropHelper = CarPropUtils.getInstance();
        //香氛开关
        map.put(FragranceSignal.FRAGRANCE_SWITCH_STATE, Comforts.ID_FRAGRANCE_MODE);
        //香氛等级
        map.put(FragranceSignal.FRAGRANCE_LEVEL, Comforts.ID_FRAGRANCE_CCT);
        //香氛模式
        map.put(FragranceSignal.FRAGRANCE_MODE, Comforts.ID_FRAGRANCE_TAT);
        //香氛时长
        map.put(FragranceSignal.FRAGRANCE_TIME, Signal.ID_FCMRUNTIMEREQ);
        //香氛槽位数据
        map.put(FragranceSignal.FRAGRANCE_SLOT_TYPE, Signal.ID_FCM_CH_TYPE);
    }

    private static final Map<Integer, String> AIR_FRAGRANCE_INTEGER_STRING_MAP = MapUtils.newLinkedHashMap(
            new Pair<>(0, "无香氛"),
            new Pair<>(1, "海静"),
            new Pair<>(2, "木影"),
            new Pair<>(3, "竹境"),
            new Pair<>(4, "风煦"),
            new Pair<>(5, "花韵"),
            new Pair<>(6, "茶悠"),
            new Pair<>(0xFF, "无效香氛")
    );

    private static final Map<String, Integer> AIR_FRAGRANCE_STRING_INTEGER_MAP = MapUtils.newLinkedHashMap(
            new Pair<>("无香氛", 0),
            new Pair<>("海静", 1),
            new Pair<>("木影", 2),
            new Pair<>("竹境", 3),
            new Pair<>("风煦", 4),
            new Pair<>("花韵", 5),
            new Pair<>("茶悠", 6),
            new Pair<>("无效香氛", 0xFF)
    );

    private static final Map<Integer, String> AIR_FRAGRANCE_CONCENTRATION_INTEGER_STRING_MAP = MapUtils.newLinkedHashMap(
            new Pair<>(Comforts.FragranceCct.FCMCCTREQ_LO, "low"),
            new Pair<>(Comforts.FragranceCct.FCMCCTREQ_MID, "mid"),
            new Pair<>(Comforts.FragranceCct.FCMCCTREQ_HI, "high")
    );

    private static final Map<String, Integer> AIR_FRAGRANCE_CONCENTRATION_STRING_INTEGER_MAP = MapUtils.newLinkedHashMap(
            new Pair<>("min", Comforts.FragranceCct.FCMCCTREQ_LO),
            new Pair<>("low", Comforts.FragranceCct.FCMCCTREQ_LO),
            new Pair<>("mid", Comforts.FragranceCct.FCMCCTREQ_MID),
            new Pair<>("high", Comforts.FragranceCct.FCMCCTREQ_HI),
            new Pair<>("max", Comforts.FragranceCct.FCMCCTREQ_HI)
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
                    resValue = 254;
                } else {
                    resValue = value;
                }
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
                CarPropertyValue fragranceStatus = carPropHelper.getPropertyRaw(realKey);
                LogUtils.d(TAG, "获取香氛槽位信息：" + ((fragranceStatus == null) ? "获取的信号是null的" : "不是空的"));
                Integer[] integers = null;
                LogUtils.d(TAG, fragranceStatus.getValue().toString());
                if (fragranceStatus != null && fragranceStatus.getValue() instanceof Integer[]) {
                    // 0:副驾 1:后左 2:后中 3:后右
                    integers = (Integer[]) fragranceStatus.getValue();
                    LogUtils.d(TAG, "香氛槽位数组是" + Arrays.toString(integers));

                }
                String[] frangeArray = new String[]{AIR_FRAGRANCE_INTEGER_STRING_MAP.get(integers[0]), AIR_FRAGRANCE_INTEGER_STRING_MAP.get(integers[1]), AIR_FRAGRANCE_INTEGER_STRING_MAP.get(integers[2])};
                LogUtils.d(TAG, "当前香氛类型是：" + Arrays.toString(frangeArray));
                res = "[" + frangeArray[0] + "," + frangeArray[1] + "," + frangeArray[2] + "]";
                LogUtils.d(TAG, "当前香氛类型是打印结果：" + res);
                break;
            case FragranceSignal.FRAGRANCE_MODE:
                //获取到的是当前槽位的香氛
                int curValue = carPropHelper.getIntProp(realKey);

                LogUtils.i(TAG, "当前香氛获取到的index数：" + curValue);
                String[] slotFrance = stringToArray(getBaseStringProp(FragranceSignal.FRAGRANCE_SLOT_TYPE, 0));
//                for (int i = 0; i < slotFrance.length; i++) {
//                    if (slotFrance[i].equals(AIR_FRAGRANCE_INTEGER_STRING_MAP.get(curValue))) {
//                        res = slotFrance[i];
//                    }
//                }
                res = slotFrance[curValue - 1];
                LogUtils.i(TAG, "当前香氛模式为：" + slotFrance[curValue - 1]);
                break;
            case FragranceSignal.FRAGRANCE_LEVEL:
                curValue = (carPropHelper.getIntProp(Comforts.ID_FRAGRANCE_CCT) == -1) ? Comforts.FragranceCct.FCMCCTREQ_LO : carPropHelper.getIntProp(Comforts.ID_FRAGRANCE_CCT);
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


        if (key.equals(FragranceSignal.FRAGRANCE_UI)) {
            boolean curFragrancePageState = HvacServiceImpl.getInstance(Utils.getApp()).isCurrentState("ACTION_IS_FRAG_DIALOG_SHOWING");
            return curFragrancePageState;
        }

        int curAirSwitchInt = carPropHelper.getIntProp(map.get(key),
                getRealArea(area));
        boolean realRes;
        switch (key) {
            case FragranceSignal.FRAGRANCE_SWITCH_STATE:
                realRes = (curAirSwitchInt == ParamsCommon.OnOffInvalid.ON);
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
        int realBoolean = -1;
        switch (key) {
            case FragranceSignal.FRAGRANCE_UI:
                if (value) {
                    LogUtils.i(TAG, "打开香氛界面");
                    HvacServiceImpl.getInstance(Utils.getApp()).exec("ACTION_OPEN_FRAG_DIALOG");
                } else {
                    LogUtils.i(TAG, "关闭香氛界面");
                    HvacServiceImpl.getInstance(Utils.getApp()).exec("ACTION_DISMISS_FRAG_DIALOG");
                }
                return;
            default:
                LogUtils.d(TAG, "香氛的调节：key是" + key + "value:" + value);
                realBoolean = getRealBoolean(value);
        }
        LogUtils.d(TAG, "香氛的调节：所有的参数，key" + map.get(key) + "value:" + value);
        carPropHelper.setIntProp(map.get(key),
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
            realBoolean = Signal.OnOffReq.ON;
        } else {
            realBoolean = Signal.OnOffReq.OFF;
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

        if (str.equals("H37A") || str.equals("H37B")) {
            screenValue = "central_screen-scratch_screen";
        } else if (configurationType == 2 || configurationType == 3) {
            screenValue = "-1";
        } else {
            if (hasCeilScreen) {
                screenValue = "central_screen-passenger_screen-ceil_screen-armrest_screen";
            } else {
                screenValue = "central_screen-passenger_screen-armrest_screen";
            }
        }
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(FragranceSignal.FRAGRANCE_UI, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, screenValue);
        LogUtils.i(TAG, "香氛的功能位：" + fragranceUIValue + " 获取到的车型：" + configurationType);
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(FragranceSignal.FRAGRANCE_SWITCH_STATE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, fragranceUIValue);

    }
}
