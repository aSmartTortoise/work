package com.voice.sdk.device.carservice.dc.context;

public class DCContext {
    public static final Boolean IS_DEBUG_GRAPH = false;

    public static final String TAG_TIME_STATISTICS = "time_statistics";
    //当前执行的图，指令
    public static final String CURRENT_GRAPH_NAME = "current_graph_name";
    public static final String CONTEXT = "context";

    public static final String INST_RESULT = "result";//结果列表

    public static final String NODE_TAG = "node_tag";
    public static final String NODE_METHOD_NAME = "node_method_name";
    public static final String NODE_KEY_PARAMS = "node_key_params";
    public static final String MAP_KEY_BRANCH = "branch";//分支信息。
    public static final String MAP_KAY_POSITION_VALUE_MAP = "map_position_value";//菱形处理后的结果，位置对应的结果
    public static final String MAP_KEY_POSITION_VALUE_MAP_BOOLEAN = "map_position_value_boolean";//判断当前棱形是否经过矩形。进行数据清空
    public static final String MAP_KEY_POSITION_FLOW = "positions_flow";

    public static final String MAP_KEY_METHOD_POSITION = "method_position";//分支信息。
    public static final String MAP_KEY_HIT_TTS = "hit_tts";//分支信息。

    public static final String MAP_KEY_DOMAIN = "domain";//用来区分到底是哪个类里的方法
    //===========================需要移到客户端的数据=============
    //槽里算法的信息对应的key
    public static final String MAP_KEY_SLOT_IS_sound_source_position = "is_sound_source_position";//判断当前槽里的位置信息是否来自声源位置。
    public static final String MAP_KEY_SLOT_POSITION = "position";
    //方法params的key
    public static final String MAP_KEY_METHOD_PARAM_SWITCH = "switch";//
    public static final String MAP_KEY_SOUND_LOCATION = "sound_location";//轩明传下来的位置信息。

    //图里的key
    public static final String GRAPH_PARAMS_KEY_SPECIFIED_POSITION = "specified_position";//图中只包含那些位置信息的key.
    public static final String FLOW_KEY_ONE_POSITION = "one_position";
    public static final String GRAPH_PARAMS_KEY_METHOD_NAME_CUR = "method_name_cur";
    public static final String GRAPH_PARAMS_KEY_METHOD_NAME_EQ = "method_name_eq";

    public static final String INTENT = "device_intent";
    public static final String NLU_INFO = "nlu_info";
    //流程图中参数的上下文
    public static final String MAP_KEY_GRAPH_CONTEXT = "map_key_graph_context";
    //tts占位符需要替换的key
    public static final String TTS_PLACEHOLDER = "placeholder";

    public static final String SEAT_NUMBER = "seat_number";

    //判断当前函数是设置了对应的参数。
    public static final String IS_SET_VALUE = "is_set_value";
}
