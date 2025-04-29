package com.voyah.ai.sdk.bean;

import com.voyah.ai.sdk.JsonUtil;


/**
 * 快捷命令
 */
public class ShortCommand {

    /**
     * "command": "打开音乐",
     * "intent": "music.on",
     * "skill": "music"
     */
    public String command;
    public String intent;
    public String skill;
    public String nlg;
    /**
     * 0表示单轮对话, 1表示多轮对话, 2表示强多轮对话
     */
    public int activeForm = 0;
    public String key;
    public Object value;

    /**
     * 被允许的mic zone
     *  FRONT_LEFT = 1;
     *  FRONT_RIGHT = 2;
     *  REAR_LEFT = 4;
     *  REAR_RIGHT = 8;
     */
    public int mics = DhMicMask.ALL;


    @Override
    public String toString() {
        return JsonUtil.toJSONString(this);
    }

}
