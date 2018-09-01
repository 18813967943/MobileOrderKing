package com.lejia.mobile.orderking.hk3d.datas;

import com.lejia.mobile.orderking.utils.TextUtils;

import java.util.HashMap;

/**
 * Author by HEKE
 *
 * @time 2018/9/1 10:04
 * TODO: 模型核心数据缓存对象
 */
public class FurnitureCache {

    /**
     * 模型子件数据缓存
     */
    public static final HashMap<String, Subset> subsetMaps = new HashMap<>();

    /**
     * 判断缓存是否存在
     *
     * @param code
     */
    public static boolean existed(String code) {
        if (subsetMaps.size() == 0)
            return false;
        return subsetMaps.containsKey(code);
    }

    /**
     * 保存
     *
     * @param code   模型编码
     * @param subset 模型子件
     */
    public static void put(String code, Subset subset) {
        if (TextUtils.isTextEmpity(code) || subset == null)
            return;
        if (existed(code))
            return;
        subsetMaps.put(code, subset);
    }

    /**
     * 删除
     *
     * @param code
     */
    public static void remove(String code) {
        if (TextUtils.isTextEmpity(code)) {
            return;
        }
        if (!existed(code))
            return;
        subsetMaps.remove(code);
    }

    /**
     * 获取子件
     *
     * @param code
     * @return
     */
    public static Subset get(String code) {
        if (TextUtils.isTextEmpity(code)) {
            return null;
        }
        if (!existed(code))
            return null;
        return subsetMaps.get(code);
    }

}
