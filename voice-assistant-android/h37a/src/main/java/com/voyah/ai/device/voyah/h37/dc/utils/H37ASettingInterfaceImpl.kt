package com.voyah.ai.device.voyah.h37.dc.utils

import com.blankj.utilcode.util.Utils
import com.voice.sdk.device.DeviceHolder
import com.voice.sdk.device.carservice.dc.carsetting.SettingConstants
import com.voice.sdk.device.carservice.dc.carsetting.SettingInterface
import com.voyah.ai.basecar.utils.SettingUtils
import com.voyah.cockpit.appadapter.aidlimpl.IPageShowManagerImpl

/**
 * @Date 2025/3/24 14:15
 * @Author 8327821
 * @Email *
 * @Description 37A 设置部分未实现ACTION
 **/
object H37ASettingInterfaceImpl : SettingInterface {
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
            SettingConstants.SUPER_POWER -> {
                IPageShowManagerImpl.getInstance(Utils.getApp()).showEcoPlusMode()
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