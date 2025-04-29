package com.voyah.ai.device;

import android.content.Context;
import android.os.UserHandle;

import com.google.gson.Gson;
import com.voice.drawing.api.DrawingAPIManager;
import com.voice.drawing.api.model.APIConfig;
import com.voice.drawing.api.model.DrawingInfo;
import com.voice.sdk.VoiceImpl;
import com.voice.sdk.device.DeviceHolder;
import com.voice.sdk.device.llm.LLMDrawingInterface;
import com.voice.sdk.device.system.DeviceScreenType;
import com.voice.sdk.device.system.ScreenInterface;
import com.voyah.ai.basecar.BaseAppPresenter;
import com.voyah.ai.basecar.helper.MegaDisplayHelper;
import com.voyah.ai.common.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * author : jie wang
 * date : 2024/8/12 9:53
 * description :
 */
public class LLMDrawingInterfaceImpl extends BaseAppPresenter implements LLMDrawingInterface {
    private static final String TAG = "LLMDrawingInterfaceImpl";

    private LLMDrawingInterfaceImpl() {
    }

    public static LLMDrawingInterfaceImpl getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public void init() {
        LogUtils.i(TAG, "init");
        initSdk();
        initVoiceDrawingSdk(mContext);
    }

    private void initVoiceDrawingSdk(Context context) {
        LogUtils.d(TAG, "drawing api sdk init.");
        DrawingAPIManager.DrawingAPIManagerCallback callbackMain = new DrawingAPIManager.DrawingAPIManagerCallback() {
            @Override
            public void onServiceBind() {
                LogUtils.d(TAG, "main onServiceBind");
            }

            @Override
            public void onServiceDisconnected() {

            }

            @Override
            public void onBinderDied() {

            }

            @Override
            public void startDrawing() {
                LogUtils.d(TAG, "main startDrawing");
            }

            @Override
            public void redraw(String prompt) {
                LogUtils.d(TAG, "main redraw prompt:" + prompt);
                VoiceImpl.getInstance().queryTest(prompt, 0, true);
            }

            @Override
            public void redraw(String prompt, UserHandle userHandle) {
                LogUtils.d(TAG, "main redraw prompt:" + prompt);
                VoiceImpl.getInstance().queryTest(prompt, 0, true);
            }
        };

        UserHandle userHandleMain = MegaDisplayHelper.getUserHandleByDisplayId(
                MegaDisplayHelper.getMainScreenDisplayId());
        APIConfig apiConfigMain = new APIConfig(userHandleMain, callbackMain);


        DrawingAPIManager.DrawingAPIManagerCallback callbackPassenger = new DrawingAPIManager.DrawingAPIManagerCallback() {
            @Override
            public void onServiceBind() {
                LogUtils.d(TAG, "passenger onServiceBind");
            }

            @Override
            public void onServiceDisconnected() {

            }

            @Override
            public void onBinderDied() {

            }

            @Override
            public void startDrawing() {
                LogUtils.d(TAG, "passenger startDrawing");
            }

            @Override
            public void redraw(String prompt) {
                LogUtils.d(TAG, "passenger redraw prompt:" + prompt);
                VoiceImpl.getInstance().queryTest(prompt, 1, true);
            }

            @Override
            public void redraw(String prompt, UserHandle userHandle) {
                LogUtils.d(TAG, "passenger redraw prompt:" + prompt);
                VoiceImpl.getInstance().queryTest(prompt, 1, true);
            }
        };
        UserHandle userHandlePassenger = MegaDisplayHelper.getUserHandleByDisplayId(
                MegaDisplayHelper.getPassengerScreenDisplayId());
        APIConfig apiConfigPassenger = new APIConfig(userHandlePassenger, callbackPassenger);

        DrawingAPIManager.DrawingAPIManagerCallback callbackCeiling = new DrawingAPIManager.DrawingAPIManagerCallback() {
            @Override
            public void onServiceBind() {
                LogUtils.d(TAG, "ceiling onServiceBind");
                mServiceConnectedFlag = true;
            }

            @Override
            public void onServiceDisconnected() {

            }

            @Override
            public void onBinderDied() {

            }

            @Override
            public void startDrawing() {
                LogUtils.d(TAG, "passenger startDrawing");
            }

            @Override
            public void redraw(String prompt) {
                LogUtils.d(TAG, "ceiling redraw prompt:" + prompt);
                VoiceImpl.getInstance().queryTest(prompt, 2, true);
            }

            @Override
            public void redraw(String prompt, UserHandle userHandle) {
                LogUtils.d(TAG, "ceiling redraw prompt:" + prompt);
                VoiceImpl.getInstance().queryTest(prompt, 2, true);
            }
        };
        UserHandle userHandleCeiling = MegaDisplayHelper.getUserHandleByDisplayId(
                MegaDisplayHelper.getCeilingScreenDisplayId());
        APIConfig apiConfigCeiling = new APIConfig(userHandleCeiling, callbackCeiling);

        List<APIConfig> apiConfigs = new ArrayList<>();
        apiConfigs.add(apiConfigMain);
        apiConfigs.add(apiConfigPassenger);
        apiConfigs.add(apiConfigCeiling);
        DrawingAPIManager.getInstance().init(context, apiConfigs);
    }

