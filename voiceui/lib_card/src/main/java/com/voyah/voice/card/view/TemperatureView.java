package com.voyah.voice.card.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;

import com.voyah.voice.card.R;


/**
 * 自定义温度小彩条
 * UI老师参照IOS天气的小彩条
 * 小彩条的长度代表温差，彩条越长温差越大。
 * 根据最近 15 天的温度，分别设置最高值和最低值。
 * 如近15天的最高温度为32度，则这组彩条最上端代表 32度。 近15天最低温为 -12 度，那么这组彩条最下端就代表 -12 度。上下两端的极值不是固定不变的，是相对值
 * （可设置 线性渐变-背景色-进度条颜色-进度条高度）
 */
public class TemperatureView extends View {

    private static final String TAG = "TemperatureView";
    public static final int RADIUS = 5;     // 圆角矩形半径
    private RectF rectFProgress;
    private Paint mPaint, mBackgroundPaint;
    private int mWidth;
    private int mHeight;
    private int radius;
    private int highColor, lowColor, mBackgroundColor;
    private LinearGradient gradient;

//    highTemp：未来几天最低温度
//    lowTemp：未来几天最高温度
//    currentLow：当前绘制天的最低温度
//    currentHigh：当前绘制天的最高温度
    private int highTemp, lowTemp, currentLow, currentHigh;

    public TemperatureView(Context context) {
        this(context, null);
    }

    public TemperatureView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TemperatureView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取自定义属性
        Resources resources = getResources();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TemperatureChart);
//        mWidth = typedArray.getDimensionPixelSize(R.styleable.TemperatureChart_width,
//                resources.getDimensionPixelSize(R.dimen.dp_126));
//        mHeight = typedArray.getDimensionPixelSize(R.styleable.TemperatureChart_height,
//                resources.getDimensionPixelSize(R.dimen.dp_12));
        highColor = typedArray.getColor(R.styleable.TemperatureChart_color_top,
                resources.getColor(R.color.temp_high));
        lowColor = typedArray.getColor(R.styleable.TemperatureChart_color_bottom,
                resources.getColor(R.color.temp_low));
        mBackgroundColor = typedArray.getColor(R.styleable.TemperatureChart_background_color,
                resources.getColor(R.color.black_66000000));
        radius = typedArray.getDimensionPixelSize(R.styleable.TemperatureChart_bg_radius,
                resources.getDimensionPixelSize(R.dimen.dp_10));

        setLayerType(LAYER_TYPE_SOFTWARE, null);//调用该方法才能用
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;

        rectFProgress = new RectF(0, 0, mWidth, mHeight);

        gradient = new LinearGradient(0, 0, mWidth, mHeight, lowColor, highColor, Shader.TileMode.CLAMP);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.drawRoundRect(rectFProgress, radius, radius, mBackgroundPaint);
        //进度条

        double top = (double)(currentHigh - lowTemp)/(highTemp - lowTemp);
        double bottom = (double)(currentLow - lowTemp)/(highTemp - lowTemp);
        rectFProgress.left = (float) (bottom * mWidth);
        rectFProgress.right = (float) (top * mWidth);

        //绘制渐变色
        mPaint.setShader(gradient);//设置线性渐变
        canvas.drawRoundRect(rectFProgress, radius, radius, mPaint);//进度
    }

    @Keep
    public void setTemp(int highTemp, int lowTemp, int currentHigh, int currentLow) {
        this.highTemp = highTemp;
        this.lowTemp = lowTemp;
        this.currentHigh = currentHigh;
        this.currentLow = currentLow;
        invalidate();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(mBackgroundColor);
        mBackgroundPaint.setAntiAlias(true);
    }


}

