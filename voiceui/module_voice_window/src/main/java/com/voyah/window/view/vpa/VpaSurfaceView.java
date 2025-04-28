package com.voyah.window.view.vpa;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.voyah.window.view.ISurfaceView;

public class VpaSurfaceView extends SurfaceView implements SurfaceHolder.Callback, ISurfaceView {

    private VpaSurfaceViewDraw drawThread;

    private SurfaceHolder surfaceHolder;
    private VpaState vpaState;
    private final Paint paint = new Paint();

    public VpaSurfaceView(Context context) {
        this(context, null);
        init();
    }

    public VpaSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public VpaSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        LogUtils.d("init");
        drawThread = new VpaSurfaceViewDraw(this);
        surfaceHolder = getHolder();
        setZOrderOnTop(true);
        surfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        surfaceHolder.addCallback(this);
    }


    public void startDraw(VpaState state) {
        LogUtils.d("startDraw()");
        vpaState = state;
        drawThread.setRes(state);
    }

    @Override
    public void release() {
        LogUtils.d("release");
        drawThread.stopDraw();
    }

    @Override
    public Canvas lockCanvas() {
        return surfaceHolder.lockCanvas();
    }

    @Override
    public void unlockCanvasAndPost(Canvas canvas) {
        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void stop() {
        drawThread.stopDraw();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LogUtils.d("surfaceCreated");
        int visibility = getVisibility();
        LogUtils.d("surfaceCreated visibility:" + visibility);
        if (visibility == View.VISIBLE) {
            drawThread.startDraw();
        }

        paint.setFilterBitmap(true);
        if (!vpaState.equals(VpaState.DEFAULT)) {
            drawThread.setRes(vpaState);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        LogUtils.d("surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        LogUtils.d("surfaceDestroyed");
        drawThread.stopDraw();
    }
}
