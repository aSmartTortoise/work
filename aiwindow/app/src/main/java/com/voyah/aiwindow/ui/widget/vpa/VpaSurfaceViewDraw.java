package com.voyah.aiwindow.ui.widget.vpa;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.voyah.aiwindow.ui.widget.ISurfaceView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VpaSurfaceViewDraw {
    private int position;
    private static final long INTERVAL_QUICK = 30;
    private BitmapFactory.Options mOptions;
    private boolean isValid;
    private ISurfaceView mView;
    private final Paint mPaint = new Paint();
    private final Object mLock = new Object();
    private VpaState mCurState = VpaState.DEFAULT;
    private final List<String> mDrawListening, mDrawSpeaking;
    private boolean isRunning = true;

    private Handler workHandler;

    VpaSurfaceViewDraw(ISurfaceView view) {
        mView = view;
        mDrawListening = getPathList(VpaState.LISTENING.toString());
        mDrawSpeaking = getPathList(VpaState.SPEAKING.toString());
        initOption();
        HandlerThread thread = new HandlerThread("t_vpa");
        thread.start();
        workHandler = new Handler(thread.getLooper());

        LogUtils.d("VpaSurfaceViewDraw create");
    }

    private void initOption() {
        mOptions = new BitmapFactory.Options();
        mPaint.setFilterBitmap(true);
        mOptions.inMutable = true;
        mOptions.inSampleSize = 1;
        mOptions.inPreferredConfig = Bitmap.Config.RGB_565;
    }

    void setRes(VpaState state) {
        if (!isValid) {
            LogUtils.i("setRes(),isValid = false, return");
            return;
        }

        if (mCurState.equals(state)) {
            LogUtils.e("setRes state equal, return");
            return;
        }

        LogUtils.i("setRes , lastState:" + state + ",curState:" + mCurState);

        mCurState = state;
    }

    public void startEngine() {
        LogUtils.i("startEngine()");
        workHandler.post(drawThread);
    }

    private final Runnable drawThread = () -> {
        LogUtils.d("mRunnable start ");
        while (isRunning) {
            drawBitmap();
            SystemClock.sleep(INTERVAL_QUICK);
        }

        //exit clear surface
        LogUtils.d("mRunnable end !!");
    };


    void stopDraw() {
        LogUtils.d("stopDraw ");
        isRunning = false;
        mCurState = VpaState.DEFAULT;
    }

    void setSurfaceViewIsValid(boolean isValid) {
        synchronized (mLock) {
            this.isValid = isValid;
            LogUtils.d("setSurfaceViewIsValid isValid:" + isValid);
        }
    }

    private synchronized void drawBitmap() {
        if (!isValid) {
            LogUtils.d("drawBitmap surfaceView isValid:false");
            return;
        }
        Canvas canvas = null;
        try {
            canvas = mView.lockCanvas();
            if (canvas != null) {
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                String path = getPath();
                position++;
                draw2Screen(path, canvas);
            }
        } finally {
            try {
                if (canvas != null) {
                    mView.unlockCanvasAndPost(canvas);
                } else {
                    mCurState = VpaState.DEFAULT;
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.e("unlockCanvasAndPost error:" + e.getMessage());
                isValid = false;
            }
        }
    }

    /**
     * 获取对应的图片
     *
     * @return
     */
    private String getPath() {
        if (mCurState == VpaState.SPEAKING) {
            if (position >= mDrawSpeaking.size()) {
                position = 0;
            }
            return mDrawSpeaking.get(position);
        } else if (mCurState == VpaState.LISTENING) {
            if (position >= mDrawListening.size()) {
                position = 0;
            }
            return mDrawListening.get(position);
        }
        return null;
    }

    private void draw2Screen(String path, Canvas canvas) {
        if (!TextUtils.isEmpty(path)) {
            Bitmap bitmap = decodeBitmapReal(path);
            if (bitmap != null) {
                canvas.drawBitmap(bitmap, 0, 0, mPaint);
                bitmap.recycle();
            }
        }
    }

    private Bitmap decodeBitmapReal(String path) {
        Bitmap temp = null;
        try {
            if (path == null) {
                return null;
            }
            temp = BitmapFactory.decodeStream(Utils.getApp().getAssets().open(path), null, mOptions);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return temp;
    }

    /**
     * @param assetsPath assets文件路径
     * @return 文件夹中的文件集合
     */
    private List<String> getPathList(String assetsPath) {
        AssetManager assetManager = Utils.getApp().getAssets();
        try {
            String[] assetFiles = assetManager.list(assetsPath);
            if (assetFiles == null) {
                return null;
            }
            if (assetFiles.length == 0) {
                LogUtils.d("no file in this asset directory");
                return new ArrayList<>(0);
            }
            //转换真实路径
            for (int i = 0; i < assetFiles.length; i++) {
                assetFiles[i] = assetsPath + File.separator + assetFiles[i];
            }
            return Arrays.asList(assetFiles);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(0);
    }
}
