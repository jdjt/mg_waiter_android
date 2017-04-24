package com.android.mgwaiter.login;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.mgwaiter.MainActivity;
import com.android.mgwaiter.R;
import com.fengmap.android.FMMapSDK;
import com.fengmap.android.data.FMDataManager;
import com.fengmap.android.wrapmv.Tools;
import com.fengmap.drpeng.FMAPI;
import com.fengmap.drpeng.OutdoorMapActivity;
import com.fengmap.drpeng.common.ResourcesUtils;

/**
 * Created by huyanan on 2017/4/14.
 *登录页面
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
        if (Build.VERSION.SDK_INT < 23) {
            copyMap();
        } else {
            int p1 = LoginActivity.this.checkSelfPermission(FMMapSDK.SDK_PERMISSIONS[0]);
            int p2 = LoginActivity.this.checkSelfPermission(FMMapSDK.SDK_PERMISSIONS[1]);

            if (p1 != PackageManager.PERMISSION_GRANTED || p2 != PackageManager.PERMISSION_GRANTED ) {
                // apply
                LoginActivity.this.requestPermissions(FMMapSDK.SDK_PERMISSIONS,
                        FMMapSDK.SDK_PERMISSION_RESULT_CODE);
            } else {
                copyMap();
            }
        }
    }
    //初始化控件
    private void inview(){
        et_login_account = (EditText)findViewById(R.id.et_login_account);
        et_login_password = (EditText)findViewById(R.id.et_login_password);
        bt_login_button = (Button) findViewById(R.id.bt_login_button);
        bt_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //添加点击事件
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });
    }

    //权限校验
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                grantResults[1] != PackageManager.PERMISSION_GRANTED) {

            this.finish();
            return;

        }
        if (requestCode == FMMapSDK.SDK_PERMISSION_RESULT_CODE) {
            copyMap();
        }
    }

    void copyMap() {
        FMMapSDK.initResource();
        writeMapFile("79980");
        writeMapFile("79981");
        writeMapFile("79982");
        writeMapFile("70144");
        writeMapFile("70145");
        writeMapFile("70146");
        writeMapFile("70147");
        writeMapFile("70148");
    }


    private void writeMapFile(String mapId) {
        String dstFileName = mapId + ".fmap";
        String srcFileName = "data/" + dstFileName;
        ResourcesUtils.writeRc(this,
                FMDataManager.getFMMapResourceDirectory() + mapId + "/", dstFileName, srcFileName);
    }
}
