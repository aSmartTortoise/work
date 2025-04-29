package com.voyah.ai.sdk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.voyah.ai.sdk.listener.ITtsPlayListener;


/**
 * binder远程服务辅助类
 */
public class TtsBinderConnector extends AbsBinderConnector {

    public static final String TAG = "TtsConnector";

    /**
     * 启动TTS服务的action和包名
     */
    private static final String HOST_TTS_SERVICE_ACTION = "com.voyah.vcos.tts.aidlService";
    private static final String HOST_TTS_SERVICE_PACKAGE = "com.voyah.vcos.ttsservices";

    private ITTSService mTtsServiceInterface;

    public TtsBinderConnector(Context context) {
        super(context);
    }


    @Override
    public void bindService() {
        Log.d(TAG, "bindService() called, mPackageName:" + mPackageName + ", id:" + id + ", SDK FLAVOR: 1.5");
        remoteReadyImpl = new RemoteSpeechReadyImpl();
        Intent intent = new Intent(HOST_TTS_SERVICE_ACTION);
        intent.setPackage(HOST_TTS_SERVICE_PACKAGE);
        boolean ret = context.bindService(intent, mTtsBinderConnection, Context.BIND_AUTO_CREATE);
        Log.i(TAG, "bindService ret: " + ret);
        super.bindService();
    }

    @Override
    public void unBindService() {
        Log.d(TAG, "unBindService() called, isAlive:" + isAlive());
        if (isAlive()) {
            context.unbindService(mTtsBinderConnection);
            mTtsServiceInterface = null;
        }
        super.unBindService();
    }

    @Override
    public boolean isAlive() {
        return mTtsServiceInterface != null && mTtsServiceInterface.asBinder().isBinderAlive();
    }

    @Override
    public boolean isAppReInstalled(String pkgName) {
        return HOST_TTS_SERVICE_PACKAGE.equals(pkgName);
    }

