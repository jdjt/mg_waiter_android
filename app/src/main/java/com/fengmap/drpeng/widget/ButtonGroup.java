package com.fengmap.drpeng.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.android.mgwaiter.R;

/**
 * Created by yangbin on 16/8/9.
 */

public class ButtonGroup extends LinearLayout{
    private DrawableCenterTextView mIBFacility;
    private DrawableCenterTextView mIBShop;
    private DrawableCenterTextView mIBFood;

    private ButtonType mType = ButtonType.NONE;
    private boolean isFacilitySelected = false;
    private boolean isShopSelected = false;
    private boolean isFoodSelected = false;

    private OnButtonGroupListener mListener;


    public ButtonGroup(Context context) {
        super(context);
        init(context);
    }

    public ButtonGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ButtonGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        View.inflate(context, R.layout.view_fengmap_buttons, this);
        this.setOrientation(VERTICAL);

        mIBFacility = (DrawableCenterTextView) findViewById(R.id.fm_bt_facility);
        mIBShop = (DrawableCenterTextView) findViewById(R.id.fm_bt_shop);
        mIBFood = (DrawableCenterTextView) findViewById(R.id.fm_bt_food);

        mIBFacility.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mType = ButtonType.FACILITY;
                    mListener.onItemClick(mType, isFacilitySelected);
                }
            }
        });
        mIBShop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mType = ButtonType.SHOP;
                    mListener.onItemClick(mType, isShopSelected);
                }
            }
        });
        mIBFood.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mType = ButtonType.FOOD;
                    mListener.onItemClick(mType, isFoodSelected);
                }
            }
        });
    }


    public void selected(ButtonType type) {
        mType = type;
        performSelected(type);
    }

    public void unSelected(ButtonType type) {
        performUnSelected(type);
        mType = ButtonType.NONE;
    }


    public ButtonType getCurrentType() {
        return mType;
    }


    public void setOnButtonGroupListener(OnButtonGroupListener pListener) {
        mListener = pListener;
    }



    private void performSelected(ButtonType pType) {
        if (pType == ButtonType.FACILITY) {
            isFacilitySelected = true;
            mIBFacility.setBackgroundResource(R.drawable.fm_green_press_button);
            mIBFacility.setTextColor(Color.WHITE);

            performUnSelected(ButtonType.SHOP);
            performUnSelected(ButtonType.FOOD);
        } else if (pType == ButtonType.SHOP) {
            isShopSelected = true;
            mIBShop.setBackgroundResource(R.drawable.fm_green_press_button);
            mIBShop.setTextColor(Color.WHITE);


            performUnSelected(ButtonType.FACILITY);
            performUnSelected(ButtonType.FOOD);
        } else {
            isFoodSelected = true;
            mIBFood.setBackgroundResource(R.drawable.fm_green_press_button);
            mIBFood.setTextColor(Color.WHITE);

            performUnSelected(ButtonType.FACILITY);
            performUnSelected(ButtonType.SHOP);
        }
    }

    private void performUnSelected(ButtonType pType) {
        if (pType == ButtonType.FACILITY) {
            isFacilitySelected = false;
            mIBFacility.setBackgroundResource(R.drawable.fm_green_normal_button);
            mIBFacility.setTextColor(Color.parseColor("#70ffffff"));
        } else if (pType == ButtonType.SHOP) {
            isShopSelected = false;
            mIBShop.setBackgroundResource(R.drawable.fm_green_normal_button);
            mIBShop.setTextColor(Color.parseColor("#70ffffff"));
        } else {
            isFoodSelected = false;
            mIBFood.setBackgroundResource(R.drawable.fm_green_normal_button);
            mIBFood.setTextColor(Color.parseColor("#70ffffff"));
        }
    }


    public enum ButtonType {
        NONE,
        FACILITY,
        SHOP,
        FOOD
    }

    public interface OnButtonGroupListener {
        void onItemClick(ButtonType type, boolean isSelected);
    }
}
