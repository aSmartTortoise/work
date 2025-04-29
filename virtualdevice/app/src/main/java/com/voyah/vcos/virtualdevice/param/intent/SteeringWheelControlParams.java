package com.voyah.vcos.virtualdevice.param.intent;

import com.example.filter_annotation.FlowChartDevices;
import com.voyah.vcos.virtualdevice.param.ParamsType;

/**
 * @Date 2024/8/6 14:25
 * @Author 8327821
 * @Email *
 * @Description .
 **/
@FlowChartDevices
public class SteeringWheelControlParams extends BaseParams {
    @Override
    public void exe() {
        mContentProviderHelper.addParams("steering_wheelCustomType", ParamsType.STRING, "", "", "low_speed_alert", "方向盘按键自定义(source_switch;360_park;trip_shoot;selfie;screen_shift;arhud;shout_out;low_speed_alert)");
        mContentProviderHelper.addParams("steering_wheelDrvEpsModeset", ParamsType.STRING, "", "", "sport", "方向盘助力模式(comfortable;sport)");
    }
}
