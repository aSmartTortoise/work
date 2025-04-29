package com.voyah.ai.logic.dc;

import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.constants.ICommon;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.carservice.signal.OmsSignal;
import com.voice.sdk.util.LogUtils;
import com.voyah.ai.logic.dc.dvr.Constant;
import com.voyah.ai.voice.platform.agent.api.context.FlowChatContext;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;


/**
 * @author:lcy OMS
 * @data:2024/4/8
 **/
public class OmsControlImp extends AbsDevices {
    private static final String TAG = OmsControlImp.class.getSimpleName();

    public OmsControlImp() {
        super();
    }

    @Override
    public String getDomain() {
        return "oms";
    }


    /**
     * 隐私保护是否开启
     *
     * @return
     */
    public boolean isPrivacyProtectionOpen(HashMap<String, Object> map) {
        int privacySecurityStatus = operator.getIntProp(CommonSignal.COMMON_PRIVACY_PROTECTION);
        return privacySecurityStatus == 1;
    }

    /**
     * 后排乘员遗忘提醒是否已关闭
     */
    public boolean isLifeForGreMindClosed(HashMap<String, Object> map) {
        int lifeForGreMindStatus = operator.getIntProp(OmsSignal.OMS_FORGET_REMIND);
        LogUtils.i(TAG, "isLifeForGreMindClosed lifeForGreMindStatus is " + lifeForGreMindStatus);
        return lifeForGreMindStatus == ICommon.Switch.OFF;
    }

    /**
     * 打开\关闭后排乘员遗忘提醒
     */
    public void switchLifeForGreMind(HashMap<String, Object> map) {
        String switch_type = "";
        int adjustTo = -1;
        if (map.containsKey("switch_type"))
            switch_type = (String) getValueInContext(map, "switch_type");
        switch (switch_type) {
            case "open":
                adjustTo = ICommon.Switch.ON;
                break;
            case "close":
                adjustTo = ICommon.Switch.OFF;
                break;
        }
        LogUtils.i(TAG, "switchLifeForGreMind switch_type is " + switch_type + " ,adjustTo is " + adjustTo);
        operator.setIntProp(OmsSignal.OMS_FORGET_REMIND, adjustTo);
    }

    /**
     * 车内监控实时画面是否已关闭
     */
    public boolean isRearMonitorClosed(HashMap<String, Object> map) {
        return DeviceHolder.INS().getDevices().getCamera().isFloatCameraStart() && (DeviceHolder.INS().getDevices().getCamera().getBackMirrorType() == 1);
    }

    public boolean isCrwOpen(HashMap<String, Object> map) {
        return DeviceHolder.INS().getDevices().getLauncher().isRcwOpen();
    }

    public boolean isOnlyGearsRForbidden(HashMap<String, Object> map) {
        return DeviceHolder.INS().getDevices().getVoiceCarSignal().isParkingLimitation() && isH56DCar(map);
    }

    /**
     * 打开\关闭车内监控实时画面
     */
    public void switchRearMonitor(HashMap<String, Object> map) {
        String switch_type = "";
        if (map.containsKey("switch_type"))
            switch_type = (String) getValueInContext(map, "switch_type");
        if (StringUtils.equals(switch_type, "open"))
            DeviceHolder.INS().getDevices().getCamera().startFloatCamera();
        else if (StringUtils.equals(switch_type, "close"))
            DeviceHolder.INS().getDevices().getCamera().stopFloatCamera();
    }

    public boolean isFrontRow(HashMap<String, Object> map) {
        int soundLocation = (int) getValueInContext(map, FlowChatContext.DSKey.SOUND_LOCATION);
        LogUtils.d(TAG, "isFrontRow soundLocation:" + soundLocation);
        return 0 == soundLocation || 1 == soundLocation;
    }

    public boolean isParkOpen(HashMap<String, Object> map) {
        return operator.getIntProp(CommonSignal.COMMON_360_STATE) == ICommon.Switch.ON;
    }

    public boolean isNotSupportGreMindAndRearMonitor(HashMap<String, Object> map) {
        String carType = DeviceHolder.INS().getDevices().getCarServiceProp().getCarType();
        boolean isSupport = operator.getBooleanProp(OmsSignal.OMS_CONFIG);
        return !isSupport && carType.contains("H56");
    }


    @Override
    public String replacePlaceHolderInner(HashMap<String, Object> map, String str) {
        return str;
    }

}
