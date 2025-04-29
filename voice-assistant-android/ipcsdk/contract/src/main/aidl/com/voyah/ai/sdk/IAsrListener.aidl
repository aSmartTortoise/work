package com.voyah.ai.sdk;

interface IAsrListener {

    /**
         * ASR识别状态
         * -1:识别失败
         * 0:开始识别
         * 1:识别结束
         *
         */
    void asrStatus(in int status);

    void asrText(in String text);
}
