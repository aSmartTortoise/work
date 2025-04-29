package com.voyah.ai.device.voyah.common.H37Car;

import com.voice.sdk.device.carservice.signal.PositionSignal;
import com.voyah.ai.basecar.carservice.CarPropUtils;

import org.json.JSONException;
import org.json.JSONObject;

import mega.car.Signal;
import mega.car.hardware.CarPropertyValue;

/**
 * @Date 2024/7/22 14:28
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class WindowHelper {

    /**
     * 返回true
     *
     * @param area
     * @return
     */
    public static boolean getChildLockStatus(int area) {
        CarPropertyValue childLockState = CarPropUtils.getInstance().getPropertyRaw(Signal.ID_CHILD_LOCK_STS);
        if (childLockState != null && childLockState.getValue() instanceof Integer[]) {
            Integer[] integers = (Integer[]) childLockState.getValue();
            if (integers.length >= 2) {
                if (area == 2) {
                    return integers[0] == Signal.ParamsDoorLockSts.DOR_LOC_STS_LOCK;
                } else if (area == 3) {
                    return integers[1] == Signal.ParamsDoorLockSts.DOR_LOC_STS_LOCK;
                }
            }
        }
        return false;
    }

    public static void setWindow(int area, int value) {
        try {
            JSONObject object = new JSONObject();
            object.put(getPositionPropValue(area), value);
            CarPropertyValue<String> json = new CarPropertyValue<>(Signal.ID_HMIR_WIMDOWPCTCMD, object.toString());
            CarPropUtils.getInstance().setRawProp(json);
        } catch (JSONException e) {
            //
        }
    }

    public static void setWindow(String value) {
        CarPropertyValue<String> json = new CarPropertyValue<>(Signal.ID_HMIR_WIMDOWPCTCMD, value);
        CarPropUtils.getInstance().setRawProp(json);
    }


    private static String getPositionPropValue(int key) {
        switch (key) {
            case PositionSignal.FIRST_ROW_LEFT:
                return "WndFLPostionCmd";
            case PositionSignal.FIRST_ROW_RIGHT:
                return "WndFRPostionCmd";
            case PositionSignal.SECOND_ROW_LEFT:
                return "WndRLPostionCmd";
            case PositionSignal.SECOND_ROW_RIGHT:
                return "WndRRPostionCmd";
        }
        return "WndFLPostionCmd";
    }
}
