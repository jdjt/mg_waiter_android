package com.fengmap.drpeng.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.fengmap.android.utils.FMLog;
import com.android.mgwaiter.R;
import com.fengmap.drpeng.common.ControlItemHold;

import java.util.ArrayList;

/**
 * Created by yangbin on 15/10/27.
 */
public class ButtonAdapter extends RecyclerView.Adapter<ControlItemHold> {
    private final LayoutInflater layoutInflater;
    private String[] buttonTypes;
    private OnItemClickListener listener;
    public ArrayList<Integer> selectedButton = new ArrayList<Integer>();

    public ButtonAdapter(Context context) {
        this.layoutInflater = LayoutInflater.from(context);
        buttonTypes = context.getResources().getStringArray(R.array.ButtonFuction);
    }

    @Override
    public ControlItemHold onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.item_button,parent,false);
        ControlItemHold hold = new ControlItemHold(v);
        return hold;
    }

    @Override
    public void onBindViewHolder(ControlItemHold holder, final int position) {
        String text = buttonTypes[position];
        holder.setText(text);
        holder.setTextSize(16);
        holder.setTextColor(Color.BLACK);
        for (int i=0; i<selectedButton.size();i++) {
        	int seleP = selectedButton.get(i);
        	FMLog.i("onBindViewHolder", "selectedButton position: " + seleP);
        	if(seleP == position) {
        		holder.setTextColor(Color.RED);
        	}
        }
        holder.button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener==null)return;
				listener.onItemClick(v, position);
			}
		});
        
    }
    
    public void setOnItemClickListener(OnItemClickListener l) {
        this.listener = l;
    }


    @Override
    public int getItemCount() {
        return buttonTypes.length;
    }

 
    public String[] getButtonTypes() {
        return buttonTypes;
    }

    
    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

   
}
