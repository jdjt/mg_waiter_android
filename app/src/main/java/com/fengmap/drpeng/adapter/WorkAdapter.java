package com.fengmap.drpeng.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.fengmap.android.wrapmv.FMActivityManager;
import com.fengmap.android.wrapmv.FMRouteManager;
import com.fengmap.android.wrapmv.entity.FMActivity;
import com.fengmap.drpeng.widget.RouteWorkerItemView;

/**
 * 业态适配器。
 * Created by yangbin on 16/8/14.
 */
public class WorkAdapter extends RecyclerView.Adapter<WorkAdapter.WorkerItemHolder> {
    private Context mContext;
    private String[][] mActs = new String[1][0];

    private FMRouteManager    mRouteManager;
    private FMActivityManager mActivityManager;

    private OnWorkerItemSelectedListener mItemListener;
    private int mSelectedPosition = -1;
    private int mCurrentLineIndex = 0;


    public WorkAdapter(Context pContext) {
        mContext = pContext;
    }


    public WorkAdapter(Context pContext, String[][] pActs) {
        mContext = pContext;
        mActs = pActs;
    }

    public void setManager(FMRouteManager pRouteManager, FMActivityManager pActivityManager) {
        mRouteManager = pRouteManager;
        mActivityManager = pActivityManager;
    }


    public void setData(String[][] pActs) {
        mActs = pActs;
        notifyDataSetChanged();
    }

    public void setCurrentLineIndex(int index) {
        this.mCurrentLineIndex = index;
        notifyDataSetChanged();
    }


    @Override
    public WorkerItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RouteWorkerItemView view = new RouteWorkerItemView(mContext);

        WorkerItemHolder holder = new WorkerItemHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(WorkerItemHolder holder, final int position) {
        if (mActs[0].length == 0 ) {
            return;
        }

        FMActivity act = mActivityManager.getActivity(mActs[mCurrentLineIndex][position]);

        RouteWorkerItemView itemView = holder.getItemTextView();
        computeMaxHeight(itemView);

        itemView.setCircleText(String.valueOf(position+1));
        itemView.setVerticalText(act.getActivityName());
        itemView.setSelected(mSelectedPosition == position);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemListener!=null) {
                    if (mSelectedPosition == position) {      // 过滤重复点击
                        return;
                    }
                    mSelectedPosition = position;
                    notifyDataSetChanged();

                    mItemListener.onWorkerItemSelected(mCurrentLineIndex, position);
                }
            }
        });
    }

    private int mMaxHeight = 0;
    private void computeMaxHeight(RouteWorkerItemView view) {
        view.measure(0, 0);
        int h = view.getMeasuredHeight();
        mMaxHeight = mMaxHeight > h ? mMaxHeight : h;
    }

    public int getMaxHeightAboveItems() {
        return mMaxHeight;
    }

    public void initMaxHeight(int height) {
        mMaxHeight = height;
    }


    @Override
    public int getItemCount() {
        return mActs[mCurrentLineIndex].length;
    }


    public void setSelectedPosition(int position) {
        mSelectedPosition = position;
    }


    public void setOnWorkerItemSelectedListener(OnWorkerItemSelectedListener pListener) {
        this.mItemListener = pListener;
    }


    class WorkerItemHolder extends RecyclerView.ViewHolder {

        private RouteWorkerItemView mItemView;

        public WorkerItemHolder(View itemView) {
            super(itemView);
            mItemView = (RouteWorkerItemView) itemView;
        }

        public RouteWorkerItemView getItemTextView() {
            return mItemView;
        }

    }


    public interface OnWorkerItemSelectedListener {
        void onWorkerItemSelected(int routePosition, int workerPosition);
    }

}
