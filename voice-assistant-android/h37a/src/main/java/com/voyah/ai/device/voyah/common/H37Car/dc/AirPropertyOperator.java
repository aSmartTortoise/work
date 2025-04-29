package com.voyah.ai.device.voyah.common.H37Car.dc;

import android.util.Pair;

import com.blankj.utilcode.util.MapUtils;
import com.blankj.utilcode.util.Utils;
import com.example.filter_annotation.CarDevices;
import com.example.filter_annotation.CarType;
import com.voice.sdk.device.carservice.signal.AirSignal;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.carservice.signal.PositionSignal;
import com.voice.sdk.device.carservice.signal.SysControlSignal;
import com.voice.sdk.device.carservice.vcar.IDeviceRegister;
import com.voyah.ai.basecar.carservice.CarServicePropUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.basecar.carservice.CarPropUtils;
import com.voyah.ai.voice.platform.agent.api.flowchart.library.bean.FunctionalPositionBean;
import com.voyah.ai.voice.platform.agent.api.functionMatrix.FunctionDeviceMappingManager;
import com.voyah.cockpit.airconditioner.HvacServiceImpl;

import java.util.Map;

import mega.car.Signal;
import mega.car.VehicleArea;
import mega.car.config.Climate;
import mega.car.config.ParamsCommon;

/**
 * 统一
 * 1是true，0是false，不符合要求的再特殊处理。
 */
@CarDevices(carType = CarType.H37_CAR)
public class AirPropertyOperator extends Base37Operator implements IDeviceRegister {
    private static final String TAG = "AirPropertyOperator";
    private CarPropUtils carPropHelper;

    //默认参数
    private static final float TEMPERATURE_MAX = 32.5f;
    private static final float TEMPERATURE_MIN = 17.5f;
    private static final int TEMPERATURE_STEP = 2;

    @Override
    void init() {
        carPropHelper = CarPropUtils.getInstance();
        HvacServiceImpl.getInstance(Utils.getApp()).startService(() ->
                LogUtils.d(TAG, "AC onServiceConnected() called"));
        //空调开关
        map.put(AirSignal.AIR_SWITCH_STATE, Climate.ID_WHOLE_CABIN_AIR_CD);
        //温区同步开关
        map.put(AirSignal.AIR_TEMP_SYN_STATE, Climate.ID_AIR_CD_SYNC_DRV_SETTING_MODE);
        //空调温度
        map.put(AirSignal.AIR_TEMP, Climate.ID_TEMPERATURE);
        //风量
        map.put(AirSignal.AIR_WIND, Climate.ID_BLW_LEVEL);
        //AC开关
        map.put(AirSignal.AC_SWITCH_STATUS, Climate.ID_AIR_CD_AC_MODE);
        //吹风模式
        map.put(AirSignal.WIND_MODE, Climate.ID_BLW_DIRECT);
        //扫风模式
        map.put(AirSignal.SCAVENGING_WIND_MODE, Climate.ID_BLOWER_AUTO_MODE);
        //前除霜开关状态
        map.put(AirSignal.FRONT_DEFROST_SWITCH_STATE, Climate.ID_REQ_FRONT_DEFROST);
        //后除霜开关状态
        map.put(AirSignal.REAR_DEFROST_SWITCH_STATE, Climate.ID_REAR_DEFROST);
        //循环模式调节
        map.put(AirSignal.AIR_CYCLE_MODE, Climate.ID_RECYCLE_DOOR_MODE);
        //自动循环模式开关
        map.put(AirSignal.AIR_CYCLE_AUTO_STATE, Climate.ID_AQS_IONIZER);
        //干燥模式
        map.put(AirSignal.DRY_MODE, Signal.ID_DRY_CLEAN_MODE);
        //干燥除味
        map.put(AirSignal.DRY_MODE_SWITCH_STATE, Signal.ID_DRY_CLEAN_ON_OFF);
        //通风降温开关
        map.put(AirSignal.VENTILATION_SWITCH_STATE, Climate.ID_AUTO_WIND_UNLOCK);
        //自动除霜开关
        map.put(AirSignal.AUTO_DEFROST_SWITCH_STATE, Climate.ID_AIR_CD_DEFROST_AUTO_MODE);
        //自动除霜等级
        map.put(AirSignal.AUTO_DEFROST_LEVEL, Signal.ID_AUTO_DEF_LEVEL);
        //auto开关
        map.put(AirSignal.AUTO_SWITCH_STATE, Climate.ID_AUTO_AIR_CD_MODE);
        //pm2.5
        map.put(AirSignal.AIR_PM, Climate.ID_IN_CABIN_PM25_LEVEL);
        //快速制冷开关
        map.put(AirSignal.AIR_FAST_COOLING_SWITCH_STATE, Signal.ID_IVI_RAPID_COOL_REQ);
        //速热开关
        map.put(AirSignal.AIR_FAST_HEATER_SWITCH_STATE, Signal.ID_IVI_RAPID_HEAT_REQ);
        //出风口开关
        map.put(AirSignal.AIR_OUTLET_SWITCH_IS_SUPPORT, Signal.ID_EAO_ONOFF);
        //自动负离子开关
        map.put(AirSignal.AIR_ANION_SWITCH_STATE, Climate.ID_AUTO_ION_MODE);
        //负离子开关
        map.put(AirSignal.AIR_AUTO_ANION_SWITCH_STATE, Climate.ID_REQ_ION_MODE);

    }

