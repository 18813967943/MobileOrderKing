package com.lejia.mobile.orderking.hk3d.classes;

import com.seisw.util.geom.Poly;
import com.seisw.util.geom.PolyDefault;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/7/17 18:14
 * TODO: GPC数据转换封装对象
 */
public class PolyE {

    /**
     * 点列表转PolyDefault
     *
     * @param pointsList 点集合对象
     * @return 返回GPC中的数据集对象
     */
    public static PolyDefault toPolyDefault(ArrayList<Point> pointsList) {
        return toPolyDefault(new PointList(pointsList));
    }

    /**
     * 点列表转PolyDefault
     *
     * @param pointList 点集合对象
     * @return 返回GPC中的数据集对象
     */
    public static PolyDefault toPolyDefault(PointList pointList) {
        if (pointList == null || pointList.invalid())
            return null;
        PolyDefault poly = new PolyDefault();
        ArrayList<Point> selfCenterPointList = pointList.getPointsList();
        for (Point point : selfCenterPointList) {
            poly.add(point.toPoint2D());
        }
        return poly;
    }

    /**
     * GPC数据集转围点列表
     *
     * @param poly GPC数据集
     * @return 围点列表
     */
    public static ArrayList<Point> toPointsList(Poly poly) {
        if (poly == null)
            return null;
        ArrayList<Point> pointsList = new ArrayList<>();
        for (int i = 0; i < poly.getNumPoints(); i++) {
            Point point = new Point();
            point.x = poly.getX(i);
            point.y = poly.getY(i);
            pointsList.add(point);
        }
        return pointsList;
    }

    /**
     * GPC数据集转围点列表
     *
     * @param poly GPC数据集
     * @return 围点列表对象
     */
    public static PointList toPointList(Poly poly) {
        if (poly == null || poly.isEmpty())
            return null;
        ArrayList<Point> pointsList = toPointsList(poly);
        if (pointsList == null)
            return null;
        return new PointList(pointsList);
    }

    /**
     * 将Poly中的内容进行近距离点、浮点偏差、重复点处理
     *
     * @param poly
     * @return
     */
    public static Poly filtrationPoly(Poly poly) {
        if (poly == null || poly.isEmpty())
            return null;
        Poly ret = null;
        try {
            ArrayList<Point> pointsList = PolyE.toPointsList(poly);
            ArrayList<Point> resultList = PointList.filtrationList(pointsList);
            ret = PolyE.toPolyDefault(resultList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 两个区域数据对齐
     *
     * @param target 对齐目标区域
     * @param chek   需要对齐的区域
     * @return 返回对齐结果
     */
    public static Poly simpleAlignPoly(Poly target, Poly chek) {
        if (target == null || target.isEmpty() || chek == null || chek.isEmpty())
            return null;
        Poly ret = null;
        try {
            boolean hasAligned = false;
            ArrayList<Point> pointsList = new ArrayList<>();
            for (int i = 0; i < chek.getNumPoints(); i++) {
                double x = chek.getX(i);
                double y = chek.getY(i);
                for (int j = 0; j < target.getNumPoints(); j++) {
                    double x1 = target.getX(j);
                    double y1 = target.getY(j);
                    // x对比
                    if (Math.abs(x1 - x) <= 0.5d) {
                        x = x1;
                    }
                    // y对比
                    if (Math.abs(y1 - y) <= 0.5d) {
                        y = y1;
                    }
                }
                Point point = new Point();
                point.x = x;
                point.y = y;
                pointsList.add(point);
            }
            ret = PolyE.toPolyDefault(pointsList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

}
