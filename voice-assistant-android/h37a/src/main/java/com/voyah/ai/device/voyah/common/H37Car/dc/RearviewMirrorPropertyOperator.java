package com.voyah.ai.device.voyah.common.H37Car.dc;

import com.blankj.utilcode.util.Utils;
import com.example.filter_annotation.CarDevices;
import com.example.filter_annotation.CarType;
import com.voice.sdk.device.carservice.signal.AirSignal;
import com.voice.sdk.device.carservice.signal.CarSettingSignal;
import com.voice.sdk.device.carservice.signal.PositionSignal;
import com.voice.sdk.device.carservice.signal.RearviewMirrorSignal;
import com.voice.sdk.device.carservice.vcar.IDeviceRegister;
import com.voice.sdk.util.LogUtils;
import com.voyah.ai.basecar.carservice.CarPropUtils;
import com.voyah.ai.basecar.carservice.CarServicePropUtils;
import com.voyah.ai.device.voyah.common.H37Car.RearViewHelper;
import com.voyah.ai.voice.platform.agent.api.flowchart.library.bean.FunctionalPositionBean;
import com.voyah.ai.voice.platform.agent.api.functionMatrix.FunctionDeviceMappingManager;
import com.voyah.cockpit.airconditioner.HvacServiceImpl;

import mega.car.Signal;
import mega.car.config.Climate;
import mega.car.config.ParamsCommon;

/**
 * @Date 2024/9/18 16:31
 * @Author 8327821
 * @Email *
 * @Description .
 **/
@CarDevices(carType = CarType.H37_CAR)
public class RearviewMirrorPropertyOperator extends Base37Operator implements IDeviceRegister {
    private static final String TAG = "RearviewMirrorPropertyOperator";
    private CarPropUtils carPropHelper;

    @Override
    void init() {
        carPropHelper = CarPropUtils.getInstance();
        HvacServiceImpl.getInstance(Utils.getApp()).startService(() ->
                LogUtils.d(TAG, "AC onServiceConnected() called"));
        //后除霜开关状态
        map.put(AirSignal.REAR_DEFROST_SWITCH_STATE, Climate.ID_REAR_DEFROST);
        map.put(RearviewMirrorSignal.AIR_REARVIEW_MIRROR_AUTO_HEATING, Signal.ID_REAR_AUTO_HEAT_ONOFF);
    }

    @Override
    public void setBaseIntProp(String key, int area, int value) {
        if (RearviewMirrorSignal.MIRROR_SHOW_DIALOG.equals(key)) {
            RearViewHelper.INSTANCE.showMirrorDialog(value);
        } else
            super.setBaseIntProp(key, area, value);
    }

    @Override
    public boolean getBaseBooleanProp(String key, int area) {
        switch (key) {
            case AirSignal.REAR_DEFROST_SWITCH_STATE:
                int curAirSwitchInt = carPropHelper.getIntProp(map.get(key),
                        getRealArea(area));
                return (curAirSwitchInt == ParamsCommon.OnOff.ON);
            case RearviewMirrorSignal.AIR_REARVIEW_MIRROR_AUTO_HEATING:
                LogUtils.i(TAG, "传下来的信号：" + RearviewMirrorSignal.AIR_REARVIEW_MIRROR_AUTO_HEATING + "  转换后的信号：" + map.get(key));
                int airRearviewMirrorAutoHeating = carPropHelper.getIntProp(map.get(key),
                        getRealArea(PositionSignal.AREA_NONE));
                return airRearviewMirrorAutoHeating == Signal.ParamsCommonOnOffInvalid.ONOFFSTS_ON;
        }
        if (RearviewMirrorSignal.MIRROR_HOT_SWITCH.equalsIgnoreCase(key)) {
            return RearViewHelper.INSTANCE.getMirrorDefrostSwitch();
        }
        return super.getBaseBooleanProp(key, area);
    }

    @Override
    public void setBaseBooleanProp(String key, int area, boolean value) {
        int realBoolean = -1;
        switch (key) {
            case AirSignal.REAR_DEFROST_SWITCH_STATE:
                //0 false,1 true
                realBoolean = value ? ParamsCommon.OnOffInvalid.ON : ParamsCommon.OnOffInvalid.OFF;
                LogUtils.d(TAG, "空调的调节：所有的参数，key" + map.get(key) + "value:" + value);
                carPropHelper.setIntProp(map.get(key),
                        getRealArea(area), realBoolean);

                break;
            case RearviewMirrorSignal.AIR_REARVIEW_MIRROR_AUTO_HEATING:
                realBoolean = value ? Signal.CommonOnOff.ON : Signal.CommonOnOff.OFF;
                carPropHelper.setIntProp(map.get(key),
                        getRealArea(PositionSignal.AREA_NONE), realBoolean);
                break;
            default:
                if (RearviewMirrorSignal.MIRROR_FOLD_STATE.equalsIgnoreCase(key)) {
                    RearViewHelper.INSTANCE.setRearViewFoldState(value);
                } else if (RearviewMirrorSignal.MIRROR_HOT_SWITCH.equals(key)) {
                    RearViewHelper.INSTANCE.setMirrorDefrostSwitch(value);
                } else {
                    super.setBaseBooleanProp(key, area, value);
                }
                break;
        }
    }

    @Override
    public void registerDevice() {
        // 37A 37B支持  56C和56D不支持 后视镜弹窗调节的展示
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(RearviewMirrorSignal.MIRROR_SUPPORT_ADJUST_CONFIG, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "1");
        // 37A 37B支持  56C和56D不支持 后视镜和展开&折叠后视镜
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(RearviewMirrorSignal.MIRROR_SUPPORT_SWITCH_CONFIG, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL, "1");

        String carType = CarServicePropUtils.getInstance().getCarType();
        //后视镜加热是否支持空调处理。
        String isSupportMirrorHeatingToAir;
        if (carType.equals("H37A")) {
            isSupportMirrorHeatingToAir = "-1";
        } else {
            isSupportMirrorHeatingToAir = "1";
        }
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(RearviewMirrorSignal.REARVIEW_MIRROR_HEATING_TO_AIR, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL,
                isSupportMirrorHeatingToAir);
        //自动后视镜加热功能位
        String airRearviewMirrorAutoHeating = "1";
        if (carType.equals("H37A")) {
            airRearviewMirrorAutoHeating = "-1";
        } else {
            airRearviewMirrorAutoHeating = "1";
        }
        FunctionDeviceMappingManager.getInstance().registerFunctionPosition(RearviewMirrorSignal.AIR_REARVIEW_MIRROR_AUTO_HEATING, FunctionalPositionBean.FUNCTIONAL_POSITION_TYPE_FUNCTIONAL,
                airRearviewMirrorAutoHeating);
    }
}
