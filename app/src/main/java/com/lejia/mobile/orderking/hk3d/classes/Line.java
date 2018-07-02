package com.lejia.mobile.orderking.hk3d.classes;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/6/23 12:12
 * TODO: 线段对象
 */
public class Line {

    public Point down; // 按下点(起始点)
    public Point up; // 弹起点(结尾点)
    public double thickess; // 厚度

    public Line(Point down, Point up) {
        this.down = down;
        this.up = up;
        this.thickess = 24.0d;
    }

    /**
     * 判断是否无效线段
     */
    public boolean invalid() {
        return down == null || up == null || down.equals(up);
    }

    /**
     * 获取厚度
     */
    public double getThickess() {
        return thickess;
    }

    /**
     * 设置厚度
     *
     * @param thickess
     */
    public void setThickess(double thickess) {
        this.thickess = thickess;
        // TODO 计算自身两端线段

    }

    /**
     * 获取线段与水平线形成的角度,范围0-360
     */
    public double getAngle() {
        if (invalid())
            return 0d;
        double x = up.x - down.x;
        double y = up.y - down.y;
        double angle = Math.atan2(y, x) * 180.0d / Math.PI;
        if (angle < 0) {
            angle += 360;
        }
        return angle;
    }

    /**
     * 获取线段的长度
     */
    public double getLength() {
        if (invalid())
            return 0d;
        double x = up.x - down.x;
        double y = up.y - down.y;
        double dist = Math.sqrt(x * x + y * y);
        return dist;
    }

    /**
     * 获取中心点
     */
    public Point getCenter() {
        if (invalid())
            return null;
        return new Point((down.x + up.x) / 2, (down.y + up.y) / 2);
    }

    /**
     * 反转线段方向
     */
    public Line reverser() {
        if (invalid())
            return null;
        return new Line(up.copy(), down.copy());
    }

    /**
     * 复制线段
     */
    public Line copy() {
        if (invalid())
            return null;
        return new Line(down.copy(), up.copy());
    }

    /**
     * 求两线段交点
     *
     * @param line
     * @return
     */
    public Point getLineIntersectedPoint(Line line) {
        if (invalid() || line == null || line.invalid())
            return null;
        Point point = null;
        try {
            Point p1 = down;
            Point p2 = up;
            Point q1 = line.down;
            Point q2 = line.up;
            double tol = 0.00001d;
            double ua = (q2.x - q1.x) * (p1.y - q1.y) - (q2.y - q1.y) * (p1.x - q1.x);
            ua /= (q2.y - q1.y) * (p2.x - p1.x) - (q2.x - q1.x) * (p2.y - p1.y);
            double ub = (p2.x - p1.x) * (p1.y - q1.y) - (p2.y - p1.y) * (p1.x - q1.x);
            ub /= (q2.y - q1.y) * (p2.x - p1.x) - (q2.x - q1.x) * (p2.y - p1.y);
            if (ua > -tol && ua < 1 + tol && ub > -tol && ub < 1 + tol) {
                double atX = p1.x + ua * (p2.x - p1.x);
                double atY = p1.y + ua * (p2.y - p1.y);
                point = new Point(atX, atY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return point;
    }

    /**
     * 供外部调用获取两条线段的相交点
     *
     * @param L1
     * @param L2
     * @return
     */
    public static Point getLineIntersectedPoint(Line L1, Line L2) {
        if (L1 == null || L1.invalid() || L2 == null || L2.invalid())
            return null;
        return L1.getLineIntersectedPoint(L2);
    }

    /**
     * 判断触摸点是否在线段上
     *
     * @param x
     * @param y
     * @return
     */
    public boolean isTouchOnLine(double x, double y) {
        if (invalid())
            return false;
        Point point = new Point(x, y);
        ArrayList<Point> pointsList = PointList.getRotateVertexs(getAngle(), getThickess(), getLength(), point);
        return PointList.pointRelationToPolygon(pointsList, point) != -1;
    }

    /**
     * 获取某个点与此线段是否是吸附关系，并返回吸附点
     *
     * @param x
     * @param y
     * @return
     */
    public Point getAdsorbPoint(double x, double y) {
        if (invalid())
            return null;
        Point point = new Point(x, y);
        ArrayList<Point> pointsList = PointList.getRotateVertexs(getAngle(), 2d * getThickess(), getLength(), point);
        boolean invalid = PointList.pointRelationToPolygon(pointsList, point) == -1;
        if (invalid)
            return null;
        Point map = null;
        try {
            ArrayList<Point> lepsList = PointList.getRotateLEPS(getAngle() + 90d, 5d * getThickess(), point);
            Line lepsLine = new Line(lepsList.get(1), lepsList.get(0));
            map = lepsLine.getLineIntersectedPoint(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 判断线段是否共线
     *
     * @param line
     * @return
     */
    public boolean isCollineation(Line line) {
        if (line == null || line.invalid() || invalid())
            return false;
        // 角度相等或与反转方向的线段角度相等，即表示两线段平行
        Line reverserLine = line.reverser();
        double selfAngle = getAngle();
        if (line.getAngle() == selfAngle || reverserLine.getAngle() == selfAngle) {
            // 两线段有交点，即表示两线段共线
            return line.getLineIntersectedPoint(this) != null;
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Line)) {
            return false;
        }
        Line other = (Line) obj;
        if (invalid() || other.invalid())
            return false;
        boolean matched = (down.equals(other.down) || down.equals(other.up)) && (up.equals(other.down) || up.equals(other.up));
        if (matched)
            return true;
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return thickess + "," + down + "," + up;
    }

}
