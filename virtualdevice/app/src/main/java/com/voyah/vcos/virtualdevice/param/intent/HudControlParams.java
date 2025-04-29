package com.voyah.vcos.virtualdevice.param.intent;


import com.example.filter_annotation.FlowChartDevices;
import com.voyah.vcos.virtualdevice.param.ParamsType;

@FlowChartDevices
public class HudControlParams extends BaseParams{
    @Override
    public void exe() {
        mContentProviderHelper.addParams("hud_MainSwitch", ParamsType.INT, 1, 0, 0, "HUD主开关(0,1)");
        mContentProviderHelper.addParams("hud_Mode", ParamsType.STRING, "", "", "ar", "HUD模式(simplest;navi;intelligent_driving;standard;ar)");
        mContentProviderHelper.addParams("hud_SnowModeSwitch", ParamsType.INT, 1,0,0,"雪地模式开关(0,1)");
        mContentProviderHelper.addParams("hud_AutoLight", ParamsType.INT, 1,0,0,"自动亮度开关(0,1)");
        mContentProviderHelper.addParams("hud_ManualLight", ParamsType.INT, 100,0,0,"手动亮度(0-100)");
        mContentProviderHelper.addParams("hud_AutoHeight", ParamsType.INT, 1,0,0,"自动高度开关(0,1)");
        mContentProviderHelper.addParams("hud_ManualHeight", ParamsType.INT, 100,0,0,"手动高度(0-100)");
        mContentProviderHelper.addParams("hud_PrivacyMode", ParamsType.INT, 1,0,0,"隐私模式开关(0,1)");
        mContentProviderHelper.addParams("hud_Temperature", ParamsType.INT, 1,0,0,"HUD温度预警(0正常，1过热)");
    }
}