    private static final Map<String, Integer> WINDMODE_STRING_INT_MAP = MapUtils.newLinkedHashMap(
            new Pair<>("to_face", Climate.ParamsFrontBlwDirect.FACE),
            new Pair<>("to_foot", Climate.ParamsFrontBlwDirect.FLOOR),
            new Pair<>("face_foot", Climate.ParamsFrontBlwDirect.FACE_FLOOR),
            new Pair<>("to_window", Climate.ParamsFrontBlwDirect.DEFROST),
            new Pair<>("window_foot", Climate.ParamsFrontBlwDirect.FLOOR_DEFROST),
            new Pair<>("face_foot_window", Climate.ParamsFrontBlwDirect.ALL),
            new Pair<>("face_window", Climate.ParamsFrontBlwDirect.FACE_DEFROST)
    );
    private static final Map<Integer, String> WINDMODE_INT_STRING_MAP = MapUtils.newLinkedHashMap(
            new Pair<>(Climate.ParamsFrontBlwDirect.FACE, "to_face"),
            new Pair<>(Climate.ParamsFrontBlwDirect.FLOOR, "to_foot"),
            new Pair<>(Climate.ParamsFrontBlwDirect.FACE_FLOOR, "face_foot"),
            new Pair<>(Climate.ParamsFrontBlwDirect.DEFROST, "to_window"),
            new Pair<>(Climate.ParamsFrontBlwDirect.FLOOR_DEFROST, "window_foot"),
            new Pair<>(Climate.ParamsFrontBlwDirect.ALL, "face_foot_window"),
            new Pair<>(Climate.ParamsFrontBlwDirect.FACE_DEFROST, "face_window")
    );

    private static final Map<String, Integer> SCAVENGING_STRING_INT_MAP = MapUtils.newLinkedHashMap(
            new Pair<>("avoid_face", Climate.ParamsBlowModeH53.AIRNOVENT),
            new Pair<>("focus_face", Climate.ParamsBlowModeH53.AIRTOVENT),
            new Pair<>("auto_swing", Climate.ParamsBlowModeH53.AIRAUTO)

    );
    private static final Map<Integer, String> SCAVENGING_INT_STRING_MAP = MapUtils.newLinkedHashMap(
            new Pair<>(Climate.ParamsBlowModeH53.AIRNOVENT, "avoid_face"),
            new Pair<>(Climate.ParamsBlowModeH53.AIRTOVENT, "focus_face"),
            new Pair<>(Climate.ParamsBlowModeH53.AIRAUTO, "auto_swing")
    );


    private static final Map<Integer, String> AIR_MODE_INTEGER_STRING_MAP = MapUtils.newLinkedHashMap(
            new Pair<>(Climate.ParamsRecycleDoorMode.LOOP_AUTO, "auto_circulation"),
            new Pair<>(Climate.ParamsRecycleDoorMode.LOOP_INNER, "inner_circulation"),
            new Pair<>(Climate.ParamsRecycleDoorMode.LOOP_OUTSIDE, "outer_circulation")
    );
    private static final Map<String, Integer> AIR_MODE_STRING_INTEGER_MAP = MapUtils.newLinkedHashMap(
            new Pair<>("auto_circulation", Climate.ParamsRecycleDoorMode.LOOP_AUTO),
            new Pair<>("inner_circulation", Climate.ParamsRecycleDoorMode.LOOP_INNER),
            new Pair<>("outer_circulation", Climate.ParamsRecycleDoorMode.LOOP_OUTSIDE)
    );

