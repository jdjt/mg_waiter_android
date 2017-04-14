package com.android.mgwaiter.net;

/**
 * 相应返回接口
 * @param <T>
 */
public interface ReqCallBack<T> {
    /**
     * 响应成功
     */
     void onReqSuccess(T result);

    /**
     * 响应失败
     */
     void onReqFailed(String errorMsg);
}