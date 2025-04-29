package com.voice.drawing.api;


/**
 * author : jie wang
 * date : 2024/3/7 14:30
 * description : 定义ai绘图作为IPC的Server主动向 所有注册的client发送通知的能力。
 */
interface IDrawingAPICallback {

    //开始绘画
    void startDrawing();

    // 重新绘画
    void redraw(String prompt);




}