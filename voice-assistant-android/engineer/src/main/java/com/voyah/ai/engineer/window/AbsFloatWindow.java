package com.voyah.ai.engineer.window;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.Utils;
import com.voyah.ai.engineer.fragment.AudioLogSaveFragment;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Locale;

public abstract class AbsFloatWindow {
    protected WindowManager windowManager;
    protected WindowManager.LayoutParams params;
    protected WeakReference<AudioLogSaveFragment> fragmentRef;
    protected int screenWidth, screenHeight;
    protected final SimpleDateFormat dateFormat1 = new SimpleDateFormat("HH:mm:ss", Locale.US);
    protected final SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy_MM_dd_HHmm", Locale.US);
    protected final SimpleDateFormat dateFormat3 = new SimpleDateFormat("HH:mm:ss:SSS", Locale.US);

    protected int initialX;
    protected int initialY;
    protected float initialTouchX;
    protected float initialTouchY;

    AbsFloatWindow() {
        windowManager = (WindowManager) Utils.getApp().getSystemService(Context.WINDOW_SERVICE);
        screenWidth = ScreenUtils.getScreenWidth();
        screenHeight = ScreenUtils.getScreenHeight();
    }

    public void setFragment(AudioLogSaveFragment fragment) {
        this.fragmentRef = new WeakReference<>(fragment);
    }

    public void updateFloatingViewPosition(View view, MotionEvent event) {
        params.x = initialX + (int) (event.getRawX() - initialTouchX);
        params.y = initialY + (int) (event.getRawY() - initialTouchY);

        // 边界检查
        if (params.x < 0) params.x = 0;
        if (params.y < 0) params.y = 0;

        if (params.x + view.getWidth() > screenWidth) {
            params.x = screenWidth - view.getWidth();
        }
        if (params.y + view.getHeight() > screenHeight) {
            params.y = screenHeight - view.getHeight();
        }

        // 更新悬浮窗位置
        windowManager.updateViewLayout(view, params);
    }

    public abstract boolean isShowing();

    public abstract void show();

    public abstract void remove();
}
