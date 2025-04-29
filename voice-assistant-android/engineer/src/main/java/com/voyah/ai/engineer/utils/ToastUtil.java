package com.voyah.ai.engineer.utils;

import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;
import com.vcos.common.widgets.vcostoast.VcosToastManager;

import java.util.Objects;

public class ToastUtil {
    public static void showToast(String text) {
        ThreadUtils.runOnUiThread(() -> Objects.requireNonNull(VcosToastManager.Companion.getInstance()).showSystemToast(Utils.getApp(), text));
    }
}
