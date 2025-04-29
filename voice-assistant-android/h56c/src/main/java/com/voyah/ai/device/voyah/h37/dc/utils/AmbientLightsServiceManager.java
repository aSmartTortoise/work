package com.voyah.ai.device.voyah.h37.dc.utils;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.ts.ambientlights.aidl.IAmbientLightsService;
import com.ts.ambientlights.aidl.ILightsSettingListener;

public class AmbientLightsServiceManager {
    private static final String TAG = "AmbientLightsServiceManager";
    private static AmbientLightsServiceManager instance = null;
    private static AmbientLightsServiceManager.IServiceConnectedListener sConnectedListener;
    private Context mContext = null;
    private IAmbientLightsService mAidl;
    public static final int VM_val_minValue_zero = 0;
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("AmbientLightsServiceManager", "Service connected, name:" + name);
            AmbientLightsServiceManager.this.mAidl = IAmbientLightsService.Stub.asInterface(service);
            if (null != AmbientLightsServiceManager.sConnectedListener) {
                AmbientLightsServiceManager.sConnectedListener.onServiceConnected();
            } else {
                Log.d("AmbientLightsServiceManager", "listener is null");
            }

        }

        public void onServiceDisconnected(ComponentName name) {
            Log.d("AmbientLightsServiceManager", "Service disconnected, name:" + name);
            AmbientLightsServiceManager.this.mAidl = null;
            if (null != AmbientLightsServiceManager.sConnectedListener) {
                AmbientLightsServiceManager.sConnectedListener.onServiceDisconnect();
            } else {
                Log.d("AmbientLightsServiceManager", "listener is null");
            }

        }
    };

    public boolean isConnected() {
        return mAidl != null;
    }

    public static AmbientLightsServiceManager getInstance(Context context, AmbientLightsServiceManager.IServiceConnectedListener connectedListener) {
        Log.d("AmbientLightsServiceManager", "start to get AmbientLightsServiceManager instance");
        sConnectedListener = connectedListener;
        if (instance == null) {
            Class var2 = AmbientLightsServiceManager.class;
            synchronized(AmbientLightsServiceManager.class) {
                if (instance == null) {
                    instance = new AmbientLightsServiceManager(context);
                }
            }
        }

        Log.d("AmbientLightsServiceManager", "finish to get AmbientLightsServiceManager instance");
        return instance;
    }

    private AmbientLightsServiceManager(Context context) {
        Log.d("AmbientLightsServiceManager", "create AmbientLightsServiceManager");
        this.mContext = context;
    }

    public void release() {
        Log.d("AmbientLightsServiceManager", "release AmbientLightsServiceManager");
    }

    @SuppressLint("WrongConstant")
    public void startBindService() {
        Log.d("AmbientLightsServiceManager", "startBindService Service");
        Intent intent = new Intent();
        intent.setAction("ts.action.AmbientLightsService");
        intent.setPackage("com.ts.ambientlightsapplication");
        this.mContext.bindService(intent, this.mConnection, 1);
    }

    public boolean Set_dynamicLightsStatus_ARS(boolean state) throws RemoteException {
        Log.d("AmbientLightsServiceManager", "Set_dynamicLightsStatus_ARS");
        return this.mAidl != null ? this.mAidl.Set_dynamicLightsStatus_ARS(state) : false;
    }

    public boolean Set_lightsStatus_ARS(boolean state) throws RemoteException {
        Log.d("AmbientLightsServiceManager", "Set_lightsStatus_ARS");
        return this.mAidl != null ? this.mAidl.Set_lightsStatus_ARS(state) : false;
    }

    public boolean Set_lightsBrightness_ARS(int brightness) throws RemoteException {
        Log.d("AmbientLightsServiceManager", "Set_lightsBrightness_ARS");
        if (brightness <= 10 && brightness >= 1) {
            return this.mAidl != null ? this.mAidl.Set_lightsBrightness_ARS(brightness) : false;
        } else {
            Log.d("AmbientLightsServiceManager", "Set_lightsBrightness_ARS brightness out of range" + brightness);
            return false;
        }
    }

    public boolean Set_lightsColour_ARS(int colour) throws RemoteException {
        Log.d("AmbientLightsServiceManager", "Set_lightsColour_ARS");
        if (colour <= 128 && colour > 0) {
            return this.mAidl != null ? this.mAidl.Set_lightsColour_ARS(colour) : false;
        } else {
            Log.d("AmbientLightsServiceManager", "Set_lightsColour_ARS out of range :" + colour);
            return false;
        }
    }

    public boolean Set_drivingModeStatus_ARS(boolean state) throws RemoteException {
        Log.d("AmbientLightsServiceManager", "Set_drivingModeStatus_ARS");
        return this.mAidl != null ? this.mAidl.Set_drivingModeStatus_ARS(state) : false;
    }

    public int Get_lightsBrightness_ARS() throws RemoteException {
        Log.d("AmbientLightsServiceManager", "Get_lightsBrightness_ARS");
        return this.mAidl != null ? this.mAidl.Get_lightsBrightness_ARS() : -1;
    }

    public int Get_lightsColour_ARS() throws RemoteException {
        Log.d("AmbientLightsServiceManager", "Get_lightsColour_ARS");
        return this.mAidl != null ? this.mAidl.Get_lightsColour_ARS() : -1;
    }

    public boolean Get_lightsStatus_ARS() throws RemoteException {
        Log.d("AmbientLightsServiceManager", "Get_lightsStatus_ARS");
        return this.mAidl != null ? this.mAidl.Get_lightsStatus_ARS() : false;
    }

    public int Get_lightsMode_ARS() throws RemoteException {
        Log.d("AmbientLightsServiceManager", "Get_lightsMode_ARS");
        return this.mAidl != null ? this.mAidl.Get_lightsMode_ARS() : -1;
    }

    public boolean Set_voiceAssistantFeedbackStatus_ARS(boolean state) throws RemoteException {
        Log.d("AmbientLightsServiceManager", "Set_voiceAssistantFeedbackStatus_ARS");
        return this.mAidl != null ? this.mAidl.Set_voiceAssistantFeedbackStatus_ARS(state) : false;
    }

    public boolean Set_voiceSpeakStatus_ARS(boolean state) throws RemoteException {
        Log.d("AmbientLightsServiceManager", "Set_voiceSpeakStatus_ARS");
        return this.mAidl != null ? this.mAidl.Set_voiceSpeakStatus_ARS(state) : false;
    }

    public boolean Set_colourSelectType_ARS(int type) throws RemoteException {
        Log.d("AmbientLightsServiceManager", "Set_colourSelectType_ARS");
        if (type <= 5 && type >= 1) {
            return this.mAidl != null ? this.mAidl.Set_colourSelectType_ARS(type) : false;
        } else {
            Log.d("AmbientLightsServiceManager", "Set_colourSelectType_ARS out of range :" + type);
            return false;
        }
    }

    public int Get_colourSelectType_ARS() throws RemoteException {
        Log.d("AmbientLightsServiceManager", "Get_colourSelectType_ARS");
        return this.mAidl != null ? this.mAidl.Get_colourSelectType_ARS() : -1;
    }

    public boolean registerCallback(ILightsSettingListener listener) throws RemoteException {
        Log.d("AmbientLightsServiceManager", "registerCallback");
        return this.mAidl != null && listener != null ? this.mAidl.registerCallback(listener) : false;
    }

    public boolean unregisterCallback(ILightsSettingListener listener) throws RemoteException {
        Log.d("AmbientLightsServiceManager", "unregisterCallback");
        return this.mAidl != null && listener != null ? this.mAidl.unregisterCallback(listener) : false;
    }

    public boolean Set_gameModeStatus_ARS(boolean state) throws RemoteException {
        Log.d("AmbientLightsServiceManager", "Set_gameModeStatus_ARS");
        return this.mAidl != null ? this.mAidl.Set_gameModeStatus_ARS(state) : false;
    }

    public boolean Set_gameMode_ARS(int mode) throws RemoteException {
        Log.d("AmbientLightsServiceManager", "Set_gameMode_ARS");
        if (mode <= 2 && mode >= 0) {
            return this.mAidl != null ? this.mAidl.Set_gameMode_ARS(mode) : false;
        } else {
            Log.d("AmbientLightsServiceManager", "Set_gameMode_ARS out of range :" + mode);
            return false;
        }
    }

    public int Get_gameMode_ARS() throws RemoteException {
        Log.d("AmbientLightsServiceManager", "Get_gameMode_ARS");
        return this.mAidl != null ? this.mAidl.Get_gameMode_ARS() : -1;
    }

    public boolean Set_dynamicRhythmReq(boolean state) throws RemoteException {
        Log.d("AmbientLightsServiceManager", "Set_dynamicRhythmReq");
        return this.mAidl != null ? this.mAidl.Set_dynamicRhythmReq(state) : false;
    }

    public boolean Set_lightsMode_ARS(int mode) {
        Log.d("AmbientLightsServiceManager", "Set_lightsMode_ARS");
        boolean istrue = false;

        try {
            if (this.mAidl != null) {
                istrue = this.mAidl.Set_lightsMode_ARS(mode);
            } else {
                istrue = false;
            }
        } catch (RemoteException var4) {
            var4.printStackTrace();
        }

        return istrue;
    }

    public boolean Set_themeColor_ARS(int theme) throws RemoteException {
        Log.d("AmbientLightsServiceManager", "Set_themeColor_ARS");
        if (theme >= 0 && theme <= 9) {
            return this.mAidl != null ? this.mAidl.Set_themeColor_ARS(theme) : false;
        } else {
            Log.e("AmbientLightsServiceManager", "Theme is illegal");
            return false;
        }
    }

    public int Get_themeColor_ARS() throws RemoteException {
        Log.d("AmbientLightsServiceManager", "Get_themeColor_ARS");
        return this.mAidl != null ? this.mAidl.Get_themeColor_ARS() : -1;
    }

    public boolean Set_musicRhythm_ARS(int colour) throws RemoteException {
        Log.d("AmbientLightsServiceManager", "Set_musicRhythm_ARS");
        if (colour <= 128 && colour > 0) {
            return this.mAidl != null ? this.mAidl.Set_musicRhythm_ARS(colour) : false;
        } else {
            Log.d("AmbientLightsServiceManager", "Set_musicRhythm_ARS out of range:" + colour);
            return false;
        }
    }

    public int Get_musicRhythm_ARS() throws RemoteException {
        Log.d("AmbientLightsServiceManager", "Get_musicRhythm_ARS");
        return this.mAidl != null ? this.mAidl.Get_musicRhythm_ARS() : -1;
    }

    public int Get_StaticOrDynamic_ARS() throws RemoteException {
        Log.d("AmbientLightsServiceManager", "Get_StaticOrDynamic_ARS");
        return this.mAidl != null ? this.mAidl.Get_StaticOrDynamic_ARS() : -1;
    }

    public int Get_StaticMode_ARS() throws RemoteException {
        Log.d("AmbientLightsServiceManager", "Get_StaticMode_ARS");
        return this.mAidl != null ? this.mAidl.Get_StaticMode_ARS() : -1;
    }

    public int Get_DynamicMode_ARS() throws RemoteException {
        Log.d("AmbientLightsServiceManager", "Get_StaticMode_ARS");
        return this.mAidl != null ? this.mAidl.Get_DynamicMode_ARS() : -1;
    }

    public boolean Set_SceneLightStatus_ARS(int status) throws RemoteException {
        Log.d("AmbientLightsServiceManager", "Set_SceneLightStatus_ARS");
        if (status == 2 && status == 1) {
            return this.mAidl != null ? this.mAidl.Set_SceneLightStatus_ARS(status) : false;
        } else {
            Log.e("AmbientLightsServiceManager", "SceneLightStatus is illegal");
            return false;
        }
    }

    public int Get_SceneLightStatus_ARS() throws RemoteException {
        Log.d("AmbientLightsServiceManager", "Get_SceneLightStatus_ARS");
        return this.mAidl != null ? this.mAidl.Get_SceneLightStatus_ARS() : -1;
    }

    public boolean Set_ClothLinkageStatus_ARS(boolean status) throws RemoteException {
        Log.d("AmbientLightsServiceManager", "Set_ClothLinkageStatus_ARS");
        return this.mAidl != null ? this.mAidl.Set_ClothLinkageStatus_ARS(status) : false;
    }

    public int Get_ClothLinkageStatus_ARS() throws RemoteException {
        Log.d("AmbientLightsServiceManager", "Get_ClothLinkageStatus_ARS");
        return this.mAidl != null ? this.mAidl.Get_ClothLinkageStatus_ARS() : -1;
    }

    public boolean Set_ClothLinkagePriority_ARS(int location) throws RemoteException {
        Log.d("AmbientLightsServiceManager", "Set_ClothLinkageStatus_ARS");
        return this.mAidl != null ? this.mAidl.Set_ClothLinkagePriority_ARS(location) : false;
    }

    public int Get_ClothLinkagePriority_ARS() throws RemoteException {
        Log.d("AmbientLightsServiceManager", "Get_ClothLinkageStatus_ARS");
        return this.mAidl != null ? this.mAidl.Get_ClothLinkagePriority_ARS() : -1;
    }

    //新增获取静态氛围灯模式多色的接口
    public int Get_staticMultipleColor_ARS() throws RemoteException {
        Log.d("AmbientLightsServiceManager", "Get_staticMultipleColor_ARS");
        return this.mAidl != null ? this.mAidl.Get_staticMultipleColor_ARS() : -1;
    }

    //新增设置静态氛围灯模式多色的接口
    public boolean Set_staticMultipleColor_ARS(int status) throws RemoteException {
        Log.d("AmbientLightsServiceManager", "Set_staticMultipleColor_ARS");
        return this.mAidl != null ? this.mAidl.Set_staticMultipleColor_ARS(status) : false;
    }

    //新增获取动态氛围灯当前亮度的接口
    public int Get_lightsBrightnessDynamic_ARS() throws RemoteException {
        Log.d("AmbientLightsServiceManager", "Get_lightsBrightnessDynamic_ARS");
        return this.mAidl != null ? this.mAidl.Get_lightsBrightnessDynamic_ARS() : -1;
    }

    //新增获取动态氛围灯当前亮度的接口
    public boolean Set_lightsBrightnessDynamic_ARS(int status) throws RemoteException {
        Log.d("AmbientLightsServiceManager", "Set_lightsBrightnessDynamic_ARS");
        return this.mAidl != null ? this.mAidl.Set_lightsBrightnessDynamic_ARS(status) : false;
    }

    public interface IServiceConnectedListener {
        void onServiceConnected();

        void onServiceDisconnect();
    }
}
