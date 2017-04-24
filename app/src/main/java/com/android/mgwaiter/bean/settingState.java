package com.android.mgwaiter.bean;

/**
 * Created by hp on 2017/4/22.
 */

public class settingState {
    private int retOk;//1失败  0成功
    private String message;//信息

    public int getRetOk() {
        return retOk;
    }

    public void setRetOk(int retOk) {
        this.retOk = retOk;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
