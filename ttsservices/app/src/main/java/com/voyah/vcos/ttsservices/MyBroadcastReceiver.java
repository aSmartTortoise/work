package com.voyah.vcos.ttsservices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.text.TextUtils;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.StringUtils;
import com.microsoft.cognitiveservices.speech.Diagnostics;
import com.voyah.ai.sdk.ITtsCallback;
import com.voyah.vcos.ttsservices.info.TtsBean;
import com.voyah.vcos.ttsservices.info.TtsPriority;
import com.voyah.vcos.ttsservices.manager.TTSManager;
import com.voyah.vcos.ttsservices.manager.TtsResolverManager;
import com.voyah.vcos.ttsservices.manager.near.ProximityInteractionManager;
import com.voyah.vcos.ttsservices.utils.LogUtils;
import com.voyah.vcos.ttsservices.utils.Util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author:lcy
 * @data:2024/3/26
 **/
//空格处需要用/代替
// adb shell am broadcast -a com.voyah.tts.test --es order "ttsStylePlay:text=我要听周杰伦的\follow\your\heart:emotion=Assistant" -f 0x01000000
// 普通发起播报
// adb shell am broadcast -a com.voyah.tts.test --es order "ttsPlay:xxxxx" -f 0x01000000

//发起流式播报
// adb shell am broadcast -a com.voyah.tts.test --es order "stream:xxxxx" -f 0x01000000

// 文本+情感
// adb shell am broadcast -a com.voyah.tts.test --es order "ttsStylePlay:text=打开电话:emotion=Assistant" -f 0x01000000

// 语速
// adb shell am broadcast -a com.voyah.tts.test --es order "rate:5" -f 0x01000000

