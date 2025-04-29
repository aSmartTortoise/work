package com.voyah.ai.device.carservice

import com.voice.sdk.device.carservice.dc.AtmoInterface
import com.voice.sdk.device.carservice.dc.ReadingLightInterface
import com.voice.sdk.device.carservice.dc.RemoteInterface
import com.voice.sdk.device.carservice.dc.SeatInterface
import com.voice.sdk.device.carservice.dc.SystemControlInterface
import com.voice.sdk.device.carservice.dc.SystemSettingInterface
import com.voice.sdk.device.carservice.vcar.CarServiceInterface
import com.voice.sdk.device.carservice.vcar.IOperatorDispatcher
import com.voice.sdk.device.system.SceneModeInterface
import com.voyah.ai.common.utils.LogUtils

/**
 * @Date 2024/12/24 16:32
 * @Author 8327821
 * @Email *
 * @Description .
 **/
object CarServiceInterfaceImpl: CarServiceInterface {
    override fun getOperatorDispatcher(): IOperatorDispatcher {
        return VirtualCarOperatorDispatcher
    }

    override fun getAtmo(): AtmoInterface? {
        println("CarServiceInterface.getAtmo: not yet implemented")
        return null
    }

    override fun getSystemSetting(): SystemSettingInterface {
        return object : SystemSettingInterface {

            //FIXME zhangwei 影响虚拟车环境
            override fun getInt(type: String?, key: String?, defaultState: Int): Int {
                return 3 // 车机环境写死测试环境
            }

            override fun putInt(type: String?, key: String?, value: Int) {
                println("SystemSettingInterface.putInt: not yet implemented")
            }

            override fun getString(type: String?, key: String?): String? {
                println("SystemSettingInterface.getString: not yet implemented")
                return ""
            }

            override fun openUpLogPage() {
                println("SystemSettingInterface.openUpLogPage: not yet implemented")
            }
        }
    }

    override fun getReadingLight(): ReadingLightInterface? {
        println("CarServiceInterface.getAtmo: not yet implemented")
        return null
    }

    override fun getSeatInterface(): SeatInterface? {
        println("CarServiceInterface.getSeatInterface: not yet implemented")
        return null
    }

    override fun getRemoteInterface(): RemoteInterface? {
        println("CarServiceInterface.getRemoteInterface: not yet implemented")
        return null
    }

    override fun getSceneModeInterface(): SceneModeInterface {
        println("CarServiceInterface.getSceneModeInterface: not yet implemented")
        return object : SceneModeInterface {
            override fun setSceneMode(p0: String?, p1: Int, p2: Int, p3: Int) {
                println("CarServiceInterface.setSceneMode: not yet implemented")
            }

            override fun startSceneModeService(p0: String?) {
                println("CarServiceInterface.startSceneModeService: not yet implemented")
            }
        }
    }

    override fun getSystemControlInterface(): SystemControlInterface? {
        println("CarServiceInterface.getSystemControlInterface: not yet implemented")
        return null
    }


}