package com.voyah.vcos.ttsservices.manager;

import com.voyah.vcos.ttsservices.info.PlayTTSBean;

/**
 * @author:lcy TTS功能接口类
 * @data:2024/1/30
 **/
public interface ITtsResolver {
    void init(int num);

    //todo:参数形式待参考语音对外sdk后确定
    void startSynthesis(PlayTTSBean voiceTtsBean);

    void stopSynthesis();

    void reset();

    void setTtsResolverCallBack(ITtsResolverCallBack ttsResolverCallBack);

    int getNum();

    void playEnd(String ttsId, int ttsType);
}
