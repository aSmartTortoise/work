package com.voyah.ai.device.voyah.common.H37Car.dc;

import com.example.filter_annotation.CarDevices;
import com.example.filter_annotation.CarType;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.signal.DmsSignal;
import com.voyah.ai.device.voyah.common.H37Car.DmsHelper;


import org.apache.commons.lang3.StringUtils;

import mega.car.SignalH53B;
import mega.car.config.Dms;

/**
 * @Date 2024/7/30 17:39
 * @Author 8327821
 * @Email *
 * @Description .
 **/
@CarDevices(carType = CarType.H37_CAR)
public class DmsPropertyOperator extends Base37Operator {
    private String carType;

    @Override
    void init() {
        carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();
        if (StringUtils.equals(carType, "H37B")) {
            map.put(DmsSignal.DMS_FATIGUE_MONITOR, SignalH53B.ID_DMS_FATIGUE_DETECT_SWITCH); //0:关 1:标准 2:灵敏
            map.put(DmsSignal.DMS_DISTRACT_MONITOR, SignalH53B.ID_DMS_DISTRACTION_SWITCH);
        } else {
            map.put(DmsSignal.DMS_FATIGUE_MONITOR, Dms.ID_FATIGUE_MONITOR_SET); //1:关 2:标准 3:灵敏
            map.put(DmsSignal.DMS_DISTRACT_MONITOR, Dms.ID_DISTRACT_MONITOR_ONOFF);
        }
    }

    @Override
    public int getBaseIntProp(String key, int area) {
        switch (key) {
            case DmsSignal.DMS_FATIGUE_MONITOR:
                int currentFatigueMonitor = DmsHelper.getFatigueMonitor(map.get(DmsSignal.DMS_FATIGUE_MONITOR));
                if (StringUtils.equals(carType, "H37B"))
                    currentFatigueMonitor = currentFatigueMonitor + 1;
                return currentFatigueMonitor;
            default:
                return super.getBaseIntProp(key, area);
        }
    }

    @Override
    public void setBaseIntProp(String key, int area, int value) {
        switch (key) {
            case DmsSignal.DMS_FATIGUE_MONITOR:
                if (StringUtils.equals(carType, "H37B"))
                    DmsHelper.setFatigueMonitor(map.get(key), value - 1);
                else
                    DmsHelper.setFatigueMonitor(map.get(key), value);
                break;
            default:
                super.setBaseIntProp(key, area, value);
        }

    }


}
