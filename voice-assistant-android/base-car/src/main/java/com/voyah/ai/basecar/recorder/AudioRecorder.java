package com.voyah.ai.basecar.recorder;

import static com.voyah.ai.basecar.recorder.DhError.RECORDER_STATE_UNINITIALIZED;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.voyah.ai.basecar.recorder.mem.AudioByteArrMgr;
import com.voyah.ai.basecar.recorder.mem.ByteArrMgr;
import com.voyah.ai.common.utils.LogUtils;


/**
 * Created by lcy on 2023/12/18.
 */

public class AudioRecorder {

    private static final String TAG = "AudioRecorder";

    // 录音机重试次数
    private static final int AUDIORECORD_RETRY_TIMES = 4;
    // 录音机声道数,默认单通道
    public static int audio_channel_num = 1;
    /**
     * 录音机配置
     */
    private RecorderConfig config;
    /**
     * Android 标准录音机api
     */
    private volatile AudioRecord mCommonRecorder;

    /**
     * 录音机启动SessionId
     */
    private volatile long mSessionId;

    private volatile boolean isRecording;
    private final int mReadBufferSize;

    private boolean isRecorderReadError = false;

    private static byte[] surplusBuffer;

    private static int type = 1;

    private static long pcm_currentTime;
    private final RecorderContract.OnRecorderListener listener;


    /**
     * 使用指定参数来初始化录音机
     *
     * @param recorderType     录音机类型
     * @param sampleRate       采样率
     * @param intervalTime     数据抛出间隔
     * @param recorderListener 回调
     */

    //"audioSource": 6,"recorderType": 63,"intervalTime": 16,"sampleRate": 16000 语音+8通道+16K
    public AudioRecorder(int recorderType, int sampleRate, int intervalTime, int readBufferSize,
                         RecorderContract.OnRecorderListener recorderListener) {
        this.mReadBufferSize = readBufferSize;
        this.listener = recorderListener;
        createAudioRecorder(recorderType, sampleRate, intervalTime);
    }

