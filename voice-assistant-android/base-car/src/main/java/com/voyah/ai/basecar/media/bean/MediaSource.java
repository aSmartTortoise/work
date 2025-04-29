package com.voyah.ai.basecar.media.bean;

import com.voyah.ai.basecar.media.vedio.BiliImpl;
import com.voyah.ai.basecar.media.vedio.IqyImpl;
import com.voyah.ai.basecar.media.vedio.MiguImpl;
import com.voyah.ai.basecar.media.vedio.TencentVideoImpl;
import com.voyah.ai.basecar.media.vedio.ThunderKtvImpl;
import com.voyah.ai.basecar.media.vedio.TiktokImpl;
import com.voyah.ai.basecar.media.vedio.UsbVideoImpl;

public enum MediaSource {
    bt_music("bt_music","bt_music","蓝牙音乐"),//蓝牙音乐
    qq_music("qq_music","qq_music","qq音乐"),// qq音乐
    usb_music("usb_music","usb_music","usb音乐"),// usb音乐
    wy_music("wy_music","wy_music","网易云音乐"),// 网易云音乐
    yt_music("yt_music","yt_music","云听"),// 云听音乐
    yt_broadcast("yt_broadcast","yt_broadcast","广播"),// 云听广播页
    xmly_music("xmly_music","xmly_music","喜马拉雅"), // 喜马拉雅音乐
    voyah_music("voyah_music","voyah_music","岚图音乐"),

    ls_ktv(ThunderKtvImpl.APP_NAME,"ls_ktv","雷石"), // 雷石
    migu_video(MiguImpl.APP_NAME,"migu_video","咪咕"),// 咪咕
    tencent_video(TencentVideoImpl.APP_NAME,"tencent_video","腾讯"),// 腾讯
    iqy_video(IqyImpl.APP_NAME,"iqy_video","爱奇艺"),// 爱奇艺
    bili_video(BiliImpl.APP_NAME,"bili_video","bili"),// bili
    tiktok_video(TiktokImpl.APP_NAME,"tiktok_video","车鱼"),// 抖音
    usb_video(UsbVideoImpl.APP_NAME,"usb_video","usb视频"),// usb视频
    browser_vedio("com.voyah.cockpit.browser","browser_vedio","浏览器"),// 浏览器视频
    dlna_vedio("com.voyah.cockpit.dlnaserver","dlna_vedio","dlna"),// DLNA投屏
    unknown_source("","","");//

    private final String name;
    private final String type;
    private final String cnName;

    MediaSource(String name, String type, String cnName) {
        this.name = name;
        this.type = type;
        this.cnName = cnName;
    }

    public String getName() {
        return name;
    }

    public String getCnName() {
        return cnName;
    }

    public String getType() {
        return type;
    }

    public static MediaSource translateMediasource(String type) {
        for (MediaSource source : MediaSource.values()) {
            if (source.getName().equals(type) || source.getType().equals(type) || source.getCnName().equals(type)) {
                return source;
            }
        }
        return unknown_source;
    }

    public static boolean isMusicSource(String source){
        MediaSource mediaSource = translateMediasource(source);
        if(mediaSource == bt_music || mediaSource == usb_music || mediaSource == wy_music || mediaSource == yt_music || mediaSource == xmly_music || mediaSource == voyah_music){
            return true;
        }
        return false;
    }

    public static boolean isVideoSource(String source){
        MediaSource mediaSource = translateMediasource(source);
        if(mediaSource == tencent_video || mediaSource == iqy_video || mediaSource == bili_video || mediaSource == tiktok_video || mediaSource == usb_video || mediaSource == browser_vedio || mediaSource == dlna_vedio){
            return true;
        }
        return false;
    }
}
