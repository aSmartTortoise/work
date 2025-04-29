package com.voice.sdk.device.media;

import com.voice.sdk.device.system.DeviceScreenType;
import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;
import com.voyah.ds.common.entity.domains.media.VideoInfo;

import java.util.List;
import java.util.Map;

public interface MediaCenterInterface {
    VoyahMusicInterface getVoyahMusic();

    VoyahMusicControlInterface getVoyahMusicControl();
    void init();

    TTSBean initVoicePosition(String queryPosition,String soundLocation);
    void destroy();
    TTSBean switchUi(boolean isOpen, String uiName, String appName, String mediaType);
    TTSBean open(boolean isOpen, String mediaType, String mediaSource, String appName, String position, String queryPosition);
    TTSBean pre();
    TTSBean next();
    TTSBean play();
    TTSBean replay();
    TTSBean stop(boolean isExit);
    TTSBean seek(String seekType, long duration);
    TTSBean switchDanmaku(boolean isOpen);
    TTSBean speed(String adjustType, String numberRate, String level);
    TTSBean definition(String adjustType, String mode, String level);
    TTSBean queryPlayInfo();
    TTSBean jump();
    TTSBean switchLyric(boolean isOpen);
    TTSBean switchCollect(boolean isCollect, String mediaType);
    TTSBean switchComment(boolean isComment, String mediaType);
    TTSBean switchLike(boolean isLike, String mediaType);
    TTSBean switchAttention(boolean isAttention, String mediaType);
    TTSBean switchPlayer(boolean isOpen);
    TTSBean switchPlayList(boolean isOpen);
    TTSBean switchOriginalSinging(boolean isOriginal);
    TTSBean switchPlayMode(String switchType, String playMode);
    TTSBean switchHistoryUI(boolean isOpen);
    TTSBean switchCollectUI(boolean isOpen);
    TTSBean play(String mediaType, String mediaTypeDetail, String appName, String mediaSource, String mediaName, String mediaArtist, String mediaAlbum, String mediaDate, String mediaMovie, String mediaStyle, String mediaLan, String mediaVersion, String mediaOffset, String mediaUi, String mediaRank, String playMode);
    TTSBean playUI(int type);
    void scheme(VideoInfo videoInfo);

    TTSBean playEpisode(int playEpisode);

    TTSBean playVideo(String mediaSource,String appName,String mediaUi);

    void openTencentOrIqy(String mediaUi, boolean isTencent);

    Map<String,Object> getVideoMap(List<VideoInfo> videoInfoList,String sessionId,String requestId);

    int getPlayingDisplayId();
    int getDisplayId(String srcScreen);

    boolean isMultiScreenForDst(String screenStr,String dstPosition);
    int getDisplayId(int screenType);
    void pushScreenToDisplay(int sourceDisplayId, int targetDisplayId,boolean isSoundLocation);
    void shareScreenSingle(int sourceDisplayId, int targetDisplayId, boolean isSoundLocation);
    void closeShareScreenSingle(int displayId);
    void shareScreenForAll(int sourceDisplayId);
    void closeShareScreenForAll();
    boolean switchVideoPage(String uiName, String appName, boolean isOpen, String mediaType);
    boolean isVideoApp(String pkgName);
    boolean judgeSupport(String key, String value);
    void setIdentifier(String identifier);

    int getAvmStatus();
    boolean getCleanModeStatus();

    Map<String, String> uploadPlayStatus();

    boolean getVideoPlayingByDisplayId(DeviceScreenType deviceScreenType);

    void setTiktokPlayStatus(boolean isPlaying);

    void gestureTencent();

    boolean isSafeLimitation();

    void speakTts(String tts);
}
