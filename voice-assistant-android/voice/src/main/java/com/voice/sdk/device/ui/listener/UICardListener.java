package com.voice.sdk.device.ui.listener;

/**
 * author : jie wang
 * date : 2024/5/10 13:42
 * description :
 */
public interface UICardListener extends IUiStateListener  {

    void onCardItemClick(int position, int itemType, int screenType);
}
