package com.voyah.ai.logic.dc;

import com.voice.sdk.device.carservice.dc.carsetting.SettingConstants;
import com.voice.sdk.device.carservice.dc.context.DCContext;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.carservice.signal.VehicleConditionSignal;
import com.voice.sdk.util.LogUtils;

import java.util.HashMap;

public class VehicleConditionControlImpl extends AbsDevices {

    private static final String TAG = "VehicleConditionControlImpl";

    public VehicleConditionControlImpl() {
        super();
    }

    @Override
    public String getDomain() {
        return "VehicleCondition";
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
            case "soc_num":
                String power = (String) getValueInContext(map, "power");
                str = str.replace("@{soc_num}", power);
                break;
            case "mileage_num":
                String number = (String) getValueInContext(map, "number");
                str = str.replace("@{mileage_num}", number);
                break;
            default:
                LogUtils.e(TAG, "当前要处理的@{" + key + "}不存在");
                return str;
        }
        LogUtils.e(TAG, "tts :" + start + " " + end + " " + key + " " + index);

        LogUtils.e(TAG, "tts :" + str);
        return str;
    }

    public void curRemainingBattery(HashMap<String, Object> map) {
        float curPower = operator.getFloatProp(CommonSignal.COMMON_REMAIN_POWER);
        //需要四舍五入
        int curPowerInteger = Math.round(curPower);
        map.put("power", curPowerInteger + "%");
    }

    public void curRemainingMileage(HashMap<String, Object> map) {
        int curRemainingMileage = operator.getIntProp(VehicleConditionSignal.CONDITION_REMAIN_MILEAGE);
        map.put("number", String.valueOf(curRemainingMileage));
    }

    //0 = 充电中 1 = 充电完成 2 = 电网未供电 3 = 充电故障 4 = 充电加热 5 = 充电等待计时 255 = 未知状态
    public boolean isCharging(HashMap<String, Object> map) {
        return operator.getBooleanProp(VehicleConditionSignal.CONDITION_IS_CHARGING);
    }

    public void openTripCardPage(HashMap<String, Object> map) {
        mSettingHelper.exec(SettingConstants.ENERGY_STATISTICS);
    }

    public boolean isSupportVehicleLevel(HashMap<String, Object> map) {
        int vehicleLevel = getVehicleLevel();
        return vehicleLevel > 0 && vehicleLevel < 5;
    }

    public void getMaximumRange(HashMap<String, Object> map) {
        int number = operator.getIntProp(VehicleConditionSignal.CONDITION_MAX_MILEAGE);
        map.put("number", String.valueOf(number));
    }

    public int getVehicleLevel() {
        return operator.getIntProp(CommonSignal.COMMON_EQUIPMENT_LEVEL);
    }

    public void getAllRange(HashMap<String, Object> map) {
        float value = operator.getFloatProp(VehicleConditionSignal.CONDITION_TRAVELED_MILEAGE);
        int intValue = Math.round(value);
        map.put("number", String.valueOf(intValue));
    }

    public void openCarHealthPage(HashMap<String, Object> map) {
        String nlu_info = (String) getValueInContext(map, DCContext.NLU_INFO);
        operator.setIntProp(CommonSignal.COMMON_CAR_HEALTH, nlu_info.contains("vehicleCondition_maintenanceTimes") ? 1 : 0);
    }

    public boolean isEVCar(HashMap<String, Object> map) {
        int isEVCar = operator.getIntProp(CommonSignal.COMMON_POWER_MODER);
        LogUtils.i(TAG, "isEVCar :" + isEVCar);
        return isEVCar == 0;
    }
}
