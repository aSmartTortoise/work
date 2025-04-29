package com.voyah.ai.device;


import com.voice.sdk.device.GalleryInterface;
import com.voyah.ai.basecar.BaseGalleryPresenter;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.cockpit.common.gallery.GalleryManager;


/**
 * author : jie wang
 * date : 2025/3/3 21:20
 * description :
 */
public class GalleryInterfaceImpl extends BaseGalleryPresenter {

    private static final String TAG = "GalleryInterfaceImpl";

    private GalleryInterfaceImpl() {
        super();
    }

    static class Holder {
        private static final GalleryInterfaceImpl INSTANCE = new GalleryInterfaceImpl();
    }

    public static GalleryInterfaceImpl getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public void init() {
        LogUtils.i(TAG, "init");
        initSdk();
        GalleryManager.getInstance().init(mContext, (componentName, serviceConnectedFlag) -> {
            if (componentName != null) {
                LogUtils.d(TAG, "onConnectState " + componentName.getClassName());
            }
            LogUtils.d(TAG, "onConnectState serviceConnectedFlag:" + serviceConnectedFlag);
            mServiceConnectedFlag = serviceConnectedFlag;
        });
    }

    @Override
    public boolean openApp() {
        boolean openFlag = false;
        if (mServiceConnectedFlag) {
            beforeOpenApp();
            GalleryManager.getInstance().setGallerySwitch(true);
            openFlag = true;
        }
        LogUtils.d(TAG, "openApp openFlag:" + openFlag);
        return openFlag;
    }

    @Override
    public boolean closeApp() {
        LogUtils.d(TAG, "closeGallery");
        boolean closeFlag = false;
        if (mServiceConnectedFlag) {
            GalleryManager.getInstance().setGallerySwitch(false);
            closeFlag = true;
        }

        return closeFlag;
    }

    @Override
    public boolean isAppForeground() {
        boolean galleryForeground = false;
        if (mServiceConnectedFlag) {
            galleryForeground = GalleryManager.getInstance().getGallerySwitch();
            LogUtils.d(TAG, "isGalleryForeground galleryForeground:" + galleryForeground);
        }
        return galleryForeground;
    }

    @Override
    public boolean isTabGalleryForeground(int galleryType) {
        boolean openFlag = false;
        if (mServiceConnectedFlag) {
            boolean galleryAppForegroundFlag = GalleryManager.getInstance().getGallerySwitch();
            if (galleryAppForegroundFlag) {

                int currentGalleryType = getCurrentGalleryType();
                LogUtils.d(TAG, "isTabGalleryForeground currentGalleryType:" + currentGalleryType);
                if (galleryType == currentGalleryType) {
                    openFlag = true;
                }
            }
        }
        LogUtils.d(TAG, "isTabGalleryForeground galleryType:" + galleryType + " openFlag:" + openFlag);
        return openFlag;
    }

    @Override
    public int getCurrentGalleryType() {
        int currentGalleryType = -1;
        if (mServiceConnectedFlag) {
            currentGalleryType = GalleryManager.getInstance().getGallerySource();
        }
        return currentGalleryType;
    }

    @Override
    public boolean openTabGallery(int galleryType) {
        LogUtils.d(TAG, "openTabGallery galleryType:" + galleryType);
        boolean openFlag = false;
        if (mServiceConnectedFlag) {
            LogUtils.d(TAG, "openTabGallery changeGallerySource");
            GalleryManager.getInstance().changeGallerySource(galleryType);
            openFlag = true;
        }
        return openFlag;
    }

    @Override
    public boolean canPictureBrowse() {
        LogUtils.d(TAG, "canPictureBrowse");
        boolean canBrowseFlag = false;
        if (mServiceConnectedFlag) {
            boolean isScanning = GalleryManager.getInstance().isScanningPhoto();
            if (isScanning) {
                canBrowseFlag = true;
            } else {
                int result = GalleryManager.getInstance().openImageBrowsing();
                if (result == 0) {
                    canBrowseFlag = false;
                } else {
                    canBrowseFlag = true;
                }
            }
        }
        LogUtils.d(TAG, "canPictureBrowse canBrowseFlag:" + canBrowseFlag);
        return canBrowseFlag;
    }

    @Override
    public boolean isScanningPicture() {
        LogUtils.d(TAG, "isScanningPicture");
        boolean isScanningFlag = false;
        if (mServiceConnectedFlag) {
            isScanningFlag = GalleryManager.getInstance().isScanningPhoto();
        }
        LogUtils.d(TAG, "isScanningPicture isScanningFlag:" + isScanningFlag);
        return isScanningFlag;
    }

    @Override
    public int isEnablePictureScan() {
        int result = -1;
        if (mServiceConnectedFlag) {
            result = GalleryManager.getInstance().openImageBrowsing();
            LogUtils.d(TAG, "isEnablePictureScan result from gallery app:" + result);
            if (result > 0) {
                result = 1;
            }
        }
        LogUtils.d(TAG, "isEnablePictureScan result:" + result);
        return result;
    }

