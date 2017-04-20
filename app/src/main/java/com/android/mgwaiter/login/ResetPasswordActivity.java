package com.android.mgwaiter.login;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.mgwaiter.R;

/**
 * Created by huyanan on 2017/4/17.
 * 修改密码
 * */

public class ResetPasswordActivity extends AppCompatActivity{
    EditText et_set_password;
    EditText et_confirm_password;
    Button bt_countersign;
    String setPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password_activity);
        inview();
    }
    //初始化控件
    private void inview(){
        et_set_password = (EditText)findViewById(R.id.et_set_password);//设置新密码
        et_confirm_password = (EditText)findViewById(R.id.et_confirm_password);//确认新密码
        bt_countersign = (Button) findViewById(R.id.bt_countersign);//确认按钮
    }
    //点击事件
    private void onclick(){
        setPassword = et_set_password.getText().toString();
        bt_countersign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //添加点击事件
                String confirmPassword = et_confirm_password.getText().toString();
                if(setPassword.equals(confirmPassword)){
                    //修改密码逻辑

                }else {
                    Toast.makeText(getApplication(),"密码不一致",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //
}
