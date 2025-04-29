package com.voice.sdk.tts;

/**
 * @author:lcy
 * @data:2024/3/13
 **/
public class TTSAnsConstant {

    public static final String VALUE_SCREEN_CENTRAL = "中控屏";
    public static final String VALUE_SCREEN_PASSENGER = "副驾屏";
    public static final String VALUE_SCREEN_CEIL = "吸顶屏";

    public static final String[] PHONE_ASK_DIAL = new String[]{"5000004", "你想打给谁。"};
    public static final String[] PHONE_ASK_SEARCH = new String[]{"5000012", "你想查找谁的联系人信息。"};
    public static final String[] PHONE_CONNECT_BT = new String[]{"5000000", "请先连接手机蓝牙。"};
    public static final String[] PHONE_SYNC_BOOK = new String[]{"5000002", "请先同步通讯录。"};
    public static final String[] PHONE_SYNCING = new String[]{"5000003", "正在同步通讯录，请稍等。"};
    public static final String[] PHONE_FIND_FAIL_NUMBER = new String[]{"5000017", "没有找到符合条件的电话。"}; //重拨

    public static final String[] PHONE_NOT_FIND = new String[]{"5000009", "抱歉，我没有找到相关号码。"}; //指定、选择

    public static final String[] PHONE_REDIAL = new String[]{"5000016", "即将呼叫@{tel_name}@{tel_number}。"};
    public static final String[] PHONE_CONTACT_INVALID = new String[]{"5000008", "没找到对应的电话号码。"}; //没找到@{tel_name}的电话号码。
    public static final String[] PHONE_DIAL_FIND_MORE_NAME = new String[]{"5000006", "找到多个相似联系人，您想拨打第几个？"};
    public static final String[] PHONE_FIND_MORE_NUMBER = new String[]{"5000014", "找到多个电话，您想拨打第几个。"};
    public static final String[] PHONE_OUT_OF_SELECT_RANG = new String[]{"5000007", "超出有效数字范围了。"};

    public static final String PHONE_NO_TASK = "当前还没有通话哦";
    public static final String PHONE_R_FORBIDDEN = "出于安全考虑，当前场景不支持此操作。";

    public static final String[] PHONE_CALL_NUMBER_FRONT = new String[]{"5000010", "仅支持电话号码前三位或前四位拨打。"};
    public static final String[] PHONE_CALL_NUMBER_END = new String[]{"5000011", "仅支持电话号码后三位或后四位拨打。"};
    public static final String[] PHONE_CALL_REDIAL_NAME = new String[]{"5000005", "即将呼叫@{tel_name}。"};
    public static final String[] PHONE_CALL_REDIAL = new String[]{"5000001", "即将呼叫@{tel_number}，确定还是取消？"};
    public static final String[] PHONE_SEARCH_ASK_NAME = new String[]{"5000015", "@{tel_name}的电话为@{tel_number}，确定立即呼叫吗？"};
    public static final String[] PHONE_SEARCH_ASK_NUMBER = new String[]{"5000013", "是@{tel_name}的电话，确定帮您呼叫吗。"};
    public static final String[] PHONE_OPEN_CONFIRM = new String[]{"1100005", "好的"};
    public static final String[] PHONE_UI_CLOSE = new String[]{"1100003", "关闭蓝牙电话了。"};
    public static final String[] PHONE_UI_CLOSE_SCREEN_CLOSE = new String[]{"1100032", "关闭蓝牙电话了。"};
    public static final String[] PHONE_UI_CLOSE_SCREEN = new String[]{"1100031", "关闭蓝牙电话了。"};
    public static final String[] LAST_PAGE = new String[]{"5000030", "已经是最后一页了。"};
    public static final String[] FIRST_PAGE = new String[]{"5000029", "已经是第一页了。"};

    public static final String[] SCROLL_PAGE = new String[]{"4021302", "已经是第@{media_num}页了。"};
    public static final String[] PHONE_OPEN_ALREADY = new String[]{"1100001", "蓝牙电话已打开。"};
    public static final String[] PHONE_OPEN_ALREADY_SCREEN = new String[]{"1100029", "蓝牙电话已打开。"};
    public static final String[] PHONE_OPEN = new String[]{"1100002", "蓝牙电话打开了。"};
    public static final String[] PHONE_OPEN_SCREEN = new String[]{"1100030", "蓝牙电话打开了。"};
    public static final String[] PHONE_OPEN_MOT_SUPPORT = new String[]{"1100034", "当前位置暂不支持打开操作哦。"};
    public static final String[] CAR_CONFIGURATION_SUPPORT = new String[]{"1100028", "抱歉，车辆不支持此配置。"};
    public static final String[] CENTRAL_SCREEN_PHONE_ALREADY_OPEN= new String[]{"1100029", "中控蓝牙电话已打开。"};
    public static final String[] CENTRAL_SCREEN_PHONE_ALREADY_CLOSE= new String[]{"1100004", "蓝牙电话关着呢。"};
    public static final String[] PHONE_OPEN_MOT_SUPPORT_CLOSE = new String[]{"1100035", "当前位置暂不支持关闭操作哦。"};
    public static final String[] PHONE_CLOSE_ALREADY = new String[]{"1100004", "蓝牙电话已关闭。"};
    public static final String[] PHONE_OPEN_ADDRESS_BOOK = new String[]{"5000022", "打开通讯录了。"};
    public static final String[] PHONE_OPEN_CALL_LOG = new String[]{"5000024", "最近通话打开了。"};

    public static final String[] PHONE_ADDRESS_BOOK_OPEN_ALREADY = new String[]{"5000023", "通讯录已打开。"}; //无法获取状态
    public static final String[] PHONE_RECENT_CALL_OPEN_ALREADY = new String[]{"5000025", "最近通话已打开。"}; //无法获取状态
    public static final String[] TOP_PAGE = new String[]{"5000026", "已经滑到顶了。"}; //电话只有翻页
    public static final String[] BOTTOM_PAGE = new String[]{"5000028", "已经滑到底了。"}; //电话只有翻页

    public static final String[] UNDERSTAND = new String[]{"1100027", "对不起,我没有听懂,请重说。"};
    public static final String[] EXIT = new String[]{"1100008", "我先退下了。"};
    public static final String[] WAKE_UP = new String[]{"1100007", "@{position}请说。"};
    public static final String NOT_SUPPORT = "抱歉,暂不支持该功能,请稍后再试";
    public static final String GEAR_NOT_SUPPORT = "为了你的行车安全，R档不支持此操作。";
    public static final String PARK_NOT_SUPPORT = "出于安全考虑，当前场景不支持此操作。";
}
