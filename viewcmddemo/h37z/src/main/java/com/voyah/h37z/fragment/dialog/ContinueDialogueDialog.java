package com.voyah.h37z.fragment.dialog;

import android.content.Context;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.lxj.xpopup.core.CenterPopupView;
import com.voyah.h37z.R;
import com.voyah.h37z.databinding.DialogContinueDialogueBinding;
import com.voyah.viewcmd.VoiceViewCmdManager;
import com.voyah.viewcmd.interceptor.IDirectRegisterInterceptor;

import java.util.Map;

/**
 * 连续对话弹窗
 */
public class ContinueDialogueDialog extends CenterPopupView {

    private static final String TAG = ContinueDialogueDialog.class.getSimpleName();
    private DialogContinueDialogueBinding binding;

    public ContinueDialogueDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_continue_dialogue;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() called");

        binding = DataBindingUtil.bind(contentView);
        assert binding != null;
        binding.ivDialogClose.setOnClickListener(v -> dismiss());

        ArrayMap<String, View> map = new ArrayMap<>();
        map.put("关闭|关闭弹窗", binding.ivDialogClose);
        VoiceViewCmdManager.getInstance().directRegister(this, true, map, new IDirectRegisterInterceptor() {
            @Override
            public Map<String, Integer> bind() {
                ArrayMap<String, Integer> arrayMap = new ArrayMap<>();
                arrayMap.put("返回", 10);
                return arrayMap;
            }

            @Override
            public boolean onTriggered(String text, int resId) {
                Log.d(TAG, "onTriggered() called with: text = [" + text + "], resId = [" + resId + "]");
                if (resId == 10) {
                    post(() -> Toast.makeText(getContext(), "执行返回", Toast.LENGTH_SHORT).show());
                    return true;
                }
                return false;
            }
        });
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
        return 800;
    }
}