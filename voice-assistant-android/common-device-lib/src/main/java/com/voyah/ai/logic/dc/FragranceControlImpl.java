package com.voyah.ai.logic.dc;


import android.text.TextUtils;
import android.util.Log2;

import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.dc.FragranceControlInterface;
import com.voice.sdk.device.carservice.dc.context.DCContext;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.carservice.signal.FragranceSignal;
import com.voice.sdk.device.func.FuncConstants;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voice.sdk.util.LogUtils;
import com.voice.sdk.util.Utils;
import com.voyah.ai.logic.util.ReflectUtils;
import com.voyah.ai.voice.platform.agent.api.flowchart.library.bean.FunctionalPositionBean;
import com.voyah.ai.voice.platform.agent.api.functionMatrix.FunctionDeviceMappingManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FragranceControlImpl extends AbsDevices implements FragranceControlInterface {
    private static final String TAG = "FragranceControlImpl";

    private static final String ACTION_OPEN_FRAG_DIALOG = "ACTION_OPEN_FRAG_DIALOG";

    private static final String ACTION_DISMISS_FRAG_DIALOG = "ACTION_DISMISS_FRAG_DIALOG";


    private boolean isDebug = DCContext.IS_DEBUG_GRAPH;

    @Override
    public String getDomain() {
        return "fragrance";
    }


    public FragranceControlImpl() {

    }


    //==========香氛相关========

    private static final Map<String, Integer> AIR_FRAGRANCE_STRING_INTEGER_MAP = new HashMap() {
        {
            put("无香氛", 0);
            put("海静", 1);
            put("木影", 2);
            put("竹境", 3);
            put("风煦", 4);
            put("花韵", 5);
            put("茶悠", 6);
            put("无效香氛", 7);
        }
    };


    /**
     * 判断当前DS香氛是否在槽位里。
     *
     * @param map
     * @return
     */
    public boolean fragranceIsInSlot(HashMap<String, Object> map) {
        String dsFragranceMode = (String) getValueInContext(map, "switch_mode");
        String[] slotFrance = getFragranceSlotType();
        for (int i = 0; i < slotFrance.length; i++) {
            if (dsFragranceMode.equals(slotFrance[i])) {
                return true;
            }
        }
        return false;
    }

    public int curFragranceSize(HashMap<String, Object> map) {
        String[] slotFrance = getFragranceSlotType();
        Set<String> hashSet = new HashSet<>();
        for (int i = 0; i < slotFrance.length; i++) {
            hashSet.add(slotFrance[i]);
        }
        if (hashSet.contains("无香氛")) {
            hashSet.remove("无香氛");
        }
        if (hashSet.contains("无效香氛")) {
            hashSet.remove("无效香氛");
        }
        return hashSet.size();
    }

    /**
     * [海静,木影,竹境]
     * 把String根据规则切割成数组。
     *
     * @param str
     * @return
     */
    private String[] stringToArray(String str) {
        String st2 = str.substring(1, str.length() - 1);
        System.out.println(st2);
        String[] array = st2.split(",");
        return array;
    }

    private String[] getFragranceSlotType() {
        String slotString = operator.getStringProp(FragranceSignal.FRAGRANCE_SLOT_TYPE);
        String[] slotArray = stringToArray(slotString);
        return slotArray;
    }


    public boolean hasPiracyFragrance(HashMap<String, Object> map) {
        String[] slotFrance = getFragranceSlotType();


        Set<String> hashSet = new HashSet<>();
        boolean notHasFragrance = false;
        for (int i = 0; i < slotFrance.length; i++) {
            if (!slotFrance[i].equals("无香氛")) {
                notHasFragrance = true;
            }
            hashSet.add(slotFrance[i]);
        }
//        if (hashSet.contains("无香氛")) {
//            hashSet.remove("无香氛");
//        }
//        if (hashSet.contains("无效香氛")) {
//            hashSet.remove("无效香氛");
//        }

        return hashSet.contains("无效香氛") || !notHasFragrance;

    }

    public void toast(HashMap<String, Object> map) {
        String text = (String) getValueInContext(map, "toastText");
        LogUtils.d(TAG, "toast text:" + text);

        //判断屏幕
        String originalScreen = FuncConstants.VALUE_SCREEN_PASSENGER;
        List<Integer> list = getMethodPosition(map);
        Set<String> screenList = mergePosition(list);
        Iterator<String> iterator = screenList.iterator();
        while (iterator.hasNext()) {
            String screenName = iterator.next();
            DeviceHolder.INS().getDevices().getSystem().getUi().showSystemToast(DeviceScreenType.fromValue(screenName), text);
        }

//        ThreadUtils.runOnUiThread(() -> {
//            if (text != null) {
//                LogUtils.d(TAG, "toast text:" + text);
//                Objects.requireNonNull(VcosToastManager.Companion.getInstance()).showToast(com.blankj.utilcode.util.Utils.getApp(), text);
//            }
//
//        });
    }

    /**
     * 根据list获取到屏幕的list
     *
     * @param list
     * @return
     */
    private Set<String> mergePosition(List<Integer> list) {
        Set<String> screenList = new HashSet<>();
        FunctionalPositionBean functionalPositionBean = FunctionDeviceMappingManager.getInstance().getFunctionalPositionMap().get(CommonSignal.COMMON_SCREEN_ENUMERATION);
        Map<String, String> curMap = functionalPositionBean.getTypFunctionalMap();
        if(curMap.size() == 2){
            screenList.add(FuncConstants.VALUE_SCREEN_CENTRAL);
            return screenList;
        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == 0) {
                screenList.add(FuncConstants.VALUE_SCREEN_CENTRAL);
            } else if (list.get(i) == 1) {
                screenList.add(FuncConstants.VALUE_SCREEN_PASSENGER);
            }
        }
        return screenList;
    }


    private boolean curFragranceState = true;

    public boolean getFragranceSwitchState(HashMap<String, Object> map) {
        if (isDebug) {
            Log2.i(TAG, "当前香氛开关状态：" + curFragranceState);
            return curFragranceState;
        }
//        int curValue = carPropHelper.getIntProp(Comforts.ID_FRAGRANCE_MODE);
//        Log.i(TAG, "当前香氛开关状态：" + (curValue == ParamsCommon.OnOffInvalid.ON));
//        return curValue == ParamsCommon.OnOffInvalid.ON;
        return operator.getBooleanProp(FragranceSignal.FRAGRANCE_SWITCH_STATE);
    }

    public void setFragranceSwitchState(HashMap<String, Object> map) {
        boolean isOpen = ((String) map.get("method_params_value")).equals("open");
        if (isDebug) {
            Log2.i(TAG, "设置香氛开关为：" + isOpen);
            curFragranceState = isOpen;
            return;
        }
        Log2.i(TAG, "设置香氛开关为：" + isOpen);
//        carPropHelper.setIntProp(Comforts.ID_FRAGRANCE_MODE,
//                isOpen ? ParamsCommon.OnOffInvalid.ON : ParamsCommon.OnOffInvalid.OFF);
        operator.setBooleanProp(FragranceSignal.FRAGRANCE_SWITCH_STATE, isOpen);
    }


    private String curFragranceMode = "water";

    public boolean judgeDsModeIsInCurrentMode(HashMap<String, Object> map) {
        //当前槽位有的香氛。
        String[] slotFrance = getFragranceSlotType();
        LogUtils.d(TAG, "当前卡槽里的香氛：" + slotFrance.toString());
        String dsMode = (String) getValueInContext(map, "switch_mode");
        for (int i = 0; i < slotFrance.length; i++) {
            if (slotFrance[i].equals(dsMode) && !dsMode.equals("无效香氛") && !dsMode.equals("无香氛")) {
                return true;
            }
        }
        return false;
    }

    public String getFragranceMode(HashMap<String, Object> map) {
//        int curValue = carPropHelper.getIntProp(Comforts.ID_FRAGRANCE_TAT);
//        Log.i(TAG, "当前香氛获取到的index数：" + curValue);
//        String[] slotFrance = getFragranceSlotType();
//        Log.i(TAG, "当前香氛模式为：" + slotFrance[curValue - 1]);
//        return slotFrance[curValue - 1];

        return operator.getStringProp(FragranceSignal.FRAGRANCE_MODE);
    }

    public void setFragranceMode(HashMap<String, Object> map) {
        String switchType = (String) map.get("switch_type");
        int curMode = 0;
        String mode = "";
        String[] slotFrance = getFragranceSlotType();
        if (switchType.equals("change")) {
            //slot index 1,2,3
//            int index = carPropHelper.getIntProp(Comforts.ID_FRAGRANCE_TAT);
            String curFragranceMode = operator.getStringProp(FragranceSignal.FRAGRANCE_MODE);
            String[] fragranceModes = getFragranceSlotType();


            Log2.i(TAG, "当前香氛：" + curFragranceMode);
            //获取当前的index
            int index1 = 0;
            for (int i = 0; i < fragranceModes.length; i++) {
                if (fragranceModes[i].equals(curFragranceMode)) {
                    index1 = i;
                }
            }
            Log2.i(TAG, "当前香氛index：" + index1);
            int count = 3;
            int i = index1;
            while (count > 0) {
                i = ((i + 1) % 3 == 0) ? 0 : (i + 1) % 3;
                if (!curFragranceMode.equals(fragranceModes[i]) && !fragranceModes[i].equals("无香氛")) {
                    curMode = i;
                    break;
                }
                count--;
            }
//            curMode = ((index + 1) % 4 == 0) ? 1 : (index + 1) % 4;
            Log2.i(TAG, "当前香氛要设置的index为：" + curMode);
            //0~7
//            String[] slotFrance = getFragranceSlotType();

            map.put(DCContext.TTS_PLACEHOLDER, slotFrance[curMode]);
            Log2.i(TAG, "设置香氛模式为：" + slotFrance[curMode]);
            mode = slotFrance[curMode];
        } else {
            String switchMode = (String) map.get("method_params_value");
            map.put(DCContext.TTS_PLACEHOLDER, switchMode);
            Log2.i(TAG, "设置香氛模式为：" + switchMode);
//            String[] slotFrance = getFragranceSlotType();
            for (int i = 0; i < slotFrance.length; i++) {
                if (slotFrance[i].equals(switchMode)) {
                    curMode = i;
                    Log2.i(TAG, "当前香氛要设置的index为：" + curMode);
                    mode = slotFrance[curMode];
                    break;
                }
            }
        }
//        carPropHelper.setIntProp(Comforts.ID_FRAGRANCE_TAT, curMode);
        LogUtils.d(TAG, "设置的香氛是：" + mode);
        operator.setStringProp(FragranceSignal.FRAGRANCE_MODE, mode);
    }

    private static final Map<String, String> AIR_FRAGRANCE_CONCENTRATION_STRING_STRING_MAP = new HashMap() {
        {
            put("min", "最低");
            put("low", "低");
            put("mid", "中");
            put("high", "高");
            put("max", "最高");
        }
    };


    private static final Map<String, String> AIR_FRAGRANCE_CONCENTRATION_STRING_STRING_TTS_MAP = new HashMap() {
        {
            put("min", "淡雅");
            put("low", "淡雅");
            put("mid", "适中");
            put("high", "浓郁");
            put("max", "浓郁");
        }
    };


    private static final Map<String, String> AIR_FRAGRANCE_CONCENTRATION_STRING_STRING_SAME_MAP = new HashMap() {
        {
            put("min", "low");
            put("low", "low");
            put("mid", "mid");
            put("high", "high");
            put("max", "high");
        }
    };


    private static final Map<Integer, String> AIR_FRAGRANCE_CONCENTRATION_INDEX_INTEGER_STRING_MAP = new HashMap() {
        {
            put(0, "min");
            put(1, "low");
            put(2, "mid");
            put(3, "high");
            put(4, "max");
        }
    };


    private static final Map<String, Integer> AIR_FRAGRANCE_CONCENTRATION_INDEX_STRING_INTEGER_MAP = new HashMap() {
        {
            put("min", 0);
            put("low", 1);
            put("mid", 2);
            put("high", 3);
            put("max", 4);
        }
    };


    private static final Map<String, String> AIR_FRAGRANCE_CONCENTRATION_MODE_STRING_String_MAP = new HashMap() {
        {
            put("soft", "low");
            put("standard", "mid");
            put("strong", "high");
        }
    };


