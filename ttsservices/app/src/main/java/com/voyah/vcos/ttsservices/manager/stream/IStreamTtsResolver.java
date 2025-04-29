package com.voyah.vcos.ttsservices.manager.stream;

import com.voyah.vcos.ttsservices.info.PlayTTSBean;
import com.voyah.vcos.ttsservices.manager.ITtsResolverCallBack;

/**
 * @author:lcy
 * @data:2024/5/21
 **/
public interface IStreamTtsResolver {

    //流式任务开始
    void beginStream(PlayTTSBean voiceTTSBean);
    //流式任务结束
    void endStream();

    void append(PlayTTSBean voiceTtsBean);

    void stopSynthesis();

    void reset();

    void setTtsResolverCallBack(ITtsResolverCallBack ttsResolverCallBack);

    void playEnd(String ttsId, int ttsType);
}
