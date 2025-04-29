package com.voyah.ai.sdk;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * 主动意图实体类定义
 */
public class SceneIntent implements Parcelable {

    public String state;      // 场景值
    public String tts;        // 主动问询文案
    public Map<String, String> params; // 参数

    public SceneIntent() {
    }

    public SceneIntent(String state, String tts, Map<String, String> params) {
        this.state = state;
        this.tts = tts;
        this.params = params;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(state);
        dest.writeString(tts);
        if (params != null) {
            dest.writeInt(params.size());
            for (Map.Entry<String, String> entry : params.entrySet()) {
                dest.writeString(entry.getKey());
                dest.writeString(entry.getValue());
            }
        } else {
            dest.writeInt(0);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected SceneIntent(Parcel in) {
        state = in.readString();
        tts = in.readString();
        int size = in.readInt();
        if (size > 0) {
            params = new HashMap<>();
            for (int i = 0; i < size; i++) {
                String key = in.readString();
                String value = in.readString();
                params.put(key, value);
            }
        }
    }

    public static final Creator<SceneIntent> CREATOR = new Creator<SceneIntent>() {
        @Override
        public SceneIntent createFromParcel(Parcel in) {
            return new SceneIntent(in);
        }

        @Override
        public SceneIntent[] newArray(int size) {
            return new SceneIntent[size];
        }
    };

    @Override
    public String toString() {
        return "SceneIntent{" +
                "state='" + state + '\'' +
                ", tts='" + tts + '\'' +
                '}';
    }
}