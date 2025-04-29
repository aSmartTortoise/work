package com.voyah.ai.basecar.manager;

import com.voyah.ai.sdk.listener.SimpleVAResultListener;

public abstract class ExtVAResultListener extends SimpleVAResultListener {

    public abstract void onAsr(boolean isOnline, String text);

    public abstract void onTts(String text);
}
