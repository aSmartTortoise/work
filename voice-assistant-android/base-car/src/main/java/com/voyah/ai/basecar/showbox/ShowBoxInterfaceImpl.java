package com.voyah.ai.basecar.showbox;

import android.content.Context;
import android.os.RemoteException;

import com.voice.sdk.device.ShowBoxInterface;
import com.voice.sdk.constant.ApplicationConstant;
import com.voyah.ai.common.utils.ContextUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.voyah.showboxsdk.manager.AidlConstants;
import com.voyah.showboxsdk.manager.AidlServiceManager;
import com.voyah.showboxsdk.manager.ResultCode;

/**
 * author : jie wang
 * date : 2025/3/3 14:46
 * description :
 */
public class ShowBoxInterfaceImpl implements ShowBoxInterface {

    private static final String TAG = "ShowBoxInterfaceImpl";

    private boolean mServiceConnectedFlag = false;
    private Context mContext;

    private ShowBoxInterfaceImpl() {
    }

    public static ShowBoxInterfaceImpl getInstance() {
        return ShowBoxInterfaceImpl.Holder.INSTANCE;
    }

    @Override
    public void init() {
        mContext = ContextUtils.getAppContext();
        AidlServiceManager.getInstance(mContext).init(
                new AidlServiceManager.IServiceConnectedListener() {
                    @Override
                    public void onServiceConnected() {
                        LogUtils.d(TAG, "onServiceConnected");
                        mServiceConnectedFlag = true;
                    }

                    @Override
                    public void onServiceDisconnected() {
                        LogUtils.d(TAG, "onServiceDisconnected");
                        mServiceConnectedFlag = false;
                    }
                });
    }

    @Override
    public int openApp() {
        LogUtils.d(TAG, "openApp");
        int result = -1;
        if (mServiceConnectedFlag) {
            beforeOpenApp();
            try {
                result = AidlServiceManager.getInstance(mContext).openApp(0);
                LogUtils.d(TAG, "openApp result:" + result);
            } catch (RemoteException e) {
                LogUtils.w(TAG, "openApp e:" + e);
            }
        }
        return result;
    }

    @Override
    public int closeApp() {
        LogUtils.d(TAG, "closeApp");
        int result = -1;
        if (mServiceConnectedFlag) {
            try {
                result = AidlServiceManager.getInstance(mContext).closeApp();
                LogUtils.d(TAG, "closeApp result:" + result);
            } catch (RemoteException e) {
                LogUtils.w(TAG, "openApp e:" + e);
            }
        }
        return result;
    }

    @Override
    public boolean isAppForeground() {
        boolean appForegroundFlag = false;
        if (mServiceConnectedFlag) {
            try {
                appForegroundFlag = AidlServiceManager.getInstance(mContext).isAppOpened();
                LogUtils.d(TAG, "isAppForeground appForegroundFlag:" + appForegroundFlag);
            } catch (RemoteException e) {
                LogUtils.w(TAG, "isAppForeground e:" + e);
            }
        }
        return appForegroundFlag;
    }

    @Override
    public boolean isShowBoxTabForeground(String tabType) {
        boolean showBoxTabForegroundFlag = false;
        if (mServiceConnectedFlag) {
            switch (tabType) {
                case "funny_exocytosis":
                    try {
                        showBoxTabForegroundFlag = AidlServiceManager.getInstance(mContext)
                                .isViewSoundOpened();
                    } catch (RemoteException e) {
                        LogUtils.d(TAG, "isShowBoxTabForeground view sound opened checked error:" + e);
                    }
                    break;
                case "music_light_show":
                    try {
                        showBoxTabForegroundFlag = AidlServiceManager.getInstance(mContext)
                                .isViewLightOpened();
                    } catch (RemoteException e) {
                        LogUtils.d(TAG, "isShowBoxTabForeground view light opened checked error:" + e);
                    }
                    break;
            }

        }
        LogUtils.d(TAG, "isShowBoxTabForeground showBoxTabForegroundFlag:" + showBoxTabForegroundFlag);
        return showBoxTabForegroundFlag;
    }

