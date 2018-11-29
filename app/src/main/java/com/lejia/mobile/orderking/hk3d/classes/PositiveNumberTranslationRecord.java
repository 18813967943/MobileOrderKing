package com.lejia.mobile.orderking.hk3d.classes;

import android.support.annotation.NonNull;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/11/22 14:53
 * TODO: 用于围点列表进行正数值平移操作的数据生成记录对象
 */
public class PositiveNumberTranslationRecord {

    public double transX; // X轴偏移
    public double transY; // Y轴偏移

    public ArrayList<Point> pointsList; // 偏移前围点列表
    public ArrayList<Point> offsetPointsList; // 偏移后围点列表

    public PositiveNumberTranslationRecord(@NonNull ArrayList<Point> pointsList) {
        this.pointsList = pointsList;
        init();
    }

    private void init() {
        if (pointsList == null || pointsList.size() == 0)
            return;
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        for (Point point : pointsList) {
            if (point.x < minX)
                minX = point.x;
            if (point.y < minY)
                minY = point.y;
        }
        transX = -minX;
        transY = -minY;
        offsetPointsList = new ArrayList<>();
        for (Point point : pointsList) {
            Point op = new Point();
            op.x = point.x + transX;
            op.y = point.y + transY;
            offsetPointsList.add(op);
        }
    }

    /**
     * 回移点
     *
     * @param point 平移后的点
     * @return 回移后的点
     */
    public Point reversePoint(Point point) {
        if (point == null)
            return null;
        Point bp = new Point();
        bp.x = point.x - transX;
        bp.y = point.y = transY;
        return bp;
    }

    /**
     * 回移围点列表
     *
     * @param pointsList 偏移处理后的围点
     * @return 平移回去的围点
     */
    public ArrayList<Point> reverseList(ArrayList<Point> pointsList) {
        if (pointsList == null || pointsList.size() == 0)
            return null;
        ArrayList<Point> backList = new ArrayList<>();
        for (Point point : pointsList) {
            Point bp = new Point();
            bp.x = point.x - transX;
            bp.y = point.y - transY;
            backList.add(bp);
        }
        return backList;
    }

    /**
     * 以此相同规则平移其他围点
     *
     * @param pointsList 需要偏移的围点列表
     * @return 返回列表
     */
    public ArrayList<Point> doList(ArrayList<Point> pointsList) {
        if (pointsList == null || pointsList.size() == 0)
            return null;
        ArrayList<Point> doList = new ArrayList<>();
        for (Point point : pointsList) {
            Point bp = new Point();
            bp.x = point.x + transX;
            bp.y = point.y + transY;
            doList.add(bp);
        }
        return doList;
    }

}
