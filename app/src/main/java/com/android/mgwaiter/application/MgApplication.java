package com.android.mgwaiter.application;

import android.app.Application;

import com.alibaba.mobileim.YWAPI;
import com.alibaba.wxlib.util.SysUtil;
import com.fengmap.android.FMMapSDK;
import com.fengmap.drpeng.common.ResourcesUtils;

/**
 * Created by Administrator on 2017/4/20/020.
 */

public class MgApplication extends Application {
    static MgApplication instance;
    final String APP_KEY = "23758144";

    public static MgApplication getInstance() {
        if (instance == null)
            instance = new MgApplication();
        return instance;
    }

    @Override
    public void onCreate() {
        FMMapSDK.init(this, ResourcesUtils.getSDPath() + "/fmap_drpeng");
        super.onCreate();
        instance=this;
        //必须首先执行这部分代码, 如果在":TCMSSevice"进程中，无需进行云旺（OpenIM）和app业务的初始化，以节省内存;
        SysUtil.setApplication(this);
        if(SysUtil.isTCMSServiceProcess(this)){
            return;
        }
        //第一个参数是Application Context
        //这里的APP_KEY即应用创建时申请的APP_KEY，同时初始化必须是在主进程中
        if(SysUtil.isMainProcess()){
            YWAPI.init(instance, APP_KEY);
        }
    }
}
