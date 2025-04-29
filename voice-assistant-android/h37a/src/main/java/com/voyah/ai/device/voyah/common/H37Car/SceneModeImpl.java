package com.voyah.ai.device.voyah.common.H37Car;

import android.content.Intent;

import com.blankj.utilcode.util.Utils;
import com.voice.sdk.device.system.SceneModeInterface;
import com.voyah.ai.common.utils.LogUtils;

public class SceneModeImpl implements SceneModeInterface {
    private static final String TAG = SceneModeImpl.class.getSimpleName();

    @Override
    public void setSceneMode(String action, int displayId, int napSeat, int napTime) {
        LogUtils.d(TAG, "action: " + action + "displayId: " + displayId + "napSeat: " + napSeat + "napTime:" + napTime);
        Intent intent = new Intent();
        intent.setClassName("com.voyah.cockpit.scenemode", "com.voyah.cockpit.scenemode.service.ScenceModeService");
        intent.setAction(action);
        intent.putExtra("displayId", displayId);
        if (napSeat != -1) {
            intent.putExtra("nap_seat", napSeat);
        }
        if (displayId != -1) {
            intent.putExtra("displayId", displayId);
        }
        if (napTime != -1) {
            intent.putExtra("nap_time", napTime);
        }
        Utils.getApp().startService(intent);
    }

    @Override
    public void startSceneModeService(String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        intent.setClassName("com.voyah.cockpit.scenemode", "com.voyah.cockpit.scenemode.OccupyActivity");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Utils.getApp().startActivity(intent);
    }
}
