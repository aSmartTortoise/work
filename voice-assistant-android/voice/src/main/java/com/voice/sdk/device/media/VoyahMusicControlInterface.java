package com.voice.sdk.device.media;

import android.content.Context;
import android.os.UserHandle;

import com.voice.sdk.device.media.bean.MediaMusicInfo;
import com.voice.sdk.device.media.bean.ObserverResponse;
import com.voice.sdk.device.media.bean.VoiceMusicSongInfo;

import java.util.List;


public interface VoyahMusicControlInterface {
    void init(Context context);

    void destroy(Context context);

    void initUserHandle(UserHandle userHandle, int displayId);

    /**
     * 页面跳转
     * @param isOpen
     * @param pageId
     * @return
     */
    int switchPage(boolean isOpen,int pageId);

    /**
     * 是否播放中
      * @return
     */
    boolean isPlaying();

    /**
     * 是否播放中
     * @return
     */
    boolean isPlayingByUserHandle(UserHandle userhandle);

    /**
     * 当前音源是否在播放
     * @return
     */
    default boolean isplayingBySource(String source,UserHandle userHandle){return false;}

    /**
     * 获取当前音源
     * @return
     */
    String getSource();

    /**
     * 显示在前台的音源
     * @return
     */
    String getMediaUiResource(UserHandle userHandle);

    /**
     * 上一首
     */
    int pre(String source);

    /**
     * 下一首
     */
    int next(String source);

    /**
     * 播放音乐
     * @param source 音源类型
     * @return
     */
    int play(String source);

    /**
     * 重播
     * @return
     */
    int replay(String source);

    /***
     * 暂停
     * @return
     */
    int stop(String source);

    /**
     * 获取进度
     */
    long getProgress(String source);

    /**
     * 获取音乐信息
     */
    MediaMusicInfo getMediaInfo(String source);

    /**
     * 设置进度
     * @param progress
     * @return
     */
    int setSeekTo(long progress,String source);

    /**
     * 播放模式
     * @return
     */
    int getPlayMode(String source);

    /**
     * 设置播放模式
     */
    int setPlayMode(int playMode,String source);

    /**
     * 获取页面状态
     */
    int getPageState(int pageId);

    /**
     * 网易是否登录
     * @return
     */
    boolean isLoginWy();

    /**
     * QQ是否登录
     * @return
     */
    boolean isLoginQq();

    /**
     * 云听是否登录
     * @return
     */
    boolean isLoginYt();

    /**
     * 喜马是否登录
     * @return
     */
    boolean isLoginXm();

    /**
     * 蓝牙状态
     * @return
     */
    boolean getBtStatus();

    /**
     * 播放蓝牙音乐
     * @return
     */
    int playBtMusic();

    /**
     * 获取USB状态
     */
    boolean getUsbStatus();

    /**
     * 播放USB音乐
     */
    int playUsbMusic();

    /**
     * 获取播放速度
     */
    Float getSpeed();

    /**
     * 设置播放速度
     */
    void setSpeed(Float speed);

    /**
     * qq音乐收藏状态获取
     * @param onResult
     */
    void qqCollectState(ObserverResponse<Boolean> onResult);

    /**
     * qq音乐收藏
     * @param isCollect
     * @param onResult
     */
    void qqCollect(boolean isCollect,ObserverResponse<Boolean> onResult);

    /**
     * QQ音乐获取收藏列表
     */
    void playCollectQq(ObserverResponse<VoiceMusicSongInfo> onResult);

    /**
     * QQ音乐获取历史列表
     */
    void playHistoryQq(ObserverResponse<VoiceMusicSongInfo> onResult);

    /**
     * QQ音乐获取推荐列表
     */
    void playRecommendQq(ObserverResponse<VoiceMusicSongInfo> onResult);

    /**
     * QQ音乐搜索
     */
    void playSearchQq(String mediaType, String mediaTypeDetail, String appName, String mediaSource, String mediaName, String mediaArtist, String mediaAlbum, String mediaDate, String mediaMovie, String mediaStyle, String mediaLan, String mediaVersion, String mediaOffset, String mediaUi, String mediaRank, String playMode,ObserverResponse<VoiceMusicSongInfo> onResult);

