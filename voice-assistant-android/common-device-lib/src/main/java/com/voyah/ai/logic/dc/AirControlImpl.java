package com.voyah.ai.logic.dc;


import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.dc.AirControlInterface;
import com.voice.sdk.device.carservice.dc.context.DCContext;
import com.voice.sdk.device.carservice.signal.AirSignal;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.carservice.signal.PositionSignal;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voice.sdk.util.LogUtils;
import com.voyah.ai.voice.platform.agent.api.context.FlowChatContext;
import com.voyah.ai.voice.platform.agent.api.flowchart.helper.NluSlotHelper;
import com.voyah.ai.voice.platform.agent.api.flowchart.library.bean.FunctionalPositionBean;
import com.voyah.ai.voice.platform.agent.api.function.FunctionCodec;
import com.voyah.ai.voice.platform.agent.api.functionMatrix.FunctionDeviceMappingManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class AirControlImpl extends AbsDevices implements AirControlInterface {
    private static final String TAG = "AirControlImpl";
    private static final String ACTION_OPEN_AC_PAGE = "ACTION_OPEN_AC_PAGE";
    private static final String ACTION_CLOSE_AC_PAGE = "ACTION_CLOSE_AC_PAGE";
    private static final String ACTION_IS_AC_FOREGROUND = "ACTION_IS_AC_FOREGROUND";
    private static final String DISPLAY_ID = "_displayId";
    private static final String DISPLAY_ID2 = "_displayId2";

    //默认参数
    private static final float TEMPERATURE_MAX = 32.5f;
    private static final float TEMPERATURE_MIN = 17.5f;
    private static final int TEMPERATURE_STEP = 2;

    //测试数据
    private boolean curAcStatus = true;//空调打开状态
    private boolean curAcTempSynStatus = true;//空调温区同步开关
    private boolean beforeDefrostingStatus = true;//前除霜开关状态

    private volatile float curAirTemp = 21.5f;

    @Override
    public String getDomain() {
        return "air";
    }

    public AirControlImpl() {
    }

    /**
     * 获取当前空调的打开状态。
     *
     * @return
     */
    public boolean getAirSwitchState(HashMap<String, Object> map) {

        boolean airSwitchState = true;

        List<Integer> positions = getMethodPosition(map);
        Set<Integer> positionList = mergePosition(positions, AirSignal.AIR_SWITCH_STATE);
        Iterator<Integer> iterator = positionList.iterator();

        HashMap<Integer, Boolean> res = new HashMap();
        while (iterator.hasNext()) {
            Integer po = iterator.next();
            boolean a = operator.getBooleanProp(AirSignal.AIR_SWITCH_STATE, po);
            airSwitchState &= a;
            LogUtils.i(TAG, "当前位置：" + po + "对应的开关状态是：" + a);
            res.put(po, a);
        }
        map.put("tempMap", res);


//        if (map.containsKey("positions")) {
//            String positions = (String) map.get("positions");
//            if (positions != null) {
//                if (positions.contains("rear_side")) {//如果是后排
//                    airSwitchState = operator.getBooleanProp(AirSignal.AIR_SWITCH_STATE, PositionSignal.REAR_ROW);
//                } else {
//                    airSwitchState = operator.getBooleanProp(AirSignal.AIR_SWITCH_STATE, PositionSignal.FIRST_ROW);
//                }
//            } else {//为空默认指定打开前排
//                airSwitchState = operator.getBooleanProp(AirSignal.AIR_SWITCH_STATE, PositionSignal.FIRST_ROW);
//            }
//        }

//        boolean airSwitchState = operator.getBooleanProp(AirSignal.AIR_SWITCH_STATE, PositionSignal.FIRST_ROW);

        LogUtils.e(TAG, "当前空调状态是" + airSwitchState);
        return airSwitchState;
    }


    /**
     * 空调开关
     * true,执行打开空调。
     * false,执行关闭空调。
     */
    public void setAirSwitchState(HashMap<String, Object> map) {
        //todo 后续优化
        String object = getOneMapValue("method_params_value", map);
        boolean isOpen = object.equals("open");

        LogUtils.i(TAG, "执行" + isOpen + (isOpen ? "打开" : "关闭") + "空调");


        //判断空调的作揖占位符是否存在。

//        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_SWITCH_STATE_SEAT_SINGLE);
        boolean isSupport = functionalPositionIsSupport(map, AirSignal.AIR_SWITCH_STATE_SEAT_SINGLE);
        //如果是关闭空调，并且只有主驾一个人的时候直接关闭全车空调。
        LogUtils.i(TAG, "占位处理是否支持：" + isSupport);
        if (isSupport) {
            if (!isOpen) {
                Integer soundLocation = (Integer) map.get("soundLocation");
                //判断当前车上有几个人
                String array = operator.getStringProp(CommonSignal.COMMON_SEAT_PEOPLE_STATE);
                String[] occupyStatusValue = array.split(",");
//            CarPropertyValue seatOccupyStatus = CarPropUtils.getInstance().getPropertyRaw(H56C.ACU_state_ACU_PASSENGERSTS);
//            Integer[] occupyStatusValue = (Integer[]) seatOccupyStatus.getValue();
                int peopleCount = 0;
                for (int i = 0; i < occupyStatusValue.length; i++) {
                    //0表示没人做。
                    if (!occupyStatusValue[i].equals("0")) {
                        peopleCount++;
                    }
                }
                if (!map.containsKey("positions") && soundLocation == 0 && peopleCount == 1) {
                    //关闭全部空调
                    operator.setBooleanProp(AirSignal.AIR_SWITCH_STATE, PositionSignal.FIRST_ROW, false);
                    return;
                }

            }
        }
        List<Integer> positions = getMethodPosition(map);
        LogUtils.i(TAG, "当前空调位置信息为：" + positions.toString());
        List<Integer> positionList = mergePositionWind(positions, AirSignal.AIR_SWITCH_STATE);
        LogUtils.i(TAG, "合并后的空调位置信息为：" + positionList.toString());
        for (int i = 0; i < positionList.size(); i++) {
            //todo 有一个false就是false
            //因为连续发信号会导致无法多次触发。睡了1s都存在打不开的情况。。所以只能特殊处理。
            if (positionList.size() > 1) {
                if (isOpen) {
                    operator.setBooleanProp(AirSignal.AIR_SWITCH_STATE, PositionSignal.SECOND_ROW, isOpen);
                } else {
                    operator.setBooleanProp(AirSignal.AIR_SWITCH_STATE, PositionSignal.FIRST_ROW, isOpen);
                }

                break;
            }
            operator.setBooleanProp(AirSignal.AIR_SWITCH_STATE, positionList.get(i), isOpen);
        }

    }

    /**
     * 获取当前温区同步的开关状态。
     *
     * @return true为温区同步打开的，false为温区同步关闭的
     */
    public boolean getAirTempSynState(HashMap<String, Object> map) {

//        int curAirTempSynSwitch = 0;//默认是0
        List<Integer> positions = getMethodPosition(map);
        int position = getAirTempSynPosition(positions);

        boolean curAirTempSynSwitch = operator.getBooleanProp(AirSignal.AIR_TEMP_SYN_STATE);
//        if (map.containsKey("positions")) {
//            String positions = (String) map.get("positions");
//            if (positions != null) {
//                if (positions.contains("rear_side")) {//如果是后排 就打开全车
//                    curAirTempSynSwitch = operator.getBooleanProp(AirSignal.AIR_TEMP_SYN_STATE, PositionSignal.ALL);
//                } else {
//                    curAirTempSynSwitch = operator.getBooleanProp(AirSignal.AIR_TEMP_SYN_STATE, PositionSignal.FIRST_ROW);
//                }
//            }
//        }

        LogUtils.e(TAG, "当前空调温区同步状态状态是" + curAirTempSynSwitch);
//        return curAirTempSynSwitch == ParamsCommon.OnOff.ON;
        return curAirTempSynSwitch;
    }

    /**
     * 温区同步的开关
     */
    public void setAirTempSynState(HashMap<String, Object> map) {
        boolean isOpen = getOneMapValue("method_params_value", map).equals("open");

        List<Integer> positions = getMethodPosition(map);
        int position = getAirTempSynPosition(positions);
        operator.setBooleanProp(AirSignal.AIR_TEMP_SYN_STATE, isOpen);

//        if (map.containsKey("positions")) {
//            String positions = (String) map.get("positions");
//            if (positions != null) {
//                if (positions.contains("rear_side")) {//如果是后排 就打开全车
//                    operator.setBooleanProp(AirSignal.AIR_TEMP_SYN_STATE, PositionSignal.ALL, isOpen);
////                    carPropHelper.setIntProp(Climate.ID_AIR_CD_SYNC_DRV_SETTING_MODE,
////                            VehicleArea.ALL, isOpen ? ParamsCommon.OnOff.ON : ParamsCommon.OnOff.OFF);
//                } else {
//                    operator.setBooleanProp(AirSignal.AIR_TEMP_SYN_STATE, PositionSignal.FIRST_ROW, isOpen);
////                    carPropHelper.setIntProp(Climate.ID_AIR_CD_SYNC_DRV_SETTING_MODE,
////                            VehicleArea.FRONT_ROW, isOpen ? ParamsCommon.OnOff.ON : ParamsCommon.OnOff.OFF);
//                }
//            }
//        }

        LogUtils.e(TAG, "执行" + (isOpen ? "打开" : "关闭") + "空调温区同步");

    }

    private int getAirTempSynPosition(List<Integer> positions) {

        int[] po = new int[6];
        //把指定的位置都标记成1
        for (int i = 0; i < positions.size(); i++) {
            po[positions.get(i)] = 1;
        }
        //只要存在2以后的温区同步给的就是全车温区同步。
        for (int i = 2; i < po.length; i++) {
            if (po[i] == 1) {
                return PositionSignal.ALL;
            }
        }

        return PositionSignal.FIRST_ROW;
    }


    private boolean select = true;

    /**
     * 获取空调当前温度
     *
     * @return
     */
    public float getAirTemp(HashMap<String, Object> map) {
        List<Integer> positions = getMethodPosition(map);
        int realPosition = getRealPosition(positions.get(0), AirSignal.AIR_TEMP);
        float curTemp;
        //双温区只有主驾和副驾可以调节温度。0，2为主驾侧
        //1，3为副驾侧。
        curTemp = operator.getFloatProp(AirSignal.AIR_TEMP, realPosition);
//        curTemp = carPropHelper.getFloatProp(Climate.ID_TEMPERATURE, realPosition);
        LogUtils.e(TAG, "当前温度是：" + curTemp);
        return curTemp;
    }


    public float getMinAirTemp(HashMap<String, Object> map) {

        float minTemp = operator.getFloatProp(AirSignal.MIN_AIR_TEMP, PositionSignal.AREA_NONE);
        LogUtils.e(TAG, "温度的最小值是：" + minTemp);
        return minTemp;
    }

    public float getMaxAirTemp(HashMap<String, Object> map) {
        float maxTemp = operator.getFloatProp(AirSignal.MAX_AIR_TEMP, PositionSignal.AREA_NONE);
        LogUtils.e(TAG, "温度的最大值是：" + maxTemp);
        return maxTemp;
    }

    /**
     * 给对应位置设置温度
     */
    public void setAirTemp(HashMap<String, Object> map) {
        String adjustType = (String) map.get("adjust_type");
        Object value = map.get("method_params_value");
        List<Integer> positions = new ArrayList<>();
        if (value instanceof Map) {
            for (Integer key : ((Map<Integer, Object>) value).keySet()) {
                positions.add(Integer.valueOf(key));
            }
        } else {
            Object position = map.get("method_params_position");
            positions.add(Integer.valueOf(position + ""));
        }
        Set<Integer> mergePosition = mergePosition(positions, AirSignal.AIR_TEMP);

        Map<Integer, Object> hash = new HashMap<>();
        LogUtils.i(TAG, "需要进行设置温度的值：" + value.toString());
        Iterator<Integer> iterator = mergePosition.iterator();
        while (iterator.hasNext()) {
            Integer po = iterator.next();

            map.put(DCContext.FLOW_KEY_ONE_POSITION, po);
            if (value instanceof Map) {
                String va = "1";
                LogUtils.i(TAG, "当前的position信息：" + mergePosition.toString());
                LogUtils.i(TAG, "当前传下来的value信息：" + value.toString());
                if (!((HashMap<Integer, Object>) value).containsKey(po)) {
                    if (po == 0) {
                        va = (String) ((HashMap<Integer, Object>) value).get(2);
                    } else if (po == 1) {
                        va = (String) ((HashMap<Integer, Object>) value).get(3);
                    } else if (po == 2) {
                        va = (String) ((HashMap<Integer, Object>) value).get(1);
                    } else if (po == 3) {
                        va = (String) ((HashMap<Integer, Object>) value).get(0);
                    } else {
                        LogUtils.i(TAG, "进入esle");
                        HashMap<Integer, Object> map2 = ((HashMap<Integer, Object>) value);
                        for (Integer key : map2.keySet()) {
                            if (key == 0 || key == 1) {
                                LogUtils.i(TAG, "进入esle1" + key);
                            } else {
                                va = (String) map2.get(key);
                                LogUtils.i(TAG, "设置的温度数据源为：" + va);
                                break;
                            }

                        }
                    }
                } else {
                    LogUtils.i(TAG, "进入esle1");
                    va = (String) ((HashMap<Integer, Object>) value).get(po);
                }

                Float res = dealNumb(Float.valueOf(va), adjustType);
                if (res < getMinAirTemp(map)) {
                    res = getMinAirTemp(map);
                } else if (res > getMaxAirTemp(map)) {
                    res = getMaxAirTemp(map);
                }
                hash.put(po, res);
            } else {
                Float res = dealNumb(Float.valueOf(value + ""), adjustType);
                if (res < getMinAirTemp(map)) {
                    res = getMinAirTemp(map);
                } else if (res > getMaxAirTemp(map)) {
                    res = getMaxAirTemp(map);
                }
                hash.put(po, res);
            }
            LogUtils.i(TAG, "设置" + po + "位置温度为：" + hash.get(po));


            float resTemp = dealNumb(Float.valueOf(hash.get(po) + ""), adjustType);
//            int realPosition = getRealPosition(mergePosition.get(i));
            //CarService接口调用。
            operator.setFloatProp(AirSignal.AIR_TEMP, po, resTemp);
//            carPropHelper.setFloatProp(Climate.ID_TEMPERATURE, realPosition,
//                    resTemp);
            LogUtils.i(TAG, "设置空调温度为：" + resTemp);

        }
        map.put("set_number", hash);
    }


    /**
     * 判断当前的位置信息是否包含副驾，不同车型判断不一样。
     *
     * @param map
     * @return
     */
    public boolean curPositionIsContainRightPosition(HashMap<String, Object> map) {
        List<Integer> positions = (List<Integer>) allPositions(map);
        //判断当前空调个数。
        FunctionalPositionBean functionalPositionBean = FunctionDeviceMappingManager.getInstance().getFunctionalPositionMap().get(AirSignal.AIR_SWITCH_STATE);
        Map<String, String> curMap = functionalPositionBean.getTypFunctionalMap();
        int size = curMap.size();
        if (size > 1) {
            //判断当前位置信息是否包含副驾。不包含副驾返回true.
            Set<Integer> set = new HashSet<>();
            for (int i = 0; i < positions.size(); i++) {
                set.add(positions.get(i));
            }
            if (!set.contains(1)) {
                return true;
            }
        } else {

            if (positions.size() == 1 && positions.get(0) != 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断当前的位置信息是否只包含副驾，不同车型判断不一样。
     *
     * @param map
     * @return
     */
    public boolean curPositionIsOnlyContainRightPosition(HashMap<String, Object> map) {
        List<Integer> positions = (List<Integer>) allPositions(map);
        //判断当前空调个数。
        FunctionalPositionBean functionalPositionBean = FunctionDeviceMappingManager.getInstance().getFunctionalPositionMap().get(AirSignal.AIR_SWITCH_STATE);
        Map<String, String> curMap = functionalPositionBean.getTypFunctionalMap();
        int size = curMap.size();
        if (size == 1) {
            //判断当前位置信息是否包含副驾。不包含副驾返回true.
            Set<Integer> set = new HashSet<>();
            for (int i = 0; i < positions.size(); i++) {
                set.add(positions.get(i));
            }
            if (set.size() == 1 && set.contains(1)) {
                return true;
            }
        } else {

            if (positions.size() == 1 && positions.get(0) == 1) {
                return true;
            }
        }
        return false;
    }

    public void changePositionInAir(HashMap<String, Object> map) {

        List<Integer> list = new ArrayList<>();


        //判断当前空调个数。
        FunctionalPositionBean functionalPositionBean = FunctionDeviceMappingManager.getInstance().getFunctionalPositionMap().get(AirSignal.AIR_SWITCH_STATE);
        Map<String, String> curMap = functionalPositionBean.getTypFunctionalMap();
        int size = curMap.size();
        if (size == 1) {
            //37A,一个空调的情况。
            list.add(0);
        } else {
            //56C，多个空调的情况。去掉副驾。其他位置保持。
            List<Integer> positions = (List<Integer>) allPositions(map);
            Set<Integer> set = new HashSet<>();
            for (int i = 0; i < positions.size(); i++) {
                set.add(positions.get(i));
            }
            if (set.contains(1)) {
                set.remove(1);
            }
            list.addAll(set);
        }
        LogUtils.i(TAG, "需要调节的位置信息：" + list.toString());

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
                for (int j = 0; j < list.size(); j++) {
                    if (i == list.get(j)) {
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
        LogUtils.i(TAG, "positionChange:" + positions.toString() + " " + hash.size());
    }

    /**
     * 对airTemp进行步长为0.5进行处理，当是0.5以下去掉小数，当大于0.5的时候+1
     *
     * @param airTemp
     * @return
     */
    private float dealNumb(float airTemp, String adjustType) {
        if (adjustType != null && adjustType.equals("increase")) {
            String numStr = Float.toString(airTemp);
            String[] numArray = numStr.split("\\.");
            if (numArray.length >= 2) {
                char firtNum = numArray[1].charAt(0);
                if (firtNum > '0' && firtNum <= '4') {
                    return Float.valueOf(numArray[0]) + 0.5f;
                } else if (firtNum >= '6' && firtNum <= '9') {
                    return Float.valueOf(numArray[0]) + 1f;
                } else {
                    return airTemp;
                }
            } else {
                return airTemp;
            }
        } else if (adjustType != null && adjustType.equals("decrease")) {
            String numStr = Float.toString(airTemp);
            String[] numArray = numStr.split("\\.");
            if (numArray.length >= 2) {
                char firtNum = numArray[1].charAt(0);
                if (firtNum > '0' && firtNum <= '4') {
                    return Float.valueOf(numArray[0]);
                } else if (firtNum >= '6' && firtNum <= '9') {
                    return Float.valueOf(numArray[0]) + 0.5f;
                } else {
                    return airTemp;
                }
            } else {
                return airTemp;
            }
        } else {
            String numStr = Float.toString(airTemp);
            String[] numArray = numStr.split("\\.");
            if (numArray.length >= 2) {
                char firtNum = numArray[1].charAt(0);
                if (firtNum > '0' && firtNum <= '4') {
                    return Float.valueOf(numArray[0]) + 0.5f;
                } else if (firtNum >= '6' && firtNum <= '9') {
                    return Float.valueOf(numArray[0]) + 1;
                } else {
                    return airTemp;
                }
            } else {
                return airTemp;
            }
        }
    }

//    public void regulatingTemp(HashMap<String,Object> map) {
//
//        if(isDebug){
//            ArrayList<Integer> positions = (ArrayList<Integer>) getValueInContext(map,DCContext.MAP_KEY_METHOD_POSITION);
//            ArrayList<Integer> mergePosition = mergePosition(positions);
//            //如果存在流程图中给了执行的位置调节，流程图里传下来的位置优先处理。
//            if(keyContextInMap(map,"appoint_position")){
//                mergePosition.clear();
//                String position = (String) getValueInContext(map,"appoint_position");
//                mergePosition.add(Integer.valueOf(position));
//            }
//            for (int i = 0; i < mergePosition.size(); i++) {
//                int realPosition = getRealPosition(mergePosition.get(i));
//                Object object = getValueInContext(map,"set_number");
//                Object temp = null;
//                if(object instanceof HashMap){
//                    HashMap<Integer,Object> hashMap = (HashMap<Integer, Object>) object;
//                    temp = hashMap.get(mergePosition.get(i));
//                }else if(object instanceof String){
//                    temp = object;
//                }
//
//                float setTemp = -1.0f;
//                if(temp instanceof String){
//                    setTemp = Float.valueOf((String) temp);
//                }else if(temp instanceof Float){
//                    setTemp = (Float)temp;
//                }
//
//                LogUtils.e(TAG,"给空调"+realPosition+"位置设置"+setTemp+"度了");
//            }
////
//            return;
//        }
//        ArrayList<Integer> positions = (ArrayList<Integer>) getValueInContext(map,DCContext.MAP_KEY_METHOD_POSITION);
//        ArrayList<Integer> mergePosition = mergePosition(positions);
//        for (int i = 0; i < mergePosition.size(); i++) {
//            int realPosition = getRealPosition(mergePosition.get(i));
//            HashMap<Integer,Object> hashMap = (HashMap<Integer, Object>) getValueInContext(map,"set_number");
//            float setTemp = Float.valueOf((Float) hashMap.get(mergePosition.get(i)));
//            carPropHelper.setFloatProp(Climate.ID_TEMPERATURE, realPosition,
//                    setTemp);
//        }
//
//    }

    /**
     * 对位置进行合并,根据不通车和功能的特点进行合并，当前合并是因为空调只有主驾和副驾的区别，温度可以调节。
     *
     * @param positions 当前分支里的位置信息。
     * @return 返回只需要执行的位置信息。
     */
    private Set<Integer> mergePosition(List<Integer> positions, String functionPosition) {
        Set<Integer> mergePosition = new HashSet<>();
        for (int i = 0; i < positions.size(); i++) {
            int position = getRealPosition(positions.get(i), functionPosition);
            mergePosition.add(position);
        }
        mergePosition.remove("-1");
        return mergePosition;
    }

    private ArrayList<Integer> mergePositionWind(List<Integer> positions, String functionPosition) {
        ArrayList<Integer> mergePosition = new ArrayList<>();
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < positions.size(); i++) {
            int a = getWindPosition(positions.get(i), functionPosition);
            if (a != PositionSignal.AREA_NONE) {
                set.add(a);
            }

//            switch (positions.get(i)) {
//                case 0:
//                case 1:
//                    if (!mergePosition.contains(9)) {
//                        mergePosition.add(9);
//                    }
//                    break;
//                case 2:
//                case 3:
//                case 4:
//                case 5:
//                    if (!mergePosition.contains(10)) {
//                        mergePosition.add(10);
//                    }
//                    break;
//            }

        }
        Iterator<Integer> iterator = set.iterator();
        while (iterator.hasNext()) {
            mergePosition.add(iterator.next());
        }
        return mergePosition;
    }

    /**
     * 把0，1，2，3的位置信息转换成底层接口对应的位置信息。
     * 因为空调是有主驾副驾能调节温度，所以分成两个位置返回。
     * <p>
     * 0 1
     * 2 3
     * 4 5
     * <p>
     * 6
     * 7
     * 8
     *
     * @param position
     * @return
     */
    private int getRealPosition(Integer position, String functionPosition) {
//        LogUtils.i(TAG, "位置信息：" + position);

        FunctionalPositionBean functionalPositionBean = FunctionDeviceMappingManager.getInstance().getFunctionalPositionMap().get(functionPosition);
        Map<String, String> curMap = functionalPositionBean.getTypFunctionalMap();
//        LogUtils.i(TAG, "功能位的map:" + curMap.toString());
        //根据空调温度的功能位去做处理
        int realPosition = -1;
        if (curMap.size() == 1) {
            realPosition = PositionSignal.FIRST_ROW_LEFT;
            return realPosition;
        }
        switch (position) {
            case 0:
                if (curMap.containsKey("0") && curMap.get("0").equals("1")) {
                    realPosition = PositionSignal.FIRST_ROW_LEFT;
                } else if (curMap.containsKey("6") && curMap.get("6").equals("1")) {
                    realPosition = PositionSignal.FIRST_ROW;
                }
                break;
            case 1:
                if (curMap.containsKey("1") && curMap.get("1").equals("1")) {
                    realPosition = PositionSignal.FIRST_ROW_RIGHT;
                } else if (curMap.containsKey("6") && curMap.get("6").equals("1")) {
                    realPosition = PositionSignal.FIRST_ROW;
                }
                break;
            case 2:
                if (curMap.containsKey("2") && curMap.get("2").equals("1")) {
                    realPosition = PositionSignal.SECOND_ROW_LEFT;
                } else if (curMap.containsKey("7") && curMap.get("7").equals("1")) {
                    realPosition = PositionSignal.SECOND_ROW;
                } else {
                    realPosition = PositionSignal.FIRST_ROW_LEFT;
                }
                break;
            case 3:
                if (curMap.containsKey("3") && curMap.get("3").equals("1")) {
                    realPosition = PositionSignal.SECOND_ROW_RIGHT;
                } else if (curMap.containsKey("7") && curMap.get("7").equals("1")) {
                    realPosition = PositionSignal.SECOND_ROW;
                } else {
                    realPosition = PositionSignal.FIRST_ROW_RIGHT;
                }
                break;
            case 4:
                if (curMap.containsKey("4") && curMap.get("4").equals("1")) {
                    realPosition = PositionSignal.THIRD_ROW_LEFT;
                } else if (curMap.containsKey("8") && curMap.get("8").equals("1")) {
                    realPosition = PositionSignal.THIRD_ROW;
                }
                break;
            case 5:
                if (curMap.containsKey("5") && curMap.get("5").equals("1")) {
                    realPosition = PositionSignal.THIRD_ROW_RIGHT;
                } else if (curMap.containsKey("8") && curMap.get("8").equals("1")) {
                    realPosition = PositionSignal.THIRD_ROW;
                }
                break;
        }
        return realPosition;
    }

    //===================风量调节方法==============
    private int curWin = 3;


    /**
     * 获取当前风量
     *
     * @return 当前风量
     */
    public int getAirWind(HashMap<String, Object> map) {
        //获取当前风量。
        List<Integer> positions = getMethodPosition(map);
        int realPosition = ((getWindPosition(positions.get(0), AirSignal.AIR_WIND) == PositionSignal.AREA_NONE) ? getWindPosition(PositionSignal.FIRST_ROW, AirSignal.AIR_WIND) : getWindPosition(positions.get(0), AirSignal.AIR_WIND));
        LogUtils.i(TAG, "当前风量调节的位置信息：" + realPosition);
        int curSpeed = operator.getIntProp(AirSignal.AIR_WIND, realPosition);
//        int curSpeed = carPropHelper.getIntProp(Climate.ID_BLW_LEVEL,
//                VehicleArea.FRONT_ROW);
        map.put("cur_speed", curSpeed);
        LogUtils.e(TAG, "当前风量为：" + curSpeed);
        return curSpeed;
    }

    /**
     * 跟空调个数相关的可统一用此方法。
     * 空调开关、auto、风量、吹风模式。
     *
     * @param position
     * @return
     */
    private int getWindPosition(int position, String functionPosition) {
        //根据功能位对位置信息进行合并
        //拿到注册的屏幕功能位。
        FunctionalPositionBean functionalPositionBean = FunctionDeviceMappingManager.getInstance().getFunctionalPositionMap().get(functionPosition);
        Map<String, String> curMap = functionalPositionBean.getTypFunctionalMap();

        if (curMap.size() == 1 && curMap.containsKey("1")) {
            //todo 只有一个设备的时候就传0,
            return PositionSignal.FIRST_ROW_LEFT;
        }
//        //空调个数为2的时候，
//        if(curMap.size() == 2){
//            switch (position) {
//                case 0:
//                case 1:
//                    return PositionSignal.FIRST_ROW;
//                default:
//                    //其他情况传二排。
//                    return PositionSignal.REAR_ROW;
//            }
//        }

        switch (position) {
            case 0:
                if (curMap.containsKey("all")) {
                    return PositionSignal.FIRST_ROW_LEFT;
                }
                if (curMap.containsKey("0") && curMap.get("0").equals("1")) {
                    return PositionSignal.FIRST_ROW_LEFT;
                }
                if (curMap.containsKey("6") && curMap.get("6").equals("1")) {
                    return PositionSignal.FIRST_ROW;
                }
                return PositionSignal.AREA_NONE;
            case 1:
                if (curMap.containsKey("all")) {
                    return PositionSignal.FIRST_ROW_RIGHT;
                }
                if (curMap.containsKey("1") && curMap.get("1").equals("1")) {
                    return PositionSignal.FIRST_ROW_RIGHT;
                }
                if (curMap.containsKey("6") && curMap.get("6").equals("1")) {
                    return PositionSignal.FIRST_ROW;
                }

                return PositionSignal.AREA_NONE;
            case 2:
                if (curMap.containsKey("all")) {
                    return PositionSignal.SECOND_ROW_LEFT;
                }
                if (curMap.containsKey("2") && curMap.get("2").equals("1")) {
                    return PositionSignal.FIRST_ROW_RIGHT;
                }
                if (curMap.containsKey("7") && curMap.get("7").equals("1")) {
                    return PositionSignal.SECOND_ROW;
                }
                //兼容功能在前排的情况
                if (curMap.get("0").equals("1")) {
                    return PositionSignal.FIRST_ROW_LEFT;
                }
                return PositionSignal.AREA_NONE;
            case 3:
                if (curMap.containsKey("all")) {
                    return PositionSignal.SECOND_ROW_RIGHT;
                }
                if (curMap.containsKey("3") && curMap.get("3").equals("1")) {
                    return PositionSignal.SECOND_ROW_RIGHT;
                }
                if (curMap.containsKey("7") && curMap.get("7").equals("1")) {
                    return PositionSignal.SECOND_ROW;
                }
                //兼容功能在前排的情况
                if (curMap.containsKey("1") && curMap.get("1").equals("1")) {
                    return PositionSignal.FIRST_ROW_RIGHT;
                }
                return PositionSignal.AREA_NONE;
            case 4:
                if (curMap.containsKey("all")) {
                    return PositionSignal.THIRD_ROW_LEFT;
                }
                if (curMap.containsKey("4") && curMap.get("4").equals("1")) {
                    return PositionSignal.THIRD_ROW_LEFT;
                }
                if (curMap.containsKey("8") && curMap.containsKey("8") && curMap.get("8").equals("1")) {
                    return PositionSignal.THIRD_ROW;
                }
                //兼容功能在前排的情况
                if (curMap.get("0").equals("1")) {
                    return PositionSignal.FIRST_ROW_LEFT;
                }
                return PositionSignal.AREA_NONE;
            case 5:
                if (curMap.containsKey("all")) {
                    return 5;
                }
                if (curMap.containsKey("4") && curMap.get("4").equals("1")) {
                    return PositionSignal.THIRD_ROW_RIGHT;
                }
                if (curMap.containsKey("8") && curMap.containsKey("8") && curMap.get("8").equals("1")) {
                    return PositionSignal.THIRD_ROW;
                }
                //兼容功能在前排的情况
                if (curMap.containsKey("1") && curMap.get("1").equals("1")) {
                    return PositionSignal.FIRST_ROW_RIGHT;
                }
                return PositionSignal.AREA_NONE;
        }
        return position;
    }

    /**
     * 获取最大风量
     *
     * @return 最大风量
     */
    public int getMaxAirWind(HashMap<String, Object> map) {
        List<Integer> list = getMethodPosition(map);
        Set<Integer> set = mergePosition(list, AirSignal.AIR_WIND);

        Iterator<Integer> iterator = set.iterator();
        int maxWind = 7;
        while (iterator.hasNext()) {
            Integer pos = iterator.next();
            maxWind = operator.getIntProp(AirSignal.MAX_AIR_WIND, pos);
            LogUtils.e(TAG, "当前位置" + pos + ",风量的最大值是：" + maxWind);
        }

        return maxWind;
    }

    /**
     * 获取最小风量
     *
     * @return 最小风量
     */
    public int getMinAirWind(HashMap<String, Object> map) {
        int minWind = operator.getIntProp(AirSignal.MIN_AIR_WIND);
        map.put("min_speed", minWind);
        //Climate.ParamsFrontBlwLevel.OFF
        LogUtils.e(TAG, "当前风量的最小值是：" + minWind);
        return minWind;
    }


    /**
     * 调节风量，对风量进行调节
     */
    public void setAirWind(HashMap<String, Object> map) {


        int curSpeed = getAirWind(map);
        Map<Integer, Object> hash = new HashMap<>();
        String adjustType = (String) getValueInContext(map, "adjust_type");

        //根据位置去设置风量

        Object value = map.get("method_params_value");
        List<Integer> positions = new ArrayList<>();
        if (value instanceof Map) {
            for (Integer key : ((Map<Integer, Object>) value).keySet()) {
                positions.add(Integer.valueOf(key));
            }
        } else {
            Object position = map.get("method_params_position");
            positions.add(Integer.valueOf(position + ""));
        }
        ArrayList<Integer> mergePosition = mergePositionWind(positions, AirSignal.AIR_WIND);

        LogUtils.i(TAG, "需要进行设置风量的值：" + value.toString());
        for (int i = 0; i < mergePosition.size(); i++) {

            int realPosition = mergePosition.get(i);

            if (adjustType == null) {
                operator.setIntProp(AirSignal.AIR_WIND, realPosition, Integer.valueOf((String) value));
                return;
            }
            if (getValueInContext(map, "adjust_type").equals("increase")) {
                if (keyContextInMap(map, "number_level") || keyContextInMap(map, "number")) {
                    int resWind = Math.min(curSpeed + Integer.valueOf(getValueInContext(map, NluSlotHelper.getInstance().getNluSlotNumber(map) + "") + ""), getMaxAirWind(map));
                    hash.put(0, resWind);
                    operator.setIntProp(AirSignal.AIR_WIND, realPosition, resWind);
//                carPropHelper.setIntProp(Climate.ID_BLW_LEVEL, VehicleArea.FRONT_ROW,
//                        resWind);
                    LogUtils.e(TAG, "设置风量为：" + resWind);
                } else {
                    int resWind = Math.min(curSpeed + 1, getMaxAirWind(map));
                    hash.put(0, resWind);
                    operator.setIntProp(AirSignal.AIR_WIND, realPosition, resWind);
//                carPropHelper.setIntProp(Climate.ID_BLW_LEVEL, VehicleArea.FRONT_ROW,
//                        resWind);
                    LogUtils.e(TAG, "设置风量为：" + resWind);
                }
            } else if (getValueInContext(map, "adjust_type").equals("decrease")) {
                if (keyContextInMap(map, "number_level") || keyContextInMap(map, "number")) {
                    int resWind = Math.max(curSpeed - Integer.valueOf(getValueInContext(map, NluSlotHelper.getInstance().getNluSlotNumber(map) + "") + ""), getMinAirWind(map));
                    hash.put(0, resWind);
                    operator.setIntProp(AirSignal.AIR_WIND, realPosition, resWind);
//                carPropHelper.setIntProp(Climate.ID_BLW_LEVEL, VehicleArea.FRONT_ROW,
//                        resWind);
                    LogUtils.e(TAG, "设置风量为：" + resWind);
                } else {
                    int resWind = Math.max(curSpeed - 1, getMinAirWind(map));
                    hash.put(0, resWind);
                    operator.setIntProp(AirSignal.AIR_WIND, realPosition, resWind);
//                carPropHelper.setIntProp(Climate.ID_BLW_LEVEL, VehicleArea.FRONT_ROW,
//                        resWind);
                    LogUtils.e(TAG, "设置风量为：" + resWind);
                }
            } else if (getValueInContext(map, "adjust_type").equals("set")) {
                if (keyContextInMap(map, "number_level") || keyContextInMap(map, "number")) {
                    String numString = getValueInContext(map, NluSlotHelper.getInstance().getNluSlotNumber(map) + "") + "";
                    int num;
                    if (numString.contains(".")) {
                        num = (int) Float.parseFloat(numString);
                    } else {
                        num = Integer.parseInt(numString);
                    }
                    if (num > getMaxAirWind(map)) {
                        hash.put(0, getMaxAirWind(map));
                        operator.setIntProp(AirSignal.AIR_WIND, realPosition, getMaxAirWind(map));
//                    carPropHelper.setIntProp(Climate.ID_BLW_LEVEL, VehicleArea.FRONT_ROW,
//                            Climate.ParamsFrontBlwLevel.LEVEL8);
                        LogUtils.e(TAG, "设置风量为：" + getMaxAirWind(map));
                    } else if (num < getMinAirWind(map)) {
                        hash.put(0, getMinAirWind(map));
                        operator.setIntProp(AirSignal.AIR_WIND, realPosition, getMinAirWind(map));
//                    carPropHelper.setIntProp(Climate.ID_BLW_LEVEL, VehicleArea.FRONT_ROW,
//                            Climate.ParamsFrontBlwLevel.LEVEL1);
                        LogUtils.e(TAG, "设置风量为：" + getMinAirWind(map));
                    } else {
                        hash.put(0, num);
                        operator.setIntProp(AirSignal.AIR_WIND, realPosition, num);
//                    carPropHelper.setIntProp(Climate.ID_BLW_LEVEL, VehicleArea.FRONT_ROW,
//                            num);
                        LogUtils.e(TAG, "设置风量为：" + num);
                    }

                } else if (keyContextInMap(map, "level")) {
                    //todo
                    String num = (String) getValueInContext(map, "level");
                    if (num.equals("max")) {
                        hash.put(0, getMaxAirWind(map));
                        operator.setIntProp(AirSignal.AIR_WIND, realPosition, getMaxAirWind(map));
//                    carPropHelper.setIntProp(Climate.ID_BLW_LEVEL, VehicleArea.FRONT_ROW,
//                            Climate.ParamsFrontBlwLevel.LEVEL8);
                    } else if (num.equals("min")) {
                        hash.put(0, getMinAirWind(map));
                        operator.setIntProp(AirSignal.AIR_WIND, realPosition, getMinAirWind(map));
//                    carPropHelper.setIntProp(Climate.ID_BLW_LEVEL, VehicleArea.FRONT_ROW,
//                            Climate.ParamsFrontBlwLevel.LEVEL1);
                    } else {
                        LogUtils.e(TAG, "出错了！其他情况代验证");
                    }
                }
            }
        }
        map.put("set_number", hash);
    }

    //===========================A/C开关======================
    public boolean ACStatus = false;

    public boolean getACSwitchStatus(HashMap<String, Object> map) {

        boolean curValue = operator.getBooleanProp(AirSignal.AC_SWITCH_STATUS, PositionSignal.FIRST_ROW);
//        int curValue = carPropHelper.getIntProp(Climate.ID_AIR_CD_AC_MODE,
//                VehicleArea.FRONT_ROW);
        LogUtils.i(TAG, "A/C开关状态:" + curValue);
        return curValue;
    }

    public void setACSwitchStatus(HashMap<String, Object> map) {
        boolean isOpen = getOneMapValue("method_params_value", map).equals("open");

        operator.setBooleanProp(AirSignal.AC_SWITCH_STATUS, PositionSignal.FIRST_ROW, isOpen);
//        carPropHelper.setIntProp(Climate.ID_AIR_CD_AC_MODE,
//                VehicleArea.FRONT_ROW, isOpen ? ParamsCommon.OnOff.ON : ParamsCommon.OnOff.OFF);

    }

    //    private int windMode = 1;
    int NONE = 0;
    int FACE = 1;
    int FLOOR = 2;
    int FACE_FLOOR = 3;
    int DEFROST = 4;
    int FLOOR_DEFROST = 6;
    int ALL = 7;
    int FACE_DEFROST = 8;

    //=========吹风模式调节========
    private String curWindMode = "to_face";

    public String getWindMode(HashMap<String, Object> map) {
        //todo
        List<Integer> positions = getMethodPosition(map);
        int position = ((getWindPosition(positions.get(0), AirSignal.WIND_MODE) == PositionSignal.AREA_NONE) ? getWindPosition(PositionSignal.FIRST_ROW, AirSignal.WIND_MODE) : getWindPosition(positions.get(0), AirSignal.WIND_MODE));

        String curValue = operator.getStringProp(AirSignal.WIND_MODE, position);
//        int curValue = carPropHelper.getIntProp(Climate.ID_BLW_DIRECT,
//                VehicleArea.FRONT_ROW);
        LogUtils.i(TAG, "当前是：" + curValue + "模式");
        return curValue;
    }


    private static final Map<String, String> WINDMODE_String_STRING_MAP = new HashMap() {
        {
            put("to_face", "吹面");
            put("to_foot", "吹脚");
            put("face_foot", "吹面吹脚");
            put("to_window", "吹窗");
            put("window_foot", "吹脚吹窗");
            put("face_foot_window", "吹面吹脚吹窗");
            put("face_window", "吹面吹窗");
        }

    };


    private static final Map<String, String> SCAVENGING_STRING_STRING_MAP = new HashMap() {
        {
            put("avoid_face", "躲避吹面");
            put("focus_face", "聚焦吹面");
            put("auto_swing", "自动扫风");
        }

    };


    public boolean judgeDsWindModIsSupportMode(HashMap<String, Object> map) {
        String windMode = (String) getValueInContext(map, "switch_mode");
        if (WINDMODE_String_STRING_MAP.containsKey(windMode)) {
            LogUtils.d(TAG, "当前吹风模式在功能里：" + windMode);
            return true;
        }
        LogUtils.d(TAG, "当前吹风模式不在功能里：" + windMode);
        return false;
    }

    public boolean judgeCurrentPositionWindModIsSupportMode(HashMap<String, Object> map) {
        List<Integer> positions = getMethodPosition(map);
        //判断空调个数
        FunctionalPositionBean functionalPositionBean = FunctionDeviceMappingManager.getInstance().getFunctionalPositionMap().get(AirSignal.AIR_SWITCH_STATE);
        Map<String, String> curMap = functionalPositionBean.getTypFunctionalMap();
        if (curMap.size() == 1) {
            return true;
        }
        String windMode = (String) getValueInContext(map, "switch_mode");
        if (!windMode.contains("window")) {
            return true;
        }
        //判断当前位置信息是否包含除了0，1的位置。
        LogUtils.i(TAG, "当前的位置信息是：" + positions.toString());
        for (int i = 0; i < positions.size(); i++) {
            int po = positions.get(i);
            if (po != 0 && po != 1) {
                LogUtils.i(TAG, "当前判断的位置信息是：" + po);
                return false;
            }
        }
        return true;
    }

    public void setWindMode(HashMap<String, Object> map) {
        String windMode = getOneMapValue("method_params_value", map);
        List<Integer> positions = getMethodPosition(map);
        ArrayList<Integer> list = mergePositionWind(positions, AirSignal.WIND_MODE);

//        int position = getWindPosition(positions.get(0));
        for (int i = 0; i < list.size(); i++) {
            //        int carWindMode = WINDMODE_STRING_INT_MAP.get(windMode);
            LogUtils.d(TAG, "吹风模式设置成：" + windMode);
            operator.setStringProp(AirSignal.WIND_MODE, list.get(i), windMode);
//        carPropHelper.setIntProp(Climate.ID_BLW_DIRECT, VehicleArea.FRONT_ROW
//                , carWindMode);
            map.put(DCContext.TTS_PLACEHOLDER, WINDMODE_String_STRING_MAP.get(windMode));
            LogUtils.i(TAG, "当前出风模式已经调节成：" + WINDMODE_String_STRING_MAP.get(windMode));
        }


    }

    private String curScavengingMode = "focus_face";

    public String getScavengingWindMode(HashMap<String, Object> map) {

        List<Integer> positions = getMethodPosition(map);
        int realPosition = mergePositionToCarServicePositionId(positions.get(0));

        String curValue = operator.getStringProp(AirSignal.SCAVENGING_WIND_MODE, realPosition);
//        int curValue = carPropHelper.getIntProp(Climate.ID_BLOWER_AUTO_MODE, VehicleArea.FRONT_ROW);
        LogUtils.i(TAG, "当前吹脸模式的返回code是：" + curValue);
        LogUtils.i(TAG, "当前吹脸模式：" + SCAVENGING_STRING_STRING_MAP.get(curValue));
        return curValue;
    }

    public void setScavengingWindMode(HashMap<String, Object> map) {
        Object value = getValueInContext(map, "method_params_value");
        //多位置处理
        List<Integer> positions = new ArrayList<>();
        String switchMode = "";
        if (value instanceof Map) {
            for (Integer key : ((Map<Integer, Object>) value).keySet()) {
                positions.add(key);
                switchMode = (String) ((Map<String, Object>) value).get(key);
            }
        } else {
            Object position = map.get("method_params_position");
            positions.add(Integer.valueOf(position + ""));
            switchMode = (String) map.get("method_params_value");
        }


        //因为向上吹脸会触发向上扫风。

        if (switchMode.equals("to_face")) {
            switchMode = "focus_face";
        }

        LogUtils.i(TAG, "设置扫风模式：" + (SCAVENGING_STRING_STRING_MAP.get(switchMode) == null ? "focus_face" : switchMode));
        LogUtils.i(TAG, "switchMode is " + switchMode);
        map.put(DCContext.TTS_PLACEHOLDER, SCAVENGING_STRING_STRING_MAP.get(switchMode));
        Set<Integer> mergePosition = mergePosition(positions, AirSignal.SCAVENGING_WIND_MODE);
        Iterator<Integer> iterator = mergePosition.iterator();
        while (iterator.hasNext()) {
            Integer po = iterator.next();
            int area = mergePositionToCarServicePositionId(po);
            operator.setStringProp(AirSignal.SCAVENGING_WIND_MODE, area, switchMode);
        }
    }

    public int mergePositionToCarServicePositionId(int mergePosition) {
        int res = 0;
        switch (mergePosition) {
            case 0:
            case 2:
                res = PositionSignal.FIRST_ROW_LEFT;
                break;
            case 1:
            case 3:
                res = PositionSignal.FIRST_ROW_RIGHT;
                break;
        }
        return res;
    }

    //=============除霜=========
    private boolean front_defrost = true;

    public boolean getFrontDefrostSwitchState(HashMap<String, Object> map) {

        boolean curValue = operator.getBooleanProp(AirSignal.FRONT_DEFROST_SWITCH_STATE);
//        int curValue = carPropHelper.getIntProp(Climate.ID_REQ_FRONT_DEFROST);
        LogUtils.e(TAG, "前除霜打开状态：" + curValue);
        return curValue;//curValue == ParamsCommon.OnOff.ON;
    }

    private boolean rear_defrost = true;

    public boolean getRearDefrostSwitchState(HashMap<String, Object> map) {

        boolean curValue = operator.getBooleanProp(AirSignal.REAR_DEFROST_SWITCH_STATE);
//        int curValue = carPropHelper.getIntProp(Climate.ID_REAR_DEFROST);
        LogUtils.e(TAG, "后除霜打开状态：" + curValue);
        return curValue;
    }

    public void setFrontDefrostSwitchState(HashMap<String, Object> map) {
        boolean isOpen = ((String) getValueInContext(map, "switch_type")).equals("open");

        operator.setBooleanProp(AirSignal.FRONT_DEFROST_SWITCH_STATE, isOpen);
        LogUtils.e(TAG, isOpen ? "打开前除霜" : "关闭前除霜");
    }

    public void setRearDefrostSwitchState(HashMap<String, Object> map) {
        boolean isOpen = ((String) getValueInContext(map, "switch_type")).equals("open");


        operator.setBooleanProp(AirSignal.REAR_DEFROST_SWITCH_STATE, isOpen);
        LogUtils.e(TAG, isOpen ? "打开后除霜" : "关闭后除霜");
    }

    //=========内外、自动循环=====
    /**
     * 空调模式映射表
     */
    private static final Map<String, String> AIR_MODE_INTEGER_STRING_MAP = new HashMap() {
        {
            put("auto_circulation", "自动循环");
            put("inner_circulation", "内循环");
            put("outer_circulation", "外循环");
        }
    };


//            MapUtils.newLinkedHashMap(
//            put("auto_circulation", "自动循环"),
//            put("inner_circulation", "内循环"),
//            put("outer_circulation", "外循环")
//    );

    private String cycle = "内循环";

    /**
     * 只有内外循环
     *
     * @param map
     * @return
     */
    public String getAirCycleMode(HashMap<String, Object> map) {

        String curValue = operator.getStringProp(AirSignal.AIR_CYCLE_MODE, PositionSignal.AREA_NONE);
//        int curValue = carPropHelper.getIntProp(Climate.ID_RECYCLE_DOOR_MODE);
        LogUtils.e(TAG, "当前循环模式为：" + AIR_MODE_INTEGER_STRING_MAP.get(curValue));
        return curValue;
    }

    private boolean autoSwitch = true;

    public boolean getAirCycleAutoState(HashMap<String, Object> map) {

//        List<Integer> positions = getMethodPosition(map);
//        int position = getWindPosition(positions.get(0));
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();
        boolean curValue;
        String curValueBoolean = operator.getStringProp(AirSignal.AIR_CYCLE_MODE, PositionSignal.AREA_NONE);
        if (carType.equals("H37B")) {
            curValue = curValueBoolean == "auto_circulation";
            LogUtils.e(TAG, "当前循环的内容是：" + curValueBoolean);
        } else {
            curValue = operator.getBooleanProp(AirSignal.AIR_CYCLE_AUTO_STATE);
        }

//        int curValue = carPropHelper.getIntProp(Climate.ID_AQS_IONIZER);
        LogUtils.e(TAG, "当前自动循环开关状态：" + curValue);
        return curValue;
    }

    public void setAirCycleAutoState(HashMap<String, Object> map) {
        String switchMode = (String) getValueInContext(map, "method_params_value");
        map.put(DCContext.IS_SET_VALUE, "true");
        boolean isOpen;
        if (switchMode == null || switchMode.isEmpty()) {
            isOpen = ((String) getValueInContext(map, "switch_type")).equals("open");
        } else {
            isOpen = switchMode.equals("open");
        }

        autoSwitch = isOpen;
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();
        if (carType.equals("H37B")) {
            boolean nluSwitchMode = ((String) getValueInContext(map, "switch_type")).equals("open");
            if (!nluSwitchMode) {
                //如果是nlu下发的是关闭则需要根据switch_mode是什么进行处理了。
                //这块应该在流程图上话题处理。但当前修改
                isOpen = false;
            }
            if (isOpen) {
                operator.setStringProp(AirSignal.AIR_CYCLE_MODE, PositionSignal.AREA_NONE, "auto_circulation");
            } else {

                if (((String) getValueInContext(map, "switch_mode")).equals("auto_circulation")) {
                    //如果是关闭自动循环，就切换为内循环
                    operator.setStringProp(AirSignal.AIR_CYCLE_MODE, PositionSignal.AREA_NONE, "inner_circulation");
                } else if (((String) getValueInContext(map, "switch_mode")).equals("outer_circulation")) {
                    //如果是关闭外循环则切换到自动循环。
                    operator.setStringProp(AirSignal.AIR_CYCLE_MODE, PositionSignal.AREA_NONE, "auto_circulation");
                } else {

                }
            }
        } else {
            operator.setBooleanProp(AirSignal.AIR_CYCLE_AUTO_STATE, isOpen);
        }

//        carPropHelper.setIntProp(Climate.ID_AQS_IONIZER, isOpen ? ParamsCommon.OnOff.ON : ParamsCommon.OnOff.OFF);
    }

    public void setAirCycleMode(HashMap<String, Object> map) {
        String switchMode = (String) getValueInContext(map, "method_params_value");
        LogUtils.i(TAG, "要设置的模式是：" + switchMode);
        map.put(DCContext.TTS_PLACEHOLDER, switchMode);
        map.put(DCContext.IS_SET_VALUE, "true");
        switch (switchMode) {
            case "auto_circulation":
                boolean isOpen = ((String) getValueInContext(map, "switch_type")).equals("open");
                autoSwitch = isOpen;
                String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();
                if (carType.equals("H37B")) {
                    if (isOpen) {
                        operator.setStringProp(AirSignal.AIR_CYCLE_MODE, PositionSignal.AREA_NONE, "auto_circulation");
                    } else {
                        LogUtils.d(TAG, "打开内循环");
                        operator.setStringProp(AirSignal.AIR_CYCLE_MODE, PositionSignal.AREA_NONE, "inner_circulation");
                    }

                } else {
                    operator.setBooleanProp(AirSignal.AIR_CYCLE_AUTO_STATE, isOpen);
                }
//                carPropHelper.setIntProp(Climate.ID_AQS_IONIZER, isOpen ? ParamsCommon.OnOff.ON : ParamsCommon.OnOff.OFF);
                break;
            case "inner_circulation":
                cycle = "inner_circulation";
                operator.setStringProp(AirSignal.AIR_CYCLE_MODE, PositionSignal.AREA_NONE, cycle);
//                carPropHelper.setIntProp(Climate.ID_RECYCLE_DOOR_MODE, Climate.ParamsRecycleDoorMode.LOOP_INNER);
                break;
            case "outer_circulation":
                cycle = "outer_circulation";
                operator.setStringProp(AirSignal.AIR_CYCLE_MODE, PositionSignal.AREA_NONE, cycle);
//                carPropHelper.setIntProp(Climate.ID_RECYCLE_DOOR_MODE, Climate.ParamsRecycleDoorMode.LOOP_OUTSIDE);
                break;
        }
    }

    //=======pm2.5=======
    private int pm = 20;

    public int getAirPM(HashMap<String, Object> map) {

        int curValue = operator.getIntProp(AirSignal.AIR_PM);
//        int curValue = carPropHelper.getIntProp(Climate.ID_IN_CABIN_PM25_LEVEL);
        map.put(DCContext.TTS_PLACEHOLDER, curValue);
        LogUtils.e(TAG, "当前车内pm2.5的结果为：" + curValue);
        return curValue;
    }

    //=======干燥模式,自己封装的底层需要验证信号=====

    private static final Map<String, String> AIR_DRY_MODE_STRING_STRING_MAP = new HashMap() {
        {
            put("standard", "标准");
            put("strong", "深度");
        }
    };


    private String curDryMode = "standard";

    public String getDryMode(HashMap<String, Object> map) {

        String curValue = operator.getStringProp(AirSignal.DRY_MODE, PositionSignal.AREA_NONE);
//        int curValue = carPropHelper.getIntProp(Signal.ID_DRY_CLEAN_MODE);
//        Signal.ParamsDryCleanMode
//        int DRY_CLEAN_MODE_STANDRAD = 1;
//        int DRY_CLEAN_MODE_DEEP = 2;
        LogUtils.i(TAG, "当前的干燥模式是：" + AIR_DRY_MODE_STRING_STRING_MAP.get(curValue));
        return curValue;
    }

    public void setDryMode(HashMap<String, Object> map) {
        String switchMode = (String) getValueInContext(map, "switch_mode");


        LogUtils.i(TAG, "设置干燥模式：" + switchMode);
        operator.setStringProp(AirSignal.DRY_MODE, PositionSignal.AREA_NONE, switchMode);
//        carPropHelper.setIntProp(Signal.ID_DRY_CLEAN_MODE, AIR_DRY_MODE_STRING_INTEGER_MAP.get(switchMode));
    }

    public boolean getDryModeSwitchState(HashMap<String, Object> map) {

        boolean curValue = operator.getBooleanProp(AirSignal.DRY_MODE_SWITCH_STATE);
//        int curValue = carPropHelper.getIntProp(Signal.ID_DRY_CLEAN_ON_OFF);
        return curValue;
    }

    private boolean dryModeIsOpen = false;

    public void setDryModeSwitchState(HashMap<String, Object> map) {
        boolean isOpen = ((String) getValueInContext(map, "switch_type")).equals("open");

        LogUtils.i(TAG, "设置干燥模式开关：" + (isOpen ? "打开" : "关闭"));
        operator.setBooleanProp(AirSignal.DRY_MODE_SWITCH_STATE, isOpen);
//        carPropHelper.setIntProp(Signal.ID_DRY_CLEAN_ON_OFF, isOpen ? Signal.OnOffReq.ON : Signal.OnOffReq.OFF);
    }

    //=========通风降温=====
    private boolean ventilationState = false;

    public boolean getVentilationSwitchState(HashMap<String, Object> map) {

        boolean curValue = operator.getBooleanProp(AirSignal.VENTILATION_SWITCH_STATE);
//        int curValue = carPropHelper.getIntProp(Climate.ID_AUTO_WIND_UNLOCK);
        LogUtils.i(TAG, "当前通风降温开关状态：" + curValue);
        return curValue;
    }

    public void setVentilationSwitchState(HashMap<String, Object> map) {
        boolean isOpen = ((String) getValueInContext(map, "switch_type")).equals("open");

        LogUtils.i(TAG, "通风降温开关设置成：" + (isOpen ? "打开" : "关闭"));
        operator.setBooleanProp(AirSignal.VENTILATION_SWITCH_STATE, isOpen);
//        carPropHelper.setIntProp(Climate.ID_AUTO_WIND_UNLOCK,
//                isOpen ? ParamsCommon.OnOffInvalid.ON : ParamsCommon.OnOffInvalid.OFF);
    }

    //==============自动除霜==========

    private boolean autoDefrostState = false;
    private static final Map<String, String> AIR_AUTO_DEFROST_STRING_STRING_MAP = new HashMap() {
        {
            put("min", "最低");
            put("low", "偏低");
            put("mid", "标准");
            put("high", "偏高");
            put("max", "最高");
        }
    };


    private static final Map<Integer, String> AIR_AUTO_DEFROST_INDEX_INTEGER_STRING_MAP = new HashMap() {
        {
            put(0, "min");
            put(1, "low");
            put(2, "mid");
            put(3, "high");
            put(4, "max");
        }
    };

    private static final Map<String, Integer> AIR_AUTO_DEFROST_INDEX_STRING_INTEGER_MAP = new HashMap() {
        {
            put("min", 0);
            put("low", 1);
            put("mid", 2);
            put("high", 3);
            put("max", 4);
        }
    };


    public boolean getAutoDefrostSwitchState(HashMap<String, Object> map) {

        boolean curValue = operator.getBooleanProp(AirSignal.AUTO_DEFROST_SWITCH_STATE, PositionSignal.AREA_NONE);
//        int curValue = carPropHelper.getIntProp(Climate.ID_AIR_CD_DEFROST_AUTO_MODE);
        LogUtils.i(TAG, "当前自动除霜开关状态：" + curValue);
        return curValue;
    }

    public void setAutoDefrostSwitchState(HashMap<String, Object> map) {
        boolean isOpen = ((String) getValueInContext(map, "method_params_value")).equals("open");

        LogUtils.i(TAG, "自动除霜开关设置成：" + (isOpen ? "打开" : "关闭"));
        operator.setBooleanProp(AirSignal.AUTO_DEFROST_SWITCH_STATE, isOpen);
//        carPropHelper.setIntProp(Climate.ID_AIR_CD_DEFROST_AUTO_MODE,
//                isOpen ? ParamsCommon.OnOffInvalid.ON : ParamsCommon.OnOffInvalid.OFF);
    }

    private String curAutoDefrostLeve = "low";

    public String getAutoDefrostLevel(HashMap<String, Object> map) {
        String autoDefrostLeve = operator.getStringProp(AirSignal.AUTO_DEFROST_LEVEL, PositionSignal.AREA_NONE);
//        int autoDefrostLeve = carPropHelper.getIntProp(Signal.ID_AUTO_DEF_LEVEL);
//        autoDefrostLeve = ((autoDefrostLeve == -1) ? Signal.ParamsAutoDefLevelSts.AUTO_DEF_LEVEL_STS_LO : autoDefrostLeve);
        LogUtils.i(TAG, "当前自动除霜等级：" + AIR_AUTO_DEFROST_STRING_STRING_MAP.get(autoDefrostLeve));
        return autoDefrostLeve;
    }

    public String getMaxAutoDefrostLevel(HashMap<String, Object> map) {
        String max = operator.getStringProp(AirSignal.MAX_AUTO_DEFROST_LEVEL, PositionSignal.AREA_NONE);
        LogUtils.i(TAG, "自动除霜最大等级：" + max);//Signal.ParamsAutoDefLevelSts.AUTO_DEF_LEVEL_STS_HI
        return max;
    }

    public String getMinAutoDefrostLevel(HashMap<String, Object> map) {
        String min = operator.getStringProp(AirSignal.MIN_AUTO_DEFROST_LEVEL, PositionSignal.AREA_NONE);
        LogUtils.i(TAG, "自动除霜最小等级：：" + min);
        return min;
    }


    /**
     * 自动除霜调到低档：air_auto_defrost#adjust@adjust_type=&set@level=&low
     * 自动除霜调到中档：air_auto_defrost#adjust@adjust_type=&set@level=&mid
     * 自动除霜调到最高：air_auto_defrost#adjust@adjust_type=&set@level=&max
     * 自动除霜调到最低：air_auto_defrost#adjust@adjust_type=&set@level=&min
     * 自动除霜调高一点：air_auto_defrost#adjust@adjust_type=&increase
     * 自动除霜调低一点：air_auto_defrost#adjust@adjust_type=&decrease
     *
     * @param map
     * @return
     */
    public void setAutoDefrostLevel(HashMap<String, Object> map) {
        LogUtils.i(TAG, map.toString());
        String level = (String) getValueInContext(map, "method_params_value");
//        int carLevel = AIR_AUTO_DEFROST_STRING_INTEGER_MAP.get(level);
        operator.setStringProp(AirSignal.AUTO_DEFROST_LEVEL, PositionSignal.AREA_NONE, level);
//        carPropHelper.setIntProp(Signal.ID_AUTO_DEF_LEVEL, carLevel);
        map.put(DCContext.TTS_PLACEHOLDER, level);
        LogUtils.i(TAG, "设置自动除霜等级：" + level);
    }

    //======auto开关==========
    private boolean curAutoState = false;

    public boolean getAutoSwitchState(HashMap<String, Object> map) {
        List<Integer> positions = getMethodPosition(map);
        int position = ((getWindPosition(positions.get(0), AirSignal.AUTO_SWITCH_STATE) == PositionSignal.AREA_NONE) ? getWindPosition(PositionSignal.FIRST_ROW, AirSignal.AUTO_SWITCH_STATE) : getWindPosition(positions.get(0), AirSignal.AUTO_SWITCH_STATE));
        boolean curValue = operator.getBooleanProp(AirSignal.AUTO_SWITCH_STATE, position);
//        int curValue = carPropHelper.getIntProp(Climate.ID_AUTO_AIR_CD_MODE,
//                VehicleArea.FRONT_ROW);
        LogUtils.i(TAG, "当前开关状态：" + curValue);
        return curValue;
    }

    public void setAutoSwitchState(HashMap<String, Object> map) {
        boolean isOpen = ((String) getValueInContext(map, "switch_type")).equals("open");

        List<Integer> positions = getMethodPosition(map);
        int position = ((getWindPosition(positions.get(0), AirSignal.AUTO_SWITCH_STATE) == PositionSignal.AREA_NONE) ? getWindPosition(PositionSignal.FIRST_ROW, AirSignal.AUTO_SWITCH_STATE) : getWindPosition(positions.get(0), AirSignal.AUTO_SWITCH_STATE));
        LogUtils.i(TAG, "设置空调AUTO开关为：" + isOpen);
        operator.setBooleanProp(AirSignal.AUTO_SWITCH_STATE, position, isOpen);
//        carPropHelper.setIntProp(Climate.ID_AUTO_AIR_CD_MODE,
//                VehicleArea.FRONT_ROW, isOpen ? ParamsCommon.OnOffInvalid.ON : ParamsCommon.OnOffInvalid.OFF);

    }

    //============负离子开关===========
    public boolean getAnionSwitchState(HashMap<String, Object> map) {
        boolean curValue = operator.getBooleanProp(AirSignal.AIR_ANION_SWITCH_STATE, PositionSignal.FIRST_ROW);
        LogUtils.i(TAG, "当前开关状态：" + curValue);
        return curValue;
    }

    public void setAnionSwitchState(HashMap<String, Object> map) {
        boolean isOpen = ((String) getValueInContext(map, "switch_type")).equals("open");
        LogUtils.i(TAG, "设置负离子开关为：" + isOpen);
        operator.setBooleanProp(AirSignal.AIR_ANION_SWITCH_STATE, PositionSignal.FIRST_ROW, isOpen);
    }

    public boolean getAnionAutoSwitchState(HashMap<String, Object> map) {
        boolean curValue = operator.getBooleanProp(AirSignal.AIR_AUTO_ANION_SWITCH_STATE, PositionSignal.FIRST_ROW);
        LogUtils.i(TAG, "当前开关状态：" + curValue);
        return curValue;
    }

    public void setAnionAutoSwitchState(HashMap<String, Object> map) {
        boolean isOpen = ((String) getValueInContext(map, "switch_type")).equals("open");
        LogUtils.i(TAG, "设置自动负离子开关为：" + isOpen);
        operator.setBooleanProp(AirSignal.AIR_AUTO_ANION_SWITCH_STATE, PositionSignal.FIRST_ROW, isOpen);
    }

    //====================智能识别开关==============
    public boolean getIntelligentIdentificationSwitchStatus(HashMap<String, Object> map) {
        boolean curValue = operator.getBooleanProp(AirSignal.AIR_INTELLIGENT_IDENTIFICATION_SWITCH_STATE, PositionSignal.FIRST_ROW);
        LogUtils.i(TAG, "当前开关状态：" + curValue);
        return curValue;
    }

    public void setIntelligentIdentificationSwitchStatus(HashMap<String, Object> map) {
        boolean isOpen = ((String) getValueInContext(map, "switch_type")).equals("open");
        LogUtils.i(TAG, "设置智能识别开关为：" + isOpen);
        operator.setBooleanProp(AirSignal.AIR_INTELLIGENT_IDENTIFICATION_SWITCH_STATE, PositionSignal.FIRST_ROW, isOpen);
    }


    //====快速制冷开关====

    public boolean getFastCoolingSwitchState(HashMap<String, Object> map) {
        boolean isOpen = operator.getBooleanProp(AirSignal.AIR_FAST_COOLING_SWITCH_STATE, PositionSignal.FIRST_ROW);
        LogUtils.i(TAG, "快速制冷开关状态：：" + isOpen);
        return isOpen;
    }

    public void setFastCoolingSwitchState(HashMap<String, Object> map) {
        boolean isOpen = ((String) getValueInContext(map, "method_params_value")).equals("open");
        operator.setBooleanProp(AirSignal.AIR_FAST_COOLING_SWITCH_STATE, PositionSignal.FIRST_ROW, isOpen);
        LogUtils.i(TAG, "设置快速制冷开关：" + isOpen);
    }

    //====快速炙热开关====
    public boolean getFastHeaterSwitchState(HashMap<String, Object> map) {
        boolean isOpen = operator.getBooleanProp(AirSignal.AIR_FAST_HEATER_SWITCH_STATE, PositionSignal.FIRST_ROW);
        LogUtils.i(TAG, "快速制热开关状态：：" + isOpen);
        return isOpen;
    }

    public void setFastHeaterSwitchState(HashMap<String, Object> map) {
        boolean isOpen = ((String) getValueInContext(map, "method_params_value")).equals("open");
        operator.setBooleanProp(AirSignal.AIR_FAST_HEATER_SWITCH_STATE, PositionSignal.FIRST_ROW, isOpen);
        LogUtils.i(TAG, "设置快速制热开关：" + isOpen);
    }

    //=================出风口开关=============
    public boolean getOutletSwitchState(HashMap<String, Object> map) {
        List<Integer> positions = getMethodPosition(map);
        ArrayList<Integer> list = mergePositionWind(positions, AirSignal.AIR_OUTLET_SWITCH_IS_SUPPORT);
        boolean isOpen = true;
        for (int i = 0; i < list.size(); i++) {
            isOpen &= operator.getBooleanProp(AirSignal.AIR_OUTLET_SWITCH_IS_SUPPORT, list.get(i));
        }

        LogUtils.i(TAG, "出风口开关状态：" + isOpen);
        return isOpen;
    }

    public void setOutletSwitchState(HashMap<String, Object> map) {
        boolean isOpen = ((String) getValueInContext(map, "switch_type")).equals("open");
        List<Integer> positions = getMethodPosition(map);
        ArrayList<Integer> list = mergePositionWind(positions, AirSignal.AIR_OUTLET_SWITCH_IS_SUPPORT);
        for (int i = 0; i < list.size(); i++) {
            operator.setBooleanProp(AirSignal.AIR_OUTLET_SWITCH_IS_SUPPORT, list.get(i), isOpen);
        }
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
     * 空调模块需要重载，需要自己控制循环次数。
     *
     * @param map
     * @param str
     * @return
     */
    public String replacePlaceHolder(HashMap<String, Object> map, String str) {
        LogUtils.i(TAG, "进入replacePlaceHolder重写方法" + str);
        //解析出nlu
        String nluStr = (String) map.get(DCContext.NLU_INFO);
        FunctionCodec functionCodec = FunctionCodec.decode(nluStr);
        String functionName = functionCodec.getFunctionName();
        if (functionName.equals("air_temp#search") || functionName.equals("air_temp_interior#search")) {
            //判断是否支持温度查询
            boolean isSupport = functionalPositionIsSupport(map, AirSignal.AIR_CABIN_TEMP);
            LogUtils.i(TAG, "当前车是否支持查询车内温度的功能" + isSupport);
            if (isSupport && functionName.equals("air_temp_interior#search")) {
                //支持，并且传的是查询车内温度的函数的时候
                Float temp = operator.getFloatProp(AirSignal.AIR_CABIN_TEMP);
                return replaceTTsString(str, temp + "", "");
            }
            //查询温度需要循环处理
            StringBuilder stringBuilder = new StringBuilder();
            HashMap<Integer, Boolean> res = (HashMap<Integer, Boolean>) map.get("tempMap");
            Boolean isOpen = false;
            for (Integer key : res.keySet()) {
                isOpen |= res.get(key);
            }
            LogUtils.i(TAG, "空调的开关状态：" + isOpen);
            if (isOpen) {
                //获取当前空调功能位。

                List<Integer> positions = getMethodPosition(map);
                Set<Integer> positionList = mergePosition(positions, AirSignal.AIR_TEMP);
                if (positionList.size() == 0) {
                    //如果等于 0 ，则把位置信息往前调。
                    //譬如三排给到2排。
                    List<Integer> newPositions = new ArrayList<>();
                    for (int i = 0; i < positions.size(); i++) {
                        if (newPositions.get(i) == 4) {
                            newPositions.add(2);
                        } else if (newPositions.get(i) == 5) {
                            newPositions.add(3);
                        } else if (newPositions.get(i) == 2) {
                            newPositions.add(0);
                        } else if (newPositions.get(i) == 3) {
                            newPositions.add(1);
                        }
                    }
                    positionList = mergePosition(newPositions, AirSignal.AIR_TEMP);
                }
                Iterator<Integer> iteratorPo = positionList.iterator();
                int i = 0;
                while (iteratorPo.hasNext()) {
                    Integer po = iteratorPo.next();
                    LogUtils.i(TAG, "position:" + po + "res:" + res.toString());
                    if (i != 0 || i != res.size()) {
                        stringBuilder.append(",");
                    }
                    i++;
                    String position;
                    String temp;
                    switch (po) {
                        case PositionSignal.FIRST_ROW_LEFT:
                            if ((res.get(PositionSignal.FIRST_ROW) != null && res.get(PositionSignal.FIRST_ROW)) ||
                                    (res.get(PositionSignal.FIRST_ROW_LEFT) != null && res.get(PositionSignal.FIRST_ROW_LEFT))
                            ) {
                                position = "主驾";
                                map.put(DCContext.FLOW_KEY_ONE_POSITION, 0);
                                temp = getAirTemp(map) + "";
                                map.remove(DCContext.FLOW_KEY_ONE_POSITION);
                                stringBuilder.append(replaceTTsString(str, temp, position));
                            }

                            break;
                        case PositionSignal.FIRST_ROW_RIGHT:

                            if ((res.get(PositionSignal.FIRST_ROW) != null && res.get(PositionSignal.FIRST_ROW)) ||
                                    (res.get(PositionSignal.FIRST_ROW_LEFT) != null && res.get(PositionSignal.FIRST_ROW_LEFT))) {
                                position = "副驾";
                                map.put(DCContext.FLOW_KEY_ONE_POSITION, 1);
                                temp = getAirTemp(map) + "";
                                map.remove(DCContext.FLOW_KEY_ONE_POSITION);
                                stringBuilder.append(replaceTTsString(str, temp, position));
                            }
                            break;
                        case PositionSignal.SECOND_ROW_LEFT:
                            if (res.get(PositionSignal.SECOND_ROW) != null && res.get(PositionSignal.SECOND_ROW)) {
                                position = "二排左";
                                map.put(DCContext.FLOW_KEY_ONE_POSITION, 2);
                                temp = getAirTemp(map) + "";
                                map.remove(DCContext.FLOW_KEY_ONE_POSITION);
                                stringBuilder.append(replaceTTsString(str, temp, position));
                            }
                            break;
                        case PositionSignal.SECOND_ROW_RIGHT:
                            if (res.get(PositionSignal.SECOND_ROW) != null && res.get(PositionSignal.SECOND_ROW)) {
                                position = "二排右";
                                map.put(DCContext.FLOW_KEY_ONE_POSITION, 3);
                                temp = getAirTemp(map) + "";
                                map.remove(DCContext.FLOW_KEY_ONE_POSITION);
                                stringBuilder.append(replaceTTsString(str, temp, position));
                            }
                            break;
                        case PositionSignal.THIRD_ROW_LEFT:
                            if (res.get(PositionSignal.THIRD_ROW) != null && res.get(PositionSignal.THIRD_ROW)) {
                                position = "三排左";
                                map.put(DCContext.FLOW_KEY_ONE_POSITION, 4);
                                temp = getAirTemp(map) + "";
                                map.remove(DCContext.FLOW_KEY_ONE_POSITION);
                                stringBuilder.append(replaceTTsString(str, temp, position));
                            }
                            break;
                        case PositionSignal.THIRD_ROW_RIGHT:
                            if (res.get(PositionSignal.THIRD_ROW) != null && res.get(PositionSignal.THIRD_ROW)) {
                                position = "三排右";
                                map.put(DCContext.FLOW_KEY_ONE_POSITION, 5);
                                temp = getAirTemp(map) + "";
                                map.remove(DCContext.FLOW_KEY_ONE_POSITION);
                                stringBuilder.append(replaceTTsString(str, temp, position));
                            }
                            break;
                        case PositionSignal.FIRST_ROW:
                            if (res.get(PositionSignal.FIRST_ROW) != null && res.get(PositionSignal.FIRST_ROW)) {
                                position = "前排";
                                map.put(DCContext.FLOW_KEY_ONE_POSITION, 0);
                                temp = getAirTemp(map) + "";
                                map.remove(DCContext.FLOW_KEY_ONE_POSITION);
                                stringBuilder.append(replaceTTsString(str, temp, position));
                            }
                            break;
                        case PositionSignal.SECOND_ROW:
                            if (res.get(PositionSignal.SECOND_ROW) != null && res.get(PositionSignal.SECOND_ROW)) {
                                FunctionalPositionBean functionalPositionBean = FunctionDeviceMappingManager.getInstance().getFunctionalPositionMap().get(AirSignal.AIR_TEMP);
                                Map<String, String> curMap = functionalPositionBean.getTypFunctionalMap();
                                int airSize = 0;
                                for (String key : curMap.keySet()) {
                                    if (curMap.get(key).equals("1")) {
                                        airSize++;
                                    }
                                }
                                LogUtils.i(TAG, "当前空调的个数是：" + airSize + " " + curMap.toString());
                                if (airSize > 3) {
                                    position = "二排";
                                } else {
                                    position = "后排";
                                }
                                map.put(DCContext.FLOW_KEY_ONE_POSITION, 2);
                                temp = getAirTemp(map) + "";
                                map.remove(DCContext.FLOW_KEY_ONE_POSITION);
                                stringBuilder.append(replaceTTsString(str, temp, position));
                            }
                            break;
                        case PositionSignal.THIRD_ROW:
                            if (res.get(PositionSignal.THIRD_ROW) != null && res.get(PositionSignal.THIRD_ROW)) {
                                position = "三排";
                                map.put(DCContext.FLOW_KEY_ONE_POSITION, 4);
                                temp = getAirTemp(map) + "";
                                map.remove(DCContext.FLOW_KEY_ONE_POSITION);
                                stringBuilder.append(replaceTTsString(str, temp, position));
                            }
                            break;
                    }
                }

            } else {
                stringBuilder.append(str);
            }


            return stringBuilder.toString();
        }

        int size = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '@') {
                size++;
            }
        }
        LogUtils.d(TAG, "@ size is ：" + size);
        if (size == 0) {
            size = 1;
        }
        for (int i = 0; i < size; i++) {
            str = replacePlaceHolderInner(map, str);
        }
        LogUtils.e(TAG, "tts :" + str);
        return str;
    }


    private String replaceTTsString(String str, String temp, String position) {
        int size = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '@') {
                size++;
            }
        }
        LogUtils.d(TAG, "@ size is ：" + size);
        if (size == 0) {
            size = 1;
        }
        for (int i = 0; i < size; i++) {
            int start = str.indexOf("@");
            if (start == -1) {
                return str;
            }
            int end = str.indexOf("}");
            String key = str.substring(start + 2, end);
            switch (key) {
                case "position":
                    str = str.replace("@{position}", position);
                    break;
                case "temperature":
                    str = str.replace("@{temperature}", temp);
                    break;
                case "temp_num":
                    str = str.replace("@{temp_num}", temp);
                    break;
            }
        }
        LogUtils.e(TAG, "SingleTts :" + str);
        return str;
    }


    /**
     * @param str
     * @return
     */
    @Override
    public String replacePlaceHolderInner(HashMap<String, Object> map, String str) {

        LogUtils.i(TAG, "进入replacePlaceHolder方法" + str);
//        String str = "先给您开空调了，@{position}温度@{temperature}度了";
        //解析出nlu
        String nluStr = (String) map.get(DCContext.NLU_INFO);
        FunctionCodec functionCodec = FunctionCodec.decode(nluStr);
        String functionName = functionCodec.getFunctionName();

        int index = 0;

        int start = str.indexOf("@");
        if (start == -1) {
            return str;
        }
        int end = str.indexOf("}");
        String key = str.substring(start + 2, end);
        //找到对应的结果，
        HashMap<Integer, Object> hashMap = (HashMap<Integer, Object>) getValueInContext(map, "set_number");
        ArrayList<Integer> positions = (ArrayList<Integer>) getValueInContext(map, DCContext.MAP_KEY_METHOD_POSITION);
        LogUtils.i(TAG, "当前需要替换的站位符是：" + key + "");
        switch (key) {
            case "position":
                String pos = "";
                if (functionName.equals("air_syn#switch")) {
                    //温区同步的特殊处理
                    pos = "主副驾";
                } else if (functionName.equals("air_wind_mode#switch") ||
                        functionName.equals("air_wind#adjust")) {
                    FunctionalPositionBean functionalPositionBean = FunctionDeviceMappingManager.getInstance().getFunctionalPositionMap().get(AirSignal.AIR_SWITCH_STATE);
                    Map<String, String> curMap = functionalPositionBean.getTypFunctionalMap();
                    if (curMap.size() == 1) {
                        pos = "";
                    } else {
                        Set<Integer> set = new HashSet<>();
                        for (int i = 0; i < positions.size(); i++) {
                            switch (positions.get(i)) {
                                case 0:
                                case 1:
                                    set.add(0);
                                    break;
                                default:
                                    set.add(1);
                                    break;
                            }
                        }
                        if (set.size() == 2) {
                            pos = "全车";
                        }
                        Iterator<Integer> iterator = set.iterator();
                        while (iterator.hasNext()) {
                            int po = iterator.next();
                            if (po == 0) {
                                pos = "前排";
                            } else {
                                pos = "后排";
                            }
                        }
                    }
                }
//                else if(functionName.equals("air_wind#switch")){
//
//                }
                else if (functionName.equals("air_hot#adjust") || functionName.equals("air_hot#switch") ||
                        functionName.equals("air_cold#adjust") || functionName.equals("air_cold#switch") ||
                        functionName.equals("air_temp#adjust") || functionName.equals("air_temp#search")) {
                    FunctionalPositionBean functionalPositionBean = FunctionDeviceMappingManager.getInstance().getFunctionalPositionMap().get(AirSignal.AIR_SWITCH_STATE);
                    Map<String, String> curMap = functionalPositionBean.getTypFunctionalMap();

                    //主驾副驾
                    Set<Integer> set = new HashSet<>();
                    for (int i = 0; i < positions.size(); i++) {
                        switch (positions.get(i)) {
                            case 0:
                                set.add(0);
                                break;
                            case 1:
                                set.add(1);
                                break;
                            case 2:
                            case 4:
//                                if (curMap.size() == 2) {
                                set.add(2);
//                                } else {
//                                    set.add(0);
//                                }
                                break;
                            case 3:
                            case 5:
//                                if (curMap.size() == 2) {
                                set.add(3);
//                                } else {
//                                    set.add(1);
//                                }
                                break;
                        }
                    }
                    Iterator<Integer> iterator = set.iterator();
                    int[] a = new int[]{0, 0, 0, 0};
                    while (iterator.hasNext()) {
                        int po = iterator.next();
                        a[po] = 1;
                    }
                    LogUtils.i(TAG, "位置数据：" + Arrays.toString(a));
                    if (a[0] == 1 && a[1] == 1 && a[2] == 1 && a[3] == 1) {
//                        if (curMap.size() == 1) {
//                            pos = "";
//                        } else {
                        pos = "全车";
//                        }

                    } else if (a[0] == 1 && a[1] == 1 && a[2] == 0 && a[3] == 0) {
                        pos = "前排";
                    } else if (a[0] == 1 && a[1] == 0 && a[2] == 0 && a[3] == 0) {
                        pos = "主驾";
                    } else if (a[0] == 0 && a[1] == 1 && a[2] == 0 && a[3] == 0) {
                        pos = "副驾";
                    } else if (a[0] == 0 && a[1] == 0 && a[2] == 1 && a[3] == 1) {
//                        if (curMap.size() == 1) {
//                            pos = "前排";
//                        } else {
                        pos = "后排";
//                        }
                    } else if ((a[0] == 1 || a[0] == 0) && a[2] == 1 && a[1] == 0 && a[3] == 0) {
                        pos = "主驾侧";
                    } else if (a[0] == 0 && (a[1] == 1 || a[1] == 0) && a[2] == 0 && a[3] == 1) {
                        pos = "副驾侧";
                    } else {
                        pos = "";
                    }

                    //因为分流的逻辑会导致不分position会不在这里，所以做个特殊处理
                    String stringPosition = (String) getValueInContext(map, FlowChatContext.FlowChatResultKey.MAP_KEY_POSITION);
                    LogUtils.d(TAG, "stringPosition是:" + stringPosition);
                    if (stringPosition != null && !stringPosition.isEmpty()) {
                        switch (stringPosition) {
                            case "total_car":
                                pos = "全车";
                                break;
                            case "rear_side":
//                                if (curMap.size() == 1) {
//                                    pos = "前排";
//                                } else {
                                pos = "后排";
//                                }
                                break;
                            case "front_side":
                                pos = "前排";
                                break;
                        }
                    }

                    LogUtils.i(TAG, "处理的pos是：" + pos);


                } else {
                    //如果是指定位置信息，则部分使用ds传    下来的位置信息。
                    String stringPosition = (String) getValueInContext(map, FlowChatContext.FlowChatResultKey.MAP_KEY_POSITION);
                    LogUtils.d(TAG, "stringPosition是:" + stringPosition);

                    FunctionalPositionBean functionalPositionBean = FunctionDeviceMappingManager.getInstance().getFunctionalPositionMap().get(AirSignal.AIR_SWITCH_STATE);
                    Map<String, String> curMap = functionalPositionBean.getTypFunctionalMap();
                    LogUtils.d(TAG, "");
                    if (!curPositionIsSoundSource(map)) {
                        switch (stringPosition) {
                            case "total_car":
                                pos = "全车";
                                break;
                            case "rear_side":
//                                if (curMap.size() == 1) {
//                                    pos = "前排";
//                                } else {
                                pos = "后排";
//                                }
                                break;
                            case "front_side":
                                pos = "前排";
                                break;
                            case "inside_car":
                                int curPos = Integer.parseInt(map.get("soundLocation") + "");
                                switch (curPos) {
                                    case 0:
                                        pos = "主驾";
                                        break;
                                    case 2:
                                        pos = "主驾侧";
                                        break;
                                    case 1:
                                        pos = "副驾";
                                        break;
                                    case 3:
                                        pos = "副驾侧";
                                        break;
                                    default:
                                        pos = "";
                                        break;
                                }
                                break;
                            default:
                                pos = mergePositionToString(map);
                                break;
                        }
                    } else {
                        pos = mergePositionToString(map);
                    }
                }
                LogUtils.d(TAG, "换位置信息：" + pos);
                str = str.replace("@{position}", pos);
                break;
            case "temp_num":
            case "temperature":
                LogUtils.i(TAG, "进入temp_num的case");
                float setTemp;
                //所有温度都从当前温度获取。

//                    //todo 需要考虑多位置的情况
//                    map.put(DCContext.FLOW_KEY_ONE_POSITION, ((ArrayList<?>) getValueInContext(map, DCContext.MAP_KEY_METHOD_POSITION)).get(0));
//                    setTemp = getAirTemp(map);
//                    map.remove(DCContext.FLOW_KEY_ONE_POSITION);
                String nlu = (String) map.get("nlu_info");
                if (nlu.equals("air_temp#search@position=&inside_car")) {
                    int curPos = Integer.parseInt(map.get("soundLocation") + "");
                    map.put(DCContext.FLOW_KEY_ONE_POSITION, curPos);
                    setTemp = getAirTemp(map);
                    map.remove(DCContext.FLOW_KEY_ONE_POSITION);
                    if (str.contains("temperature")) {
                        LogUtils.i(TAG, "进入temperature");
                        str = str.replace("@{temperature}", FloatNumberIs0End(setTemp + "") + "");
                    } else {
                        LogUtils.i(TAG, "进入temp_num");
                        str = str.replace("@{temp_num}", FloatNumberIs0End(setTemp + "") + "");
                    }
                    break;
                }

                if (hashMap != null) {
                    Iterator<Integer> iterator = hashMap.keySet().iterator();
                    Integer position = iterator.next();
                    LogUtils.i(TAG, "空调温度：从HashMAP获取：" + hashMap.get(position));
                    setTemp = Float.valueOf(hashMap.get(position) + "");
                } else {
                    LogUtils.i(TAG, "空调温度里map里的值" + map.toString());
                    map.put(DCContext.FLOW_KEY_ONE_POSITION, ((ArrayList<?>) getValueInContext(map, DCContext.MAP_KEY_METHOD_POSITION)).get(0));
                    setTemp = getAirTemp(map);
                    map.remove(DCContext.FLOW_KEY_ONE_POSITION);
//                        setTemp = dealNumb(Float.valueOf((String) getValueInContext(map,NluSlotHelper.getInstance().getNluSlotNumber(map) + "")));
                }
                if (str.contains("temperature")) {
                    LogUtils.i(TAG, "进入temperature");
                    str = str.replace("@{temperature}", FloatNumberIs0End(setTemp + "") + "");
                } else {
                    LogUtils.i(TAG, "进入temp_num");
                    str = str.replace("@{temp_num}", FloatNumberIs0End(setTemp + "") + "");
                }

                break;
            case "cmh_num":
            case "level":
                LogUtils.i(TAG, hashMap != null ? hashMap.toString() : "map是null的");
                LogUtils.i(TAG, "通过key里获取leve的值：" + (hashMap == null ? "hashmap为null" : hashMap.get(0) + ""));
                LogUtils.i(TAG, "通过CarService获取数据：" + getAirWind(map));
                String res;
                if (hashMap != null) {
                    res = hashMap.get(0) + "";
                } else {
                    res = getAirWind(map) + "";
                }
                if (str.contains("level")) {
                    str = str.replace("@{level}", res);
                } else {
                    str = str.replace("@{cmh_num}", res);
                }
                break;
            case "ac_swing_mode":
            case "mode":
                String mode = WINDMODE_String_STRING_MAP.get(getValueInContext(map, "switch_mode"));
                if (mode == null) {
                    mode = SCAVENGING_STRING_STRING_MAP.get(getValueInContext(map, "switch_mode"));
                }
                if (str.contains("ac_swing_mode")) {
                    str = str.replace("@{ac_swing_mode}", mode);
                } else {
                    str = str.replace("@{mode}", mode);
                }

                break;
            case "ac_cycle_mode":
            case "cycle_mode":
                //占位符的变量优先，其次是从ds拿到的数据。
                String cycleMode = (String) getValueInContext(map, DCContext.TTS_PLACEHOLDER);
                LogUtils.i(TAG, "replace 方法里获取到占位符的信息：" + cycleMode);
                if (cycleMode == null || "".equals(cycleMode)) {
                    if (nluStr.contains("air_cycle#switch") && nluStr.contains("close")) {
                        //todo 打补丁
                        Object object = map.get(DCContext.IS_SET_VALUE);
                        if (object == null) {
                            cycleMode = getAirCycleMode(map);
                            LogUtils.i(TAG, "cure airCycleMode is:" + AIR_MODE_INTEGER_STRING_MAP.get(cycleMode));
                        } else {
                            cycleMode = (String) getValueInContext(map, "switch_mode");
                            if (cycleMode.equals("inner_circulation")) {
                                cycleMode = "outer_circulation";
                            } else if (cycleMode.equals("outer_circulation")) {
                                FunctionalPositionBean functionalPositionBean = FunctionDeviceMappingManager.getInstance().getFunctionalPositionMap().get(AirSignal.AIR_CYCLE_AUTO_STATE2);
                                Map<String, String> curMap = functionalPositionBean.getTypFunctionalMap();
                                if (curMap.containsKey("1")) {
                                    cycleMode = "inner_circulation";
                                } else {
                                    cycleMode = "auto_circulation";
                                }
                            } else {
                                cycleMode = "inner_circulation";
                            }
                        }
                    }
                }
                //因为存在ds中没有switch_mode,而是从图中设置下来的，所以在设置swich_mode的时候会往全局上下文中设置
                if (cycleMode == null || "".equals(cycleMode)) {
                    cycleMode = (String) getValueInContext(map, "switch_mode");
                }

                if (str.contains("ac_cycle_mode")) {
                    str = str.replace("@{ac_cycle_mode}", AIR_MODE_INTEGER_STRING_MAP.get(cycleMode) + "");
                } else {
                    str = str.replace("@{cycle_mode}", AIR_MODE_INTEGER_STRING_MAP.get(cycleMode) + "");
                }
                break;
            case "incar_aqi":
            case "AQI":
                String airPM = getValueInContext(map, DCContext.TTS_PLACEHOLDER) + "";
                if (str.contains("AQI")) {
                    str = str.replace("@{AQI}", airPM + "");
                } else {
                    str = str.replace("@{incar_aqi}", airPM + "");
                }
                break;
            case "dry_level":
                str = str.replace("@{dry_level}", AIR_DRY_MODE_STRING_STRING_MAP.get(getValueInContext(map, "switch_mode")) + "");
                break;
            case "ac_defrost_level":
            case "defrost_level":
                //占位符的变量优先，其次是从ds拿到的数据。
                String defrostLevel = (String) getValueInContext(map, DCContext.TTS_PLACEHOLDER);
                //因为存在ds中没有switch_mode,而是从图中设置下来的，所以在设置swich_mode的时候会往全局上下文中设置
                if (defrostLevel == null || "".equals(defrostLevel)) {
                    defrostLevel = (String) getValueInContext(map, "level");
                }
                LogUtils.i(TAG, "当前除霜结果是" + defrostLevel);
                if (str.contains("ac_defrost_level")) {
                    str = str.replace("@{ac_defrost_level}", AIR_AUTO_DEFROST_STRING_STRING_MAP.get(defrostLevel));
                } else {
                    str = str.replace("@{defrost_level}", AIR_AUTO_DEFROST_STRING_STRING_MAP.get(defrostLevel));
                }
                break;
            case "temp_num_ac":
            case "number_temp":
//                    if(){
//
//                    }
//                    if (hashMap != null) {
//                        Iterator<Integer> iterator = hashMap.keySet().iterator();
//                        Integer position = iterator.next();
//                        LogUtils.i(TAG, "空调温度：从HashMAP获取：" + hashMap.get(position));
//                        setTemp = Float.valueOf(hashMap.get(position) + "");
//                    }else{
//                        setTemp = dealNumb(Float.valueOf(NluSlotHelper.getInstance().getNluSlotNumber(map) + ""));
//                    }
                if (str.contains("number_temp")) {
                    str = str.replace("@{number_temp}",
                            FloatNumberIs0End(
                                    dealNumb(Float.valueOf((String) getValueInContext(map, NluSlotHelper.getInstance().getNluSlotNumber(map) + "")), "set") + "") + "");
                } else {
                    str = str.replace("@{temp_num_ac}",
                            FloatNumberIs0End(
                                    dealNumb(Float.valueOf((String) getValueInContext(map, NluSlotHelper.getInstance().getNluSlotNumber(map) + "")), "set") + "") + "");
                }
                break;
            case "cycle mode":
                String cycleMode2 = (String) getValueInContext(map, DCContext.TTS_PLACEHOLDER);
                LogUtils.i(TAG, "replace 方法里获取到占位符的信息：" + cycleMode2);
                //因为存在ds中没有switch_mode,而是从图中设置下来的，所以在设置swich_mode的时候会往全局上下文中设置
                if (cycleMode2 == null || "".equals(cycleMode2)) {
                    cycleMode2 = (String) getValueInContext(map, "switch_mode");
                }
                str = str.replace("@{cycle mode}", AIR_MODE_INTEGER_STRING_MAP.get(cycleMode2));
                break;
            case "deo_mode":
            case "switch_mode":
                if (str.contains("switch_mode")) {
                    str = str.replace("@{switch_mode}", AIR_DRY_MODE_STRING_STRING_MAP.get(getValueInContext(map, "switch_mode")));
                } else {
                    str = str.replace("@{deo_mode}", AIR_DRY_MODE_STRING_STRING_MAP.get(getValueInContext(map, "switch_mode")));
                }

                break;
            case "temp_min":
                str = str.replace("@{temp_min}", getMinAirTemp(map) + "");
                break;
            case "temp_max":
                str = str.replace("@{temp_max}", getMaxAirTemp(map) + "");
                break;
            case "cmh_min":
                str = str.replace("@{cmh_min}", getMinAirWind(map) + "");
                break;
            case "cmh_max":
                str = str.replace("@{cmh_max}", getMaxAirWind(map) + "");
                break;
            case "ac_cmh_mode":
                //获取设置的模式
                String ac_cmh_mode = (String) map.get(DCContext.TTS_PLACEHOLDER);
                if (ac_cmh_mode == null || ac_cmh_mode.isEmpty()) {
                    String switchMode = (String) getValueInContext(map, "switch_mode");
                    if (switchMode.equals("to_face")) {
                        switchMode = "focus_face";
                    }
                    ac_cmh_mode = SCAVENGING_STRING_STRING_MAP.get(switchMode);
                }


                str = str.replace("@{ac_cmh_mode}", ac_cmh_mode);
                break;
            case "nlu_mode":
                String switchMode = (String) getValueInContext(map, "switch_mode");
                str = str.replace("@{nlu_mode}", WINDMODE_String_STRING_MAP.get(switchMode));
                break;
            case "exe_mode":
                String exe_mode = (String) map.get(DCContext.TTS_PLACEHOLDER);
                str = str.replace("@{exe_mode}", exe_mode);
                break;
            default:
                LogUtils.e(TAG, "当前要处理的@{" + key + "}不存在");
                return str;

        }
//            index = end + 1;
        System.out.println(start + " " + end + " " + key + " " + index + " " + str);

        return str;
    }

    //========空调界面开关======
    private int airUiSwitchState = -1;

    public boolean getAirUiSwitchState(HashMap<String, Object> map) {
        List<Integer> positions = getMethodPosition(map);
        LogUtils.i(TAG, "位置信息：" + positions.get(0));
//        for (int i = 0; i < positions.size(); i++) {
//
//        }
        Set<Integer> set = getScreen(map, positions.get(0));
        Iterator<Integer> iterator = set.iterator();
        boolean curAirPageState = true;
        //先判断当前车型支持的屏幕是哪几种屏幕
        FunctionalPositionBean functionalPositionBean = FunctionDeviceMappingManager.getInstance().getFunctionalPositionMap().get(CommonSignal.COMMON_SCREEN_ENUMERATION);
        Map<String, String> curMap = functionalPositionBean.getTypFunctionalMap();
        String screen;
        if (curMap.size() == 2) {
            screen = null;
        } else {
            //清除霸屏弹窗,在operator里调用。
            screen = DeviceHolder.INS().getDevices().getLauncher().getAssignScreen(map);
            LogUtils.i(TAG, "当前屏幕是：" + screen);
        }
        DeviceHolder.INS().getDevices().getLauncher().closeScreenCentral(DeviceScreenType.fromValue(screen), "AirPage");
        while (iterator.hasNext()) {
            Integer po = iterator.next();
            curAirPageState = DeviceHolder.INS().getDevices().getLauncher().isAirOpen(DeviceScreenType.fromValue(screen));
            LogUtils.i(TAG, "位置信息：" + po + " 界面开关状态：" + curAirPageState);
//            curAirPageState = operator.getBooleanProp(AirSignal.AIR_UI_STATE, po);
            break;
        }
        LogUtils.i(TAG, "空调界面开关状态：" + curAirPageState);
        return curAirPageState;
    }

    public void setAirUiSwitchState(HashMap<String, Object> map) {
        String isOpen = (String) getValueInContext(map, "method_params_value");


        Set<Integer> set = getScreen(map);
        Iterator<Integer> iterator = set.iterator();
        boolean res = true;
        while (iterator.hasNext()) {
            Integer position = iterator.next();
            operator.setBooleanProp(AirSignal.AIR_UI_STATE, position, isOpen.equals("open"));
        }


//        if (isOpen.equals("open")) {
//            LogUtils.i(TAG, "打开空调界面：" + 0);
//            HvacServiceImpl.getInstance(mContext).exec(ACTION_OPEN_AC_PAGE + DISPLAY_ID);
//        } else {
//            LogUtils.i(TAG, "关闭空调界面：" + -1);
//            HvacServiceImpl.getInstance(mContext).exec(ACTION_CLOSE_AC_PAGE + DISPLAY_ID);
//        }
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

    /**
     * 判断当前车是否是多个屏幕，最好用功能位去判断。
     *
     * @param map
     * @return
     */
    public boolean isCarHasMoreScreen(HashMap<String, Object> map) {

        String str = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();
        boolean isMoreScreen;
        switch (str) {
            case "H56C":
                isMoreScreen = true;
                break;
            default:
                isMoreScreen = false;
                break;
        }
        return isMoreScreen;
    }

    private Object FloatNumberIs0End(String num) {
        String[] array = num.split("\\.");
        System.out.println(Arrays.toString(array));
        if (array[array.length - 1].equals("0")) {
            return array[0];
        } else {
            return num;
        }
    }


    private Set<Integer> getScreen(HashMap<String, Object> map) {
        Set<Integer> set = new HashSet<>();

        FunctionalPositionBean functionalPositionBean = FunctionDeviceMappingManager.getInstance().getFunctionalPositionMap().get(CommonSignal.COMMON_SCREEN_ENUMERATION);
        Map<String, String> curMap = functionalPositionBean.getTypFunctionalMap();
        if (curMap.size() == 2) {
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
     * 当前除霜打开后,判断传入的数据是否不包含副驾
     *
     * @return
     */
    public boolean getBeforeDefrostingOpenPositionIsSupportTempChange(HashMap<String, Object> map) {
        List<Integer> list = getMethodPosition(map);

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == 1) {
                return false;
            }
        }
        return true;
    }

    private Set<Integer> getScreen(HashMap<String, Object> map, int position) {
        Set<Integer> set = new HashSet<>();

        FunctionalPositionBean functionalPositionBean = FunctionDeviceMappingManager.getInstance().getFunctionalPositionMap().get(CommonSignal.COMMON_SCREEN_ENUMERATION);
        Map<String, String> curMap = functionalPositionBean.getTypFunctionalMap();
        if (curMap.size() == 2) {
            set.add(0);
            return set;
        }

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


        if (position == 0) {
            set.add(0);
        }
        if (position == 1) {
            set.add(1);
        }


        return set;
    }


    private String mergePositionToString(HashMap<String, Object> map) {
        ArrayList<Integer> positionList = (ArrayList<Integer>) getValueInContext(map, DCContext.MAP_KEY_METHOD_POSITION);

        FunctionalPositionBean functionalPositionBean = FunctionDeviceMappingManager.getInstance().getFunctionalPositionMap().get(CommonSignal.COMMON_SEAT_STATE);
        Map<String, String> curMap = functionalPositionBean.getTypFunctionalMap();
        //判断车型是几座位的。
        if (curMap.size() == 4) {

            //3个位置的一语多意图，待处理。
            if (positionList.size() == 4 || positionList.size() == 3) {
                return "全车";
            }

            if (positionList.size() == 1) {
                switch (positionList.get(0)) {
                    case 0:
                        return "主驾";
                    case 2:
                        return "后排";
                    case 1:
                        return "副驾";
                    case 3:
                        return "后排";
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
            LogUtils.i(TAG, "进入mergePositionToString方法：" + Arrays.toString(arr));
            if (arr[2] == 1 && arr[3] == 1) {
                if (curMap.size() == 2) {
                    return "后排";
                } else {
                    return "前排";
                }
            }
            //出错了。
            return "全车";
        } else {
            //3个位置的一语多意图，待处理。
            if (positionList.size() >= 3) {
                return "全车";
            }

            if (positionList.size() == 1) {
                switch (positionList.get(0)) {
                    case 0:
                        return "主驾";
                    case 2:
                        return "后排";
                    case 1:
                        return "副驾";
                    case 3:
                        return "后排";
                }
            }
            //表示是两个位置。
            //0表示没有，1表示有
            int[] arr = new int[6];
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


}
