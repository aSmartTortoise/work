package com.voice.sdk.device.media;

import android.content.Context;

public interface AudioZoneInterface {
    void init(Context context);

    void destroy();
    int getZoneIdByDisplayId(int displayId);

    boolean isConnectBtByDisplayId(int displayId);

    boolean isConnectBtPassengerScreen();

    boolean isConnectBtCeilScreen();
}
