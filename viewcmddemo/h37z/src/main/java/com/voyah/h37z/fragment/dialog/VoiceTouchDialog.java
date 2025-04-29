package com.voyah.h37z.fragment.dialog;

import android.content.Context;
import android.graphics.Color;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lxj.xpopup.core.CenterPopupView;
import com.voyah.h37z.R;
import com.voyah.h37z.SpannableUtils;
import com.voyah.h37z.VpaViewModel;
import com.voyah.h37z.adapter.GridSpacingItemDecoration;
import com.voyah.h37z.adapter.CommonTipAdapter;
import com.voyah.h37z.databinding.DialogVoiceTouchBinding;
import com.voyah.viewcmd.ClickRippleEffect;
import com.voyah.viewcmd.GestureUtils;
import com.voyah.viewcmd.Response;
import com.voyah.viewcmd.aspect.VoiceInterceptor;
import com.voyah.viewcmd.aspect.VoiceRegisterView;
import com.voyah.viewcmd.interceptor.IInterceptor;
import com.voyah.viewcmd.interceptor.SimpleInterceptor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 一语即达弹窗
 */
public class VoiceTouchDialog extends CenterPopupView {
    private static final String TAG = VoiceTouchDialog.class.getSimpleName();

    private VpaViewModel viewModel;
    private DialogVoiceTouchBinding binding;

    public VoiceTouchDialog(@NonNull Context context, VpaViewModel viewModel) {
        super(context);
        this.viewModel = viewModel;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_voice_touch;
    }

    @VoiceRegisterView
    @Override
    protected void onCreate() {
        super.onCreate();
        binding = DataBindingUtil.bind(contentView);
        assert binding != null;

        binding.ivDialogClose.setOnClickListener(v -> dismiss());

        // 设置颜色
        List<TextView> textViews = Arrays.asList(binding.tvDescLeft, binding.tvDescRight);
        for (TextView tv : textViews) {
            String text = tv.getText().toString();
            int index = text.contains("，") ? text.indexOf("，") : text.indexOf(",");
            SpannableUtils.formatString(tv, text, 0, index, Color.parseColor("#333333"));
        }

        // 提示列表
        RecyclerView tipsRecycler = findViewById(R.id.recyclerView_tips);
        tipsRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        tipsRecycler.addItemDecoration(new GridSpacingItemDecoration(2, 32, false));
        CommonTipAdapter viewCmdAdapter = new CommonTipAdapter(viewModel.getVoiceTouchTipList());
        tipsRecycler.setAdapter(viewCmdAdapter);
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
        Log.d(TAG, "onDismiss() called");
    }

    @VoiceInterceptor
    private final IInterceptor<String> interceptor = new SimpleInterceptor() {

        @Override
        public Map<String, Integer> bind() {
            ArrayMap<String, Integer> resIdMap = new ArrayMap<>();
            resIdMap.put("切至前排", 10);
            resIdMap.put("切至后排", 11);
            return resIdMap;
        }


        @Override
        public Response onTriggered(View view, String text, int resId) {
            if (resId == 10) {
                if (isFront()) {
                    // todo
                    return Response.response("好的");
                } else {
                    return Response.response("当前已经是前排了");
                }
            }
            return null;
        }
    };

    private boolean isFront() {
        return true;
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