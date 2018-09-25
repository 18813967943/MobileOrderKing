package com.lejia.mobile.orderking.hk3d.datas_2d;

import android.graphics.Bitmap;

import com.lejia.mobile.orderking.hk3d.classes.Texture;
import com.lejia.mobile.orderking.utils.TextUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Author by HEKE
 *
 * @time 2018/7/16 11:35
 * TODO: 所有资源材质纹理贴图缓存管理对象
 */
public class TexturesCache {

    /**
     * 缓存序列
     */
    private static final HashMap<String, Texture> staticMap = new HashMap<>();

    /**
     * 保存
     *
     * @param key
     * @param textureId
     * @param bitmap
     */
    public static synchronized void put(String key, int textureId, Bitmap bitmap) {
        if (TextUtils.isTextEmpity(key) || bitmap == null || bitmap.isRecycled())
            return;
        Texture texture = new Texture();
        texture.key = key;
        texture.textureId = textureId;
        texture.bitmap = bitmap;
        staticMap.put(key, texture);
    }

    /**
     * 获取对应材质缓存
     *
     * @param key 唯一标识
     * @return 对应贴图
     */
    public static Texture get(String key) {
        if (TextUtils.isTextEmpity(key))
            return null;
        return staticMap.get(key);
    }

    /**
     * 清除数据
     */
    public static void clear() {
        TexturesCache.staticMap.clear();
    }

    /**
     * 释放数据
     */
    public static void release() {
        Iterator<Map.Entry<String, Texture>> iterator = staticMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Texture> entry = iterator.next();
            Texture texture = entry.getValue();
            texture.release();
            entry.setValue(null);
        }
        clear();
    }

}
