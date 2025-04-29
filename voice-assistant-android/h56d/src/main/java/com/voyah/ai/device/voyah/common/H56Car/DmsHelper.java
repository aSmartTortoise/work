package com.voyah.ai.device.voyah.common.H56Car;

import com.voyah.ai.basecar.carservice.CarPropUtils;
import com.voyah.ai.common.utils.LogUtils;

import mega.car.config.Dms;

/**
 * @Date 2024/7/30 19:06
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class DmsHelper {
    private static final String TAG = "DmsHelper";

    public static int getFatigueMonitor() {
        int fatigueMonitorReq = CarPropUtils.getInstance().getIntProp(Dms.ID_FATIGUE_DETECT_SWITCH);
        LogUtils.i(TAG, "isFatigueMonitorOpen fatigueMonitorReq is " + fatigueMonitorReq);
        return fatigueMonitorReq;
    }
}
