package com.voyah.ai.voice.agent.navi;


import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.navi.NaviConstants;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.device.navi.bean.NaviResponse;
import com.voice.sdk.device.navi.bean.NluPoi;
import com.voice.sdk.device.navi.bean.Poi;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.common.utils.GsonUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.generic.BaseAgentX;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NaviViaPoiUtils {
    private static final String TAG = "NaviViaPoiUtils";

    private static final Set<String> KEY_WORD_ON_WAY_SEARCH = new HashSet<>();

    static {
        KEY_WORD_ON_WAY_SEARCH.add("充电站");
        KEY_WORD_ON_WAY_SEARCH.add("充电");
        KEY_WORD_ON_WAY_SEARCH.add("服务区");
    }


    public static Pair<Integer, Poi> toPoi(String poiStr) {
        if (!poiStr.startsWith("[")) {
            poiStr = "[" + poiStr + "]";
        }
        List<NluPoi> nluPoiList = GsonUtils.fromJson(poiStr, new TypeToken<List<NluPoi>>() {
        }.getType());
        LogUtils.i(TAG, "nluPoiList:" + GsonUtils.toJson(nluPoiList));
        if (nluPoiList != null && nluPoiList.size() > 1) {
            return new Pair<>(NaviConstants.ErrCode.TOO_MANY_VIA_POINTS, null);
        }
        NluPoi nluPoi = nluPoiList != null && !nluPoiList.isEmpty() ? nluPoiList.get(0) : null;
        LogUtils.i(TAG, "nluPoi:" + GsonUtils.toJson(nluPoi));
        Poi poi = null;
        if (nluPoi == null) {
            return new Pair<>(NaviConstants.ErrCode.SUCCESS, null);
        }
        if (nluPoi.isCompany() || nluPoi.isHome()) {
            int type = nluPoi.isCompany() ? NaviConstants.FavoritesType.COMPANY : NaviConstants.FavoritesType.HOME;
            NaviResponse<List<Poi>> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getFavoritesPoiList(type, true);
            if (naviResponse != null && naviResponse.getResultCode() == NaviConstants.ErrCode.NO_PERMISSION) {
                return new Pair<>(NaviConstants.ErrCode.NO_PERMISSION, null);
            }
            if (naviResponse != null && naviResponse.isSuccess() && naviResponse.getData() != null && !naviResponse.getData().isEmpty()) {
                poi = naviResponse.getData().get(0);
            }
        } else {
            poi = new Poi();
            poi.setName(nluPoi.getKeyword());
        }
        return new Pair<>(NaviConstants.ErrCode.SUCCESS, poi);
    }

    public static List<Poi> getMatchViaPoiList(Poi poi) {
        List<Poi> viaPoiList = DeviceHolder.INS().getDevices().getNavi().getNaviViaPoint().getViaPoints();
        List<Poi> result = new ArrayList<>();
        if (poi == null) {
            return result;
        }
        if (viaPoiList != null && !viaPoiList.isEmpty()) {
            for (Poi p : viaPoiList) {
                if (p.getId() != null && p.getId().equals(poi.getId())) {
                    result.add(p);
                } else if (p.getName() != null && p.getName().contains(poi.getName())) {
                    result.add(p);
                } else if (p.getAddress() != null && p.getAddress().contains(poi.getName())) {
                    result.add(p);
                } else if (p.getCustomName() != null && p.getCustomName().contains(poi.getName())) {
                    result.add(p);
                }
            }
        }
        return result;
    }


    public static ClientAgentResponse addViaPoint(Map<String, Object> flowContext, Poi poi, int index) {
        if (!DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation() && !DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isPlanRoute()) {
            return new ClientAgentResponse(NaviResponseCode.SELECT_ILLEGAL_INDEX.getValue(), flowContext, TtsBeanUtils.getTtsBean(3004005));
        }
        if (DeviceHolder.INS().getDevices().getNavi().getNaviViaPoint().isViaPointsFull()) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3004000));
        }
        if (DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isPlanRoute()) {
            NaviResponse<String> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviViaPoint().addViaPoint(index);
            DeviceHolder.INS().getDevices().getNavi().getNaviSetting().setPreView(true, true);
            if (naviResponse.isSuccess()) {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3004007, poi.getName()));
            } else if (naviResponse.getResultCode() == NaviConstants.ErrCode.VIA_POINT_FULL) {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3004000));
            } else if (naviResponse.getResultCode() == NaviConstants.ErrCode.VIA_POINT_ALREADY_EXIT) {
                return new ClientAgentResponse(NaviResponseCode.SELECT_ILLEGAL_INDEX.getValue(), flowContext, TtsBeanUtils.getTtsBean(3004006));
            } else {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3015500));
            }
        } else {
            NaviResponse<String> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviViaPoint().addViaPoint(poi);
            DeviceHolder.INS().getDevices().getNavi().getNaviSetting().setPreView(true, true);
            if (naviResponse.isSuccess()) {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3004007, poi.getName()));
            } else if (naviResponse.getResultCode() == NaviConstants.ErrCode.VIA_POINT_FULL) {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3004000));
            } else if (naviResponse.getResultCode() == NaviConstants.ErrCode.VIA_POINT_ALREADY_EXIT) {
                return new ClientAgentResponse(NaviResponseCode.SELECT_ILLEGAL_INDEX.getValue(), flowContext, TtsBeanUtils.getTtsBean(3004006));

            } else {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3015500));
            }
        }
    }

    public static ClientAgentResponse deleteViaPoint(Map<String, Object> flowContext, List<Poi> viaPoints, String keyWord) {
        if (viaPoints.isEmpty()) {
            LogUtils.i(TAG, "deleteViaPoint:" + keyWord);
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3004501, keyWord));
        } else {
            boolean result = DeviceHolder.INS().getDevices().getNavi().getNaviViaPoint().deleteViaPoints(viaPoints);
            DeviceHolder.INS().getDevices().getNavi().getNaviSetting().setPreView(true, true);
            if (result) {
                TTSBean ttsBean;
                if (keyWord != null && !keyWord.isEmpty()) {
                    ttsBean = TtsBeanUtils.getTtsBean(3004302, keyWord);
                } else {
                    ttsBean = TtsBeanUtils.getTtsBean(3004302, viaPoints.get(0).getName());
                }
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, ttsBean);
            } else {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3015400));
            }
        }

    }

    public static ClientAgentResponse searchViaPoint(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap) {
        if (DeviceHolder.INS().getDevices().getNavi().getNaviViaPoint().isViaPointsFull()) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3004000));
        }
        if (!DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation() && !DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isPlanRoute()) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3004005));
        }

        String viaPoiStr = BaseAgentX.getParamKey(paramsMap, NaviConstants.VIA_POI, 0);
        if (viaPoiStr.isEmpty()) {
            return new ClientAgentResponse(NaviResponseCode.ADD_VIA_POI_ADDRESS_EMPTY.getValue(), flowContext, TtsBeanUtils.getTtsBean(3004001));
        } else {
            Pair<Integer, Poi> pair = NaviViaPoiUtils.toPoi(viaPoiStr);
            if (pair.first == NaviConstants.ErrCode.TOO_MANY_VIA_POINTS) {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));

            }
            if (pair.first == NaviConstants.ErrCode.NO_PERMISSION) {
                return new ClientAgentResponse(NaviResponseCode.NO_PERMISSION.getValue(), flowContext, TtsBeanUtils.getTtsBean(3001202));
            }
            Poi poi = pair.second;
            if (poi == null) {
                TTSBean tts;
                if (viaPoiStr.contains(NaviConstants.HOME)) {
                    tts = TtsBeanUtils.getTtsBean(3005100, NaviConstants.HOME);
                } else {
                    tts = TtsBeanUtils.getTtsBean(3005100, NaviConstants.COMPANY);
                }
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, tts);

            }
            if (poi.getId() != null) {
                return NaviViaPoiUtils.addViaPoint(flowContext, poi, 0);

            } else {
                NaviResponse<List<Poi>> naviResponse;
                if (KEY_WORD_ON_WAY_SEARCH.contains(poi.getName())) {
                    naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviPoi().searchOnWay(poi.getName(), true);
                } else {
                    naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviPoi().searchPoi(poi.getName(), NaviConstants.SearchType.SEARCH_TYPE_VIA_POINT, -1);
                }
                if (naviResponse.isSuccess() && naviResponse.getData() != null && naviResponse.getData().size() > 1) {
                    return new ClientAgentResponse(NaviResponseCode.ADD_VIA_POI_ADDRESS_RESULT_MULTIPLE.getValue(), flowContext, TtsBeanUtils.getTtsBean(3004002));

                } else if (naviResponse.isSuccess() && naviResponse.getData() != null && naviResponse.getData().size() == 1) {
                    return new ClientAgentResponse(NaviResponseCode.ADD_VIA_POI_ADDRESS_RESULT_ONE.getValue(), flowContext, TtsBeanUtils.getTtsBean(3004003));
                } else if (naviResponse.getResultCode() == NaviConstants.ErrCode.USER_CANCEL) {
                    return new ClientAgentResponse(NaviResponseCode.ADD_VIA_POI_ADDRESS_RESULT_EMPTY.getValue(), flowContext, "");
                } else {
                    return new ClientAgentResponse(NaviResponseCode.ADD_VIA_POI_ADDRESS_RESULT_EMPTY.getValue(), flowContext, TtsBeanUtils.getTtsBean(3004004));
                }

            }

        }
    }

}