    @Override
    public boolean isAppForeground() {
        boolean appForegroundFlag = false;
        LogUtils.d(TAG, "isAppForeground appForegroundFlag:" + appForegroundFlag);
        return appForegroundFlag;
    }

    @Override
    public boolean openApp(int displayId) {
        LogUtils.d(TAG, "openApp, displayId:" + displayId);
        beforeOpenApp();
        UserHandle userHandle = MegaDisplayHelper.getUserHandleByDisplayId(
                MegaDisplayHelper.getMainScreenDisplayId());
        int result = DrawingAPIManager.getInstance().openApp(0, userHandle);
        LogUtils.d(TAG, "openApp result:" + result);
        return true;
    }

    public void openVoiceDrawingApp(String screenType) {
        LogUtils.d(TAG, "openVoiceDrawingApp screenType:" + screenType);
        ScreenInterface screenInterface = DeviceHolder.INS().getDevices().getSystem().getScreen();
        int result = DrawingAPIManager.getInstance().openApp(
                0,
                screenInterface.getUserHandle(DeviceScreenType.fromValue(screenType)));
        LogUtils.d(TAG, "openApp result:" + result);
    }

    @Override
    public boolean closeApp(int displayId) {
        int result = DrawingAPIManager.getInstance().closeApp();
        LogUtils.d(TAG, "closeApp result:" + result);
        return true;
    }

    @Override
    public void postDrawingState(String prompt,
                                    String keyWords,
                                 String ttxText,
                                 int streamMode,
                                 int code,
                                 int imageSize,
                                 int drawingState,
                                 List<String> imgUrls,
                                 String requestId,
                                 String screenType) {

        DrawingInfo drawingInfo = new DrawingInfo();
        drawingInfo.setPrompt(prompt);
        drawingInfo.setKeyWords(keyWords);
        drawingInfo.setRequestId(requestId);
        drawingInfo.setTime(System.currentTimeMillis());
        drawingInfo.setTtsText(ttxText);
        drawingInfo.setStreamMode(streamMode);
        drawingInfo.setCode(code);
        drawingInfo.setPicSize(imageSize);
        drawingInfo.setDrawingState(drawingState);

        if (imgUrls != null) {
            drawingInfo.setUrlList(imgUrls);
        }

        String drawingStateJson = new Gson().toJson(drawingInfo);

        LogUtils.d(TAG, "postDrawingState drawingStateJson:" + drawingStateJson);
        ScreenInterface screenInterface = DeviceHolder.INS().getDevices().getSystem().getScreen();
        DeviceScreenType deviceScreenType = DeviceScreenType.fromValue(screenType);
        int displayId = screenInterface.getDisplayId(deviceScreenType);
        UserHandle userHandle = screenInterface.getUserHandle(deviceScreenType);
        DrawingAPIManager.getInstance().postDrawingState(drawingStateJson, displayId, userHandle);
    }

    private static class Holder {
        private static final LLMDrawingInterfaceImpl INSTANCE = new LLMDrawingInterfaceImpl();
    }
}
