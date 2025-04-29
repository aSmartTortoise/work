package com.voyah.h37z;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

import com.voyah.viewcmd.aspect.VoiceViewCmdInit;

/**
 * 可见开启兼容模式，默认响应
 */
@VoiceViewCmdInit(isCompatibleMode = false, processName = "com.voyah.h37z:remote_service")
public class MainApplication extends Application implements ViewModelStoreOwner {

    private static final String TAG = "MainApplication";
    public static MainApplication sApplication;
    private ViewModelStore mViewModelStore = new ViewModelStore();

    public static MainApplication getApplication() {
        return sApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
        //VoiceViewCmdUtils.setLogLevel(BuildConfig.DEBUG ? Log.VERBOSE : Log.DEBUG);
    }

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        return mViewModelStore;
    }
}
