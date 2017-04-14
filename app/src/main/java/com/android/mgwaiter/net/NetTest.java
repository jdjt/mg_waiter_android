package com.android.mgwaiter.net;

import android.content.Context;

import java.util.HashMap;

/**
 * Created by Administrator on 2017/4/14/014.
 */

public class NetTest implements  ReqCallBack {

    private void test(Context context){

        RequestManager.getInstance(context).requestAsyn("url",RequestManager.TYPE_POST_JSON,new HashMap<String, String>(),this);
    }
    @Override
    public void onReqSuccess(Object result) {

    }

    @Override
    public void onReqFailed(String errorMsg) {

    }
}
