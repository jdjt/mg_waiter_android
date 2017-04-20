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
 * @ FileName:StatusTitle
 * @ Date 2017/4/17 15:14
 */
public class StatusTitle extends RelativeLayout implements View.OnClickListener{
  private RelativeLayout action_bar_container;
  private LinearLayout ll_action_bar_left;
  private TextView tv_action_bar_left;
  private LinearLayout ll_action_bar_right;
  private TextView tv_action_bar_right;
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
    action_bar_container = (RelativeLayout) findViewById(R.id.action_bar_container);

    ll_action_bar_left = (LinearLayout) findViewById(R.id.ll_action_bar_left);
    ll_action_bar_left.setOnClickListener(this);
    tv_action_bar_left = (TextView) findViewById(R.id.tv_action_bar_left);

    ll_action_bar_right = (LinearLayout) findViewById(R.id.ll_action_bar_right);
    ll_action_bar_right.setOnClickListener(this);
    tv_action_bar_right = (TextView) findViewById(R.id.tv_action_bar_right);

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
      ll_action_bar_left.setBackgroundResource(R.color.title_bg);
      tv_action_bar_left.setTextColor(getResources().getColor(R.color.white));
      ll_action_bar_right.setBackgroundResource(R.color.gray_btn_false);
      tv_action_bar_right.setTextColor(getResources().getColor(R.color.white));
    } else {
      ll_action_bar_right.setBackgroundResource(R.color.title_bg);
      tv_action_bar_right.setTextColor(getResources().getColor(R.color.white));
      ll_action_bar_left.setBackgroundResource(R.color.gray_btn_false);
      tv_action_bar_left.setTextColor(getResources().getColor(R.color.white));
    }

    this.isCenterLeft = isLeft;
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()){
      case R.id.ll_action_bar_left: // 中心左键
        setCenterCheckedItem(true);
        onTitleListener.onCenterLeft();
        break;

      case R.id.ll_action_bar_right: // 中心右键
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
