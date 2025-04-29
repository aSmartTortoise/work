package com.voyah.aiwindow.ui.widget.wave;

enum WaveState {
    /**
     * 动画状态说明
     */
    DEFAULT("default"),
    LISTENING("anim/wave/listener"); //声浪聆听

    private String text;

    WaveState(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
