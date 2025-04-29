package com.voyah.ai.device.voyah.common.H56Car.dc;


import android.util.Pair;

import com.blankj.utilcode.util.MapUtils;
import com.blankj.utilcode.util.Utils;
import com.example.filter_annotation.CarDevices;
import com.example.filter_annotation.CarType;
import com.voice.sdk.device.carservice.signal.AirSignal;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.carservice.signal.PositionSignal;
import com.voice.sdk.device.carservice.vcar.IDeviceRegister;
import com.voyah.ai.basecar.carservice.CarPropUtils;
import com.voyah.ai.basecar.carservice.CarServicePropUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.voice.platform.agent.api.flowchart.library.bean.FunctionalPositionBean;
import com.voyah.ai.voice.platform.agent.api.functionMatrix.FunctionDeviceMappingManager;
import com.voyah.cockpit.airconditioner.HvacServiceImpl;

import java.util.Map;

import mega.car.VehicleArea;
import mega.car.config.Climate;
import mega.car.config.H56C;
import mega.car.config.ParamsCommon;

/**
 * 统一
 * 1是true，0是false，不符合要求的再特殊处理。
 */
@CarDevices(carType = CarType.H37_CAR)
public class AirPropertyOperator extends Base56Operator implements IDeviceRegister {
    private static final String TAG = "AirPropertyOperator";
    private CarPropUtils carPropHelper;

    //默认参数
    private static final float TEMPERATURE_MAX = 30.5f;
    private static final float TEMPERATURE_MIN = 15.5f;
    private static final int TEMPERATURE_STEP = 2;

    private HvacServiceImpl hvacService;

    private static final String DISPLAY_ID = "_displayId";
    private static final String DISPLAY_ID2 = "_displayId2";

