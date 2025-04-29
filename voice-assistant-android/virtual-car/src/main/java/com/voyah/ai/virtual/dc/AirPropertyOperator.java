package com.voyah.ai.virtual.dc;


import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.signal.AirSignal;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.carservice.vcar.IPropertyOperator;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.voice.platform.agent.api.flowchart.library.bean.FunctionalPositionBean;
import com.voyah.ai.voice.platform.agent.api.functionMatrix.FunctionDeviceMappingManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AirPropertyOperator extends BaseVirtualPropertyOperator {
    private final Logger mLog = LoggerFactory.getLogger(AirPropertyOperator.class);

    public int getBaseIntProp(String key, int area) {
        String virtualKey = getVirtualKey(key, area);
        return (Integer) getValue(virtualKey);
    }


    public void setBaseIntProp(String key, int area, int value) {
        String virtualKey = getVirtualKey(key, area);
        setValue(virtualKey, value);
    }


    public float getBaseFloatProp(String key, int area) {
        String virtualKey = getVirtualKey(key, area);
        return (Float) getValue(virtualKey);
    }


    public void setBaseFloatProp(String key, int area, float value) {
        String virtualKey = getVirtualKey(key, area);
        setValue(virtualKey, value);
    }


    public String getBaseStringProp(String key, int area) {
        String virtualKey = getVirtualKey(key, area);
        return getValue(virtualKey) + "";
    }


    public void setBaseStringProp(String key, int area, String value) {
        String virtualKey = getVirtualKey(key, area);
        switch (key) {
            case AirSignal.AIR_CYCLE_MODE:
                //联动，当设置循环模式的时候，自动循环就会变成false,
                //先删除联动，跟标注保持一致。
//                setValue(AirSignal.AIR_CYCLE_AUTO_STATE, false);
                break;
        }
        setValue(virtualKey, value);
    }


    public boolean getBaseBooleanProp(String key, int area) {
        String virtualKey = getVirtualKey(key, area);
        mLog.info("key:" + virtualKey + "---" + "value:" + area);
        mLog.info("虚拟车是否为null:" + (iVirtualDevice == null));
        return (Boolean) getValue(virtualKey);
    }

    /**
     * 需要多位置的信号是少数，因此只对这些这些做拼接
     *
     * @param key  原始key
     * @param area 多位置， 0，1，2，3
     * @return
     */
    String getVirtualKey(String key, int area) {
        if (area != 0 && area != IPropertyOperator.AREA_NONE && area != 9) {
            return key + "_" + area;
        } else {
            return key;
        }
    }


    public void setBaseBooleanProp(String key, int area, boolean value) {
        String virtualKey = getVirtualKey(key, area);
        //自动循环涉及到联动
        switch (key) {
            case AirSignal.AIR_CYCLE_AUTO_STATE:
//                if (value) {
//                    setValue(AirSignal.AIR_CYCLE_MODE, "auto_circulation");
//                } else {
//                    //todo 缺少自动循环关闭会设置成什么结果的情况。
//                }

                break;
        }
        setValue(virtualKey, value);
    }

    @Override
    public void registerDevice() {

        //注册当前车的屏幕类型。
        boolean hasCeilScreen = getBooleanProp(CommonSignal.COMMON_CEIL_CONFIG);
        String screenValue;
        if (hasCeilScreen) {
            screenValue = "central_screen-passenger_screen-ceil_screen-armrest_screen";
        } else {
            screenValue = "central_screen-passenger_screen-armrest_screen";
        }

        //空调开关功能，可以当成空调个数。
        String airSwitchValue;
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();
        if (carType.equals("H56D")) {
            airSwitchValue = "0-0-0-0-0-0-1-1-0";
        } else {
            airSwitchValue = "1";
        }
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_SWITCH_STATE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, airSwitchValue);

        //负离子功能
        //判断当前车型是
        String anionValue;
        int configurationType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarModeConfig();
        if (configurationType == 2 || configurationType == 3) {
            anionValue = "-1";
        } else {
            anionValue = "1";
        }
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_ANION_SWITCH_STATE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, anionValue);
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_AUTO_ANION_SWITCH_STATE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, anionValue);


        String AirUIValue = "";
        //空调界面功能位判断
        if (hasCeilScreen) {
            AirUIValue = "central_screen-passenger_screen-ceil_screen-armrest_screen";
        } else {
            AirUIValue = "central_screen-passenger_screen-armrest_screen";
        }
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_UI_STATE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, AirUIValue);

        //智能识别开关功能位
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_INTELLIGENT_IDENTIFICATION_SWITCH_STATE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "1");

        //Auto开关功能位,前排，二排
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AUTO_SWITCH_STATE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "0-0-0-0-0-0-1-1-0");
        //自动循环开关
        String autoCycleValue;
        if (configurationType == 2) {
            autoCycleValue = "-1";
        } else {
            autoCycleValue = "1";
        }
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_CYCLE_AUTO_STATE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, autoCycleValue);

        //通风降温功能位
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.VENTILATION_SWITCH_STATE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "1");
        //干燥除味功能位
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.DRY_MODE_SWITCH_STATE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "1");
        //PM2.5功能位
        String PMValue;
        if (configurationType == 2) {
            PMValue = "-1";
        } else {
            PMValue = "1";
        }
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_PM, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, PMValue);
        //扫风模式功能位
        String windModeValue;
        if (carType.equals("H56D")) {
            windModeValue = "-1";
        } else {
            windModeValue = "1";
        }
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.SCAVENGING_WIND_MODE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, windModeValue);

        //空调温度功能位的处理
        //主驾、副驾、二排是支持温度调节的
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_TEMP, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "1-1-0-0-0-0-0-1-0");
        //温区同步功能位
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_TEMP_SYN_STATE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "0-0-0-0-0-0-1-0-0");
        //吹风模式
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.WIND_MODE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "0-0-0-0-0-0-1-1");
        //todo AirWind 功能位添加，需要别的车型也要添加
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_WIND, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "0-0-0-0-0-0-1-1");
        //出风口开关功能是否支持的功能位
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_OUTLET_SWITCH, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "1");

        //============通用的功能位注册=========


        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(CommonSignal.COMMON_SCREEN_ENUMERATION, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL,
                screenValue);
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(CommonSignal.COMMON_SEAT_STATE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "1-1-1-1-1-1");


        //===特殊功能位判断56有的逻辑，但37A里没有===
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_SWITCH_STATE_SEAT_SINGLE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL,
                "1");


        String fastCooling;
        String fastHeater;

        fastCooling = "-1";
        fastHeater = "-1";

        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_FAST_COOLING_SWITCH_STATE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL,
                fastCooling);
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_FAST_HEATER_SWITCH_STATE, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL,
                fastHeater);
        //AIR_FRONT_DEFROST_OPEN_RIGHT_TEMP_IS_ADJUST,前除霜打开后，是否可以支持副驾温度调节功能位。
        String isFrontDefrostOpenRightTempIsAdjust;

        isFrontDefrostOpenRightTempIsAdjust = "-1";

        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_FRONT_DEFROST_OPEN_RIGHT_TEMP_IS_ADJUST, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL,
                isFrontDefrostOpenRightTempIsAdjust);
        //AUTO开关是否支持
        String autoSwitchIsSupport;

        autoSwitchIsSupport = "-1";

        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_AUTO_SWITCH_IS_SUPPORT, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL,
                autoSwitchIsSupport);
        //出风口开关功能是否存在。
        String airOutletIsSupport;

        airOutletIsSupport = "-1";

        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(AirSignal.AIR_OUTLET_SWITCH_IS_SUPPORT, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL,
                airOutletIsSupport);
    }


}
