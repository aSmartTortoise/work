package com.voice.sdk.device.media;

import android.content.Context;

import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;

public interface MediaInterface {
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

    default TTSBean switchHistoryUI(boolean isOpen){return null;};

    default TTSBean switchCollectUI(boolean isOpen){return null;};

    TTSBean playUI(int type);

    TTSBean pre();

    TTSBean next();

    TTSBean play();

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
}
