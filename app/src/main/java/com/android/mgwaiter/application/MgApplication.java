package com.android.mgwaiter.application;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.os.Process;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/20/020.
 */

public class MgApplication extends Application {
    static MgApplication instance;
    private String deviceToken;
    private PushAgent mPushAgent;
    private String systemTime1;
    private String systemTime2;
    private String workTime;
    public void setSystemTime1(String time){
        systemTime1=time;
    }
    public void setSystemTime2(String time){
        systemTime2=time;
    }
    public String getSystemTime1(){
        return systemTime1;
    }
    public String getSystemTime2(){
        return systemTime2;
    }

    public void setWorkTime(String time){
        workTime=time;
    }
    public String getWorkTime(){
        return workTime;
    }
    public static MgApplication getInstance() {
        if (instance == null)
            instance = new MgApplication();
        return instance;
    }


    public void onCreate() {
        super.onCreate();
        instance=this;
        mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setDebugMode(true);
//注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {
            public void onSuccess(String deviceToken) {
                Log.d("mytoken", "LLLLL获取到的友盟 device token = " + deviceToken);
            }
            public void onFailure(String s, String s1) {

            }
        });
        mPushAgent.setMuteDurationSeconds(1);
        mPushAgent.setDisplayNotificationNumber(5);//通知最多条数
        //sdk开启通知声音
        mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE);



        UmengMessageHandler messageHandler = new UmengMessageHandler() {
        /*
        自定义通知栏
         */
            public Notification getNotification(Context context, UMessage msg) {
                switch (msg.builder_id) {
                    case 1:
//                        Notification.Builder builder = new Notification.Builder(context);
//                        RemoteViews myNotificationView = new RemoteViews(context.getPackageName(),
//                                R.layout.notification_view);
//                        myNotificationView.setTextViewText(R.id.notification_title, msg.title);
//                        myNotificationView.setTextViewText(R.id.notification_text, msg.text);
//                        myNotificationView.setImageViewBitmap(R.id.notification_large_icon,
//                                getLargeIcon(context, msg));
//                        myNotificationView.setImageViewResource(R.id.notification_small_icon,
//                                getSmallIconId(context, msg));
//                        builder.setContent(myNotificationView)
//                                .setSmallIcon(getSmallIconId(context, msg))
//                                .setTicker(msg.ticker)
//                                .setAutoCancel(true);

                    //    return builder.getNotification();
                    default:
                        //默认为0，若填写的builder_id并不存在，也使用默认。
                        return super.getNotification(context, msg);
                }
            }


            public void dealWithNotificationMessage(Context context, UMessage uMessage) {
                super.dealWithNotificationMessage(context, uMessage);
                Log.d("mytoken", "LLLLL获取到的友盟 dealWithNotificationMessage = " +uMessage.extra);
            }

/*
自定义消息
 */
public void dealWithCustomMessage(Context context, UMessage uMessage) {
                super.dealWithCustomMessage(context, uMessage);
               String name= uMessage.custom;
                Log.d("mytoken", "LLLLL获取到的友盟 name = " +name);
            }
        };
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler(){
           /*
           消息通知自定义行为
            */
            public void dealWithCustomAction(Context context, UMessage msg) {
                Log.d("mytoken", "LLLLL获取到的友盟 name友盟 = " +msg.custom);
            }
        };
        mPushAgent.setNotificationClickHandler(notificationClickHandler);
        mPushAgent.setMessageHandler(messageHandler);
    }
    /*
    获取deviceToken
    */
    public String getDeviceToken(){
        deviceToken=mPushAgent.getRegistrationId();
        return deviceToken;
    }

}
