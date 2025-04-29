package com.voyah.vcos.virtualdevice.param.intent;

import com.example.filter_annotation.FlowChartDevices;
import com.voyah.vcos.virtualdevice.param.ParamsType;

/**
 * @Date 2024/8/16 14:19
 * @Author 8327821
 * @Email *
 * @Description .
 **/
@FlowChartDevices
public class SunShadeParams extends BaseParams {
    @Override
    public void exe() {
        mContentProviderHelper.addParams("sunshade_state", ParamsType.INT, 100, 0, 0, "0是全关，100是全开，50表示所有暂停，中间不能调");
    }
}
