package com.voyah.vcos.ttsservices.manager;

import android.content.Context;
import android.os.RemoteException;
import android.text.TextUtils;

import com.voyah.ai.sdk.IGeneralTtsCallback;
import com.voyah.ai.sdk.ITtsCallback;
import com.voyah.vcos.ttsservices.AppContext;
import com.voyah.vcos.ttsservices.BinderList;
import com.voyah.vcos.ttsservices.Constant;
import com.voyah.vcos.ttsservices.audio.TtsAudioFocusManager;
import com.voyah.vcos.ttsservices.buriedpoint.BuriedPointManager;
import com.voyah.vcos.ttsservices.factory.IVoiceTts;
import com.voyah.vcos.ttsservices.factory.TTSFactory;
import com.voyah.vcos.ttsservices.factory.TTSType;
import com.voyah.vcos.ttsservices.info.PlayTTSBean;
import com.voyah.vcos.ttsservices.info.TtsBean;
import com.voyah.vcos.ttsservices.info.TtsPriority;
import com.voyah.vcos.ttsservices.manager.near.ProximityInteractionManager;
import com.voyah.vcos.ttsservices.utils.LogUtils;
import com.voyah.vcos.ttsservices.utils.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author:lcy
 * @data:2024/3/25
 **/
public class TTSManager {

    private static final String TAG = "TTSManager";

    private static volatile TTSManager ttsManager = new TTSManager();

    private static ConcurrentHashMap<Integer, VoiceTtsProxy> ttsMap = new ConcurrentHashMap<>();

    //    private ConcurrentHashMap<Integer, HashMap<String, IGeneralTtsCallback>> generalTtsMap = new ConcurrentHashMap<>();
//    private static ConcurrentHashMap<String, IGeneralTtsCallback> generalTtsMap = new ConcurrentHashMap<>();
    private static List<IGeneralTtsCallback> generalTtsList = new ArrayList<>();

    private static final Object generalLock = new Object();

    private TTSManager() {
    }

    public static TTSManager getInstance() {
        return ttsManager;
    }

//    /**
//     * @param context     上下文
//     * @param usage       通道
//     * @param packageName 包名
//     */
//    public void initTts(Context context, int usage, int useSource) {
//        LogUtils.i(TAG, "initTts  usage is " + usage + " ,useSource is " + useSource);
//        if (!ttsMap.containsKey(useSource)) {
//            VoiceTtsProxy voiceTtsProxy = new VoiceTtsProxy();
//            voiceTtsProxy.init(context, usage, useSource);
//            ttsMap.put(useSource, voiceTtsProxy);
//        }
//    }

    /**
     * @param context 上下文
     * @param usage   通道
     */
    public void initTts(Context context, int usage) {
        LogUtils.i(TAG, "initTts  usage is " + usage + " ,context:" + context);
        if (!ttsMap.containsKey(usage)) {
            VoiceTtsProxy voiceTtsProxy = new VoiceTtsProxy();
            voiceTtsProxy.init(context, usage);
            ttsMap.put(usage, voiceTtsProxy);
        }
    }

    public boolean existUsage(int usage) {
        return ttsMap.containsKey(usage);
    }

    public void playTts(int usage, TtsBean ttsBean) {
        LogUtils.i(TAG, "playTts usage is " + usage + " , tts is " + (null == ttsBean ? "null" : ttsBean.getTts()));
//        LogUtils.e(TAG, "playTts " + new Gson().toJson(ttsBean));
        if (ttsMap.containsKey(usage))
            ttsMap.get(usage).play(ttsBean);
        else
            LogUtils.i(TAG, "playTts " + usage + " is not exist");

    }

    public void playStreamTts(int usage, TtsBean ttsBean) {
        if (ttsMap.containsKey(usage))
            ttsMap.get(usage).play(ttsBean);
        else
            LogUtils.i(TAG, "playStreamTts " + usage + " is not exist");
    }

