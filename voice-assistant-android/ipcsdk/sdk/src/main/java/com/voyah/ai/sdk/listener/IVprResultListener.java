package com.voyah.ai.sdk.listener;

import com.voyah.ai.sdk.bean.VprResult;

public interface IVprResultListener {

    /**
     * 声纹录制与保存结果回调
     */
    void onVprResult(VprResult result);
}