    public void createAudioRecorder(int recorderType, int sampleRate, int intervalTime) {
        config = new RecorderConfig.Builder()
                .setSampleRate(sampleRate)
                .setIntervalTime(intervalTime)
                .setRecorderType(recorderType)
                .setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION)
                .setAudioEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .create();
        newAudioRecorder();
    }

    /**
     * 创建录音机及读线程池
     */

    private void newAudioRecorder() {
        int retryCounter = 0;
        int recorderType = config.recorderType;
        int audioSampleRate = config.sampleRate;
        int intervalTime = config.intervalTime;
        int audioSource = config.audioSource;

        int audioEncoding = config.audioEncoding;
        LogUtils.i(TAG, "recorderType is " + recorderType + " ,audioSampleRate is " + audioSampleRate
                + "intervalTime is " + intervalTime + " , audioEncoding is " + audioEncoding);
        while (true) {
            // 带重试的创建录音机
            try {
                if (recorderType == RecorderConfig.TYPE_COMMON_MIC) {
                    audio_channel_num = 1;
                    int bufferSizeInBytes = getReadBufferSize(audioSampleRate, intervalTime, audioEncoding) * 10;
                    mCommonRecorder = new AudioRecord(audioSource, audioSampleRate,
                            AudioFormat.CHANNEL_CONFIGURATION_MONO, audioEncoding,
                            bufferSizeInBytes);
                } else if (recorderType == RecorderConfig.TYPE_COMMON_ECHO_2MIC || recorderType == RecorderConfig.TYPE_COMMON_ECHO_4MIC) {
                    audio_channel_num = 8;
                    int bufferSizeInBytes = getReadBufferSize(audioSampleRate, intervalTime, audioEncoding) * 10;
                    //2mic和4mic暂时都输出8路音频由底层决定，对于2mic模式需要在ASP层去进行过滤另外4路音频
                    // (前左M | 前右M | 后左M | 后后M | 前左R | 前右R | 后左R | 后后R)
                    //255
                    final AudioFormat audioFormat = new AudioFormat.Builder()
                            .setEncoding(audioEncoding)
                            .setSampleRate(audioSampleRate)
                            .setChannelIndexMask(0xff)
                            .build();
                    mCommonRecorder = new AudioRecord.Builder().setAudioFormat(audioFormat)
                            .setAudioSource(audioSource)
                            .setBufferSizeInBytes(bufferSizeInBytes)
                            .build();
                    // 4路唤醒+2路识别  15 63
                } else if (recorderType == RecorderConfig.TYPE_COMMON_ADSP_ECHO_2MIC || recorderType == RecorderConfig.TYPE_COMMON_ADSP_ECHO_4MIC) {
                    //6通道  ChannelIndexMask 0x3f  audioEncoding 2 audioSampleRate 16000  intervalTime  16
                    audio_channel_num = 6;
                    int bufferSizeInBytes = getReadBufferSize(audioSampleRate, intervalTime, audioEncoding) * 10;
                    final AudioFormat audioFormat = new AudioFormat.Builder()
                            .setEncoding(audioEncoding)
                            .setSampleRate(audioSampleRate)
                            .setChannelIndexMask(0x3f)
                            .build();
                    mCommonRecorder = new AudioRecord.Builder().setAudioFormat(audioFormat)
                            .setAudioSource(audioSource)
                            .setBufferSizeInBytes(bufferSizeInBytes)
                            .build();
                }else if (recorderType == RecorderConfig.TYPE_COMMON_GAIN_4MIC) {
                    audio_channel_num = 10;
                    int bufferSizeInBytes = getReadBufferSize(audioSampleRate, intervalTime, audioEncoding) * 10;
                    //10通道增益
                    final AudioFormat audioFormat = new AudioFormat.Builder()
                            .setEncoding(audioEncoding)
                            .setSampleRate(audioSampleRate)
                            .setChannelIndexMask(0x3ff)
                            .build();
                    mCommonRecorder = new AudioRecord.Builder().setAudioFormat(audioFormat)
                            .setAudioSource(audioSource)
                            .setBufferSizeInBytes(bufferSizeInBytes)
                            .build();
                }

                LogUtils.i(TAG, "audio_channel_num is : " + audio_channel_num);
                if (mCommonRecorder == null) {
                    throw new DhError(DhError.RECORDER_TYPE_NONEXISTENT);
                } else if (mCommonRecorder.getState() == AudioRecord.STATE_UNINITIALIZED) {
                    LogUtils.e(TAG, "Error: AudioRecord state == STATE_UNINITIALIZED");
                    throw new DhError(RECORDER_STATE_UNINITIALIZED);
                } else {
                    LogUtils.i(TAG, "recorder.new() retry count: " + retryCounter);
                    break;
                }
            } catch (DhError e) {
                e.getMessage();
                if (retryCounter < AUDIORECORD_RETRY_TIMES) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    retryCounter++;
                    continue;
                }
                onException(e);
                return;
            }
        }
    }

    //启动录音机
    public void start() {
//        AtomicInteger count = new AtomicInteger();
        final long[] startTime = {System.currentTimeMillis()};
        if (null == mCommonRecorder || isRecording)
            return;
        isRecording = true;
        new Thread(() -> {
            byte[] readBuffer = new byte[mReadBufferSize];
            int readSize = 0;
            if (startAudioRecorder()) {
                mSessionId = PcmUtil.generateRandom();
                while (isRecording) {
                    try {
                        if (mCommonRecorder != null) {
                            readSize = mCommonRecorder.read(readBuffer, 0, mReadBufferSize);//从录音机录的实时数据
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    if (readSize == mReadBufferSize) {
//                        onRawBufferReceived(mSessionId, readBuffer, mReadBufferSize);
//                    } else
                    if (readSize == 0) {
//                        if (!isRecorderReadError) {
//                            isRecorderReadError = true;
//                            AudioRecorderManager.getInstance().stopAudioRecorder();
//                            LogUtils.e(TAG, "recorder error read size : " + readSize + " ,stop recorder");
//                        }
                        //todo:读取异常时考虑停止一段时间然后重新开启(cpu增长80%)
//                        if (count.get() == 100000) {
//                            LogUtils.e(TAG, "recorder error read size : " + readSize);
//                            count.set(0);
//                        }
//
//                        count.getAndIncrement();

                        long currentTime = System.currentTimeMillis();
                        if ((currentTime - startTime[0]) >= 10 * 1000) {
                            LogUtils.e(TAG, "recorder error read size : " + readSize);
                            startTime[0] = currentTime;
                        }
                    } else {
//                        LogUtils.i(TAG, "readBuffer.length is " + readBuffer.length);
                        splitBuffer1(mSessionId, readBuffer);
                    }
                }
            }
        }).start();
    }

    //停录音机
    public void stop() {
        if (null == mCommonRecorder || !isRecording)
            return;
        isRecording = false;
        mCommonRecorder.stop();
        onRecordStopped(mSessionId);
    }


    //释放录音机
    public void release() {
        LogUtils.i(TAG, "release");
        if (mCommonRecorder == null) {
            LogUtils.e(TAG, "recorder new failed");
            return;
        }
        stop();
        releaseAudioRecorder();
    }

    //释放录音机
    private void releaseAudioRecorder() {
        LogUtils.d(TAG, "AudioRecord.release() before");
        if (mCommonRecorder != null) {
            mCommonRecorder.release();
            mCommonRecorder = null;
        }
        onRecordReleased();
        LogUtils.d(TAG, "AudioRecord.release() after");
    }

    /**
     * 带重试地启动录音机
     *
     * @return true 启动成功, false 启动失败
     */
    private boolean startAudioRecorder() {
        int retryCounter = 0;
        while (true) {
            try {
                LogUtils.d(TAG, "recorder.startRecording()");
                if (mCommonRecorder != null) {
                    mCommonRecorder.startRecording();
                    if (mCommonRecorder.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
                        throw new DhError();
                    } else {
                        onRecordStarted(mSessionId);
                        LogUtils.d(TAG, "recorder.start() retry count: " + retryCounter);
                        break;
                    }
                }
            } catch (DhError e) {
                e.printStackTrace();
                if (retryCounter < AUDIORECORD_RETRY_TIMES) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    retryCounter++;
                    continue;
                }
                onException(e);
                return false;
            }
        }
        return true;
    }

    /**
     * 异常回调
     *
     * @param e
     */
    private void onException(DhError e) {
        if (listener != null) {
            listener.onException(e);
        }
    }

    /**
     * 录音开启回调
     *
     * @param sessionId
     */
    private void onRecordStarted(long sessionId) {
        if (listener != null) {
            listener.onRecordStarted(sessionId);
        }
    }


    /**
     * 从录音机获取的原始pcm数据
     *
     * @param sessionId
     * @param buffer
     * @param size
     */
    private void onRawBufferReceived(long sessionId, final byte[] buffer, final int size) {
        if (listener != null) {
            listener.onRawDataReceived(sessionId, buffer, size);
        }
    }

    /**
     * 停止录音回调
     *
     * @param sessionId
     */
    private void onRecordStopped(long sessionId) {
        if (listener != null) {
            listener.onRecordStopped(sessionId);
        }
    }

    /**
     * 释放录音机回调
     */
    private synchronized void onRecordReleased() {
        if (listener != null) {
            listener.onDestroy();
        }
    }

    /**
     * Util to get record device.
     */
    private int getReadBufferSize(int sampleRate, int intervalTime, int audioEncoding) {
        int read_buffer_size = sampleRate * audio_channel_num * audioEncoding
                * intervalTime / 1000;
        LogUtils.d(TAG, "[SampleRate = " + sampleRate + ", ReadBufferSize = " + read_buffer_size + "]");
        return read_buffer_size;
    }

    public void splitBuffer1(long sessionId, byte[] bytes) {
        byte[] originBytes;
        //剩余数据拼接
        int surplusBufferSize = null == surplusBuffer ? 0 : surplusBuffer.length;
        if (surplusBuffer != null && surplusBufferSize > 0) {
            originBytes = new byte[surplusBufferSize + bytes.length];
            System.arraycopy(surplusBuffer, 0, originBytes, 0, surplusBufferSize);
            System.arraycopy(bytes, 0, originBytes, surplusBufferSize, bytes.length);
        } else {
            originBytes = bytes;
        }
        int count = originBytes.length / mReadBufferSize;
        for (int i = 0; i < count; ++i) {
            ByteArrMgr.ByteArrObj obj = AudioByteArrMgr.getInstance().getByteObj(type);
            if (obj.len > 0) {
                obj.reset();
            }
            int len = originBytes.length - i * mReadBufferSize;
            if (len > mReadBufferSize) {
                len = mReadBufferSize;
            }
            System.arraycopy(originBytes, i * mReadBufferSize, obj.arr, 0, len);
            obj.len = len;
            onRawBufferReceived(sessionId, obj.arr, mReadBufferSize);
        }
        int surplusBufferLen = originBytes.length - (count * mReadBufferSize);
        if (surplusBufferLen > 0) {
            surplusBuffer = new byte[surplusBufferLen];
            System.arraycopy(originBytes, count * mReadBufferSize, surplusBuffer, 0, surplusBufferLen);
        } else {
            surplusBuffer = null;
        }
    }

    public long getPcm_currentTime() {
        return pcm_currentTime;
    }

    public boolean isRecordingStart() {
        return isRecording;
    }

}
