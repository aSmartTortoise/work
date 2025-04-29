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
 * @Description 37B 设置部分未实现ACTION
 **/
object H37BSettingInterfaceImpl : SettingInterface {
    override fun exec(action: String) {
        when (action) {
            SettingConstants.BLUETOOTH -> {
                SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_BLUETOOTH)
            }

            SettingConstants.HOTSPOT_PAGE -> {
                SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_HOTSPOT)
            }

            SettingConstants.WLAN_PAGE -> {
                SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_WLAN)
            }

            SettingConstants.DEVICE_SETTINGS -> {
                SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_DEVICE)
            }

            SettingConstants.TRAFFIC_QUERY -> {
                SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_NETWORK)
            }

            SettingConstants.WALLPAPER -> {
                SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_WALLPAPER)
            }

            SettingConstants.VERSION_INFO -> {
                SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_SYSTEMVERSIONINFO)
            }

            SettingConstants.SYS_UPGRADE -> {
                SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_SYSTEMSYSUPDATE)
            }

            SettingConstants.VOICE_PAGE -> {
                SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_VOICE)
            }

            SettingConstants.SKILL_DESCRIPTION_CENTER -> {
                SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_VPASKILL)
            }

            SettingConstants.LIGHT_PAGE -> {//灯光
                SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_CARLIGHT)
            }

            SettingConstants.DRIVE_PREFERENCE_PAGE -> {//驾驶偏好
                SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_DRIVINGPREFERENCE)
            }

            SettingConstants.ENERGY_CENTER_PAGE -> {//能量中心
                SettingUtils.getInstance().exec(SettingConstants.NEW_ENERGY_CENTER)
            }

            SettingConstants.DOOR_WINDOW_PAGE -> {//门窗
                SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_WINDOWDOOR)
            }

            SettingConstants.DRIVE_ASSIST_PAGE -> {//驾驶辅助
                SettingUtils.getInstance().exec(SettingConstants.NEW_DRIVE_ASSIST)
            }

            SettingConstants.NIGHTTIME_SILENT -> { // 夜间静音弹窗
                SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_NIGHTMUTETIME)
            }

            SettingConstants.SOUND_EFFECTPAGE -> { // 音效页面
                SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_SOUNDEFFECT)
            }

            SettingConstants.SOUND_VOLUME -> { // 音量页面
                SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_VOLUME)
            }

            SettingConstants.SOUND_PAGE -> { // 声音页面
                SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_SOUND)
            }

            SettingConstants.VEHICLE_LOCK -> { // 车锁页面
                SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_CARLOCK)
            }

            SettingConstants.SMART_GESTURE -> { // 智能手势页面
                SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_SMARTGESTURE)
            }

            SettingConstants.DISPLAY_PAGE -> { // 显示
                SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_DISPLAY)
            }

            SettingConstants.DISCHARGE_PAGE -> {//放电设置
                SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_DISCHARGE)
            }

            SettingConstants.CHARGE_PAGE -> {//充电设置
                SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_CHARGING)
            }

            SettingConstants.DEVICE_NAME -> {//设备名称
                SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_EDITDEVICENAME)
            }

            SettingConstants.FACTORY_RESET -> {//恢复出厂
                SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_RESETFACTORY)
            }

            SettingConstants.PRIVACY_CLAUSE -> {//隐私条款
                SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_PRIVACY)
            }

            SettingConstants.WAKE_UP_OFF -> {//语音唤醒
                SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_CLOSEVOICECONFIRMDIALOG)
            }

            SettingConstants.ENERGY_CONSUMPTION_CURVE -> {//能耗曲线
                SettingUtils.getInstance().exec(SettingConstants.NEW_ENERGYCONSUMPTION)
            }

            SettingConstants.ENERGY_STATISTICS -> {//行程统计
                SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_STATISTICS)
            }

            SettingConstants.SUPER_POWER -> {//超级省电
                SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_SUPERPOWER)
            }

            SettingConstants.CUSTOM_STEER_WHEEL_KEY -> {
                SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_CUSTOMSTEERWHEELKEY)
            }

            SettingConstants.VEHICLE_LIGHT_PAGE -> {//近光灯高度
                SettingUtils.getInstance().exec(SettingConstants.NEW_LIGHT_CAR_LIGHT)
            }

            SettingConstants.VEHICLE_PAGE -> {//车辆
                SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_VEHICLE)
            }

            SettingConstants.LINK_PAGE -> {//连接
                SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_CONNECTION)
            }

            SettingConstants.SYSTEM_PAGE -> {//系统
                SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_SYSTEM)
            }

            SettingConstants.INSTRUMENT_SCREEN_PAGE -> {//仪表卡片设置
                SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_DASHBOARDSCREEN)
            }

            SettingConstants.SMART_MONITORING_PAGE -> {//智能监测
                SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_SMARTMONITOR)
            }

            SettingConstants.CENTRAL_CONTROL_SCREEN_PAGE -> {//中控
                SettingUtils.getInstance().exec(SettingConstants.NEW_ACTION_CENTRALSCREEN)
            }

            SettingConstants.LABORATORY_PAGE -> {//实验室
                SettingUtils.getInstance().exec(SettingConstants.NEW_LABORATORY_PAGE)
            }

            else -> {
                SettingUtils.getInstance().exec(action)
            }
        }
    }

    override fun isCurrentState(action: String): Boolean {
        when (action) {
            SettingConstants.BLUETOOTH -> {
                return SettingUtils.getInstance().isCurrentState(SettingConstants.NEW_ACTION_BLUETOOTH)
            }

            SettingConstants.HOTSPOT_PAGE -> {
                return SettingUtils.getInstance().isCurrentState(SettingConstants.NEW_ACTION_HOTSPOT)
            }

            SettingConstants.WLAN_PAGE -> {
                return SettingUtils.getInstance().isCurrentState(SettingConstants.NEW_ACTION_WLAN)
            }

            SettingConstants.DEVICE_SETTINGS -> {
                return SettingUtils.getInstance().isCurrentState(SettingConstants.NEW_ACTION_DEVICE)
            }

            SettingConstants.TRAFFIC_QUERY -> {
                return SettingUtils.getInstance().isCurrentState(SettingConstants.NEW_ACTION_NETWORK)
            }

            SettingConstants.WALLPAPER -> {
                return SettingUtils.getInstance().isCurrentState(SettingConstants.NEW_ACTION_WALLPAPER)
            }

            SettingConstants.VERSION_INFO -> {
                return SettingUtils.getInstance().isCurrentState(SettingConstants.NEW_ACTION_SYSTEMVERSIONINFO)
            }

            SettingConstants.SYS_UPGRADE -> {
                return SettingUtils.getInstance().isCurrentState(SettingConstants.NEW_ACTION_SYSTEMSYSUPDATE)
            }

            SettingConstants.VOICE_PAGE -> {
                return SettingUtils.getInstance().isCurrentState(SettingConstants.NEW_ACTION_VOICE)
            }

            SettingConstants.SKILL_DESCRIPTION_CENTER -> {
                return SettingUtils.getInstance().isCurrentState(SettingConstants.NEW_ACTION_VPASKILL)
            }

            SettingConstants.LIGHT_PAGE -> {//灯光设置
                return SettingUtils.getInstance().isCurrentState(SettingConstants.NEW_ACTION_CARLIGHT)
            }

            SettingConstants.DRIVE_PREFERENCE_PAGE -> {//驾驶偏好
                return SettingUtils.getInstance().isCurrentState(SettingConstants.NEW_ACTION_DRIVINGPREFERENCE)
            }

            SettingConstants.ENERGY_CENTER_PAGE -> {//能量中心
                return SettingUtils.getInstance().isCurrentState(SettingConstants.NEW_ENERGY_CENTER)
            }

            SettingConstants.DOOR_WINDOW_PAGE -> {//门窗
                return SettingUtils.getInstance().isCurrentState(SettingConstants.NEW_ACTION_WINDOWDOOR)
            }

            SettingConstants.DRIVE_ASSIST_PAGE -> {//驾驶辅助
                return SettingUtils.getInstance().isCurrentState(SettingConstants.NEW_DRIVE_ASSIST)
            }

            SettingConstants.NIGHTTIME_SILENT -> { // 夜间静音弹窗
                return SettingUtils.getInstance().isCurrentState(SettingConstants.NEW_ACTION_NIGHTMUTETIME)
            }

            SettingConstants.SOUND_EFFECTPAGE -> { // 音效页面
                return SettingUtils.getInstance().isCurrentState(SettingConstants.NEW_ACTION_SOUNDEFFECT)
            }

            SettingConstants.SOUND_VOLUME -> { // 音量页面
                return SettingUtils.getInstance().isCurrentState(SettingConstants.NEW_ACTION_VOLUME)
            }

            SettingConstants.SOUND_PAGE -> { // 声音页面
                return SettingUtils.getInstance().isCurrentState(SettingConstants.NEW_ACTION_SOUND)
            }

            SettingConstants.VEHICLE_LOCK -> { // 车锁页面
                return SettingUtils.getInstance().isCurrentState(SettingConstants.NEW_ACTION_CARLOCK)
            }

            SettingConstants.SMART_GESTURE -> { // 智能手势页面
                return SettingUtils.getInstance().isCurrentState(SettingConstants.NEW_ACTION_SMARTGESTURE)
            }

            SettingConstants.DISPLAY_PAGE -> {
                return SettingUtils.getInstance().isCurrentState(SettingConstants.NEW_ACTION_DISPLAY)
            }

            SettingConstants.DISCHARGE_PAGE -> {//放电设置
                return SettingUtils.getInstance().isCurrentState(SettingConstants.NEW_ACTION_DISCHARGE)
            }

            SettingConstants.CHARGE_PAGE -> {//充电设置
                return SettingUtils.getInstance().isCurrentState(SettingConstants.NEW_ACTION_CHARGING)
            }

            SettingConstants.DEVICE_NAME -> {//设备名称
                return SettingUtils.getInstance().isCurrentState(SettingConstants.NEW_ACTION_EDITDEVICENAME)
            }

            SettingConstants.FACTORY_RESET -> {//恢复出厂
                return SettingUtils.getInstance().isCurrentState(SettingConstants.NEW_ACTION_RESETFACTORY)
            }

            SettingConstants.PRIVACY_CLAUSE -> {//隐私条款
                return SettingUtils.getInstance().isCurrentState(SettingConstants.NEW_ACTION_PRIVACY)
            }

            SettingConstants.ENERGY_CONSUMPTION_CURVE -> {//能耗曲线
                return SettingUtils.getInstance().isCurrentState(SettingConstants.NEW_ENERGYCONSUMPTION)
            }

            SettingConstants.ENERGY_STATISTICS -> {//行程统计
                return SettingUtils.getInstance().isCurrentState(SettingConstants.NEW_ACTION_STATISTICS)
            }

            SettingConstants.SUPER_POWER -> {//超级省电
                return SettingUtils.getInstance().isCurrentState(SettingConstants.NEW_ACTION_SUPERPOWER)
            }

            SettingConstants.VEHICLE_PAGE -> {//车辆
                return SettingUtils.getInstance().isCurrentState(SettingConstants.NEW_ACTION_VEHICLE)
            }

            SettingConstants.VEHICLE_LIGHT_PAGE -> {//近光灯高度
                return SettingUtils.getInstance().isCurrentState(SettingConstants.NEW_LIGHT_CAR_LIGHT)
            }

            SettingConstants.LINK_PAGE -> {//连接
                return SettingUtils.getInstance().isCurrentState(SettingConstants.NEW_ACTION_CONNECTION)
            }

            SettingConstants.SYSTEM_PAGE -> {//系统
                return SettingUtils.getInstance().isCurrentState(SettingConstants.NEW_ACTION_SYSTEM)
            }

            SettingConstants.INSTRUMENT_SCREEN_PAGE -> {//仪表卡片设置
                return SettingUtils.getInstance().isCurrentState(SettingConstants.NEW_ACTION_DASHBOARDSCREEN)
            }

            SettingConstants.SMART_MONITORING_PAGE -> {//智能监测
                return SettingUtils.getInstance().isCurrentState(SettingConstants.NEW_ACTION_SMARTMONITOR)
            }

            SettingConstants.CENTRAL_CONTROL_SCREEN_PAGE -> {//中控
                return SettingUtils.getInstance().isCurrentState(SettingConstants.NEW_ACTION_CENTRALSCREEN)
            }

            SettingConstants.LABORATORY_PAGE -> {//实验室
                return SettingUtils.getInstance().isCurrentState(SettingConstants.NEW_LABORATORY_PAGE)
            }
        }
        return SettingUtils.getInstance().isCurrentState(action)
    }

    override fun getCurrentState(action: String): String {
        return SettingUtils.getInstance().getCurrentState(action)
    }
}