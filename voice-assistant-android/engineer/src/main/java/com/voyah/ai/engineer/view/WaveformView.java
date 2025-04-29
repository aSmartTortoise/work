package com.voyah.ai.engineer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.voyah.ai.engineer.R;
import com.voyah.ai.engineer.utils.SoundFile;

/**
 * WaveformView 这个根据你的音频进行处理成完整的波形
 * 如果文件很大可能会很慢哦
 */
public class WaveformView extends View {
    // Colors
    private int lineOffset;
    private final Paint mSelectedLinePaint;
    private final Paint mUnselectedLinePaint;
    private final Paint circlePaint;
    private final Paint paintLine;
    private final Paint centerLine;
    private int playFinish;

    private SoundFile mSoundFile;
    private int[] mLenByZoomLevel;
    private double[][] mValuesByZoomLevel;
    private double[] mZoomFactorByZoomLevel;
    private int[] mHeightsAtThisZoomLevel;
    private int mZoomLevel;
    private int mSampleRate;
    private int mSamplesPerFrame;
    private final int mOffset;
    private final int mSelectionStart;
    private final int mSelectionEnd;
    private int mPlaybackPos;
    private int state = 0;

    public void setPlayFinish(int playFinish) {
        this.playFinish = playFinish;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setLineOffset(int lineOffset) {
        this.lineOffset = lineOffset;
    }

    public WaveformView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // We don't want keys, the markers get these
        setFocusable(false);
        circlePaint = new Paint();//画圆
        circlePaint.setColor(Color.rgb(246, 131, 126));
        circlePaint.setAntiAlias(true);

        mSelectedLinePaint = new Paint();
        mSelectedLinePaint.setAntiAlias(false);
        mSelectedLinePaint.setColor(
                ContextCompat.getColor(context, R.color.waveform_selected));
        mUnselectedLinePaint = new Paint();
        mUnselectedLinePaint.setAntiAlias(false);
        mUnselectedLinePaint.setColor(
                ContextCompat.getColor(context, R.color.waveform_unselected));

        centerLine = new Paint();
        centerLine.setColor(Color.rgb(39, 199, 175));

        paintLine = new Paint();
        paintLine.setColor(Color.rgb(169, 169, 169));

        mSoundFile = null;
        mLenByZoomLevel = null;
        mValuesByZoomLevel = null;
        mHeightsAtThisZoomLevel = null;
        mOffset = 0;
        mPlaybackPos = -1;
        mSelectionStart = 0;
        mSelectionEnd = 0;
    }

    public void setSoundFile(SoundFile soundFile) {
        mSoundFile = soundFile;
        mSampleRate = mSoundFile.getSampleRate();
        mSamplesPerFrame = mSoundFile.getSamplesPerFrame();
        computeDoublesForAllZoomLevels();
        mHeightsAtThisZoomLevel = null;
    }


    public int maxPos() {
        return mLenByZoomLevel[mZoomLevel];
    }

    private double pixelsToSeconds() {
        double z = mZoomFactorByZoomLevel[0];
        return (mSoundFile.getmNumFramesFloat() * 2 * (double) mSamplesPerFrame / (mSampleRate * z));
    }

    public int millisToPixels(int mills) {
        double z = mZoomFactorByZoomLevel[mZoomLevel];
        return (int) ((mills * 1.0 * mSampleRate * z) /
                (1000.0 * mSamplesPerFrame) + 0.5);
    }

    public int pixelsToMillis(int pixels) {
        double z = mZoomFactorByZoomLevel[mZoomLevel];
        return (int) (pixels * (1000.0 * mSamplesPerFrame) /
                (mSampleRate * z) + 0.5);
    }

    public int pixelsToMillsTotal() {
        return (int) (mSoundFile.getmNumFramesFloat() * 1 * (1000.0 * mSamplesPerFrame) /
                (mSampleRate * 1f) + 0.5);
    }

    public int getStart() {
        return mSelectionStart;
    }

    public int getEnd() {
        return mSelectionEnd;
    }

    public void setPlayback(int pos) {
        mPlaybackPos = pos;
    }


    public void recomputeHeights() {
        mHeightsAtThisZoomLevel = null;
        invalidate();
    }


