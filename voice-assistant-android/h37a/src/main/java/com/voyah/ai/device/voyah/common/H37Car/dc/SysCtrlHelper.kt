package com.voyah.ai.device.voyah.common.H37Car.dc

import com.mega.ecu.MegaProperties
import com.mega.nexus.os.MegaSystemProperties
import com.voyah.ai.basecar.carservice.CarPropUtils

/**
 * @Date 2025/1/8 17:02
 * @Author 8327821
 * @Email *
 * @Description .
 **/
object SysCtrlHelper {

    /**
     * @see ISysCtrl.ICeilingLevel
     */
    fun getCeilingLevel(): Int {
        return CarPropUtils.getInstance().getIntProp(
            //Qnx.ID_CEILING_ROTATION_LOCATION
            return 0
        )
    }

    fun setCeilingLevel(level: Int) {
    }

    fun isCeilingOpen(): Boolean {
        val ceilingLocation = getCeilingLevel()
        //20250115 0/1/2/255/254 都代表关闭
        return if (ceilingLocation == 254 || ceilingLocation == 255) {
            false
        } else {
            ceilingLocation > 2
        }
    }

    /**
     * 吸顶屏配置字
     */
    fun getCeilingConfig(): Boolean {
        return MegaSystemProperties.getInt(MegaProperties.CONFIG_CEILINGSCREEN_EQUIPMENT, -1) != 0;
    }

    fun setCeilingConfig(value: Boolean) {
        //不支持写
    }
}