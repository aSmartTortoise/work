// IReceiveWindowMsgCallback.aidl
package com.voyah.cockpit.window;
import com.voyah.cockpit.window.model.WindowMessage;

/**
 * author : jie wang
 * date : 2024/3/7 14:31
 * description : 定义voice_ui 进程主动向订阅的client发送通知的接口
 */
interface IReceiveWindowMsgCallback {

    // voice-ui 发送通知给注册的回调，通知Client。
    void onReceiveWindowMessage(in WindowMessage msg);

    // voice-ui 发送通知给注册的回调，通知Client。
    void onReceiveVoyahWindowMessage(in String msgJson);

    void onCardScroll(in String cardType, in int direction, in boolean canScroll);

    void interruptStreamInput(String domainType);

}