    protected void drawWaveformLine(Canvas canvas,
                                    int x, int y0, int y1,
                                    Paint paint) {

        int pos = maxPos();
        float rat = ((float) getMeasuredWidth() / pos);
        canvas.drawLine((int) (x * rat), y0, (int) (x * rat), y1, paint);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        int height = measuredHeight - lineOffset;

        canvas.drawARGB(255, 239, 239, 239);
        canvas.drawLine(0, height * 0.5f + lineOffset / 2f, measuredWidth, height * 0.5f + lineOffset / 2f, centerLine);//中心线

        canvas.drawLine(0, lineOffset / 2f, measuredWidth, lineOffset / 2f, paintLine);//最上面的那根线
        canvas.drawLine(0, measuredHeight - lineOffset / 2f - 1, measuredWidth, measuredHeight - lineOffset / 2f - 1, paintLine);//最下面的那根线
        if (state == 1) {
            mSoundFile = null;
            state = 0;
            return;
        }

        if (mSoundFile == null) {
            height = measuredHeight - lineOffset;
            canvas.drawLine(0, height * 0.5f + lineOffset / 2f, measuredWidth, height * 0.5f + lineOffset / 2f, centerLine);//中心线
            canvas.drawLine(0, lineOffset / 2f, measuredWidth, lineOffset / 2f, paintLine);//最上面的那根线
            canvas.drawLine(0, measuredHeight - lineOffset / 2f - 1, measuredWidth, measuredHeight - lineOffset / 2f - 1, paintLine);//最下面的那根线
            return;
        }
        if (mHeightsAtThisZoomLevel == null)
            computeIntsForThisZoomLevel();

        // Draw waveform
        int start = mOffset;
        int width = mHeightsAtThisZoomLevel.length - start;
        int ctr = measuredHeight / 2;
        if (width > measuredWidth)
            width = measuredWidth;

        // Draw grid
        double onePixelInSecs = pixelsToSeconds();
        double fractionalSecs = mOffset * onePixelInSecs;
        int integerSecs = (int) fractionalSecs;
        int i = 0;
        while (i < width) {
            i++;
            fractionalSecs += onePixelInSecs;
            int integerSecsNew = (int) fractionalSecs;
            if (integerSecsNew != integerSecs) {
                integerSecs = integerSecsNew;
            }
        }


        // Draw waveform
        for (i = 0; i < maxPos(); i++) {
            Paint paint;
            if (i + start >= mSelectionStart &&
                    i + start < mSelectionEnd) {
                paint = mSelectedLinePaint;
            } else {
                paint = mUnselectedLinePaint;
            }
            paint.setColor(Color.rgb(39, 199, 175));
            paint.setStrokeWidth(1);

            drawWaveformLine(
                    canvas, i,
                    (ctr - mHeightsAtThisZoomLevel[start + i]),
                    (ctr + 1 + mHeightsAtThisZoomLevel[start + i]),
                    paint);

            if (i + start == mPlaybackPos && playFinish != 1) {
                canvas.drawCircle((float) (i * getMeasuredWidth()) / maxPos(), lineOffset / 4f, lineOffset / 4f, circlePaint);// 上圆
                canvas.drawCircle((float) (i * getMeasuredWidth()) / maxPos(), getMeasuredHeight() - lineOffset / 4f, lineOffset / 4f, circlePaint);// 下圆
                canvas.drawLine((float) (i * getMeasuredWidth()) / maxPos(), 0, (float) (i * getMeasuredWidth()) / maxPos(), getMeasuredHeight(), circlePaint);//垂直的线
            }
        }
    }


