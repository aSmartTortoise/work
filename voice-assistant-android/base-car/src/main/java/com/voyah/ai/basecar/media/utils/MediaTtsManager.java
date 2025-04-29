package com.voyah.ai.basecar.media.utils;

import com.voice.sdk.tts.TtsReplyUtils;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;

public class MediaTtsManager {

    public static final String APP_NAME_BILI = "哔哩哔哩";
    public static final String APP_NAME_IQY = "爱奇艺";
    public static final String APP_NAME_MI_GU = "咪咕视频";
    public static final String APP_NAME_TENCENT = "腾讯视频";
    public static final String APP_NAME_KTV = "雷石KTV";
    public static final String APP_NAME_TIKTOK = "车鱼视听";
    public static final String APP_NAME_LOCAL_VIDEO = "本地视频";

    private static volatile MediaTtsManager mediaTtsManager;

    private MediaTtsManager() {
    }

    public static MediaTtsManager getInstance() {
        if (mediaTtsManager == null) {
            synchronized (MediaTtsManager.class) {
                if (mediaTtsManager == null) {
                    mediaTtsManager = new MediaTtsManager();
                }
            }
        }
        return mediaTtsManager;
    }

    public TTSBean getOpenAppTts(String appName, String screenName) {
        if (MediaHelper.isSupportMultiScreen()) {
            return TtsReplyUtils.getTtsBean("1100030", "@{screen_name}", screenName, "@{app_name}", appName);
        } else {
            return TtsReplyUtils.getTtsBean("1100002", "@{app_name}", appName);
        }
    }

    public TTSBean getAlreadyOpenAppTts(String appName, String screenName) {
        if (MediaHelper.isSupportMultiScreen()) {
            return TtsReplyUtils.getTtsBean("1100029", "@{screen_name}", screenName, "@{app_name}", appName);
        } else {
            return TtsReplyUtils.getTtsBean("1100001", "@{app_name}", appName);
        }
    }

    public TTSBean getCloseAppTts(String appName, String screenName) {
        if (MediaHelper.isSupportMultiScreen()) {
            return TtsReplyUtils.getTtsBean("1100031", "@{screen_name}", screenName, "@{app_name}", appName);
        } else {
            return TtsReplyUtils.getTtsBean("1100003", "@{app_name}", appName);
        }
    }

    public TTSBean getAlreadyCloseAppTts(String appName, String screenName) {
        if (MediaHelper.isSupportMultiScreen()) {
            return TtsReplyUtils.getTtsBean("1100032", "@{screen_name}", screenName, "@{app_name}", appName);
        } else {
            return TtsReplyUtils.getTtsBean("1100004", "@{app_name}", appName);
        }
    }
}
