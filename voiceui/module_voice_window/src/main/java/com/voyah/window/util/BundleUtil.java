package com.voyah.window.util;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;

public class BundleUtil {
    private static final String TAG = "BundleUtil";
    public static final String METHOD = "method";
    private Bundle bundle;
    public BundleUtil builder() {
        bundle = new Bundle();
        return this;
    }

    public BundleUtil builder(String method) {
        bundle = new Bundle();
        bundle.putString(METHOD, method);
        return this;
    }

    public BundleUtil builder(Bundle sBundle) {
        bundle = new Bundle(sBundle);
        return this;
    }

    public BundleUtil put(String key, Object value) {
        if (bundle == null) {
            Log.e(TAG, "Bundle is not initialized, please call builder first");
            bundle = new Bundle();
        }
        if (value == null) {
            bundle.putString(key, null);
            return this;
        }
        if (value instanceof Boolean) {
            bundle.putBoolean(key, (Boolean) value);
        } else if (value instanceof String) {
            bundle.putString(key, (String) value);
        } else if (value instanceof Byte) {
            bundle.putByte(key, (Byte) value);
        } else if (value instanceof CharSequence) {
            bundle.putCharSequence(key, (CharSequence) value);
        } else if (value instanceof Integer) {
            bundle.putInt(key, (Integer) value);
        } else if (value instanceof Float) {
            bundle.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            bundle.putLong(key, (Long) value);
        } else if (value instanceof Double) {
            bundle.putDouble(key, (Double) value);
        } else if (value instanceof Short) {
            bundle.putShort(key, (Short) value);
        } else if (value instanceof Parcelable) {
            bundle.putParcelable(key, (Parcelable) value);
        } else if (value instanceof Bundle) {
            bundle.putBundle(key, (Bundle) value);
        } else if (value instanceof Serializable) {
            bundle.putSerializable(key, (Serializable) value);
        } else if (value instanceof int[]) {
            bundle.putIntArray(key, (int[]) value);
        } else if (value instanceof String[]) {
            bundle.putStringArray(key, (String[]) value);
        } else {
            Log.e(TAG, "Unsupported type: " + value.getClass().getName());
        }
        return this;
    }

    public Bundle build() {
        return bundle;
    }

    public Bundle methodBuilder() {
        if (bundle == null) {
            Log.e(TAG, "Bundle is not initialized, please call builder first");
            return null;
        }
        if (!bundle.containsKey(METHOD)) {
            Log.e(TAG, "method is not exist, please call put method first");
            return null;
        }
        return bundle;
    }

}
