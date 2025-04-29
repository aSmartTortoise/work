package com.voyah.ai.sdk.bean;

public class VprError {
    public int errCode;
    public String errMessage;

    public VprError() {
    }

    public VprError(int errCode, String errMessage) {
        this.errCode = errCode;
        this.errMessage = errMessage;
    }

    @Override
    public String toString() {
        return "VprError{" +
                "errCode=" + errCode +
                ", errMessage='" + errMessage + '\'' +
                '}';
    }
}
