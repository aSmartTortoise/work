package com.voyah.vcos.virtualdevice.param.intent;

import com.example.filter_annotation.FlowChartDevices;
import com.voyah.vcos.virtualdevice.param.ParamsType;

/**
 * @Date 2024/8/7 10:24
 * @Author 8327821
 * @Email *
 * @Description .
 **/
@FlowChartDevices
public class DmsControlParams extends BaseParams {
    @Override
    public void exe() {
        mContentProviderHelper.addParams("dms_FatigueMonitor", ParamsType.STRING, "", "", "", "疲劳监测(off,standard,sensitive)");
        mContentProviderHelper.addParams("dms_DistractMonitor", ParamsType.INT, 1, 0, 0, "分心监测");
    }
}
