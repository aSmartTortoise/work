package com.voyah.ai.sdk;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class JsonUtil {

    private static final int FAST_JSON_PARSER = 1;
    private static final int GSON_JSON_PARSER = 2;

    public static int getJSONParser() {
        try {
            Class.forName("com.alibaba.fastjson.JSON");
            return FAST_JSON_PARSER;
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("com.google.gson.Gson");
                return GSON_JSON_PARSER;
            } catch (ClassNotFoundException e2) {
                // 未找到fastjson和gson，报错处理
                throw new IllegalStateException("Neither fastjson nor gson is available.");
            }
        }
    }

    public static String toJSONString(Object object) {
        int parser = getJSONParser();
        if (parser == FAST_JSON_PARSER) {
            return JSON.toJSONString(object);
        } else {
            return new Gson().toJson(object);
        }
    }

    public static <T> T fromJson(String json, Class<T> type) {
        int parser = getJSONParser();
        if (parser == FAST_JSON_PARSER) {
            return JSON.parseObject(json, type);
        } else {
            return new Gson().fromJson(json, type);
        }
    }

    public static <T> List<T> fromJsonToList(String json, Class<T> type) {
        int parser = getJSONParser();
        if (parser == FAST_JSON_PARSER) {
            return JSON.parseArray(json, type);
        } else {
            Gson gson = new Gson();
            Type listType = TypeToken.getParameterized(List.class, type).getType();
            return gson.fromJson(json, listType);
        }
    }

}
