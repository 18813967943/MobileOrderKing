package com.lejia.mobile.orderking.hk3d.factory;

import com.lejia.mobile.orderking.hk3d.classes.IndexPoint;
import com.lejia.mobile.orderking.hk3d.classes.Line;
import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.classes.PointList;
import com.lejia.mobile.orderking.hk3d.classes.PolyE;
import com.seisw.util.geom.Poly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Author by HEKE
 *
 * @time 2018/8/14 9:35
 * TODO: 点拆分对象
 */
public class PointsSplitor {

    /**
     * 获取交点信息列表
     *
     * @param uncloseList       当前连接的线段
     * @param fixedTLpointsList 右最左边的最上面的点为起始点的列表
     * @param close             是否闭合房间
     * @return 返回两组数据相交结果
     */
    public static ArrayList<IndexPoint> getIndexPointList(ArrayList<Point> uncloseList, ArrayList<Point> fixedTLpointsList, boolean close) {
        if (uncloseList == null || uncloseList.size() == 0 || fixedTLpointsList == null || fixedTLpointsList.size() == 0)
            return null;
        PointList pointList = new PointList(fixedTLpointsList);
        ArrayList<Line> linesList = close ? pointList.toLineList() : pointList.toNotClosedLineList();
        // 获取两端点与闭合区域的交点位置
        ArrayList<IndexPoint> indexPointsList = new ArrayList<>();
        Point begain = uncloseList.get(0);
        Point end = uncloseList.get(uncloseList.size() - 1);
        for (int i = 0; i < fixedTLpointsList.size(); i++) { // 先检测是否点重合
            Point point = fixedTLpointsList.get(i);
            if (begain.equals(point)) {
                indexPointsList.add(new IndexPoint(i, point, true, true));
            }
            if (end.equals(point)) {
                indexPointsList.add(new IndexPoint(i, point, true, false));
            }
        }
        for (int i = 0; i < linesList.size(); i++) { // 检测线段上的吸附点
            Line line = linesList.get(i);
            Point point = line.getAdsorbPoint(begain.x, begain.y, 1.0d);
            if (point != null) {
                addCheck(new IndexPoint(i, point, false, true), indexPointsList);
            }
            point = line.getAdsorbPoint(end.x, end.y, 1.0d);
            if (point != null) {
                addCheck(new IndexPoint(i, point, false, false), indexPointsList);
            }
        }
        // 排序
        Collections.sort(indexPointsList, new Comparator<IndexPoint>() {
            @Override
            public int compare(IndexPoint o1, IndexPoint o2) {
                int index = Integer.compare(o1.index, o2.index);
                if (index != 0)
                    return index;
                return 0;
            }
        });
        if (indexPointsList.size() == 0)
            indexPointsList = null;
        return indexPointsList;
    }

