package com.voyah.cockpit.window.model;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * author : jie wang
 * date : 2024/3/7 14:19
 * description : 执行悬浮窗的action
 */
@StringDef({WindowAction.WINDOW_ACTION_SHOW,
        WindowAction.WINDOW_ACTION_DISMISS,
        WindowAction.WINDOW_ACTION_EXPAND_CARD,
        WindowAction.WINDOW_ACTION_REFRESH_CARD,
        WindowAction.WINDOW_ACTION_REPLACE_CARD,
        WindowAction.WINDOW_ACTION_COLLAPSE_CARD,
        WindowAction.WINDOW_ACTION_INPUT_TYPEWRITER,
        WindowAction.WINDOW_ACTION_SET_VOICE_STATE,
        WindowAction.WINDOW_ACTION_CARD_USER_TOUCH})
@Retention(RetentionPolicy.SOURCE)
public @interface WindowAction {
    String WINDOW_ACTION_SHOW = "window_action_show";
    String WINDOW_ACTION_DISMISS = "window_action_dismiss";
    String WINDOW_ACTION_EXPAND_CARD = "window_action_expand_card";
    // 刷新卡片内容 domain没更换
    String WINDOW_ACTION_REFRESH_CARD = "window_action_refresh_card";
    // 替换卡片 domain更换了
    String WINDOW_ACTION_REPLACE_CARD = "window_action_replace_card";
    String WINDOW_ACTION_COLLAPSE_CARD = "window_action_collapse_card";
    String WINDOW_ACTION_INPUT_TYPEWRITER = "window_action_input_typewriter";
    String WINDOW_ACTION_SET_VOICE_STATE = "window_action_SET_VOICE_STATE";
    String WINDOW_ACTION_CARD_USER_TOUCH = "window_action_card_user_touch";
}
