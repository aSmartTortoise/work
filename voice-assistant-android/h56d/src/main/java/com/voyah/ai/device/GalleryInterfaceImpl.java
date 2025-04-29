package com.voyah.ai.device;

import com.voyah.ai.basecar.BaseGalleryPresenter;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.cockpit.appadapter.aidlimpl.GalleryServiceImpl;


/**
 * author : jie wang
 * date : 2025/4/15 14:43
 * description :
 */
public class GalleryInterfaceImpl extends BaseGalleryPresenter {

    private static final String TAG = "DeviceGalleryInterfaceImpl";

    private GalleryInterfaceImpl() {
        super();
    }

    static class Holder {
        private static GalleryInterfaceImpl INSTANCE = new GalleryInterfaceImpl();
    }

    public static GalleryInterfaceImpl getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public void init() {
        LogUtils.i(TAG, "init");
        initSdk();
    }

    @Override
    public boolean openApp() {
        LogUtils.d(TAG, "openApp");
        beforeOpenApp();
        GalleryServiceImpl.getInstance(mContext).setGallerySwitch(true);
        return true;
    }

    @Override
    public boolean closeApp() {
        LogUtils.d(TAG, "closeGallery");
        GalleryServiceImpl.getInstance(mContext).setGallerySwitch(false);
        return true;
    }

    @Override
    public boolean isAppForeground() {
        boolean galleryForeground = GalleryServiceImpl.getInstance(mContext).getGallerySwitch();
        LogUtils.d(TAG, "isGalleryForeground galleryForeground:" + galleryForeground);
        return galleryForeground;
    }

    @Override
    public boolean isTabGalleryForeground(int galleryType) {
        boolean openFlag = false;
        boolean galleryAppForegroundFlag = GalleryServiceImpl.getInstance(mContext).getGallerySwitch();
        if (galleryAppForegroundFlag) {
            int currentGalleryType = getCurrentGalleryType();
            LogUtils.d(TAG, "isTabGalleryForeground currentGalleryType:" + currentGalleryType);
            if (galleryType == currentGalleryType) {
                openFlag = true;
            }
        }
        LogUtils.d(TAG, "isTabGalleryForeground galleryType:" + galleryType + " openFlag:" + openFlag);
        return openFlag;
    }

    @Override
    public int getCurrentGalleryType() {
        return GalleryServiceImpl.getInstance(mContext).getGallerySource();
    }

    @Override
    public boolean openTabGallery(int galleryType) {
        LogUtils.d(TAG, "openTabGallery galleryType:" + galleryType);
        GalleryServiceImpl.getInstance(mContext).changeGallerySource(galleryType);
        return true;
    }

    @Override
    public boolean canPictureBrowse() {
        LogUtils.d(TAG, "canPictureBrowse");
        boolean canBrowseFlag;
        boolean isScanning = GalleryServiceImpl.getInstance(mContext).isScanningPhoto();
        if (isScanning) {
            canBrowseFlag = true;
        } else {
            int result = GalleryServiceImpl.getInstance(mContext).openImageBrowsing();
            canBrowseFlag = result != 0;
        }
        LogUtils.d(TAG, "canPictureBrowse canBrowseFlag:" + canBrowseFlag);
        return canBrowseFlag;
    }

    @Override
    public boolean isScanningPicture() {
        LogUtils.d(TAG, "isScanningPicture");
        boolean isScanningFlag = GalleryServiceImpl.getInstance(mContext).isScanningPhoto();
        LogUtils.d(TAG, "isScanningPicture isScanningFlag:" + isScanningFlag);
        return isScanningFlag;
    }

    @Override
    public int isEnablePictureScan() {
        int result = GalleryServiceImpl.getInstance(mContext).openImageBrowsing();
        LogUtils.d(TAG, "isEnablePictureScan result from gallery app:" + result);
        if (result > 0) {
            result = 1;
        }
        LogUtils.d(TAG, "isEnablePictureScan result:" + result);
        return result;
    }

