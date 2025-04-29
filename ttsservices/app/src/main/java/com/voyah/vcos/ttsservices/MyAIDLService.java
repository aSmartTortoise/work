package com.voyah.vcos.ttsservices;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.voyah.ai.sdk.IGeneralTtsCallback;
import com.voyah.ai.sdk.ITTSService;
import com.voyah.ai.sdk.ITtsCallback;
import com.voyah.ai.sdk.IVAReadyCallback;
import com.voyah.ai.sdk.VoiceTtsBean;
import com.voyah.vcos.ttsservices.info.TtsBean;
import com.voyah.vcos.ttsservices.info.TtsPriority;
import com.voyah.vcos.ttsservices.manager.ITtsResolverCallBack;
import com.voyah.vcos.ttsservices.manager.SpeakerManager;
import com.voyah.vcos.ttsservices.manager.TTSManager;
import com.voyah.vcos.ttsservices.manager.near.ProximityInteractionManager;
import com.voyah.vcos.ttsservices.utils.LogUtils;
import com.voyah.vcos.ttsservices.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author:lcy
 * @data:2024/3/26
 **/
public class MyAIDLService extends Service {
    private static final String TAG = "MyAIDLService";
    private static final TTSManager ttsManager = TTSManager.getInstance();

    private static final Object initLock = new Object();
    private static final Object usageLock = new Object();
    private static final Object speakLock = new Object();

//    private Map<String, Binder> binderMap = new HashMap<>();

    private Map<String, Integer> usageMap = new HashMap<>();
//    private String mPackageName;

    private IVAReadyCallback mIvaReadyCallback;

    private String defaultPackageName = "defaultPackageName"; //注册方包名传空的话 赋值默认的
//    private MyBinder myBinder;


