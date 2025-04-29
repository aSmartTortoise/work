package com.voyah.ai.basecar.system;

import android.car.Car;
import android.car.media.CarAudioManager;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;

import com.blankj.utilcode.util.Utils;
import com.voice.sdk.device.system.VolumeInterface;
import com.voice.sdk.device.system.VolumeStream;
import com.voyah.ai.common.utils.ContextUtils;
import com.voyah.ai.common.utils.LogUtils;

public class CommonVolumeInterfaceImpl implements VolumeInterface {
    private static final String TAG = "VolumeInterfaceImpl";

    private final CarAudioManager carAudioManager;
    private final AudioManager audioManager;

    private static final int STREAM_MUSIC = 3; //媒体
    private static final int STREAM_VOICE_CALL = 0; //通话
    private static final int STREAM_NOTIFICATION = 5; //系统
    private static final int STREAM_ASSISTANT = 11; //语音
    private static final int STREAM_NAVI = 9; //导航


    public CommonVolumeInterfaceImpl() {
        Car car = Car.createCar(ContextUtils.getAppContext());
        carAudioManager = (CarAudioManager) car.getCarManager(Car.AUDIO_SERVICE);
        audioManager = (AudioManager) Utils.getApp().getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void setMuted(VolumeStream stream, boolean muted, int minVolume) {
        LogUtils.i(TAG, "setMuted:" + stream + ",muted:" + muted + ",minVolume:" + minVolume);
        int guidance = getStreamGuidance(stream);
        setMuteSound(guidance, muted);
        if (!muted) {
            int volume = getVolume(stream);
            if (minVolume > volume) {
                setVolume(stream, minVolume);
            }
        }

    }

    @Override
    public boolean isMuted(VolumeStream module) {
        int guidance = getStreamGuidance(module);
        boolean isMuted = carAudioManager.isVolumeGroupMuted(0, getGroupIdForUsage(guidance));
        LogUtils.i(TAG, "isMuted:" + isMuted + ",module:" + module);
        return isMuted;
    }

    @Override
    public int getVolume(VolumeStream module) {
        int stream = getStreamValue(module);
        int volume = audioManager.getStreamVolume(stream);
        LogUtils.i(TAG, "getVolume:" + volume + ",module:" + module);
        return volume;
    }

    @Override
    public void setVolume(VolumeStream module, int volume) {
        int stream = getStreamValue(module);
        LogUtils.i(TAG, "setVolume:" + volume + ",module:" + module);
        audioManager.setStreamVolume(stream, volume, AudioManager.FLAG_SHOW_UI);
    }

    @Override
    public int getStreamValue(VolumeStream volumeStream) {
        int stream = -1;
        switch (volumeStream) {
            case STREAM_NAVI:
                stream = STREAM_NAVI;
                break;
            case STREAM_PHONE:
                stream = STREAM_VOICE_CALL;
                break;
            case STREAM_VOICE:
                stream = STREAM_ASSISTANT;
                break;
            case STREAM_MEDIA:
                stream = STREAM_MUSIC;
                break;
            case STREAM_SYSTEM:
                stream = STREAM_NOTIFICATION;
                break;
            default:
                break;
        }
        return stream;
    }

    private int getStreamGuidance(VolumeStream volumeStream) {
        int guidance = AudioAttributes.USAGE_UNKNOWN;
        switch (volumeStream) {
            case STREAM_NAVI:
                guidance = AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE;
                break;
            case STREAM_PHONE:
                guidance = AudioAttributes.USAGE_VOICE_COMMUNICATION;
                break;
            case STREAM_VOICE:
                guidance = AudioAttributes.USAGE_ASSISTANT;
                break;
            case STREAM_MEDIA:
                guidance = AudioAttributes.USAGE_MEDIA;
                break;
            case STREAM_SYSTEM:
                guidance = AudioAttributes.USAGE_NOTIFICATION;
                break;
            default:
                break;
        }
        return guidance;
    }


    private void setMuteSound(int guidance, boolean mute) {
        LogUtils.i(TAG, "setMuteSound:" + guidance + ",mute:" + mute);
        if (carAudioManager == null) {
            LogUtils.d("VolumeUtils", "setMuteSound -- CarAudioManager = null");
            return;
        }
        carAudioManager.setVolumeGroupMute(0, getGroupIdForUsage(guidance), mute, AudioManager.FLAG_SHOW_UI);
    }

    private int getGroupIdForUsage(int usage) {
        return carAudioManager.getVolumeGroupIdForUsage(usage);
    }

}
