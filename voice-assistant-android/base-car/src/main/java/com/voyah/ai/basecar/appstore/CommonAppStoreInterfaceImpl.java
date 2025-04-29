package com.voyah.ai.basecar.appstore;


import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.appstore.AppStoreInterface;
import com.voice.sdk.device.appstore.AppStorePage;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voice.sdk.constant.ApplicationConstant;
import com.voyah.ai.common.utils.ContextUtils;
import com.voyah.ai.common.utils.LogUtils;
import com.xiaoma.xmsdk.XmSdk;
import com.xiaoma.xmsdk.constant.ResultCode;
import com.xiaoma.xmsdk.constant.ResultKey;
import com.xiaoma.xmsdk.dispose.Disposable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class CommonAppStoreInterfaceImpl implements AppStoreInterface {

    private static final String TAG = "AppStoreInterfaceImpl";

    private static final int DEFAULT_WAIT_TIME = 10000;

    private static final String[] APP_STORE_APPS = {
            "哔哩哔哩",
            "车鱼视听",
            "雷石KTV",
            "演示助手",
            "天翼云游戏",
            "随乐游",
            "小马街机厅",
            "小宇宙",
            "随乐游tv版",
    };
    private Disposable disposable;

    public CommonAppStoreInterfaceImpl() {

    }

    @Override
    public void init() {
        LogUtils.i(TAG, "init");
        XmSdk.Companion.getINSTANCE().init(ContextUtils.getAppContext());
    }

    @Override
    public void openAppStore(DeviceScreenType screenType) {
        tryShowToast(screenType);
        LogUtils.i(TAG, "openAppStore:" + screenType);
        disposable = XmSdk.Companion.getINSTANCE().openLtAppStore(DeviceHolder.INS().getDevices().getSystem().getScreen().getDisplayId(screenType), (result, bundle) -> {
            int code = (bundle != null) ? bundle.getInt(ResultKey.KEY_CODE, ResultCode.FAILURE) : ResultCode.FAILURE;
            LogUtils.i(TAG, "onResult:" + result + ",code:" + code);
            if (disposable != null) {
                disposable.dispose();
                disposable = null;
            }
        });
    }

    @Override
    public void closeAppStore(DeviceScreenType screenType) {
        LogUtils.i(TAG, "closeAppStore:" + screenType);
        disposable = XmSdk.Companion.getINSTANCE().closeLtAppStore(DeviceHolder.INS().getDevices().getSystem().getScreen().getDisplayId(screenType), (result, bundle) -> {
            int code = (bundle != null) ? bundle.getInt(ResultKey.KEY_CODE, ResultCode.FAILURE) : ResultCode.FAILURE;
            LogUtils.i(TAG, "onResult:" + result + ",code:" + code);
            if (disposable != null) {
                disposable.dispose();
                disposable = null;
            }
        });
    }

    @Override
    public void openAppStorePage(DeviceScreenType screenType, int page) {
        tryShowToast(screenType);
        LogUtils.i(TAG, "openAppStorePage:" + page + ",screenName:" + screenType);
        disposable = XmSdk.Companion.getINSTANCE().openPage(DeviceHolder.INS().getDevices().getSystem().getScreen().getDisplayId(screenType), page, (result, bundle) -> {
            int code = (bundle != null) ? bundle.getInt(ResultKey.KEY_CODE, ResultCode.FAILURE) : ResultCode.FAILURE;
            LogUtils.i(TAG, "onResult:" + result + ",code:" + code);
            if (disposable != null) {
                disposable.dispose();
                disposable = null;
            }
        });
    }

    @Override
    public void openAppStorePage(DeviceScreenType screenType, int page, String tabName) {
        LogUtils.i(TAG, "openAppStorePage:" + page + ",tabName:" + tabName + ",screenName:" + screenType);
        openAppStorePage(screenType, page);
    }

    @Override
    public boolean isInFront(DeviceScreenType screenType) {
        return DeviceHolder.INS().getDevices().getSystem().getApp().isAppForeGround(ApplicationConstant.PKG_APP_STORE, screenType);
    }

    @Override
    public int getCurrentPage(DeviceScreenType screenType) {
        LogUtils.i(TAG, "getCurrentPage:" + screenType);
        if (!isInFront(screenType)) {
            return AppStorePage.PageNone.getValue();
        }
        CompletableFuture<Integer> completableFuture = new CompletableFuture<>();
        disposable = XmSdk.Companion.getINSTANCE().getCurrentPage(DeviceHolder.INS().getDevices().getSystem().getScreen().getDisplayId(screenType), (result, bundle) -> {
            int page = (bundle != null) ? bundle.getInt(ResultKey.KEY_CODE, AppStorePage.PageNone.getValue()) : ResultCode.FAILURE;
            LogUtils.i(TAG, "onResult:" + result + ",page:" + page);
            completableFuture.complete(page);
            if (disposable != null) {
                disposable.dispose();
                disposable = null;
            }
        });
        try {
            int page = completableFuture.get(DEFAULT_WAIT_TIME, TimeUnit.MILLISECONDS);
            LogUtils.i(TAG, "getCurrentPage:" + page);
            return page;
        } catch (Exception e) {
            return AppStorePage.PageNone.getValue();
        }
    }

    @Override
    public boolean isInAppStore(String name) {
        boolean ret = false;
        for (String curAppName : APP_STORE_APPS) {
            if (curAppName.equalsIgnoreCase(name)) {
                ret = true;
                break;
            }
        }
        LogUtils.i(TAG, "isInAppStore:" + name + ",isInAppStore:" + ret);
        return ret;
    }

    @Override
    public int searchApp(DeviceScreenType screenType, String appName) {
        tryShowToast(screenType);
        LogUtils.i(TAG, "search app:" + appName + ",screenName:" + screenType);
        CompletableFuture<Integer> completableFuture = new CompletableFuture<>();
        disposable = XmSdk.Companion.getINSTANCE().searchApp(DeviceHolder.INS().getDevices().getSystem().getScreen().getDisplayId(screenType), appName, (result, bundle) -> {
            int code = (bundle != null) ? bundle.getInt(ResultKey.KEY_CODE, ResultCode.FAILURE) : ResultCode.FAILURE;
            LogUtils.i(TAG, "onResult:" + result + ",code:" + code);
            if (disposable != null) {
                disposable.dispose();
                disposable = null;
            }
            completableFuture.complete(code);
        });
        try {
            int code = completableFuture.get(DEFAULT_WAIT_TIME, TimeUnit.MILLISECONDS);
            LogUtils.i(TAG, "code:" + code);
            return code;
        } catch (Exception e) {
            return ResultCode.FAILURE;
        }
    }

    @Override
    public int updateApp(DeviceScreenType screenType, String appName) {
        tryShowToast(screenType);
        LogUtils.i(TAG, "update app:" + appName + ",screenName:" + screenType);
        CompletableFuture<Integer> completableFuture = new CompletableFuture<>();
        disposable = XmSdk.Companion.getINSTANCE().updateApp(DeviceHolder.INS().getDevices().getSystem().getScreen().getDisplayId(screenType), appName, (result, bundle) -> {
            int code = (bundle != null) ? bundle.getInt(ResultKey.KEY_CODE, ResultCode.FAILURE) : ResultCode.FAILURE;
            LogUtils.i(TAG, "onResult:" + result + ",code:" + code);
            if (disposable != null) {
                disposable.dispose();
                disposable = null;
            }
            completableFuture.complete(code);
        });
        try {
            int code = completableFuture.get(DEFAULT_WAIT_TIME, TimeUnit.MILLISECONDS);
            LogUtils.i(TAG, "code:" + code);
            return code;
        } catch (Exception e) {
            return ResultCode.FAILURE;
        }
    }

    @Override
    public int uninstallApp(DeviceScreenType screenType, String appName) {
        tryShowToast(screenType);
        LogUtils.i(TAG, "uninstall app:" + appName + ",screenName:" + screenType);
        CompletableFuture<Integer> completableFuture = new CompletableFuture<>();
        disposable = XmSdk.Companion.getINSTANCE().uninstallApp(DeviceHolder.INS().getDevices().getSystem().getScreen().getDisplayId(screenType), appName, (result, bundle) -> {
            int code = (bundle != null) ? bundle.getInt(ResultKey.KEY_CODE, ResultCode.FAILURE) : ResultCode.FAILURE;
            LogUtils.i(TAG, "onResult:" + result + ",code:" + code);
            if (disposable != null) {
                disposable.dispose();
                disposable = null;
            }
            completableFuture.complete(code);
        });
        try {
            int code = completableFuture.get(DEFAULT_WAIT_TIME, TimeUnit.MILLISECONDS);
            LogUtils.i(TAG, "code:" + code);
            return code;
        } catch (Exception e) {
            return ResultCode.FAILURE;
        }
    }

    @Override
    public int downLoadApp(DeviceScreenType screenType, String appName) {
        tryShowToast(screenType);
        LogUtils.i(TAG, "download app:" + appName + ",screenName:" + screenType);
        CompletableFuture<Integer> completableFuture = new CompletableFuture<>();
        disposable = XmSdk.Companion.getINSTANCE().downloadApp(DeviceHolder.INS().getDevices().getSystem().getScreen().getDisplayId(screenType), appName, (result, bundle) -> {
            int code = (bundle != null) ? bundle.getInt(ResultKey.KEY_CODE, ResultCode.FAILURE) : ResultCode.FAILURE;
            LogUtils.i(TAG, "onResult:" + result + ",code:" + code);
            if (disposable != null) {
                disposable.dispose();
                disposable = null;
            }
            completableFuture.complete(code);
        });
        try {
            int code = completableFuture.get(DEFAULT_WAIT_TIME, TimeUnit.MILLISECONDS);
            LogUtils.i(TAG, "code:" + code);
            return code;
        } catch (Exception e) {
            return ResultCode.FAILURE;
        }
    }

    @Override
    public boolean isSuccessCode(int code) {
        return code == ResultCode.SUCCESS || code == ResultCode.ALREADY
                || code == ResultCode.SINGLE_DATA || code == ResultCode.MULTI_DATA;
    }


    public void tryShowToast(DeviceScreenType targetScreen) {
        LogUtils.i(TAG, "tryShowToast:" + targetScreen);
        if (!DeviceHolder.INS().getDevices().getSystem().getScreen().isSupportMultiScreen()) {
            LogUtils.i(TAG, "single screen not show toast");
            return;
        }
        if (targetScreen == null) {
            targetScreen = DeviceScreenType.CENTRAL_SCREEN;
        }
        boolean needShow = false;
        String pkgName = ApplicationConstant.PKG_APP_STORE;
        DeviceScreenType originalScreen = null;

        if (DeviceHolder.INS().getDevices().getSystem().getApp().isAppForeGround(pkgName, DeviceScreenType.CENTRAL_SCREEN)
                && targetScreen != DeviceScreenType.CENTRAL_SCREEN) {
            //原来在主屏显示，现在希望显示到其他屏
            originalScreen = DeviceScreenType.CENTRAL_SCREEN;
            needShow = true;
        } else if (DeviceHolder.INS().getDevices().getSystem().getApp().isAppForeGround(pkgName, DeviceScreenType.PASSENGER_SCREEN)
                && targetScreen != DeviceScreenType.PASSENGER_SCREEN) {
            //原来在副驾显示，现在希望显示到其他屏
            originalScreen = DeviceScreenType.PASSENGER_SCREEN;
            needShow = true;
        } else if (DeviceHolder.INS().getDevices().getSystem().getApp().isAppForeGround(pkgName, DeviceScreenType.CEIL_SCREEN)
                && targetScreen != DeviceScreenType.CEIL_SCREEN) {
            //原来在吸顶显示，现在希望显示到其他屏
            originalScreen = DeviceScreenType.CEIL_SCREEN;
            needShow = true;
        }

        if (targetScreen == DeviceScreenType.CENTRAL_SCREEN || originalScreen == DeviceScreenType.CENTRAL_SCREEN) {
            if (DeviceHolder.INS().getDevices().getSystem().getSplitScreen().isSplitScreening()) {
                needShow = false;
            }
        }
        if (!needShow) {
            LogUtils.i(TAG, "no need to show toast");
            return;
        }
        String screenName = "中控屏";
        if (targetScreen == DeviceScreenType.PASSENGER_SCREEN) {
            screenName = "副驾屏";
        }
        if (targetScreen == DeviceScreenType.CEIL_SCREEN) {
            screenName = "吸顶屏";
        }
        String toastMsg = String.format("%s已在%s打开", "岚图商城", screenName);
        LogUtils.i(TAG, "toastMsg:" + toastMsg + ",originalScreen:" + originalScreen);
        DeviceHolder.INS().getDevices().getSystem().getUi().showSystemToast(originalScreen, toastMsg);
    }
}