    //todo:binder断开后清除map缓存
    public MyAIDLService() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
//        String type = intent.getStringExtra("packageName");
//        LogUtils.i(TAG, "onBind action is " + intent.getAction() + " ,type is " + type);
//        if (TextUtils.isEmpty(type)) {
//            LogUtils.e(TAG, " params is empty");
//            return null;
//        }

//        if (!BinderList.packageNameList.contains(mPackageName)) {
//            LogUtils.e(TAG, mPackageName + " no permission ");
//            return null;
//        }
//        int usage = BinderList.UsageMap.get(mPackageName);
//        int useSource = BinderList.UseSourceMap.get(mPackageName);
//        LogUtils.i(TAG, "usage is " + usage + " ,useSource is " + useSource + " , " + binderMap.containsKey(type));
//        if (!binderMap.containsKey(mPackageName)) {
//            MyBinder myBinder = new MyBinder();
//            myBinder.setParams(usage, useSource);
//            binderMap.put(type, myBinder);
//            LogUtils.i(TAG, "binderMap.size is " + binderMap.size());
//        }
//        LogUtils.i(TAG, " " + (null == binderMap.get(type)));
//        return binderMap.get(type);
        LogUtils.i(TAG, " onBind: intent is " + intent);
        return new MyBinder();
    }

    public class MyBinder extends ITTSService.Stub {
        int registerUsage = Constant.Usage.VOICE_USAGE;

        @Override
        public void setUsage(String pkgname, int usage) throws RemoteException {
            synchronized (usageLock) {
                LogUtils.i(TAG, "pkgname is " + pkgname + " usage is " + usage);
                usageMap.put(pkgname, usage);
                ttsManager.initTts(AppContext.instant, usage);
            }
        }


        @Override
        public void registerReadyCallback(String packageName, IVAReadyCallback callback) throws RemoteException {
            synchronized (usageLock) {
//            mPackageName = packageName;
                mIvaReadyCallback = callback;
                int defaultUsage = TextUtils.equals(packageName, BinderList.VR_SERVICE) ? Constant.Usage.VOICE_USAGE : Constant.Usage.DEFAULT_USAGE;
                LogUtils.i(TAG, "packageName is " + packageName + " ,defaultUsage is " + defaultUsage);
                usageMap.put(packageName, defaultUsage);
                ttsManager.initTts(AppContext.instant, defaultUsage);

//            mIvaReadyCallback.onSpeechReady();
//        }
            }
        }

        @Override
        public void unregisterReadyCallback(String packageName, IVAReadyCallback callback) throws RemoteException {

        }

        @Override
        public boolean isRemoteReady() throws RemoteException {
            return true;
        }

        @Override
        public void speak(String pkgname, String text, ITtsCallback callback, String speaker, boolean highPriority) throws RemoteException {
            synchronized (speakLock) {
//        if (!BinderList.packageNameList.contains(pkgname)) {
//            LogUtils.e(TAG, pkgname + " no permission ");
//        } else {
                LogUtils.d(TAG, "pkgname is " + pkgname + " ,speaker is " + speaker + " ,highPriority:" + highPriority + " ,text:" + text);
                if (isEmptyTts(text)) {
                    if (null != callback)
                        callback.onTtsError(text, ITtsResolverCallBack.TtsResultCode.OTHER);
                    return;
                }
                TtsPriority ttsPriority;
                if (!TextUtils.isEmpty(speaker) && TextUtils.equals(pkgname, BinderList.VR_SERVICE))
                    ttsPriority = TtsPriority.P3;
                else
                    ttsPriority = highPriority ? TtsPriority.P1 : TtsPriority.P2;
                TtsBean ttsBean = new TtsBean("", text, pkgname, ttsPriority);
                ttsBean.setTtsId(Util.generateRandom(9));
                ttsBean.setStartTime(System.currentTimeMillis());
                ttsBean.setiTtsCallback(callback);
                ttsManager.playTts(getUsageByPackageName(pkgname), ttsBean);
//        }
            }
        }

        //流式TTS-暂不可用 (每个分片都要带有streamStatus  int 0:流式合成开始(第一片) 1:流式分片 2:流式合成结尾(最后一片))
        @Override
        public void streamSpeak(String pkgname, String text, ITtsCallback callback, int streamStatus) throws RemoteException {
//            synchronized (speakLock) {
            LogUtils.i(TAG, "streamSpeak1 pkgname is " + pkgname + " ,text is " + text + " ,streamStatus is " + streamStatus);
//            if (!TextUtils.equals(pkgname, BinderList.VR_SERVICE))
//                return;
            //todo:大模型调试
//            if (!TextUtils.equals(pkgname, BinderList.VR_SERVICE) && !TextUtils.equals(pkgname, BinderList.BOT))
//                return;
//            LogUtils.i(TAG, "streamSpeak2 pkgname is " + pkgname + " ,text is " + text + " ,streamStatus is " + streamStatus);
//                TtsBean ttsBean = new TtsBean(text, pkgname, streamStatus);
//                ttsBean.setTtsPriority(TtsPriority.P3);
//                ttsBean.setStartTime(System.currentTimeMillis());
//                ttsBean.setiTtsCallback(callback);
//                ttsManager.playTts(getUsageByPackageName(pkgname), ttsBean);
//            }
        }

        @Override
        public boolean isSpeaking(int usage) throws RemoteException {
            return ttsManager.isPlaying(usage);
        }

        @Override
        public void setTtsSpeaker(String pkgname, String speaker) throws RemoteException {
            synchronized (speakLock) {
                LogUtils.d(TAG, "setTtsSpeaker pkgname:" + pkgname + " ,speaker:" + speaker);
                //模拟测试数据
//            speaker = "{"voiceName":"邹子悦","profileId":"ae543e5a-2b89-4f36-a688-75421d7268e5","voiceSex":0}";
                if (TextUtils.equals(pkgname, BinderList.VR_SERVICE) && !TextUtils.isEmpty(speaker)) {
                    try {
                        JSONObject jsonObject = new JSONObject(speaker);
                        String voiceName = getStrValue("voiceName", jsonObject, Constant.VoiceName.SPEAKER_LIST.get(0));
                        String profileId = getStrValue("profileId", jsonObject, "");
                        int sex = getIntValue("voiceSex", jsonObject, -1);
                        LogUtils.d(TAG, "voiceName=" + voiceName + " ,profileId=" + profileId + " ,sex=" + sex);

                        if (!TextUtils.isEmpty(profileId) && !TextUtils.isEmpty(voiceName) && -1 != sex) {
                            SpeakerManager.getInstance().saveCopyMes(voiceName, profileId, sex);
                        } else {
                            SpeakerManager.getInstance().clearCopyMsgInfo();
                            SpeakerManager.getInstance().setSpeaker(voiceName);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void speakBean(String pkgname, VoiceTtsBean voiceTtsBean, ITtsCallback callback) throws RemoteException {
            if (null == voiceTtsBean || isEmptyTts(voiceTtsBean.getTts())) {
                LogUtils.d(TAG, "voiceTtsBean is null or tts is empty");
                if (null != callback)
                    callback.onTtsError(voiceTtsBean.getTts(), ITtsResolverCallBack.TtsResultCode.OTHER);
                return;
            }
            LogUtils.d(TAG, "speakBean is " + pkgname + " ,voiceTtsBean is " + voiceTtsBean.toString());
//            TtsPriority ttsPriority;
//            TtsBean ttsBean = new TtsBean(Util.generateRandom(9), text, pkgname, ttsPriority);
            voiceTtsBean.setPackageName(pkgname);
            TtsBean ttsBean = TtsBean.createBean(voiceTtsBean);
            ttsBean.setTtsId(Util.generateRandom(9));
            ttsBean.setStartTime(System.currentTimeMillis());
            ttsBean.setiTtsCallback(callback);
            ttsManager.playTts(getUsageByPackageName(pkgname), ttsBean);
        }

        @Override
        public void streamSpeakBean(String pkgname, VoiceTtsBean voiceTtsBean, ITtsCallback callback, int streamStatus) throws RemoteException {
            if (null == voiceTtsBean) {
                LogUtils.d(TAG, "voiceTtsBean is null");
                if (null != callback)
                    callback.onTtsError("", ITtsResolverCallBack.TtsResultCode.OTHER);
                return;
            }
            LogUtils.i(TAG, "streamSpeakBean pkgname is " + pkgname + " ,voiceTtsBean is " + voiceTtsBean.toString() + " ,streamStatus is " + streamStatus);
            voiceTtsBean.setPackageName(pkgname);
            TtsBean ttsBean = TtsBean.createBean(voiceTtsBean);
//            ttsBean.setTtsId(Util.generateRandom(9));
            ttsBean.setStartTime(System.currentTimeMillis());
            ttsBean.setStreamStatus(streamStatus);
            ttsBean.setiTtsCallback(callback);
            ttsManager.playTts(getUsageByPackageName(pkgname), ttsBean);
        }

        @Override
        public void speakInformationBean(String pkgname, VoiceTtsBean voiceTtsBean, ITtsCallback callback, String type, boolean highPriority) throws RemoteException {
            LogUtils.d(TAG, "pkgname is " + pkgname + " ,type is " + type);
            if (null == voiceTtsBean || TextUtils.isEmpty(voiceTtsBean.getTts()) || TextUtils.equals(voiceTtsBean.getTts(), "\"\"")) {
                LogUtils.d(TAG, "speakInformationBean voiceTtsBean is null or tts is empty");
                if (null != callback)
                    callback.onTtsError("", ITtsResolverCallBack.TtsResultCode.OTHER);
                return;
            }
            TtsPriority ttsPriority;
            if (!TextUtils.isEmpty(type) && TextUtils.equals(pkgname, BinderList.VR_SERVICE))
                ttsPriority = TtsPriority.P3;
            else
                ttsPriority = highPriority ? TtsPriority.P1 : TtsPriority.P2;
            voiceTtsBean.setPackageName(pkgname);
            TtsBean ttsBean = TtsBean.createBean(voiceTtsBean);
//            ttsBean.setTtsId(Util.generateRandom(9));
            ttsBean.setStartTime(System.currentTimeMillis());
            ttsBean.setTtsPriority(ttsPriority);
            ttsBean.setiTtsCallback(callback);
            ttsManager.playTts(getUsageByPackageName(pkgname), ttsBean);
        }

        @Override
        public void setNearByTtsStatus(boolean isNearByTts) throws RemoteException {
            LogUtils.d(TAG, "setNearByTtsStatus:" + isNearByTts);
            ProximityInteractionManager.getInstance().saveNearByTtsStatus(isNearByTts);
        }

        @Override
        public boolean requestAudioFocusByUsage(String packageName, int usage, int type) throws RemoteException {
            LogUtils.d(TAG, "requestAudioFocusByUsage packageName:" + packageName + " ,usage:" + usage + " ,type:" + type);
            return ttsManager.requestAudioFocusByUsage(usage, type);
        }

        @Override
        public void shutUpOneSelf(String pkgname) throws RemoteException {
            ttsManager.shutUpOneSelf(getUsageByPackageName(pkgname), pkgname);
        }

        @Override
        public void speakWithUsage(String pkgname, String text, ITtsCallback callback, String speaker, boolean highPriority, int usage) throws RemoteException {
            synchronized (speakLock) {
                LogUtils.d(TAG, "speakWithUsage pkgname is " + pkgname + " ,text is " + text + " ,speaker:" + speaker + " ,highPriority:" + highPriority + " ,usage:" + usage);
                if (isEmptyTts(text)) {
                    if (null != callback)
                        callback.onTtsError(text, ITtsResolverCallBack.TtsResultCode.OTHER);
                    return;
                }
                TtsPriority ttsPriority;
                if (!TextUtils.isEmpty(speaker) && TextUtils.equals(pkgname, BinderList.VR_SERVICE))
                    ttsPriority = TtsPriority.P3;
                else
                    ttsPriority = highPriority ? TtsPriority.P1 : TtsPriority.P2;
                TtsBean ttsBean = new TtsBean("", text, pkgname, ttsPriority);
                ttsBean.setTtsId(Util.generateRandom(9));
                ttsBean.setStartTime(System.currentTimeMillis());
                ttsBean.setiTtsCallback(callback);
                if (!ttsManager.existUsage(usage)) {
                    ttsManager.initTts(AppContext.instant, usage);
                }
                ttsManager.playTts(usage, ttsBean);
            }
        }

        @Override
        public void stopById(String pkgname, String originTtsId) throws RemoteException {
            LogUtils.d(TAG, "stopById pkgname:" + pkgname + " ,originTtsId:" + originTtsId);
            if (TextUtils.isEmpty(originTtsId))
                return;
            synchronized (speakLock) {
                ttsManager.stopById(getUsageByPackageName(pkgname), originTtsId);
            }
        }

        @Override
        public void registerGeneralTtsStatus(String pkgname, IGeneralTtsCallback callback) throws RemoteException {
            ttsManager.registerGeneralTtsStatus(pkgname, callback);
        }

        @Override
        public void unregisterGeneralTtsStatus(String pkgname, IGeneralTtsCallback callback) throws RemoteException {
            ttsManager.unregisterGeneralTtsStatus(pkgname, callback);
        }

        @Override
        public void forbiddenNearByTts(boolean isForbidden) throws RemoteException {
            //就近禁用
            LogUtils.d(TAG, "isForbidden:" + isForbidden);
            ProximityInteractionManager.getInstance().saveNearByTtsForbiddenStatus(isForbidden);
        }

        @Override
        public void speakWithUsageAndLocation(String pkgname, String text, ITtsCallback callback, String speaker, boolean highPriority, int usage, int location) throws RemoteException {
            synchronized (speakLock) {
                LogUtils.d(TAG, "speakWithUsageAndLocation pkgname is " + pkgname + " ,text is " + text + " ,speaker:" + speaker + " ,highPriority:" + highPriority + " ,usage:" + usage + " ,location:" + location);
                if (isEmptyTts(text)) {
                    if (null != callback)
                        callback.onTtsError(text, ITtsResolverCallBack.TtsResultCode.OTHER);
                    return;
                }
                TtsPriority ttsPriority;
                if (!TextUtils.isEmpty(speaker) && TextUtils.equals(pkgname, BinderList.VR_SERVICE))
                    ttsPriority = TtsPriority.P3;
                else
                    ttsPriority = highPriority ? TtsPriority.P1 : TtsPriority.P2;
                TtsBean ttsBean = new TtsBean("", text, pkgname, ttsPriority);
                ttsBean.setTtsId(Util.generateRandom(9));
                ttsBean.setSoundLocation(Math.max(location, 0));
                ttsBean.setStartTime(System.currentTimeMillis());
                ttsBean.setiTtsCallback(callback);
                if (!ttsManager.existUsage(usage)) {
                    ttsManager.initTts(AppContext.instant, usage);
                }
                ttsManager.playTts(usage, ttsBean);
            }
        }

        @Override
        public void stopCurTts(String pkgname) throws RemoteException {
//        if (!BinderList.packageNameList.contains(pkgname)) {
//            LogUtils.e(TAG, pkgname + " no permission ");
//        } else {
            LogUtils.d(TAG, "stopCurTts pkgname:" + pkgname);
            //todo:停止所有播报
            ttsManager.stopTts(getUsageByPackageName(pkgname), pkgname);
//        }
        }

        @Override
        public void shutUp(String pkgname) throws RemoteException {
//        if (!BinderList.packageNameList.contains(pkgname)) {
//            LogUtils.e(TAG, pkgname + " no permission ");
//        } else {
            ttsManager.shutUp(getUsageByPackageName(pkgname), pkgname);
//        }
        }
    }

//    private int getUsage(String packageName) {
//        int usage = -1;
//        if (BinderList.UsageMap.containsKey(packageName)) {
//            usage = BinderList.UsageMap.get(packageName);
//        }
//        return usage;
//    }

//    private int getUseSource(String packageName) {
//        int useSource = -1;
//        if (BinderList.UseSourceMap.containsKey(packageName)) {
//            useSource = BinderList.UseSourceMap.get(packageName);
//        }
//        return useSource;
//    }

    private int getUsageByPackageName(String packageName) {
        int currentUsage = -1000;
        if (null != usageMap && usageMap.containsKey(packageName))
            currentUsage = usageMap.get(packageName);
        return currentUsage;
    }

    private String getStrValue(String key, JSONObject responseObject, String defaultValue) {
        try {
            if (responseObject.has(key))
                defaultValue = (String) responseObject.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    private int getIntValue(String key, JSONObject responseObject, int defaultValue) {
        try {
            if (responseObject.has(key))
                defaultValue = (int) responseObject.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    private boolean isEmptyTts(String tts) {
        return TextUtils.isEmpty(tts) || TextUtils.equals(tts, "\"\"") || TextUtils.equals(tts, "“”");
    }

}
