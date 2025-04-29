package com.voyah.ai.device.voyah.common.media.impl;

import android.os.UserHandle;

import com.voyah.ipcsdk.annotation.Notify;
import com.voyah.ipcsdk.annotation.Param;
import com.voyah.ipcsdk.annotation.Path;
import com.voyah.ipcsdk.annotation.Request;
import com.voyah.ipcsdk.annotation.Sync;
import com.voyah.ipcsdk.annotation.User;
import com.voyah.ipcsdk.model.SyncResponse;
import com.voyah.media.common.model.mediamanager.MediaInfo;
import com.voyah.media.common.model.mediamanager.PlayModeInfo;
import com.voyah.media.common.model.mediamanager.PlayStatusInfo;
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

import java.util.List;

import io.reactivex.Observable;

public interface MusicApi {

    @Sync
    @Request("{sourceType}" + "/" + "mediamanagerbridgesm/prev")
    SyncResponse<Object> prev(@Path("sourceType") String sourceType, @Param String source,@User UserHandle userHandle);

    @Sync
    @Request("{sourceType}" + "/" + "mediamanagerbridgesm/next")
    SyncResponse<Object> next(@Path("sourceType") String sourceType, @Param String source,@User UserHandle userHandle);

    @Sync
    @Request("{sourceType}" + "/" + "mediamanagerbridgesm/play")
    SyncResponse<Object> play(@Path("sourceType") String sourceType, @User UserHandle userHandle);

    //playsouce修改为play带参
    @Sync
    @Request("{sourceType}" + "/" + "mediamanagerbridgesm/play")
    SyncResponse<Object> playSource(@Path("sourceType") String sourceType, @Param String source, @User UserHandle userHandle);

    @Sync
    @Request("{sourceType}" + "/" + "mediamanagerbridgesm/pause")
    SyncResponse<Object> pause(@Path("sourceType") String sourceType,@Param String source, @User UserHandle userHandle);

    @Sync
    @Request("{sourceType}" + "/" + "mediamanagerbridgesm/seekto")
    SyncResponse<Object> seekTo(@Path("sourceType") String sourceType, @Param ProgressInfo progressInfo, @User UserHandle userHandle);

    // TODO: 2024/8/13 应该传时间
    @Sync
    @Request("{sourceType}" + "/" + "mediamanagerbridgesm/rewind")
    SyncResponse<Object> rewind(@Path("sourceType") String sourceType, @User UserHandle userHandle);

    // TODO: 2024/8/13 应该传时间
    @Sync
    @Request("{sourceType}" + "/" + "mediamanagerbridgesm/forward")
    SyncResponse<Object> forward(@Path("sourceType") String sourceType, @User UserHandle userHandle);

    @Sync
    @Request("{sourceType}" + "/" + "mediamanagerbridgesm/setplaymode")
    SyncResponse<Object> setPlayMode(@Path("sourceType") String sourceType, @Param PlayModeInfo playMod, @User UserHandle userHandle);

    @Sync
    @Request("{sourceType}" + "/" + "mediamanagerbridgesm/getplaymode")
    SyncResponse<Integer> getPlayMode(@Path("sourceType") String sourceType, @Param String source, @User UserHandle userHandle);

    @Sync
    @Request("{sourceType}" + "/" + "mediamanagersm/getsource")
    SyncResponse<String> getSource(@Path("sourceType") String sourceType, @User UserHandle userHandle);

    @Sync
    @Request("{sourceType}" + "/" + "mediamanagerbridgesm/getplaystate")
    SyncResponse<Integer> getPlayState(@Path("sourceType") String sourceType, @User UserHandle userHandle);

    @Sync
    @Request("{sourceType}" + "/" + "mediamanagerbridgesm/getmediainfo")
    SyncResponse<MediaInfo> getMediainfo(@Path("sourceType") String sourceType, @Param String source,@User UserHandle userHandle);

    @Sync
    @Request("{sourceType}" + "/" + "mediamanagerbridgesm/getspeedmode")
    SyncResponse<Float> getSpeedMode(@Path("sourceType") String sourceType, @User UserHandle userHandle);

    @Sync
    @Request("{sourceType}" + "/" + "mediamanagerbridgesm/setspeedmode")
    SyncResponse<Object> setSpeedMode(@Path("sourceType") String sourceType, @Param SpeedModeInfo speedModeInfo, @User UserHandle userHandle);

    @Sync
    @Request("{sourceType}" + "/" + "mediamanagerbridgesm/getduration")
    SyncResponse<Long> getDuration(@Path("sourceType") String sourceType, @User UserHandle userHandle);

