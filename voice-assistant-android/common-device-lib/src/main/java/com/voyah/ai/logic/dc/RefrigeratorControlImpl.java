package com.voyah.ai.logic.dc;

import android.text.TextUtils;

import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.carservice.signal.RefrigeratorSignal;
import com.voice.sdk.util.LogUtils;

import java.util.HashMap;

public class RefrigeratorControlImpl extends AbsDevices {

    private static final String TAG = RefrigeratorControlImpl.class.getSimpleName();


    public RefrigeratorControlImpl() {
        super();
    }

    @Override
    public String getDomain() {
        return "Refrigerator";
    }

    @Override
    public String replacePlaceHolderInner(HashMap<String, Object> map, String str) {
        LogUtils.i(TAG, "tts :" + str);
        int index = 0;

        int start = str.indexOf("@");
        if (start == -1) {
            return str;
        }
        int end = str.indexOf("}");
        String key = str.substring(start + 2, end);
        switch (key) {
            case "fridge_run_mode":
                str = str.replace("@{fridge_run_mode}", getOrderRefrigeratorMode(map) == 1 ? "制冷" : "制热");
                break;
            case "fridge_energy_mode":
                str = str.replace("@{fridge_energy_mode}", getOrderEnergyMode(map) == 1 ? "节能" : "标准");
                break;
            case "fridge_cold_temp_num":
                str = str.replace("@{fridge_cold_temp_num}", String.valueOf(getOrderRefrigeratorTemp(map)));
                break;
            case "fridge_hot_temp_num":
                str = str.replace("@{fridge_hot_temp_num}", String.valueOf(getOrderRefrigeratorTemp(map)));
                break;
            default:
                LogUtils.e(TAG, "当前要处理的@{" + key + "}不存在");
                return str;
        }
        LogUtils.i(TAG, "tts :" + start + " " + end + " " + key + " " + index);
        return str;
    }

    public boolean isSupportRefrigerator(HashMap<String, Object> map) {
//        int state = MegaSystemProperties.getInt(MegaProperties.CONFIG_CAR_REFRIGERATOR, 0);
        int state = operator.getIntProp(CommonSignal.COMMON_SUPPORT_REFRIGERATOR);
        return state == 1;
    }

    public boolean isSupportELeDoor(HashMap<String, Object> map) {
        return !isH37ACar(map) && !isH37BCar(map);
    }

    public boolean isSupportRefrigeratorLock(HashMap<String, Object> map) {
        return !isH37ACar(map) && !isH37BCar(map);
    }

    public boolean isSupportRefrigeratorKillGerms(HashMap<String, Object> map) {
        return isH37BCar(map);
    }

    public int getRefrigeratorDoorState(HashMap<String, Object> map) {
        int state = operator.getIntProp(RefrigeratorSignal.REFRIGERATOR_DOOR);
        LogUtils.d(TAG, "getRefrigeratorDoorState: " + state);
        return state;
    }

    public void setRefrigeratorDoorState(HashMap<String, Object> map) {
        String str = getOneMapValue("switch_type", map);
        if (!TextUtils.isEmpty(str)) {
            operator.setIntProp(RefrigeratorSignal.REFRIGERATOR_DOOR, str.equals("open") ? 1 : 2);
        }
    }

    public int getRefrigeratorPowerState(HashMap<String, Object> map) {
        int state = operator.getIntProp(RefrigeratorSignal.REFRIGERATOR_POWER);
        LogUtils.d(TAG, "getRefrigeratorPowerState: " + state);
        return state;
    }

    public void setRefrigeratorPowerState(HashMap<String, Object> map) {
        String str = getOneMapValue("switch_type", map);
        if (!TextUtils.isEmpty(str)) {
            operator.setIntProp(RefrigeratorSignal.REFRIGERATOR_POWER, str.equals("open") ? 2 : 1);
            return;
        }
        operator.setIntProp(RefrigeratorSignal.REFRIGERATOR_POWER, 2);
    }

    /**
     * 获取当前空调模式
     *
     * @param map
     * @return 1制冷，2制热
     */
    public int getCurRefrigeratorMode(HashMap<String, Object> map) {
        int mode = operator.getIntProp(RefrigeratorSignal.REFRIGERATOR_MODE);
        LogUtils.d(TAG, "getCurRefrigeratorMode: " + mode);
        return mode;
    }

    public int getOrderRefrigeratorMode(HashMap<String, Object> map) {
        int mode = getCurRefrigeratorMode(map);
        String orderMode = getOneMapValue("switch_mode", map);
        if (!TextUtils.isEmpty(orderMode)) {
            mode = orderMode.equals("cold") ? 1 : 2;
            return mode;
        }
        String operationalMode = getOneMapValue("refrigerator_operational_mode", map);
        if (!TextUtils.isEmpty(operationalMode)) {
            mode = operationalMode.equals("cold") ? 1 : 2;
            return mode;
        }
        LogUtils.d(TAG, "getOrderRefrigeratorMode: " + mode);
        return mode;
    }

    public void setRefrigeratorMode(HashMap<String, Object> map) {
        if (getRefrigeratorPowerState(map) != 2) {
            operator.setIntProp(RefrigeratorSignal.REFRIGERATOR_POWER, 2);
        }
        operator.setIntProp(RefrigeratorSignal.REFRIGERATOR_MODE, getOrderRefrigeratorMode(map));
    }

    /**
     * 获取当前冰箱温度
     *
     * @param map
     * @return
     */
    public int getCurRefrigeratorTemp(HashMap<String, Object> map) {
        int mode = operator.getIntProp(RefrigeratorSignal.REFRIGERATOR_TEMP);
        LogUtils.d(TAG, "getCurRefrigeratorTemp: " + mode);
        return mode;
    }