    /**
     * 相册图片列表界面是否可见；VOYAH_COMMON sdk，应该缺少接口
     * @return
     */
    @Override
    public boolean isPictureListVisible() {
        LogUtils.d(TAG, "isPictureListVisible");
        boolean pictureListVisible = false;
        if (mServiceConnectedFlag) {
            pictureListVisible = GalleryManager.getInstance().isScanningPhoto();
        }
        return pictureListVisible;
    }

    @Override
    public boolean browsePicture() {
        LogUtils.d(TAG, "browsePicture");
        boolean browseFlag = false;
        if (mServiceConnectedFlag) {
            int galleryType = GalleryManager.getInstance().getGallerySource();
            GalleryManager.getInstance().playPhoto(galleryType);
            browseFlag = true;
        }
        return browseFlag;
    }

    @Override
    public boolean isBrowsingPicture() {
        LogUtils.d(TAG, "isBrowsingPicture");
        boolean browsingFlag = false;
        if (mServiceConnectedFlag) {
            browsingFlag = GalleryManager.getInstance().isScanningPhoto();
        }
        return browsingFlag;
    }

    @Override
    public boolean exitBrowsePicture() {
        LogUtils.d(TAG, "exitBrowsePicture");
        boolean exitFlag = false;
        if (mServiceConnectedFlag) {
            GalleryManager.getInstance().exitScanPhoto();
            exitFlag = true;
        }
        return exitFlag;
    }

    @Override
    public boolean isFirstItem() {
        LogUtils.d(TAG, "isFirstItem");
        boolean isFirst = false;
        if (mServiceConnectedFlag) {
            isFirst = GalleryManager.getInstance().isFristImage();

        }
        return isFirst;
    }

    @Override
    public boolean isLastItem() {
        LogUtils.d(TAG, "isLastItem");
        boolean isLast = false;
        if (mServiceConnectedFlag) {
            isLast = GalleryManager.getInstance().isLastImage();

        }
        return isLast;
    }

    /**
     *  切换图片，offset，相对于当前item的便宜量
     * @param offset
     * @return
     */
    @Override
    public boolean setCurrentItem(int offset) {
        LogUtils.d(TAG, "setCurrentItem");
        boolean isSelected = false;
        if (mServiceConnectedFlag) {
            if (offset < 0) {
                GalleryManager.getInstance().scanPrevPhoto();
                isSelected = true;
            } else if (offset > 0) {
                GalleryManager.getInstance().scanNextPhoto();
                isSelected = true;
            }
        }
        return isSelected;
    }

    @Override
    public boolean isZoomInMax() {
        LogUtils.d(TAG, "isZoomInMax");
        boolean flag = false;
        if (mServiceConnectedFlag) {
            flag = GalleryManager.getInstance().isZoomInMax();

        }
        return flag;
    }

    @Override
    public int zoomInPicture() {
        LogUtils.d(TAG, "zoomInPicture");
        int result = 2;
        if (mServiceConnectedFlag) {
            result = GalleryManager.getInstance().zoomInPhoto();
            LogUtils.d(TAG, "zoomInPicture result:" + result);
        }
        return result;
    }

    @Override
    public boolean isZoomOutMin() {
        LogUtils.d(TAG, "isZoomOutMin");
        boolean flag = false;
        if (mServiceConnectedFlag) {
            flag = GalleryManager.getInstance().isZoomOutMin();

        }
        return flag;
    }

    @Override
    public int zoomOutPicture() {
        LogUtils.d(TAG, "zoomOutPicture");
        int result = 2;
        if (mServiceConnectedFlag) {
            result = GalleryManager.getInstance().zoomOutPhoto();
            LogUtils.d(TAG, "zoomOutPicture result:" + result);
        }
        return result;
    }

    /**
     *
     * @param direction 旋转的方向，1右旋转 ；0左旋转
     * @return
     */
    @Override
    public int rotatePicture(int direction) {
        LogUtils.d(TAG, "rotatePicture direction:" + direction);
        int result = 2;
        if (mServiceConnectedFlag) {
            if (direction == 1) {
                result = GalleryManager.getInstance().rightRotatePhoto();
            } else {
                GalleryManager.getInstance().leftRotatePhoto();
                result = 0;
            }
            LogUtils.d(TAG, "rotatePicture result:" + result);
        }
        return result;
    }

    @Override
    public int getGalleryBrowseState() {
        LogUtils.d(TAG, "getGalleryBrowseState");
        int browseState = 0;
        if (mServiceConnectedFlag) {
            browseState = GalleryManager.getInstance().getCurrentFileType();
        }
        LogUtils.d(TAG, "getGalleryBrowseState browseState:" + browseState);
        return browseState;
    }

}
