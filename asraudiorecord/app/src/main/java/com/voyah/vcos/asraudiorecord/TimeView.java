package com.voyah.vcos.asraudiorecord;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.voyah.vcos.asraudiorecord.util.LogUtils;


public class TimeView extends android.support.v7.widget.AppCompatTextView {
    private int time = 0;

    private ITimeListener timeListener;

    public TimeView(Context context) {
        super(context);
    }

    public TimeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            time -= 100;
            if (time <= 0) {
                time = 0;
                handler.removeCallbacksAndMessages(null);
                if (timeListener != null) {
                    timeListener.timeDone();
                }
            }
            updateView();
        }
    };

    public void setTime(int time) {
        handler.removeCallbacksAndMessages(null);
        this.time = time;
        updateView();

    }

    private void updateView() {
        setText(String.valueOf(time));
        handler.removeCallbacksAndMessages(null);
        if (time > 0) {
            handler.sendEmptyMessageDelayed(0, 100);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacksAndMessages(null);
    }

    public void pause() {
        handler.removeCallbacksAndMessages(null);
    }

    public void setTimeListener(ITimeListener timeListener) {
        this.timeListener = timeListener;
    }
}
