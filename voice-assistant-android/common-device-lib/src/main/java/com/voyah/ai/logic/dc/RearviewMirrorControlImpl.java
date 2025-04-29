package com.voyah.ai.logic.dc;


import android.util.Log2;

import com.voice.sdk.device.carservice.signal.AirSignal;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.carservice.signal.PositionSignal;
import com.voice.sdk.device.carservice.signal.RearviewMirrorSignal;
import com.voice.sdk.util.LogUtils;
import com.voyah.ai.voice.platform.agent.api.flowchart.library.bean.FunctionalPositionBean;
import com.voyah.ai.voice.platform.agent.api.functionMatrix.FunctionDeviceMappingManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RearviewMirrorControlImpl extends AbsDevices {

    private static final String TAG = RearviewMirrorControlImpl.class.getSimpleName();

    public RearviewMirrorControlImpl() {
        super();
    }

    @Override
    public String getDomain() {
        return "RearviewMirror";
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
                String position = (String) getValueInContext(map, "positions");
                str = str.replace("@{position}", getPositionSide().get(position));
                break;
            default:
                LogUtils.e(TAG, "当前要处理的@{" + key + "}不存在");
                return str;
        }
        Log2.i(TAG, "tts :" + start + " " + end + " " + key + " " + index);

        Log2.i(TAG, "tts :" + str);
        return str;
    }

    /**
     * 是否有左侧右侧的位置指定
     */
    public boolean isHasPositions(HashMap<String, Object> map) {
        String position = (String) getValueInContext(map, "positions");
        return position != null;
    }

    /**
     * 设置后视镜状态
     */
    public void setRearviewMirror(HashMap<String, Object> map) {
        String switch_type = (String) getValueInContext(map, "switch_type");
        LogUtils.i(TAG, "setRearviewMirror : switch_type-" + switch_type);
        operator.setBooleanProp(RearviewMirrorSignal.MIRROR_FOLD_STATE, !"open".equals(switch_type));
    }

    /**
     * 打开后视镜tab页并高亮选择position的后视镜
     */
    public void setRearviewMirrorTab(HashMap<String, Object> map) {
        String position = (String) getValueInContext(map, "positions");
        operator.setIntProp(RearviewMirrorSignal.MIRROR_SHOW_DIALOG,position.equals("left_side") ? 0 : 1);
    }

    /**
     * 打开后视镜tab页并高亮选择position的后视镜
     */
    public void setLeftRearviewMirrorTab(HashMap<String, Object> map) {
        operator.setIntProp(RearviewMirrorSignal.MIRROR_SHOW_DIALOG,0);
    }

    /**
     * 获取后视镜加热状态
     */
    public boolean getRearviewMirrorHot(HashMap<String, Object> map) {
        return operator.getBooleanProp(RearviewMirrorSignal.MIRROR_HOT_SWITCH);
    }

    /**
     * 设置后视镜加热状态
     */
    public void setRearviewMirrorHot(HashMap<String, Object> map) {
        String switch_type = (String) getValueInContext(map, "switch_type");
        LogUtils.i(TAG, "setRearviewMirrorHot : switch_type-" + switch_type);
        operator.setBooleanProp(RearviewMirrorSignal.MIRROR_HOT_SWITCH, "open".equals(switch_type));
    }
    //=============后视镜加热开关========
    public boolean getRearDefrostSwitchState(HashMap<String, Object> map) {

        boolean curValue = operator.getBooleanProp(AirSignal.REAR_DEFROST_SWITCH_STATE);
//        int curValue = carPropHelper.getIntProp(Climate.ID_REAR_DEFROST);
        LogUtils.e(TAG, "后除霜打开状态：" + curValue);
        return curValue;
    }

    public void setRearDefrostSwitchState(HashMap<String, Object> map) {
        boolean isOpen = ((String) getValueInContext(map, "switch_type")).equals("open");


        operator.setBooleanProp(AirSignal.REAR_DEFROST_SWITCH_STATE, isOpen);
        LogUtils.e(TAG, isOpen ? "打开后除霜" : "关闭后除霜");
    }


    //===============后视镜自动加热开关=========
    public boolean getRearviewMirrorAutoHeatingSwitchState(HashMap<String, Object> map) {
        List<Integer> positions = getMethodPosition(map);
        ArrayList<Integer> list = mergePositionWind(positions, RearviewMirrorSignal.AIR_REARVIEW_MIRROR_AUTO_HEATING);
        boolean isOpen = true;
        for (int i = 0; i < list.size(); i++) {
            isOpen &= operator.getBooleanProp(RearviewMirrorSignal.AIR_REARVIEW_MIRROR_AUTO_HEATING);
        }

        LogUtils.i(TAG, "自动后视镜开关状态：" + isOpen);
        return isOpen;
    }

    public void setRearviewMirrorAutoHeatingSwitchState(HashMap<String, Object> map) {
        boolean isOpen = ((String) getValueInContext(map, "switch_type")).equals("open");
        List<Integer> positions = getMethodPosition(map);
        ArrayList<Integer> list = mergePositionWind(positions, RearviewMirrorSignal.AIR_REARVIEW_MIRROR_AUTO_HEATING);
        for (int i = 0; i < list.size(); i++) {
            operator.setBooleanProp(RearviewMirrorSignal.AIR_REARVIEW_MIRROR_AUTO_HEATING, list.get(i), isOpen);
        }
    }

    private ArrayList<Integer> mergePositionWind(List<Integer> positions, String functionPosition) {
        ArrayList<Integer> mergePosition = new ArrayList<>();
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < positions.size(); i++) {
            int a = getWindPosition(positions.get(i), functionPosition);
            if (a != PositionSignal.AREA_NONE) {
                set.add(a);
            }
        }
        Iterator<Integer> iterator = set.iterator();
        while (iterator.hasNext()) {
            mergePosition.add(iterator.next());
        }
        return mergePosition;
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

        switch (position) {
            case 0:
                if (curMap.containsKey("all")) {
                    return PositionSignal.FIRST_ROW_LEFT;
                }
                if (curMap.get("0").equals("1")) {
                    return PositionSignal.FIRST_ROW_LEFT;
                }
                if (curMap.get("6").equals("1")) {
                    return PositionSignal.FIRST_ROW;
                }
                return PositionSignal.AREA_NONE;
            case 1:
                if (curMap.containsKey("all")) {
                    return PositionSignal.FIRST_ROW_RIGHT;
                }
                if (curMap.get("1").equals("1")) {
                    return PositionSignal.FIRST_ROW_RIGHT;
                }
                if (curMap.get("6").equals("1")) {
                    return PositionSignal.FIRST_ROW;
                }

                return PositionSignal.AREA_NONE;
            case 2:
                if (curMap.containsKey("all")) {
                    return PositionSignal.SECOND_ROW_LEFT;
                }
                if (curMap.get("2").equals("1")) {
                    return PositionSignal.FIRST_ROW_RIGHT;
                }
                if (curMap.get("7").equals("1")) {
                    return PositionSignal.SECOND_ROW;
                }
                return PositionSignal.AREA_NONE;
            case 3:
                if (curMap.containsKey("all")) {
                    return PositionSignal.SECOND_ROW_RIGHT;
                }
                if (curMap.get("3").equals("1")) {
                    return PositionSignal.SECOND_ROW_RIGHT;
                }
                if (curMap.get("7").equals("1")) {
                    return PositionSignal.SECOND_ROW;
                }
                return PositionSignal.AREA_NONE;
            case 4:
                if (curMap.containsKey("all")) {
                    return PositionSignal.THIRD_ROW_LEFT;
                }
                if (curMap.get("4").equals("1")) {
                    return PositionSignal.THIRD_ROW_LEFT;
                }
                if (curMap.containsKey("8") && curMap.get("8").equals("1")) {
                    return PositionSignal.THIRD_ROW;
                }
                return PositionSignal.AREA_NONE;
            case 5:
                if (curMap.containsKey("all")) {
                    return 5;
                }
                if (curMap.get("4").equals("1")) {
                    return PositionSignal.THIRD_ROW_RIGHT;
                }
                if (curMap.containsKey("8") && curMap.get("8").equals("1")) {
                    return PositionSignal.THIRD_ROW;
                }
                return PositionSignal.AREA_NONE;
        }
        return position;
    }
    //===============================

    /**
     * 车速是否过快
     */
    public boolean isExcessiveSpeed(HashMap<String, Object> map) {
        //获取当前车速
        float curDriveSpeed = operator.getFloatProp(CommonSignal.COMMON_SPEED_INFO);
        LogUtils.i(TAG, "isSpeedRange : curDriveSpeed -" + curDriveSpeed);
        return curDriveSpeed > 15f;
    }

    /**
     * left_side = 左后视镜  right_side = 右后视镜
     */
    public Map<String, String> getPositionSide() {
        return new HashMap<String, String>() {{
            put("left_side", "左");
            put("right_side", "右");
        }};
    }
}
