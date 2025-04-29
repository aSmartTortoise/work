package com.voice.sdk.asrInput;

public interface IOnAsrCallback {
    void onRecognizeResult(String asrText, boolean isFinal);

    void onRecognizeFinish();

    void onRecognizeClose(boolean success, boolean remote);

    void onRecognizeError(boolean isNetworkError);
}
