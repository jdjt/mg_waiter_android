package com.fengmap.drpeng.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.fengmap.drpeng.common.ViewHolder;

import java.util.List;

/**
 * Created by yangbin on 15/10/28.
 */
public abstract class CommonAdapter<T> extends BaseAdapter{

    protected Context context;
    protected List<T> data;
    protected int layoutID;

    public CommonAdapter(Context context, List<T> data, int layoutID) {
        this.context = context;
        this.data = data;
        this.layoutID = layoutID;
    }

    public void updateData(List<T> newData) {
        data.clear();
        this.data = newData;
        notifyDataSetChanged();
    }

    public void addData(List<T> data) {
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public void clearData() {
        data.clear();
        notifyDataSetChanged();
    }


    @Override
    public final int getCount() {
        return data.size();
    }

    @Override
    public final T getItem(int position) {
        return data.get(position);
    }

    @Override
    public final long getItemId(int position) {
        return position;
    }

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.getViewHolder(context, parent, convertView, layoutID, position);
        deal(holder, getItem(position));
        return holder.getConvertView();
    }


    abstract void deal(ViewHolder holder, T t);

}
