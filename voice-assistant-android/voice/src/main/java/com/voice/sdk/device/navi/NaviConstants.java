package com.voice.sdk.device.navi;

@SuppressWarnings("unused")
public class NaviConstants {
    private NaviConstants() {

    }

    public static final String SESSION_COMMON_OPT = "V_COS_1.0";


    public static final int MAP_MAX_LEVEL = 22;

    public static final int MAP_MIN_LEVEL = 4;

    public static final String DAY = "day";

    public static final String NIGHT = "night";

    public static final String AUTO = "auto";

    public static final String VIEW_TYPE = "view_type";

    public static final String VIEW_POINT = "view_point";

    public static final String VIEW_TYPE_2D = "2d";

    public static final String VIEW_TYPE_3D = "3d";

    public static final String VIEW_POINT_NORTH_TO_UP = "north_to_up";

    public static final String VIEW_POINT_CAR_TO_UP = "car_to_up";

    public static final String DES_QUERY_TYPE = "des_query_type";

    public static final String TOTAL_TIME = "total_time";

    public static final String REMAIN_TIME = "remain_time";

    public static final String TOTAL_DISTANCE = "total_distance";

    public static final String REMAIN_DISTANCE = "remain_distance";

    public static final String POI = "poi";

    public static final String FAVORITES_TYPE = "favorites_type";

    public static final String DEST_POI = "dest_poi";

    public static final String VIA_POI = "via_poi";

    public static final String ABANDON_DEST_POI = "abandon_dest_poi";

    public static final String NEW_DEST_POI = "new_dest_poi";

    public static final String TRAFFIC_TYPE = "traffic_type";

    public static final String TRAFFIC_FRONT = "front";

    public static final String TRAFFIC_WHOLE = "whole";

    public static final String DES = "des";

    public static final String SEARCH_INFO = "search_info";

    public static final String Q_LOCATION = "q_location";

    public static final String Q_DISTANCE = "q_distance";

    public static final String Q_DURATION = "q_duration";

    public static final String Q_TOTAL_DISTANCE = "q_total_distance";

    public static final String Q_TOTAL_DURATION = "q_total_duration";

    public static final String Q_ENERGY = "q_energy";

    public static final String Q_NUM = "q_num";

    public static final String Q_NAME = "q_name";

    public static final String SERVICE_AREA = "服务区";

    public static final String TOLL_STATION = "收费站";

    public static final String ROAD_TYPE = "road_type";

    public static final String MAIN_ROAD = "main_road";

    public static final String SIDE_ROAD = "side_road";

    public static final String ON_VIADUCT = "on_viaduct";

    public static final String DOWN_VIADUCT = "down_viaduct";

    public static final String BROADCAST_MODE_DETAIL = "detail";

    public static final String BROADCAST_MODE_PROMPT = "prompt_sound";

    public static final String BROADCAST_MODE_BRIEF = "brief";

    public static final String BROADCAST_MODE_STANDARD = "standard";

    public static final String BROADCAST_MODE = "broadcast_mode";

    public static final String SWITCH_MODE = "switch_mode";

    public static final String TEAM_OPERATION_TYPE = "team_operation_type";

    public static final String TEAM_OPERATION_CREATE = "create";

    public static final String TEAM_OPERATION_JOIN = "join";

    public static final String TEAM_OPERATION_VIEW = "view";

    public static final String TEAM_OPERATION_DISBAND = "disband";

    public static final String CHOOSE_TYPE = "choose_type";

    public static final String PREVIEW_TYPE = "preview_type";

    public static final String REGULAR_OVERVIEW = "regular_overview";

    public static final String CONFIRM = "confirm";

    public static final String CANCEL = "cancel";

    public static final String ROUTE_TYPE = "route_type";

    public static final String HOME = "家";

    public static final String COMPANY = "公司";

    public static final String DESTINATION = "目的地";

    public static final String WAY_POINT = "途经点";

    public static final String CURRENT_LOCATION = "当前位置";

    public static final String SEARCH_TYPE_AROUND = "around";

    public static final String SEARCH_TYPE_WAY = "way";

    public static final String FAVORITE = "favorite";

    public static final String FREQUENTLY = "frequently";

    public static final String FAVORITE_CH = "收藏";

    public static final String FREQUENTLY_CH = "常用";

    public static final int MAX_VIA_POINTS_SIZE = 15;

    public static final String POI_TAG_PROVINCE = "省级";
    public static final String POI_TAG_CITY = "市级";
    public static final String POI_TAG_AREA = "区县级";

    public static final String NAVI_MODE = "navi_mode";
    public static final String NAVI_MODE_NORMAL = "normal";
    public static final String NAVI_MODE_LANE = "lane";

    public static final String LOGIN_IN = "login";
    public static final String LOGIN_OUT = "logout";

    public interface ThemeType {
        int MAP_THEME_AUTO = 0;

        int MAP_THEME_DAY = 1;

        int MAP_THEME_NIGHT = 2;
    }

