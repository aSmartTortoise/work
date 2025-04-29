package com.voyah.ai.engineer.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.voyah.ai.engineer.R;
import com.voyah.ai.engineer.databinding.DialogClearStorageBinding;

public class ClearStorageDialog extends Dialog implements View.OnClickListener {
    private DialogClearStorageBinding binding;
    private View.OnClickListener cancelListener, confirmListener;
    private String tip;

    public ClearStorageDialog(@NonNull Context context) {
        super(context);
    }

    public ClearStorageDialog setTip(String tip) {
        this.tip = tip;
        return this;
    }

    public ClearStorageDialog setConfirmListener(View.OnClickListener listener) {
        this.confirmListener = listener;
        return this;
    }

    public ClearStorageDialog setCancelListener(View.OnClickListener listener) {
        this.cancelListener = listener;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_clear_storage, null, false);
        setContentView(binding.getRoot());
        setCancelable(false);
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        if (!TextUtils.isEmpty(tip)) {
            binding.tvMessage.setText(tip);
        }
        binding.btConfirm.setOnClickListener(this);
        binding.btCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.btConfirm) {
            dismiss();
            if (confirmListener != null) {
                confirmListener.onClick(view);
            }
        } else if (view == binding.btCancel) {
            dismiss();
            if (cancelListener != null) {
                cancelListener.onClick(view);
            }
        }
    }
}