package com.voice.sdk.device.system;

import android.content.Context;
import android.os.UserHandle;

import java.util.HashMap;

public interface SceneModeInterface {

    void setSceneMode(String action, int displayId, int position, int napTime);

    void startSceneModeService(String action);

}