    @Sync
    @Request("{sourceType}" + "/" + "mediamanagervoicesm/voiceplaysong")
    SyncResponse<Object> playSong(@Path("sourceType") String sourceType, @Param VoicePlayRequest request, @User UserHandle userHandle);


    @Request("{sourceType}" + "/" + "ytmusicvoicesm/voicesearchyt")
    Observable<List<VoiceSongInfo>> ytSearch(@Path("sourceType") String sourceType, @Param YtVoiceRequest request, @User UserHandle userHandle);

    @Request("{sourceType}" + "/" + "ytmusicvoicesm/voicehistory")
    Observable<List<VoiceSongInfo>> ytHistory(@Path("sourceType") String sourceType, @Param YtVoiceRequest request, @User UserHandle userHandle);

    @Request("{sourceType}" + "/" + "ytmusicvoicesm/voicecollectsonglist")
    Observable<List<VoiceSongInfo>> ytCollectList(@Path("sourceType") String sourceType, @Param YtVoiceRequest request, @User UserHandle userHandle);

    @Request("{sourceType}" + "/" + "ytmusicvoicesm/voicecollect")
    Observable<Object> ytCollect(@Path("sourceType") String sourceType, @Param YtVoiceRequest request, @User UserHandle userHandle);

    @Request("{sourceType}" + "/" + "ytmusicvoicesm/voicecollectstate")
    Observable<Boolean> ytCollectState(@Path("sourceType") String sourceType, @User UserHandle userHandle);

    @Sync
    @Request("{sourceType}" + "/" + "ytmusicvoicesm/voiceislogin")
    SyncResponse<Boolean> ytIsLogin(@Path("sourceType") String sourceType, @User UserHandle userHandle);

    @Request("{sourceType}" + "/" + "xmlymusicvoicesm/voicesearchxm")
    Observable<List<VoiceSongInfo>> xmSearch(@Path("sourceType") String sourceType, @Param XmlyVoiceRequest request, @User UserHandle userHandle);

    @Request("{sourceType}" + "/" + "xmlymusicvoicesm/voicehistory")
    Observable<List<VoiceSongInfo>> xmHistory(@Path("sourceType") String sourceType, @Param XmlyVoiceRequest request, @User UserHandle userHandle);

    @Request("{sourceType}" + "/" + "xmlymusicvoicesm/voicecollectsonglist")
    Observable<List<VoiceSongInfo>> xmCollectList(@Path("sourceType") String sourceType, @Param XmlyVoiceRequest request, @User UserHandle userHandle);

    @Request("{sourceType}" + "/" + "xmlymusicvoicesm/voicecollect")
    Observable<Object> xmCollect(@Path("sourceType") String sourceType, @Param XmlyVoiceRequest request, @User UserHandle userHandle);

    @Request("{sourceType}" + "/" + "xmlymusicvoicesm/voicecollectstate")
    Observable<Boolean> xmCollectState(@Path("sourceType") String sourceType, @User UserHandle userHandle);

    @Sync
    @Request("{sourceType}" + "/" + "xmlymusicvoicesm/voiceislogin")
    SyncResponse<Boolean> xmIsLogin(@Path("sourceType") String sourceType, @User UserHandle userHandle);


    @Request("{sourceType}" + "/" + "wymusicvoicesm/voicecollect")
    Observable<Object> wyCollect(@Path("sourceType") String sourceType, @Param WyVoiceRequest request, @User UserHandle userHandle);

    @Request("{sourceType}" + "/" + "wymusicvoicesm/voicecollectstate")
    Observable<Boolean> wyCollectState(@Path("sourceType") String sourceType, @User UserHandle userHandle);

    @Sync
    @Request("{sourceType}" + "/" + "wymusicvoicesm/voiceislogin")
    SyncResponse<Boolean> wyIsLogin(@Path("sourceType") String sourceType, @User UserHandle userHandle);

    @Request("{sourceType}" + "/" + "wymusicvoicesm/voicecollectsonglist")
    Observable<List<VoiceSongInfo>> wyCollectList(@Path("sourceType") String sourceType, @Param WyVoiceRequest request, @User UserHandle userHandle);

    @Request("{sourceType}" + "/" + "wymusicvoicesm/voicehistory")
    Observable<List<VoiceSongInfo>> wyHistory(@Path("sourceType") String sourceType, @Param WyVoiceRequest request, @User UserHandle userHandle);

