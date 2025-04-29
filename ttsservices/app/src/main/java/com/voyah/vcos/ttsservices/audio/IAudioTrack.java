package com.voyah.vcos.ttsservices.audio;

import android.content.Context;
import android.media.AudioAttributes;

import androidx.annotation.NonNull;


/**
 * 接口说明： 播放器接口
 */
public interface IAudioTrack {

    /**
     * 初始化
     */
    void init(Context context, int channel);

    /**
     * 播放
     */
    long play();

    /**
     * 停止
     */
    void stop();

    /**
     * 恢复
     */
    void resume();

    /**
     * 暂停
     */
    void pause();

    /**
     * 清空缓冲区
     */
    void flush();

    /**
     * 释放资源
     */
    void release();

    /**
     * 设置播放器回调接口
     *
     * @param listener
     */
//    void setPlayerListener(IPlayerListener listener);

    /**
     * AudioTrack播放接口
     *
     * @param buffer
     * @param length
     */
    void appendAudioData(@NonNull byte[] buffer, int length);

    void appendAudioData(@NonNull byte[] buffer, int length, String ttsId);


    /**
     * 设置音频流总大小
     *
     * @param size
     */
//    void setTotalAudioDataSize(int size);

    void appendAudioData(@NonNull byte[] buffer, int size, String ttsId, int ttsType);

    /**
     * 得到minBuffer大小
     *
     * @return
     */
    int getMinBuffSize();

    /**
     * 是否在播放中
     *
     * @return
     */
    boolean isPlaying();

    void setAudioTrackCallBack(AudioTrackCallBack audioTrackCallBack);
}
