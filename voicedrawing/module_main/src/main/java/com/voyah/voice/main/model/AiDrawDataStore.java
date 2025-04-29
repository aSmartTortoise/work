package com.voyah.voice.main.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.vcos.vehicle.env.EnvApi;
import com.voyah.ds.common.auth.DsAuthUtil;
import com.voyah.ds.sdk.engine.ssl.SslUtils;
import com.voyah.voice.framework.report.ReportHelp;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@SuppressWarnings("unused")
public class AiDrawDataStore {
    public static final int ENV_PRO = 1;
    public static final int ENV_PRE_PRO = 2;
    public static final int ENV_TEST = 3;
    public static final int ENV_DEV = 4;
    public static final int ENV_MOCK = 5;

    private static final String TAG = "AiDrawDataStore";

    private static final String DEFAULT_HOME_DATA = "{\n" +
            "    \"descList\": [\n" +
            "        \"“山风忽西落，池中月渐东升”\",\n" +
            "        \"“未来科幻感的宇宙战舰”\",\n" +
            "        \"“周末和小狗一起去公园”\"\n" +
            "    ],\n" +
            "    \"styleList\": [\n" +
            "        \"日系动漫\",\n" +
            "        \"厚涂抹\",\n" +
            "        \"油画大师\",\n" +
            "        \"像素\",\n" +
            "        \"插画\",\n" +
            "        \"水彩画\",\n" +
            "        \"写实\",\n" +
            "        \"游戏手绘\",\n" +
            "        \"赛博朋克\"\n" +
            "    ],\n" +
            "    \"urlBigList\": [\n" +
            "        \"b_im_0_1\",\n" +
            "        \"b_im_0_2\",\n" +
            "        \"b_im_0_3\",\n" +
            "        \"b_im_0_4\",\n" +
            "        \"b_im_0_5\",\n" +
            "        \"b_im_0_6\",\n" +
            "        \"b_im_0_7\",\n" +
            "        \"b_im_0_8\",\n" +
            "        \"b_im_0_9\",\n" +
            "        \"b_im_1_1\",\n" +
            "        \"b_im_1_2\",\n" +
            "        \"b_im_1_3\",\n" +
            "        \"b_im_1_4\",\n" +
            "        \"b_im_1_5\",\n" +
            "        \"b_im_1_6\",\n" +
            "        \"b_im_1_7\",\n" +
            "        \"b_im_1_8\",\n" +
            "        \"b_im_1_9\",\n" +
            "        \"b_im_2_1\",\n" +
            "        \"b_im_2_2\",\n" +
            "        \"b_im_2_3\",\n" +
            "        \"b_im_2_4\",\n" +
            "        \"b_im_2_5\",\n" +
            "        \"b_im_2_6\",\n" +
            "        \"b_im_2_7\",\n" +
            "        \"b_im_2_8\",\n" +
            "        \"b_im_2_9\"\n" +
            "    ],\n" +
            "    \"urlSmallList\": [\n" +
            "        \"im_0_1\",\n" +
            "        \"im_0_2\",\n" +
            "        \"im_0_3\",\n" +
            "        \"im_0_4\",\n" +
            "        \"im_0_5\",\n" +
            "        \"im_0_6\",\n" +
            "        \"im_0_7\",\n" +
            "        \"im_0_8\",\n" +
            "        \"im_0_9\",\n" +
            "        \"im_1_1\",\n" +
            "        \"im_1_2\",\n" +
            "        \"im_1_3\",\n" +
            "        \"im_1_4\",\n" +
            "        \"im_1_5\",\n" +
            "        \"im_1_6\",\n" +
            "        \"im_1_7\",\n" +
            "        \"im_1_8\",\n" +
            "        \"im_1_9\",\n" +
            "        \"im_2_1\",\n" +
            "        \"im_2_2\",\n" +
            "        \"im_2_3\",\n" +
            "        \"im_2_4\",\n" +
            "        \"im_2_5\",\n" +
            "        \"im_2_6\",\n" +
            "        \"im_2_7\",\n" +
            "        \"im_2_8\",\n" +
            "        \"im_2_9\"\n" +
            "    ]\n" +
            "}";

    public static int getEnv(Context context) {
        int env = EnvApi.getEnv(context);
        LogUtils.i("getEnv envConfig:" + env);
        return env;
    }

