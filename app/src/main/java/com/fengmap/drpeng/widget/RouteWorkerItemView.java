package com.fengmap.drpeng.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.fengmap.android.FMDevice;


/**
 * 业态视图。
 * Created by yangbin on 16/8/13.
 */

public class RouteWorkerItemView extends View {
    private Paint mPaint;

    private float  mCircleRadius      = 14;
    private String mCircleText        = "18";
    private float  mCircleTextSize    = 15;

    private String mVerticalText      = "弱水三千";
    private float  mVerticalTextSpace = 1;
    private float  mVerticalMarginTop = 8;
    private float  mVerticalTextSize  = 12;

    //selected;
    private int mSelectedBgColor = Color.parseColor("#EE1E9F77");
    private int mSelectedCircleColor = mSelectedBgColor;
    private int mSelectedVerticalTextColor = Color.WHITE;
    private int mSelectedCircleTextColor = mSelectedVerticalTextColor;

    // normal
    private int mNormalBgColor = Color.TRANSPARENT;
    private int mNormalCircleColor = Color.parseColor("#ff59494d");
    private int mNormalVerticalTextColor = Color.DKGRAY;
    private int mNormalCircleTextColor = Color.WHITE;

    private float mRound = 6.0f;

    private boolean isSelected = false;

    Rect r = new Rect();


    public RouteWorkerItemView(Context context) {
        super(context);
        this.setWillNotDraw(false);   // 如果重写了onDraw方法, 则必须设置这个标记
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        mPaint.setTypeface(Typeface.);
    }

    public RouteWorkerItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setWillNotDraw(false);   // 如果重写了onDraw方法, 则必须设置这个标记
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        mPaint.setTypeface(Typeface.MONOSPACE);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        int circleTextColor = 0;
        int circleColor = 0;
        int verticalTextColor = 0;
        int bgColor = 0;
        if (isSelected) {
            circleColor = mSelectedCircleColor;
            circleTextColor = mSelectedCircleTextColor;
            verticalTextColor = mSelectedVerticalTextColor;
            bgColor = mSelectedBgColor;
        } else {
            circleColor = mNormalCircleColor;
            circleTextColor = mNormalCircleTextColor;
            verticalTextColor = mNormalVerticalTextColor;
            bgColor = mNormalBgColor;
        }

        float circleWidth = getCircleWidth();
        float radius = circleWidth/2;
        // 画背景
        int totalHeight = getHeight();
        mPaint.setColor(bgColor);
        mPaint.setStyle(Paint.Style.FILL);
        if (getAndroidSDKVersion()>=21) {
            canvas.drawRoundRect(0, radius, circleWidth, totalHeight, mRound, mRound, mPaint);
        } else {
            canvas.drawRect(0, radius, circleWidth, totalHeight, mPaint);
        }

        //画圆
        canvas.save();
        float x = radius;
        float y = radius;
        mPaint.setColor(circleColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(x, y, radius, mPaint);
        canvas.restore();

        //画圆中的字
        mPaint.setColor(circleTextColor);

        mPaint.getTextBounds(mCircleText, 0, mCircleText.length(), r);

        float             cih        = r.bottom - r.top;
        float             ciw        = mPaint.measureText(mCircleText);
        Paint.FontMetrics fm         = mPaint.getFontMetrics();
        float             textHeight = fm.bottom - fm.top;
        float             offsetX    = ciw / 2;
        float             offsetY    = cih/2 - (fm.bottom - (textHeight - cih)/2);

        canvas.drawText(mCircleText, x - offsetX, y + offsetY, mPaint);

        //画下面垂直的字
        mPaint.setTextSize(mVerticalTextSize * FMDevice.getDeviceDensity());
        mPaint.setColor(verticalTextColor);

        int lines = mVerticalText.length();

        y = circleWidth + mVerticalMarginTop * FMDevice.getDeviceDensity();
        mPaint.getTextBounds(mVerticalText, 0, 1, r);
        cih = r.bottom - r.top;
        fm = mPaint.getFontMetrics();
        textHeight = fm.bottom - fm.top;
        offsetY = cih/2 - (fm.bottom - (textHeight - cih)/2);
        String str;
        float h = 0;
        for (int i=0; i<lines; i++) {
            str = String.valueOf(mVerticalText.charAt(i));
            ciw = mPaint.measureText(str);
            offsetX = ciw / 2;
            x = radius - offsetX;
            h = y + i * textHeight + i * mVerticalTextSpace * FMDevice.getDeviceDensity() + offsetY;
            canvas.drawText(String.valueOf(str), x, h, mPaint);
        }
        //FMLog.le("draw", "text: " + mVerticalText + ", line: " + lines + ", height: " + (h-y -offsetY));

        super.onDraw(canvas);
    }


