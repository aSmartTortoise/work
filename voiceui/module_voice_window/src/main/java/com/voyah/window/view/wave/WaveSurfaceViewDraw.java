package com.voyah.window.view.wave;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.SystemClock;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.voyah.cockpit.window.model.VoiceLocation;
import com.voyah.window.view.ISurfaceView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WaveSurfaceViewDraw {
    private static final Object locker = new Object();
    private int position;
    private static final long INTERVAL_QUICK = 80;
    private BitmapFactory.Options options;
    private final ISurfaceView view;
    private final Paint paint = new Paint();
    private volatile WaveState curState = WaveState.DEFAULT;
    private volatile List<String> drawListening;
    private boolean isRunning = true;
    private volatile int mDirection = VoiceLocation.FRONT_LEFT;
    private final Matrix mMatrix = new Matrix();
    private Thread drawThread = null;

    WaveSurfaceViewDraw(ISurfaceView view) {
        this.view = view;
        initOption();
    }

    private void initOption() {
        options = new BitmapFactory.Options();
        paint.setFilterBitmap(true);
        options.inMutable = true;
        options.inSampleSize = 1;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
    }

    void setRes(WaveState state) {
        synchronized (locker) {
            long timeStart = System.currentTimeMillis();
            if (curState.equals(state)) {
                LogUtils.i("executeSetResRunnable state equal, return");
                return;
            }
            LogUtils.i("executeSetResRunnable , new state:" + state + ",curState:" + curState);
            curState = state;
            if (curState.equals(WaveState.LISTENING_NIGHT)) {
                drawListening = getPathList(WaveState.LISTENING_NIGHT.toString());
            } else {
                drawListening = getPathList(WaveState.LISTENING_LIGHT.toString());
            }
            LogUtils.i("executeSetResRunnable total consuming time:" + (System.currentTimeMillis() - timeStart));
        }
    }


    public void startDraw() {
        LogUtils.i("startDraw():" + drawThread);
        if (drawThread == null) {
            drawThread = new Thread(drawRunnable);
            drawThread.setName("waveDrawThread");
            drawThread.start();
        }
    }

    private final Runnable drawRunnable = () -> {
        LogUtils.d("drawRunnable start ");
        while (isRunning) {
            drawBitmap();
            SystemClock.sleep(INTERVAL_QUICK);
        }
        LogUtils.d("drawRunnable end !!");
    };

    void stopDraw() {
        LogUtils.d("stopDraw ");
        isRunning = false;
        if (drawThread != null) {
            try {
                drawThread.join(1000);
                drawThread = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        curState = WaveState.DEFAULT;
    }

    private void drawBitmap() {
        if (position % 20 == 0) {
            LogUtils.d("drawBitmap");
        }
        synchronized (locker) {
            Canvas canvas = null;
            try {
                canvas = view.lockCanvas();
                if (canvas != null) {
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    String path = getPath();
                    position += 2;
                    draw2Screen(path, canvas);
                }
            } finally {
                try {
                    if (canvas != null) {
                        view.unlockCanvasAndPost(canvas);
                    } else {
                        curState = WaveState.DEFAULT;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.e("unlockCanvasAndPost error:" + e.getMessage());
                }
            }
        }
    }

    private String getPath() {
        if (curState.equals(WaveState.LISTENING_NIGHT) || (curState.equals(WaveState.LISTENING_LIGHT))) {
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
                if (mDirection == VoiceLocation.FRONT_RIGHT) {
                    bitmap = getFlipBitmap(bitmap, -1, 1);
                }
                canvas.drawBitmap(bitmap, 0, 0, paint);
                bitmap.recycle();
            }
        }
    }

    private Bitmap getFlipBitmap(Bitmap bitmap, int sx, int sy) {
        mMatrix.reset();
        mMatrix.postScale(sx, sy);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mMatrix, false);
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

    public void setDirection(int direction) {
        mDirection = direction;
    }
}
