package com.voyah.ai.basecar.media;

import com.voice.sdk.device.media.MediaPageInterface;
import com.voice.sdk.model.UIPageInfo;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.cockpit.window.WindowMessageManager;
import com.voyah.cockpit.window.model.PageInfo;
import com.voyah.cockpit.window.model.ViewType;

/**
 * author : jie wang
 * date : 2025/3/7 11:17
 * description :
 */
public class MediaPageInterfaceImpl implements MediaPageInterface {
    private static final String TAG = "MediaPageInterfaceImpl";

    private MediaPageInterfaceImpl() {
    }

    public static MediaPageInterfaceImpl getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public UIPageInfo getCurrentPage(int screenType) {
        LogUtils.i(TAG, "getCurrentPage screenType:" + screenType);
        PageInfo pageInfo = WindowMessageManager.getInstance().getCurrentPage(screenType);
        return new UIPageInfo(pageInfo.getPosition(),
                pageInfo.getItemCount(),
                pageInfo.getMaxItemCount());
    }

    @Override
    public boolean setCurrentPage(int pageIndex, int screenType) {
        LogUtils.i(TAG, "setCurrentPage pageIndex:" + pageIndex + " screenType:" + screenType);
        boolean result = WindowMessageManager
                .getInstance()
                .setCurrentPage(pageIndex, screenType);
        LogUtils.i(TAG, "setCurrentPage result:" + result);
        return result;
    }

    @Override
    public String getCurrentCardType(int screenType) {
        return WindowMessageManager.getInstance().getCurrentCardType(screenType);
    }

    @Override
    public boolean isViewType(int itemType) {
        return itemType == ViewType.MEDIA_TYPE;
    }

    @Override
    public boolean isMusicType(int itemType) {
        return itemType == ViewType.MUSIC_TYPE;
    }

    private static class Holder {
        private static final MediaPageInterfaceImpl INSTANCE = new MediaPageInterfaceImpl();
    }
}
