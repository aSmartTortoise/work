package com.voyah.ai.device.voyah.common.H37Car;

import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.basecar.carservice.CarPropUtils;

import mega.car.config.Dms;

/**
 * @Date 2024/7/30 19:06
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class DmsHelper {
    private static final String TAG = "DmsHelper";

    public static int getFatigueMonitor(int id) {
        int fatigueMonitorReq = CarPropUtils.getInstance().getIntProp(id);
        LogUtils.i(TAG, "isFatigueMonitorOpen id:" + id + " ,fatigueMonitorReq is " + fatigueMonitorReq);
        return fatigueMonitorReq;
    }

    public static void setFatigueMonitor(int id,int value) {
        CarPropUtils.getInstance().setIntProp(id, value);
    }
}
