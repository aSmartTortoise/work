package com.voyah.ai.logic.dc.dvr.listener;

/**
 * @author:lcy
 * @data:2025/3/8
 **/
public interface MyOnPreviewChangeListener {

    void onPreviewChangeStart();

    void onPreviewChangeSuccess();

    void onPreviewChangeError(String s);
}
