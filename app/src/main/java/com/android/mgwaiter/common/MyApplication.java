package com.android.mgwaiter.common;

import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.provider.Settings;
import android.widget.Toast;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

/**
 * Created by hp on 2017/4/18.
 */

public class MyApplication extends Application{
private String deviceToken;
    private PushAgent mPushAgent;
    public void onCreate() {
        super.onCreate();
         mPushAgent = PushAgent.getInstance(this);
//注册推送服务，每次调用register方法都会回调该接口


        UmengMessageHandler messageHandler = new UmengMessageHandler(){
            public void dealWithCustomMessage(final Context context, final UMessage msg) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        // 对于自定义消息，PushSDK默认只统计送达。若开发者需要统计点击和忽略，则需手动调用统计方法。
                        boolean isClickOrDismissed = true;
                        if(isClickOrDismissed) {
                            //自定义消息的点击统计
                            UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
                        } else {
                            //自定义消息的忽略统计
                            UTrack.getInstance(getApplicationContext()).trackMsgDismissed(msg);
                        }
                        Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
                    }
                });
            }


            public void dealWithNotificationMessage(Context context, UMessage uMessage) {
                super.dealWithNotificationMessage(context, uMessage);
                Toast.makeText(context, uMessage.custom, Toast.LENGTH_LONG).show();
            }
        };
        mPushAgent.setMessageHandler(messageHandler);




        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {

            public void dealWithCustomAction(Context context, UMessage msg) {
                Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
            }


            public void openActivity(Context context, UMessage uMessage) {
                super.openActivity(context, uMessage);

            }


            public void launchApp(Context context, UMessage uMessage) {
                super.launchApp(context, uMessage);
            }


            public void openUrl(Context context, UMessage uMessage) {
                super.openUrl(context, uMessage);
            }
        };
        mPushAgent.setNotificationClickHandler(notificationClickHandler);


        mPushAgent.register(new IUmengRegisterCallback() {
            public void onSuccess(String deviceToken) {


            }
            public void onFailure(String s, String s1) {

            }
        });
        mPushAgent.setMuteDurationSeconds(1);
        mPushAgent.setDisplayNotificationNumber(5);//通知最多条数
    }
    /*
     获取deviceToken
     */
    public String getDeviceToken(){
        deviceToken=mPushAgent.getRegistrationId();
        return deviceToken;
    }

}
