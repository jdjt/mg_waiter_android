package com.fengmap.drpeng.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

/**
 * drawableLeft与文本一起居中显示
 */
public class DrawableCenterTextView extends TextView {

    public DrawableCenterTextView(Context context, AttributeSet attrs,
                                  int defStyle) {
        super(context, attrs, defStyle);
    }

    public DrawableCenterTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawableCenterTextView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        preDraw(canvas);
//        Drawable[] drawables = getCompoundDrawables();
//        if (drawables != null) {
//            String text = getText().toString();
//            Drawable drawableLeft = drawables[0];
//            if (drawableLeft != null) {
//                float textWidth = getPaint().measureText(text);
//                int drawablePadding = getCompoundDrawablePadding();
//                int drawableWidth = 0;
//                drawableWidth = drawableLeft.getIntrinsicWidth();
//                float bodyWidth = textWidth + drawableWidth + drawablePadding;
//                canvas.translate((getWidth() - bodyWidth) / 2, 0);
//            }
//            Drawable drawableTop = drawables[1];
//            if (drawableTop != null) {
//                Rect  r = new Rect();
//                getPaint().getTextBounds(text, 0, text.length(), r);
//                float textHeight = r.bottom - r.top;
//
//                int drawablePadding = getCompoundDrawablePadding();
//                int drawableHeight = drawableTop.getIntrinsicWidth();
//                float bodyHeight = textHeight + drawableHeight + drawablePadding;
//                canvas.translate(0, (getHeight() - bodyHeight) / 2);
//            }
//        }
        super.onDraw(canvas);
    }


    private void onCenterDraw(Canvas canvas, Drawable drawable, int gravity) {
        int drawablePadding = getCompoundDrawablePadding();
        int ratio = 1;
        float total;

        switch (gravity) {
            case Gravity.RIGHT:
                ratio = -1;
            case Gravity.LEFT:
                total = getPaint().measureText(getText().toString()) + drawable.getIntrinsicWidth() + drawablePadding + getPaddingLeft() + getPaddingRight();
                canvas.translate(ratio * (getWidth() - total) / 2, 0);
                break;
            case Gravity.BOTTOM:
                ratio = -1;
            case Gravity.TOP:
                Paint.FontMetrics fontMetrics0 = getPaint().getFontMetrics();
                total = fontMetrics0.descent - fontMetrics0.ascent + drawable.getIntrinsicHeight() + drawablePadding + getPaddingTop() + getPaddingBottom();
                canvas.translate(0, ratio * (getHeight() - total) / 2);
                break;
        }
    }


    private void preDraw(Canvas canvas) {
        Drawable[] drawables = getCompoundDrawables();
        if (drawables != null) {
            if (drawables[0] != null) {
                setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
                onCenterDraw(canvas, drawables[0], Gravity.LEFT);
            } else if (drawables[1] != null) {
                setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
                onCenterDraw(canvas, drawables[1], Gravity.TOP);
            } else if (drawables[2] != null) {
                setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
                onCenterDraw(canvas, drawables[2], Gravity.RIGHT);
            } else if (drawables[3] != null) {
                setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
                onCenterDraw(canvas, drawables[3], Gravity.BOTTOM);
            }
        }
    }

}