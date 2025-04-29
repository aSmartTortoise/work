package com.voice.sdk.device.system;

import android.text.TextUtils;

import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.dc.context.DCContext;
import com.voice.sdk.device.carservice.signal.PositionSignal;
import com.voice.sdk.device.func.FuncConstants;
import com.voyah.ai.common.utils.GsonUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.voice.platform.agent.api.context.FlowChatContext;
import com.voyah.ds.common.entity.context.FlowContextKey;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractAppProcessor implements IAppProcessor {

    private static final String TAG = "AbstractAppProcessor";

    protected AppInterface appInterface = DeviceHolder.INS().getDevices().getSystem().getApp();

    private static final Map<String, Integer> LOCATION_MAP = new HashMap<>();

    static {
        LOCATION_MAP.put("first_row_left", 0);
        LOCATION_MAP.put("first_row_right", 1);
        LOCATION_MAP.put("second_row_left", 2);
        LOCATION_MAP.put("second_row_right", 3);
        LOCATION_MAP.put("third_row_left", 4);
        LOCATION_MAP.put("third_row_right", 5);
    }

    public Object getValueInContext(Map<String, Object> map, String key) {
        Map<String, Object> graphMap = (Map<String, Object>) map.get(DCContext.MAP_KEY_GRAPH_CONTEXT);
        if (graphMap != null && graphMap.containsKey(key)) {
            return graphMap.get(key);
        }
        Map<String, Object> graphMap2 = (Map<String, Object>) map.get(FlowChatContext.FlowChatResultKey.RESULT_MAP);
        if (graphMap2 != null && graphMap2.containsKey(key)) {
            return graphMap2.get(key);
        }
        return map.get(key);
    }

    public String getOneMapValue(String paramsKey, Map<String, Object> map) {
        Object object = getValueInContext(map, paramsKey);
        String value = "";
        if (object instanceof Map) {
            for (Map.Entry<Integer, Object> entry : ((Map<Integer, Object>) object).entrySet()) {
                value = (String) entry.getValue();
                break;
            }
        } else {
            value = (String) object;
        }
        return value;
    }

    public String getOriginScreen(Map<String, Object> map) {
        String screenName = getOneMapValue(FuncConstants.KEY_SCREEN_NAME, map);
        if (FuncConstants.VALUE_SCREEN.equals(screenName)) {
            screenName = "";
        }
        if (TextUtils.isEmpty(screenName)) {
            String position = getOneMapValue("awakenLocation", map);
            LogUtils.i(TAG, "position:" + position);
            LogUtils.i(TAG, "map:" + GsonUtils.toJson(map));
            if (!TextUtils.isEmpty(position)) {
                int location = translateLocation(position);
                screenName = getScreenFromSound(location);
            }
        }
        LogUtils.i(TAG, "screenName:" + screenName);
        return screenName;
    }


    public static int translateLocation(String awakenLocation) {
        LogUtils.i(TAG, "translateLocation awakenLocation is " + awakenLocation);
        int location = LOCATION_MAP.getOrDefault(awakenLocation, 0);
        return location;
    }

    public String getScreenFromSound(int location) {
        String screenName;
        switch (location) {
            case 0:
                screenName = FuncConstants.VALUE_SCREEN_CENTRAL;
                break;
            case 1:
                if (DeviceHolder.INS().getDevices().getSystem().getScreen().isSupportScreen(DeviceScreenType.PASSENGER_SCREEN)) {
                    screenName = FuncConstants.VALUE_SCREEN_PASSENGER;
                } else {
                    screenName = FuncConstants.VALUE_SCREEN_CENTRAL;
                }
                break;
            default:
                if (DeviceHolder.INS().getDevices().getSystem().getScreen().isSupportScreen(DeviceScreenType.CEIL_SCREEN)) {
                    screenName = FuncConstants.VALUE_SCREEN_CEIL;
                } else {
                    screenName = FuncConstants.VALUE_SCREEN_CENTRAL;
                }
                break;
        }
        return screenName;
    }

    public static int getAwakenLocation(Map<String, Object> flowContext) {
        String awakenLocation = getFlowContextKey(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_DEVICE_AWAKEN_LOCATION, flowContext);
        return translateLocation(awakenLocation);
    }

    public static String getUiSoundLocation(Map<String, Object> flowContext) {
        //是否为全时免唤醒
        boolean isAllTimeWakeUp = getBooleanFlowContextKey(FlowContextKey.FC_IS_NO_WAKEUP_TIME, flowContext);
        if (isAllTimeWakeUp) {
            return getFlowContextKey(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_SOUND_LOCATION, flowContext); //免唤醒取指令位置
        } else {
            return getFlowContextKey(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_DEVICE_AWAKEN_LOCATION, flowContext); //正常指令取前置唤醒位置
        }
    }

    public static String getFlowContextKey(String key, Map<String, Object> flowContext) {
        return flowContext.containsKey(key) ? (String) flowContext.get(key) : "";
    }

    public static boolean getBooleanFlowContextKey(String key, Map<String, Object> flowContext) {
        return flowContext.containsKey(key) ? (boolean) flowContext.get(key) : false;
    }

    public boolean isSecondRound(Map<String, Object> map) {
        String chooseType = getOneMapValue("choose_type", map);
        LogUtils.i(TAG, "isSecondRound chooseType is " + chooseType);
        return !StringUtils.isEmpty(chooseType);
    }

    public boolean isSecondConfirm(Map<String, Object> map) {
        String chooseType = getOneMapValue("choose_type", map);
        return "confirm".equals(chooseType);
    }

    public int getSoundSourcePos(HashMap<String, Object> map) {
        Object object = getValueInContext(map, DCContext.MAP_KEY_SOUND_LOCATION);
        if (object instanceof ArrayList) {
            return ((ArrayList<Integer>) object).get(0);
        }
        return PositionSignal.FIRST_ROW_LEFT;//兜底主驾
    }

    protected boolean isCarSupportScreen(String screen) {
        return DeviceHolder.INS().getDevices().getSystem().getScreen().isSupportScreen(DeviceScreenType.fromValue(screen));
    }

    protected boolean isAppSupportScreen(String pkgName, String screen) {
        return appInterface.isAppSupportScreen(pkgName, DeviceScreenType.fromValue(screen));
    }

    protected boolean isFront(String pkgName, String screen) {
        return appInterface.isAppForeGround(pkgName, DeviceScreenType.fromValue(screen));
    }

    protected boolean isCeilingOpen() {
        return DeviceHolder.INS().getDevices().getSystem().getScreen().isCeilScreenOpen();
    }

    protected boolean isPreemptiveApp(String pkgName) {
        return appInterface.isPreemptiveApp(pkgName);
    }

    //是否主驾声源
    protected boolean isFromFirstRowLeft(HashMap<String, Object> map) {
        Object object = getValueInContext(map, DCContext.MAP_KEY_SOUND_LOCATION);
        if (object instanceof ArrayList) {
            ArrayList<Integer> array = (ArrayList<Integer>) object;
            return 0 == array.get(0);
        }
        return false;
    }
}
