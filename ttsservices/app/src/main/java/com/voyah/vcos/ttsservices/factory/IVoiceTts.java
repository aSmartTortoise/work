package com.voyah.vcos.ttsservices.factory;

import android.content.Context;

import com.voyah.vcos.ttsservices.info.PlayTTSBean;
import com.voyah.vcos.ttsservices.manager.VoiceTtsListener;

/**
 * @author:lcy
 * @data:2024/1/30
 **/
public interface IVoiceTts {

    void init(Context context, int usage);

    void playTts(PlayTTSBean voiceTtsBean);

    void playStreamTts(PlayTTSBean voiceTtsBean);

    boolean isSyncing();

    void stopTts();

    void destroy();

    void onAudioFocus();

    //外部应用主动申请使用
    boolean requestFocus(int usage, int type);

    void setTtsListener(VoiceTtsListener voiceTtsListener);

}
