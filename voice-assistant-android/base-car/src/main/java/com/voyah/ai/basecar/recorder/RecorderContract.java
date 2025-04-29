package com.voyah.ai.basecar.recorder;

/**
 * Created by lcy on 2023/12/18.
 */

public interface RecorderContract {
    public interface OnRecorderListener{
        void onRecordStarted(long sessionId);
        void onRecordStopped(long sessionId);
        void onException(DhError dhError);
        void onRawDataReceived(long sessionId, final byte[] buffer, final int size);
        void onDestroy();
    }
}
