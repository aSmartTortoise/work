package com.voyah.ai.sdk.bean;

import com.voyah.ai.sdk.JsonUtil;

/**
 * nlu结果
 */
public class NluResult {

    /**
     * 唯一标识
     */
    public String id;
    /**
     * asr文本
     */
    public String rawText;
    /**
     * 语音方案商返回的执行回答语术
     */
    public String answerText;
    /**
     * 垂类
     */
    public String domain;

    /**
     * 初始的nlu内容
     */
    public String rawNlu;

    /**
     * 方位
     */
    public int direction;

    /**
     * 意图
     */
    public String intent;

    /**
     * 具体数据源数组
     */
    public String data;

    /**
     * 是否为命令词
     */
    public boolean isShortcut;

    /**
     * 是否在线nlu
     */
    public boolean isOnline;

    @Override
    public String toString() {
        return JsonUtil.toJSONString(this);
    }
}
