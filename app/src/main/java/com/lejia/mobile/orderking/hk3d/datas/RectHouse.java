package com.lejia.mobile.orderking.hk3d.datas;

import android.content.Context;

import com.lejia.mobile.orderking.bases.OrderKingApplication;
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

    private Point down; // 按下点
    private Point up; // 弹起点

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
        if (up == null) {
            up = new Point(x, y);
        } else {
            up.setXY(x, y);
        }
        createLists();
        ((OrderKingApplication) getContext().getApplicationContext()).render(); // refresh render contents
    }

    /**
     * TODO 创建围点信息
     */
    private void createLists() {
        if (down == null || up == null)
            return;
        // 面积小于1个平方米，视为无效
        double dist = up.dist(down);
        if (dist < 1.414d)
            return;
        // 创建围点列表
        double poorX = Math.abs(up.x - down.x);
        double poorY = Math.abs(up.y - down.y);
        Point center = new Point((down.x + up.x) / 2, (down.y + up.y) / 2);
        ArrayList<Point> centerPointsList = PointList.getRotateVertexs(0d, poorY, poorX, center);
        centerPointList = createList(centerPointList, centerPointsList);
        // 内外围点列表
        ArrayList<Point> innerPointsList = centerPointList.offsetList(false, 12);
        innerPointList = createList(innerPointList, innerPointsList);
        ArrayList<Point> outerPointsList = centerPointList.offsetList(true, 12);
        outerPointList = createList(outerPointList, outerPointsList);
        createRenderer();
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

    /**
     * TODO 创建绘制对象
     */
    private void createRenderer() {
        wallsList.clear();
        int size = innerPointList.size();
        for (int i = 0; i < size; i++) {
            Point inow = innerPointList.getIndexAt(i);
            Point onow = outerPointList.getIndexAt(i);
            Point inext = null;
            Point onext = null;
            if (i == size - 1) {
                inext = innerPointList.getIndexAt(0);
                onext = outerPointList.getIndexAt(0);
            } else {
                inext = innerPointList.getIndexAt(i + 1);
                onext = outerPointList.getIndexAt(i + 1);
            }
            ArrayList<Point> pointsList = new ArrayList<>();
            pointsList.add(onow);
            pointsList.add(onext);
            pointsList.add(inext);
            pointsList.add(inow);
            PointList pointList = new PointList(pointsList);
            pointsList = pointList.antiClockwise();
            Wall wall = new Wall(pointsList);
            wallsList.add(wall);
        }
        ground = new Ground(innerPointList);
    }

    @Override
    public void render(int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition) {
        try {
            for (Wall wall : wallsList) {
                wall.render(positionAttribute, normalAttribute, colorAttribute, onlyPosition);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // ground.render(positionAttribute, normalAttribute, colorAttribute, onlyPosition);
    }

}
