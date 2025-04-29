package com.voice.sdk.device.navi.bean;


import com.voice.sdk.device.navi.NaviConstants;

import org.json.JSONObject;


public class NaviResponse<T> {
    private boolean success;
    private int resultCode;
    private JSONObject jsonObject;

    private T data;

    public NaviResponse(int code) {
        this.success = false;
        this.resultCode = code;
    }

    public NaviResponse(String result) {
        if (result == null || result.isEmpty()) {
            this.success = false;
        } else {
            try {
                JSONObject jsonObject = new JSONObject(result);
                this.resultCode = jsonObject.optInt("resultCode");
                this.jsonObject = jsonObject;
                this.success = (resultCode == NaviConstants.ErrCode.SUCCESS ||
                        resultCode == NaviConstants.ErrCode.ALREADY_CLOSE ||
                        resultCode == NaviConstants.ErrCode.ALREADY_OPEN);
            } catch (Exception e) {
                this.success = false;
            }
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isAlreadySuccess() {
        return resultCode == NaviConstants.ErrCode.ALREADY_CLOSE ||
                resultCode == NaviConstants.ErrCode.ALREADY_OPEN;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getResultCode() {
        return resultCode;
    }
}
