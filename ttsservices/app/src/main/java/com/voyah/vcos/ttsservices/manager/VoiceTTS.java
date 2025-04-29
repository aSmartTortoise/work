package com.voyah.vcos.ttsservices.manager;

import static com.voyah.vcos.ttsservices.manager.ITtsResolverCallBack.ResolverStatus.RESOLVER_COMPLETED;
import static com.voyah.vcos.ttsservices.manager.ITtsResolverCallBack.ResolverStatus.RESOLVER_DISCARD;
import static com.voyah.vcos.ttsservices.manager.ITtsResolverCallBack.ResolverStatus.RESOLVER_FAIL;
import static com.voyah.vcos.ttsservices.manager.ITtsResolverCallBack.ResolverStatus.RESOLVER_START;

import android.text.TextUtils;

import com.blankj.utilcode.util.GsonUtils;
import com.voyah.vcos.ttsservices.AppContext;
import com.voyah.vcos.ttsservices.audio.AudioDataHandlerThread;
import com.voyah.vcos.ttsservices.audio.AudioTrack;
import com.voyah.vcos.ttsservices.audio.AudioTrackCallBack;
import com.voyah.vcos.ttsservices.audio.TtsAudioFocusManager;
import com.voyah.vcos.ttsservices.info.PlayTTSBean;
import com.voyah.vcos.ttsservices.manager.stream.IStreamTtsResolver;
import com.voyah.vcos.ttsservices.mem.ByteArrMgr;
import com.voyah.vcos.ttsservices.mem.TtsByteArrMgr;
import com.voyah.vcos.ttsservices.utils.AppUtils;
import com.voyah.vcos.ttsservices.utils.LogUtils;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author:lcy TTS播报流程管理类(管理初始化 、 合成请求 、 状态管理 、 数据下发)
 * @data:2024/1/30
 **/
public class VoiceTTS extends TtsAbstract {
    private String TAG = "VoiceTTS";

    private static final int REQUEST_LOSS_FOCUS = 0;
    private static final int REQUEST_HOLD_FOCUS = 1;
    private AudioTrack audioTrack;
    private AudioDataHandlerThread dataHandlerThread;

    private TtsAudioFocusManager mTtsAudioFocusManager;
    private ITtsResolver mTtsResolver;

    private IStreamTtsResolver mStreamTtsResolver;

    private ITtsResolverCallBack iTtsResolverCallBack;

    private boolean isStop;

    //ttsId与tts实例做关联绑定记录
    private ConcurrentHashMap<String, ITtsResolver> ttsResolverMap = new ConcurrentHashMap<>();


    @Override
    protected void initTTS() {
        TAG = TAG + "-" + mUsage;
        initAudioHandler(TAG);
        TtsByteArrMgr.getInstance().init(mUsage);
        initCallback();
        initAudioTrack();
        initAudioFocus();
    }

    private void initAudioHandler(String threadName) {
        //创建自定义指定优先级的handler
        dataHandlerThread = new AudioDataHandlerThread(threadName);
        dataHandlerThread.start();
    }

    private void initCallback() {
        iTtsResolverCallBack = new ITtsResolverCallBack() {
            @Override
            public void onStatus(String ttsId, int playStatus, int ttsType) {
                //todo:判断 ttsId及playStatus状态
                LogUtils.i(TAG, "iTtsResolverCallBack onStatus  ttsId is " + ttsId + " ,curReqId is " + curReqId + " ,playStatus is "
                        + playStatus + " ,mUsage is " + mUsage);
                if (!TextUtils.equals(ttsId, curReqId) && !TextUtils.isEmpty(ttsId) && !TextUtils.isEmpty(curReqId))
                    return;
                postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        switch (playStatus) {
                            case RESOLVER_START:
//                                onStart(ttsId, ttsType);
                                start(ttsId, ttsType);
                                break;
                            case RESOLVER_COMPLETED:
                                releaseTts(ttsId);
//                                onComplete(ttsId, ttsType);
                                complete(ttsId, ttsType);
                                break;
                            case RESOLVER_FAIL:
                                LogUtils.d(TAG, "ttsId:" + ttsId + " ,curReqId:" + curReqId);
                                if (TextUtils.equals(ttsId, curReqId)) {
                                    releaseTts(ttsId);
                                    stopAudioTrack();
                                }
                                onError(ttsId, TtsResultCode.OTHER, "unknown error", ttsType);
                                break;
                            case RESOLVER_DISCARD:
                                LogUtils.d(TAG, "ttsId:" + ttsId + " ,curReqId:" + curReqId);
                                releaseTts(ttsId);
//                                onError(ttsId, TtsResultCode.DISCARD, "low priority data is discarded", ttsType);
                                onDiscard(ttsId, TtsResultCode.DISCARD, "low priority data is discarded", ttsType);
                                break;
                        }
                    }
                });
            }

            //            @Override
