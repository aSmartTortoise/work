package com.voyah.vcos.ttsservices.manager;

import android.content.Context;
import android.os.HandlerThread;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.voyah.vcos.ttsservices.Constant;
import com.voyah.vcos.ttsservices.utils.LogUtils;
import com.voyah.vcos.ttsservices.utils.MMKVHelper;

/**
 * @author:lcy 记录音色切换
 * @data:2024/8/16
 **/
public class SpeakerManager {
    private static final String TAG = "SpeakerManager";

    private static final Object speakerLock = new Object();
    private static final Object copyMesLock = new Object();

    private static final Gson gson = new Gson();
    private static volatile CopyMsgInfo copyMsgInfo = new CopyMsgInfo();


    private HandlerThread mHandlerThread;


    private Context mcontext;

    private static class InnerHolder {
        private static final SpeakerManager instance = new SpeakerManager();
    }

    public static SpeakerManager getInstance() {
        return InnerHolder.instance;
    }

    public void init(Context context) {
        mcontext = context;
        mHandlerThread = new HandlerThread(TAG);
        mHandlerThread.start();
//        getSpeaker();
    }

    //获取微软合成可用音色
    public String getOnLineTtsSpeaker() {
        LogUtils.e(TAG, "getTtsSpeaker");
        synchronized (speakerLock) {
            //获取设置中音色名
            String speaker = MMKVHelper.getInstance().getSpeaker();
            //映射为微软在线音色名
            return Constant.VoiceName.ON_LINE_NAME_MAP.get((TextUtils.isEmpty(speaker) || !Constant.VoiceName.SPEAKER_LIST.contains(speaker)) ? Constant.VoiceName.SPEAKER_LIST.get(0) : speaker);
        }
    }

    public String getOffLineTtsSpeaker() {
        LogUtils.e(TAG, "getTtsSpeaker");
        synchronized (speakerLock) {
            //获取设置中音色名
            String speaker = MMKVHelper.getInstance().getSpeaker();
            //映射为微软在线音色名
            return Constant.VoiceName.OFF_LINE_NAME_MAP.get((TextUtils.isEmpty(speaker) || !Constant.VoiceName.SPEAKER_LIST.contains(speaker)) ? Constant.VoiceName.SPEAKER_LIST.get(0) : speaker);
        }
    }

    public void setSpeaker(String speaker) {
        synchronized (speakerLock) {
            LogUtils.i(TAG, "speaker:" + speaker);
            //语音变化主动通知
            //todo:当speaker为空时使用那种音色(当前保存中有有音色or当前保存中无音色)
            if (TextUtils.isEmpty(speaker))
                MMKVHelper.getInstance().setSpeaker(Constant.VoiceName.SPEAKER_LIST.get(0));
            if (!TextUtils.isEmpty(speaker) && !TextUtils.equals(speaker, MMKVHelper.getInstance().getSpeaker()))
                MMKVHelper.getInstance().setSpeaker(speaker);
        }
    }

    public void saveCopyMes(String voiceName, String profileId, int sex) {
        //todo:后续改为本地缓存
//        {"voiceName":"哈哈","profileId":"2938fb11-dc91-4ada-8a6e-907ae5f0e656","voiceSex":0}
        LogUtils.d(TAG, "saveCopyMes voiceName:" + voiceName + " ,profileId:" + profileId + " ,sex:" + sex);
        try {
            if (copyMsgInfo == null) {
                copyMsgInfo = new CopyMsgInfo();
                LogUtils.d(TAG, "copyMsgInfo is null");
            }
            copyMsgInfo.setVoiceName(voiceName);
            copyMsgInfo.setProfileId(profileId);
            copyMsgInfo.setVoiceSex(sex);
            LogUtils.d(TAG, "copyMsgInfo:" + copyMsgInfo.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CopyMsgInfo getCopyMsgInfo() {
        LogUtils.d(TAG, "getCopyMsgInfo");
        if (copyMsgInfo == null || (TextUtils.isEmpty(copyMsgInfo.getProfileId()) || copyMsgInfo.getVoiceSex() == -1))
            return null;
        else
            return copyMsgInfo;

    }

    public void clearCopyMsgInfo() {
        LogUtils.d(TAG, "clearCopyMsgInfo");
        copyMsgInfo.reset();
    }
}
