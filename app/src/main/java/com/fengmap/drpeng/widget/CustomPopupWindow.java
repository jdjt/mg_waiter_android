package com.fengmap.drpeng.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.fengmap.android.FMDevice;
import com.fengmap.android.utils.FMLog;

/**
 * Created by yangbin on 16/6/17.
 */
public class CustomPopupWindow {
    private Context mContext;
    private View mView;
    private PopupWindow mWindow;

    private OnWindowCloseListener mListener;

    private float mStartX;
    private float mStartY;
    private float mOffsetX;
    private float mOffsetY;
    private static float MIN_HEIGHT = 60;

    private boolean isGestureClose = false;


    public CustomPopupWindow(Context context, int layoutId) {
        mView = LayoutInflater.from(context).inflate(layoutId, null);
        mContext = context;

        init();
    }

    public CustomPopupWindow(Context context, View view) {
        mView = view;
        mContext = context;

        init();
    }


    private void init() {
        FMLog.le("CustomPopupWindow", "init");
        mWindow = new PopupWindow(mContext);
        mWindow.setContentView(mView);
        mWindow.setWidth(FMDevice.getDeviceWidth());
        mWindow.setFocusable(false);
        mWindow.setOutsideTouchable(false);
        mWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
    }


    public View getConvertView() {
        return mView;
    }


    public void setOutsideTouchable(boolean touchable) {
        mWindow.setOutsideTouchable(touchable);
        mWindow.update();
    }


    public View getChildView(int id) {
        return mView.findViewById(id);
    }


    public void measureWindow() {
        mWindow.getContentView().measure(0, 0);
        int height = mWindow.getContentView().getMeasuredHeight();
        float maxHeight = 0.5f * FMDevice.getDeviceHeight();
        int fixHeight = height > maxHeight ? (int)maxHeight : height;
        mWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mWindow.setHeight(fixHeight);

        if (mView instanceof CustomViewCheck) {
            ((CustomViewCheck) mView).check();
        }
    }

    public void setAnimationStyle(int animationStyle) {
        mWindow.setAnimationStyle(animationStyle);
    }

    public boolean isShowing() {
        return mWindow.isShowing();
    }


    public void showAtLocation(View parent, int gravity, int x, int y) {
        measureWindow();
        mWindow.showAtLocation(parent,
                               gravity,
                               x ,
                               y);
    }


    public void showAsDropDown(View anchor, int offsetX, int offsetY) {
        measureWindow();
        mWindow.showAsDropDown(anchor, offsetX, offsetY);
    }

    public void update() {
        measureWindow();
        mWindow.update();
    }

    public void openSwipeDownGesture() {
        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View pView, MotionEvent e) {

                int act = e.getAction() & MotionEvent.ACTION_MASK;
                if (act == MotionEvent.ACTION_DOWN) {//按下
                    mStartX = e.getX();
                    mStartY = e.getY();
                    return true;
                }

                if (act == MotionEvent.ACTION_MOVE) {//移动
                    return true;
                }

                if (act == MotionEvent.ACTION_UP) {//抬起
                    mOffsetX = e.getX() - mStartX;
                    mOffsetY = e.getY() - mStartY;
                }

                int height = mWindow.getHeight();
                int minHeight = height > MIN_HEIGHT ? (int) MIN_HEIGHT : height * 2 / 3;
                if (mOffsetY >= minHeight) {
                    isGestureClose = true;
                    close();
                    return true;
                }

                return false;
            }
        });
    }



    public void close() {
        mWindow.dismiss();
        if (mListener != null) {
            if (isGestureClose) {
                mListener.onClose(true, mView);
                isGestureClose = false;
            } else {
                mListener.onClose(false, mView);
            }
        }
    }

    public void setOnWindowCloseListener(OnWindowCloseListener pListener) {
        mListener = pListener;
    }

    public interface OnWindowCloseListener {
        void onClose(boolean isGestureClose, View v);
    }

}
