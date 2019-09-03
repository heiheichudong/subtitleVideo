package com.gess.textvideo;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SizeUtils;

public class SimpleScaleView extends View {

    public final static String TAG = "SimpleScaleView";
    private int mMax; //最大刻度
    private int mMin; // 最小刻度

    private int mScaleMargin; //刻度间距
    private int mScaleHeight; //刻度线的高度
    private int mScaleMaxHeight; //整刻度线高度

    private int mRectWidth; //总宽度
    private int mRectHeight; //高度
    private int mScaleUnit = 5;//刻度单位

    private int mScaleStart = SizeUtils.dp2px(8); //刻度起点 留出数字显示位置

    public SimpleScaleView(Context context) {
        this(context, null);
    }

    public SimpleScaleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleScaleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SimpleScaleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        // 获取自定义属性
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.SimpleScaleView);
        mMin = ta.getInteger(R.styleable.SimpleScaleView_lf_scale_view_max, 0);
        mMax = ta.getInteger(R.styleable.SimpleScaleView_lf_scale_view_min, 200);
        mScaleMargin = ta.getDimensionPixelOffset(R.styleable.SimpleScaleView_lf_scale_view_margin, 15);
        mScaleHeight = ta.getDimensionPixelOffset(R.styleable.SimpleScaleView_lf_scale_view_height, 20);
        ta.recycle();

        initVar();
    }

    public int getScaleMargin() {
        return mScaleMargin;
    }

    public void setMax(int mMax) {
        this.mMax = mMax;
        initVar();
        invalidate();
    }

    private void initVar() {
        mRectHeight = (mMax - mMin) * mScaleUnit * mScaleMargin;
        mRectWidth = mScaleHeight * 8;
        mScaleMaxHeight = mScaleHeight * 2;
        LogUtils.d(TAG, "initVar mMax = " + mMax + " mMin = " + mMin + " (mMax - mMin) = " + (mMax - mMin));
        LogUtils.d(TAG, "initVar mRectHeight = " + mRectHeight + " mScaleMargin = " + mScaleMargin);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        LogUtils.d(TAG, "onMeasure mRectHeight = " + mRectHeight + " mScaleMargin = " + mScaleMargin);
        setMeasuredDimension(mRectWidth, mRectHeight + SizeUtils.dp2px(mScaleStart));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 画笔
        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        // 抗锯齿
        paint.setAntiAlias(true);
        // 设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
        paint.setDither(true);
        // 空心
        paint.setStyle(Paint.Style.STROKE);
        // 文字居中
        paint.setTextAlign(Paint.Align.CENTER);

        onDrawLine(canvas, paint);
        onDrawScale(canvas, paint); //画刻度
        super.onDraw(canvas);
    }

    private void onDrawLine(Canvas canvas, Paint paint) {//
        LogUtils.d(TAG, "onDrawLine mRectHeight = " + mRectHeight + " mScaleMargin = " + mScaleMargin);
        canvas.drawLine(0, mScaleStart, 0, mRectHeight + mScaleStart, paint);
    }

    private void onDrawScale(Canvas canvas, Paint paint) {
        paint.setTextSize(mRectWidth / 4);

        for (int i = 0, k = mMin; i <= (mMax - mMin) * mScaleUnit; i++) {
            if (i % mScaleUnit == 0) { //整值
                canvas.drawLine(0, i * mScaleMargin + mScaleStart, mScaleMaxHeight, i * mScaleMargin + mScaleStart, paint);
                //整值文字
                canvas.drawText(String.valueOf(k), mScaleMaxHeight + 40, i * mScaleMargin + paint.getTextSize() / 3 + mScaleStart, paint);
                k += 1;
            } else {
                canvas.drawLine(0, i * mScaleMargin + mScaleStart, mScaleHeight, i * mScaleMargin + mScaleStart, paint);
            }
        }
    }

}