    private static final Map<Integer, String> AIR_DRY_MODE_INTEGER_STRING_MAP = MapUtils.newLinkedHashMap(
            //todo
            new Pair<>(Signal.ParamsDryCleanMode.DRY_CLEAN_MODE_DEFAULT, "standard"),
            new Pair<>(Signal.ParamsDryCleanMode.DRY_CLEAN_MODE_STANDRAD, "standard"),
            new Pair<>(Signal.ParamsDryCleanMode.DRY_CLEAN_MODE_DEEP, "strong")
    );
    private static final Map<String, Integer> AIR_DRY_MODE_STRING_INTEGER_MAP = MapUtils.newLinkedHashMap(
            new Pair<>("standard", Signal.ParamsDryCleanMode.DRY_CLEAN_MODE_STANDRAD),
            new Pair<>("strong", Signal.ParamsDryCleanMode.DRY_CLEAN_MODE_DEEP)
    );


    private static final Map<String, Integer> AIR_AUTO_DEFROST_STRING_INTEGER_MAP = MapUtils.newLinkedHashMap(
            new Pair<>("min", Signal.ParamsAutoDefLevelSts.AUTO_DEF_LEVEL_STS_LO),
            new Pair<>("low", Signal.ParamsAutoDefLevelSts.AUTO_DEF_LEVEL_STS_LO),
            new Pair<>("mid", Signal.ParamsAutoDefLevelSts.AUTO_DEF_LEVEL_STS_MID),
            new Pair<>("high", Signal.ParamsAutoDefLevelSts.AUTO_DEF_LEVEL_STS_HI),
            new Pair<>("max", Signal.ParamsAutoDefLevelSts.AUTO_DEF_LEVEL_STS_HI)
    );

    private static final Map<Integer, String> AIR_AUTO_DEFROST_INTEGER_STRING_MAP = MapUtils.newLinkedHashMap(
            new Pair<>(Signal.ParamsAutoDefLevelSts.AUTO_DEF_LEVEL_STS_LO, "low"),
            new Pair<>(Signal.ParamsAutoDefLevelSts.AUTO_DEF_LEVEL_STS_MID, "mid"),
            new Pair<>(Signal.ParamsAutoDefLevelSts.AUTO_DEF_LEVEL_STS_HI, "high")
    );

    @Override
    public int getBaseIntProp(String key, int area) {
        Integer curKey = map.get(key);
        int curValue;
        int resArea = getRealArea(area);
        if (curKey == null) {
            switch (key) {
                case AirSignal.MAX_AIR_WIND:
                    String carType = CarServicePropUtils.getInstance().getCarType();
                    if (carType.equals("H37A")) {
                        curValue = Climate.ParamsFrontBlwLevel.LEVEL8;
                    } else {
                        //H37B
                        curValue = Climate.ParamsFrontBlwLevel.LEVEL7;
                    }
                    break;
                case AirSignal.MIN_AIR_WIND:
                    curValue = Climate.ParamsFrontBlwLevel.LEVEL1;
                    break;
                default:
                    curValue = -1;
                    LogUtils.e(TAG, "空调里getBaseIntProp方法缺少对应key的处理" + key);
                    break;
            }
        } else {
            switch (key) {
                case AirSignal.AIR_WIND:
                    resArea = VehicleArea.FRONT_ROW;
                    curValue = carPropHelper.getIntProp(curKey,
                            resArea);
                    break;
                default:
                    curValue = carPropHelper.getIntProp(curKey,
                            resArea);
                    break;
            }

        }

        return curValue;
    }

    @Override
    public void setBaseIntProp(String key, int area, int value) {
        int resKey = map.get(key);
        int resArea = getRealArea(area);
        switch (key) {
            case AirSignal.AIR_WIND:
                resArea = VehicleArea.FRONT_ROW;
                break;
        }
        carPropHelper.setIntProp(resKey,
                resArea, value);
    }

    @Override
    public float getBaseFloatProp(String key, int area) {
        Integer curKey = map.get(key);
        float curValue;
        if (curKey == null) {
            switch (key) {
                case AirSignal.MAX_AIR_TEMP:
                    curValue = TEMPERATURE_MAX;
                    break;
                case AirSignal.MIN_AIR_TEMP:
                    curValue = TEMPERATURE_MIN;
                    break;
                default:
                    curValue = -1;
                    LogUtils.e(TAG, "空调里getBaseFloatProp方法缺少对应key的处理" + key);
                    break;
            }
        } else {
            int realPosition = getRealArea(area);
            switch (key) {
                case AirSignal.AIR_TEMP:
                    if (area == PositionSignal.FIRST_ROW_LEFT || area == PositionSignal.SECOND_ROW_LEFT || area == PositionSignal.THIRD_ROW_LEFT) {
                        realPosition = getRealArea(PositionSignal.FIRST_ROW_LEFT);
                    } else {
                        realPosition = getRealArea(PositionSignal.FIRST_ROW_RIGHT);
                    }
                    break;
            }
            curValue = carPropHelper.getFloatProp(curKey,
                    realPosition);
        }
        return curValue;
    }

