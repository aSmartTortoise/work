package com.voyah.ai.sdk.bean;

import androidx.annotation.NonNull;

import com.voyah.ai.sdk.JsonUtil;

/**
 * 声纹注册回调结果
 */
public class VprResult {

    public String id;
    public VprError error;
    public String extra;

    public VprResult() {
    }

    public VprResult(String id) {
        this.id = id;
    }

    public VprResult(String id, VprError err) {
        this.id = id;
        this.error = err;
    }

    @NonNull
    @Override
    public String toString() {
        return JsonUtil.toJSONString(this);
    }
}
