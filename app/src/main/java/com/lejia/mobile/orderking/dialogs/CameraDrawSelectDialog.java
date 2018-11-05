package com.lejia.mobile.orderking.dialogs;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.lejia.mobile.orderking.R;
import com.lejia.mobile.orderking.utils.FileUtils;
import com.lejia.mobile.orderking.utils.TextUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author by HEKE
 *
 * @time 2018/10/15 11:11
 * TODO: 摄像机拍照或本地选择窗口，用于图片识别户型功能
 */
public class CameraDrawSelectDialog extends Dialog {

    @BindView(R.id.dcimSelect)
    TextView dcimSelect;
    @BindView(R.id.dcimTake)
    TextView dcimTake;

    private Activity mTActivity;

    private File outputImagepath; // 转入识别图片文件

    public CameraDrawSelectDialog(@NonNull Context context) {
        super(context, R.style.transparentDiag);
        mTActivity = (Activity) context;
    }

    private void init() {
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = TextUtils.dip2px(getContext(), 200);
        layoutParams.height = TextUtils.dip2px(getContext(), 90);
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.dialog_camera_draw);
        ButterKnife.bind(this);
        init();
    }

    @OnClick({R.id.dcimSelect, R.id.dcimTake})
    public void onViewClicked(View view) {
        dismiss();
        switch (view.getId()) {
            case R.id.dcimSelect:
                dcmi();
                break;
            case R.id.dcimTake:
                takePhone();
                break;
        }
    }

    /**
     * 相册选择
     */
    public void dcmi() {
        Intent idf = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        mTActivity.startActivityForResult(idf, 1015);
    }

    /**
     * 拍照
     */
    public void takePhone() {
        // 已有拍照权限，执行拍照
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            //获取系統版本
            int currentapiVersion = Build.VERSION.SDK_INT;
            // 激活相机
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // 判断存储卡是否可以用，可用进行存储
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat timeStampFormat = new SimpleDateFormat(
                        "yyyy_MM_dd_HH_mm_ss");
                String filename = timeStampFormat.format(new Date());
                String parentPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LeJia2/cameradraw/";
                FileUtils.createDirectory(parentPath);
                FileUtils.deleteAllFiles(parentPath);
                outputImagepath = new File(parentPath, filename + ".jpg");
                if (currentapiVersion < 24) {
                    // 从文件中创建uri
                    Uri uri = Uri.fromFile(outputImagepath);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                } else {
                    //兼容android7.0 使用共享文件的形式
                    ContentValues contentValues = new ContentValues(1);
                    contentValues.put(MediaStore.Images.Media.DATA, outputImagepath.getAbsolutePath());
                    Uri uri = getContext().getApplicationContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                }
            }
            // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
            mTActivity.startActivityForResult(intent, 1014);
        }
        // 无拍照权限，申请权限
        else {
            ActivityCompat.requestPermissions(mTActivity, new String[]{Manifest.permission.CAMERA}, 1013);
        }
    }

    /**
     * 获取相册选择的图片文件
     */
    public File getOutputImagepath() {
        return outputImagepath;
    }

}