    @Override
    public void setBaseFloatProp(String key, int area, float value) {
        int realPosition = getRealArea(area);
        switch (key) {
            case AirSignal.AIR_TEMP:
                if (area == PositionSignal.FIRST_ROW_LEFT || area == PositionSignal.SECOND_ROW_LEFT || area == PositionSignal.THIRD_ROW_LEFT) {
                    realPosition = getRealArea(PositionSignal.FIRST_ROW_LEFT);
                } else {
                    realPosition = getRealArea(PositionSignal.FIRST_ROW_RIGHT);
                }
                break;
        }
        carPropHelper.setFloatProp(map.get(key), realPosition,
                value);
    }

    @Override
    public String getBaseStringProp(String key, int area) {
        int resPosition = getRealArea(area);

        //是本地常量，不是通过接口获取的。所以map里也不需要映射。
        switch (key) {
            case AirSignal.MAX_AUTO_DEFROST_LEVEL:
                return AIR_AUTO_DEFROST_INTEGER_STRING_MAP.get(Signal.ParamsAutoDefLevelSts.AUTO_DEF_LEVEL_STS_HI);
            case AirSignal.MIN_AUTO_DEFROST_LEVEL:
                return AIR_AUTO_DEFROST_INTEGER_STRING_MAP.get(Signal.ParamsAutoDefLevelSts.AUTO_DEF_LEVEL_STS_LO);
            case AirSignal.WIND_MODE:
                resPosition = VehicleArea.FRONT_ROW;
                ;
                break;
        }


        int curValue = carPropHelper.getIntProp(map.get(key),
                resPosition);
        String res;
        switch (key) {
            case AirSignal.WIND_MODE:
                res = WINDMODE_INT_STRING_MAP.get(curValue);
                break;
            case AirSignal.SCAVENGING_WIND_MODE:
                res = SCAVENGING_INT_STRING_MAP.get(curValue);
                if (res == null) {
                    //因为向上吹脸会触发向上扫风。
                    //没有任何扫风模式的时候，给一个""
                    res = "";//SCAVENGING_INT_STRING_MAP.get(2);
                }
                break;
            case AirSignal.AIR_CYCLE_MODE:
                res = AIR_MODE_INTEGER_STRING_MAP.get(curValue);
                break;
            case AirSignal.DRY_MODE:
                res = AIR_DRY_MODE_INTEGER_STRING_MAP.get(curValue);
                break;
            case AirSignal.AUTO_DEFROST_LEVEL:
                res = AIR_AUTO_DEFROST_INTEGER_STRING_MAP.get(curValue);
                break;
            default:
                res = "";
                LogUtils.e(TAG, "空调当前方法getBaseStringProp存在没处理的情况：" + key);
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
            case AirSignal.WIND_MODE:
                curArea = VehicleArea.FRONT_ROW;
                ;
                curValue = WINDMODE_STRING_INT_MAP.get(value);
                break;
            case AirSignal.SCAVENGING_WIND_MODE:
                curValue = SCAVENGING_STRING_INT_MAP.get(value);
                break;
            case AirSignal.AIR_CYCLE_MODE:
                curValue = AIR_MODE_STRING_INTEGER_MAP.get(value);
                break;
            case AirSignal.DRY_MODE:
                curValue = AIR_DRY_MODE_STRING_INTEGER_MAP.get(value);
                break;
            case AirSignal.AUTO_DEFROST_LEVEL:
                curValue = AIR_AUTO_DEFROST_STRING_INTEGER_MAP.get(value);
                break;
            default:
                curValue = -1;
                LogUtils.e(TAG, "空调当前方法setBaseStringProp存在没处理的情况：" + key);
                break;
        }

        carPropHelper.setIntProp(curKey, curArea
                , curValue);
    }

