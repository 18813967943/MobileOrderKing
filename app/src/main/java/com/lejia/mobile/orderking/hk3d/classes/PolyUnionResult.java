package com.lejia.mobile.orderking.hk3d.classes;

import com.seisw.util.geom.Poly;

import java.util.Map;

/**
 * Author by HEKE
 *
 * @time 2018/7/19 11:49
 * TODO: 区域组合结果
 */
public class PolyUnionResult {

    public Map.Entry<Integer, Poly> entry; // 对应实体对象
    public Poly result; // 组合结果

    public PolyUnionResult() {
        super();
    }

    public PolyUnionResult(Map.Entry<Integer, Poly> entry, Poly result) {
        this.entry = entry;
        this.result = result;
    }

    // 释放绑定关系
    public void release() {
        entry = null;
    }

}
