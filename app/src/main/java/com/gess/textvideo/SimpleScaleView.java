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
    private Paint mPaint; //画笔


    private int mMax; //最大刻度
    private int mMin; // 最小刻度

    private int mScaleMargin; //刻度间距
    private int mScaleHeight; //刻度线的长度
    private int mScaleMaxHeight; //整刻度线长度

    private int mRectWidth; //总宽度
    private int mRectHeight; //高度
    private int mScaleUnit;//刻度单位

    private float mScalsPointer;

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
        mMin = ta.getInteger(R.styleable.SimpleScaleView_lf_scale_view_min, 0);
        mMax = ta.getInteger(R.styleable.SimpleScaleView_lf_scale_view_max, 20);
        mScaleMargin = ta.getDimensionPixelOffset(R.styleable.SimpleScaleView_lf_scale_view_margin, 8);
        mScaleHeight = ta.getDimensionPixelOffset(R.styleable.SimpleScaleView_lf_scale_view_height, 16);
        mScaleUnit = ta.getInteger(R.styleable.SimpleScaleView_lf_scale_unit, 5);
        ta.recycle();

        initPaint();
        //计算view宽高
        initVar();
    }

    private void initPaint() {
        mPaint = new Paint();
        //画笔
        mPaint.setColor(Color.GRAY);
        //抗锯齿
        mPaint.setAntiAlias(true);
        //设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
        mPaint.setDither(true);
        //空心
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        //文字居中
        mPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setMax(int mMax) {
        this.mMax = mMax;
        initVar();
        invalidate();
    }

    /**
     * 计算view宽高
     */
    private void initVar() {
        //刻度值差值 * 刻度间子刻度值 * 子刻度间距离
        mRectHeight = (mMax - mMin) * mScaleUnit * mScaleMargin;
        //刻度线长度的8倍
        mRectWidth = mScaleHeight * 8;
        //整刻度长度是子刻度的两倍
        mScaleMaxHeight = mScaleHeight * 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        LogUtils.d(TAG, "onMeasure mRectHeight = " + mRectHeight + " mScaleMargin = " + mScaleMargin);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        //判断宽度类型
        switch (MeasureSpec.getMode(widthMeasureSpec)) {
            case MeasureSpec.EXACTLY://确定的值
                if (mRectWidth > width) {
                    width = mRectWidth;
                }
                break;
            default:
                width = mRectWidth;
                break;
        }
        //判断高度类型
        switch (MeasureSpec.getMode(heightMeasureSpec)) {
            case MeasureSpec.EXACTLY:
                if (mRectHeight + SizeUtils.dp2px(mScaleStart) > height) {
                    height = mRectHeight + SizeUtils.dp2px(mScaleStart);
                }
                break;
            default:
                height = mRectHeight + SizeUtils.dp2px(mScaleStart);
                break;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画边线
        onDrawLine(canvas);
        //画刻度
        onDrawScale(canvas);
        if (mScalsPointer != 0){
            onDrawPointer(canvas);
        }
    }

    /**
     *  画指针
     */
    private void onDrawPointer(Canvas canvas) {
        if (mScalsPointer <= mMax && mScalsPointer >= mMin){
            canvas.drawLine(0, mScaleStart, 0, mRectHeight + mScaleStart, mPaint);
            canvas.drawText(String.valueOf(mScalsPointer), mScaleMaxHeight + 40, 2 * mScaleMargin + mPaint.getTextSize() / 3 + mScaleStart, mPaint);
        }
    }

    private void onDrawLine(Canvas canvas) {//
        LogUtils.d(TAG, "onDrawLine mRectHeight = " + mRectHeight + " mScaleMargin = " + mScaleMargin);
        mPaint.setStrokeWidth(SizeUtils.dp2px(1));
        canvas.drawLine(0, mScaleStart, 0, mRectHeight + mScaleStart, mPaint);
    }

    private void onDrawScale(Canvas canvas) {
        mPaint.setTextSize(mRectWidth / 4);

        for (int i = 0, k = mMin; i <= (mMax - mMin) * mScaleUnit; i++) {
            if (i % mScaleUnit == 0) { //整值
                mPaint.setStrokeWidth(SizeUtils.dp2px(1));
                canvas.drawLine(0, i * mScaleMargin + mScaleStart, mScaleMaxHeight, i * mScaleMargin + mScaleStart, mPaint);
                //整值文字
                mPaint.setStrokeWidth(1);
                canvas.drawText(String.valueOf(k), mScaleMaxHeight + 40, i * mScaleMargin + mPaint.getTextSize() / 3 + mScaleStart, mPaint);
                k += 1;
            } else {
                mPaint.setStrokeWidth(1);
                canvas.drawLine(0, i * mScaleMargin + mScaleStart, mScaleHeight, i * mScaleMargin + mScaleStart, mPaint);
            }
        }
    }

    public void setPointer(float pointer){
        mScalsPointer = pointer;
        invalidate();
    }
}
