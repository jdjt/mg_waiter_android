package com.fengmap.drpeng.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.mgwaiter.R;


/**
 * Created by yangbin on 16/8/12.
 */

public class NaviProcessingView extends RelativeLayout {
    private Context mContext;
    private TextView mTimeText;
    private TextView mShiftText;
    private TextView mStopNavi;

    public NaviProcessingView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }


    public NaviProcessingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    private void init() {
        View.inflate(mContext, R.layout.view_navi_processing, this);
        mTimeText = (TextView) findViewById(R.id.fm_navi_processing_time);
        mShiftText = (TextView) findViewById(R.id.fm_navi_processing_shift);
        mStopNavi = (TextView) findViewById(R.id.fm_stop_navi_processing);
    }

    public void setRemainingTime(String time) {
        mTimeText.setText(time);
    }

    public void setRemainingDistance(String distance) {
        mShiftText.setText(distance);
    }

    public TextView getStopNaviButton() {
        return mStopNavi;
    }
}
