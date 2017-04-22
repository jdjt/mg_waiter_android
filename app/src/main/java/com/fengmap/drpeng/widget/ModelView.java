package com.fengmap.drpeng.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fengmap.android.FMDevice;
import com.fengmap.android.FMMapSDK;
import com.fengmap.android.wrapmv.Tools;
import com.fengmap.android.wrapmv.entity.FMExternalModelRelation;
import com.fengmap.drpeng.FMAPI;
import com.fengmap.drpeng.IndoorMapActivity;
import com.fengmap.drpeng.OutdoorMapActivity;
import com.android.mgwaiter.R;

/**
 * Created by yangbin on 16/8/11.
 */

public class ModelView extends LinearLayout implements CustomViewCheck {
    public  Context                 mContext;
    private DrawableCenterTextView  mArrive;
    private DrawableCenterTextView  mEnter;
    private TextView                mTitle;
    private TextView                mAddress;
    private DrawableCenterTextView  mDetail;
    private View                    mDivider;

    private String mEnterMapId;


    public ModelView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public ModelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    private void init() {
        View.inflate(mContext, R.layout.view_model_info, this);
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


        mDivider = findViewById(R.id.divider);


        mEnter = (DrawableCenterTextView) findViewById(R.id.fm_window_model_enter_inside);
        mEnter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 进入室内界面
                if (mEnterMapId==null || mEnterMapId.equals("")) {
                    CustomToast.show(mContext, "没有室内地图");
                    return;
                }
                Activity a = (Activity)mContext;
                Bundle   b = new Bundle();
                if (a instanceof OutdoorMapActivity) {
                    b.putString(FMAPI.ACTIVITY_WHERE, OutdoorMapActivity.class.getName());
                    b.putString(FMAPI.ACTIVITY_MAP_ID, mEnterMapId);
                    b.putString(FMAPI.ACTIVITY_HOTEL_NAME, Tools.getInsideMapName(mEnterMapId));
                }
                FMAPI.instance().gotoActivity(a, IndoorMapActivity.class, b);
            }
        });

        mArrive = (DrawableCenterTextView) findViewById(R.id.fm_window_model_tv_arrive);
    }

    /**
     * 通过点击到的模型的id, 查询进入室内地图的ID
     * @param pModelFid
     */
    public void setEnterMapIdByModelFid(String pModelFid) {
        FMExternalModelRelation emr = FMMapSDK.getExternalModelRelations().get(pModelFid);
        if (emr == null) {   // 没有室内
            setEnterViewVisible(false);
            return;
        } else {
            setEnterViewVisible(true);
        }
        mEnterMapId = emr.getMapId();
    }


    public DrawableCenterTextView getArriveButton() {
        return mArrive;
    }
    public DrawableCenterTextView getEnterButton() {
        return mEnter;
    }

    /**
     * 设置标题。
     * @param pTitle
     */
    public void setTitle(String pTitle) {
        if (pTitle.equals("") || pTitle == null) {
            mTitle.setText("暂无名称");
        } else {
            mTitle.setText(pTitle);
        }
    }

    /**
     * 设置地址。
     * @param pAddress
     */
    public void setAddress(String pAddress) {
        mAddress.setText(pAddress);
    }


    ////////////////

    public void setEnterViewVisible(boolean pVisible) {
        if (pVisible) {
            mEnter.setVisibility(VISIBLE);
            mDivider.setVisibility(VISIBLE);
        } else {
            mEnter.setVisibility(GONE);
            mDivider.setVisibility(GONE);
        }
    }


    public void setAddressViewVisible(boolean pVisible) {
        if (pVisible) {
            mAddress.setVisibility(VISIBLE);

        } else {
            mAddress.setVisibility(GONE);
        }
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
