package com.voice.drawing.api;

import android.content.Context;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.voice.drawing.api.model.APIConfig;

import java.util.ArrayList;
import java.util.List;


/**
 * author : jie wang
 * date : 2024/3/7 15:29
 * description : 负责与voice ui process ipc的类。
 */
public class DrawingAPIManager {

    private static final String TAG = "DrawingAPIManager";

    private Context mContext;


    private List<APIConfig> mApiConfigList;


    private DrawingAPIManager() {

    }

    public static DrawingAPIManager getInstance() {
        return Holder.INSTANCE;
    }

    public void init(Context context) {
        if (mContext == null) {
            mContext = context;
        }
    }

    public void init(Context context, @NonNull List<APIConfig> apiConfigs) {
        Log.d(TAG, "init: apiConfigs size:" + apiConfigs.size());
        if (mContext == null) {
            mContext = context;
        }
        for (APIConfig apiConfig: apiConfigs) {
            if (apiConfig == null) {
                Log.w(TAG, "init: illegal argument, apiConfig is null.");
            } else {
                UserHandle userHandle = apiConfig.getUserHandle();
                Log.i(TAG, "init userHandle:" + userHandle);
                if (userHandle == null) {
                    Log.w(TAG, "init: illegal argument userHandle must not null.");
                    return;
                }

                if (mApiConfigList == null) {
                    mApiConfigList = new ArrayList<>();
                }
                mApiConfigList.add(apiConfig);
                UserServiceHelper serviceHelper = new UserServiceHelper(context, apiConfig);
                serviceHelper.bindServiceNew();
            }
        }
    }

    public int openApp(int displayId, UserHandle userHandle) {
        Log.i(TAG, "openApp: displayId:" + displayId);
        int result = APIResult.NO_RESOURCE;
        IDrawingAPI drawingAPI = getDrawingAPIBinder(userHandle);
        if (drawingAPI != null) {
            try {
                result = drawingAPI.openAppAsDisplayId(displayId);
            } catch (RemoteException e) {
                result = APIResult.ERROR;
                Log.d(TAG, "openApp: error:" + e);
            }
        }

        return result;
    }



    public int closeApp() {
        int result = APIResult.NO_RESOURCE;

//        if (mDrawingAPI != null) {
//            try {
//                result = mDrawingAPI.closeApp();
//            } catch (RemoteException e) {
//                result = APIResult.ERROR;
//                Log.d(TAG, "closeApp: error:" + e);
//            }
//        } else {
//            result = APIResult.NO_RESOURCE;
//        }
        return result;
    }

    public int closeApp(int displayId, UserHandle userHandle) {
        Log.i(TAG, "closeApp: displayId:" + displayId + " userHandle:" + userHandle);
        int result = APIResult.NO_RESOURCE;
        IDrawingAPI drawingAPI = getDrawingAPIBinder(userHandle);
        if (drawingAPI != null) {
            try {
                result = drawingAPI.closeApp();
            } catch (RemoteException e) {
                result = APIResult.ERROR;
                Log.d(TAG, "closeApp: error:" + e);
            }
        }
        return result;
    }

    public boolean isAppForeground(int displayId, UserHandle userHandle) {
        Log.i(TAG, "isAppForeground: displayId:" + displayId + " userHandle:" + userHandle);
        int result = APIResult.NO_RESOURCE;
        IDrawingAPI drawingAPI = getDrawingAPIBinder(userHandle);
        if (drawingAPI != null) {
            try {
                result = drawingAPI.isAppForeground();
            } catch (RemoteException e) {
                result = APIResult.ERROR;
                Log.d(TAG, "isAppForeground: error:" + e);
            }
        }
        return result == APIResult.UI_FOREGROUND;
    }

    public void postDrawingState(String drawingStateJson, int displayId, UserHandle userHandle) {
        Log.i(TAG, "postDrawingState: displayId:" + displayId + " userHandle:" + userHandle);
        IDrawingAPI drawingAPI = getDrawingAPIBinder(userHandle);
        if (drawingAPI != null) {
            try {
                drawingAPI.postDrawingStateAsDisplayId(drawingStateJson, displayId);
            } catch (RemoteException e) {
                Log.w(TAG, "postDrawingState: error:" + e);
            }
        } else {
            Log.w(TAG, "postDrawingState: drawingAPI is null");
        }
    }

    private IDrawingAPI getDrawingAPIBinder(UserHandle userHandle) {
        IDrawingAPI drawingAPI = null;
        if (mApiConfigList != null && !mApiConfigList.isEmpty()) {
            for (APIConfig apiConfig: mApiConfigList) {
                if (apiConfig.getUserHandle() == userHandle) {
                    drawingAPI = apiConfig.getDrawingAPI();
                    Log.i(TAG, "getDrawingAPIBinder: userHandle:" + userHandle);
                    break;
                }
            }
        }
        return drawingAPI;
    }

    public interface DrawingAPIManagerCallback {
        void onServiceBind();

        void onServiceDisconnected();

        void onBinderDied();

        void startDrawing();

        void redraw(String prompt);

        void redraw(String prompt, UserHandle userHandle);

    }

    private static class Holder {
        private static final DrawingAPIManager INSTANCE = new DrawingAPIManager();
    }
}
