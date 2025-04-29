package com.voyah.ai.common.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/**
 * @author:lcy
 * @data:2024/3/12
 **/
public class ToastUtils {

    public static void showToast(Context context, String str) {
        Handler mainHandler = new Handler(Looper.getMainLooper());

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }
        });
    }
}
