package com.voice.sdk.model;

import com.voyah.ds.common.entity.IData;

/**
 * author : jie wang
 * date : 2024/8/27 10:18
 * description :
 */
public class CPEntity {
    private IData data;

    private String requestId;

    public IData getData() {
        return data;
    }

    public void setData(IData data) {
        this.data = data;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