    public int getOrderRefrigeratorTemp(HashMap<String, Object> map) {
        int temp = 0;
        String orderTemp = getOneMapValue("number_temp", map);
        if (!TextUtils.isEmpty(orderTemp)) {
            temp = Integer.parseInt(orderTemp);
        }
        String orderNumber = getOneMapValue("number", map);
        if (!TextUtils.isEmpty(orderNumber)) {
            temp = Integer.parseInt(orderNumber);
        }
        LogUtils.d(TAG, "getOrderRefrigeratorTemp: " + temp);
        return temp;
    }

    public int getTempMin(HashMap<String, Object> map) {
        return getCurRefrigeratorMode(map) == 1 ? -6 : 30;
    }


    public int getTempMax(HashMap<String, Object> map) {
        return getCurRefrigeratorMode(map) == 1 ? 15 : 50;
    }

    public boolean isTempLimit(HashMap<String, Object> map) {
        int num = getOrderRefrigeratorTemp(map);
        if (getCurRefrigeratorMode(map) == 1) {
            return num > -7 && num < 16;
        } else {
            return num > 29 && num < 51;
        }

    }

    public void setRefrigeratorTemp(HashMap<String, Object> map) {
        LogUtils.d(TAG, "setRefrigeratorTemp");
        String adjustType = "";
        String level = "";
        if (map.containsKey("adjust_type")) {
            adjustType = (String) getValueInContext(map, "adjust_type");
            LogUtils.d(TAG, "adjustType: " + adjustType);
        }
        if (map.containsKey("level")) {
            level = (String) getValueInContext(map, "level");
            LogUtils.d(TAG, "level: " + level);
        }
        int number = getOrderRefrigeratorTemp(map);
        int newVal = 0;
        int curVal = getCurRefrigeratorTemp(map);
        if (adjustType.equals("set") && level.equals("min")) {
            newVal = getTempMin(map);
        } else if (adjustType.equals("set") && level.equals("max")) {
            newVal = getTempMax(map);
        } else if (adjustType.equals("increase") && number != 0) {
            newVal = Math.min( number + curVal, getTempMax(map));
        } else if (adjustType.equals("increase")) {
            newVal = Math.min(curVal + 1, getTempMax(map));
        } else if (adjustType.equals("decrease") && number != 0) {
            newVal = Math.max(curVal - (int) number, getTempMin(map));
        } else if (adjustType.equals("decrease")) {
            newVal = Math.max(curVal - 1, getTempMin(map));
        } else {
            newVal = Math.min(Math.max((int) number, getTempMin(map)), getTempMax(map));
        }
        operator.setIntProp(RefrigeratorSignal.REFRIGERATOR_TEMP,  newVal);
    }

    public int getRefrigeratorLockState(HashMap<String, Object> map) {
        int state = operator.getIntProp(RefrigeratorSignal.REFRIGERATOR_LOCK);
        LogUtils.d(TAG, "getRefrigeratorLockState: " + state);
        return state;
    }

    public void setRefrigeratorLockState(HashMap<String, Object> map) {
        String str = getOneMapValue("switch_type", map);
        if (!TextUtils.isEmpty(str)) {
            operator.setIntProp(RefrigeratorSignal.REFRIGERATOR_LOCK, str.equals("open") ? 2 : 1);
        }
    }

    public int getRefrigeratorWorkState(HashMap<String, Object> map) {
        int state = operator.getIntProp(RefrigeratorSignal.REFRIGERATOR_WORK);
        LogUtils.d(TAG, "getRefrigeratorWorkState: " + state);
        return state;
    }

    public void setRefrigeratorWorkState(HashMap<String, Object> map) {
        String str = getOneMapValue("switch_type", map);
        if (!TextUtils.isEmpty(str)) {
            operator.setIntProp(RefrigeratorSignal.REFRIGERATOR_WORK, str.equals("open") ? 2 : 1);
        }
    }

    public int getCurEnergyMode(HashMap<String, Object> map) {
        int state = operator.getIntProp(RefrigeratorSignal.REFRIGERATOR_ENERGY);
        LogUtils.d(TAG, "getCurEnergyMode: " + state);
        return state;
    }

    public int getOrderEnergyMode(HashMap<String, Object> map) {
        int state = 0;
        String str = getOneMapValue("refrigerator_energyConsumption_switch_mode", map);
        if (!TextUtils.isEmpty(str)) {
            state = str.equals("energy") ? 1 : 2;
        }
        String switchMode = getOneMapValue("switch_mode", map);
        if (!TextUtils.isEmpty(switchMode)) {
            state = switchMode.equals("energy") ? 1 : 2;
        }
        LogUtils.d(TAG, "getOrderEnergyMode: " + state);
        return state;
    }

    public void setRefrigeratorEnergyMode(HashMap<String, Object> map) {
        operator.setIntProp(RefrigeratorSignal.REFRIGERATOR_ENERGY, getOrderEnergyMode(map));
    }

    public void setRefrigeratorKillState(HashMap<String, Object> map) {
        String str = getOneMapValue("switch_type", map);
        if (!TextUtils.isEmpty(str)) {
            operator.setIntProp(RefrigeratorSignal.REFRIGERATOR_KILL, str.equals("open") ? 2 : 1);
        }
    }

    public int getRefrigeratorKillState(HashMap<String, Object> map) {
        int state = operator.getIntProp(RefrigeratorSignal.REFRIGERATOR_KILL);
        LogUtils.d(TAG, "getRefrigeratorKillState: " + state);
        return state;
    }
}