    public void stopTts(int usage, String packageName) {
        LogUtils.d(TAG, " stopTts -- stop usage:" + usage + " ,packageName:" + packageName);
//        if (usage == Constant.Usage.VOICE_USAGE && !isVoicePackName(packageName))
//            return;
        if (ttsMap.containsKey(usage))
            ttsMap.get(usage).stop(packageName);
        else
            LogUtils.i(TAG, usage + " is not exist");
    }

    public void shutUp(int usage, String packageName) {
        LogUtils.d(TAG, " shutUp -- shut usage is " + usage + " ,packageName is " + packageName);
//        if (usage == Constant.Usage.VOICE_USAGE && !isVoicePackName(packageName))
//            return;
        if (ttsMap.containsKey(usage))
            ttsMap.get(usage).shutUp(packageName);
        else
            LogUtils.i(TAG, usage + " is not exist");
    }

    public void shutUpOneSelf(int usage, String packageName) {
        LogUtils.d(TAG, " shutUpOneSelf -- shut usage is " + usage + " ,packageName is " + packageName);
//        if (usage == Constant.Usage.VOICE_USAGE && !isVoicePackName(packageName))
//            return;
        if (ttsMap.containsKey(usage))
            ttsMap.get(usage).shutUpOneSelf(packageName);
        else
            LogUtils.i(TAG, usage + " is not exist");
    }

    public boolean isPlaying(int usage) {
        if (ttsMap.containsKey(usage))
            return ttsMap.get(usage).isSpeaking();
        else
            LogUtils.i(TAG, usage + " is not exist");
        return false;
    }

    public void stopById(int usage, String originTtsId) {
        if (ttsMap.containsKey(usage))
            ttsMap.get(usage).stopById(originTtsId);
        else
            LogUtils.i(TAG, usage + " is not exist");
    }

    /**
     * 应用申请焦点接口
     *
     * @param usage 指定通道
     * @param type  申请类型
     * @return 是否申请成功
     */
    public boolean requestAudioFocusByUsage(int usage, int type) {
        if (!ttsMap.containsKey(usage))
            initTts(AppContext.instant, usage);
        return ttsMap.get(usage).requestFocus(type);
    }

    public void registerGeneralTtsStatus(String pkgname, IGeneralTtsCallback callback) {
        synchronized (generalLock) {
//            if (!generalTtsMap.containsKey(pkgname))
//                generalTtsMap.put(pkgname, callback);
            LogUtils.d(TAG, "registerGeneralTtsStatus pkgname:" + pkgname);
            if (null != generalTtsList && !generalTtsList.contains(callback))
                generalTtsList.add(callback);
        }
    }

    public void unregisterGeneralTtsStatus(String pkgname, IGeneralTtsCallback callback) {
        synchronized (generalLock) {
//            if (generalTtsMap.containsKey(pkgname))
//                generalTtsMap.remove(pkgname);
            LogUtils.d(TAG, "unregisterGeneralTtsStatus pkgname:" + pkgname);
            if (null != generalTtsList)
                generalTtsList.remove(callback);
        }
    }


    //todo:注册监听、接口实现

    private static class VoiceTtsProxy {
        private final String tag = "TTSManager VoiceTtsProxy-";
        IVoiceTts iVoiceTts;
        String mCurrentTtsId;
        HashMap<String, PlayTTSBean> ttsBeanHashMap = new HashMap<>();
        private ExecutorService cleanExecutor = new ThreadPoolExecutor(1, 1, 5L, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(500)); //有界的线程池
        private final Object shutUpLock = new Object();
        private LinkedList<TtsBean> discardList = new LinkedList<>();
        int mUsage;
        Context mContext;

//        boolean isStreamHaveBegin = false; //流式任务是否开始

        String streamCurrentId = ""; //记录下当前流式任务id(流式任务存在响应慢-跨音区交互时如果第一个音区还在查询数据的话要保证后续能够播出来)


        private void init(Context context, int usage) {
            LogUtils.d(tag + usage, "VoiceTtsProxy init usage is " + usage);
            this.mUsage = usage;
            this.mContext = context;
            iVoiceTts = TTSFactory.createTTS(TTSType.MICROSOFT);
            iVoiceTts.init(context, mUsage);
            iVoiceTts.setTtsListener(voiceTtsListener);
        }

