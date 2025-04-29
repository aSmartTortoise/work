package com.voyah.ai.device.voyah.common.H56Car.dc;

import com.example.filter_annotation.CarDevices;
import com.example.filter_annotation.CarType;
import com.voice.sdk.device.carservice.signal.DmsSignal;
import com.voyah.ai.device.voyah.common.H56Car.DmsHelper;

import mega.car.config.Dms;

/**
 * @Date 2024/7/30 17:39
 * @Author 8327821
 * @Email *
 * @Description .
 **/
@CarDevices(carType = CarType.H37_CAR)
public class DmsPropertyOperator extends Base56Operator {
    @Override
    void init() {
        map.put(DmsSignal.DMS_FATIGUE_MONITOR, Dms.ID_FATIGUE_DETECT_SWITCH);
        map.put(DmsSignal.DMS_DISTRACT_MONITOR, Dms.ID_DISTRACT_MONITOR_ONOFF);
    }

    @Override
    public int getBaseIntProp(String key, int area) {
        switch (key) {
            case DmsSignal.DMS_FATIGUE_MONITOR:
                return DmsHelper.getFatigueMonitor() + 1;
            default:
                return super.getBaseIntProp(key, area);
        }
    }

    @Override
    public void setBaseIntProp(String key, int area, int value) {
        switch (key) {
            case DmsSignal.DMS_FATIGUE_MONITOR:
                setCommonInt(key, area, value - 1);
                break;
            default:
                setCommonInt(key, area, value);
        }
    }


}
