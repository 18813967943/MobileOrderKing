package com.lejia.mobile.orderking.hk3d.classes;

import com.lejia.mobile.orderking.utils.TextUtils;
import com.seisw.util.geom.Poly;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Author by HEKE
 *
 * @time 2018/8/8 16:01
 * TODO: 未闭合房间数据处理对象
 */
public class PolyUM {


    /**
     * 未闭合集合列表,key为房间对应的hashcode
     */
    private static HashMap<String, Poly> poliesMap = new HashMap<>();


    /**
     * 判断两个组合区域是否相同
     *
     * @param poly1 任意组合1
     * @param poly2 任意组合2
     * @return true 相匹配
     */
    public static boolean polyMatched(Poly poly1, Poly poly2) {
        if (poly1 == null || poly2 == null || poly1.isEmpty() || poly2.isEmpty())
            return false;
        if (poly1.equals(poly2))
            return true;
        boolean equals = true;
        try {
            ArrayList<Point> pointsList1 = PolyE.toPointsList(poly1);
            ArrayList<Point> pointsList2 = PolyE.toPointsList(poly2);
            for (Point p1 : pointsList1) {
                boolean existed = false;
                for (Point p2 : pointsList2) {
                    existed = p1.equals(p2);
                    if (existed) {
                        break;
                    }
                }
                equals = (equals && existed);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return equals;
    }

    /**
     * 判断是否已经存在
     */
    public static boolean existed(Poly poly) {
        if (poly == null || poly.isEmpty())
            return false;
        boolean existed = false;
        if (poliesMap.size() == 0)
            return false;
        else {
            ArrayList<Poly> poliesList = new ArrayList<>(poliesMap.values());
            for (Poly poly1 : poliesList) {
                existed = polyMatched(poly1, poly);
                if (existed)
                    break;
            }
        }
        return existed;
    }

    /**
     * 存入数据
     *
     * @param hashCode
     * @param poly
     */
    public static void put(String hashCode, Poly poly) {
        if (TextUtils.isTextEmpty(hashCode) || poly == null || poly.isEmpty())
            return;
        Poly ip = poliesMap.get(hashCode);
        if (ip == null) { // 不存在加入
            poliesMap.put(hashCode, poly);
        } else {
            if (!polyMatched(poly, ip)) { // 存在，但是不匹配刷新
                poliesMap.put(hashCode, poly);
            }
        }
    }

    /**
     * 移除对应的房间墙体数据缓存
     *
     * @param hashCode
     */
    public static void remove(String hashCode) {
        if (TextUtils.isTextEmpty(hashCode))
            return;
        poliesMap.remove(hashCode);
    }

    /**
     * 清空
     */
    public static void clear() {
        poliesMap.clear();
    }


}
