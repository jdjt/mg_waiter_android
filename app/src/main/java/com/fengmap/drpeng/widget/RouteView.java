package com.fengmap.drpeng.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fengmap.android.FMDevice;
import com.fengmap.android.wrapmv.FMActivityManager;
import com.fengmap.android.wrapmv.FMRouteManager;
import com.fengmap.android.wrapmv.entity.FMRoute;
import com.android.mgwaiter.R;
import com.fengmap.drpeng.adapter.LineAdapter;
import com.fengmap.drpeng.common.CustomLinearLayoutManager;
import com.fengmap.drpeng.common.DividerItemDecoration;


/**
 * 线路视图。
 * Created by yangbin on 16/8/10.
 */

public class RouteView extends LinearLayout{
    private Context      mContext;
    private RecyclerView mLineView;
    private LineAdapter  mLineAdapter;
    private ImageView    mLineLeftView;
    private ImageView    mLineRightView;

    private RecyclerView mWorkView;
    private ImageView    mWorkLineView;
    private ImageView    mWorkLeftView;
    private ImageView    mWorkRightView;

    private CustomLinearLayoutManager mLineLinearLayoutManager;
    private CustomLinearLayoutManager mWorkLinearLayoutManager;


    public RouteView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public RouteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    private void init() {
        View.inflate(mContext, R.layout.view_route, this);
        this.setOrientation(VERTICAL);

        mLineView = (RecyclerView) findViewById(R.id.fm_route_list);
        mWorkLineView = (ImageView) findViewById(R.id.fm_route_work_line);
        mWorkView = (RecyclerView) findViewById(R.id.fm_work_list);

        mLineView.setLayoutManager(mLineLinearLayoutManager = new CustomLinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mWorkView.setLayoutManager(mWorkLinearLayoutManager = new CustomLinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));

        Drawable              d  = mContext.getResources().getDrawable(R.drawable.fm_t_divide);
        DividerItemDecoration dd = new DividerItemDecoration(d, true, true);

        mLineView.setHasFixedSize(false);
        mLineView.addItemDecoration(dd);
        mLineView.setClickable(true);

        d = mContext.getResources().getDrawable(R.drawable.fm_t_divide2);
        dd = new DividerItemDecoration(d, true, true);
        mWorkView.setHasFixedSize(false);
        mWorkView.addItemDecoration(dd);
        mWorkView.setNestedScrollingEnabled(false);
        mWorkView.setClickable(true);

        mLineLeftView = (ImageView) findViewById(R.id.fm_route_left);
        mLineRightView = (ImageView) findViewById(R.id.fm_route_right);
        mWorkLeftView = (ImageView) findViewById(R.id.fm_route_work_left);
        mWorkRightView = (ImageView) findViewById(R.id.fm_route_work_right);

        initAdapter();

        // 处理线  注意,这个半径是RouteWorkerItemView圆的半径 - 线的高度一半
        float offsetY = (14-2) * FMDevice.getDeviceDensity();
        mWorkLineView.setTranslationY(offsetY);

    }

    private void initAdapter() {
        mLineAdapter = new LineAdapter(mContext);
        mLineView.setAdapter(mLineAdapter);
        mWorkView.setAdapter(mLineAdapter.getWorkAdapter());

        dealFlag();
    }

    public RecyclerView getWorkView() {
        return mWorkView;
    }

    public RecyclerView getLineView() {
        return mLineView;
    }


    public void setManager(FMRouteManager pRouteManager, FMActivityManager pActivityManager) {
        mLineAdapter.setManager(pRouteManager, pActivityManager);
    }

    public void setData(FMRoute[] pRoutes) {
        mLineAdapter.setData(pRoutes);
    }


    public void dealFlag() {
        mLineView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int fistVisiblePos = mLineLinearLayoutManager.findFirstCompletelyVisibleItemPosition();
                int lastVisiblePos = mLineLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (fistVisiblePos == 0) {
                    mLineLeftView.setImageResource(R.mipmap.fm_left_normal);
                } else {
                    mLineLeftView.setImageResource(R.mipmap.fm_left_press);
                }
                if (lastVisiblePos == mLineAdapter.getItemCount()-1) {
                    mLineRightView.setImageResource(R.mipmap.fm_right_normal);
                } else {
                    mLineRightView.setImageResource(R.mipmap.fm_right_press);
                }
            }
        });
        mWorkView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int fistVisiblePos = mWorkLinearLayoutManager.findFirstCompletelyVisibleItemPosition();
                int lastVisiblePos = mWorkLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (fistVisiblePos == 0) {
                    mWorkLeftView.setImageResource(R.mipmap.fm_left_normal);
                } else {
                    mWorkLeftView.setImageResource(R.mipmap.fm_left_press);
                }
                if (lastVisiblePos == mLineAdapter.getWorkAdapter().getItemCount()-1) {
                    mWorkRightView.setImageResource(R.mipmap.fm_right_normal);
                } else {
                    mWorkRightView.setImageResource(R.mipmap.fm_right_press);
                }
            }
        });
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int measureWidth = FMDevice.getDeviceWidth();
        int workMaxHeight = mLineAdapter.getWorkAdapter().getMaxHeightAboveItems();

        ViewGroup.LayoutParams params = mWorkView.getLayoutParams();
        params.height = workMaxHeight;
        mWorkView.setLayoutParams(params);

        float height = 50 + 0.5f + 16;
        int measureHeight = (int) (height * FMDevice.getDeviceDensity() ) + workMaxHeight;

        // 设置自定义的控件MyViewGroup的大小
        setMeasuredDimension(measureWidth, measureHeight);
    }


}
