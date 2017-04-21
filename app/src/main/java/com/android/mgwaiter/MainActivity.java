package com.android.mgwaiter;

import android.app.Activity;
import android.content.Context;

import android.graphics.Color;


import android.content.Intent;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.mgwaiter.login.LoginActivity;

import com.android.mgwaiter.net.Constant;
import com.android.mgwaiter.net.ReqCallBack;
import com.android.mgwaiter.net.RequestManager;
import com.android.mgwaiter.view.Tasktimer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener {

    private Context context;
    private SwipeRefreshLayout wrfLayout;   //下拉刷新控件
    private TextView tv_taking_Off;    //下班
    private TextView tv_count;        //统计
    private TextView tv_master_number;  //工号
    private TextView tv_master_name;//姓名
    private TextView tv_master_division;//所属部门
    private Button bt_work_state;//接单
    private Button bt_tasking;//进行的任务
    private ListView task_list;//任务列表
    private Tasktimer service_time;//服务时长
    private ImageView iv_record;
    private ImageView iv_message;//消息
    private TextView tv_name;//客户姓名
    private TextView tv_number;//房间号
    private TextView tv_call_region;//呼叫区域
    private TextView tv_call_content;//呼叫内容
    private TextView tv_ordertime;//下单时间
    private Button bt_end;//完成
    private Tasktimer mWorkTimer;
    private LinearLayout ll_layout_bottom;
    private String workingState = "0";   //服务员工作状态
    private List<HashMap<String,String>> taskingList;
    private TaskingListAdapter taskingAdapter;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        int i  = dm.widthPixels;
//        int heightPixels = dm.heightPixels;
        setContentView(R.layout.activity_main);
        context = this;
         taskingList=new ArrayList<>();
        init();
    }

    public void init() {
        wrfLayout = (SwipeRefreshLayout) findViewById(R.id.sl_container);
        tv_taking_Off = (TextView) findViewById(R.id.tv_takingoff);
        tv_count = (TextView) findViewById(R.id.tv_count);
        tv_master_number = (TextView) findViewById(R.id.tv_master_number);
        tv_master_name = (TextView) findViewById(R.id.tv_master_name);
        tv_master_division = (TextView) findViewById(R.id.tv_master_division);
        bt_work_state = (Button) findViewById(R.id.bt_work_state);
        bt_tasking = (Button) findViewById(R.id.bt_tasking);
        task_list = (ListView) findViewById(R.id.task_list);
        service_time= (Tasktimer) findViewById(R.id.tv_service_time);
        iv_record = (ImageView) findViewById(R.id.iv_record);
        iv_message = (ImageView) findViewById(R.id.iv_message);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_number = (TextView) findViewById(R.id.tv_number);
        ll_layout_bottom= (LinearLayout) findViewById(R.id.ll_layout_bottom);
        tv_call_region = (TextView) findViewById(R.id.tv_call_region);
        tv_call_content = (TextView) findViewById(R.id.tv_call_content);
        tv_ordertime = (TextView) findViewById(R.id.tv_ordertime);
        bt_end = (Button) findViewById(R.id.bt_end);
        mWorkTimer = (Tasktimer) findViewById(R.id.tv_worktimestart);
        tv_taking_Off.setOnClickListener(this);
        tv_count.setOnClickListener(this);
        bt_work_state.setOnClickListener(this);
        bt_end.setOnClickListener(this);
        wrfLayout.setColorSchemeResources(R.color.blue_70, R.color.material_deep_teal_50,
                R.color.blue_btn_bg_color, R.color.error_stroke_color);
        wrfLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {

            }
        });
        intTimerUi(0);
    }


    protected void onStart() {
        super.onStart();
        /*
        获取员工信息
         */
        RequestManager.getInstance(context).requestAsyn(Constant.GETMASTERMESSAGE, RequestManager.TYPE_POST_JSON, new HashMap<String, String>(), new ReqCallBack<String>() {
            public void onReqSuccess(String result) {

            }

            public void onReqFailed(String errorMsg) {

            }
        });

    }

    //点击事件
    public void onClick(View v) {

        switch (v.getId()) {
            //下班
            case R.id.tv_takingoff:
                break;
            //接单
            case R.id.bt_work_state:

                switch (Integer.parseInt(workingState)){
                    case 0:
                        workingState = "2";
                        bt_work_state.setText("停止抢单");
                        bt_work_state.setBackgroundResource(R.drawable.work_state_end_btn);
//                        settingMasterState(workingState);
//                        getMaskingList();

                        break;
                    case 1:
                        break;
                    case 2:
                        showdialog("请确认停止接单","确认","取消");

                        break;
                }
                break;
            //完成
            case R.id.bt_end:
              bt_end.setFocusable(false);
                bt_end.setBackgroundResource(R.drawable.work_state_btn_tasking);
                break;
            //统计
            case R.id.tv_count:

                break;
        }
    }
  /*
  初始化工作时长
   */
    public void intTimerUi(long startTime) {
        mWorkTimer.initTime(startTime, 3600 * 24);
        mWorkTimer.start();
    }
    /*
    初始化工作时长
   */
    public void intTimerService(long startTime) {
        service_time.initTime(startTime, 3600 * 24);
        service_time.start();
    }
