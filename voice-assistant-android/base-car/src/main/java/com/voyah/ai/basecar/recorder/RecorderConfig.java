package com.voyah.ai.basecar.recorder;

import android.media.AudioFormat;
import android.media.MediaRecorder;

/**
 * 录音机初始化配置
 */
public class RecorderConfig {
    /**
     * 录音机采集数据间隔
     */
    public int intervalTime;
    /**
     * audio source
     */
    public int audioSource;
    /**
     * 采样率
     */
    public int sampleRate;

    /**
     * 编码： AudioFormat.ENCODING_PCM_16BIT
     */
    public int audioEncoding;

    /**
     * 录音类型
     */
    public int recorderType;

    /**
     * 单麦模式,获取单通道音频
     */
    public static final int TYPE_COMMON_MIC = 1;

    /**
     * echo 2mic模式,获取4通道音频(包含2路参考音)
     */
    public static final int TYPE_COMMON_ECHO_2MIC = 2;

    /**
     * echo 4mic模式,获取8通道音频(包含4路参考音)
     */
    public static final int TYPE_COMMON_ECHO_4MIC = 4;

    /**
     * 十通道增益
     */
    public static final int TYPE_COMMON_GAIN_4MIC = 10;

    /**
     * adsp回消后的4路音频
     */
    public static final int TYPE_COMMON_ADSP_ECHO_2MIC = 0x0f; //15

    /**
     * adsp回消后的6路音频
     */
    public static final int TYPE_COMMON_ADSP_ECHO_4MIC = 0x3f;//63

    private RecorderConfig(int intervalTime, int audioSource, int recorderType, int sampleRate, int audioEncoding) {
        this.intervalTime = intervalTime;
        this.audioSource = audioSource;
        this.recorderType = recorderType;
        this.sampleRate = sampleRate;
        this.audioEncoding = audioEncoding;
    }

    private RecorderConfig(Builder builder) {
        this(builder.intervalTime, builder.audioSource, builder.recorderType, builder.sampleRate, builder.audioEncoding);
    }

    public static class Builder {

        private int intervalTime = 100;

        private int audioSource = MediaRecorder.AudioSource.VOICE_RECOGNITION;

        private int recorderType = TYPE_COMMON_ADSP_ECHO_4MIC;

        private int sampleRate = 16000;

        private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

        /**
         * 设置录音机采集数据间隔
         *
         * @param intervalTime 录音机采样间隔,默认间隔100ms
         * @return {@link Builder}
         */
        public Builder setIntervalTime(int intervalTime) {
            this.intervalTime = intervalTime;
            return this;
        }

        /**
         * 设置audioRecorder的声音源
         *
         * @param audioSource 默认  {@link MediaRecorder.AudioSource#MIC}
         *                    可选值：{@link MediaRecorder.AudioSource#MIC} 和 {@link MediaRecorder.AudioSource#VOICE_RECOGNITION}
         * @return {@link Builder}
         */
        public Builder setAudioSource(int audioSource) {
            this.audioSource = audioSource;
            return this;
        }

        /**
         * 设置录音机类型
         *
         * @param recorderType
         * @return {@link Builder}
         */
        public Builder setRecorderType(int recorderType) {
            this.recorderType = recorderType;
            return this;
        }

        /**
         * 设置采样率
         *
         * @param sampleRate
         * @return
         */
        public Builder setSampleRate(int sampleRate) {
            this.sampleRate = sampleRate;
            return this;
        }

        /**
         * 设置编码
         *
         * @param audioEncoding
         * @return
         */
        public Builder setAudioEncoding(int audioEncoding) {
            this.audioEncoding = audioEncoding;
            return this;
        }

        public RecorderConfig create() {
            return new RecorderConfig(this);
        }
    }

}
