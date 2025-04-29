package com.voyah.vcos.virtualdevice.param.intent;

import com.example.filter_annotation.FlowChartDevices;
import com.voyah.vcos.virtualdevice.param.ParamsType;

@FlowChartDevices
public class OutLightControlParams extends BaseParams {

    @Override
    public void exe() {
        mContentProviderHelper.addParams("OutLightSpecifyType", ParamsType.BOOLEAN, "", "", true, "是否指定灯光类型");
        mContentProviderHelper.addParams("OutLightType", ParamsType.INT, 2, 4, 4, "当前车灯类型");
        mContentProviderHelper.addParams("OutLightSetType", ParamsType.INT, 2, 4, 4, "当前想设置的灯光类型");
    }
}
