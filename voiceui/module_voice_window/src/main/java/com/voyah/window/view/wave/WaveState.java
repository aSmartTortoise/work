package com.voyah.window.view.wave;

public enum WaveState {
    /**
     * 动画状态说明
     */
    DEFAULT("default"),
    LISTENING_LIGHT("anim/wave/light/listener"), //浅色声浪聆听
    LISTENING_NIGHT("anim/wave/night/listener"); //深色声浪聆听

    private String text;

    WaveState(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