//            public void onSyncError(String ttsId, int errorCode, String msg, int ttsType) {
//                onError(ttsId, errorCode, msg, ttsType);
//            }

            private void start(String ttsId, int ttsType) {
                onStart(ttsId, ttsType);
            }

            private void error(String ttsId, int ttsType) {
                onError(ttsId, TtsResultCode.DISCARD, "low priority data is discarded", ttsType);
            }

            private void complete(String ttsId, int ttsType) {
                onComplete(ttsId, ttsType);
            }

            @Override
            public void onData(String ttsId, ByteArrMgr.ByteArrObj arrObject, int ttsType) {
                postAudioDataRunnable(new Runnable() {
                    @Override
                    public void run() {
                        if (arrObject.len == 0)
                            LogUtils.d(TAG, "onData postAudioDataRunnable ttsId last buffer");
                        audioTrack.appendAudioData(arrObject.arr, arrObject.len, ttsId, ttsType);
                    }
                });
            }

            @Override
            public void onData(String ttsId, byte[] buffer) {
//            postRunnable(new Runnable() {
//                @Override
//                public void run() {
                //传音频
//                audioTrack.appendAudioData(buffer, buffer.length);
//                }
//            });
            }
        };
    }

    @Override
    protected void start(PlayTTSBean voiceTtsBean) {
        LogUtils.i(TAG, "start ttsBean is " + GsonUtils.toJson(voiceTtsBean));
//        stop();
        //todo:修改为焦点申请成功后再发起合成(焦点申请需要为同步接口)
        if (requestFocus()) {
            isStop = false;
            curReqId = voiceTtsBean.getTtsId();
            if (voiceTtsBean.getStreamStatus() == 0 || voiceTtsBean.getStreamStatus() == -1)
                flushAudioTrack();
            if (voiceTtsBean.getStreamStatus() != 1)
                startAudio();
            startMicSyn(voiceTtsBean);
        } else {
            LogUtils.d(TAG, "request focus fail");
            onError(voiceTtsBean.getTtsId(), ITtsResolverCallBack.TtsResultCode.OTHER, "unknown error", voiceTtsBean.getStreamStatus() != -1 ? 1 : 0);
        }
    }

    private boolean requestFocus() {
        if (mTtsAudioFocusManager.hasFocus(mUsage))
            return true;

        LogUtils.d(TAG, "request audio focus");
        return mTtsAudioFocusManager.requestAudioFocusSync(mUsage);
    }

    @Override
    protected void stop() {
        isStop = true;
        clearRunnable();
        stopMicSyn();
        stopStreamSyn();
        stopAudioTrack();
    }


    @Override
    protected void release() {
        curReqId = "";
    }

    public boolean requestFocus(int usage, int type) {
        LogUtils.d(TAG, "requestFocus usage:" + usage + " ,type:" + type);
        boolean request;
        if (REQUEST_HOLD_FOCUS == type) {
            request = mTtsAudioFocusManager.requestAudioFocusSync(usage);
            LogUtils.d(TAG, "requestFocus request:" + request);
            mTtsAudioFocusManager.setHoldFocus(usage, request);
        } else {
            if (isSyncing())
                mTtsAudioFocusManager.setHoldFocus(usage, false);
            else
                mTtsAudioFocusManager.releaseAudioFocus(usage, type);
            request = true;
        }
        return request;
    }

    private void initAudioTrack() {
        LogUtils.i(TAG, "initAudioTrack   mUsage is " + mUsage);
        audioTrack = new AudioTrack();
        audioTrack.init(mContext, mUsage);
        audioTrack.setAudioTrackCallBack(new AudioTrackCallBack() {
            @Override
            public void complete(String ttsId, int ttsType) {
                LogUtils.d(TAG, "audioTrack complete ttsId:" + ttsId);
//                if (null != iTtsResolverCallBack)
//                    iTtsResolverCallBack.onStatus(ttsId, ITtsResolverCallBack.ResolverStatus.RESOLVER_COMPLETED, ttsType);
                if (null != mTtsResolver)
                    mTtsResolver.playEnd(ttsId, ttsType);
                if (null != mStreamTtsResolver)
                    mStreamTtsResolver.playEnd(ttsId, ttsType);
//                if (null!=mStreamTtsResolver)
//                    mStreamTtsResolver.
            }
        });
    }

    private void initAudioFocus() {
        LogUtils.i(TAG, "initAudioFocus " + " ,mUsage is " + mUsage);
        mTtsAudioFocusManager = TtsAudioFocusManager.getInstance();
        mTtsAudioFocusManager.init(mContext, mUsage);
        mTtsAudioFocusManager.addBeanAudioListener(mUsage, audioListener);
    }

    private void startAudio() {
        LogUtils.i(TAG, "startAudioTrack" + " ,mUsage is " + mUsage);
//        audioTrack = new AudioTrack();
//        audioTrack.init(mContext, mUsage);
        audioTrack.play();
    }

    private void stopAudioTrack() {
        LogUtils.i(TAG, "stopAudioTrack" + " ,mUsage is " + mUsage);
        audioTrack.stop();
//        AudioFocusUtils.abandonAudioFocus(mContext);
//        mTtsAudioFocusManager.releaseAudioFocus(mUsage);
    }

    private void flushAudioTrack() {
        LogUtils.i(TAG, "flushAudioTrack" + " ,mUsage is " + mUsage);
        audioTrack.flush();
    }

    private void releaseAudioTrack() {
        LogUtils.d(TAG, "releaseAudioTrack");
        audioTrack.release();
    }

    private void startMicSyn(PlayTTSBean voiceTtsBean) {
        int streamStatus = voiceTtsBean.getStreamStatus();
        //todo:判断是否需要复刻合成-流式只在streamStatus==0时选择是否复刻
        CopyMsgInfo copyMsgInfo = SpeakerManager.getInstance().getCopyMsgInfo();
        boolean isNeedCopySynthesis = null != copyMsgInfo;
        LogUtils.d(TAG, "startMicSyn streamStatus is " + streamStatus + " ,tts is " + voiceTtsBean + " ,mUsage is " + mUsage
                + " ,isNeedCopySynthesis is " + isNeedCopySynthesis + " ,appVersion:" + AppUtils.getAppVersionName(AppContext.instant));
        if (streamStatus == -1) {
            if (isNeedCopySynthesis) {
                PlayTTSBean.VoiceCopyBean voiceCopyBean = new PlayTTSBean.VoiceCopyBean();
                voiceCopyBean.setVoiceName(copyMsgInfo.getVoiceName());
                voiceCopyBean.setProfileId(copyMsgInfo.getProfileId());
                voiceCopyBean.setVoiceSex(copyMsgInfo.getVoiceSex());
                voiceTtsBean.setVoiceCopyBean(voiceCopyBean);
                mTtsResolver = TtsResolverManager.getInstance().getCopyMcResolver();
            } else {
                mTtsResolver = TtsResolverManager.getInstance().getMcResolver();
            }

            ttsResolverMap.put(voiceTtsBean.getTtsId(), mTtsResolver);
            mTtsResolver.setTtsResolverCallBack(iTtsResolverCallBack);
            //todo:传参要带有ttsId及带合成
            mTtsResolver.startSynthesis(voiceTtsBean);
        } else {
            if (streamStatus == 0) {
                if (isNeedCopySynthesis) {
                    PlayTTSBean.VoiceCopyBean voiceCopyBean = new PlayTTSBean.VoiceCopyBean();
                    voiceCopyBean.setVoiceName(copyMsgInfo.getVoiceName());
                    voiceCopyBean.setProfileId(copyMsgInfo.getProfileId());
                    voiceCopyBean.setVoiceSex(copyMsgInfo.getVoiceSex());
                    voiceTtsBean.setVoiceCopyBean(voiceCopyBean);
                    mStreamTtsResolver = TtsResolverManager.getInstance().getCopyStreamTtsResolver();
                } else {
                    mStreamTtsResolver = TtsResolverManager.getInstance().getStreamTtsResolver();
                }

                mStreamTtsResolver.setTtsResolverCallBack(iTtsResolverCallBack);
                mStreamTtsResolver.beginStream(voiceTtsBean);
                mStreamTtsResolver.append(voiceTtsBean);
            } else if (streamStatus == 2) {
                mStreamTtsResolver.append(voiceTtsBean);
                mStreamTtsResolver.endStream();
            } else if (streamStatus == 1) {
                mStreamTtsResolver.append(voiceTtsBean);
            }
        }
    }

    private void stopMicSyn() {
        //todo:停止播报时只能停止当前的
        LogUtils.i(TAG, "stopMicSyn" + " ,mUsage is " + mUsage);
        if (mTtsResolver != null)
            mTtsResolver.stopSynthesis();
//        releaseTts();
    }

    //todo:结束流式合成
    private void stopStreamSyn() {
        LogUtils.i(TAG, "stopStreamSyn" + " ,mUsage is " + mUsage);
        if (mStreamTtsResolver != null)
            mStreamTtsResolver.stopSynthesis();
    }

    private void releaseTts(String ttsId) {
        LogUtils.d(TAG, "releaseTts");
        ITtsResolver iTtsResolver = null;
        if (ttsResolverMap.containsKey(ttsId)) {
            iTtsResolver = ttsResolverMap.get(ttsId);
            ttsResolverMap.remove(ttsId);
        }
        if (null != iTtsResolver) {
            LogUtils.i(TAG, "releaseTts: mMicTtsResolver" + " ,mUsage is " + mUsage + " ,ttsId:" + ttsId + " ,curReqId:" + curReqId + " , iTtsResolver is null " + (null == iTtsResolver));
            TtsResolverManager.getInstance().recycleMcResolver(iTtsResolver);
            if (TextUtils.equals(ttsId, curReqId))
                mTtsResolver = null;
        }
    }

    private TtsAudioFocusManager.BeanAudioListener audioListener = new TtsAudioFocusManager.BeanAudioListener() {
        @Override
        public void onBeforeRequest() {

        }

        @Override
        public void onBeforeRelease() {

        }

        @Override
        public void onAudioFocusChange(boolean audioFocus) {
            LogUtils.i(TAG, "BeanAudioListener onAudioFocusChange audioFocus is " + audioFocus + " , usage is " + mUsage);
            if (!audioFocus) {
                stop();
                TtsAudioFocusManager.getInstance().releaseAudioFocus(mUsage, 2);
            }
        }

        @Override
        public void onAudioFocusObtain(boolean audioFocus) {
            LogUtils.i(TAG, "BeanAudioListener onAudioFocusObtain audioFocus is " + audioFocus + " , usage is " + mUsage);
        }
    };

    private void postAudioDataRunnable(Runnable runnable) {
        if (dataHandlerThread != null && !isStop) {
            dataHandlerThread.post(runnable);
        } else {
            LogUtils.d(TAG, "postAudioDataRunnable dataHandlerThread is null:" + (null == dataHandlerThread) + " ,isStop:" + isStop);
        }
    }

    private void clearRunnable() {
        LogUtils.d(TAG, "clearRunnable");
        if (dataHandlerThread != null) {
            dataHandlerThread.clear();
        }
    }

}
