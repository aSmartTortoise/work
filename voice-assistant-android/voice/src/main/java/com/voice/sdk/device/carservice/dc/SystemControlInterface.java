package com.voice.sdk.device.carservice.dc;

import java.util.HashMap;
import java.util.Map;

public interface SystemControlInterface {

    String getTtsText(String switch_mode);

    boolean isSupportMode(String switch_mode);

    boolean isCurVolumeFeatureMode(String switch_mode);

    void setVolumeFeatureMode(String switch_mode);

    String changeVolumeFeatureMode();

    Map<String, Integer> getVolumeFeatureModeN3N4();

    boolean isCurVolumeFocusMode(HashMap<String, Object> map);

    void setVolumeFocusMode(HashMap<String, Object> map);

    void changeVolumeFocusMode(HashMap<String, Object> map);

    boolean isSupportCurVolumeFocusMode(String mode);
}