    /**
     * 获取普通画墙墙体自身绘制闭合区域
     *
     * @param interPointsList 当前绘制的墙体点列表
     * @param checkPointsList 当前未闭合区域的点列表
     * @param checkSide       是否需要检测包含起点与重点组合方向
     * @return 相交切割后的区域对象
     */
    public static Poly splitSelfUnclosedArea(ArrayList<Point> interPointsList, ArrayList<Point> checkPointsList, boolean checkSide) {
        if (interPointsList == null || interPointsList.size() == 0 || checkPointsList == null || checkPointsList.size() == 0)
            return null;
        try {
            ArrayList<IndexPoint> indexPointsList = getIndexPointList(interPointsList, checkPointsList, checkSide);
            // 根据两点的编号开始切割分区
            IndexPoint begainIP = indexPointsList.get(0);
            IndexPoint endIP = indexPointsList.get(1);
            boolean isBegainMin = begainIP.isBegain;
            ArrayList<Point> copyList = null;
            if (isBegainMin) {
                copyList = new PointList(interPointsList).copy();
            } else {
                copyList = new ArrayList<>();
                for (int i = interPointsList.size() - 1; i > -1; i--) {
                    copyList.add(interPointsList.get(i).copy());
                }
            }
            ArrayList<Point> pointsList1 = new ArrayList<>();
            // 需要检测组合区域
            if (checkSide) {
                // 分别于切割组合，计算面积小的区域为切割出的区域
                ArrayList<Point> poly1List = new ArrayList<>();
                poly1List.addAll(copyList);
                ArrayList<Point> poly2List = new ArrayList<>();
                poly2List.addAll(copyList);
                // 切割组合
                // 区域1
                for (int i = endIP.index; begainIP.isSidePoint ? i >= begainIP.index : i > begainIP.index; i--) {
                    addCheck(checkPointsList.get(i).copy(), poly1List);
                }
                // 区域2
                for (int i = (endIP.isSidePoint ? endIP.index : endIP.index + 1); i < checkPointsList.size(); i++) {
                    addCheck(checkPointsList.get(i).copy(), poly2List);
                }
                for (int i = 0; i <= begainIP.index; i++) {
                    addCheck(checkPointsList.get(i).copy(), poly2List);
                }
                // 过滤
                Poly poly1 = PolyE.filtrationPoly(PolyE.toPolyDefault(poly1List));
                Poly poly2 = PolyE.filtrationPoly(PolyE.toPolyDefault(poly2List));
                PointList pointList1 = PolyE.toPointList(poly1);
                PointList pointList2 = PolyE.toPointList(poly2);
                pointsList1 = (pointList1.area() < pointList2.area()) ? pointList1.antiClockwise() : pointList2.antiClockwise();
            }
            // 无需检测组合区域，所属情况为自身断墙相交闭合
            else {
                pointsList1.addAll(copyList);
                for (int i = endIP.index; begainIP.isSidePoint ? i >= begainIP.index : i > begainIP.index; i--) {
                    addCheck(checkPointsList.get(i).copy(), pointsList1);
                }
            }
            if (pointsList1.size() == 0)
                return null;
            Poly poly1 = PolyE.filtrationPoly(PolyE.toPolyDefault(pointsList1));
            return poly1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 线段点区域与闭合围点区域分割
     *
     * @param linePoly
     * @param closePoly
     * @return
     */
    public static ArrayList<Poly> splitLineAreaWithCloseArea(Poly linePoly, Poly closePoly) {
        if (linePoly == null || linePoly.isEmpty() || closePoly == null || closePoly.isEmpty())
            return null;
        try {
            // 闭合区域转顺时针
            PointList pointList = PolyE.toPointList(closePoly);
            ArrayList<Point> pointsList = pointList.fixToLeftTopPointsList();
            pointList.setPointsList(pointsList);
            ArrayList<Line> linesList = pointList.toLineList();
            // 线段点
            ArrayList<Point> uncloseList = PolyE.toPointsList(linePoly);
            // 获取两端点与闭合区域的交点位置
            ArrayList<IndexPoint> indexPointsList = getIndexPointList(uncloseList, pointsList, true);
            if (indexPointsList == null || indexPointsList.size() < 2) {
                return null;
            }
            // 根据两点的编号开始切割分区
            IndexPoint begainIP = indexPointsList.get(0);
            IndexPoint endIP = indexPointsList.get(1);
            boolean isBegainMin = begainIP.isBegain;
            ArrayList<Point> copyList = null;
            if (isBegainMin) {
                copyList = new PointList(uncloseList).copy();
            } else {
                copyList = new ArrayList<>();
                for (int i = uncloseList.size() - 1; i > -1; i--) {
                    copyList.add(uncloseList.get(i).copy());
                }
            }
            // 区域1
            ArrayList<Point> pointsList1 = new ArrayList<>();
            pointsList1.addAll(copyList);
            for (int i = endIP.index; begainIP.isSidePoint ? i >= begainIP.index : i > begainIP.index; i--) {
                addCheck(pointsList.get(i).copy(), pointsList1);
            }
            Poly poly1 = PolyE.filtrationPoly(PolyE.toPolyDefault(pointsList1));
            // 区域2
            ArrayList<Point> pointsList2 = new ArrayList<>();
            pointsList2.addAll(new PointList(copyList).copy());
            for (int i = (endIP.isSidePoint ? endIP.index : endIP.index + 1); i < pointsList.size(); i++) {
                addCheck(pointsList.get(i).copy(), pointsList2);
            }
            for (int i = 0; i <= begainIP.index; i++) {
                addCheck(pointsList.get(i).copy(), pointsList2);
            }
            Poly poly2 = PolyE.filtrationPoly(PolyE.toPolyDefault(pointsList2));
            // 返回数据列表
            ArrayList<Poly> poliesList = new ArrayList<>();
            poliesList.add(poly1);
            poliesList.add(poly2);
            return poliesList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 增加相交点检测
     *
     * @param point
     * @param pointsList
     */
    private static void addCheck(Point point, ArrayList<Point> pointsList) {
        if (point == null || pointsList == null)
            return;
        if (pointsList.size() == 0)
            pointsList.add(point);
        else {
            boolean existed = false;
            for (Point point1 : pointsList) {
                if (point.equals(point1)) {
                    existed = true;
                    break;
                }
            }
            if (!existed)
                pointsList.add(point);
        }
    }

    /**
     * 增加相交索引点并检测
     *
     * @param indexPoint
     * @param checkList
     */
    private static void addCheck(IndexPoint indexPoint, ArrayList<IndexPoint> checkList) {
        if (indexPoint == null)
            return;
        if (checkList.size() == 0)
            checkList.add(indexPoint);
        else {
            boolean notExisted = false;
            for (IndexPoint ip : checkList) {
                if (ip.point.equals(indexPoint.point)) {
                    notExisted = true;
                    break;
                }
            }
            if (!notExisted) {
                checkList.add(indexPoint);
            }
        }
    }

    /**
     * 组合两个未闭合房间，只端点相交的情况下
     *
     * @param selfPointsList 自身围点列表
     * @param interList      相交围点列表
     * @return 组合后的围点列表
     */
    public static ArrayList<Point> polyUncloseHouses(ArrayList<Point> selfPointsList, ArrayList<Point> interList) {
        if (selfPointsList == null || selfPointsList.size() == 0 || interList == null || interList.size() == 0)
            return null;
        ArrayList<Point> polyList = new ArrayList<>();
        try {
            Point selfBegagin = selfPointsList.get(0);
            Point selfEnd = selfPointsList.get(selfPointsList.size() - 1);
            Point interBegain = interList.get(0);
            Point interEnd = interList.get(interList.size() - 1);
            // 起始点与起始点相交
            if (selfBegagin.equals(interBegain)) {
                ArrayList<Point> pointsList = new ArrayList<>();
                for (int i = selfPointsList.size() - 1; i > 0; i--) {
                    pointsList.add(selfPointsList.get(i).copy());
                }
                pointsList.addAll(new PointList(interList).copy());
                return pointsList;
            }
            // 起始点与终点相交
            if (selfBegagin.equals(interEnd)) {
                ArrayList<Point> pointsList = new ArrayList<>();
                pointsList.addAll(new PointList(interList).copy());
                for (int i = 1; i < selfPointsList.size(); i++) {
                    pointsList.add(selfPointsList.get(i).copy());
                }
                return pointsList;
            }
            // 终点与起始点相交
            if (selfEnd.equals(interBegain)) {
                ArrayList<Point> pointsList = new ArrayList<>();
                int nsize = selfPointsList.size() - 1;
                for (int i = 0; i < nsize; i++) {
                    pointsList.add(selfPointsList.get(i).copy());
                }
                pointsList.addAll(new PointList(interList).copy());
                return pointsList;
            }
            // 终点与终点相交
            if (selfEnd.equals(interEnd)) {
                ArrayList<Point> pointsList = new ArrayList<>();
                pointsList.addAll(new PointList(selfPointsList).copy());
                int isize = interList.size() - 1;
                for (int i = isize - 1; i > -1; i--) {
                    pointsList.add(interList.get(i).copy());
                }
                return pointsList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return polyList;
    }

}
