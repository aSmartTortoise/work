package com.voyah.viewcmd;

import android.app.Activity;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map;

public class WeakReferenceUtil {
    private WeakReferenceUtil() {
    }

    public static <T> T safeGetMap(Map<WeakReference<View>, T> map, View view) {
        T object = null;
        Iterator<Map.Entry<WeakReference<View>, T>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<WeakReference<View>, T> entry = iterator.next();
            WeakReference<View> ref = entry.getKey();
            View refView = ref.get();

            if (refView == null) {
                iterator.remove();
            } else if (refView == view) {
                object = entry.getValue();
                break;
            }
        }
        return object;
    }

    public static <T> void safePutMap(Map<WeakReference<View>, T> map, View view, T value) {
        Iterator<Map.Entry<WeakReference<View>, T>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<WeakReference<View>, T> entry = iterator.next();
            WeakReference<View> ref = entry.getKey();
            View refView = ref.get();

            if (refView == null || refView == view) {
                iterator.remove();
            }
        }
        if (value != null) {
            map.put(new WeakReference<>(view), value);
        }
    }

    public static <T> T safeGetMapActivity(Map<WeakReference<Activity>, T> map, Activity activity) {
        T object = null;
        Iterator<Map.Entry<WeakReference<Activity>, T>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<WeakReference<Activity>, T> entry = iterator.next();
            WeakReference<Activity> ref = entry.getKey();
            Activity refActivity = ref.get();

            if (refActivity == null) {
                iterator.remove();
            } else if (refActivity == activity) {
                object = entry.getValue();
                break;
            }
        }
        return object;
    }

    public static <T> void safePutMapActivity(Map<WeakReference<Activity>, T> map, Activity activity, T value) {
        Iterator<Map.Entry<WeakReference<Activity>, T>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<WeakReference<Activity>, T> entry = iterator.next();
            WeakReference<Activity> ref = entry.getKey();
            Activity refActivity = ref.get();

            if (refActivity == null || refActivity == activity) {
                iterator.remove();
            }
        }
        if (value != null) {
            map.put(new WeakReference<>(activity), value);
        }
    }
}
