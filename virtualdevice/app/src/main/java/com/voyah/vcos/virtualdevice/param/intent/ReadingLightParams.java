package com.voyah.vcos.virtualdevice.param.intent;

import com.example.filter_annotation.FlowChartDevices;
import com.voyah.vcos.virtualdevice.param.ParamsType;

/**
 * @Date 2024/8/14 14:07
 * @Author 8327821
 * @Email *
 * @Description .
 **/
@FlowChartDevices
public class ReadingLightParams extends BaseParams {
    @Override
    public void exe() {
        mContentProviderHelper.addParams("reading_Switch", ParamsType.INT, 1, 0 ,0 ,"主驾阅读灯开关[0,1]");
        mContentProviderHelper.addParams("reading_Switch_1", ParamsType.INT, 1, 0 ,0 ,"副驾阅读灯开关[0,1]");
        mContentProviderHelper.addParams("reading_Switch_2", ParamsType.INT, 1, 0 ,0 ,"左后阅读灯开关[0,1]");
        mContentProviderHelper.addParams("reading_Switch_3", ParamsType.INT, 1, 0 ,0 ,"右后阅读灯开关[0,1]");
    }
}
