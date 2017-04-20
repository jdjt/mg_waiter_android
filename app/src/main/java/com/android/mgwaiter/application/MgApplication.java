package com.android.mgwaiter.application;

import android.app.Application;

/**
 * Created by Administrator on 2017/4/20/020.
 */

public class MgApplication extends Application {
    static MgApplication instance;

    public static MgApplication getInstance() {
        if (instance == null)
            instance = new MgApplication();
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
    }
}
