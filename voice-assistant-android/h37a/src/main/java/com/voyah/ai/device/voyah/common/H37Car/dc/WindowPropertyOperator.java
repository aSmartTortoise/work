package com.voyah.ai.device.voyah.common.H37Car.dc;

import com.example.filter_annotation.CarDevices;
import com.example.filter_annotation.CarType;
import com.voice.sdk.device.carservice.signal.WindowSignal;
import com.voyah.ai.device.voyah.common.H37Car.WindowHelper;
import com.voyah.ai.basecar.carservice.CarPropUtils;

import mega.car.Signal;
import mega.car.config.Windows;

/**
 * @Date 2024/7/22 14:30
 * @Author 8327821
 * @Email *
 * @Description .
 **/
@CarDevices(carType = CarType.H37_CAR)
public class WindowPropertyOperator extends Base37Operator {
    @Override
    void init() {
        map.put(WindowSignal.WINDOW_CHILD_LOCK, Signal.ID_CHILD_LOCK_STS);
        map.put(WindowSignal.WINDOW_WINDOW, Windows.ID_WINDOW);

    }

    @Override
    public int getBaseIntProp(String key, int area) {
        if (key.equals(WindowSignal.WINDOW_WINDOW)) {
            return CarPropUtils.getInstance().getIntProp(Windows.ID_WINDOW, area);
        } else {
            //通用逻辑
            int key_37 = getRealKey(key);
            int area_37 = getRealArea(area);
            if (key_37 != -1) {
                return CarPropUtils.getInstance().getIntProp(key_37, area_37);
            }
            return INVALID;
        }
    }

    @Override
    public void setBaseIntProp(String key, int area, int value) {
        if (key.equals(WindowSignal.WINDOW_WINDOW)) {
            WindowHelper.setWindow(area, value);
        } else {
            super.setBaseIntProp(key, area, value);
        }
    }


    @Override
    public boolean getBaseBooleanProp(String key, int area) {

        if (key.equals(WindowSignal.WINDOW_CHILD_LOCK)) {
            return WindowHelper.getChildLockStatus(area);
        }
        return getCommonBoolean(key, area);
    }

    @Override
    public void setBaseStringProp(String key, int area, String value) {
        if (key.equals(WindowSignal.WINDOW_WINDOW)) {
            WindowHelper.setWindow(value);
        } else {
            super.setBaseStringProp(key, area, value);
        }
    }
}
