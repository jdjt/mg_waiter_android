package com.android.mgwaiter.application;

import android.app.Application;

import com.fengmap.android.FMMapSDK;
import com.fengmap.drpeng.common.ResourcesUtils;

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
        FMMapSDK.init(this, ResourcesUtils.getSDPath() + "/fm_drpeng");
        super.onCreate();
        instance=this;
    }
}