    /**
     * 网易云收藏状态获取
     * @param onResult
     */
    void wyCollectState(ObserverResponse<Boolean> onResult);

    /**
     * 网易云收藏
     * @param isCollect
     * @param onResult
     */
    void wyCollect(boolean isCollect,ObserverResponse<Boolean> onResult);

    /**
     * 网易云获取收藏列表
     */
    void playCollectWy(ObserverResponse<VoiceMusicSongInfo> onResult);

    /**
     * 网易云获取历史列表
     */
    void playHistoryWy(ObserverResponse<VoiceMusicSongInfo> onResult);

    /**
     * 网易云获取推荐列表
     */
    void playRecommendWy(ObserverResponse<VoiceMusicSongInfo> onResult);

    /**
     * 网易云搜索
     */
    void playSearchWy(String mediaType, String mediaTypeDetail, String appName, String mediaSource, String mediaName, String mediaArtist, String mediaAlbum, String mediaDate, String mediaMovie, String mediaStyle, String mediaLan, String mediaVersion, String mediaOffset, String mediaUi, String mediaRank, String playMode,ObserverResponse<VoiceMusicSongInfo> onResult);

    /**
     * 喜马拉雅收藏状态获取
     * @param onResult
     */
    void xmCollectState(ObserverResponse<Boolean> onResult);

    /**
     * 喜马拉雅收藏
     * @param isCollect
     * @param onResult
     */
    void xmCollect(boolean isCollect,ObserverResponse<Boolean> onResult);

    /**
     * 喜马拉雅获取收藏列表
     */
    void playCollectXm(ObserverResponse<VoiceMusicSongInfo> onResult);

    /**
     * 喜马拉雅获取历史列表
     */
    void playHistoryXm(ObserverResponse<VoiceMusicSongInfo> onResult);

    /**
     * 喜马拉雅搜索
     */
    void playSearchXm(String mediaType, String mediaTypeDetail, String appName, String mediaSource, String mediaName, String mediaArtist, String mediaAlbum, String mediaDate, String mediaMovie, String mediaStyle, String mediaLan, String mediaVersion, String mediaOffset, String mediaUi, String mediaRank, String playMode,ObserverResponse<VoiceMusicSongInfo> onResult);

    /**
     * 云听收藏状态获取
     * @param onResult
     */
    void ytCollectState(ObserverResponse<Boolean> onResult);

    /**
     * 云听收藏
     * @param isCollect
     * @param onResult
     */
    void ytCollect(boolean isCollect,ObserverResponse<Boolean> onResult);

    /**
     * 云听获取收藏列表
     */
    void playCollectYt(ObserverResponse<VoiceMusicSongInfo> onResult);

    /**
     * 云听获取历史列表
     */
    void playHistoryYt(ObserverResponse<VoiceMusicSongInfo> onResult);

    /**
     * 云听搜索
     */
    void playSearchYt(String mediaType, String mediaTypeDetail, String appName, String mediaSource, String mediaName, String mediaArtist, String mediaAlbum, String mediaDate, String mediaMovie, String mediaStyle, String mediaLan, String mediaVersion, String mediaOffset, String mediaUi, String mediaRank, String playMode,ObserverResponse<VoiceMusicSongInfo> onResult);

    boolean isVoyahMusicFront();

    default void setCardInfo(String sessionid,String  reqid, String mAgentIdentifier, int soundLocation){
        // Default implementation does nothing
    }

    default void setMusicList(List<VoiceMusicSongInfo> voiceMusicSongInfoList){
        // Default implementation returns an empty list
    }
    default List<VoiceMusicSongInfo> getMusicList(){
        // Default implementation returns an empty list
        return null;
    }

    default void playSongsByMediaId(List<VoiceMusicSongInfo> voiceMusicSongInfoList, int voiceSongNum, int pos,boolean isAppendSong, String sourceType){
        // Default implementation does nothing
    }
    default void playSongsByMediaId(List<VoiceMusicSongInfo> voiceMusicSongInfoList, int voiceSongNum, int pos,boolean isAppendSong, String sourceType,UserHandle userHandle){
        // Default implementation does nothing
    }

    default boolean isShowMusicCard(){
        return false;
    }
}
