package com.fengmap.drpeng.entity;

import com.fengmap.android.map.geometry.FMMapCoord;

/**
 * Created by yangbin on 16/7/6.
 */
public class FMMapCoordWithAngle {

    private FMMapCoord mCoord;
    private float      mAngle;

    public FMMapCoordWithAngle(FMMapCoord pCoord, float pAngle) {
        this.mCoord = pCoord;
        this.mAngle = pAngle;
    }


    public FMMapCoord getCoord() {
        return mCoord;
    }

    public float getAngle() {
        return mAngle;
    }
}
