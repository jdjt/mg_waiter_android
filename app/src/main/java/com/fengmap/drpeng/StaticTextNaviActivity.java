package com.fengmap.drpeng;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.android.mgwaiter.R;

import java.util.ArrayList;

/**
 * Created by yangbin on 16/5/23.
 */
public class StaticTextNaviActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textstatic_navi);

        Intent mIntent = getIntent();
        ArrayList<String> results = mIntent.getStringArrayListExtra("static_text_navi");

        TextView tv_navi = (TextView) findViewById(R.id.tv_static_navi_rs);

        if (results != null) {
            StringBuffer sb = new StringBuffer();
            for (String s : results) {
                sb.append(s + "\n");
            }
            sb.deleteCharAt(sb.length()-1);
            tv_navi.setText(sb.toString());
        }

    }
}
