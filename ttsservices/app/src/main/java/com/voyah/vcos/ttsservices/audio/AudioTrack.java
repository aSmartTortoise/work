package com.voyah.vcos.ttsservices.audio;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.voyah.vcos.ttsservices.Constant;
import com.voyah.vcos.ttsservices.utils.LogUtils;


/**
 * 类说明： AudioTrack播放器，可以播放完整的pcm和wav文件
 */
public class AudioTrack implements IAudioTrack {
    private static final String TAG = "MyAudioTrack";
    private static final int STATE_STOP = 0;
    private static final int STATE_PLAY = 1;
    private static final int STATE_PAUSE = 2;
    private static final int STATE_RELEASE = 3;

    private volatile int mAudioTrackState; //audioTrack状态

    private android.media.AudioTrack mAudioTrack = null;
    private int mMinBuffSize = 0;

    private boolean isFirstByte = true;

    private String currentTtsId = "";
    private AudioTrackCallBack mAudioTrackCallBack;


    @Override
    public void appendAudioData(byte[] buff, int size) {
//        if (mAudioTrackState == STATE_PLAY || mAudioTrackState == STATE_PAUSE) {
//            if (size > 0) {
////                mAudioTrack.write(buff, 0, size);
//                mAudioTrack.write(buff, 0, size, android.media.AudioTrack.WRITE_BLOCKING);
//            } else {
//                mAudioTrack.setNotificationMarkerPosition(1);
//                mAudioTrackState = STATE_STOP;
//            }
//        } else {
//            LogUtils.i(TAG, "appendAudioData() called, current state:" + mAudioTrackState + "  size:" + size);
//        }
    }

    @Override
    public void appendAudioData(@NonNull byte[] buffer, int length, String ttsId) {

    }

    @Override
    public void appendAudioData(@NonNull byte[] buffer, int size, String ttsId, int ttsType) {
//        LogUtils.d(TAG, "appendAudioData2  ttsId:" + ttsId);
//        if (mAudioTrackState == STATE_PLAY || mAudioTrackState == STATE_PAUSE) {
        //检查下当前是否为可播放状态
        if (mAudioTrackState != STATE_PLAY) {
            play();
        }

        if (isFirstByte && !TextUtils.equals(currentTtsId, ttsId))
            LogUtils.d(TAG, "appendAudioData  ttsId:" + ttsId + " ,size:" + size);
        if (size > 0) {
//                mAudioTrack.write(buffer, 0, size);
            mAudioTrack.write(buffer, 0, size, android.media.AudioTrack.WRITE_BLOCKING);
            isFirstByte = false;
            currentTtsId = ttsId;
        } else if (size == 0) {
            LogUtils.d(TAG, "appendAudioData size is 0 ttsId:" + ttsId);
            mAudioTrackCallBack.complete(ttsId, ttsType);
        } else {
            mAudioTrack.setNotificationMarkerPosition(1);
            mAudioTrackState = STATE_STOP;
        }
//        } else {
//            LogUtils.i(TAG, "appendAudioData() called, current state:" + mAudioTrackState + "  size:" + size);
//        }
    }


    @Override
    public int getMinBuffSize() {
        return mMinBuffSize;
    }

    @Override
    public boolean isPlaying() {
        if (mAudioTrack != null) {
            return mAudioTrackState == STATE_PLAY;
        } else {
            return false;
        }
    }

    @Override
    public void setAudioTrackCallBack(AudioTrackCallBack audioTrackCallBack) {
        this.mAudioTrackCallBack = audioTrackCallBack;
    }

    @Override
    public void init(Context context, int usage) {
        LogUtils.i(TAG, "usage is " + usage);
        init(new AudioAttributes.Builder().setUsage(usage).setContentType(Constant.AudioTrackConfig.CONTENT_TYPE)
                .build());
    }

