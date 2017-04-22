package com.fengmap.drpeng.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fengmap.android.FMDevice;
import com.android.mgwaiter.R;
import com.fengmap.drpeng.entity.Floor;

import java.util.ArrayList;
import java.util.List;


/**
 * 切换楼层
 * @author Yang
 * @date 2015年1月14日 下午5:45:10
 */
public class SwitchFloorView extends LinearLayout {
    private SlideOnePageGallery mGallery;
    private List<Floor> mFloors = new ArrayList<Floor>();

    public void setFloors(List<Floor> floors) {
        this.mFloors = floors;
        mItemAdapter.notifyDataSetChanged();
    }

    private Context mContext;
    private ItemAdapter mItemAdapter;

    private float mBgWidth;
    private float mBgHeight;

    private int mCurrentPosition = 0;

    public int getCurrentPosition() {
        return mCurrentPosition;
    }

    private OnCallBackFloor mCallBackFloor;

    public void setCallBackFloor(OnCallBackFloor callBackFloor) {
        this.mCallBackFloor = callBackFloor;
    }

    public SwitchFloorView(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public SwitchFloorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    public SwitchFloorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        initView();
    }

    private void initView() {
        this.setOrientation(LinearLayout.HORIZONTAL);
        View.inflate(mContext, R.layout.view_layout_gallery, this);
        this.setWillNotDraw(false);
        mGallery = (SlideOnePageGallery) findViewById(R.id.gallery);
        mGallery.setCallbackDuringFling(false);

        mBgWidth = FMDevice.getDeviceWidth();
        mBgHeight = 50 * FMDevice.getDeviceDensity();

        mItemAdapter = new ItemAdapter();
        mGallery.setAdapter(mItemAdapter);
        mGallery.setOnItemSelectedListener(selectedListener);
        mPaint = new Paint();
    }

    public void setSelectedPosition(int position) {
        mCurrentPosition = position;
        mGallery.setSelection(mCurrentPosition);
        mItemAdapter.notifyDataSetChanged();
    }

    public void setSelectedGroupId(int groupId) {
        for (int i=0; i<mFloors.size(); i++) {
            if (mFloors.get(i).getGroupId() == groupId) {
                setSelectedPosition(i);
                return;
            }
        }

    }


    private Paint mPaint;

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(getResources().getColor(R.color.white));
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Style.FILL);
        RectF rectBG = new RectF(0, 0, mBgWidth, mBgHeight);

        // 画边缘线
        Paint p = new Paint();
        p.setColor(Color.DKGRAY);
        p.setStrokeWidth(1);
        p.setAntiAlias(true);
        p.setStyle(Style.STROKE);
        canvas.drawRect(rectBG, p);
        canvas.drawCircle(mBgWidth / 2, mBgHeight / 2, 32 * FMDevice.getDeviceDensity(), p);

        // 填充
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Style.FILL);
        rectBG = new RectF(0, 0, mBgWidth, mBgHeight - 1);
        canvas.drawRect(rectBG, mPaint);
        canvas.drawCircle(mBgWidth / 2, (mBgHeight-2) / 2, 32 * FMDevice.getDeviceDensity(), mPaint);

        mPaint.setStyle(Style.FILL);
        mPaint.setColor(getResources().getColor(R.color.green));
        canvas.drawCircle(mBgWidth / 2, mBgHeight / 2, 20 * FMDevice.getDeviceDensity(), mPaint);

        super.onDraw(canvas);
    }

    /**
     * 楼层适配器
     */
    public class ItemAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mFloors.size();
        }

        @Override
        public Floor getItem(int position) {
            return mFloors.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView;
            if (convertView == null) {
                textView = new TextView(mContext);
                convertView = textView;
            } else {
                textView = (TextView) convertView;
            }

            String name = getItem(position).getGroupName();
            if (!TextUtils.isEmpty(name)) {
                textView.setText(name.toUpperCase());
            } else {
                textView.setText(null);
            }
            textView.setTextSize(16);
            if (mCurrentPosition == position) {
                textView.setTextColor(getResources().getColor(R.color.white));
            } else {
                textView.setTextColor(getResources().getColor(R.color.green));
            }
            textView.setGravity(Gravity.CENTER);

            textView.setLayoutParams(new Gallery.LayoutParams((int)(85 * FMDevice.getDeviceDensity()),
                                                              (int)(50 * FMDevice.getDeviceDensity())));

            return convertView;
        }

    }

    private TextView mLastTextView;
    /**
     * 选中监听
     */
    private OnItemSelectedListener selectedListener = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (mCurrentPosition == position) {
                return;
            }
            if (mLastTextView == null) {
                mLastTextView = (TextView) mGallery.getChildAt(mCurrentPosition%mGallery.getChildCount());
            }

            mLastTextView.setTextSize(16);
            mLastTextView.setTextColor(getResources().getColor(R.color.green));

            mCurrentPosition = position;
            if (view != null) {
                mLastTextView = ((TextView) view);
                mLastTextView.setTextSize(18);
                mLastTextView.setTextColor(Color.WHITE);
            }

            if (mCallBackFloor != null) {
                mCallBackFloor.switchFloor(mFloors.get(mCurrentPosition));
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    };


    public interface OnCallBackFloor {
        /**
         * 查看楼层
         * @param pFloor
         */
        void switchFloor(Floor pFloor);

    }


}
