package com.lejia.mobile.orderking.hk3d.classes;

import android.graphics.Bitmap;

/**
 * Author by HEKE
 *
 * @time 2018/7/16 11:46
 * TODO: 纹理对象
 */
public class Texture {
    public String key; // 唯一标识
    public int textureId; // 纹理编号
    public Bitmap bitmap; // 对应纹理位图

    public void release() {
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        key = null;
    }
}
