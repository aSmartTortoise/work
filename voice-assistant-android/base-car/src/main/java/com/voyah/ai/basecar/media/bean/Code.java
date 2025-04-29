package com.voyah.ai.basecar.media.bean;


public enum Code {
    /**
     * 0:请求成功
     * 100:网络异常
     * 101:服务器异常
     * 102:服务内部异常
     * 103:音频焦点申请失败
     * 104:没有此功能
     * 105:数据为空
     * 106:ipc请求超时
     * 107:请求参数有误
     * 108:播放失败
     */
    RGEAR(-3, "R档"),
    PLAYING(-2, "播放中"),
    FAILED(-1, "失败"),
    SUCESS(0, "成功"),
    NETERROR(100, "网络异常"),
    SERVICE_ERROR(101, "服务器异常"),
    INTERNAL_ERROR(102, "内部异常"),
    NO_AUDIOFORCE(103, "音频焦点申请失败"),
    NO_ATTRIBUTE(104, "没有此功能"),
    NO_DATA(105, "数据为空"),
    ICP_TIMEOUT(106, "请求超时"),
    PARAM_ERROR(107, "请求参数有误"),
    PLAY_ERROR(108, "播放失败"),
    NOT_LOGIN(109, "账户未登录"),
    NOT_VIP(110, "需要VIP会员"),
    NO_NEXT_LIVE(111, "没有更多电台了"),
    BT_DISCONNECT(112, "蓝牙未连接"),
    USB_DISCONNECT(113, "USB未连接"),
    NO_PREV_DATA(114, "当前已经是第一个"),
    NO_NEXT_DATA(115, "当前已经是最后一个"),
    NO_SONG_CAN_PALY(116, "没可播放的歌曲"),
    NOT_PAY(117, "单曲需要付费"),

    /**********************************语音相关code*****************************************/
    VOICE_ISLOGIN(210, "已登录,无法操作"),
    VOICE_PAGEISEXIST(211, "页面已被打开,无法操作"),
    VOICE_PAGENOTEXIST(212, "页面已关闭,无法操作"),

    /**********************************蓝牙音乐相关code*****************************************/
    MS_DISCONNECT(301, "mediasession断开连接"),

    /**********************************QQ音乐code*****************************************/
    PlAYER_API_IS_NULL(402, "PlayerApi为空"),
    SONG_CAN_NOT_PLAY(403, "歌曲无法播放"),
    QUALITY_CHANGE_ERROR(404, "切换音质失败"),
    QQ_COLLECTED(405, "QQ收藏"),
    QQ_NOT_COLLECTED(406, "QQ取消收藏"),
    QQ_HAS_COLLECTED(407, "QQ已收藏"),
    QQ_HAS_NOT_COLLECTED(408, "QQ已取消收藏"),

    /**********************************Wy音乐code*****************************************/

    WY_COLLECTED(605, "网易收藏"),
    WY_NOT_COLLECTED(606, "网易取消收藏"),
    WY_HAS_COLLECTED(607, "网易已收藏"),
    WY_HAS_NOT_COLLECTED(608, "网易已取消收藏"),
    SONG_NOT_PLAY_VISIBLE(601, "歌曲无版权"),
    SONG_CAN_NOT_PLAY_WY(602, "歌曲不支持车机端播放"),
    VOICE_SONG_CAN_NOT_PLAY(1,"因合作商要求，这首歌仅支持手机端收听，现在为你推荐其他音乐。"),
    VOICE_SONG_NOT_PLAY_VISIBLE(2,"因合作方要求，该资源暂时无法收听，现在为你推荐其他音乐。"),
    VOICE_SONG_NOT_FOUND(3,"没找到该歌曲，为你推荐其他音乐。"),

    XM_COLLECTED(705, "网易收藏"),
    XM_NOT_COLLECTED(706, "网易取消收藏"),
    XM_HAS_COLLECTED(707, "网易已收藏"),
    XM_HAS_NOT_COLLECTED(708, "网易已取消收藏"),

    YT_COLLECTED(505, "网易收藏"),
    YT_NOT_COLLECTED(506, "网易取消收藏"),
    YT_HAS_COLLECTED(507, "网易已收藏"),
    YT_HAS_NOT_COLLECTED(508, "网易已取消收藏");


    private int code;
    private String msg;

    Code(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int code() {
        return code;
    }

    public String msg() {
        return msg;
    }

    public static Code getCode(int code) {
        for (Code c : Code.values()) {
            if (c.code() == code) {
                return c;
            }

        }
        return INTERNAL_ERROR;
    }

    public boolean equals(Code code) {
        return code.code() == this.code;
    }

    @Override
    public String toString() {
        return "Code{" +
                "code=" + code +
                '}';
    }
}
