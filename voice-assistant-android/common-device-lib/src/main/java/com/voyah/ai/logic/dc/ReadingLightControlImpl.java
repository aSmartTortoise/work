package com.voyah.ai.logic.dc;

import android.text.TextUtils;

import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.constants.ICommon;
import com.voice.sdk.device.carservice.dc.ReadingLightInterface;
import com.voice.sdk.device.carservice.dc.context.DCContext;
import com.voice.sdk.device.carservice.signal.ReadingLightSignal;
import com.voice.sdk.util.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReadingLightControlImpl extends AbsDevices {

    private static final String TAG = ReadingLightControlImpl.class.getSimpleName();
    private final ReadingLightInterface readingLight;

    public ReadingLightControlImpl() {
        super();
        readingLight = DeviceHolder.INS().getDevices().getCarService().getReadingLight();
    }

    @Override
    public String getDomain() {
        return "readinglight";
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

    public int getOtherPositiosnOpenSize(HashMap<String, Object> map) {
        Object other_positions_open_status = map.get("other_positions_open_status");
        if (other_positions_open_status != null) {
            ArrayList<Integer> positions = (ArrayList<Integer>) map.get("other_positions_open_status");
            return positions.size();
        } else {
            return 0;
        }
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

    public void setReadingLightStatus(HashMap<String, Object> map) {
        String switchType = (String) getValueInContext(map, "switch_type");
        List<Integer> methodPosition = getMethodPosition(map);
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();//获取车型
        if ("H37A".equals(carType) || "H37B".equals(carType)) {
            for (Integer position : methodPosition) {
                operator.setIntProp(ReadingLightSignal.READING_LIGHT_SWITCH, position, switchType.equals("open") ? ICommon.Switch.ON : ICommon.Switch.OFF);
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

//        for (Integer position : methodPosition) {
//            operator.setIntProp(ReadingLightSignal.READING_LIGHT_SWITCH, position, switchType.equals("open") ? 2 : 1);
//        }
    }

    private String mergePositionToString(HashMap<String, Object> map) {
        ArrayList<Integer> positionList;
        if (getOtherPositiosnOpenSize(map) == 0) {
            positionList = (ArrayList<Integer>) getValueInContext(map, DCContext.MAP_KEY_METHOD_POSITION);
        } else {
            positionList = (ArrayList<Integer>) map.get("other_positions_open_status");
        }

        if (isH56CCar(map) || isH56DCar(map)) {
            if (positionList.size() == 5 || positionList.size() == 6) {
                return "全车";
            }
        } else {
            if (positionList.size() == 4 || positionList.size() == 3) {
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
        if (isH56CCar(map) || isH56DCar(map)) {
            arr = new int[6];
        }else {
            arr = new int[4];
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

    //判断车型是否是H56
    public boolean isH56CorH56D(HashMap<String, Object> map){
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();//获取车型
        if (carType.equals("H56C") || carType.equals("H56D")) {
            return true;
        }else {
            return false;
        }
    }
    //代表用户说的是第三排
    public boolean isThirdPosition(HashMap<String, Object> map){
        boolean isThird = false;
        String positions = getOneMapValue("positions", map);
        if (!TextUtils.isEmpty(positions)) {
            if(positions.equals("third_side")) {//front_side
                isThird =true;
            }
        }
        return isThird;
    }
}
