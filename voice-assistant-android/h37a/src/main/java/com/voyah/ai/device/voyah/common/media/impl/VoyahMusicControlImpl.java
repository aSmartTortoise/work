package com.voyah.ai.device.voyah.common.media.impl;

import android.content.ComponentName;
import android.content.Context;
import android.os.UserHandle;
import android.text.TextUtils;

import com.voice.sdk.constant.MediaConstant;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.carservice.constants.Domain;
import com.voice.sdk.device.carservice.constants.GearInfo;
import com.voice.sdk.device.carservice.signal.CommonSignal;
import com.voice.sdk.device.media.VoyahMusicControlInterface;
import com.voice.sdk.device.media.bean.MediaMusicInfo;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voyah.ai.basecar.helper.MegaDisplayHelper;
import com.voyah.ai.basecar.media.bean.Code;
import com.voyah.ai.basecar.media.bean.MediaSource;
import com.voice.sdk.device.media.bean.ObserverResponse;
import com.voyah.ai.basecar.media.bean.PageId;
import com.voyah.ai.basecar.media.bean.PlayMode;
import com.voice.sdk.device.media.bean.VoiceMusicSongInfo;
import com.voyah.ai.basecar.media.bean.ServiceConfigure;
import com.voyah.ai.basecar.media.bean.UserHandleInfo;
import com.voyah.ai.basecar.media.utils.MediaHelper;
import com.voyah.ai.common.utils.GpsUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.ai.common.utils.NumberUtils;
import com.voyah.ai.common.utils.SPUtil;
import com.voyah.ipcsdk.engine.IPCClientEngine;
import com.voyah.ipcsdk.model.RemoteConfig;
import com.voyah.ipcsdk.model.SyncResponse;
import com.voyah.media.common.model.mediamanager.MediaInfo;
import com.voyah.media.common.model.mediamanager.PlayModeInfo;
import com.voyah.media.common.model.mediamanager.ProgressInfo;
import com.voyah.media.common.model.mediamanager.SpeedModeInfo;
import com.voyah.media.common.model.voice.QQVoiceRequest;
import com.voyah.media.common.model.voice.UIVoiceRequest;
import com.voyah.media.common.model.voice.VoicePlayRequest;
import com.voyah.media.common.model.voice.VoiceSearchSongInfo;
import com.voyah.media.common.model.voice.VoiceSongInfo;
import com.voyah.media.common.model.voice.WyVoiceRequest;
import com.voyah.media.common.model.voice.XmlyVoiceRequest;
import com.voyah.media.common.model.voice.YtVoiceRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum VoyahMusicControlImpl implements VoyahMusicControlInterface {
    INSTANCE;

    private static final String TAG = VoyahMusicControlImpl.class.getSimpleName();
    private Context context;
    private MusicApi musicApi;

    private UserHandle userHandle;
    private int displayId;

    @Override
    public void init(Context context){
        this.context = context;

        userHandle = UserHandleInfo.central_screen.getUserHandle();
        displayId = UserHandleInfo.central_screen.getDisplayId();

        List<RemoteConfig> configs = new ArrayList<>();
        // 播控等公共功能
        ComponentName mediaManagerName = new ComponentName(MediaConstant.APP_NAME, ServiceConfigure.MEDIAMANAGER.pkgName() + ".service." + ServiceConfigure.MEDIAMANAGER.serviceName());
        configs.add(new RemoteConfig(ServiceConfigure.MEDIAMANAGER.sourceType(), mediaManagerName, ServiceConfigure.MEDIAMANAGER.pkgName() + "." + ServiceConfigure.MEDIAMANAGER.serviceName(), userHandle));
        // UI
        ComponentName uiName = new ComponentName(MediaConstant.APP_NAME, ServiceConfigure.UI.pkgName() + ".service." + ServiceConfigure.UI.serviceName());
        configs.add(new RemoteConfig(ServiceConfigure.UI.sourceType(), uiName, ServiceConfigure.UI.pkgName() + "." + ServiceConfigure.UI.serviceName(), userHandle));

        // 云听音乐
        ComponentName ytMusicName = new ComponentName(MediaConstant.APP_NAME, ServiceConfigure.YTMUSIC.pkgName() + ".service." + ServiceConfigure.YTMUSIC.serviceName());
        configs.add(new RemoteConfig(ServiceConfigure.YTMUSIC.sourceType(), ytMusicName, ServiceConfigure.YTMUSIC.pkgName() + "." + ServiceConfigure.YTMUSIC.serviceName(), userHandle));

        // 喜马拉雅音乐
        ComponentName xmMusicName = new ComponentName(MediaConstant.APP_NAME, ServiceConfigure.XMLYMUSIC.pkgName() + ".service." + ServiceConfigure.XMLYMUSIC.serviceName());
        configs.add(new RemoteConfig(ServiceConfigure.XMLYMUSIC.sourceType(), xmMusicName, ServiceConfigure.XMLYMUSIC.pkgName() + "." + ServiceConfigure.XMLYMUSIC.serviceName(), userHandle));

        // QQ音乐
        ComponentName qqMusicName = new ComponentName(MediaConstant.APP_NAME, ServiceConfigure.QQMUSIC.pkgName() + ".service." + ServiceConfigure.QQMUSIC.serviceName());
        configs.add(new RemoteConfig(ServiceConfigure.QQMUSIC.sourceType(), qqMusicName, ServiceConfigure.QQMUSIC.pkgName() + "." + ServiceConfigure.QQMUSIC.serviceName(), userHandle));

        // 网易云音乐
        ComponentName wyMusicName = new ComponentName(MediaConstant.APP_NAME, ServiceConfigure.WYMUSIC.pkgName() + ".service." + ServiceConfigure.WYMUSIC.serviceName());
        configs.add(new RemoteConfig(ServiceConfigure.WYMUSIC.sourceType(), wyMusicName, ServiceConfigure.WYMUSIC.pkgName() + "." + ServiceConfigure.WYMUSIC.serviceName(), userHandle));

        // 蓝牙
        ComponentName btMusicName = new ComponentName(MediaConstant.APP_NAME, ServiceConfigure.BTMUSIC.pkgName() + ".service." + ServiceConfigure.BTMUSIC.serviceName());
        configs.add(new RemoteConfig(ServiceConfigure.BTMUSIC.sourceType(), btMusicName, ServiceConfigure.BTMUSIC.pkgName() + "." + ServiceConfigure.BTMUSIC.serviceName(), userHandle));

        // USB
        ComponentName usbMusicName = new ComponentName(MediaConstant.APP_NAME, ServiceConfigure.USBMUSIC.pkgName() + ".service." + ServiceConfigure.USBMUSIC.serviceName());
        configs.add(new RemoteConfig(ServiceConfigure.USBMUSIC.sourceType(), usbMusicName, ServiceConfigure.USBMUSIC.pkgName() + "." + ServiceConfigure.USBMUSIC.serviceName(), userHandle));

        IPCClientEngine.get().setServiceConnectedListener(new IPCClientEngine.ServiceConnectListener() {
            @Override
            public void onServiceConnected(RemoteConfig remoteConfig) {
                LogUtils.d(TAG, "onServiceConnected: " + remoteConfig.toString());
            }

            @Override
            public void onServiceDisConnected(RemoteConfig remoteConfig) {
                LogUtils.d(TAG, "onServiceDisConnected: " + remoteConfig.toString());
            }
        }).init("voice", context, configs);

        musicApi = IPCClientEngine.get().getApi(MusicApi.class);

        registerPlayStateChanged();
    }

    @Override
    public void destroy(Context context) {

    }

    @Override
    public void initUserHandle(UserHandle userHandle, int displayId) {
    }

    /***************媒体功能功能接口实现*****************/
    public int switchPage(boolean isOpen,int pageId){
        if(displayId == UserHandleInfo.user_null.getDisplayId()){
            return Code.FAILED.code();
        }
        if(isOpen){
            int drvStatus = DeviceHolder.INS().getDevices().getCarService().getOperatorDispatcher()
                    .getOperatorByDomain(Domain.SYS_CTRL.getDomain()).getIntProp(CommonSignal.COMMON_GEAR_INFO);
            LogUtils.i(TAG,"switchPage gear:"+drvStatus+";displayId:"+displayId);
            closeScreenCentral(displayId);
            if(drvStatus == GearInfo.CARSET_GEAR_REVERSE && displayId == MegaDisplayHelper.getMainScreenDisplayId()){
                return Code.RGEAR.code();
            }
        }
        UIVoiceRequest request = new UIVoiceRequest();
        if (pageId == PageId.QQ_play_page.getId()) {
            request.setPageId(PageId.QQ_main.getId());
        } else if (pageId == PageId.wy_play_page.getId()) {
            request.setPageId(PageId.wy_main.getId());
        } else if (pageId == PageId.xm_play_page.getId()) {
            request.setPageId(PageId.xm_main.getId());
        } else if (pageId == PageId.yt_play_page.getId()) {
            request.setPageId(PageId.yt_main.getId());
        } else if (pageId == PageId.bt_play_page.getId()) {
            request.setPageId(PageId.bt_main.getId());
        } else if (pageId == PageId.usb_play_page.getId()) {
            request.setPageId(PageId.usb_main.getId());
        } else {
            request.setPageId(pageId);
        }
        SyncResponse<Object> command =
                isOpen
                        ? musicApi.openPage(MediaConstant.TYPE_UI, request,userHandle)
                        : musicApi.closePage(MediaConstant.TYPE_UI, request,userHandle);
        LogUtils.d(TAG, "switchPage command: "+command+";user"+userHandle+";"+displayId);
        if (command == null) {
            return Code.FAILED.code();
        }
        return command.getCode();
    }

    public void closeScreenCentral(int displayId){
        DeviceScreenType assignScreen = DeviceScreenType.CENTRAL_SCREEN;
        if(displayId == MegaDisplayHelper.getPassengerScreenDisplayId()){
            assignScreen = DeviceScreenType.PASSENGER_SCREEN;
        } else if(displayId == MegaDisplayHelper.getCeilingScreenDisplayId()){
            assignScreen = DeviceScreenType.CEIL_SCREEN;
        }
        DeviceHolder.INS().getDevices().getLauncher().closeScreenCentral(assignScreen, "");
    }

    @Override
    public boolean isPlaying() {
        boolean ret = false;
        SyncResponse<Integer> command = musicApi.getPlayState(MediaConstant.SOURCE_TYPE, userHandle);
        if (command != null && command.getCode() == 0) {
            ret = command.getData() == 1 || command.getData() == 2;
        }
        LogUtils.d(TAG, "isPlaying: "+ret+" ;command :"+ command);
        return ret;
    }

    @Override
    public boolean isPlayingByUserHandle(UserHandle userhandle) {
        boolean ret = false;
        SyncResponse<Integer> command = musicApi.getPlayState(MediaConstant.SOURCE_TYPE, userhandle);
        if (command != null && command.getCode() == 0) {
            ret = command.getData() == 1 || command.getData() == 2;
        }
        LogUtils.d(TAG, "isPlaying: "+ret+" ;userhandle :"+ userhandle);
        return ret;
    }


    @Override
    public String getSource() {
        SyncResponse<String> source = musicApi.getSource(MediaConstant.SOURCE_TYPE, userHandle);
        LogUtils.d(TAG, "source command: " + source);
        if(source != null && source.getCode() == 0){
            return source.getData();
        }
        return null;
    }

    /**
     * @return显示在前台的音源
     */
    @Override
    public String getMediaUiResource(UserHandle user){
        SyncResponse<String> uiSourceType = musicApi.getUiSourceType(MediaConstant.TYPE_UI, userHandle);
        LogUtils.i(TAG,"uiSourceType command:"+uiSourceType);
        if(uiSourceType != null && uiSourceType.getCode() == 0){
            return uiSourceType.getData();
        }
        return null;
    }

    @Override
    public int pre(String source) {
        SyncResponse<Object> command = musicApi.prev(MediaConstant.SOURCE_TYPE, source,userHandle);
        LogUtils.d(TAG, "pre command: " + command);
        if (command != null) {
            return command.getCode();
        }
        return Code.FAILED.code();
    }

    @Override
    public int next(String source) {
        SyncResponse<Object> command = musicApi.next(MediaConstant.SOURCE_TYPE,source, userHandle);
        LogUtils.d(TAG, "next command: " + command);
        if (command != null) {
            return command.getCode();
        }
        return Code.FAILED.code();
    }

    @Override
    public int play(String source) {
        if (isPlaying() && (TextUtils.equals(source, getSource()) || TextUtils.isEmpty(source))) {
            return Code.PLAYING.code();
        }
        SyncResponse<Object> command = musicApi.playSource(MediaConstant.SOURCE_TYPE,source, userHandle);
        LogUtils.d(TAG, "play command: " + command);
        if (command != null) {
            return command.getCode();
        }
        return Code.FAILED.code();
    }

    @Override
    public int replay(String source) {
        ProgressInfo progressInfo = new ProgressInfo(source,0L);
        SyncResponse<Object> command = musicApi.seekTo(MediaConstant.SOURCE_TYPE, progressInfo, userHandle);
        LogUtils.d(TAG, "replay command: " + command);
        if (command != null) {
            return command.getCode();
        }
        return Code.FAILED.code();
    }

    @Override
    public int stop(String source) {
        LogUtils.d(TAG, "stop");
        SyncResponse<Object> command = musicApi.pause(MediaConstant.SOURCE_TYPE,source,userHandle);
        LogUtils.d(TAG, "stop command: " + command);
        if (command != null) {
            return command.getCode();
        }
        return Code.FAILED.code();
    }

    @Override
    public long getProgress(String source){
        SyncResponse<Long> mediaProgress= musicApi.getProgress(MediaConstant.SOURCE_TYPE,source, userHandle);
        LogUtils.i(TAG,"mediaProgress command: "+mediaProgress);
        if(mediaProgress == null){
            return Code.FAILED.code();
        }
        return mediaProgress.getData();
    }

    @Override
    public MediaMusicInfo getMediaInfo(String source){
        SyncResponse<MediaInfo> mediaInfo = musicApi.getMediainfo(MediaConstant.SOURCE_TYPE, source,userHandle);
        LogUtils.d(TAG, "mediaInfo command: " + mediaInfo);
        if (mediaInfo != null) {
            if (mediaInfo.getCode() == Code.SUCESS.code()) {
                long time = mediaInfo.getData().getDuration();
                MediaMusicInfo media = new MediaMusicInfo();
                media.setMediaId(mediaInfo.getData().getMediaId());
                media.setDuration(time);
                media.setMediaName(mediaInfo.getData().getMediaName());
                media.setSingerName(MediaHelper.getArtist(mediaInfo.getData().getSingerName()));
                return media;
            }
        }
        return null;
    }

    @Override
    public int setSeekTo(long progress, String source){
        ProgressInfo progressInfo = new ProgressInfo(source,progress);
        SyncResponse<Object> command = musicApi.seekTo(MediaConstant.SOURCE_TYPE, progressInfo,userHandle);
        LogUtils.d(TAG, "forward command: " + command);
        if (command != null) {
            return command.getCode();
        }
        return Code.FAILED.code();
    }

    @Override
    public int getPlayMode(String source){
        SyncResponse<Integer> commandGet = musicApi.getPlayMode(MediaConstant.SOURCE_TYPE, source,userHandle);
        LogUtils.d(TAG, "commandGet: " + commandGet);
        if (commandGet != null) {
            if (commandGet.getCode() == Code.SUCESS.code()) {
                return commandGet.getData();
            }
        }
        return Code.FAILED.code();
    }

    @Override
    public int setPlayMode(int playMode,String source){
        PlayModeInfo playModeInfo = new PlayModeInfo();
        playModeInfo.setSourceType(source);
        playModeInfo.setPlayMode(playMode);
        SyncResponse<Object> commandSet = musicApi.setPlayMode(MediaConstant.SOURCE_TYPE, playModeInfo, userHandle);
        LogUtils.d(TAG, "commandSet: " + commandSet);
        if (commandSet != null) {
            return commandSet.getCode();
        }
        return Code.FAILED.code();
    }

    @Override
    public int getPageState(int pageId) {
        UIVoiceRequest request = new UIVoiceRequest();
        request.setPageId(pageId);
        SyncResponse<Integer> command = musicApi.getPageState(MediaConstant.TYPE_UI, request, userHandle);
        LogUtils.d(TAG, "getPageState command: " + command);
        if (command != null) {
            if (command.getCode() == Code.SUCESS.code()) {
                return command.getData();
            }
        }
        return Code.FAILED.code();
    }

    public Float getSpeed(){
        SyncResponse<Float> getSpeed = musicApi.getSpeedMode(MediaConstant.SOURCE_TYPE, userHandle);
        LogUtils.d(TAG, "getSpeed command: " + getSpeed);
        if (getSpeed != null){
            if(getSpeed.getCode() == Code.SUCESS.code()){
                return getSpeed.getData();
            }
        }
        return -1f;
    }

    public void setSpeed(Float speed){
        SpeedModeInfo speedModeInfo = new SpeedModeInfo();
        speedModeInfo.setSpeedMode(speed);
        musicApi.setSpeedMode(MediaConstant.SOURCE_TYPE, speedModeInfo, userHandle);
    }

    public boolean isVoyahMusicFront() {
        if(displayId == UserHandleInfo.user_null.getDisplayId()){
            return false;
        }
        boolean isFront = DeviceHolder.INS().getDevices().getSystem().getApp().isAppForeGround(MediaConstant.APP_NAME, displayId);;
        LogUtils.d(TAG, "VoyahMusic isFront: " + isFront);
        return isFront;
    }

    public void registerPlayStateChanged(){
        musicApi.onPlayStateChanged(MediaConstant.SOURCE_TYPE, UserHandleInfo.central_screen.getUserHandle()).subscribe(notify -> {
            if (MediaSource.wy_music.getName().equals(notify.getSourceType())) {
                if (notify.getPlayStatus() == 2) {
                    LogUtils.d(TAG, "central_screen wy is playing");
                    SPUtil.putBoolean(context, MediaConstant.IS_WY_PLAY, true);
                }
            } else if (MediaSource.qq_music.getName().equals(notify.getSourceType())) {
                if (notify.getPlayStatus() == 2) {
                    LogUtils.d(TAG, "central_screen qq is playing");
                    SPUtil.putBoolean(context, MediaConstant.IS_WY_PLAY, false);
                }
            }
        });

    }


    /***************网易功能功能接口实现*****************/
    public boolean isLoginWy(){
        SyncResponse<Boolean> command = musicApi.wyIsLogin(MediaConstant.TYPE_WY, userHandle);
        LogUtils.d(TAG, "wy isLogin command: " + command);
        if (command != null) {
            if (command.getCode() == Code.SUCESS.code()) {
                boolean isLogin = command.getData();
                LogUtils.d(TAG, "wy isLogin: " + isLogin);
                if (!isLogin) {
                    switchPage(true, PageId.wy_login.getId());
                }
                return isLogin;
            }
        }
        return false;
    }

    public void wyCollectState(ObserverResponse<Boolean> onResult){
        musicApi.wyCollectState(MediaConstant.TYPE_WY, userHandle).subscribe(new IpcObserverResponse<Boolean>() {
            @Override
            public void onSuccess(Boolean response) {
                LogUtils.d(TAG, "wyCollectState onSuccess: " + response);
                onResult.onSuccess(response);
            }
            @Override
            public void onFailed(int code, String msg) {
                super.onFailed(code, msg);
                LogUtils.d(TAG, "onFailed code: " + code + " msg: " + msg);
                onResult.onFailed(Code.FAILED.code(), Code.FAILED.msg());
            }
        });
    }

    public void wyCollect(boolean isCollect,ObserverResponse<Boolean> onResult){
        WyVoiceRequest request = new WyVoiceRequest();
        request.setCollect(isCollect);
        musicApi.wyCollect(MediaConstant.TYPE_WY, request, userHandle).subscribe(new IpcObserverResponse<Object>() {
            @Override
            public void onSuccess(Object response) {
                LogUtils.d(TAG, "onSuccess response: " + response);
                onResult.onSuccess(true);
            }

            @Override
            public void onFailed(int code, String msg) {
                super.onFailed(code, msg);
                LogUtils.d(TAG, "onFailed code: " + code + " msg: " + msg);
                onResult.onSuccess(false);
            }
        });
    }

    public void playCollectWy(ObserverResponse<VoiceMusicSongInfo> onResult){
        WyVoiceRequest request = new WyVoiceRequest();
        request.setCount(MediaConstant.SEARCH_COUNT);
        musicApi.wyCollectList(MediaConstant.TYPE_WY, request, userHandle).subscribe(new IpcObserverResponse<List<VoiceSongInfo>>() {
            @Override
            public void onSuccess(List<VoiceSongInfo> response) {
                List<VoiceMusicSongInfo> voiceMusicSongInfoList = updateMusicList(response,MediaSource.wy_music.getName());
                if (voiceMusicSongInfoList  != null && !voiceMusicSongInfoList .isEmpty()) {
                    playSongsByMediaId(voiceMusicSongInfoList , voiceMusicSongInfoList.size(),0,false, MediaSource.wy_music.getName());
                    onResult.onSuccess(voiceMusicSongInfoList.get(0));
                } else {
                    onResult.onSuccess(null);
                }
            }
            @Override
            public void onFailed(int code, String msg) {
                super.onFailed(code, msg);
                LogUtils.d(TAG, "onFailed code: " + code + " msg: " + msg);
                onResult.onFailed(code,msg);
            }
        });
    }

    public void playHistoryWy(ObserverResponse<VoiceMusicSongInfo> onResult){
        WyVoiceRequest request = new WyVoiceRequest();
        request.setCount(MediaConstant.SEARCH_COUNT);
        musicApi.wyHistory(MediaConstant.TYPE_WY, request, userHandle).subscribe(new IpcObserverResponse<List<VoiceSongInfo>>() {
            @Override
            public void onSuccess(List<VoiceSongInfo> response) {
                List<VoiceMusicSongInfo> voiceMusicSongInfoList = updateMusicList(response,MediaSource.wy_music.getName());
                if (voiceMusicSongInfoList  != null && !voiceMusicSongInfoList .isEmpty()) {
                    playSongsByMediaId(voiceMusicSongInfoList , voiceMusicSongInfoList.size(),0,false, MediaSource.wy_music.getName());
                    onResult.onSuccess(voiceMusicSongInfoList.get(0));
                } else {
                    onResult.onSuccess(null);
                }
            }
            @Override
            public void onFailed(int code, String msg) {
                super.onFailed(code, msg);
                LogUtils.d(TAG, "onFailed code: " + code + " msg: " + msg);
                onResult.onFailed(code,msg);
            }
        });
    }

    public void playRecommendWy(ObserverResponse<VoiceMusicSongInfo> onResult){
        WyVoiceRequest request = new WyVoiceRequest();
        request.setCount(MediaConstant.SEARCH_COUNT);
        musicApi.wyRecommend(MediaConstant.TYPE_WY, request, userHandle).subscribe(new IpcObserverResponse<List<VoiceSongInfo>>() {
            @Override
            public void onSuccess(List<VoiceSongInfo> response) {
                List<VoiceMusicSongInfo> voiceMusicSongInfoList = updateMusicList(response,MediaSource.wy_music.getName());
                if (voiceMusicSongInfoList  != null && !voiceMusicSongInfoList .isEmpty()) {
                    playSongsByMediaId(voiceMusicSongInfoList , voiceMusicSongInfoList.size(),0,false, MediaSource.wy_music.getName());
                    onResult.onSuccess(voiceMusicSongInfoList.get(0));
                } else {
                    onResult.onSuccess(null);
                }
            }
            @Override
            public void onFailed(int code, String msg) {
                super.onFailed(code, msg);
                LogUtils.d(TAG, "onFailed code: " + code + " msg: " + msg);
                onResult.onFailed(code,msg);
            }
        });
    }

    public void playSearchWy(String mediaType, String mediaTypeDetail, String appName, String mediaSource, String mediaName, String mediaArtist, String mediaAlbum, String mediaDate, String mediaMovie, String mediaStyle, String mediaLan, String mediaVersion, String mediaOffset, String mediaUi, String mediaRank, String playMode,ObserverResponse<VoiceMusicSongInfo> onResult){
        WyVoiceRequest request = new WyVoiceRequest();
        request.setCount(MediaConstant.SEARCH_COUNT);
        if (NumberUtils.areAllStringsEmpty(mediaArtist, mediaAlbum, mediaDate, mediaMovie, mediaStyle, mediaLan, mediaVersion, mediaRank)) {
            request.setMediaType(1);
            request.setSongName(mediaName);
        } else if (NumberUtils.areAllStringsEmpty(mediaName, mediaAlbum, mediaDate, mediaMovie, mediaStyle, mediaLan, mediaVersion, mediaRank)) {
            if (mediaArtist.contains(",")) {
                String[] artists = mediaArtist.split(",");
                request.setSongName(artists[0]);
                request.setArtistName(artists[1]);
            } else {
                request.setSongName(mediaArtist);
            }
            request.setMediaType(3);
        } else if (NumberUtils.areAllStringsEmpty(mediaName, mediaArtist, mediaDate, mediaMovie, mediaStyle, mediaLan, mediaVersion, mediaRank)) {
            request.setMediaType(2);
            request.setSongName(mediaAlbum);
        } else {
            if (!TextUtils.isEmpty(mediaArtist) && !TextUtils.isEmpty(mediaName)) {
                if (mediaArtist.contains(",")) {
                    request.setMediaType(1);
                    request.setSongName(mediaArtist.replaceAll(",", "") + mediaName);
                } else {
                    request.setMediaType(1);
                    request.setSongName(mediaName);
                    request.setArtistName(mediaArtist);
                }
            } else if (!TextUtils.isEmpty(mediaArtist) && !TextUtils.isEmpty(mediaAlbum)) {
                request.setMediaType(2);
                request.setSongName(mediaAlbum);
                request.setArtistName(mediaArtist);
            } else if(!TextUtils.isEmpty(mediaRank)){
                request.setMediaType(7);
                request.setSongName(mediaRank);
            } else {
                if (!TextUtils.isEmpty(mediaArtist) && mediaArtist.contains(",") && !TextUtils.isEmpty(mediaVersion) && mediaVersion.contains("合唱")) {
                    String[] artists = mediaArtist.split(",");
                    request.setMediaType(3);
                    request.setSongName(artists[0]);
                    request.setArtistName(artists[1]);
                } else {
                    StringBuilder sb = new StringBuilder();
                    String[] strings = new String[]{mediaName, mediaArtist, mediaAlbum, mediaDate, mediaMovie, mediaStyle, mediaLan, mediaVersion, mediaRank};
                    for (String str : strings) {
                        if (!TextUtils.isEmpty(str)) {
                            sb.append(str);
                        }
                    }
                    request.setSongName(sb.toString().replaceAll(",", "").trim());
                    request.setMediaType(0);
                }
            }
        }
        LogUtils.d(TAG, "request: " + request);
        musicApi.wySearchNew(MediaConstant.TYPE_WY, request, userHandle).subscribe(new IpcObserverResponse<VoiceSearchSongInfo>(){
            @Override
            public void onSuccess(VoiceSearchSongInfo response) {
                LogUtils.d(TAG,"wySearchNew response:"+response);
                if(response != null){
                    List<VoiceMusicSongInfo> voiceMusicSongInfoList = updateMusicList(response.getVoiceSongInfoList(),MediaSource.wy_music.getName());
                    if(voiceMusicSongInfoList != null && !voiceMusicSongInfoList.isEmpty()){
                        if (getPlayModeId(playMode) != Code.FAILED.code()) {
                            setPlayMode(getPlayModeId(playMode),MediaSource.wy_music.getName());
                        }
                        playSongsByMediaId(voiceMusicSongInfoList,TextUtils.isEmpty(mediaName) ? voiceMusicSongInfoList.size() : MediaConstant.ONE_COUNT,0,true, MediaSource.wy_music.getName());
                        if(response.getVoiceTips() != null){
                            if (response.getVoiceTips().getType() == Code.VOICE_SONG_CAN_NOT_PLAY.code()){
                                voiceMusicSongInfoList.get(0).setCode(Code.VOICE_SONG_CAN_NOT_PLAY.code());
                                onResult.onSuccess(voiceMusicSongInfoList.get(0));
                                return;
                            } else if (response.getVoiceTips().getType() == Code.VOICE_SONG_NOT_PLAY_VISIBLE.code()){
                                voiceMusicSongInfoList.get(0).setCode(Code.VOICE_SONG_NOT_PLAY_VISIBLE.code());
                                onResult.onSuccess(voiceMusicSongInfoList.get(0));
                                return;
                            } else if (response.getVoiceTips().getType() == Code.VOICE_SONG_NOT_FOUND.code()){
                                voiceMusicSongInfoList.get(0).setCode(Code.VOICE_SONG_NOT_FOUND.code());
                                onResult.onSuccess(voiceMusicSongInfoList.get(0));
                                return;
                            }
                        }
                        onResult.onSuccess(voiceMusicSongInfoList.get(0));
                    }  else {
                        onResult.onSuccess(null);
                    }
                }
            }
            @Override
            public void onFailed(int code, String msg) {
                super.onFailed(code, msg);
                LogUtils.d(TAG, "onFailed code: " + code + " msg: " + msg);
                onResult.onFailed(code,msg);
            }
        });
    }

    /***************QQ音乐功能功能接口实现*****************/
    public boolean isLoginQq(){
        SyncResponse<Boolean> command = musicApi.qqIsLogin(MediaConstant.TYPE_QQ, userHandle);
        LogUtils.d(TAG, "qq isLogin command: " + command);
        if (command != null) {
            if (command.getCode() == Code.SUCESS.code()) {
                boolean isLogin = command.getData();
                LogUtils.d(TAG, "qq isLogin: " + isLogin);
                if (!isLogin) {
                    switchPage(true, PageId.QQ_login.getId());
                }
                return isLogin;
            }
        }
        return false;
    }

    public void qqCollectState(ObserverResponse<Boolean> onResult){
        musicApi.qqCollectState(MediaConstant.TYPE_QQ, userHandle).subscribe(new IpcObserverResponse<Boolean>() {
            @Override
            public void onSuccess(Boolean response) {
                LogUtils.d(TAG, "qqCollectState onSuccess: " + response);
                onResult.onSuccess(response);
            }
            @Override
            public void onFailed(int code, String msg) {
                super.onFailed(code, msg);
                LogUtils.d(TAG, "onFailed code: " + code + " msg: " + msg);
                onResult.onFailed(Code.FAILED.code(), Code.FAILED.msg());
            }
        });
    }

    public void qqCollect(boolean isCollect,ObserverResponse<Boolean> onResult){
        QQVoiceRequest request = new QQVoiceRequest();
        request.setCollect(isCollect);
        musicApi.qqCollect(MediaConstant.TYPE_QQ, request, userHandle).subscribe(new IpcObserverResponse<Object>() {
            @Override
            public void onSuccess(Object response) {
                LogUtils.d(TAG, "onSuccess response: " + response);
                onResult.onSuccess(true);
            }

            @Override
            public void onFailed(int code, String msg) {
                super.onFailed(code, msg);
                LogUtils.d(TAG, "onFailed code: " + code + " msg: " + msg);
                onResult.onSuccess(false);
            }
        });
    }

    public void playCollectQq(ObserverResponse<VoiceMusicSongInfo> onResult){
        QQVoiceRequest request = new QQVoiceRequest();
        request.setCount(MediaConstant.SEARCH_COUNT);
        musicApi.qqCollectList(MediaConstant.TYPE_QQ, request, userHandle).subscribe(new IpcObserverResponse<List<VoiceSongInfo>>() {
            @Override
            public void onSuccess(List<VoiceSongInfo> response) {
                List<VoiceMusicSongInfo> voiceMusicSongInfoList = updateMusicList(response,MediaSource.qq_music.getName());
                if (voiceMusicSongInfoList  != null && !voiceMusicSongInfoList .isEmpty()) {
                    playSongsByMediaId(voiceMusicSongInfoList , voiceMusicSongInfoList.size(),0,false, MediaSource.qq_music.getName());
                    onResult.onSuccess(voiceMusicSongInfoList.get(0));
                } else {
                    onResult.onSuccess(null);
                }
            }
            @Override
            public void onFailed(int code, String msg) {
                super.onFailed(code, msg);
                LogUtils.d(TAG, "onFailed code: " + code + " msg: " + msg);
                onResult.onFailed(code,msg);
            }
        });
    }

    public void playHistoryQq(ObserverResponse<VoiceMusicSongInfo> onResult){
        QQVoiceRequest request = new QQVoiceRequest();
        request.setCount(MediaConstant.SEARCH_COUNT);
        musicApi.qqHistory(MediaConstant.TYPE_QQ, request, userHandle).subscribe(new IpcObserverResponse<List<VoiceSongInfo>>() {
            @Override
            public void onSuccess(List<VoiceSongInfo> response) {
                List<VoiceMusicSongInfo> voiceMusicSongInfoList = updateMusicList(response,MediaSource.qq_music.getName());
                if (voiceMusicSongInfoList  != null && !voiceMusicSongInfoList .isEmpty()) {
                    playSongsByMediaId(voiceMusicSongInfoList , voiceMusicSongInfoList.size(),0,false, MediaSource.qq_music.getName());
                    onResult.onSuccess(voiceMusicSongInfoList.get(0));
                } else {
                    onResult.onSuccess(null);
                }
            }
            @Override
            public void onFailed(int code, String msg) {
                super.onFailed(code, msg);
                LogUtils.d(TAG, "onFailed code: " + code + " msg: " + msg);
                onResult.onFailed(code,msg);
            }
        });
    }

    public void playRecommendQq(ObserverResponse<VoiceMusicSongInfo> onResult){
        QQVoiceRequest request = new QQVoiceRequest();
        request.setCount(MediaConstant.SEARCH_COUNT);
        musicApi.qqRecommend(MediaConstant.TYPE_QQ, request, userHandle).subscribe(new IpcObserverResponse<List<VoiceSongInfo>>() {
            @Override
            public void onSuccess(List<VoiceSongInfo> response) {
                List<VoiceMusicSongInfo> voiceMusicSongInfoList = updateMusicList(response,MediaSource.qq_music.getName());
                if (voiceMusicSongInfoList  != null && !voiceMusicSongInfoList .isEmpty()) {
                    playSongsByMediaId(voiceMusicSongInfoList , voiceMusicSongInfoList.size(),0,false, MediaSource.qq_music.getName());
                    onResult.onSuccess(voiceMusicSongInfoList.get(0));
                } else {
                    onResult.onSuccess(null);
                }
            }
            @Override
            public void onFailed(int code, String msg) {
                super.onFailed(code, msg);
                LogUtils.d(TAG, "onFailed code: " + code + " msg: " + msg);
                onResult.onFailed(code,msg);
            }
        });
    }

    public void playSearchQq(String mediaType, String mediaTypeDetail, String appName, String mediaSource, String mediaName, String mediaArtist, String mediaAlbum, String mediaDate, String mediaMovie, String mediaStyle, String mediaLan, String mediaVersion, String mediaOffset, String mediaUi, String mediaRank, String playMode,ObserverResponse<VoiceMusicSongInfo> onResult){
        QQVoiceRequest request = new QQVoiceRequest();
        request.setCount(MediaConstant.SEARCH_COUNT);
        request.setOriginQuestion("");
        request.setSlots(new HashMap<>());
        if (!TextUtils.isEmpty(mediaArtist) && mediaArtist.contains(",")) {
            request.getSlots().put("Singer", mediaArtist.replaceAll(",", " "));
        } else {
            request.getSlots().put("Singer", mediaArtist);
        }
        request.getSlots().put("TrackType", mediaStyle);
        request.getSlots().put("Track", mediaName);
        request.getSlots().put("Lang", mediaLan);
        request.getSlots().put("Album", mediaAlbum);
        request.getSlots().put("Movie", mediaMovie);
        request.getSlots().put("Toplist", mediaRank);
        if (!TextUtils.isEmpty(mediaVersion) && mediaVersion.contains("合唱")) {
            request.getSlots().put("Version", "");
        } else {
            request.getSlots().put("Version", mediaVersion);
        }
        request.getSlots().put("Period", mediaOffset);
        request.getSlots().put("Date", mediaDate);
        //            request.slots.put("Sort",mediaArtist);
        //            request.slots.put("Season",mediaArtist);
        //            request.slots.put("Tvshow",mediaArtist);
        LogUtils.d(TAG, "qqSearch request: " + request);
        musicApi.qqSearch(MediaConstant.TYPE_QQ, request, userHandle).subscribe(new IpcObserverResponse<List<VoiceSongInfo>>(){
            @Override
            public void onSuccess(List<VoiceSongInfo> response) {
                List<VoiceMusicSongInfo> voiceMusicSongInfoList = updateMusicList(response,MediaSource.qq_music.getName());
                if (voiceMusicSongInfoList  != null && !voiceMusicSongInfoList .isEmpty()) {
                    if (getPlayModeId(playMode) != Code.FAILED.code()) {
                        setPlayMode(getPlayModeId(playMode),MediaSource.qq_music.getName());
                    }
                    playSongsByMediaId(voiceMusicSongInfoList , TextUtils.isEmpty(mediaName) ? voiceMusicSongInfoList.size() : MediaConstant.ONE_COUNT, 0,true, MediaSource.qq_music.getName());
                    onResult.onSuccess(voiceMusicSongInfoList.get(0));
                } else {
                    onResult.onSuccess(null);
                }
            }
            @Override
            public void onFailed(int code, String msg) {
                super.onFailed(code, msg);
                LogUtils.d(TAG, "onFailed code: " + code + " msg: " + msg);
                onResult.onFailed(code,msg);
            }
        });
    }

    /***************云听功能功能接口实现*****************/
    public boolean isLoginYt(){
        SyncResponse<Boolean> command = musicApi.ytIsLogin(MediaConstant.TYPE_YT, userHandle);
        LogUtils.d(TAG, "yt isLogin command: " + command);
        if (command != null) {
            if (command.getCode() == Code.SUCESS.code()) {
                boolean isLogin = command.getData();
                LogUtils.d(TAG, "yt isLogin: " + isLogin);
                if (!isLogin) {
                    switchPage(true, PageId.yt_login.getId());
                }
                return isLogin;
            }
        }
        return false;
    }

    public void ytCollectState(ObserverResponse<Boolean> onResult){
        musicApi.ytCollectState(MediaConstant.TYPE_YT, userHandle).subscribe(new IpcObserverResponse<Boolean>() {
            @Override
            public void onSuccess(Boolean response) {
                LogUtils.d(TAG, "ytCollectState onSuccess: " + response);
                onResult.onSuccess(response);
            }
            @Override
            public void onFailed(int code, String msg) {
                super.onFailed(code, msg);
                LogUtils.d(TAG, "onFailed code: " + code + " msg: " + msg);
                onResult.onFailed(Code.FAILED.code(), Code.FAILED.msg());
            }
        });
    }

    public void ytCollect(boolean isCollect,ObserverResponse<Boolean> onResult){
        YtVoiceRequest request = new YtVoiceRequest();
        request.setCollect(isCollect);
        musicApi.ytCollect(MediaConstant.TYPE_YT, request, userHandle).subscribe(new IpcObserverResponse<Object>() {
            @Override
            public void onSuccess(Object response) {
                LogUtils.d(TAG, "onSuccess response: " + response);
                onResult.onSuccess(true);
            }

            @Override
            public void onFailed(int code, String msg) {
                super.onFailed(code, msg);
                LogUtils.d(TAG, "onFailed code: " + code + " msg: " + msg);
                onResult.onSuccess(false);
            }
        });
    }

    public void playCollectYt(ObserverResponse<VoiceMusicSongInfo> onResult){
        YtVoiceRequest request = new YtVoiceRequest();
        request.setCount(MediaConstant.SEARCH_COUNT);
        musicApi.ytCollectList(MediaConstant.TYPE_YT, request, userHandle).subscribe(new IpcObserverResponse<List<VoiceSongInfo>>() {
            @Override
            public void onSuccess(List<VoiceSongInfo> response) {
                List<VoiceMusicSongInfo> voiceMusicSongInfoList = updateMusicList(response,MediaSource.yt_music.getName());
                if (voiceMusicSongInfoList  != null && !voiceMusicSongInfoList .isEmpty()) {
                    playSongsByMediaId(voiceMusicSongInfoList , voiceMusicSongInfoList.size(),0,false, MediaSource.yt_music.getName());
                    onResult.onSuccess(voiceMusicSongInfoList.get(0));
                } else {
                    onResult.onSuccess(null);
                }
            }
            @Override
            public void onFailed(int code, String msg) {
                super.onFailed(code, msg);
                LogUtils.d(TAG, "onFailed code: " + code + " msg: " + msg);
                onResult.onFailed(code,msg);
            }
        });
    }

    public void playHistoryYt(ObserverResponse<VoiceMusicSongInfo> onResult){
        YtVoiceRequest request = new YtVoiceRequest();
        request.setCount(MediaConstant.SEARCH_COUNT);
        musicApi.ytHistory(MediaConstant.TYPE_YT, request, userHandle).subscribe(new IpcObserverResponse<List<VoiceSongInfo>>() {
            @Override
            public void onSuccess(List<VoiceSongInfo> response) {
                List<VoiceMusicSongInfo> voiceMusicSongInfoList = updateMusicList(response,MediaSource.yt_music.getName());
                if (voiceMusicSongInfoList  != null && !voiceMusicSongInfoList .isEmpty()) {
                    playSongsByMediaId(voiceMusicSongInfoList , voiceMusicSongInfoList.size(),0,false, MediaSource.yt_music.getName());
                    onResult.onSuccess(voiceMusicSongInfoList.get(0));
                } else {
                    onResult.onSuccess(null);
                }
            }
            @Override
            public void onFailed(int code, String msg) {
                super.onFailed(code, msg);
                LogUtils.d(TAG, "onFailed code: " + code + " msg: " + msg);
                onResult.onFailed(code,msg);
            }
        });
    }

    public void playSearchYt(String mediaType, String mediaTypeDetail, String appName, String mediaSource, String mediaName, String mediaArtist, String mediaAlbum, String mediaDate, String mediaMovie, String mediaStyle, String mediaLan, String mediaVersion, String mediaOffset, String mediaUi, String mediaRank, String playMode,ObserverResponse<VoiceMusicSongInfo> onResult){
        YtVoiceRequest request = new YtVoiceRequest();
        if ("am".equals(mediaTypeDetail) || "fm".equals(mediaTypeDetail)) {
            request.setCount(MediaConstant.SEARCH_COUNT);
            request.setField( 6);
            request.setFreq(mediaName.replaceAll("\\.", ""));
            request.setKeyword(mediaTypeDetail+mediaName);
        } else {
            StringBuilder sb = new StringBuilder();
            String[] strings = new String[]{mediaName, /*mediaTypeDetail,*/ mediaArtist, mediaAlbum, /*mediaDate,*/ mediaMovie, mediaStyle, mediaLan, mediaVersion, mediaRank};
            for (String str : strings) {
                if (!TextUtils.isEmpty(str)) {
                    sb.append(str).append(" ");
                }
            }
            String key = sb.toString().trim();
            if (TextUtils.isEmpty(key)) {
                key = "广播";
                if (GpsUtils.getCurrentLocation() != null) {
                    try {
                        JSONObject gpsJson = new JSONObject(GpsUtils.getCurrentLocation());
                        String city = gpsJson.optString("cityName");
                        if (!TextUtils.isEmpty(city)) {
                            key = city + key;
                        }
                    } catch (Exception ignore) {
                    }
                }
            }
            if("新闻".equals(mediaTypeDetail)){
                key = key + mediaTypeDetail;
            }
            request.setKeyword(key);
            request.setCount(MediaConstant.SEARCH_COUNT);
            if("radio".equals(mediaType)){
                if((TextUtils.isEmpty(mediaName) || "广播".equals(mediaTypeDetail)) && !"新闻".equals(mediaTypeDetail) ){
                    request.setField(6);
                } else {
                    request.setField(2);
                }
            } else {
                request.setField(2);
            }
            request.setText("");
        }
        LogUtils.d(TAG, "request: " + request);
        musicApi.ytSearch(MediaConstant.TYPE_YT, request, userHandle).subscribe(new IpcObserverResponse<List<VoiceSongInfo>>(){
            @Override
            public void onSuccess(List<VoiceSongInfo> response) {
                List<VoiceMusicSongInfo> voiceMusicSongInfoList = updateMusicList(response,MediaSource.yt_music.getName());
                if (voiceMusicSongInfoList  != null && !voiceMusicSongInfoList .isEmpty()) {
                    playSongsByMediaId(voiceMusicSongInfoList , voiceMusicSongInfoList.size(),0,false, MediaSource.yt_music.getName());
                    onResult.onSuccess(voiceMusicSongInfoList.get(0));
                } else {
                    onResult.onSuccess(null);
                }
            }
            @Override
            public void onFailed(int code, String msg) {
                super.onFailed(code, msg);
                LogUtils.d(TAG, "onFailed code: " + code + " msg: " + msg);
                onResult.onFailed(code,msg);
            }
        });
    }

    /***************喜马拉雅功能功能接口实现*****************/
    public boolean isLoginXm(){
        SyncResponse<Boolean> command = musicApi.xmIsLogin(MediaConstant.TYPE_XM, userHandle);
        LogUtils.d(TAG, "xm isLogin command: " + command);
        if (command != null) {
            if (command.getCode() == Code.SUCESS.code()) {
                boolean isLogin = command.getData();
                LogUtils.d(TAG, "xm isLogin: " + isLogin);
                if (!isLogin) {
                    switchPage(true, PageId.xm_login.getId());
                }
                return isLogin;
            }
        }
        return false;
    }

    public void xmCollectState(ObserverResponse<Boolean> onResult){
        musicApi.xmCollectState(MediaConstant.TYPE_XM, userHandle).subscribe(new IpcObserverResponse<Boolean>() {
            @Override
            public void onSuccess(Boolean response) {
                LogUtils.d(TAG, "xmCollectState onSuccess: " + response);
                onResult.onSuccess(response);
            }
            @Override
            public void onFailed(int code, String msg) {
                super.onFailed(code, msg);
                LogUtils.d(TAG, "onFailed code: " + code + " msg: " + msg);
                onResult.onFailed(code, msg);
            }
        });
    }

    public void xmCollect(boolean isCollect,ObserverResponse<Boolean> onResult){
        XmlyVoiceRequest request = new XmlyVoiceRequest();
        request.setCollect(isCollect);
        musicApi.xmCollect(MediaConstant.TYPE_XM, request, userHandle).subscribe(new IpcObserverResponse<Object>() {
            @Override
            public void onSuccess(Object response) {
                LogUtils.d(TAG, "onSuccess response: " + response);
                onResult.onSuccess(true);
            }

            @Override
            public void onFailed(int code, String msg) {
                super.onFailed(code, msg);
                LogUtils.d(TAG, "onFailed code: " + code + " msg: " + msg);
                onResult.onSuccess(false);
            }
        });
    }

    public void playCollectXm(ObserverResponse<VoiceMusicSongInfo> onResult){
        XmlyVoiceRequest request = new XmlyVoiceRequest();
        request.setCount(MediaConstant.SEARCH_COUNT);
        musicApi.xmCollectList(MediaConstant.TYPE_XM, request, userHandle).subscribe(new IpcObserverResponse<List<VoiceSongInfo>>() {
            @Override
            public void onSuccess(List<VoiceSongInfo> response) {
                List<VoiceMusicSongInfo> voiceMusicSongInfoList = updateMusicList(response,MediaSource.xmly_music.getName());
                if (voiceMusicSongInfoList  != null && !voiceMusicSongInfoList .isEmpty()) {
                    playSongsByMediaId(voiceMusicSongInfoList , voiceMusicSongInfoList.size(),0,false, MediaSource.xmly_music.getName());
                    onResult.onSuccess(voiceMusicSongInfoList.get(0));
                } else {
                    onResult.onSuccess(null);
                }
            }
            @Override
            public void onFailed(int code, String msg) {
                super.onFailed(code, msg);
                LogUtils.d(TAG, "onFailed code: " + code + " msg: " + msg);
                onResult.onFailed(code,msg);
            }
        });
    }

    public void playHistoryXm(ObserverResponse<VoiceMusicSongInfo> onResult){
        XmlyVoiceRequest request = new XmlyVoiceRequest();
        request.setCount(MediaConstant.SEARCH_COUNT);
        musicApi.xmHistory(MediaConstant.TYPE_XM, request, userHandle).subscribe(new IpcObserverResponse<List<VoiceSongInfo>>() {
            @Override
            public void onSuccess(List<VoiceSongInfo> response) {
                List<VoiceMusicSongInfo> voiceMusicSongInfoList = updateMusicList(response,MediaSource.xmly_music.getName());
                if (voiceMusicSongInfoList  != null && !voiceMusicSongInfoList .isEmpty()) {
                    playSongsByMediaId(voiceMusicSongInfoList , voiceMusicSongInfoList.size(),0,false, MediaSource.xmly_music.getName());
                    onResult.onSuccess(voiceMusicSongInfoList.get(0));
                } else {
                    onResult.onSuccess(null);
                }
            }
            @Override
            public void onFailed(int code, String msg) {
                super.onFailed(code, msg);
                LogUtils.d(TAG, "onFailed code: " + code + " msg: " + msg);
                onResult.onFailed(code,msg);
            }
        });
    }

    public void playSearchXm(String mediaType, String mediaTypeDetail, String appName, String mediaSource, String mediaName, String mediaArtist, String mediaAlbum, String mediaDate, String mediaMovie, String mediaStyle, String mediaLan, String mediaVersion, String mediaOffset, String mediaUi, String mediaRank, String playMode,ObserverResponse<VoiceMusicSongInfo> onResult){
        XmlyVoiceRequest request = new XmlyVoiceRequest();
        StringBuilder sb = new StringBuilder();
        String[] strings = new String[]{mediaName, mediaTypeDetail, mediaArtist, mediaAlbum, /*mediaDate,*/ mediaMovie, mediaStyle, mediaLan, mediaVersion, mediaRank};
        for (String str : strings) {
            if (!TextUtils.isEmpty(str)) {
                sb.append(str).append(" ");
            }
        }
        request.setKeyword(sb.toString().replaceAll(",", " ").trim());
        request.setCount(MediaConstant.SEARCH_COUNT);
        request.setSearchType(1);
        LogUtils.d(TAG, "request: " + request);
        musicApi.xmSearch(MediaConstant.TYPE_XM, request, userHandle).subscribe(new IpcObserverResponse<List<VoiceSongInfo>>(){
            @Override
            public void onSuccess(List<VoiceSongInfo> response) {
                List<VoiceMusicSongInfo> voiceMusicSongInfoList = updateMusicList(response,MediaSource.xmly_music.getName());
                if (voiceMusicSongInfoList  != null && !voiceMusicSongInfoList .isEmpty()) {
                    playSongsByMediaId(voiceMusicSongInfoList , voiceMusicSongInfoList.size(),0,false, MediaSource.xmly_music.getName());
                    onResult.onSuccess(voiceMusicSongInfoList.get(0));
                } else {
                    onResult.onSuccess(null);
                }
            }
            @Override
            public void onFailed(int code, String msg) {
                super.onFailed(code, msg);
                LogUtils.d(TAG, "onFailed code: " + code + " msg: " + msg);
                onResult.onFailed(code,msg);
            }
        });
    }

    /***************蓝牙音乐功能功能接口实现*****************/
    public boolean getBtStatus(){
        boolean isConnect = false;
        SyncResponse<Integer> command = musicApi.btStatus(MediaConstant.TYPE_BT, userHandle);
        LogUtils.d(TAG, "bt playMusic command: " + command);
        if (command != null && command.getCode() == 0) {
            isConnect = command.getData() != 0;
        }
        return isConnect;
    }

    public int playBtMusic(){
        VoicePlayRequest voicePlayRequest = new VoicePlayRequest();
        voicePlayRequest.setSourceType("bt_music");
        SyncResponse<Object> playCommand = musicApi.playSong(MediaConstant.SOURCE_TYPE, voicePlayRequest, userHandle);
        LogUtils.d(TAG, "playCommand: " + playCommand);
        if (playCommand != null) {
            return playCommand.getCode();
        }
        return Code.FAILED.code();
    }

    /***************USB音乐功能功能接口实现*****************/
    public boolean getUsbStatus(){
        boolean isConnect = false;
        SyncResponse<Integer> command = musicApi.usbStatus(MediaConstant.TYPE_USB, userHandle);
        LogUtils.d(TAG, "usbstatus command: " + command);
        if (command != null && command.getCode() == 0) {
            isConnect = command.getData() == 0 || command.getData() == 2 || command.getData() == 3 || command.getData() == 4;
        }
        return isConnect;
    }

    public int playUsbMusic(){
        VoicePlayRequest voicePlayRequest = new VoicePlayRequest();
        voicePlayRequest.setSourceType("usb_music");
        SyncResponse<Object> playCommand = musicApi.playSong(MediaConstant.SOURCE_TYPE, voicePlayRequest, userHandle);
        LogUtils.d(TAG, "playCommand: " + playCommand);
        if (playCommand != null) {
            return playCommand.getCode();
        }
        return Code.FAILED.code();
    }

    public void playSongsByMediaId(List<VoiceMusicSongInfo> voiceMusicSongInfoList, int voiceSongNum, int pos, boolean isAppendSong, String sourceType) {
        if (voiceMusicSongInfoList != null && !voiceMusicSongInfoList.isEmpty()) {
            if(voiceSongNum <= 0 || voiceSongNum > voiceMusicSongInfoList.size()){
                voiceSongNum = voiceMusicSongInfoList.size();
            }
            List<String> ids = new ArrayList<>();
            for (int i = 0; i < voiceSongNum; i++) {
                LogUtils.d(TAG, "VoiceSongInfo: " + voiceMusicSongInfoList.get(i).toString());
                ids.add(voiceMusicSongInfoList.get(i).getMediaId());
            }
            VoicePlayRequest voicePlayRequest = new VoicePlayRequest();
            voicePlayRequest.setAppendSong(isAppendSong);
            voicePlayRequest.setPosition(pos);
            voicePlayRequest.setSourceType(sourceType);
            voicePlayRequest.setIds(ids);
            SyncResponse<Object> command = musicApi.playSong(MediaConstant.SOURCE_TYPE, voicePlayRequest, userHandle);
            LogUtils.d(TAG, "playSongs command: " + command);
        } else {
            LogUtils.d(TAG, "voiceSongInfos null or empty");
        }
    }

    public List<VoiceMusicSongInfo> updateMusicList(List<VoiceSongInfo> voiceSongInfoList,String mediasource){
        if(voiceSongInfoList == null || voiceSongInfoList.isEmpty()){
            return null;
        }
        List<VoiceMusicSongInfo> voiceMusicSongInfoList = new ArrayList<>();
        for (int i = 0;i < voiceSongInfoList.size();i++){
            VoiceSongInfo voiceSongInfo = voiceSongInfoList.get(i);
            if(voiceSongInfo.isAudition() || voiceSongInfo.isCanPlay()){
                voiceMusicSongInfoList.add(new VoiceMusicSongInfo(voiceSongInfo.getMediaId(), voiceSongInfo.getMediaName(), MediaHelper.getArtist(voiceSongInfo.getArtist()), MediaHelper.getAlbum(voiceSongInfo.getAlbumName()), voiceSongInfo.getCover(), voiceSongInfo.getDuration(),voiceSongInfo.isCanPlay(),voiceSongInfo.isAudition(),mediasource));
            }
        }
        return voiceMusicSongInfoList;
    }

    public int getPlayModeId(String playMode){
        if (PlayMode.single_cycle.name().equals(playMode) || PlayMode.cycle.name().equals(playMode)) {
            return  0;
        } else if (PlayMode.in_order.name().equals(playMode)) {
            return  2;
        } else if (PlayMode.list_cycle.name().equals(playMode)) {
            return  2;
        } else if (PlayMode.random_play.name().equals(playMode)) {
            return  3;
        }
        return Code.FAILED.code();
    }

}
