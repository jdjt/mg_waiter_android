package com.android.mgwaiter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
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
//    private TextView rr, rrr;//首页返回按钮和日历按钮的  （占位）
//    private StatusTitle statusTitle;
////    TaskAdapter adapter;
//
//
//    List<String> parent = null;//列表头
//    Map<String, List<String>> map = null;//列表详情
//    private ViewPager viewpager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_statistics_activity);
//        inView();
//        initStatusBar();
    }
//
//    //初始化控件
//    private void inView() {
//        viewpager = (ViewPager) findViewById(R.id.viewpager);
//
//    }
//
//    private void initStatusBar() {
//        statusTitle = (StatusTitle) findViewById(R.id.status_title);
//        initData();
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        adapter = new TaskAdapter(fragmentManager);
//
//
////        statusTitle.setViewPager(viewpager);
//        statusTitle.setOnTitleListener(new StatusTitle.OnTitleListener() {
//            @Override
//            //已完成点击事件
//            public void onCenterLeft() {
//                viewpager.setCurrentItem(0);
//            }
//
//            //已取消点击事件
//            @Override
//            public void onCenterRight() {
//                viewpager.setCurrentItem(1);
//            }
//        });
//
//        viewpager.setAdapter(adapter);
//    }
//
//    private List<Fragment> list_fragment;
//
//    //初始化按钮数据
//    private void initData() {
//        list_fragment = new ArrayList<>();
//        list_fragment.add(new FinishFragment());
//        list_fragment.add(new AbolishFragment());
//    }
//
//
//    boolean[] fragmentsUpdateFlag = {false, false};
//
//
//
//    public class TaskAdapter extends FragmentPagerAdapter {
//        FragmentManager fm;
//
//        public TaskAdapter(FragmentManager fm) {
//            super(fm);
//            this.fm = fm;
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            return null;
//        }
//
//        @Override
//        public int getCount() {
//                return list_fragment.size();
//        }
//    }
//
//
////    public Object instantiateItem(ViewGroup container, int position) {
////        //得到缓存的fragment
////        Fragment fragment = (Fragment) super.instantiateItem(container,position);
////        //得到tag
////        String fragmentTag = fragment.getTag();
////        if (fragmentsUpdateFlag[position % fragmentsUpdateFlag.length]) {
////            //如果这个fragment需要更新
////            FragmentTransaction ft = fm.beginTransaction();
////            //移除旧的fragment
////            ft.remove(fragment);
////            //换成新的fragment
////            fragment = list_fragment.get(position % list_fragment.size());
////            //添加新fragment时必须用前面获得的tag ❶
////            ft.add(container.getId(), fragment, fragmentTag);
////            ft.attach(fragment);
////            ft.commit();
////            //复位更新标志
////            fragmentsUpdateFlag[position % fragmentsUpdateFlag.length] = false;
////
////        }
////        return fragment;
////
////    }
//
//    public Fragment getItem(int position) {
//        return list_fragment.get(position);
////            switch (position) {
////                case 0:
////                    if (finishFragment == null) {
////                        finishFragment = new FinishFragment();
////                    }
////                    return finishFragment;
////                case 1:
////                    if (abolishFragment == null) {
////                        abolishFragment = new AbolishFragment();
////                    }
////                    return abolishFragment;
////                default:
////                    return null;
////
////            }
//    }

}

