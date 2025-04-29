package com.voyah.viewcmd;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.display.DisplayManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;

import androidx.appcompat.widget.AppCompatImageView;


/**
 * 可见即可说点击动效
 */
public class ClickRippleEffect extends AppCompatImageView {
    private static final String TAG = ClickRippleEffect.class.getSimpleName();

    private static WindowManager windowManager;
    private static WindowManager.LayoutParams params;
    private static ClickRippleEffect rippleView;
    public static final Handler uiHandler = new Handler(Looper.getMainLooper());

    public ClickRippleEffect(Context context) {
        super(context);
    }

    /**
     * 显示点击效果
     *
     * @param displayId 屏幕id
     * @param x         坐标
     * @param y         坐标
     * @param callback  回调
     */
    @SuppressLint("WrongConstant")
    public static void show(Context context, int displayId, final int x, final int y, final Callback callback) {
        Log.d(TAG, "show() called with: context = [" + context + "], displayId = [" + displayId + "], x = [" + x + "], y = [" + y + "]");
        uiHandler.post(() -> {
            try {
                windowManager = getWindowManager(context, displayId);
                if (rippleView == null) {
                    rippleView = new ClickRippleEffect(context);
                } else if (rippleView.getParent() != null) {
                    windowManager.removeView(rippleView);
                }
                if (params == null) {
                    params = new WindowManager.LayoutParams();
                    //创建非模态、不可碰触
                    params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                            | WindowManager.LayoutParams.TYPE_STATUS_BAR | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                    params.format = PixelFormat.TRANSPARENT;
                    //显示位置
                    params.gravity = Gravity.START | Gravity.TOP;
                    params.height = 140;
                    params.width = 140;
                    //View类型
                    params.type = VoiceViewCmdUtils.windowType;
                }
                params.x = x;
                params.y = y;
                windowManager.addView(rippleView, params);
                //开启动画
                startAnimation(R.drawable.click_ripple_animator, callback);
            } catch (Exception e) {
                e.printStackTrace();
                callback.onCallback();
            }

        });
    }

    private static WindowManager getWindowManager(Context context, int displayId) {
        windowManager = null;
        Display display = context.getSystemService(DisplayManager.class).getDisplay(displayId);
        if (display != null) {
            Context displayContext = context.createDisplayContext(display);
            windowManager = (WindowManager) displayContext.getSystemService(Context.WINDOW_SERVICE);
        }
        if (windowManager == null) {
            Log.e(TAG, "can not get windowManager according by displayId, displayId:" + displayId);
            windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return windowManager;
    }

    /**
     * 带动画监听的播放
     *
     * @param resId
     */
    private static void startAnimation(int resId, Callback callback) {
        rippleView.setImageResource(resId);
        AnimationDrawable anim = (AnimationDrawable) rippleView.getDrawable();
        anim.setOneShot(true);
        anim.start();

        // 计算动态图片所花费的事件
        int durationTime = 0;
        for (int i = 0; i < anim.getNumberOfFrames(); i++) {
            durationTime += anim.getDuration(i);
        }

        // 动画执行到一半
        uiHandler.postDelayed(() -> {
            if (callback != null) {
                callback.onCallback();
            }
        }, durationTime / 2);
        // 动画结束后
        uiHandler.postDelayed(() -> {
            if (windowManager != null && rippleView.getParent() != null) {
                windowManager.removeView(rippleView);
            }
        }, durationTime);
    }

    /**
     * 对外提供的回调方法
     */
    public interface Callback {
        void onCallback();
    }
}
