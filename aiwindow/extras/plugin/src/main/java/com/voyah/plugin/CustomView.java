package com.voyah.plugin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;

/**
 * 将会加载到宿主app中的页面
 */
public class CustomView extends FrameLayout implements View.OnClickListener {

    private static final String TAG = "CustomView";
    private Activity hostActivity;
    private TextView textView;

    public CustomView(Context context) {
        this(context, null);
    }

    public CustomView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.d(TAG, "CustomView");
        initView(context);
    }

    private void initView(Context context) {
        Log.d(TAG, "initView() called with: context = [" + context + "]");
        LayoutInflater.from(context).inflate(R.layout.custom_layout, this);
        Button btnJump = findViewById(R.id.bt_jump);
        Button btnJump2 = findViewById(R.id.bt_jump2);
        btnJump.setOnClickListener(this);
        btnJump2.setOnClickListener(this);
        textView = findViewById(R.id.tv_title);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //插件需要使用Activity时，必须在这调用这句代码
        hostActivity = getActivity(getContext());
        Log.d(TAG, "onAttachedToWindow activity:" + hostActivity);
        Log.d(TAG, "data:" + getTag());
        textView.setText((String) getTag());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_jump:
                Log.d(TAG, "Button jump onClick111");
                Toast.makeText(getContext(), "我是TOAST", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_jump2:
                // 创建一个 AlertDialog.Builder 对象
                if (hostActivity == null) {
                    Toast.makeText(getContext(), "AI小窗没有Activity, 无法传递宿主activity给插件", Toast.LENGTH_SHORT).show();
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(hostActivity);

                // 设置对话框的标题和消息
                builder.setTitle("Test Dialog")
                        .setMessage("This is a test dialog.")
                        .setPositiveButton("OK", (dialog, which) -> {
                            // 点击确定按钮后的操作
                            System.out.println("OK button clicked");
                            dialog.dismiss();
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            // 点击取消按钮后的操作
                            System.out.println("Cancel button clicked");
                            dialog.dismiss();
                        });

                // 创建并显示 AlertDialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;
        }
    }

    private Activity getActivity(Context context) {
        try {
            Class contextImplClass = Class.forName("android.app.ContextImpl");
            Field outerContextField = contextImplClass.getDeclaredField("mOuterContext");
            outerContextField.setAccessible(true);
            Activity activity = (Activity) outerContextField.get(context);
            return activity;
        } catch (Exception e) {
            Log.d(TAG, "exception:" + e);
            e.printStackTrace();
            return null;
        }
    }
}
