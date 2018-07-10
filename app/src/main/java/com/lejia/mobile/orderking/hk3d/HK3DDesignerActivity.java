package com.lejia.mobile.orderking.hk3d;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import com.lejia.mobile.orderking.R;
import com.lejia.mobile.orderking.activitys.PermissionsActivity;
import com.lejia.mobile.orderking.hk3d.classes.Tile;

/**
 * Author by HEKE
 *
 * @time 2018/7/2 9:52
 * TODO: 三维设计界面
 */
public class HK3DDesignerActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.setContentView(R.layout.activity_main);
        // 打开权限申请
        startActivityForResult(new Intent(this, PermissionsActivity.class), -1);
        // 测试材质对象
        String imageUrl = "http://e.hiphotos.baidu.com/image/pic/item/2f738bd4b31c8701ad467c1a2b7f9e2f0608ff5e.jpg";
        Tile tile = new Tile();
        tile.setImageUrl(imageUrl);
        tile.getBitmap(new Tile.OnTileBitmapListener() {
            @Override
            public void onTileBitmapThenDoSomething(Bitmap bitmap) {
                System.out.println("####");
            }
        });
    }

}
