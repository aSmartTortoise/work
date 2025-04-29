package com.voyah.ai.device.voyah.common.H56Car;


import com.voice.sdk.device.carservice.signal.PositionSignal;
import com.voyah.ai.basecar.carservice.CarPropUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.vshare.utils.Logger;

import org.json.JSONException;
import org.json.JSONObject;


import mega.car.config.EntryLocks;
import mega.car.config.H56D;
import mega.car.hardware.CarPropertyValue;

/**
 * @Date 2024/7/22 14:28
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class WindowHelper {

    private static final String TAG = "WindowHelper";
    /**
     * 返回true
     * @param area
     * @return
     */
    public static boolean getChildLockStatus(int area) {
        CarPropertyValue childLockState = CarPropUtils.getInstance().getPropertyRaw(EntryLocks.ID_LOCKS_CHILD_SEC_DOOR_LOCK);
        if (childLockState != null && childLockState.getValue() instanceof Integer[]) {
            Integer[] integers = (Integer[]) childLockState.getValue();
            if (integers.length >= 2) {
                if (area == 2) {
                    return integers[0] == 0;
                } else if (area == 3) {
                    return integers[1] == 1;
                }
            }
        }
        return false;
    }

    public static void setWindow(int area, int value) {
        try {
            JSONObject object = new JSONObject();
            object.put(getPositionPropValue(area), value);
            //1696452610   Signal.ID_HMIR_WIMDOWPCTCMD   H56D.IVI_BodySet4_IVI_FRWINDOWHORIZONTALREQ
            CarPropertyValue<String> jsons ;
            switch (area) {
                case PositionSignal.FIRST_ROW_LEFT:
                    jsons = new CarPropertyValue<>(H56D.IVI_BodySet4_IVI_FLWINDOWHORIZONTALREQ, object.toString());//默认是主驾
                    break;
                case PositionSignal.FIRST_ROW_RIGHT:
                    jsons = new CarPropertyValue<>(H56D.IVI_BodySet4_IVI_FRWINDOWHORIZONTALREQ, object.toString());//副驾
                    break;
                case PositionSignal.SECOND_ROW_LEFT:
                case PositionSignal.THIRD_ROW_LEFT:
                    jsons = new CarPropertyValue<>(H56D.IVI_BodySet4_IVI_RLWINDOWHORIZONTALREQ, object.toString());//二排左
                    break;
                case PositionSignal.SECOND_ROW_RIGHT:
                case PositionSignal.THIRD_ROW_RIGHT:
                    jsons = new CarPropertyValue<>(H56D.IVI_BodySet4_IVI_RRWINDOWHORIZONTALREQ, object.toString());//二排右
                    break;
                default:
                    jsons = new CarPropertyValue<>(H56D.IVI_BodySet4_IVI_FLWINDOWHORIZONTALREQ, object.toString());//默认是主驾
                    break;
            }


            Logger.d("--------------"+H56D.IVI_BodySet4_IVI_FLWINDOWHORIZONTALREQ,"==================");
            CarPropUtils.getInstance().setRawProp(jsons);
        } catch (JSONException e) {
            //
        }
    }

    public static void setWindow(int area,String value) {
        CarPropertyValue<String> jsons ;

        switch (area+"") {
            case "0":
//                jsons = new CarPropertyValue<>(H56D.IVI_BodySet4_IVI_FLWINDOWHORIZONTALREQ, value);//默认是主驾
                CarPropUtils.getInstance().setIntProp(H56D.IVI_BodySet4_IVI_FLWINDOWHORIZONTALREQ,Integer.parseInt(value));
                break;
            case "1":
//                jsons = new CarPropertyValue<>(H56D.IVI_BodySet4_IVI_FRWINDOWHORIZONTALREQ, value);//副驾
                CarPropUtils.getInstance().setIntProp(H56D.IVI_BodySet4_IVI_FRWINDOWHORIZONTALREQ,Integer.parseInt(value));
                break;
            case "4":
            case "2":
//                jsons = new CarPropertyValue<>(H56D.IVI_BodySet4_IVI_RLWINDOWHORIZONTALREQ, value);//二排左
                CarPropUtils.getInstance().setIntProp(H56D.IVI_BodySet4_IVI_RLWINDOWHORIZONTALREQ,Integer.parseInt(value));
                LogUtils.i(TAG, "setWindow :area======" + area);
                break;
            case "5":
            case "3":
//                jsons = new CarPropertyValue<>(H56D.IVI_BodySet4_IVI_RRWINDOWHORIZONTALREQ, value);//二排右
                CarPropUtils.getInstance().setIntProp(H56D.IVI_BodySet4_IVI_RRWINDOWHORIZONTALREQ,Integer.parseInt(value));
                LogUtils.i(TAG, "setWindow :area======" + area);
                break;
            default:
//                jsons = new CarPropertyValue<>(H56D.IVI_BodySet4_IVI_FLWINDOWHORIZONTALREQ, value);//默认是主驾
                CarPropUtils.getInstance().setIntProp(H56D.IVI_BodySet4_IVI_FLWINDOWHORIZONTALREQ,Integer.parseInt(value));
                break;
        }


//        CarPropertyValue<String> json = new CarPropertyValue<>(H56D.IVI_BodySet4_IVI_FLWINDOWHORIZONTALREQ, value);
//        CarPropUtils.getInstance().setRawProp(jsons);
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
