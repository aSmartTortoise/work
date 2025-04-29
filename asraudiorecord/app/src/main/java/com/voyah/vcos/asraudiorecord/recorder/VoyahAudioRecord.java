package com.voyah.vcos.asraudiorecord.recorder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.voyah.vcos.asraudiorecord.util.DeviceUtils;
import com.voyah.vcos.asraudiorecord.util.LogUtils;


public class VoyahAudioRecord extends Thread {

    private static final String TAG = "VoyahAudioRecord";

    private AudioRecord audioRecord;

    private final byte[] audioCache;

    private volatile boolean isRecord = false;

    private final IRecordListener recordListener;

    private final int SAMPLE_RATE = 16000;

    private final int AUDIO_CHANNEL_NUM = 10;

    private final int INTERVAL_TIME = 100;


    private int getReadBufferSize() {
        int bufferSize = SAMPLE_RATE * AUDIO_CHANNEL_NUM * AudioFormat.ENCODING_PCM_16BIT
                * INTERVAL_TIME / 1000;
        LogUtils.i(TAG, "getReadBufferSize:" + bufferSize);
        return bufferSize;
    }


    public VoyahAudioRecord(IRecordListener recordListener) {
        this.recordListener = recordListener;
        LogUtils.i(TAG, "VoyahAudioRecord");
        if (DeviceUtils.isVoyahDevice()) {
            int bufferSize = getReadBufferSize();
            audioCache = new byte[bufferSize];
            LogUtils.i(TAG, "bufferSizeInBytes:" + bufferSize);
            int channelMask = 0x3ff;

            final AudioFormat audioFormat = new AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelIndexMask(channelMask)
                    .build();
            audioRecord = new AudioRecord.Builder().setAudioFormat(audioFormat)
                    .setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION)
                    .setBufferSizeInBytes(bufferSize)
                    .build();
        } else {
            audioCache = new byte[(16000 * AudioFormat.ENCODING_PCM_16BIT * 100) / 1000];
            int bufferSizeInBytes = AudioRecord.getMinBufferSize(16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            LogUtils.i(TAG, "bufferSizeInBytes:" + bufferSizeInBytes);

            final AudioFormat audioFormat = new AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                    .setSampleRate(16000)
                    .build();
            audioRecord = new AudioRecord.Builder().setAudioFormat(audioFormat)
                    .setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION)
                    .setBufferSizeInBytes(bufferSizeInBytes)
                    .build();
        }
        LogUtils.i(TAG, "audioCache:" + audioCache.length);
        if (audioRecord.getRecordingState() != AudioRecord.STATE_INITIALIZED) {
            LogUtils.e(TAG, "audio record init failed");
            throw new RuntimeException("record not init error");
        } else {
            LogUtils.i(TAG, "audio record init success");
        }
    }


    public void startRecord() {
        LogUtils.i(TAG, "startRecord:" + isRecord + ",state:" + audioRecord.getRecordingState());
        if (!isRecord && audioRecord.getRecordingState() == AudioRecord.STATE_INITIALIZED) {
            audioRecord.startRecording();
            isRecord = true;
            start();
        }
    }


    public void stopRecord() {
        LogUtils.i(TAG, "stopRecord:" + isRecord);
        if (isRecord) {
            isRecord = false;
            try {
                join(3000);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.e(TAG, "stopRecord:" + e);
            }
        }

    }


    public void releaseRecord() {
        LogUtils.i(TAG, "releaseRecord:" + (audioRecord != null ? audioRecord.getRecordingState() : null));
        if (audioRecord != null) {
            stopRecord();
            if (audioRecord != null) {
                if (audioRecord.getRecordingState() == AudioRecord.STATE_INITIALIZED) {
                    audioRecord.release();
                }
                audioRecord = null;
            }
        }
    }

    @Override
    public void run() {
        super.run();
        if (recordListener != null) {
            recordListener.recordStart();
        }
        while (isRecord) {
            int size = audioRecord.read(audioCache, 0, audioCache.length);
            if (size > 0) {
                if (recordListener != null) {
                    byte[] transBuffer = new byte[(size / 10 * 8)];
                    transferRecordDataFor2560(audioCache, transBuffer, size);
                    recordListener.recordData(transBuffer, transBuffer.length);
                }
            }
        }
        if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
            audioRecord.stop();
        }
        if (recordListener != null) {
            recordListener.recordStop();
        }
    }


    public static void transferRecordDataFor2560(byte[] pcmData, byte[] transBuf, int size) {
        int j = 0;
        for (int i = 0; i < size; i = i + 20) {
            transBuf[j++] = pcmData[i];
            transBuf[j++] = pcmData[i + 1];

            transBuf[j++] = pcmData[i + 2];
            transBuf[j++] = pcmData[i + 3];

            transBuf[j++] = pcmData[i + 4];
            transBuf[j++] = pcmData[i + 5];

            transBuf[j++] = pcmData[i + 6];
            transBuf[j++] = pcmData[i + 7];

            transBuf[j++] = pcmData[i + 12];
            transBuf[j++] = pcmData[i + 13];

            transBuf[j++] = pcmData[i + 14];
            transBuf[j++] = pcmData[i + 15];

            transBuf[j++] = pcmData[i + 16];
            transBuf[j++] = pcmData[i + 17];

            transBuf[j++] = pcmData[i + 18];
            transBuf[j++] = pcmData[i + 19];
        }
    }
}
