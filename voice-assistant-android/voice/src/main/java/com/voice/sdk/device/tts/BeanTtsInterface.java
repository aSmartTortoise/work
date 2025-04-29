package com.voice.sdk.device.tts;

import com.voyah.ai.sdk.IGeneralTtsCallback;
import com.voyah.ai.sdk.VoiceTtsBean;
import com.voyah.ai.sdk.listener.ITtsPlayListener;

/**
 * @author:lcy
 * @data:2024/4/16
 **/
public interface BeanTtsInterface {
    void initialize();

    void speak(String text);

    void speak(String text, ITtsPlayListener listener);

//    void speakSynchronized(String text, ITtsPlayListener listener);

    void speakSynchronized(String text, boolean isImt, ITtsPlayListener listener);

    void speak(String text, ITtsPlayListener listener, String speaker);

    void speakImt(String text);

    void speakImt(String text, ITtsPlayListener listener);

    void speak(String text, boolean isImt, ITtsPlayListener listener);

    void speak(String text, int usage, int location, ITtsPlayListener listener);

    void speak(String text, int direction, ITtsPlayListener listener);

    void speakInformation(String text, String type, ITtsPlayListener listener);

    void speakImt(String text, ITtsPlayListener listener, String speaker);

    void speakStream(String text, ITtsPlayListener listener, int streamStatus); // streamStatus  0:流式合成请求开始 1:流式合成请求中 2:流式合成请求结束

    void speakBean(VoiceTtsBean voiceTtsBean, ITtsPlayListener listener);

    void speakStreamBean(VoiceTtsBean voiceTtsBean, ITtsPlayListener listener, int streamStatus); // streamStatus  0:流式合成请求开始 1:流式合成请求中 2:流式合成请求结束

    void speakInformationBean(VoiceTtsBean voiceTtsBean, ITtsPlayListener listener, String type);

    void stopCurTts();

    void shutUp();

    void shutUpOneSelf();

    boolean isSpeaking(int usage);

    void requestAudioFocusByUsage();

    void releaseAudioFocusByUsage();

    void stopById(String originTtsId);

    void registerGeneralTtsStatus(IGeneralTtsCallback callback);

    void unregisterGeneralTtsStatus(IGeneralTtsCallback callback);

    void onDestroy();
}
