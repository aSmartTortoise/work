package com.voice.sdk.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;

public class MapUtil {
    private static final String TAG = "MapUtil";
    public static final String METHOD = "Method";
    Map<String, Object> map;
    public MapUtil builder() {
        map = new HashMap<>();
        return this;
    }

    public MapUtil builder(Map<String, Object> sMap) {
        map = new HashMap<>(sMap);
        return this;
    }

    public MapUtil put(String key, Object value) {
        if (map == null) {
            LogUtils.e(TAG, "map is not initial, please call builder first");
            return this;
        }
        map.put(key, value);
        return this;
    }

    public Map<String, Object> build() {
        return map;
    }

    /**
     * 方法检查，如果不包含Method，则方法结构错误。
     * 如果仅是数据传递，不需要调用此方法
     * @return self
     */
    public MapUtil buildMethod() {
        if (map == null || !map.containsKey(METHOD)) {
            LogUtils.e(TAG, "miss METHOD key, please check data");
            return this;
        }
        return this;
    }

    public static String getStr(Map<?, ?> map, Object key) {
        return get(map, key, String.class);
    }

    /**
     * 获取Map指定key的值，并转换为Integer
     *
     * @param map Map
     * @param key 键
     * @return 值
     * @since 4.0.6
     */
    public static Integer getInt(Map<?, ?> map, Object key) {
        return get(map, key, Integer.class);
    }

    /**
     * 获取Map指定key的值，并转换为Double
     *
     * @param map Map
     * @param key 键
     * @return 值
     * @since 4.0.6
     */
    public static Double getDouble(Map<?, ?> map, Object key) {
        return get(map, key, Double.class);
    }

    /**
     * 获取Map指定key的值，并转换为Float
     *
     * @param map Map
     * @param key 键
     * @return 值
     * @since 4.0.6
     */
    public static Float getFloat(Map<?, ?> map, Object key) {
        return get(map, key, Float.class);
    }

    /**
     * 获取Map指定key的值，并转换为Short
     *
     * @param map Map
     * @param key 键
     * @return 值
     * @since 4.0.6
     */
    public static Short getShort(Map<?, ?> map, Object key) {
        return get(map, key, Short.class);
    }

    /**
     * 获取Map指定key的值，并转换为Bool
     *
     * @param map Map
     * @param key 键
     * @return 值
     * @since 4.0.6
     */
    public static Boolean getBool(Map<?, ?> map, Object key) {
        return get(map, key, Boolean.class);
    }

    /**
     * 获取Map指定key的值，并转换为Character
     *
     * @param map Map
     * @param key 键
     * @return 值
     * @since 4.0.6
     */
    public static Character getChar(Map<?, ?> map, Object key) {
        return get(map, key, Character.class);
    }

    /**
     * 获取Map指定key的值，并转换为Long
     *
     * @param map Map
     * @param key 键
     * @return 值
     * @since 4.0.6
     */
    public static Long getLong(Map<?, ?> map, Object key) {
        return get(map, key, Long.class);
    }

    /**
     * 获取Map指定key的值，并转换为{@link Date}
     *
     * @param map Map
     * @param key 键
     * @return 值
     * @since 4.1.2
     */
    public static Date getDate(Map<?, ?> map, Object key) {
        return get(map, key, Date.class);
    }

    /**
     * 获取Map指定key的值，并转换为指定类型
     *
     * @param <T>  目标值类型
     * @param map  Map
     * @param key  键
     * @param type 值类型
     * @return 值
     * @since 4.0.6
     */
    public static <T> T get(Map<?, ?> map, Object key, Class<T> type) {
        return null == map ? null : Convert.convert(type, map.get(key));
    }

    /**
     * 获取Map指定key的值，并转换为指定类型
     *
     * @param <T>  目标值类型
     * @param map  Map
     * @param key  键
     * @param type 值类型
     * @return 值
     * @since 4.5.12
     */
    public static <T> T get(Map<?, ?> map, Object key, TypeReference<T> type) {
        return null == map ? null : Convert.convert(type, map.get(key));
    }

}
