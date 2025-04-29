package com.voyah.h37z.fragment.dialog;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.voyah.h37z.R;
import com.voyah.viewcmd.aspect.VoiceRegisterView;

public class CustomPopWindow extends PopupWindow {
    private static final String TAG = "CustomPopWindow";
    private final View view;

    public CustomPopWindow(Activity context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.layout_float_window, null);//alt+ctrl+f
        initView();
        initPopWindow();
    }

    private void initView() {
        view.findViewById(R.id.button3).setOnClickListener(v -> dismiss());
    }

    @VoiceRegisterView
    private void initPopWindow() {
        this.setContentView(view);
        // 设置弹出窗体的宽
        this.setWidth(1000);
        // 设置弹出窗体的高
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        // 设置弹出窗体可点击()
        this.setFocusable(true);
        this.setOutsideTouchable(true);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        Log.d(TAG, "dismiss() called");
    }
}