    private void init(AudioAttributes audioAttributes) {
        mMinBuffSize = android.media.AudioTrack.getMinBufferSize(Constant.AudioTrackConfig.SAMPLE_RATE, Constant.AudioTrackConfig.CHANNEL_CONFIG, Constant.AudioTrackConfig.AUDIO_FORMAT);
        LogUtils.i(TAG, "mMinBuffSize is " + mMinBuffSize); //mMinBuffSize is 3856
        if (mAudioTrack == null) {
            mAudioTrack = new android.media.AudioTrack.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setAudioFormat(new AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(Constant.AudioTrackConfig.SAMPLE_RATE)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO) //单声道-没有空间定位的声音 双声道-需要空间定位的声音
                            .build())
                    .setTransferMode(android.media.AudioTrack.MODE_STREAM)
                    .setBufferSizeInBytes(mMinBuffSize)
                    .build();
            if (mAudioTrack.getState() != android.media.AudioTrack.STATE_INITIALIZED) {
                LogUtils.e(TAG, "init: fail");
                return;
            }
            mAudioTrack.setNotificationMarkerPosition(0);
            mAudioTrack.setPlaybackPositionUpdateListener(new android.media.AudioTrack.OnPlaybackPositionUpdateListener() {
                @Override
                public void onMarkerReached(android.media.AudioTrack track) {
                    //todo:播放完成？
                    LogUtils.e(TAG, "onMarkerReached " + track.getPlayState());
                    mAudioTrack.flush();
                    mAudioTrack.setNotificationMarkerPosition(0);
                }

                @Override
                public void onPeriodicNotification(android.media.AudioTrack track) {
                    LogUtils.e(TAG, "onPeriodicNotification " + track.getPlayState());
                }
            });
        }
        mAudioTrackState = STATE_STOP;
    }


    @Override
    public long play() {
        LogUtils.i(TAG, "play mAudioTrackState:" + mAudioTrackState);
//        if (this.mAudioTrackState != STATE_RELEASE) {
        this.mAudioTrackState = STATE_PLAY;
        mAudioTrack.flush();
        mAudioTrack.play();
        isFirstByte = true;
        currentTtsId = "";
//        } else {
//            LogUtils.d(TAG, "play() called, current state:" + mAudioTrackState);
//        }

        return 0;
    }

    @Override
    public void resume() {
        LogUtils.i(TAG, "resume mAudioTrackState:" + mAudioTrackState);
//        if (mAudioTrackState == STATE_PAUSE) {
        mAudioTrackState = STATE_PLAY;
        mAudioTrack.play();
//        } else {
//            LogUtils.d(TAG, "resume() called, current state:" + mAudioTrackState);
//        }
    }

    @Override
    public void pause() {
        LogUtils.i(TAG, "pause mAudioTrackState:" + mAudioTrackState);
//        if (mAudioTrackState == STATE_PLAY) {
        mAudioTrackState = STATE_PAUSE;
        mAudioTrack.pause();
//        } else {
//            LogUtils.d(TAG, "pause() called, current state:" + mAudioTrackState);
//        }
    }

    @Override
    public void flush() {
        LogUtils.i(TAG, "flush mAudioTrackState:" + mAudioTrackState);
        if (null != mAudioTrack) {
//            mAudioTrack.stop();
            mAudioTrack.flush();
        }
    }

    @Override
    public void stop() {
        LogUtils.i(TAG, "stop mAudioTrackState:" + mAudioTrackState);
//        if (mAudioTrackState == STATE_PAUSE || mAudioTrackState == STATE_PLAY) {
        mAudioTrackState = STATE_STOP;
        /** For an immediate stop, use pause(), followed by flush() to discard audio data
         * that hasn't been played back yet.
         */
        mAudioTrack.stop();
        mAudioTrack.flush();
        isFirstByte = true;
        currentTtsId = "";
//        } else {
//            LogUtils.d(TAG, "stop() called, current state:" + mAudioTrackState);
//        }
    }

    @Override
    public void release() {
        mAudioTrackState = STATE_RELEASE;
        mAudioTrack.release();
    }
}
