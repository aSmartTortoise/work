package com.voyah.ai.basecar.utils;

import static com.mega.nexus.media.MegaAudioManager.EXTRA_VOLUME_STREAM_TYPE;
import static com.mega.nexus.media.MegaAudioManager.VOLUME_CHANGED_ACTION;

import android.bluetooth.BluetoothA2dp;
import android.car.Car;
import android.car.media.CarAudioManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.provider.Settings;

import com.blankj.utilcode.util.Utils;
import com.voyah.ai.basecar.carservice.CarPropUtils;
import com.voyah.ai.common.utils.ContextUtils;
import com.voyah.ai.common.utils.LogUtils;

import java.util.List;

import mega.config.MegaDataStorageConfig;

public class VolumeUtils {

    public AudioManager mAudioManager;
    private static VolumeUtils mVolumeUtils;
    private CarAudioManager carAudioManager;

    public static final int STREAM_MUSIC = 3; //媒体
    public static final int STREAM_VOICE_CALL = 0; //通话
    public static final int STREAM_NOTIFICATION = 5; //系统
    public static final int STREAM_ASSISTANT = 11; //语音
    public static final int STREAM_NVI = 9; //导航
    public static final int STREAM_BLUETOOTH = 12; //蓝牙
    public static final int STEP_VOLUME = 3; //音量设置步长
    public static final int VOLUME_MAX = 30; //音量最大
    public static final int VOLUME_MIN = 0; //音量最小
    public static final int VOLUME_VOL_MIN = 1; //通话音量最小
    public int mLastBtHeadsetVol = 15; //默认蓝牙耳机音量=15
    public int isNaviSpeakState = -1;
    public BluetoothA2dp mBluetoothA2dp;

    public static VolumeUtils getInstance() {
        if (null == mVolumeUtils) {
            synchronized (CarPropUtils.class) {
                if (null == mVolumeUtils) {
                    mVolumeUtils = new VolumeUtils();
                }
            }
        }
        return mVolumeUtils;
    }

    public int getNaviSpeakState(){
        return isNaviSpeakState;
    }

