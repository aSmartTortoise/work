package com.voice.sdk.device;

import android.content.Context;

/**
 * author : jie wang
 * date : 2024/4/10 19:29
 * description : 业务功能Presenter接口
 */
public interface IDomainPresenter {

    void init(Context context);

    boolean isAppForeground(String pkg);

    void openApp(String pkgName);


    void backToLauncher();

    void closeApp(String pkgName);
}
