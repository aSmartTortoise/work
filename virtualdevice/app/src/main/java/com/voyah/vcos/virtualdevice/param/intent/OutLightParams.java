package com.voyah.vcos.virtualdevice.param.intent;

import com.example.filter_annotation.FlowChartDevices;
import com.voyah.vcos.virtualdevice.param.ParamsType;

/**
 * @Date 2024/8/12 18:58
 * @Author 8327821
 * @Email *
 * @Description .
 **/
@FlowChartDevices
public class OutLightParams extends BaseParams {
    @Override
    public void exe() {
        mContentProviderHelper.addParams("lamp_Mode", ParamsType.STRING, "", "", "", "大灯模式[default, off, position, low, auto]");
        mContentProviderHelper.addParams("lamp_Fog", ParamsType.INT, 1, 0 , 0, "雾灯开关[0,1]");
        mContentProviderHelper.addParams("lamp_Switch", ParamsType.INT, 1, 0, 0, "大灯开关[0,1]");
        mContentProviderHelper.addParams("lamp_Height", ParamsType.INT, 5, 1, 1, "大灯高度[1,5]");
    }
}
