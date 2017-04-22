package com.fengmap.drpeng.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.mgwaiter.R;


/**
 * 路径规划视图。
 * Created by yangbin on 16/8/11.
 */

public class NaviView extends LinearLayout {
    private Context mContext;

    private TextView mNaviTime;
    private TextView mNaviDistance;
    private DrawableCenterTextView mNaviCalorie;

    private TextView mNaviStart;
    private TextView mNaviEnd;
    private ImageView mNaviExchange;
    private TextView mOpenNavi;


    public NaviView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public NaviView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    private void init() {
        View.inflate(mContext, R.layout.view_w_navi, this);
        this.setOrientation(VERTICAL);

        mNaviTime = (TextView) findViewById(R.id.fm_navi_need_time);
        mNaviDistance = (TextView) findViewById(R.id.fm_navi_need_distance);
        mNaviCalorie = (DrawableCenterTextView) findViewById(R.id.fm_navi_need_calorie);


        mNaviStart = (TextView) findViewById(R.id.fm_navi_start);
        mNaviEnd = (TextView) findViewById(R.id.fm_navi_end);
        mNaviExchange = (ImageView) findViewById(R.id.fm_navi_exchange);
        mOpenNavi = (TextView) findViewById(R.id.fm_open_navi);
    }

    public void setStartText(String startName) {
        mNaviStart.setText(startName);
        mNaviStart.setTextSize(16);
    }

    public void setEndText(String endName) {
        mNaviEnd.setText(endName);
        mNaviEnd.setTextSize(16);
    }

    public void setNaviNeedTime(String needTime) {
        mNaviTime.setText(needTime);
    }

    public void setNaviNeedDistance(String needDistance) {
        mNaviDistance.setText(needDistance);
    }

    public void setNaviNeedCalorie(String needCalorie) {
        mNaviCalorie.setText(needCalorie);
    }

    public TextView getOpenNaviButton() {
        return mOpenNavi;
    }

    public ImageView getNaviExchageButton() {
        return mNaviExchange;
    }

    public TextView getNaviStartView() {
        return mNaviStart;
    }

    public TextView getNaviEndView() {
        return mNaviEnd;
    }

}
