package com.voyah.ai.logic.dc;

import android.text.TextUtils;

import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.dc.context.DCContext;
import com.voice.sdk.device.carservice.signal.DoorSignal;
import com.voice.sdk.device.carservice.signal.WindowSunshadeSignal;
import com.voice.sdk.util.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class WindowSunshadeControlImpl extends AbsDevices{

    private static final String TAG = WindowSunshadeControlImpl.class.getSimpleName();
    @Override
    public String getDomain() {
        return "windowSunshade";
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
                String pos = mergePositionToString(positions);
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

    //判断用户要执行的位置是否打开
    public boolean isOpenWindowSunshade (HashMap<String, Object> map){
        boolean isOpen = false;//默认关闭  false关闭  true打开
        int curValueLeft = operator.getIntProp(WindowSunshadeSignal.WINDOWSUNSHADE_GET_LEFT);//0x1：完全关闭 0x2：完全打开 0x3：滑动位置 0x4：通风_5rm
        int curValueRight = operator.getIntProp(WindowSunshadeSignal.WINDOWSUNSHADE_GET_RIGHT);//0x1：完全关闭 0x2：完全打开 0x3：滑动位置 0x4：通风_5rm

        String positions = getOneMapValue("positions", map);
        if (!TextUtils.isEmpty(positions)) {
            switch (positions){
                case "second_row_left"://二排左
                case "left_side"://左侧
                case "rear_side_left"://左后
                case "third_row_left"://三排左
                    if(curValueLeft ==2){//0x1：完全关闭 0x2：完全打开 0x3：滑动位置 0x4：通风_5rm
                        isOpen = true;
                        map.put("position","左后");
                    }
                    break;

                case "second_row_right"://二排右
                case "right_side":
                case "rear_side_right"://右后
                case "third_row_right":
                    if(curValueRight ==2){//0x1：完全关闭 0x2：完全打开 0x3：滑动位置 0x4：通风_5rm
                        isOpen = true;
                        map.put("position","右后");
                    }
                    break;
//                case "first_row_left"://主驾
//                case "first_row_right"://副驾
//                case "second_side"://二排
//                case "rear_side"://后排
//                    break;
                default://主驾副驾，或者后排中间等，统一当做全部车窗遮阳帘处理
                    LogUtils.e(TAG,"------指定位置-------"+positions+"==================");

                    if(curValueLeft ==2 && curValueRight ==2 ){//0x1：完全关闭 0x2：完全打开 0x3：滑动位置 0x4：通风_5rm
                        isOpen = true;
                        map.put("position","后排");
                    }
                    break;
            }
        }else {
            int soundSource = getSoundSourcePos(map); //声源位置
            switch (soundSource) {
                case 2:
                case 4:
                    if(curValueLeft ==2){//0x1：完全关闭 0x2：完全打开 0x3：滑动位置 0x4：通风_5rm
                        isOpen = true;
                        map.put("position","左后");
                    }
                    break;
                case 3:
                case 5:
                    if(curValueRight ==2){//0x1：完全关闭 0x2：完全打开 0x3：滑动位置 0x4：通风_5rm
                        isOpen = true;
                        map.put("position","右后");
                    }
                    break;
                default:
                    LogUtils.e(TAG,"------声源位置-------"+soundSource+"==================");

                    if(curValueLeft ==2 && curValueRight ==2 ){//0x1：完全关闭 0x2：完全打开 0x3：滑动位置 0x4：通风_5rm
                        isOpen = true;
                        map.put("position","左后");
                    }
                    break;
            }
        }

        return isOpen;
    }


    //判断用户要执行的位置是否关闭
    public boolean isCloseWindowSunshade (HashMap<String, Object> map){
        boolean isClose = false;//false 是 不是关闭，   true代表完全关闭
        int curValueLeft = operator.getIntProp(WindowSunshadeSignal.WINDOWSUNSHADE_GET_LEFT);//0x1：完全关闭 0x2：完全打开 0x3：滑动位置 0x4：通风_5rm
        int curValueRight = operator.getIntProp(WindowSunshadeSignal.WINDOWSUNSHADE_GET_RIGHT);//0x1：完全关闭 0x2：完全打开 0x3：滑动位置 0x4：通风_5rm

        String positions = getOneMapValue("positions", map);
        if (!TextUtils.isEmpty(positions)) {
            switch (positions){
                case "second_row_left"://二排左
                case "left_side"://左侧
                case "rear_side_left"://左后
                case "third_row_left"://三排左
                    if(curValueLeft ==1){//0x1：完全关闭 0x2：完全打开 0x3：滑动位置 0x4：通风_5rm
                        isClose = true;
                        map.put("position","左后");
                    }
                    break;

                case "second_row_right"://二排右
                case "right_side":
                case "rear_side_right"://右后
                case "third_row_right":
                    if(curValueRight ==1){//0x1：完全关闭 0x2：完全打开 0x3：滑动位置 0x4：通风_5rm
                        isClose = true;
                        map.put("position","右后");
                    }
                    break;
                default://主驾副驾，或者后排中间等，统一当做全部车窗遮阳帘处理
                    LogUtils.e(TAG,"------指定位置-------"+positions+"==================");

                    if(curValueLeft ==1 && curValueRight ==1 ){//0x1：完全关闭 0x2：完全打开 0x3：滑动位置 0x4：通风_5rm
                        isClose = true;
                        map.put("position","后排");
                    }
                    break;
            }
        }else {
            int soundSource = getSoundSourcePos(map); //声源位置
            switch (soundSource) {
                case 2:
                case 4:
                    if(curValueLeft ==1){//0x1：完全关闭 0x2：完全打开 0x3：滑动位置 0x4：通风_5rm
                        isClose = true;
                        map.put("position","左后");
                    }
                    break;
                case 3:
                case 5:
                    if(curValueRight ==1){//0x1：完全关闭 0x2：完全打开 0x3：滑动位置 0x4：通风_5rm
                        isClose = true;
                        map.put("position","右后");
                    }
                    break;
                default:
                    LogUtils.e(TAG,"------声源位置-------"+soundSource+"==================");

                    if(curValueLeft ==1 && curValueRight ==1 ){//0x1：完全关闭 0x2：完全打开 0x3：滑动位置 0x4：通风_5rm
                        isClose = true;
                        map.put("position","后排");
                    }
                    break;
            }
        }

        return isClose;
    }


    //执行 打开/关闭 车窗遮阳帘
    public void setWindowSunshade(HashMap<String, Object> map){
        String switchType = (String) getValueInContext(map, "switch_type");
        String positions = getOneMapValue("positions", map);
        if (!TextUtils.isEmpty(positions)) {
            switch (positions){
                case "second_row_left"://二排左
                case "left_side"://左侧
                case "rear_side_left"://左后
                case "third_row_left"://三排左
                    operator.setIntProp(WindowSunshadeSignal.WINDOWSUNSHADE_SET_LEFT,switchType.equals("open") ? 1 : 3);//1 降下（打开）  3 升起（关闭）
                    break;
                case "second_row_right"://二排右
                case "right_side":
                case "rear_side_right"://右后
                case "third_row_right":
                    operator.setIntProp(WindowSunshadeSignal.WINDOWSUNSHADE_SET_RIGHT,switchType.equals("open") ? 1 : 3);//1 降下（打开）  3 升起（关闭）
                    break;
                default://主驾副驾，或者后排中间等，统一当做全部车窗遮阳帘处理
                    LogUtils.e(TAG,"------指定位置-------"+positions+"==================");

                    operator.setIntProp(WindowSunshadeSignal.WINDOWSUNSHADE_SET_LEFT,switchType.equals("open") ? 1 : 3);//1 降下（打开）  3 升起（关闭）
                    operator.setIntProp(WindowSunshadeSignal.WINDOWSUNSHADE_SET_RIGHT,switchType.equals("open") ? 1 : 3);//1 降下（打开）  3 升起（关闭）
                    break;
            }
        }else {
            int soundSource = getSoundSourcePos(map); //声源位置
            switch (soundSource) {
                case 2:
                case 4:
                    operator.setIntProp(WindowSunshadeSignal.WINDOWSUNSHADE_SET_LEFT,switchType.equals("open") ? 1 : 3);//1 降下（打开）  3 升起（关闭）
                    break;
                case 3:
                case 5:
                    operator.setIntProp(WindowSunshadeSignal.WINDOWSUNSHADE_SET_RIGHT,switchType.equals("open") ? 1 : 3);//1 降下（打开）  3 升起（关闭）
                    break;
                default:
                    LogUtils.e(TAG,"------声源位置-------"+soundSource+"==================");

                    operator.setIntProp(WindowSunshadeSignal.WINDOWSUNSHADE_SET_LEFT,switchType.equals("open") ? 1 : 3);//1 降下（打开）  3 升起（关闭）
                    operator.setIntProp(WindowSunshadeSignal.WINDOWSUNSHADE_SET_RIGHT,switchType.equals("open") ? 1 : 3);//1 降下（打开）  3 升起（关闭）
                    break;
            }
        }

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
}
