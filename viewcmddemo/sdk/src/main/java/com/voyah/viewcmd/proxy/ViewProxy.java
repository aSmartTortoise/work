package com.voyah.viewcmd.proxy;

import android.view.View;

import com.voyah.viewcmd.OnScrollViewListener;

public abstract class ViewProxy {
    protected OnScrollViewListener onScrollViewListener;

    public void setOnScrollViewListener(OnScrollViewListener listener) {
        this.onScrollViewListener = listener;
    }

    protected final View.OnAttachStateChangeListener onAttachListener = new View.OnAttachStateChangeListener() {
        @Override
        public void onViewAttachedToWindow(View v) {
        }

        @Override
        public void onViewDetachedFromWindow(View v) {
            if (onScrollViewListener != null) {
                onScrollViewListener.onDetachedFromWindow();
            }
        }
    };

    public abstract void init();

    public abstract void destroy();
}
