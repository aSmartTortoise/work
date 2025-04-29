package com.voice.sdk;

import android.content.Context;
import android.content.SharedPreferences;

import com.voice.sdk.device.DeviceHolder;
import com.voyah.ai.common.ParamsGather;
import com.voyah.ai.common.utils.ContextUtils;
import com.voyah.ai.common.utils.GpsUtils;
import com.voyah.ai.common.utils.GsonUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.voice.platform.soa.api.plugin.IEnvPlugin;
import com.voyah.ds.common.entity.domains.GpsLocation;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author:lcy
 * @data:2024/4/8
 **/
public class VoiceEnvPlugin implements IEnvPlugin {
    private static final String TAG = "VoiceEnvPlugin";

    private final List<String> envList = Arrays.asList(IEnvPlugin.ENV_TEST_NAME, IEnvPlugin.ENV_DEV_NAME, IEnvPlugin.ENV_PRE_NAME, IEnvPlugin.ENV_PROD_NAME);

    public VoiceEnvPlugin() {
    }

    @Override
    public boolean hasNetwork() {
        return true;
    }

    @Override
    public String getEnv() {
        String env = IEnvPlugin.ENV_TEST_NAME;
        String configEnv = VoiceConfigManager.getInstance().getVoiceEnv();
        LogUtils.d(TAG, "configEnv is " + configEnv);
        if (!StringUtils.isBlank(configEnv)) {
            if (envList.contains(configEnv)) env = configEnv;
        }
        LogUtils.d(TAG, "当前环境：" + (StringUtils.isBlank(env) ? IEnvPlugin.ENV_TEST_NAME : env));
        return StringUtils.isBlank(env) ? IEnvPlugin.ENV_TEST_NAME : env;
    }

    @Override
    public boolean isBlueBoothEnable() {
        return ParamsGather.isBtConnect;
    }

    @Override
    public Object getGpsLocation() {
        GpsLocation gpsLocation = null;
        if (GpsUtils.getCurrentLocation() != null) {
            try {
                JSONObject gpsJson = new JSONObject(GpsUtils.getCurrentLocation());
                gpsLocation = new GpsLocation();
                gpsLocation.setId(gpsJson.optString("id"));
                gpsLocation.setAddress(gpsJson.optString("address"));
                gpsLocation.setProvince(gpsJson.optString("province"));
                gpsLocation.setName(gpsJson.optString("name"));
                gpsLocation.setCityName(gpsJson.optString("cityName"));
                gpsLocation.setCityCode(gpsJson.optInt("cityCode"));
                gpsLocation.setLat(String.valueOf(gpsJson.optDouble("lat")));
                gpsLocation.setLon(String.valueOf(gpsJson.optDouble("lon")));
                gpsLocation.setDistrict(gpsJson.optString("district"));
                gpsLocation.setDistance(gpsJson.optDouble("distance"));
            } catch (Exception e) {
                e.printStackTrace();
                gpsLocation = null;
            }
        }
        LogUtils.i(TAG, "getGpsLocation:" + GsonUtils.toJson(gpsLocation));
        return gpsLocation;
    }

    @Override
    public String getVideoPreference() {
        SharedPreferences sp = ContextUtils.getAppContext().getSharedPreferences("device_config", Context.MODE_PRIVATE);
        int pre = sp.getInt("video_preference", 0);
        LogUtils.d(TAG, "getVideoPreference: " + pre);
        return String.valueOf(pre);
    }

    @Override
    public Map<String, String> getVCarData() {
        Map<String, String> mediaData = DeviceHolder.INS().getDevices().getMediaCenter().uploadPlayStatus();
        LogUtils.d(TAG, "media status data: " + mediaData);
        return mediaData;
    }

}
