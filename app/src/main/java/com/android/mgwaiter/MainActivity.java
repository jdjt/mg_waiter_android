package com.android.mgwaiter;

import android.app.Activity;
import android.content.Context;

import android.graphics.Color;


import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.mgwaiter.application.MgApplication;
import com.android.mgwaiter.bean.masterMessage;
import com.android.mgwaiter.bean.settingState;
import com.android.mgwaiter.bean.taskMessage;
import com.android.mgwaiter.net.Constant;
import com.android.mgwaiter.net.ReqCallBack;
import com.android.mgwaiter.net.RequestManager;
import com.android.mgwaiter.net.ResponseEntity;
import com.android.mgwaiter.view.Tasktimer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.umeng.message.provider.a.a.i;


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
    private int workingState = 0;   //服务员工作状态
    private int s;
    private int m;
    private Handler mHandler;
    private List<HashMap<String, String>> taskingList;
    private TaskingListAdapter taskingAdapter;
    private MgApplication app;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        int i  = dm.widthPixels;
//        int heightPixels = dm.heightPixels;
        setContentView(R.layout.activity_main);
        context = this;

        taskingList = new ArrayList<>();
        app = MgApplication.getInstance();
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
        service_time = (Tasktimer) findViewById(R.id.tv_service_time);
        iv_record = (ImageView) findViewById(R.id.iv_record);
        iv_message = (ImageView) findViewById(R.id.iv_message);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_number = (TextView) findViewById(R.id.tv_number);
        ll_layout_bottom = (LinearLayout) findViewById(R.id.ll_layout_bottom);
        tv_call_region = (TextView) findViewById(R.id.tv_call_region);
        tv_call_content = (TextView) findViewById(R.id.tv_call_content);
        tv_ordertime = (TextView) findViewById(R.id.tv_ordertime);
        bt_end = (Button) findViewById(R.id.bt_end);
        mWorkTimer = (Tasktimer) findViewById(R.id.tv_worktimestart);
        tv_taking_Off.setOnClickListener(this);
        tv_count.setOnClickListener(this);
        bt_work_state.setOnClickListener(this);
        bt_end.setOnClickListener(this);
        mHandler = new Handler();
        wrfLayout.setColorSchemeResources(R.color.blue_70, R.color.material_deep_teal_50,
                R.color.blue_btn_bg_color, R.color.error_stroke_color);
        wrfLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                getMaskingList();
            }
        });
        intTimerUi(0);
    }


    protected void onStart() {
        super.onStart();
        /*
        获取员工信息
         */
        final HashMap<String, String> map = new HashMap<>();
        map.put("waiterId", "");
        RequestManager.getInstance(context).requestAsyn(Constant.GETMASTERMESSAGE, RequestManager.TYPE_POST_JSON, new HashMap<String, String>(), new ReqCallBack<ResponseEntity>() {
            public void onReqSuccess(ResponseEntity result) {
                result.getHeaders();
                masterMessage master = new Gson().fromJson(result.getContentAsString(), new TypeToken<masterMessage>() {
                }.getType());
                String time = master.getWorkTimeCal();
                if (time != null && "".equals(time)) {
                    intTimerUi(timerStart(time));
                    tv_master_number.setText(master.getEmpNo());
                    tv_master_name.setText(master.getName());
                    tv_master_division.setText(master.getDepName());
                    workingState = master.getWorkStatus();
                    switch (workingState){
                        case 0:
                            break;
                        case 1:
                            break;
                        case 2:

                            break;
                    }
                }

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

                switch (workingState) {
                    case 0:
                        HashMap<String, String> map = new HashMap<>();
                        map.put("name", "王二");
                        map.put("number", "1000687");
                        map.put("call", "郁林酒店");
                        map.put("message", "您好，请来我这打扫一下房间");
                        map.put("time", "2017-3-6");
                        taskingList.add(map);
                        workingState = 2;
                        bt_work_state.setText("停止抢单");
                        bt_work_state.setBackgroundResource(R.drawable.work_state_end_btn);
//                         settingMasterState(workingState);
//                        getMaskingList();
                        taskingAdapter = new TaskingListAdapter(context, taskingList);
                        task_list.setAdapter(taskingAdapter);
                        break;
                    case 1:
                        break;
                    case 2:
                        showdialog("请确认停止接单", "确认", "取消");

                        break;
                }
                break;
            //完成
            case R.id.bt_end:

                completeDialog("请确认已完成当前任务","确认","取消");
                break;
            //统计
            case R.id.tv_count:
                Intent intent = new Intent(context, TaskStatisticsActivity.class);
                startActivity(intent);
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
    private void showdialog(String message, String TextY, String TextN) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.create();
        View diaView = LayoutInflater.from(context).inflate(R.layout.dialog_alert, null);
        dialog.setView(diaView, 0, 0, 0, 0);
        TextView hintMessage = (TextView) diaView.findViewById(R.id.tv_dia_message);
        hintMessage.setText(message);
        TextView diaYes = (TextView) diaView.findViewById(R.id.tv_dia_yes);
        TextView diaNo = (TextView) diaView.findViewById(R.id.tv_dia_no);
        diaYes.setText(TextY);
        diaNo.setText(TextN);
        diaYes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                workingState = 0;
                bt_work_state.setText("开始接单");
                bt_work_state.setBackgroundResource(R.drawable.work_state_btn);
                settingMasterState(workingState);
                taskingList.clear();
                taskingAdapter.notifyDataSetChanged();
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
    public void settingMasterState(int workingState1) {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("workStatus", workingState);
        RequestManager.getInstance(context).requestAsyn(Constant.MASTERSTATE, RequestManager.TYPE_POST_JSON, new HashMap<String, String>(), new ReqCallBack<ResponseEntity>() {
            public void onReqSuccess(ResponseEntity result) {
                settingState state = new Gson().fromJson(result.getContentAsString(), new TypeToken<settingState>() {
                }.getType());
                int ok = state.getRetOk();
                if (1 == ok) {
                    settingMasterState(workingState);
                }
            }

            public void onReqFailed(String errorMsg) {

            }
        });
    }

    /*
    获取任务列表
     */
    public void getMaskingList() {
        RequestManager.getInstance(context).requestAsyn(Constant.GETMASKINGLIST, RequestManager.TYPE_POST_JSON, new HashMap<String, String>(), new ReqCallBack<String>() {
            public void onReqSuccess(String result) {
                //new Gson().fromJson(arg0.result, new TypeToken<>(){}.getType());

                if (taskingList != null && taskingList.size() > 0) {
                    taskingAdapter = new TaskingListAdapter(context, taskingList);
                }
            }

            public void onReqFailed(String errorMsg) {

            }
        });
    }

    /*
    任务列表adapter
     */
    class TaskingListAdapter extends BaseAdapter {
        private Context context;
        private List<HashMap<String, String>> list;
        private LayoutInflater inflater;

        public TaskingListAdapter(Context context, List<HashMap<String, String>> list) {
            this.context = context;
            this.list = list;
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
                convertView = inflater.inflate(R.layout.item_task_list, null);
                holder.itemCallContent = (TextView) convertView.findViewById(R.id.tv_item_call_content);
                holder.itemName = (TextView) convertView.findViewById(R.id.tv_item_name);
                holder.itemNumber = (TextView) convertView.findViewById(R.id.tv_item_number);
                holder.itemCallRegion = (TextView) convertView.findViewById(R.id.tv_item_call_region);
                holder.itemOrderTime = (TextView) convertView.findViewById(R.id.tv_item_ordertime);
                holder.itemSingle = (Button) convertView.findViewById(R.id.item_bt_single);
                convertView.setTag(holder);
            } else {
                holder = (viewHolder) convertView.getTag();
            }
            holder.itemName.setText(list.get(position).get("name"));
            holder.itemNumber.setText(list.get(position).get("number"));
            holder.itemCallRegion.setText(list.get(position).get("call"));
            holder.itemCallContent.setText(list.get(position).get("message"));
            holder.itemOrderTime.setText(list.get(position).get("time"));
            holder.itemSingle.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    showdialog("请确认抢单？", "确认", "放弃", position);
                }
            });
            return convertView;
        }

        class viewHolder {
            private TextView itemName;
            private TextView itemNumber;
            private TextView itemCallRegion;
            private TextView itemCallContent;
            private TextView itemOrderTime;
            private Button itemSingle;
        }

        private void showdialog(String message, String TextY, String TextN, final int position) {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            final AlertDialog dialog = builder.create();
            View diaView = LayoutInflater.from(context).inflate(R.layout.dialog_alert, null);
            dialog.setView(diaView, 0, 0, 0, 0);
            TextView hintMessage = (TextView) diaView.findViewById(R.id.tv_dia_message);
            hintMessage.setText(message);
            TextView diaYes = (TextView) diaView.findViewById(R.id.tv_dia_yes);
            TextView diaNo = (TextView) diaView.findViewById(R.id.tv_dia_no);
            diaYes.setText(TextY);
            diaNo.setText(TextN);
            diaYes.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // grabSingle();
                    workingState = 1;
                    //settingMasterState(workingState);
                    bt_work_state.setBackgroundResource(R.drawable.work_state_btn_tasking);
                    bt_work_state.setFocusable(false);
                    bt_tasking.setText("进行中任务(1)");
                    bt_tasking.setTextColor(Color.parseColor("#ffffff"));
                    ll_layout_bottom.setVisibility(View.VISIBLE);
                    wrfLayout.setVisibility(View.GONE);
                    tv_taking_Off.setFocusable(false);
                    tv_taking_Off.setTextColor(Color.parseColor("#70ffffff"));
                    HashMap<String, String> map = list.get(position);
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

    /**
     * @method 获取上班起始时间
     */
    private long timerStart(String Currenttime) {
        long startTime = 0;
        long h1 = 0, m1 = 0, s1 = 0;
        //2016-05-04 14:52:32.0
        //00:00:00
        h1 = Long.parseLong(Currenttime.substring(1, 3));
        m1 = Long.parseLong(Currenttime.substring(4, 6));
        s1 = Long.parseLong(Currenttime.substring(7, 9));
        startTime = (h1 * 3600 + m1 * 60 + s1);
        return startTime;
    }


    protected void onPause() {
        super.onPause();

    }

    public void Toast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /*
    抢单
     */
    public void grabSingle(final int position) {
        RequestManager.getInstance(context).requestAsyn(Constant.GRABSINGLE, RequestManager.TYPE_POST_JSON, new HashMap<String, String>(), new ReqCallBack<ResponseEntity>() {
            public void onReqSuccess(ResponseEntity result) {
                result.getHeaders();
                settingState state = new Gson().fromJson(result.getContentAsString(), new TypeToken<settingState>() {
                }.getType());
                int ok = state.getRetOk();
                if (0 == ok) {
                    workingState = 1;
                    settingMasterState(workingState);
                    bt_work_state.setBackgroundResource(R.drawable.work_state_btn_tasking);
                    bt_work_state.setFocusable(false);
                    bt_tasking.setText("进行中任务(1)");
                    bt_tasking.setTextColor(Color.parseColor("#ffffff"));
                    ll_layout_bottom.setVisibility(View.VISIBLE);
                    wrfLayout.setVisibility(View.GONE);
                    tv_taking_Off.setFocusable(false);
                    tv_taking_Off.setTextColor(Color.parseColor("#70ffffff"));
                    HashMap<String, String> map = taskingList.get(position);
                    tv_name.setText(map.get(""));
                    tv_number.setText(map.get(""));
                    tv_call_region.setText(map.get(""));
                    tv_call_content.setText(map.get(""));
                    tv_ordertime.setText(map.get(""));
                    intTimerService(0);
                }

            }

            public void onReqFailed(String errorMsg) {

            }
        });
    }

    Handler handler = new Handler(new Handler.Callback() {

        public boolean handleMessage(Message msg) {
            if (msg.what == 10) {
                int minute = msg.arg1;
                int second = msg.arg2;
                if (minute >= 10 && second >= 10) {
                    bt_tasking.setText("完成待确认" + "(" + minute + ":" + second + ")");
                } else if (minute >= 10 && second < 10) {
                    bt_tasking.setText("完成待确认" + "(" + minute + ":" + "0" + second + ")");
                } else if (minute < 10 && second >= 10) {
                    bt_tasking.setText("完成待确认" + "(" + "0" + minute + ":" + second + ")");
                } else if (minute < 10 && second < 10) {
                    bt_tasking.setText("完成待确认" + "(" + "0" + minute + ":" + "0" + second + ")");
                }
            }
            return true;
        }
    });

    /*
    下班
     */
    public void WaiterRequestAfterWork() {
        RequestManager.getInstance(context).requestAsyn(Constant.GETMASKINGLIST, RequestManager.TYPE_POST_JSON, new HashMap<String, String>(), new ReqCallBack<ResponseEntity>() {
            public void onReqSuccess(ResponseEntity result) {
                settingState state = new Gson().fromJson(result.getContentAsString(), new TypeToken<settingState>() {
                }.getType());
                int ok = state.getRetOk();
                if ("0".equals(ok)) {
                    mWorkTimer.stop();
                    service_time.stop();

                }

            }

            public void onReqFailed(String errorMsg) {

            }
        });
    }
    /*
    根据编号获取任务信息
     */
    public void getTaskMessage() {
        RequestManager.getInstance(context).requestAsyn(Constant.GETTASKMESSAGE, RequestManager.TYPE_POST_JSON, new HashMap<String, String>(), new ReqCallBack<ResponseEntity>() {
            public void onReqSuccess(ResponseEntity result) {
                taskMessage message = new Gson().fromJson(result.getContentAsString(), new TypeToken<taskMessage>() {
                }.getType());
                Date Currenttime=message.getAcceptTime();
                Date Limittime=message.getNowDate();
                String startTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Currenttime);
                String systemTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Limittime);
               long time= timerStart(startTime,systemTime);
                intTimerService(time);


            }

            public void onReqFailed(String errorMsg) {

            }
        });
    }
    /**
     * @method  获取服务起始时间
     */
    private long timerStart(String Currenttime, String Limittime) {
        long startTime = 0;
        long h1 = 0, m1 = 0, s1 = 0;
        long h2 = 0, m2 = 0, s2 = 0;
        //2016-05-04 14:52:32.0
        h1 = Long.parseLong(Currenttime.substring(11, 13));
        m1 = Long.parseLong(Currenttime.substring(14, 16));
        s1 = Long.parseLong(Currenttime.substring(17, 19));
        h2 = Long.parseLong(Limittime.substring(11, 13));
        m2 = Long.parseLong(Limittime.substring(14, 16));
        s2 = Long.parseLong(Limittime.substring(17, 19));
        startTime = (h1 * 3600 + m1 * 60 + s1) - (h2 * 3600 + m2 * 60 + s2);
        return startTime;
    }
    /*
    完成操作
     */
    public void complete() {
        RequestManager.getInstance(context).requestAsyn(Constant.COMPLETE, RequestManager.TYPE_POST_JSON, new HashMap<String, String>(), new ReqCallBack<ResponseEntity>() {
            public void onReqSuccess(ResponseEntity result) {
                settingState  state = new Gson().fromJson(result.getContentAsString(), new TypeToken<settingState>() {
                }.getType());
               int ok=state.getRetOk();
                if("0".equals(ok)){

                }


            }

            public void onReqFailed(String errorMsg) {

            }
        });
    }
    /*
   完成提示框
    */
    private void completeDialog(String message, String TextY, String TextN) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.create();
        View diaView = LayoutInflater.from(context).inflate(R.layout.dialog_alert, null);
        dialog.setView(diaView, 0, 0, 0, 0);
        TextView hintMessage = (TextView) diaView.findViewById(R.id.tv_dia_message);
        hintMessage.setText(message);
        TextView diaYes = (TextView) diaView.findViewById(R.id.tv_dia_yes);
        TextView diaNo = (TextView) diaView.findViewById(R.id.tv_dia_no);
        diaYes.setText(TextY);
        diaNo.setText(TextN);
        diaYes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bt_end.setFocusable(false);
                bt_end.setBackgroundResource(R.drawable.work_state_btn_tasking);
                bt_tasking.setTextColor(Color.parseColor("#ffffff"));
                service_time.stop();
                m = 29;
                s = 60;
                new Thread(new Runnable() {

                    public void run() {
                        while (s > 0) {
                            s--;
                            try {
                                Thread.sleep(1000);
                                Message msg = Message.obtain();
                                msg.arg1 = m;
                                msg.arg2 = s;
                                msg.what = 10;
                                if (s == 1 && m > 0) {
                                    s = 60;
                                    m--;
                                }
                                handler.sendMessage(msg);
                            } catch (InterruptedException e) {

                                e.printStackTrace();
                            }
                        }
                    }
                }).start();

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

