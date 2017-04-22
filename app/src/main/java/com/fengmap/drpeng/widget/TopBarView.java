package com.fengmap.drpeng.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengmap.drpeng.FMAPI;
import com.fengmap.drpeng.IndoorMapActivity;
import com.fengmap.drpeng.OutdoorMapActivity;
import com.android.mgwaiter.R;
import com.fengmap.drpeng.SearchActivity;


/**
 * Created by yangbin on 16/8/14.
 */

public class TopBarView extends RelativeLayout {
    private Context mContext;
    private ImageView mLeftView;
    private TextView mTitleView;
    private ImageView mRightView;


    public TopBarView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public TopBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        View.inflate(mContext, R.layout.fm_topbar, this);
        mLeftView = (ImageView) findViewById(R.id.fm_topbar_back);
        mTitleView = (TextView) findViewById(R.id.fm_topbar_title);
        mRightView = (ImageView) findViewById(R.id.fm_topbar_search);

        mLeftView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FMAPI.instance().needFilterNavigationWhenOperation(mContext))
                    return;

                Activity a = (Activity) mContext;
                if (a instanceof OutdoorMapActivity) {
                    a.onBackPressed();
                } else if (a instanceof IndoorMapActivity) {
                    a.onBackPressed();
                }
            }
        });

        mRightView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FMAPI.instance().needFilterNavigationWhenOperation(mContext))
                    return;

                Bundle b = new Bundle();
                Activity a = (Activity) mContext;
                if (a instanceof OutdoorMapActivity) {
                    OutdoorMapActivity.mInstance.clearSpecialMarker();
                    OutdoorMapActivity.mInstance.clearCalculateRouteLineMarker();
                    OutdoorMapActivity.mInstance.clearStartAndEndMarker();
                    OutdoorMapActivity.mInstance.clearMeLocationMarker();

                    b.putString(FMAPI.ACTIVITY_WHERE, OutdoorMapActivity.class.getName());
                    b.putString(FMAPI.ACTIVITY_MAP_ID, OutdoorMapActivity.mInstance.getMap().currentMapId());
                    b.putInt(FMAPI.ACTIVITY_MAP_GROUP_ID, OutdoorMapActivity.mInstance.getMap().getFocusGroupId());
                } else if (a instanceof IndoorMapActivity) {
                    IndoorMapActivity.mInstance.clearSpecialMarker();
                    IndoorMapActivity.mInstance.clearCalculateRouteLineMarker();
                    IndoorMapActivity.mInstance.clearStartAndEndMarker();
                    IndoorMapActivity.mInstance.clearMeLocationMarker();

                    b.putString(FMAPI.ACTIVITY_WHERE, IndoorMapActivity.class.getName());
                    b.putString(FMAPI.ACTIVITY_MAP_ID, IndoorMapActivity.mInstance.getMap().currentMapId());
                    b.putInt(FMAPI.ACTIVITY_MAP_GROUP_ID, IndoorMapActivity.mInstance.getMap().getFocusGroupId());
                }
                FMAPI.instance().gotoActivity(a, SearchActivity.class, b);
            }
        });
    }




    public void setTitle(String pTitle) {
        mTitleView.setText(pTitle);
    }


    public ImageView getLeftView() {
        return mLeftView;
    }


    public ImageView getRightView() {
        return mRightView;
    }
}
