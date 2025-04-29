package com.voyah.aiwindow.ui.widget.vpa;

enum VpaState {
    /**
     * 动画状态说明
     */
    DEFAULT("default"),
    LISTENING("anim/vpa/normal/listener"), //正常模式聆听
    SPEAKING("anim/vpa/normal/tts"), //正常模式播报
    LISTENING_PRIVATE("anim/vpa/private/listener"), //隐私模式聆听
    SPEAKING_PRIVATE("anim/vpa/private/tts"); //隐私模式播报

    private String text;

    VpaState(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
