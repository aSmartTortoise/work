package com.voyah.ai.logic.dc;

import android.text.TextUtils;

import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.constants.ICommon;
import com.voice.sdk.device.carservice.constants.ILamp;
import com.voice.sdk.device.carservice.dc.ReadingLightInterface;
import com.voice.sdk.device.carservice.dc.context.DCContext;
import com.voice.sdk.device.carservice.signal.LightSignal;
import com.voice.sdk.device.carservice.signal.PositionSignal;
import com.voice.sdk.device.carservice.signal.ReadingLightSignal;
import com.voice.sdk.util.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class InLightControlImpl extends AbsDevices {

    private static final String TAG = "OutLightControlImpl";
    private final ReadingLightInterface readingLight;

    public InLightControlImpl() {
        super();
        readingLight = DeviceHolder.INS().getDevices().getCarService().getReadingLight();
    }

    @Override
    public String getDomain() {
        return "InLight";
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
                String pos = mergePositionToString(map);
                str = str.replace("@{position}", pos);
                break;
            default:
                LogUtils.e(TAG, "当前要处理的@{" + key + "}不存在");
                return str;
        }
        LogUtils.e(TAG, "tts :" + start + " " + end + " " + key + " " + index);

        LogUtils.e(TAG, "tts :" + str);
        return str;
    }
    //判断车型是否是H56
    public boolean isH56CorH56D(HashMap<String, Object> map){
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();//获取车型
        if (carType.equals("H56C") || carType.equals("H56D")) {
            return true;
        }else {
            return false;
        }
    }

    //(航空灯的判断)判断position是否是指定第二排，或者是否是二排声源
    public boolean isSecondSideSound(HashMap<String, Object> map){
        boolean isSecondSide = false;//默认没有指定第二排，或者不是二排声源  false 不是指定二排  true指定二排
        String positions = getOneMapValue("positions", map);
        if (!TextUtils.isEmpty(positions)) {
            switch (positions){
                case "second_side"://二排
                case "second_row_left"://二排左
                case "second_row_right"://二排右
                case "second_row_mid"://二排中间
                case "rear_side"://后排
                case "rear_side_mid"://后排中
                case "rear_side_left"://左后
                case "rear_side_right"://右后
                case "left_side":
                case "right_side":
                case "total_car"://全车
                    isSecondSide = true;
                    break;
                default://其他位置
                    break;
            }
        }else {//根据音区判断
            ArrayList<Integer> curSoundPositions = (ArrayList<Integer>) curSoundPositions(map);
            int curPosition = curSoundPositions.get(0);
            if(curPosition ==2 ||curPosition ==3 ){//如果是二排声源
                isSecondSide = true;
            }
        }
        LogUtils.e(TAG, "isSecondSideSound=========isSecondSide====== :" + isSecondSide);
        return isSecondSide;
    }
    public boolean isCeilingLights(HashMap<String, Object> map) {
        if (map.containsKey("inLight_type")) {
            String inLight_type = (String) getValueInContext(map, "inLight_type");
            return inLight_type.equals("ceiling_lights");
        }
        return false;
    }

    public boolean isDomeLights(HashMap<String, Object> map) {
        if (map.containsKey("inLight_type")) {
            String inLight_type = (String) getValueInContext(map, "inLight_type");
            return inLight_type.equals("dome_lights");
        }
        return false;
    }

    public void setAllLights(HashMap<String, Object> map) {
        //打开所有阅读灯和航空灯
        String switchType = (String) getValueInContext(map, "switch_type");
        operator.setIntProp(ReadingLightSignal.DOME_LIGHT_SWITCH, switchType.equals("open") ? 2 : 1);
    }

    public boolean isHasPosition(HashMap<String, Object> map) {
        String positions = (String) map.get("positions");
        return positions != null;
    }

    public void setCeilingLightsState(HashMap<String, Object> map) {
        String switchType = (String) getValueInContext(map, "switch_type");
//        operator.setIntProp(ReadingLightSignal.DOME_LIGHT_SWITCH, switchType.equals("open") ? 2 : 1);
        String positions = getOneMapValue("positions", map);
        if (!TextUtils.isEmpty(positions)) {
            switch (positions){
                case "second_row_left"://二排左
                case "left_side":
                case "rear_side_left"://左后
                    operator.setIntProp(LightSignal.LIGHT_CEILINGLEFT, switchType.equals("open") ? 2 : 1);
                    break;
                case "second_row_right"://二排右
                case "right_side":
                case "rear_side_right"://右后
                    operator.setIntProp(LightSignal.LIGHT_CEILINGRIGHT, switchType.equals("open") ? 2 : 1);
                    break;
                case "second_side"://二排
                case "rear_side"://后排
                case "second_row_mid"://二排中间
                case "rear_side_mid"://后排中
                case "total_car"://全车
                    operator.setIntProp(LightSignal.LIGHT_CEILINGLEFT, switchType.equals("open") ? 2 : 1);
                    operator.setIntProp(LightSignal.LIGHT_CEILINGRIGHT, switchType.equals("open") ? 2 : 1);
                    break;
                default://其他位置
                    break;
            }
        }else {//根据音区判断
            ArrayList<Integer> curSoundPositions = (ArrayList<Integer>) curSoundPositions(map);
            int curPosition = curSoundPositions.get(0);
            if(curPosition ==2){//如果是二排左
                operator.setIntProp(LightSignal.LIGHT_CEILINGLEFT, switchType.equals("open") ? 2 : 1);
            } else if (curPosition ==3) {//如果是二排右
                operator.setIntProp(LightSignal.LIGHT_CEILINGRIGHT, switchType.equals("open") ? 2 : 1);
            }else{
                LogUtils.e(TAG, "setCeilingLightsState======声源位置出错===声源====="+curPosition);
            }
        }
    }

    public boolean isEffectionPosition(HashMap<String, Object> map) {
        if (map.containsKey("positions")) {
            String positions = (String) map.get("positions");
            if (positions != null) {
                if (positions.contains("outside_car")) {
                    String[] positionList = positions.split(",");
                    if (positionList.length > 1) {
                        return false;
                    } else {
                        if (map.containsKey("inLight_type")) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public boolean isOutSidePositions(HashMap<String, Object> map) {
        if (map.containsKey("positions")) {
            String positions = (String) map.get("positions");
            if (positions != null) {
                if (positions.contains("outside_car")) {
                    return true;
                }
            } else {
                if (!map.containsKey("inLight_type")) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isAutomaticHeadlampsOpened(HashMap<String, Object> map) {
        int value = operator.getIntProp(LightSignal.LAMP_MODE);
        LogUtils.e(TAG, "isAutomaticHeadlampsOpened : " + value);
        return value == ILamp.IModes.AUTO;
    }

    public void openFogLights(HashMap<String, Object> map) {
        //1.打开后雾灯 需要先打开近光灯 再打开后雾灯 2.打开前雾灯时，由于没有前雾灯，所以也是打开后雾灯
        int value = operator.getIntProp(LightSignal.LAMP_SWITCH, PositionSignal.FIRST_ROW_LEFT);
        if (value != ICommon.Switch.ON) {
            String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();//获取车型
            if ("H37A".equals(carType) || "H37B".equals(carType)) {
                operator.setIntProp(LightSignal.LAMP_MODE, ILamp.IMode.LOW);
            }else{//H56C H56D
                operator.setIntProp(LightSignal.LOW_BEAM, ILamp.ISetting.LOW);
            }

        }
        operator.setIntProp(LightSignal.LAMP_FOG, PositionSignal.ALL, ICommon.Switch.ON);
    }

    public void setAutomaticHeadlampsState(HashMap<String, Object> map) {
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();//获取车型
        if ("H37A".equals(carType) || "H37B".equals(carType)) {
            operator.setIntProp(LightSignal.LAMP_MODE, ILamp.IMode.AUTO);
        }else{//H56C H56D
            operator.setIntProp(LightSignal.LIGHT_AUTOSWITCH, ILamp.ISetting.AUTO);
        }

    }

    public boolean isOutLightClosed(HashMap<String, Object> map) {
        int curValue = operator.getIntProp(LightSignal.LAMP_MODE);
        return curValue == ILamp.IModes.OFF;
    }

    public void closeOutLight(HashMap<String, Object> map) {
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();//获取车型
        if ("H37A".equals(carType) || "H37B".equals(carType)) {
            operator.setIntProp(LightSignal.LAMP_MODE, ILamp.IMode.OFF);
        }else{//H56C H56D
            operator.setIntProp(LightSignal.LIGHT_OFFLAMP, ILamp.ISetting.OFF);
        }

    }

    public boolean getReadingLightStatus(HashMap<String, Object> map) {
        String switchType = (String) getValueInContext(map, "switch_type");
        List<Integer> methodPosition = getMethodPosition(map);
        if (isH56CCar(map) || isH56DCar(map)) {
            for (Integer position : methodPosition) {
                String porpId;
                if (position == 0) {
                    porpId = ReadingLightSignal.INLIGHT_FLREADLIGHTSTS;
                } else if (position == 1) {
                    porpId = ReadingLightSignal.INLIGHT_FRREADLIGHTSTS;
                } else if (position == 2) {
                    porpId = ReadingLightSignal.INLIGHT_RLREADLIGHTSTS;
                } else if (position == 3) {
                    porpId = ReadingLightSignal.INLIGHT_RRREADLIGHTSTS;
                } else if (position == 4) {
                    porpId = ReadingLightSignal.INLIGHT_THIRDROWLHREADLIGHTSTS;
                } else {
                    porpId = ReadingLightSignal.INLIGHT_THIRDROWRHREADLIGHTSTS;
                }
                int curValue = operator.getIntProp(porpId);
                if (switchType.equals("open")) {
                    if (curValue == 0) {
                        return false;
                    }
                } else {
                    if (curValue > 0) {
                        return false;
                    }
                }
            }
        }else{
            for (Integer position : methodPosition) {
                int curValue = operator.getIntProp(ReadingLightSignal.READING_LIGHT_SWITCH, position);
                if (switchType.equals("open")) {
                    if (curValue == 0) {
                        return false;
                    }
                } else {
                    if (curValue != 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void setReadingLightStatus(HashMap<String, Object> map) {
        String switchType = (String) getValueInContext(map, "switch_type");
        List<Integer> methodPosition = getMethodPosition(map);
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();//获取车型
        if ("H37A".equals(carType) || "H37B".equals(carType)) {
            for (Integer position : methodPosition) {
                operator.setIntProp(ReadingLightSignal.READING_LIGHT_SWITCH, position, switchType.equals("open") ? 1 : 0);
            }
        }else{//H56C H56D
            for (Integer position : methodPosition) {
                if (position == 4) {
                    operator.setIntProp(ReadingLightSignal.INLIGHT_THIRDROWLHREADLIGHTREQ,switchType.equals("open") ? 2 : 1);
                } else if (position == 5) {
                    operator.setIntProp(ReadingLightSignal.INLIGHT_THIRDROWRHREADLIGHTREQ,switchType.equals("open") ? 2 : 1);
                }else{
                    operator.setIntProp(ReadingLightSignal.READING_LIGHT_SWITCH, position, switchType.equals("open") ? 2 : 1);
                }
            }
        }
    }

    public int getOtherPositiosnOpenSize(HashMap<String, Object> map) {
        Object other_positions_open_status = map.get("other_positions_open_status");
        if (other_positions_open_status != null) {
            ArrayList<Integer> positions = (ArrayList<Integer>) map.get("other_positions_open_status");
            return positions.size();
        } else {
            return 0;
        }
    }

    public void setOtherPositionClose(HashMap<String, Object> map) {
        List<Integer> methodPosition = getMethodPosition(map);
        List<Integer> allPositions = getAllPositions(map);
        List<Integer> resultList = new ArrayList<>();
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();//获取车型
        for (Integer num : allPositions) {
            if (!methodPosition.contains(num)) {
                resultList.add(num);
            }
        }
        List<Integer> newPosition = new ArrayList<>();

        if ("H37A".equals(carType) || "H37B".equals(carType)) {
            for (Integer position : resultList) {
                int curValue = operator.getIntProp(ReadingLightSignal.READING_LIGHT_SWITCH, position);
                if (curValue != ICommon.Switch.OFF) {
                    newPosition.add(position);
                    operator.setIntProp(ReadingLightSignal.READING_LIGHT_SWITCH, position,
                            ICommon.Switch.OFF);
                }
            }
        }else{//H56C H56D
            for (Integer position : resultList) {
                String porpId;
                if (position == 0) {
                    porpId = ReadingLightSignal.INLIGHT_FLREADLIGHTSTS;
                } else if (position == 1) {
                    porpId = ReadingLightSignal.INLIGHT_FRREADLIGHTSTS;
                } else if (position == 2) {
                    porpId = ReadingLightSignal.INLIGHT_RLREADLIGHTSTS;
                } else if (position == 3) {
                    porpId = ReadingLightSignal.INLIGHT_RRREADLIGHTSTS;
                } else if (position == 4) {
                    porpId = ReadingLightSignal.INLIGHT_THIRDROWLHREADLIGHTSTS;
                } else {
                    porpId = ReadingLightSignal.INLIGHT_THIRDROWRHREADLIGHTSTS;
                }
                int curValue = operator.getIntProp(porpId);
                if (curValue > 0) {
                    newPosition.add(position);
                    if (position == 4) {
                        operator.setIntProp(ReadingLightSignal.INLIGHT_THIRDROWLHREADLIGHTREQ, 1);
                    } else if (position == 5) {
                        operator.setIntProp(ReadingLightSignal.INLIGHT_THIRDROWRHREADLIGHTREQ, 1);
                    } else {
                        operator.setIntProp(ReadingLightSignal.READING_LIGHT_SWITCH, position, 1);
                    }
                }
            }
        }


        map.put("other_positions_open_status", newPosition);
    }

    public List<Integer> getAllPositions(HashMap<String, Object> map) {
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();//获取车型
        List<Integer> positiosns = new ArrayList<>();
        positiosns.add(0);
        positiosns.add(1);
        positiosns.add(2);
        positiosns.add(3);
        if ("H56C".equals(carType) || "H56D".equals(carType)) {
            positiosns.add(4);
            positiosns.add(5);
        }
        return positiosns;
    }

    public boolean isCeilingLightssupportPositions(HashMap<String, Object> map) {
        String positions = (String) map.get("positions");
        if (positions == null) {
            return true;
        }
        if (positions.equals("second_side") || positions.equals("total_car")) {
            return true;
        }
        return false;
    }

    private String mergePositionToString(HashMap<String, Object> map) {
        ArrayList<Integer> positionList;
        if (getOtherPositiosnOpenSize(map) == 0) {
            positionList = (ArrayList<Integer>) getValueInContext(map, DCContext.MAP_KEY_METHOD_POSITION);
        } else {
            positionList = (ArrayList<Integer>) map.get("other_positions_open_status");
        }
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();//获取车型

        if ("H37A".equals(carType) || "H37B".equals(carType)) {
            if (positionList.size() == 4 || positionList.size() == 3) {
                return "全车";
            }
        }else{//H56C H56D
            if (positionList.size() == 5 || positionList.size() == 6) {
                return "全车";
            }
        }

        if (positionList.size() == 1) {
            switch (positionList.get(0)) {
                case 0:
                    return "主驾";
                case 2:
                    return "后排左边";
                case 1:
                    return "副驾";
                case 3:
                    return "后排右边";
                case 4:
                    return "三排左";
                case 5:
                    return "三排右";
            }
        }

        //表示是两个位置。
        //0表示没有，1表示有
        int[] arr ;
        if ("H37A".equals(carType) || "H37B".equals(carType)) {
            arr = new int[4];
        }else{//H56C H56D
            arr = new int[6];
        }

        for (int i = 0; i < positionList.size(); i++) {
            arr[positionList.get(i)] = 1;
        }
        if (isH56CCar(map) || isH56DCar(map)) {
            if (arr[0] == 1 && arr[2] == 1 && arr[4] == 1) {
                return "左侧";
            }
            if (arr[1] == 1 && arr[3] == 1 && arr[5] == 1) {
                return "右侧";
            }
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
        if (arr[4] == 1 && arr[5] == 1) {
            return "三排";
        }
        if ((arr[0] == 1 && arr[3] == 1) || (arr[3] == 1 && arr[0] == 1)) {
            return "左前和右后";
        }
        if ((arr[1] == 1 && arr[2] == 1) || (arr[2] == 1 && arr[1] == 1)) {
            return "右前和左后";
        }
        //出错了。
        return "全车";
    }
}
