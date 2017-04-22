package com.fengmap.drpeng.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fengmap.android.FMDevice;
import com.android.mgwaiter.R;

/**
 * Created by yangbin on 16/8/11.
 */

public class InsideModelView extends LinearLayout implements CustomViewCheck {
    public  Context                 mContext;
    private DrawableCenterTextView  mArrive;
    private TextView                mTitle;
    private TextView                mAddress;
    private DrawableCenterTextView  mDetail;


    public InsideModelView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public InsideModelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    private void init() {
        View.inflate(mContext, R.layout.view_inside_model_info, this);
        this.setOrientation(VERTICAL);
        this.setGravity(Gravity.CENTER);

        mTitle = (TextView) findViewById(R.id.fm_window_model_tv_title);
        mAddress = (TextView) findViewById(R.id.fm_window_model_tv_address);
        mDetail = (DrawableCenterTextView) findViewById(R.id.fm_window_model_tv_detail);
        mDetail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 进入详情界面
            }
        });

        mArrive = (DrawableCenterTextView) findViewById(R.id.fm_window_model_tv_arrive);
    }


    public DrawableCenterTextView getArriveButton() {
        return mArrive;
    }

    public void setTitle(String pTitle) {
        mTitle.setText(pTitle);
    }

    public void setAddress(String pAddress) {
        mAddress.setText(pAddress);
    }

    @Override
    public void check() {
        float w1 = mTitle.getPaint().measureText(mTitle.getText().toString());
        float w2 = mAddress.getPaint().measureText(mAddress.getText().toString());

        float len =  w1 + w2 + 25 * FMDevice.getDeviceDensity();
        float offset = 40 * FMDevice.getDeviceDensity();
        if (len > FMDevice.getDeviceWidth() - offset) {
            mAddress.setMaxWidth((int)(FMDevice.getDeviceWidth() - w1 - offset));
        } else {
            mAddress.setMaxWidth((int) w2);
        }
    }
}
