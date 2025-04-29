package com.voyah.ai.basecar.viewcmd.accessibility;

public enum ResponseType {
    ID(":id"),
    GESTURE(":gesture"),
    TEXT(":text");

    private final String type;

    ResponseType(String type) {
        this.type = type;
    }

    public String type() {
        return this.type;
    }
}