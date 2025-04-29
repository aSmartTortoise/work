package com.voyah.h37z.fragment.dialog;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.lxj.xpopup.core.CenterPopupView;
import com.voyah.h37z.R;
import com.voyah.h37z.adapter.GlobalWakeupAdapter;
import com.voyah.h37z.databinding.DialogGlobalWakeupBinding;
import com.voyah.viewcmd.aspect.VoiceRegisterView;

import q.rorbin.verticaltablayout.VerticalTabLayout;
import q.rorbin.verticaltablayout.widget.TabView;


/**
 * 免唤醒提示框
 */
public class GlobalWakeupDialog extends CenterPopupView {
    private static final String TAG = GlobalWakeupDialog.class.getSimpleName();
    private DialogGlobalWakeupBinding binding;

    public GlobalWakeupDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_global_wakeup;
    }

    @VoiceRegisterView
    @Override
    protected void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() called");

        binding = DataBindingUtil.bind(contentView);
        assert binding != null;

        GlobalWakeupAdapter globalWakeupAdapter = new GlobalWakeupAdapter(getContext());
        binding.viewPager.setAdapter(globalWakeupAdapter);
        binding.viewPager.setCanScroll(false);
        binding.viewPager.setHasScrollAnim(false);
        binding.tabLayout.setupWithViewPager(binding.viewPager);

        int tabCount = binding.tabLayout.getTabCount();
        binding.tabLayout.addOnTabSelectedListener(new VerticalTabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabView tab, int position) {
                Log.d(TAG, "onTabSelected() called with: tab = [" + tab + "], position = [" + position + "]");
                for (int i = 0; i < tabCount; i++) {
                    if (position != i) {
                        binding.tabLayout.getTabAt(i).setBackground(null);
                    } else {
                        tab.setBackground(R.drawable.rect_item_tab);
                    }
                }
            }

            @Override
            public void onTabReselected(TabView tab, int position) {
                Log.d(TAG, "onTabReselected() called with: tab = [" + tab + "], position = [" + position + "]");
            }
        });
        // 设置默认第一个item颜色
        binding.tabLayout.getTabAt(0).setBackground(R.drawable.rect_item_tab);
        // 修改默认的layoutParam
        for (int i = 0; i < tabCount; i++) {
            TabView tabView = binding.tabLayout.getTabAt(i);
            FrameLayout.LayoutParams layoutParams = (LayoutParams) tabView.
                    getTitleView().getLayoutParams();
            layoutParams.gravity = Gravity.START;
            layoutParams.leftMargin = 18;
        }

        binding.ivDialogClose.setOnClickListener(v -> dismiss());
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
        return 1420;
    }
}