package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.models;

import com.lejia.mobile.orderking.utils.TextUtils;

import java.util.HashMap;

/**
 * Author by HEKE
 *
 * @time 2018/12/4 17:26
 * TODO: 当前设计方案所对应的模型数据库对象
 */
public class ModelsLibrary {

    /**
     * 单一模型数据缓存集合
     */
    private HashMap<String, L3DFile> l3dFileHashMap;

    public ModelsLibrary() {
        l3dFileHashMap = new HashMap<>();
    }

    /**
     * 存入数据
     *
     * @param code 模型编码
     */
    public void put(String code) {
        if (TextUtils.isTextEmpty(code))
            return;
        L3DFile l3dFile = l3dFileHashMap.get(code);
        if (l3dFile != null)
            return;
        // 存入并创建模型数据
        l3dFileHashMap.put(code, new L3DFile(code));
    }

    /**
     * 模型数量
     */
    public int size() {
        return l3dFileHashMap.size();
    }

    /**
     * 获取所有模型数据集合
     */
    public HashMap<String, L3DFile> getL3dFileHashMap() {
        return l3dFileHashMap;
    }

}
