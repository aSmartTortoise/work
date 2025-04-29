package com.voyah.ai.basecar.media;

import androidx.annotation.IntDef;

@IntDef(value = {ConnectStatus.CONNECTED, ConnectStatus.SUSPENDED, ConnectStatus.FAILED})
public @interface ConnectStatus {
    int CONNECTED = 0;
    int SUSPENDED = 1;
    int FAILED = 2;
}
