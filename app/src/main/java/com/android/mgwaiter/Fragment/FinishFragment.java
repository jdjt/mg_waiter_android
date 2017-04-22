package com.android.mgwaiter.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import com.android.mgwaiter.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author huyanan
 * @ FileName: FinishFragment
 * @ Date 2017/4/17
 */
public class FinishFragment extends Fragment {

    ExpandableListView tasklistview;//收缩列表选项

//    List<String> parent = null;//列表头
//    Map<String, List<String>> map = null;//列表详情
//    RatingBar ratingBar;
//    String waiterId;
//    private ArrayList<HashMap<String, Object>> arraylist;//任务编号列表
//    ArrayList<HashMap<String, String>> taskArrayList = new ArrayList<HashMap<String, String>>();
////    MealExpandableListAdapter mealExpandableListAdapter;
//
//    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_fragment, container, false);



//        //获取waiterId
//        SharedPreferences sp = getActivity().getSharedPreferences("WaiterId", Context.MODE_PRIVATE);
//        waiterId = sp.getString("waiterId", "");
//
//        arraylist = new ArrayList<HashMap<String, Object>>();
//        //已结束任务 收缩列表选项
        tasklistview = (ExpandableListView) view.findViewById(R.id.task_expandablelistview);
//        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
//        getWaiterTaskStatic();
////        ratingBar.setFocusable(false);
//        tasklistview.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//            @Override
//            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
//                TextView text = (TextView) v.findViewById(R.id.task_amount);
//                String taskCode = text.getText() + "";
//                if (Handler_Network.isNetworkAvailable(getApplicationContext())) {
//                    String url = Url.GET_TASK;
//                    JsonObject json = new JsonObject();
//                    json.addProperty("taskCode", taskCode);     //编号
//                    InternetConfig config = new InternetConfig();
//                    config.setKey(26);
//                    FastHttpHander.ajaxString(url, json.toString(), config, FinishFragment.this);
//                } else {
//                    Toast.makeText(getContext(), "网络暂时不可用,请断开连接后重新尝试", Toast.LENGTH_SHORT).show();
//                }
////                Toast.makeText(getActivity(), text.getText() + "", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });
////        tasklistview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
////            @Override
////            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
////                Intent intent = new Intent(getActivity(), MealDatailActivity.class);
////                startActivity(intent);
////                System.out.print("胡雅楠fffffffffffffffffff" );
////                return false;
////            }
////        });
//        tasklistview.setGroupIndicator(null);
//        //fragment注入方法
//        Handler_Inject.injectFragment(this, view);
////        initData();
////        tasklistview.setAdapter(new MealExpandableListAdapter(this));
//        int listItemFinish =  tasklistview.getCount();
//        TextView action_bar_center_left_txt = (TextView)getActivity().findViewById(R.id.action_bar_center_left_txt);
//        action_bar_center_left_txt.setText("已完成"+listItemFinish);
//
        return view;
    }






//
//    /**
//     * 初始化任务列表数据
//     */
//    private void initDataList(List<HashMap<String, String>> list) {
//        for (int i = 0; i < list.size(); i++) {
//            parent = new ArrayList<String>();
//            parent.add(list.get(i).get("messageInfo")+ list.get(i).get("taskCode"));
//            HashMap<String, Object> map = new HashMap<>();
//            map.put("messageInfo",list.get(i).get("messageInfo"));
//            map.put("taskCode",list.get(i).get("taskCode"));
//            arraylist.add(map);
//        }
//    }
//
//    //
//
//    // 根据任务号查询时间(已完成)
////    private void getWaiterTask() {
////        if (Handler_Network.isNetworkAvailable(getApplicationContext())) {
////            String url = Url.GET_TASK;
////            JsonObject json = new JsonObject();
////            json.addProperty("taskCode", taskCode);     //编号
////            InternetConfig config = new InternetConfig();
////            config.setKey(26);
////            FastHttpHander.ajaxString(url, json.toString(), config, this);
////        } else {
////            Toast.makeText(getContext(), "网络暂时不可用,请断开连接后重新尝试", Toast.LENGTH_SHORT).show();
////        }
////    }
//
//
//    // 服务员查询历史任务统计信息(已完成)
//    private void getWaiterTaskStatic() {
//        if (Handler_Network.isNetworkAvailable(getApplicationContext())) {
//            String url = Url.GET_TASK_ARRAY;
//            JsonObject json = new JsonObject();
//            json.addProperty("waiterId", waiterId);     //编号
//            json.addProperty("status", "1");  //状态
//            json.addProperty("startDate", "2016-10-1 "+"00:00:00");  //开始时间
//            json.addProperty("endDate", "2019-10-14 "+ "23:59:59");  //结束时间
//            json.addProperty("pageNo", "1");  //页码
//            json.addProperty("pageCount", "10");  //每页显示数量
//            InternetConfig config = new InternetConfig();
//            config.setKey(25);
//            FastHttpHander.ajaxString(url, json.toString(), config, this);
//        } else {
//            Toast.makeText(getContext(), "网络暂时不可用,请断开连接后重新尝试", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    //服务员查询历史任务统计信息
//    @InjectHttpOk({25, 26})
//    private void resultOk(ResponseEntity r) {
//        switch (r.getKey()) {
//            //服务员查询历史任务统计信息
//            case 25:
//                Long count = Long.valueOf(Handler_Json.getValue("count", r.getContentAsString()).toString());
//                Log.d("TAGTAGTAG", "任务数量：" + count);
//                if (count > 0) {
//                    List<HashMap<String, String>> list = Handler_Json.jsonToList("list", r.getContentAsString());
//                    mealExpandableListAdapter = new MealExpandableListAdapter(getActivity(),list);
//                    tasklistview.setAdapter(mealExpandableListAdapter);
////                    initDataList(list);
//                    int listItemFinish =  tasklistview.getCount();
//                    TextView action_bar_center_left_txt = (TextView)getActivity().findViewById(R.id.action_bar_center_left_txt);
//                    action_bar_center_left_txt.setText("已完成"+ "("+ listItemFinish+")");
//                    Log.d("TAGTAGTAG", "任务类型messageInfo为：" + list.get(0).get("messageInfo") + "  任务编号=" + list.get(0).get("taskCode"));
//                } else {
//
//                }
//                break;
//            //根据任务号查询时间
//            case 26:
//                Log.d("TAGTAGTAG","任务进度信息："+Handler_Json.jsonToListOrMap("progressInfo", r.getContentAsString()));
//                HashMap map1 = (HashMap) Handler_Json.jsonToListOrMap("progressInfo", r.getContentAsString());
//                HashMap map = (HashMap) Handler_Json.jsonToListOrMap("taskInfo", r.getContentAsString());
//                map1.put("taskCode",""+map.get("taskCode"));
//                Log.d("TAGTAGTAG","taskcode信息："+ map1.toString());
//                mealExpandableListAdapter.setChildData(map1);
//                Log.d("TAGTAGTAG","任务进度信息："+Handler_Json.jsonToListOrMap("progressInfo", r.getContentAsString()));
//                break;
//        }
//    }
//
//    @InjectHttpErr({25, 26})
//    private void resultErr(ResponseEntity r) {
//        Toast.makeText(getContext(), "请求失败，请稍候再试", Toast.LENGTH_SHORT).show();
//    }
//
//    public void setRating (float rating){
//
//    }
}
