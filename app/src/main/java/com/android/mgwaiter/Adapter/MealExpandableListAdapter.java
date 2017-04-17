//package com.android.mgwaiter.Adapter;
//
//import android.content.Context;
//import android.content.Intent;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseExpandableListAdapter;
//import android.widget.LinearLayout;
//import android.widget.RatingBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import mgwaiter.jdjt.com.R;
//import mgwaiter.jdjt.com.activity.MealDatailActivity;
//
///**
// * Created by huyanan on 2017/4/17.
// *
// */
//public class MealExpandableListAdapter extends BaseExpandableListAdapter {
//    List<HashMap<String, String>> list;
//    //    Map map,map1;
//    private Context context = null;
//    private String drOrderNo = "";
//
//    public MealExpandableListAdapter(Context context, List<HashMap<String, String>> list) {
//        this.context = context;
//        this.list = list;
//
//    }
//
//    public void setChildData(Map map) {
//        Log.d("TAGTAGTAG", "adapter 数据" + map.toString());
//        Log.d("TAGTAGTAG", "list 数据" + list.toString());
//        for (int i = 0; i < list.size(); i++) {
//            if (map.get("taskCode").equals(list.get(i).get("taskCode"))) {
//                list.get(i).put("finishTime", "" + map.get("finishTime"));
//                list.get(i).put("createTime", "" + map.get("createTime"));
//                list.get(i).put("workNum", "" + map.get("workNum"));
//                list.get(i).put("acceptTime", "" + map.get("acceptTime"));
//            }
//        }
//        notifyDataSetChanged();
//    }
//
//    //得到子item需要关联的数据
//    @Override
//    public Object getChild(int groupPosition, int childPosition) {
////        String key = context.parent.get(groupPosition);
//        return childPosition;
////        return (context.map.get(key).get(childPosition));
//    }
//
//    //得到子item的ID
//    @Override
//    public long getChildId(int groupPosition, int childPosition) {
//        return childPosition;
//    }
//
//    //点击事件发生后:先执行事件监听,然后调用此getChildView()
//    @Override
//    public View getChildView(int groupPosition, int childPosition,
//                             boolean isLastChild, View convertView, ViewGroup parent) {
////        String key = context.parent.get(groupPosition);
////        String info = context.map.get(key).get(childPosition);
//        if (convertView == null) {
//            LayoutInflater inflater = (LayoutInflater) context
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = inflater.inflate(R.layout.expandable_item, null);
//        }
//        //下单时间
//        TextView place_order_time = (TextView) convertView
//                .findViewById(R.id.place_order_time);
//        //接单时间
//        TextView order_receiving_time = (TextView) convertView
//                .findViewById(R.id.order_receiving_time);
//        //完成时间
//        TextView finish_times = (TextView) convertView
//                .findViewById(R.id.finish_times);
//        //服务内容
//        TextView service_item = (TextView) convertView
//                .findViewById(R.id.service_item);
//        TextView particulars = (TextView)convertView.findViewById(R.id.particulars);
//        //完成时间item
//        LinearLayout finish_item = (LinearLayout)convertView.findViewById(R.id.finish_item);
//        //要求完成时间item
//        LinearLayout completion_time_item = (LinearLayout)convertView.findViewById(R.id.completion_time_item);
//        //要求完成时间
//        TextView completion_time = (TextView)convertView.findViewById(R.id.completion_time);
//
//        place_order_time.setText("" + list.get(groupPosition).get("createTime"));
//        order_receiving_time.setText("" + list.get(groupPosition).get("acceptTime"));
//        System.out.print("胡雅楠"+ list.get(groupPosition).get("timeLimit"));
//        drOrderNo = list.get(groupPosition).get("drOrderNo");
//        if (list.get(groupPosition).get("finishTime") + "" != "") {
//            finish_times.setText("" + list.get(groupPosition).get("finishTime"));
//            finish_item.setVisibility(View.VISIBLE);
//        } else {
//            finish_item.setVisibility(View.GONE);
//        }
//        if ("0".equals(list.get(groupPosition).get("category")+"")) {
//            service_item.setText("到场服务");
//            particulars.setText("进入聊天记录");
//            completion_time_item.setVisibility(View.GONE);
////            particulars.setVisibility(View.GONE);
//        } else if ("4".equals(list.get(groupPosition).get("category")+"")) {
////            LinearLayout finish_item = (LinearLayout)convertView.findViewById(R.id.finish_item);
////            finish_item.setVisibility(View.INVISIBLE);
//            service_item.setText("送餐服务");
//            particulars.setText("查看菜单列表");
//            completion_time.setText("" + list.get(groupPosition).get("timeLimit"));
//
//        }
//        particulars.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent();
//                intent.setClass(context, MealDatailActivity.class);
//                intent.putExtra("drOrderNo",drOrderNo);
//                context.startActivity(intent);
//            }
//        });
//        return convertView;
//    }
//
//    //获取当前父item下的子item的个数
//    @Override
//    public int getChildrenCount(int groupPosition) {
////        String key = context.get(groupPosition);
////        int size = context.map.get(key).size();
//        return 1;
//    }
//
//    //获取当前父item的数据
//    @Override
//    public Object getGroup(int groupPosition) {
//        return list.get(groupPosition);
//    }
//
//
//    @Override
//    public int getGroupCount() {
//        return list.size();
//    }
//
//    @Override
//    public long getGroupId(int groupPosition) {
//        return groupPosition;
//    }
//
//    //设置父item组件
//    @Override
//    public View getGroupView(int groupPosition, boolean isExpanded,
//                             View convertView, ViewGroup parent) {
//        if (convertView == null) {
//            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = inflater.inflate(R.layout.task_title_layout, null);
//        }
//        //任务类型
//        TextView taskTypeTv = (TextView) convertView.findViewById(R.id.task_type);
//        //任务编号
//        TextView task_amount = (TextView) convertView.findViewById(R.id.task_amount);
//        //评分
//        RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
//
//        taskTypeTv.setText(list.get(groupPosition).get("messageInfo"));
//        task_amount.setText(list.get(groupPosition).get("taskCode"));
//        ratingBar.setRating(Float.parseFloat(list.get(groupPosition).get("score") + ""));
//
//        return convertView;
//    }
//
//    @Override
//    public boolean hasStableIds() {
//        return true;
//    }
//
//    @Override
//    public boolean isChildSelectable(int groupPosition, int childPosition) {
//        return true;
//    }
//
//}
