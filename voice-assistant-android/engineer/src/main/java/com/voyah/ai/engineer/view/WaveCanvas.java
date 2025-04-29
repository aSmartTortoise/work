package com.voyah.ai.engineer.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.Log;


public class WaveCanvas {
    private static final String TAG = "WaveCanvas";
    private final short[] buffer = new short[2048 * 500];
    private int tail = 0;
    private int lineOff;
    public int rateX = 100;
    public int rateY = 1;
    private final int marginRight = 30;
    private float divider = 0.2f;
    long cTime;
    private Paint circlePaint;
    private Paint center;
    private Paint paintLine;
    private Paint paint;

    private WaveSurfaceView sfv;
    public boolean isRecording = false;// 录音线程控制标记


    public void start(WaveSurfaceView sfv) {
        this.isRecording = true;
        this.sfv = sfv;
        this.lineOff = (int) sfv.getLineOff();
        init();
    }

    /**
     * 停止录音
     */
    public void stop() {
        this.isRecording = false;
    }

    public void init() {

        circlePaint = new Paint();
        circlePaint.setColor(Color.rgb(246, 131, 126));

        center = new Paint();
        center.setColor(Color.rgb(39, 199, 175));
        center.setStrokeWidth(1);
        center.setAntiAlias(true);
        center.setFilterBitmap(true);
        center.setStyle(Style.FILL);

        paintLine = new Paint();
        paintLine.setColor(Color.rgb(221, 221, 221));
        Paint paintText = new Paint();
        paintText.setColor(Color.rgb(255, 255, 255));
        paintText.setStrokeWidth(3);
        paintText.setTextSize(22);

        Paint paintRect = new Paint();
        paintRect.setColor(Color.rgb(39, 199, 175));

        paint = new Paint();
        paint.setColor(Color.rgb(39, 199, 175));
        paint.setStrokeWidth(1);
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setStyle(Style.FILL);
    }


    void draw(short[] buffer, int baseLine, int left, int right) {
        if (sfv.isDestroyed()) {
            return;
        }
        int marginLeft = 20;
        divider = (float) ((sfv.getWidth() - marginRight - marginLeft) / (48000 / rateX * 20.00));
        rateY = (65535 / 16 / (sfv.getHeight() - lineOff));
        Canvas canvas = sfv.getHolder().lockCanvas(
                new Rect(0, 0, sfv.getWidth(), sfv.getHeight()));
        if (canvas == null)
            return;
        canvas.drawARGB(255, 239, 239, 239);
        int length = right - left + 1;
        int start = (int) (length * divider);
        float y;

        if (sfv.getWidth() - start <= marginRight) {
            start = sfv.getWidth() - marginRight;
        }
        canvas.drawLine(0, (float) lineOff / 2, sfv.getWidth(), (float) lineOff / 2, paintLine);
        canvas.drawLine(0, sfv.getHeight() - (float) lineOff / 2 - 1, sfv.getWidth(), sfv.getHeight() - (float) lineOff / 2 - 1, paintLine);//最下面的那根线
        canvas.drawCircle(start, (float) lineOff / 2, (float) lineOff / 10, circlePaint);
        canvas.drawCircle(start, sfv.getHeight() - (float) lineOff / 2 - 1, (float) lineOff / 10, circlePaint);
        canvas.drawLine(start, (float) lineOff / 2, start, sfv.getHeight() - (float) lineOff / 2, circlePaint);
        int height = sfv.getHeight() - lineOff;
        canvas.drawLine(0, height * 0.5f + (float) lineOff / 2, sfv.getWidth(), height * 0.5f + (float) lineOff / 2, center);//中心线

        for (int i = 0; i <= right - left; i++) {
            y = (float) buffer[i + left] / rateY + baseLine;
            float x = (i) * divider;
            if (sfv.getWidth() - (i - 1) * divider <= marginRight) {
                x = sfv.getWidth() - marginRight;
            }
            float y1 = sfv.getHeight() - y;
            if (y < (float) lineOff / 2) {
                y = (float) lineOff / 2;
            }
            if (y > sfv.getHeight() - (float) lineOff / 2 - 1) {
                y = sfv.getHeight() - (float) lineOff / 2 - 1;

            }
            if (y1 < (float) lineOff / 2) {
                y1 = (float) lineOff / 2;
            }
            if (y1 > (sfv.getHeight() - (float) lineOff / 2 - 1)) {
                y1 = (sfv.getHeight() - (float) lineOff / 2 - 1);
            }
            canvas.drawLine(x, y, x, y1, paint);
        }
        sfv.getHolder().unlockCanvasAndPost(canvas);
    }

    public void clear() {
        synchronized (buffer) {
            tail = 0;
            draw(buffer, sfv.getHeight() / 2, 0, 0);
        }
    }


    public void processData(byte[] data, int len) {
        if (!isRecording) {
            return;
        }
        if (sfv.isDestroyed()) {
            return;
        }
        if (len >= 0) {
            synchronized (buffer) {
                for (int i = 0; i + 1 < len; i += rateX) {
                    buffer[tail] = (short) (((data[i + 1] & 0xff) << 8) | data[i] & 0xff);
                    tail++;
                }
                if (tail > buffer.length * 0.8) {
                    int copySize = buffer.length / 10;
                    System.arraycopy(buffer, tail - copySize, buffer, 0, copySize);
                    tail = copySize;
                    Log.i(TAG, "move data");
                }
                long time = System.currentTimeMillis();
                int drawTime = 1000 / 200;
                int start = 0;
                if (time - cTime >= drawTime) {
                    synchronized (buffer) {
                        if (tail == 0) {
                            return;
                        }
                        int maxSize = (int) ((sfv.getWidth() - marginRight) / divider);
                        if (tail > maxSize) {
                            start = tail - maxSize;
                        }

                    }
                    draw(buffer, sfv.getHeight() / 2, start, tail - 1);
                    cTime = System.currentTimeMillis();
                }
            }
        }
    }
}

