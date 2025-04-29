package com.voyah.h37z.fragment.dialog;

import android.content.Context;
import android.util.ArrayMap;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.lxj.xpopup.core.CenterPopupView;
import com.voyah.h37z.R;
import com.voyah.h37z.VpaViewModel;
import com.voyah.h37z.databinding.DialogMainWakeupBinding;
import com.voyah.viewcmd.VoiceViewCmdManager;

/**
 * 主唤醒提示框
 */
public class MainWakeupDialog extends CenterPopupView implements View.OnClickListener {

    private VpaViewModel viewModel;
    private DialogMainWakeupBinding binding;

    public MainWakeupDialog(Context context, VpaViewModel viewModel) {
        super(context);
        this.viewModel = viewModel;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_main_wakeup;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        binding = DataBindingUtil.bind(contentView);
        assert binding != null;
        binding.dialogButtonConfirm.setOnClickListener(this);
        binding.dialogButtonCancel.setOnClickListener(this);

        ArrayMap<String, View> map = new ArrayMap<>();
        map.put("确定", binding.dialogButtonConfirm);
        map.put("取消", binding.dialogButtonCancel);
        VoiceViewCmdManager.getInstance().directRegister(this, map);
    }

    /**
     * 弹窗的宽度，用来动态设定当前弹窗的宽度，受getMaxWidth()限制
     *
     * @return
     */
    protected int getPopupWidth() {
        return 972;
    }

    /**
     * 弹窗的高度，用来动态设定当前弹窗的高度，受getMaxHeight()限制
     *
     * @return
     */
    protected int getPopupHeight() {
        return 640;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_button_confirm:
                viewModel.enableMainWakeup(false);
                dismiss();
                break;
            case R.id.dialog_button_cancel:
                dismiss();
                break;
        }
    }
}