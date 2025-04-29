package com.voyah.ai.basecar.media.utils;

import android.content.Context;

import com.voyah.ai.basecar.helper.MegaDisplayHelper;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.cockpit.vehiclesettings.audiozone.AudioZoneUtils;

public class MediaAudioZoneUtils {
    private static final String TAG = MediaAudioZoneUtils.class.getSimpleName();

    private static MediaAudioZoneUtils mediaAudioZoneUtils;
    private boolean isConnectBtPassengerScreen = false;
    private boolean isConnectBtCeilScreen = false;

    private final int passengerScreenDisplayId = MegaDisplayHelper.getPassengerScreenDisplayId();

    private final int ceilingScreenDisplayId = MegaDisplayHelper.getCeilingScreenDisplayId();
    private Context mContext;

    public static MediaAudioZoneUtils getInstance() {
        if (null == mediaAudioZoneUtils) {
            synchronized (MediaAudioZoneUtils.class) {
                if (null == mediaAudioZoneUtils) {
                    mediaAudioZoneUtils = new MediaAudioZoneUtils();
                }
            }
        }
        return mediaAudioZoneUtils;
    }

    public void init(Context context) {
        mContext = context;
        AudioZoneUtils.getInstance().observeZoneIdChanged(mContext);
        AudioZoneUtils.getInstance().registerZoneIdChangeListener(onZoneIdChangeListener);

        isConnectBtPassengerScreen = AudioZoneUtils.getInstance().getZoneIdByDisplayId(context,passengerScreenDisplayId) == 1;
        isConnectBtCeilScreen = AudioZoneUtils.getInstance().getZoneIdByDisplayId(context,ceilingScreenDisplayId) == 2;
    }

    private final AudioZoneUtils.OnZoneIdChangeListener onZoneIdChangeListener = new AudioZoneUtils.OnZoneIdChangeListener() {
        @Override
        public void onZoneIdChanged(int displayId, int oldZoneId, int newZoneId) {
            LogUtils.i(TAG,"onZoneIdChanged displayId:"+displayId+";newZoneId:"+newZoneId);
             if(displayId == passengerScreenDisplayId){
                 isConnectBtPassengerScreen = newZoneId == 1;
             }
             if(displayId == ceilingScreenDisplayId){
                 isConnectBtCeilScreen = newZoneId == 2;
             }
        }
    };

    public void destroy() {

    }

    public int getZoneIdByDisplayId(int displayId) {
        return AudioZoneUtils.getInstance().getZoneIdByDisplayId(mContext,displayId);
    }

    public boolean isConnectBtByDisplayId(int displayId) {
        if(passengerScreenDisplayId == displayId){
            return isConnectBtPassengerScreen;
        } else if(ceilingScreenDisplayId == displayId){
            return isConnectBtCeilScreen;
        }
        return false;
    }

    public boolean isConnectBtPassengerScreen() {
        return isConnectBtPassengerScreen;
    }

    public boolean isConnectBtCeilScreen() {
        return isConnectBtCeilScreen;
    }

}
