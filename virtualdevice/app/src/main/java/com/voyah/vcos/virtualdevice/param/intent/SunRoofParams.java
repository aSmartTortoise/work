package com.voyah.vcos.virtualdevice.param.intent;

import com.example.filter_annotation.FlowChartDevices;
import com.voyah.vcos.virtualdevice.param.ParamsType;

/**
 * @Date 2024/8/16 15:56
 * @Author 8327821
 * @Email *
 * @Description .
 **/
@FlowChartDevices
public class SunRoofParams extends BaseParams {
    @Override
    public void exe() {
        mContentProviderHelper.addParams("sunroof_window", ParamsType.INT, 100, 0, 0,"100全开、0全关[0,100]");
        mContentProviderHelper.addParams("sunroof_ventilation", ParamsType.BOOLEAN, "", "", false, "天窗是否翘起[true,false]");
    }
}
