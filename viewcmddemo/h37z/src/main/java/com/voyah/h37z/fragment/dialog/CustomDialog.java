package com.voyah.h37z.fragment.dialog;
 
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
 
import androidx.annotation.NonNull;

import com.voyah.h37z.R;
import com.voyah.viewcmd.aspect.VoiceRegisterView;


public class CustomDialog extends Dialog implements View.OnClickListener {
    
    private TextView mTitle, mMessage, mConfirm, mCancel;
    private String sTitle, sMessage, sConfirm, sCancel;
    private View.OnClickListener cancelListener, confirmListener;
 
    public CustomDialog setsTitle(String sTitle) {
        this.sTitle = sTitle;
        return this;
    }
 
    public CustomDialog setsMessage(String sMessage) {
        this.sMessage = sMessage;
        return this;
    }
 
    public CustomDialog setsConfirm(String sConfirm, View.OnClickListener listener) {
        this.sConfirm = sConfirm;
        this.confirmListener = listener;
        return this;
    }
 
    public CustomDialog setsCancel(String sCancel, View.OnClickListener listener) {
        this.sCancel = sCancel;
        this.cancelListener = listener;
        return this;
    }
 
 
    public CustomDialog(@NonNull Context context) {
        super(context);
    }
 
    public CustomDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_custom_dialog);
 
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
 
        setCancelable(true);
           
        //自定义Dialog宽度
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        Point size = new Point();
        d.getSize(size);
        p.width = (int) ((size.x)*0.7);        //设置为屏幕的0.7倍宽度
        getWindow().setAttributes(p);
 
 
        mTitle = findViewById(R.id.title);
        mMessage = findViewById(R.id.message);
        mCancel = findViewById(R.id.cancel);
        mConfirm = findViewById(R.id.confirm);
 
        if (!TextUtils.isEmpty(sTitle)) {
            mTitle.setText(sTitle);
        }
        if (!TextUtils.isEmpty(sMessage)) {
            mMessage.setText(sMessage);
        }
        if (!TextUtils.isEmpty(sCancel)) {
            mCancel.setText(sCancel);
        }
        if (!TextUtils.isEmpty(sConfirm)) {
            mConfirm.setText(sConfirm);
        }
 
        mConfirm.setOnClickListener(this);
        mCancel.setOnClickListener(this);
    }
    
    @VoiceRegisterView
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("xyj_test", "onStart() called");

        //  这个注册方式更安全，onStart被回调时有时getWindow().getDecorView()并未完全就绪
//        if(getWindow() != null) {
//            getWindow().getDecorView().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
//                @VoiceRegisterView
//                @Override
//                public void onViewAttachedToWindow(View v) {
//                }
//
//                @Override
//                public void onViewDetachedFromWindow(View v) {
//                    v.removeOnAttachStateChangeListener(this);
//                }
//            });
//        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.confirm:
                if(confirmListener != null){
                    confirmListener.onClick(view);
                }
                break;
            case R.id.cancel:
                if(cancelListener != null){
                    cancelListener.onClick(view);
                }
                break;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d("CustomDialog", "CustomDialog onWindowFocusChanged() called with: hasFocus = [" + hasFocus + "]");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("CustomDialog", "onStop() called");
    }
}