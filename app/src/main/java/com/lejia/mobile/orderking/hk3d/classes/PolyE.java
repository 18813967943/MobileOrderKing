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
            Point point = new Point(poly.getX(i), poly.getY(i));
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
        ArrayList<Point> pointsList = toPointsList(poly);
        if (pointsList == null)
            return null;
        return new PointList(pointsList);
    }

}
