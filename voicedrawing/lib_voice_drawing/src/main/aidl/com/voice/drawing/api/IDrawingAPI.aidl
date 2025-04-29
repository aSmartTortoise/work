package com.voice.drawing.api;
import com.voice.drawing.api.IDrawingAPICallback;
import android.os.UserHandle;


/**
 * author : jie wang
 * date : 2024/3/7 14:30
 * description : 定义ai绘图 进程对外暴露的能力接口
 */
interface IDrawingAPI {

    void registerDrawingAPICallback(IDrawingAPICallback callback);

    void unRegisterDrawingAPICallback(IDrawingAPICallback callback);

    //打开应用
    int openApp();

    int openAppAsDisplayId(int displayId);

    int closeApp();

    int isAppForeground();

    int openDrawingInfo();

    void postDrawingState(String drawingStateJson);

    void postDrawingStateAsDisplayId(String drawingStateJson, int displayId);



}