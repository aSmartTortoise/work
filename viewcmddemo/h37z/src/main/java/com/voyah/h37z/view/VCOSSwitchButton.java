package com.voyah.h37z.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import androidx.annotation.Nullable;

import com.voyah.h37z.R;

public class VCOSSwitchButton extends View implements View.OnClickListener {
    private int mCheckColor = Color.parseColor("#1156E1");
    private int mUnCheckColor = Color.parseColor("#878B96");
    private Paint mOutPaint;
    private Paint mSwitchPaint;
    private RectF mDrawRoundRectF = new RectF();
    private int mSwitchColor = Color.WHITE;
    private boolean mCheck = false;
    private int mRadius;
    private int mRx;
    private SwitchListener mSwitchListener;
    private boolean mDisable;
    private int startX;

    public interface SwitchListener {
        void changeCheck(boolean check);
    }

    public VCOSSwitchButton(Context context) {
        this(context, null);
    }

    public VCOSSwitchButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VCOSSwitchButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VCOSSwitchButton);

            mCheckColor = typedArray.getColor(R.styleable.VCOSSwitchButton_check_color, Color.parseColor("#1156E1"));
            mUnCheckColor = typedArray.getColor(R.styleable.VCOSSwitchButton_uncheck_color, Color.parseColor("#878B96"));
            mSwitchColor = typedArray.getColor(R.styleable.VCOSSwitchButton_switchColor, Color.WHITE);
            mCheck = typedArray.getBoolean(R.styleable.VCOSSwitchButton_check, false);
            mDisable = typedArray.getBoolean(R.styleable.VCOSSwitchButton_disable, false);

            typedArray.recycle();
        }
        mOutPaint = new Paint();
        mSwitchPaint = new Paint();

        mOutPaint.setColor(mUnCheckColor);
        mOutPaint.setAntiAlias(true);
        mSwitchPaint.setColor(mSwitchColor);
        mSwitchPaint.setAntiAlias(true);

        setOnClickListener(this);
        setClickable(!mDisable);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        mDrawRoundRectF.set(0, 0, getWidth(), getHeight());
        mRadius = getHeight() / 2 - dipToPx(8);

        if (mDisable) {
            mOutPaint.setAlpha(10);
        }

        mOutPaint.setColor(mCheck ? mCheckColor : mUnCheckColor);
        canvas.drawRoundRect(mDrawRoundRectF, getHeight() / 2,
                getHeight() / 2, mOutPaint);

        if (mRx == 0) {
            if (!mCheck) {
                mRx = mRadius + dipToPx(8);
            } else {
                mRx = getWidth() - getHeight() / 2;
            }
        }
        canvas.drawCircle(mRx, getHeight() / 2, mRadius, mSwitchPaint);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.AT_MOST) {//warp_content 给个默认的
            width = dipToPx(80);
            height = width / 2;
        } else if (heightMode == MeasureSpec.AT_MOST && widthMode == MeasureSpec.EXACTLY) {
            height = width / 2;
        }

        height = Math.min(width / 2, height);//高度不能大于 宽度的一半

        setMeasuredDimension(width, height);
    }


    private int dipToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
    }

    private void drawAnim(boolean anim) {
        ValueAnimator valueAnimator = ObjectAnimator.ofInt(mCheck ? getHeight() / 2 : getWidth() - getHeight() / 2
                , mCheck ? getWidth() - getHeight() / 2 : getHeight() / 2);
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        if (anim) {
            valueAnimator.setDuration(300);
        } else {
            valueAnimator.setDuration(0);
        }

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mRx = (int) animation.getAnimatedValue();
                invalidate();
            }
        });

        valueAnimator.start();

        if (anim) {//如果是用户点击，做动画并回调状态改变，否则是数据改变导致，不需要重新回调，不然会导致状态和数据更改循环
            if (mSwitchListener != null) {
                mSwitchListener.changeCheck(mCheck);
            }
        }
    }


    public void setSwitchListener(SwitchListener switchListener) {
        mSwitchListener = switchListener;
    }

    @Override
    public void onClick(View v) {
        if (!mDisable) {
            mCheck = !mCheck;
            setSelected(mCheck);
            drawAnim(true);
        }
    }

    public void setChecked(boolean isChecked) {
        mCheck = isChecked;
        setSelected(isChecked);
        drawAnim(false);
    }

    public Boolean isChecked() {
        return mCheck;
    }

    public void setSwitchColor(int checkColor, int unCheckColor) {
        mCheckColor = checkColor;
        mUnCheckColor = unCheckColor;
        invalidate();
    }

    public void setDisable(boolean disable) {
        mDisable = disable;
        setOnClickListener(this);
        setClickable(!mDisable);
    }
}
