package com.lejia.mobile.orderking.hk3d.classes;

import com.lejia.mobile.orderking.hk3d.datas.House;
import com.seisw.util.geom.Poly;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Author by HEKE
 *
 * @time 2018/7/18 10:07
 * TODO: 所有房间区域组合管理对象
 */
public class PolyM {

    private static int index = -1; // 下标，组合编号

    /**
     * 当前所有组合区域结果列表
     */
    private static HashMap<Integer, Poly> poliesMap = new HashMap<>();

    /**
     * 判断两个组合区域是否相同
     *
     * @param poly1 任意组合1
     * @param poly2 任意组合2
     * @return true 相匹配
     */
    public static boolean polyMatched(Poly poly1, Poly poly2) {
        if (poly1 == null || poly2 == null)
            return false;
        if (poly1.equals(poly2))
            return true;
        PointList pointList1 = PolyE.toPointList(poly1);
        PointList pointList2 = PolyE.toPointList(poly2);
        return pointList1.equals(pointList2);
    }

    /**
     * 判断当前是否包含组合
     *
     * @param poly 检测组合
     * @return true包含
     */
    public static boolean contains(Poly poly) {
        if (poly == null)
            return false;
        if (poliesMap.size() == 0)
            return false;
        boolean contains = false;
        Iterator<Map.Entry<Integer, Poly>> iterator = poliesMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Poly> entry = iterator.next();
            Poly poly1 = entry.getValue();
            if (polyMatched(poly1, poly)) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    /**
     * 存储新的组合区域
     *
     * @param index 对应唯一编号
     * @param poly  数据
     * @return 返回该组合区域的下标位置
     */
    public static void put(int index, Poly poly) {
        if (poly == null)
            return;
        poliesMap.put(index, poly);
    }

    /**
     * 获取指定位置的组合数据
     *
     * @param index
     */
    public static Poly get(int index) {
        if (index < 0 || index >= poliesMap.size())
            return null;
        return poliesMap.get(index);
    }

    /**
     * 获取房间所在的组合区域
     *
     * @param house 指定闭合房间
     * @return 所在区域实体数据对象
     */
    public static Map.Entry<Integer, Poly> get(House house) {
        if (house == null || !house.isWallClosed || poliesMap.size() == 0)
            return null;
        Poly poly = PolyE.toPolyDefault(house.centerPointList);
        Iterator<Map.Entry<Integer, Poly>> iterator = poliesMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Poly> entry = iterator.next();
            Poly poly1 = entry.getValue();
            Poly ret = poly.intersection(poly1);
            if (ret != null && !ret.isEmpty()) {
                return entry;
            }
        }
        return null;
    }

    /**
     * 移除区域
     *
     * @param index
     */
    public static synchronized void removePoly(int index) {
        removePolies(new int[]{index});
    }

    /**
     * 移除区域
     *
     * @param indexs 需要移除的位置集合
     */
    public static synchronized void removePolies(int[] indexs) {
        if (indexs == null || indexs.length == 0 || poliesMap.size() == 0)
            return;
        HashMap<Integer, Poly> removedPoliesMap = new HashMap<>();
        Iterator<Map.Entry<Integer, Poly>> iterator = poliesMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Poly> entry = iterator.next();
            int index = entry.getKey();
            boolean removed = false;
            for (int i = 0; i < indexs.length; i++) {
                if (index == indexs[i]) {
                    removed = true;
                    break;
                }
            }
            if (!removed) {
                removedPoliesMap.put(index, entry.getValue());
            }
        }
        poliesMap = removedPoliesMap;
    }

    /**
     * 创建新的索引位置
     */
    public static int newCreateIndex() {
        index++;
        return index;
    }

    /**
     * 清空
     */
    public static void clear() {
        index = -1;
        poliesMap.clear();
    }

}
