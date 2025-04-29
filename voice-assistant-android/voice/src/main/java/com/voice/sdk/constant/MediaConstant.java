package com.voice.sdk.constant;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MediaConstant {
    public static final String APP_NAME = "com.voyah.cockpit.voyahmusic";
    public static final String MEDIACENTER_APP_NAME = "com.voyah.cockpit.media";
    public static final String BLUETOOTH_APP_NAME = "com.android.bluetooth";

    public static final String DOMAIN_TYPE_MULTIMEDIA_VIDEO = "domain_type_multimedia_video";
    public static final String DOMAIN_TYPE_MULTIMEDIA_MUSIC = "domain_type_multimedia_music";

    public static String IS_WY_PLAY = "is_wy_play";
    // MEDIA_TYPE
    public static final String MUSIC = "music";
    public static final String VIDEO = "video";
    public static final String AUDIO_BOOK = "audio_book";
    public static final String RADIO = "radio";
    public static final String KTV = "ktv";
    public static final String AUDIO = "audio";

    //媒体接口类型
    public static final String SOURCE_TYPE = "media_manager";
    public static final String TYPE_UI = "ui";
    public static final String TYPE_QQ = "qq_music";
    public static final String TYPE_YT = "yt_music";
    public static final String TYPE_WY = "wy_music";
    public static final String TYPE_XM = "xmly_music";
    public static final String TYPE_BT = "bt_music";
    public static final String TYPE_USB = "usb_music";

    public static final String SP_HISTORY_MEDIASOURCE= "history_mediasource";

    //媒体搜索列表数量
    public static final int SEARCH_COUNT = 50;

    public static final int ONE_COUNT = 1;

    public static class MediaControl{
        public static final String PLAY = "play";
        public static final String PAUSE = "pause";
        public static final String ADD_FAVORITE = "addFavorite";
        public static final String CANCEL_FAVORITE = "cancelFavorite";
        public static final String REPLAY = "replay";
        public static final String PRE = "skipToPrevious";
        public static final String NEXT = "skipToNext";
        public static final String FORWARD = "forward";
        public static final String REVERSE = "reverse";
        public static final String SEEK = "seek";
        public static final String DAN_MA_KU_SET = "danmakuSet";
        public static final String INCREASE_SPEED = "increaseSpeed";
        public static final String DECREASE_SPEED = "decreaseSpeed";
        public static final String SET_SPEED = "setSpeed";
        public static final String INCREASE_QUALITY = "increaseQuality";
        public static final String DECREASE_QUALITY = "decreaseQuality";
        public static final String QUALITY_SWITCH = "qualitySwitch";
        public static final String PLAY_EPISODE = "playEpisode";
        public static final String MEDIA_REQUEST = "mediaRequest";
        public static final String SET_PLAY_MODE = "setPlayMode";
    }

    public static class PageName{
        public static final String HISTORY = "history";
        public static final String FAVORITE = "favorite";
        public static final String VIDEO = "video";
    }

    public static class MediaResult{
        public static final int SUCCESS = 0;//成功
        public static final int NOT_SUPPORT = 1;//当前场景不支持
        public static final int ALREADY_FIRST_OR_MAX = 2;//已是第一个/最大值
        public static final int ALREADY_LAST_OR_MIN = 3;//已是最后一个/最小值
        public static final int OUT_RANGE = 4;//目标超出范围
        public static final int NET_FAIL = 5;//网络原因导致失败
        public static final int NO_OBJECT = 6;//当前无可关注对象
        public static final int ALREADY_CONCERN = 7;//当前对象已关注
        public static final int ALREADY_NO_CONCERN = 8;//当前对象未关注
        public static final int ALREADY_COLLECTION = 11;//当前对象已收藏
        public static final int ALREADY_NO_COLLECTION = 12;//当前对象未收藏
        public static final int NEED_LOGIN = 17;//需要登录
        public static final int KEY_IS_NULL = 18;//参数为空
        public static final int REPEAT_OPERATION = 19;//重复操作
        public static final int BUSINESS_NOT_SUPPORT = 98;//业务不支持
        public static final int OUT_TIME = 99;//通用失败/10秒超时
    }

    public static class  Location {
        public static List<String> Location_LIST = Arrays.asList("first_row_left", "first_row_right", "second_row_left", "second_row_right", "third_row_left", "third_row_right");
        public static Map<String, Integer> Location_MAP = new HashMap() {
            {
                put(Location_LIST.get(0), 0);
                put(Location_LIST.get(1), 1);
                put(Location_LIST.get(2), 2);
                put(Location_LIST.get(3), 3);
                put(Location_LIST.get(4), 4);
                put(Location_LIST.get(5), 5);
            }
        };
    }
}
