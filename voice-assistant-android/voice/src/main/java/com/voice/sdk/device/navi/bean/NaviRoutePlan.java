package com.voice.sdk.device.navi.bean;

import androidx.annotation.NonNull;

public enum NaviRoutePlan {
    DEFAULT("DEFAULT", 0, "智能推荐"),
    AVOID_HIGHWAY("AVOID_HIGHWAY", 1, "不走高速"),
    MONEY_LEAST("MONEY_LEAST", 2, "少收费"),
    FREE("FREE", 2, "少收费"),
    AVOID_CONGESTION("AVOID_CONGESTION", 3, "躲避拥堵"),
    TIME_FASTEST("TIME_FASTEST", 4, "时间优先"),
    DISTANCE_SHORTEST("DISTANCE_SHORTEST", 4, "距离最短"),
    HIGHWAY_PRIORITY("HIGHWAY_PRIORITY", 5, "高速优先"),
    NAVI_PRIORITY("NAVI_PRIORITY", 6, "NOA优先"),
    AVOID_RESTRICTION("AVOID_RESTRICTION", 100, "规避限行"),
    TRAFFIC_LIGHT_LEAST("TRAFFIC_LIGHT_LEAST", -1, "红绿灯最少"),
    AVOID("AVOID", -1, "不去XXX"),
    OTHER("OTHER", -1, "其他");

    private final String name;
    private final String chName;
    private final int value;

    NaviRoutePlan(String name, int value, String chName) {
        this.name = name;
        this.value = value;
        this.chName = chName;
    }

    public int getValue() {
        return value;
    }

    public String getChName() {
        return chName;
    }

    @NonNull
    @Override
    public String toString() {
        return this.value + "_" + this.name + "_" + this.chName;
    }

    public int getTtsId() {
        if (name.equals(DEFAULT.name)) {
            return 3006400;
        }
        if (name.equals(AVOID_HIGHWAY.name)) {
            return 3006100;
        }
        if (name.equals(MONEY_LEAST.name)) {
            return 3006500;
        }
        if (name.equals(FREE.name)) {
            return 3006500;
        }
        if (name.equals(AVOID_CONGESTION.name)) {
            return 3006300;
        }
        if (name.equals(TIME_FASTEST.name)) {
            return 3005700;
        }
        if (name.equals(DISTANCE_SHORTEST.name)) {
            return 3005800;
        }
        if (name.equals(HIGHWAY_PRIORITY.name)) {
            return 3006000;
        }
        if (name.equals(NAVI_PRIORITY.name)) {
            return 3006600;
        }
        if (name.equals(AVOID_RESTRICTION.name)) {
            return 3006700;
        }
        if (name.equals(TRAFFIC_LIGHT_LEAST.name)) {
            return 3006200;
        }
        if (name.equals(AVOID.name)) {
            return 3006800;
        }
        return 1100005;
    }

    public static NaviRoutePlan fromValue(String value) {
        for (NaviRoutePlan e : NaviRoutePlan.values()) {
            if (value != null && value.equalsIgnoreCase(e.name)) {
                return e;
            }
        }
        return OTHER;
    }
}