    /**
     * 相册图片列表界面是否可见；VOYAH_COMMON sdk，应该缺少接口
     *
     * @return
     */
    @Override
    public boolean isPictureListVisible() {
        boolean pictureListVisible = GalleryServiceImpl.getInstance(mContext).isScanningPhoto();
        LogUtils.d(TAG, "isPictureListVisible pictureListVisible:" + pictureListVisible);
        return pictureListVisible;
    }

    @Override
    public boolean browsePicture() {
        int galleryType = GalleryServiceImpl.getInstance(mContext).getGallerySource();
        LogUtils.d(TAG, "browsePicture galleryType:" + galleryType);
        GalleryServiceImpl.getInstance(mContext).playPhoto(galleryType);
        return true;
    }

    @Override
    public boolean isBrowsingPicture() {
        boolean browsingFlag = GalleryServiceImpl.getInstance(mContext).isScanningPhoto();
        LogUtils.d(TAG, "isBrowsingPicture browsingFlag:" + browsingFlag);
        return browsingFlag;
    }

    @Override
    public boolean exitBrowsePicture() {
        LogUtils.d(TAG, "exitBrowsePicture");
        GalleryServiceImpl.getInstance(mContext).exitScanPhoto();
        return true;
    }

    @Override
    public boolean isFirstItem() {
        boolean isFirst = GalleryServiceImpl.getInstance(mContext).isFristImage();
        LogUtils.d(TAG, "isFirstItem isFirst:" + isFirst);
        return isFirst;
    }

    @Override
    public boolean isLastItem() {
        boolean isLast = GalleryServiceImpl.getInstance(mContext).isLastImage();
        LogUtils.d(TAG, "isLastItem isLast:" + isLast);
        return isLast;
    }

    /**
     * 切换图片，offset，相对于当前item的便宜量
     *
     * @param offset
     * @return
     */
    @Override
    public boolean setCurrentItem(int offset) {
        LogUtils.d(TAG, "setCurrentItem");
        if (offset < 0) {
            GalleryServiceImpl.getInstance(mContext).scanPrevPhoto();
        } else if (offset > 0) {
            GalleryServiceImpl.getInstance(mContext).scanNextPhoto();
        }
        return true;
    }

    @Override
    public boolean isZoomInMax() {
        boolean flag = GalleryServiceImpl.getInstance(mContext).isZoomInMax();
        LogUtils.d(TAG, "isZoomInMax flag:" + flag);
        return flag;
    }

    @Override
    public int zoomInPicture() {
        int result = GalleryServiceImpl.getInstance(mContext).zoomInPhoto();
        LogUtils.d(TAG, "zoomInPicture result:" + result);
        return result;
    }

    @Override
    public boolean isZoomOutMin() {
        boolean flag = GalleryServiceImpl.getInstance(mContext).isZoomOutMin();
        LogUtils.d(TAG, "isZoomOutMin flag:" + flag);

        return flag;
    }

    @Override
    public int zoomOutPicture() {
        LogUtils.d(TAG, "zoomOutPicture");
        int result = GalleryServiceImpl.getInstance(mContext).zoomOutPhoto();
        LogUtils.d(TAG, "zoomOutPicture result:" + result);
        return result;
    }

    /**
     * @param direction 旋转的方向，1右旋转 ；0左旋转
     * @return
     */
    @Override
    public int rotatePicture(int direction) {
        LogUtils.d(TAG, "rotatePicture direction:" + direction);
        int result = 2;
        if (direction == 1) {
            result = GalleryServiceImpl.getInstance(mContext).rightRotatePhoto();
        } else {
            GalleryServiceImpl.getInstance(mContext).leftRotatePhoto();
            result = 0;
        }
        LogUtils.d(TAG, "rotatePicture result:" + result);
        return result;
    }

    @Override
    public int getGalleryBrowseState() {
        int browseState = GalleryServiceImpl.getInstance(mContext).getCurrentFileType();
        LogUtils.d(TAG, "getGalleryBrowseState browseState:" + browseState);
        return browseState;
    }

}
