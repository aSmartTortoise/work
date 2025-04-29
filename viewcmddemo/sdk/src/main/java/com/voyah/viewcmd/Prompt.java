package com.voyah.viewcmd;

import static com.voyah.viewcmd.VoiceViewCmdUtils.mCtx;

public class Prompt {

    /**
     * prompt switch 常量
     */
    public static final String _PROMPT_TYPE_SWITCH = mCtx.getString(R.string.attr_switch);
    public static final String PROMPT_SWITCH = create(_PROMPT_TYPE_SWITCH);

    /**
     * prompt tab 常量
     */
    public static final String _PROMPT_TYPE_TAB = mCtx.getString(R.string.attr_tab);
    public static final String PROMPT_TAB = create(_PROMPT_TYPE_TAB);

    /**
     * prompt Gesture 常量
     */
    public static final String _PROMPT_TYPE_GESTURE = mCtx.getString(R.string.attr_gesture);
    public static final String _PROMPT_VALUE_GESTURE_VERTICAL = mCtx.getString(R.string.attr_gesture_value_vertical);
    public static final String _PROMPT_VALUE_GESTURE_HORIZONTAL = mCtx.getString(R.string.attr_gesture_value_horizontal);
    public static final String _PROMPT_VALUE_GESTURE_PAGE = mCtx.getString(R.string.attr_gesture_value_page);

    public static final String PROMPT_GESTURE_VERTICAL = create(_PROMPT_TYPE_GESTURE, _PROMPT_VALUE_GESTURE_VERTICAL);
    public static final String PROMPT_GESTURE_HORIZONTAL = create(_PROMPT_TYPE_GESTURE, _PROMPT_VALUE_GESTURE_HORIZONTAL);
    public static final String PROMPT_GESTURE_PAGE = create(_PROMPT_TYPE_GESTURE, _PROMPT_VALUE_GESTURE_PAGE);

    /**
     * prompt List 常量
     */
    public static final String _PROMPT_TYPE_LIST = mCtx.getString(R.string.attr_list);
    /**
     * prompt badge 常量
     */
    public static final String _PROMPT_TYPE_BADGE = mCtx.getString(R.string.attr_badge);

    public static String create(String type, String value) {
        return type + ":" + value;
    }

    public static String create(String type) {
        return type;
    }

    public static String getType(String prompt) {
        return prompt.split(":")[0];
    }
}