package com.voyah.ai.common.utils;

import java.util.HashMap;

public class MemoryCacheUtils {
    private final static HashMap<String, Object> MEMORY_CACHE = new HashMap<>();

    public static void saveCache(String key, Object value) {
        MEMORY_CACHE.put(key, value);
    }

    public static Object getCache(String key) {
        return MEMORY_CACHE.getOrDefault(key, null);
    }
}
