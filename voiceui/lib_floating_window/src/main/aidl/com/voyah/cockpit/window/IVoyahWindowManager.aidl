// IVoyahWindowManager.aidl
package com.voyah.cockpit.window;
import com.voyah.cockpit.window.model.WindowMessage;
import com.voyah.cockpit.window.IReceiveWindowMsgCallback;


/**
 * author : jie wang
 * date : 2024/3/7 14:30
 * description : 定义voice_ui 进程对外暴露的能力接口
 */
interface IVoyahWindowManager {
    // 向Server发送消息，windowMessage是json结构，根据对应的字段来执行指定的window，
    // 根据对应的业务数据字段，展开卡片并绑定数据。
    void sendWindowMessage(in String windowMessage);

    void registerReceiveCallback(IReceiveWindowMsgCallback callback);

    void unRegisterReceiveCallback(IReceiveWindowMsgCallback callback);

    void onVoiceAwake(in int voiceServiceMode, in int voiceLocation);

    void onVoiceListening();

    void onVoiceSpeaking();

    void onVoiceExit();

    void inputTypewriterText(in String text, in int textStyle);

    void showCard(in String cardJson);

    void updateCardData(in String cardJson);

    void dismissCard();

    void showExecuteFeedbackWindow(in String feedbackJson);

    void updateExecuteFeedbackWindowData(in String feedbackJson);

    void dismissExecuteFeedbackWindow(in int voiceLocation);

    void scrollCard(in int direction);

    boolean scrollCardView(in int direction);


}