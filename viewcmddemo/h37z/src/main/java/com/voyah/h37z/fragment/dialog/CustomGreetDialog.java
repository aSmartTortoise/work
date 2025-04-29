package com.voyah.h37z.fragment.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.lxj.xpopup.core.CenterPopupView;
import com.voyah.h37z.R;
import com.voyah.h37z.VpaViewModel;
import com.voyah.h37z.databinding.DialogCustomGreetBinding;

import java.util.Map;

/**
 * 自定义欢迎语提示框
 */
public class CustomGreetDialog extends CenterPopupView {

    private int location;
    private VpaViewModel viewModel;
    private DialogCustomGreetBinding binding;

    private final Map<Integer, String> LOCATION_NAMES = new ArrayMap<>();

    public CustomGreetDialog(Context context, int location, VpaViewModel viewModel) {
        super(context);
        this.viewModel = viewModel;
        this.location = location;

        LOCATION_NAMES.put(1, context.getString(R.string.settings_item_custom_greet_0));
        LOCATION_NAMES.put(2, context.getString(R.string.settings_item_custom_greet_1));
        LOCATION_NAMES.put(4, context.getString(R.string.settings_item_custom_greet_2));
        LOCATION_NAMES.put(8, context.getString(R.string.settings_item_custom_greet_3));
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_custom_greet;
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        binding = DataBindingUtil.bind(contentView);
        assert binding != null;

        binding.etCustomGreet.setText(viewModel.getCustomGreet(location));
        binding.etCustomGreet.setSelection(binding.etCustomGreet.getText().length());

        String title = String.format(getContext().getString(R.string.dialog_custom_greet_title), LOCATION_NAMES.get(location));
        binding.tvTitle.setText(title);

        findViewById(R.id.iv_delete).setOnClickListener(v -> {
            String text = binding.etCustomGreet.getText().toString();
            if (!TextUtils.isEmpty(text)) {
                String newText = text.substring(0, text.length() - 1);
                binding.etCustomGreet.setText(newText);
                binding.etCustomGreet.setSelection(newText.length());
            }
        });

        binding.dialogButtonConfirm.setOnClickListener(v -> {
            String text = binding.etCustomGreet.getText().toString().trim();
            if (text.length() < 1 || text.length() > 6) {
                Toast.makeText(getContext(), R.string.toast_error_length, Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.setCustomGreet(location, text);
            dismiss();
        });
        binding.dialogButtonCancel.setOnClickListener(v -> dismiss());
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
        return 800;
    }
}