    @Override
    public boolean openShowBoxTab(String tabType) {
        int result = -1;
        if (mServiceConnectedFlag) {
            switch (tabType) {
                case ApplicationConstant.TAB_NAME_FUNNY_EXOCYTOSIS:
                    try {
                        result = AidlServiceManager.getInstance(mContext)
                                .openApp(AidlConstants.OPEN_APP_FLAG_OUTSIDE);
                    } catch (RemoteException e) {
                        LogUtils.d(TAG, "openShowBoxTab open funny_exocytosis error:" + e);
                    }
                    break;
                case ApplicationConstant.TAB_NAME_MUSIC_LIGHT_SHOW:
                    try {
                        result = AidlServiceManager.getInstance(mContext)
                                .openApp(AidlConstants.OPEN_APP_FLAG_LIGHT_SHOW);
                    } catch (RemoteException e) {
                        LogUtils.d(TAG, "openShowBoxTab open music_light_show error:" + e);
                    }
                    break;
            }
        }
        LogUtils.d(TAG, "openShowBoxTab result:" + result);
        return result == 0;
    }

    @Override
    public boolean isShoutOutOpen() {
        boolean shoutOutFlag = false;
        if (mServiceConnectedFlag) {
            try {
                shoutOutFlag = AidlServiceManager.getInstance(mContext).isRecordOpened();
            } catch (RemoteException e) {
                LogUtils.w(TAG, "isShoutOutOpen check record open error:" + e);
            }
        }
        LogUtils.d(TAG, "isShoutOutOpen shoutOutFlag:" + shoutOutFlag);
        return shoutOutFlag;
    }

    @Override
    public boolean isRecorderEnable() {
        boolean recorderEnable = false;
        if (mServiceConnectedFlag) {
            try {
                int result = AidlServiceManager.getInstance(mContext).checkRecordEnable();
                LogUtils.d(TAG, "isRecorderEnable result:" + result);
                if (result == 0) {
                    recorderEnable = true;
                }
            } catch (RemoteException e) {
                LogUtils.w(TAG, "checkRecordEnable check recorder enable error:" + e);
            }
        }
        LogUtils.d(TAG, "isRecorderEnable recorderEnable:" + recorderEnable);
        return recorderEnable;
    }

    @Override
    public boolean openShoutOut() {
        boolean shoutOutOpenFlag = false;
        if (mServiceConnectedFlag) {
            try {
                int result = AidlServiceManager.getInstance(mContext).startRecord();
                shoutOutOpenFlag = (result == ResultCode.SUCCESS);
                LogUtils.d(TAG, "openShoutOut result:" + result);
            } catch (RemoteException e) {
                LogUtils.w(TAG, "openShoutOut start record error:" + e);
            }
        }
        LogUtils.d(TAG, "openShoutOut shoutOutOpenFlag:" + shoutOutOpenFlag);
        return shoutOutOpenFlag;
    }

    @Override
    public boolean isParkingGearPosition() {
        boolean parkingGearFlag = false;
        if (mServiceConnectedFlag) {
            try {
                parkingGearFlag = AidlServiceManager.getInstance(mContext).isParkingGearPosition();
            } catch (RemoteException e) {
                LogUtils.w(TAG, "isParkingGearPosition error:" + e);
            }
        }
        LogUtils.d(TAG, "isParkingGearPosition parkingGearFlag:" + parkingGearFlag);
        return parkingGearFlag;
    }

    @Override
    public boolean isMusicLightShowOpen() {
        boolean musicLightShowFlag = false;
        if (mServiceConnectedFlag) {
            try {
                musicLightShowFlag = AidlServiceManager.getInstance(mContext).isAllLightShowOn();
            } catch (RemoteException e) {
                LogUtils.w(TAG, "isMusicLightShowOpen error:" + e);
            }
        }
        LogUtils.d(TAG, "isMusicLightShowOpen musicLightShowFlag:" + musicLightShowFlag);
        return musicLightShowFlag;
    }

    @Override
    public int closeMusicLightShowSwitch() {
        int result = -1;
        if (mServiceConnectedFlag) {
            try {
                result = AidlServiceManager.getInstance(mContext).openOrCloseLightShowAll(false);
                LogUtils.d(TAG, "closeMusicLightShowSwitch result:" + result);
            } catch (RemoteException e) {
                LogUtils.w(TAG, "closeMusicLightShowSwitch error:" + e);
            }
        }
        return result;
    }

    public void beforeOpenApp() {
        LogUtils.d(TAG, "beforeOpenApp");
//        //收起负一屏
//        GlobalProcessingHelper.getInstance().closeNegativeScreen();
//        //收起应用中心
//        DeviceInterfaceImpl.getInstant(Utils.getApp()).getLauncherImpl().hideAllAppPanel();
    }


    static class Holder {
        private static ShowBoxInterfaceImpl INSTANCE = new ShowBoxInterfaceImpl();
    }
}
