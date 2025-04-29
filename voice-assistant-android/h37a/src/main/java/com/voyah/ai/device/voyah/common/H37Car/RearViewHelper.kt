package com.voyah.ai.device.voyah.common.H37Car

import android.provider.Settings
import com.blankj.utilcode.util.Utils
import com.google.gson.Gson
import com.voice.sdk.device.carservice.dc.carsetting.SettingConstants
import com.voyah.ai.common.utils.LogUtils
import com.voyah.ai.basecar.carservice.CarPropUtils
import com.voyah.ai.device.voyah.h37.dc.bean.RearviewMirrorBean
import mega.car.VehicleArea
import mega.car.config.Cabin
import mega.car.config.Climate
import mega.car.config.ParamsCommon

/**
 * @Date 2024/9/18 16:40
 * @Author 8327821
 * @Email *
 * @Description .
 **/
object RearViewHelper {

    private const val TAG = "RearViewHelper"

    /**
     * 后视镜折叠，true折叠，false展开
     */
    fun setRearViewFoldState(fold: Boolean) {
        CarPropUtils.getInstance().setIntProp(
            Cabin.ID_REAR_VIEW_MIRROR_FOLD_CONTROL,
            VehicleArea.NONE,
            if (fold) ParamsCommon.OnOff.ON else ParamsCommon.OnOff.OFF
        )
    }

    /**========================后视镜加热 START==========================**/

    fun getMirrorDefrostSwitch(): Boolean {
        val curValue = CarPropUtils.getInstance().getIntProp(Climate.ID_REAR_DEFROST);
        LogUtils.i(TAG, "getRearviewMirrorHot : curValue-$curValue")
        return curValue == 1
    }

    fun setMirrorDefrostSwitch(switch: Boolean) {
        CarPropUtils.getInstance().setIntProp(
            Climate.ID_REAR_DEFROST,
            if (switch) ParamsCommon.OnOffInvalid.ON else ParamsCommon.OnOffInvalid.OFF
        )
    }

    fun showMirrorDialog(value: Int) {
        //0=选择左侧后视镜 1=选择右侧后视镜
        val rearviewMirrorBean = RearviewMirrorBean("com.voyah.ai.voice")
        rearviewMirrorBean.setPosition(value)
        val gson = Gson()
        Settings.System.putString(Utils.getApp().getContentResolver(), SettingConstants.REAR_MIRROR_ADJUSTMENT, gson.toJson(rearviewMirrorBean))
    }
}