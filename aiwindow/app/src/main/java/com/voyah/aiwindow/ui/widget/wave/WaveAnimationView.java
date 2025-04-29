package com.voyah.aiwindow.ui.widget.wave;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.blankj.utilcode.util.LogUtils;
import com.voyah.ai.sdk.bean.DhDirection;
import com.voyah.aiwindow.R;

public class WaveAnimationView extends FrameLayout {

    private final Context mContext;

    /**
     * 加载不同状态动画的view
     */
    private WaveSurfaceView waveSurfaceView;

    public int direction = -1;

    public WaveAnimationView(Context context) {
        this(context, null);
    }

    public WaveAnimationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveAnimationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        initViews();
    }

    private void initViews() {
        LogUtils.i("initViews()");
        LayoutInflater.from(mContext).inflate(R.layout.view_wave, this);
        waveSurfaceView = findViewById(R.id.wave_anim_icon);
    }

    public void startWave(@DhDirection int direction) {
        this.direction = direction;
        if (null != waveSurfaceView) {
            waveSurfaceView.setDirection(direction);
            waveSurfaceView.startDraw(WaveState.LISTENING);
        }
    }

    @Deprecated  // 目前只有一个状态，不需要切换
    public void startListening() {
        if (waveSurfaceView != null) {
            waveSurfaceView.startDraw(WaveState.LISTENING);
        }
    }

    public void stop() {
        LogUtils.d("stop");
        if (waveSurfaceView != null) {
            waveSurfaceView.stop();
            waveSurfaceView.setVisibility(View.GONE);
        }
        direction = -1;
    }

    public void release() {
        LogUtils.d("release");
        removeAllViews();
        if (null != waveSurfaceView) {
            waveSurfaceView.release();
            waveSurfaceView = null;
        }
        direction = -1;
    }
}
