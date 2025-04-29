package com.voyah.ai.voice.receiver.voice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log2;

import androidx.annotation.NonNull;

import com.google.gson.reflect.TypeToken;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.GalleryInterface;
import com.voice.sdk.device.navi.NaviConstants;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.device.navi.bean.NaviResponse;
import com.voice.sdk.device.navi.bean.NaviRoutePlan;
import com.voice.sdk.device.navi.bean.NluPoi;
import com.voice.sdk.device.navi.bean.Poi;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.common.utils.ContextUtils;
import com.voyah.ai.common.utils.GpsUtils;
import com.voyah.ai.common.utils.GsonUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.agent.llm.LLMSearchAgent;
import com.voyah.ai.voice.agent.navi.NaviFavoriteUtils;
import com.voyah.ai.voice.agent.navi.NaviPoiUtils;
import com.voyah.ai.voice.agent.navi.NaviQueryUtils;
import com.voyah.ai.voice.agent.weather.WeatherAirQualitySearchAgent;
import com.voyah.ai.voice.agent.weather.WeatherInterestSearchAgent;
import com.voyah.ai.voice.agent.weather.WeatherSearchAgent;
import com.voyah.ai.voice.agent.weather.WeatherSunRiseDownSearchAgent;
import com.voyah.ai.voice.agent.weather.WeatherWindSearchAgent;
import com.voyah.ai.voice.sdk.api.task.AgentInfoHolder;
import com.voyah.ds.common.entity.context.FlowContextKey;
import com.voyah.ds.common.entity.domains.GpsLocation;
import com.voyah.ds.common.entity.domains.weather.MultiDaysWeather;
import com.voyah.ds.common.entity.domains.weather.MultiHoursWeather;
import com.voyah.ds.common.entity.domains.weather.OneDayWeather;
import com.voyah.ds.common.entity.domains.weather.OneHourWeather;
import com.voyah.ds.common.entity.domains.weather.WeatherInterest;
import com.voyah.ds.common.entity.domains.weather.WeatherType;
import com.voyah.ds.common.entity.nlu.NluInfo;
import com.voyah.ds.common.entity.nlu.Slot;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class FunctionTestBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "FunctionTestBroadcastReceiver";

    public FunctionTestBroadcastReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int testCase = intent.getIntExtra("testCase", 0);
        NaviResponse<?> naviResponse = null;
        ClientAgentResponse clientAgentResponse = null;
        Poi poi = GsonUtils.fromJson("{\"address\":\"北京市东城区长安街\",\"cityCode\":131,\"cityName\":\"北京市\",\"distance\":0.0,\"district\":\"\",\"id\":\"65e1ee886c885190f60e77ff\",\"lat\":39.90877595175356,\"lon\":116.39759035355206,\"name\":\"天安门\"}", Poi.class);
        String poiListStr = "[\n" +
                "    {\n" +
                "        \"address\": \"湖北省武汉市黄陂区长岭岗服务区(武麻高速公路南)\",\n" +
                "        \"cityCode\": 1289,\n" +
                "        \"distance\": 0,\n" +
                "        \"id\": \"d33f87b0971ff84da8175a7d\",\n" +
                "        \"index\": 0,\n" +
                "        \"lat\": 30.99211450327547,\n" +
                "        \"lon\": 114.54433875590128,\n" +
                "        \"name\": \"长岭岗服务区(沪蓉高速上海方向)\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"address\": \"信阳市息县大广高速\",\n" +
                "        \"cityCode\": 1692,\n" +
                "        \"distance\": 0,\n" +
                "        \"id\": \"fb8493d9b966512a4afd3fb3\",\n" +
                "        \"index\": 0,\n" +
                "        \"lat\": 32.47274675245547,\n" +
                "        \"lon\": 114.85639819274247,\n" +
                "        \"name\": \"息县服务区(大广高速大庆方向)\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"address\": \"新乡市封丘县\",\n" +
                "        \"cityCode\": 2775,\n" +
                "        \"distance\": 0,\n" +
                "        \"id\": \"e758cb3b69a2280f1894c6e9\",\n" +
                "        \"index\": 0,\n" +
                "        \"lat\": 35.0809014487857,\n" +
                "        \"lon\": 114.58536301347124,\n" +
                "        \"name\": \"封丘服务区\"\n" +
                "    }\n" +
                "]";
        Type type = new TypeToken<List<Poi>>() {
        }.getType();
        List<Poi> poiList = GsonUtils.fromJson(poiListStr, type);
        Log2.d(TAG, "onReceive: action:" + intent.getAction() + ",testCase:" + testCase);
        switch (testCase) {
            case 0:
                DeviceHolder.INS().getDevices().getNavi().openNaviApp();
                break;
            case 1:
                DeviceHolder.INS().getDevices().getNavi().closeNaviApp();
                break;
            case 2:
                DeviceHolder.INS().getDevices().getNavi().getNaviMap().setThemeStyle(NaviConstants.ThemeType.MAP_THEME_AUTO);
                break;
            case 3:
                DeviceHolder.INS().getDevices().getNavi().getNaviMap().setThemeStyle(NaviConstants.ThemeType.MAP_THEME_DAY);
                break;
            case 4:
                DeviceHolder.INS().getDevices().getNavi().getNaviMap().setThemeStyle(NaviConstants.ThemeType.MAP_THEME_NIGHT);
                break;
            case 5:
                DeviceHolder.INS().getDevices().getNavi().getNaviMap().zoomMap(true);
                break;
            case 6:
                DeviceHolder.INS().getDevices().getNavi().getNaviMap().zoomMap(false);
                break;
            case 7:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviPoi().locateSelf();
                break;
            case 8:
                DeviceHolder.INS().getDevices().getNavi().getNaviMap().maxMinMap(true);
                break;
            case 9:
                DeviceHolder.INS().getDevices().getNavi().getNaviMap().maxMinMap(false);
                break;
            case 10:
                DeviceHolder.INS().getDevices().getNavi().getNaviMap().switchTraffic(true);
                break;
            case 11:
                DeviceHolder.INS().getDevices().getNavi().getNaviMap().switchTraffic(false);
                break;
            case 12:
                DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchNaviSettingPage(true);
                break;
            case 13:
                DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchNaviSettingPage(false);
                break;
            case 14:
                DeviceHolder.INS().getDevices().getNavi().getNaviSetting().setPreView(true, true);
                break;
            case 15:
                DeviceHolder.INS().getDevices().getNavi().getNaviSetting().setPreView(false, true);
                break;
            case 16:
                DeviceHolder.INS().getDevices().getNavi().getNaviSetting().setPreView(true, false);
                break;
            case 17:
                DeviceHolder.INS().getDevices().getNavi().getNaviSetting().setPreView(false, false);
                break;
            case 18:
                DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchNaviFavoritesPage(true);
                break;
            case 19:
                DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchNaviFavoritesPage(false);
                break;
            case 20:
                DeviceHolder.INS().getDevices().getNavi().getNaviSetting().setSpeakMute(true);
                break;
            case 21:
                DeviceHolder.INS().getDevices().getNavi().getNaviSetting().setSpeakMute(false);
                break;
            case 22:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviPoi().searchPoi("天安门", NaviConstants.SearchType.SEARCH_TYPE_NORMAL, -1);
                LogUtils.i(TAG, "naviResponse:" + GsonUtils.toJson(naviResponse));
                break;
            case 23:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviTeam().getTeamInfo();
                LogUtils.i(TAG, "naviResponse:" + GsonUtils.toJson(naviResponse));
                break;
            case 24:
                DeviceHolder.INS().getDevices().getNavi().getNaviTeam().viewTeam();
                break;
            case 25:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchNaviLoginPage(true);
                break;
            case 26:
                DeviceHolder.INS().getDevices().getNavi().getNaviTeam().disbandTeam();
                break;
            case 27:
                int value = (new Random().nextInt(100)) % 6;
                LogUtils.i(TAG, "switchRoutePlan:" + value);
                DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchRoutePlan(value);
                break;
            case 28:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviMap().queryTrafficInfo();
                break;
            case 29:
                DeviceHolder.INS().getDevices().getNavi().routePlan(poi, null, NaviRoutePlan.DEFAULT.getValue());
                break;
            case 30:
                DeviceHolder.INS().getDevices().getNavi().startNavi(0);
                break;
            case 31:
                DeviceHolder.INS().getDevices().getNavi().startNavi(-1);
                break;
            case 32:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchLicensePlatePage(true);
                break;
            case 33:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getFavoritesPoiList(0, true);
                break;
            case 34:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getFavoritesPoiList(1, true);
                break;
            case 35:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getFavoritesPoiList(2, true);
                break;
            case 36:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().refreshRoute();
                break;
            case 37:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviSetting().setLaneLevelNavigation(true);
                break;
            case 38:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviSetting().setLaneLevelNavigation(false);
                break;
            case 39:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviPoi().searchAroundPoi("酒店", null, 5000);
                break;
            case 40:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviPoi().searchAroundPoi("酒店", null, 1000);
                break;
            case 41:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviPoi().searchAroundPoi("酒店", poi, 1000);
                break;
            case 42:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().startNavi(poi);
                break;
            case 44:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().startNavi("鼋头渚");
                break;
            case 45:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().setFavoritesPoi(NaviConstants.FavoritesType.HOME, poi);
                break;
            case 46:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().setFavoritesPoi(NaviConstants.FavoritesType.COMPANY, poi);
                break;
            case 47:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviPoi().searchOnWay("川菜", true);
                break;
            case 48:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviPoi().searchOnWay("吃饭", true);
                break;
            case 49:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviPoi().searchOnWay("服务区", true);
                break;
            case 50:
                List<Poi> viaPoints = DeviceHolder.INS().getDevices().getNavi().getNaviViaPoint().getViaPoints();
                LogUtils.i(TAG, "viaPoints:" + GsonUtils.toJson(viaPoints));
                break;
            case 51:
                if (poiList != null) {
                    for (Poi p : poiList) {
                        DeviceHolder.INS().getDevices().getNavi().getNaviViaPoint().addViaPoint(p);
                    }
                }
                break;
            case 52:
                DeviceHolder.INS().getDevices().getNavi().getNaviViaPoint().deleteViaPoint(0);
                break;
            case 53:
                DeviceHolder.INS().getDevices().getNavi().getNaviViaPoint().deleteAllViaPoints();
                break;
            case 54:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviPoi().searchPoi("鼋头渚", NaviConstants.SearchType.SEARCH_TYPE_NORMAL, -1);
                LogUtils.i(TAG, "naviResponse:" + GsonUtils.toJson(naviResponse));
                break;
            case 55:
                DeviceHolder.INS().getDevices().getNavi().routePlan(poi, poiList, NaviRoutePlan.DEFAULT.getValue());
                break;
            case 56: {
                List<Poi> poiList2 = new ArrayList<>();
                Poi poiX = new Poi();
                poiX.setName("火车站");
                poiList2.add(poiX);
                Poi desPoi = new Poi();
                desPoi.setName("天安门");
                DeviceHolder.INS().getDevices().getNavi().routePlan(desPoi, poiList2, NaviRoutePlan.DEFAULT.getValue());
                break;
            }
            case 57: {
                List<Poi> poiList2 = new ArrayList<>();
                Poi poiX = new Poi();
                poiX.setName("火车站");
                poiList2.add(poiX);
                DeviceHolder.INS().getDevices().getNavi().routePlan(poi, poiList2, NaviRoutePlan.DEFAULT.getValue());
                break;
            }
            case 58:
                List<Poi> poiList1 = new ArrayList<>();
                Poi poi1 = new Poi();
                poi1.setName("火车站");
                poiList1.add(poi1);
                Poi poi2 = new Poi();
                poi2.setName("加油站");
                poiList1.add(poi2);
                if (poiList != null && !poiList.isEmpty()) {
                    poiList1.add(poiList.get(0));
                }
                DeviceHolder.INS().getDevices().getNavi().routePlan(poi, poiList1, NaviRoutePlan.DEFAULT.getValue());
                break;
            case 59:
                Poi poi3 = new Poi();
                poi3.setName("王家湾");
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviPoi().searchAroundPoi("酒店", poi3, 5000);
                break;
            case 60:
                Poi poi4 = new Poi();
                poi4.setName("王家湾");
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviPoi().searchAroundPoi("酒店", poi4, 1000);
                break;
            case 67:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviPoi().searchAroundPoi("酒店", null, 1000);
                LogUtils.i(TAG, "naviResponse:" + GsonUtils.toJson(naviResponse));
                break;
            case 68:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviPoi().searchAroundPoi("酒店", null, 5000);
                LogUtils.i(TAG, "naviResponse:" + GsonUtils.toJson(naviResponse));
                break;
            case 69:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviPoi().searchPoi("鼋头渚", NaviConstants.SearchType.SEARCH_TYPE_NORMAL, 5000);
                LogUtils.i(TAG, "naviResponse:" + GsonUtils.toJson(naviResponse));
                break;
            case 70: {
                Intent intent3 = new Intent();
                intent3.setAction("com.android.systemui.split_screen_enter");
                intent3.putExtra("packageName", "com.mega.map");
                intent3.putExtra("position", 0);
                ContextUtils.getAppContext().sendBroadcast(intent3);
            }
            break;
            case 71: {
                Intent intent3 = new Intent();
                intent3.setAction("com.android.systemui.split_screen_enter");
                intent3.putExtra("packageName", "com.arcvideo.car.iqy.video");
                intent3.putExtra("position", 1);
                ContextUtils.getAppContext().sendBroadcast(intent3);
            }
            break;
            case 72: {
                Intent intent3 = new Intent();
                intent3.setAction("com.android.systemui.split_screen_dismiss");
                intent3.putExtra("dissmissLeft", false);
                ContextUtils.getAppContext().sendBroadcast(intent3);
            }
            break;
            case 73:
                DeviceHolder.INS().getDevices().getNavi().getNaviScreen().enterSplitScreen();
                break;
            case 74:
                DeviceHolder.INS().getDevices().getNavi().getNaviScreen().enterFullScreen();
                break;
            case 75:
                DeviceHolder.INS().getDevices().getNavi().getNaviScreen().isSpiltScreen();
                break;
            case 76: {
                Intent intent3 = new Intent();
                intent3.setAction("com.android.systemui.split_screen_dismiss");
                intent3.putExtra("dissmissLeft", true);
                ContextUtils.getAppContext().sendBroadcast(intent3);
            }
            break;
            case 78:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().continueNavi();
                LogUtils.i(TAG, "naviResponse:" + GsonUtils.toJson(naviResponse));
                break;
            case 79:
                int routeSize = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getRoutePaths();
                LogUtils.i(TAG, "routeSize:" + routeSize);
                int selected = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getRoutePathSelected();
                LogUtils.i(TAG, "selected:" + selected);
                selected = (selected + 1) % routeSize;
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviSetting().selectRoutePlanInNavi(selected);
                break;
            case 80:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviSetting().selectRoutePlanInNavi(-1);
                break;
            case 81:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviPoi().searchPoi("鼋头渚", NaviConstants.SearchType.SEARCH_TYPE_HOME, 5000);
                LogUtils.i(TAG, "naviResponse:" + GsonUtils.toJson(naviResponse));
                break;
            case 82:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviPoi().searchPoi("鼋头渚", NaviConstants.SearchType.SEARCH_TYPE_COMPANY, 5000);
                LogUtils.i(TAG, "naviResponse:" + GsonUtils.toJson(naviResponse));
                break;
            case 83:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviPoi().searchPoi("鼋头渚", NaviConstants.SearchType.SEARCH_TYPE_VIA_POINT, 5000);
                LogUtils.i(TAG, "naviResponse:" + GsonUtils.toJson(naviResponse));
                break;
            case 84:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviMap().changeRoad(NaviConstants.RoadType.MAIN_ROAD_VALUE);
                LogUtils.i(TAG, "naviResponse:" + GsonUtils.toJson(naviResponse));
                break;
            case 85:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviMap().changeRoad(NaviConstants.RoadType.SIDE_ROAD_VALUE);
                LogUtils.i(TAG, "naviResponse:" + GsonUtils.toJson(naviResponse));
                break;
            case 86:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviMap().changeRoad(NaviConstants.RoadType.ON_ELEVATED_VALUE);
                LogUtils.i(TAG, "naviResponse:" + GsonUtils.toJson(naviResponse));
                break;
            case 87:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviMap().changeRoad(NaviConstants.RoadType.UNDER_ELEVATED_VALUE);
                LogUtils.i(TAG, "naviResponse:" + GsonUtils.toJson(naviResponse));
                break;
            case 88:
                StringBuilder longStr = new StringBuilder();
                for (int i = 0; i < 5000; i++) {
                    longStr.append(i);
                    longStr.append(",");
                }
                LogUtils.i(TAG, "longStr:" + longStr);
                break;
            case 89:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviPoi().searchPoi("新疆", NaviConstants.SearchType.SEARCH_TYPE_NORMAL, 5000);
                LogUtils.i(TAG, "naviResponse:" + GsonUtils.toJson(naviResponse));
                break;
            case 90:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchCruiseBroadcast(true);
                LogUtils.i(TAG, "naviResponse:" + GsonUtils.toJson(naviResponse));
                break;
            case 91:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchCruiseBroadcast(false);
                LogUtils.i(TAG, "naviResponse:" + GsonUtils.toJson(naviResponse));
                break;
            case 92:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviMap().queryServiceAreaInfo();
                LogUtils.i(TAG, "naviResponse:" + GsonUtils.toJson(naviResponse));
                break;
            case 93:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviPoi().searchPoi("乌鲁木齐", NaviConstants.SearchType.SEARCH_TYPE_VIA_POINT, 5000);
                LogUtils.i(TAG, "naviResponse:" + GsonUtils.toJson(naviResponse));
                break;
            case 94:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviPoi().searchPoi("乌鲁木齐", NaviConstants.SearchType.SEARCH_TYPE_NORMAL, 5000);
                LogUtils.i(TAG, "naviResponse:" + GsonUtils.toJson(naviResponse));
                break;
            case 95:
                DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchLicensePlatePage(false);
                break;
            case 96:
                DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchRoutePlanInNavi(NaviRoutePlan.MONEY_LEAST.getValue());
                break;
            case 97:
                DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchRoutePlan(NaviRoutePlan.MONEY_LEAST.getValue());
                break;
            case 98:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviPoi().searchPoi("黄山景区", NaviConstants.SearchType.SEARCH_TYPE_NORMAL, 5000);
                break;
            case 99:
                List<Poi> list = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getCurrentPoiList();
                LogUtils.i(TAG, "getCurrentPoiList:" + GsonUtils.toJson(list));
                break;
            case 100:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviMap().getViewMode();
                break;
            case 101:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchRoutePreference(true);
                break;
            case 102:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviSetting().setSpeakMode(-1);
                break;
            case 103:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchAvoidRestriction(true);
                break;
            case 104:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchAvoidRestriction(false);
                break;
            case 105:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getFavoritesPoiList(3, true);
                break;
            case 106:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getNaviInfo();
                break;
            case 107:
                DeviceHolder.INS().getDevices().getNavi().getNaviSetting().isSpeakMute();
                break;
            case 108:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviPoi().searchPoi("万达", NaviConstants.SearchType.SEARCH_TYPE_VIA_POINT, 5000);
                break;
            case 109:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchPoiDetailPage(0);
                break;
            case 110:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().backToNaviHome();
                break;
            case 111:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviMap().queryTollStationInfo();
                break;
            case 112:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviMap().openHighWayInfoView();
                break;
            case 127:
                DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchQuickNavi(true);
                break;
            case 128:
                DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchQuickNavi(false);
                break;
            case 129:
                DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isQuickNavi();
                break;
            case 131:
                GpsLocation gpsLocation = null;
                if (GpsUtils.getCurrentLocation() != null) {
                    try {
                        JSONObject gpsJson = new JSONObject(GpsUtils.getCurrentLocation());
                        gpsLocation = new GpsLocation();
                        gpsLocation.setId(gpsJson.optString("id"));
                        gpsLocation.setAddress(gpsJson.optString("address"));
                        gpsLocation.setProvince(gpsJson.optString("province"));
                        gpsLocation.setName(gpsJson.optString("name"));
                        gpsLocation.setCityName(gpsJson.optString("cityName"));
                        gpsLocation.setCityCode(gpsJson.optInt("cityCode"));
                        gpsLocation.setLat(String.valueOf(gpsJson.optDouble("lat")));
                        gpsLocation.setLon(String.valueOf(gpsJson.optDouble("lon")));
                        gpsLocation.setDistrict(gpsJson.optString("district"));
                        gpsLocation.setDistance(gpsJson.optDouble("distance"));
                    } catch (Exception e) {
                        gpsLocation = null;
                    }
                }
                LogUtils.i(TAG, "getGpsLocation:" + GsonUtils.toJson(gpsLocation));
                break;
            case 132:
                DeviceHolder.INS().getDevices().getNavi().stopNavi();
                break;
            case 133:
                DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isAgreeDisclaimer();
                break;
            case 134:
                int v = Settings.System.getInt(ContextUtils.getAppContext().getContentResolver(), "system_info_hiding", 0);
                LogUtils.i(TAG, "system_info_hiding:" + v);
                break;
            case 135:
                NluPoi nluPoi = new NluPoi();
                nluPoi.setKeyword("充电站");
                nluPoi.setSearchType("around");
                List<Object> list2 = new ArrayList<>();
                list2.add(GsonUtils.toJson(nluPoi));
                Map<String, List<Object>> map = new HashMap<>();
                map.put(NaviConstants.DEST_POI, list2);
                NaviPoiUtils.searchPoi(null, map, false);
                break;
            case 136:
                DeviceHolder.INS().getDevices().getNavi().getNaviSetting().shareTrip();
                break;
            case 137:
                DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchNaviRestrictInfo(true);
                break;
            case 138:
                DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchNaviRestrictInfo(false);
                break;
            case 139:
                DeviceHolder.INS().getDevices().getNavi().getNaviMap().getThemeStyle();
                break;
            case 140:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviMap().prevPage();
                break;
            case 141:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviMap().nextPage();
                break;
            case 142:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getHistoryPoiList(0);
                break;
            case 143: {
                NaviResponse<Poi> naviResponse3 = DeviceHolder.INS().getDevices().getNavi().getNaviPoi().locateSelf();
                if (!naviResponse3.isSuccess() || naviResponse3.getData() == null) {
                    LogUtils.i(TAG, "naviResponse3 null");
                }
                NaviResponse<String> naviResponse2 = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().setFavoritesPoi(NaviConstants.FavoritesType.FAVORITES, naviResponse3.getData());
                LogUtils.i(TAG, "naviResponse2+" + GsonUtils.toJson(naviResponse2));
            }
            break;
            case 144:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchNaviLoginPage(false);
                break;
            case 145:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchHistoryPage(true);
                break;
            case 146:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchHistoryPage(false);
                break;
            case 147:
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviSetting().clearHistoryInfo();
                break;
            case 148:
                clientAgentResponse = NaviFavoriteUtils.clearHistoryInfo(null);
                LogUtils.i(TAG, "clientAgentResponse:" + GsonUtils.toJson(clientAgentResponse));
                break;
            case 149:
                DeviceHolder.INS().getDevices().getSystem().getSplitScreen().switchSplitScreen();
                break;
            case 150:
                NluPoi nluPoi1 = new NluPoi();
                nluPoi1.setKeyword("目的地");
                clientAgentResponse = NaviQueryUtils.queryEnergy(new HashMap<>(), nluPoi1);
                break;
            case 151:
                NluPoi nluPoi2 = new NluPoi();
                nluPoi2.setKeyword("途经点");
                clientAgentResponse = NaviQueryUtils.queryEnergy(new HashMap<>(), nluPoi2);
                break;
            case 152:
                NluPoi nluPoi3 = new NluPoi();
                nluPoi3.setKeyword("途经点");
                clientAgentResponse = NaviQueryUtils.queryRemainTime(new HashMap<>(), nluPoi3);
                break;
            case 153:
                Poi lastPoi = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getLastDestPoi();
                LogUtils.i(TAG, "lastPoi:" + GsonUtils.toJson(lastPoi));
                break;
            case 154:
                NaviResponse<Poi> naviResponse2 = DeviceHolder.INS().getDevices().getNavi().getNaviPoi().locateSelf();
                if (!naviResponse2.isSuccess() || naviResponse2.getData() == null) {
                    clientAgentResponse = new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), null, "抱歉，定位失败了，请稍后重试。");
                } else {
                    clientAgentResponse = NaviFavoriteUtils.favoritePoi(null, naviResponse2.getData());
                }
                break;
            case 155: {
                NluPoi nluPoi4 = new NluPoi();
                nluPoi4.setKeyword("目的地");
                Poi poiD = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getDestPoi();
                if (!DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation() || poiD == null) {
                    clientAgentResponse = new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), null, TtsBeanUtils.getTtsBean(3003900));
                } else {
                    if (nluPoi4.isFitDestPoi(poiD)) {
                        clientAgentResponse = NaviFavoriteUtils.favoritePoi(null, poiD);
                    } else {
                        clientAgentResponse = new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), null, "抱歉，没有找到对应的目的地。");
                    }
                }
            }
            break;
            case 156: {
                NluPoi nluPoi4 = new NluPoi();
                nluPoi4.setKeyword("目的地");
                nluPoi4.setDesc("万达广场");
                Poi poiD = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getDestPoi();
                if (!DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation() || poiD == null) {
                    clientAgentResponse = new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), null, TtsBeanUtils.getTtsBean(3003900));
                } else {
                    if (nluPoi4.isFitDestPoi(poiD)) {
                        clientAgentResponse = NaviFavoriteUtils.favoritePoi(null, poiD);
                    } else {
                        clientAgentResponse = new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), null, "抱歉，没有找到对应的目的地。");
                    }
                }
            }
            break;
            case 157: {
                NluPoi nluPoi4 = new NluPoi();
                nluPoi4.setKeyword("途经点");
                nluPoi4.setDesc("万达广场");

                List<Poi> wayPoiList = DeviceHolder.INS().getDevices().getNavi().getNaviViaPoint().getViaPoints();
                if (wayPoiList == null || wayPoiList.isEmpty()) {
                    clientAgentResponse = new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), null, TtsBeanUtils.getTtsBean(3004202));
                }
                if (TextUtils.isEmpty(nluPoi4.getDesc())) {
                    clientAgentResponse = NaviFavoriteUtils.favoritePoi(null, wayPoiList.get(0));
                } else {
                    List<Poi> poiList2 = new ArrayList<>();
                    for (Poi poiC : wayPoiList) {
                        if (poiC.containKeyWord(nluPoi4.getDesc())) {
                            poiList2.add(poiC);
                        }
                    }
                    if (poiList2.isEmpty()) {
                        clientAgentResponse = new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), null, "抱歉，没有找到对应的途经点。");
                    } else {
                        clientAgentResponse = NaviFavoriteUtils.favoritePoi(null, poiList2.get(0));
                    }
                }

            }
            break;
            case 158: {
                clientAgentResponse = NaviFavoriteUtils.deleteAll(null);
            }
            case 159: {
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchNaviCommutePage(true);
                break;
            }
            case 160: {
                naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviSetting().switchNaviCommutePage(false);
                break;

            }
            case 161: {
                DeviceHolder.INS().getDevices().getTts().speak("运势不错哦。综合指数⭐⭐⭐⭐");
                break;

            }
            case 162: {
                LLMSearchAgent llmSearchAgent = new LLMSearchAgent();
                DeviceHolder.INS().getDevices().getTts().speak(llmSearchAgent.replaceSpecialText("运势不错哦。综合指数⭐⭐⭐⭐"));
                break;
            }
            case 163: {
                LLMSearchAgent llmSearchAgent = new LLMSearchAgent();
                DeviceHolder.INS().getDevices().getTts().speak(llmSearchAgent.replaceSpecialText("运势不错哦。综合指数⭐⭐"));
                break;

            }
            default:
                break;
        }
        if (naviResponse != null) {
            LogUtils.i(TAG, "naviResponse:" + GsonUtils.toJson(naviResponse));
        }
        LogUtils.i(TAG, "clientAgentResponse:" + GsonUtils.toJson(clientAgentResponse));
        if (TextUtils.equals(intent.getAction(), "com.voyah.ai.ui.query")) {
            String queryString = intent.getStringExtra("query");
            Log2.i(TAG, "queryString is " + queryString);
            if (queryString == null) {
                return;
            }
            switch (queryString) {
                case "weatherTimeRange":
                    executeWeatherTimeRangeTask();
                    break;
                case "weatherTempDiffTime":
                    break;
                case "windDate":
                    executeWindDateTask();
                    break;
                case "windDateRange":
                    executeWindDateRangeTask();
                    break;
                case "windTime":
                    executeWindTimeTask();
                    break;
                case "interestTime":
                    executeInterestTimeTask();
                    break;
                case "sunRiseDate":
                    executeSunRiseDateTask();
                    break;
                case "sunRiseDateRange":
                    executeSunRiseDateRangeTask();
                    break;
                case "sunRiseTime":
                    executeSunRiseTimeTask();
                    break;
                case "airQualityDate":
                    executeAirQualityDateTask();
                    break;
                case "previousPicture":
                    executeSelectPicture(-1);
                    break;
                case "nextPicture":
                    executeSelectPicture(1);
                    break;
                case "rightRotatePicture":
                    break;
                case "openShoutOut":
                    executeOpenShoutOut();
                    break;
            }
        }

    }

    private void executeAirQualityDateTask() {
        Map<String, Object> flowContext = createWeatherDateContext();
        WeatherAirQualitySearchAgent aqiSearchAgent = new WeatherAirQualitySearchAgent();
        aqiSearchAgent.execute(new AgentInfoHolder(null, flowContext, null, null, null, null), null);
    }

    private void executeSunRiseTimeTask() {
        Map<String, Object> flowContext = createWeatherTimeContext();
        WeatherSunRiseDownSearchAgent sunRiseDownSearchAgent = new WeatherSunRiseDownSearchAgent();
        sunRiseDownSearchAgent.execute(new AgentInfoHolder(null, flowContext, null, null, null, null), null);
    }

    private void executeSunRiseDateRangeTask() {
        Map<String, Object> flowContext = createWeatherDateRangeContext();
        WeatherSunRiseDownSearchAgent sunRiseDownSearchAgent = new WeatherSunRiseDownSearchAgent();
        sunRiseDownSearchAgent.execute(new AgentInfoHolder(null, flowContext, null, null, null, null), null);
    }

    private void executeSunRiseDateTask() {
        Map<String, Object> flowContext = createWeatherDateContext();
        WeatherSunRiseDownSearchAgent sunRiseDownSearchAgent = new WeatherSunRiseDownSearchAgent();
        sunRiseDownSearchAgent.execute(new AgentInfoHolder(null, flowContext, null, null, null, null), null);
    }

    private void executeInterestTimeTask() {
        Map<String, Object> flowContext = createWeatherTimeContext();

        WeatherInterestSearchAgent interestSearchAgent = new WeatherInterestSearchAgent();
        interestSearchAgent.execute(new AgentInfoHolder(null, flowContext, null, null, null, null), null);
    }

    private void executeWindTimeTask() {
        Map<String, Object> flowContext = createWeatherTimeContext();

        WeatherWindSearchAgent windSearchAgent = new WeatherWindSearchAgent();
        windSearchAgent.execute(new AgentInfoHolder(null, flowContext, null, null, null, null), null);
    }

    private void executeWindDateRangeTask() {
        Map<String, Object> flowContext = createWeatherDateRangeContext();

        WeatherWindSearchAgent windSearchAgent = new WeatherWindSearchAgent();
        windSearchAgent.execute(new AgentInfoHolder(null, flowContext, null, null, null, null), null);
    }

    private void executeWindDateTask() {
        Map<String, Object> flowContext = createWeatherDateContext();

        WeatherWindSearchAgent windSearchAgent = new WeatherWindSearchAgent();
        windSearchAgent.execute(new AgentInfoHolder(null, flowContext, null, null, null, null), null);
    }

    private static void executeWeatherTimeRangeTask() {
        Map<String, Object> flowContext = new HashMap<>();
        flowContext.put("weatherType", WeatherType.TIME_RANGE_WEATHER);
        MultiHoursWeather timeRangeWeather = new MultiHoursWeather();
        timeRangeWeather.location = "昌平区";
        timeRangeWeather.tempLow = 15;
        timeRangeWeather.tempHigh = 30;
        timeRangeWeather.mainWeatherDesc = "晴";
        flowContext.put(FlowContextKey.FC_MULTI_HOURS_WEATHER, timeRangeWeather);
        NluInfo nluInfo = new NluInfo();
        List<Slot> slotList = new ArrayList<>();
        Slot slot = new Slot("location", "{\"district\": \"昌平区\"}");
        slotList.add(slot);
        slot = new Slot("time_range", "{\"start\": \"2024-05-23 18:00:00\", " +
                "\"end\": \"2024-05-23 20:00:00\"}");
        slotList.add(slot);
        nluInfo.slotList = slotList;
        flowContext.put("nluResult", nluInfo);

        WeatherSearchAgent weatherSearchAgent = new WeatherSearchAgent();
        weatherSearchAgent.execute(new AgentInfoHolder(null, flowContext, null, null, null, null), null);
    }

    @NonNull
    private static Map<String, Object> createWeatherDateContext() {
        Map<String, Object> flowContext = new HashMap<>();
        flowContext.put("weatherType", WeatherType.ONE_DAY_WEATHER);
        OneDayWeather oneDayWeather = new OneDayWeather();
        oneDayWeather.windLevelDay = "强风";
        oneDayWeather.windDirDay = "东风";
        oneDayWeather.windLevelNight = "微风";
        oneDayWeather.windDirNight = "东风";

        oneDayWeather.sunRise = "2024-05-24 06:00:00";
        oneDayWeather.sunDown = "2024-05-24 18:50:00";

        flowContext.put(FlowContextKey.FC_ONE_DAY_WEATHER, oneDayWeather);
        NluInfo nluInfo = new NluInfo();
        List<Slot> slotList = new ArrayList<>();
        Slot slot = new Slot("location", "{\"district\": \"昌平区\"}");
        slotList.add(slot);
        slot = new Slot("date", "2024-05-23");
        slotList.add(slot);
        nluInfo.slotList = slotList;
        flowContext.put("nluResult", nluInfo);
        return flowContext;
    }

    @NonNull
    private static Map<String, Object> createWeatherDateRangeContext() {
        Map<String, Object> flowContext = new HashMap<>();
        flowContext.put("weatherType", WeatherType.MULTI_DAYS_WEATHER);
        MultiDaysWeather multiDaysWeather = new MultiDaysWeather();
        List<MultiDaysWeather.DayWeather> dayWeathersList = new ArrayList<>();


        for (int i = 0; i < 8; i++) {
            MultiDaysWeather.DayWeather dayWeather = new MultiDaysWeather.DayWeather();

            dayWeather.windLevelDay = "强风";
            dayWeather.windDirDay = "东风";
            dayWeather.windLevelNight = "微风";
            dayWeather.windDirNight = "东风";

            if (i == 0) {
                dayWeather.date = "2024-05-23";
                dayWeather.sunRise = "2024-05-23 06:00:00";
                dayWeather.sunDown = "2024-05-23 18:50:00";
            } else if (i == 1) {
                dayWeather.date = "2024-05-24";
                dayWeather.sunRise = "2024-05-24 06:00:00";
                dayWeather.sunDown = "2024-05-24 18:50:00";
            } else if (i == 2) {
                dayWeather.date = "2024-05-25";
                dayWeather.sunRise = "2024-05-25 06:00:00";
                dayWeather.sunDown = "2024-05-25 18:50:00";
            } else if (i == 3) {
                dayWeather.date = "2024-05-26";
                dayWeather.sunRise = "2024-05-26 06:00:00";
                dayWeather.sunDown = "2024-05-26 18:50:00";
            } else if (i == 4) {
                dayWeather.date = "2024-05-27";
                dayWeather.sunRise = "2024-05-27 06:00:00";
                dayWeather.sunDown = "2024-05-27 18:50:00";
            } else if (i == 5) {
                dayWeather.date = "2024-05-28";
                dayWeather.sunRise = "2024-05-28 06:00:00";
                dayWeather.sunDown = "2024-05-28 18:50:00";
            } else if (i == 6) {
                dayWeather.date = "2024-05-29";
                dayWeather.sunRise = "2024-05-29 06:00:00";
                dayWeather.sunDown = "2024-05-29 18:50:00";
            } else {
                dayWeather.date = "2024-05-30";
                dayWeather.sunRise = "2024-05-30 06:00:00";
                dayWeather.sunDown = "2024-05-30 18:50:00";
            }

            dayWeathersList.add(dayWeather);
        }


        multiDaysWeather.dayWeathersList = dayWeathersList;
        flowContext.put(FlowContextKey.FC_MULTI_DAYS_WEATHER, multiDaysWeather);

        NluInfo nluInfo = new NluInfo();
        List<Slot> slotList = new ArrayList<>();
        Slot slot = new Slot("location", "{\"district\": \"昌平区\"}");
        slotList.add(slot);
        slot = new Slot("date_range", "{\"start\": \"2024-05-23\", " +
                "\"end\": \"2024-05-24\"}");
        slotList.add(slot);
        nluInfo.slotList = slotList;
        flowContext.put("nluResult", nluInfo);
        return flowContext;
    }

    @NonNull
    private static Map<String, Object> createWeatherTimeContext() {
        Map<String, Object> flowContext = new HashMap<>();
        flowContext.put("weatherType", WeatherType.TIME_WEATHER);
        OneHourWeather timeWeather = new OneHourWeather();
        timeWeather.windLevel = "强风";
        timeWeather.windDir = "东风";

        List<WeatherInterest> interestList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            WeatherInterest interest = new WeatherInterest();
            if (i == 0) {
                interest.interestType = "雨";
                interest.hit = true;
            } else if (i == 1) {
                interest.interestType = "雪";
                interest.hit = true;
            } else {
                interest.interestType = "霜";
                interest.hit = false;
            }
            interestList.add(interest);
        }
        timeWeather.interestList = interestList;
        timeWeather.weatherDesc = "雨为主";

        flowContext.put(FlowContextKey.FC_ONE_HOUR_WEATHER, timeWeather);
        NluInfo nluInfo = new NluInfo();
        List<Slot> slotList = new ArrayList<>();
        Slot slot = new Slot("location", "{\"district\": \"昌平区\"}");
        slotList.add(slot);
        slot = new Slot("time", "2024-05-23 18:00:00");
        slotList.add(slot);
        nluInfo.slotList = slotList;
        flowContext.put("nluResult", nluInfo);
        return flowContext;
    }

    private void executeSelectPicture(int offset) {
        boolean isBrowsingFlag = false;
        GalleryInterface galleryInterface = DeviceHolder.INS().getDevices().getGallery();
        if (galleryInterface != null) {
            isBrowsingFlag = galleryInterface.isBrowsingPicture();
            LogUtils.d(TAG, "executePreviousPicture isBrowsingFlag:" + isBrowsingFlag);
            if (isBrowsingFlag) {
                if (offset == -1) {
                    boolean firstFlag = galleryInterface.isFirstItem();
                    LogUtils.d(TAG, "executePreviousPicture firstFlag:" + firstFlag);
                    if (!firstFlag) {
                        boolean selectFlag = galleryInterface.setCurrentItem(offset);
                        LogUtils.d(TAG, "executePreviousPicture selectFlag:" + selectFlag);
                    }
                } else if (offset == 1) {
                    boolean lastFlag = galleryInterface.isLastItem();
                    LogUtils.d(TAG, "executePreviousPicture lastFlag:" + lastFlag);
                    if (!lastFlag) {
                        boolean selectFlag = galleryInterface.setCurrentItem(offset);
                        LogUtils.d(TAG, "executePreviousPicture selectFlag:" + selectFlag);
                    }
                }

            }
        }

    }

    private void executeOpenShoutOut() {
    }

}
