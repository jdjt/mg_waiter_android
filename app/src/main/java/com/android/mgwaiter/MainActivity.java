package com.android.mgwaiter;

import android.content.Context;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.mgwaiter.login.LoginActivity;
import com.fengmap.drpeng.MapMainActivity;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

   private Context context;
    private SwipeRefreshLayout wrfLayout;   //下拉刷新控件
    private TextView tv_taking_Off;    //下班
    private TextView tv_count;        //统计
    private TextView tv_worktime;   //工作时长
    private TextView tv_master_number;  //工号
    private TextView tv_master_name;//姓名
    private TextView tv_master_division;//所属部门
    private Button bt_work_state;//接单
    private Button bt_tasking;//进行的任务
    private ListView task_list;//任务列表
    private  TextView tv_service_time;//服务时长
    private ImageView iv_record;
    private ImageView iv_message;//消息
    private LinearLayout ll_task_time;//服务时长布局
    private RelativeLayout rl_taskcontent;
    private TextView tv_name;//客户姓名
    private TextView tv_number;//房间号
    private TextView  tv_call_region;//呼叫区域
    private TextView  tv_call_content;//呼叫内容
    private TextView  tv_ordertime;//下单时间
    private Button bt_end;//完成
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;
        init();
    }
    public void init(){
        wrfLayout= (SwipeRefreshLayout) findViewById(R.id.sl_container);
         tv_taking_Off= (TextView) findViewById(R.id.tv_takingoff);
        tv_count= (TextView) findViewById(R.id.tv_count);
        tv_worktime= (TextView) findViewById(R.id.tv_worktime);
        tv_master_number= (TextView) findViewById(R.id.tv_master_number);
        tv_master_name= (TextView) findViewById(R.id.tv_master_name);
        tv_master_division= (TextView) findViewById(R.id.tv_master_division);
        bt_work_state= (Button) findViewById(R.id. bt_work_state);
        bt_tasking= (Button) findViewById(R.id.bt_tasking);
        task_list= (ListView) findViewById(R.id.task_list);
        tv_service_time= (TextView) findViewById(R.id.tv_service_time);
        iv_record= (ImageView) findViewById(R.id. iv_record);
        iv_message= (ImageView) findViewById(R.id.iv_message);
        ll_task_time= (LinearLayout) findViewById(R.id.ll_task_time);
        rl_taskcontent= (RelativeLayout) findViewById(R.id.rl_taskcontent);
        tv_name= (TextView) findViewById(R.id.tv_name);
        tv_number= (TextView) findViewById(R.id.tv_number);
        tv_call_region= (TextView) findViewById(R.id.tv_call_region);
        tv_call_content= (TextView) findViewById(R.id.tv_call_content);
        tv_ordertime= (TextView) findViewById(R.id.tv_ordertime);
        bt_end= (Button) findViewById(R.id.bt_end);
        tv_taking_Off.setOnClickListener(this);
        tv_count.setOnClickListener(this);
        bt_work_state.setOnClickListener(this);
        bt_end.setOnClickListener(this);
        wrfLayout.setColorSchemeResources(R.color.blue_70,R.color.material_deep_teal_50,
                R.color.blue_btn_bg_color,R.color.error_stroke_color);
        wrfLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {

            }
        });
    };
    //点击事件
    public void onClick(View v) {
         switch (v.getId()){
             //下班
             case R.id.tv_takingoff:
                 break;
             //接单
             case R.id.bt_work_state:
                 openMap();
                 break;
             //完成
             case R.id.bt_end:

                 break;
             //统计
             case R.id.tv_count:
                startActivity(new Intent(this,TaskStatisticsActivity.class));
                 break;
         }
    }


    //打开地图
    private void openMap() {
        startActivity(new Intent(this, MapMainActivity.class));
    }
}