    @Override
    void init() {

        carPropHelper = CarPropUtils.getInstance();
        hvacService = HvacServiceImpl.getInstance(Utils.getApp());
        hvacService.startService(() ->
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
        //吹风模式 修改
        map.put(AirSignal.WIND_MODE, Climate.ID_BLW_DIRECT);
        //扫风模式  修改
        map.put(AirSignal.SCAVENGING_WIND_MODE, Climate.ID_BLW_DIRECT);
        //前除霜开关状态
        map.put(AirSignal.FRONT_DEFROST_SWITCH_STATE, Climate.ID_REQ_FRONT_DEFROST);
        //后除霜开关状态
        map.put(AirSignal.REAR_DEFROST_SWITCH_STATE, Climate.ID_REAR_DEFROST);
        //循环模式调节
        map.put(AirSignal.AIR_CYCLE_MODE, Climate.ID_RECYCLE_DOOR_MODE);
        //自动循环模式开关 修改
        map.put(AirSignal.AIR_CYCLE_AUTO_STATE, Climate.ID_RECYCLE_DOOR_MODE);
        //干燥模式 修改
        map.put(AirSignal.DRY_MODE, H56C.AC_AirQuality_AC_DORMODESTS);
        //干燥除味，开关状态。修改是另外的信号
        map.put(AirSignal.DRY_MODE_SWITCH_STATE, H56C.AC_AirQuality_AC_ACDRYINGANDODORREMOVALSTS);
        //通风降温开关  修改

        map.put(AirSignal.VENTILATION_SWITCH_STATE, H56C.AC_AirQuality_AC_VENTILATIONCOOLSTS);

//        自动除霜开关  修改
//        map.put(AirSignal.AUTO_DEFROST_SWITCH_STATE, Climate.ID_AIR_CD_DEFROST_AUTO_MODE);
        //自动除霜等级  修改
//        map.put(AirSignal.AUTO_DEFROST_LEVEL, Signal.ID_AUTO_DEF_LEVEL);
        //auto开关
        map.put(AirSignal.AUTO_SWITCH_STATE, Climate.ID_AUTO_AIR_CD_MODE);
        //pm2.5
        map.put(AirSignal.AIR_PM, Climate.ID_IN_CABIN_PM25_LEVEL);
        //负离子
        map.put(AirSignal.AIR_ANION_SWITCH_STATE, Climate.ID_REQ_ION_MODE);
        //自动负离子
        map.put(AirSignal.AIR_AUTO_ANION_SWITCH_STATE, Climate.ID_AUTO_ION_MODE);
        //智能开关
        map.put(AirSignal.AIR_INTELLIGENT_IDENTIFICATION_SWITCH_STATE, H56C.AC_AirQuality_AC_BACKROWACAUTOSTA);
    }

    //    0x0: FACE
//0x1: FACE_AND_FOOT
//0x2: FOOT
//0x3: FOOT_AND_DEFROST
//0x4: DEFROST
//0x5:FACE_AND_DEFROST
//0x6:FACE_AND_FOOT_AND_DEFROST
    private static final Map<String, Integer> WINDMODE_STRING_INT_MAP = MapUtils.newLinkedHashMap(
            new Pair<>("to_face", 0),
            new Pair<>("to_foot", 2),
            new Pair<>("face_foot", 1),
            new Pair<>("to_window", 4),
            new Pair<>("window_foot", 3),
            new Pair<>("face_foot_window", 6),
            new Pair<>("face_window", 5)
    );
    private static final Map<Integer, String> WINDMODE_INT_STRING_MAP = MapUtils.newLinkedHashMap(
            new Pair<>(0, "to_face"),
            new Pair<>(1, "face_foot"),
            new Pair<>(2, "to_foot"),
            new Pair<>(4, "to_window"),
            new Pair<>(3, "window_foot"),
            new Pair<>(6, "face_foot_window"),
            new Pair<>(5, "face_window")
    );

    private static final Map<String, Integer> SCAVENGING_STRING_INT_MAP = MapUtils.newLinkedHashMap(
            new Pair<>("avoid_face", ParamsBlowModeH53.AIRNOVENT),
            new Pair<>("focus_face", ParamsBlowModeH53.AIRTOVENT),
            new Pair<>("auto_swing", ParamsBlowModeH53.AIRAUTO)

    );
    private static final Map<Integer, String> SCAVENGING_INT_STRING_MAP = MapUtils.newLinkedHashMap(
            new Pair<>(ParamsBlowModeH53.AIRNOVENT, "avoid_face"),
            new Pair<>(ParamsBlowModeH53.AIRTOVENT, "focus_face"),
            new Pair<>(ParamsBlowModeH53.AIRAUTO, "auto_swing")
    );


    private static final Map<Integer, String> AIR_MODE_INTEGER_STRING_MAP = MapUtils.newLinkedHashMap(
            new Pair<>(ParamsRecycleDoorMode.LOOP_AUTO, "auto_circulation"),
            new Pair<>(ParamsRecycleDoorMode.LOOP_INNER, "inner_circulation"),
            new Pair<>(ParamsRecycleDoorMode.LOOP_OUTSIDE, "outer_circulation")
    );
    private static final Map<String, Integer> AIR_MODE_STRING_INTEGER_MAP = MapUtils.newLinkedHashMap(
            new Pair<>("auto_circulation", ParamsRecycleDoorMode.LOOP_AUTO),
            new Pair<>("inner_circulation", ParamsCommon.ParamsRecycleDoorMode.LOOP_INNER),
            new Pair<>("outer_circulation", ParamsCommon.ParamsRecycleDoorMode.LOOP_OUTSIDE)
    );

    private static final Map<Integer, String> AIR_DRY_MODE_INTEGER_STRING_MAP = MapUtils.newLinkedHashMap(
            //todo
            new Pair<>(0, "standard"),
            new Pair<>(1, "strong")
    );
    private static final Map<String, Integer> AIR_DRY_MODE_STRING_INTEGER_MAP = MapUtils.newLinkedHashMap(
            new Pair<>("standard", 0),
            new Pair<>("strong", 1)
    );


    private static final Map<String, Integer> AIR_AUTO_DEFROST_STRING_INTEGER_MAP = MapUtils.newLinkedHashMap(
            new Pair<>("min", ParamsAutoDefLevelSts.AUTO_DEF_LEVEL_STS_LO),
            new Pair<>("low", ParamsAutoDefLevelSts.AUTO_DEF_LEVEL_STS_LO),
            new Pair<>("mid", ParamsAutoDefLevelSts.AUTO_DEF_LEVEL_STS_MID),
            new Pair<>("high", ParamsAutoDefLevelSts.AUTO_DEF_LEVEL_STS_HI),
            new Pair<>("max", ParamsAutoDefLevelSts.AUTO_DEF_LEVEL_STS_HI)
    );

    private static final Map<Integer, String> AIR_AUTO_DEFROST_INTEGER_STRING_MAP = MapUtils.newLinkedHashMap(
            new Pair<>(ParamsAutoDefLevelSts.AUTO_DEF_LEVEL_STS_LO, "low"),
            new Pair<>(ParamsAutoDefLevelSts.AUTO_DEF_LEVEL_STS_MID, "mid"),
            new Pair<>(ParamsAutoDefLevelSts.AUTO_DEF_LEVEL_STS_HI, "high")
    );

    @Override
    public int getBaseIntProp(String key, int area) {
        Integer curKey = map.get(key);
        Integer realPosition;
        int curValue;
        if (curKey == null) {
            switch (key) {
                case AirSignal.MAX_AIR_WIND:
                    curValue = ParamsFrontBlwLevel.LEVEL7;
                    break;
                case AirSignal.MIN_AIR_WIND:
                    curValue = ParamsFrontBlwLevel.LEVEL1;
                    break;
                default:
                    curValue = -1;
                    LogUtils.e(TAG, "空调里getBaseIntProp方法缺少对应key的处理" + key);
                    break;
            }
        } else {

            switch (key) {
                case AirSignal.AIR_WIND:
                    if (area == PositionSignal.FIRST_ROW_LEFT || area == PositionSignal.FIRST_ROW_RIGHT || area == PositionSignal.FIRST_ROW) {
                        realPosition = getRealArea(PositionSignal.FIRST_ROW);
                    } else {
                        realPosition = getRealArea(PositionSignal.REAR_ROW);
                    }
                    break;
                default:
                    realPosition = getRealArea(area);
                    break;
            }
            curValue = carPropHelper.getIntProp(curKey,
                    realPosition);
        }

        return curValue;
    }

    @Override
    public void setBaseIntProp(String key, int area, int value) {
        int realValue;
        Integer realPosition;
        switch (key) {
            case AirSignal.AIR_WIND:
                if (area == PositionSignal.FIRST_ROW_LEFT || area == PositionSignal.FIRST_ROW_RIGHT || area == PositionSignal.FIRST_ROW) {
                    realPosition = getRealArea(PositionSignal.FIRST_ROW);
                } else {
                    realPosition = getRealArea(PositionSignal.REAR_ROW);
                }
                realValue = (value + 1 > 7 ? 8 : value + 1);
                break;
            default:
                realPosition = getRealArea(area);
                realValue = value;
                break;
        }
        carPropHelper.setIntProp(map.get(key),
                realPosition, realValue);

    }

    @Override
    public float getBaseFloatProp(String key, int area) {
        Integer curKey = map.get(key);
        int realArea;
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
            switch (key) {
                case AirSignal.AIR_TEMP:
                    if (area == PositionSignal.FIRST_ROW_LEFT) {
                        //主驾、副驾、前排都是前排
                        realArea = getRealArea(PositionSignal.FIRST_ROW_LEFT);
                    } else if (area == PositionSignal.FIRST_ROW_RIGHT) {
                        realArea = getRealArea(PositionSignal.FIRST_ROW_RIGHT);
                    } else {
                        //else先都给全车
                        realArea = getRealArea(PositionSignal.REAR_ROW);
                    }
                    break;
                default:
                    realArea = getRealArea(area);
                    break;
            }
            curValue = carPropHelper.getFloatProp(curKey,
                    realArea);
        }
        return curValue;
    }

