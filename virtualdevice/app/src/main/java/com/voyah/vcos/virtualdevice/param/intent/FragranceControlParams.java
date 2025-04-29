package com.voyah.vcos.virtualdevice.param.intent;


import com.example.filter_annotation.FlowChartDevices;
import com.voyah.vcos.virtualdevice.param.ParamsType;

/**
 * 模式的值跟nlu的结果保持一致。
 *
 * 广播 这样发送
 * adb shell am broadcast -a com.voyah.ai.test.virtualDevice --es query "virtualDeviceSetInstruction:AirSwitchState=true:AirTemp_0=25.0f" -f 0x01000000
 */
@FlowChartDevices
public class FragranceControlParams extends BaseParams{
    @Override
    public void exe() {
        mContentProviderHelper.addParams("FragranceSwitchState", ParamsType.BOOLEAN,"","",true,"香氛开关");
        mContentProviderHelper.addParams("FragranceMode", ParamsType.STRING,"","","海静","香氛模式");
        mContentProviderHelper.addParams("FragranceSlotType", ParamsType.STRING,"","","[海静,木影,竹境]","香氛槽位信息");
        mContentProviderHelper.addParams("FragranceLevel", ParamsType.STRING,"","","low","香氛等级（low、mid、high）");
        mContentProviderHelper.addParams("FragranceTime", ParamsType.INT,60000,1800,1800,"香氛时长（1800，3600，无限）");
    }
}
