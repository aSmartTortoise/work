package com.voyah.vcos.virtualdevice.param.intent;

import com.example.filter_annotation.FlowChartDevices;
import com.voyah.vcos.virtualdevice.param.ParamsType;

/**
 * @Date 2024/8/7 10:18
 * @Author 8327821
 * @Email *
 * @Description .
 **/
@FlowChartDevices
public class OmsControlParams extends BaseParams {
    @Override
    public void exe() {
        mContentProviderHelper.addParams("oms_ForgetRemind", ParamsType.INT, 1, 0, 0, "后排乘员遗忘开关");
    }
}
