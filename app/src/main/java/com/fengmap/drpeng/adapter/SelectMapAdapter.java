package com.fengmap.drpeng.adapter;

import android.content.Context;
import android.widget.TextView;

import com.android.mgwaiter.R;
import com.fengmap.drpeng.common.ViewHolder;
import com.fengmap.drpeng.entity.ItemMap;

import java.util.List;

/**
 * 选择地图适配器。
 * Created by yangbin on 16/8/27.
 */

public class SelectMapAdapter extends CommonAdapter<ItemMap> {


    public SelectMapAdapter(Context context, List<ItemMap> data, int layoutID) {
        super(context, data, layoutID);
    }


    @Override
    void deal(ViewHolder holder, final ItemMap item) {
        TextView name = holder.getView(R.id.fm_search_select_map_name);
        name.setText(item.mMapName);
    }

}
