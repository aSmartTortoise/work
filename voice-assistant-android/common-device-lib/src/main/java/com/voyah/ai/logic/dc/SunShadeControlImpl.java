package com.voyah.ai.logic.dc;


import android.text.TextUtils;

import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.signal.SunShadeSignal;
import com.voice.sdk.util.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class SunShadeControlImpl extends AbsDevices {

    private static final String TAG = SunShadeControlImpl.class.getSimpleName();

    public SunShadeControlImpl() {
        super();
    }

    @Override
    public String getDomain() {
        return "SunShade";
    }

    @Override
    public String replacePlaceHolderInner(HashMap<String, Object> map, String str) {
        LogUtils.d(TAG, "tts :" + str);
        return str;
    }

    //判断用户说的是打开前排还是后排遮阳帘
    public boolean frontOrRearSunShade(HashMap<String, Object> map) {
        boolean frontOrBack = false;//默认false    false代表后排 true 代表要执行前排
        String positions = getOneMapValue("positions", map);
        if (!TextUtils.isEmpty(positions)) {
            switch (positions) {
                case "first_row_left"://主驾
                case "first_row_right"://副驾
                case "first_row":
                case "front_side":
                    frontOrBack = true;
                    break;
                default:
                    break;
            }

        } else {
            ArrayList<Integer> curSoundPositions = (ArrayList<Integer>) curSoundPositions(map);
            int curPosition = curSoundPositions.get(0);
            if (curPosition == 0 || curPosition == 1) {//代表是前排
                frontOrBack = true;
            }
        }

        return frontOrBack;
    }




    /**
     * 当前遮阳帘开启数值
     *
     * @return 打开程度值
     */
    public int curSunShadeOpenDegree(HashMap<String, Object> map) {
        int curValue = operator.getIntProp(SunShadeSignal.SUNSHADE_STATE);
        LogUtils.d(TAG, "curSunShadeOpenDegree curValue : " + curValue);
        return curValue;
    }

    /**
     * 关闭程度转化成打开程度(例子：打开40% = 关闭60%)
     */
    public void closeTransformOpen(HashMap<String, Object> map) {
        int number = getNumber(map);
        map.put("number", String.valueOf(100 - number));
    }

    /**
     * 打开遮阳帘到指定数值
     */
    public void openSunShade(HashMap<String, Object> map) {
        int sunShadeOpenNum = Integer.valueOf((String) getValueInContext(map, "sun_shade_open_num"));
        operator.setIntProp(SunShadeSignal.SUNSHADE_STATE, sunShadeOpenNum);
        LogUtils.d(TAG, "openSunShade sunShadeOpenNum : " + sunShadeOpenNum);
    }

    /**
     * 条件对比
     *
     * @return true：符合条件 false：不符合条件
     */
    public boolean isEligible(HashMap<String, Object> map) {
        int number = getNumber(map);
        return curSunShadeOpenDegree(map) == number;
    }

    /**
     * 设置遮阳帘开大一点
     */
    public void openSunShadeLittle(HashMap<String, Object> map) {
        int curSunShadeOpenDegree = curSunShadeOpenDegree(map);
        int min = Math.min((curSunShadeOpenDegree + 10), 100);
        operator.setIntProp(SunShadeSignal.SUNSHADE_STATE, min);
        LogUtils.d(TAG, "openSunShadeLittle : " + min);
    }

    /**
     * 设置遮阳帘关小一点
     */
    public void closeSunShadeLittle(HashMap<String, Object> map) {
        int curSunShadeOpenDegree = curSunShadeOpenDegree(map);
        int max = Math.max(curSunShadeOpenDegree - 10, 0);
        operator.setIntProp(SunShadeSignal.SUNSHADE_STATE, max);
        LogUtils.d(TAG, "closeSunShadeLittle : " + max);
    }

    /**
     * 设置遮阳帘打开num
     */
    public void setOpenNum(HashMap<String, Object> map) {
        int number = getNumber(map);
        operator.setIntProp(SunShadeSignal.SUNSHADE_STATE, number);
        LogUtils.d(TAG, "setOpenNum :" + number);
    }

    /**
     * 再打开遮阳帘num
     */
    public void setReopen(HashMap<String, Object> map) {
        int number = getNumber(map);
        int curSunShadeOpenDegree = curSunShadeOpenDegree(map);
        int min = Math.min(curSunShadeOpenDegree + number, 100);
        operator.setIntProp(SunShadeSignal.SUNSHADE_STATE, min);
        LogUtils.d(TAG, "setReopen :" + min);
    }

    /**
     * 再关闭遮阳帘num
     */
    public void setCloseAgain(HashMap<String, Object> map) {
        int number = getNumber(map);
        int curSunShadeOpenDegree = curSunShadeOpenDegree(map);
        int max = Math.max(curSunShadeOpenDegree - number, 0);
        operator.setIntProp(SunShadeSignal.SUNSHADE_STATE, max);
        LogUtils.d(TAG, "setCloseAgain :" + max);
    }

    /**
     * 获取number的值
     */
    public int getNumber(HashMap<String, Object> map) {
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

    public boolean isTwoSunshade(HashMap<String, Object> map){//是否是两个遮阳帘，是否是H56C  H56D
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();//获取车型
        if ("H56C".equals(carType) || "H56D".equals(carType)) {
            return true;
        }else{//H37A H37B
            return false;
        }
    }
}
