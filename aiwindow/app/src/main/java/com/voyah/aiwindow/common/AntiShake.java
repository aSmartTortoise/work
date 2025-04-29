package com.voyah.aiwindow.common;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ================================================
 * <p>
 * 页面内容介绍: 使用方法: 在任何需要拦截的地方(任何对象/点击/执行方法等) 加入 if (AntiShake.check(position)) return;
 * 或 if (AntiShake.check(position, 1500)) return;
 * <p>
 * ================================================
 */
public class AntiShake {
    private static final Map<String, Long> map = new LinkedHashMap<String, Long>() {
        @Override
        protected boolean removeEldestEntry(Entry<String, Long> pEldest) {
            return size() > 200;
        }
    };

    public static boolean check(Object obj) {
        return check(obj, 1000);
    }

    public static boolean check(Object obj, int delayTime) {
        Long time = map.get(obj.toString());
        if (time == null) {
            map.put(obj.toString(), System.currentTimeMillis());
            return false;
        } else {
            boolean b = System.currentTimeMillis() - time <= delayTime;
            if (!b) {
                map.put(obj.toString(), System.currentTimeMillis());
            }
            return b;
        }
    }
}