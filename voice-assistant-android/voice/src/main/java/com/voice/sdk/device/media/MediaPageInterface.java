package com.voice.sdk.device.media;

import com.voice.sdk.model.UIPageInfo;

/**
 * author : jie wang
 * date : 2025/3/7 11:22
 * description :
 */
public interface MediaPageInterface {

    UIPageInfo getCurrentPage(int screenType);

    boolean setCurrentPage(int pageIndex, int screenType);

    String getCurrentCardType(int screenType);

    boolean isViewType(int itemType);

    boolean isMusicType(int itemType);

}
