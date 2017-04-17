package com.android.mgwaiter.login;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.mgwaiter.R;

/**
 * Created by huyanan on 2017/4/14.
 *
 */

public class LoginActivity extends AppCompatActivity{

    EditText et_login_account;//账号
    EditText et_login_password;//密码
    Button bt_login_button;//登录

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        inview();
    }
    //初始化控件
    private void inview(){
        et_login_account = (EditText)findViewById(R.id.et_login_account);
        et_login_password = (EditText)findViewById(R.id.et_login_password);
        bt_login_button = (Button) findViewById(R.id.bt_login_button);
    }
    //点击事件
    private void onclick(){
        bt_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //添加点击事件
            }
        });
    }
    //
}