    @Override
    public boolean getBaseBooleanProp(String key, int area) {
        LogUtils.d(TAG, "key是：" + key + " value:" + map.get(key));

        if (AirSignal.AIR_UI_STATE.equals(key)) {
            boolean curAirPageState = HvacServiceImpl.getInstance(Utils.getApp()).isCurrentState("ACTION_IS_AC_FOREGROUND");
            return curAirPageState;
        }
        //出风口开关
        if (AirSignal.AIR_OUTLET_SWITCH_IS_SUPPORT.equals(key)) {

            boolean isOpen = true;
            if (area == PositionSignal.FIRST_ROW_LEFT) {
                //左侧

                isOpen &= carPropHelper.getIntProp(map.get(key),
                        VehicleArea.FRONT_LEFT) == Signal.ParamsCommonOnOffInvalid.ONOFFSTS_ON;
                isOpen &= carPropHelper.getIntProp(map.get(key),
                        VehicleArea.FRONT_LEFT_CENTER) == Signal.ParamsCommonOnOffInvalid.ONOFFSTS_ON;
            } else {
                //右侧
                isOpen &= carPropHelper.getIntProp(map.get(key),
                        VehicleArea.FRONT_RIGHT) == Signal.ParamsCommonOnOffInvalid.ONOFFSTS_ON;
                isOpen &= carPropHelper.getIntProp(map.get(key),
                        VehicleArea.FRONT_RIGHT_CENTER) == Signal.ParamsCommonOnOffInvalid.ONOFFSTS_ON;
            }
            return isOpen;
        }


        int position = getRealArea(area);
        switch (key) {
            case AirSignal.AUTO_SWITCH_STATE:
            case AirSignal.AIR_TEMP_SYN_STATE:
            case AirSignal.AIR_SWITCH_STATE:
                position = VehicleArea.FRONT_ROW;
                break;
            case AirSignal.AIR_FAST_HEATER_SWITCH_STATE:
            case AirSignal.AIR_FAST_COOLING_SWITCH_STATE:
                position = VehicleArea.NONE;
                break;
        }


        int curAirSwitchInt = carPropHelper.getIntProp(map.get(key),
                position);
        boolean realRes;
        LogUtils.i(TAG, "进入到目标方法了" + key);
        switch (key) {
            case AirSignal.DRY_MODE_SWITCH_STATE:
                realRes = (curAirSwitchInt == Signal.OnOffReq.ON);
                break;
//            case AirSignal.AIR_SWITCH_STATE:
//                realRes = (curAirSwitchInt == ParamsCommon.OnOff.ON);
//                LogUtils.i(TAG,"的到的结果是："+realRes +" "+curAirSwitchInt);
//                break;
            case AirSignal.AIR_FAST_HEATER_SWITCH_STATE:
            case AirSignal.AIR_FAST_COOLING_SWITCH_STATE:
                realRes = (curAirSwitchInt == Signal.ParamsCommonOnOffInvalid.ONOFFSTS_ON);
                break;

            default:
                realRes = (curAirSwitchInt == ParamsCommon.OnOff.ON);
                break;
        }
        //默认0是false，1是true
        return realRes;
    }