        private PriorityQueue<TtsBean> ttsQueue = new PriorityQueue<>((o1, o2) -> {
            if (o1.getTtsPriority() != o2.getTtsPriority()) {
                return o2.getTtsPriority().ordinal() - o1.getTtsPriority().ordinal();
            } else {
                return Long.compare(o1.getStartTime(), o2.getStartTime());
            }
        });


        VoiceTtsListener voiceTtsListener = new VoiceTtsListener() {
            @Override
            public void onTtsStart(String ttsId, int ttsType) {
                callBack(ttsId, ITtsResolverCallBack.ResolverStatus.RESOLVER_START, ITtsResolverCallBack.ResolverStatus.RESOLVER_START, "", ttsType);
            }

            @Override
            public void onTtsError(String ttsId, int errorCode, String msg, int ttsType) {
                callBack(ttsId, ITtsResolverCallBack.ResolverStatus.RESOLVER_FAIL, errorCode, msg, ttsType);
            }

            @Override
            public void onTtsDiscard(String ttsId, int errorCode, String msg, int ttsType) {
                callBack(ttsId, ITtsResolverCallBack.ResolverStatus.RESOLVER_DISCARD, errorCode, msg, ttsType);
            }

            @Override
            public void onTtsStop(String ttsId, int ttsType) {
                //stop失败(stop client by user)和success都算作完成
//                LogUtils.i(TAG, "onTtsStop ttsId is " + ttsId);

            }

            @Override
            public void onTtsComplete(String ttsId, int ttsType) {
                LogUtils.i(tag + mUsage, "onTtsComplete ttsId is " + ttsId);
                callBack(ttsId, ITtsResolverCallBack.ResolverStatus.RESOLVER_COMPLETED, ITtsResolverCallBack.ResolverStatus.RESOLVER_COMPLETED, "", ttsType);
            }
        };

