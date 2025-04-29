package com.voyah.ai.sdk.listener;

import java.util.List;

public interface IHintResultListener {

    /**
     * hint结果回调
     *
     * @param result
     */
    void onHintResult(List<String> result);
}
