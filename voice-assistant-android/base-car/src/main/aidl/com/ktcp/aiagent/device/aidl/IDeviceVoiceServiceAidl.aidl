// IDeviceVoiceServiceAidl.aidl
package com.ktcp.aiagent.device.aidl;

// Declare any non-default types here with import statements
import com.ktcp.aiagent.device.aidl.IDeviceAudioEventAidl;
import com.ktcp.aiagent.device.aidl.IDeviceCallbackAidl;

interface IDeviceVoiceServiceAidl {
    /**
     * 请求系统的音频分发（进入了腾讯视频界面）
     *
     * @param sampleRate  录音采样率
     * @param channel     录音通道配置
     * @param encoding    录音音频编码
     * @param event       设备服务回调Agent事件通知的Binder接口
     */
    void requestAudio(int sampleRate, int channel, int encoding, IDeviceAudioEventAidl event);

    /**
     * 放弃系统的音频分发（退出了腾讯视频界面）
     */
    void abandonAudio();

    /**
     * 读取分发的音频数据
     *
     * @param data  读取的音频数据
     * @return  读取的数据字节数
     */
    int readAudioData(inout byte[] data);

    /**
     * 给设备反馈处理结果，设备根据情况决定要不要继续处理
     *
     * @param result  返回给系统处理的结果
     *        {"resultCode": 0, "voiceId": "abcdefg", "voice"asrText": "你好", "action": "", "feedback": "你好", "errMsg": ""}
     */
    void onAsrResult(String result);

    /**
     * 调用系统的播放TTS
     *
     * @param json 播放文本TTS, {"text": "你好"}
     */
    void playTTS(String json);

    /**
     * 正在语音输入，通知设备打开语音输入的提示灯
     */
    void openLight();

    /**
     * 结束语音输入，通知设备关闭语音输入的提示灯
     */
    void closeLight();

    /**
     * 向设备语音服务提供的泛化回调接口，设备方可以通过该接口调用到腾讯视频接口，Agent只进行透明转发
     *
     * @param callback 泛化回调接口
     */
    void setCallback(IDeviceCallbackAidl callback);

    /**
     * 调用设备方的泛化接口，腾讯视频可以调用到设备方的提供的接口，Agent只进行透明转发
     *
     * @param method 泛化的方法名
     * @param params 泛化的方法参数，可为json格式
     * @return
     */
    String call(String method, String params);

    /**
     * Agent获取分配给设备的身份校验信息
     *
     * @return json格式 {"􏰆􏰂􏰂appId":"xxx", "secretKey":"xxx"}
     */
    String getAuthConfig();
}