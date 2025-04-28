package com.voyah.cockpit.window;

import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.voyah.cockpit.window.util.BundleUtil;

import java.io.File;
import java.io.FileNotFoundException;

public abstract class IWindowExtender {
    public static final String TAG = "IWindowExtender";

    public Bundle call(String method, Bundle bundle) {
        return WindowMessageManager.getInstance().call(method, bundle);
    }

    public void callAsync(String method, Bundle bundle, ICallback callback) {
        WindowMessageManager.getInstance().callAsync(method, bundle, callback);
    }

    public void register(String method, ICallback callback) {
        WindowMessageManager.getInstance().register(method, callback);
    }

    public void unregister(String method) {
        WindowMessageManager.getInstance().unRegister(method);
    }

    public void sendFile(String method, String filePath, String tag, ICallback callback) {
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        ParcelFileDescriptor pfd = null;
        try {
            pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            Bundle bundle = new BundleUtil().builder().put("fileDescriptor", pfd)
                    .put("fileName", file.getName())
                    .put("fileSize", file.length())
                    .put("tag", tag)
                    .build();
            callAsync(method, bundle, callback);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "sendFile: ", e);
        }
    }

    /**
     * callback:
     * Bundle{result=ParcelFileDescriptor}
     */
    public void getFile(String filePath, ICallback callback) {
        Bundle bundle = new BundleUtil().builder()
                .put("filePath", filePath)
                .build();
        callAsync("getFile", bundle, callback);
    }

    /**
     * callback:
     * like: Bundle{result=ParcelFileDescriptor,fileType="AI_SCREEN_PICTURE"}
     */
    public void registerFileUpdate(ICallback callback) {
        register("fileListener", callback);
    }

    public void unregisterFileUpdate() {
        unregister("fileListener");
    }

    /**
     * callback:
     * like: Bundle{result="click", domain="map", action="search#beijing"}
     */
    public void registerUserAction(ICallback callback) {
        register("userAction", callback);
    }

    public void unregisterUserAction() {
        unregister("userAction");
    }

}
