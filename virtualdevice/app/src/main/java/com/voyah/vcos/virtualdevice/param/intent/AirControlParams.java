package com.voyah.vcos.virtualdevice.param.intent;


import com.example.filter_annotation.FlowChartDevices;
import com.voyah.vcos.virtualdevice.param.ParamsType;

/**
 * 模式的值跟nlu的结果保持一致。
 *
 * 广播 这样发送
 * adb shell am broadcast -a com.voyah.ai.test.virtualDevice --es query "virtualDeviceSetInstruction:AirSwitchState=true:AirTemp_0=25.0f" -f 0x01000000
 */
@FlowChartDevices
public class AirControlParams extends BaseParams{
    @Override
    public void exe() {
        mContentProviderHelper.addParams("AirSwitchState", ParamsType.BOOLEAN,"","",true,"空调开关");
        mContentProviderHelper.addParams("AirTempSynState", ParamsType.BOOLEAN,"","",true,"空调同步开关");
        mContentProviderHelper.addParams("AirTemp", ParamsType.FLOAT,32.5f,17.5f,25.0f,"主驾当前温度");
        mContentProviderHelper.addParams("AirTemp_1", ParamsType.FLOAT,32.5f,17.5f,25.0f,"副驾当前温度");
        mContentProviderHelper.addParams("MinAirTemp", ParamsType.FLOAT,17.5f,17.5f,17.5f,"最小温度");
        mContentProviderHelper.addParams("MaxAirTemp", ParamsType.FLOAT,32.5f,32.5f,32.5f,"最大温度");
        mContentProviderHelper.addParams("AirWind", ParamsType.INT,8,0,2,"空调风量");
        mContentProviderHelper.addParams("MaxAirWind", ParamsType.INT,8,8,8,"空调最大风量");
        mContentProviderHelper.addParams("MinAirWind", ParamsType.INT,0,0,0,"空调最小风量");
        mContentProviderHelper.addParams("ACSwitchStatus", ParamsType.BOOLEAN,"","",true,"空调AC开关");
        mContentProviderHelper.addParams("WindMode", ParamsType.STRING,"","","to_face","空调吹风模式");
        mContentProviderHelper.addParams("ScavengingWindMode", ParamsType.STRING,"","","focus_face","主驾空调扫风模式");
        mContentProviderHelper.addParams("ScavengingWindMode_1", ParamsType.STRING,"","","focus_face","副驾空调扫风模式");
        mContentProviderHelper.addParams("FrontDefrostSwitchState", ParamsType.BOOLEAN,"","",false,"前除霜开关");
        mContentProviderHelper.addParams("RearDefrostSwitchState", ParamsType.BOOLEAN,"","",false,"后除霜开关");
        mContentProviderHelper.addParams("AirCycleMode", ParamsType.STRING,"","","outer_circulation","空调循环模式");
        mContentProviderHelper.addParams("AirCycleAutoState", ParamsType.BOOLEAN,"","",false,"空调自动循环开关");
        mContentProviderHelper.addParams("AirPM", ParamsType.INT,"","",20,"pm2.5的值");
        mContentProviderHelper.addParams("DryMode", ParamsType.STRING,"","","standard","干燥模式");
        mContentProviderHelper.addParams("DryModeSwitchState", ParamsType.BOOLEAN,"","",false,"干燥模式开关");
        mContentProviderHelper.addParams("VentilationSwitchState", ParamsType.BOOLEAN,"","",false,"通风降温开关");
        mContentProviderHelper.addParams("AutoDefrostSwitchState", ParamsType.BOOLEAN,"","",false,"自动除霜开关");
        mContentProviderHelper.addParams("AutoDefrostLevel", ParamsType.STRING,"min","min","min","自动除霜等级");
        mContentProviderHelper.addParams("MaxAutoDefrostLevel", ParamsType.STRING,"max","max","max","自动除霜最大等级");
        mContentProviderHelper.addParams("MinAutoDefrostLevel", ParamsType.STRING,"min","min","min","自动除霜最小等级");
        mContentProviderHelper.addParams("AutoSwitchState", ParamsType.BOOLEAN,"","",false,"auto开关");
    }
}
