package com.voyah.vcos.ttsservices.manager;


import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;

import com.voyah.vcos.ttsservices.AppContext;
import com.voyah.vcos.ttsservices.factory.IVoiceTts;
import com.voyah.vcos.ttsservices.info.PlayTTSBean;
import com.voyah.vcos.ttsservices.utils.LogUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author:lcy TTS管理基类
 * @data:2024/1/30
 **/

//todo:完善父类实现-
public abstract class TtsAbstract implements IVoiceTts {
    private String TAG = "TtsAbstract";

    private static final int MSG_INIT = 0;
    private static final int MSG_START = 1;
    private static final int MSG_STOP = 2;
    private static final int MSG_RELEASE = 3;
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();


    protected volatile Context mContext;
    protected int mUsage;
    protected String curReqId;
    private Handler handler;

    private HandlerThread handlerThread;

    private VoiceTtsListener mTtsListener;


    @Override
    public void init(Context context, int usage) {
        TAG = TAG + "-" + usage;
        LogUtils.d(TAG, "init " + (context == null));
        //
        if (context == null)
            this.mContext = AppContext.instant;
        else
            this.mContext = context;
        this.mUsage = usage;
        initHandler();
        handler.sendEmptyMessage(MSG_INIT);
    }

    private void initHandler() {
        handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                LogUtils.i(TAG, "msg is " + (msg.obj != null ? msg.obj.toString() : "msg.obj is null") + " ,msg.what is " + msg.what + " ,mUsage is " + mUsage);
                switch (msg.what) {
                    case MSG_INIT:
                        initTTS();
                        break;
                    case MSG_START:
                        if (msg.obj instanceof PlayTTSBean) {
                            PlayTTSBean bean = (PlayTTSBean) msg.obj;
                            bean.setUsage(mUsage);
                            start(bean);
                        }
                        break;
                    case MSG_STOP:
                        stop();
                        break;
                    case MSG_RELEASE:
                        release();
                        break;
                }
            }
        };

    }

    @Override
    public void setTtsListener(VoiceTtsListener voiceTtsListener) {
        this.mTtsListener = voiceTtsListener;
    }

    @Override
    public void playTts(PlayTTSBean voiceTtsBean) {
        LogUtils.d(TAG, "playTts usage:" + mUsage);
        if (null != handler) {
            Message message = handler.obtainMessage();
            message.what = MSG_START;
            message.obj = voiceTtsBean;
            handler.removeMessages(MSG_START);
            handler.sendMessage(message);
        }
    }

    public synchronized void playStreamTts(PlayTTSBean voiceTtsBean) {
        LogUtils.d(TAG, "playStreamTts usage:" + mUsage);
        if (null != handler) {
            Message message = handler.obtainMessage();
            message.what = MSG_START;
            message.obj = voiceTtsBean;
            if (voiceTtsBean.getStreamStatus() == 0)
                handler.removeMessages(MSG_START);
            handler.sendMessage(message);
        }
    }

    @Override
    public void stopTts() {
        LogUtils.d(TAG, "stopTts usage:" + mUsage);
        if (null != handler) {
//            handler.removeMessages(MSG_START);
            handler.sendEmptyMessage(MSG_STOP);
        }
    }

    @Override
    public boolean isSyncing() {
        LogUtils.d(TAG, "isSyncing curReqId:" + curReqId);
        return !TextUtils.isEmpty(curReqId);
    }

    @Override
    public void destroy() {

    }

    @Override
    public void onAudioFocus() {

    }

    protected void onStart(String ttsId, int ttsType) {
        LogUtils.i(TAG, "onStart --- " + ttsId + " ,curReqId:" + curReqId + " ， ttsType : " + ttsType + " ,mUsage is " + mUsage);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                if (mTtsListener != null)
                    mTtsListener.onTtsStart(ttsId, ttsType);
            }
        });
    }

    protected void onError(String ttsId, int code, String errMsg, int ttsType) {
        LogUtils.i(TAG, "onError --- " + ttsId + " ,curReqId:" + curReqId + " ,code:" + code + " ,msg: " + errMsg + " ,mUsage is " + mUsage);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                if (mTtsListener != null) {
                    if (TextUtils.equals(curReqId, ttsId))
                        curReqId = "";
                    mTtsListener.onTtsError(ttsId, code, errMsg, ttsType);
                }
            }
        });
    }

    protected void onDiscard(String ttsId, int code, String errMsg, int ttsType) {
        LogUtils.i(TAG, "onDiscard --- " + ttsId + " ,curReqId:" + curReqId + " ,code:" + code + " ,msg: " + errMsg + " ,mUsage is " + mUsage);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                if (mTtsListener != null) {
                    if (TextUtils.equals(curReqId, ttsId))
                        curReqId = "";
                    mTtsListener.onTtsDiscard(ttsId, code, errMsg, ttsType);
                }
            }
        });
    }


    protected void onStop(String ttsId, int ttsType) {
        LogUtils.i(TAG, "onStop --- " + ttsId + " ,curReqId:" + curReqId + " ,ttsType:" + ttsType + " ,mUsage:" + mUsage);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                if (mTtsListener != null) {
                    if (TextUtils.equals(curReqId, ttsId))
                        curReqId = "";
                    mTtsListener.onTtsStop(ttsId, ttsType);
                }
            }
        });
    }

    protected void onComplete(String ttsId, int ttsType) {
        LogUtils.i(TAG, "onComplete --- " + ttsId + " ,curReqId:" + curReqId + " ,ttsType:" + ttsType + " ,mUsage:" + mUsage);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                if (mTtsListener != null) {
                    if (TextUtils.equals(curReqId, ttsId))
                        curReqId = "";
                    mTtsListener.onTtsComplete(ttsId, ttsType);
                }
            }
        });

    }


    protected void postRunnable(Runnable runnable) {
        if (handler != null) {
            handler.post(runnable);
        }
    }

    protected abstract void initTTS();

    protected abstract void start(PlayTTSBean voiceTtsBean);

    protected abstract void stop();

    protected abstract void release();

//    protected abstract boolean requestFocus(int type);
}
