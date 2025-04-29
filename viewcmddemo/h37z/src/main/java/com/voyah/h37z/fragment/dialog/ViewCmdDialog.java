package com.voyah.h37z.fragment.dialog;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.lxj.xpopup.core.CenterPopupView;
import com.voyah.h37z.R;
import com.voyah.h37z.VpaViewModel;
import com.voyah.h37z.adapter.GridSpacingItemDecoration;
import com.voyah.h37z.adapter.CommonTipAdapter;
import com.voyah.h37z.databinding.DialogViewCmdBinding;
import com.voyah.viewcmd.aspect.VoiceRegisterView;

/**
 * 可见即可说弹窗
 */
public class ViewCmdDialog extends CenterPopupView {

    private VpaViewModel viewModel;
    private DialogViewCmdBinding binding;

    public ViewCmdDialog(@NonNull Context context, VpaViewModel viewModel) {
        super(context);
        this.viewModel = viewModel;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_view_cmd;
    }

    @VoiceRegisterView
    @Override
    protected void onCreate() {
        super.onCreate();
        binding = DataBindingUtil.bind(contentView);
        assert binding != null;

        binding.ivDialogClose.setOnClickListener(v -> dismiss());
        // 可见即可说提示列表
        binding.recyclerViewTips.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.recyclerViewTips.addItemDecoration(new GridSpacingItemDecoration(2, 32, false));
        CommonTipAdapter viewCmdAdapter = new CommonTipAdapter(viewModel.getViewCmdTipList());
        binding.recyclerViewTips.setAdapter(viewCmdAdapter);
    }

    /**
     * 弹窗的宽度，用来动态设定当前弹窗的宽度，受getMaxWidth()限制
     *
     * @return
     */
    protected int getPopupWidth() {
        return 1320;
    }

    /**
     * 弹窗的高度，用来动态设定当前弹窗的高度，受getMaxHeight()限制
     *
     * @return
     */
    protected int getPopupHeight() {
        return 1000;
    }
}