    public VolumeUtils() {
        try {
            if (mAudioManager == null) {
                mAudioManager = (AudioManager) Utils.getApp().getSystemService(Context.AUDIO_SERVICE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e("VolumeUtils", "AudioManager init error");
        }

        try {
            if (carAudioManager == null) {
                Car car = Car.createCar(ContextUtils.getAppContext());
                carAudioManager = (CarAudioManager) car.getCarManager(Car.AUDIO_SERVICE);
                carAudioManager.registerCarAudioFocusChangeCallback(new CarAudioManager.CarAudioFocusChangeCallback() {
                    @Override
                    public void onFocusChanged(int zoneId, int changed, int usage) {
                        super.onFocusChanged(zoneId, changed, usage);
                        LogUtils.d("VolumeUtils", "onFocusChanged: zoneId :" + zoneId + "--changed :" + changed + "--usage:" + usage);
                        if (usage == AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE) {
                            /**
                             * 丢失焦点
                             * AudioManager.AUDIOFOCUS_LOSS = -1 丢失音频焦点
                             * AudioManager.AUDIOFOCUS_LOSS_TRANSIENT = -2 短暂丢失音频焦点
                             * AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK = -3 短暂丢失焦点并允许降低音量
                             *
                             * 获取焦点
                             * AudioManager.AUDIOFOCUS_GAIN = 1 申请一个永久的音频焦点，并且希望上一个持有音频焦点的App停止播放
                             * AudioManager.AUDIOFOCUS_GAIN_TRANSIENT = 2 申请一个短暂的音频焦点，并且马上就会被释放，此时希望上一个持有音频焦点的App暂停播放
                             * AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK = 3 申请一个短暂的音频焦点，并且会希望系统不要播放任何突然的声音
                             */
                            //changed小于0代表失去焦点，大于0代表获取焦点
                            if (changed < AudioManager.AUDIOFOCUS_NONE) { //导航丢失焦点
                                isNaviSpeakState = AudioManager.AUDIOFOCUS_LOSS;
                            } else if (changed > AudioManager.AUDIOFOCUS_NONE) { //导航申请焦点
                                isNaviSpeakState = AudioManager.AUDIOFOCUS_GAIN;
                            }
                        }
                    }

                    @Override
                    public void onFocusGranted(int zoneId, int usage) {
                        super.onFocusGranted(zoneId, usage);
                        LogUtils.d("VolumeUtils", "onFocusGranted: zoneId :" + zoneId + "--usage:" + usage);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e("VolumeUtils", "CarAudioManager init error");
        }
        registerVolume();
    }

    private void registerVolume() {
        Utils.getApp().registerReceiver(broadcastReceiver, new IntentFilter(VOLUME_CHANGED_ACTION));
    }

    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int volType = intent.getIntExtra(EXTRA_VOLUME_STREAM_TYPE, -1);
            if (mAudioManager == null) {
                LogUtils.d("VolumeUtils", "Broadcast: mAudioManager == null");
                return;
            }
            int value = 0;
            if (volType == STREAM_BLUETOOTH) {
                value = mAudioManager.getStreamVolume(STREAM_BLUETOOTH);
                if (value != 0) {
                    mLastBtHeadsetVol = value;
                }
                LogUtils.d("VolumeUtils", "Broadcast: Volume type -> " + volType + " value =" + value);
            }
        }
    };

    /**
     * 获取指定通道的最小音量
     */
    public int getVolumeMin(int streamType) {
        //通话最小音量 = 1
        if (streamType == STREAM_VOICE_CALL) {
            return VOLUME_VOL_MIN;
        }
        //其他最小音量 = 0
        return VOLUME_MIN;
    }

    /**
     * 获取当前type音量
     */
    public int getVolume(int streamType) {
        if (mAudioManager == null) {
            LogUtils.d("VolumeUtils", "getVolume -- mAudioManager = null");
            return 0;
        }
        int volume = mAudioManager.getStreamVolume(streamType);
        return volume;
    }

    /**
     * 设置指定type的音量
     */
    public void setVolume(int streamType, int volume) {
        if (mAudioManager == null) {
            LogUtils.d("VolumeUtils", "setVolume -- mAudioManager = null");
            return;
        }
        mAudioManager.setStreamVolume(streamType, volume, AudioManager.FLAG_SHOW_UI);
    }

    /**
     * 设置静音状态
     */
    public void setMuteSound(String moduleName, boolean mute) {
        if (moduleName.equals("navi")) {
            if (mute) {
                //是静音意图就直接静音
                setMuteSound(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE, true);
            } else {
                //取消静音就需要先处理当前音源是不是静音状态，是静音就需要取消静音，不是静音还需要判断是不是音量=0.是0就需要设置成15
                boolean navi = getMuteSound("navi");
                if (navi) {
                    setMuteSound(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE, false);
                } else {
                    int naviVolume = getVolume(STREAM_NVI);
                    if (naviVolume == 0) {
                        setVolume(STREAM_NVI, 15);
                    }
                }
            }
        } else if (moduleName.equals("system_sound") || moduleName.equals("system")) {
            setMuteSound(AudioAttributes.USAGE_NOTIFICATION, mute);
        } else if (moduleName.equals("voice")) {
            if (mute) {
                //是静音意图就直接静音
                setMuteSound(AudioAttributes.USAGE_ASSISTANT, true);
            } else {
                //取消静音就需要先处理当前音源是不是静音状态，是静音就需要取消静音，不是静音还需要判断是不是音量=0.是0就需要设置成15
                boolean voice = getMuteSound("voice");
                if (voice) {
                    setMuteSound(AudioAttributes.USAGE_ASSISTANT, false);
                } else {
                    int naviVolume = getVolume(STREAM_ASSISTANT);
                    if (naviVolume == 0) {
                        setVolume(STREAM_ASSISTANT, 15);
                    }
                }
            }
        } else if (moduleName.equals("media")) {
            if (mute) {
                //是静音意图就直接静音
                setMuteSound(AudioAttributes.USAGE_MEDIA, true);
            } else {
                //取消静音就需要先处理当前音源是不是静音状态，是静音就需要取消静音，不是静音还需要判断是不是音量=0.是0就需要设置成15
                boolean media = getMuteSound("media");
                if (media) {
                    setMuteSound(AudioAttributes.USAGE_MEDIA, false);
                } else {
                    int mediaVolume = getVolume(STREAM_MUSIC);
                    if (mediaVolume == 0) {
                        setVolume(STREAM_MUSIC, 15);
                    }
                }
            }
        } else if (moduleName.equals("phone")) {
            //通话不能静音，只能设置为1
            setVolume(STREAM_VOICE_CALL, 1);
        } else if (moduleName.equals("bluetooth_headset") || moduleName.equals("bluetooth")) {
            if (mute) {
                //静音
//                mLastBtHeadsetVol = getVolume(STREAM_BLUETOOTH);
                setVolume(STREAM_BLUETOOTH, 0);
            } else {
                int bluetoothVolume = getVolume(STREAM_BLUETOOTH);
                if (bluetoothVolume == 0) {
                    setVolume(STREAM_BLUETOOTH, 15);
                }
            }
        }
    }

    /**
     * 设置静音状态
     */
    public void setAllMuteSound(boolean mute, boolean isBtEarphonesOpened) {
        if (mute) {
            //静音处理
            setMuteSound(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE, mute);
            setMuteSound(AudioAttributes.USAGE_ASSISTANT, mute);
            if (isBtEarphonesOpened) {
                mLastBtHeadsetVol = getVolume(STREAM_BLUETOOTH);
                setVolume(STREAM_BLUETOOTH, 0);
            } else {
                setMuteSound(AudioAttributes.USAGE_MEDIA, mute);
            }
        } else {
            //取消静音处理，如果已经静音需要取消，如果音量=0，需要设置成15
            boolean navi = getMuteSound("navi");
            if (navi) {
                setMuteSound(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE, mute);
            } else {
                int naviVolume = getVolume(STREAM_NVI);
                if (naviVolume == 0) {
                    setVolume(STREAM_NVI, 15);
                }
            }

            boolean voice = getMuteSound("voice");
            if (voice) {
                setMuteSound(AudioAttributes.USAGE_ASSISTANT, mute);
            } else {
                int voiceVolume = getVolume(STREAM_ASSISTANT);
                if (voiceVolume == 0) {
                    setVolume(STREAM_ASSISTANT, 15);
                }
            }

            if (isBtEarphonesOpened) {
                int bluetoothvolume = getVolume(STREAM_BLUETOOTH);
                if (bluetoothvolume == 0) {
                    setVolume(STREAM_BLUETOOTH,15);
                }
            } else {
                boolean media = getMuteSound("media");
                if (media) {
                    setMuteSound(AudioAttributes.USAGE_MEDIA, mute);
                } else {
                    int musicVolume = getVolume(STREAM_MUSIC);
                    if (musicVolume == 0) {
                        setVolume(STREAM_MUSIC,15);
                    }
                }
            }
        }
    }

    /**
     * 设置静音状态
     */
    public void setMuteSound(int streamType, boolean mute) {
        if (carAudioManager == null) {
            LogUtils.d("VolumeUtils", "setMuteSound -- CarAudioManager = null");
            return;
        }
        carAudioManager.setVolumeGroupMute(0, getGroupIdForUsage(streamType), mute, AudioManager.FLAG_SHOW_UI);
    }

    /**
     * 获取静音状态
     */
    public boolean getMuteSound(String moduleName) {
        if (carAudioManager == null) {
            LogUtils.d("VolumeUtils", "getMuteSound -- CarAudioManager = null");
            return false;
        }
        boolean volumeGroupMuted = false;
        if (moduleName.equals("navi")) {
            volumeGroupMuted = carAudioManager.isVolumeGroupMuted(0, getGroupIdForUsage(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE));
        } else if (moduleName.equals("system_sound") || moduleName.equals("system")) {
            volumeGroupMuted = carAudioManager.isVolumeGroupMuted(0, getGroupIdForUsage(AudioAttributes.USAGE_NOTIFICATION));
        } else if (moduleName.equals("voice")) {
            volumeGroupMuted = carAudioManager.isVolumeGroupMuted(0, getGroupIdForUsage(AudioAttributes.USAGE_ASSISTANT));
        } else if (moduleName.equals("media")) {
            volumeGroupMuted = carAudioManager.isVolumeGroupMuted(0, getGroupIdForUsage(AudioAttributes.USAGE_MEDIA));
        } else if (moduleName.equals("bluetooth_headset") || moduleName.equals("bluetooth")) {
            //蓝牙耳机音量 = 0 代表是静音
            int volume = getVolume(STREAM_BLUETOOTH);
            volumeGroupMuted = volume < 1;
        }
        return volumeGroupMuted;
    }

    /**
     * 是否所有音源都静音（20241105 音量 = 0 也算静音）
     */
    public boolean isAllMuteSound(boolean isBtEarphonesOpened,boolean isMute) {
        if (carAudioManager == null) {
            LogUtils.d("VolumeUtils", "getAllMuteSound -- CarAudioManager = null");
            return false;
        }
        int volume1 = getVolume(STREAM_NVI);
        int volume2 = getVolume(STREAM_ASSISTANT);
        int volume3;
        boolean volumeGroupMuted1 = carAudioManager.isVolumeGroupMuted(0, getGroupIdForUsage(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE));
        boolean volumeGroupMuted2 = carAudioManager.isVolumeGroupMuted(0, getGroupIdForUsage(AudioAttributes.USAGE_ASSISTANT));
        boolean volumeGroupMuted3;
        //蓝牙耳机连接
        if (isBtEarphonesOpened) {
            volume3 = getVolume(STREAM_BLUETOOTH);
            volumeGroupMuted3 = volume3 < 1;
        } else {
            volume3 = getVolume(STREAM_MUSIC);
            volumeGroupMuted3 = carAudioManager.isVolumeGroupMuted(0, getGroupIdForUsage(AudioAttributes.USAGE_MEDIA));
        }

        if (isMute) {
            if (volumeGroupMuted1 && volumeGroupMuted2 && volumeGroupMuted3) {
                return true;
            } else {
                if (volume1 == 0 && volume2 == 0 && volume3 == 0) {
                    return true;
                }
                boolean isNaviMute = false;
                boolean isVoiceMute = false;
                boolean isMusicMute = false;
                if (!volumeGroupMuted1) {
                    isNaviMute = volume1 == 0;
                }
                if (!volumeGroupMuted2) {
                    isVoiceMute = volume2 == 0;
                }
                if (!volumeGroupMuted3) {
                    isMusicMute = volume3 == 0;
                }
                return isNaviMute && isVoiceMute && isMusicMute;
            }
        } else {
            if (volumeGroupMuted1 && volumeGroupMuted2 && volumeGroupMuted3) {
                return false;
            } else if(!volumeGroupMuted1 && !volumeGroupMuted2 && !volumeGroupMuted3) {
                if (volume1 == 0 || volume2 == 0 || volume3 == 0) {
                    return false;
                }
                return true;
            } else {
                if (volume1 == 0 && volume2 == 0 && volume3 == 0) {
                    return false;
                }
                boolean isNaviMute = false;
                boolean isVoiceMute = false;
                boolean isMusicMute = false;
                if (!volumeGroupMuted1) {
                    isNaviMute = volume1 == 0;
                }
                if (!volumeGroupMuted2) {
                    isVoiceMute = volume2 == 0;
                }
                if (!volumeGroupMuted3) {
                    isMusicMute = volume3 == 0;
                }
                return isNaviMute && isVoiceMute && isMusicMute;
            }
        }
    }

    private int getGroupIdForUsage(int usage) {
        return carAudioManager.getVolumeGroupIdForUsage(usage);
    }

    public List<String> getAudioFocusList() {
        if (carAudioManager == null) {
            LogUtils.d("VolumeUtils", "getAudioFocusList -- CarAudioManager = null");
            return null;
        }
        List<String> current = carAudioManager.getCurrentFocusPackages(0);
        List<String> packages = carAudioManager.getCurrentDuckFocusPackages(0);
        packages.addAll(current);
        return packages;
    }

    /**
     * 获取当前音频焦点的type
     */
    public int getStreamType() {
        if (carAudioManager == null) {
            LogUtils.d("VolumeUtils", "getStreamType -- CarAudioManager = null");
            return -1;
        }
        List<String> current = carAudioManager.getCurrentFocusPackages(0);
        List<String> packages = carAudioManager.getCurrentDuckFocusPackages(0);
        packages.addAll(current);
        int flag = 0;
        for (String pa : packages) {
            LogUtils.i("VolumeUtils", "getStreamType-" + pa);
            switch (pa) {
                case "com.tencent.wecarflow":
                case "com.tencent.tai.pal.platform.app":
                case "com.tencent.qqlive.audiobox":
                case "com.voyah.cockpit.media":
                case "com.voyah.cockpit.video":
                case "com.voyah.car.radio":
                case "com.thunder.carplay":
                case "com.edog.car":
                case "com.android.bluetooth":
                case "com.voyah.cockpit.voyahmusic":
                    flag = flag | 8;
                    break;
                case "com.baidu.mapautotest":
                case "com.mega.map":
                    flag = flag | 4;
                    break;
                case "com.voyah.ai.voice":
                    flag = flag | 2;
                    break;
            }
        }
        LogUtils.i("VolumeUtils", "flag-" + flag);
        int temp = 15;
        int x = flag & temp;
        if (x >= 8) {
            return STREAM_MUSIC;
        }
        if (x >= 4) {
            return STREAM_NVI;
        }
        if (x >= 2) {
            return STREAM_ASSISTANT;
        }
        return -1;
    }

    /**
     * 打开按键音(open = 1 , close = 0)
     */
    public void setKeyTone(int code) {
        Settings.System.putInt(
                Utils.getApp().getContentResolver(),
                Settings.System.SOUND_EFFECTS_ENABLED,
                code
        );
        Utils.getApp().getContentResolver().notifyChange(
                Settings.System.getUriFor(Settings.System.SOUND_EFFECTS_ENABLED),
                null
        );
    }

    /**
     * 获取按键音的状态(open = 1 , close = 0)
     */
    public int getKeyTone() {
        return Settings.System.getInt(
                Utils.getApp().getContentResolver(),
                Settings.System.SOUND_EFFECTS_ENABLED,
                1);
    }

    /**
     * 打开系统提示音
     */
    public void setSystemTone(boolean open) {
        MegaDataStorageConfig.putBoolean("key_system_sound_effects", open);
        if (open) {
            MediaPlayer mediaPlayer = new MediaPlayer();
            AudioAttributes.Builder builder = new AudioAttributes.Builder();
            builder.setUsage(AudioAttributes.USAGE_NOTIFICATION);
            AudioAttributes audioAttributes = builder.build();
            mediaPlayer.setAudioAttributes(audioAttributes);
        }
        MegaDataStorageConfig.getContentResolver().notifyChange(
                Settings.System.getUriFor("key_system_sound_effects"),
                null
        );
    }

    /**
     * 获取系统提示音
     */
    public boolean getSystemTone() {
        return MegaDataStorageConfig.getBoolean("key_system_sound_effects", true);
    }

}
