package com.voyah.ai.virtual.dc;

import com.voice.sdk.device.carservice.signal.OtherSignal;
import com.voyah.ai.common.utils.BiDirectionalMap;

/**
 * @Date 2024/8/20 15:24
 * @Author 8327821
 * @Email *
 * @Description .
 **/
public class OtherPropertyOperator extends BaseVirtualPropertyOperator {
    public final BiDirectionalMap<Integer, String> musicMap = new BiDirectionalMap<>();
    public final BiDirectionalMap<Integer, String> videoMap = new BiDirectionalMap<>();

    public OtherPropertyOperator() {
        musicMap.put(0, "auto");
        musicMap.put(1, "qq音乐");
        musicMap.put(2, "网易云音乐");
        videoMap.put(0, "auto");
        videoMap.put(1, "腾讯视频");
        videoMap.put(2, "爱奇艺");
    }

    @Override
    public int getBaseIntProp(String key, int area) {
        if (OtherSignal.OTHER_MUSIC_PREFERENCE.equalsIgnoreCase(key)) {
            String state = (String) getValue(key);
            return musicMap.getReverse(state.toLowerCase());
        } else if (OtherSignal.OTHER_VIDEO_PREFERENCE.equalsIgnoreCase(key)) {
            String state = (String) getValue(key);
            return videoMap.getReverse(state.toLowerCase());
        }
        return super.getBaseIntProp(key, area);
    }

    @Override
    public void setBaseIntProp(String key, int area, int value) {
        if (OtherSignal.OTHER_MUSIC_PREFERENCE.equalsIgnoreCase(key)) {
            String state = musicMap.getForward(value);
            setValue(key, state);
        } else if (OtherSignal.OTHER_VIDEO_PREFERENCE.equalsIgnoreCase(key)) {
            String state = videoMap.getForward(value);
            setValue(key, state);
        } else {
            super.setBaseIntProp(key, area, value);
        }

    }
}
