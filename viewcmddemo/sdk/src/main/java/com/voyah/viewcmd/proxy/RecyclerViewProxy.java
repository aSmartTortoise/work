package com.voyah.viewcmd.proxy;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.voyah.viewcmd.AntiShake;

public class RecyclerViewProxy extends ViewProxy {

    private final RecyclerView recyclerView;

    public RecyclerViewProxy(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Override
    public void init() {
        recyclerView.post(() -> {
            recyclerView.addOnScrollListener(onScrollListener);
            recyclerView.addOnAttachStateChangeListener(onAttachListener);
        });
    }

    private final RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (AntiShake.check(150)) {
                    return;
                }
                if (onScrollViewListener != null) {
                    onScrollViewListener.onScrollStop();
                }
            }
        }
    };

    @Override
    public void destroy() {
        recyclerView.removeOnAttachStateChangeListener(onAttachListener);
        recyclerView.removeOnScrollListener(onScrollListener);
    }
}
