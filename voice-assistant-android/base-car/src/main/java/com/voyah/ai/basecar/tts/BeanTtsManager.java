package com.voyah.ai.basecar.tts;

import com.blankj.utilcode.util.Utils;
import com.voice.sdk.VoiceImpl;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.constants.Domain;
import com.voice.sdk.device.carservice.signal.SysSettingSignal;
import com.voice.sdk.device.tts.BeanTtsInterface;
import com.voyah.ai.basecar.CommonSystemUtils;
import com.voyah.ai.basecar.manager.DialogueManager;
import com.voyah.ai.basecar.manager.SettingsManager;
import com.voyah.ai.common.helper.LogTool;
import com.voyah.ai.common.helper.ParamsKey;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.sdk.DhSpeechSDK;
import com.voyah.ai.sdk.IGeneralTtsCallback;
import com.voyah.ai.sdk.VoiceTtsBean;
import com.voyah.ai.sdk.bean.DhDialect;
import com.voyah.ai.sdk.bean.DhSwitch;
import com.voyah.ai.sdk.bean.LifeState;
import com.voyah.ai.sdk.bean.ServiceAbility;
import com.voyah.ai.sdk.listener.ITtsPlayListener;
import com.voyah.ai.sdk.manager.TTSManager;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @author:lcy
 * @data:2024/4/16
 **/
public class BeanTtsManager implements BeanTtsInterface{
    private static final String TAG = BeanTtsManager.class.getSimpleName();

    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static volatile BeanTtsManager beanTtsManager;
    private boolean isFirstWakeUp = false;


    private BeanTtsManager() {
    }

    public static BeanTtsManager getInstance() {
        if (null == beanTtsManager) {
            synchronized (BeanTtsManager.class) {
                if (null == beanTtsManager) beanTtsManager = new BeanTtsManager();
            }
        }
        return beanTtsManager;
    }

    public void initialize() {
        LogUtils.i(TAG, "initialize  DhSpeechSDK");
        executorService.execute(new Runnable() {
            @Override
            public void run() {DhSpeechSDK.initialize(Utils.getApp(), ServiceAbility.TTS_ABILITY, () -> {
                    LogUtils.i(TAG, "TTS onSpeechReady");
                    DhDialect curDialect = SettingsManager.get().getCurrentDialect();
                    boolean isNearByTts = SettingsManager.get().isEnableSwitch(DhSwitch.NearbyTTS);
                    boolean isHeadRestOpened = DeviceHolder.INS().getDevices().getCarService().getOperatorDispatcher()
                            .getOperatorByDomain(Domain.SYS_SETTING.getDomain()).getIntProp(SysSettingSignal.SYS_HEADREST_SOUND_STATE) > 1;
                    TTSManager.forbiddenNearByTts(isHeadRestOpened);
                    //TODO 是否禁用就近播报
                    if (curDialect != null) {
                        TTSManager.setTtsSpeaker(DeviceHolder.INS().getDevices().getVoiceCopy().getTTSData(curDialect.tts));
                        TTSManager.setNearByTtsStatus(isNearByTts);
                    }
                    registerGeneralTtsStatus(iGeneralTtsCallback);
                });

                DeviceHolder.INS().getDevices().getHeadSet().registerHeadSetObserver(value -> {
                    TTSManager.forbiddenNearByTts(value);
                    return null;
                });
            }
        });
    }

    private static final IGeneralTtsCallback iGeneralTtsCallback = new IGeneralTtsCallback.Stub() {
        @Override
        public void onGeneralTtsStatus(int[] ints){
            if (null != ints)
                VoiceImpl.getInstance().uploadTtsPlayStatus(ints);
        }
    };


    public void speak(String text) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                showAutoTestLog(text);
                TTSManager.speak(text, new GlobalTTSListener());
            }
        });
    }

    public void speak(String text, ITtsPlayListener listener) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                showAutoTestLog(text);
                TTSManager.speak(text, new GlobalTTSListener(listener));
            }
        });
    }

    public void speakSynchronized(String text, boolean isImt, ITtsPlayListener listener) {
//        executorService.execute(new Runnable() {
//            @Override
//            public void run() {
//                countDownLatch = new CountDownLatch(1);
//                GlobalTTSListener globalListener = new GlobalTTSListener(listener) {
//
//                    @Override
//                    public void onPlayEnd(String text, @REASON int reason) {
//                        super.onPlayEnd(text, reason);
//                        countDownLatch.countDown();
//                    }
//
//                    @Override
//                    public void onPlayError(String text, @REASON int reason) {
//                        super.onPlayError(text, reason);
//                        countDownLatch.countDown();
//                    }
//                };
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                speak(text, isImt, listener);
            }
        });
