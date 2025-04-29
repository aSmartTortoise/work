package com.voyah.h37z.fragment;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.vcos.common.widgets.base.BaseDialog;
import com.vcos.common.widgets.base.VcosDialog;
import com.voyah.h37z.R;
import com.voyah.h37z.TestActivity;
import com.voyah.h37z.databinding.FragmentLightBinding;
import com.voyah.h37z.fragment.dialog.CustomDialog;
import com.voyah.h37z.fragment.dialog.CustomDialogFragment;
import com.voyah.h37z.fragment.dialog.CustomPopWindow;
import com.voyah.viewcmd.VoiceViewCmdUtils;
import com.voyah.viewcmd.aspect.VoiceRegisterView;

public class LightFragment extends Fragment {
    private static final String TAG = LightFragment.class.getSimpleName();
    private FragmentLightBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_light, container, false);
        binding.btnShow.setOnClickListener(v -> showWindowView());
        binding.btnShow2.setOnClickListener(v -> showDialog());
        binding.btnShow3.setOnClickListener(v -> showDialogFragment());
        binding.btnShow4.setOnClickListener(v -> showPopWindow());
        binding.btnShow5.setOnClickListener(v -> showVcosDialog());
        return binding.getRoot();
    }

    private void showVcosDialog() {
        new VcosDialog(requireContext()).setShowCloseButton().show();
//        VcosOptionDialog.Builder builder = new VcosOptionDialog.Builder(requireContext())
//                .setListener(new VcosOptionDialog.OnListener() {
//                    @Override
//                    public void onTertiaryButtonClicked(@Nullable BaseDialog baseDialog) {
//
//                    }
//
//                    @Override
//                    public void onPrimaryButtonClicked(@Nullable BaseDialog baseDialog) {
//                        Log.d(TAG, "onPrimaryButtonClicked");
//                    }
//
//                    @Override
//                    public void onSecondaryButtonClicked(@Nullable BaseDialog baseDialog) {
//                        Log.d(TAG, "onSecondaryButtonClicked");
//                    }
//                });
//        builder.setDesc("我是测试公共控件VcosOptionDialog织入用例");
//        builder.setAutoDismiss(true);
//        builder.setCanceledOnTouchOutside(false);
//        builder.setOptionStyle(VcosOptionDialog.STYLE_ONE_BUTTON);
//        builder.setOptionStyle(VcosOptionDialog.STYLE_TWO_BUTTON);
//        builder.show();
////        if (builder.getContentView() != null) {
////            builder.getContentView().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
////                @VoiceRegisterView(isSticky = true)
////                @Override
////                public void onViewAttachedToWindow(@NonNull View v) {
////                }
////
////                @Override
////                public void onViewDetachedFromWindow(@NonNull View v) {
////                    v.removeOnAttachStateChangeListener(this);
////                }
////            });
////        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void showPopWindow() {
        CustomPopWindow window = new CustomPopWindow(requireActivity());
        window.showAtLocation(binding.getRoot(), Gravity.CENTER, 0, 0);
    }

    private void showDialog() {
        CustomDialog dialog = new CustomDialog(requireActivity());
        dialog.setsConfirm("确定", v -> {
            Intent intent = new Intent(requireContext(), TestActivity.class);
            requireContext().startActivity(intent);
        });
        dialog.setsCancel("取消", v -> dialog.dismiss());
        dialog.show();
    }

    public void showDialogFragment() {
        CustomDialogFragment dialogFragment = new CustomDialogFragment();
        dialogFragment.show(getChildFragmentManager(), "dialog");
    }

    private void showWindowView() {
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.layout_float_window, null);

        view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {

            @VoiceRegisterView(isTopCoverView = true)
            @Override
            public void onViewAttachedToWindow(View v) {
                Log.d(TAG, "onViewAttachedToWindow() called with: v = [" + v + "]");
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                Log.d(TAG, "onViewDetachedFromWindow() called with: v = [" + v + "]");
                view.removeOnAttachStateChangeListener(this);
            }
        });
        WindowManager wm = getWindowManager(requireContext(), 3);
        WindowManager.LayoutParams wl = new WindowManager.LayoutParams();
        wl.gravity = Gravity.CENTER;
        wl.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        wl.width = 800;
        wl.height = 500;
        wl.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

        wl.format = PixelFormat.TRANSPARENT; //控制背景透明
        view.findViewById(R.id.button2).setOnClickListener(v -> {
            Log.d(TAG, "dismiss by confirm");
            wm.removeView(view);

        });
        view.findViewById(R.id.button3).setOnClickListener(v -> {
            Log.d(TAG, "dismiss cancel");
            wm.removeView(view);
        });
        wm.addView(view, wl);
//        View.OnKeyListener keyListener = (v, keyCode, event) -> {
//            Log.d(TAG, "onKey() called with: v = [" + v + "], keyCode = [" + keyCode + "], event = [" + event + "]");
//            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
//                wm.removeView(view);
//                return true;
//            }
//            return false;
//        };
//        view.setFocusable(true);
//        view.setFocusableInTouchMode(true);
//        view.requestFocus();
//        view.setOnKeyListener(keyListener);
    }

    private WindowManager getWindowManager(Context context, int displayId) {
        WindowManager windowManager = null;
        Display display = context.getSystemService(DisplayManager.class).getDisplay(displayId);
        if (display != null) {
            Context displayContext = context.createDisplayContext(display);
            windowManager = (WindowManager) displayContext.getSystemService(Context.WINDOW_SERVICE);
        }
        if (windowManager == null) {
            Log.e(TAG, "can not get windowManager according by displayId, displayId:" + displayId);
            windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return windowManager;
    }
}
