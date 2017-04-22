package com.fengmap.drpeng.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengmap.android.wrapmv.Tools;
import com.fengmap.android.wrapmv.db.FMDBSearchElement;
import com.fengmap.android.wrapmv.db.FMDatabaseDefine;
import com.fengmap.drpeng.FMAPI;
import com.fengmap.drpeng.IndoorMapActivity;
import com.fengmap.drpeng.OutdoorMapActivity;
import com.android.mgwaiter.R;
import com.fengmap.drpeng.SearchActivity;
import com.fengmap.drpeng.SearchFragment;
import com.fengmap.drpeng.common.ViewHolder;
import com.fengmap.drpeng.widget.DrawableCenterTextView;

import java.util.List;

import static com.fengmap.drpeng.FMAPI.TARGET_ADD_MARKER;
import static com.fengmap.drpeng.FMAPI.TARGET_CALCULATE_ROUTE;


/**
 * 历史记录适配器。
 * Created by yangbin on 16/8/24.
 */

public class HistoryTextAdapter extends CommonAdapter<FMDBSearchElement> {


    public HistoryTextAdapter(Context context, List<FMDBSearchElement> data, int layoutID) {
        super(context, data, layoutID);
    }


    @Override
    void deal(ViewHolder holder, final FMDBSearchElement e) {
        ImageView              leftView    = holder.getView(R.id.fm_search_item_left_marker);
        DrawableCenterTextView rightView   = holder.getView(R.id.fm_search_item_right_marker);
        TextView               nameView    = holder.getView(R.id.fm_search_item_name);
        TextView               typeView    = holder.getView(R.id.fm_search_item_type);
        TextView               addressView = holder.getView(R.id.fm_search_item_address);
        RelativeLayout         rlView      = holder.getView(R.id.fm_search_item_rl);


        if (null==e.getHistoryname() || "".equals(e.getHistoryname())) {  // 历史记录
            leftView.setImageResource(R.mipmap.fm_search_pos);

            nameView.setText(e.getName());

            rightView.setVisibility(View.VISIBLE);
            typeView.setVisibility(View.VISIBLE);
            addressView.setVisibility(View.VISIBLE);

            typeView.setText(e.getTypename());
            addressView.setText("・"+e.getAddress());

            // 监听  历史记录
            rightView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {   // 跳地图界面 路径规划
                    Bundle b = new Bundle();
                    b.putString(FMAPI.ACTIVITY_TARGET, TARGET_CALCULATE_ROUTE);
                    gotoMap(b, e);
                }
            });

            rlView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {    // 跳地图界面  添加标注
                    Bundle b = new Bundle();
                    b.putString(FMAPI.ACTIVITY_TARGET, TARGET_ADD_MARKER);
                    gotoMap(b, e);
                }
            });

        } else {   // 搜索记录
            leftView.setImageResource(R.mipmap.fm_search);

            nameView.setText(e.getHistoryname());

            rightView.setVisibility(View.GONE);
            typeView.setVisibility(View.GONE);
            addressView.setVisibility(View.GONE);

            rlView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {  // 跳搜索结果界面
                    ((SearchActivity)context).mEditView.setText(e.getHistoryname());
                    SearchActivity.fixedCursor(((SearchActivity)context).mEditView);

                    String name = e.getHistoryname();
                    if (FMDatabaseDefine.TYPENAME_ATM.equals(name) ||
                        FMDatabaseDefine.TYPENAME_ELEVATOR.equals(name) ||
                        FMDatabaseDefine.TYPENAME_FOOD.equals(name) ||
                        FMDatabaseDefine.TYPENAME_HOTEL.equals(name) ||
                        FMDatabaseDefine.TYPENAME_RELAX.equals(name) ||
                        FMDatabaseDefine.TYPENAME_SERVER.equals(name) ||
                        FMDatabaseDefine.TYPENAME_SHOP.equals(name) ||
                        FMDatabaseDefine.TYPENAME_WC.equals(name)) {
                        ((SearchActivity) context).getSearchResultFragment().searchByTypename(name);
                    } else {
                        ((SearchActivity) context).getSearchResultFragment().searchByKeywords(name);
                    }
                    ((SearchActivity)context).showSearchResultFragment();
                }
            });
        }


    }


    private void gotoMap(Bundle b, FMDBSearchElement e) {
        String mapId = e.getMapId();
        boolean isInside = Tools.isInsideMap(mapId);

        String className = SearchFragment.class.getName();
        b.putSerializable(className, e);
        b.putString(FMAPI.ACTIVITY_WHERE, className);

        if (isInside) {
            FMAPI.instance().gotoActivity((SearchActivity) context,
                                          IndoorMapActivity.class,
                                          b);
        } else {
            FMAPI.instance().gotoActivity((SearchActivity) context,
                                          OutdoorMapActivity.class,
                                          b);
        }
    }
}
