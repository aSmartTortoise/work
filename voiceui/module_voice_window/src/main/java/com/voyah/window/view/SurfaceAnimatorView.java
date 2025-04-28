package com.voyah.window.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.voyah.window.R;

/**
 * author : jie wang
 * date : 2024/11/29 17:21
 * description :
 */

public class SurfaceAnimatorView extends SurfaceView implements SurfaceHolder.Callback {

    private IAnimator iAnimator;
    private Thread mRenderThread;
    private SurfaceHolder surfaceHolder;
    private Paint mPaintClear;
    // 控制帧率
    private int fps = 1000 / 35;
    private volatile boolean isStop = false;
    // 负责不断绘制的runnable，在子线程执行
    private Runnable mRenderRunnable = new Runnable() {
        @Override
        public void run() {
            while (!isStop) {
                long start = System.currentTimeMillis();
                onDrawAnimator();
                long spendTime = System.currentTimeMillis() - start;
                try {
                    if ((fps - spendTime) > 0) {
                        Thread.sleep(fps - spendTime);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void onDrawAnimator() {

        // 从surfaceHolder 获取离屏的canvas
        Canvas canvas = surfaceHolder.lockCanvas();
        if (canvas == null) {
            return;
        }
        // 清屏。这步很重要，不然画布会有上次绘制的内容
        canvas.drawPaint(mPaintClear);
        // 将画布给animator，实现对应的动画
        iAnimator.onDraw(canvas);
        // 释放canvas
        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    public SurfaceAnimatorView(Context context) {
        super(context);
        init();
    }

    public SurfaceAnimatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {
        setFocusable(true);
        if (surfaceHolder == null) {
            surfaceHolder = getHolder();
            surfaceHolder.addCallback(this);
        }
        this.setZOrderOnTop(true);
        this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mPaintClear = new Paint();
        mPaintClear.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        setBackgroundColor(getContext().getResources().getColor(R.color.bg_surface));
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        // 初始化animator
        iAnimator = new CircleLoadingAnimator(getContext());
        iAnimator.onLayout(getWidth(), getHeight());
        isStop = false;
        // 启动绘制的线程
        mRenderThread = new Thread(mRenderRunnable);
        mRenderThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        isStop = true;
        try {
            mRenderThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
