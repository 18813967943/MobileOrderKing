package com.lejia.mobile.orderking.hk3d.datas;

import android.content.Context;

import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.classes.PointList;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/7/16 12:05
 * TODO:  矩形画房间
 */
public class RectHouse extends House {

    public double width; // 宽度
    public double height; // 高度

    public RectHouse(Context context) {
        super(context);
    }

    public Point getDown() {
        return down;
    }

    public void setDown(Point down) {
        this.down = down;
    }

    public Point getUp() {
        return up;
    }

    public void setUp(double x, double y) {
        // 设置起点
        if (up == null) {
            up = new Point(x, y);
        } else {
            up.setXY(x, y);
        }
        createLists();
        refreshRenderer();
    }

    /**
     * TODO 创建围点信息
     */
    private void createLists() {
        try {
            if (down == null || up == null)
                return;
            // 面积小于1个平方米，视为无效
            double dist = up.dist(down);
            if (dist < 1.414d)
                return;
            // 矩形画墙默认都为闭合
            isWallClosed = true;
            // 创建围点列表
            double poorX = Math.abs(up.x - down.x);
            double poorY = Math.abs(up.y - down.y);
            width = poorX;
            height = poorY;
            Point center = new Point((down.x + up.x) / 2, (down.y + up.y) / 2);
            ArrayList<Point> centerPointsList = PointList.getRotateVertexs(0d, poorY, poorX, center);
            centerPointList = createList(centerPointList, centerPointsList);
            // 内外围点列表
            ArrayList<Point> innerPointsList = centerPointList.offsetList(false, 12);
            innerPointList = createList(innerPointList, innerPointsList);
            ArrayList<Point> outerPointsList = centerPointList.offsetList(true, 12);
            outerPointList = createList(outerPointList, outerPointsList);
            createRenderer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 宽或者高不符合要求
     */
    public boolean isSizeInvalid() {
        return width < 75 || height < 75;
    }

    /**
     * 列表创建及设置
     *
     * @param pointList
     * @param cententList
     */
    private PointList createList(PointList pointList, ArrayList<Point> cententList) {
        if (pointList == null)
            pointList = new PointList(cententList);
        else
            pointList.setPointsList(cententList);
        return pointList;
    }

}
