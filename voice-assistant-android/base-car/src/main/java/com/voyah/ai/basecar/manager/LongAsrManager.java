package com.voyah.ai.basecar.manager;


import android.os.RemoteException;

import com.voice.sdk.VoiceImpl;
import com.voice.sdk.asrInput.IOnAsrCallback;
import com.voice.sdk.asrInput.IftyAsr;
import com.voice.sdk.asrInput.RecognizePhase;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.base.LongAsrInterface;
import com.voyah.ai.common.utils.LogUtils;
import com.voice.sdk.util.ThreadPoolUtils;
import com.voyah.ai.sdk.ILongAsrCallback;
import com.voyah.ai.sdk.bean.DhScreenType;
import com.voyah.ai.sdk.bean.LongAsrEC;
import com.voyah.ai.sdk.listener.ILongASRListener;
import com.voyah.ai.voice.platform.soa.api.channel.IAudioSteam;

public class LongAsrManager implements LongAsrInterface {

    private static final String TAG = "LongAsrManager";
    private IftyAsr impl;
    public static final String LONG_ASR_SOURCE = "longAsrSource";

    /**
     * 开启长识别
     */
    public void startLongASR(int screenType, ILongAsrCallback callback) {
        //禁用唤醒
        DeviceHolder.INS().getDevices().getVoiceCarSignal().deviceForbiddenStatus(LONG_ASR_SOURCE, 0);
        startLongASRInner(screenType, new ILongASRListener() {

            @Override
            public void onRecognize(String text) {
                try {
                    callback.onRecognize(text);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onEnd(int errCode, String reason) {
                try {
                    //允许唤醒
                    DeviceHolder.INS().getDevices().getVoiceCarSignal().deviceForbiddenStatus(LONG_ASR_SOURCE, 1);
                    callback.onEnd(errCode, reason);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void startLongASRInner(int screenType, ILongASRListener listener) {

        IAudioSteam stream = new IAudioSteam() {
            @Override
            public void write(byte[] bytes) {
                impl.receiveAudio(bytes, RecognizePhase.RECOGNIZE_START);
            }

            @Override
            public void finish() {
                LogUtils.d(TAG, "pulled bytes finish");
                impl.receiveAudio(new byte[0], RecognizePhase.RECOGNIZE_END);
            }
        };

        ThreadPoolUtils.INSTANCE.threadExecute(() -> {
            //IftyAsr 内部连接DS服务是同步的，必须放在子线程
            impl = new IftyAsr(new IOnAsrCallback() {
                @Override
                public void onRecognizeResult(String asrText, boolean isFinal) {
                    listener.onRecognize(asrText);
                    LogUtils.d(TAG, "onRecognizeResult, asrText:" + asrText + ",isFinal :" + isFinal);
                }

                @Override
                public void onRecognizeFinish() {
                    LogUtils.d(TAG, "onRecognizeFinish");
                    //ASR识别超时或者正常结束后，不再拉取音频
                    VoiceImpl.getInstance().stopPull(getVoiceAreaByScreenType(screenType));
                    listener.onEnd(LongAsrEC.NORMAL, "success");
                }

                @Override
                public void onRecognizeClose(boolean success, boolean remote) {
                    LogUtils.d(TAG, "onRecognizeClose success:" + success + ", remote:" + remote);
                }

                @Override
                public void onRecognizeError(boolean isNetworkError) {
                    LogUtils.d(TAG, "onRecognizeError isNetworkError:" + isNetworkError);
                    //ASR识别失败后，不再拉取音频
                    VoiceImpl.getInstance().stopPull(getVoiceAreaByScreenType(screenType));
                    if (isNetworkError) {
                        listener.onEnd(LongAsrEC.NO_NET, "network error");
                    } else {
                        listener.onEnd(LongAsrEC.CLOUD_ERR, "unknown");
                    }
                }
            });
            VoiceImpl.getInstance().pullAudioStream(getVoiceAreaByScreenType(screenType), stream);
        });
    }

    /**
     * 根据屏幕位置确定收音区域
     *
     * @param screenType 屏幕类型
     * @return 收音区域
     */
    private int getVoiceAreaByScreenType(@DhScreenType int screenType) {
        switch (screenType) {
            case DhScreenType.SCREEN_MAIN:
            case DhScreenType.SCREEN_INSTRUMENT:
            case DhScreenType.SCREEN_VEHICLE:
                return 1 + 2;
            case DhScreenType.SCREEN_PASSENGER:
                return 2;
            case DhScreenType.SCREEN_CEILING:
            case DhScreenType.SCREEN_LEFT_REAR:
            case DhScreenType.SCREEN_RIGHT_REAR:
            case DhScreenType.SCREEN_MID_REAR:
                return 4 + 8;
            default:
                return 1 + 2;
        }
    }

    /**
     * 结束长识别
     */
    public void stopLongASR(int screenType) {
        VoiceImpl.getInstance().stopPull(getVoiceAreaByScreenType(screenType));

        try {
            if (impl != null) {
                //impl.receiveAudio(new byte[0], RecognizePhase.RECOGNIZE_END);
                impl.stopAsr();
            }
        } catch (Exception e) {
            LogUtils.e(TAG, "stopLongASR error:" + e.getMessage());
        }
        //允许唤醒
        DeviceHolder.INS().getDevices().getVoiceCarSignal().deviceForbiddenStatus(LONG_ASR_SOURCE, 1);
    }

    /**
     * 是否正在进行长识别
     *
     * @return
     */
    public boolean isLongAsrGoing(int screenType) {
        if (impl == null) {
            return false;
        }
        return impl.isOpen();
    }

    private static class Holder {
        private static final LongAsrManager _INSTANCE = new LongAsrManager();
    }

    private LongAsrManager() {
        super();
    }

    public static LongAsrManager get() {
        return LongAsrManager.Holder._INSTANCE;
    }
}
