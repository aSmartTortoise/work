// IVoyahWindowManager.aidl
package com.voyah.cockpit.window;
import com.voyah.cockpit.window.model.WindowMessage;
import com.voyah.cockpit.window.model.PageInfo;
import com.voyah.cockpit.window.model.UIMessage;
import com.voyah.cockpit.window.IReceiveWindowMsgCallback;
import android.os.Bundle;
import com.voyah.cockpit.window.ICallback;
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

    void onVoiceAwake(in int voiceServiceMode, in int languageType, in int voiceLocation);

    void onVoiceListening();

    void onVoiceSpeaking();

    void onVoiceExit();

    void inputTypewriterText(in String text, in int textStyle);

    void showCard(in String cardJson);


    void updateCardData(in String cardJson);

    void dismissCard(in int screenType);


    void showExecuteFeedbackWindow(in String feedbackJson);

    void updateExecuteFeedbackWindowData(in String feedbackJson);

    void dismissExecuteFeedbackWindow(in int voiceLocation);

    void scrollCard(in int direction, in int screenType);

    boolean scrollCardView(in int direction, in int screenType);

    // 向前或者向后翻页， pageOffset 相对当前page的偏移量，负数为向前（上）翻页，正数为向后（向下翻页）。
    boolean nextPage(in int pageOffset, in int screenType);


    // 获取卡片ViewPage的当前page
    PageInfo getCurrentPage(in int screenType);

    boolean setCurrentItem(in int itemIndex, in int screenType);


    // 跳转到卡片ViewPager的指定page
    boolean setCurrentPage(in int pageIndex, in int screenType);

    // 获取当前card type
    String getCurrentCardType(in int screenType);

    void setVoiceMode(int voiceMode);

    void setLanguageType(int languageType);

    // 返回值为APIResult.CARD_REGISTER_VIEW_CMD表示卡片展开且注册了可见
    // 返回值为APIResult.CARD_NOT_REGISTER_VIEW_CMD表示卡片展开未注册可见
    // 返回值为APIResult.CARD_COLLAPSED表示卡片未展开
    // 返回值为APIResult.NO_RESOURCE表示VoiceUI的逻辑出错。
    // 返回值为APIResult.INIT表示VoiceUI线程切换出现了异常
    // 返回值为APIResult.ERROR表示VoiceUI处理过程中发生了线程打断异常
    int getCardViewRegisterViewCmdState(int displayId, in int screenType);

    void showVoiceView(in int voiceServiceMode, in int languageType, in int voiceLocation);
    void dismissVoiceView(in int screenType);

    // 返回值为APIResult.INFORMATION_CARD_EXPAND表示信息卡片展开了，
    // APIResult.OTHER_CARD_EXPAND 表示非信息类卡片展开了，
    // APIResult.CARD_COLLAPSED，表示卡片没展开，
    // APIResult.NO_RESOURCE，表示 window 没show或者出现异常
    int getCardState(in int screenType);

    void showWave(int location);

    void dismissWave();

    //VPA显示需要的属性
    //text      文本
    //textStyle     文本样式
    //voiceState
    void showVpa(String text, int textStyle, String voiceState);

    void showVoiceVpa(in UIMessage uiMessage);

    void setCeilingScreenEnable(in boolean enable);

    void setScreenEnable(in int screenType, in boolean enable);

    Bundle call(in String method, in Bundle bundle);

    oneway void callAsync(in String method, in Bundle bundle, ICallback callback);

    oneway void register(in String name, ICallback callback);

    oneway void unRegister(in String name);

}