    @Override
    public void setBaseFloatProp(String key, int area, float value) {
        int realArea;
        switch (key) {
            case AirSignal.AIR_TEMP:
                if (area == PositionSignal.FIRST_ROW_LEFT) {
                    //主驾、副驾、前排都是前排
                    realArea = getRealArea(PositionSignal.FIRST_ROW_LEFT);
                } else if (area == PositionSignal.FIRST_ROW_RIGHT) {
                    realArea = getRealArea(PositionSignal.FIRST_ROW_RIGHT);
                } else {
                    //else先都给全车
                    realArea = getRealArea(PositionSignal.REAR_ROW);
                }
                break;
            default:
                realArea = getRealArea(area);
                break;
        }
        carPropHelper.setFloatProp(map.get(key), realArea,
                value);
    }

    @Override
    public String getBaseStringProp(String key, int area) {

        //是本地常量，不是通过接口获取的。所以map里也不需要映射。
        switch (key) {
            case AirSignal.MAX_AUTO_DEFROST_LEVEL:
                return AIR_AUTO_DEFROST_INTEGER_STRING_MAP.get(ParamsAutoDefLevelSts.AUTO_DEF_LEVEL_STS_HI);
            case AirSignal.MIN_AUTO_DEFROST_LEVEL:
                return AIR_AUTO_DEFROST_INTEGER_STRING_MAP.get(ParamsAutoDefLevelSts.AUTO_DEF_LEVEL_STS_LO);
            case AirSignal.WIND_MODE:
                if (area == PositionSignal.FIRST_ROW_LEFT || area == PositionSignal.FIRST_ROW_RIGHT || area == PositionSignal.FIRST_ROW) {
                    //主驾、副驾、前排都是前排
                    area = PositionSignal.FIRST_ROW;
                } else {
                    //else先都给全车
                    area = PositionSignal.REAR_ROW;
                }
                break;
        }

        if (key.equals(CommonSignal.COMMON_SEAT_PEOPLE_STATE)) {
//                String array = operator.getStringProp(CommonSignal.COMMON_SEAT_PEOPLE_STATE);
//                    String[] occupyStatusValue = array.split(",");
            String str = "";
            int rightSTS1 = carPropHelper.getIntProp(H56C.ACU_state_ACU_PASSENGERSTS);
            int leftSTS2 = carPropHelper.getIntProp(H56C.ACU_state_ACU_SECONDLEFTSTS);
            int rightSTS2 = carPropHelper.getIntProp(H56C.ACU_state_ACU_SECONDRIGHTSTS);
            int leftSTS3 = carPropHelper.getIntProp(H56C.BCM_state3_BCM_3NDLEFTSTS);
            int rightSTS3 = carPropHelper.getIntProp(H56C.BCM_state3_BCM_3NDRIGHTSTS);

            str += 1 + "," + rightSTS1 + "," + leftSTS2 + "," + rightSTS2 + "," + leftSTS3 + "," + rightSTS3;


            return str;
        }


        int curValue = carPropHelper.getIntProp(map.get(key),
                getRealArea(area));
        String res;
        switch (key) {
            case AirSignal.WIND_MODE:
                res = WINDMODE_INT_STRING_MAP.get(curValue);
                break;
            case AirSignal.SCAVENGING_WIND_MODE:
                res = SCAVENGING_INT_STRING_MAP.get(curValue);
                if (res == null) {
                    //因为向上吹脸会触发向上扫风。
                    res = SCAVENGING_INT_STRING_MAP.get(2);
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
                if (area == PositionSignal.FIRST_ROW_LEFT || area == PositionSignal.FIRST_ROW_RIGHT || area == PositionSignal.FIRST_ROW) {
                    //主驾、副驾、前排都是前排
                    curArea = getRealArea(PositionSignal.FIRST_ROW);
                } else {
                    //else先都给全车
                    curArea = getRealArea(PositionSignal.REAR_ROW);
                }
                curValue = WINDMODE_STRING_INT_MAP.get(value);
                break;
            case AirSignal.SCAVENGING_WIND_MODE:
                curValue = SCAVENGING_STRING_INT_MAP.get(value);
                break;
            case AirSignal.AIR_CYCLE_MODE:
                if (value.equals("inner_circulation")) {
                    curValue = 1;
                } else if (value.equals("outer_circulation")) {
                    curValue = 2;
                } else {
                    curValue = 3;
                }
//                0x1：内循环 0x2：外循环 0x3：自动循环
                curKey = H56C.IVI_BodySet3_IVI_HVACRECIRCMODESET;
                break;
            case AirSignal.DRY_MODE:
                curKey = H56C.IVI_ACHCMCtrl_IVI_ACDORMODE;
                curValue = AIR_DRY_MODE_STRING_INTEGER_MAP.get(value) + 1;
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
        LogUtils.d(TAG, "key是：" + key + " value:" + map.get(key) + " area:" + area);

        if (key.equals(CommonSignal.COMMON_CEIL_CONFIG)) {
            return super.getBaseBooleanProp(key, -1);
        }

        if (key == AirSignal.AIR_UI_STATE) {
            LogUtils.i(TAG, "当前空调界面状态：" + area);
            boolean res = hvacService.isCurrentState("ACTION_IS_AC_FOREGROUND" + (area == 0 ? DISPLAY_ID : DISPLAY_ID2));
            LogUtils.i(TAG, "空调的res：" + res);
            return res;
        }
        int relPosition = getRealArea(area);

        //对area进行处理合并
        switch (key) {
            case AirSignal.AIR_TEMP_SYN_STATE:
                //主驾、副驾、前排都是前排
                relPosition = VehicleArea.FRONT_ROW;
                break;
            case AirSignal.AIR_SWITCH_STATE:
            case AirSignal.AUTO_SWITCH_STATE:
                if (area == PositionSignal.FIRST_ROW_LEFT || area == PositionSignal.FIRST_ROW_RIGHT || area == PositionSignal.FIRST_ROW) {
                    //主驾、副驾、前排都是前排
                    relPosition = getRealArea(PositionSignal.FIRST_ROW);
                    LogUtils.i(TAG, "设置成前排" + relPosition);
                } else {
                    //else先都给全车
                    relPosition = getRealArea(PositionSignal.REAR_ROW);
                    LogUtils.i(TAG, "设置成后排" + relPosition);
                }
                break;
//            case AirSignal.AIR_CYCLE_AUTO_STATE:
//                area = PositionSignal.FIRST_ROW;
//                break;

//                if (area == PositionSignal.FIRST_ROW_LEFT || area == PositionSignal.FIRST_ROW_RIGHT || area == PositionSignal.FIRST_ROW) {
//                    //主驾、副驾、前排都是前排
//                    relPosition = PositionSignal.FIRST_ROW;
//                    LogUtils.i(TAG, "设置成前排" + area);
//                } else {
//                    //else先都给全车
//                    LogUtils.i(TAG, "设置成后排" + area);
//                    relPosition = PositionSignal.REAR_ROW;
//                }
//                break;
            default:
                break;

        }


        int curAirSwitchInt = carPropHelper.getIntProp(map.get(key),
                relPosition);
        boolean realRes;
        switch (key) {
            case AirSignal.AIR_TEMP_SYN_STATE:
                //温区同步0是开的，1是关的。跟文档上的信号对不上。
                realRes = (curAirSwitchInt == 0);
                break;
            case AirSignal.AIR_CYCLE_AUTO_STATE:
                realRes = (curAirSwitchInt == 2);
                break;
            default:
                //1是开的处理
                realRes = (curAirSwitchInt == 1);
                break;
        }
        //默认0是false，1是true
        return realRes;
    }

    @Override
    public void setBaseBooleanProp(String key, int area, boolean value) {

        if (key.equals(AirSignal.AIR_UI_STATE)) {
            if (value) {
                hvacService.exec("ACTION_OPEN_AC_PAGE" + (area == 0 ? DISPLAY_ID : DISPLAY_ID2));
            } else {
                hvacService.exec("ACTION_CLOSE_AC_PAGE" + (area == 0 ? DISPLAY_ID : DISPLAY_ID2));
            }
            return;
        }

        int realBoolean = -1;
        int realKey = map.get(key);
        int realPosition = getRealArea(area);
        switch (key) {
            case AirSignal.DRY_MODE_SWITCH_STATE:
                realKey = H56C.IVI_ACHCMCtrl_IVI_ACDRYINGANDODORREMOVAL;
                realBoolean = 1;
                break;
            case AirSignal.AIR_TEMP_SYN_STATE:
                realBoolean = 1;
                realPosition = getRealArea(PositionSignal.FIRST_ROW);
                break;
            case AirSignal.AUTO_SWITCH_STATE:
                if (area == PositionSignal.FIRST_ROW_LEFT || area == PositionSignal.FIRST_ROW_RIGHT || area == PositionSignal.FIRST_ROW) {
                    //主驾、副驾、前排都是前排
                    realPosition = getRealArea(PositionSignal.FIRST_ROW);
                } else {
                    //else先都给全车
                    realPosition = getRealArea(PositionSignal.REAR_ROW);
                }
                break;
            case AirSignal.AIR_SWITCH_STATE:
                //56C的车，开关设备都是一个信号。1表示设置信号。
                realBoolean = 1;
                //空调开关，当出现全车的时候需要根据当前车的情况进行适配。
                if (area == PositionSignal.ALL) {
                    realPosition = getRealArea(PositionSignal.FIRST_ROW);
                    carPropHelper.setIntProp(realKey,
                            realPosition, realBoolean);
                    realPosition = getRealArea(PositionSignal.REAR_ROW);
                } else if (area == PositionSignal.FIRST_ROW_LEFT || area == PositionSignal.FIRST_ROW_RIGHT || area == PositionSignal.FIRST_ROW) {
                    //主驾、副驾、前排都是前排
                    realPosition = getRealArea(PositionSignal.FIRST_ROW);
                } else {
                    //else先都给全车
                    realPosition = getRealArea(PositionSignal.REAR_ROW);
                }
                break;
            case AirSignal.REAR_DEFROST_SWITCH_STATE:
                realBoolean = value ? 2 : 1;
                break;
            case AirSignal.VENTILATION_SWITCH_STATE:
                realKey = H56C.IVI_ACHCMCtrl_IVI_VENTILATIONCOOLSET;
                realBoolean = 1;
                break;
            case AirSignal.AIR_INTELLIGENT_IDENTIFICATION_SWITCH_STATE:
                realKey = H56C.IVI_ACHCMCtrl_IVI_BACKROWACAUTOSET;
                realBoolean = value ? 1 : 2;
                break;
            case AirSignal.AIR_CYCLE_AUTO_STATE:
                //打开自动循环
                realKey = H56C.IVI_BodySet3_IVI_HVACRECIRCMODESET;
                if (value) {
                    realBoolean = 3;
                } else {
                    realBoolean = 1;
                }
                break;
            default:
                realKey = map.get(key);
                realBoolean = 1;
                LogUtils.d(TAG, "空调的调节：key是:" + key + " value:" + value + " setValue: " + realBoolean);
                break;
//                realBoolean = getRealBoolean(value);
        }
        LogUtils.d(TAG, "空调的调节：所有的参数，key" + map.get(key) + "value:" + value);
        carPropHelper.setIntProp(realKey,
                realPosition, realBoolean);
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

    public interface ParamsFrontBlwDirect {
        int NONE = 0;
        int FACE = 1;
        int FLOOR = 2;
        int FACE_FLOOR = 3;
        int DEFROST = 4;
        int FLOOR_DEFROST = 6;
        int ALL = 7;
        int FACE_DEFROST = 8;
    }

    public interface ParamsBlowModeH53 {
        int NOREQUEST = 0;
        int AIRMANUAL = 1;
        int AIRTOVENT = 2;
        int AIRNOVENT = 3;
        int AIRAUTO = 4;
        int AIROFF = 5;
    }

    public interface ParamsFrontBlwLevel {
        int OFF = 0;
        int LEVEL1 = 1;
        int LEVEL2 = 2;
        int LEVEL3 = 3;
        int LEVEL4 = 4;
        int LEVEL5 = 5;
        int LEVEL6 = 6;
        int LEVEL7 = 7;
        int LEVEL8 = 8;
    }

    public interface ParamsRecycleDoorMode {
        int LOOP_OUTSIDE = 0;
        int LOOP_INNER = 1;
        int LOOP_AUTO = 2;
        int LOOP_DEFAULT = 3;
        int LOOP_INVALID = 4;
    }

    public interface ParamsDryCleanMode {
        int DRY_CLEAN_MODE_DEFAULT = 0;
        int DRY_CLEAN_MODE_STANDRAD = 1;
        int DRY_CLEAN_MODE_DEEP = 2;
    }

    public interface ParamsAutoDefLevelSts {
        int AUTO_DEF_LEVEL_STS_DEFAULT = 0;
        int AUTO_DEF_LEVEL_STS_LO = 1;
        int AUTO_DEF_LEVEL_STS_MID = 2;
        int AUTO_DEF_LEVEL_STS_HI = 3;
        int AUTO_DEF_LEVEL_STS_INVALID = 255;
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
        boolean hasCeilScreen = getBooleanProp(CommonSignal.COMMON_CEIL_CONFIG);
        String screenValue;
        if (hasCeilScreen) {
            screenValue = "central_screen-passenger_screen-ceil_screen-armrest_screen";
        } else {
            screenValue = "central_screen-passenger_screen-armrest_screen";
        }

        //空调开关功能，可以当成空调个数。
        String airSwitchValue;
        String carType = CarServicePropUtils.getInstance().getCarType();
        if (carType.equals("H56C")) {
            airSwitchValue = "0-0-0-0-0-0-1-1-0";
        } else {
            airSwitchValue = "1";
        }
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_SWITCH_STATE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, airSwitchValue);

        //负离子功能
        //判断当前车型是
        String anionValue;
        int configurationType = CarServicePropUtils.getInstance().getCarModeConfig();
        if (configurationType == 2 || configurationType == 3) {
            anionValue = "-1";
        } else {
            anionValue = "1";
        }
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_ANION_SWITCH_STATE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, anionValue);
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_AUTO_ANION_SWITCH_STATE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, anionValue);


        String AirUIValue = "";
        //空调界面功能位判断
        if (hasCeilScreen) {
            AirUIValue = "central_screen-passenger_screen-ceil_screen-armrest_screen";
        } else {
            AirUIValue = "central_screen-passenger_screen-armrest_screen";
        }
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_UI_STATE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, AirUIValue);

