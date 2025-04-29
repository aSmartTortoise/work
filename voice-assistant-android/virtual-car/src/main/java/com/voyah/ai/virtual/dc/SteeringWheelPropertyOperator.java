package com.voyah.ai.virtual.dc;

import com.voice.sdk.device.carservice.constants.ISteeringWheel;
import com.voice.sdk.device.carservice.signal.SteeringWheelSignal;
import com.voyah.ai.common.utils.BiDirectionalMap;

/**
 * @Date 2024/8/6 14:17
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class SteeringWheelPropertyOperator extends BaseVirtualPropertyOperator {

    private final BiDirectionalMap<Integer, String> customMap = new BiDirectionalMap<>();
    private final BiDirectionalMap<Integer, String> epsMode = new BiDirectionalMap<>();

    public SteeringWheelPropertyOperator() {
        //方向盘按键自定义
        customMap.put(ISteeringWheel.CustomType.SOURCE_SWITCH, "source_switch");
        customMap.put(ISteeringWheel.CustomType.PARK_360, "360_park");
        customMap.put(ISteeringWheel.CustomType.TRIP_SHOOT, "trip_shoot");
        customMap.put(ISteeringWheel.CustomType.SELFIE, "selfie");
        customMap.put(ISteeringWheel.CustomType.SCREEN_SHIFT, "screen_shift");
        customMap.put(ISteeringWheel.CustomType.AR_HUD, "arhud");
        customMap.put(ISteeringWheel.CustomType.SHOUT_OUT, "shout_out");
        customMap.put(ISteeringWheel.CustomType.LOW_SPEED_ALERT, "low_speed_alert");

        epsMode.put(ISteeringWheel.EpsMode.COMFORT, "comfortable");
        epsMode.put(ISteeringWheel.EpsMode.SPORT, "sport");
    }

    @Override
    public int getBaseIntProp(String key, int area) {
        switch (key) {
            case SteeringWheelSignal.STEERING_WHEEL_CUSTOM_TYPE:
                String type = (String) getValue(key);
                return customMap.getReverse(type.toLowerCase());
            case SteeringWheelSignal.STEERING_WHEEL_DRV_EPS_MODESET:
                String mode = (String) getValue(key);
                return epsMode.getReverse(mode.toLowerCase());
            default:
                return super.getBaseIntProp(key, area);
        }
    }

    @Override
    public void setBaseIntProp(String key, int area, int value) {
        switch (key) {
            case SteeringWheelSignal.STEERING_WHEEL_CUSTOM_TYPE:
                String type = customMap.getForward(value);
                setValue(key, type);
                break;
            case SteeringWheelSignal.STEERING_WHEEL_DRV_EPS_MODESET:
                String mode = epsMode.getForward(value);
                setValue(key, mode);
                break;
            default:
                super.setBaseIntProp(key, area, value);
        }
    }
}
