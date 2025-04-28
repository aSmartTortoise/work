package com.voyah.window.view.vpa;

public enum VpaState {
    /**
     * 动画状态说明
     */
    DEFAULT("default"),
    LISTENING_NIGHT_NORMAL("anim/vpa/night/normal/listener"), //深色正常模式聆听
    LISTENING_NIGHT_PRIVATE("anim/vpa/night/private/listener"), //深色隐私模式聆听
    LISTENING_LIGHT_NORMAL("anim/vpa/light/normal/listener"), //浅色正常模式聆听
    LISTENING_LIGHT_PRIVATE("anim/vpa/light/private/listener"), //深色隐私模式聆听
    SPEAKING_NIGHT_NORMAL("anim/vpa/night/normal/tts"), //深色正常模式播报
    SPEAKING_NIGHT_PRIVATE("anim/vpa/night/private/tts"), //深色隐私模式播报
    SPEAKING_LIGHT_NORMAL("anim/vpa/light/normal/tts"), //浅色正常模式播报
    SPEAKING_LIGHT_PRIVATE("anim/vpa/light/private/tts"); //浅色隐私模式播报

    private String text;

    VpaState(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