//    private static final Map<String, Integer> AIR_FRAGRANCE_CONCENTRATION_STRING_INTEGER_MAP = MapUtils.newLinkedHashMap(
//            put("min", 0),
//            put("low", Comforts.FragranceCct.FCMCCTREQ_LO),
//            put("mid", Comforts.FragranceCct.FCMCCTREQ_MID),
//            put("high", Comforts.FragranceCct.FCMCCTREQ_HI),
//            put("max", Comforts.FragranceCct.FCMCCTREQ_HI)
//    );
//    private static final Map<Integer, String> AIR_FRAGRANCE_CONCENTRATION_INTEGER_STRING_MAP = MapUtils.newLinkedHashMap(
//            put(Comforts.FragranceCct.FCMCCTREQ_LO, "low"),
//            put(Comforts.FragranceCct.FCMCCTREQ_MID, "mid"),
//            put(Comforts.FragranceCct.FCMCCTREQ_HI, "high")
//    );
//
//    private static final Map<Integer, String> AIR_FRAGRANCE_CONCENTRATION_MODE_INTEGER_STRING_MAP = MapUtils.newLinkedHashMap(
//            put(Comforts.FragranceCct.FCMCCTREQ_LO, "soft"),
//            put(Comforts.FragranceCct.FCMCCTREQ_MID, "standard"),
//            put(Comforts.FragranceCct.FCMCCTREQ_HI, "strong")
//    );
//    private static final Map<String, Integer> AIR_FRAGRANCE_CONCENTRATION_MODE_STRING_INTEGER_MAP = MapUtils.newLinkedHashMap(
//            put("soft", Comforts.FragranceCct.FCMCCTREQ_LO),
//            put("standard", Comforts.FragranceCct.FCMCCTREQ_MID),
//            put("strong", Comforts.FragranceCct.FCMCCTREQ_HI)
//    );

    private static final Map<String, String> AIR_FRAGRANCE_CONCENTRATION_MODE_STRING_STRING_MAP2 = new HashMap() {
        {
            put("low", "soft");
            put("mid", "standard");
            put("high", "strong");
        }
    };


    private static final Map<String, String> AIR_FRAGRANCE_CONCENTRATION_MODE_STRING_STRING_MAP = new HashMap() {
        {
            put("soft", "淡雅");
            put("standard", "适中");
            put("strong", "浓郁");
        }
    };


    private static final Map<String, String> AIR_FRAGRANCE_CONCENTRATION_MODE_STRING_STRING_MAP_REVERSAL = new HashMap() {
        {
            put("淡雅", "soft");
            put("适中", "standard");
            put("浓郁", "strong");
        }
    };


    public String getFragranceConcentrationLevel(HashMap<String, Object> map) {


//        int curValue = (carPropHelper.getIntProp(Comforts.ID_FRAGRANCE_CCT) == -1) ? Comforts.FragranceCct.FCMCCTREQ_LO : carPropHelper.getIntProp(Comforts.ID_FRAGRANCE_CCT);
//        Log.i(TAG, "当前香氛浓度为：" + curValue + " " + AIR_FRAGRANCE_CONCENTRATION_INTEGER_STRING_MAP.get(curValue));
//        return AIR_FRAGRANCE_CONCENTRATION_INTEGER_STRING_MAP.get(curValue);
        return operator.getStringProp(FragranceSignal.FRAGRANCE_LEVEL);
    }

    public void setFragranceConcentrationLevel(HashMap<String, Object> map) {
        String adjustType = (String) map.get("adjust_type");
        String level = (String) map.get("level");
        String curLeve = getFragranceConcentrationLevel(map);//ds的low等级

        String res = "";
        if (adjustType.equals("set")) {
//            int carLevel = AIR_FRAGRANCE_CONCENTRATION_STRING_INTEGER_MAP.get(level);
//            carPropHelper.setIntProp(Comforts.ID_FRAGRANCE_CCT, carLevel);
            operator.setStringProp(FragranceSignal.FRAGRANCE_LEVEL, AIR_FRAGRANCE_CONCENTRATION_STRING_STRING_SAME_MAP.get(level));
            res = level;
        } else if (adjustType.equals("increase")) {
//            int curLevelIndex = AIR_FRAGRANCE_CONCENTRATION_INDEX_STRING_INTEGER_MAP.get(curLeve);
//            int addLevelIndex = Math.min(curLevelIndex + 1, AIR_FRAGRANCE_CONCENTRATION_STRING_INTEGER_MAP.size());
//            String addLevel = AIR_FRAGRANCE_CONCENTRATION_INDEX_INTEGER_STRING_MAP.get(addLevelIndex);
//            //转换成对应的CarService信号
//            int carLevel = AIR_FRAGRANCE_CONCENTRATION_STRING_INTEGER_MAP.get(addLevel);
//            res = addLevel;
//            carPropHelper.setIntProp(Comforts.ID_FRAGRANCE_CCT, carLevel);


            int curLevelIndex = AIR_FRAGRANCE_CONCENTRATION_INDEX_STRING_INTEGER_MAP.get(curLeve);
            int addLevelIndex = Math.min(curLevelIndex + 1, AIR_FRAGRANCE_CONCENTRATION_INDEX_STRING_INTEGER_MAP.size());
            String addLevel = AIR_FRAGRANCE_CONCENTRATION_INDEX_INTEGER_STRING_MAP.get(addLevelIndex);

            operator.setStringProp(FragranceSignal.FRAGRANCE_LEVEL, addLevel);
        } else if (adjustType.equals("decrease")) {
//            int curLevelIndex = AIR_FRAGRANCE_CONCENTRATION_INDEX_STRING_INTEGER_MAP.get(curLeve);
//            int addLevelIndex = Math.max(curLevelIndex - 1, 0);
//            String addLevel = AIR_FRAGRANCE_CONCENTRATION_INDEX_INTEGER_STRING_MAP.get(addLevelIndex);
//            //转换成对应的CarService信号
//            int carLevel = AIR_FRAGRANCE_CONCENTRATION_STRING_INTEGER_MAP.get(addLevel);
//            res = addLevel;
//            carPropHelper.setIntProp(Comforts.ID_FRAGRANCE_CCT, carLevel);


            int curLevelIndex = AIR_FRAGRANCE_CONCENTRATION_INDEX_STRING_INTEGER_MAP.get(curLeve);
            int addLevelIndex = Math.max(curLevelIndex - 1, 0);
            String addLevel = AIR_FRAGRANCE_CONCENTRATION_INDEX_INTEGER_STRING_MAP.get(addLevelIndex);
            operator.setStringProp(FragranceSignal.FRAGRANCE_LEVEL, addLevel);
        } else {
            res = "setAutoDefrostLevel 里不存在这种情况adjust_type：" + adjustType;
            Log2.e(TAG, res);
        }
        map.put(DCContext.TTS_PLACEHOLDER, res);
        Log2.i(TAG, "设置香氛浓度为：" + res);
        return;
    }


    public String getFragranceConcentrationMode(HashMap<String, Object> map) {
//        int curValue = (carPropHelper.getIntProp(Comforts.ID_FRAGRANCE_CCT) == -1) ? Comforts.FragranceCct.FCMCCTREQ_LO : carPropHelper.getIntProp(Comforts.ID_FRAGRANCE_CCT);
//        Log.i(TAG, "当前香氛浓度为：" + curValue + " " + AIR_FRAGRANCE_CONCENTRATION_MODE_INTEGER_STRING_MAP.get(curValue));
//        return AIR_FRAGRANCE_CONCENTRATION_MODE_INTEGER_STRING_MAP.get(curValue);

        return AIR_FRAGRANCE_CONCENTRATION_MODE_STRING_STRING_MAP2.get(operator.getStringProp(FragranceSignal.FRAGRANCE_LEVEL));
    }

    public void setFragranceConcentrationMode(HashMap<String, Object> map) {
//        String adjustType = (String) map.get("adjust_type");
        String level = (String) map.get("adjust_mode");
//        String curLeve = getFragranceConcentrationLevel(map);//ds的low等级

//        String res = "";
        String carLevel = AIR_FRAGRANCE_CONCENTRATION_MODE_STRING_String_MAP.get(level);
//        carPropHelper.setIntProp(Comforts.ID_FRAGRANCE_CCT, carLevel);
//        res = level;


        Log2.i(TAG, "设置的香氛浓度为：" + level + " 映射的类型：" + carLevel);
        operator.setStringProp(FragranceSignal.FRAGRANCE_LEVEL, carLevel);

        String res = level;

        map.put(DCContext.TTS_PLACEHOLDER, res);
        Log2.i(TAG, "设置香氛浓度为：" + res);
    }

    //=======调节香氛时间====
    private int curFragranceTime = 30;

    public void setFragranceTime(HashMap<String, Object> map) {
        String time = (String) map.get("method_params_value");


//        if (time.equals("60000") || Integer.valueOf(time) > 3600) {
//            Log.i(TAG, "香氛时长设置为：无限");
//            carPropHelper.setIntProp(Signal.ID_FCMRUNTIMEREQ, 254);
//        } else {
//            int times = Integer.valueOf(time) / 60;
//            Log.i(TAG, "香氛时长设置为：" + times + "分钟");
//            carPropHelper.setIntProp(Signal.ID_FCMRUNTIMEREQ, times);
//        }

        if (time.equals("60000") || Integer.valueOf(time) > 3600) {
            Log2.i(TAG, "香氛时长设置为：无限");
            operator.setIntProp(FragranceSignal.FRAGRANCE_TIME, 60000);

        } else {
            int times = Integer.valueOf(time) / 60;
            Log2.i(TAG, "香氛时长设置为：" + times + "分钟");
            if (times == 30) {

                operator.setIntProp(FragranceSignal.FRAGRANCE_TIME, Integer.valueOf(30));
            } else {

                operator.setIntProp(FragranceSignal.FRAGRANCE_TIME, Integer.valueOf(60));
            }

        }
    }


    private String getmethodNameToString(String eqMethodName, HashMap<String, Object> map) {
        if (Utils.isNumeric(eqMethodName.charAt(0) + "")) {
            return eqMethodName;
        } else {
            return (String) ReflectUtils.executeMethod(this, eqMethodName, map);
        }
    }


    //=======香氛界面开关====

    public boolean getScreenIsSupportFragranceUi(HashMap<String, Object> map) {
        List<Integer> positions = getMethodPosition(map);
        String nluScreen = (String) map.get("screen_name");
        //中控屏、主驾屏、副驾屏、前排娱乐屏、副驾娱乐屏
        if (nluScreen.equals("central_screen") || nluScreen.equals("passenger_screen")) {
            //中控屏幕、副驾屏幕可以通过一个函数拿到当前的屏幕
            return true;
        }
        //判断位置信息里是否包含0，1
        boolean isHitPosition = false;
        for (int i = 0; i < positions.size(); i++) {
            if (positions.get(i) == 0 || positions.get(i) == 1) {
                isHitPosition = true;
                break;
            }
        }

        if (nluScreen.equals("screen") && isHitPosition) {
            //副驾屏、主驾屏幕、前排娱乐屏、副驾娱乐屏需要根据位置信息和屏幕 或者只根据位置信息去判断当前是否能执行。
            return true;
        }
        return false;
    }

    public boolean getFragranceUiSwitchState(HashMap<String, Object> map) {
        //判断当前有哪几个屏幕
        Set<Integer> set = getScreen(map);
        Iterator<Integer> iterator = set.iterator();
        boolean res = true;
        while (iterator.hasNext()) {
            Integer position = iterator.next();
            if (position == 0) {
                res &= operator.getBooleanProp(FragranceSignal.FRAGRANCE_UI, position);
            } else {
                res &= operator.getBooleanProp(FragranceSignal.FRAGRANCE_UI, position);
            }
        }
//        boolean curFragrancePageState = HvacServiceImpl.getInstance(mContext).isCurrentState("ACTION_IS_FRAG_DIALOG_SHOWING");
        LogUtils.i(TAG, "香氛界面开关状态：" + res);
        return res;
    }

    public void setFragranceUiSwitchState(HashMap<String, Object> map) {

        String isOpen = (String) getValueInContext(map, "method_params_value");


        Set<Integer> set = getScreen(map);
        Iterator<Integer> iterator = set.iterator();
        while (iterator.hasNext()) {
            Integer position = iterator.next();
//            String end;
            operator.setBooleanProp(FragranceSignal.FRAGRANCE_UI, position, isOpen.equals("open"));
//            if (position == 0) {
//                end = DISPLAY_ID;
//            } else {
//                end = DISPLAY_ID2;
//            }
//            if (isOpen.equals("open")) {
//                LogUtils.i(TAG, "打开香氛界面");
//                operator.setBooleanProp(FragranceSignal.FRAGRANCE_UI,position,isOpen.equals("open"));
//                HvacServiceImpl.getInstance(mContext).exec(ACTION_OPEN_FRAG_DIALOG + end);
//            } else {
//                LogUtils.i(TAG, "关闭香氛界面");
//                HvacServiceImpl.getInstance(mContext).exec(ACTION_DISMISS_FRAG_DIALOG + end);
//            }
        }

    }

    private Set<Integer> getScreen(HashMap<String, Object> map) {
        Set<Integer> set = new HashSet<>();
        FunctionalPositionBean functionalPositionBean = FunctionDeviceMappingManager.getInstance().getFunctionalPositionMap().get(CommonSignal.COMMON_SCREEN_ENUMERATION);
        Map<String, String> curMap = functionalPositionBean.getTypFunctionalMap();
        if(curMap.size() == 2){
            set.add(0);
            return set;
        }
        List<Integer> positions = getMethodPosition(map);
        String nluScreen = (String) map.get("screen_name");
        //中控屏、主驾屏、副驾屏、前排娱乐屏、副驾娱乐屏
        if (nluScreen != null && nluScreen.equals("central_screen")) {
            //中控屏幕、副驾屏幕可以通过一个函数拿到当前的屏幕
            set.add(0);
            return set;
        }
        if (nluScreen != null && nluScreen.equals("passenger_screen")) {
            set.add(1);
            return set;
        }
        //判断位置信息里是否包含0，1

        for (int i = 0; i < positions.size(); i++) {
            if (positions.get(i) == 0) {
                set.add(0);
            }
            if (positions.get(i) == 1) {
                set.add(1);
            }
        }

        return set;
    }

    /**
     * 判断当前屏幕信息是否是兜底屏幕信息
     *
     * @return
     */
    public boolean isDouDiScreen(HashMap<String, Object> map) {
        String screenName = (String) getValueInContext(map, "screen_name");
        if (screenName != null && screenName.equals("ceil_screen")) {
            return true;
        }
        if (screenName != null && screenName.equals("armrest_screen")) {
            return true;
        }
        if (screenName != null && screenName.equals("entertainment_screen")) {
            Object posString = map.get("positions");
            if (posString != null) {
                //nlu给的position
                String p = (String) posString;
                if (!p.isEmpty()) {
                    if (p.equals("rear_side") || p.equals("rear_side_left") || p.equals("rear_side_right")) {
                        return true;
                    }
                }
            }
        }
        if (!keyContextInMap(map, "screen_name")) {
            //声源位置和指定位置是非0，1则返回true。
            List<Integer> positions = getMethodPosition(map);
            for (int i = 0; i < positions.size(); i++) {
                int p = positions.get(i);
                if (p != 0 && p != 1) {
                    return true;
                }
            }
        }
        return false;
    }


    //==============R挡位判断=========
    public boolean isReverseGear(HashMap<String, Object> map) {
        int curDriveGearPosition = operator.getIntProp(CommonSignal.COMMON_GEAR_INFO);
        LogUtils.i(TAG, "curDriveGearPosition :" + curDriveGearPosition);
        return curDriveGearPosition == 1;
    }

    /**
     * 判断当前屏幕是否是中控屏相关的逻辑
     *
     * @param map
     * @return
     */
    public boolean judgeScreenIsNotCenterScreen(HashMap<String, Object> map) {

        if (keyContextInMap(map, "screen_name")) {
            String screenName = (String) getValueInContext(map, "screen_name");
            if (screenName.equals("central_screen")) {
                return true;
            }
        }
        List<Integer> positions = getMethodPosition(map);
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < positions.size(); i++) {
            set.add(positions.get(i));
        }
        LogUtils.i(TAG, "判断当前屏幕是否是中控屏，" + set);
        return set.contains(0);
    }


    /**
     * @param str
     * @return
     */
    public String replacePlaceHolderInner(HashMap<String, Object> map, String str) {
        LogUtils.i(TAG, "进入replacePlaceHolderInner方法" + str);
//        String str = "先给您开空调了，@{position}温度@{temperature}度了";
        int index = 0;

        int start = str.indexOf("@");
        if (start == -1) {
            return str;
        }
        int end = str.indexOf("}");
        String key = str.substring(start + 2, end);
        LogUtils.d(TAG, "当前的key是：" + key);
        String ttsRes = (String) map.get(DCContext.TTS_PLACEHOLDER);
        //找到对应的结果，
        HashMap<Integer, Object> hashMap = (HashMap<Integer, Object>) getValueInContext(map, "set_number");
        ArrayList<Integer> positions = (ArrayList<Integer>) getValueInContext(map, DCContext.MAP_KEY_METHOD_POSITION);
        switch (key) {
            case "position":
                String pos = mergePositionToString(map);
                str = str.replace("@{position}", pos);
                break;

            case "level":
                str = str.replace("@{level}", hashMap.get(positions.get(0)) + "");
                break;
            case "edp_mode":
            case "Fragrance":
                String res = (String) getValueInContext(map, "method_params_value");
                if (TextUtils.isEmpty(res)) {
                    res = (String) getValueInContext(map, "switch_mode");
                }
                if (TextUtils.isEmpty(res)) {
                    res = ttsRes;
                }
                if (str.contains("edp_mode")) {
                    str = str.replace("@{edp_mode}", res);
                } else {
                    str = str.replace("@{Fragrance}", res);
                }
                break;
            case "edp_vol":
            case "concentration_level":
                boolean isLevel = map.containsKey("level");

                LogUtils.d(TAG, "当前key是：" + (String) (isLevel ? map.get("level") : map.get("adjust_mode")));
                if (isLevel) {
                    String concentrationLevel;
                    if (!TextUtils.isEmpty(ttsRes)) {
                        //先从设置香氛等级里获取。
                        concentrationLevel = AIR_FRAGRANCE_CONCENTRATION_STRING_STRING_TTS_MAP.get(ttsRes);
                    } else {
                        //之后再从ds里获取。
                        concentrationLevel = AIR_FRAGRANCE_CONCENTRATION_STRING_STRING_TTS_MAP.get(getValueInContext(map, "level"));
                    }
                    if (str.contains("concentration_level")) {
                        str = str.replace("@{concentration_level}", concentrationLevel);
                    } else {
                        str = str.replace("@{edp_vol}", concentrationLevel);
                    }
                } else {
                    LogUtils.i(TAG, "ttsRes为：" + ttsRes);
                    String mode;
                    if (!TextUtils.isEmpty(ttsRes)) {
                        mode = AIR_FRAGRANCE_CONCENTRATION_MODE_STRING_STRING_MAP.get(ttsRes);
                    } else {
                        LogUtils.i(TAG, "adjust_mode：" + getValueInContext(map, "adjust_mode") + " ");
                        //之后再从ds里获取。
                        mode = AIR_FRAGRANCE_CONCENTRATION_MODE_STRING_STRING_MAP.get(getValueInContext(map, "adjust_mode"));
                    }
                    str = str.replace("@{edp_vol}", mode);
                }

                break;
            case "mode":
                LogUtils.i(TAG, "ttsRes为：" + ttsRes);
                String mode;
                if (!TextUtils.isEmpty(ttsRes)) {
                    mode = AIR_FRAGRANCE_CONCENTRATION_MODE_STRING_STRING_MAP.get(ttsRes);
                } else {
                    LogUtils.i(TAG, "adjust_mode：" + getValueInContext(map, "adjust_mode") + " ");
                    //之后再从ds里获取。
                    mode = AIR_FRAGRANCE_CONCENTRATION_MODE_STRING_STRING_MAP.get(getValueInContext(map, "adjust_mode"));
                }
                str = str.replace("@{mode}", mode);
                break;
            case "edp_time":
                String duration = (String) map.get("duration");
                int time = Integer.valueOf(duration);
                String rep = "";
                if (time <= 1800) {
                    rep = "30分钟";
                } else if (time <= 3600) {
                    rep = "60分钟";
                } else {
                    rep = "持续";
                }
                str = str.replace("@{edp_time}", rep);
                break;
            default:
                LogUtils.e(TAG, "当前要处理的@{" + key + "}不存在");
                return str;

        }
//            index = end + 1;
        System.out.println(start + " " + end + " " + key + " " + index);

        return str;
    }


    private String mergePositionToString(HashMap<String, Object> map) {
        ArrayList<Integer> positionList = (ArrayList<Integer>) getValueInContext(map, DCContext.MAP_KEY_METHOD_POSITION);
        //3个位置的一语多意图，待处理。
        if (positionList.size() == 4 || positionList.size() == 3) {
            return "全车";
        }

        if (positionList.size() == 1) {
            switch (positionList.get(0)) {
                case 0:
                    return "主驾";
                case 2:
                    return "主驾侧";
                case 1:
                    return "副驾";
                case 3:
                    return "副驾侧";
            }
        }
        //表示是两个位置。
        //0表示没有，1表示有
        int[] arr = new int[4];
        for (int i = 0; i < positionList.size(); i++) {
            arr[positionList.get(i)] = 1;
        }
        if (arr[0] == 1 && arr[1] == 1) {
            return "前排";
        }
        if (arr[0] == 1 && arr[2] == 1) {
            return "主驾侧";
        }
        if (arr[1] == 1 && arr[3] == 1) {
            return "副驾侧";
        }
        if (arr[2] == 1 && arr[3] == 1) {
            return "后排";
        }
        //出错了。
        return "全车";
    }


}
