package com.voyah.vcos.ttsservices.audio;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;

import com.voyah.vcos.ttsservices.Constant;
import com.voyah.vcos.ttsservices.utils.LogUtils;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author:lcy
 * @data:2024/4/26
 **/
public class TtsAudioFocusManager {
    private static final String TAG = "TtsAudioFocusManager";

    private static final Object holdFocusLock = new Object();

    private Map<Integer, AudioFocusProxy> audioFocusProxyMap = new ConcurrentHashMap<>();

    private TtsAudioFocusManager() {
    }


    private static class InnerHolder {
        private static final TtsAudioFocusManager instance = new TtsAudioFocusManager();
    }

    public static TtsAudioFocusManager getInstance() {
        return InnerHolder.instance;
    }

    public void init(Context context, int usage) {
        AudioFocusProxy audioFocusProxy = audioFocusProxyMap.get(usage);
        if (null == audioFocusProxy) {
            audioFocusProxy = new AudioFocusProxy(context, usage);
            audioFocusProxyMap.put(usage, audioFocusProxy);
        }
    }

    public void requestAudioFocus(int usage) {
        LogUtils.i(TAG, "requestAudioFocus usage is " + usage);
        if (audioFocusProxyMap.containsKey(usage)) {
            audioFocusProxyMap.get(usage).requestAudioFocus();
        } else {
            LogUtils.i(TAG, "requestAudioFocus No usage : " + usage);
        }
    }

    public boolean requestAudioFocusSync(int usage) {
        boolean requestFocusSuccess = false;
        LogUtils.i(TAG, "requestAudioFocusSync usage is " + usage);
        if (audioFocusProxyMap.containsKey(usage)) {
            requestFocusSuccess = audioFocusProxyMap.get(usage).requestAudioFocusSync();
        } else {
            LogUtils.i(TAG, "requestAudioFocus No usage : " + usage);
        }
        LogUtils.d(TAG, "requestAudioFocusSync usage:" + usage + " ,requestFocusSuccess:" + requestFocusSuccess);
        if (!requestFocusSuccess)
            releaseAudioFocus(usage, 3);
        return requestFocusSuccess;
    }

    /**
     * @param usage  通道
     * @param source 释放调用方 1:外部主动释放 2:焦点丢失释放 3:TTS服务内部逻辑释放
     */
    public void releaseAudioFocus(int usage, int source) {
        if (audioFocusProxyMap.containsKey(usage)) {
            AudioFocusProxy audioFocusProxy = audioFocusProxyMap.get(usage);
            boolean isHoldFocus = audioFocusProxy.isHoldFocus;
            LogUtils.i(TAG, "releaseAudioFocus usage is " + usage + " ,isHoldFocus is " + isHoldFocus + " ,source:" + source);
            if (isHoldFocus && source == 3) {
                LogUtils.d(TAG, "need hold the focus");
            } else {
                audioFocusProxy.releaseAudioFocus();
                audioFocusProxy.setHoldFocus(false);
            }
        } else {
            LogUtils.i(TAG, "releaseAudioFocus No usage : " + usage);
        }
    }

    public void addBeanAudioListener(int usage, BeanAudioListener listener) {
        LogUtils.i(TAG, "addBeanAudioListener usage is " + usage);
        if (audioFocusProxyMap.containsKey(usage)) {
            audioFocusProxyMap.get(usage).addBeanAudioListener(listener);
        } else {
            LogUtils.i(TAG, "addBeanAudioListener No usage : " + usage);
        }
    }

    public boolean hasFocus(int usage) {
        if (audioFocusProxyMap.containsKey(usage)) {
            return audioFocusProxyMap.get(usage).hasFocus();
        } else {
            LogUtils.i(TAG, "hasFocus No usage : " + usage);
        }
        return false;
    }

    public void setHoldFocus(int usage, boolean request) {
        LogUtils.d(TAG, "setHoldFocus usage:" + usage + " ,request:" + request);
        if (audioFocusProxyMap.containsKey(usage)) {
            AudioFocusProxy audioFocusProxy = audioFocusProxyMap.get(usage);
            audioFocusProxy.setHoldFocus(request);
        }
    }


    private static class AudioFocusProxy implements IBeanDump {

        private static final long DELAY_REQ = 500; //焦点请求超时时长
        private AudioManager audioManager;
        private AudioFocusRequest audioFocusRequest;
        protected volatile boolean audioFocus = false;
        private int usage;

        private boolean isHoldFocus = false;

        private Context context;
        private ArrayList<BeanAudioListener> beanAudioListenerList = new ArrayList<>();

        private Handler handler;
        private HandlerThread handlerThread;

