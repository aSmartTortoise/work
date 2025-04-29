package com.voyah.ai.logic.dc;

import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.dc.RemoteInterface;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.util.LogUtils;

import java.util.HashMap;

public class RemoteControlControlImpl extends AbsDevices {

    private static final String TAG = "RemoteControlControlImpl";
    private final RemoteInterface remoteInterface;

    public RemoteControlControlImpl() {
        super();
        remoteInterface = DeviceHolder.INS().getDevices().getCarService().getRemoteInterface();
        if (remoteInterface != null) {
            remoteInterface.init();
            remoteInterface.register();
        }
    }

    @Override
    public String getDomain() {
        return "RemoteControl";
    }

    @Override
    public String replacePlaceHolderInner(HashMap<String, Object> map, String str) {
        int index = 0;

        int start = str.indexOf("@");
        if (start == -1) {
            return str;
        }
        int end = str.indexOf("}");
        String key = str.substring(start + 2, end);
        switch (key) {
            case "app_name":
                str = str.replace("@{app_name}", "遥控器应用");
                break;
            default:
                LogUtils.e(TAG, "当前要处理的@{" + key + "}不存在");
                return str;
        }
        LogUtils.i(TAG, "tts :" + start + " " + end + " " + key + " " + index);

        LogUtils.i(TAG, "tts :" + str);
        return str;
    }

    public boolean isSupportRemoteApp(HashMap<String, Object> map) {
        return operator.getBooleanProp(CommonSignal.COMMON_HAS_REMOTE_APP);
    }

    public boolean isHasCeilingScreen(HashMap<String, Object> map) {
        boolean hasCeilingScreen = operator.getBooleanProp(CommonSignal.COMMON_CEIL_CONFIG);
        LogUtils.i(TAG, "isHasCeilingScreen hasCeilingScreen :" + hasCeilingScreen);
        return hasCeilingScreen;
    }

    public boolean isCeilingScreenOpend(HashMap<String, Object> map) {
        return operator.getBooleanProp(CommonSignal.COMMON_CEIL_OPEN);
    }

    public boolean isShowMenuPage(HashMap<String, Object> map) {
        return map.containsKey("page_name");
    }

    public boolean isRemoteControlAppOpend(HashMap<String, Object> map) {
        int isRemoteControlAppOpend = remoteInterface.getRemoteControlAppState();
        return isRemoteControlAppOpend == 1;
    }

    public void setRemoteControlAppClose(HashMap<String, Object> map) {
        remoteInterface.setRemoteControlAppClose();
    }

    public boolean isRemoteControlConnected(HashMap<String, Object> map) {
        int isRemoteControlConnected = remoteInterface.getRemoteControlConnectState();
        return isRemoteControlConnected == 1;
    }

    public void setRemoteControlDisConnect(HashMap<String, Object> map) {
        remoteInterface.setRemoteControlDisConnect();
    }

    public boolean isRemoteControlMenuPageOpend(HashMap<String, Object> map) {
        int isShowRemoteControlMenuUI = remoteInterface.getRemoteControlMenuPageState();
        return isShowRemoteControlMenuUI == 1;
    }
}
