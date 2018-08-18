package com.lejia.mobile.orderking.hk3d.classes;

import com.lejia.mobile.orderking.hk3d.datas.House;
import com.seisw.util.geom.Poly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Author by HEKE
 *
 * @time 2018/7/18 10:07
 * TODO: 所有闭合房间区域组合管理对象
 */
public class PolyM {

    private static int index = -1; // 下标，组合编号

    /**
     * 当前所有组合区域结果列表
     */
    private static HashMap<Integer, Poly> poliesMap = new HashMap<>();

    /**
     * 闭合区间数量大小
     */
    public static int size() {
        return poliesMap.size();
    }

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
        if (poly == null || poly.isEmpty())
            return;
        if (poliesMap.size() > 0) {
            // 检测所有合并
            boolean unioned = false;
            ArrayList<PolyUnionResult> polyUnionResultList = new ArrayList<>();
            Iterator<Map.Entry<Integer, Poly>> iterator = poliesMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, Poly> entry = iterator.next();
                Poly poly1 = entry.getValue();
                Poly unionPoly = poly.union(poly1);
                if (unionPoly != null && !unionPoly.isEmpty() && unionPoly.getNumInnerPoly() == 1) {
                    unioned = true;
                    polyUnionResultList.add(new PolyUnionResult(entry, unionPoly));
                }
            }
            // 非联合，加入
            if (!unioned) {
                poliesMap.put(index, poly);
            } else {
                // 组合区域处理
                reduceIndex();
                // 遍历重新组合
                Poly resultPoly = null;
                int[] removeIndexs = new int[polyUnionResultList.size()];
                int count = 0;
                for (PolyUnionResult result : polyUnionResultList) {
                    removeIndexs[count] = result.entry.getKey();
                    if (resultPoly == null) {
                        resultPoly = PolyE.filtrationPoly(poly.union(result.entry.getValue()));
                    } else {
                        resultPoly = PolyE.filtrationPoly(resultPoly.union(result.entry.getValue()));
                    }
                    count++;
                    result.release();
                }
                removePolies(removeIndexs);
                poliesMap.put(removeIndexs[0], PolyE.filtrationPoly(resultPoly));
            }
        } else {
            poliesMap.put(index, poly);
        }
        // 打印测试结果
        if (poliesMap.size() > 0) {
            Iterator<Map.Entry<Integer, Poly>> iterator = poliesMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, Poly> entry = iterator.next();
                System.out.println("#### Poly : " + entry.getKey() + "  V : " + PolyE.toPointList(entry.getValue()));
            }
        }
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
     * 获取当前全部组合区域数据
     */
    public static HashMap<Integer, Poly> getPoliesMap() {
        return poliesMap;
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
     * 获取区域所在组合区域
     *
     * @param pointList 围点列表
     * @return 返回围点列表所在区域
     */
    public static Poly getPolyAtPoly(PointList pointList) {
        if (pointList == null || poliesMap.size() == 0)
            return null;
        try {
            ArrayList<Point> selfList = pointList.getPointsList();
            Iterator<Map.Entry<Integer, Poly>> iterator = poliesMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, Poly> polyEntry = iterator.next();
                Poly poly = polyEntry.getValue();
                ArrayList<Point> pointsList = PolyE.toPointsList(poly);
                int count = 0;
                for (Point point : selfList) {
                    boolean on = PointList.pointRelationToPolygon(pointsList, point) != -1;
                    if (on) {
                        count++;
                        if (count > 1) {
                            return poly;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建新的索引位置
     */
    public static int newCreateIndex() {
        index++;
        return index;
    }

    // 编号回减
    private static void reduceIndex() {
        index--;
    }

    /**
     * 清空
     */
    public static void clear() {
        index = -1;
        poliesMap.clear();
    }

}
