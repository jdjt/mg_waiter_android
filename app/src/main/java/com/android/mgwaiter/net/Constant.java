package com.android.mgwaiter.net;

/**
 *  常量类定义接口地址
 */
public final class Constant {
    public static final String URL="192.168.1.45:8181";
    public static final String LOGIN =  "uum/mem/sso/login.json";
    public static final String GETMASTERMESSAGE= URL+"/hotelcallservice/waiter/getWaiterInfoByWaiterId.json";
    public static final String MASTERSTATE= URL+"/hotelcallservice/waiter/settingWorkingStatus.json";
    public static final String GETMASKINGLIST= URL+"/hotelcallservice/waiter/getTaskInfoAfterAccept.json";
    public static final String  GRABSINGLE=URL+"/hotelcallservice/waiter/acceptTask.json";
    public static final String  AFTERWORK=URL+"/hotelcallservice/waiter/logout.json";
    public static final String  GETTASKMESSAGE=URL+"/hotelcallservice/waiter/getTaskInfoByTaskCode.json";
    public static final String COMPLETE=URL+"/hotelcallservice/waiter/confirmTask.json";
}
