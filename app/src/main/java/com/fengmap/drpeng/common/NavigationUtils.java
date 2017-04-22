package com.fengmap.drpeng.common;

import com.fengmap.android.wrapmv.FMNaviAnalysisHelper;
import com.fengmap.drpeng.FMAPI;

/**
 * 导航工具类。
 * Created by yangbin on 2016/11/26.
 */

public class NavigationUtils {

    public static NavigationDescription forNavigationDescription(float pWalkedDis) {
        float remainingDis  = FMAPI.instance().mInitNeedDistance - pWalkedDis;
        if (remainingDis < FMAPI.COMPLETED_MIN_DISTANCE) {
            remainingDis = 0;
            pWalkedDis = FMAPI.instance().mInitNeedDistance;
        }

        String strRemainingDistance = StringUtils.fixedRemainingDistance(remainingDis);

        // 时间
        float walkedTime = pWalkedDis / FMNaviAnalysisHelper.SPEED_PER_MINUTE;
        float remainingTime = FMAPI.instance().mInitNeedTime - walkedTime;
        if (remainingDis < 0 || remainingTime < 0) {
            remainingTime = 0;
        }
        String strRemainingTime = StringUtils.fixedInitTime(remainingTime);

        return new NavigationDescription(remainingDis, strRemainingDistance,
                                         remainingTime, strRemainingTime);
    }

    public static NavigationDataDescription forNavigationDataDescription(int[] pCalcuPathResultCode) {
        float[] sportValues = FMNaviAnalysisHelper.instance().getPathSportValues(pCalcuPathResultCode);
        FMAPI.instance().mWalkedDistance = 0;
        FMAPI.instance().mInitNeedDistance = sportValues[0];
        FMAPI.instance().mInitNeedTime = sportValues[1];
        FMAPI.instance().mInitNeedCalorie = sportValues[2];

        String needDistance = StringUtils.fixedInitDistance(FMAPI.instance().mInitNeedDistance);
        String needTime = StringUtils.fixedInitTime(FMAPI.instance().mInitNeedTime);
        String needCalorie = StringUtils.fixedInitCalorie(FMAPI.instance().mInitNeedCalorie);

        return new NavigationDataDescription(needDistance, needTime, needCalorie);
    }

    public static class NavigationDescription {
        private float mRemainedDistance;
        private String mRemainedDistanceDesc;
        private float mRemainedTime;
        private String mRemainedTimeDesc;
        public NavigationDescription(float pRemainedDistance, String pRemainedDistanceDesc,
                                     float pRemainedTime, String pRemainedTimeDesc) {
            this.mRemainedDistance = pRemainedDistance;
            this.mRemainedDistanceDesc = pRemainedDistanceDesc;
            this.mRemainedTime = pRemainedTime;
            this.mRemainedTimeDesc = pRemainedTimeDesc;
        }

        public float getRemainedDistance() {
            return mRemainedDistance;
        }

        public String getRemainedDistanceDesc() {
            return mRemainedDistanceDesc;
        }

        public float getRemainedTime() {
            return mRemainedTime;
        }

        public String getRemainedTimeDesc() {
            return mRemainedTimeDesc;
        }
    }

    public static class NavigationDataDescription {
        private String mTotalDistanceDesc;
        private String mTotalTimeDesc;
        private String mTotalCalorieDesc;
        public NavigationDataDescription(String pTotalDistanceDesc, String pTotalTimeDesc, String pTotalCalorieDesc) {
            this.mTotalDistanceDesc = pTotalDistanceDesc;
            this.mTotalTimeDesc = pTotalTimeDesc;
            this.mTotalCalorieDesc = pTotalCalorieDesc;
        }

        public String getTotalCalorieDesc() {
            return mTotalCalorieDesc;
        }

        public String getTotalTimeDesc() {
            return mTotalTimeDesc;
        }

        public String getTotalDistanceDesc() {
            return mTotalDistanceDesc;
        }
    }
}