    /**
     * Called once when a new sound file is added
     */
    private void computeDoublesForAllZoomLevels() {
        int numFrames = mSoundFile.getNumFrames();
        int[] frameGains = mSoundFile.getFrameGains();
        double[] smoothedGains = new double[numFrames];
        if (numFrames == 1) {
            smoothedGains[0] = frameGains[0];
        } else if (numFrames == 2) {
            smoothedGains[0] = frameGains[0];
            smoothedGains[1] = frameGains[1];
        } else if (numFrames > 2) {
            smoothedGains[0] = (double) (
                    (frameGains[0] / 2.0) +
                            (frameGains[1] / 2.0));
            for (int i = 1; i < numFrames - 1; i++) {
                smoothedGains[i] = (double) (
                        (frameGains[i - 1] / 3.0) +
                                (frameGains[i] / 3.0) +
                                (frameGains[i + 1] / 3.0));
            }
            smoothedGains[numFrames - 1] = (double) (
                    (frameGains[numFrames - 2] / 2.0) +
                            (frameGains[numFrames - 1] / 2.0));
        }

        // Make sure the range is no more than 0 - 255
        double maxGain = 1.0;
        for (int i = 0; i < numFrames; i++) {
            if (smoothedGains[i] > maxGain) {
                maxGain = smoothedGains[i];
            }
        }
        double scaleFactor = 1.0;
        if (maxGain > 255.0) {
            scaleFactor = 255 / maxGain;
        }

        // Build histogram of 256 bins and figure out the new scaled max
        maxGain = 0;
        int[] gainHist = new int[256];
        for (int i = 0; i < numFrames; i++) {
            int smoothedGain = (int) (smoothedGains[i] * scaleFactor);
            if (smoothedGain < 0)
                smoothedGain = 0;
            if (smoothedGain > 255)
                smoothedGain = 255;
            if (smoothedGain > maxGain)
                maxGain = smoothedGain;

            gainHist[smoothedGain]++;
        }

        // Re-calibrate the min to be 5%
        double minGain = 0;
        int sum = 0;
        while (minGain < 255 && sum < numFrames / 20) {
            sum += gainHist[(int) minGain];
            minGain++;
        }

        // Re-calibrate the max to be 99%
        sum = 0;
        while (maxGain > 2 && sum < numFrames / 100) {
            sum += gainHist[(int) maxGain];
            maxGain--;
        }
        if (maxGain <= 50) {
            maxGain = 80;
        } else if (maxGain > 50 && maxGain < 120) {
            maxGain = 142;
        } else {
            maxGain += 10;
        }


        // Compute the heights
        double[] heights = new double[numFrames];
        double range = maxGain - minGain;
        for (int i = 0; i < numFrames; i++) {
            double value = (smoothedGains[i] * scaleFactor - minGain) / range;
            if (value < 0.0)
                value = 0.0;
            if (value > 1.0)
                value = 1.0;
            heights[i] = value * value;
        }

        mLenByZoomLevel = new int[5];
        mZoomFactorByZoomLevel = new double[5];
        mValuesByZoomLevel = new double[5][];

        // Level 0 is doubled, with interpolated values
        mLenByZoomLevel[0] = numFrames * 2;
        mZoomFactorByZoomLevel[0] = 2.0;
        mValuesByZoomLevel[0] = new double[mLenByZoomLevel[0]];
        if (numFrames > 0) {
            mValuesByZoomLevel[0][0] = 0.5 * heights[0];
            mValuesByZoomLevel[0][1] = heights[0];
        }
        for (int i = 1; i < numFrames; i++) {
            mValuesByZoomLevel[0][2 * i] = 0.5 * (heights[i - 1] + heights[i]);
            mValuesByZoomLevel[0][2 * i + 1] = heights[i];
        }

        // Level 1 is normal
        mLenByZoomLevel[1] = numFrames;
        mValuesByZoomLevel[1] = new double[mLenByZoomLevel[1]];
        mZoomFactorByZoomLevel[1] = 1.0;
        System.arraycopy(heights, 0, mValuesByZoomLevel[1], 0, mLenByZoomLevel[1]);

        // 3 more levels are each halved
        for (int j = 2; j < 5; j++) {
            mLenByZoomLevel[j] = mLenByZoomLevel[j - 1] / 2;
            mValuesByZoomLevel[j] = new double[mLenByZoomLevel[j]];
            mZoomFactorByZoomLevel[j] = mZoomFactorByZoomLevel[j - 1] / 2.0;
            for (int i = 0; i < mLenByZoomLevel[j]; i++) {
                mValuesByZoomLevel[j][i] =
                        0.5 * (mValuesByZoomLevel[j - 1][2 * i] +
                                mValuesByZoomLevel[j - 1][2 * i + 1]);
            }
        }


        if (numFrames > 5000) {
            mZoomLevel = 3;
        } else if (numFrames > 1000) {
            mZoomLevel = 2;
        } else if (numFrames > 300) {
            mZoomLevel = 1;
        } else {
            mZoomLevel = 0;
        }
    }

    /**
     * Called the first time we need to draw when the zoom level has changed
     * or the screen is resized
     */
    private void computeIntsForThisZoomLevel() {

        int halfHeight = (getMeasuredHeight() / 2) - 1;
        mHeightsAtThisZoomLevel = new int[mLenByZoomLevel[mZoomLevel]];
        for (int i = 0; i < mLenByZoomLevel[mZoomLevel]; i++) {
            mHeightsAtThisZoomLevel[i] =
                    (int) (mValuesByZoomLevel[mZoomLevel][i] * halfHeight);
        }
    }
}
