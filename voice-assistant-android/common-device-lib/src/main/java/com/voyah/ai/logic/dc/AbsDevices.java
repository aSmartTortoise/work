package com.voyah.ai.logic.dc;
import android.util.Log2;

import com.voice.sdk.constant.ApplicationConstant;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.constants.GearInfo;
import com.voice.sdk.device.carservice.dc.carsetting.SettingConstants;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.carservice.vcar.IPropertyOperator;
import com.voice.sdk.device.func.FuncConstants;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voyah.ai.voice.platform.agent.api.flowchart.library.CommonMethod;
import com.voyah.ai.voice.platform.agent.api.flowchart.library.bean.FunctionalPositionBean;
import com.voyah.ai.voice.platform.agent.api.functionMatrix.FunctionDeviceMappingManager;
import com.voyah.ai.voice.platform.agent.api.helper.LocationHelper;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbsDevices extends BaseAbsDevices {
    private static final String TAG = "AbsDevices";

    protected static final String FINAL_TTS = "final_tts";
    public static final List<String> LOCATION_LIST = Arrays.asList("first_row_left", "first_row_right", "second_row_left", "second_row_right", "third_row_left", "third_row_right");
    //中控屏    副驾屏、滑移屏  娱乐屏、吸顶屏
    public static final List<String> SCREEN_LIST = Arrays.asList("central_screen", "passenger_screen", "scratch_screen", "entertainment_screen", "ceil_screen");

    public static final String SCREEN = "screen"; //屏幕(没有指定单一的屏幕)
    public static final List<String> FIRST_SCREEN = Arrays.asList("central_screen");
    public static final List<String> THIRD_SCREEN = Arrays.asList("entertainment_screen", "ceil_screen");
    public static final String ENTERTAINMENT_SCREEN = "entertainment_screen"; //娱乐屏(需要根据指定位置和声源位置来判断是副驾屏还是吸顶屏)

    private static final String[] specialTabName =
            new String[]{"hotspot_trust", "fcw", "aeb", "rcw", "meb", "lda", "elk", "esa", "lca", "fcta",
                    "rcta", "dow", "fvsr", "tlc", "bsa", "bsd", "tsr", "noa", "alco", "lcoc", "eol", "isa", "owa", "islc", "suspension_mode", "meter_card","trailerMode","powerSocket","faeb"};

    private static final String[] isNotSettingPage =
            new String[]{"order_service", "order_record"};

    public static final Map<String, String> SCREEN_STR = new HashMap() {
        {
            put("ceil_screen", "吸顶屏");
            put("central_screen", "中控屏");
            put("passenger_screen", "副驾屏");
            put("entertainment_screen", "娱乐屏");
            put("instrument_screen", "仪表屏");
            put("scratch_screen", "滑移屏");

        }
    };

    public static final Map<String, String> SOUND_LOCATION_STR = new HashMap() {
        {
            put("first_row_left", "主驾");
            put("first_row_right", "副驾");
            put("front_side", "前排");
            put("rear_side", "后排");
            put("rear_side_left", "左后");
            put("rear_side_right", "右后");
            put("left_side", "左侧");
            put("right_side", "右侧");
            put("second_side", "二排");
            put("second_row_right", "二排右");
            put("second_row_left", "二排左");
            put("third_side", "三排");
            put("third_row_left", "三排左");
            put("third_row_right(三排右)", "三排右");
        }
    };

    /**
     * CarService的封装
     */
    protected IPropertyOperator operator;
    protected String carType;


    //低代码里通用的方法，暂时可以提供给各个模块去使用，例如往某个上下问里去传数据
    protected CommonMethod commonMethod;

    public AbsDevices() {
        commonMethod = new CommonMethod();
        operator = DeviceHolder.INS().getDevices().getCarService().getOperatorDispatcher().getOperatorByDomain(getDomain());
        carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();
    }

    public abstract String getDomain();

    public boolean isGearsR(HashMap<String, Object> map) {
        return GearInfo.CARSET_GEAR_REVERSE == operator.getIntProp(CommonSignal.COMMON_GEAR_INFO);
    }

    public boolean isGearsD() {
        return GearInfo.CARSET_GEAR_DRIVING == operator.getIntProp(CommonSignal.COMMON_GEAR_INFO);
    }

    protected boolean isVCOS15() {
        return DeviceHolder.INS().getDevices().getCarServiceProp().isVCOS15();
    }

    //R挡限制
    public boolean isRestrictGearsR(HashMap<String, Object> map) {
        return isH56DCar(map) && DeviceHolder.INS().getDevices().getVoiceCarSignal().isParkingLimitation();
    }

    public String dsToString(String dsPosition) {
        String resPosition = "";
        switch (dsPosition) {
            case "front_side":
                resPosition = "前排";
                break;
            case "total_car":
                resPosition = "全车";
                break;
            default:
                Log2.e(TAG, "位置转换缺失，不应该出现这种情况，需要补充：" + dsPosition);
                break;
        }
        return resPosition;
    }

    /**
     * 根据指定屏幕和指定位置获取对应tts
     * 不支持回复使用
     *
     * @param screenName    指定屏幕
     * @param position      指定位置
     * @param soundLocation 声源位置
     * @return String
     */
    public String getScreenSaverReplaceStr(String screenName, String position, String soundLocation) {
        String str = "";
        if (StringUtils.isBlank(screenName)) {
            //指定屏幕为空
            if (StringUtils.isBlank(position)) {
                //没有指定屏幕和位置-根据声源位置
                str = SOUND_LOCATION_STR.get(soundLocation);
            } else if (!StringUtils.isBlank(position)) {
                //没有指定屏幕有指定位置-根据指定位置
                str = SOUND_LOCATION_STR.get(position);
            }
        } else {
            if (StringUtils.equals(screenName, SCREEN)) {
                //指定了屏幕，但是是非单一屏幕
                str = SOUND_LOCATION_STR.get(position);
            } else if (StringUtils.equals(screenName, ENTERTAINMENT_SCREEN)) {
                //指定屏幕且为娱乐屏
                if (!StringUtils.isBlank(position))
                    str = SOUND_LOCATION_STR.get(position);
                else
                    str = SOUND_LOCATION_STR.get(soundLocation);
            } else {
                str = SCREEN_STR.get(screenName);
            }
        }
        Log2.d(TAG, "getScreenSaverReplaceStr screenName:" + screenName + " ,soundLocation:" + soundLocation + " ,str:" + str);
        return str;
    }

    /**
     * @param key   功能位key
     * @param value 要判断是否支持的值
     * @return
     */
    public boolean judgeSupport(String key, String value) {
        FunctionalPositionBean bean = FunctionDeviceMappingManager.getInstance().getFunctionalPositionMap().get(key);
        if (bean == null || bean.getTypFunctionalMap() == null) {
            return false;
        }
        return bean.getTypFunctionalMap().containsKey(value);
    }

    public String getSoundLocation(HashMap<String, Object> map) {
        return LOCATION_LIST.get((int) getValueInContext(map, "soundLocation"));
    }

    public String getAssignScreen(HashMap<String, Object> map) {
        String screenName = map.containsKey("screen_name") ? (String) getValueInContext(map, "screen_name") : "";
        String position = map.containsKey("positions") ? (String) getValueInContext(map, "positions") : "";
        String soundLocation = getSoundLocation(map);
        String screen = SCREEN_LIST.get(0);
        if (StringUtils.isBlank(screenName) && StringUtils.isBlank(position)) {
            screen = soundLocation;
        } else if (StringUtils.isBlank(screenName)) {
            //指定屏幕为空
            if (StringUtils.isBlank(position)) {
                //没有指定屏幕和位置-根据声源位置
                screen = soundLocation;
            } else if (!StringUtils.isBlank(position)) {
                //没有指定屏幕有指定位置-根据指定位置
                screen = position;
            }
        } else {
            if (StringUtils.equals(screenName, SCREEN)) {
                //指定了屏幕，但是是非单一屏幕
                screen = position;
            } else if (StringUtils.equals(screenName, ENTERTAINMENT_SCREEN)) {
                //指定屏幕且为娱乐屏
                if (!StringUtils.isBlank(position))
                    screen = position;
                else
                    screen = soundLocation;
            } else {
                screen = screenName;
            }
        }
        String assignScreenName = assignScreenToFuncName(screen);
        Log2.d(TAG, "getAssignScreen screenName:" + screenName + " ,position:" + position + " ,soundLocation:" + soundLocation + " ,screen:" + screen + " ,assignScreenName:" + assignScreenName);
        return assignScreenName;
    }

    public String assignScreenToFuncName(String screen) {
        String assignScreenName = FuncConstants.VALUE_SCREEN_CENTRAL;
        if (LOCATION_LIST.contains(screen)) {
            if (StringUtils.equals(screen, LOCATION_LIST.get(0)))
                assignScreenName = FuncConstants.VALUE_SCREEN_CENTRAL;
            else if (StringUtils.equals(screen, LOCATION_LIST.get(1)))
                assignScreenName = FuncConstants.VALUE_SCREEN_PASSENGER;
            else
                assignScreenName = FuncConstants.VALUE_SCREEN_CEIL;
        } else {
            if (FIRST_SCREEN.contains(screen))
                assignScreenName = FuncConstants.VALUE_SCREEN_CENTRAL;
            else if (THIRD_SCREEN.contains(screen))
                assignScreenName = FuncConstants.VALUE_SCREEN_CEIL;
            else
                assignScreenName = FuncConstants.VALUE_SCREEN_PASSENGER;

        }
        return assignScreenName;
    }

    public boolean isBaseCeilScreen(HashMap<String, Object> map) {
        String screenName = getAssignScreen(map);
        boolean isCeilScreen = StringUtils.equals(assignScreenToFuncName(screenName), FuncConstants.VALUE_SCREEN_CEIL);
        Log2.d(TAG, "isCeilScreen:" + isCeilScreen);
        return isCeilScreen;
    }

    public boolean isBaseHaveCeilScreen() {
        boolean hasCeilScreen = DeviceHolder.INS().getDevices().getSystem().getScreen().isSupportScreen(DeviceScreenType.CEIL_SCREEN);
        Log2.d(TAG, "isHaveCeilScreen hasCeilScreen:" + hasCeilScreen);
        return hasCeilScreen;
    }

    public boolean functionalPositionIsSupport(HashMap<String, Object> map, String key) {

        Map<String, FunctionalPositionBean> functionalPositionBeanMap = FunctionDeviceMappingManager.getInstance().getFunctionalPositionMap();
        Log2.i(TAG, "当前功能位是否为null：" + ((functionalPositionBeanMap.size() == 0) ? "map是null的" : functionalPositionBeanMap.toString()));
        //没有注册表示没有这个功能，返回false
        if (!functionalPositionBeanMap.containsKey(key)) {
            Log2.i(TAG, "functionalPositionBeanMap not contains " + key + ", so return false");
            return false;
        }

        //拿到图中value里的数据内容。
        String value = (String) getValueInContext(map, FunctionalPositionBean.FUNCTIONAL_POSITION_VALUE);
        if (value != null && !value.isEmpty()) {

            //对value进行处理
            String resValue;
            if (value.contains("@")) {
                resValue = com.voyah.ai.voice.platform.agent.api.util.StringUtils.getDSValue(value);
            } else if (value.contains("$")) {
                resValue = value.substring(1);
            } else {
                resValue = value;
            }
            Log2.i(TAG, "value不为null:" + resValue);
            //

            FunctionalPositionBean functionalPositionBean = functionalPositionBeanMap.get(key);

            //比较resValue里的数据，跟当前key里的数据是否一致。
            String type = functionalPositionBean.getType();
            boolean res = false;
            switch (type) {
                case FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL:
                    Map<String, String> functionMap = functionalPositionBean.getTypFunctionalMap();
                    Log2.i(TAG, "当前功能位里的数据是：" + functionMap.toString());
                    if (functionMap.size() == 1) {
                        Set<String> set = functionMap.keySet();
                        Iterator<String> iterator = set.iterator();
                        String centerValue = iterator.next();
                        if (centerValue.equals("all")) {
                            return true;
                        }
                        if (centerValue.equals("-1")) {
                            return false;
                        }
                    }

                    if (value.contains("@")) {
                        String[] arr = value.split("@");
                        //拿到要么是指定位置的信息，要么是声源位置的信息。

                        if (arr[1].equals("position")) {
                            Set<Integer> positionSet = (Set<Integer>) map.get("position");
                            boolean result = false;
                            for (Integer position : positionSet) {
                                //只要有一个位置信息在注册的函数里是1则表示是命中的。
                                if (functionMap.get(position + "").equals("1")) {
                                    return true;
                                }
                            }
                        } else {
                            //如果是screen_name，但参数里没有screen_name的，需要根据位置信息去找一下结果。
                            if (arr[1].equals("screen_name") && !keyContextInMap(map, "screen_name")) {
                                //把位置信息转换成屏幕信息，会进行转换的是主驾屏幕，和副驾
                                Set<Integer> positionSet = (Set<Integer>) map.get("position");
                                Iterator<Integer> iterator = positionSet.iterator();
                                boolean isHit = false;
                                while (iterator.hasNext()) {
                                    Integer po = iterator.next();
                                    if (po == 0) {
                                        //主驾
                                        //central_screen;
                                        res = isSameString("central_screen", functionalPositionBean.getTypFunctionalMap());
                                        if (res) {
                                            isHit = true;
                                        }
                                    } else if (po == 1) {
                                        //副驾
                                        //passenger_screen
                                        res = isSameString("passenger_screen", functionalPositionBean.getTypFunctionalMap());
                                        if (res) {
                                            isHit = true;
                                        }
                                    } else {
                                        //暂时没有其他屏幕。
                                    }
                                }
                                return isHit;
                            }

                            //key,如果包含对应的值，返回true，否则返回false
                            String keyValue = (String) getValueInContext(map, arr[1]);
                            if (keyValue == null || keyValue.isEmpty()) {
                                //如果值是null也返回true,让客户端去处理。
                                return true;
                            }
                            res = isSameString(resValue, functionalPositionBean.getTypFunctionalMap());
                            //有想等的返回true.没想等的返回false
                            return res;
                        }

                    } else {
                        //字符串。
                        if (LocationHelper.isContainPositionString(resValue)) {
                            Log2.i(TAG, "是position类型的功能位");
                            Set<Integer> position = LocationHelper.getIntPosition(resValue);
                            for (Integer pos : position) {
                                if (functionMap.get(pos + "").equals("1")) {
                                    return true;
                                }
                            }
                        } else {
                            //如果是screen_name，但参数里没有screen_name的，需要根据位置信息去找一下结果。
                            Log2.i(TAG, "是String类型的功能位");
                            String[] ar = resValue.split("-");
                            boolean isScreen = isScreenName(ar[0]);
                            Log2.i(TAG, "是否是屏幕类型的功能位：" + ar[0] + " " + isScreen);
                            for (int i = 0; i < ar.length; i++) {
                                //是屏幕的单独处理
                                if (isScreen) {
                                    if (!keyContextInMap(map, "screen_name")) {
                                        //把位置信息转换成屏幕信息，会进行转换的是主驾屏幕，和副驾
                                        Set<Integer> positionSet = (Set<Integer>) map.get("position");
                                        Iterator<Integer> iterator = positionSet.iterator();
                                        boolean isHit = false;
                                        while (iterator.hasNext()) {
                                            Integer po = iterator.next();
                                            if (po == 0) {
                                                //主驾
                                                //central_screen;
                                                res = isSameString("central_screen", functionalPositionBean.getTypFunctionalMap());
                                                if (res) {
                                                    isHit = true;
                                                }
                                            } else if (po == 1) {
                                                //副驾
                                                //passenger_screen
                                                res = isSameString("passenger_screen", functionalPositionBean.getTypFunctionalMap());
                                                if (res) {
                                                    isHit = true;
                                                }
                                            } else {
                                                //暂时没有其他屏幕。默认为吸顶屏幕
                                                res = isSameString("ceil_screen", functionalPositionBean.getTypFunctionalMap());
                                                if (res) {
                                                    isHit = true;
                                                }
                                            }
                                        }
                                        return isHit;
                                    } else {
                                        res |= isSameString(resValue, functionalPositionBean.getTypFunctionalMap());
                                    }
                                } else {
                                    //不是屏幕的String方式处理
                                    res |= isSameString(resValue, functionalPositionBean.getTypFunctionalMap());
                                }
                            }


                            //有想等的返回true.没想等的返回false
                            return res;
                        }
                    }
                    break;
                case FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_NUMBER:
                    //根据传下来的数据，判断当前数据是什么类型。
                    if (resValue.contains("-")) {
                        String[] array = resValue.split("-");
                        //预留处理，目前没想到会有什么情况。
                    } else {
                        res = isSameNum(resValue, functionalPositionBean.getTypeNumberSet());
                    }

                    break;
                case FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_VALUE:
                    if (resValue.contains("-")) {
                        String[] array = resValue.split("-");
                        //预留处理，目前没想到会有什么情况。
                    } else {
                        res = isSameString(resValue, functionalPositionBean.getTypeValueSet());
                    }
                    break;
                default:
                    res = false;
                    break;
            }
            return res;
        } else {
            Log2.i(TAG, "value为null");
            //value为null的时候，

            FunctionalPositionBean functionalPositionBean = functionalPositionBeanMap.get(key);

            //比较resValue里的数据，跟当前key里的数据是否一致。
            String type = functionalPositionBean.getType();
            boolean res = false;
            switch (type) {
                case FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL:
                    Map<String, String> functionMap = functionalPositionBean.getTypFunctionalMap();

                    if (functionMap.size() == 1) {
                        Set<String> set = functionMap.keySet();
                        Iterator<String> iterator = set.iterator();
                        String resValue = iterator.next();
                        if (resValue.equals("all")) {
                            return true;
                        }
                        if (resValue.equals("1")) {
                            return true;
                        }
                        if (resValue.equals("-1")) {
                            return false;
                        }


                    }
                    //拿到要么是指定位置的信息，要么是声源位置的信息。
                    //case:后排的取值范围不确定，所以不是-1就给true.
//                    Set<Integer> positionSet = (Set<Integer>) map.get("position");
//                    boolean result = false;
//                    Log.i(TAG,"当前functionMap里的数据是：" + functionMap);
//                    for (Integer position : positionSet) {
//                        //只要有一个位置信息在注册的函数里是1则表示是命中的。
//                        Log.i(TAG,"position is :" + position);
//                        if (functionMap.get(position + "") != null && functionMap.get(position + "").equals("1")) {
//                            return true;
//                        }
//                    }
                    return true;
            }

            return false;

        }

    }

    /**
     * 判断app是否在栈顶
     *
     * @param map 对应的数据
     * @return true/false
     */
    public boolean getTopPage(HashMap<String, Object> map) {
        Log2.d(TAG, "getTopPage");
        // 以下页面打开，不涉及关闭页面的指令，可以通过当前设置提供的接口实现
        int pageState = openPage(map);
        if (pageState == 1) {
            return true;
        }
        return false;
    }

    public boolean isCurrentState(HashMap<String, Object> map) {
        String deviceName = getOneMapValue("device_name", map);
        String tabName = getOneMapValue("tab_name", map);
        String pageName = getOneMapValue("page_name", map);
        String switchType = getOneMapValue("switch_type", map);
        // todo
        if (!StringUtils.isEmpty(switchType) && switchType.equals("close")) {
            if (tabName == null || !Arrays.toString(specialTabName).contains(tabName)) {
                return DeviceHolder.INS().getDevices().getSystem().getApp().isAppForeGround("com.voyah.cockpit.vehiclesettings", 0);
            }
        }
        if (!StringUtils.isEmpty(deviceName)) {
            switch (deviceName) {
                case "bluetooth":
                    return mSettingHelper.isCurrentState(SettingConstants.BLUETOOTH);
                case "hotspot":
                    return mSettingHelper.isCurrentState(SettingConstants.HOTSPOT_PAGE);
                case "wifi":
                    return mSettingHelper.isCurrentState(SettingConstants.WLAN_PAGE);
                case "network":
                    return mSettingHelper.isCurrentState(SettingConstants.NETWORK_PAGE);
            }
        }
        if (!StringUtils.isEmpty(tabName)) {
            switch (tabName) {
                case "wallpaper":
                    return mSettingHelper.isCurrentState(SettingConstants.WALLPAPER);
                case "charging":
                case "super_charging":
                case "charging_capacity":
                    return mSettingHelper.isCurrentState(SettingConstants.CHARGE_PAGE);
                case "discharging":
                case "discharging_capacity":
                    return mSettingHelper.isCurrentState(SettingConstants.DISCHARGE_PAGE);
                case "journey_count":
                    return mSettingHelper.isCurrentState(SettingConstants.ENERGY_STATISTICS);
                case "driving_preference":
                    return mSettingHelper.isCurrentState(SettingConstants.DRIVE_PREFERENCE_PAGE);
                case "power_protection"://保电目标、行驶辅助
                    return mSettingHelper.isCurrentState(SettingConstants.DRIVER_POWERMODE);
                case "energyCenter":
                    return mSettingHelper.isCurrentState(SettingConstants.ENERGY_CENTER_PAGE);
                case "powerSocket"://电源插座
                    return mSettingHelper.isCurrentState(SettingConstants.NEW_POWER_SOCKET);
                case "hud":
                    return mSettingHelper.isCurrentState(SettingConstants.HUD_PAGE);
                case "instrument_screen":
                    return mSettingHelper.isCurrentState(SettingConstants.INSTRUMENT_SCREEN_PAGE);
                case "central_screen":
                    return mSettingHelper.isCurrentState(SettingConstants.CENTRAL_CONTROL_SCREEN_PAGE);
                case "intelligent_monitor":
                    return mSettingHelper.isCurrentState(SettingConstants.SMART_MONITORING_PAGE);
                case "trailerMode"://拖车模式
                    return mSettingHelper.isCurrentState(SettingConstants.NEW_TRAILER_MODE);
                case "intelligent_gesture":
                    return mSettingHelper.isCurrentState(SettingConstants.SMART_GESTURES_PAGE);
                case "door_window":
                    return mSettingHelper.isCurrentState(SettingConstants.DOOR_WINDOW_PAGE);
                case "sound_effects"://音效设置页面
                case "sound_field"://声场设置页面
                    return mSettingHelper.isCurrentState(SettingConstants.SOUND_EFFECTPAGE);
                case "car_lights":
                    return mSettingHelper.isCurrentState(SettingConstants.VEHICLE_LIGHT_PAGE);
                case "vehicle": //车辆设置页
                    return mSettingHelper.isCurrentState(SettingConstants.VEHICLE_PAGE);
                case "lab": //实验室
                    return mSettingHelper.isCurrentState(SettingConstants.LABORATORY_PAGE);
                case "maintenance"://车辆维护
                    return mSettingHelper.isCurrentState(SettingConstants.MAINTENANCE_CAR);
                case "lightness": //灯光设置
                    return mSettingHelper.isCurrentState(SettingConstants.LIGHT_PAGE);
                case "intelligent_recommend": //打开智能推荐设置
                    return mSettingHelper.isCurrentState(SettingConstants.SMART_RECOMMENDATION_PAGE);
                case "driving_assist": //驾驶辅助设置
                    return mSettingHelper.isCurrentState(SettingConstants.DRIVE_ASSIST_PAGE);
                case "display": //显示设置
                    return mSettingHelper.isCurrentState(SettingConstants.DISPLAY_PAGE);
                case "volume": //音量设置
                    return mSettingHelper.isCurrentState(SettingConstants.SOUND_VOLUME);
                case "sound"://声音设置
                    return mSettingHelper.isCurrentState(SettingConstants.SOUND_PAGE);
                case "voice": //语音设置
                    return mSettingHelper.isCurrentState(SettingConstants.VOICE_PAGE);
                case "connect": //连接设置
                    return mSettingHelper.isCurrentState(SettingConstants.LINK_PAGE);
                case "system":
                    return mSettingHelper.isCurrentState(SettingConstants.SYSTEM_PAGE);
                case "car_lock":
                    return mSettingHelper.isCurrentState(SettingConstants.VEHICLE_LOCK);
                case "skill_description_center":
                    return mSettingHelper.isCurrentState(SettingConstants.SKILL_CENTER);
                case "device_settings":
                    return mSettingHelper.isCurrentState(SettingConstants.DEVICE_SETTINGS);
                case "screen_saver":
                    return mSettingHelper.isCurrentState(SettingConstants.PASSENGER_SCREEN_PAGE);
                case "suspension_mode"://悬架
                    return mSettingHelper.isCurrentState(SettingConstants.DRIVER_PREFERENCE_PAGE_SETTINGS);
            }
        }
        return true;
    }

    /**
     * 打开对应界面
     *
     * @param map 对应的数据
     */
    public void setTopPage(HashMap<String, Object> map) {
        Log2.d(TAG, "openPage");
        openPage(map);
    }

    private int openPage(HashMap<String, Object> map) {
        int pageState = -1;
        String switchType = getOneMapValue("switch_type", map);
        String deviceName = getOneMapValue("device_name", map);
        String tabName = getOneMapValue("tab_name", map);
        String pageName = getOneMapValue("page_name", map);
        if (map.containsKey("switch_type")) {
            if (switchType.equals("close")) {
                if (tabName == null || !Arrays.toString(specialTabName).contains(tabName)) {
                    DeviceHolder.INS().getDevices().getSystem().getApp().closeApp(ApplicationConstant.PKG_SETTINGS, DeviceScreenType.CENTRAL_SCREEN);
                    return pageState;
                }
            }
        }
        if (!StringUtils.isEmpty(deviceName)) {
            switch (deviceName) {
                case "bluetooth":
                    mSettingHelper.exec(SettingConstants.BLUETOOTH);
                    break;
                case "hotspot":
                    mSettingHelper.exec(SettingConstants.HOTSPOT_PAGE);
                    break;
                case "wifi":
                    mSettingHelper.exec(SettingConstants.WLAN_PAGE);
                    break;
                case "network":
                    mSettingHelper.exec(SettingConstants.NETWORK_PAGE);
            }
            return pageState;
        } else if (!StringUtils.isEmpty(pageName) && tabName == null) {
            //打开设置
            mSettingHelper.exec(SettingConstants.SETTING_PAGE);
            return 0;
        }
        // 以下页面打开，不涉及关闭页面的指令，可以通过当前设置提供的接口实现
        if (!StringUtils.isEmpty(tabName)) {
            switch (tabName) {
                case "hotspot_trust":
                    mSettingHelper.exec(SettingConstants.HOTSPOT_PAGE);
                    break;
                case "device_name":
                    mSettingHelper.exec(SettingConstants.DEVICE_NAME);
                    break;
                case "system":
                    mSettingHelper.exec(SettingConstants.SYSTEM_PAGE);
                    break;
                case "flow":
                    mSettingHelper.exec(SettingConstants.TRAFFIC_QUERY);
                    break;
                case "system_upgrade":
                    mSettingHelper.exec(SettingConstants.SYS_UPGRADE);
                    break;
                case "system_version":
                case "voice_version":
                    mSettingHelper.exec(SettingConstants.VERSION_INFO);
                    break;
                case "restore_factory":
                    mSettingHelper.exec(SettingConstants.FACTORY_RESET);
                    break;
                case "privacy_clause":
                    mSettingHelper.exec(SettingConstants.PRIVACY_CLAUSE);
                    break;
                case "skill_description_center":
                    mSettingHelper.exec(SettingConstants.SKILL_DESCRIPTION_CENTER);
                    break;
                case "fcw":
                    mSettingHelper.exec(SettingConstants.ACTIVE_SAFETY_FCW);
                    break;
                case "faeb":
                case "aeb":
                    mSettingHelper.exec(SettingConstants.ACTIVE_SAFETY_AEB);
                    break;
                case "rcw":
                    mSettingHelper.exec(SettingConstants.ACTIVE_SAFETY_RCW);
                    break;
                case "meb":
                    mSettingHelper.exec(SettingConstants.ACTIVE_SAFETY_MEB);
                    break;
                case "lda":
                    mSettingHelper.exec(SettingConstants.ACTIVE_SAFETY_LDA);
                    break;
                case "elk":
                    mSettingHelper.exec(SettingConstants.ACTIVE_SAFETY_ELK);
                    break;
                case "esa":
                    mSettingHelper.exec(SettingConstants.ACTIVE_SAFETY_ESA);
                    break;
                case "lca":
                    mSettingHelper.exec(SettingConstants.BLIND_SPOT_ASSIST_LCA);
                    break;
                case "fcta":
                    mSettingHelper.exec(SettingConstants.BLIND_SPOT_ASSIST_FCTA);
                    break;
                case "rcta":
                    mSettingHelper.exec(SettingConstants.BLIND_SPOT_ASSIST_RCTA);
                    break;
                case "dow":
                case "dow_broadcast":
                    mSettingHelper.exec(SettingConstants.BLIND_SPOT_ASSIST_DOW);
                    break;
                case "fvsr":
                    mSettingHelper.exec(SettingConstants.SMART_DRIVE_FVSR);
                    break;
                case "tlc":
                    mSettingHelper.exec(SettingConstants.SMART_DRIVE_TLC);
                    break;
                case "driving_preference": //驾驶TAB
                    mSettingHelper.exec(SettingConstants.DRIVE_PREFERENCE_PAGE);
                    break;
                case "power_protection":
                    mSettingHelper.exec(SettingConstants.DRIVER_POWERMODE);
                    break;
                case "volume": //声音设置
                    mSettingHelper.exec(SettingConstants.SOUND_VOLUME);
                    break;
                case "sound"://声音设置
                    mSettingHelper.exec(SettingConstants.SOUND_PAGE);
                    break;
                case "voice": //语音设置
                    mSettingHelper.exec(SettingConstants.VOICE_PAGE);
                    break;
                case "connect": //连接设置
                    mSettingHelper.exec(SettingConstants.LINK_PAGE);
                    break;
                case "lab": //实验室
                    mSettingHelper.exec(SettingConstants.LABORATORY_PAGE);
                    break;
                case "maintenance"://车辆维护
                    mSettingHelper.exec(SettingConstants.MAINTENANCE_CAR);
                    break;
                case "vehicle": //车辆设置页
                    mSettingHelper.exec(SettingConstants.VEHICLE_PAGE);
                    break;
                case "display": //显示设置
                    mSettingHelper.exec(SettingConstants.DISPLAY_PAGE);
                    break;
                case "lightness": //灯光设置
                    mSettingHelper.exec(SettingConstants.LIGHT_PAGE);
                    break;
                case "door_window":
                    mSettingHelper.exec(SettingConstants.DOOR_WINDOW_PAGE);
                    break;
                case "bsa": // 盲区辅助
                    mSettingHelper.exec(SettingConstants.BLIND_SPOT_ASSIST_PAGE);
                    break;
                case "bsd": // 盲区监测预警
                    mSettingHelper.exec(SettingConstants.BLIND_SPOT_ASSIST_BSD);
                    break;
                case "tsr":
                    mSettingHelper.exec(SettingConstants.SMART_DRIVE_TSR);
                    break;
                case "noa":
                    mSettingHelper.exec(SettingConstants.SMART_DRIVE_NOA);
                    break;
                case "alco"://自动变道超车
                    mSettingHelper.exec(SettingConstants.SMART_DRIVEA_LCO);
                    break;
                case "lcoc"://变道前确认
                    mSettingHelper.exec(SettingConstants.SMART_DRIVEL_COL);
                    break;
                case "eol"://主动驶出超车道
                    mSettingHelper.exec(SettingConstants.SMART_DRIVE_EOL);
                    break;
                case "isa"://智能限速提醒 56上和限速控制共用设置项
                    if (DeviceHolder.INS().getDevices().getCarServiceProp().getCarType().contains("56")) {
                        mSettingHelper.exec(SettingConstants.SMART_DRIVE_ISLC);
                    } else {
                        mSettingHelper.exec(SettingConstants.SMART_DRIVE_ISA);
                    }
                    break;
                case "owa"://超速报警提示
                    mSettingHelper.exec(SettingConstants.SMART_DRIVE_OSA);
                    break;
                case "islc"://智能限速控制
                    mSettingHelper.exec(SettingConstants.SMART_DRIVE_ISLC);
                    break;
                case "intelligent_driving": //智能行车TAB
                    mSettingHelper.exec(SettingConstants.SMART_DRIVE_PAGE);
                    break;
                case "order_service":
                    operator.setIntProp(CommonSignal.COMMON_CAR_HEALTH, 0);
                    break;
                case "order_record":
                    operator.setIntProp(CommonSignal.COMMON_CAR_HEALTH, 1);
                    break;
                case "hud":
                    mSettingHelper.exec(SettingConstants.HUD_PAGE);
                    break;
                case "instrument_screen":
                    mSettingHelper.exec(SettingConstants.INSTRUMENT_SCREEN_PAGE);
                    break;
                case "central_screen":
                    mSettingHelper.exec(SettingConstants.CENTRAL_CONTROL_SCREEN_PAGE);
                    break;
                case "intelligent_monitor":
                    mSettingHelper.exec(SettingConstants.SMART_MONITORING_PAGE);
                    break;
                case "trailerMode"://拖车模式
                    mSettingHelper.exec(SettingConstants.NEW_TRAILER_MODE);
                    break;
                case "intelligent_gesture":
                    mSettingHelper.exec(SettingConstants.SMART_GESTURES_PAGE);
                    break;
                case "wallpaper":
                    mSettingHelper.exec(SettingConstants.WALLPAPER);
                    break;
                case "super_charging":
                case "charging":
                case "charging_capacity":
                case "charging_timer":
                    mSettingHelper.exec(SettingConstants.CHARGE_PAGE);
                    break;
                case "discharging":
                case "discharging_capacity":
                    mSettingHelper.exec(SettingConstants.DISCHARGE_PAGE);
                    break;
                case "journey_count":
                    mSettingHelper.exec(SettingConstants.ENERGY_STATISTICS);
                    break;
                case "custom_volume":
                    mSettingHelper.exec(SettingConstants.PERSONAL_VOICE);
                    break;
                case "energy_curve":
                    mSettingHelper.exec(SettingConstants.ENERGY_CONSUMPTION_CURVE);
                    break;
                case "energyCenter":
                    mSettingHelper.exec(SettingConstants.ENERGY_CENTER_PAGE);
                    break;
                case "powerSocket"://电源插座
                    mSettingHelper.exec(SettingConstants.NEW_POWER_SOCKET);
                    break;
                case "sound_effects"://音效设置页面
                case "sound_field"://声场设置页面
                    mSettingHelper.exec(SettingConstants.SOUND_EFFECTPAGE);
                    break;
                case "car_lights":
                    mSettingHelper.exec(SettingConstants.VEHICLE_LIGHT_PAGE);
                    break;
                case "intelligent_recommend": //打开智能推荐设置
                    mSettingHelper.exec(SettingConstants.SMART_RECOMMENDATION_PAGE);
                    break;
                case "driving_assist": //驾驶辅助设置
                    mSettingHelper.exec(SettingConstants.DRIVE_ASSIST_PAGE);
                    break;
                case "car_lock":
                    mSettingHelper.exec(SettingConstants.VEHICLE_LOCK);
                    break;
                case "suspension_mode"://悬架
                    mSettingHelper.exec(SettingConstants.DRIVER_PREFERENCE_PAGE_SETTINGS);
                    break;
                case "meter_card"://仪表卡片
                    mSettingHelper.exec(SettingConstants.DISPLAY_PAGE);
                    break;
                case "device_settings":
                    mSettingHelper.exec(SettingConstants.DEVICE_SETTINGS);
                    break;
                case "screen_saver":
                    mSettingHelper.exec(SettingConstants.PASSENGER_SCREEN_PAGE);
                    break;
                case "new_push":
                    mSettingHelper.exec(SettingConstants.NEW_ACTION_NEWPUSH);
                    break;
                default:
                    break;
            }
        }
        return pageState;
    }

    public void closeSettingApp(HashMap<String, Object> map) {
        DeviceHolder.INS().getDevices().getSystem().getApp().closeApp(ApplicationConstant.PKG_SETTINGS, DeviceScreenType.CENTRAL_SCREEN);
    }

    private boolean isOpenSettingPage(HashMap<String, Object> map, String tabName) {
        return !Arrays.toString(isNotSettingPage).contains(tabName);
    }

    /**
     * 判断当前字符串是否在设置范围内。
     *
     * @param resValue
     * @param typeValueMap
     * @return
     */
    private boolean isSameString(String resValue, Map<String, String> typeValueMap) {
        return typeValueMap.containsKey(resValue);
    }

    /**
     * 判断当前字符串是否在设置范围内。
     *
     * @param resValue
     * @param typeValueSet
     * @return
     */
    private boolean isSameString(String resValue, Set<String> typeValueSet) {
        //set集合的
        return typeValueSet.contains(resValue);
    }

    private Set<String> screenName = new HashSet() {
        {
            add("central_screen");
            add("scratch_screen");
            add("armrest_screen");
            add("ceil_screen");
            add("entertainment_screen");
            add("instrument_screen");
            add("passenger_screen");
        }
    };

    private boolean isScreenName(String s) {
        return screenName.contains(s);
    }

    /**
     * 判断当前number是 小数，整数，百分数，还是分数。
     *
     * @param num
     * @param numSet
     * @return
     */
    private boolean isSameNum(String num, Set<String> numSet) {
        //set集合的
        Iterator<String> iterator = numSet.iterator();
        while (iterator.hasNext()) {
            String value = iterator.next();
            //判断当前字符串是否是数字
            if (Character.isDigit(num.charAt(0))) {
                //是数字，判断是什么类型的数字
                boolean curRes = numStringType(numDigitType(num), value);
                if (curRes) {
                    return true;
                }
            } else {
                //不是数字，则直接进行比较
                boolean curRes = numStringType(num, value);
                if (curRes) {
                    return true;
                }
            }
        }
        return false;
    }

    private String numDigitType(String num) {
        String res;
        if (num.contains("%")) {
            res = FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_NUMBER_PERCENT;
        } else if (num.contains(".")) {
            res = FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_NUMBER_FLOAT;
        } else if (num.contains("/")) {
            res = FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_NUMBER_PERCENT2;
        } else {
            res = FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_NUMBER;
        }
        return res;
    }

    private boolean numStringType(String num, String numSet) {
        if (num.equals(numSet)) {
            return true;
        }
        return false;
    }

    /**
     * 当前屏幕个数是否大于1
     *
     * @return
     */
    public boolean getScreenSizeIsGreaterOne(HashMap<String, Object> map) {
        FunctionalPositionBean functionalPositionBean = FunctionDeviceMappingManager.getInstance().getFunctionalPositionMap().get(CommonSignal.COMMON_SCREEN_ENUMERATION);
        Map<String, String> curMap = functionalPositionBean.getTypFunctionalMap();
        return curMap.size() > 2;
    }
}


