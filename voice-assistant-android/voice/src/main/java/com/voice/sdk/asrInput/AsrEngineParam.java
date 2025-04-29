package com.voice.sdk.asrInput;


import java.util.HashMap;
import java.util.Map;

public class AsrEngineParam {
    public String wsId;
    public String reqId;
    public String deviceId;
    public String accountId;
    public Map<String, Object> extraInfo;

    public AsrEngineParam(String wsId, String reqId) {
        this.wsId = wsId;
        this.reqId = reqId;
        this.extraInfo = new HashMap<>();
    }

    public AsrEngineParam setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public AsrEngineParam setAccountId(String accountId) {
        this.accountId = accountId;
        return this;
    }

    public AsrEngineParam putExtraInfo(String key, Object object) {
        this.extraInfo.put(key, object);
        return this;
    }
}
