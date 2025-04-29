package com.voyah.ai.voice.agent.navi;

import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.signal.VehicleConditionSignal;
import com.voice.sdk.device.carservice.vcar.IPropertyOperator;
import com.voice.sdk.device.navi.NaviResponseCode;
import com.voice.sdk.device.navi.bean.HighWayPoi;
import com.voice.sdk.device.navi.bean.NaviInfo;
import com.voice.sdk.device.navi.bean.NaviResponse;
import com.voice.sdk.device.navi.bean.NluPoi;
import com.voice.sdk.device.navi.bean.Poi;
import com.voice.sdk.tts.TtsBeanUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.logic.agent.generic.ClientAgentResponse;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NaviQueryUtils {
    private NaviQueryUtils() {

    }

    private static final String TAG = "NaviQueryUtils";
    public static final String[] unitTimeArray2 = new String[]{"m", "h", "d", "分钟", "小时", "天"};
    public static final String lessOneMinute = "少于1分钟";
    private static final int SECOND_OF_MINUTE = 60;
    private static final int SECOND_OF_HOUR = 3600;
    private static final String UNIT_MINUTE = "分钟";


    public static int curRemainingMileage() {
        IPropertyOperator operator = DeviceHolder.INS().getDevices().getCarService().getOperatorDispatcher().getOperatorByDomain("VehicleCondition");
        int curUsableMileage = (int) operator.getFloatProp(VehicleConditionSignal.CONDITION_REMAIN_MILEAGE);
        LogUtils.i(TAG, "curRemainingMileage:" + curUsableMileage);
        return curUsableMileage;
    }


    public static ClientAgentResponse queryTollStation(Map<String, Object> flowContext) {
        if (!DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation()) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3003302));
        }
        NaviResponse<List<HighWayPoi>> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviMap().queryTollStationInfo();
        if (naviResponse == null || !naviResponse.isSuccess() || naviResponse.getData() == null || naviResponse.getData().isEmpty()) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3007101));
        } else {
            DeviceHolder.INS().getDevices().getNavi().getNaviMap().openHighWayInfoView();
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3007100, String.valueOf(naviResponse.getData().size())));
        }
    }

    public static ClientAgentResponse queryServiceArea(Map<String, Object> flowContext) {
        if (!DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation()) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3003302));
        }
        NaviResponse<List<HighWayPoi>> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviMap().queryServiceAreaInfo();
        if (naviResponse == null || !naviResponse.isSuccess() || naviResponse.getData() == null || naviResponse.getData().isEmpty()) {
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3003301));
        } else {
            DeviceHolder.INS().getDevices().getNavi().getNaviMap().openHighWayInfoView();
            List<HighWayPoi> highWayPoiList = naviResponse.getData();
            highWayPoiList.sort(Comparator.comparingInt(HighWayPoi::getRemainDist));
            TTSBean tts = TtsBeanUtils.getTtsBean(3003400, highWayPoiList.get(0).getName());
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, tts);
        }
    }

    public static ClientAgentResponse queryDestination(Map<String, Object> flowContext, NluPoi poi) {
        if (!DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation()) {
            return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3007102));
        }
        Poi desPoi = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getDestPoi();
        if (desPoi == null) {
            return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3007102));
        }
        if (poi == null || !poi.isFitDestPoi(desPoi)) {
            return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3007300));
        }
        String address = desPoi.getAddress() + desPoi.getName();
        DeviceHolder.INS().getDevices().getNavi().getNaviSetting().setPreView(true, true);
        return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, TtsBeanUtils.getTtsBean(3007700, address));
    }


    public static ClientAgentResponse queryEnergy(Map<String, Object> flowContext, NluPoi poi) {
        if (!DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation()) {
            return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3007102));
        }
        Poi desPoi = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getDestPoi();
        if (desPoi == null) {
            return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3007102));
        }
        if (poi == null || poi.isFitDestPoi(desPoi)) {
            NaviResponse<NaviInfo> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getNaviInfo();
            if (naviResponse == null || !naviResponse.isSuccess() || naviResponse.getData() == null) {
                return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
            }
            int remainEnergy = naviResponse.getData().getRemainingDistanceAfterDestination();
            String tts;
            if (remainEnergy < 0) {
                tts = String.format(Locale.getDefault(), "您的剩余里程为%s公里，预计不足以到达终点，请及时补能。", curRemainingMileage());
            } else {
                tts = String.format(Locale.getDefault(), "到达终点后，剩余里程预计为%s公里", (remainEnergy / 1000));
            }
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, tts);
        }
        if (poi.isWayPoint()) {
            List<Poi> wayPointList = DeviceHolder.INS().getDevices().getNavi().getNaviViaPoint().getViaPoints();
            if (wayPointList == null || wayPointList.isEmpty()) {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, "您还没有设置途经点哦。");
            } else {
                if (poi.isFitWayPoi(wayPointList.get(0))) {
                    NaviResponse<NaviInfo> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getNaviInfo();
                    int remainEnergy = naviResponse.getData().getRemainingDistanceAfterNextWaypoint();
                    String tts;
                    if (remainEnergy < 0) {
                        tts = String.format(Locale.getDefault(), "您的剩余里程为%s公里，预计不足以到达%s，请及时补能。", curRemainingMileage(), wayPointList.get(0).getName());
                    } else {
                        tts = String.format(Locale.getDefault(), "到达%s后，剩余里程预计为%s公里", wayPointList.get(0).getName(), remainEnergy / 1000);
                    }
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, tts);
                } else {
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, "导航还只能查询到下个途经点信息哦。");
                }
            }
        }
        return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3007300));
    }


    public static ClientAgentResponse queryRemainTime(Map<String, Object> flowContext, NluPoi poi) {
        if (!DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation()) {
            return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3007102));
        }
        Poi desPoi = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getDestPoi();
        if (desPoi == null) {
            return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3007102));
        }
        if (poi == null || poi.isFitDestPoi(desPoi)) {
            NaviResponse<NaviInfo> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getNaviInfo();
            if (naviResponse == null || !naviResponse.isSuccess() || naviResponse.getData() == null) {
                return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
            }
            int leftDistance = naviResponse.getData().getRemainedDistance();
            int leftTime = naviResponse.getData().getRemainedTime();
            DeviceHolder.INS().getDevices().getNavi().getNaviSetting().setPreView(true,true);
            TTSBean tts = TtsBeanUtils.getTtsBean(3015900, getDistance(leftDistance), getTime(leftTime));
            return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, tts);
        }
        if (poi.isWayPoint()) {
            List<Poi> wayPointList = DeviceHolder.INS().getDevices().getNavi().getNaviViaPoint().getViaPoints();
            if (wayPointList == null || wayPointList.isEmpty()) {
                return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, "您还没有设置途经点哦。");
            } else {
                if (poi.isFitWayPoi(wayPointList.get(0))) {
                    NaviResponse<NaviInfo> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getNaviInfo();
                    if (naviResponse == null || !naviResponse.isSuccess() || naviResponse.getData() == null) {
                        return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
                    }
                    int leftDistance = naviResponse.getData().getNextViaRemainDistance();
                    int leftTime = naviResponse.getData().getNextViaRemainTime();
                    DeviceHolder.INS().getDevices().getNavi().getNaviSetting().setPreView(true,true);
                    String tts = String.format(Locale.getDefault(), "距离下个途经点%s还有%s，将在%s后到达。", wayPointList.get(0).getName(), getDistance(leftDistance), getTime(leftTime));
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, tts);
                } else {
                    return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, "导航还只能查询到下个途经点信息哦。");
                }
            }
        }
        return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3007300));
    }

    public static ClientAgentResponse queryTotalTime(Map<String, Object> flowContext, NluPoi poi) {
        if (!DeviceHolder.INS().getDevices().getNavi().getNaviStatus().isInNavigation()) {
            return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3007102));
        }
        if (poi != null && poi.isDestination()) {
            poi = null;
        }
        Poi desPoi = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getDestPoi();
        if (desPoi == null) {
            return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3007102));
        }
        if (poi != null && !desPoi.containKeyWord(poi.getKeyword())) {
            return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3007300));
        }
        NaviResponse<NaviInfo> naviResponse = DeviceHolder.INS().getDevices().getNavi().getNaviStatus().getNaviInfo();
        if (naviResponse == null || !naviResponse.isSuccess() || naviResponse.getData() == null) {
            return new ClientAgentResponse(NaviResponseCode.ERROR.getValue(), flowContext, TtsBeanUtils.getTtsBean(3013103));
        }
        int totalDistance = naviResponse.getData().getTotalDistance();
        int totalTime = naviResponse.getData().getTotalTime();
        TTSBean tts = TtsBeanUtils.getTtsBean(3015800, getDistance(totalDistance), getTime(totalTime));
        return new ClientAgentResponse(NaviResponseCode.SUCCESS.getValue(), flowContext, tts);
    }


    public static String getDistance(int distance) {
        String unitKM = "公里";
        String unitM = "米";
        String distanceStr;
        String unit;
        if (distance >= 1000) {
            if (distance >= 100 * 1000) {
                distanceStr = String.format("%d", (int) (distance / 1000f));
            } else {
                distanceStr = String.format("%.1f", distance / 1000f).replace(".0", "");
            }
            unit = unitKM;
        } else {
            distanceStr = String.valueOf(distance);
            unit = unitM;
        }
        return distanceStr + unit;
    }


    public static String getTime(int nTime) {
        StringBuffer timeBuffer = new StringBuffer();
        formatTime2(nTime, 2, timeBuffer);
        String timeText = timeBuffer.toString();
        if ("0分钟".equals(timeText)) {
            timeText = "1分钟";
        }
        return timeText;
    }

    public static void formatTime2(int nTime, int nUnit, StringBuffer formatTime) {
        if (formatTime != null) {
            if (nTime < SECOND_OF_MINUTE) {
                formatTime.append(lessOneMinute);
            } else {
                nTime = (int) correctTime(nTime);
                int hour;
                int minute;
                hour = nTime / SECOND_OF_HOUR;

                if (nTime < SECOND_OF_HOUR) {
                    minute = (nTime / SECOND_OF_MINUTE) % 60;
                    formatTime.append(minute).append(unitTimeArray2[nUnit + 1]);
                } else {
                    minute = nTime % SECOND_OF_HOUR / 60;
                    formatTime.append(hour).append(unitTimeArray2[nUnit + 2]);
                    if (minute > 0) {
                        formatTime.append(minute).append(UNIT_MINUTE);
                    }
                }
            }
        }
    }


    private static long correctTime(long time) {
        long second = time % SECOND_OF_MINUTE;
        if (second >= SECOND_OF_HALF_MINUTE) {
            time += SECOND_OF_MINUTE - second;
        }
        return time;
    }

    private static final int SECOND_OF_HALF_MINUTE = 30;


}
