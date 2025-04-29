package com.voyah.ai.engineer.window;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.text.Layout;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inspector.WindowInspector;

import androidx.databinding.DataBindingUtil;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.Utils;
import com.voyah.ai.basecar.manager.DialogueManager;
import com.voyah.ai.basecar.manager.ExtVAResultListener;
import com.voyah.ai.engineer.R;
import com.voyah.ai.engineer.databinding.FloatSavePathChooseBinding;
import com.voyah.ai.engineer.databinding.FloatTracePanelBinding;
import com.voyah.ai.engineer.fragment.AudioLogSaveFragment;
import com.voyah.ai.engineer.utils.SpannableBuilder;
import com.voyah.ai.engineer.utils.StorageUtil;
import com.voyah.ai.engineer.utils.ToastUtil;
import com.voyah.ai.sdk.bean.LifeState;
import com.voyah.ai.sdk.bean.NluResult;
import com.voyah.ai.sdk.listener.IVAResultListener;
import com.voyah.ai.sdk.listener.IVAStateListener;

import java.nio.charset.StandardCharsets;

public class TraceFloatWindow extends AbsFloatWindow {

    private final FloatTracePanelBinding binding;
    private boolean smallMode = false;

    @SuppressLint("ClickableViewAccessibility")
    private TraceFloatWindow() {

        binding = DataBindingUtil.inflate(LayoutInflater.from(Utils.getApp()), R.layout.float_trace_panel, null, false);

        params = new WindowManager.LayoutParams(
                (int) (screenWidth * 0.36f),
                (int) (screenHeight * 0.7f),
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 100;
        params.y = 200;

        binding.tvTitle.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialX = params.x;
                    initialY = params.y;
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    return true;
                case MotionEvent.ACTION_UP:
                    return true;
                case MotionEvent.ACTION_MOVE:
                    updateFloatingViewPosition(binding.getRoot(), event);
                    return true;
            }
            return false;
        });

        binding.closeButton.setOnClickListener(v -> {
            remove();
        });

        binding.saveButton.setOnClickListener(v -> {
            showSavePathDialog();
        });

        binding.refreshButton.setOnClickListener(v -> {
            binding.scrollableText.scrollTo(0, 0);
            binding.scrollableText.setText("");
        });

        binding.scaleButton.setOnClickListener(v -> {
            smallMode = !smallMode;
            params.width = smallMode ? (int) (screenWidth * 0.36f) / 2 : (int) (screenWidth * 0.36f);
            params.height = smallMode ? (int) (screenHeight * 0.7f) / 2 : (int) (screenHeight * 0.7f);
            windowManager.updateViewLayout(binding.getRoot(), params);
            binding.saveButton.setVisibility(smallMode ? View.GONE : View.VISIBLE);
            binding.refreshButton.setVisibility(smallMode ? View.GONE : View.VISIBLE);
        });

        binding.scrollableText.setMovementMethod(new ScrollingMovementMethod());
    }

    private void traceVoiceDialogue() {
        DialogueManager.get().registerStateCallback(vaStateListener);
        DialogueManager.get().registerResultListener(vaResultListener);
    }

    private void unregisterTraceDialogue() {
        DialogueManager.get().unregisterStateListener(vaStateListener);
        DialogueManager.get().unregisterResultListener(vaResultListener);
    }

    @Override
    public void show() {
        if (isShowing()) {
            return;
        }
        windowManager.addView(binding.getRoot(), params);
        traceVoiceDialogue();
    }

    @Override
    public void remove() {
        if (!isShowing()) {
            return;
        }
        windowManager.removeView(binding.getRoot());
        unregisterTraceDialogue();
        onFloatWindowDismiss();
    }

    private void onFloatWindowDismiss() {
        AudioLogSaveFragment fragment = fragmentRef.get();
        if (fragment != null && fragment.isAdded() && fragment.getView() != null) {
            fragment.onTraceWindowDismiss();
        }
    }

    @Override
    public boolean isShowing() {
        return WindowInspector.getGlobalWindowViews().contains(binding.getRoot());
    }

    private final IVAStateListener vaStateListener = state -> {
        String text = null;
        if (LifeState.READY.equals(state)) {
            text = "语音初始化成功";
        } else if (LifeState.AWAKE.equals(state)) {
            text = "开启对话, 唤醒音区:" + DialogueManager.get().getDirectionNature();
        } else if (LifeState.ASLEEP.equals(state)) {
            text = "退出对话";
        } else if (LifeState.INPUTTING.equals(state)) {
            text = "检测到人声开始";
        } else if (LifeState.RECOGNIZING.equals(state)) {
            text = "检测到人声结束";
        }
        if (text != null) {
            SpannableString spanStr = SpannableBuilder.withText(format(text))
                    .setColor(Color.YELLOW)
                    .build();
            appendText(spanStr);
        }

    };

    private final IVAResultListener vaResultListener = new ExtVAResultListener() {
        @Override
        public void onAsr(boolean isOnline, String text) {
            appendText(format((isOnline ? "在线" : "离线") + "asr结果=" + text));
        }

        @Override
        public void onTts(String text) {
            appendText(format("tts结果=" + text));
        }

        @Override
        public void onNluResult(NluResult result) {
            appendText(format("requestId=" + result.id));
            appendText(format((result.isOnline ? "在线" : "离线") + "nlu结果=" + result.rawNlu));
        }
    };

    // 追加文本并滚动到底部
    public void appendText(CharSequence text) {
        ThreadUtils.runOnUiThread(() -> {
            binding.scrollableText.append(text);
            Layout layout = binding.scrollableText.getLayout();
            if (layout != null) {
                int scrollAmount = layout.getLineTop(binding.scrollableText.getLineCount())
                        - binding.scrollableText.getHeight() + 48;
                if (scrollAmount < 100) {
                    binding.scrollableText.scrollTo(0, 0);
                } else {
                    binding.scrollableText.scrollTo(0, scrollAmount);
                }
            }
        });
    }

    private void showSavePathDialog() {
        FloatSavePathChooseBinding savePathBinding = DataBindingUtil.inflate(LayoutInflater.from(Utils.getApp()),
                R.layout.float_save_path_choose, null, false);

        WindowManager.LayoutParams wl = new WindowManager.LayoutParams();
        wl.gravity = Gravity.CENTER;
        wl.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        wl.width = 600;
        wl.height = 300;
        wl.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        wl.format = PixelFormat.TRANSPARENT;
        savePathBinding.btPathUdisk.setOnClickListener(v -> {
            if (!StorageUtil.isUsbConnected()) {
                ToastUtil.showToast("U盘未连接，请先连接上U盘");
                return;
            }
            windowManager.removeView(savePathBinding.getRoot());
            saveTraceRecord(true);
        });
        savePathBinding.btPathSdcard.setOnClickListener(v -> {
            windowManager.removeView(savePathBinding.getRoot());
            saveTraceRecord(false);
        });
        savePathBinding.btPathClose.setOnClickListener(v -> windowManager.removeView(savePathBinding.getRoot()));
        windowManager.addView(savePathBinding.getRoot(), wl);
    }

    private void saveTraceRecord(boolean isUDisk) {
        LogUtils.d("saveTraceRecord() called with: isUDisk = [" + isUDisk + "]");
        String savePath;
        if (isUDisk) {
            savePath = StorageUtil.getUDiskPath() + "/VoiceTraceLog_" +
                    TimeUtils.millis2String(System.currentTimeMillis(), dateFormat2) + ".txt";
        } else {
            savePath = "/sdcard/VoiceTraceLog_" +
                    TimeUtils.millis2String(System.currentTimeMillis(), dateFormat2) + ".txt";
        }
        byte[] bytes = binding.scrollableText.getText().toString().getBytes(StandardCharsets.UTF_8);
        boolean ret = StorageUtil.writeFileFromBytesByStream(savePath, bytes, false);
        LogUtils.d("writeFileFromBytesByStream ret:" + ret);
        ToastUtil.showToast("保存对话记录成功，保存路径：" + savePath);
    }

    private String format(String text) {
        return TimeUtils.getNowString(dateFormat3) + " " + text + '\n';
    }

    private static class Holder {
        private static final TraceFloatWindow _INSTANCE = new TraceFloatWindow();
    }

    public static TraceFloatWindow get() {
        return TraceFloatWindow.Holder._INSTANCE;
    }
}