package com.voice.sdk.device;

/**
 * author : jie wang
 * date : 2025/3/3 21:12
 * description :
 */
public interface GalleryInterface extends DomainInterface {

    boolean openApp();

    boolean closeApp();

    boolean isTabGalleryForeground(int galleryType);

    int getCurrentGalleryType();

    boolean openTabGallery(int galleryType);

    boolean canPictureBrowse();

    boolean isScanningPicture();

    int isEnablePictureScan();

    boolean isPictureListVisible();

    boolean browsePicture();

    boolean isBrowsingPicture();

    boolean exitBrowsePicture();

    boolean isFirstItem();

    boolean isLastItem();

    boolean setCurrentItem(int offset);

    boolean isZoomInMax();

    int zoomInPicture();

    boolean isZoomOutMin();

    int zoomOutPicture();

    int rotatePicture(int direction);

    int getGalleryBrowseState();

    String getGalleryName(String tabName);

    boolean isGalleryApp(String pkgName);

    int getGalleryType(String tabName);


}
