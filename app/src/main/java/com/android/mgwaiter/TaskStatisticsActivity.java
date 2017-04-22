package com.android.mgwaiter;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.mgwaiter.Fragment.AbolishFragment;
import com.android.mgwaiter.Fragment.FinishFragment;
import com.android.mgwaiter.view.StatusTitle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by huyanan on 2017/4/17.
 * 统计页面
 */

public class TaskStatisticsActivity extends AppCompatActivity {
    private TextView tv_date,tv_order_task,tv_miss_task,tv_general_assignment,tv_order_rate;
    private RadioGroup radioGroup;
    TaskAdapter adapter;


    List<String> parent = null;//列表头
    Map<String, List<String>> map = null;//列表详情
    private ViewPager viewpager;
    Fragment finishFragment,abolishFragment;
    TabLayout tb_title;
    RadioButton rb_all,rb_autonomously,rb_system;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_statistics_activity);
        inView();
        initStatusBar();
    }

    //初始化控件
    private void inView() {
        viewpager = (ViewPager) findViewById(R.id.viewpager);//viewpager
        tv_order_task = (TextView) findViewById(R.id.tv_order_task);//接单任务总计
        tv_miss_task = (TextView) findViewById(R.id.tv_miss_task);//未接单任务总计
        tv_general_assignment = (TextView) findViewById(R.id.tv_general_assignment);//推送总计
        tv_order_rate = (TextView) findViewById(R.id.tv_order_rate);//接单率
        tb_title= (TabLayout) findViewById(R.id.tb_title);
        rb_all = (RadioButton)findViewById(R.id.rb_all);
        rb_autonomously = (RadioButton)findViewById(R.id.rb_autonomously);
        rb_system = (RadioButton)findViewById(R.id.rb_system);
        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
//        checkRadio();

    }

    private void initStatusBar() {
        initData();
        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter = new TaskAdapter(fragmentManager);
        viewpager.setAdapter(adapter);
        viewpager.setOnGenericMotionListener(new View.OnGenericMotionListener() {
            @Override
            public boolean onGenericMotion(View view, MotionEvent motionEvent) {
                return false;
            }
        });
        tb_title.setupWithViewPager(viewpager);
    }
//    private void checkRadio() {
//        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                pager.setPage_index("0");
//                imgList.clear();
//                adapter.notifyDataSetChanged();
//                switch (radioGroup.getCheckedRadioButtonId()) {
//                    case R.id.rb_all:
//
//                        getBusiness(1, "");
//                        break;
//                    case R.id.rb_autonomously:
//
//                        getBusiness(2, "");
//                        break;
//                    case R.id.rb_system:
//
//                        getBusiness(3, "");
//
//                        break;
//
//
//                }
//            }
//        });
//        radioGroup.check(R.id.rb_all);
//
//    }
    private List<Fragment> list_fragment;

    //初始化按钮数据
    private void initData() {
        list_fragment = new ArrayList<>();
        list_fragment.add(new FinishFragment());
        list_fragment.add(new AbolishFragment());
    }


    boolean[] fragmentsUpdateFlag = {false, false};

    public class TaskAdapter extends FragmentPagerAdapter {
        FragmentManager fm;
        private List<String> list_Title;//tab名的列表  
        public TaskAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
            list_Title=new ArrayList<>();
            list_Title.add("已完成");
            list_Title.add("已取消");
        }

        @Override
        public Fragment getItem(int position) {
            return list_fragment.get(position);
        }
        @Override
        public int getCount() {
                return list_fragment.size();
        }

        //此方法用来显示tab上的名字
        @Override
        public CharSequence getPageTitle(int position) {
            return list_Title.get(position % list_Title.size());
        }
        public Object instantiateItem(ViewGroup container, int position) {
            //得到缓存的fragment
            Fragment fragment = (Fragment) super.instantiateItem(container,position);
            Log.e("TaskStatisticsActivity","position ="+position);
            //得到tag
            String fragmentTag = fragment.getTag();
            if (fragmentsUpdateFlag[position % fragmentsUpdateFlag.length]) {
                //如果这个fragment需要更新
                FragmentTransaction ft = fm.beginTransaction();
                //移除旧的fragment
                ft.remove(fragment);
                //换成新的fragment
                fragment = list_fragment.get(position % list_fragment.size());
                //添加新fragment时必须用前面获得的tag ❶
                ft.add(container.getId(), fragment, fragmentTag);
                ft.attach(fragment);
                ft.commit();
                //复位更新标志
                fragmentsUpdateFlag[position % fragmentsUpdateFlag.length] = false;
            }
            return fragment;
        }


    }

}

