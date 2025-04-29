package com.voyah.ai.device.voyah

import com.voice.sdk.device.carservice.dc.AtmoInterface
import com.voice.sdk.device.carservice.dc.ReadingLightInterface
import com.voice.sdk.device.carservice.dc.RemoteInterface
import com.voice.sdk.device.carservice.dc.SeatInterface
import com.voice.sdk.device.carservice.dc.SystemControlInterface
import com.voice.sdk.device.carservice.dc.SystemSettingInterface
import com.voice.sdk.device.carservice.vcar.CarServiceInterface
import com.voice.sdk.device.carservice.vcar.IOperatorDispatcher
import com.voice.sdk.device.system.SceneModeInterface
import com.voyah.ai.basecar.system.SystemSettingImpl
import com.voyah.ai.basecar.utils.RemoteUtils
import com.voyah.ai.device.voyah.common.H37Car.SceneModeImpl
import com.voyah.ai.device.voyah.common.H37CarOperatorDispatcher
import com.voyah.ai.device.voyah.h37.dc.utils.AmbLightsImpl
import com.voyah.ai.device.voyah.h37.dc.utils.ReadingLightImpl
import com.voyah.ai.device.voyah.h37.dc.utils.SeatImpl
import com.voyah.ai.device.voyah.h37.dc.utils.SystemControlImpl

/**
 * @Date 2024/12/24 16:32
 * @Author 8327821
 * @Email *
 * @Description .
 **/
object CarServiceInterfaceImpl:
    CarServiceInterface {

    private val atmo = AmbLightsImpl().also {
        it.init()
    }

    private val readingLights by lazy {
        ReadingLightImpl()
    }

    override fun getOperatorDispatcher(): IOperatorDispatcher? {
        return H37CarOperatorDispatcher
    }

    override fun getAtmo(): AtmoInterface {
        return atmo
    }

    private val systemSetting by lazy {
        SystemSettingImpl()
    }

    override fun getSystemSetting(): SystemSettingInterface {
        return systemSetting
    }

    override fun getReadingLight(): ReadingLightInterface {
        return readingLights
    }

    private val seatImpl by lazy {
        SeatImpl()
    }
    override fun getSeatInterface(): SeatInterface {
        return seatImpl;
    }

    private val sceneModeImpl by lazy {
        SceneModeImpl()
    }

    private val systemControlImpl by lazy {
        SystemControlImpl();
    }

    override fun getSceneModeInterface(): SceneModeInterface {
        return sceneModeImpl
    }

    override fun getSystemControlInterface(): SystemControlInterface {
        return systemControlImpl;
    }

    private val remoteInterface by lazy {
        RemoteUtils()
    }

    override fun getRemoteInterface(): RemoteInterface {
        return RemoteUtils.getInstance()
    }
}