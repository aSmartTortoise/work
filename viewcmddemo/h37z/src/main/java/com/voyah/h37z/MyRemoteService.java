package com.voyah.h37z;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.voyah.viewcmd.aspect.VoiceRegisterView;

public class MyRemoteService extends Service {
    private static final String TAG = "MyRemoteService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service onCreate in process: " + android.os.Process.myPid());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service onStartCommand in process: " + android.os.Process.myPid());
        showWindowView();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service onDestroy in process: " + android.os.Process.myPid());
    }

    private void showWindowView() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_float_window, null);

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
        WindowManager wm = (WindowManager) getSystemService(Service.WINDOW_SERVICE);
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
    }
}