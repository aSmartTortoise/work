package com.voyah.vcos.virtualdevice.param.intent;

import com.example.filter_annotation.FlowChartDevices;
import com.voyah.vcos.virtualdevice.param.ParamsType;

@FlowChartDevices
public class AtmosphereControlParams extends BaseParams {

    @Override
    public void exe() {
        mContentProviderHelper.addParams("AtmosphereStatus", ParamsType.BOOLEAN, "", "", false, "氛围灯开关");
        mContentProviderHelper.addParams("AtmosphereMode", ParamsType.INT, 4, 0, 0, "氛围灯模式");
        mContentProviderHelper.addParams("AtmosphereSetMode", ParamsType.INT, 4, 0, 0, "氛围灯将要设置的模式");
        mContentProviderHelper.addParams("AtmosphereType", ParamsType.INT, 6, 1, 1, "氛围灯类型");
        mContentProviderHelper.addParams("AtmosphereSetType", ParamsType.INT, 6, 1, 1, "氛围灯将要设置的类型");
        mContentProviderHelper.addParams("AtmosphereColor", ParamsType.INT, 128, 9, 9, "氛围灯颜色");
        mContentProviderHelper.addParams("AtmosphereSetType", ParamsType.INT, 128, 9, 9, "氛围灯将要设置的颜色");
        mContentProviderHelper.addParams("AtmosphereColorScheme", ParamsType.INT, 3, 1, 1, "氛围灯色系");
        mContentProviderHelper.addParams("AtmosphereTheme", ParamsType.INT, 9, 0, 0, "氛围灯主题");
        mContentProviderHelper.addParams("AtmosphereSetTheme", ParamsType.INT, 9, 0, 0, "氛围灯将要设置的主题");
    }
}
