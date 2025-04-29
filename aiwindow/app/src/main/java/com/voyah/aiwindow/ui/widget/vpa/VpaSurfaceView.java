package com.voyah.aiwindow.ui.widget.vpa;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.blankj.utilcode.util.LogUtils;
import com.voyah.aiwindow.ui.widget.ISurfaceView;

public class VpaSurfaceView extends SurfaceView implements SurfaceHolder.Callback, ISurfaceView {

    private VpaSurfaceViewDraw mDrawThread;

    private SurfaceHolder mSurfaceHolder;
    private final Paint mPaint = new Paint();

    public VpaSurfaceView(Context context) {
        this(context, null);
    }

    public VpaSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VpaSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mDrawThread = new VpaSurfaceViewDraw(this);
        mDrawThread.startEngine();

        mSurfaceHolder = getHolder();
        setZOrderOnTop(true);
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        mSurfaceHolder.addCallback(this);
        mPaint.setFilterBitmap(true);
    }


    public void startDraw(VpaState state) {
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
        mDrawThread.setRes(VpaState.LISTENING);
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
}
