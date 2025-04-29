package com.voyah.ai.logic.dc.dvr.listener;

/**
 * @author:lcy
 * @data:2025/3/8
 **/
public interface MyOnTakePhotoListener {
    void onTakePhotoStart();

    void onTakePhotoSuccess();

    void onTakePhotoError(String s);
}
