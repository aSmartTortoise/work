package com.voyah.window.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.animation.DecelerateInterpolator;

import com.voyah.window.R;

/**
 * author : jie wang
 * date : 2024/11/29 17:24
 * description :
 */
public class CircleLoadingAnimator implements IAnimator {
    private DecelerateInterpolator fastToSlow = new DecelerateInterpolator();

    // 每帧变化的幅度，越小越慢，帧区别也越小
    private int INCREASE_VALUE = 4;
    private int MAX_CIRCLE_RADIUS = 200;
    private int CIRCLE_RADIUS = MAX_CIRCLE_RADIUS >> 1;
    private int mSmallCircleRadius = 0;
    private int mBigCircleRadius = MAX_CIRCLE_RADIUS;
    private int increaseValue = -1;
    private int recordValue = MAX_CIRCLE_RADIUS;
    private Paint mSmallPaint = new Paint();
    private Paint mBigPaint = new Paint();
    private int mX;
    private int mY;
    private static final String TAG = "CircleLoadingAnimator";

    public CircleLoadingAnimator(Context context) {
        mBigPaint.setStyle(Paint.Style.FILL);
        mBigPaint.setStrokeCap(Paint.Cap.ROUND);
        mBigPaint.setAntiAlias(true);
        mBigPaint.setColor(context.getResources().getColor(R.color.white_90));

        mSmallPaint.setStyle(Paint.Style.FILL);
        mSmallPaint.setStrokeCap(Paint.Cap.ROUND);
        mSmallPaint.setAntiAlias(true);
        mSmallPaint.setColor(context.getResources().getColor(R.color.white));
    }

    @Override
    public void onLayout(int width, int height) {
        mX = width >> 1;
        mY = height >> 1;
    }

    @Override
    public void onDraw(Canvas canvas) {
        updateInCreaseValue();
        recordValue += increaseValue;
        // 模拟属性动画 0 - > 1 的过程
        float value = (float) ((MAX_CIRCLE_RADIUS - recordValue) * 1.0 / (CIRCLE_RADIUS));
        // 更新圆半径
        mBigCircleRadius = (int) (fastToSlow.getInterpolation(1 - value) * CIRCLE_RADIUS + CIRCLE_RADIUS);
        mSmallCircleRadius = (int) (CIRCLE_RADIUS - fastToSlow.getInterpolation(1 - value) * CIRCLE_RADIUS);
        // 画圆
        canvas.drawCircle(mX, mY, mSmallCircleRadius, mSmallPaint);
        canvas.drawCircle(mX, mY, mBigCircleRadius, mBigPaint);
    }

    // 更新边界值
    private void updateInCreaseValue() {
        if (mBigCircleRadius >= MAX_CIRCLE_RADIUS) {
            increaseValue = -1 * INCREASE_VALUE;
        } else if (mBigCircleRadius <= (CIRCLE_RADIUS)) {
            increaseValue = INCREASE_VALUE;
        }
    }
}
