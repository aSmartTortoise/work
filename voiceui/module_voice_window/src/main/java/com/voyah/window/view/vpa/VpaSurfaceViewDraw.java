package com.voyah.window.view.vpa;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewParent;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.voyah.window.view.ISurfaceView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VpaSurfaceViewDraw {
    private int position;
    private static final long INTERVAL_QUICK = 66;
    private BitmapFactory.Options options;
    private final ISurfaceView surfaceView;
    private final Paint paint = new Paint();
    private volatile VpaState curState = VpaState.DEFAULT;
    private volatile List<String> drawListening, drawSpeaking;
    private boolean isRunning = true;
    private Thread drawThread = null;
    private static final Object locker = new Object();


    VpaSurfaceViewDraw(ISurfaceView view) {
        surfaceView = view;
        initOption();
        LogUtils.d("VpaSurfaceViewDraw create");
    }

    private void initOption() {
        options = new BitmapFactory.Options();
        paint.setFilterBitmap(true);
        options.inMutable = true;
        options.inSampleSize = 1;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
    }

    void setRes(VpaState state) {
        synchronized (locker) {
            long timeStart = System.currentTimeMillis();
            if (curState.equals(state)) {
                LogUtils.i("executeSetResRunnable state equal, return");
                return;
            }
            LogUtils.i("executeSetResRunnable , new state:" + state + ",curState:" + curState);
            curState = state;
            if (curState.equals(VpaState.LISTENING_NIGHT_NORMAL)) {
                drawListening = getPathList(VpaState.LISTENING_NIGHT_NORMAL.toString());
            } else if (curState.equals(VpaState.LISTENING_NIGHT_PRIVATE)) {
                drawListening = getPathList(VpaState.LISTENING_NIGHT_PRIVATE.toString());
            } else if (curState.equals(VpaState.LISTENING_LIGHT_NORMAL)) {
                drawListening = getPathList(VpaState.LISTENING_LIGHT_NORMAL.toString());
            } else if (curState.equals(VpaState.LISTENING_LIGHT_PRIVATE)) {
                drawListening = getPathList(VpaState.LISTENING_LIGHT_PRIVATE.toString());
            } else if (curState.equals(VpaState.SPEAKING_NIGHT_NORMAL)) {
                drawSpeaking = getPathList(VpaState.SPEAKING_NIGHT_NORMAL.toString());
            } else if (curState.equals(VpaState.SPEAKING_NIGHT_PRIVATE)) {
                drawSpeaking = getPathList(VpaState.SPEAKING_NIGHT_PRIVATE.toString());
            } else if (curState.equals(VpaState.SPEAKING_LIGHT_NORMAL)) {
                drawSpeaking = getPathList(VpaState.SPEAKING_LIGHT_NORMAL.toString());
            } else if (curState.equals(VpaState.SPEAKING_LIGHT_PRIVATE)) {
                drawSpeaking = getPathList(VpaState.SPEAKING_LIGHT_PRIVATE.toString());
            }
            LogUtils.i("executeSetResRunnable total consuming time:" + (System.currentTimeMillis() - timeStart));
        }
    }


    public void startDraw() {
        LogUtils.i("startEngine()" + drawThread);
        if (drawThread == null) {
            drawThread = new Thread(drawRunnable);
            drawThread.setName("drawVpa");
            drawThread.start();
        }

    }

    private final Runnable drawRunnable = () -> {
        LogUtils.d("drawVpa thread start");
        while (isRunning) {
            drawBitmap();
            SystemClock.sleep(INTERVAL_QUICK);
        }
        LogUtils.d("drawVpa thread end");
    };


    void stopDraw() {
        LogUtils.d("stopDraw ");
        isRunning = false;
        if (drawThread != null) {
            try {
                drawThread.join(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            drawThread = null;
        }
        curState = VpaState.DEFAULT;
    }

    private void drawBitmap() {
        if (position % 20 == 0) {
            LogUtils.d("drawBitmap");
        }
        synchronized (locker) {
            Canvas canvas = null;
            try {
                canvas = surfaceView.lockCanvas();
                if (canvas != null) {
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    String path = getPath();
                    position += 2;
                    draw2Screen(path, canvas);
                }
            } finally {
                try {
                    if (canvas != null) {
                        surfaceView.unlockCanvasAndPost(canvas);
                    } else {
                        curState = VpaState.DEFAULT;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.e("unlockCanvasAndPost error:" + e.getMessage());
                }
            }
        }
    }

    /**
     * 获取对应的图片
     */
    private String getPath() {
        if (curState.equals(VpaState.SPEAKING_NIGHT_NORMAL)
                || curState.equals(VpaState.SPEAKING_NIGHT_PRIVATE)
                || curState.equals(VpaState.SPEAKING_LIGHT_NORMAL)
                || curState.equals(VpaState.SPEAKING_LIGHT_PRIVATE)) {
            if (drawSpeaking != null && !drawSpeaking.isEmpty()) {
                if (position >= drawSpeaking.size()) {
                    position = 0;
                }
                return drawSpeaking.get(position);
            }

        } else if (curState.equals(VpaState.LISTENING_NIGHT_NORMAL)
                || curState.equals(VpaState.LISTENING_NIGHT_PRIVATE)
                || curState.equals(VpaState.LISTENING_LIGHT_NORMAL)
                || curState.equals(VpaState.LISTENING_LIGHT_PRIVATE)) {
            if (drawListening != null && !drawListening.isEmpty()) {
                if (position >= drawListening.size()) {
                    position = 0;
                }
                return drawListening.get(position);
            }

        }
        return null;
    }

    private void draw2Screen(String path, Canvas canvas) {
        if (!TextUtils.isEmpty(path)) {
            Bitmap bitmap = decodeBitmapReal(path);
            if (bitmap != null) {
                SurfaceView view = (SurfaceView) surfaceView;
                ViewParent parent = view.getParent();
                if (parent != null && parent.getParent() != null) {
                    int alpha = (int) (255 * ((View) parent.getParent()).getAlpha());
                    paint.setAlpha(alpha);
                }
                canvas.drawBitmap(bitmap, 0, 0, paint);
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
            temp = BitmapFactory.decodeStream(Utils.getApp().getAssets().open(path), null, options);
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
            for (int i = 0; i < assetFiles.length; i++) {
                assetFiles[i] = assetsPath + File.separator + (i + 1) + ".png";
            }
            return Arrays.asList(assetFiles);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(0);
    }
}
