package com.fengmap.drpeng.entity;

import java.io.Serializable;

/**
 * 楼层类
 */
public class Floor implements Serializable {
    private static final long serialVersionUID = -2255713125671414127L;
    private int mGroupId;
    private String mGroupName;

    public Floor() {

    }

    public Floor(int pGroupId, String pGroupname) {
        this.mGroupId = pGroupId;
        this.mGroupName = pGroupname;
    }

    public int getGroupId() {
        return mGroupId;
    }

    public void setGroupId(int pGroupId) {
        this.mGroupId = pGroupId;
    }

    public String getGroupName() {
        return mGroupName;
    }

    public void setGroupName(String pGroupName) {
        this.mGroupName = pGroupName;
    }
}
