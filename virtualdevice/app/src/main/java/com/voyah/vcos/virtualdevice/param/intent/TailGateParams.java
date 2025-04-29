package com.voyah.vcos.virtualdevice.param.intent;

import com.example.filter_annotation.FlowChartDevices;
import com.voyah.vcos.virtualdevice.param.ParamsType;

/**
 * @Date 2024/8/12 15:57
 * @Author 8327821
 * @Email *
 * @Description .
 **/
@FlowChartDevices
public class TailGateParams extends BaseParams {
    @Override
    public void exe() {
        mContentProviderHelper.addParams("tailgate_Status", ParamsType.STRING, "", "", "fully_closed", "尾门状态，枚举[fully_closed,fully_opened,opening,closing,stopped,releasing,locking]");
    }
}
