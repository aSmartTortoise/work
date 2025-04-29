package com.voyah.ai.voice.agent.navi;


import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.navi.NaviConstants;
import com.voice.sdk.device.navi.NaviConvert;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.device.navi.bean.NaviResponse;
import com.voice.sdk.device.navi.bean.NaviRoutePlan;
import com.voice.sdk.device.navi.bean.NluPoi;
import com.voice.sdk.device.navi.bean.Poi;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.common.utils.GsonUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.generic.BaseAgentX;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;
import com.voyah.ds.common.entity.context.FlowContextKey;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NaviPoiUtils {
    private static final String TAG = "NaviPoiUtils";

    private static final Map<String, String> KEY_WORD_MAP = new HashMap<>();

    static {
        KEY_WORD_MAP.put("好玩", "休闲娱乐");
        KEY_WORD_MAP.put("好吃", "美食");
        KEY_WORD_MAP.put("住宿", "酒店");
        KEY_WORD_MAP.put("休息", "酒店");
    }


    public static ClientAgentResponse searchPoi(Map<String, Object> flowContext, Map<String, List<Object>> paramsMap, boolean directNavi) {
        String routeType = BaseAgentX.getParamKey(paramsMap, NaviConstants.ROUTE_TYPE, 0);
        String destPoiStr = BaseAgentX.getParamKey(paramsMap, NaviConstants.DEST_POI, 0);
        String viaListStr = BaseAgentX.getParamKey(paramsMap, NaviConstants.VIA_POI, 0);
        String newDestPoiStr = BaseAgentX.getParamKey(paramsMap, NaviConstants.NEW_DEST_POI, 0);
        if (destPoiStr.isEmpty()) {
            destPoiStr = newDestPoiStr;
        }
        int code = convertViaStr(viaListStr);
        DeviceHolder.INS().getDevices().getNavi().getNaviStash().setRouteType(routeType);
        if (code == NaviConstants.ErrCode.TOO_MANY_VIA_POINTS) {
            return new ClientAgentResponse(NaviResponseCode.FAVORITE_ADDRESS_EMPTY.getValue(), flowContext, TtsBeanUtils.getTtsBean(3015300));
        } else if (code == NaviConstants.FavoriteErrorCode.COMPANY_EMPTY) {
            if (!DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation()) {
                DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchNaviCommutePage(true);
            }
            return new ClientAgentResponse(NaviResponseCode.FAVORITE_ADDRESS_EMPTY.getValue(), flowContext, TtsBeanUtils.getTtsBean(3003801, NaviConstants.COMPANY));
        } else if (code == NaviConstants.FavoriteErrorCode.HOME_EMPTY) {
            if (!DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation()) {
                DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchNaviCommutePage(true);
            }
            return new ClientAgentResponse(NaviResponseCode.FAVORITE_ADDRESS_EMPTY.getValue(), flowContext, TtsBeanUtils.getTtsBean(3003801, NaviConstants.HOME));
        } else if (code == NaviConstants.FavoriteErrorCode.NO_PERMISSION_HOME || code == NaviConstants.FavoriteErrorCode.NO_PERMISSION_COMPANY) {
            String text = code == NaviConstants.FavoriteErrorCode.NO_PERMISSION_HOME ? NaviConstants.HOME : NaviConstants.COMPANY;
            return new ClientAgentResponse(NaviResponseCode.NO_PERMISSION.getValue(), flowContext, TtsBeanUtils.getTtsBean(3001104, text));
        }

        if (destPoiStr.isEmpty()) {
            return new ClientAgentResponse(NaviResponseCode.SEARCH_POI_DES_EMPTY.getValue(), flowContext, TtsBeanUtils.getTtsBean(3001000));
        } else {
            NluPoi destPoi = convertDestPoiStr(destPoiStr);
            if (destPoi != null) {
                if (KEY_WORD_MAP.containsKey(destPoi.getKeyword())) {
                    destPoi.setKeyword(KEY_WORD_MAP.get(destPoi.getKeyword()));
                }
            }
            if (destPoi != null && destPoi.isDestination()) {
                return NaviQueryUtils.queryDestination(flowContext, null);
            }
            if (destPoi == null || destPoi.getKeyword() == null || destPoi.getKeyword().isEmpty()) {
                if (!(destPoi != null && (NaviConstants.FREQUENTLY.equals(destPoi.getFavoriteType()) || NaviConstants.FAVORITE.equals(destPoi.getFavoriteType())))) {
                    return new ClientAgentResponse(NaviResponseCode.SEARCH_POI_DES_EMPTY.getValue(), flowContext, TtsBeanUtils.getTtsBean(3001000));
                }
            }
            Poi centerPoi = null;
            boolean missDestination = false;
            if (destPoi.getCenterPoi() != null && !destPoi.getCenterPoi().isEmpty()) {
                if (NaviConstants.HOME.equals(destPoi.getCenterPoi()) || NaviConstants.COMPANY.equals(destPoi.getCenterPoi())) {
                    int type = NaviConstants.HOME.equals(destPoi.getCenterPoi()) ? NaviConstants.FavoritesType.HOME : NaviConstants.FavoritesType.COMPANY;
                    NaviResponse<List<Poi>> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getFavoritesPoiList(type, true);
                    if (naviResponse.getResultCode() == NaviConstants.ErrCode.NO_PERMISSION) {
                        return new ClientAgentResponse(NaviResponseCode.NO_PERMISSION.getValue(), flowContext, TtsBeanUtils.getTtsBean(3001202));

                    } else {
                        if (naviResponse.isSuccess() && naviResponse.getData() != null && !naviResponse.getData().isEmpty()) {
                            centerPoi = naviResponse.getData().get(0);
                        } else {
                            if (!DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation()) {
                                DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchNaviCommutePage(true);
                            }
                            return new ClientAgentResponse(NaviResponseCode.FAVORITE_ADDRESS_EMPTY.getValue(), flowContext, TtsBeanUtils.getTtsBean(3003801, destPoi.getCenterPoi()));
                        }
                    }
                } else if (NaviConstants.DESTINATION.equals(destPoi.getCenterPoi())) {
                    if (DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation()) {
                        centerPoi = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getDestPoi();
                    } else {
                        return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3007102));
                    }
                } else {
                    centerPoi = new Poi();
                    centerPoi.setName(destPoi.getCenterPoi());
                }
            }
            if (destPoi.isCompany() || destPoi.isHome()) {
                int type = destPoi.isHome() ? NaviConstants.FavoritesType.HOME : NaviConstants.FavoritesType.COMPANY;
                NaviResponse<List<Poi>> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getFavoritesPoiList(type, true);
                if (naviResponse.getResultCode() == NaviConstants.ErrCode.NO_PERMISSION) {
                    TTSBean ttsBean = TtsBeanUtils.getTtsBean(3001104, destPoi.isHome() ? "回家" : "去公司");
                    return new ClientAgentResponse(NaviResponseCode.NO_PERMISSION.getValue(), flowContext, ttsBean);
                }
                if (naviResponse.isSuccess() && naviResponse.getData() != null && !naviResponse.getData().isEmpty()) {
                    Poi poi = naviResponse.getData().get(0);
                    return naviSelectPoi(flowContext, poi, DeviceHolder.INS().getDevices().getNavi().getNaviStash().getViaPoiList(), destPoi.getKeyword());

                } else {
                    DeviceHolder.INS().getDevices().getNavi().getNaviStash().setFavoriteType(destPoi.getKeyword());
                    return changeHomeOrCompany(flowContext, destPoi.getKeyword(), "");
                }
            } else if (NaviConstants.FAVORITE.equals(destPoi.getFavoriteType())) {
                int type = NaviConstants.FavoritesType.FAVORITES;
                String keyWord = destPoi.getKeyword();
                NaviResponse<List<Poi>> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getFavoritesPoiList(type, true);
                if (naviResponse.getResultCode() == NaviConstants.ErrCode.NO_PERMISSION) {
                    return new ClientAgentResponse(NaviResponseCode.NO_PERMISSION.getValue(), flowContext, TtsBeanUtils.getTtsBean(3001202));
                }
                if (keyWord == null || keyWord.isEmpty()) {
                    if (naviResponse.getData() == null || naviResponse.getData().isEmpty()) {
                        return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3001401));
                    } else {
                        if (DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation()) {
                            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, "需要您先退出导航后才能打开导航收藏夹");

                        } else {
                            DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchNaviFavoritesPage(true);
                            return new ClientAgentResponse(NaviResponseCode.SEARCH_POI_RESULT_MULTIPLE.getValue(), flowContext, TtsBeanUtils.getTtsBean(3001400));
                        }

                    }
                } else {
                    List<Poi> poiList = new ArrayList<>();
                    if (naviResponse.isSuccess() && naviResponse.getData() != null && !naviResponse.getData().isEmpty()) {
                        for (Poi poi : naviResponse.getData()) {
                            if (poi.containKeyWord(keyWord)) {
                                poiList.add(poi);
                            }
                        }
                    }
                    if (poiList.isEmpty()) {
                        NluPoi nluPoi = new NluPoi();
                        nluPoi.setKeyword(keyWord);
                        List<Object> list = new ArrayList<>();
                        list.add(GsonUtils.toJson(nluPoi));
                        paramsMap.put(NaviConstants.DEST_POI, list);
                        return searchPoi(flowContext, paramsMap, false);
                    } else if (poiList.size() > 1) {
                        if (DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation()) {
                            String tts = String.format(Locale.getDefault(), "查到您有多个收藏的%s，需要您先退出导航后才能打开地图收藏夹确认结果", keyWord);
                            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, tts);

                        } else {
                            DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchNaviFavoritesPage(true);
                            return new ClientAgentResponse(NaviResponseCode.SEARCH_POI_RESULT_MULTIPLE.getValue(), flowContext, "找到以下收藏地址，要去第几个？");
                        }
                    } else {
                        return naviSelectPoi(flowContext, poiList.get(0), DeviceHolder.INS().getDevices().getNavi().getNaviStash().getViaPoiList(), NaviConstants.FAVORITE);
                    }
                }
            } else if (NaviConstants.FREQUENTLY.equals(destPoi.getFavoriteType())) {
                int type = NaviConstants.FavoritesType.FREQUENTLY;
                String keyWord = destPoi.getKeyword();
                NaviResponse<List<Poi>> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getFavoritesPoiList(type, true);
                if (naviResponse.getResultCode() == NaviConstants.ErrCode.NO_PERMISSION) {
                    return new ClientAgentResponse(NaviResponseCode.NO_PERMISSION.getValue(), flowContext, TtsBeanUtils.getTtsBean(3001202));
                }
                if (keyWord == null || keyWord.isEmpty()) {
                    Object tts;
                    if (naviResponse.getData() == null || naviResponse.getData().isEmpty()) {
                        tts = TtsBeanUtils.getTtsBean(3011601);
                    } else {
                        if (!DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation()) {
                            DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchNaviCommutePage(true);
                            tts = TtsBeanUtils.getTtsBean(3015700, NaviConstants.FREQUENTLY_CH);
                        } else {
                            tts = "需要先结束导航才可以打开通勤页面哦";
                        }
                    }
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, tts);

                } else {
                    List<Poi> poiList = new ArrayList<>();
                    if (naviResponse.isSuccess() && naviResponse.getData() != null && !naviResponse.getData().isEmpty()) {
                        for (Poi poi : naviResponse.getData()) {
                            if (poi.containKeyWord(keyWord)) {
                                poiList.add(poi);
                            }
                        }
                    }
                    LogUtils.i(TAG, "poiList:" + GsonUtils.toJson(poiList));
                    if (!poiList.isEmpty()) {
                        return naviSelectPoi(flowContext, poiList.get(0), DeviceHolder.INS().getDevices().getNavi().getNaviStash().getViaPoiList(), NaviConstants.FREQUENTLY);
                    } else {
                        NluPoi nluPoi = new NluPoi();
                        nluPoi.setKeyword(keyWord);
                        List<Object> list = new ArrayList<>();
                        list.add(GsonUtils.toJson(nluPoi));
                        paramsMap.put(NaviConstants.DEST_POI, list);
                        return searchPoi(flowContext, paramsMap, false);
                    }
                }
            } else {
                if (directNavi || (DeviceHolder.INS().getDevices().getNavi().getNaviStash().getViaPoiList() != null && !DeviceHolder.INS().getDevices().getNavi().getNaviStash().getViaPoiList().isEmpty())) {
                    Poi poi = new Poi();
                    poi.setName(destPoi.getKeyword());
                    return naviSelectPoi(flowContext, poi, DeviceHolder.INS().getDevices().getNavi().getNaviStash().getViaPoiList(), null);
                }
                List<com.voyah.ds.common.entity.domains.navi.Poi> dsPoiList = new ArrayList<>();
                NaviResponse<List<Poi>> naviResponse;
                if (destPoi.isAroundSearch() || centerPoi != null || destPoi.getMaxDistance() > 0) {
                    naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviPoi().searchAroundPoi(destPoi.getKeyword(), centerPoi, destPoi.getMaxDistance() > 0 ? destPoi.getMaxDistance() : -1);
                } else if (destPoi.isWaySearch()) {
                    if (DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation()) {
                        naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviPoi().searchOnWay(destPoi.getKeyword(), false);
                    } else {
                        missDestination = true;
                        naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviPoi().searchAroundPoi(destPoi.getKeyword(), null, destPoi.getMaxDistance() > 0 ? destPoi.getMaxDistance() : -1);

                    }
                } else {
                    naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviPoi().searchPoi(destPoi.getKeyword(), NaviConstants.SearchType.SEARCH_TYPE_NORMAL, destPoi.getMaxDistance() > 0 ? destPoi.getMaxDistance() : -1);
                }
                if (naviResponse.isSuccess() && naviResponse.getData() != null) {
                    if (!naviResponse.getData().isEmpty()) {
                        for (Poi p : naviResponse.getData()) {
                            dsPoiList.add(NaviConvert.convertToDsPoi(p));
                        }
                    }
                    flowContext.put(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_NAVI_POIS_LIST, dsPoiList);
                    if (naviResponse.getData().size() > 1) {
                        if ("服务区".equals(destPoi.getKeyword()) && destPoi.isWaySearch() && DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation()) {
                            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3003300));
                        }
                        if (missDestination) {
                            return new ClientAgentResponse(NaviResponseCode.SEARCH_POI_RESULT_MULTIPLE.getValue(), flowContext, TtsBeanUtils.getTtsBean(3003004, destPoi.getKeyword()));
                        } else if (destPoi.isAroundSearch() || centerPoi != null || destPoi.getMaxDistance() > 0) {
                            return new ClientAgentResponse(NaviResponseCode.SEARCH_POI_RESULT_MULTIPLE.getValue(), flowContext, TtsBeanUtils.getTtsBean(3003501, destPoi.getKeyword()));
                        } else {
                            return new ClientAgentResponse(NaviResponseCode.SEARCH_POI_RESULT_MULTIPLE.getValue(), flowContext, TtsBeanUtils.getTtsBean(3003000));
                        }
                    } else if (naviResponse.getData().size() == 1) {
                        if ("服务区".equals(destPoi.getKeyword()) && destPoi.isWaySearch() && DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation()) {
                            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3003300));
                        }
                        if (missDestination) {
                            return new ClientAgentResponse(NaviResponseCode.SEARCH_POI_RESULT_ONE.getValue(), flowContext, TtsBeanUtils.getTtsBean(3003004, destPoi.getKeyword()));
                        } else if (destPoi.isAroundSearch() || centerPoi != null || destPoi.getMaxDistance() > 0) {
                            return new ClientAgentResponse(NaviResponseCode.SEARCH_POI_RESULT_ONE.getValue(), flowContext, TtsBeanUtils.getTtsBean(3003503, destPoi.getKeyword()));
                        } else if (destPoi.isWaySearch()) {
                            return new ClientAgentResponse(NaviResponseCode.SEARCH_POI_RESULT_ONE.getValue(), flowContext, TtsBeanUtils.getTtsBean(3003001, destPoi.getKeyword()));

                        } else {
                            return new ClientAgentResponse(NaviResponseCode.SEARCH_POI_RESULT_ONE.getValue(), flowContext, TtsBeanUtils.getTtsBean(3001603));
                        }
                    }
                } else if (naviResponse.getResultCode() == NaviConstants.ErrCode.USER_CANCEL) {
                    return new ClientAgentResponse(NaviResponseCode.SEARCH_POI_RESULT_EMPTY.getValue(), flowContext, "");
                }
                return new ClientAgentResponse(NaviResponseCode.SEARCH_POI_RESULT_EMPTY.getValue(), flowContext, TtsBeanUtils.getTtsBean(3001600));
            }
        }
    }

    public static NluPoi convertDestPoiStr(String destPoiStr) {
        LogUtils.i(TAG, "convertDestPoiStr:" + destPoiStr);
        if (!destPoiStr.startsWith("[")) {
            destPoiStr = "[" + destPoiStr + "]";
        }
        Type type = new TypeToken<List<NluPoi>>() {
        }.getType();
        List<NluPoi> nluPoiList = GsonUtils.fromJson(destPoiStr, type);
        if (nluPoiList != null && !nluPoiList.isEmpty()) {
            NluPoi nluPoi = nluPoiList.get(0);
            for (int i = 1; i < nluPoiList.size(); i++) {
                if (!TextUtils.isEmpty(nluPoiList.get(i).getKeyword())) {
                    nluPoi.setKeyword(nluPoi.getKeyword() + nluPoiList.get(i).getKeyword());
                }
            }
            return nluPoi;
        }
        return null;
    }

    public static int convertViaStr(String viaListStr) {
        DeviceHolder.INS().getDevices().getNavi().getNaviStash().clearStash();
        if (viaListStr == null || viaListStr.isEmpty()) {
            return NaviConstants.FavoriteErrorCode.SUCCESS;
        }
        if (!viaListStr.startsWith("[")) {
            viaListStr = "[" + viaListStr + "]";
        }
        Type type = new TypeToken<List<NluPoi>>() {
        }.getType();
        List<NluPoi> nluPoiList = GsonUtils.fromJson(viaListStr, type);
        List<Poi> listPoi = new ArrayList<>();
        if (nluPoiList != null && !nluPoiList.isEmpty()) {
            if (nluPoiList.size() > 2) {
                return NaviConstants.ErrCode.TOO_MANY_VIA_POINTS;
            }
            for (NluPoi nluPoi : nluPoiList) {
                if (NaviConstants.HOME.equals(nluPoi.getKeyword())) {
                    NaviResponse<List<Poi>> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getFavoritesPoiList(NaviConstants.FavoritesType.HOME, true);
                    if (naviResponse != null && naviResponse.getResultCode() == NaviConstants.ErrCode.NO_PERMISSION) {
                        return NaviConstants.FavoriteErrorCode.NO_PERMISSION_HOME;
                    }
                    if (naviResponse != null && naviResponse.isSuccess() && naviResponse.getData() != null && !naviResponse.getData().isEmpty()) {
                        naviResponse.getData().get(0).setCustomName(NaviConstants.HOME);
                        listPoi.add(naviResponse.getData().get(0));
                    } else {
                        return NaviConstants.FavoriteErrorCode.HOME_EMPTY;
                    }
                } else if (NaviConstants.COMPANY.equals(nluPoi.getKeyword())) {
                    NaviResponse<List<Poi>> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getFavoritesPoiList(NaviConstants.FavoritesType.COMPANY, true);
                    if (naviResponse != null && naviResponse.getResultCode() == NaviConstants.ErrCode.NO_PERMISSION) {
                        return NaviConstants.FavoriteErrorCode.NO_PERMISSION_COMPANY;
                    }
                    if (naviResponse != null && naviResponse.isSuccess() && naviResponse.getData() != null && !naviResponse.getData().isEmpty()) {
                        naviResponse.getData().get(0).setCustomName(NaviConstants.COMPANY);
                        listPoi.add(naviResponse.getData().get(0));
                    } else {
                        return NaviConstants.FavoriteErrorCode.COMPANY_EMPTY;
                    }

                } else {
                    Poi poi = new Poi();
                    poi.setName(nluPoi.getKeyword());
                    listPoi.add(poi);
                }
            }
        }
        DeviceHolder.INS().getDevices().getNavi().getNaviStash().setViaPoiList(listPoi);
        return NaviConstants.FavoriteErrorCode.SUCCESS;
    }


    public static ClientAgentResponse changeHomeOrCompany(Map<String, Object> flowContext, String favoritesType, String poiStr) {
        if (favoritesType == null || favoritesType.isEmpty()) {
            favoritesType = DeviceHolder.INS().getDevices().getNavi().getNaviStash().getFavoriteType();
        }
        if (DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isHideInformation()) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3011500));
        }
        if (DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation()) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3001102, favoritesType));
        }
        if (NaviConstants.COMPANY.equals(favoritesType) || NaviConstants.HOME.equals(favoritesType)) {
            DeviceHolder.INS().getDevices().getNavi().getNaviStash().setFavoriteType(favoritesType);
        } else {
            return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
        }
        NluPoi destPoi = GsonUtils.fromJson(poiStr, NluPoi.class);
        if (destPoi == null) {
            return new ClientAgentResponse(NaviResponseCode.FAVORITE_POI_EMPTY.getValue(), flowContext, TtsBeanUtils.getTtsBean(3001103, favoritesType));
        }
        NaviResponse<List<Poi>> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviPoi().searchPoi(destPoi.getKeyword(), NaviConstants.HOME.equals(favoritesType) ? NaviConstants.SearchType.SEARCH_TYPE_HOME : NaviConstants.SearchType.SEARCH_TYPE_COMPANY, -1);
        if (naviResponse != null && naviResponse.getData() != null && !naviResponse.getData().isEmpty()) {
            List<com.voyah.ds.common.entity.domains.navi.Poi> dsPoiList = new ArrayList<>();
            for (Poi p : naviResponse.getData()) {
                dsPoiList.add(NaviConvert.convertToDsPoi(p));
            }
            flowContext.put(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_NAVI_POIS_LIST, dsPoiList);
            if (naviResponse.getData().size() > 1) {
                return new ClientAgentResponse(NaviResponseCode.FAVORITE_POI_RESULT_MULTIPLE.getValue(), flowContext, TtsBeanUtils.getTtsBean(3011501, favoritesType));
            } else {
                return new ClientAgentResponse(NaviResponseCode.FAVORITE_POI_RESULT_ONE.getValue(), flowContext, TtsBeanUtils.getTtsBean(3011502, favoritesType));
            }
        } else {
            return new ClientAgentResponse(NaviResponseCode.FAVORITE_POI_RESULT_EMPTY.getValue(), flowContext, TtsBeanUtils.getTtsBean(3001600));

        }

    }

    public static ClientAgentResponse naviSelectPoi(Map<String, Object> flowContext, Poi poi, List<Poi> viaList, String favoriteType) {
        if (NaviConstants.HOME.equals(favoriteType)) {
            poi.setCustomName(NaviConstants.HOME);
        }
        if (NaviConstants.COMPANY.equals(favoriteType)) {
            poi.setCustomName(NaviConstants.COMPANY);
        }
        int routeType = NaviRoutePlan.fromValue(DeviceHolder.INS().getDevices().getNavi().getNaviStash().getRouteType()).getValue();
        DeviceHolder.INS().getDevices().getNavi().getNaviStash().clearStash();
        boolean isHomeOrCompany = NaviConstants.HOME.equals(favoriteType) || NaviConstants.COMPANY.equals(favoriteType);
        if (DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isDirectNavi()) {
            NaviResponse<List<String>> naviResponse = DeviceHolder.INS().getDevices().getNavi().routePlan(poi, viaList, routeType);
            if (naviResponse.isSuccess()) {
                Object tts = TtsBeanUtils.getTtsBean(1100005);
                if (isHomeOrCompany) {
                    tts = TtsBeanUtils.getTtsBean(3001101, NaviConstants.HOME.equals(favoriteType) ? "回家" : "去公司");
                } else if (!TextUtils.isEmpty(favoriteType)) {
                    tts = TtsBeanUtils.getTtsBean(3001101, poi.getShowName());
                }
                if (viaList != null && !viaList.isEmpty()) {
                    if (viaList.size() == 1) {
                        tts = TtsBeanUtils.getTtsBean(3004900, viaList.get(0).getShowName(), poi.getShowName());
                    } else {
                        tts = TtsBeanUtils.getTtsBean(3004900, viaList.get(0).getShowName() + "和" + viaList.get(1).getShowName(), poi.getShowName());
                    }
                }
                return new ClientAgentResponse(NaviResponseCode.START_NAVIGATION.getValue(), flowContext, tts);
            } else if (naviResponse.getResultCode() == NaviConstants.ErrCode.END_POINT_NULL) {
                return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, "抱歉，没有找到您想去的目的地");
            } else {
                return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3015100));
            }
        } else {
            NaviResponse<List<String>> naviResponse = DeviceHolder.INS().getDevices().getNavi().routePlan(poi, viaList, routeType);
            if (naviResponse.isSuccess()) {
                List<String> routeNames = new ArrayList<>(naviResponse.getData());
                flowContext.put(FlowContextKey.SC_KEY_PFREFIX + FlowContextKey.SC_ROUTE_NAMES, routeNames);
                Object tts;
                if (isHomeOrCompany) {
                    tts = TtsBeanUtils.getTtsBean(3001200, favoriteType);
                } else if (!TextUtils.isEmpty(favoriteType)) {
                    tts = TtsBeanUtils.getTtsBean(3001200, poi.getShowName());

                } else {
                    tts = TtsBeanUtils.getTtsBean(3000501);
                }
                if (viaList != null && !viaList.isEmpty()) {
                    if (viaList.size() == 1) {
                        tts = TtsBeanUtils.getTtsBean(3004900, viaList.get(0).getShowName(), poi.getShowName());
                    } else {
                        tts = TtsBeanUtils.getTtsBean(3004900, viaList.get(0).getShowName() + "和" + viaList.get(1).getShowName(), poi.getShowName());
                    }
                }
                return new ClientAgentResponse(NaviResponseCode.ROUTE_PLAN.getValue(), flowContext, tts);
            } else if (naviResponse.getResultCode() == NaviConstants.ErrCode.END_POINT_NULL) {
                return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, "抱歉，没有找到您想去的目的地");
            } else {
                return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3015100));
            }
        }
    }

    public static ClientAgentResponse viewSelectPoi(Map<String, Object> flowContext, int index) {
        DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchPoiDetailPage(index);
        return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(1100005));
    }


    public static ClientAgentResponse favoriteAddress(Map<String, Object> flowContext, Poi poi) {
        int type = getType();
        DeviceHolder.INS().getDevices().getNavi().getNaviStatus().setFavoritesPoi(type, poi);
        if (!DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation()) {
            DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchNaviCommutePage(true);
        }
        return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3011506, type == NaviConstants.FavoritesType.HOME ? NaviConstants.HOME : NaviConstants.COMPANY));
    }

    private static int getType() {
        int type;
        if (DeviceHolder.INS().getDevices().getNavi().getNaviStash().getFavoriteType() != null) {
            type = NaviConstants.HOME.equals(DeviceHolder.INS().getDevices().getNavi().getNaviStash().getFavoriteType()) ? NaviConstants.FavoritesType.HOME : NaviConstants.FavoritesType.COMPANY;
        } else {
            type = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getSearchType() == NaviConstants.SearchUpdateType.SEARCH_TYPE_HOME ? NaviConstants.FavoritesType.HOME : NaviConstants.FavoritesType.COMPANY;
        }
        return type;
    }
}
