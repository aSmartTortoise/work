package com.voyah.ai.common.utils;

import java.util.Locale;
import java.util.Random;

public class TtsUtils {
    private TtsUtils() {

    }

    public static String getTtsByStringId(int id) {
        String str = ContextUtils.getAppContext().getString(id);
        if (str.contains("#")) {
            String[] strArr = str.split("#");
            int randomIndex = new Random().nextInt(strArr.length);
            return strArr[randomIndex];
        } else {
            return str;
        }
    }


    public static String getTtsByStringId(int id, Object... args) {
        String tts = getTtsByStringId(id);
        tts = String.format(Locale.getDefault(), tts, args);
        return tts;
    }


    public static String getTtsByString(String tts) {
        if (tts.contains("#")) {
            String[] strArr = tts.split("#");
            int randomIndex = new Random().nextInt(strArr.length);
            return strArr[randomIndex];
        } else {
            return tts;
        }
    }

    public static String getTtsByString(String tts, Object... args) {
        String ttsNew = getTtsByString(tts);
        ttsNew = String.format(Locale.getDefault(), ttsNew, args);
        return ttsNew;
    }




}
