package com.lejia.mobile.orderking.hk3d.classes;

/**
 * Author by HEKE
 *
 * @time 2018/6/23 12:12
 * TODO: 线段对象
 */
public class Line {

    public Point down;
    public Point up;

    public Line(Point down, Point up) {
        this.down = down;
        this.up = up;
    }

    /**
     * 判断是否无效线段
     */
    public boolean invalid() {
        return down == null || up == null;
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


}
