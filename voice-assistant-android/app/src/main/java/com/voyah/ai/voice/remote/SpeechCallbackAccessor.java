package com.voyah.ai.voice.remote;

import android.os.RemoteCallbackList;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.LogUtils;
import com.voice.sdk.device.DeviceHolder;
import com.voyah.ai.sdk.IImageRecognizeCallback;
import com.voyah.ai.sdk.IRmsDbCallback;
import com.voyah.ai.sdk.IVAReadyCallback;
import com.voyah.ai.sdk.IVAResultCallback;
import com.voyah.ai.sdk.IVAStateCallback;
import com.voyah.ai.sdk.bean.LifeState;
import com.voyah.ai.sdk.bean.NluResult;
import com.voyah.ai.sdk.listener.IImageRecognizeListener;
import com.voyah.ai.sdk.listener.IRmsDbListener;
import com.voyah.ai.sdk.listener.IVAReadyListener;
import com.voyah.ai.sdk.listener.IVAResultListener;
import com.voyah.ai.sdk.listener.IVAStateListener;

import java.util.Set;

import cn.hutool.core.collection.ConcurrentHashSet;

public class SpeechCallbackAccessor {

    private SpeechCallbacks callbacks;
    private final Set<String> frozenPkgSet = new ConcurrentHashSet<>();
    private static final Object lock = new Object();

    public SpeechCallbackAccessor() {
    }

    public void init(SpeechCallbacks callbacks) {
        this.callbacks = callbacks;
        DeviceHolder.INS().getDevices().getDialogue().registerReadyListener(readyListener);
        DeviceHolder.INS().getDevices().getDialogue().registerStateCallback(stateListener);
        DeviceHolder.INS().getDevices().getDialogue().registerResultListener(resultListener);
        DeviceHolder.INS().getDevices().getDialogue().registerRmsDbListener(rmsDbListener);
        DeviceHolder.INS().getDevices().getAI().registerIRListener(imageRecognizeListener);
    }

    public void unInit() {
        DeviceHolder.INS().getDevices().getDialogue().unregisterReadyListener(readyListener);
        DeviceHolder.INS().getDevices().getDialogue().unregisterStateListener(stateListener);
        DeviceHolder.INS().getDevices().getDialogue().unregisterResultListener(resultListener);
        DeviceHolder.INS().getDevices().getDialogue().unregisterRmsDbListener(rmsDbListener);
        DeviceHolder.INS().getDevices().getAI().unregisterIRListener(imageRecognizeListener);
    }

