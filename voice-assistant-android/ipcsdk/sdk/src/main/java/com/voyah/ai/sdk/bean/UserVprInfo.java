package com.voyah.ai.sdk.bean;

import androidx.annotation.NonNull;

import com.voyah.ai.sdk.JsonUtil;

import java.util.ArrayList;
import java.util.List;

public class UserVprInfo {

    public String id;
    public String name;
    public int age;
    public boolean isFemale;
    public int direction = -1;
    public List<String> texts = new ArrayList<>();
    public long timestamp;
    public String extra;

    public UserVprInfo() {
    }

    public UserVprInfo(String id) {
        this.id = id;
    }

    @NonNull
    @Override
    public String toString() {
        return JsonUtil.toJSONString(this);
    }
}
