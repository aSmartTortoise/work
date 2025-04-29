package com.voyah.ai.logic.dc;


import android.os.RemoteException;
import android.util.Log2;


import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.signal.LightSignal;
import com.voice.sdk.device.carservice.signal.PositionSignal;
import com.voice.sdk.device.carservice.constants.ICommon;
import com.voice.sdk.device.carservice.constants.ILamp;
import com.voice.sdk.device.carservice.dc.carsetting.SettingConstants;
import com.voice.sdk.util.LogUtils;


import java.util.HashMap;
import java.util.Map;



public class OutLightControlImpl extends AbsDevices {

    private static final String TAG = OutLightControlImpl.class.getSimpleName();

    public OutLightControlImpl() {
        super();
    }

    @Override
    public String getDomain() {
        return "outlight";
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
            case "outLight_type":
                String outLight_type = (String) getValueInContext(map, "outLight_type");
                str = str.replace("@{outLight_type}", getOutLightName().get(outLight_type));
                break;
            case "lb_height":
                if (map.containsKey("level")) {
                    String level = (String) getValueInContext(map, "level");
                    str = str.replace("@{lb_height}", getLevelStr().get(level));
                } else if (map.containsKey("number_level")) {
                    String number_level = (String) getValueInContext(map, "number_level");
                    str = str.replace("@{lb_height}", getLowBeamHighValue().get(number_level));
                }
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
     * 是否指定灯光类型
     */
    public boolean getOutLightSpecifyType(HashMap<String, Object> map) {
        String outLightType = (String) getValueInContext(map, "outLight_type");
        return outLightType != null;
    }

    /**
     * 未指定灯光类型需设置默认类型（未指定 = 自动大灯）
     */
    public void setOutLightSpecifyType(HashMap<String, Object> map) {
        map.put("outLight_type", "automatic_headlamps");
    }

    /**
     * 打开近光灯
     */
    public void setLowbeam(HashMap<String, Object> map) {
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();
        if ("H37A".equals(carType) || "H37B".equals(carType)) {
            operator.setIntProp(LightSignal.LAMP_MODE, ILamp.IMode.LOW);
        }else{
            operator.setIntProp(LightSignal.LOW_BEAM, ILamp.ISetting.LOW);
        }

    }


    /**
     * 当前车灯类型
     */
    public int getOutLightType(HashMap<String, Object> map) {
        int curValue;
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();
        if (map.containsKey("outLight_type")) {
            String outLightType = (String) getValueInContext(map, "outLight_type");
            if (outLightType.equals("rear_fog_lights") || outLightType.equals("front_fog_lights")) {
                //雾灯
                curValue = operator.getIntProp(LightSignal.LAMP_FOG, PositionSignal.ALL);
            } else {
                //车外灯，自动大灯，位置灯，近光灯
                curValue = operator.getIntProp(LightSignal.LAMP_MODE);
                if ("H56C".equals(carType) || "H56D".equals(carType)) {
                    if (curValue == 0) {//为了不修改流程图，这里获取到的如果是0，代表是位置灯，此处需要关闭位置灯，改成4，方便执行关闭位置灯
                        curValue = 4;
                    }
                }
            }
            LogUtils.d(TAG, "getOutLightType: " + curValue);
        } else {
            curValue = operator.getIntProp(LightSignal.LAMP_MODE);
        }
        return curValue;
    }

    /**
     * 当前想设置的灯光类型
     */
    public int getOutLightSetType(HashMap<String, Object> map) {
        String outLightType = (String) getValueInContext(map, "outLight_type");
        Integer outLightValue = getOutLightData().get(outLightType);
        LogUtils.d(TAG, "getOutLightSetType: " + outLightType + "--outLightValue: " + outLightValue);
        return outLightValue;
    }

    /**
     * 设置灯光类型
     */
    public void setOutLightType(HashMap<String, Object> map) {
        String switchType = (String) getValueInContext(map, "switch_type");
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();

        if (switchType.equals("open")) {
            String outLightType = (String) getValueInContext(map, "outLight_type");
            if (outLightType.equals("rear_fog_lights") || outLightType.equals("front_fog_lights")) {
                //1.打开后雾灯 需要先打开近光灯 再打开后雾灯 2.打开前雾灯时，由于没有前雾灯，所以也是打开后雾灯
                int value = operator.getIntProp(LightSignal.LAMP_SWITCH, PositionSignal.FIRST_ROW_LEFT);
                if (value != ICommon.Switch.ON) {
                    if ("H37A".equals(carType) || "H37B".equals(carType)) {
                        operator.setIntProp(LightSignal.LAMP_MODE, ILamp.IMode.LOW);
                    }else{
                        operator.setIntProp(LightSignal.LOW_BEAM, ILamp.ISetting.LOW);
                    }
                }
                if ("H37A".equals(carType) || "H37B".equals(carType)) {
                    operator.setIntProp(LightSignal.LAMP_FOG, PositionSignal.ALL, ICommon.Switch.ON);
                }else{
                    operator.setIntProp(LightSignal.LAMP_SETFOGREAR, 1);
                }
            } else {
                //打开 车外灯，自动大灯，位置灯，近光灯
                if ("H37A".equals(carType) || "H37B".equals(carType)) {
                    operator.setIntProp(LightSignal.LAMP_MODE, getOutLightSetType(map));
                }else{
                    switch (getOutLightSetType(map)) {
                        case 1://近光灯软开关lighted
                            operator.setIntProp(LightSignal.LOW_BEAM, ILamp.ISetting.LOW);
                            break;
                        case 2://位置灯软开关lighted
                            operator.setIntProp(LightSignal.LIGHT_POSITIONSWITCH, ILamp.ISetting.POSITION);
                            break;
                        case 3://OFF灯光灯软开关lighted"
                            operator.setIntProp(LightSignal.LIGHT_OFFLAMP, ILamp.ISetting.OFF);
                            break;
                        case 0://AUTO灯软开关lighted
                        default://默认自动大灯
                            operator.setIntProp(LightSignal.LIGHT_AUTOSWITCH, ILamp.ISetting.AUTO);
                            break;
                    }
                }
            }

        } else if (switchType.equals("close")) {
            if (map.containsKey("outLight_type")) {
                String outLightType = (String) getValueInContext(map, "outLight_type");
                if (outLightType.equals("automatic_headlamps")) {
                    //关闭自动大灯时需要开启近光灯
                    if ("H37A".equals(carType) || "H37B".equals(carType)) {
                        operator.setIntProp(LightSignal.LAMP_MODE, ILamp.IMode.LOW);
                    }else{
                        operator.setIntProp(LightSignal.LOW_BEAM, ILamp.ISetting.LOW);
                    }

                } else if (outLightType.equals("position_lights")) {

                    if ("H37A".equals(carType) || "H37B".equals(carType)) {
                        operator.setIntProp(LightSignal.LAMP_MODE, ILamp.IMode.OFF);
                    }else{
                        operator.setIntProp(LightSignal.LIGHT_OFFLAMP, ILamp.ISetting.OFF);
                    }
                } else if (outLightType.equals("low_beam")) {
                    //关闭近光灯先处理雾灯
                    int fogValue = operator.getIntProp(LightSignal.LAMP_FOG);
                    if (fogValue == ICommon.Switch.ON) {
                        if ("H37A".equals(carType) || "H37B".equals(carType)) {
                            operator.setIntProp(LightSignal.LAMP_FOG, ICommon.Switch.OFF);
                        }else{
                            operator.setIntProp(LightSignal.LAMP_SETFOGREAR, 1);
                        }
                    }
                    //关闭近光灯时需要开启位置灯
                    if ("H37A".equals(carType) || "H37B".equals(carType)) {
                        operator.setIntProp(LightSignal.LAMP_MODE, ILamp.IMode.POSITION);
                    }else{
                        operator.setIntProp(LightSignal.LIGHT_POSITIONSWITCH, ILamp.ISetting.POSITION);
                    }
                } else if (outLightType.equals("rear_fog_lights")) {
                    //关闭后雾灯
                    if ("H37A".equals(carType) || "H37B".equals(carType)) {
                        operator.setIntProp(LightSignal.LAMP_FOG, PositionSignal.ALL, ICommon.Switch.OFF);
                    }else{
                        operator.setIntProp(LightSignal.LAMP_SETFOGREAR, 1);
                    }
                }
            } else {
                if ("H37A".equals(carType) || "H37B".equals(carType)) {
                    operator.setIntProp(LightSignal.LAMP_MODE, ILamp.IMode.OFF);
                }else{
                    operator.setIntProp(LightSignal.LIGHT_OFFLAMP, ILamp.ISetting.OFF);
                }
            }
        }
    }

    /**
     * 车外灯数据(自动大灯，位置灯，近光灯)"
     * 0x0：AUTO灯软开关lighted
     * 0x1：近光灯软开关lighted
     * 0x2：位置灯软开关lighted
     * 0x3：OFF灯光灯软开关lighted"
     */
    public Map<String, Integer> getOutLightData() {
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();//获取车型
        if ("H37A".equals(carType) || "H37B".equals(carType)) {
            return new HashMap<String, Integer>(){
                {
                    put("automatic_headlamps", 4);
                    put("position_lights", 2);
                    put("low_beam", 3);
                }
            };
        }else{//H56C H56D
            return new HashMap<String, Integer>() {
                {
                    put("automatic_headlamps", 0);
                    put("position_lights", 2);
                    put("low_beam", 1);
                }
            };
        }

    }




    public boolean isLowbeamOpen(HashMap<String, Object> map) {
        return operator.getIntProp(LightSignal.LAMP_SWITCH, PositionSignal.FIRST_ROW_LEFT) ==1;
    }

    public boolean isPositionOpen(HashMap<String, Object> map) {
        return operator.getIntProp(LightSignal.LAMP_POSITION, PositionSignal.FIRST_ROW_LEFT) == 1;
//        return operator.getIntProp(LightSignal.LAMP_MODE, PositionSignal.FIRST_ROW_LEFT) == 3 ? false : true;
    }

    /**
     * 车外灯名称(自动大灯，位置灯，近光灯)
     */
    public Map<String, String> getOutLightName() {
        return new HashMap<String, String>() {
            {
                put("automatic_headlamps", "自动大灯");
                put("position_lights", "位置灯");
                put("low_beam", "近光灯");
            }
        };
    }

    /**
     * 当前近光灯高度
     */
    public int curLowBeamHighValue(HashMap<String, Object> map) {
        int curValue = operator.getIntProp(LightSignal.LAMP_HEIGHT);
        LogUtils.d(TAG, "curLowBeamHighValue: " + curValue);
        return curValue;
    }

    /**
     * 当前想设置的近光灯高度值
     */
    public int curSetHighValue(HashMap<String, Object> map) {
        int numberLevel = Integer.parseInt((String) getValueInContext(map, "number_level"));
        LogUtils.d(TAG, "curSetHighValue: " + numberLevel);
        return numberLevel;
    }

    /**
     * 设置近光灯高度
     */
    public void setLowBeamHighValue(HashMap<String, Object> map) {
        //先打开近光灯
        int value = operator.getIntProp(LightSignal.LAMP_SWITCH, PositionSignal.FIRST_ROW_LEFT);
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();
        if (value != ICommon.Switch.ON) {
            if ("H37A".equals(carType) || "H37B".equals(carType)) {
                operator.setIntProp(LightSignal.LAMP_MODE, ILamp.IMode.LOW);
            }else{
                operator.setIntProp(LightSignal.LOW_BEAM, ILamp.ISetting.LOW);
            }

        }
        int newHeight = curLowBeamHighValue(map);
        if (map.containsKey("adjust_type")) {
            String adjustType = (String) getValueInContext(map, "adjust_type");
            if (adjustType.equals("increase")) {
                String numberLevel = (String) getValueInContext(map, "number_level");
                if (numberLevel == null) {
                    // = 调高
                    newHeight = Math.min(5, newHeight + 1);
                } else {
                    //调高numberLevel挡
                    int level = Integer.parseInt(numberLevel);
                    newHeight = Math.min(5, newHeight + level);
                }
            } else if (adjustType.equals("decrease")) {
                String numberLevel = (String) getValueInContext(map, "number_level");
                if (numberLevel == null) {
                    //调低
                    newHeight = Math.max(1, newHeight - 1);
                } else {
                    //调低numberLevel挡
                    int level = Integer.parseInt(numberLevel);
                    newHeight = Math.max(1, newHeight - level);
                }
            } else if (adjustType.equals("set")) {
                if (map.containsKey("level")) {
                    String level = (String) getValueInContext(map, "level");
                    if (level.equals("max")) {
                        //调最高
                        newHeight = 5;
                    } else if (level.equals("min")) {
                        //调最低
                        newHeight = 1;
                    } else if (level.equals("mid")) {
                        //调最中间档
                        newHeight = 3;
                    } else if (level.equals("low")) {
                        //调到低档
                        newHeight = 2;
                    } else if (level.equals("high")) {
                        //调到高档
                        newHeight = 4;
                    }
                } else if (map.containsKey("number_level")) {
                    String numberLevel = (String) getValueInContext(map, "number_level");
                    //调到numberLevel挡
                    int number = Integer.parseInt(numberLevel);
                    if (number > 5) {
                        newHeight = 5;
                    } else if (number < 1) {
                        newHeight = 1;
                    } else {
                        newHeight = number;
                    }
                }
            }
        }
        if ("H37A".equals(carType) || "H37B".equals(carType)) {
            operator.setIntProp(LightSignal.LAMP_HEIGHT, newHeight);
        }else{
            operator.setIntProp(LightSignal.LIGHR_HEIGHT, newHeight);
        }

    }

    /**
     * 车外灯名称(自动大灯，位置灯，近光灯)
     */
    public Map<String, String> getLowBeamHighValue() {
        return new HashMap<String, String>() {
            {
                put("1", "最低");
                put("2", "低");
                put("3", "中间");
                put("4", "高");
                put("5", "最高");
            }
        };
    }

    public Map<String, String> getLevelStr() {
        return new HashMap<String, String>() {
            {
                put("min", "最低档");
                put("low", "低档");
                put("mid", "中档");
                put("high", "高档");
                put("max", "最高档");
            }
        };
    }

    /**
     * 获取近光灯高度调节页面
     */
    public boolean getLowBeamAdjustPage(HashMap<String, Object> map) throws RemoteException {
        LogUtils.d(TAG, "getLowBeamAdjustPage");
        return mSettingHelper.isCurrentState(SettingConstants.VEHICLE_LIGHT_PAGE);
    }

    /**
     * 打开近光灯高度调节页面
     */
    public void openLowBeamAdjustPage(HashMap<String, Object> map) throws RemoteException  {
        LogUtils.d(TAG, "openLowBeamAdjustPage");
//        closeSystemWindow();
        mSettingHelper.exec(SettingConstants.VEHICLE_LIGHT_PAGE);
    }
}
