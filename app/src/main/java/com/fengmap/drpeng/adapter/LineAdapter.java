package com.fengmap.drpeng.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.fengmap.android.FMDevice;
import com.fengmap.android.wrapmv.FMActivityManager;
import com.fengmap.android.wrapmv.FMRouteManager;
import com.fengmap.android.wrapmv.entity.FMRoute;
import com.android.mgwaiter.R;
import com.fengmap.drpeng.widget.DrawableCenterTextView;


/**
 * 路线适配器。
 * Created by yangbin on 16/8/14.
 */
public class LineAdapter extends RecyclerView.Adapter<LineAdapter.LineItemHolder> {
    private Context   mContext;
    private FMRoute[] mRoutes;
    private int mSelectedPosition = -1;

    private WorkAdapter mWorkAdapter;

    private OnLineItemSelectedListener mListener = null;


    public LineAdapter(Context context) {
        this.mContext = context;
        mWorkAdapter = new WorkAdapter(context);
    }

    public LineAdapter(Context context, FMRoute[] pRoutes) {
        this.mContext = context;
        mRoutes = pRoutes;
        mWorkAdapter = new WorkAdapter(context);
    }

    public WorkAdapter getWorkAdapter() {
        return mWorkAdapter;
    }

    public void setManager(FMRouteManager pRouteManager, FMActivityManager pActivityManager) {
        mWorkAdapter.setManager(pRouteManager, pActivityManager);
    }

    /**
     * before LineAdapter.
     * @param pRoutes
     */
    public void setData(FMRoute[] pRoutes) {
        mRoutes = pRoutes;

        String[][] acts = new String[pRoutes.length][];
        for (int i=0; i<pRoutes.length; i++) {
            acts[i] = pRoutes[i].getActivityCodeList();
        }
        mWorkAdapter.setData(acts);

        notifyDataSetChanged();
    }

    @Override
    public LineItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LineItemHolder holder = new LineItemHolder(new DrawableCenterTextView(mContext));
        return holder;
    }

    @Override
    public void onBindViewHolder(LineItemHolder holder, final int position) {
        if (mRoutes.length==0) {
            return;
        }
        FMRoute currRoute = mRoutes[position];
        holder.setText(currRoute.getRouteName());
        holder.setSelected(mSelectedPosition == position);

        holder.mLineText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedPosition == position) {  // 过滤重复点击
                    return;
                }
                mSelectedPosition = position;
                mWorkAdapter.setCurrentLineIndex(mSelectedPosition);

                if (mListener != null) {
                    mListener.onItemSelected(position);
                }

                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRoutes.length;
    }

    public int getSelectedPosition() {
        return mSelectedPosition;
    }

    public void setSelectedPosition(int position) {
        mSelectedPosition = position;
    }

    public FMRoute getRoute(int position) {
        return mRoutes[position];
    }


    public void setOnLineItemSelectedListener(OnLineItemSelectedListener pListener) {
        mListener = pListener;
    }


    /**
     * Item holder
     */
    class LineItemHolder extends RecyclerView.ViewHolder {
        private DrawableCenterTextView mLineText;

        public LineItemHolder(View itemView) {
            super(itemView);
            Resources res = mContext.getResources();

            mLineText = (DrawableCenterTextView) itemView;
            int lr = (int) (12 * FMDevice.getDeviceDensity());
            int bt = (int) (8 * FMDevice.getDeviceDensity());
            mLineText.setPadding(lr, bt, lr, bt);
            mLineText.setBackgroundResource(R.drawable.btn_corner_coffee);
            mLineText.setGravity(Gravity.CENTER);
            mLineText.setTextColor(Color.WHITE);
            mLineText.setTextSize(14);
            Drawable d = res.getDrawable(R.mipmap.fm_route_position);
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            mLineText.setCompoundDrawables(d, null, null, null);
            mLineText.setCompoundDrawablePadding((int) (3 * FMDevice.getDeviceDensity()));
        }

        public void setText(String text) {
            mLineText.setText(text);
        }

        public void setTextSize(float textSize) {
            mLineText.setTextSize(textSize);
        }

        public void setTextColor(int color) {
            mLineText.setTextColor(color);
        }

        public void setSelected(boolean selected) {
            if (selected) {
                mLineText.setBackgroundResource(R.drawable.btn_corner_green_selected);
            } else {
                mLineText.setBackgroundResource(R.drawable.btn_corner_coffee);
            }
        }
    }


    /**
     * 定义监听接口。
     */
    public interface OnLineItemSelectedListener {

        /**
         * 选中某一项。
         * @param position 选中项的下标索引
         */
        void onItemSelected(int position);
    }

}
