package com.voyah.vcos.ttsservices.manager;

import com.voyah.vcos.ttsservices.mem.ByteArr;
import com.voyah.vcos.ttsservices.mem.ByteArrMgr;

/**
 * @author:lcy TTS合成回调监听类
 * @data:2024/1/30
 **/
public interface ITtsResolverCallBack {
    interface ResolverStatus {
        int RESOLVER_START = 0; //开始合成
        int RESOLVING = 1; //合成中
        int RESOLVER_COMPLETED = 2;
        int RESOLVER_FAIL = 3;
        int RESOLVER_DISCARD = 4;

        int PROMPT_TONE_COMPLETED = 5;
    }

    interface TtsResultCode {
        int DISCARD = -1; //被丢弃
        int OTHER = -2; //其他错误

        int START = 0; //开始
        int RESOLVING = 1; //合成中
        int COMPLETED = 2; //完成

    }

    //todo:合成状态是否要细分，还是使用playStatus、使用status需要定义下各个返回状态
    void onStatus(String ttsId, int playStatus, int ttsType);

//    void onSyncError(String ttsId, int errorCode, String msg, int ttsType);

    void onData(String ttsId, ByteArrMgr.ByteArrObj arrObject,int ttsType);

    void onData(String ttsId, byte[] buffer);
}
