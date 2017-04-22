package com.fengmap.drpeng.common;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by yangbin on 15/10/29.
 */
public final class ViewHolder {

    private SparseArray<View> views;

    private int position;
    private View convertView;


    public ViewHolder(Context context, ViewGroup parent, View convertView, int layoutID, int position) {
        this.position = position;
        this.views = new SparseArray<View>();
        this.convertView = LayoutInflater.from(context).inflate(layoutID, parent, false);
        this.convertView.setTag(this);
    }

    public static ViewHolder getViewHolder(Context context, ViewGroup parent, View convertView, int layoutID, int position) {
        if(convertView==null){
            return new ViewHolder(context, parent, convertView, layoutID, position);
        }else{
            ViewHolder holder = (ViewHolder)convertView.getTag();
            holder.position = position;    //update position
            return holder;
        }
    }

    public View getConvertView() {
        return convertView;
    }


    public <T extends View> T getView(int viewID) {
        View view = views.get(viewID);
        if(view == null) {
            view =  convertView.findViewById(viewID);
            views.put(viewID, view);
        }
        return (T)view;
    }

    public ViewHolder setBackgroud(int viewID, int color) {
        View v = getView(viewID);
        v.setBackgroundColor(color);
        return this;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public ViewHolder setBackgroud(int viewID, Drawable d) {
        View v = getView(viewID);
        v.setBackground(d);
        return this;
    }


    //TextView
    public ViewHolder setText(int viewID, String text) {
        TextView tv = getView(viewID);
        tv.setText(text);
        return this;
    }
    public ViewHolder setText(int viewID, String text, float textSize) {
        TextView tv = getView(viewID);
        tv.setText(text);
        tv.setTextSize(textSize);
        return this;
    }
    public ViewHolder setText(int viewID, String text, int color) {
        TextView tv = getView(viewID);
        tv.setText(text);
        tv.setTextColor(color);
        return this;
    }
    public ViewHolder setText(int viewID, String text, float textSize, int color) {
        TextView tv = getView(viewID);
        tv.setText(text);
        tv.setTextColor(color);
        tv.setTextSize(textSize);
        return this;
    }



    //ImageView
    public ViewHolder setImageBitmap(int viewID, Bitmap bmp) {
        ImageView iv = getView(viewID);
        iv.setImageBitmap(bmp);
        return this;
    }
    public ViewHolder setImageDrawable(int viewID, Drawable d) {
        ImageView iv = getView(viewID);
        iv.setImageDrawable(d);
        return this;
    }
    public ViewHolder setImageResource(int viewID, int resID) {
        ImageView iv = getView(viewID);
        iv.setImageResource(resID);
        return this;
    }
    public ViewHolder setImageBackgroundResource(int viewID, int resID) {
        ImageView iv = getView(viewID);
        iv.setBackgroundResource(resID);
        return this;
    }
    @Deprecated
    public ViewHolder setImageBackgroundDrawable(int viewID, Drawable d) {
        ImageView iv = getView(viewID);
        iv.setBackgroundDrawable(d);
        return this;
    }


}
