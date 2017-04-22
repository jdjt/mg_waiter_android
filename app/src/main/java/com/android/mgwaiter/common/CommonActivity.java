package com.android.mgwaiter.common;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Administrator on 2017/4/22/022.
 */

public abstract class CommonActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (initPageLayoutID() != 0) {
            setContentView(initPageLayoutID());
        }
        initView();
    }

    /**
     * 返回主布局id
     */
    protected abstract int initPageLayoutID();

    /**
     * 执行初始化 view
     *
     * @return
     */
    protected abstract void initView();
}