// 保存音频开关
// adb shell am broadcast -a com.voyah.tts.test --es order "audioSave:0" -f 0x01000000
// 微软sdklog保存开关
// adb shell am broadcast -a com.voyah.tts.test --es order "sdklog:0" -f 0x01000000
public class MyBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "MyBroadcastReceiver";

    private Map<String, Object> keyValueMap = new HashMap<>();
    private TTSManager ttsManager = TTSManager.getInstance();

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.e(TAG, "action:" + intent.getAction());
        LogUtils.e(TAG, "order:" + intent.getStringExtra("order"));
        if (TextUtils.equals(intent.getAction(), "com.voyah.tts.test")) {
            String order = intent.getStringExtra("order");
            if (TextUtils.isEmpty(order))
                return;
            String trim = order.substring(order.indexOf(":") + 1).trim();
            LogUtils.d(TAG, "order:" + order + " ,trim:" + trim);
            if (TextUtils.isEmpty(trim))
                return;
            if (order.contains("ttsPlay:")) {
                ttsManager.initTts(AppContext.instant, Constant.Usage.VOICE_USAGE);
                TtsBean ttsBean = new TtsBean(trim, "com.voyah.ai.voice");
                ttsBean.setTtsId(Util.generateRandom(9));
                ttsBean.setTtsPriority(TtsPriority.P2);
                LogUtils.i(TAG, "ttsBean is " + GsonUtils.toJson(ttsBean));
                ttsManager.playTts(Constant.Usage.VOICE_USAGE, ttsBean);
            } else if (order.contains("stream0:")) {
                ttsManager.initTts(AppContext.instant, Constant.Usage.VOICE_USAGE);
//                TtsBean ttsBean = new TtsBean("先帝创业未半而中道崩殂，今天下三分，益州疲弊，此诚危急存亡之秋也。然侍卫之臣不懈于内，忠志之士忘身于外者，盖追先帝之殊遇，欲报之于陛下也。诚宜开张圣听，以光先帝遗德，恢弘志士之气，不宜妄自菲薄，引喻失义，以塞忠谏之路也。\n" +
//                        "\n" +
//                        "宫中府中，俱为一体，陟罚臧否，不宜异同。若有作奸犯科及为忠善者，宜付有司论其刑赏，以昭陛下平明之理，不宜偏私，使内外异法也。\n" +
//                        "\n" +
//                        "侍中、侍郎郭攸之、费祎、董允等，此皆良实，志虑忠纯，是以先帝简拔以遗陛下。愚以为宫中之事，事无大小，悉以咨之，然后施行，必能裨补阙漏，有所广益。", "com.voyah.ai.voice");
                TtsBean ttsBean = new TtsBean(trim, "com.voyah.ai.voice");
                ttsBean.setTtsId(Util.generateRandom(9));
                ttsBean.setTtsPriority(TtsPriority.P2);
                ttsBean.setStreamStatus(0);
                LogUtils.i(TAG, "ttsBean is " + GsonUtils.toJson(ttsBean));
                ttsManager.playTts(Constant.Usage.VOICE_USAGE, ttsBean);
            } else if (order.contains("stream1:")) {
                ttsManager.initTts(AppContext.instant, Constant.Usage.VOICE_USAGE);
                TtsBean ttsBean = new TtsBean(trim, "com.voyah.ai.voice");
                ttsBean.setTtsId(Util.generateRandom(9));
                ttsBean.setTtsPriority(TtsPriority.P2);
                ttsBean.setStreamStatus(1);
                LogUtils.i(TAG, "ttsBean is " + GsonUtils.toJson(ttsBean));
                ttsManager.playTts(Constant.Usage.VOICE_USAGE, ttsBean);
            } else if (order.contains("stream2:")) {
                ttsManager.initTts(AppContext.instant, Constant.Usage.VOICE_USAGE);
                TtsBean ttsBean = new TtsBean("模拟结束。", "com.voyah.ai.voice");
                ttsBean.setTtsId(Util.generateRandom(9));
                ttsBean.setTtsPriority(TtsPriority.P2);
                ttsBean.setStreamStatus(2);
                LogUtils.i(TAG, "ttsBean is " + GsonUtils.toJson(ttsBean));
                ttsManager.playTts(Constant.Usage.VOICE_USAGE, ttsBean);
            } else if (order.contains("audioSave:")) {
                if (TextUtils.equals(trim, "0"))
                    TtsResolverManager.getInstance().setMicrosoftDumpSwitch(false);
                else if (TextUtils.equals(trim, "1"))
                    TtsResolverManager.getInstance().setMicrosoftDumpSwitch(true);
            } else if (order.contains("ttsStylePlay")) {
                getParams(trim);
                //文本加情感
                String ttsText = keyValueMap.containsKey("text") ? (String) keyValueMap.get("text") : "";
                String ttsEmotion = keyValueMap.containsKey("emotion") ? (String) keyValueMap.get("emotion") : "";
                LogUtils.d(TAG, "ttsText:" + ttsText + " ,ttsEmotion:" + ttsEmotion);
                if (TextUtils.isEmpty(ttsText) && TextUtils.isEmpty(ttsEmotion))
                    return;
                ttsManager.initTts(AppContext.instant, Constant.Usage.VOICE_USAGE);
                TtsBean ttsBean = new TtsBean(ttsText, "com.voyah.ai.voice");
                ttsBean.setTtsId(Util.generateRandom(9));
                ttsBean.setTtsPriority(TtsPriority.P2);
                ttsBean.setEmotion(ttsEmotion);
                LogUtils.i(TAG, "ttsBean is " + GsonUtils.toJson(ttsBean));
                ttsManager.playTts(Constant.Usage.VOICE_USAGE, ttsBean);
            } else if (order.contains("rate:")) {
                //语速
                if (!TextUtils.isEmpty(trim) && isNumeric(trim))
                    TtsResolverManager.getInstance().setPlayRate(trim);
            } else if (order.contains("sdklog:")) {
                if (TextUtils.equals("0", trim))
                    Diagnostics.startConsoleLogging();
                else if (TextUtils.equals("1", trim))
                    Diagnostics.stopConsoleLogging();
            } else if (order.contains("near:")) {
                if (TextUtils.equals("0", trim))
                    ProximityInteractionManager.getInstance().saveNearByTtsStatus(true);
                else if (TextUtils.equals("1", trim))
                    ProximityInteractionManager.getInstance().saveNearByTtsStatus(false);
            }
        }
    }

    private ITtsCallback iTtsCallback = new ITtsCallback.Stub() {
        @Override
        public void onTtsBeginning(String text) throws RemoteException {
            LogUtils.i(TAG, "onTtsBeginning text is " + text);
        }

        @Override
        public void onTtsEnd(String text, int reason) throws RemoteException {
            LogUtils.i(TAG, "onTtsEnd text is " + text + " ,reason " + reason);
        }

        @Override
        public void onTtsError(String text, int errCode) throws RemoteException {
            LogUtils.i(TAG, "onTtsError text is " + text + " ,errCode " + errCode);
        }
    };

    private void getParams(String input) {
        String[] pairs = input.split(":");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                // 去除空格
                key = key.trim();
                value = value.trim();
                if (StringUtils.equals(key, "text")) {
                    keyValueMap.put(key, value);
                } else if (StringUtils.equals(key, "emotion")) {
                    keyValueMap.put(key, value);
                }
            }
        }
    }

    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
