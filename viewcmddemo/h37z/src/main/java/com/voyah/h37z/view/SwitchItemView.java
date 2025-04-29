package com.voyah.h37z.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
//import com.vcos.common.widgets.vcosswitch.VcosSwitch;

import com.vcos.common.widgets.vcosswitch.VcosSwitch;
import com.voyah.h37z.R;
import com.voyah.h37z.databinding.ViewSwitchItemBinding;


/***
 * 带Switch的ItemView
 * 带有一行文字，右下角有图标
 */
public class SwitchItemView extends FrameLayout {
    private static final String TAG = "SwitchItemView";
    private ViewSwitchItemBinding mBinding;

    public SwitchItemView(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        mBinding = ViewSwitchItemBinding.inflate(LayoutInflater.from(context), this, true);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SwitchItemView);
        String label = typedArray.getString(R.styleable.SwitchItemView_labelText);
        boolean showClickIcon = typedArray.getBoolean(R.styleable.SwitchItemView_showClickIcon, true);
        mBinding.ivIcon.setVisibility(showClickIcon ? VISIBLE : GONE);
        mBinding.tvLabel.setText(label);
        typedArray.recycle();
        //mBinding.btnSwitch.setIsNeedLoading(false);
    }

    public SwitchItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SwitchItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void setItemEnabled(boolean enabled) {
        mBinding.btnSwitch.setEnabled(enabled);
        mBinding.tvLabel.setEnabled(enabled);
    }

    public void setChecked(boolean checked) {
        if (mBinding != null) {
            mBinding.btnSwitch.setChecked(checked);
        } else {
            Log.w(TAG, "setChecked: binding is null");
        }
    }

    public void setLabel(String label) {
        if (mBinding != null) {
            mBinding.tvLabel.setText(label);
        } else {
            Log.w(TAG, "setLabel: binding is null");
        }
    }

    public void setSwitchListener(VcosSwitch.OnCheckedChangeListener changeListener) {
        if (mBinding != null) {
            mBinding.btnSwitch.setOnCheckedChangeListener(changeListener);
        } else {
            Log.w(TAG, "setSwitchListener: binding is null ");
        }
    }


    public void setClickListener(OnClickListener clickListener) {
        if (mBinding != null) {
            mBinding.getRoot().setOnClickListener(clickListener);
        } else {
            Log.w(TAG, "setClickListener: binding is null ");
        }
    }
}
