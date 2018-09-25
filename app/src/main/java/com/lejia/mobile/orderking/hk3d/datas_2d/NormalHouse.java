package com.lejia.mobile.orderking.hk3d.datas_2d;

import android.content.Context;

import com.lejia.mobile.orderking.hk3d.classes.AuxiliaryLine;
import com.lejia.mobile.orderking.hk3d.classes.Line;
import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.classes.PointList;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/7/18 16:11
 * TODO: 普通房间
 */
public class NormalHouse extends House {

    private Line currentLine; // 当前绘制的线段

    public NormalHouse(Context context) {
        super(context);
    }

    public NormalHouse(Context context, boolean isLineSeg) {
        super(context, isLineSeg);
    }

    public NormalHouse(Context context, PointList center_pointList, int thickness) {
        super(context, center_pointList, thickness);
    }

    public NormalHouse(Context context, PointList center_pointList, int thickness, boolean isLineSeg) {
        super(context, center_pointList, thickness, isLineSeg);
    }

    public Point getDown() {
        return down;
    }

    public Point getUp() {
        return up;
    }

    public void setDown(Point down) {
        this.down = down;
    }

    public void setUp(Point up) {
        this.up = up;
        // 两点最小距离
        double dist = this.up.dist(down.x, down.y);
        if (dist < 24) {
            return;
        }
        // 检测倾斜矫正
        double poorX = Math.abs(this.up.x - down.x);
        double poorY = Math.abs(this.up.y - down.y);
        if (poorX < 50) {
            this.up.x = down.x;
        }
        if (poorY < 50) {
            this.up.y = down.y;
        }
        // 线建墙
        if (isLineSeg) {
            currentLine = new Line(down, this.up);
            ArrayList<Point> pointsList = new ArrayList<>();
            pointsList.add(down.copy());
            pointsList.add(this.up.copy());
            if (lineSeg == null) {
                lineSeg = new LineSeg(pointsList);
            } else {
                lineSeg.setPointsList(pointsList);
            }
            centerPointList = new PointList(pointsList);
            refreshRenderer();
        } else { // 普通建墙
            currentLine = new Line(down, this.up);
            currentLine.loadAuxiliaryArray();
            ArrayList<AuxiliaryLine> auxiliaryLinesList = currentLine.getAuxiliaryLineList();
            if (auxiliaryLinesList != null && auxiliaryLinesList.size() > 0) {
                centerPointList = new PointList(currentLine.toPointList());
                innerPointList = new PointList(auxiliaryLinesList.get(0).getPointList());
                outerPointList = new PointList(auxiliaryLinesList.get(1).getPointList());
                createRenderer();
                refreshRenderer();
            }
        }
    }

    /**
     * 设置中心点列表
     *
     * @param pointsList
     * @param close
     */
    public void setCenterPointList(ArrayList<Point> pointsList, boolean close) {
        if (pointsList == null || pointsList.size() < 2)
            return;
        centerPointList = new PointList(pointsList);
        // 线建墙
        if (isLineSeg) {
            if (lineSeg == null) {
                lineSeg = new LineSeg(centerPointList.getPointsList());
            } else {
                lineSeg.setPointsList(centerPointList.getPointsList());
            }
        } else { // 普通建墙
            if (!close) {
                ArrayList<Line> linesList = centerPointList.toNotClosedLineList();
                ArrayList<Point> innerPointsList = new ArrayList<>();
                ArrayList<Point> outerPointsList = new ArrayList<>();
                int size = linesList.size() - 1;
                for (int i = 0; i < size; i++) {
                    Line now = linesList.get(i);
                    Line next = linesList.get(i + 1);
                    now.loadAuxiliaryArray();
                    next.loadAuxiliaryArray();
                    ArrayList<AuxiliaryLine> nowList = now.getAuxiliaryLineList();
                    ArrayList<AuxiliaryLine> nextList = next.getAuxiliaryLineList();
                    if (i == 0) { // 第一面墙体
                        innerPointsList.add(nowList.get(0).up.copy());
                        outerPointsList.add(nowList.get(1).down.copy());
                    }
                    // 添加两条线段的边线相交点
                    Point inter0 = nowList.get(0).getAuxiliaryIntersectePoint(nextList.get(0));
                    Point inter1 = nowList.get(1).getAuxiliaryIntersectePoint(nextList.get(1));
                    innerPointsList.add(inter0);
                    outerPointsList.add(inter1);
                    if (i == size - 1) { // 最后一面墙
                        innerPointsList.add(nextList.get(0).down.copy());
                        outerPointsList.add(nextList.get(1).up.copy());
                    }
                }
                innerPointList = new PointList(innerPointsList);
                outerPointList = new PointList(outerPointsList);
            } else {
                innerPointList = new PointList(centerPointList.offsetList(false, 12));
                outerPointList = new PointList(centerPointList.offsetList(true, 12));
            }
            createRenderer();
        }
        refreshRenderer();
    }

    /**
     * 获取当前墙体线段的长度
     */
    public double getCurrentWallLength() {
        if (currentLine == null)
            return 0;
        return currentLine.getLength();
    }

    /**
     * 矩形精准设置宽高，方向只遵循按下点至弹起点的方向延伸
     *
     * @param xlong
     */
    public void accurateSet(int xlong) {
        if (down == null)
            return;
        // 检测获取起点
        Line line = new Line(down.copy(), up.copy());
        int distLength = xlong + thickness;
        ArrayList<Point> lepsList = PointList.getRotateLEPS(line.getAngle(), 2 * distLength, down.copy());
        Point newUp = null;
        for (int i = 0; i < lepsList.size(); i++) {
            Point point = lepsList.get(i);
            Line testLine = new Line(down.copy(), point);
            // 判断该点是否在原线段上或者原起点在该线段上
            if (testLine.getAdsorbPoint(up.x, up.y, 1d) != null || line.getAdsorbPoint(point.x, point.y, 1d) != null) {
                newUp = point;
                break;
            }
        }
        if (newUp == null)
            return;
        up = newUp;
        // 刷新数据显示
        setUp(up);
    }

    /**
     * 检测自身线段画墙是否无效
     *
     * @return true 无效去除
     */
    public boolean checkInvalid() {
        if (up == null || down == null)
            return true;
        // 两点最小距离
        double dist = up.dist(down);
        if (dist < 24) {
            return true;
        }
        return false;
    }

}
