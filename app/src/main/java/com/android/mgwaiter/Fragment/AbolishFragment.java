package com.android.mgwaiter.Fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.mgwaiter.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author huyanan
 * @ FileName: AbolishFragment
 * @ Date 2017/4/17
 */

public class AbolishFragment extends Fragment {

//    ExpandableListView tasklistview;//收缩列表选项
//    List<String> parent = null;//列表头
//    Map<String, List<String>> map = null;//列表详情
//    String waiterId;
//    MealExpandableListAdapter mealExpandableListAdapter;
//    String taskCode = "";
//
//    private List list1;
//
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_fragment, container, false);
//        //fragment注入方法
//        Handler_Inject.injectFragment(this, view);
//        //获取waiterid
//        SharedPreferences sp = getActivity().getSharedPreferences("WaiterId", Context.MODE_PRIVATE);
//        waiterId = sp.getString("waiterId", "");
//        //已结束任务收缩列表选项
//        tasklistview = (ExpandableListView) view.findViewById(R.id.task_expandablelistview);
//        tasklistview.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//            @Override
//            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
//                TextView text = (TextView) v.findViewById(R.id.task_amount);
//                taskCode = text.getText() + "";
//                if (Handler_Network.isNetworkAvailable(getApplicationContext())) {
//                    String url = Url.GET_TASK;
//                    JsonObject json = new JsonObject();
//                    json.addProperty("taskCode", taskCode);     //编号
//                    InternetConfig config = new InternetConfig();
//                    config.setKey(27);
//                    FastHttpHander.ajaxString(url, json.toString(), config, AbolishFragment.this);
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
//////                TextView text = (TextView) v.findViewById(R.id.task_amount);
//////                taskCode = text.getText() + "";
//////                System.out.print("胡雅楠" + taskCode);
////                return false;
////            }
////        });
////        //点击进入菜单按钮
////        TextView particulars = (TextView)view.findViewById(R.id.particulars);
////        particulars.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////
////            }
////        });
//        tasklistview.setGroupIndicator(null);
//        getWaiterTaskStatic();
//
////        getWaiterTask();
//
        return view;
    }
//
////    public void isGroupExpanded(int groupPosition) {
////        for (int i = 0; i < mealExpandableListAdapter.getGroupCount(); i++) {
////            if (groupPosition != i && isGroupExpanded(groupPosition)) {
////                collapseGroup(i);
////            }
////        }
////    }
//
//    //刷新listview的时候用
//    @Override
//    public void onHiddenChanged(boolean hidden) {
//        super.onHiddenChanged(hidden);
//    }
//
//
//    // 根据任务号查询时间(已取消)
//    private void getWaiterTask() {
//
//    }
//
//    // 服务员查询历史任务统计信息(已取消)
//    private void getWaiterTaskStatic() {
//        if (Handler_Network.isNetworkAvailable(getApplicationContext())) {
//            String url = Url.GET_TASK_ARRAY;
//            JsonObject json = new JsonObject();
//            json.addProperty("waiterId", waiterId);     //编号
//            json.addProperty("status", "9");  //状态
//            json.addProperty("startDate", "2016-10-1 "+"00:00:00");  //开始时间
//            json.addProperty("endDate", "2019-10-14 "+ "23:59:59");  //结束时间
//            json.addProperty("pageNo", "1");  //页码
//            json.addProperty("pageCount", "10");  //每页显示数量
//            InternetConfig config = new InternetConfig();
//            config.setKey(24);
//            FastHttpHander.ajaxString(url, json.toString(), config, this);
//        } else {
//            Toast.makeText(getContext(), "网络暂时不可用,请断开连接后重新尝试", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    //服务员查询历史任务统计信息
//    @InjectHttpOk({24, 27})
//    private void resultOk(ResponseEntity r) {
//        switch (r.getKey()) {
//            case 24:
//                Long count = Long.valueOf(Handler_Json.getValue("count", r.getContentAsString()).toString());
//                if (count > 0) {
//                    List<HashMap<String, String>> list = Handler_Json.jsonToList("list", r.getContentAsString());
//                    mealExpandableListAdapter = new MealExpandableListAdapter(getActivity(), list);
//                    tasklistview.setAdapter(mealExpandableListAdapter);
////                    initDataList(list);
//                    int listItemAbolish =  tasklistview.getCount();
//                    TextView action_bar_center_right_txt = (TextView)getActivity().findViewById(R.id.action_bar_center_right_txt);
//                    action_bar_center_right_txt.setText("已取消"+ "("+ listItemAbolish+")");
//                    Log.d("TAGTAGTAG", "任务类型messageInfo为hyn：" + list.get(0).get("messageInfo") + "  任务编号=" + list.get(0).get("taskCode"));
//                } else {
//                }
//                break;
//            case 27:
//                HashMap map1 = (HashMap) Handler_Json.jsonToListOrMap("progressInfo", r.getContentAsString());
//                HashMap map = (HashMap) Handler_Json.jsonToListOrMap("taskInfo", r.getContentAsString());
//                map1.put("taskCode",""+map.get("taskCode"));
//                mealExpandableListAdapter.setChildData(map1);
//                Log.d("TAGTAGTAG","任务进度信息："+Handler_Json.jsonToListOrMap("progressInfo", r.getContentAsString()));
////                mealExpandableListAdapter.setChildData((Map) Handler_Json.jsonToListOrMap("progressInfo", r.getContentAsString()));
//                break;
//        }
//    }
//
//    //    private void setList(){
////        for( Map map:list1){
////            if(map.get("taskCode").equals(Map)){
////
////            }
////        }
////        mealExpandableListAdapter.notifyDataSetChanged();
////    }
//    @InjectHttpErr({24, 27})
//    private void resultErr(ResponseEntity r) {
//        Toast.makeText(getContext(), "请求失败，请稍候再试", Toast.LENGTH_SHORT).show();
//    }
}
