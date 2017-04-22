package com.fengmap.drpeng.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.mgwaiter.R;


/**
 * Created by yangbin on 16/8/14.
 */

public class BottomBarView extends LinearLayout {
    private Context  mContext;
    private TextView mRoomView;
    private TextView mGuideView;
    private TextView mServeView;


    public BottomBarView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public BottomBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    private void init() {
        View.inflate(mContext, R.layout.fm_bottombar, this);
        this.setOrientation(VERTICAL);

        mRoomView = (TextView) findViewById(R.id.fm_bottombar_room);
        mGuideView = (TextView) findViewById(R.id.fm_bottombar_guide);
        mServeView = (TextView) findViewById(R.id.fm_bottombar_serve);
    }
}