    public float getCircleRadius() {
        return mCircleRadius;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setBackgroundRound(float round) {
        this.mRound = round;
    }

    public void setSelectedBgColor(int color) {
        this.mSelectedBgColor = color;
    }

    public void setNormalCircleColor(int color) {
        this.mNormalCircleColor = color;
    }

    public void setNormalVerticalTextColor(int color) {
        mNormalVerticalTextColor = color;
    }

    public void setSelectedVerticalTextColor(int color) {
        mSelectedVerticalTextColor = color;
    }




    public void setCircleText(String pCircleText) {
        mCircleText = pCircleText;
    }

    public void setCircleTextSize(float pCircleTextSize) {
        mCircleTextSize = pCircleTextSize;
    }

    public void setVerticalText(String pVerticalText) {
        mVerticalText = pVerticalText;
    }

    public void setVerticalTextSize(float pVerticalTextSize) {
        mVerticalTextSize = pVerticalTextSize;
    }




    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measureWidth = (int) getCircleWidth();
        int measureHeight = (int) (getCircleWidth() + getVerticalTextHeight() + mVerticalMarginTop * FMDevice.getDeviceDensity() );
        // 设置自定义的控件MyViewGroup的大小
        //FMLog.le("text", "text: " + mVerticalText + "height: " + measureHeight);
        setMeasuredDimension(measureWidth, measureHeight);
    }


    private float getCircleWidth() {
        return 2 * mCircleRadius * FMDevice.getDeviceDensity();
    }

    private float getCircleTextWidth() {
        mPaint.setTextSize(mCircleTextSize * FMDevice.getDeviceDensity());
        return mPaint.measureText(mCircleText) * FMDevice.getDeviceDensity();
    }

    private float getVerticalTextHeight() {
        mPaint.setTextSize(mVerticalTextSize * FMDevice.getDeviceDensity());
        Paint.FontMetrics fm         = mPaint.getFontMetrics();
        float             textHeight = fm.bottom - fm.top;
        int lines = mVerticalText.length();
        float h = textHeight * lines + (lines -1) * mVerticalTextSpace * FMDevice.getDeviceDensity();
        return h;
    }

    public static int getAndroidSDKVersion() {
        int version = 0;
        try {
            version = Integer.valueOf(Build.VERSION.SDK);
        } catch (NumberFormatException e) {
        }
        return version;
    }


    public static Path composeRoundedRectPath(RectF rect, float topLeftRadius, float topRightRadius, float bottomRightRadius, float bottomLeftRadius){
        Path path = new Path();
        topLeftRadius = topLeftRadius < 0 ? 0 : topLeftRadius;
        topRightRadius = topRightRadius < 0 ? 0 : topRightRadius;
        bottomLeftRadius = bottomLeftRadius < 0 ? 0 : bottomLeftRadius;
        bottomRightRadius = bottomRightRadius < 0 ? 0 : bottomRightRadius;

        path.moveTo(rect.left + topLeftRadius/2 ,rect.top);
        path.lineTo(rect.right - topRightRadius/2,rect.top);
        path.quadTo(rect.right, rect.top, rect.right, rect.top + topRightRadius/2);
        path.lineTo(rect.right ,rect.bottom - bottomRightRadius/2);
        path.quadTo(rect.right ,rect.bottom, rect.right - bottomRightRadius/2, rect.bottom);
        path.lineTo(rect.left + bottomLeftRadius/2,rect.bottom);
        path.quadTo(rect.left,rect.bottom,rect.left, rect.bottom - bottomLeftRadius/2);
        path.lineTo(rect.left,rect.top + topLeftRadius/2);
        path.quadTo(rect.left,rect.top, rect.left + topLeftRadius/2, rect.top);
        path.close();

        return path;
    }

}