        private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener =
                new AudioManager.OnAudioFocusChangeListener() {
                    @Override
                    public void onAudioFocusChange(int focusChange) {
                        LogUtils.i(TAG, "AudioFocusProxy onAudioFocusChange:" + focusChange + ",usage:" + usage);
                        switch (focusChange) {
                            case AudioManager.AUDIOFOCUS_GAIN:
                            case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                            case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE:
                            case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
//                                audioFocus = true;
//                                notifyChanged(true);
                                break;
                            case AudioManager.AUDIOFOCUS_LOSS:
                            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                            default:
                                audioFocus = false;
                                notifyChanged(false);
                                break;
                        }
                    }
                };

        private void notifyChanged(boolean flag) {
            for (BeanAudioListener beanAudioListener : beanAudioListenerList) {
                beanAudioListener.onAudioFocusChange(flag);
            }
        }

        private void notifyReqRlt(boolean flag) {
            for (BeanAudioListener beanAudioListener : beanAudioListenerList) {
                beanAudioListener.onAudioFocusObtain(flag);
            }
        }

        private void initHandler() {
            if (handler == null) {
                handlerThread = new HandlerThread("AudioFocusProxy#" + usage, Process.THREAD_PRIORITY_MORE_FAVORABLE);
                handlerThread.start();
                handler = new Handler(handlerThread.getLooper());
            }
        }

        private AudioFocusProxy(Context context, int usage) {
            this.usage = usage;
            this.context = context;
            AudioAttributes.Builder attributesBuilder = new AudioAttributes.Builder()
                    .setUsage(usage)
                    .setContentType(Constant.AudioTrackConfig.CONTENT_TYPE);
            audioFocusRequest = new AudioFocusRequest.Builder(Constant.AudioFocusType.GAIN_TYPE)
                    .setAudioAttributes(attributesBuilder.build())
                    .setOnAudioFocusChangeListener(audioFocusChangeListener)
                    .build();
            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            initHandler();
        }

        private void addBeanAudioListener(BeanAudioListener listener) {
            beanAudioListenerList.add(listener);
        }

        public void requestAudioFocus() {
            if (handler != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        doRequestAudioFocus();
                    }
                });
                return;
            }
            synchronized (this) {
                doRequestAudioFocus();
            }
        }

        public boolean requestAudioFocusSync() {
            final boolean[] requestFocusSuccess = new boolean[1];
            if (handler != null) {
                if (Thread.currentThread() != handler.getLooper().getThread()) {
                    CountDownLatch countDownLatch = new CountDownLatch(1);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            requestFocusSuccess[0] = doRequestAudioFocus();
                            countDownLatch.countDown();
                        }
                    });
                    try {
                        countDownLatch.await(DELAY_REQ, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    LogUtils.i(TAG, "AudioFocusProxy requestAudioFocusSync --- return");
                    return requestFocusSuccess[0];
                }
            }
//            synchronized (this) {
//                requestFocusSuccess[0] = doRequestAudioFocus();
//            }
            return false;
        }

        private boolean doRequestAudioFocus() {
            LogUtils.e(TAG, "AudioFocusProxy doRequestAudioFocus:  hasFocus is " + audioFocus + " ,usage:" + usage);
            if (!audioFocus) {
                int result = audioManager.requestAudioFocus(audioFocusRequest);
                LogUtils.e(TAG, "AudioFocusProxy doRequestAudioFocus result:" + result + ",usage:" + usage);
                audioFocus = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
            }
            notifyReqRlt(audioFocus);
            return audioFocus;
        }

        public void releaseAudioFocus() {
            if (handler != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        doReleaseAudioFocus();
                    }
                });
                return;
            }
            synchronized (this) {
                doReleaseAudioFocus();
            }
        }

        public void doReleaseAudioFocus() {
            LogUtils.e(TAG, "AudioFocusProxy doReleaseAudioFocus:  audioFocus is " + audioFocus + ",usage is " + usage);
            if (!audioFocus) {//未持有
                LogUtils.d(TAG, "AudioFocusProxy doReleaseAudioFocus with: already abandon.");
            } else {
//                int result = audioManager.abandonAudioFocus(audioFocusChangeListener);
                int result = audioManager.abandonAudioFocusRequest(audioFocusRequest);
                LogUtils.d(TAG, "AudioFocusProxy doReleaseAudioFocus, result=" + result);
                audioFocus = result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
            }
        }

        public boolean hasFocus() {
            LogUtils.i(TAG, "AudioFocusProxy hasFocus is " + audioFocus + " ,usage is " + usage);
            return audioFocus;
        }

        @Override
        public String getDumpInfo() {
            return "AudioFocusProxy{" +
                    "audioFocus=" + audioFocus +
                    ", usage=" + usage +
                    '}';
        }

        public boolean isHoldFocus() {
            synchronized (holdFocusLock) {
                return isHoldFocus;
            }
        }

        public void setHoldFocus(boolean holdFocus) {
            synchronized (holdFocusLock) {
                isHoldFocus = holdFocus;
            }
        }
    }


    public interface BeanAudioListener {
        void onBeforeRequest();

        void onBeforeRelease();

        void onAudioFocusChange(boolean audioFocus);

        void onAudioFocusObtain(boolean audioFocus);
    }

}