        //智能识别开关功能位
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_INTELLIGENT_IDENTIFICATION_SWITCH_STATE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "1");

        //Auto开关功能位,前排，二排
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AUTO_SWITCH_STATE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "0-0-0-0-0-0-1-1-0");
        //自动循环开关
        String autoCycleValue;
        if (configurationType == 2) {
            autoCycleValue = "-1";
        } else {
            autoCycleValue = "1";
        }
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_CYCLE_AUTO_STATE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, autoCycleValue);

        String autoCycleValue2;
        autoCycleValue2 = "1";
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_CYCLE_AUTO_STATE2, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, autoCycleValue2);

        //通风降温功能位
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.VENTILATION_SWITCH_STATE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "1");
        //干燥除味功能位
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.DRY_MODE_SWITCH_STATE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "1");
        //PM2.5功能位
        String PMValue;
        if (configurationType == 2) {
            PMValue = "-1";
        } else {
            PMValue = "1";
        }
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_PM, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, PMValue);
        //扫风模式功能位
        String windModeValue;
        if (carType.equals("H56C")) {
            windModeValue = "-1";
        } else {
            windModeValue = "1";
        }
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.SCAVENGING_WIND_MODE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, windModeValue);

        //空调温度功能位的处理
        //主驾、副驾、二排是支持温度调节的
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_TEMP, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "1-1-0-0-0-0-0-1-0");
        //温区同步功能位
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_TEMP_SYN_STATE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "0-0-0-0-0-0-1-0-0");
        //吹风模式
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.WIND_MODE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "0-0-0-0-0-0-1-1");
        //todo AirWind 功能位添加，需要别的车型也要添加
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_WIND, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "0-0-0-0-0-0-1-1");
        //出风口开关功能是否支持的功能位
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_OUTLET_SWITCH, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "0-0-0-0-0-0-1-1-0");

        //============通用的功能位注册=========


        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(CommonSignal.COMMON_SCREEN_ENUMERATION, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL,
                screenValue);
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(CommonSignal.COMMON_SEAT_STATE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "1-1-1-1-1-1");


        //===特殊功能位判断56有的逻辑，但37A里没有===
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_SWITCH_STATE_SEAT_SINGLE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL,
                "1");


        String fastCooling;
        String fastHeater;

        fastCooling = "-1";
        fastHeater = "-1";

        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_FAST_COOLING_SWITCH_STATE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL,
                fastCooling);
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_FAST_HEATER_SWITCH_STATE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL,
                fastHeater);
        //AIR_FRONT_DEFROST_OPEN_RIGHT_TEMP_IS_ADJUST,前除霜打开后，是否可以支持副驾温度调节功能位。
        String isFrontDefrostOpenRightTempIsAdjust;

        isFrontDefrostOpenRightTempIsAdjust = "1";

        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_FRONT_DEFROST_OPEN_RIGHT_TEMP_IS_ADJUST, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL,
                isFrontDefrostOpenRightTempIsAdjust);
        //AUTO开关是否支持
        String autoSwitchIsSupport;

        autoSwitchIsSupport = "-1";

        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_AUTO_SWITCH_IS_SUPPORT, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL,
                autoSwitchIsSupport);
        //出风口开关功能是否存在。
        String airOutletIsSupport;

        airOutletIsSupport = "-1";

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
