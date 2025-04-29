package com.voyah.h37z;

import androidx.annotation.NonNull;

public interface TTSPlayCallback {
    void onTTSPlayBegin(@NonNull String ttsId);

    void onTTSPlayEnd(String ttsId);
}
