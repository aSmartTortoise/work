package com.voice.drawing.api;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.voice.drawing.api.model.APIConfig;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * author : jie wang
 * date : 2025/1/25 14:02
 * description :
 */
public class UserServiceHelper {

    private static final String TAG = "UserServiceHelper";

    private final Context mContext;
    private final APIConfig mApiConfig;
    private final DrawingAPIManager.DrawingAPIManagerCallback mCallback;

    private IDrawingAPI mDrawingAPI;
    private Handler mHandler;

    private final IDrawingAPICallback mBinderCallback = new IDrawingAPICallback.Stub() {

        @Override
        public void startDrawing() throws RemoteException {
            Log.d(TAG, "startDrawing");
            if (mCallback != null) {
                mCallback.startDrawing();
            }
        }

        @Override
        public void redraw(String prompt) throws RemoteException {
            Log.d(TAG, "redraw: prompt:" + prompt);
            if (mCallback != null) {
                mCallback.redraw(prompt, mApiConfig.getUserHandle());
            }
        }
    };

    private final IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {

        @Override
        public void binderDied() {
            Log.d(TAG, "binderDied mDrawingAPI:" + mDrawingAPI);

            if (mDrawingAPI != null) {
                boolean result = mDrawingAPI.asBinder().unlinkToDeath(this, 0);
                Log.d(TAG, "binderDied: unlink to death result:" + result);

                try {
                    mDrawingAPI.unRegisterDrawingAPICallback(mBinderCallback);
                } catch (RemoteException e) {
                    Log.w(TAG, "onServiceDisconnected: unRegisterDrawingAPICallback error:" + e);
                } finally {
                    mDrawingAPI = null;
                    mApiConfig.setDrawingAPI(null);
                }
            }

            if (mCallback != null) {
                mCallback.onBinderDied();
            }

            bindServiceNew();
        }
    };

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "onServiceConnected: mUserHandle:" + mApiConfig.getUserHandle());
            mDrawingAPI = IDrawingAPI.Stub.asInterface(service);
            Log.i(TAG, "onServiceConnected: mDrawingAPI:" + mDrawingAPI);
            try {
                if (mDrawingAPI != null) {
                    mApiConfig.setDrawingAPI(mDrawingAPI);
                    mDrawingAPI.asBinder().linkToDeath(mDeathRecipient, 0);
                    mDrawingAPI.registerDrawingAPICallback(mBinderCallback);
                }

                if (mCallback != null) {
                    mCallback.onServiceBind();
                }
            } catch (RemoteException e) {
                Log.w(TAG, "onServiceConnected: registerReceiveCallback error:" + e);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "onServiceDisconnected: mUserHandle:" + mApiConfig.getUserHandle());
            Log.i(TAG, "onServiceDisconnected: mDrawingAPI:" + mDrawingAPI);

            if (mCallback != null) {
                mCallback.onServiceDisconnected();
            }
        }
    };

    public UserServiceHelper(Context context, @NonNull APIConfig apiConfig) {
        this.mContext = context;
        this.mApiConfig = apiConfig;
        mCallback = apiConfig.getCallback();
        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());
    }

    public void bindService() {
        UserHandle userHandle = mApiConfig.getUserHandle();
        Log.i(TAG, "bindService userHandle:" + userHandle);
        if (userHandle == null) {
            Log.w(TAG, "bindService: illegal argument userHandle must not null.");
            return;
        }

        Intent intent = new Intent();
        intent.setAction("com.voyah.voice.drawing.DRAWING_SERVICE");
        intent.setPackage("com.voyah.voice.drawing");

        try {
            boolean result = mContext.bindServiceAsUser(
                    intent,
                    mServiceConnection,
                    Context.BIND_AUTO_CREATE,
                    userHandle);
            Log.i(TAG, "bindService: result:" + result);
        } catch (Exception e) {
            Log.w(TAG, "bindService: bind remote service error, e:" + e);
        }
    }

    public void bindServiceNew() {
        Log.d(TAG, "bindService");
        UserHandle userHandle = mApiConfig.getUserHandle();
        Log.i(TAG, "bindService userHandle:" + userHandle);
        if (userHandle == null) {
            Log.w(TAG, "bindService: illegal argument userHandle must not null.");
            return;
        }
        AtomicInteger count = new AtomicInteger();
        Runnable binderServiceTask = new Runnable() {
            @Override
            public void run() {
                boolean bindFlag = checkAPIServiceBind();
                Log.d(TAG, "bindService: bindFlag:" + bindFlag);

                if (!bindFlag) {
                    Intent intent = new Intent();
                    intent.setAction("com.voyah.voice.drawing.DRAWING_SERVICE");
                    intent.setPackage("com.voyah.voice.drawing");
                    try {
                        boolean result = mContext.bindServiceAsUser(
                                intent,
                                mServiceConnection,
                                Context.BIND_AUTO_CREATE,
                                userHandle);
                        Log.d(TAG, "bindService: result:" + result);
                    } catch (Exception e) {
                        Log.d(TAG, "bindService: bind remote service error, e:" + e);
                    }
                    count.getAndIncrement();
                    Log.d(TAG, "bindService: bind service count:" + count);
                    if (count.get() < 5) {
                        mHandler.postDelayed(this, 2000);
                    } else {
                        mHandler.removeCallbacks(this);
                    }
                } else {
                    mHandler.removeCallbacks(this);
                }
            }
        };
        mHandler.post(binderServiceTask);
    }

    private boolean checkAPIServiceBind() {
        return mDrawingAPI != null && mDrawingAPI.asBinder().isBinderAlive();
    }
}
