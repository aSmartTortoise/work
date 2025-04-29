package com.voyah.h37z;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

public class NoScrollViewPager extends ViewPager {

    private boolean isCanScroll = true;
    private boolean isHasScrollAnim = true;

    public NoScrollViewPager(Context context) {
        super(context);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置其是否能滑动
     *
     * @param isCanScroll false 禁止滑动， true 可以滑动
     */
    public void setCanScroll(boolean isCanScroll) {
        this.isCanScroll = isCanScroll;
    }

    /**
     * 设置是否去除滑动效果
     *
     * @param isHasScrollAnim false 去除滚动效果， true 不去除
     */
    public void setHasScrollAnim(boolean isHasScrollAnim) {
        this.isHasScrollAnim = isHasScrollAnim;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isCanScroll && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return isCanScroll && super.onTouchEvent(ev);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    /**
     * 设置其是否去求切换时的滚动动画
     * isHasScrollAnim为false时，会去除滚动效果
     */
    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item, isHasScrollAnim);
    }

}