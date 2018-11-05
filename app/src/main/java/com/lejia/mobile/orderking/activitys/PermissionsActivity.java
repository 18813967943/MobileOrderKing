package com.lejia.mobile.orderking.activitys;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.WindowManager;

/**
 * Author by HEKE
 *
 * @time 2018/7/5 15:29
 * TODO: 权限申请界面
 */
public class PermissionsActivity extends Activity {

    /**
     * 读写权限
     */
    protected String[] rwPermissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    /**
     * 摄像机权限
     */
    protected String[] cameraPermissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_CONTACTS
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestRWPermissions();
    }

    /**
     * 读写权限
     */
    private void requestRWPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 存储权限
            if (ContextCompat.checkSelfPermission(this, rwPermissions[0]) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, rwPermissions, 1);
            } else {
                requestCamera();
            }
        }
    }

    private void requestCamera() {
        // 摄像机权限
        if (ContextCompat.checkSelfPermission(this, cameraPermissions[0]) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, cameraPermissions, 2);
        } else {
            // TODO 可执行下一条
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 读写权限
        if (requestCode == 1) {
            // 申请读写权限成功
            if (grantResults != null && grantResults[0] == 0) {
                requestCamera();
            } else {
                requestCamera();
            }
        }
        // 摄像机权限
        else if (requestCode == 2) {
            // 申请摄像机权限成功
            if (grantResults != null && grantResults[0] == 0) {
                // TODO 可执行下一条
                finish();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
