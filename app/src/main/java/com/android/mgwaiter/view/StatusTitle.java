package com.android.mgwaiter.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.mgwaiter.R;


/**
 * @author hyn
 * @Description:
 * @FileName:StatusTitle
 * @Package
 * @Date 2016/9/19 15:14
 */
public class StatusTitle extends RelativeLayout implements View.OnClickListener{
  private RelativeLayout mContainer;
  private LinearLayout mLLCenterLeft;
  private TextView mCenterLeftTxt;
  private LinearLayout mLLCenterRight;
  private TextView mCenterRightTxt;
  private ViewPager pager;


  private OnTitleListener onTitleListener;
  private boolean isCenterLeft = true; // 当前中心左侧为选中项

  public StatusTitle(Context context) {
    this(context, null);
  }

  public StatusTitle(Context context, AttributeSet attrs) {
    super(context, attrs);

    initView(context);
  }
  public void setViewPager(ViewPager pager) {
    this.pager = pager;

    if (pager.getAdapter() == null) {
      throw new IllegalStateException("ViewPager does not have adapter instance.");
    }

//    pager.setOnPageChangeListener(pageListener);
//
//    notifyDataSetChanged();
  }


  /*
  * 初始化actionBar上的控件对象
  * */
  private void initView(Context context){
    LayoutInflater.from(context).inflate(R.layout.activity_task_title, this);
    mContainer = (RelativeLayout) findViewById(R.id.action_bar_container);

    mLLCenterLeft = (LinearLayout) findViewById(R.id.action_bar_ll_center_left);
    mLLCenterLeft.setOnClickListener(this);
    mCenterLeftTxt = (TextView) findViewById(R.id.action_bar_center_left_txt);

    mLLCenterRight = (LinearLayout) findViewById(R.id.action_bar_ll_center_right);
    mLLCenterRight.setOnClickListener(this);
    mCenterRightTxt = (TextView) findViewById(R.id.action_bar_center_right_txt);

  }

  /*
  * 设置监听器
  * */
  public void setOnTitleListener(OnTitleListener onTitleListener){
    this.onTitleListener = onTitleListener;
  }

  /*
  * 设置选中项
  * @param isLeft true: 表示左侧; false: 表示右侧
  * */
  private void setCenterCheckedItem(boolean isLeft){
    if (isCenterLeft == isLeft){ // 单击选中项无效
      return;
    }

    if (isLeft){ // 左侧选中
      mLLCenterLeft.setBackgroundResource(R.color.title_bg);
      mCenterLeftTxt.setTextColor(getResources().getColor(R.color.black));
      mLLCenterRight.setBackgroundResource(R.color.white);
      mCenterRightTxt.setTextColor(getResources().getColor(R.color.black));
    } else {
      mLLCenterRight.setBackgroundResource(R.color.title_bg);
      mCenterRightTxt.setTextColor(getResources().getColor(R.color.black));
      mLLCenterLeft.setBackgroundResource(R.color.white);
      mCenterLeftTxt.setTextColor(getResources().getColor(R.color.black));
    }

    this.isCenterLeft = isLeft;
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()){
      case R.id.action_bar_ll_center_left: // 中心左键
        setCenterCheckedItem(true);
        onTitleListener.onCenterLeft();
        break;

      case R.id.action_bar_ll_center_right: // 中心右键
        setCenterCheckedItem(false);
        onTitleListener.onCenterRight();
        break;
    }
  }

  /*
  * Title_1监听事件类
  * */
  public interface OnTitleListener{
    public abstract void onCenterLeft();
    public abstract void onCenterRight();
  }

}
