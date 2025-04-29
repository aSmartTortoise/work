package com.voyah.ai.logic.dc;


import android.util.Log2;

import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.dc.Devices;
import com.voice.sdk.device.carservice.dc.carsetting.SettingInterface;
import com.voice.sdk.device.carservice.dc.context.DCContext;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.carservice.signal.PositionSignal;
import com.voice.sdk.util.LogUtils;
import com.voyah.ai.logic.util.LocationHelper;
import com.voyah.ai.voice.platform.agent.api.context.FlowChatContext;
import com.voyah.ai.voice.platform.agent.api.flowchart.library.bean.FunctionalPositionBean;
import com.voyah.ai.voice.platform.agent.api.functionMatrix.FunctionDeviceMappingManager;
import com.voyah.ds.common.entity.context.FlowContextKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class BaseAbsDevices implements Devices {
    private static final String TAG = "BaseAbsDevices";

    private LocationHelper locationHelper = new LocationHelper();

    protected SettingInterface mSettingHelper = DeviceHolder.INS().getDevices().getSetting();

    public abstract String replacePlaceHolderInner(HashMap<String, Object> map, String str);

    public String replacePlaceHolder(HashMap<String, Object> map, String str) {
        LogUtils.i(TAG, "进入replacePlaceHolder方法" + str);
        int size = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '@') {
                size++;
            }
        }
        Log2.d(TAG, "@ size is ：" + size);
        if (size == 0) {
            size = 1;
        }
        for (int i = 0; i < size; i++) {
            str = replacePlaceHolderInner(map, str);
        }
        Log2.e(TAG, "tts :" + str);
        return str;
    }

    public Object curPositions(HashMap<String, Object> map) {
        return map.get(DCContext.MAP_KEY_METHOD_POSITION);
    }

    /**
     * 获取声源位置
     */
    public Object curSoundPositions(HashMap<String, Object> map) {
        return getValueInContext(map, DCContext.MAP_KEY_SOUND_LOCATION);
    }

    /**
     * 判断是否主驾声源
     */
    public boolean isFromFirstRowLeft(HashMap<String, Object> map) {
        Object object = curSoundPositions(map);
        if (object instanceof ArrayList) {
            ArrayList<Integer> array = (ArrayList<Integer>) object;
            return 0 == array.get(0);
        }
        return false;
    }

    /**
     * 判断是否儿童声纹
     */
    public boolean isChildSound(HashMap<String, Object> map) {
        return false;
    }

    /**
     * 获取声源位置
     */
    public int getSoundSourcePos(HashMap<String, Object> map) {
        Object object = getValueInContext(map, DCContext.MAP_KEY_SOUND_LOCATION);
        if (object instanceof ArrayList) {
            return ((ArrayList<Integer>) object).get(0);
        }
        return PositionSignal.FIRST_ROW_LEFT;//兜底主驾
    }

    /**
     * 返回当前方法需要处理的位置信息。
     * DCContext.FLOW_KEY_ONE_POSITION 一个位置信息
     * DCContext.MAP_KEY_METHOD_POSITION 多个位置信息
     *
     * @param map
     * @return
     */
    public List<Integer> getMethodPosition(HashMap<String, Object> map) {
        ArrayList<Integer> list = new ArrayList<>();
        //表达式循环多位置的处理，此时只会获得一个位置信息。
        if (map.containsKey(DCContext.FLOW_KEY_ONE_POSITION)) {
            int position = (int) map.get(DCContext.FLOW_KEY_ONE_POSITION);
            list.add(position);
            return list;
        }
        //regulatingTemp(position:total_car)
        //图中设置进来的位置信息需要处理的情况
        HashMap<String, Object> graphContext = (HashMap<String, Object>) map.get(DCContext.MAP_KEY_GRAPH_CONTEXT);
        if (graphContext.containsKey(DCContext.MAP_KEY_SLOT_POSITION)) {
            String position = (String) map.get(DCContext.MAP_KEY_SLOT_POSITION);
            return locationHelper.convertLocationInformation(position);
        }

        ArrayList<Integer> mapList = (ArrayList<Integer>) map.get(DCContext.MAP_KEY_METHOD_POSITION);
        if (mapList != null) {
            list = mapList;
        }
        return list;
    }

    /**
     * 通用方法，把key和value放入到Context里
     * <p>
     * setValueInContextMap
     *
     * @param map
     */
    public void setValueInContextMap(HashMap<String, Object> map) {
        String key = (String) getValueInContext(map, "context_key");
        String value = (String) getValueInContext(map, "context_value");
        map.put(key, value);
    }

    /**
     * 通用方法，把key和value放入到ResultMapContext里,也就是二次交互的map上下文里.
     *
     * @param map
     */
    public void setValueInContextSecondInteractionMap(HashMap<String, Object> map) {
        String key = (String) getValueInContext(map, "context_key_second_interaction");
        String value = (String) getValueInContext(map, "context_value_second_interaction");
        HashMap<String, Object> hashMap = (HashMap<String, Object>) map.get(FlowChatContext.FlowChatResultKey.SECOND_INTERACTION_IN_MAP_GRAPH_KEY);
        hashMap.put(key, value);
    }


    /**
     * nlu传下来的参数
     *
     * @return
     */
    public Object allPositions(HashMap<String, Object> map) {
        Object object = map.get("position");
        if (object instanceof Set) {
            List<Integer> list = new ArrayList<>();
            Set<Integer> set = (Set<Integer>) object;
            Iterator<Integer> iterator = set.iterator();
            while (iterator.hasNext()) {
                list.add(iterator.next());
            }
            return list;
        } else {
            return map.get("position");
        }
    }


    /**
     * 获取图中方法的key对应的值，或者ds里数据对应的值。
     * 图的方法参数放到了，上下文中图的上下文里。
     * ds数据放在了上下文里。
     * 先从图的上下文里找，再从全局上下文找
     *
     * @param map
     * @param key
     * @return
     */
    public Object getValueInContext(Map<String, Object> map, String key) {
        HashMap<String, Object> graphMap = (HashMap<String, Object>) map.get(DCContext.MAP_KEY_GRAPH_CONTEXT);
        if (graphMap.containsKey(key)) {
            return graphMap.get(key);
        }
        //二次交互里的上下文中获取数据
        HashMap<String, Object> graphMap2 = (HashMap<String, Object>) map.get(FlowChatContext.FlowChatResultKey.RESULT_MAP);
        if (graphMap2 != null && graphMap2.containsKey(key)) {
            return graphMap2.get(key);
        }
        return map.get(key);
    }

    /**
     * 判断key是否在图上下文，或者ds的上下文里。
     * 现在图的上下文里判断，再从全局上下文里判断。
     *
     * @param map
     * @param key
     * @return
     */
    public boolean keyContextInMap(Map<String, Object> map, String key) {
        HashMap<String, Object> graphMap = (HashMap<String, Object>) map.get(DCContext.MAP_KEY_GRAPH_CONTEXT);
        if (graphMap.containsKey(key)) {
            return true;
        }
        //二次交互里的上下文中获取数据
        HashMap<String, Object> graphMap2 = (HashMap<String, Object>) map.get(FlowChatContext.FlowChatResultKey.RESULT_MAP);
        if (graphMap2 != null && graphMap2.containsKey(key)) {
            return true;
        }
        return map.containsKey(key);
    }

    /**
     * 进入当前节点的位置信息是否为指令传入的所有位置信息
     *
     * @return
     */
    public boolean curPositionIsTotalPosition(HashMap<String, Object> map) {
        //当前方法的位置信息,大概率是之前的数据。之前是要删除的。
        ArrayList<Integer> list = (ArrayList<Integer>) map.get(DCContext.MAP_KEY_METHOD_POSITION);
        //指令传入位置新的状态。
        ArrayList<Integer> positions = (ArrayList<Integer>) map.get(DCContext.MAP_KEY_POSITION_FLOW);
        int lowSize = 0;
        for (int i = 0; i < positions.size(); i++) {
            if (positions.get(i) != -1) {
                lowSize++;
            }
        }
        return lowSize == list.size();
    }

    /**
     * 获取当前节点的位置信息个数
     *
     * @param map
     * @return
     */
    public int getCurPositionSize(HashMap<String, Object> map) {
        //todo 这块遇到了bug。先临时改下，看后面会有问题么
//        ArrayList<Integer> list = (ArrayList<Integer>) getMethodPosition(map);

        ArrayList<Integer> mapList = (ArrayList<Integer>) map.get(DCContext.MAP_KEY_METHOD_POSITION);
        if (mapList != null) {
            Log2.i(TAG, "位置信息的长度：" + mapList.size() + "mapList：" + mapList.toString());
        }

        return mapList.size();
    }

    /**
     * 信号之间有可能需要睡200ms，单位默认是ms
     *
     * @param map
     */
    public void timeSleep(HashMap<String, Object> map) {
        int sleepTime = Integer.valueOf((String) getValueInContext(map, "sleep_time"));
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断当前位置信息是否是来自声源位置信息。
     * 从算法的槽位里获取此方法的信息。
     *
     * @return true 是来自声源位置信息，false不是来自声源位置信息
     */
    public boolean curPositionIsSoundSource(HashMap<String, Object> map) {
        boolean isSoundSource = Boolean.valueOf(getValueInContext(map, DCContext.MAP_KEY_SLOT_IS_sound_source_position) + "");
        Log2.i(TAG, "当前是否是声源位置处理：" + isSoundSource);
        return isSoundSource;
    }


    /**
     * @param map
     */
    public void positionChange(HashMap<String, Object> map) {
        String changePosition = (String) getValueInContext(map, "change");//此处获取到{0,1}等需要给当前position设置的值。
        String stringInt = changePosition.substring(1, changePosition.length() - 1);
        String[] arrays = stringInt.split(",");


        //当位置信息进入到流程图的时候，{0,1}会对应所有位置生成一个数组{0,0,-1,-1}
        //-1表示当前位置不在指令流程中，0表示当前位置还在流程判断中
        //在流程中被分流走了，或者被当前方法去除掉了0的值需要变成1。
        ArrayList<Integer> positions = (ArrayList<Integer>) getValueInContext(map, DCContext.MAP_KEY_POSITION_FLOW);
        boolean isBreak = false;
        for (int i = 0; i < positions.size(); i++) {
            System.out.println("i:" + i);
            isBreak = false;
            if (positions.get(i) != -1) {
                //只处理，0，1的情况
                for (int j = 0; j < arrays.length; j++) {
                    if ((i + "").equals(arrays[j])) {
                        positions.set(i, 0);
                        isBreak = true;
                        break;
                    }
                }
            }
            if (positions.get(i) != -1 && !isBreak) {
                positions.set(i, 1);
            }
        }
        //positions里为0的位置信息拿出来，设置到MAP_KAY_POSITION_VALUE_MAP里。
        HashMap<Integer, Object> hash = new HashMap<>();
        for (int i = 0; i < positions.size(); i++) {
            if (positions.get(i) == 0) {
                hash.put(i, null);
            }
        }
        map.put(DCContext.MAP_KAY_POSITION_VALUE_MAP, hash);
        //修改位置信息就把这句话加上，暂时还没用到。
        map.put(DCContext.MAP_KEY_POSITION_VALUE_MAP_BOOLEAN, false);
        Log2.i(TAG, "positionChange:" + positions.toString() + " " + hash.size());
    }

    /**
     * 从上下文对应的key里获取一个value值。
     * 因为多位置的情况会返回map。而不是具体的值。而当前的接口里只需要一个值。
     */
    public String getOneMapValue(String paramsKey, HashMap<String, Object> map) {
        Object object = getValueInContext(map, paramsKey);
        String value = "";
        if (object instanceof Map) {
            for (Map.Entry<Integer, Object> entry : ((Map<Integer, Object>) object).entrySet()) {
                value = (String) entry.getValue();
                break;
            }
        } else {
            value = (String) object;
        }
        return value;
    }
    //==========二次交互相关=======

    /**
     * 设置code码，设置场景值
     *
     * @param map
     */
    public void secondInteractionParams(HashMap<String, Object> map) {
        //拿到流程图中传下来的code码
        Log2.i(TAG, map.toString());
        String scene = (String) getOneMapValue("method_params_value", map);
        map.put(FlowChatContext.FlowChatResultKey.RESULT_CODE, 20000);
        map.put(FlowChatContext.FlowChatResultKey.RESULT_SCENE, scene);
        Log2.i(TAG, "拿到的场景值是：" + scene);
        Log2.i(TAG, "secondInteractionParams:" + map.toString());
    }

    public void secondInteractionMissingSlot(HashMap<String, Object> map) {

    }

    public boolean isSwitchTypeOpen(HashMap<String, Object> map) {
        String switchType = (String) getValueInContext(map, "switch_type");
        return "open".equals(switchType);
    }

    public boolean isFirstQueryAfterWakeup(HashMap<String, Object> map) {
        boolean isFirstQuery = false;
        if (map.containsKey(FlowContextKey.FC_FIRST_QUERY_AFTER_WAKEUP)) {
            isFirstQuery = (boolean) getValueInContext(map, FlowContextKey.FC_FIRST_QUERY_AFTER_WAKEUP);
            Log2.i(TAG, "isFirstQueryAfterWakeup：" + isFirstQuery);
        } else {
            //兼容二次交互，因为二次交互肯定不是第一条query，但是必须要支持，根据choose_type槽位判断
            isFirstQuery = map.containsKey("choose_type");
        }
        return isFirstQuery;
    }

    public boolean isH56CCar(HashMap<String, Object> map) {
        return DeviceHolder.INS().getDevices().getCarServiceProp().isH56C();
    }

    public boolean isH37ACar(HashMap<String, Object> map) {
        return DeviceHolder.INS().getDevices().getCarServiceProp().isH37A();
    }

    public boolean isH37BCar(HashMap<String, Object> map) {
        return DeviceHolder.INS().getDevices().getCarServiceProp().isH37B();
    }

    public boolean isH56DCar(HashMap<String, Object> map) {
        return DeviceHolder.INS().getDevices().getCarServiceProp().isH56D();
    }

    /**
     * 判断当前车是否支持传入的屏幕
     *
     * @param map
     * @return
     */
    public boolean judgeCarHasSelectScreen(Map<String, Object> map) {
        //流程图中传入的屏幕数据，当前方法最好是传入的字符串数据。
        String graphScreen = (String) getValueInContext(map, "screen");
        //拿到注册的屏幕功能位。
        FunctionalPositionBean functionalPositionBean = FunctionDeviceMappingManager.getInstance().getFunctionalPositionMap().get(CommonSignal.COMMON_SCREEN_ENUMERATION);
        Map<String, String> curMap = functionalPositionBean.getTypFunctionalMap();
        Log2.i(TAG, "screen_map:" + curMap.toString() + " graphScreen:" + graphScreen);
        return curMap.containsKey(graphScreen);
    }

    /**
     * 判断当前的屏幕信息是否在功能位规定的范围内。
     *
     * @param map
     * @return
     */
    public boolean judgeCarHasSelectScreenName(HashMap<String, Object> map) {
        String screenName = (String) getValueInContext(map, "screen_name");
        if (screenName == null || screenName.isEmpty()) {
            //没有屏幕信息。返回false，往下走。
            return true;
        }

        if (screenName.equals("screen")) {
            //如果屏幕信息是screen，则要根据位置信息判断屏幕是什么屏幕，是否在支持的范围内。
            List<Integer> positions = getMethodPosition(map);
            for (int i = 0; i < positions.size(); i++) {
                Integer position = positions.get(i);
                if (position == 0 || position == 1) {
                    return true;
                }
            }
        }

        //此时一定是有某个屏幕信息了。
        //此时的处理逻辑，有屏幕优先屏幕，不管指定位置和声源位置。

        //拿到注册的屏幕功能位。
        FunctionalPositionBean functionalPositionBean = FunctionDeviceMappingManager.getInstance().getFunctionalPositionMap().get(CommonSignal.COMMON_SCREEN_ENUMERATION);
        Map<String, String> curMap = functionalPositionBean.getTypFunctionalMap();
        if (curMap.containsKey(screenName)) {
            return true;
        }
        boolean isHit = false;
        for (String key : curMap.keySet()) {
            //central_screen-passenger_screen-ceil_screen-armrest_screen
            switch (key) {

                case "passenger_screen":
                    if (screenName.equals(key)) {
                        return true;
                    }
                    //如果是娱乐屏幕，并且说的指定位置是包含1的位置。则表示命中
                    if (screenName.equals("entertainment_screen")) {
                        String nluPosition = (String) map.get("positions");

                        if (nluPosition == null || nluPosition.isEmpty()) {

                        } else {
                            Log2.i(TAG, "nlu_position is :" + nluPosition);
                            String[] arrayPositions = nluPosition.split(",");
                            for (int i = 0; i < arrayPositions.length; i++) {
                                switch (arrayPositions[i]) {
                                    case "first_row_right":
                                    case "right_side":
                                    case "total_car":
                                        return true;
                                    default:
                                        break;
                                }
                            }
                        }
                    }
                    break;
                case "ceil_screen":
                    if (screenName.equals(key)) {
                        return true;
                    }
                    //如果是娱乐屏幕，并且说的指定位置是包含2,3的位置。则表示命中
                    if (screenName.equals("entertainment_screen")) {
                        String nluPosition = (String) map.get("positions");

                        if (nluPosition == null || nluPosition.isEmpty()) {

                        } else {
                            Log2.i(TAG, "nlu_position is :" + nluPosition);
                            String[] arrayPositions = nluPosition.split(",");
                            for (int i = 0; i < arrayPositions.length; i++) {
                                switch (arrayPositions[i]) {
                                    case "rear_side":
                                    case "rear_side_left":
                                    case "rear_side_right":
                                    case "second_side":
                                    case "second_row_left":
                                    case "second_row_right":
                                    case "total_car":
                                        //全车这种最后都需要各模块判断。
                                        return true;
                                    default:
                                        break;
                                }
                            }
                        }
                    }
                    break;
                case "armrest_screen":
                case "central_screen":
                    if (screenName.equals(key)) {
                        return true;
                    }
                    break;
                default:
                    break;
            }

        }
        return false;

    }

}
