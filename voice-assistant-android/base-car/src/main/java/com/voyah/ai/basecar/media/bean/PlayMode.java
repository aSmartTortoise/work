package com.voyah.ai.basecar.media.bean;

public enum PlayMode {
    cycle(0), single_cycle(0), in_order(1), list_cycle(2), random_play(3);

    private final int type;

    PlayMode(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
