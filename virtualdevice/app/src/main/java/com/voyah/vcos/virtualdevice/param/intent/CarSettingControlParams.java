package com.voyah.vcos.virtualdevice.param.intent;

import com.example.filter_annotation.FlowChartDevices;
import com.voyah.vcos.virtualdevice.param.ParamsType;

/**
 * @Date 2024/8/5 15:00
 * @Author 8327821
 * @Email *
 * @Description .
 **/
@FlowChartDevices
public class CarSettingControlParams extends BaseParams {

    @Override
    public void exe() {
        mContentProviderHelper.addParams("car_SafeSensitiveLevel", ParamsType.INT, 3, 1, 1, "1低2中3高");
        mContentProviderHelper.addParams("car_WipeActionState", ParamsType.INT, 1, 0, 0, "雨刮工作状态(0关闭1打开)");
        mContentProviderHelper.addParams("car_WipeAutoSwitch", ParamsType.BOOLEAN, "", "", false, "自动雨刷开关");
        mContentProviderHelper.addParams("car_WipeRepairState", ParamsType.INT, 1, 0, 0, "雨刷维修模式开关");
        mContentProviderHelper.addParams("car_AutoWipeSensitive", ParamsType.INT, 4, 1, 1, "自动雨刷灵敏度等级，1最低4最高    ");
        mContentProviderHelper.addParams("car_DriveMode", ParamsType.STRING, "", "", "economy", "驾驶模式(comfortable;economy;high_energy;picnic;snowfield;super_power_saving;custom)");
        mContentProviderHelper.addParams("car_PowerMode", ParamsType.STRING, "", "", "economy", "动力模式(economy;standard;sport)");
        mContentProviderHelper.addParams("car_EleParking", ParamsType.INT, 1, 0, 0, "电子驻车开关(0关闭1打开)");
        mContentProviderHelper.addParams("car_SafeESP", ParamsType.BOOLEAN, "", "", false, "车身稳定系统(0关闭1打开)");
        mContentProviderHelper.addParams("car_ComfortParking", ParamsType.BOOLEAN, "", "", false, "舒适停车(0关闭1打开)");
        mContentProviderHelper.addParams("car_HillDescent", ParamsType.BOOLEAN, "", "", false, "陡坡缓降(0关闭1打开)");
        mContentProviderHelper.addParams("car_AutoParking", ParamsType.STRING, "", "", "deep_step_activation", "自动驻车(deep_step_activation;auto_activation)");
        mContentProviderHelper.addParams("car_EnergyRecovery", ParamsType.INT, 3, 0, 1, "能量回收等级(0关闭1低2中3高)");
        mContentProviderHelper.addParams("car_DoorLock", ParamsType.INT, 1, 0, 0, "车门锁中控锁(0关闭1打开)");
        mContentProviderHelper.addParams("car_ChildLock", ParamsType.INT, 1, 0, 0, "儿童安全锁(0解锁1上锁)");
        mContentProviderHelper.addParams("car_UnlockMode", ParamsType.STRING, "", "", "total_car", "车辆解锁模式(total_car;first_row_left)");
        mContentProviderHelper.addParams("car_WalkAwayLock", ParamsType.INT, 1, 0, 0, "送宾闭锁(0关闭1打开)");
        mContentProviderHelper.addParams("car_ParkingUnlock", ParamsType.INT, 1, 0, 0, "P挡解锁、驻车解锁(0关闭1打开)");
        mContentProviderHelper.addParams("car_LockUnlockVoice", ParamsType.INT, 1, 0, 0, "解闭锁喇叭提示音(0关闭1打开)");
        mContentProviderHelper.addParams("car_LockCloseWindow", ParamsType.INT, 1, 0, 0, "锁车自动升窗(0关闭1打开)");
        mContentProviderHelper.addParams("car_RmmAutoFold", ParamsType.INT, 1, 0, 0, "锁车后视镜自动折叠(0关闭1打开)");
        mContentProviderHelper.addParams("car_RmAutoDown", ParamsType.INT, 1, 0, 0, "倒车后视镜自动下翻(0关闭1打开)");

        mContentProviderHelper.addParams("car_RearPosLamp", ParamsType.INT, 1, 0, 0, "后位置灯随日行灯开启(0关闭1打开)");
        mContentProviderHelper.addParams("car_HighBeamAssist", ParamsType.INT, 1, 0, 0, "智能远光辅助(0关闭1打开)");
        mContentProviderHelper.addParams("car_InteriorLightAutoDome", ParamsType.INT, 1, 0, 0, "顶灯自动点亮(0关闭1打开)");
        mContentProviderHelper.addParams("car_ChargeLightEffect", ParamsType.INT, 1, 0, 0, "充电动态灯效(0关闭1打开)");
        mContentProviderHelper.addParams("car_FollowHome", ParamsType.STRING, "", "", "off", "伴我回家(off;15;30;60)");
        mContentProviderHelper.addParams("car_WelcomeLights", ParamsType.STRING, "", "", "off", "迎宾灯效(off;joyful;comfortable;free)");
    }
}
