package com.voyah.window.view.wave;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.blankj.utilcode.util.LogUtils;
import com.voyah.window.view.ISurfaceView;

public class WaveSurfaceView extends SurfaceView implements SurfaceHolder.Callback, ISurfaceView {

    private WaveSurfaceViewDraw drawThread;

    private SurfaceHolder surfaceHolder;
    private final Paint mPaint = new Paint();
    private WaveState waveState;

    public WaveSurfaceView(Context context) {
        this(context, null);
    }

    public WaveSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LogUtils.d("init");
        drawThread = new WaveSurfaceViewDraw(this);
        surfaceHolder = getHolder();
        setZOrderOnTop(true);
        surfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        surfaceHolder.addCallback(this);
        mPaint.setFilterBitmap(true);
    }


    public void startDraw(WaveState state) {
        LogUtils.d("startDraw()");
        waveState = state;
        drawThread.setRes(state);
    }

    public void setRes(WaveState state) {
        LogUtils.d("setRes state:" + state);
        waveState = state;
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
        drawThread.startDraw();
        if (!waveState.equals(WaveState.DEFAULT)) {
            drawThread.setRes(waveState);
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

    public void setDirection(int direction) {
        if (drawThread != null) {
            drawThread.setDirection(direction);
        }
    }
}
