package com.voice.sdk.device.media;

import android.content.Context;

import com.voyah.ai.voice.platform.agent.api.tts.bean.TTSBean;

public interface MediaMusicInterface {
    void init(Context context);

    void destroy(Context context);

    /**
     * 跳转页面
     * @param isOpen 打开 or 关闭
     * @param pageId 页面的pageid
     */
    TTSBean switchPage(boolean isOpen, int pageId);

    /**
     * 查询播放信息
     */
    TTSBean queryPlayInfo();

    /**
     * 跳转至歌词页
     */
    TTSBean switchLyric(boolean isOpen);

    /**
     * 收藏和订阅歌曲
     * @param isCollect 收藏 or 取消收藏
     */
    TTSBean collect(boolean isCollect);

    /**
     * 跳转至播放列表
     * @param isOpen 打开 or 关闭
     */
    TTSBean switchPlayList(boolean isOpen);

    /**
     * 跳转至播放页
     * @param isOpen 打开 or 关闭
     * @return
     */
    TTSBean switchPlayPage(boolean isOpen);

    /**
     * 跳转至收藏页面
     * @param isOpen 打开 or 关闭
     * @return
     */
    TTSBean switchCollectUI(boolean isOpen);

    /**
     * 跳转至历史页面
     * @param isOpen 打开 or 关闭
     */
    TTSBean switchHistoryUI(boolean isOpen);

    /**
     * 点播
     */
    TTSBean playMusic();

    /**
     * 播放收藏列表
     */
    TTSBean playCollect();

    /**
     * 播放历史列表
     */
    TTSBean playHistory();

    /**
     * 播放音乐
     */
    TTSBean playRecommend();

    /**
     * 搜索播放
     * @param mediaType
     * @param mediaTypeDetail
     * @param appName
     * @param mediaSource
     * @param mediaName
     * @param mediaArtist
     * @param mediaAlbum
     * @param mediaDate
     * @param mediaMovie
     * @param mediaStyle
     * @param mediaLan
     * @param mediaVersion
     * @param mediaOffset
     * @param mediaUi
     * @param mediaRank
     * @param playMode
     * @return
     */
    TTSBean playSearch(String mediaType, String mediaTypeDetail, String appName, String mediaSource, String mediaName, String mediaArtist, String mediaAlbum, String mediaDate, String mediaMovie, String mediaStyle, String mediaLan, String mediaVersion, String mediaOffset, String mediaUi, String mediaRank, String playMode);

    /**
     * 媒体开关
     * @param isOpen 打开 or 关闭
     * @return
     */
    TTSBean mediaSwitch(boolean isOpen);

    /**
     * 是否登录
     * @return 是否登录
     */
    boolean isLogin();

    /**
     * 打开云听广播
     * @param isOpen 打开 or 关闭
     * @return
     */
    default TTSBean mediaSwitchBroadcastYt(boolean isOpen){return null;}

    /**
     * 喜马拉雅速度调节
     * @return
     */
    default TTSBean mediaSpeedAdjustXm(String adjustType, String numberRate, String level){return null;}
}
