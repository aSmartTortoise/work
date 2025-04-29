package com.voyah.viewcmd.proxy;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

public class ScrollViewProxy extends ViewProxy {

    private final View scrollView;
    private final Handler handler;
    private final Runnable scrollRunnable;
    // 设置延迟时间，根据实际需求调整
    private static final int DELAY_SCROLL_STOP = 300;

    public ScrollViewProxy(View scrollView) {
        this.scrollView = scrollView;
        handler = new Handler(Looper.getMainLooper());
        scrollRunnable = () -> {
            if (onScrollViewListener != null) {
                onScrollViewListener.onScrollStop();
            }
        };
    }

    @Override
    public void init() {
        scrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            handler.removeCallbacks(scrollRunnable);
            handler.postDelayed(scrollRunnable, DELAY_SCROLL_STOP);
        });

        scrollView.addOnAttachStateChangeListener(onAttachListener);
    }

    @Override
    public void destroy() {
        scrollView.removeOnAttachStateChangeListener(onAttachListener);
        scrollView.setOnScrollChangeListener(null);
    }

}