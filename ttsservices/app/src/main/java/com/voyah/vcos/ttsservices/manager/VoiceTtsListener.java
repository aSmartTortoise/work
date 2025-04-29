package com.voyah.vcos.ttsservices.manager;

/**
 * @author:lcy 播放状态回调
 * @data:2024/2/8
 **/
public interface VoiceTtsListener {

    void onTtsStart(String ttsId, int ttsType);

    void onTtsError(String ttsId, int errorCode, String msg, int ttsType);

    void onTtsDiscard(String ttsId, int errorCode, String msg, int ttsType);

    void onTtsStop(String ttsId, int ttsType);

    void onTtsComplete(String ttsId, int ttsType);

//    //---------------------流式--------------------------
//
//    void onStreamTtsStart(String ttsId);
//
//    void onStreamTtsError(String ttsId,int errorCode,String msg);
//
//    void onStreamTtsDiscard(String ttsId,int errorCode,String msg);
//
//    void onStreamTtsStop(String ttsId);
//
//    void onStreamTtsComplete(String ttsId);
}
