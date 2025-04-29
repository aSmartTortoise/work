package com.voyah.ai.common.utils;

/**
 * @Date 2024/7/18 10:41
 * @Author 8327821
 * @Email *
 * @Description 处理双向映射规则
 **/
import java.util.HashMap;
import java.util.Map;

public class BiDirectionalMap<K, V> {
    private Map<K, V> forwardMap = new HashMap<>();
    private Map<V, K> reverseMap = new HashMap<>();

    public void put(K key, V value) {
        forwardMap.put(key, value);
        reverseMap.put(value, key);
    }

    public V getForward(K key) {
        return forwardMap.get(key);
    }

    public K getReverse(V value) {
        return reverseMap.get(value);
    }
}

