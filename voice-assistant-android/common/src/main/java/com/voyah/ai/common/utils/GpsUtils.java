package com.voyah.ai.common.utils;

public class GpsUtils {

    private static String currentLocation = null;

    private GpsUtils() {

    }

    public static void setCurrentLocation(String currentLocation) {
        GpsUtils.currentLocation = currentLocation;
    }

    public static String getCurrentLocation() {
        return currentLocation;
    }
}