    @Override
    public void setBaseBooleanProp(String key, int area, boolean value) {
        int realBoolean = -1;
        int position = getRealArea(area);
        realBoolean = getRealBoolean(value);
        switch (key) {
            case AirSignal.AIR_UI_STATE:
                if (value) {
                    LogUtils.i(TAG, "打开空调界面：" + 0);
                    HvacServiceImpl.getInstance(Utils.getApp()).exec("ACTION_OPEN_AC_PAGE");
                } else {
                    LogUtils.i(TAG, "关闭空调界面：" + -1);
                    HvacServiceImpl.getInstance(Utils.getApp()).exec("ACTION_CLOSE_AC_PAGE");
                }
                return;
            case AirSignal.AUTO_SWITCH_STATE:
            case AirSignal.AIR_TEMP_SYN_STATE:
            case AirSignal.AIR_SWITCH_STATE:
                position = VehicleArea.FRONT_ROW;
                break;
            case AirSignal.DRY_MODE_SWITCH_STATE:
                realBoolean = getRealBoolean2(value);
                break;
            case AirSignal.AIR_OUTLET_SWITCH_IS_SUPPORT:
                //37B的出风口接口的开关设置是另外一套逻辑，2是打开，1是关闭。
                if (area == PositionSignal.FIRST_ROW_LEFT) {
                    realBoolean = value ? Signal.CommonOnOff.ON : Signal.CommonOnOff.OFF;
                    //左侧
                    carPropHelper.setIntProp(map.get(key),
                            VehicleArea.FRONT_LEFT, realBoolean);
                    carPropHelper.setIntProp(map.get(key),
                            VehicleArea.FRONT_LEFT_CENTER, realBoolean);
                } else {
                    realBoolean = value ? Signal.CommonOnOff.ON : Signal.CommonOnOff.OFF;
                    //右侧
                    carPropHelper.setIntProp(map.get(key),
                            VehicleArea.FRONT_RIGHT, realBoolean);
                    carPropHelper.setIntProp(map.get(key),
                            VehicleArea.FRONT_RIGHT_CENTER, realBoolean);
                }
                return;
            case AirSignal.AIR_FAST_HEATER_SWITCH_STATE:
            case AirSignal.AIR_FAST_COOLING_SWITCH_STATE:
                position = VehicleArea.NONE;
                realBoolean = value ? Signal.ParamsCommonOnOffInvalid.ONOFFSTS_ON : Signal.ParamsCommonOnOffInvalid.ONOFFSTS_OFF;
                break;
            default:
                LogUtils.d(TAG, "空调的调节：key是" + key + "value:" + value);
        }
        LogUtils.d(TAG, "空调的调节：所有的参数，key:" + map.get(key) + "position:" + position + " value:" + value);
        carPropHelper.setIntProp(map.get(key),
                position, realBoolean);
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
            realBoolean = ParamsCommon.OnOff.ON;
        } else {
            realBoolean = ParamsCommon.OnOff.OFF;
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


    /**
     * 1代表支持。0代表不支持。-代表没处理。（当前没处理表示支持）
     *
     * @param key
     * @return
     */
    @Override
    public String isSupport(String key) {
        switch (key) {
            case AirSignal.AIR_SWITCH_STATE:
            case AirSignal.AIR_TEMP_SYN_STATE:
            case AirSignal.AIR_TEMP:
            case AirSignal.AIR_WIND:
            case AirSignal.AC_SWITCH_STATUS:
            case AirSignal.WIND_MODE:
            case AirSignal.SCAVENGING_WIND_MODE:
            case AirSignal.FRONT_DEFROST_SWITCH_STATE:
            case AirSignal.REAR_DEFROST_SWITCH_STATE:
            case AirSignal.AIR_CYCLE_MODE:
            case AirSignal.AIR_CYCLE_AUTO_STATE:
            case AirSignal.AIR_PM:
            case AirSignal.DRY_MODE:
            case AirSignal.DRY_MODE_SWITCH_STATE:
            case AirSignal.VENTILATION_SWITCH_STATE:
            case AirSignal.AUTO_DEFROST_SWITCH_STATE:
            case AirSignal.AUTO_DEFROST_LEVEL:
            case AirSignal.AUTO_SWITCH_STATE:
            case AirSignal.ANION_SWITCH_STATE:
                return "1";
            default:
                return "-";
        }
    }


    @Override
    public void registerDevice() {
        LogUtils.i(TAG, "空调注册");

//        List<String> listSwitchState = new ArrayList<>();
//        listSwitchState.add("open");
//        listSwitchState.add("close");
//        //空调开关注册
//        FunctionDeviceMappingManager.getInstance().registerDeviceSupport(AirSignal.AIR_SWITCH_STATE, "switch_type", operator.isSupport(AirSignal.AIR_SWITCH_STATE), listSwitchState);
//        FunctionDeviceMappingManager.getInstance().registerDeviceSupport(AirSignal.AIR_TEMP_SYN_STATE, "switch_type", operator.isSupport(AirSignal.AIR_TEMP_SYN_STATE), listSwitchState);
//        FunctionDeviceMappingManager.getInstance().registerDeviceSupport(AirSignal.AC_SWITCH_STATUS, "switch_type", operator.isSupport(AirSignal.AC_SWITCH_STATUS), listSwitchState);
//        FunctionDeviceMappingManager.getInstance().registerDeviceSupport(AirSignal.FRONT_DEFROST_SWITCH_STATE, "switch_type", operator.isSupport(AirSignal.FRONT_DEFROST_SWITCH_STATE), listSwitchState);
//        FunctionDeviceMappingManager.getInstance().registerDeviceSupport(AirSignal.REAR_DEFROST_SWITCH_STATE, "switch_type", operator.isSupport(AirSignal.REAR_DEFROST_SWITCH_STATE), listSwitchState);
//        FunctionDeviceMappingManager.getInstance().registerDeviceSupport(AirSignal.AIR_CYCLE_AUTO_STATE, "switch_type", operator.isSupport(AirSignal.AIR_CYCLE_AUTO_STATE), listSwitchState);
//        FunctionDeviceMappingManager.getInstance().registerDeviceSupport(AirSignal.DRY_MODE_SWITCH_STATE, "switch_type", operator.isSupport(AirSignal.DRY_MODE_SWITCH_STATE), listSwitchState);
//        FunctionDeviceMappingManager.getInstance().registerDeviceSupport(AirSignal.VENTILATION_SWITCH_STATE, "switch_type", operator.isSupport(AirSignal.VENTILATION_SWITCH_STATE), listSwitchState);
//        FunctionDeviceMappingManager.getInstance().registerDeviceSupport(AirSignal.AUTO_DEFROST_SWITCH_STATE, "switch_type", operator.isSupport(AirSignal.AUTO_DEFROST_SWITCH_STATE), listSwitchState);
//        FunctionDeviceMappingManager.getInstance().registerDeviceSupport(AirSignal.AUTO_SWITCH_STATE, "switch_type", operator.isSupport(AirSignal.AUTO_SWITCH_STATE), listSwitchState);
//        FunctionDeviceMappingManager.getInstance().registerDeviceSupport(AirSignal.ANION_SWITCH_STATE, "switch_type", operator.isSupport(AirSignal.ANION_SWITCH_STATE), listSwitchState);
//        //数值类型的参数，可以不填，参数。
//        FunctionDeviceMappingManager.getInstance().registerDeviceSupport(AirSignal.AIR_TEMP, "", operator.isSupport(AirSignal.AIR_TEMP), new ArrayList<>());
//        FunctionDeviceMappingManager.getInstance().registerDeviceSupport(AirSignal.AIR_WIND, "", operator.isSupport(AirSignal.AIR_WIND), new ArrayList<>());
//        //暂时可以不用，我这只是做一下尝试。
//        List<String> listWindMode = new ArrayList<>();
//        listWindMode.add("to_face");
//        listWindMode.add("to_foot");
//        listWindMode.add("face_foot");
//        listWindMode.add("to_window");
//        listWindMode.add("face_window");
//        listWindMode.add("window_foot");
//        listWindMode.add("face_foot_window");
//        FunctionDeviceMappingManager.getInstance().registerDeviceSupport(AirSignal.WIND_MODE, "switch_mode", operator.isSupport(AirSignal.WIND_MODE), listWindMode);
//
//
//        List<String> listScavengingMode = new ArrayList<>();
//        listScavengingMode.add("avoid_face");
//        listScavengingMode.add("focus_face");
//        listScavengingMode.add("auto_swing");
//        FunctionDeviceMappingManager.getInstance().registerDeviceSupport(AirSignal.SCAVENGING_WIND_MODE, "switch_mode", operator.isSupport(AirSignal.SCAVENGING_WIND_MODE), listScavengingMode);
//
//
//        List<String> listCycleMode = new ArrayList<>();
//        listCycleMode.add("inner_circulation");
//        listCycleMode.add("outer_circulation");
//        listCycleMode.add("auto_circulation");
//        FunctionDeviceMappingManager.getInstance().registerDeviceSupport(AirSignal.AIR_CYCLE_MODE, "switch_mode", operator.isSupport(AirSignal.AIR_CYCLE_MODE), listCycleMode);
//
//        FunctionDeviceMappingManager.getInstance().registerDeviceSupport(AirSignal.AIR_PM, "", operator.isSupport(AirSignal.AIR_PM), null);
//        List<String> listDryMode = new ArrayList<>();
//        listDryMode.add("standard");
//        listDryMode.add("strong");
//        FunctionDeviceMappingManager.getInstance().registerDeviceSupport(AirSignal.DRY_MODE, "switch_mode", operator.isSupport(AirSignal.DRY_MODE), listDryMode);
//
//        List<String> listLevel = new ArrayList<>();
//        listLevel.add("max");
//        listLevel.add("high");
//        listLevel.add("mid");
//        listLevel.add("low");
//        listLevel.add("min");
//
//        FunctionDeviceMappingManager.getInstance().registerDeviceSupport(AirSignal.AUTO_DEFROST_LEVEL, "switch_mode", operator.isSupport(AirSignal.AUTO_DEFROST_LEVEL), listLevel);

        //注册当前车的屏幕类型。

        boolean hasCeilScreen;
        try {
            hasCeilScreen = getBooleanProp(CommonSignal.COMMON_CEIL_CONFIG);
        } catch (Exception e) {
            hasCeilScreen = false;
            e.printStackTrace();
        }
        LogUtils.i(TAG, "吸顶屏是否存在：" + hasCeilScreen);
        String screenValue;
        //在37A上只有中控屏、划移屏也是中控屏
        if (hasCeilScreen) {
            screenValue = "central_screen-scratch_screen";
        } else {
            screenValue = "central_screen-scratch_screen";
        }

        //空调开关功能，可以当成空调个数。
        String airSwitchValue;
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equals("H56C")) {
            airSwitchValue = "0-0-0-0-0-0-1-1-0";
        } else {
            //已经适配了37A
            airSwitchValue = "1";
        }
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_SWITCH_STATE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, airSwitchValue);

