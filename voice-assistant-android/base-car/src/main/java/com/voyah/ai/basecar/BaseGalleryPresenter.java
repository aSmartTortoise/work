package com.voyah.ai.basecar;


import com.voice.sdk.device.GalleryInterface;
import com.voice.sdk.constant.ApplicationConstant;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.cockpit.common.gallery.GalleryManager;
import com.voyah.cockpit.common.util.Constant;

import java.util.Locale;

/**
 * author : jie wang
 * date : 2025/3/3 21:20
 * description :
 */
public abstract class BaseGalleryPresenter extends BaseAppPresenter implements GalleryInterface {

    private static final String TAG = "BaseGalleryPresenter";

    protected BaseGalleryPresenter() {
        super();
    }



    @Override
    public String getGalleryName(String tabName) {
        String galleryName = tabName;
        switch (tabName) {
            case ApplicationConstant.SLOT_NAME_LOCAL_GALLERY:
                galleryName = String.format(Locale.CHINA, "%s相册", "本地");
                break;
            case ApplicationConstant.SLOT_NAME_ONLINE_GALLERY:
                galleryName = String.format(Locale.CHINA, "%s相册", "云端");
                break;
            case ApplicationConstant.SLOT_NAME_USB_GALLERY:
                galleryName = String.format(Locale.CHINA, "%s相册", "USB");
                break;
            case ApplicationConstant.SLOT_NAME_TRANSFER_LIST_GALLERY:
                galleryName = String.format(Locale.CHINA, "%s", "传输列表");
                break;
            case ApplicationConstant.SLOT_NAME_DEFAULT_GALLERY:
                galleryName = String.format(Locale.CHINA, "%s相册", "默认");
                break;
            case ApplicationConstant.SLOT_NAME_TRIP_SHOOT_GALLERY:
                galleryName = String.format(Locale.CHINA, "%s相册", "旅拍");
                break;
            case ApplicationConstant.SLOT_NAME_AI_DRAWING_GALLERY:
                galleryName = String.format(Locale.CHINA, "%s相册", "AI绘画");
                break;
            case ApplicationConstant.SLOT_NAME_COLLECTION_GALLERY:
                galleryName = String.format(Locale.CHINA, "%s相册", "收藏");
                break;
            case ApplicationConstant.SLOT_NAME_VOYAH_SHARE_GALLERY:
                galleryName = String.format(Locale.CHINA, "%s相册", "互联");
                break;
            default:
                break;
        }
        LogUtils.i(TAG, "getGalleryName galleryName:" + galleryName);
        return galleryName;
    }

    @Override
    public boolean isGalleryApp(String pkgName) {
        return "com.voyah.cockpit.gallery".equals(pkgName);
    }

    @Override
    public int getGalleryType(String tabName) {
        int galleryType = -1;
        switch (tabName) {
            case ApplicationConstant.SLOT_NAME_USB_GALLERY:
                galleryType = Constant.GalleryType.USB;
                break;
            case ApplicationConstant.SLOT_NAME_LOCAL_GALLERY:
                galleryType = Constant.GalleryType.LOCAL;
                break;
            case ApplicationConstant.SLOT_NAME_ONLINE_GALLERY:
                galleryType = Constant.GalleryType.CLOUD;
                break;
            case ApplicationConstant.SLOT_NAME_TRANSFER_LIST_GALLERY:
                galleryType = Constant.GalleryType.TRANSFER;
                break;
            case ApplicationConstant.SLOT_NAME_DEFAULT_GALLERY:
                galleryType = Constant.GalleryType.GALLERY_TYPE_DEFAULT;
                break;
            case ApplicationConstant.SLOT_NAME_AI_DRAWING_GALLERY:
                galleryType = Constant.GalleryType.GALLERY_TYPE_AI_ALBUM;
                break;
            case ApplicationConstant.SLOT_NAME_TRIP_SHOOT_GALLERY:
                galleryType = Constant.GalleryType.GALLERY_TYPE_TRAVEL;
                break;
            case ApplicationConstant.SLOT_NAME_COLLECTION_GALLERY:
                galleryType = Constant.GalleryType.GALLERY_TYPE_STORE;
                break;
            case ApplicationConstant.SLOT_NAME_VOYAH_SHARE_GALLERY:
                galleryType = Constant.GalleryType.CONNECT;
                break;
        }
        LogUtils.i(TAG, "getGalleryType galleryType:" + galleryType);
        return galleryType;
    }

}