    public interface RoadType {
        int MAIN_ROAD_VALUE = 1;

        int SIDE_ROAD_VALUE = 2;

        int ON_ELEVATED_VALUE = 4;

        int UNDER_ELEVATED_VALUE = 8;
    }

    public interface NaviStatueType {
        int NAVIGATION_NOT_STARTED = 0;
        int NAVIGATION_ROUTE_PLANNING = 1;
        int NAVIGATION_STARTED = 2;
        int NAVIGATION_FAMILIAR = 3;
    }

    public interface MapViewType {
        int MAP_VIEW_MODE_2D_CAR_UP = 4;

        int MAP_VIEW_MODE_2D_NORTH_UP = 1;

        int MAP_VIEW_MODE_3D_CAR_UP = 0;

        static String getMapViewDesc(int view) {
            if (view == MAP_VIEW_MODE_3D_CAR_UP) {
                return "3D车头朝上";
            }
            if (view == MAP_VIEW_MODE_2D_CAR_UP) {
                return "2D车头朝上";
            }
            if (view == MAP_VIEW_MODE_2D_NORTH_UP) {
                return "2D正北朝上";
            }
            return "";
        }
    }

    public interface SpeakModeType {
        int SPEAK_MODE_BRIEF = 1;
        int SPEAK_MODE_SILENT = 2;
        int SPEAK_MODE_PROMPT = 3;
        int SPEAK_MODE_DETAIL = 4;

        static String getSpeakModeDesc(int view) {
            if (view == SPEAK_MODE_BRIEF) {
                return "简洁播报模式";
            }
            if (view == SPEAK_MODE_PROMPT) {
                return "提示音播报模式";
            }
            if (view == SPEAK_MODE_DETAIL) {
                return "详细播报模式";
            }
            return "";
        }
    }

    public interface FavoritesType {
        int FAVORITES = 0;
        int HOME = 1;
        int COMPANY = 2;
        int FREQUENTLY = 3;
    }


    public interface SearchType {
        int SEARCH_TYPE_NORMAL = 0;
        int SEARCH_TYPE_HOME = 1;
        int SEARCH_TYPE_COMPANY = 2;
        int SEARCH_TYPE_VIA_POINT = 4;
    }

    public interface SearchUpdateType {
        int SEARCH_TYPE_NORMAL = 0;
        int SEARCH_TYPE_AROUND = 1;
        int SEARCH_TYPE_HOME = 3;
        int SEARCH_TYPE_COMPANY = 2;
        int SEARCH_TYPE_VIA_POINT = 6;
        int SEARCH_TYPE_ON_WAY = 7;
    }


    public interface TeamType {
        int TEAM_NO = 0;
        int TEAM_CAPTAIN = 1;
        int TEAM_MEMBER = 2;
    }

    public interface FavoriteErrorCode {
        int SUCCESS = 0;
        int HOME_EMPTY = -1;
        int COMPANY_EMPTY = -2;
        int NO_PERMISSION_HOME = -3;
        int NO_PERMISSION_COMPANY = -4;
    }

    public interface ErrCode {
        int UNKNOWN = -1;
        int SUCCESS = 0;
        int INVALID_PARAM = 1;
        int ACTION_REQUESTING = 2;
        int JSON_ERROR = 3;
        int NOT_INIT = 4;
        int ALREADY_MAX = 5;
        int ALREADY_MIN = 6;
        int ALREADY_OPEN = 7;
        int ALREADY_CLOSE = 8;
        int CANNOT_OPT = 9;
        int NEED_LOGIN = 10;
        int RESULT_NULL = 11;
        int NO_NAVI = 20;
        int ERR_OPT = 21;
        int NOT_ROUTE = 30;
        int NOT_ALLOW = 31;
        int CANNOT_CHANGE = 40;
        int ALREADY_MAP_VIEW_2D_NORTH_UP = 41;
        int ALREADY_MAP_VIEW_3D = 42;
        int ALREADY_MAP_VIEW_2D_CAR_UP = 43;
        int SEARCH_NULL = 50;
        int NEED_LICENSE_PLATE = 58;
        int VIA_POINT_FULL = 61;
        int VIA_POINT_ALREADY_EXIT = 62;
        int VIA_POINT_NOT_EXIT = 63;
        int USER_CANCEL = 71;
        int NO_OPT_SEE = 100;
        int VIEW_NO_OPT = 101;
        int VIEW_NO_SLIDE = 102;
        int VIEW_NO_SUPPORT = 103;
        int NO_PERMISSION = 104;
        int ALREADY_TOP = 110;
        int ALREADY_BOTTOM = 111;
        int ALREADY_LANE_LEVEL_NAVIGATION = 310;
        int ALREADY_NORMAL_NAVIGATION = 312;
        int NOT_SUPPORT_TO_LANE_LEVEL_NAVIGATION = 311;
        int END_POINT_NULL = 314;
        int TOO_MANY_VIA_POINTS = 401;
    }

}
