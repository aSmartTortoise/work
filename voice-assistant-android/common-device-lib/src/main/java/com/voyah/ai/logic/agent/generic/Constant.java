package com.voyah.ai.logic.agent.generic;

import com.voyah.ds.common.entity.context.FlowContextKey;

/**
 * @author:lcy 电话模块上下文参数常量类
 * @data:2024/3/5
 **/
//todo:提出作为通用
public class Constant {
    //------------------------------------------场景
    public static final String SCENARIO_NAME = "State.CALL_CONTACT_NAME_OPTIONS";//联系人(名字)选择场景
    public static final String SCENARIO_NUMBER = "State.CALL_CONTACT_NUMBER_OPTIONS";//号码选择场景

    public static final String SCENARIO_CALL_CONFIRM = "State.CALL_OUT_CONFIRM"; //呼出确认

    //来电
    public static final String INCOMING_CALL = "State.INCOMING_CALL";

    //去电
    public static final String OUTGOING_CALL = "State.OUTGOING_CALL";

    public static final String SCENARIO_INIT = "State.INIT";//初始化

    public static final String CALL_INQUIRY = "State.CALL_INQUIRY"; //模糊意图拨打场景

    public static final String KEEP_SCENARIO = "@State";

    //------------------------------------------上下文中参数
    //call
    //联系人、黄页选项列表 List<ContactInfo>
    public static final String PARAMS_CONTACT_LIST = FlowContextKey.SC_KEY_PFREFIX + "callContactList";
    //电话号码列表 List<ContactNumberInfo>
    public static final String PARAMS_NUMBER_LIST = FlowContextKey.SC_KEY_PFREFIX + "callPhoneNumberList";
    //找到的唯一号码 ContactNumberInfo
    public static final String PARAMS_NUMBER = FlowContextKey.SC_KEY_PFREFIX + "callSinglePhoneNumber";
    //扎到的唯一联系人或黄页 ContactInfo
    public static final String PARAMS_NAME = FlowContextKey.SC_KEY_PFREFIX + "callSingleContact";

    public static final String PARAMS_SEARCH_TYPE = FlowContextKey.SC_KEY_PFREFIX + "searchType";
    //拨打或查询动作
    public static final String PARAMS_OPERATION_TYPE = FlowContextKey.SC_KEY_PFREFIX + "callOpDialOrQuery";

    public static final String PARAMS_DEVICE_SCENARIO_STATE = FlowContextKey.SC_KEY_PFREFIX + "deviceScenarioState";

    public static final String PARAMS_SC_SESSION_ID = FlowContextKey.SC_KEY_PFREFIX + "sessionId";

    // media params
    public static final String PARAMS_MEDIA_VIDEOS_LIST = FlowContextKey.SC_KEY_PFREFIX + "mediaVideosList";


////     无效指令计数
//    public static final String PARAMS_INVALID_COUNT = FlowContextKey.SC_KEY_PFREFIX + "invalidCount";

    //多轮交互询问tts保存key
    public static final String MULTI_INTERACTION = FlowContextKey.SC_KEY_PFREFIX + "multiInteractionTts";

    //是否为全时触发
    public static final String FC_IS_NO_WAKEUP_TIME = "isNoWakeupTime";

    //------------------------------------------slot中参数key

    public static final String SWITCH_TYPE = "switch_type";
    public static final String ADJUST_TYPE = "adjust_type";
    public static final String MEDIA_TYPE = "media_type";
    public static final String MEDIA_NAME = "media_name";
    public static final String MEDIA_SOURCE = "media_source";
    public static final String JUMP_TYPE = "jump_type";
    public static final String MEDIA_CONTROL_TYPE = "media_control_type";
    public static final String INQUIRY_CONTENT = "inquiry_content";
    public static final String COMMON_BOOL = "common_bool";
    public static final String PLAY_MODE = "play_mode";
    public static final String UI_NAME = "ui_name";
    public static final String SCREEN_NAME = "screen_name";
    public static final String POSITION = "position";
    public static final String PLAYER = "player";

    public static final String LEVEL = "level";

    public static final String SWITCH_THEME = "switch_theme";

    public static final String OPERATION_TYPE = "operation_type";

    public static final String APP_NAME = "app_name";

    public static final String CONTROL_TYPE = "control_type";

    public static final String OPERATION_SOURCE = "operation_source";

    public static final String NAME = "name";

    public static final String INDEX = "index";

    public static final String YELLOW_PAGE_LIST = "yellow_page_list";

    public static final String NUMBER = "number";

    public static final String NUMBER_FRONT = "number_front";

    public static final String NUMBER_END = "number_end";

    public static final String CHOOSE_TYPE = "choose_type";

    public static final String EVENT = "Event";

    public static final String OPEN = "open";

    public static final String CLOSE = "close";

    public static final String CHANGE = "change";

    public static final String DOWNLOAD = "download";

    public static final String INCREASE = "increase";

    public static final String DECREASE = "decrease";

    public static final String MAX = "max";

    public static final String MIN = "min";

    public static final String SET = "set";

    public static final String INDEX_TYPE = "index_type";

    public static final String SELECT_INDEX = "select_index";

    public static final String RELATIVE = "relative";

    public static final String ABSOLUTE = "absolute";

    public static final String START = "start";

    public static final String CANCEL = "cancel";

    public static final String RESUME = "resume";

    public static final String RETURN = "return";

