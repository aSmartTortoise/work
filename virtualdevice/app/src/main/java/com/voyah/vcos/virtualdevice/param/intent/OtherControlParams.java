package com.voyah.vcos.virtualdevice.param.intent;

import com.example.filter_annotation.FlowChartDevices;
import com.voyah.vcos.virtualdevice.param.ParamsType;

/**
 * @Date 2024/8/6 13:40
 * @Author 8327821
 * @Email *
 * @Description .
 **/
@FlowChartDevices
public class OtherControlParams extends BaseParams {
    @Override
    public void exe() {
        mContentProviderHelper.addParams("other_StaticGestureSwitch", ParamsType.INT,1,0,0,"静态手势开关(0,1)");
        mContentProviderHelper.addParams("other_DynamicGestureSwitch", ParamsType.INT,1,0,0,"动态手势开关(0,1)");
        mContentProviderHelper.addParams("other_WakeupSwitch", ParamsType.BOOLEAN,"","",true,"语音唤醒开关[true,false]");
        mContentProviderHelper.addParams("other_ContinuousDialogue", ParamsType.BOOLEAN,"","",true,"连续对话开关[true,false]");
        mContentProviderHelper.addParams("other_MultiZoneDialogue", ParamsType.BOOLEAN,"","",true,"多音区对话开关[true,false]");
        mContentProviderHelper.addParams("other_VoiceArea", ParamsType.INT,15,1,1,"可收音区[主驾1；副驾2；左后4；右后8]，多位置做加法计算");
        mContentProviderHelper.addParams("other_MusicPreference", ParamsType.STRING,"","","qq音乐","音乐媒体偏好[auto,qq音乐,网易云音乐]");
        mContentProviderHelper.addParams("other_VideoPreference", ParamsType.STRING,"","","爱奇艺","视频媒体偏好[auto,爱奇艺,腾讯视频]");
    }
}
