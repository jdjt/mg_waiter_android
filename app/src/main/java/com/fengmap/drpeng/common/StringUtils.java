package com.fengmap.drpeng.common;


import static com.fengmap.drpeng.FMAPI.COMPLETED_MIN_DISTANCE;

/**
 * 字符工具类。
 * Created by yangbin on 16/9/5.
 */

public class StringUtils {

    private StringUtils() {}


    public static String fixedInitDistance(float initDis) {
        if (initDis < COMPLETED_MIN_DISTANCE) {
            initDis = 0;
            return String.format("%d米", (int)initDis);
        } else {
            return String.format("%d米", (int) (initDis+0.5));
        }
    }


    public static String fixedInitTime(float initTime) {
//        if (initTime <= 2) {
            initTime *= 60;
            return String.format("%d秒", (int)initTime);
//        }
//        else {
//            return String.format("%d分钟", (int) (initTime+0.5));
//        }
    }


    public static String fixedInitCalorie(float calorie) {
        return String.format("%d卡路里", (int) (calorie+0.5));
    }


    public static String fixedRemainingDistance(float initDis) {
        if (initDis <= COMPLETED_MIN_DISTANCE) {
            initDis = 0;
            return String.format("全程剩余%d米", (int)initDis);
        } else {
            return String.format("全程剩余%d米", (int) Math.ceil(initDis));
        }
    }



}
