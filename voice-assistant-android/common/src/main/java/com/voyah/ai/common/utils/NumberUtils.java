package com.voyah.ai.common.utils;

public class NumberUtils {

    public static String extractNumbers(String str) {
        return str.chars()
                .filter(Character::isDigit)
                .collect(StringBuilder::new,
                        StringBuilder::appendCodePoint,
                        StringBuilder::append)
                .toString();
    }

    public static float findClosestValue(float value, float[] targets) {
        float minDiff = Float.MAX_VALUE;
        float closestValue = Float.NaN;
        for (float target : targets) {
            float diff = Math.abs(value - target);
            if (diff < minDiff) {
                minDiff = diff;
                closestValue = target;
            }
        }
        return closestValue;
    }

    public static boolean areAllStringsEmpty(String... strings) {
        if (strings == null) {
            return true;
        }
        for (String str : strings) {
            if (str != null && !str.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
