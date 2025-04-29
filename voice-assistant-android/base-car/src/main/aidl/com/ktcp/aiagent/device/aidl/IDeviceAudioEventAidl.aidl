// IDeviceAudioEventAidl.aidl
package com.ktcp.aiagent.device.aidl;

// Declare any non-default types here with import statements

interface IDeviceAudioEventAidl {
   /**
     * 设备服务通知腾讯视频事件的回调接口
     *
     * @param event 事件值。具体定义如下：
     *              EVENT_DATA_READABLE:   1 - 音频数据可读的事件
     *              EVENT_DATA_END:        2 - 音频数据读取结束事件
     *              EVENT_ASR_TEXT_RESULT: 3 - 语音ASR后的文本结果
     * @param jsonParams 事件参数，用于以后扩展，暂时不用
     */
    void onAudioEvent(int event, String jsonParams);
}