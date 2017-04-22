package com.android.mgwaiter.common;


import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;

import com.android.mgwaiter.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.Calendar;
import java.util.Date;

public class CalenderActivity extends CommonActivity {

    private MaterialCalendarView calendarView;

    @Override
    protected int initPageLayoutID() {
        return R.layout.activity_calender;
    }

    @Override
    protected void initView() {
        calendarView= (MaterialCalendarView) findViewById(R.id.calendarView);

        calendarView.setShowOtherDates(MaterialCalendarView.SHOW_ALL);
        Calendar instance = Calendar.getInstance();
        calendarView.setSelectedDate(instance.getTime());
        Calendar instance1 = Calendar.getInstance();
        instance1.set(1990, Calendar.JANUARY, 1); //可选择日期
        Calendar instance2 = Calendar.getInstance();
        //设置最大可选日期为当天
        instance2.set(instance2.get(Calendar.YEAR), instance2.get(Calendar.MONTH), instance2.get(Calendar.DAY_OF_MONTH));
        calendarView.state().edit()
                .setMinimumDate(instance1.getTime())
                .setMaximumDate(instance2.getTime())
                .commit();
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                    if(selected){
                        Toast.makeText(CalenderActivity.this,
                        "您的选择的是"+date.getYear()+"年"+(date.getMonth()+1)+"月"+date.getDay()+"日", Toast.LENGTH_SHORT).show();
                    }
            }
        });
    }

}
