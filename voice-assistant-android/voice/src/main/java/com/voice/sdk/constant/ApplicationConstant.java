package com.voice.sdk.constant;

public interface ApplicationConstant {

    String PACKAGE_NAME_MAP = "com.mega.map";
    String ACTION_NAME_MAP = "com.mega.map.assistant.AssistantService";
    String PKG_GALLERY = "com.voyah.cockpit.gallery";
    String PKG_WEATHER = "com.voyah.cockpit.weather";
    String PKG_LAUNCHER = "com.voyah.cockpit.launcher";
    String PKG_ARCREATOR = "com.crystal.h37.arcreator";
    String PKG_CALENDAR = "com.voyah.cockpit.calendar";
    String MODE_DAY_NIGHT = "persist.mega.daynight.mode";
    String MAIN_ACTIVITY_LAUNCHER = PKG_LAUNCHER + ".LauncherVCOS";
    String PKG_SETTINGS = "com.voyah.cockpit.vehiclesettings";
    String PKG_SEAT = "com.voyah.cockpit.seat";
    String PKG_HVAC = "com.voyah.cockpit.airconditioner";
    String PKG_SCENARIO = "com.voyah_invent.scenario";
    String PKG_APP_STORE = "com.xiaoma.appstore";
    String PKG_SYSTEMUI = "com.android.systemui";
    String PKG_SYSTEMUI_PLUGIN = "com.voyah.cockpit.systemui";
    String PKG_VOICEUI = "com.voyah.voice.ui";
    String PKG_SCENE_MODE = "com.voyah.cockpit.scenemode";
    String PKG_TTS_SERVICE = "com.voyah.vcos.ttsservices";
    String PKG_THUNDER_KTV = "com.thunder.carplay";
    String PKG_MIGU_VIDEO = "cn.cmvideo.car.play";
    String PKG_ADAS = "com.voyah.cockpit.adas";

    String ACTION_USER_CENTER_LOGIN = "com.voyah.personalcenter.login";
    String ACTION_USER_CENTER_LOGOUT = "com.voyah.personalcenter.logout";
    String ACTION_USER_CENTER_USERINFO_UPDATE = "com.voyah.personalcenter.userinfo.update";


    String SP_NAME_COMMON = "app_common";
    String SP_KEY_USER_ID = "user_id";

    /**
     * 可见即可说intent
     */
    String INTENT_VIEWCMD = "viewcmd";
    String ACTION_WIEWCMD_TRIGGER_CHANGED = "com.voyah.ai.action.TRIGGER_CHANGED";

    String SLOT_NAME_USB_GALLERY = "usb";
    String SLOT_NAME_LOCAL_GALLERY = "local";
    String SLOT_NAME_ONLINE_GALLERY = "online";
    String SLOT_NAME_TRANSFER_LIST_GALLERY = "transfer_list";
    String SLOT_NAME_DEFAULT_GALLERY = "default";
    String SLOT_NAME_AI_DRAWING_GALLERY = "ai_drawing";
    String SLOT_NAME_TRIP_SHOOT_GALLERY = "trip_shoot";
    String SLOT_NAME_COLLECTION_GALLERY = "collection";

    String SLOT_SWITCH_TYPE_OPEN = "open";
    String SLOT_SWITCH_TYPE_CLOSE = "close";

    String SLOT_NAME_VOYAH_SHARE_GALLERY = "voyah_share";

    String APP_NAME_WEATHER = "天气";
    String DOMAIN_NAME_WEATHER = "weather";

    String APP_NAME_APPSTORE = "岚图商城";

    String APP_NAME_SHOW_BOX = "酷玩盒子";

    String SLOT_APP_NAME_AI_DRAWING = "AI绘画";
    String SLOT_APP_NAME_WEATHER = "天气";
    String SLOT_APP_NAME_GALLERY = "相册";
    String APP_NAME_AI_DRAWING = "AI绘画";
    String APP_NAME_GALLERY = "相册";

    String UI_NAME_COLLECTION = "collection";

    String UI_NAME_HISTORY = "history";

    String UI_NAME_RECOMMEND = "recommend";

    String UI_NAME_APP = "app";

    String UI_NAME_SERVICE = "service";

    String UI_NAME_MINE = "mine";

    String UI_NAME_FILE_TRANSFER = "file_transfer";

    String TAB_NAME_FUNNY_EXOCYTOSIS = "funny_exocytosis";
    String TAB_NAME_MUSIC_LIGHT_SHOW = "music_light_show";

    /**
     * 负一屏显示状态，system提供给语音判断负一屏是否在显示。 1：显示， 0：未显示
     */
    String QS_PANEL_SHOW_STATE = "systemui_qs_panel_show_state";

    String KEY_SYSTEM_INFO_HIDING = "system_info_hiding";
    String LAUNCHER_PACKAGE_1 = "com.crystal.h37.arcreator";
    String USER_CENTER_PACKAGE_NAME = "com.voyah.cockpit.usercenter";
    String MICRO_SCENE_PACKAGE_NAME = "com.voyah_invent.scenario";

    public static final int VCOS_LEFT_SPLIT_SCREEN_WIDTH = 850;

}
