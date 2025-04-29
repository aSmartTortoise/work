package com.voyah.ai.engineer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class WaveSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private float lineOff;
    private boolean isDestroyed = false;


    public float getLineOff() {
        return lineOff;
    }


    public void setLineOff(float lineOff) {
        this.lineOff = lineOff;
    }


    public WaveSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
    }


    public void initSurfaceView(final SurfaceView sfv) {
        isDestroyed = false;
        Canvas canvas = sfv.getHolder().lockCanvas(
                new Rect(0, 0, sfv.getWidth(), sfv.getHeight()));
        if (canvas == null) {
            return;
        }
        canvas.drawARGB(255, 239, 239, 239);
        int height = (int) (sfv.getHeight() - lineOff);
        Paint paintLine = new Paint();
        Paint centerLine = new Paint();
        Paint circlePaint = new Paint();
        circlePaint.setColor(Color.rgb(246, 131, 126));
        circlePaint.setAntiAlias(true);
        canvas.drawCircle(0, lineOff / 4, lineOff / 4, circlePaint);
        canvas.drawCircle(0, sfv.getHeight() - lineOff / 4, lineOff / 4, circlePaint);
        canvas.drawLine(0, 0, 0, sfv.getHeight(), circlePaint);
        paintLine.setColor(Color.rgb(169, 169, 169));
        centerLine.setColor(Color.rgb(39, 199, 175));
        canvas.drawLine(0, lineOff / 2, sfv.getWidth(), lineOff / 2, paintLine);
        canvas.drawLine(0, sfv.getHeight() - lineOff / 2 - 1, sfv.getWidth(), sfv.getHeight() - lineOff / 2 - 1, paintLine);//最下面的那根线
        canvas.drawLine(0, height * 0.5f + lineOff / 2, sfv.getWidth(), height * 0.5f + lineOff / 2, centerLine);//中心线
        sfv.getHolder().unlockCanvasAndPost(canvas);
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        initSurfaceView(this);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isDestroyed = true;
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }
}