    private final ServiceConnection mTtsBinderConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.w(TAG, "onServiceDisconnected() called with: name = [" + name + "]");
            mTtsServiceInterface = null;
            handler.removeCallbacksAndMessages(null);
            handler.postDelayed(() -> bindService(), 500);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.w(TAG, "onServiceConnected() called with: name = [" + name + "], service = [" + service + "]");
            try {
                // 注册 onSpeechReadyListener 到 service
                mTtsServiceInterface = ITTSService.Stub.asInterface(service);
                mTtsServiceInterface.registerReadyCallback(mPackageName, remoteReadyImpl);
                mTtsServiceInterface.asBinder().linkToDeath(mBinderPoolDeathRecipient, 0);

                if (isRemoteReady()) {
                    executeRemoteReady();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };


    /**
     * 判断远程服务是否准备就绪
     *
     * @return 是否准备就绪
     */
    public boolean isRemoteReady() {
        boolean isRemoteReady = false;
        if (isAlive()) {
            try {
                isRemoteReady = mTtsServiceInterface.isRemoteReady();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return isRemoteReady;
    }

    /**
     * 播报指定文本内容
     */
    public void speak(String text, ITtsPlayListener listener, String speaker, boolean highPriority) {
        try {
            if (isAlive()) {
                mTtsServiceInterface.speak(mPackageName, text, new RemoteTtsCallbackImpl(listener), speaker, highPriority);
            } else {
                Log.e(TAG, "serviceInterface is died");
                if (listener != null) {
                    listener.onPlayBeginning(text);
                    listener.onPlayError(text, ITtsPlayListener.REASON.OTHERS);
                }
                bindService();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 播报指定文本内容
     */
    public void speak(String text, ITtsPlayListener listener, String speaker, boolean highPriority, int usage) {
        try {
            if (isAlive()) {
                mTtsServiceInterface.speakWithUsage(mPackageName, text, new RemoteTtsCallbackImpl(listener), speaker, highPriority, usage);
            } else {
                Log.e(TAG, "serviceInterface is died");
                if (listener != null) {
                    listener.onPlayBeginning(text);
                    listener.onPlayError(text, ITtsPlayListener.REASON.OTHERS);
                }
                bindService();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 播报指定文本内容
     */
    public void speak(String text, ITtsPlayListener listener, String speaker, boolean highPriority, int usage, int location) {
        try {
            if (isAlive()) {
                mTtsServiceInterface.speakWithUsageAndLocation(mPackageName, text, new RemoteTtsCallbackImpl(listener), speaker, highPriority, usage, location);
            } else {
                Log.e(TAG, "serviceInterface is died");
                if (listener != null) {
                    listener.onPlayBeginning(text);
                    listener.onPlayError(text, ITtsPlayListener.REASON.OTHERS);
                }
                bindService();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void speakInformationBean(VoiceTtsBean voiceTtsBean, ITtsPlayListener listener, String speaker, boolean highPriority) {
        try {
            if (isAlive()) {
                if (null != voiceTtsBean)
                    voiceTtsBean.setTtsVoice(speaker);
                mTtsServiceInterface.speakBean(mPackageName, voiceTtsBean, new RemoteTtsCallbackImpl(listener));
            } else {
                Log.e(TAG, "serviceInterface is died");
                if (listener != null) {
                    listener.onPlayBeginning(voiceTtsBean.getTts());
                    listener.onPlayError(voiceTtsBean.getTts(), ITtsPlayListener.REASON.OTHERS);
                }
                bindService();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void speakBean(VoiceTtsBean voiceTtsBean, ITtsPlayListener listener) {
        try {
            if (isAlive()) {
                mTtsServiceInterface.speakBean(mPackageName, voiceTtsBean, new RemoteTtsCallbackImpl(listener));
            } else {
                Log.e(TAG, "serviceInterface is died");
                if (listener != null) {
                    listener.onPlayBeginning(voiceTtsBean.getTtsId());
                    listener.onPlayError(voiceTtsBean.getTtsId(), ITtsPlayListener.REASON.OTHERS);
                }
                bindService();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 流式TTS播报(不可用)
     *
     * @param text         tts文本
     * @param listener     回调
     * @param streamStatus 流式请求状态 0:流式合成开始(第一片) 1:流式分片 2:流式合成结尾(最后一片))
     */
    public void speakStream(String text, ITtsPlayListener listener, int streamStatus) {
        try {
            if (isAlive()) {
                mTtsServiceInterface.streamSpeak(mPackageName, text, new RemoteTtsCallbackImpl(listener), streamStatus);
            } else {
                Log.e(TAG, "serviceInterface is died");
                if (listener != null) {
                    listener.onPlayBeginning(text);
                    listener.onPlayError(text, ITtsPlayListener.REASON.OTHERS);
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void speakStreamBean(VoiceTtsBean voiceTtsBean, ITtsPlayListener listener, int streamStatus) {
        try {
            if (isAlive()) {
                mTtsServiceInterface.streamSpeakBean(mPackageName, voiceTtsBean, new RemoteTtsCallbackImpl(listener), streamStatus);
            } else {
                Log.e(TAG, "serviceInterface is died");
                if (listener != null) {
                    listener.onPlayBeginning(voiceTtsBean.getTtsId());
                    listener.onPlayError(voiceTtsBean.getTtsId(), ITtsPlayListener.REASON.OTHERS);
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止语音合成
     */
    public void stopCurTts() {
        try {
            if (isAlive()) {
                mTtsServiceInterface.stopCurTts(mPackageName);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * TTS 语音停止所有播报
     */
    public void shutUp() {
        try {
            if (isAlive()) {
                mTtsServiceInterface.shutUp(mPackageName);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * TTS 停止语音通道中语音发起的播报请求
     */
    public void shutUpOneSelf() {
        try {
            if (isAlive()) {
                mTtsServiceInterface.shutUpOneSelf(mPackageName);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 指定通道
     */
    public void setUsage(int usage) {
        try {
            if (isAlive()) {
                mTtsServiceInterface.setUsage(mPackageName, usage);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean isSpeaking(int usage) {
        try {
            if (isAlive()) {
                return mTtsServiceInterface.isSpeaking(usage);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void setTtsSpeaker(String speaker) {
        try {
            if (isAlive()) {
                mTtsServiceInterface.setTtsSpeaker(mPackageName, speaker);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setNearByTtsStatus(boolean isNearByTts) {
        try {
            if (isAlive()) {
                mTtsServiceInterface.setNearByTtsStatus(isNearByTts);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void forbiddenNearByTts(boolean isForbidden) {
        try {
            if (isAlive()) {
                mTtsServiceInterface.forbiddenNearByTts(isForbidden);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    public boolean requestAudioFocusByUsage(int usage, int type) {
        boolean result = false;
        try {
            if (isAlive()) {
                result = mTtsServiceInterface.requestAudioFocusByUsage(mPackageName, usage, type);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void stopById(String originTtsId) {
        try {
            if (isAlive()) {
                mTtsServiceInterface.stopById(mPackageName, originTtsId);
            } else {
                Log.e(TAG, "serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void registerGeneralTtsStatus(IGeneralTtsCallback callback) {
        try {
            if (isAlive()) {
                mTtsServiceInterface.registerGeneralTtsStatus(mPackageName, callback);
            } else {
                Log.e(TAG, "unregisterGeneralTtsStatus serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void unregisterGeneralTtsStatus(IGeneralTtsCallback callback) {
        try {
            if (isAlive()) {
                mTtsServiceInterface.unregisterGeneralTtsStatus(mPackageName, callback);
            } else {
                Log.e(TAG, "unregisterGeneralTtsStatus serviceInterface is died");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    /**
     * 释放
     */
    public void release() {
        try {
            if (isAlive()) {
                if (remoteReadyImpl != null) {
                    mTtsServiceInterface.unregisterReadyCallback(mPackageName, remoteReadyImpl);
                    remoteReadyImpl = null;
                }
                unBindService();
            }
            mReadyListeners.clear();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 远程服务死亡监听
     */
    private final IBinder.DeathRecipient mBinderPoolDeathRecipient = new IBinder.DeathRecipient() {

        @Override
        public void binderDied() {
            Log.e(TAG, "binder died.");
            if (mTtsServiceInterface != null) {
                mTtsServiceInterface.asBinder().unlinkToDeath(mBinderPoolDeathRecipient, 0);
                mTtsServiceInterface = null;
            }
            Log.e(TAG, "reBind service...");
            handler.removeCallbacksAndMessages(null);
            handler.postDelayed(() -> bindService(), 500);
        }
    };

    /**
     * TTS远程回调
     */
    private static class RemoteTtsCallbackImpl extends ITtsCallback.Stub {

        private final ITtsPlayListener listener;

        public RemoteTtsCallbackImpl(ITtsPlayListener listener) {
            this.listener = listener;
        }

        @Override
        public void onTtsBeginning(String text) throws RemoteException {
            if (listener != null) {
                listener.onPlayBeginning(text);
            }
        }

        @Override
        public void onTtsEnd(String text, int reason) throws RemoteException {
            if (listener != null) {
                listener.onPlayEnd(text, reason);
            }
        }

        @Override
        public void onTtsError(String text, int errCode) throws RemoteException {
            if (listener != null) {
                listener.onPlayError(text, errCode);
            }
        }
    }
}
