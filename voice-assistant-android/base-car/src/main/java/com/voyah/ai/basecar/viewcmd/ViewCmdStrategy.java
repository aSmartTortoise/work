package com.voyah.ai.basecar.viewcmd;

public enum ViewCmdStrategy {
    UI_TEXT("uiText"),
    CONTENT_DESCRIPTION("contentDescription");

    private final String strategy;

    ViewCmdStrategy(String strategy) {
        this.strategy = strategy;
    }

    public String strategy() {
        return this.strategy;
    }
}