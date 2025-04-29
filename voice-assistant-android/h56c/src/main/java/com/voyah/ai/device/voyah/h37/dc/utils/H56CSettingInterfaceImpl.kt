package com.voyah.ai.device.voyah.h37.dc.utils

import com.blankj.utilcode.util.Utils
import com.voice.sdk.device.DeviceHolder
import com.voice.sdk.device.carservice.dc.carsetting.SettingConstants
import com.voice.sdk.device.carservice.dc.carsetting.SettingInterface
import com.voyah.ai.basecar.utils.SettingUtils
import com.voyah.ai.common.utils.LogUtils
import com.voyah.cockpit.appadapter.aidlimpl.IPageShowManagerImpl

/**
 * @Date 2025/3/24 14:15
 * @Author 8327821
 * @Email *
 * @Description 37A 设置部分未实现ACTION
 **/
object H56CSettingInterfaceImpl : SettingInterface {
    override fun exec(action: String) {
        when (action) {
            SettingConstants.SETTING_PAGE -> {
                if (DeviceHolder.INS().devices.system.splitScreen.isNeedSplitScreen()) {
                    DeviceHolder.INS().devices.system.splitScreen.enterSplitScreen("com.voyah.cockpit.vehiclesettings",
                        "com.voyah.cockpit.vehiclesettings.activity.MainActivity")
                } else {
                    IPageShowManagerImpl.getInstance(Utils.getApp()).openSettingApp()
                }
            }
            SettingConstants.NEW_ACTION_ELE_PARKING -> {
                IPageShowManagerImpl.getInstance(Utils.getApp()).showDriveAssist()
            }
            SettingConstants.CARSETTING_MODEPOWER -> {//强制纯电
                IPageShowManagerImpl.getInstance(Utils.getApp()).showForcePureElectricConfirm();
            }

            SettingConstants.SUSPENSION_LOADADJUST -> {//便捷载物
                IPageShowManagerImpl.getInstance(Utils.getApp()).showSuspensionEasyLoadConfirm();
            }

            SettingConstants.SUSPENSION_BOARDINGCAR -> {//便捷上下车弹窗
                IPageShowManagerImpl.getInstance(Utils.getApp()).showSuspensionEasyBoardConfirm();
            }


            SettingConstants.DRIVER_POWERMODE -> {//动力模式
                IPageShowManagerImpl.getInstance(Utils.getApp()).showPowerModeSetting();
            }

            else -> {
                SettingUtils.getInstance().exec(action)
            }
        }
    }

    override fun isCurrentState(action: String): Boolean {
        return SettingUtils.getInstance().isCurrentState(action)
    }

    override fun getCurrentState(action: String): String {
        return SettingUtils.getInstance().getCurrentState(action)
    }
}