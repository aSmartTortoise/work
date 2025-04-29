package com.voyah.vcos.virtualdevice.param.intent;

import com.example.filter_annotation.FlowChartDevices;
import com.voyah.vcos.virtualdevice.param.ParamsType;

/**
 * @Date 2024/8/7 10:08
 * @Author 8327821
 * @Email *
 * @Description .
 **/
@FlowChartDevices
public class FuelPortControlParams extends BaseParams{
    @Override
    public void exe() {
        mContentProviderHelper.addParams("fuel_Config", ParamsType.INT, 1, 0, 0,"是否有配置加油口（0没有1有）");
        mContentProviderHelper.addParams("fuel_Switch", ParamsType.INT, 1, 0, 0,"加油口开关（0,1）");
    }
}
