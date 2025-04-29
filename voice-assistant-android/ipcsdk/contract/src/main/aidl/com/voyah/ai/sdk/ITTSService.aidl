package com.voyah.ai.sdk;

import com.voyah.ai.sdk.IVAReadyCallback;
import com.voyah.ai.sdk.ITtsCallback;
import com.voyah.ai.sdk.VoiceTtsBean;
import com.voyah.ai.sdk.IGeneralTtsCallback;

interface ITTSService {
    /**
    * 注册tts服务就绪回调
    */
    void registerReadyCallback(String packageName, IVAReadyCallback callback);

    /**
    * 反注册tts服务就绪回调
    */
    void unregisterReadyCallback(String packageName, IVAReadyCallback callback);

    //tts服务是否已就绪
    boolean isRemoteReady();

    /**
    * TTS播报
    * @param pkgname 包名
    */
    oneway void speak(in String pkgname, in String text, in ITtsCallback callback, in String speaker, in boolean highPriority);

    /**
     * 停止正在进行的播报
     * @param pkgname 包名
     */
     void stopCurTts(String pkgname);

    /**
    * 停止所有播报
    * @param pkgname 包名
    */
    oneway void shutUp(String pkgname);

    /**
    * 指定通道
    * @param usage 通道
    */
    void setUsage(String pkgname, int usage);

    /**
      * 流式TTS播报(不可用)
      * @param pkgname 包名
      * @param text  tts文本
      * @param callback 回调
      * @param streamStatus 流式请求状态
      */
    oneway void streamSpeak(in String pkgname, in String text, in ITtsCallback callback, in int streamStatus);

    /**
     * 指定的usage通道是否正在进行TTS播报
     */
     boolean isSpeaking(int usage);

     /**
      * 设置tts播报音色
      *
      * @param speaker 对应的音色
      */
     void setTtsSpeaker(String pkgname, String speaker);

    oneway void speakBean(in String pkgname,in VoiceTtsBean voiceTtsBean ,in ITtsCallback callback);

    oneway void streamSpeakBean(in String pkgname, in VoiceTtsBean voiceTtsBean, in ITtsCallback callback, in int streamStatus);

    oneway void speakInformationBean(in String pkgname, in VoiceTtsBean voiceTtsBean, in ITtsCallback callback, in String speaker, in boolean highPriority);

    oneway void setNearByTtsStatus(boolean isNearByTts);

    /**
     * 申请指定通道焦点
     * @param packageName 申请业务方包名
     * @param usage 申请的指定通道
     * @param type 申请类型 0:取消持有焦点 1:申请持有焦点
     */
     boolean requestAudioFocusByUsage(String packageName, int usage, int type);

    /**
     * 停止当前服务发起的所有播报
     * @param pkgname 包名
     */
     void shutUpOneSelf(String pkgname);

    /**
    * TTS播报
    */
    oneway void speakWithUsage(in String pkgname, in String text, in ITtsCallback callback, in String speaker, in boolean highPriority, in int usage);

    /**
    * 停止或取消指定任务
    */
    oneway void stopById(in String pkgname, in String originTtsId);

    /**
    * 监听当前播报使用通道
    */
    void registerGeneralTtsStatus(in String pkgname ,IGeneralTtsCallback callback);

    void unregisterGeneralTtsStatus(in String pkgname ,IGeneralTtsCallback callback);

    /**
       * 就近禁用(头枕音响开关打开后，就近播报开关状态改为禁用)
       */
    oneway void forbiddenNearByTts(boolean isForbidden);

     /**
      * TTS播报
      * 指定通道及就近位置播报
      */
    oneway void speakWithUsageAndLocation(in String pkgname, in String text, in ITtsCallback callback, in String speaker, in boolean highPriority, in int usage,in int location);

}