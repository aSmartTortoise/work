package com.voyah.vcos.ttsservices.utils;

import com.tencent.mmkv.MMKV;
import com.voyah.vcos.ttsservices.Constant;

public class MMKVHelper {
    private static final String TAG = "MMKVHelper";

    private static volatile MMKVHelper mmkvHelper;

    private static final String SPEAKER_NAME_KEY = "tts_service";

    private static final String SPEAKER = "speaker";

    private static final Object speakLock = new Object();

    private final MMKV mKV;

    private MMKVHelper() {
        mKV = MMKV.mmkvWithID(TAG, MMKV.MULTI_PROCESS_MODE);
    }

    public static MMKVHelper getInstance() {
        if (null == mmkvHelper) {
            synchronized (MMKVHelper.class) {
                if (null == mmkvHelper) {
                    mmkvHelper = new MMKVHelper();
                }
            }
        }
        return mmkvHelper;
    }

    public MMKV getmKV(){
        return mKV;
    }


    public synchronized void setTTSId(String key, String ttsId) {
        mKV.putString(key, ttsId);
        mKV.commit();
    }

    public synchronized String getTTSId(String key,String ttsId) {
        return mKV.getString(key, ttsId);
    }

    public synchronized void setSpeaker(String speaker) {
        mKV.putString(SPEAKER, speaker);
    }

    public synchronized String getSpeaker() {
        return mKV.getString(SPEAKER, Constant.VoiceName.SPEAKER_LIST.get(0));
    }

    public void remove(String key) {
        mKV.remove(key);
    }

}
