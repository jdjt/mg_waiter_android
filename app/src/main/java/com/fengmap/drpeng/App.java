package com.fengmap.drpeng;

import android.app.Application;

import com.fengmap.android.FMMapSDK;
import com.fengmap.drpeng.common.ResourcesUtils;

public class App extends Application {

	@Override
	public void onCreate() {

		FMMapSDK.init(this, ResourcesUtils.getSDPath() + "/fm_drpeng");

		Thread.setDefaultUncaughtExceptionHandler(CrashHandler.getAppExceptionHandler(this));

		super.onCreate();
	}

}