/*
提示框
 */
    private void showdialog(String message,String TextY,String TextN) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.create();
        View diaView= LayoutInflater.from(context).inflate(R.layout.dialog_alert,null);
        dialog.setView(diaView,0,0,0,0);
        TextView hintMessage = (TextView) diaView.findViewById(R.id.tv_dia_message);
        hintMessage.setText(message);
        TextView diaYes= (TextView) diaView.findViewById(R.id.tv_dia_yes);
        TextView diaNo= (TextView) diaView.findViewById(R.id.tv_dia_no);
        diaYes.setText(TextY);
        diaNo.setText(TextN);
        diaYes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                        workingState="0";
                        bt_work_state.setText("开始接单");
                        bt_work_state.setBackgroundResource(R.drawable.work_state_btn);
                        settingMasterState(workingState);
                dialog.cancel();
            }
        });
        diaNo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.cancel();

            }
        });
        dialog.show();
    }
    /*
     设置员工状态
    */
    public void settingMasterState(String workingState1){

        RequestManager.getInstance(context).requestAsyn(Constant.MASTERSTATE, RequestManager.TYPE_POST_JSON, new HashMap<String, String>(), new ReqCallBack<String>() {
            public void onReqSuccess(String result) {

            }

            public void onReqFailed(String errorMsg) {

            }
        });
    }
    /*
    获取任务列表
     */
    public void getMaskingList(){
        RequestManager.getInstance(context).requestAsyn(Constant.GETMASKINGLIST, RequestManager.TYPE_POST_JSON, new HashMap<String, String>(), new ReqCallBack<String>() {
            public void onReqSuccess(String result) {
                if(taskingList!=null&&taskingList.size()>0){
                    taskingAdapter=new TaskingListAdapter(context,taskingList);
                }
            }

            public void onReqFailed(String errorMsg) {

            }
        });
    }
   /*
   任务列表adapter
    */
class TaskingListAdapter{
    private Context context;
    private List<HashMap<String, String>> list;
    private LayoutInflater inflater;
    public TaskingListAdapter(Context context,List<HashMap<String, String>> list){
        this.context=context;
        this.list=list;
        this.inflater = LayoutInflater.from(context);
    }
    public int getCount() {
        return list.size();
    }


    public Object getItem(int position) {

        return list.get(position);
    }


    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
       viewHolder holder = null;
        if (null == convertView) {
            holder = new viewHolder();
            convertView=inflater.inflate(R.layout.item_task_list,null);
            holder.itemCallContent= (TextView) convertView.findViewById(R.id.tv_item_call_content);
            holder.itemName= (TextView) convertView.findViewById(R.id.tv_item_name);
            holder.itemNumber= (TextView) convertView.findViewById(R.id.tv_item_number);
            holder.itemCallRegion= (TextView) convertView.findViewById(R.id.tv_item_call_region);
            holder.itemOrderTime= (TextView) convertView.findViewById(R.id.tv_item_ordertime);
            holder.itemSingle= (Button) convertView.findViewById(R.id.item_bt_single);
            convertView.setTag(holder);
        } else {
            holder = (viewHolder) convertView.getTag();
        }
        holder.itemName.setText(list.get(position).get(""));
        holder.itemNumber.setText(list.get(position).get(""));
        holder.itemCallRegion.setText(list.get(position).get(""));
        holder.itemCallContent.setText(list.get(position).get(""));
        holder.itemOrderTime.setText(list.get(position).get(""));
        holder.itemSingle.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                showdialog("请确认抢单？","确认","放弃",position);
            }
        });
        return convertView;
    }
    class viewHolder{
        private TextView itemName;
        private TextView itemNumber;
        private TextView itemCallRegion;
        private TextView itemCallContent;
        private TextView itemOrderTime;
        private Button  itemSingle;
    }
    private void showdialog(String message, String TextY, String TextN, final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.create();
        View diaView= LayoutInflater.from(context).inflate(R.layout.dialog_alert,null);
        dialog.setView(diaView,0,0,0,0);
        TextView hintMessage = (TextView) diaView.findViewById(R.id.tv_dia_message);
        hintMessage.setText(message);
        TextView diaYes= (TextView) diaView.findViewById(R.id.tv_dia_yes);
        TextView diaNo= (TextView) diaView.findViewById(R.id.tv_dia_no);
        diaYes.setText(TextY);
        diaNo.setText(TextN);
        diaYes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
             workingState="1";
             //settingMasterState(workingState);
                bt_work_state.setBackgroundResource(R.drawable.work_state_btn_tasking);
                bt_work_state.setFocusable(false);
                bt_tasking.setText("进行中任务(1)");
                ll_layout_bottom.setVisibility(View.VISIBLE);
                wrfLayout.setVisibility(View.GONE);
                tv_taking_Off.setFocusable(false);
                tv_taking_Off.setTextColor(Color.parseColor("#70ffffff"));
                HashMap<String,String> map=list.get(position);
                tv_name.setText(map.get(""));
                tv_number.setText(map.get(""));
                tv_call_region.setText(map.get(""));
                tv_call_content.setText(map.get(""));
                tv_ordertime.setText(map.get(""));
                intTimerService(0);
                dialog.cancel();
            }
        });
        diaNo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.cancel();

            }
        });
        dialog.show();
    }
    }
}

