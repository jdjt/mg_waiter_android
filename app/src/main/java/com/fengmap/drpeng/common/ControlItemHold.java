package com.fengmap.drpeng.common;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.android.mgwaiter.R;


/**
 * Created by yangbin on 15/10/27.
 */
public class ControlItemHold extends RecyclerView.ViewHolder {
    public Button button;
    
    public ControlItemHold(View itemView) {
        super(itemView);
        if(button == null) 
        	button = (Button) itemView.findViewById(R.id.button);
    }

    public void setText(String text) {
    	button.setText(text);
    }

    public void setTextSize(float textSize) {
    	button.setTextSize(textSize);
    }

    public void setTextColor(int color) {
    	button.setTextColor(color);
    }


}
