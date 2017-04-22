package com.fengmap.drpeng.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.android.mgwaiter.R;
import com.fengmap.android.wrapmv.db.FMDBMapElement;
import com.fengmap.drpeng.common.ViewHolder;
import com.fengmap.drpeng.widget.DrawableCenterTextView;

import java.util.List;

/**
 * Created by yangbin on 16/8/24.
 */

public class AutoTextAdapter extends CommonAdapter<FMDBMapElement> {

    public AutoTextAdapter(Context context, List<FMDBMapElement> data, int layoutID) {
        super(context, data, layoutID);
    }


    @Override
    void deal(ViewHolder holder, FMDBMapElement e) {
        //ImageView              leftView    = holder.getView(R.id.fm_search_item_left_marker);
        DrawableCenterTextView rightView   = holder.getView(R.id.fm_search_item_right_marker);
        TextView               nameView    = holder.getView(R.id.fm_search_item_name);
        TextView               typeView    = holder.getView(R.id.fm_search_item_type);
        TextView               addressView = holder.getView(R.id.fm_search_item_address);

        rightView.setVisibility(View.GONE);

        nameView.setText(e.getName());
        typeView.setText(e.getTypename());
        addressView.setText("・"+e.getAddress());
    }


}