        //负离子功能
        //判断当前车型是
        String anionValue;
        int configurationType = CarServicePropUtils.getInstance().getCarModeConfig();
        LogUtils.i(TAG, "当前车型是：" + configurationType);
//        if (configurationType == 2 || configurationType == 3) {
        anionValue = "-1";
//        } else {
//            anionValue = "1";
//        }
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_ANION_SWITCH_STATE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, anionValue);
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_AUTO_ANION_SWITCH_STATE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, anionValue);


        String AirUIValue = "";
        //空调界面功能位判断
        if (hasCeilScreen) {
            AirUIValue = "central_screen-scratch_screen";
        } else {
            AirUIValue = "central_screen-scratch_screen";
        }
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_UI_STATE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, AirUIValue);

        //智能识别开关功能位
        //37没这个功能
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_INTELLIGENT_IDENTIFICATION_SWITCH_STATE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "-1");

        //Auto开关功能位,前排，二排
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AUTO_SWITCH_STATE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "1");
        //自动循环开关，37有
        String autoCycleValue;

        autoCycleValue = "1";

        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_CYCLE_AUTO_STATE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, autoCycleValue);
        //自动循环2 AIR_CYCLE_AUTO_STATE2
        String autoCycleValue2;
        //-1表示自动循环是开关，1表示自动循环是功能切换
        if (carType.equals("H37B")) {
            autoCycleValue2 = "-1";
        } else {
            autoCycleValue2 = "1";
        }
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_CYCLE_AUTO_STATE2, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, autoCycleValue2);


        //通风降温功能位
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.VENTILATION_SWITCH_STATE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "1");
        //干燥除味功能位
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.DRY_MODE_SWITCH_STATE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "1");
        //PM2.5功能位
        String PMValue;
