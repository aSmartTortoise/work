package com.voyah.ai.device.voyah.common.media.impl;

import android.os.UserHandle;

import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.media.MediaSourceManagerInterface;
import com.voice.sdk.device.media.VoyahMusicControlInterface;
import com.voyah.ai.basecar.helper.MegaDisplayHelper;
import com.voyah.ai.basecar.media.bean.MediaSource;
import com.voyah.ai.basecar.media.bean.UserHandleInfo;
import com.voyah.ai.basecar.media.utils.MediaAudioZoneUtils;
import com.voyah.ai.basecar.media.utils.MediaHelper;
import com.voyah.ai.basecar.media.vedio.BiliImpl;
import com.voyah.ai.basecar.media.vedio.IqyImpl;
import com.voyah.ai.basecar.media.vedio.MiguImpl;
import com.voyah.ai.basecar.media.vedio.TencentVideoImpl;
import com.voyah.ai.basecar.media.vedio.TiktokImpl;
import com.voyah.ai.basecar.media.vedio.UsbVideoImpl;
import com.voyah.mirror.MirrorServiceManager;

import org.apache.commons.lang3.StringUtils;

public class MediaSourceManagerImpl implements MediaSourceManagerInterface {
    private static final String TAG = MediaSourceManagerImpl.class.getSimpleName();

    private final VoyahMusicControlInterface voyahMusicControlInterface = DeviceHolder.INS().getDevices().getMediaCenter().getVoyahMusicControl();

    UserHandle userHandle;

    int displayId;

    private static MediaSourceManagerImpl mediaSourceManagerImpl;

    public static MediaSourceManagerInterface getInstance() {
        if (mediaSourceManagerImpl == null) {
            synchronized (MediaSourceManagerImpl.class) {
                if ( mediaSourceManagerImpl == null) {
                    mediaSourceManagerImpl = new MediaSourceManagerImpl();
                }
            }
        }
        return  mediaSourceManagerImpl;
    }

    public void initUserHandle(UserHandle userHandle,int displayId) {
        this.userHandle = userHandle;
        this.displayId = displayId;

        //TODO 洪锋 给VedioControl设置userHandle或displayid vedio
        voyahMusicControlInterface.initUserHandle(userHandle, displayId);
    }
    @Override
    public String getMeidaPlayingSource(String soundLocation) {
        if(isPlayingAllScreen(soundLocation)){
            return voyahMusicControlInterface.getSource();//获取的是当前正在播放的音源
            //TODO 若无媒体中心则添加 return VedioControl.getMediaPlayingByDisplayId(displayId);
        }
        return "";
    }

    @Override
    public boolean isPlayingAllScreen(String soundLocation) {
        if(MediaHelper.isPassengerScreen(soundLocation) && MediaAudioZoneUtils.getInstance().isConnectBtPassengerScreen()){
            if(voyahMusicControlInterface.isPlayingByUserHandle(UserHandleInfo.passenger_screen.getUserHandle())
                //TODO 若无媒体中心 则添加 ||viedo isplaying（byDisplaid）
            ){
                initUserHandle(UserHandleInfo.passenger_screen.getUserHandle(),UserHandleInfo.passenger_screen.getDisplayId());
                return true;
            }
            return false;
        }
        if(MediaHelper.isCeilScreen(soundLocation) && MediaAudioZoneUtils.getInstance().isConnectBtCeilScreen()){
            if (voyahMusicControlInterface.isPlayingByUserHandle(UserHandleInfo.ceil_screen.getUserHandle())) {
                initUserHandle(UserHandleInfo.ceil_screen.getUserHandle(), UserHandleInfo.ceil_screen.getDisplayId());
                return true;
            }
            return false;
        }

        if (voyahMusicControlInterface.isPlayingByUserHandle(UserHandleInfo.central_screen.getUserHandle())) {
            initUserHandle(UserHandleInfo.central_screen.getUserHandle(), UserHandleInfo.central_screen.getDisplayId());
            return true;
        } else if (!MediaAudioZoneUtils.getInstance().isConnectBtPassengerScreen() && voyahMusicControlInterface.isPlayingByUserHandle(UserHandleInfo.passenger_screen.getUserHandle())) {
            initUserHandle(UserHandleInfo.passenger_screen.getUserHandle(), UserHandleInfo.passenger_screen.getDisplayId());
            return true;
        } else if (!MediaAudioZoneUtils.getInstance().isConnectBtCeilScreen() && voyahMusicControlInterface.isPlayingByUserHandle(UserHandleInfo.ceil_screen.getUserHandle())) {
            initUserHandle(UserHandleInfo.ceil_screen.getUserHandle(), UserHandleInfo.ceil_screen.getDisplayId());
            return true;
        }
        return false;
    }

    @Override
    public String getMeidaFrontSource() {
        return "";

    }

    @Override
    public String getMeidaPlaypageSource() {
        return "";
    }


    public String getVideoMirrorSource() {
        String currentSource = null;
        String mirrorPackage = MirrorServiceManager.INSTANCE.getMirrorPackage();
        if (StringUtils.isNotBlank(mirrorPackage)) {
            if (TencentVideoImpl.APP_NAME.equals(mirrorPackage)) {
                currentSource = MediaSource.tencent_video.getName();
            } else if (IqyImpl.APP_NAME.equals(mirrorPackage)) {
                currentSource = MediaSource.iqy_video.getName();
            } else if (MiguImpl.APP_NAME.equals(mirrorPackage)) {
                currentSource = MediaSource.migu_video.getName();
            } else if (BiliImpl.APP_NAME.equals(mirrorPackage)) {
                currentSource = MediaSource.bili_video.getName();
            } else if (TiktokImpl.APP_NAME.equals(mirrorPackage)) {
                currentSource = MediaSource.tiktok_video.getName();
            } else if (UsbVideoImpl.APP_NAME.equals(mirrorPackage)) {
                currentSource = MediaSource.usb_video.getName();
            }
            displayId = MirrorServiceManager.INSTANCE.getSourceScreen();
            initUserHandle(MegaDisplayHelper.getUserHandleByDisplayId(displayId),displayId);
            return currentSource;
        }
        return currentSource;
    }
}
