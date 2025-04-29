package com.voyah.vcos.virtualdevice.param.intent;


import com.voyah.vcos.virtualdevice.param.ContentProviderHelper;
import com.voyah.vcos.virtualdevice.param.ParamsType;

public abstract class BaseParams {
    protected ContentProviderHelper mContentProviderHelper;
    public BaseParams(){
        mContentProviderHelper = ContentProviderHelper.getInstance();
        mContentProviderHelper.addParams("TopPage", ParamsType.BOOLEAN,"","",false,"当前界面");
        mContentProviderHelper.addParams("common_SpeedInfo", ParamsType.FLOAT, 300f, 0f, 0f, "车速");
        mContentProviderHelper.addParams("common_GearInfo", ParamsType.STRING, "", "", "P", "车辆档位：P-R-N-D-S");
        mContentProviderHelper.addParams("common_PrivacyProtection", ParamsType.BOOLEAN, "", "", false, "隐私保护开关");
        mContentProviderHelper.addParams("common_InfoHiding", ParamsType.INT, 1, 0, 0, "信息隐藏开关");
        mContentProviderHelper.addParams("common_SplitSwitch", ParamsType.INT, 1, 0, 0, "分屏开关");
    }
    public abstract void exe();
}
