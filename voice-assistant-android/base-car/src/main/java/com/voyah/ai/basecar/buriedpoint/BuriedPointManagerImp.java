package com.voyah.ai.basecar.buriedpoint;

import com.blankj.utilcode.util.Utils;
import com.voice.sdk.VoiceEnvPlugin;
import com.voice.sdk.buriedpoint.IBuriedPointManager;
import com.voice.sdk.buriedpoint.bean.BuriedPointData;
import com.voice.sdk.buriedpoint.bean.VadTime;
import com.voyah.ai.common.utils.NetWorkUtils;
import com.voyah.ds.common.tool.GsonUtils;

public class BuriedPointManagerImp implements IBuriedPointManager {
    @Override
    public void init(String path) {
        BuriedPointManager.getInstance().init(Utils.getApp());
    }

    @Override
    public BuriedPointData createBuriedPointBeanToRequestId(String requestId) {
        return BuriedPointManager.getInstance().createBuriedPointBeanToRequestId(requestId);
    }

    @Override
    public void upLoading(boolean isConnect, String requestId, String mode, boolean isSend, String position) {
        BuriedPointManager.getInstance().upLoading(isConnect, requestId, mode, isSend, position);
    }

    @Override
    public VadTime getVadTime(String requestId, String soundLocation) {
        return BuriedPointManager.getInstance().getVadTime(requestId, soundLocation);
    }

    @Override
    public void saveVadTimeToRequestId(String requestId, String position) {
        BuriedPointManager.getInstance().saveVadTimeToRequestId(requestId, position);
    }

    @Override
    public void saveVadStartTime(Long startTime, String position) {
        BuriedPointManager.getInstance().saveVadStartTime(startTime, position);
    }

    @Override
    public void saveVadEndTime(Long startTime, String position) {
        BuriedPointManager.getInstance().saveVadEndTime(startTime, position);
    }


    @Override
    public String getPackageName() {
        return Utils.getApp().getPackageName();
    }

    public boolean isConnected(boolean isCar){
        return NetWorkUtils.isConnected(Utils.getApp(),isCar);
    }

    public String getLocation(boolean isCar){
        VoiceEnvPlugin envPlugin = new VoiceEnvPlugin();
        return GsonUtils.toJson(envPlugin.getGpsLocation());
    }




}
