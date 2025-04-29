package com.voice.sdk.device.media;

import android.content.Context;

import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;

public interface VoyahMusicInterface {
    void init(Context context);

    void destroy(Context context);

    default void initUserHandle(String position,String uiSoundLocation){};

    TTSBean play(String mediaType,
                 String mediaTypeDetail,
                 String appName,
                 String mediaSource,
                 String mediaName,
                 String mediaArtist,
                 String mediaAlbum,
                 String mediaDate,
                 String mediaMovie,
                 String mediaStyle,
                 String mediaLan,
                 String mediaVersion,
                 String mediaOffset,
                 String mediaUi,
                 String mediaRank,
                 String playMode);

    TTSBean switchHistoryUI(boolean isOpen,String appName);

    TTSBean switchCollectUI(boolean isOpen,String appName);

    TTSBean playUI(int type);

    TTSBean pre(String source);

    TTSBean next(String source);

    TTSBean play(String source);

    TTSBean replay();

    TTSBean stop(boolean isExit);

    TTSBean seek(String seekType, long duration);

    TTSBean switchPlayMode(String switchType, String playMode);

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

    TTSBean switchPlayList(boolean isOpen);

    TTSBean switchPlayer(boolean isOpen);

    TTSBean switchOriginalSinging(boolean isOriginal);

    TTSBean playMusic(String appName);

    TTSBean open(boolean isOpen, String type, boolean isStop, String position, String queryPosition);

    int getPageState(int pageId);

    boolean isOpenWy(String appName);

    boolean isLogin(String appName);

    TTSBean openMediaAppByAppName(String appName,boolean isOpen);

    boolean isMedia(String appName);

    boolean isPlaying();

    boolean isMediaByAppName(String appName, String source);

    String getMediaPlayingResource();
}
