package com.voyah.vcos.virtualdevice.param.intent;

import com.example.filter_annotation.FlowChartDevices;
import com.voyah.vcos.virtualdevice.param.ParamsType;

@FlowChartDevices
public class SystemSettingControlParams extends BaseParams{
    @Override
    public void exe() {
        mContentProviderHelper.addParams("sys_BlueToothSwitch", ParamsType.BOOLEAN,"","",false,"蓝牙开关");
        mContentProviderHelper.addParams("sys_HotSpotSwitch", ParamsType.BOOLEAN,"","",false,"热点开关");
        mContentProviderHelper.addParams("sys_WifiSwitch", ParamsType.BOOLEAN,"","",false,"wifi开关");
        mContentProviderHelper.addParams("sys_WirelessCharge", ParamsType.INT,3,0,false,"无限充电状态：(0->未激活;1->关闭;2->打开;3->无效)");
        mContentProviderHelper.addParams("sys_VolumeFollowSpeed", ParamsType.BOOLEAN,"","",false,"音量随速开关");
        mContentProviderHelper.addParams("sys_AcousticVehAlert", ParamsType.INT,1,0,0,"低速行人警示音开关(0->关闭;1->打开)");
        mContentProviderHelper.addParams("sys_VolumeImitate", ParamsType.STRING,"","","off","模拟声浪(off;tradition;technology)");
        mContentProviderHelper.addParams("sys_ImitatePos", ParamsType.STRING,"","","all","模拟声浪发声位置(incar;outcar;all)");
        mContentProviderHelper.addParams("sys_MediaOutplaySwitch", ParamsType.INT,1,0,0,"媒体外放开关(0->关闭;1->打开)");
        mContentProviderHelper.addParams("sys_MediaOutplayConfirm", ParamsType.INT,1,0,0,"媒体外放二次确认开关(0->关闭;1->打开)");
        mContentProviderHelper.addParams("sys_DrvAssistBroadcast", ParamsType.STRING,"","","off","驾驶辅助播报(off;brief;detail)");
        mContentProviderHelper.addParams("sys_TimeType", ParamsType.STRING,"","","12","时间格式(12;24)");
        mContentProviderHelper.addParams("sys_Language", ParamsType.STRING,"","","中文","语言(中文;英文)");
        mContentProviderHelper.addParams("sys_VolumeStreamType", ParamsType.STRING,"","","media","当前要调节的音频通道焦点(中文;英文)");
        mContentProviderHelper.addParams("sys_VolumeNavi", ParamsType.INT,30,0,15,"导航音量[0,30]");
        mContentProviderHelper.addParams("sys_VolumeSystem", ParamsType.INT,30,0,15,"系统音量[0,30]");
        mContentProviderHelper.addParams("sys_VolumeBt", ParamsType.INT,30,0,15,"蓝牙音量[0,30]");
        mContentProviderHelper.addParams("sys_VolumeAssistant", ParamsType.INT,30,0,15,"语音助手音量[0,30]");
        mContentProviderHelper.addParams("sys_VolumeMedia", ParamsType.INT,30,0,15,"媒体音量[0,30]");
        mContentProviderHelper.addParams("sys_VolumePhone", ParamsType.INT,30,1,15,"通话音量[1,30]");
        //静音
        mContentProviderHelper.addParams("sys_MutePhone", ParamsType.BOOLEAN,"","",false,"通话是否静音[true,false]");
        mContentProviderHelper.addParams("sys_MuteNavi", ParamsType.BOOLEAN,"","",false,"导航是否静音[true,false]");
        mContentProviderHelper.addParams("sys_MuteSystem", ParamsType.BOOLEAN,"","",false,"系统是否静音[true,false]");
        mContentProviderHelper.addParams("sys_MuteBt", ParamsType.BOOLEAN,"","",false,"蓝牙是否静音[true,false]");
        mContentProviderHelper.addParams("sys_MuteAssistant", ParamsType.BOOLEAN,"","",false,"语音助手是否静音[true,false]");
        mContentProviderHelper.addParams("sys_MuteMedia", ParamsType.BOOLEAN,"","",false,"媒体是否静音[true,false]");

        mContentProviderHelper.addParams("sys_KeyTone", ParamsType.INT, 1, 0, 1, "按键音开关[0,1]");

    }
}
