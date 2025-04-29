package com.voyah.ai.virtual.dc;

import com.voice.sdk.device.carservice.constants.ISysSetting;
import com.voice.sdk.device.carservice.signal.SysSettingSignal;
import com.voyah.ai.common.utils.BiDirectionalMap;

/**
 * @Date 2024/8/5 10:10
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class SystemSettingPropertyOperator extends BaseVirtualPropertyOperator {

    private final BiDirectionalMap<Integer, String> imitateMap = new BiDirectionalMap<>();
    private final BiDirectionalMap<Integer, String> imitatePosMap = new BiDirectionalMap<>();
    private final BiDirectionalMap<Integer, String> broadcastMap = new BiDirectionalMap<>();//驾驶辅助播报
    private final BiDirectionalMap<Integer, String> streamMap = new BiDirectionalMap<>();
    private final BiDirectionalMap<String, String> timeMap = new BiDirectionalMap<>();

    public SystemSettingPropertyOperator() {
        imitateMap.put(ISysSetting.VolumeImitate.OFF, "off");
        imitateMap.put(ISysSetting.VolumeImitate.TRADITION, "tradition");
        imitateMap.put(ISysSetting.VolumeImitate.TECHNOLOGY, "technology");
        imitatePosMap.put(ISysSetting.VolumeImitatePos.IN_CAR, "incar");
        imitatePosMap.put(ISysSetting.VolumeImitatePos.OUT_CAR, "outcar");
        imitatePosMap.put(ISysSetting.VolumeImitatePos.ALL, "all");
        broadcastMap.put(ISysSetting.DrvAssistBroadcast.OFF, "off");
        broadcastMap.put(ISysSetting.DrvAssistBroadcast.BRIEF, "brief");
        broadcastMap.put(ISysSetting.DrvAssistBroadcast.DETAIL, "detail");
        streamMap.put(ISysSetting.IVolume.STREAM_VOICE_CALL, "phone");
        streamMap.put(ISysSetting.IVolume.STREAM_NVI, "navi");
        streamMap.put(ISysSetting.IVolume.STREAM_NOTIFICATION, "system_sound");
        streamMap.put(ISysSetting.IVolume.STREAM_BLUETOOTH, "bluetooth_headset");
        streamMap.put(ISysSetting.IVolume.STREAM_ASSISTANT, "voice");
        streamMap.put(ISysSetting.IVolume.STREAM_MUSIC, "media");
        timeMap.put("12", "12h");
        timeMap.put("24", "24h");
    }

    @Override
    public int getBaseIntProp(String key, int area) {
        switch (key) {
            case SysSettingSignal.SYS_VOLUME_IMITATE:
                String providerValue = (String) getValue(key);
                return imitateMap.getReverse(providerValue.toLowerCase());
            case SysSettingSignal.SYS_IMITATE_POS:
                String posValue = (String) getValue(key);
                return imitatePosMap.getReverse(posValue.toLowerCase());
            case SysSettingSignal.SYS_DRV_ASSIST_CAST:
                String castValue = (String) getValue(key);
                return broadcastMap.getReverse(castValue.toLowerCase());
            case SysSettingSignal.SYS_VOLUME_STREAM_TYPE:
                String streamValue = (String) getValue(key);
                return streamMap.getReverse(streamValue.toLowerCase());
            default:
                return super.getBaseIntProp(key, area);
        }
    }

    @Override
    public void setBaseIntProp(String key, int area, int value) {
        switch (key) {
            case SysSettingSignal.SYS_VOLUME_IMITATE:
                String str = imitateMap.getForward(value);
                setValue(key, str);
                break;
            case SysSettingSignal.SYS_IMITATE_POS:
                String pos = imitatePosMap.getForward(value);
                setValue(key, pos);
                break;
            case SysSettingSignal.SYS_DRV_ASSIST_CAST:
                String cast = broadcastMap.getForward(value);
                setValue(key, cast);
                break;
            case SysSettingSignal.SYS_VOLUME_STREAM_TYPE:
                String stream = streamMap.getForward(value);
                setValue(key, stream);
                break;
            default:
                super.setBaseIntProp(key, area, value);
        }
    }

    @Override
    public String getBaseStringProp(String key, int area) {
        if (SysSettingSignal.SYS_TIME_TYPE.equalsIgnoreCase(key)) {
            String time = (String) getValue(key);
            return timeMap.getReverse(time.toLowerCase());
        }
        return super.getBaseStringProp(key, area);
    }

    @Override
    public void setBaseStringProp(String key, int area, String value) {
        if (SysSettingSignal.SYS_TIME_TYPE.equalsIgnoreCase(key)) {
            String time = timeMap.getForward(value);
            setValue(key, time);
        } else {
            super.setBaseStringProp(key, area, value);
        }
    }
}
