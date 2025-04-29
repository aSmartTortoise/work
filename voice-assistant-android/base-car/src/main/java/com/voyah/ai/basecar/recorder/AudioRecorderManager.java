package com.voyah.ai.basecar.recorder;


import com.voice.sdk.Constant;
import com.voice.sdk.VoiceImpl;
import com.voice.sdk.device.audio.AudioRecorderInterface;
import com.voice.sdk.device.audio.IPcmAudioCallback;
import com.voyah.ai.basecar.recorder.mem.AudioByteArrMgr;
import com.voyah.ai.common.utils.DeviceUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.common.utils.RecordDataUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by lcy on 2023/12/18.
 */

public class AudioRecorderManager implements AudioRecorderInterface {
    private static final String TAG = "AudioRecorderManager";
    private static volatile AudioRecorderManager audioRecorderManager = null;
    /**
     * pcm音频回调
     */
    private final CopyOnWriteArraySet<IPcmAudioCallback> pcmAudioCallbacks = new CopyOnWriteArraySet<>();

    private AudioRecorder audioRecorder;

    private int totalChannel = 10;

    private byte[] transBuf;
    private static boolean enableSendAudio = true;

    private boolean isSelfInnovate;

    private boolean isSendAll; //H37B10通道数据直传

    private int readBufferSize;

    private int recorderType;


    private AudioRecorderManager() {
    }

    public static AudioRecorderManager getInstance() {
        if (null == audioRecorderManager) {
            synchronized (AudioRecorderManager.class) {
                if (null == audioRecorderManager) {
                    audioRecorderManager = new AudioRecorderManager();
                }
            }
        }
        return audioRecorderManager;
    }

    public void init(String vehicleType) {
        isSelfInnovate = DeviceUtils.isVoyahDevice();
        isSendAll = StringUtils.equals(vehicleType, "H37B");
        LogUtils.i(TAG, "init isSelfInnovate:" + isSelfInnovate + " ,isSendAll:" + isSendAll);
        if (!DeviceUtils.isVoyahDevice()) {
            return;
        }
        //自研
        if (isSelfInnovate) {
            //自研10通道
            totalChannel = 10;
            readBufferSize = totalChannel * 320;
            transBuf = new byte[(Constant.channels.micChannelsDefault + Constant.channels.RefChannelsDefault) * 320];
            recorderType = RecorderConfig.TYPE_COMMON_GAIN_4MIC;
        } else {
            //非自研8通道
            totalChannel = 8;
            readBufferSize = totalChannel * 320;
            recorderType = RecorderConfig.TYPE_COMMON_ECHO_4MIC;
        }
        int intervalTime = 16;
        int sampleRate = 16000;
        AudioByteArrMgr.getInstance().init(1, readBufferSize);
        audioRecorder = new AudioRecorder(recorderType, sampleRate, intervalTime, readBufferSize, onRecorderListener);
    }

    public boolean isAudioRecorderStart() {
        boolean isStart = false;
        if (audioRecorder != null) {
            LogUtils.i(TAG, "isAudioRecorderStart audioRecorder==null");
            isStart = audioRecorder.isRecordingStart();
        }
        LogUtils.i(TAG, "isAudioRecorderStart isStart:" + isStart);
        return isStart;
    }

    public void startAudioRecorder() {
        if (audioRecorder == null)
            return;
        audioRecorder.start();
    }

    public void stopAudioRecorder() {
        if (audioRecorder == null)
            return;
        audioRecorder.stop();
    }

    public void enableSendAudio(boolean enableSend) {
        LogUtils.d(TAG, "enableSendAudio:" + enableSend);
        enableSendAudio = enableSend;
    }

    public void releaseAudioRecorder() {
        audioRecorder.release();
        audioRecorder = null;
    }


    private final RecorderContract.OnRecorderListener onRecorderListener = new RecorderContract.OnRecorderListener() {
        @Override
        public void onRecordStarted(long sessionId) {
            //todo:
            LogUtils.i(TAG, "onRecordStarted sessionId is  " + sessionId);
        }

        @Override
        public void onRecordStopped(long sessionId) {
            //todo:
            LogUtils.i(TAG, "onRecordStopped sessionId is  " + sessionId);
        }

        @Override
        public void onException(DhError dhError) {
            //todo:
        }

        @Override
        public void onRawDataReceived(long sessionId, byte[] buffer, int size) {
            if (null == buffer || !enableSendAudio)
                return;
            if (isSelfInnovate) {
                if (isSendAll) {
                    //H37B数据不做拆分重组
                    VoiceImpl.getInstance().sendAudio(buffer);
                } else {
                    //10通道数据拆分为8通道后传
                    byte[] bytes = RecordDataUtils.transferRecordDataFor2560(buffer, transBuf);
                    VoiceImpl.getInstance().sendAudio(bytes);
                }
            } else {
                //8通道数据直传
                VoiceImpl.getInstance().sendAudio(buffer);
            }
            for (IPcmAudioCallback callBack : pcmAudioCallbacks) {
                callBack.onPcmInAudio(buffer);
            }
        }

        @Override
        public void onDestroy() {
            //todo:
        }
    };


    /**
     * 添加音频回调
     */
    public void addPcmAudioCallback(IPcmAudioCallback callBack) {
        pcmAudioCallbacks.add(callBack);
    }

    /**
     * 移除音频回调
     */
    public void removePcmAudioCallback(IPcmAudioCallback callBack) {
        pcmAudioCallbacks.remove(callBack);
    }
}
