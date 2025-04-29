package com.voice.sdk.device.navi.bean;

import java.util.Locale;
@SuppressWarnings("unused")
public class MapFilterCategory {
    public static final String PRICE = "priceFilter";
    public static final String DISTANCE = "distanceFilter";
    public static final String GRADE = "gradeFilter";

    public String category;
    public float leftClosure;
    public float rightClosure;

    public MapFilterCategory(String category, String left, String right) {
        this.category = category;
        if (PRICE.equalsIgnoreCase(category)) {
            if ("MIN".equalsIgnoreCase(left)) {
                left = "0";
            } else if ("MAX".equalsIgnoreCase(right)) {
                right = "100000";
            }

            leftClosure = Float.parseFloat(left);
            rightClosure = Float.parseFloat(right);

            if (leftClosure == rightClosure) {
                leftClosure = (float) (leftClosure - leftClosure * 0.3);
                rightClosure = (float) (leftClosure + leftClosure * 0.3);
            }
        } else if (DISTANCE.equalsIgnoreCase(category)) {
            if ("MIN".equalsIgnoreCase(left)) {
                left = "0";
            } else if ("MAX".equalsIgnoreCase(right)) {
                right = "100000";
            }
            leftClosure = Float.parseFloat(left);
            rightClosure = Float.parseFloat(right);
        } else if (GRADE.equalsIgnoreCase(category)) {
            if ("MIN".equalsIgnoreCase(left)) {
                left = "0";
            } else if ("MAX".equalsIgnoreCase(right)) {
                right = "5";
            }

            leftClosure = Float.parseFloat(left);
            rightClosure = Float.parseFloat(right);
        }
    }

    public String getCategory() {
        return category;
    }

    public String getValue() {
        return String.format(Locale.getDefault(), "%.1f_%.1f", leftClosure, rightClosure);
    }
}
