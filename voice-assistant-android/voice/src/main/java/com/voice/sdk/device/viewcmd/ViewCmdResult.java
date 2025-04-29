package com.voice.sdk.device.viewcmd;

public class ViewCmdResult {
    public String query;
    public String text;
    public String prompt;
    public int direction = -1;

    @Override
    public String toString() {
        return "ViewCmdResult{" +
                "query='" + query + '\'' +
                ", text='" + text + '\'' +
                ", prompt='" + prompt + '\'' +
                ", direction='" + direction + '\'' +
                '}';
    }
}
