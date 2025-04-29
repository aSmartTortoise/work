package com.voyah.ai.voice.agent.navi;

import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.navi.NaviConstants;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.device.navi.bean.NaviResponse;
import com.voice.sdk.device.navi.bean.Poi;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NaviFavoriteUtils {
    private static final String TAG = "NaviFavoriteUtils";

    public static ClientAgentResponse favoritePoi(Map<String, Object> flowContext, Poi poi) {
        NaviResponse<String> naviResponse2 = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().setFavoritesPoi(NaviConstants.FavoritesType.FAVORITES, poi);
        if (naviResponse2.isSuccess()) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, String.format(Locale.getDefault(), "%s已收藏", poi.getName()));
        } else {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100011));
        }
    }

    public static ClientAgentResponse deleteAll(Map<String, Object> flowContext) {
        if (DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isHideInformation()) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, "请关闭信息隐藏后，再试试手动操作");
        }
        NaviResponse<List<Poi>> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getFavoritesPoiList(NaviConstants.FavoritesType.FAVORITES, true);
        if (naviResponse.getData() == null || naviResponse.getData().isEmpty()) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3001401));
        }
        if (DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation()) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, "请退出导航后，再试试手动操作。");
        }
        DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchNaviFavoritesPage(true);
        return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, "请确认后，试试手动操作。");
    }


    public static ClientAgentResponse clearHistoryInfo(Map<String, Object> flowContext) {
        if (DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isHideInformation()) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, "信息隐藏已开启，无法进行该操作。");
        }
        NaviResponse<List<Poi>> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getHistoryPoiList(0);
        if (naviResponse == null || naviResponse.getData() == null || naviResponse.getData().isEmpty()) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, "当前没有导航记录哦");
        }
        NaviResponse<String> deleteNaviRes = DeviceHolder.INS().getDevices().getNavi().getNaviSetting().clearHistoryInfo();
        if (deleteNaviRes != null && deleteNaviRes.isSuccess()) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, "已清空导航历史记录。");
        } else {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));

        }
    }
}
