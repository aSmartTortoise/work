package com.voyah.ai.sdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;

import com.voyah.ai.sdk.listener.IVAReadyListener;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class AbsBinderConnector {

    private final String TAG;
    protected Context context;
    protected String mPackageName;
    protected final Set<IVAReadyListener> mReadyListeners = new CopyOnWriteArraySet<>();
    protected RemoteSpeechReadyImpl remoteReadyImpl;
    protected final Handler handler = new Handler(Looper.getMainLooper());
    private PackageReceiver mReceiver;
    protected final String id;

    public AbsBinderConnector(Context context) {
        this.context = context;
        this.mPackageName = context.getPackageName();
        this.TAG = getTAG();
        this.id = getAppUID(context) + "_" + android.os.Process.myPid();
    }

    private String getTAG() {
        if (this instanceof AssistantBinderConnector) {
            return AssistantBinderConnector.TAG;
        } else if (this instanceof TtsBinderConnector) {
            return TtsBinderConnector.TAG;
        } else {
            return AbsBinderConnector.class.getSimpleName();
        }
    }

    /**
     * 添加就绪监听器
     *
     * @param listener 监听器
     */
    public void addOnReadyListener(final IVAReadyListener listener) {
        Log.d(TAG, "addOnReadyListener() called with: listener = [" + listener + "]");
        if (listener != null) {
            mReadyListeners.add(listener);
        }
    }

    /**
     * 移除就绪监听器
     *
     * @param listener 监听器
     */
    public void removeOnReadyListener(final IVAReadyListener listener) {
        Log.d(TAG, "removeOnReadyListener() called with: listener = [" + listener + "]");
        mReadyListeners.remove(listener);
    }

    public void executeRemoteReady() {
        if (mReadyListeners.size() > 0) {
            Log.d(TAG, "executeRemoteReady listener");
            for (IVAReadyListener listener : mReadyListeners) {
                listener.onSpeechReady();
            }
        } else {
            Log.w(TAG, "mReadyListeners is empty!");
        }
    }

    /**
     * 远程服务就绪监听
     */
    public class RemoteSpeechReadyImpl extends IVAReadyCallback.Stub {

        @Override
        public void onSpeechReady() throws RemoteException {
            Log.d(TAG, "onSpeechReady");
            if (isBinderAlive()) {
                executeRemoteReady();
            } else {
                Log.d(TAG, "onSpeechReady: binder is not ready");
            }
        }
    }

    public void bindService() {
        registerPackageReceiver();
    }

    public void unBindService() {
        if (mReceiver != null) {
            context.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    public abstract boolean isAlive();

    public abstract boolean isAppReInstalled(String pkgName);

    private void registerPackageReceiver() {
        if (mReceiver == null) {
            mReceiver = new PackageReceiver();
            IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
            filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
            filter.addDataScheme("package");
            context.registerReceiver(mReceiver, filter);
        }
    }

    private class PackageReceiver extends BroadcastReceiver {
        private final String TAG_APP = TAG + "_PackageReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_PACKAGE_CHANGED.equals(action) || Intent.ACTION_PACKAGE_ADDED.equals(action)) {
                Uri data = intent.getData();
                if (data != null) {
                    String packageName = data.getSchemeSpecificPart();
                    if (isAppReInstalled(packageName)) {
                        Log.d(TAG_APP, packageName + " is changed, bindService again");
                        handler.removeCallbacksAndMessages(null);
                        handler.postDelayed(AbsBinderConnector.this::bindService, 2000);
                    }
                }
            }
        }
    }

    private int getAppUID(Context context) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            return appInfo.uid;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
