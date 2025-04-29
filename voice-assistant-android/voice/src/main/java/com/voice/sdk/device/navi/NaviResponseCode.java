package com.voice.sdk.device.navi;

import androidx.annotation.NonNull;

public enum NaviResponseCode {

    ERROR(-1, null, -1, "通用错误,退出流程"),
    SUCCESS(0, null, -1, "成功,退出流程"),
    TEAM_DISBAND_CAPTAIN(1, "State.NAVI_DISBAND_TEAM_CONFIRM", -1, "队长,是否解散车队"),
    TEAM_EXIT_MEMBER(2, "State.NAVI_DISBAND_TEAM_CONFIRM", -1, "队员,是否退出车队"),
    SEARCH_POI_DES_EMPTY(3, "State.DEST_POI_INQUIRY", 3001000, "搜索POI,未指定POI,如我要去一个地方"),
    SEARCH_POI_RESULT_EMPTY(4, null, 3001600, "搜索POI,没有找到结果"),
    SEARCH_POI_RESULT_ONE(5, "State.NAVI_POI_ONE_OPTION", 3001603, "搜索POI,只找到一个结果"),
    SEARCH_POI_RESULT_MULTIPLE(6, "State.NAVI_POI_OPTIONS", 3003000, "搜索POI,找到多个结果"),

    SELECT_ILLEGAL_INDEX(7, null, -1, "选择场景,不管翻页还是选择第几个,只要计算后索引超出返回就返回此值,比如现在在最后一页,说下一页也返回此值"),
    ROUTE_PLAN(8, "State.NAVI_ROUTE_OPTIONS", 3000501, "进入路径规划选择"),
    START_NAVIGATION(9, null, -1, "开始导航"),
    PAGE_SELECT(10, null, -1, "翻页执行完毕"),
    UN_LOGIN(11, null, -1, "没有登录,该功能依赖用户先登录"),
    NO_PERMISSION(12, null, -1, "用户开启了信息隐藏,没有权限获取用户信息"),
    FAVORITE_ADDRESS_EMPTY(13, null, -1, "需要的地址为空,如家的地址,公司地址"),
    FAVORITE_POI_EMPTY(14, "State.SET_FAVORITE_INQUIRY", -1, "设置家或者公司的地址没有指定POI,如修改家的地址"),
    FAVORITE_POI_RESULT_EMPTY(15, null, -1, "设置家或者公司的地址,没有找到结果"),
    FAVORITE_POI_RESULT_ONE(16, "State.NAVI_FAVORITE_POI_ONE_OPTION", 3011502, "设置家或者公司的地址,只找到一个结果"),
    FAVORITE_POI_RESULT_MULTIPLE(17, "State.NAVI_FAVORITE_POI_OPTIONS", 3011501, "设置家或者公司的地址,找到多个结果"),
    ADD_VIA_POI_ADDRESS_EMPTY(18, "State.VIA_POI_INQUIRY", -1, "增加途径点没有说具体POI,如添加途径点"),
    ADD_VIA_POI_ADDRESS_RESULT_EMPTY(19, null, -1, "增加途径点,没有搜索到结果"),
    ADD_VIA_POI_ADDRESS_RESULT_ONE(20, "State.NAVI_ADD_VIA_POI_ONE_OPTION", 3004003, "增加途径点,搜索到一个结果"),
    ADD_VIA_POI_ADDRESS_RESULT_MULTIPLE(21, "State.NAVI_ADD_VIA_POI_OPTIONS", 3004002, "增加途径点,搜索到多个结果"),
    DELETE_VIA_POI_RESULT_MULTIPLE(22, "State.NAVI_DEL_VIA_POI_OPTIONS", -1, "删除途径点有多个结果,需要选择第几个"),
    CONTINUE_NAVI_CONFIRM(23, "State.CONTINUE_NAVI_CONFIRM", -1, "继续导航确认取消");


    private final int value;
    private final String des;

    private final int tts;

    private final String state;

    NaviResponseCode(int value, String state, int ttsId, String des) {
        this.value = value;
        this.des = des;
        this.tts = ttsId;
        this.state = state;
    }

    public int getValue() {
        return value;
    }

    public String getDes() {
        return des;
    }

    public String getState() {
        return state;
    }

    public int getTts() {
        return tts;
    }


    @NonNull
    @Override
    public String toString() {
        return getDes() + "_" + getState() + "_" + getValue();
    }

    public static boolean isNaviState(String state) {
        for (NaviResponseCode e : NaviResponseCode.values()) {
            if (e.getState() != null && e.getState().equals(state)) {
                return true;
            }
        }
        return false;
    }


}
