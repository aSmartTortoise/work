package com.voyah.ai.sdk.listener;

import com.voyah.ai.sdk.bean.PvcResult;

import java.util.List;

public interface IPvcResultListener {
    /**
     * 声纹复刻结果回调
     */
    void pvcResult(List<PvcResult> result);
}
