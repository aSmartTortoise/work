package com.voyah.ai.sdk;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.voyah.ai.sdk.bean.ServiceAbility;
import com.voyah.ai.sdk.listener.IVAReadyListener;
import com.voyah.ai.sdk.manager.DialogueManager;

import java.util.ArrayList;
import java.util.List;


/**
 * sdk 入口
 */
public class DhSpeechSDK {

    private static final String TAG = DhSpeechSDK.class.getSimpleName();

    public static AssistantBinderConnector assistantConnector;
    public static TtsBinderConnector ttsConnector;
    public static int serviceAbility = -1;
    private static final List<IVAReadyListener> cacheReadyListeners = new ArrayList<>();

    /**
     * 初始化SDK
     *
     * @param context  上下文
     * @param listener 启动监听器
     */
    public static void initialize(@NonNull Context context, @NonNull IVAReadyListener listener) {
        initialize(context, ServiceAbility.ALL_ABILITY, listener);
    }

    /**
     * 初始化SDK
     *
     * @param context  上下文
     * @param ability  连接的语音服务能力
     * @param listener 启动监听器
     */
    public static void initialize(@NonNull Context context, @ServiceAbility int ability, @NonNull IVAReadyListener listener) {
        Log.d(TAG, "initialize() called with: context = [" + context + "], ability = [" + ability + "], listener = [" + listener + "]");
        if (serviceAbility == -1) {
            serviceAbility = ability;
        } else if (ability == ServiceAbility.ALL_ABILITY) {
            serviceAbility = ServiceAbility.ALL_ABILITY;
        } else if (serviceAbility == ServiceAbility.TTS_ABILITY && ability != ServiceAbility.TTS_ABILITY) {
            serviceAbility = ServiceAbility.ALL_ABILITY;
        } else if (serviceAbility == ServiceAbility.ASSISTANT_ABILITY && ability != ServiceAbility.ASSISTANT_ABILITY) {
            serviceAbility = ServiceAbility.ALL_ABILITY;
        }
        if (serviceAbility == ServiceAbility.TTS_ABILITY) {
            connectTtsService(context, listener);
        } else if (serviceAbility == ServiceAbility.ASSISTANT_ABILITY) {
            connectAssistantService(context, listener);
        } else {
            connectAllService(context, listener);
        }

        handleCacheVAReadyListenerIfNeed();
    }

    private static void connectTtsService(Context context, @NonNull IVAReadyListener listener) {
        if (ttsConnector == null) {
            ttsConnector = new TtsBinderConnector(context);
        }
        ttsConnector.addOnReadyListener(listener);
        boolean remoteReady = ttsConnector.isRemoteReady();
        if (remoteReady) {
            listener.onSpeechReady();
        } else {
            ttsConnector.bindService();
        }
    }

    private static void connectAssistantService(Context context, @NonNull IVAReadyListener listener) {
        if (assistantConnector == null) {
            assistantConnector = new AssistantBinderConnector(context);
        }
        assistantConnector.addOnReadyListener(listener);
        boolean remoteReady = assistantConnector.isRemoteReady();
        if (remoteReady) {
            listener.onSpeechReady();
        } else {
            assistantConnector.bindService();
        }
    }

    private static void connectAllService(Context context, @NonNull IVAReadyListener listener) {
        if (assistantConnector == null) {
            assistantConnector = new AssistantBinderConnector(context);
        }
        if (ttsConnector == null) {
            ttsConnector = new TtsBinderConnector(context);
        }
        ttsConnector.addOnReadyListener(() -> {
            Log.d(TAG, "tts module onSpeechReady() called");
        });
        assistantConnector.addOnReadyListener(() -> {
            Log.d(TAG, "assistant module onSpeechReady() called");
            listener.onSpeechReady();
        });

        boolean assistantRemoteReady = assistantConnector.isRemoteReady();
        boolean ttsRemoteReady = ttsConnector.isRemoteReady();
        if (!ttsRemoteReady) {
            // 绑定tts服务等待onReady
            ttsConnector.bindService();
        }
        if (!assistantRemoteReady) {
            // 绑定语音服务等待onReady
            assistantConnector.bindService();
        }

        if (assistantRemoteReady) {
            listener.onSpeechReady();
        }
    }


    private static void handleCacheVAReadyListenerIfNeed() {
        if (cacheReadyListeners.size() > 0) {
            if (serviceAbility == ServiceAbility.TTS_ABILITY) {
                for (IVAReadyListener l : cacheReadyListeners) {
                    ttsConnector.addOnReadyListener(l);
                }
                boolean remoteReady = ttsConnector.isRemoteReady();
                if (remoteReady) {
                    for (IVAReadyListener l : cacheReadyListeners) {
                        l.onSpeechReady();
                    }
                }
            } else {
                for (IVAReadyListener l : cacheReadyListeners) {
                    assistantConnector.addOnReadyListener(l);
                }
                boolean remoteReady = assistantConnector.isRemoteReady();
                if (remoteReady) {
                    for (IVAReadyListener l : cacheReadyListeners) {
                        l.onSpeechReady();
                    }
                }
            }
        }
    }

    /**
     * 设置语音就绪监听器(需要在调用initialize后调用)
     *
     * @param listener
     */
    public static void setVAReadyListener(@NonNull IVAReadyListener listener) {
        if (serviceAbility == ServiceAbility.TTS_ABILITY) {
            if (ttsConnector != null) {
                boolean remoteReady = ttsConnector.isRemoteReady();
                if (remoteReady) {
                    listener.onSpeechReady();
                }
                ttsConnector.addOnReadyListener(listener);
            } else {
                Log.e(TAG, "you should call initialize ASSISTANT_TTS first");
                cacheReadyListeners.add(listener);
            }
        } else {
            if (assistantConnector != null) {
                boolean remoteReady = assistantConnector.isRemoteReady();
                if (remoteReady) {
                    listener.onSpeechReady();
                }
                assistantConnector.addOnReadyListener(listener);
            } else {
                Log.e(TAG, "you should call initialize ASSISTANT_ABILITY first");
                cacheReadyListeners.add(listener);
            }
        }
    }

    /**
     * 移除语音是就绪监听器
     *
     * @param listener
     */
    public static void removeVAReadyListener(@NonNull IVAReadyListener listener) {
        if (ttsConnector != null) {
            ttsConnector.removeOnReadyListener(listener);
        }
        if (assistantConnector != null) {
            assistantConnector.removeOnReadyListener(listener);
        }
        cacheReadyListeners.remove(listener);
    }

    /**
     * 释放 sdk 资源
     */
    public static void release() {
        if (ttsConnector != null) {
            ttsConnector.release();
        }
        if (assistantConnector != null) {
            assistantConnector.release();
        }
    }

    /**
     * 语音是否准备就绪
     */
    public static boolean isSpeechReady() {
        if (serviceAbility == ServiceAbility.TTS_ABILITY) {
            return ttsConnector != null && ttsConnector.isRemoteReady();
        } else {
            return assistantConnector != null && assistantConnector.isRemoteReady();
        }
    }


    /**
     * 当前语音是否处于交互状态
     */
    public static boolean isInteractionState() {
        if (serviceAbility == ServiceAbility.TTS_ABILITY) {
            Log.e(TAG, "please call initialize ASSISTANT_ABILITY first");
            return false;
        } else {
            return DialogueManager.isInteractionState();
        }
    }
}
