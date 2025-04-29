package com.voyah.ai.basecar.viewcmd.accessibility;

public enum ViewCmdGesture {
    UP("up"),
    DOWN("down"),
    LEFT("left"),
    RIGHT("right");


    private String direct = "";

    ViewCmdGesture(String direct) {
        this.direct = direct;
    }

    public String direct() {
        return this.direct;
    }
}