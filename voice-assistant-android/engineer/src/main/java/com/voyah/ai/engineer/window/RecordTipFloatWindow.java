package com.voyah.ai.engineer.window;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.inspector.WindowInspector;

import androidx.databinding.DataBindingUtil;

import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.Utils;
import com.voyah.ai.basecar.manager.DialogueManager;
import com.voyah.ai.engineer.EngineerActivity;
import com.voyah.ai.engineer.R;
import com.voyah.ai.engineer.databinding.FloatRecordTipBinding;
import com.voyah.ai.engineer.fragment.AudioLogSaveFragment;
import com.voyah.ai.sdk.bean.DhDirection;
import com.voyah.ai.sdk.bean.LifeState;
import com.voyah.ai.sdk.listener.IVAStateListener;

public class RecordTipFloatWindow extends AbsFloatWindow {

    private final FloatRecordTipBinding binding;
    private final float SLOP = ViewConfiguration.get(Utils.getApp()).getScaledTouchSlop();
    private long mLastTime;

    @SuppressLint("ClickableViewAccessibility")
    private RecordTipFloatWindow() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(Utils.getApp()), R.layout.float_record_tip, null, false);

        params = new WindowManager.LayoutParams(
                (int) (screenWidth * 0.13f),
                WindowManager.LayoutParams.WRAP_CONTENT,
                2033,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = screenWidth - 50;
        params.y = 100;

        binding.tvTitle.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialX = params.x;
                    initialY = params.y;
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    mLastTime = System.currentTimeMillis();
                    return true; // 拦截事件

                case MotionEvent.ACTION_UP:
                    float finalX = event.getRawX();
                    float finalY = event.getRawY();
                    float dx = finalX - initialTouchX;
                    float dy = finalY - initialTouchY;
                    if (System.currentTimeMillis() - mLastTime < 800) {
                        if (Math.abs(dx) < SLOP && Math.abs(dy) < SLOP) {
                            binding.getRoot().performClick();
                        }
                    }
                    return true;

                case MotionEvent.ACTION_MOVE:
                    float currentX = event.getRawX();
                    float currentY = event.getRawY();
                    float dX = currentX - initialTouchX;
                    float dY = currentY - initialTouchY;

                    // 如果移动距离超过阈值，则视为拖动
                    if (Math.abs(dX) > SLOP || Math.abs(dY) > SLOP) {
                        updateFloatingViewPosition(binding.getRoot(), event);
                    }
                    return true;
            }
            return false;
        });

        binding.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(Utils.getApp(), EngineerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Utils.getApp().startActivity(intent);
        });

    }

    @Override
    public void show() {
        if (isShowing()) {
            return;
        }
        windowManager.addView(binding.getRoot(), params);
        DialogueManager.get().registerStateCallback(vaStateListener);
    }

    @Override
    public void remove() {
        if (!isShowing()) {
            return;
        }
        windowManager.removeView(binding.getRoot());
        onFloatWindowDismiss();
        DialogueManager.get().unregisterStateListener(vaStateListener);
        binding.wakeupDirect.setText("初始状态");
    }

    private final IVAStateListener vaStateListener = state -> {
        if (LifeState.AWAKE.equals(state)) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.wakeupDirect.setTextColor(DialogueManager.get().getDirection() == DhDirection.FRONT_LEFT
                            ? Color.YELLOW : Color.BLACK);
                    binding.wakeupDirect.setText(TimeUtils.getNowString(dateFormat1) + " " +
                            DialogueManager.get().getDirectionNature() + "唤醒");
                }
            });
        } else if (LifeState.ASLEEP.equals(state)) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.wakeupDirect.setTextColor(Color.YELLOW);
                    binding.wakeupDirect.setText("未唤醒状态");
                }
            });
        }
    };

    private void onFloatWindowDismiss() {
        AudioLogSaveFragment fragment = fragmentRef.get();
        if (fragment != null && fragment.isAdded() && fragment.getView() != null) {
            fragment.onRecordTipWindowDismiss();
        }
    }

    @Override
    public boolean isShowing() {
        return WindowInspector.getGlobalWindowViews().contains(binding.getRoot());
    }

    private static class Holder {
        private static final RecordTipFloatWindow _INSTANCE = new RecordTipFloatWindow();
    }

    public static RecordTipFloatWindow get() {
        return RecordTipFloatWindow.Holder._INSTANCE;
    }
}