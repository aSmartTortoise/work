package com.voice.sdk.device.viewcmd;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ViewCmdCache {

    public static final int GLOBAL_ID = 1000;
    public static final int KWS_ID = 1001;
    // 待注册可见即可说命令缓存, 动态修改
    private final Map<Integer, Cache> viewCmdCache = new ConcurrentHashMap<>();

    public synchronized void addViewCommands(String pkgName, int id, List<String> list) {
        viewCmdCache.put(id, new Cache(pkgName, list));
    }

    public synchronized void removeViewCommands(String pkgName, int id) {
        viewCmdCache.remove(id);
    }

    public synchronized Cache getCache(int id) {
        return viewCmdCache.get(id);
    }

    public synchronized void clear() {
        viewCmdCache.clear();
    }

    public synchronized boolean isEmpty() {
        return viewCmdCache.isEmpty();
    }

    public static class Cache {

        /**
         * 包名
         */
        public String pkgName;
        /**
         * uid
         */
        public String uid;
        /**
         * 命令集合
         */
        public List<String> list;

        public Cache(String pkgName, List<String> list) {
            if (pkgName.contains("&")) {
                String[] split = pkgName.split("&");
                this.pkgName = split[0];
                this.uid = split[1];
            } else {
                this.pkgName = pkgName;
                this.uid = "";
            }
            this.list = list;
        }
    }
}