    private String getHomeDataUrl(Context context) {
        int env = getEnv(context);
        switch (env) {
            case ENV_PRE_PRO:
                return "https://ai-draw-pre.tos-cn-shanghai.volces.com/cover.json";
            case ENV_TEST:
                return "https://ai-draw-test.tos-cn-shanghai.volces.com/cover.json";
            case ENV_DEV:
                return "https://ai-draw-dev.tos-cn-shanghai.volces.com/cover.json";
            default:
                return "https://ai-draw-prod.tos-cn-shanghai.volces.com/cover.json";
        }
    }

    private String getRemainTimeUrl(Context context, String vin) {
        int env = getEnv(context);
        String url;
        switch (env) {
            case ENV_PRE_PRO:
                url = "https://ai-ds-pre.voyah.cn:1443/ds/v1/api/drawing/times?vin=";
                break;
            case ENV_TEST:
                url = "http://14.103.48.30:8281/ds/v1/api/drawing/times?vin=";
                break;
            case ENV_DEV:
                url = "http://14.103.48.30:8271/ds/v1/api/drawing/times?vin=";
                break;
            default:
                url = "https://ai-ds-prd.voyah.cn:1443/ds/v1/api/drawing/times?vin=";
                break;
        }
        url = url + vin;
        Log.i(TAG, "getRemainTimeUrl:" + url);
        return url;

    }

    private static final AiDrawDataStore instance = new AiDrawDataStore();
    private List<List<PhotoItem>> photoItemArr;


    private AiDrawDataStore() {

    }

    public static AiDrawDataStore getInstance(Context context) {
        if (instance.photoItemArr == null) {
            HomePhotoData homePhotoData = GsonUtils.fromJson(DEFAULT_HOME_DATA, HomePhotoData.class);
            Log.i(TAG, "use default data");
            int length = homePhotoData.getDescList().size();
            instance.photoItemArr = new ArrayList<>(length);
            int size = (homePhotoData.getUrlBigList() != null && !homePhotoData.getUrlBigList().isEmpty()) ? homePhotoData.getUrlBigList().size() / homePhotoData.getDescList().size() : homePhotoData.getStyleList().size();
            if (homePhotoData.getUrlBigList() != null && !homePhotoData.getUrlBigList().isEmpty()) {
                for (int i = 0; i < length; i++) {
                    instance.photoItemArr.add(new ArrayList<>());
                    for (int j = 0; j < size; j++) {
                        instance.photoItemArr.get(i).add(new PhotoItem(homePhotoData.getUrlBigList().get(i * size + j), homePhotoData.getUrlSmallList().get(i * size + j), homePhotoData.getDescList().get(i), homePhotoData.getStyleList().get(j)));
                    }
                }
            }
        }
        return instance;
    }

    public List<List<PhotoItem>> getPhotoItemArr() {
        return photoItemArr;
    }


    public void performHomeDataHttpGetRequest(Context context) {
        Log.i(TAG, "performHomeDataHttpGetRequest:");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(getHomeDataUrl(context))
                .build();
        try (Response response = client.newCall(request).execute()) {
            Log.i(TAG, "response:" + response);
            if (response.isSuccessful() && response.body() != null) {
                String content = response.body().string();
                Log.i("performHttpGetRequest", content);
                if (!content.isEmpty()) {
                    try {
                        HomePhotoData homePhotoData = GsonUtils.fromJson(content, HomePhotoData.class);
                        if (homePhotoData != null) {
                            SharedPreferences sharedPreferences = context.getSharedPreferences(AiDrawDataStore.class.getSimpleName(), Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("homedata", content);
                            editor.apply();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                throw new IOException("Unexpected code " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public RemainTimes performRemainTimeHttpGetRequest(Context context) {
        RemainTimes remainTimes = null;
        Map<String, String> authHeaders = null;
        OkHttpClient okHttpClient = null;
        try {
            authHeaders = DsAuthUtil.authHeaders("/ds/v1/api/drawing/times", "GET");
            SSLContext sslContext = SslUtils.getSslContext(new URI(getRemainTimeUrl(context, "")));
            Log.i(TAG, "authHeaders:" + authHeaders + ",sslContext:" + sslContext);
            if (sslContext != null) {
                okHttpClient = new OkHttpClient.Builder().sslSocketFactory(sslContext.getSocketFactory()).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient();
        }
        Request request = new Request.Builder()
                .url(getRemainTimeUrl(context, ReportHelp.getInstance().getVin()))
                .headers(Headers.of(authHeaders))
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String content = response.body().string();
                Log.i(TAG, content);
                if (!content.isEmpty()) {
                    remainTimes = GsonUtils.fromJson(content, RemainTimes.class);
                }
            } else {
                Log.e(TAG, "error:" + response);
                throw new IOException("Unexpected code " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return remainTimes;
    }

}