    public static final String SLOT_NAME_DATE = "date";
    public static final String SLOT_NAME_DATE_RANGE = "date_range";
    public static final String SLOT_NAME_TIME = "time";
    public static final String SLOT_NAME_TIME_RANGE = "time_range";
    public static final String SLOT_NAME_LOCATION = "location";
    public static final String SLOT_NAME_EVENT = "event";
    public static final String SLOT_NAME_INDEX_TYPE = "index_type";

    public static final String OPERATION_UPDATE = "update";

    public static final String OPERATION_UNINSTALL = "uninstall";

    public static final String OPERATION_DOWNLOAD = "download";

    public static final String OPERATION_SEARCH = "search";

    public static final String MEDIA_POSITION = "position";


    //-------------------------------------------slot中value
    public static final String EVENT_WAKEUP = "Wakeup";
    public static final String EVENT_ASR_RESULT = "AsrResult";
    public static final String ASR_RESULT = "Result";
    public static final String SCENARIO_SCHEDULE_TIME_INQUIRY = "State.DC_SCHEDULE_TIME_INQUIRY";
    public static final String SCENARIO_SCHEDULE_EVENT_INQUIRY = "State.DC_SCHEDULE_EVENT_INQUIRY";


    //-----------------------------------------通用参数返回值范围
    public interface OperationType {
        String DIAL = "dial";
        String SEARCH = "search";
        String INSERT = "add";
        String DELETE = "delete";
        String UPDATE = "modify";
        String QUERY = "search";
    }

    public interface OperationSource {
        String CONTACT = "contact";
        String YELLOW_PAGE = "yellow_page";
    }

    public interface CommonResponseCode {
        int SUCCESS = 0;//通知ds query执行成功，结束会话
        int ERROR = -1;// 通知ds 异常情况，query未成功执行，结束会话
        int SECONDARY_INTERACTION = 20000;//通知ds，属于二次交互，缺少必要slot，同时上传场景值
    }


    public interface PhoneAgentResponseCode {
        int BT_STATE_NOT_SATISFY = -1;
        int SUCCESS = 0;
        int MULTIPLE_NUMBER = 1;
        int MULTIPLE_PERSON = 2;
        int ASK_CALL = 3;
        int NO_SEARCH_RESULT = 4;

        int KEEP_SCENE = 10000;
    }

    public interface MediaAgentResponseCode {
        int SUCCESS = 0;
        int MULTIPLE_VIDEO = 1;
    }

    public interface WeatherAgentResponseCode {
        int WEATHER_SEARCH_DATE = 200;
        int WEATHER_SEARCH_DATE_RANGE = 201;
        int WEATHER_APP_SWITCH = 202;
        int WEATHER_SEARCH_TIME = 203;
        int WEATHER_SEARCH_TIME_RANGE = 204;

        int WEATHER_TEM_DIFF_SEARCH_DAY = 205;
        int WEATHER_TEM_DIFF_SEARCH_DAY_RANGE = 206;
        int WEATHER_TEM_DIFF_SEARCH_TIME_RANGE = 207;
        int WEATHER_WIND_SEARCH_DATE = 208;
        int WEATHER_WIND_SEARCH_DATE_RANGE = 209;
        int WEATHER_WIND_SEARCH_TIME = 210;
        int WEATHER_WIND_SEARCH_TIME_RANGE = 211;

        int WEATHER_INTEREST_SEARCH_DATE = 212;
        int WEATHER_INTEREST_SEARCH_DATE_RANGE = 213;
        int WEATHER_INTEREST_SEARCH_TIME = 214;
        int WEATHER_INTEREST_SEARCH_TIME_RANGE = 215;

        int WEATHER_SUN_RISE_DOWN_SEARCH_DATE = 216;
        int WEATHER_SUN_RISE_DOWN_SEARCH_DATE_RANGE = 217;
        int WEATHER_SUN_RISE_DOWN_SEARCH_TIME = 218;
        int WEATHER_SUN_RISE_DOWN_SEARCH_TIME_RANGE = 219;

        int WEATHER_AQI_SEARCH_DATE = 220;
        int WEATHER_AQI_SEARCH_DATE_RANGE = 221;
        int WEATHER_AQI_SEARCH_TIME = 222;
        int WEATHER_AQI_SEARCH_TIME_RANGE = 223;

        int WEATHER_LIFE_INDEX_SEARCH_DATE = 224;
        int WEATHER_LIFE_INDEX_SEARCH_DATE_RANGE = 225;
        int WEATHER_LIFE_INDEX_SEARCH_TIME = 226;
        int WEATHER_LIFE_INDEX_SEARCH_TIME_RANGE = 227;

        int WEATHER_SEARCH_ERROR = 228;

    }

    public interface CalendarAgentResponseCode {
        int INSERT_SCHEDULE = 300;
        int DELETE_SCHEDULE = 301;
        int UPDATE_SCHEDULE = 302;
        int QUERY_SCHEDULE = 303;

        int CALENDAR_APP_SWITCH = 304;

        int SYSTEM_INFO_HIDING_OPEN = 305;

    }

    public interface UserCenterAgentResponseCode {
        int TO_LOGIN = 400;


    }

    public interface SkillStockAgentResponseCode {
        int STOCK_SEARCH = 500;


    }

    public interface LLMSearchAgentResponseCode {
        int LLM_SEARCH = 600;

        int LLM_DRAWING = 601;

        //LLM异步返回(流式非结束状态)
        int LLM_AYSNC_RETURN_CODE = 10001;
        //LLM耗时同步返回(流式结束状态)
        int LLM_LONG_TIME_SNC_RETURN_CODE = 10002;

    }
}
