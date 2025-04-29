package com.voice.drawing.api.model;

import android.os.UserHandle;

import com.voice.drawing.api.DrawingAPIManager;
import com.voice.drawing.api.IDrawingAPI;

/**
 * author : jie wang
 * date : 2025/1/22 10:58
 * description :
 */
public class APIConfig {
    private UserHandle userHandle;

    private DrawingAPIManager.DrawingAPIManagerCallback callback;

    private IDrawingAPI drawingAPI;

    public APIConfig(UserHandle userHandle) {
        this.userHandle = userHandle;
    }

    public APIConfig(UserHandle userHandle, DrawingAPIManager.DrawingAPIManagerCallback callback) {
        this.userHandle = userHandle;
        this.callback = callback;
    }

    public UserHandle getUserHandle() {
        return userHandle;
    }

    public void setUserHandle(UserHandle userHandle) {
        this.userHandle = userHandle;
    }

    public DrawingAPIManager.DrawingAPIManagerCallback getCallback() {
        return callback;
    }

    public void setCallback(DrawingAPIManager.DrawingAPIManagerCallback callback) {
        this.callback = callback;
    }

    public IDrawingAPI getDrawingAPI() {
        return drawingAPI;
    }

    public void setDrawingAPI(IDrawingAPI drawingAPI) {
        this.drawingAPI = drawingAPI;
    }
}
