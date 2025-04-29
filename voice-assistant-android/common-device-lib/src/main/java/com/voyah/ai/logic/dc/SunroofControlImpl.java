package com.voyah.ai.logic.dc;


import android.text.TextUtils;

import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.signal.SunroofSignal;
import com.voice.sdk.util.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class SunroofControlImpl extends AbsDevices {

    private static final String TAG = SunroofControlImpl.class.getSimpleName();

    public SunroofControlImpl() {
        super();
    }

    @Override
    public String getDomain() {
        return "Sunroof";
    }

    @Override
    public String replacePlaceHolderInner(HashMap<String, Object> map, String str) {
        LogUtils.d(TAG, "tts :" + str);
        return str;
    }

    /**
     * 判断天窗当前是否是关闭状态
     */
    public boolean isClosedSunroof(HashMap<String, Object> map){
        boolean isClosed = false;//默认不是关闭状态   false不是关闭  true是关闭
        int valueClosed = operator.getIntProp(SunroofSignal.SUNROOF_ISSUNROOFAIR);
        if(valueClosed == 3){//代表天窗是关闭状态
            isClosed = true;
        }
        return isClosed;
    }


    /**
     * 当前天窗开启数值
     *
     * @return 打开程度值
     */
    public int curSunroofOpenDegree(HashMap<String, Object> map) {
        int curValue = operator.getIntProp(SunroofSignal.SUNROOF_WINDOW);
        LogUtils.d(TAG, "curSunroofOpenDegree curValue : " + curValue);
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
     * 条件对比
     *
     * @return true：符合条件 false：不符合条件
     */
    public boolean isEligible(HashMap<String, Object> map) {
        int number = getNumber(map);
        return curSunroofOpenDegree(map) == number;
    }

    /**
     * 打开天窗到指定数值
     */
    public void openSunroof(HashMap<String, Object> map) {
        int sunroofOpenNum = Integer.valueOf((String) getValueInContext(map, "sunroof_open_num"));
        LogUtils.d(TAG, "openSunroof sunroofOpenNum : " + sunroofOpenNum);
        setSunroof(sunroofOpenNum);
    }

    /**
     * 设置天窗打开num
     */
    public void setOpenNum(HashMap<String, Object> map) {
        int number = getNumber(map);
        LogUtils.d(TAG, "setOpenNum number : " + number);
        setSunroof(number);
    }

    /**
     * 设置天窗开大一点
     */
    public void openSunroofLittle(HashMap<String, Object> map) {
        int curSunroofOpenDegree = curSunroofOpenDegree(map);
        int min = Math.min((curSunroofOpenDegree + 10), 100);
        LogUtils.d(TAG, "openSunroofLittle : " + min);
        setSunroof(min);
    }

    /**
     * 设置天窗关小一点
     */
    public void closeSunroofLittle(HashMap<String, Object> map) {
        int curSunroofOpenDegree = curSunroofOpenDegree(map);
        int max = Math.max(curSunroofOpenDegree - 10, 0);
        LogUtils.d(TAG, "closeSunroofLittle : " + max);
        setSunroof(max);
    }

    /**
     * 再打开天窗num
     */
    public void setReopen(HashMap<String, Object> map) {
        int number = getNumber(map);
        int curSunroofOpenDegree = curSunroofOpenDegree(map);
        int min = Math.min(curSunroofOpenDegree + number, 100);
        LogUtils.d(TAG, "setReopen : " + min);
        setSunroof(min);
    }

    /**
     * 再关闭天窗num
     */
    public void setCloseAgain(HashMap<String, Object> map) {
        int number = getNumber(map);
        int curSunroofOpenDegree = curSunroofOpenDegree(map);
        int max = Math.max(curSunroofOpenDegree - number, 0);
        LogUtils.d(TAG, "setCloseAgain : " + max);
        setSunroof(max);
    }

    /**
     * 天窗翘起状态判断（天窗通风）
     *
     * @return true：是天窗翘起状态 false：不是天窗翘起状态
     */
    public boolean isSunroofAir(HashMap<String, Object> map) {

        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();//获取车型
        if ("H37A".equals(carType) || "H37B".equals(carType)) {
            boolean curValue = operator.getBooleanProp(SunroofSignal.SUNROOF_VENTILATION);
            LogUtils.d(TAG, "isSunroofAir : " + curValue);
            return curValue;
        }else{//H56C H56D
            int cur =operator.getIntProp(SunroofSignal.SUNROOF_ISSUNROOFAIR);
            LogUtils.d(TAG, "isSunroofAir : " + cur);
            return cur ==0 ? true:false;
        }

    }

    /**
     * 设置成天窗翘起（天窗通风）
     */
    public void setSunroofAir(HashMap<String, Object> map) {
        LogUtils.d(TAG, "setSunroofAir");
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();//获取车型
        if ("H37A".equals(carType) || "H37B".equals(carType)) {
            operator.setBooleanProp(SunroofSignal.SUNROOF_VENTILATION, true);
        }else{//H56C H56D
            operator.setIntProp(SunroofSignal.SUNROOF_SETSUNROOFAIR, 4);
        }
    }

    /**
     * 判断是否是要翘起后排天窗
     */
    public boolean judgBack(HashMap<String, Object> map){
        boolean isBack = false;//默认不是后排
        String positions = getOneMapValue("positions", map);
        if (!TextUtils.isEmpty(positions)) {
            switch (positions){
                case "second_row_left"://二排左
                case "second_row_right"://二排右
                case "third_row_left"://三排左
                case "third_row_right"://三排右
                case "second_row"://二排
                case "third_row"://三排
                case "rear_row"://后排
                    isBack = true;//证明要调节后排天窗翘起
                    break;
                default://主驾副驾，或者后排中间等，需要提示用户说打开左边还是右边的，这里不做处理
                    LogUtils.d("------指定位置-------"+positions,"==================");
                    break;
            }
        }else {
            LogUtils.d("------指定位置为空，默认是前排------","==================");
        }
        return isBack;
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

    /**
     * 具体执行
     */
    public void setSunroof(int number) {
        operator.setIntProp(SunroofSignal.SUNROOF_WINDOW, number);
    }

    //判断是否执行前排 还是后排     打开/关闭 后排天窗
    public boolean isRearSunroof(HashMap<String, Object> map){
        boolean frontOrBack = false;//默认false    false代表后排 true 代表要执行前排
        String positions = getOneMapValue("positions", map);
        if (!TextUtils.isEmpty(positions)) {
            switch (positions){
                case "second_row_left"://二排左
                case "second_row_right"://二排右
                case "second_row_mid"://二排中/后排中
                case "third_row_left"://三排左
                case "third_row_right"://三排右
                case "second_side":
                case "second_row_side"://后排/二排
                case "third_row_side"://三排
                case "rear_side":
                case "rear_side_left":
                case "rear_side_right":
                case "rear_side_mid":
                    frontOrBack = true;
                    break;
                default:
                    break;
            }

        }else {
            ArrayList<Integer> curSoundPositions = (ArrayList<Integer>) curSoundPositions(map);
            int curPosition = curSoundPositions.get(0);
            if(curPosition ==2 ||curPosition ==3 || curPosition ==4 || curPosition==5 ){//代表是前排
                frontOrBack = true;
            }
        }

        return  frontOrBack;
    }


    //判断是否是H56C 或者H56D
    public boolean isH56CorH56D(HashMap<String, Object> map){
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();//获取车型

        if ("H37A".equals(carType) || "H37B".equals(carType)) {
            return false;
        }else{//H56C H56D
            return true;
        }
    }
}