//        if (carType.equals("H37B")) {
//            PMValue = "-1";
//        } else {
        PMValue = "1";
//        }
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_PM, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, PMValue);
        //扫风模式功能位
        String windModeValue;
//        if (carType.equals("H56C")) {
//            windModeValue = "-1";
//        } else {
        windModeValue = "1-1-0-0";
//        }
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.SCAVENGING_WIND_MODE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, windModeValue);

        //空调温度功能位的处理
        //主驾、副驾、二排是支持温度调节的
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_TEMP, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "1-1-0-0-0-0-0-0-0");
        //温区同步功能位
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_TEMP_SYN_STATE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "0-0-0-0-0-0-1-0-0");
        //吹风模式
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.WIND_MODE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "1");
        //todo AirWind 功能位添加，需要别的车型也要添加
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_WIND, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "1");
        //出风口开关功能是否支持的功能位
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_OUTLET_SWITCH, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "1");


        //============通用的功能位注册=========


        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(CommonSignal.COMMON_SCREEN_ENUMERATION, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL,
                screenValue);
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(CommonSignal.COMMON_SEAT_STATE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "1-1-1-1");

        //===特殊功能位判断56有的逻辑，但37A里没有===
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_SWITCH_STATE_SEAT_SINGLE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL,
                "-1");

        //============需要56C也注册的功能位，此处是37B新增和修改的功能位需求============
        //速冷、速热模式开关
        String fastCooling;
        String fastHeater;
        if (carType.equals("H37B")) {
            fastCooling = "1";
            fastHeater = "1";
        } else {
            fastCooling = "-1";
            fastHeater = "-1";
        }
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_FAST_COOLING_SWITCH_STATE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL,
                fastCooling);
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_FAST_HEATER_SWITCH_STATE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL,
                fastHeater);
        //前除霜打开后，是否可以支持副驾温度调节功能位。
        String isFrontDefrostOpenRightTempIsAdjust;
        if (carType.equals("H37B")) {
            isFrontDefrostOpenRightTempIsAdjust = "1";
        } else {
            isFrontDefrostOpenRightTempIsAdjust = "-1";
        }
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_FRONT_DEFROST_OPEN_RIGHT_TEMP_IS_ADJUST, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL,
                isFrontDefrostOpenRightTempIsAdjust);
        //AUTO开关是否支持
        String autoSwitchIsSupport;
        if (carType.equals("H37B")) {
            autoSwitchIsSupport = "1";
        } else {
            autoSwitchIsSupport = "-1";
        }
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_AUTO_SWITCH_IS_SUPPORT, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL,
                autoSwitchIsSupport);
        //出风口开关功能是否存在。
        String airOutletIsSupport;
        if (carType.equals("H37B")) {
            airOutletIsSupport = "1-1-0-0";
        } else {
            airOutletIsSupport = "-1";
        }
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_OUTLET_SWITCH_IS_SUPPORT, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL,
                airOutletIsSupport);
        //R挡功能是否支持
        String isSupportR = "-1";
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(CommonSignal.COMMON_IS_SUPPORT_R, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL,
                isSupportR);
        //查询座舱温度功能位
        String isSupportSearchCabinTemp = "-1";
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_CABIN_TEMP, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL,
                isSupportSearchCabinTemp);


    }
}
