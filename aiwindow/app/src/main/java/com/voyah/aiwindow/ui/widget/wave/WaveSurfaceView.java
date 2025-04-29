package com.voyah.aiwindow.ui.widget.wave;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.blankj.utilcode.util.LogUtils;
import com.voyah.aiwindow.ui.widget.ISurfaceView;

public class WaveSurfaceView extends SurfaceView implements SurfaceHolder.Callback, ISurfaceView {

    private WaveSurfaceViewDraw mDrawThread;

    private SurfaceHolder mSurfaceHolder;
    private final Paint mPaint = new Paint();

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
        mDrawThread = new WaveSurfaceViewDraw(this);
        mDrawThread.startEngine();

        mSurfaceHolder = getHolder();
        setZOrderOnTop(true);
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        mSurfaceHolder.addCallback(this);
        mPaint.setFilterBitmap(true);
    }


    public void startDraw(WaveState state) {
        LogUtils.d("startDraw()");
        mDrawThread.setRes(state);
    }

    @Override
    public void release() {
        LogUtils.d("release");
        mDrawThread.stopDraw();
    }

    @Override
    public Canvas lockCanvas() {
        return mSurfaceHolder.lockCanvas();
    }

    @Override
    public void unlockCanvasAndPost(Canvas canvas) {
        mSurfaceHolder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void stop() {
        mDrawThread.stopDraw();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LogUtils.d("surfaceCreated");
        mDrawThread.setSurfaceViewIsValid(true);
        mDrawThread.setRes(WaveState.LISTENING);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        LogUtils.d("surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        LogUtils.d("surfaceDestroyed");
        mDrawThread.setSurfaceViewIsValid(false);
        mDrawThread.stopDraw();
    }

    public void setDirection(int direction) {
        if (mDrawThread != null) {
            mDrawThread.setDirection(direction);
        }
    }
}
