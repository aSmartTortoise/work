package com.voyah.vcos.virtualdevice.param.intent;

import com.example.filter_annotation.FlowChartDevices;
import com.voyah.vcos.virtualdevice.param.ParamsType;

@FlowChartDevices
public class ScreenControlParams extends BaseParams {
    @Override
    public void exe() {
        mContentProviderHelper.addParams("screen_Pos", ParamsType.STRING, "", "", "mid_side", "屏幕位置(leftward;mid_side;rightward)");
        mContentProviderHelper.addParams("screen_MoveState", ParamsType.BOOLEAN, "", "", false, "屏幕移动状态,是否移动中");
        mContentProviderHelper.addParams("screen_AbnormalState", ParamsType.BOOLEAN, "", "", false, "中控屏发出阻停卡滞信号");
        mContentProviderHelper.addParams("screen_Brightness", ParamsType.INT, 100, 1, 1, "中控屏亮度(1-100, 1最低)");
        mContentProviderHelper.addParams("screen_InstrumentBrightness", ParamsType.INT, 100, 1, 1, "仪表屏亮度(1-100, 1最低)");
        mContentProviderHelper.addParams("screen_PowerOffReset", ParamsType.BOOLEAN, "", "", false, "下电复位开关(false,true)");
        mContentProviderHelper.addParams("screen_OnOff", ParamsType.INT, 1, 0, 0, "屏幕熄屏亮屏[0,1]");
    }
}
