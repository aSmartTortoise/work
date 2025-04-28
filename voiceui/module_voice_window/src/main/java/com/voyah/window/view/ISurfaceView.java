package com.voyah.window.view;

import android.graphics.Canvas;

public interface ISurfaceView {

    Canvas lockCanvas();

    void unlockCanvasAndPost(Canvas canvas);

    int getWidth();

    int getHeight();

    void stop();

    void release();
}
