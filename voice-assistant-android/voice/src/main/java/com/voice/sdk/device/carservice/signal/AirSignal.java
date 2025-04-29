package com.voice.sdk.device.carservice.signal;

/**
 *   mContentProviderHelper.addParams("AirSwitchState", ParamsType.BOOLEAN,"","",true,"空调开关");
 *         mContentProviderHelper.addParams("AirTempSynState", ParamsType.BOOLEAN,"","",true,"空调同步开关");
 *         mContentProviderHelper.addParams("AirTemp_0", ParamsType.FLOAT,32.5f,17.5f,25.0f,"主驾当前温度");
 *         mContentProviderHelper.addParams("AirTemp_1", ParamsType.FLOAT,32.5f,17.5f,25.0f,"副驾当前温度");
 *         mContentProviderHelper.addParams("MinAirTemp", ParamsType.FLOAT,17.5f,17.5f,17.5f,"最小温度");
 *         mContentProviderHelper.addParams("MaxAirTemp", ParamsType.FLOAT,32.5f,32.5f,32.5f,"最大温度");
 *         mContentProviderHelper.addParams("AirWind", ParamsType.INT,8,0,2,"空调风量");
 *         mContentProviderHelper.addParams("MaxAirWind", ParamsType.INT,8,8,8,"空调最大风量");
 *         mContentProviderHelper.addParams("MinAirWind", ParamsType.INT,0,0,0,"空调最小风量");
 *         mContentProviderHelper.addParams("ACSwitchStatus", ParamsType.BOOLEAN,"","",true,"空调AC开关");
 *         mContentProviderHelper.addParams("WindMode", ParamsType.STRING,"","","to_face","空调吹风模式");
 *         mContentProviderHelper.addParams("ScavengingWindMode_0", ParamsType.STRING,"","","focus_face","主驾空调扫风模式");
 *         mContentProviderHelper.addParams("ScavengingWindMode_1", ParamsType.STRING,"","","focus_face","副驾空调扫风模式");
 *         mContentProviderHelper.addParams("FrontDefrostSwitchState", ParamsType.BOOLEAN,"","",false,"前除霜开关");
 *         mContentProviderHelper.addParams("RearDefrostSwitchState", ParamsType.BOOLEAN,"","",false,"后除霜开关");
 *         mContentProviderHelper.addParams("AirCycleMode", ParamsType.STRING,"","","outer_circulation","空调循环模式");
 *         mContentProviderHelper.addParams("AirCycleAutoState", ParamsType.BOOLEAN,"","",false,"空调自动循环开关");
 *         mContentProviderHelper.addParams("AirPM", ParamsType.INT,"","",20,"pm2.5的值");
 *         mContentProviderHelper.addParams("DryMode", ParamsType.STRING,"","","standard","干燥模式");
 *         mContentProviderHelper.addParams("DryModeSwitchState", ParamsType.BOOLEAN,"","",false,"干燥模式开关");
 *         mContentProviderHelper.addParams("VentilationSwitchState", ParamsType.BOOLEAN,"","",false,"通风降温开关");
 *         mContentProviderHelper.addParams("AutoDefrostSwitchState", ParamsType.BOOLEAN,"","",false,"自动除霜开关");
 *         mContentProviderHelper.addParams("AutoDefrostLevel", ParamsType.INT,3,1,1,"自动除霜等级");
 *         mContentProviderHelper.addParams("AutoSwitchState", ParamsType.BOOLEAN,"","",false,"auto开关");
 */
public class AirSignal {
    public static final String AIR_SWITCH_STATE = "AirSwitchState";
    public static final String AIR_TEMP_SYN_STATE = "AirTempSynState";
    public static final String AIR_TEMP = "AirTemp";
    public static final String MIN_AIR_TEMP = "MinAirTemp";
    public static final String MAX_AIR_TEMP = "MaxAirTemp";
    public static final String AIR_WIND = "AirWind";
    public static final String MAX_AIR_WIND = "MaxAirWind";
    public static final String MIN_AIR_WIND = "MinAirWind";
    public static final String AC_SWITCH_STATUS = "ACSwitchStatus";
    public static final String WIND_MODE = "WindMode";
    public static final String SCAVENGING_WIND_MODE = "ScavengingWindMode";
    public static final String FRONT_DEFROST_SWITCH_STATE = "FrontDefrostSwitchState";
    public static final String REAR_DEFROST_SWITCH_STATE = "RearDefrostSwitchState";
    public static final String AIR_CYCLE_MODE = "AirCycleMode";
    public static final String AIR_CYCLE_AUTO_STATE = "AirCycleAutoState";
    public static final String AIR_CYCLE_AUTO_STATE2 = "AirCycleAutoState2";
    public static final String AIR_PM = "AirPM";
    public static final String DRY_MODE = "DryMode";
    public static final String DRY_MODE_SWITCH_STATE = "DryModeSwitchState";
    public static final String VENTILATION_SWITCH_STATE = "VentilationSwitchState";
    public static final String AUTO_DEFROST_SWITCH_STATE = "AutoDefrostSwitchState";
    public static final String AUTO_DEFROST_LEVEL = "AutoDefrostLevel";
    public static final String MAX_AUTO_DEFROST_LEVEL = "MaxAutoDefrostLevel";
    public static final String MIN_AUTO_DEFROST_LEVEL = "MinAutoDefrostLevel";
    public static final String AUTO_SWITCH_STATE = "AutoSwitchState";
    public static final String ANION_SWITCH_STATE = "AnionSwitchState";

    public static final String AIR_ANION_SWITCH_STATE = "air_AnionSwitchState";
    public static final String AIR_AUTO_ANION_SWITCH_STATE = "air_AutoAnionSwitchState";
    public static final String AIR_INTELLIGENT_IDENTIFICATION_SWITCH_STATE = "air_IntelligentIdentificationSwitchState";
    public static final String AIR_UI_STATE = "air_airUiState";

    //开关空调是否有座位判断逻辑的占位符
    public static final String AIR_SWITCH_STATE_SEAT_SINGLE = "air_airSwitchStateSeatSingle";
    //37B新增的功能位
    public static final String AIR_FAST_COOLING_SWITCH_STATE = "air_fastCoolingSwitchState";
    public static final String AIR_FAST_HEATER_SWITCH_STATE = "air_fastHeaterSwitchState";
    public static final String AIR_FRONT_DEFROST_OPEN_RIGHT_TEMP_IS_ADJUST = "air_frontDefrostOpenRightTempIsAdjust";
    public static final String AIR_AUTO_SWITCH_IS_SUPPORT = "air_autoSwitchIsSupport";
    public static final String AIR_OUTLET_SWITCH_IS_SUPPORT = "air_outletSwitchIsSupport";
    public static final String AIR_OUTLET_SWITCH = "air_outletSwitch";
    public static final String AIR_CABIN_TEMP = "air_cabinTemp";

}
