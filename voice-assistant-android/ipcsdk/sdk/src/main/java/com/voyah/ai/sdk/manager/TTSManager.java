package com.voyah.ai.sdk.manager;

import static com.voyah.ai.sdk.DhSpeechSDK.ttsConnector;

import android.text.TextUtils;
import android.util.Log;

import com.voyah.ai.sdk.IGeneralTtsCallback;
import com.voyah.ai.sdk.VoiceTtsBean;
import com.voyah.ai.sdk.listener.ITtsPlayListener;

/**
 * TTS接口管理器
 */
public class TTSManager {

    private static final String TAG = TTSManager.class.getSimpleName();

    /**
     * TTS播报
     *
     * @param text 播报内容
     */
    public static void speak(String text) {
        speak(text, null);
    }

    /**
     * 指定usage的TTS播报
     *
     * @param text  播报内容
     * @param usage 指定通道
     */
    public static void speak(String text, int usage) {
        speak(text, usage, null);
    }

    /**
     * 指定usage及location的TTS播报
     *
     * @param text  播报内容
     * @param usage 指定通道
     */
    public static void speak(String text, int usage, int location) {
        speak(text, usage, location, null);
    }

    /**
     * 带回调的TTS播报
     *
     * @param text     播报内容
     * @param listener 播报回调
     */
    public static void speak(String text, ITtsPlayListener listener) {
        Log.d(TAG, "speak() called with: text = [" + text + "], listener = [" + listener + "]");
        if (ttsConnector != null) {
            if (!TextUtils.isEmpty(text)) {
                ttsConnector.speak(text, listener, null, false);
            } else {
                if (listener != null) {
                    listener.onPlayBeginning(text);
                    listener.onPlayEnd(text, ITtsPlayListener.REASON.FINISH);
                }
            }
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 带回调的TTS播报
     *
     * @param text     播报内容
     * @param usage    通道
     * @param listener 播报回调
     */
    public static void speak(String text, int usage, ITtsPlayListener listener) {
        Log.d(TAG, "speak() called with: text = [" + text + "], usage = [" + usage + "], listener = [" + listener + "]");
        if (ttsConnector != null) {
            if (!TextUtils.isEmpty(text)) {
                ttsConnector.speak(text, listener, null, false, usage);
            } else {
                if (listener != null) {
                    listener.onPlayBeginning(text);
                    listener.onPlayEnd(text, ITtsPlayListener.REASON.FINISH);
                }
            }
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 带回调的TTS播报
     *
     * @param text     播报内容
     * @param usage    通道
     * @param location 指定就近播报位置(就近播报开关可用时生效)
     * @param listener 播报回调
     */
    public static void speak(String text, int usage, int location, ITtsPlayListener listener) {
        Log.d(TAG, "speak() called with: text = [" + text + "], usage = [" + usage + "], listener = [" + listener + "]");
        if (ttsConnector != null) {
            if (!TextUtils.isEmpty(text)) {
                ttsConnector.speak(text, listener, null, false, usage, location);
            } else {
                if (listener != null) {
                    listener.onPlayBeginning(text);
                    listener.onPlayEnd(text, ITtsPlayListener.REASON.FINISH);
                }
            }
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 带回调和发声人的TTS播报
     *
     * @param text     播报内容
     * @param listener 播报回调
     * @param speaker  发声人
     */
    public static void speak(String text, ITtsPlayListener listener, String speaker) {
        Log.d(TAG, "speak() called with: text = [" + text + "], listener = [" + listener + "], speaker = [" + speaker + "]");
        if (ttsConnector != null) {
            if (!TextUtils.isEmpty(text)) {
                ttsConnector.speak(text, listener, speaker, false);
            } else {
                if (listener != null) {
                    listener.onPlayBeginning(text);
                    listener.onPlayEnd(text, ITtsPlayListener.REASON.FINISH);
                }
            }
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * TTS立即播报
     *
     * @param text 播报内容
     */
    public static void speakImt(String text) {
        speakImt(text, null);
    }

    /**
     * 指定usage的TTS立即播报
     *
     * @param text  播报内容
     * @param usage 指定通道
     */
    public static void speakImt(String text, int usage) {
        speakImt(text, usage, null);
    }

    /**
     * TTS立即播报
     *
     * @param text     播报内容
     * @param listener 播报回调
     */
    public static void speakImt(String text, ITtsPlayListener listener) {
        Log.d(TAG, "speakImt() called with: text = [" + text + "], listener = [" + listener + "]");
        if (ttsConnector != null) {
            if (!TextUtils.isEmpty(text)) {
                ttsConnector.speak(text, listener, null, true);
            } else {
                if (listener != null) {
                    listener.onPlayBeginning(text);
                    listener.onPlayEnd(text, ITtsPlayListener.REASON.FINISH);
                }
            }
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * TTS立即播报
     *
     * @param text     播报内容
     * @param usage    通道
     * @param listener 播报回调
     */
    public static void speakImt(String text, int usage, ITtsPlayListener listener) {
        Log.d(TAG, "speakImt() called with: text = [" + text + "], usage = [" + usage + "], listener = [" + listener + "]");
        if (ttsConnector != null) {
            if (!TextUtils.isEmpty(text)) {
                ttsConnector.speak(text, listener, null, true, usage);
            } else {
                if (listener != null) {
                    listener.onPlayBeginning(text);
                    listener.onPlayEnd(text, ITtsPlayListener.REASON.FINISH);
                }
            }
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 带回调和发声人的TTS播报
     *
     * @param text     播报内容
     * @param listener 播报回调
     * @param speaker  发声人
     */
    public static void speakImt(String text, ITtsPlayListener listener, String speaker) {
        Log.d(TAG, "speakImt() called with: text = [" + text + "], listener = [" + listener + "], speaker = [" + speaker + "]");
        if (ttsConnector != null) {
            if (!TextUtils.isEmpty(text)) {
                ttsConnector.speak(text, listener, speaker, true);
            } else {
                if (listener != null) {
                    listener.onPlayBeginning(text);
                    listener.onPlayEnd(text, ITtsPlayListener.REASON.FINISH);
                }
            }
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 流式TTS播报(不可用)
     *
     * @param text         tts文本
     * @param listener     回调
     * @param streamStatus 流式请求状态 0:流式合成开始(第一片) 1:流式分片 2:流式合成结尾(最后一片))
     */
    public static void streamSpeak(String text, ITtsPlayListener listener, int streamStatus) {
        Log.d(TAG, "streamSpeak() called with: text = [" + text + "], callback = [" + listener + "], streamStatus = [" + streamStatus + "]");
        if (ttsConnector != null) {
            if (streamStatus == 0 || streamStatus == 2) {
                ttsConnector.speakStream(text, listener, streamStatus);
            } else if (!TextUtils.isEmpty(text)) {
                ttsConnector.speakStream(text, listener, streamStatus);
            } else {
                if (listener != null) {
                    listener.onPlayBeginning(text);
                    listener.onPlayEnd(text, ITtsPlayListener.REASON.FINISH);
                }
            }
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 停止语音当前播报
     */
    public static void stopCurTts() {
        if (ttsConnector != null) {
            ttsConnector.stopCurTts();
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * TTS 停止所有播报
     */
    public static void shutUp() {
        Log.d(TAG, "shutUp()");
        if (ttsConnector != null) {
            ttsConnector.shutUp();
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * TTS 停止注册通道中该应用发起的播报请求
     */
    public static void shutUpOneSelf() {
        Log.d(TAG, "shutUpOneSelf()");
        if (ttsConnector != null) {
            ttsConnector.shutUpOneSelf();
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 指定通道
     */
    public static void setUsage(int usage) {
        Log.d(TAG, "setUsage() called with: usage = [" + usage + "]");
        if (ttsConnector != null) {
            ttsConnector.setUsage(usage);
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 指定的usage通道是否正在进行TTS播报
     *
     * @param usage 通道
     */
    public static boolean isSpeaking(int usage) {
        if (ttsConnector != null) {
            return ttsConnector.isSpeaking(usage);
        } else {
            Log.e(TAG, "please call initialize first");
            return false;
        }
    }

    /**
     * 设置tts播报音色
     *
     * @param speaker 对应的音色
     */
    public static void setTtsSpeaker(String speaker) {
        Log.d(TAG, "setTtsSpeaker() called with: speaker = [" + speaker + "]");
        if (ttsConnector != null) {
            ttsConnector.setTtsSpeaker(speaker);
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    public static void speakBean(VoiceTtsBean voiceTtsBean, ITtsPlayListener listener, String speaker) {
        if (null == voiceTtsBean) {
            Log.e(TAG, "voiceTtsBean is null ");
            return;
        }
        Log.d(TAG, "speakBean() called with: text = [" + voiceTtsBean.getTts() + "], listener = [" + listener + "], speaker = [" + speaker + "]");
        if (ttsConnector != null) {
            ttsConnector.speakBean(voiceTtsBean, listener);
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    public static void streamSpeak(VoiceTtsBean voiceTtsBean, ITtsPlayListener listener, int streamStatus) {
        Log.d(TAG, "streamSpeak() called with: text = [" + voiceTtsBean.getTts() + "], callback = [" + listener + "], streamStatus = [" + streamStatus + "]");
        if (ttsConnector != null) {
            if (streamStatus == 0 || streamStatus == 2) {
                ttsConnector.speakStreamBean(voiceTtsBean, listener, streamStatus);
            } else if (!TextUtils.isEmpty(voiceTtsBean.getTts())) {
                ttsConnector.speakStreamBean(voiceTtsBean, listener, streamStatus);
            } else {
                if (listener != null) {
                    listener.onPlayBeginning(voiceTtsBean.getTts());
                    listener.onPlayEnd(voiceTtsBean.getTts(), ITtsPlayListener.REASON.FINISH);
                }
            }
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    public static void speakInformationBean(VoiceTtsBean voiceTtsBean, ITtsPlayListener listener, String speaker) {
        if (null == voiceTtsBean) {
            Log.e(TAG, "voiceTtsBean is null ");
            return;
        }
        Log.d(TAG, "speakInformationBean() called with: text = [" + voiceTtsBean.getTts() + "], listener = [" + listener + "], speaker = [" + speaker + "]");
        if (ttsConnector != null) {
            ttsConnector.speakInformationBean(voiceTtsBean, listener, speaker, false);
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 就近交互开关状态
     *
     * @param isNearByTts 对应的音色
     */
    public static void setNearByTtsStatus(boolean isNearByTts) {
        Log.d(TAG, "setNearByTtsStatus() called with: isNearByTts = [" + isNearByTts + "]");
        if (ttsConnector != null) {
            ttsConnector.setNearByTtsStatus(isNearByTts);
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 就近交互禁用
     */
    public static void forbiddenNearByTts(boolean isForbidden) {
        Log.d(TAG, "forbiddenNearByTts() called with: isForbidden = [" + isForbidden + "]");
        if (ttsConnector != null) {
            ttsConnector.forbiddenNearByTts(isForbidden);
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    /**
     * 申请指定通道焦点(持续占用)
     *
     * @param usage 申请的指定通道
     * @param type  申请类型 0:取消持有焦点 1:申请持有焦点
     */
    public static boolean requestAudioFocusByUsage(int usage, int type) {
        boolean result = false;
        if (ttsConnector != null) {
            result = ttsConnector.requestAudioFocusByUsage(usage, type);
        } else {
            Log.e(TAG, "please call initialize first");
        }
        return result;
    }

    /**
     * 停止或取消指定任务
     *
     * @param originTtsId 发起任务id
     */
    public static void stopById(String originTtsId) {
        if (ttsConnector != null) {
            ttsConnector.stopById(originTtsId);
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    public static void registerGeneralTtsStatus(IGeneralTtsCallback callback) {
        if (ttsConnector != null) {
            ttsConnector.registerGeneralTtsStatus(callback);
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }

    public static void unregisterGeneralTtsStatus(IGeneralTtsCallback callback) {
        if (ttsConnector != null) {
            ttsConnector.unregisterGeneralTtsStatus(callback);
        } else {
            Log.e(TAG, "please call initialize first");
        }
    }
}
