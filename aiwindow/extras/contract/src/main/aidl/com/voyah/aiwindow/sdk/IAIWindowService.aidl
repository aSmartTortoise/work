package com.voyah.aiwindow.sdk;

import com.voyah.aiwindow.sdk.IMsgResultCallback;
import com.voyah.aiwindow.aidlbean.AIMessage;

interface IAIWindowService {

    /**
    * 发送ai消息
    * @param msg 消息
    * @param callback 执行回调
    */
    void sendAIMessage(in AIMessage msg, IMsgResultCallback callback);

}