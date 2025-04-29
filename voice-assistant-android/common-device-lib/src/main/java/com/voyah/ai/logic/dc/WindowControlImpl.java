package com.voyah.ai.logic.dc;


import android.text.TextUtils;

import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.dc.context.DCContext;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.carservice.signal.PositionSignal;
import com.voice.sdk.device.carservice.signal.WindowSignal;
import com.voice.sdk.util.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class WindowControlImpl extends AbsDevices {

    private static final String TAG = WindowControlImpl.class.getSimpleName();
    private static final int WINDOW_SEVEN_F = 127;


    @Override
    public String getDomain() {
        return "window";
    }

    @Override
    public String replacePlaceHolderInner(HashMap<String, Object> map, String str) {
        int index = 0;

        int start = str.indexOf("@");
        if (start == -1) {
            return str;
        }
        int end = str.indexOf("}");
        String key = str.substring(start + 2, end);
        switch (key) {
            case "position":
                ArrayList<Integer> positions = (ArrayList<Integer>) getValueInContext(map, DCContext.MAP_KEY_METHOD_POSITION);
                ArrayList<Integer> fStatusPosition = get7FStatusPosition(positions);
                String pos = mergePositionToString(fStatusPosition.size() != 0 ? fStatusPosition : positions);
                str = str.replace("@{position}", pos);
                break;
            case "window_min":
                str = str.replace("@{window_min}", "0");
                break;
            case "window_max":
                str = str.replace("@{window_max}", "100");
                break;
            default:
                LogUtils.e(TAG, "当前要处理的@{" + key + "}不存在");
                return str;
        }
        LogUtils.e(TAG, "tts :" + start + " " + end + " " + key + " " + index);

        LogUtils.e(TAG, "tts :" + str);
        return str;

    }

    /**
     * 处理位置是否包含主驾
     */
    public boolean getPositionInFirstRowLeft(HashMap<String, Object> map) {
        ArrayList<Integer> positions = (ArrayList<Integer>) getValueInContext(map, DCContext.MAP_KEY_METHOD_POSITION);
        return positions.contains(0);
    }

    /**
     * 获取想设置的number值
     */
    public int curSetNumber(HashMap<String, Object> map) {
        String numberString = (String) getValueInContext(map, "number");
        if (numberString.contains("%")) {
            numberString = numberString.replace("%", "");
        }
        if (numberString.contains("/")) {
            String[] parts = numberString.split("/");
            double numerator = Double.parseDouble(parts[0]);
            double denominator = Double.parseDouble(parts[1]);
            numberString = ((numerator / denominator) * 100) + "";
        }
        double doubleValue = Double.parseDouble(numberString);
        double roundedUp = Math.ceil(doubleValue); // 向上取整
        int number = (int) roundedUp; // 转换为int
        LogUtils.i(TAG, "curSetNumber number :" + number);
        return number;
    }

    /**
     * 关闭程度转化成打开程度(例子：打开40% = 关闭60%)
     */
    public void closeTransformOpen(HashMap<String, Object> map) {
        int number = curSetNumber(map);
        map.put("number", String.valueOf(100 - number));
    }

    /**
     * 当前儿童锁是否开启
     */
    public boolean isChildLockOpen(HashMap<String, Object> map) {
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();


        if ("H37A".equals(carType) || "H37B".equals(carType)) {
            ArrayList<Integer> positions = (ArrayList<Integer>) getValueInContext(map, DCContext.MAP_KEY_METHOD_POSITION);
            return (positions.contains(2) && operator.getBooleanProp(WindowSignal.WINDOW_CHILD_LOCK, 2))
                    || (positions.contains(3) && operator.getBooleanProp(WindowSignal.WINDOW_CHILD_LOCK, 3));
        }else{
            int leftWinSign = operator.getIntProp(WindowSignal.WINDOW_CHILDLOCKLEFT);
            int rightWinSign = operator.getIntProp(WindowSignal.WINDOW_CHILDLOCKRIGHT);

            if(leftWinSign ==0 && rightWinSign ==0){//代表车窗上锁 儿童锁上锁
                return true;
            }else{
                return false;
            }
        }


    }

    /**
     * 是否有车窗下发7F状态
     */
    public boolean isHas7FStatus(HashMap<String, Object> map) {
        boolean isHas7FStatus = false;
        ArrayList<Integer> positions = (ArrayList<Integer>) getValueInContext(map, DCContext.MAP_KEY_METHOD_POSITION);
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();
        if ("H37A".equals(carType) || "H37B".equals(carType)) {

            ArrayList<Integer> mergePosition = analysisPosition(positions);
            for (int i = 0; i < mergePosition.size(); i++) {
                int curValue = operator.getIntProp(WindowSignal.WINDOW_WINDOW, mergePosition.get(i));
                if (curValue == WINDOW_SEVEN_F) {
                    isHas7FStatus = true;
                }
            }
        }else{
            for (int i = 0; i < positions.size(); i++) {
                int curValue =operator.getIntProp(WindowSignal.WINDOW_DRIVER, positions.get(i));
                if (positions.get(i) == PositionSignal.FIRST_ROW_LEFT) {//驾驶位
                    curValue = operator.getIntProp(WindowSignal.WINDOW_DRIVER, positions.get(i));
                } else if (positions.get(i) == PositionSignal.FIRST_ROW_RIGHT) {//副驾
                    curValue = operator.getIntProp(WindowSignal.WINDOW_PASSENGER, positions.get(i));
                } else if (positions.get(i) == PositionSignal.SECOND_ROW_LEFT || positions.get(i) == PositionSignal.THIRD_ROW_LEFT) {//第二排左
                    curValue = operator.getIntProp(WindowSignal.WINDOW_LEFTREAR, positions.get(i));
                } else if (positions.get(i) == PositionSignal.SECOND_ROW_RIGHT || positions.get(i) ==PositionSignal.THIRD_ROW_RIGHT) {//第二排右
                    curValue = operator.getIntProp(WindowSignal.WINDOW_RIGHTREAR, positions.get(i));
                } else {
                    curValue = operator.getIntProp(WindowSignal.WINDOW_DRIVER, positions.get(i));
                }
                if (curValue == WINDOW_SEVEN_F) {
                    isHas7FStatus = true;
                }
            }
        }


        return isHas7FStatus;
    }

    /**
     * 获取7F车窗位置
     */
    public ArrayList<Integer> get7FStatusPosition(ArrayList<Integer> positions) {
        ArrayList<Integer> positionList = new ArrayList<>();
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();

        if ("H37A".equals(carType) || "H37B".equals(carType)) {
            ArrayList<Integer> mergePosition = analysisPosition(positions);
            for (int i = 0; i < mergePosition.size(); i++) {
                int curValue = operator.getIntProp(WindowSignal.WINDOW_WINDOW, mergePosition.get(i));
                if (curValue == WINDOW_SEVEN_F) {
                    positionList.add(positions.get(i));
                }
            }
        }else{
            for (int i = 0; i < positions.size(); i++) {
                int curValue = operator.getIntProp(WindowSignal.WINDOW_WINDOW, positions.get(i));
                if (positions.get(i) == PositionSignal.FIRST_ROW_LEFT) {//驾驶位
                    curValue = operator.getIntProp(WindowSignal.WINDOW_DRIVER, positions.get(i));
                } else if (positions.get(i) == PositionSignal.FIRST_ROW_RIGHT) {//副驾
                    curValue = operator.getIntProp(WindowSignal.WINDOW_PASSENGER, positions.get(i));
                } else if (positions.get(i) == PositionSignal.SECOND_ROW_LEFT || positions.get(i) == PositionSignal.THIRD_ROW_LEFT) {//第二排左
                    curValue = operator.getIntProp(WindowSignal.WINDOW_LEFTREAR, positions.get(i));
                } else if (positions.get(i) == PositionSignal.SECOND_ROW_RIGHT || positions.get(i) ==PositionSignal.THIRD_ROW_RIGHT) {//第二排右
                    curValue = operator.getIntProp(WindowSignal.WINDOW_RIGHTREAR, positions.get(i));
                } else {
                    curValue = operator.getIntProp(WindowSignal.WINDOW_DRIVER, positions.get(i));
                }
                if (curValue == WINDOW_SEVEN_F) {
                    positionList.add(positions.get(i));
                }
            }
        }

        return positionList;
    }

    public boolean isNumberMoreThan100(HashMap<String, Object> map) {
        int number = curSetNumber(map);
        return number > 100;
    }

    public boolean isNumberLessThan0(HashMap<String, Object> map) {
        int number = curSetNumber(map);
        return number < 0;
    }

    /**
     * 车窗打开程度是否符合条件
     */
    public boolean isEligibleWindow(HashMap<String, Object> map) {
        boolean isEligibleWindow = true;
        String adjustType = (String) getValueInContext(map, "adjust_type");
        LogUtils.d(TAG, "isEligibleWindow adjustType : " + adjustType);
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();

        if ("H37A".equals(carType) || "H37B".equals(carType)) {
            ArrayList<Integer> positions = (ArrayList<Integer>) getValueInContext(map, DCContext.MAP_KEY_METHOD_POSITION);
            ArrayList<Integer> mergePosition = analysisPosition(positions);
            LogUtils.d(TAG, "isEligibleWindow adjustType : " + adjustType);
            if (!TextUtils.isEmpty(adjustType)) {
                for (int position : mergePosition) {
                    int curValue = operator.getIntProp(WindowSignal.WINDOW_WINDOW, position);
                    if (adjustType.equals("decrease")) {
                        if (curValue != 0) {
                            isEligibleWindow = false;
                            break;
                        }
                    } else if (adjustType.equals("increase")) {
                        if (curValue != 100) {
                            isEligibleWindow = false;
                            break;
                        }
                    } else if (adjustType.equals("decrease_to") || adjustType.equals("increase_to")) {
                        int number = curSetNumber(map);
                        if (!(curValue > number - 7 && curValue < number + 7)) {
                            isEligibleWindow = false;
                            break;
                        }
                    } else if (adjustType.equals("set")) {
                        if (map.containsKey("level")) {
                            String level = (String) getValueInContext(map, "level");
                            if (level.equals("max")) {
                                if (curValue != 100) {
                                    isEligibleWindow = false;
                                    break;
                                }
                            } else if (level.equals("min")) {
                                if (curValue != 0) {
                                    isEligibleWindow = false;
                                    break;
                                }
                            } else if (level.equals("low")) {
                                if (position == 16 || position == 64) {
                                    if (curValue < 3 || curValue > 17) {
                                        isEligibleWindow = false;
                                        break;
                                    }
                                }
                                if (position == 256 || position == 1024) {
                                    if (curValue < 11 || curValue > 25) {
                                        isEligibleWindow = false;
                                        break;
                                    }
                                }
                            }
                        } else {
                            int number = curSetNumber(map);
                            if (!(curValue > number - 7 && curValue < number + 7)) {
                                isEligibleWindow = false;
                                break;
                            }
                        }
                    }
                }
            } else {
                String switchType = (String) getValueInContext(map, "switch_type");
                for (int position : mergePosition) {
                    int curValue = operator.getIntProp(WindowSignal.WINDOW_WINDOW, position);
                    if (switchType.equals("open")) {
                        if (curValue != 100) {
                            isEligibleWindow = false;
                            break;
                        }
                    } else if (switchType.equals("close")) {
                        if (curValue != 0) {
                            isEligibleWindow = false;
                            break;
                        }
                    }
                }
            }
        }else{//56C  56D
            List<Integer> positions = getMethodPosition(map);
            if (!TextUtils.isEmpty(adjustType)) {
                for (int position : positions) {
                    int curValue = operator.getIntProp(WindowSignal.WINDOW_DRIVER, position);
//                int curValue = Integer.parseInt(operator.getStringProp(WindowSignal.WINDOW_WINDOW, position));
                    if (position == PositionSignal.FIRST_ROW_LEFT) {//驾驶位
                        curValue = operator.getIntProp(WindowSignal.WINDOW_DRIVER, position);
                    } else if (position == PositionSignal.FIRST_ROW_RIGHT) {//副驾
                        curValue = operator.getIntProp(WindowSignal.WINDOW_PASSENGER, position);
                    } else if (position == PositionSignal.SECOND_ROW_LEFT || position ==PositionSignal.THIRD_ROW_LEFT) {//第二排左
                        curValue = operator.getIntProp(WindowSignal.WINDOW_LEFTREAR, position);
                    } else if (position == PositionSignal.SECOND_ROW_RIGHT || position ==PositionSignal.THIRD_ROW_RIGHT) {//第二排右
                        curValue = operator.getIntProp(WindowSignal.WINDOW_RIGHTREAR, position);
                    } else {
                        curValue = operator.getIntProp(WindowSignal.WINDOW_DRIVER, position);
                    }
                    if (adjustType.equals("decrease")) {
                        if (curValue != 0) {
                            isEligibleWindow = false;
                            break;
                        }
                    } else if (adjustType.equals("increase")) {
                        if (curValue != 100) {
                            isEligibleWindow = false;
                            break;
                        }
                    } else if (adjustType.equals("decrease_to") || adjustType.equals("increase_to")) {
                        int number = curSetNumber(map);
                        if (!(curValue > number - 7 && curValue < number + 7)) {
                            isEligibleWindow = false;
                            break;
                        }
                    } else if (adjustType.equals("set")) {
                        if (map.containsKey("level")) {
                            String level = (String) getValueInContext(map, "level");
                            if (level.equals("max")) {
                                if (curValue != 100) {
                                    isEligibleWindow = false;
                                    break;
                                }
                            }else if (level.equals("min")) {
                                if (curValue != 0) {
                                    isEligibleWindow = false;
                                    break;
                                }
                            } else if (level.equals("low")) {
                                if (position == PositionSignal.FIRST_ROW_LEFT || position == PositionSignal.FIRST_ROW_RIGHT) {
                                    if (curValue < 3 || curValue > 17) {
                                        isEligibleWindow = false;
                                        break;
                                    }
                                }
                                if (position == PositionSignal.SECOND_ROW_LEFT || position == PositionSignal.SECOND_ROW_RIGHT || position == PositionSignal.THIRD_ROW_LEFT || position ==PositionSignal.THIRD_ROW_RIGHT ) {
                                    if (curValue < 11 || curValue > 25) {
                                        isEligibleWindow = false;
                                        break;
                                    }
                                }
                            }
                        } else {
                            int number = curSetNumber(map);
                            if (!(curValue > number - 7 && curValue < number + 7)) {
                                isEligibleWindow = false;
                                break;
                            }
                        }
                    }
                }
            } else {
                String switchType = (String) getValueInContext(map, "switch_type");
                for (int position : positions) {
                    int curValue = operator.getIntProp(WindowSignal.WINDOW_DRIVER, position);
                    if (position == PositionSignal.FIRST_ROW_LEFT) {//驾驶位
                        curValue = operator.getIntProp(WindowSignal.WINDOW_DRIVER, position);
                    } else if (position == PositionSignal.FIRST_ROW_RIGHT) {//副驾
                        curValue = operator.getIntProp(WindowSignal.WINDOW_PASSENGER, position);
                    } else if (position == PositionSignal.SECOND_ROW_LEFT || position ==PositionSignal.THIRD_ROW_LEFT) {//第二排左
                        curValue = operator.getIntProp(WindowSignal.WINDOW_LEFTREAR, position);
                    } else if (position == PositionSignal.SECOND_ROW_RIGHT || position ==PositionSignal.THIRD_ROW_RIGHT) {//第二排右
                        curValue = operator.getIntProp(WindowSignal.WINDOW_RIGHTREAR, position);
                    } else {
                        curValue = operator.getIntProp(WindowSignal.WINDOW_DRIVER, position);
                    }
                    if (switchType.equals("open")) {
                        if (curValue != 100) {
                            isEligibleWindow = false;
                            break;
                        }
                    } else if (switchType.equals("close")) {
                        if (curValue != 0) {
                            isEligibleWindow = false;
                            break;
                        }
                    }
                }
            }
        }


        return isEligibleWindow;
    }

    /**
     * 车窗的打开和关闭指令执行
     */
    public void setWindowStatus(HashMap<String, Object> map) throws JSONException {
        String switchType = (String) getValueInContext(map, "switch_type");
        int curSpeedRange = curSpeedRange(map);
        ArrayList<Integer> positions = (ArrayList<Integer>) getValueInContext(map, DCContext.MAP_KEY_METHOD_POSITION);
        LogUtils.d(TAG, "setWindowStatus switchType : " + switchType + " curSpeedRange :" + curSpeedRange);

        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();
        if ("H37A".equals(carType) || "H37B".equals(carType)) {
            ArrayList<Integer> mergePosition = analysisPosition(positions);
            LogUtils.d(TAG, "setWindowStatus switchType : " + switchType + " curSpeedRange :" + curSpeedRange);
            JSONObject objects = new JSONObject();
            for (int position : mergePosition) {
                int curValue = operator.getIntProp(WindowSignal.WINDOW_WINDOW, position);
                if (switchType.equals("open")) {
                    if (curValue != 100 && curValue != WINDOW_SEVEN_F) {
                        if (curSpeedRange <= 80) {
                            objects.put(getPositionPropValue(position), 100);
                        } else {
                            if (curValue < 20) {
                                objects.put(getPositionPropValue(position), 20);
                            }
                        }
                    }
                } else if (switchType.equals("close")) {
                    if (curValue != 0 && curValue != WINDOW_SEVEN_F) {
                        objects.put(getPositionPropValue(position), 0);
                    }
                }
            }
            operator.setStringProp(WindowSignal.WINDOW_WINDOW, objects.toString());
        }else{//H56C  H56D
            int newValue = 0;
            for (int position : positions) {
                int curValue = operator.getIntProp(WindowSignal.WINDOW_DRIVER, position);
                if (position == PositionSignal.FIRST_ROW_LEFT) {//驾驶位
                    curValue = operator.getIntProp(WindowSignal.WINDOW_DRIVER, position);
                } else if (position == PositionSignal.FIRST_ROW_RIGHT) {//副驾
                    curValue = operator.getIntProp(WindowSignal.WINDOW_PASSENGER, position);
                } else if (position == PositionSignal.SECOND_ROW_LEFT || position ==PositionSignal.THIRD_ROW_LEFT) {//第二排左
                    curValue = operator.getIntProp(WindowSignal.WINDOW_LEFTREAR, position);
                } else if (position == PositionSignal.SECOND_ROW_RIGHT || position ==PositionSignal.THIRD_ROW_RIGHT) {//第二排右
                    curValue = operator.getIntProp(WindowSignal.WINDOW_RIGHTREAR, position);
                } else {
                    curValue = operator.getIntProp(WindowSignal.WINDOW_DRIVER, position);
                }
                if (switchType.equals("open")) {
                    if (curValue != 100 && curValue != WINDOW_SEVEN_F) {
                        if (curSpeedRange <= 80) {
                            newValue = 100;
                        } else {
                            if (curValue < 20) {
                                newValue = 20;
                            }
                        }
                    }
                } else if (switchType.equals("close")) {
                    if (curValue != 0 && curValue != WINDOW_SEVEN_F) {
                        newValue = 0;
                    }
                }
                operator.setStringProp(WindowSignal.WINDOW_WINDOW, position, String.valueOf(newValue));
            }
        }


    }

    /**
     * 执行具体车窗调节操作
     */
    public void accordingSituationOpenWindow(HashMap<String, Object> map) throws JSONException {
        String adjustType = (String) getValueInContext(map, "adjust_type");
        ArrayList<Integer> positions = (ArrayList<Integer>) getValueInContext(map, DCContext.MAP_KEY_METHOD_POSITION);
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();

        if ("H37A".equals(carType) || "H37B".equals(carType)) {
            ArrayList<Integer> mergePosition = analysisPosition(positions);
            JSONObject object = new JSONObject();
            for (int position : mergePosition) {
                int curValue = operator.getIntProp(WindowSignal.WINDOW_WINDOW, position);
                //车窗调低 = 打开车窗   车窗调高 = 关闭车窗
                if (adjustType.equals("increase")) {
                    //当前不能为完全打开和7F状态
                    if (curValue != 100 && curValue != WINDOW_SEVEN_F) {
                        if (map.containsKey("number")) {
                            int number = curSetNumber(map);
                            //车窗调低number(如果number > 10 就设置成当前值+number，否则就是当前值+10)
                            if (number > 10) {
                                object.put(getPositionPropValue(position), Math.min((curValue + number), 100));
                            } else {
                                object.put(getPositionPropValue(position), Math.min((curValue + 10), 100));
                            }
                        } else {
                            //车窗调低一点
                            object.put(getPositionPropValue(position), Math.min((curValue + 10), 100));
                        }
                    }
                } else if (adjustType.equals("increase_to") || adjustType.equals("decrease_to")) {
                    //车窗调低到&调高到(绝对调节)
                    int number = curSetNumber(map);
                    if (!(number - 7 < curValue && curValue < number + 7) && curValue != WINDOW_SEVEN_F) {
                        object.put(getPositionPropValue(position), number);
                    }
                } else if (adjustType.equals("decrease")) {
                    //当前不能为完全关闭和7F状态
                    if (curValue != 0 && curValue != WINDOW_SEVEN_F) {
                        if (map.containsKey("number")) {
                            int number = curSetNumber(map);
                            //车窗调高number(如果number > 10 就设置成当前值-number，否则就是当前值-10)
                            if (number > 10) {
                                object.put(getPositionPropValue(position), Math.max((curValue - number), 0));
                            } else {
                                object.put(getPositionPropValue(position), Math.max((curValue - 10), 0));
                            }
                        } else {
                            //车窗调高一点
                            object.put(getPositionPropValue(position), Math.max((curValue - 10), 0));
                        }
                    }
                } else if (adjustType.equals("set")) {
                    if (map.containsKey("level")) {
                        //车窗设置指定位置(车窗set只有level=max 和 level=low的场景)
                        String level = (String) getValueInContext(map, "level");
                        if (level.equals("max")) {
                            //车窗开到底(开到最大)
                            if (curValue != 100 && curValue != WINDOW_SEVEN_F) {
                                object.put(getPositionPropValue(position), 100);
                            }
                        } else if (level.equals("low")) {
                            //车窗设置时不能是7F状态
                            if (curValue != WINDOW_SEVEN_F) {
                                //车窗通风,前排开启成10，后排开启成18
                                if (position == 16 || position == 64) {
                                    if (curValue < 3 || curValue > 17) {
                                        object.put(getPositionPropValue(position), 10);
                                    }
                                }
                                if (position == 256 || position == 1024) {
                                    if (curValue < 11 || curValue > 25) {
                                        object.put(getPositionPropValue(position), 18);
                                    }
                                }
                            }
                        }
                    } else {
                        if (map.containsKey("number")) {
                            int number = curSetNumber(map);
                            //车窗调低到&调高到(绝对调节)
                            if (!(number - 7 < curValue && curValue < number + 7) && curValue != WINDOW_SEVEN_F) {
                                object.put(getPositionPropValue(position), number);
                            }
                        }
                    }
                }
            }
            operator.setStringProp(WindowSignal.WINDOW_WINDOW, object.toString());
        }else{//H56C  H56D
            for (int position : positions) {
                int curValue = operator.getIntProp(WindowSignal.WINDOW_DRIVER, position);
                if (position == PositionSignal.FIRST_ROW_LEFT) {//驾驶位
                    curValue = operator.getIntProp(WindowSignal.WINDOW_DRIVER, position);
                } else if (position == PositionSignal.FIRST_ROW_RIGHT) {//副驾
                    curValue = operator.getIntProp(WindowSignal.WINDOW_PASSENGER, position);
                } else if (position == PositionSignal.SECOND_ROW_LEFT || position ==PositionSignal.THIRD_ROW_LEFT ) {//第二排左
                    curValue = operator.getIntProp(WindowSignal.WINDOW_LEFTREAR, position);
                } else if (position == PositionSignal.SECOND_ROW_RIGHT ||position ==PositionSignal.THIRD_ROW_RIGHT ) {//第二排右
                    curValue = operator.getIntProp(WindowSignal.WINDOW_RIGHTREAR, position);
                } else {
                    curValue = operator.getIntProp(WindowSignal.WINDOW_DRIVER, position);
                }
                int newValue = curValue;
                //车窗调低 = 打开车窗   车窗调高 = 关闭车窗
                if (adjustType.equals("increase")) {
                    //当前不能为完全打开和7F状态
                    if (curValue != 100 && curValue != WINDOW_SEVEN_F) {
                        if (map.containsKey("number")) {
                            int number = curSetNumber(map);
                            if (number > 10) {
                                newValue = Math.min((curValue + number), 100);
                            } else {
                                newValue = Math.min((curValue + 10), 100);
                            }
                        } else {
                            //车窗调低一点
                            newValue = Math.min((curValue + 10), 100);
                        }
                    }
                } else if (adjustType.equals("increase_to") || adjustType.equals("decrease_to")) {
                    //车窗调低到&调高到(绝对调节)
                    int number = curSetNumber(map);
                    if (!(number - 7 < curValue && curValue < number + 7) && curValue != WINDOW_SEVEN_F) {
                        newValue = number;
                    }
                } else if (adjustType.equals("decrease")) {
                    //当前不能为完全关闭和7F状态
                    if (curValue != 0 && curValue != WINDOW_SEVEN_F) {
                        if (map.containsKey("number")) {
                            int number = curSetNumber(map);
                            if (number > 10) {
                                newValue = Math.max((curValue - number), 0);
                            } else {
                                newValue = Math.max((curValue - 10), 0);
                            }
                        } else {
                            //车窗调高一点
                            newValue = Math.max((curValue - 10), 0);
                        }
                    }
                } else if (adjustType.equals("set")) {
                    if (map.containsKey("level")) {
                        //车窗设置指定位置(车窗set只有level=max 和 level=low的场景)
                        String level = (String) getValueInContext(map, "level");
                        if (level.equals("max")) {
                            //车窗开到底(开到最大)
                            if (curValue != 100 && curValue != WINDOW_SEVEN_F) {
                                newValue = 100;
                            }
                        } else if (level.equals("low")) {
                            //车窗设置时不能是7F状态
                            if (curValue != WINDOW_SEVEN_F) {
                                //车窗通风,前排开启成10，后排开启成18
                                if (position == PositionSignal.FIRST_ROW_LEFT || position == PositionSignal.FIRST_ROW_RIGHT) {
                                    newValue = 10;
                                }
                                if (position == PositionSignal.SECOND_ROW_LEFT || position == PositionSignal.SECOND_ROW_RIGHT || position ==PositionSignal.THIRD_ROW_LEFT || position ==PositionSignal.THIRD_ROW_RIGHT) {
                                    newValue = 18;
                                }
                            }
                        }
                    } else {
                        int number = curSetNumber(map);
                        //车窗调低到&调高到(绝对调节)
                        if (!(number - 7 < curValue && curValue < number + 7) && curValue != WINDOW_SEVEN_F) {
                            newValue = number;
                        }
                    }
                }
                if (newValue != curValue) {
//                operator.setIntProp(WindowSignal.WINDOW_WINDOW, position, newValue);
                    switch (position) {
                        case PositionSignal.FIRST_ROW_LEFT:
//                        jsons = new CarPropertyValue<>(H56C.IVI_BodySet4_IVI_FLWINDOWHORIZONTALREQ, object.toString());//默认是主驾
//                        CarPropUtils.getInstance().setIntProp(H56C.IVI_BodySet4_IVI_FLWINDOWHORIZONTALREQ, newValue);//默认是主驾
                            operator.setIntProp(WindowSignal.WINDOW_SETDRIVER, newValue);//默认是主驾
                            break;
                        case PositionSignal.FIRST_ROW_RIGHT:
//                        CarPropUtils.getInstance().setIntProp(H56C.IVI_BodySet4_IVI_FRWINDOWHORIZONTALREQ, newValue);//副驾
                            operator.setIntProp(WindowSignal.WINDOW_SETPASSENGER, newValue);//副驾
                            break;
                        case PositionSignal.SECOND_ROW_LEFT:
                        case PositionSignal.THIRD_ROW_LEFT:
//                        CarPropUtils.getInstance().setIntProp(H56C.IVI_BodySet4_IVI_RLWINDOWHORIZONTALREQ, newValue);//二排左
                            operator.setIntProp(WindowSignal.WINDOW_SETLEFTREAR, newValue);//二排左
                            break;
                        case PositionSignal.SECOND_ROW_RIGHT:
                        case PositionSignal.THIRD_ROW_RIGHT:
//                        CarPropUtils.getInstance().setIntProp(H56C.IVI_BodySet4_IVI_RRWINDOWHORIZONTALREQ, newValue);//二排右
                            operator.setIntProp(WindowSignal.WINDOW_SETRIGHTREAR, newValue);//二排右
                            break;
                        default:
//                        CarPropUtils.getInstance().setIntProp(H56C.IVI_BodySet4_IVI_FLWINDOWHORIZONTALREQ, newValue);//默认是主驾
                            operator.setIntProp(WindowSignal.WINDOW_SETDRIVER, newValue);//默认是主驾
                            break;
                    }
                }
            }
        }


    }

    /**
     * 打开到20%
     */
    public void openWindowTo20(HashMap<String, Object> map) throws JSONException{
        ArrayList<Integer> positions = (ArrayList<Integer>) getValueInContext(map, DCContext.MAP_KEY_METHOD_POSITION);
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();//获取车型

        if ("H37A".equals(carType) || "H37B".equals(carType)) {
            ArrayList<Integer> mergePosition = analysisPosition(positions);
            JSONObject object = new JSONObject();
            for (int position : mergePosition) {
                int curValue = operator.getIntProp(WindowSignal.WINDOW_WINDOW, position);
                if (curValue != 20 && curValue != WINDOW_SEVEN_F) {
                    object.put(getPositionPropValue(position), 20);
                }
            }
            operator.setStringProp(WindowSignal.WINDOW_WINDOW, object.toString());
        }else{
            for (int position : positions) {
                int curValue = operator.getIntProp(WindowSignal.WINDOW_WINDOW, position);
                if (position == PositionSignal.FIRST_ROW_LEFT) {//驾驶位
                    curValue = operator.getIntProp(WindowSignal.WINDOW_DRIVER, position);
                } else if (position == PositionSignal.FIRST_ROW_RIGHT) {//副驾
                    curValue = operator.getIntProp(WindowSignal.WINDOW_PASSENGER, position);
                } else if (position == PositionSignal.SECOND_ROW_LEFT || position== PositionSignal.THIRD_ROW_LEFT) {//第二排左
                    curValue = operator.getIntProp(WindowSignal.WINDOW_LEFTREAR, position);
                } else if (position == PositionSignal.SECOND_ROW_RIGHT || position ==PositionSignal.THIRD_ROW_RIGHT ) {//第二排右
                    curValue = operator.getIntProp(WindowSignal.WINDOW_RIGHTREAR, position);
                } else {
                    curValue = operator.getIntProp(WindowSignal.WINDOW_DRIVER, position);
                }
                if (curValue != 20 && curValue != WINDOW_SEVEN_F) {
                    operator.setStringProp(WindowSignal.WINDOW_WINDOW, position, 20 + "");
                }
            }
        }


    }

    /**
     * 打开到100%
     */
    public void openWindowTo100(HashMap<String, Object> map)  throws JSONException {
        ArrayList<Integer> positions = (ArrayList<Integer>) getValueInContext(map, DCContext.MAP_KEY_METHOD_POSITION);
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();//获取车型

        if ("H37A".equals(carType) || "H37B".equals(carType)) {
            ArrayList<Integer> mergePosition = analysisPosition(positions);
            JSONObject object = new JSONObject();
            for (int position : mergePosition) {
                int curValue = operator.getIntProp(WindowSignal.WINDOW_WINDOW, position);
                if (curValue != 100 && curValue != WINDOW_SEVEN_F) {
                    object.put(getPositionPropValue(position), 100);
                }
            }
            operator.setStringProp(WindowSignal.WINDOW_WINDOW, object.toString());
        }else{//H56C H56D
            for (int position : positions) {
                int curValue = operator.getIntProp(WindowSignal.WINDOW_WINDOW, position);
                if (position == PositionSignal.FIRST_ROW_LEFT) {//驾驶位
                    curValue = operator.getIntProp(WindowSignal.WINDOW_DRIVER, position);
                } else if (position == PositionSignal.FIRST_ROW_RIGHT) {//副驾
                    curValue = operator.getIntProp(WindowSignal.WINDOW_PASSENGER, position);
                } else if (position == PositionSignal.SECOND_ROW_LEFT || position== PositionSignal.THIRD_ROW_LEFT) {//第二排左
                    curValue = operator.getIntProp(WindowSignal.WINDOW_LEFTREAR, position);
                } else if (position == PositionSignal.SECOND_ROW_RIGHT || position ==PositionSignal.THIRD_ROW_RIGHT) {//第二排右
                    curValue = operator.getIntProp(WindowSignal.WINDOW_RIGHTREAR, position);
                } else {
                    curValue = operator.getIntProp(WindowSignal.WINDOW_DRIVER, position);
                }
                if (curValue != 100 && curValue != WINDOW_SEVEN_F) {
                    operator.setStringProp(WindowSignal.WINDOW_WINDOW, position, 100 + "");
                }
            }
        }

    }

    /**
     * 打开到0%
     */
    public void openWindowTo0(HashMap<String, Object> map)  throws JSONException{
        ArrayList<Integer> positions = (ArrayList<Integer>) getValueInContext(map, DCContext.MAP_KEY_METHOD_POSITION);

        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();//获取车型

        if ("H37A".equals(carType) || "H37B".equals(carType)) {
            ArrayList<Integer> mergePosition = analysisPosition(positions);
            JSONObject object = new JSONObject();
            for (int position : mergePosition) {
                int curValue = operator.getIntProp(WindowSignal.WINDOW_WINDOW, position);
                if (curValue != 0 && curValue != WINDOW_SEVEN_F) {
                    object.put(getPositionPropValue(position), 0);
                }
            }
            operator.setStringProp(WindowSignal.WINDOW_WINDOW, object.toString());
        }else{//H56C H56D
            for (int position : positions) {
                int curValue = operator.getIntProp(WindowSignal.WINDOW_WINDOW, position);
                if (position == PositionSignal.FIRST_ROW_LEFT) {//驾驶位
                    curValue = operator.getIntProp(WindowSignal.WINDOW_DRIVER, position);
                } else if (position == PositionSignal.FIRST_ROW_RIGHT) {//副驾
                    curValue = operator.getIntProp(WindowSignal.WINDOW_PASSENGER, position);
                } else if (position == PositionSignal.SECOND_ROW_LEFT || position== PositionSignal.THIRD_ROW_LEFT) {//第二排左
                    curValue = operator.getIntProp(WindowSignal.WINDOW_LEFTREAR, position);
                } else if (position == PositionSignal.SECOND_ROW_RIGHT || position== PositionSignal.THIRD_ROW_RIGHT) {//第二排右
                    curValue = operator.getIntProp(WindowSignal.WINDOW_RIGHTREAR, position);
                } else {
                    curValue = operator.getIntProp(WindowSignal.WINDOW_DRIVER, position);
                }
                if (curValue != 0 && curValue != WINDOW_SEVEN_F) {
                    operator.setStringProp(WindowSignal.WINDOW_WINDOW, position, 0 + "");
                }
            }
        }


    }

    /**
     * 所有车窗开启程度是否都小于20%
     */
    public boolean isAllWindowLess20(HashMap<String, Object> map) {
        boolean isAllWindowLess20 = true;
        ArrayList<Integer> positions = (ArrayList<Integer>) getValueInContext(map, DCContext.MAP_KEY_METHOD_POSITION);
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();//获取车型

        if ("H37A".equals(carType) || "H37B".equals(carType)) {
            ArrayList<Integer> mergePosition = analysisPosition(positions);
            for (int position : mergePosition) {
                int curValue = operator.getIntProp(WindowSignal.WINDOW_WINDOW, position);
                if (curValue >= 20) {
                    isAllWindowLess20 = false;
                }
            }
        }else{//H56C H56D
            for (int position : positions) {
                int curValue = operator.getIntProp(WindowSignal.WINDOW_WINDOW, position);
                if (position == PositionSignal.FIRST_ROW_LEFT) {//驾驶位
                    curValue = operator.getIntProp(WindowSignal.WINDOW_DRIVER, position);
                } else if (position == PositionSignal.FIRST_ROW_RIGHT) {//副驾
                    curValue = operator.getIntProp(WindowSignal.WINDOW_PASSENGER, position);
                } else if (position == PositionSignal.SECOND_ROW_LEFT || position== PositionSignal.THIRD_ROW_LEFT) {//第二排左
                    curValue = operator.getIntProp(WindowSignal.WINDOW_LEFTREAR, position);
                } else if (position == PositionSignal.SECOND_ROW_RIGHT || position== PositionSignal.THIRD_ROW_RIGHT) {//第二排右
                    curValue = operator.getIntProp(WindowSignal.WINDOW_RIGHTREAR, position);
                } else {
                    curValue = operator.getIntProp(WindowSignal.WINDOW_DRIVER, position);
                }
                if (curValue >= 20) {
                    isAllWindowLess20 = false;
                }
            }
        }
        return isAllWindowLess20;
    }

    /**
     * 所有车窗开启程度是否都大于或等于20%
     */
    public boolean isAllWindowMore20(HashMap<String, Object> map) {
        boolean isAllWindowMore20 = true;
        ArrayList<Integer> positions = (ArrayList<Integer>) getValueInContext(map, DCContext.MAP_KEY_METHOD_POSITION);
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();//获取车型

        if ("H37A".equals(carType) || "H37B".equals(carType)) {
            ArrayList<Integer> mergePosition = analysisPosition(positions);
            for (int position : mergePosition) {
                int curValue = operator.getIntProp(WindowSignal.WINDOW_WINDOW, position);
                if (curValue < 20) {
                    isAllWindowMore20 = false;
                }
            }
        }else{//H56C H56D
            for (int position : positions) {
                int curValue = operator.getIntProp(WindowSignal.WINDOW_WINDOW, position);
                if (position == PositionSignal.FIRST_ROW_LEFT) {//驾驶位
                    curValue = operator.getIntProp(WindowSignal.WINDOW_DRIVER, position);
                } else if (position == PositionSignal.FIRST_ROW_RIGHT) {//副驾
                    curValue = operator.getIntProp(WindowSignal.WINDOW_PASSENGER, position);
                } else if (position == PositionSignal.SECOND_ROW_LEFT || position== PositionSignal.THIRD_ROW_LEFT) {//第二排左
                    curValue = operator.getIntProp(WindowSignal.WINDOW_LEFTREAR, position);
                } else if (position == PositionSignal.SECOND_ROW_RIGHT || position== PositionSignal.THIRD_ROW_RIGHT) {//第二排右
                    curValue = operator.getIntProp(WindowSignal.WINDOW_RIGHTREAR, position);
                } else {
                    curValue = operator.getIntProp(WindowSignal.WINDOW_DRIVER, position);
                }
                if (curValue < 20) {
                    isAllWindowMore20 = false;
                }
            }
        }

        return isAllWindowMore20;
    }



    /**
     * 当前车速
     */
    public int curSpeedRange(HashMap<String, Object> map) {
        int curDriveSpeed = (int) operator.getFloatProp(CommonSignal.COMMON_SPEED_INFO);
        return curDriveSpeed;
    }

    /**
     * 获取执行的座椅位置
     */
    public int getPositionSize(HashMap<String, Object> map) {
        int positions = getCurPositionSize(map);
        LogUtils.e(TAG, "getPositionSize :" + positions);
        return positions;
    }



    private String mergePositionToString(ArrayList<Integer> positionList) {
        int[] arr ;
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();//获取车型
        //3个位置的一语多意图，待处理。
        if ("H37A".equals(carType) || "H37B".equals(carType)) {
            arr = new int[4];
            if (positionList.size() == 4 || positionList.size() == 3) {
                return "全车";
            }
        }else{//H56C H56D
            arr = new int[6];//代表6个位置
            if (positionList.size() >3 ) {
                return "全车";
            }
        }

        if (positionList.size() == 1) {
            switch (positionList.get(0)) {
                case 0:
                    return "主驾";
                case 2:
                case 4:
                    return "左后";
                case 1:
                    return "副驾";
                case 3:
                case 5:
                    return "右后";
            }
        }
        //表示是两个位置。
        //0表示没有，1表示有
//        int[] arr = new int[6];
        for (int i = 0; i < positionList.size(); i++) {
            arr[positionList.get(i)] = 1;
        }
        if (arr[0] == 1 && arr[1] == 1) {
            return "前排";
        }
        if (arr[0] == 1 && arr[2] == 1) {
            return "左侧";
        }
        if (arr[1] == 1 && arr[3] == 1) {
            return "右侧";
        }
        if (arr[2] == 1 && arr[3] == 1) {
            return "后排";
        }
        if (arr[0] == 1 && arr[3] == 1) {
            return "左前和右后";
        }
        if (arr[1] == 1 && arr[2] == 1) {
            return "右前和左后";
        }
        //出错了。
        return "全车";
    }

    /**
     * 对{0.1.2.3}的位置转化为VehicleArea位置信息
     */
    private ArrayList<Integer> analysisPosition(ArrayList<Integer> positions) {
        ArrayList<Integer> mergePosition = new ArrayList<>();
        for (int i = 0; i < positions.size(); i++) {
            switch (positions.get(i)) {
                case 0:
                    if (!mergePosition.contains(16)) {
                        mergePosition.add(16);
                    }
                    break;
                case 1:
                    if (!mergePosition.contains(64)) {
                        mergePosition.add(64);
                    }
                    break;
                case 2:
                    if (!mergePosition.contains(256)) {
                        mergePosition.add(256);
                    }
                    break;
                case 3:
                    if (!mergePosition.contains(1024)) {
                        mergePosition.add(1024);
                    }
                    break;
            }
        }
        return mergePosition;
    }


    /**
     * 通过位置获取对应的propid的value
     */
    public String getPositionPropValue(int key) {
        switch (key) {
            case 16:
                return "WndFLPostionCmd";
            case 64:
                return "WndFRPostionCmd";
            case 256:
                return "WndRLPostionCmd";
            case 1024:
                return "WndRRPostionCmd";
        }
        return "WndFLPostionCmd";
    }

    public boolean is56Car(HashMap<String, Object> map){
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();//获取车型
        if ("H56C".equals(carType) || "H56D".equals(carType)) {
            return true;
        }else{//H56C H56D
            return false;
        }
    }
}
