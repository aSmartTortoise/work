package com.voice.sdk.buriedpoint;

import com.voice.sdk.buriedpoint.bean.BuriedPointData;
import com.voice.sdk.buriedpoint.bean.VadTime;

public interface IBuriedPointManager {
    void init(String path);

    BuriedPointData createBuriedPointBeanToRequestId(String requestId);

    void upLoading(boolean isConnect, String requestId, String mode, boolean isSend, String position);

    VadTime getVadTime(String requestId, String soundLocation);

    void saveVadTimeToRequestId(String requestId, String position);

    void saveVadStartTime(Long startTime, String position);

    void saveVadEndTime(Long startTime, String position);

    String getPackageName();
    boolean isConnected(boolean isCar);
    String getLocation(boolean isCar);



}
