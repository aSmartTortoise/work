package com.voyah.cockpit.window.model;

import java.util.List;

/**
 * author : jie wang
 * date : 2024/7/3 20:31
 * description :
 */
public class ChatMessage extends MultiItemEntity {

    /**
     *  llm响应的内容，可以是markdown格式或者含有html标签。
     */
    private String content = "";

    private String ttsContent;


    /**
     *  llm响应的额外推荐的item
     */
    private List<String> recommends;

    /**
     *  llm响应的内容是否是流式的
     */
    private int streamMode;

    /**
     *  聊天话题分类
     */
    private String topicType;

    /**
     * 流式响应的内容总长度
     */
    private int totalLen;

    /**
     *  深度思考文本
     */
    private String noTaskReasonText;

    /**
     *  深度思考首帧
     */
    private boolean modelDeepFirstReason;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTtsContent() {
        return ttsContent;
    }

    public void setTtsContent(String ttsContent) {
        this.ttsContent = ttsContent;
    }

    public List<String> getRecommends() {
        return recommends;
    }

    public void setRecommends(List<String> recommends) {
        this.recommends = recommends;
    }

    public int getStreamMode() {
        return streamMode;
    }

    public void setStreamMode(int streamMode) {
        this.streamMode = streamMode;
    }

    public String getTopicType() {
        return topicType;
    }

    public void setTopicType(String topicType) {
        this.topicType = topicType;
    }

    public int getTotalLen() {
        return totalLen;
    }

    public void setTotalLen(int totalLen) {
        this.totalLen = totalLen;
    }

    public String getNoTaskReasonText() {
        return noTaskReasonText;
    }

    public void setNoTaskReasonText(String noTaskReasonText) {
        this.noTaskReasonText = noTaskReasonText;
    }
    public boolean isModelDeepFirstReason() {
        return modelDeepFirstReason;
    }

    public void setModelDeepFirstReason(boolean modelDeepFirstReason) {
        this.modelDeepFirstReason = modelDeepFirstReason;
    }

    public ChatMessage copy() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(content);
        chatMessage.setTtsContent(ttsContent);
        chatMessage.setRecommends(recommends);
        chatMessage.setStreamMode(streamMode);
        chatMessage.setTopicType(topicType);
        chatMessage.setTotalLen(totalLen);
        chatMessage.setItemType(itemType);
        chatMessage.setNoTaskReasonText(noTaskReasonText);
        chatMessage.setModelDeepFirstReason(modelDeepFirstReason);
        return chatMessage;
    }

}