    @Request("{sourceType}" + "/" + "wymusicvoicesm/voicerecommend")
    Observable<List<VoiceSongInfo>> wyRecommend(@Path("sourceType") String sourceType, @Param WyVoiceRequest request, @User UserHandle userHandle);

    @Request("{sourceType}" + "/" + "wymusicvoicesm/voicesearchwybykeyword")
    Observable<List<VoiceSongInfo>> wySearch(@Path("sourceType") String sourceType, @Param WyVoiceRequest request, @User UserHandle userHandle);

    @Request("{sourceType}" + "/" + "wymusicvoicesm/voicesearchwy")
    Observable<VoiceSearchSongInfo> wySearchNew(@Path("sourceType") String sourceType, @Param WyVoiceRequest request, @User UserHandle userHandle);

    @Sync
    @Request("{sourceType}" + "/" + "uimusicvoicesm/getpagestate")
    SyncResponse<Integer> getPageState(@Path("sourceType") String sourceType, @Param UIVoiceRequest request, @User UserHandle userHandle);

    @Sync
    @Request("{sourceType}" + "/" + "uimusicvoicesm/openpage")
    SyncResponse<Object> openPage(@Path("sourceType") String sourceType, @Param UIVoiceRequest request, @User UserHandle userHandle);

    @Sync
    @Request("{sourceType}" + "/" + "uimusicvoicesm/closepage")
    SyncResponse<Object> closePage(@Path("sourceType") String sourceType, @Param UIVoiceRequest request, @User UserHandle userHandle);

    @Request("{sourceType}" + "/" + "qqmusicvoicesm/voicecollect")
    Observable<Object> qqCollect(@Path("sourceType") String sourceType, @Param QQVoiceRequest request, @User UserHandle userHandle);

    @Request("{sourceType}" + "/" + "qqmusicvoicesm/voicecollectstate")
    Observable<Boolean> qqCollectState(@Path("sourceType") String sourceType, @User UserHandle userHandle);

    @Sync
    @Request("{sourceType}" + "/" + "qqmusicvoicesm/voiceislogin")
    SyncResponse<Boolean> qqIsLogin(@Path("sourceType") String sourceType, @User UserHandle userHandle);

    @Request("{sourceType}" + "/" + "qqmusicvoicesm/voicecollectsonglist")
    Observable<List<VoiceSongInfo>> qqCollectList(@Path("sourceType") String sourceType, @Param QQVoiceRequest request, @User UserHandle userHandle);

    @Request("{sourceType}" + "/" + "qqmusicvoicesm/voicehistory")
    Observable<List<VoiceSongInfo>> qqHistory(@Path("sourceType") String sourceType, @Param QQVoiceRequest request, @User UserHandle userHandle);

    @Request("{sourceType}" + "/" + "qqmusicvoicesm/voicerecommend")
    Observable<List<VoiceSongInfo>> qqRecommend(@Path("sourceType") String sourceType, @Param QQVoiceRequest request, @User UserHandle userHandle);

    @Request("{sourceType}" + "/" + "qqmusicvoicesm/voicesearchqq")
    Observable<List<VoiceSongInfo>> qqSearch(@Path("sourceType") String sourceType, @Param QQVoiceRequest request, @User UserHandle userHandle);


    @Notify("{sourceType}" + "/" + "mediamanagerbridgesm/onprogresschanged")
    Observable<ProgressInfo> onProgressChanged(@Path("sourceType") String sourceType, @User UserHandle userHandle);

    @Notify("{sourceType}" + "/" + "mediamanagerbridgesm/onplaystatechanged")
    Observable<PlayStatusInfo> onPlayStateChanged(@Path("sourceType") String sourceType, @User UserHandle userHandle);

    @Sync
    @Request("{sourceType}" + "/" + "btmusicvoicesm/voicegetconnect")
    SyncResponse<Integer> btStatus(@Path("sourceType") String sourceType, @User UserHandle userHandle);

    @Sync
    @Request("{sourceType}" + "/" + "usbmusicvoicesm/voicegetconnect")
    SyncResponse<Integer> usbStatus(@Path("sourceType") String sourceType, @User UserHandle userHandle);

    @Sync
    @Request("{sourceType}" + "/" + "uimusicvoicesm/getuisourcetype")
    SyncResponse<String> getUiSourceType(@Path("sourceType") String sourceType, @User UserHandle userHandle);

    @Sync
    @Request("{sourceType}" + "/" + "mediamanagerbridgesm/getprogress")
    SyncResponse<Long> getProgress(@Path("sourceType") String sourceType,@Param String source, @User UserHandle userHandle);
}