        void play(TtsBean ttsBean) {
            String tts = ttsBean.getTts().trim();
            if (ttsBean.getStreamStatus() == -1 && (TextUtils.isEmpty(tts) || TextUtils.equals(tts, "\"\""))) {
                //空请求直接返回，不做处理
                try {
                    if (null != ttsBean.getiTtsCallback())
                        ttsBean.getiTtsCallback().onTtsError(ttsBean.getTts(), ITtsResolverCallBack.TtsResultCode.OTHER);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else {
                if (mUsage == Constant.Usage.VOICE_USAGE) {
                    //语音通道中，语音设置为最高优先级，其他维持正常发起的优先级
                    if (isVoicePackName(ttsBean.getPackageName())) {
                        ttsBean.setTtsPriority(TtsPriority.P0);
                    } else {
//                    ttsBean.setTtsPriority(TtsPriority.P1);
                        ttsBean.setStartTime(System.currentTimeMillis());
                    }
                }
                //冲突处理
                conflictDispose(ttsBean);
            }
        }

        void stop() {
            if (mUsage == Constant.Usage.VOICE_USAGE) {
                LogUtils.i(tag + mUsage, "1stop...");
                iVoiceTts.stopTts();
            } else if (isSpeaking()) {
                LogUtils.i(tag + mUsage, "2stop...");
                iVoiceTts.stopTts();
            }
        }

        void stop(String pakName) {
            PlayTTSBean playTTSBean = ttsBeanHashMap.get(mCurrentTtsId);
            LogUtils.d(TAG, "pakName:" + pakName + " ,playTTSBean==null:" + (null == playTTSBean));
            if (isSpeaking() && (null != playTTSBean) && TextUtils.equals(playTTSBean.getPackageName(), pakName)) {
                LogUtils.i(tag + mUsage, "stop...");
                iVoiceTts.stopTts();
            }
        }

        public void stopById(String originTtsId) {
            LogUtils.d(TAG, "stopById originTtsId:" + originTtsId);
            if (TextUtils.isEmpty(originTtsId))
                return;

            synchronized (shutUpLock) {
                LogUtils.d(TAG, "stopById mCurrentTtsId:" + mCurrentTtsId);
                PlayTTSBean playTTSBean = TextUtils.isEmpty(mCurrentTtsId) ? null : ttsBeanHashMap.get(mCurrentTtsId);
                String currentOriginTtsId = null == playTTSBean ? "" : playTTSBean.getOriginTtsId();
                if (TextUtils.equals(originTtsId, currentOriginTtsId)) {
                    stop();
                } else {
                    cleanExecutor.execute(new stopAssignRunnable(originTtsId));
                }
            }
        }

        public void shutUp(String packageName) {
            LogUtils.d(TAG, "shutUp packageName:" + packageName);
            synchronized (shutUpLock) {
                if (!ttsQueue.isEmpty()) {
                    ttsQueue.clear();
                    if (discardList.size() > 0 && TextUtils.equals(packageName, BinderList.VR_SERVICE)) {
                        cleanExecutor.execute(new cleanRunnable(discardList, discardList.size(), ""));
                    }
                }
                stop();
            }
        }

        public void shutUpOneSelf(String packageName) {
            LogUtils.d(TAG, "shutUpOneSelf packageName:" + packageName);
            synchronized (shutUpLock) {
                if (!ttsQueue.isEmpty()) {
                    ttsQueue.clear();
                    if (discardList.size() > 0) {
                        LinkedList<TtsBean> temporaryQueue = new LinkedList<>();
                        Util.copyQueue(discardList, temporaryQueue);
                        discardList.clear();
                        cleanExecutor.execute(new cleanRunnable(temporaryQueue, temporaryQueue.size(), packageName));
                    }
                }
                stop(packageName);
            }
        }

        boolean isSpeaking() {
            LogUtils.i(TAG, "isSpeaking mCurrentTtsId:" + mCurrentTtsId);
            return !TextUtils.isEmpty(mCurrentTtsId);
        }

        boolean requestFocus(int type) {
            return iVoiceTts.requestFocus(mUsage, type);
        }

        private synchronized void callBack(String ttsId, int status, int errorCode, String msg, int ttsType) {
            //ttsType 0:普通 1:流式
            LogUtils.i(tag + mUsage, "callBack ttsId is " + ttsId + " ,usage is " + mUsage + " ,status is " + status +
                    (errorCode != -1 ? " ,code is " + errorCode : "") + " ,msg is " + msg + " ,ttsType is " + ttsType + " ,mCurrentTtsId:" + mCurrentTtsId);
            PlayTTSBean endTtsBean = ttsBeanHashMap.get(ttsId);
            String tts_status = "";
            String tts_time_end = "";
            if (null != endTtsBean) {
                if (ITtsResolverCallBack.ResolverStatus.RESOLVER_FAIL == status) {
                    tts_status = "fail";
                } else if (ITtsResolverCallBack.ResolverStatus.RESOLVER_COMPLETED == status || ITtsResolverCallBack.ResolverStatus.PROMPT_TONE_COMPLETED == status) {
                    tts_status = "normal";
                    tts_time_end = System.currentTimeMillis() + "";
                } else if (ITtsResolverCallBack.ResolverStatus.RESOLVER_DISCARD == status) {
                    tts_status = "break";
                }
            }
//                LogUtils.i(TAG, "endTtsBean is " + endTtsBean.toString());
            if (null == endTtsBean) {
                LogUtils.e(TAG, "endTtsBean is null");
                if ((ITtsResolverCallBack.ResolverStatus.RESOLVER_COMPLETED == status
                        || ITtsResolverCallBack.ResolverStatus.RESOLVER_DISCARD == status
                        || ITtsResolverCallBack.ResolverStatus.RESOLVER_FAIL == status)
                        && TextUtils.equals(ttsId, mCurrentTtsId))
                    mCurrentTtsId = "";
                return;
            }
            String tts = endTtsBean.getTts();
            String callbackTtsId = TextUtils.isEmpty(endTtsBean.getOriginTtsId()) ? endTtsBean.getTtsId() : endTtsBean.getOriginTtsId();
            LogUtils.d(TAG, "tts is " + tts);
            ITtsCallback mITtsCallback = null != endTtsBean ? endTtsBean.getiTtsCallback() : null;
            LogUtils.i(tag + mUsage, "mITtsCallback is null " + (null == mITtsCallback));

            //todo:回调播报位置，目前就近播报只有语音，播报位置处理时只需要单独处理语音通道
            if (ITtsResolverCallBack.ResolverStatus.RESOLVER_START == status)
                reportPlayStatus(endTtsBean.isNearPlay(), endTtsBean.getSoundLocation(), 0);
            else if (ITtsResolverCallBack.ResolverStatus.RESOLVING != status)
                reportPlayStatus(endTtsBean.isNearPlay(), endTtsBean.getSoundLocation(), 1);

            try {
                if (null != mITtsCallback) {
                    if (ITtsResolverCallBack.ResolverStatus.RESOLVER_START == status)
                        mITtsCallback.onTtsBeginning(ttsType == 0 ? tts : callbackTtsId);
                    else if (ITtsResolverCallBack.ResolverStatus.RESOLVER_FAIL == status)
                        mITtsCallback.onTtsError(ttsType == 0 ? tts : callbackTtsId, ITtsResolverCallBack.TtsResultCode.OTHER);
                    else if (ITtsResolverCallBack.ResolverStatus.RESOLVER_COMPLETED == status || ITtsResolverCallBack.ResolverStatus.PROMPT_TONE_COMPLETED == status)
                        mITtsCallback.onTtsEnd(ttsType == 0 ? tts : callbackTtsId, errorCode);
                    else if (ITtsResolverCallBack.ResolverStatus.RESOLVER_DISCARD == status)
                        mITtsCallback.onTtsError(ttsType == 0 ? tts : callbackTtsId, ITtsResolverCallBack.TtsResultCode.DISCARD);
                }


                if (ITtsResolverCallBack.ResolverStatus.RESOLVER_START != status && ITtsResolverCallBack.ResolverStatus.RESOLVING != status) {
                    LogUtils.d(tag + mUsage, "remove ttsBeanHashMap record  ttsBeanHashMap.size is " + ttsBeanHashMap.size() + " ,ttsId is " + ttsId + " ,mCurrentTtsId is " + mCurrentTtsId);
                    ttsBeanHashMap.remove(ttsId);
                    if (TextUtils.equals(mCurrentTtsId, ttsId))
                        mCurrentTtsId = "";
                    doPlay();
                }
                //埋点
                buriedPointData(endTtsBean, status, tts_status, tts_time_end);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    LogUtils.d(TAG, "retry callback");
                    if (null != mITtsCallback) {
                        if (ITtsResolverCallBack.ResolverStatus.RESOLVER_START == status)
                            mITtsCallback.onTtsBeginning(ttsType == 0 ? tts : ttsId);
                        else if (ITtsResolverCallBack.ResolverStatus.RESOLVER_FAIL == status)
                            mITtsCallback.onTtsError(ttsType == 0 ? tts : ttsId, ITtsResolverCallBack.TtsResultCode.OTHER);
                        else if (ITtsResolverCallBack.ResolverStatus.RESOLVER_COMPLETED == status || ITtsResolverCallBack.ResolverStatus.PROMPT_TONE_COMPLETED == status)
                            mITtsCallback.onTtsEnd(ttsType == 0 ? tts : ttsId, errorCode);
                        else if (ITtsResolverCallBack.ResolverStatus.RESOLVER_DISCARD == status)
                            mITtsCallback.onTtsError(ttsType == 0 ? tts : ttsId, ITtsResolverCallBack.TtsResultCode.DISCARD);

                    }
                } catch (Exception exception) {
                    e.printStackTrace();
                }

                if (ITtsResolverCallBack.ResolverStatus.RESOLVER_START != status && ITtsResolverCallBack.ResolverStatus.RESOLVING != status) {
                    LogUtils.d(tag + mUsage, "retry remove ttsBeanHashMap record  ttsBeanHashMap.size is " + ttsBeanHashMap.size() + " ,ttsId is " + ttsId + " ,mCurrentTtsId is " + mCurrentTtsId);
                    ttsBeanHashMap.remove(ttsId);
                    if (TextUtils.equals(mCurrentTtsId, ttsId))
                        mCurrentTtsId = "";
                    doPlay();
                }
                buriedPointData(endTtsBean, status, tts_status, tts_time_end);
            }
        }

        private synchronized void conflictDispose(TtsBean ttsBean) {
//            LogUtils.e(TAG, "tts:" + ttsBean.getTts());
            //埋点
            if (ttsBean.getStreamStatus() == -1 || ttsBean.getStreamStatus() == 0)
                BuriedPointManager.getInstance().buriedPointBean(ttsBean);

            if (mUsage == Constant.Usage.VOICE_USAGE) {
                PlayTTSBean playTTSBean = ttsBeanHashMap.get(mCurrentTtsId);
                String currentPackName = playTTSBean == null ? "" : playTTSBean.getPackageName();
                String newPackName = ttsBean.getPackageName();
                LogUtils.d(TAG, "conflictDispose currentPackName:" + currentPackName + " ,newPackName:" + newPackName + " ,isSpeaking():" + isSpeaking());
                //判断当前播报的是否为语音发起的任务
//                if (isSpeaking() && isVoicePackName(currentPackName) && !isVoicePackName(newPackName)) {
//                if (isSpeaking() && !isVoicePackName(newPackName)) {
                if (!isVoicePackName(newPackName)) {
                    //如果当前有正在播报且正在播报的为语音的且新发起的不为语音的加到队列
                    if (ttsBean.getTtsPriority() == TtsPriority.P1) {
                        if (discardList.size() > 0) {
//                            cleanExecutor.execute(new cleanRunnable(discardQueue, discardQueue.size(), ttsBean.getPackageName()));
                            shutUpOneSelf(ttsBean.getPackageName());
                        }
                        ttsQueue.offer(ttsBean);
                        discardList.add(ttsBean);
                        if (!isSpeaking())
                            doPlay();
                        else if (!isVoicePackName(currentPackName)) {
                            stop();
                            doPlay();
                        }
                    } else {
                        ttsQueue.offer(ttsBean);
                        discardList.add(ttsBean);
                        if (!isSpeaking())
                            doPlay();
                        LogUtils.d(TAG, "1------------" + ttsBean.getTtsId());
                    }
                    LogUtils.d(TAG, "conflictDispose offer queue");
                } else {
                    int streamStatus = ttsBean.getStreamStatus();
                    PlayTTSBean voiceTTSBean = PlayTTSBean.createBean(ttsBean);
                    voiceTTSBean.setUsage(Constant.Usage.VOICE_USAGE);
                    if (-1 == streamStatus) { //非流式
                        stop();
//                    isStreamHaveBegin = false;
                        mCurrentTtsId = voiceTTSBean.getTtsId();
                        ttsBeanHashMap.put(mCurrentTtsId, voiceTTSBean);
                        iVoiceTts.playTts(voiceTTSBean);
                    } else { //流式
//                    LogUtils.d(TAG, "conflictDispose isStreamHaveBegin:" + isStreamHaveBegin);
                        if (voiceTTSBean.getStreamStatus() == 0) {
                            LogUtils.d(TAG, " StreamStatus==0 -- stop");
//                        isStreamHaveBegin = true;
                            stop();
                            mCurrentTtsId = Util.generateRandom(9);
                            streamCurrentId = mCurrentTtsId;
                            voiceTTSBean.setTtsId(mCurrentTtsId);
                            LogUtils.i(TAG, "ttsBeanHashMap.put " + mCurrentTtsId);
                        }
//                    if (isStreamHaveBegin) {
                        LogUtils.i(TAG, "stream1-mCurrentTtsId is " + streamCurrentId);
                        voiceTTSBean.setTtsId(streamCurrentId);
                        ttsBeanHashMap.put(streamCurrentId, voiceTTSBean);
                        iVoiceTts.playStreamTts(voiceTTSBean);
//                    }
                    }
                }
            } else {
                LogUtils.i(TAG, " isSpeaking is " + isSpeaking() + " ,mCurrentTtsId is " + mCurrentTtsId + " ,ttsBeanHashMap.size is " + ttsBeanHashMap.size());
                if (!ttsBeanHashMap.isEmpty()) {
                    PlayTTSBean currentTtsBean = TextUtils.isEmpty(mCurrentTtsId) ? null : ttsBeanHashMap.get(mCurrentTtsId);
                    LogUtils.i(tag + mUsage, "currentTtsBean is " + ((null == currentTtsBean) ? "null" : currentTtsBean.toString()));
                    if (ttsBean.getTtsPriority() == TtsPriority.P1 || ttsBean.getTtsPriority() == TtsPriority.P0) {
                        if (discardList.size() > 0) {
//                            cleanExecutor.execute(new cleanRunnable(discardQueue, discardQueue.size(), ttsBean.getPackageName()));
                            shutUpOneSelf(ttsBean.getPackageName());
                        }
                        ttsQueue.offer(ttsBean);
                        discardList.add(ttsBean);
                        if (!isSpeaking())
                            doPlay();
                        else
                            stop();
                    } else {
                        ttsQueue.offer(ttsBean);
                        discardList.add(ttsBean);
                        if (!isSpeaking())
                            doPlay();
                        LogUtils.d(TAG, "1------------" + ttsBean.getTtsId());
                    }
                } else {
                    mCurrentTtsId = ttsBean.getTtsId();
                    PlayTTSBean voiceTTSBean = PlayTTSBean.createBean(ttsBean);
                    voiceTTSBean.setUsage(mUsage);
                    if (voiceTTSBean.getStreamStatus() == -1) {
                        LogUtils.d(TAG, "2------------ttsBeanHashMap.put " + mCurrentTtsId);
                        ttsBeanHashMap.put(mCurrentTtsId, voiceTTSBean);
                        iVoiceTts.playTts(voiceTTSBean);
                    }
                }
            }
        }

        private synchronized void doPlay() {
            if (isSpeaking()) {
                LogUtils.d(TAG, "doPlay current is play");
                return;
            }
            TtsBean ttsBean = ttsQueue.poll();
            if (null == ttsBean) {
                TtsAudioFocusManager.getInstance().releaseAudioFocus(mUsage, 3);
                LogUtils.i(tag + mUsage, "ttsQueue is empty , release audio focus");
                return;
            }
            if (mUsage == Constant.Usage.VOICE_USAGE) {
                long currentTime = System.currentTimeMillis();
                long startTime = ttsBean.getStartTime();
                LogUtils.d(TAG, "doPlay currentTime:" + currentTime + " ,startTime:" + startTime + " ," + (currentTime - startTime));
                if (currentTime - startTime >= Constant.OUT_LIFE_DROP_TIME) {
                    LogUtils.d(TAG, "doPlay out life drop");
                    if (null != ttsBean.getiTtsCallback()) {
                        try {
                            ttsBean.getiTtsCallback().onTtsError(ttsBean.getTts(), ITtsResolverCallBack.TtsResultCode.OTHER);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    doPlay();
                    return;
//                callBack(ttsBean.getTtsId(), ITtsResolverCallBack.ResolverStatus.RESOLVER_DISCARD, ITtsResolverCallBack.TtsResultCode.OTHER, "out life", 0);
                }
            }
            //取出数据播放的同时清除discardQueue中记录
            if (null != discardList && !discardList.isEmpty() && discardList.contains(ttsBean))
                discardList.remove(ttsBean);

            LogUtils.d(tag + mUsage, " doPlay -- stop");
            stop();
            mCurrentTtsId = ttsBean.getTtsId();
            PlayTTSBean voiceTTSBean = PlayTTSBean.createBean(ttsBean);
            voiceTTSBean.setUsage(mUsage);
            if (!ttsBeanHashMap.containsKey(mCurrentTtsId)) {
                LogUtils.d(TAG, "3------------" + ttsBean.getTtsId() + " , ttsBeanHashMap.put mCurrentTtsId is " + mCurrentTtsId);
                ttsBeanHashMap.put(mCurrentTtsId, voiceTTSBean);
            }
            iVoiceTts.playTts(voiceTTSBean);
        }

        private class cleanRunnable implements Runnable {
            private LinkedList<TtsBean> mQueue;
            private int size;
            private String packageName;

            public cleanRunnable(LinkedList<TtsBean> list, int size, String packageName) {
                this.mQueue = list;
                this.size = size;
                this.packageName = packageName;
            }

            @Override
            public void run() {
                LogUtils.i(tag + mUsage, "cleanRunnable run  size:" + size + " ,packageName:" + packageName + " , Thread.currentThread().getName() is " + Thread.currentThread().getName());
                for (int i = 0; i < size; i++) {
                    TtsBean disTtsBean = mQueue.remove();
                    if (null != disTtsBean) {
                        //是否为任务发起方的请求
                        boolean isNeedStop = TextUtils.equals(disTtsBean.getPackageName(), packageName);
                        if (isNeedStop) {
                            ITtsCallback iTtsCallback = disTtsBean.getiTtsCallback();
                            if (null != iTtsCallback) {
                                try {
                                    iTtsCallback.onTtsError(disTtsBean.getTts(), ITtsResolverCallBack.TtsResultCode.DISCARD);
                                    BuriedPointManager.getInstance().lastSetBuriedPointData(disTtsBean.getOriginTtsId(), "discard", "", "", "", "");
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            } else
                                LogUtils.d(TAG, "cleanRunnable iTtsCallback is null");
                        } else {
                            ttsQueue.add(disTtsBean);
                            discardList.add(disTtsBean);
                            if (!isSpeaking()) {
                                doPlay();
                            }
                        }
                    }
                }
            }
        }

        private class stopAssignRunnable implements Runnable {
            private String originTtsId;

            public stopAssignRunnable(String id) {
                this.originTtsId = id;
            }

            @Override
            public void run() {
                if (ttsQueue.isEmpty() || TextUtils.isEmpty(originTtsId))
                    return;
                for (TtsBean ttsBean : ttsQueue) {
                    if (null != ttsBean && TextUtils.equals(originTtsId, ttsBean.getOriginTtsId())) {
                        ttsQueue.remove(ttsBean);
                        if (!discardList.isEmpty())
                            discardList.remove(ttsBean);
                    }
                }
            }
        }
    }

    private static void buriedPointData(PlayTTSBean endTtsBean, int status, String tts_status, String tts_time_end) {
        //埋点
        if (ITtsResolverCallBack.ResolverStatus.RESOLVER_START == status) {
            BuriedPointManager.getInstance().setBuriedPointData(endTtsBean.getOriginTtsId(), tts_status, "", tts_time_end, "", "");
        } else {
            BuriedPointManager.getInstance().lastSetBuriedPointData(endTtsBean.getOriginTtsId(), tts_status, "", tts_time_end, "", "");
        }
    }

    private static boolean isVoicePackName(String packName) {
        return TextUtils.equals(Constant.VOICE_PACKAGE_NAME, packName);
    }

    /**
     * 回调当前播报的位置信息
     *
     * @param nearPlay 是否为就近播报
     * @param position 指定的声源位置
     * @param code     开始合成
     */
    private static void reportPlayStatus(boolean nearPlay, int position, int code) {
        LogUtils.d(TAG, "reportPlayStatus: position:" + position + " ,nearPlay:" + nearPlay + " ,code:" + code);
        synchronized (generalLock) {
            if (generalTtsList.isEmpty())
                return;
            int[] nearPosition = ProximityInteractionManager.getInstance().generalSts(nearPlay, position, code);
            for (IGeneralTtsCallback iGeneralTtsCallback : generalTtsList) {
                try {
                    iGeneralTtsCallback.onGeneralTtsStatus(nearPosition);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
