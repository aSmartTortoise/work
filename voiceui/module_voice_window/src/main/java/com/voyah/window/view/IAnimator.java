package com.voyah.window.view;

import android.graphics.Canvas;

/**
 * author : jie wang
 * date : 2024/11/29 17:23
 * description :
 */
public interface IAnimator {
    void onLayout(int width, int height);

    void onDraw(Canvas canvas);
}