//                try {
//                    countDownLatch.await();
//                } catch (InterruptedException e) {
//                    countDownLatch.countDown();
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    public void speak(String text, boolean isImt, ITtsPlayListener listener) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                showAutoTestLog(text);
                if (isImt)
                    TTSManager.speakImt(text, new GlobalTTSListener(listener));
                else
                    TTSManager.speak(text, new GlobalTTSListener(listener));
            }
        });
    }

    /**
     * 指定播报通道及位置
     *
     * @param text     待播报文本
     * @param usage    指定播报通道 默认传-1 为语音通道
     * @param location 指令触发声源位置
     * @param listener 监听
     */
    public void speak(String text, int usage, int location, ITtsPlayListener listener) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                showAutoTestLog(text);
                TTSManager.speak(text, usage == -1 ? 16 : usage, location, new GlobalTTSListener(listener));
            }
        });
    }

    public void speak(String text, int direction, ITtsPlayListener listener) {
        speak(text, 16, CommonSystemUtils.getNearByTtsLocation(direction), listener);
    }

    //todo:信息类临时使用-天气、股票、闲聊等，非流式
    public void speakInformation(String text, String type, ITtsPlayListener listener) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                showAutoTestLog(text);
                TTSManager.speak(text, new GlobalTTSListener(listener), type);
            }
        });
    }

    public void speak(String text, ITtsPlayListener listener, String speaker) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                showAutoTestLog(text);
                TTSManager.speak(text, new GlobalTTSListener(listener), speaker);
            }
        });
    }

    public void speakImt(String text) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                showAutoTestLog(text);
                TTSManager.speakImt(text, new GlobalTTSListener());
            }
        });
    }

    public void speakImt(String text, ITtsPlayListener listener) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                showAutoTestLog(text);
                TTSManager.speakImt(text, new GlobalTTSListener(listener));
            }
        });
    }


    public void speakImt(String text, ITtsPlayListener listener, String speaker) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                showAutoTestLog(text);
                TTSManager.speakImt(text, new GlobalTTSListener(listener), speaker);
            }
        });
    }

    public void speakStream(String text, ITtsPlayListener listener, int streamStatus) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                showAutoTestLog(text);
                TTSManager.streamSpeak(text, new GlobalTTSListener(listener), streamStatus);
            }
        });
    }

    public void speakBean(VoiceTtsBean voiceTtsBean, ITtsPlayListener listener) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                showAutoTestLog(voiceTtsBean.getTts());
                TTSManager.speakBean(voiceTtsBean, new GlobalTTSListener(listener), "");
            }
        });
    }

    public void speakStreamBean(VoiceTtsBean voiceTtsBean, ITtsPlayListener listener, int streamStatus) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                showAutoTestLog(voiceTtsBean.getTts());
                TTSManager.streamSpeak(voiceTtsBean, new GlobalTTSListener(listener), streamStatus);
            }
        });
    }

    public void speakInformationBean(VoiceTtsBean voiceTtsBean, ITtsPlayListener listener, String type) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                showAutoTestLog(voiceTtsBean.getTts());
                TTSManager.speakInformationBean(voiceTtsBean, new GlobalTTSListener(listener), type);
            }
        });
    }


    public void stopCurTts() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                TTSManager.stopCurTts();
            }
        });
    }

    public void shutUp() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                TTSManager.shutUp();
            }
        });
    }

    @Override
    public void shutUpOneSelf() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                TTSManager.shutUpOneSelf();
            }
        });
    }

    public boolean isSpeaking(int usage) {
        return TTSManager.isSpeaking(usage);
    }


    public void requestAudioFocusByUsage() {
        LogUtils.d(TAG, "requestAudioFocusByUsage isFirstWakeUp:" + isFirstWakeUp);
        if (!isFirstWakeUp) {
            TTSManager.requestAudioFocusByUsage(16, 1);
            isFirstWakeUp = true;
        }
    }

    public void releaseAudioFocusByUsage() {
        LogUtils.d(TAG, "releaseAudioFocusByUsage isFirstWakeUp:" + isFirstWakeUp);
        if (isFirstWakeUp) {
            LogUtils.d(TAG, "releaseAudioFocusByUsage");
            TTSManager.requestAudioFocusByUsage(16, 0);
            isFirstWakeUp = false;
        }
    }

    @Override
    public void stopById(String originTtsId) {
        LogUtils.d(TAG, "stopById originTtsId:" + originTtsId);
        TTSManager.stopById(originTtsId);
    }

    public void registerGeneralTtsStatus(IGeneralTtsCallback callback) {
        if (null != callback)
            TTSManager.registerGeneralTtsStatus(callback);
    }

    public void unregisterGeneralTtsStatus(IGeneralTtsCallback callback) {
        if (null != callback)
            TTSManager.unregisterGeneralTtsStatus(callback);
    }


    private void showAutoTestLog(String ttsText) {
        HashMap<String, String> hashMap = new HashMap<>();
        LogTool.JsonStructure jsonStructure = new LogTool.JsonStructure();
        jsonStructure.addJsonParam("tts", ttsText);
        jsonStructure.addJsonParam("module_type", "tts");
        hashMap.put(ParamsKey.ModuleKey.TEST_MODULE, jsonStructure.toString());
        LogUtils.i(TAG, hashMap);
    }

    public void onDestroy() {
        DeviceHolder.INS().getDevices().getHeadSet().unRegisterHeadSetObserver();
        unregisterGeneralTtsStatus(iGeneralTtsCallback);
    }

    private static class GlobalTTSListener implements ITtsPlayListener {

        private ITtsPlayListener listener;

        public GlobalTTSListener() {
        }

        public GlobalTTSListener(ITtsPlayListener listener) {
            this.listener = listener;
        }

        @Override
        public void onPlayBeginning(String text) {
            if (listener != null) {
                listener.onPlayBeginning(text);
            }
            DialogueManager.get().onVoiceStateCallback(LifeState.SPEAKING);
        }

        @Override
        public void onPlayEnd(String text, @REASON int reason) {
            if (listener != null) {
                listener.onPlayEnd(text, reason);
            }
            DialogueManager.get().onVoiceStateCallback(LifeState.LISTENING);
        }

        @Override
        public void onPlayError(String text, @REASON int reason) {
            if (listener != null) {
                listener.onPlayError(text, reason);
            }
            DialogueManager.get().onVoiceStateCallback(LifeState.LISTENING);
        }
    }
}
