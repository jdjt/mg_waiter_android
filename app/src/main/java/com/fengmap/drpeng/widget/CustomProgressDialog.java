package com.fengmap.drpeng.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.fengmap.android.FMDevice;
import com.android.mgwaiter.R;

/**
 * 自定义进度条视图。
 * Created by yangbin on 16/8/31.
 */

public class CustomProgressDialog extends Dialog {
    private Context mContext;
    private View mView;
    private TextView mInfoView;

    public CustomProgressDialog(Context context) {
        super(context);
        mContext = context;
    }

    public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    public void setCustomContentView(int layoutId) {
        mView = LayoutInflater.from(mContext).inflate(layoutId, null);
        mInfoView = (TextView) mView.findViewById(R.id.fm_custom_dialog_info);
        setContentView(mView);
        init();
    }


    private void init() {
        // 设置对话框关闭之前对话框之外的区域不可点击
        this.setCanceledOnTouchOutside(false);
        Window dialogWindow = this.getWindow();
        // 将对话框的大小按屏幕大小的百分比设置
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值

        mView.measure(0, 0);
        int w = mView.getMeasuredWidth();
        int h = mView.getMeasuredHeight();

        int maxHeight = (int) (FMDevice.getDeviceHeight() * 0.5); // 高度设置为屏幕的0.5
        int maxWidth = (int) (FMDevice.getDeviceWidth() * 0.9); // 宽度设置为屏幕的0.9

        p.width = w > maxWidth ? maxWidth : w;
        p.height = h > maxHeight ? maxHeight : h;

        dialogWindow.setAttributes(p);
    }


    public void setInfoViewContext(String info) {
        mInfoView.setText(info);
    }


}
