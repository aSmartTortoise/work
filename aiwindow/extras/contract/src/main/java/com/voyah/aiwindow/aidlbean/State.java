package com.voyah.aiwindow.aidlbean;

public enum State {
    // 消息被取出
    TAKEN(0),
    // 弹窗显示
    SHOW(1),
    // 弹窗关闭
    DISMISS(2),
    // 出错
    ERROR(-1);

    private int code;

    State(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static State fromCode(int code) {
        for (State state : State.values()) {
            if (state.getCode() == code) {
                return state;
            }
        }
        // 如果找不到对应的枚举值，默认返回 ERROR
        return ERROR;
    }
}