    private final IVAReadyListener readyListener = () -> {
        LogUtils.d("onSpeechReady() called");
        if (callbacks != null) {
            doCallback(callbacks.readyCallbacks, new RemoteCallback() {
                @Override
                public void doSomething(int numCallbacks) {
                    for (int i = 0; i < numCallbacks; i++) {
                        SpeechCallbackWrapper<IVAReadyCallback> wrapper = callbacks.readyCallbacks.getBroadcastItem(i);
                        try {
                            wrapper.getCallback().onSpeechReady();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    };

    private final IVAStateListener stateListener = new IVAStateListener() {
        @Override
        public void onState(String state) {
            if (callbacks != null) {
                doCallback(callbacks.stateCallbacks, numCallbacks -> {
                    for (int i = 0; i < numCallbacks; i++) {
                        SpeechCallbackWrapper<IVAStateCallback> wrapper = callbacks.stateCallbacks.getBroadcastItem(i);
                        try {
                            wrapper.getCallback().onState(state);
                        } catch (Exception e) {
                            LogUtils.w("onState callback fail:" + wrapper.getPackageName());
                            recordFrozenPkgIfNeed(wrapper);
                            continue;
                        }
                        notifyUnfrozenPkgIfNeed(state, wrapper);
                    }
                });
            }
        }
    };

    private void notifyUnfrozenPkgIfNeed(String state, SpeechCallbackWrapper<IVAStateCallback> wrapper) {
        if (frozenPkgSet.contains(wrapper.getPackageName())) {
            frozenPkgSet.remove(wrapper.getPackageName());
            if (!LifeState.AWAKE.equals(state) && DeviceHolder.INS().getDevices().getDialogue().isInteractionState()) {
                try {
                    wrapper.getCallback().onState(LifeState.AWAKE);
                } catch (Exception e) {
                    LogUtils.w("notifyUnfrozenPkgIfNeed exception:" + e.getMessage());
                }
            }
        }
    }

    private void recordFrozenPkgIfNeed(SpeechCallbackWrapper<IVAStateCallback> wrapper) {
        if (wrapper.getCallback().asBinder() != null && wrapper.getCallback().asBinder().isBinderAlive()) {
            frozenPkgSet.add(wrapper.getPackageName());
        } else {
            frozenPkgSet.remove(wrapper.getPackageName());
        }
    }

    private final IVAResultListener resultListener = new IVAResultListener() {
        @Override
        public void onAsr(String text, boolean isEnd) {
        }

        @Override
        public void onNluResult(NluResult result) {
        }

        @Override
        public void onShortCommand(String command, NluResult result) {
        }

        @Override
        public void onViewCommand(String word, NluResult result) {
            if (callbacks != null && callbacks.resultCallbacks != null) {
                doCallback(callbacks.resultCallbacks, numCallbacks -> {
                    for (int i = 0; i < numCallbacks; i++) {
                        SpeechCallbackWrapper<IVAResultCallback> wrapper = callbacks.resultCallbacks.getBroadcastItem(i);
                        if (wrapper.getPackageName().equals(result.domain)) {
                            LogUtils.v("onViewCommand package:" + wrapper.getPackageName() + ", callback:" + wrapper.getCallback());
                            try {
                                wrapper.getCallback().onViewCommand(word, JSON.toJSONString(result));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }
    };

    private final IRmsDbListener rmsDbListener = new IRmsDbListener() {
        @Override
        public void onRmsChanged(float rms) {
            if (callbacks != null) {
                doCallback(callbacks.rmsDbCallbacks, numCallbacks -> {
                    for (int i = 0; i < numCallbacks; i++) {
                        SpeechCallbackWrapper<IRmsDbCallback> wrapper = callbacks.rmsDbCallbacks.getBroadcastItem(i);
                        try {
                            wrapper.getCallback().onRmsChanged(rms);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    };

    private final IImageRecognizeListener imageRecognizeListener = new IImageRecognizeListener() {
        @Override
        public void onIRResult(String sessionId, int screenType, int state, String msg) {
            if (callbacks != null) {
                doCallback(callbacks.imageRecognizeCallbacks, numCallbacks -> {
                    for (int i = 0; i < numCallbacks; i++) {
                        SpeechCallbackWrapper<IImageRecognizeCallback> wrapper = callbacks.imageRecognizeCallbacks.getBroadcastItem(i);
                        try {
                            wrapper.getCallback().onIRResult(sessionId, screenType, state, msg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    };

    private void doCallback(RemoteCallbackList<?> callbackList, RemoteCallback remoteCallback) {
        synchronized (lock) {
            if (callbackList != null) {
                try {
                    int numCallbacks = callbackList.beginBroadcast();
                    remoteCallback.doSomething(numCallbacks);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        callbackList.finishBroadcast();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static class SpeechCallbacks {
        public RemoteCallbackList<SpeechCallbackWrapper<IVAReadyCallback>> readyCallbacks;
        public RemoteCallbackList<SpeechCallbackWrapper<IVAStateCallback>> stateCallbacks;
        public RemoteCallbackList<SpeechCallbackWrapper<IVAResultCallback>> resultCallbacks;
        public RemoteCallbackList<SpeechCallbackWrapper<IRmsDbCallback>> rmsDbCallbacks;
        public RemoteCallbackList<SpeechCallbackWrapper<IImageRecognizeCallback>> imageRecognizeCallbacks;
    }

    public interface RemoteCallback {
        void doSomething(int numCallbacks);
    }
}
