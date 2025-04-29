package com.voice.sdk.model;

/**
 * author : jie wang
 * date : 2024/11/9 11:16
 * description :
 */
public class LLMDataInfo {

    /**
     * reqId : 139fa07a-9509-4d18-a574-1e73c1770c4b
     * streamMode : -1
     * extraData : {"FaqLen": 12}
     */

    private String reqId;
    private int streamMode;
    private String extraData;

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

    public int getStreamMode() {
        return streamMode;
    }

    public void setStreamMode(int streamMode) {
        this.streamMode = streamMode;
    }

    public String getExtraData() {
        return extraData;
    }

    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }

    public class ExtraData {

        /**
         * FaqLen : 12
         */

        private Integer FaqLen;

        private int imageNum = 4;

        private Integer len;

        public Integer getFaqLen() {
            return FaqLen;
        }

        public void setFaqLen(Integer FaqLen) {
            this.FaqLen = FaqLen;
        }

        public int getImageNum() {
            return imageNum;
        }

        public void setImageNum(int imageNum) {
            this.imageNum = imageNum;
        }

        public Integer getLen() {
            return len;
        }

        public void setLen(Integer len) {
            this.len = len;
        }
    }
}
