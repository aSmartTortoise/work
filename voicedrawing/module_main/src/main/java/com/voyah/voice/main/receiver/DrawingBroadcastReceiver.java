package com.voyah.voice.main.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.google.gson.Gson;
import com.voice.drawing.api.DrawingAPIManager;
import com.voice.drawing.api.model.DrawingInfo;
import com.voice.drawing.api.model.DrawingState;
import com.voyah.vcos.manager.MegaDisplayHelper;
import com.voyah.voice.framework.util.DateUtil;

import java.util.Arrays;


/**
 * @author:lcy
 * @data:2024/2/19
 **/
public class DrawingBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "DrawingReceiver";

    private String[] imgUrlArr = new String[]{
            "https://cn.bing.com/th?id=OHR.ChongyangFestival_ZH-CN5260976551_1920x1200.jpg&rf=LaDigue_1920x1200.jpg",
            "https://n.sinaimg.cn/sinakd2021523s/570/w800h570/20210523/4d18-kqpyffy9822676.jpg",
            "https://tse3-mm.cn.bing.net/th/id/OIP-C.5i8S9bwZl-GUM5Dg7DTu3AHaFU?rs=1&pid=ImgDetMain",
            "https://n.sinaimg.cn/sinakd20210417ac/160/w1920h1440/20210417/d7e4-knvsnuf9772547.jpg"};



    private String[] imgUrlArr1 = new String[]{
            "https://cn.bing.com/th?id=OHR.ChongyangFestival_ZH-CN5260976551_1920x1200.jpg&rf=LaDigue_1920x1200.jpg"};



    public DrawingBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: action:" + intent.getAction());
        if (TextUtils.equals(intent.getAction(), "com.voyah.voice.drawing.test")) {
            String queryString = intent.getStringExtra("query");
            Log.d(TAG, "onReceive queryString is " + queryString);
            int displayIdMain = MegaDisplayHelper.getMainScreenDisplayId();
            UserHandle userHandleMain = MegaDisplayHelper.getUserHandleByDisplayId(displayIdMain);
            LogUtils.i("drawing start main displayId:" + displayIdMain
                    + " ,main userHandleMain:" + userHandleMain);
            if (TextUtils.equals(queryString, "openApp")) {
                ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Boolean>() {

                    @Override
                    public Boolean doInBackground() throws Throwable {
                        int mainDisplayId = MegaDisplayHelper.getMainScreenDisplayId();

                        UserHandle mainUserHandle = MegaDisplayHelper
                                .getUserHandleByDisplayId(mainDisplayId);

//                        boolean appForeground = DrawingAPIManager.getInstance().isAppForeground();
//                        Log.d(TAG, "onReceive: onReceive appForeground:" + appForeground);
//                        if (!appForeground) {
//                            int result = DrawingAPIManager.getInstance().openApp(mainUserHandle);
//                            Log.d(TAG, "doInBackground: open app result:" + result);
//                        }
                        return true;
                    }

                    @Override
                    public void onSuccess(Boolean result) {

                    }
                });
            } else if (TextUtils.equals(queryString, "closeApp")) {
                ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Boolean>() {

                    @Override
                    public Boolean doInBackground() throws Throwable {
//                        boolean appForeground = DrawingAPIManager.getInstance().isAppForeground();
//                        Log.d(TAG, "onReceive: onReceive appForeground:" + appForeground);
//                        if (appForeground) {
//                            int result = DrawingAPIManager.getInstance().closeApp();
//                            Log.d(TAG, "doInBackground: close app result:" + result);
//                        }
                        return true;
                    }

                    @Override
                    public void onSuccess(Boolean result) {

                    }
                });
            }
            else if (TextUtils.equals(queryString, "openDrawingInfo")) {
//                int result = DrawingAPIManager.getInstance().
            } else if (TextUtils.equals(queryString, "postDrawingStateStart1")) {
                DrawingInfo drawingInfo = new DrawingInfo();
                drawingInfo.setPicSize(1);
                drawingInfo.setPrompt("帮我画一幅美人吟");
                drawingInfo.setDrawingState(DrawingState.START);
                String drawingStateJson = new Gson().toJson(drawingInfo);
                DrawingAPIManager.getInstance().postDrawingState(
                        drawingStateJson,
                        displayIdMain,
                        userHandleMain);
            }  else if (TextUtils.equals(queryString, "postDrawingStateStart4")) {
                DrawingInfo drawingInfo = new DrawingInfo();
                drawingInfo.setPicSize(4);
                drawingInfo.setPrompt("帮我画一幅美人吟");
                drawingInfo.setDrawingState(DrawingState.START);
                String drawingStateJson = new Gson().toJson(drawingInfo);

                DrawingAPIManager.getInstance().postDrawingState(
                        drawingStateJson,
                        displayIdMain,
                        userHandleMain);
            } else if (TextUtils.equals(queryString, "postDrawingStateOnGoing")) {
                DrawingInfo drawingInfo = new DrawingInfo();
                drawingInfo.setDrawingState(DrawingState.COMPLETION);
                drawingInfo.setUrlList(Arrays.asList(imgUrlArr));
                drawingInfo.setPrompt("帮我画一幅美人吟");
                String drawingStateJson = new Gson().toJson(drawingInfo);
//                DrawingAPIManager.getInstance().postDrawingState(drawingStateJson);
            } else if (TextUtils.equals(queryString, "postDrawingStateCompletion4")) {
                ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Boolean>() {

                    @Override
                    public Boolean doInBackground() throws Throwable {
                        DrawingInfo drawingInfo = new DrawingInfo();
                        drawingInfo.setDrawingState(DrawingState.COMPLETION);
                        drawingInfo.setUrlList(Arrays.asList(imgUrlArr));
                        drawingInfo.setPrompt("帮我画一幅美人吟");
                        drawingInfo.setKeyWords("画一幅美人吟");
                        drawingInfo.setTime(DateUtil.getNow());
                        drawingInfo.setTtsText("为您绘画完成。");
                        String drawingStateJson = new Gson().toJson(drawingInfo);
                        DrawingAPIManager.getInstance().postDrawingState(
                                drawingStateJson,
                                displayIdMain,
                                userHandleMain
                                );
                        return true;
                    }

                    @Override
                    public void onSuccess(Boolean result) {

                    }
                });

            } else if (TextUtils.equals(queryString, "postDrawingStateCompletion1")) {
                ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Boolean>() {

                    @Override
                    public Boolean doInBackground() throws Throwable {
                        DrawingInfo drawingInfo = new DrawingInfo();
                        drawingInfo.setPicSize(1);
                        drawingInfo.setDrawingState(DrawingState.COMPLETION);
                        drawingInfo.setUrlList(Arrays.asList(imgUrlArr1));
                        drawingInfo.setPrompt("帮我画一幅美人吟");
                        drawingInfo.setKeyWords("画一幅美人吟");
                        drawingInfo.setTtsText("为您绘画完成。");
                        drawingInfo.setTime(DateUtil.getNow());
                        String drawingStateJson = new Gson().toJson(drawingInfo);
                        DrawingAPIManager.getInstance().postDrawingState(
                                drawingStateJson,
                                displayIdMain,
                                userHandleMain);
                        return true;
                    }

                    @Override
                    public void onSuccess(Boolean result) {

                    }
                });

            }

        }

    }





}
