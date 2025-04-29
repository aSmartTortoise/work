package com.voice.sdk.device.carservice.dc;

import java.util.HashMap;
import java.util.HashSet;

public interface AdasInterface {

    void registerAVMStateCallback();

    boolean isNoaOrLccOpened();

    boolean isNeedDetermineLaneChangeSwitch();

    void setChangeLanes(HashMap<String, Object> map);

    void setActivateNOA();

    boolean isAccOrLccOpened();

    boolean isWorkshopDoesNotQualify(HashMap<String, Object> map);

    void setCurWorkshopTimeInterval(HashMap<String, Object> map);

    boolean isLessThanLowest(HashMap<String, Object> map);

    boolean isBeyondHighest(HashMap<String, Object> map);

    boolean isMaxEqualTo4();
}
