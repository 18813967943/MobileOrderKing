package com.lejia.mobile.orderking.hk3d.classes;

import android.graphics.Path;
import android.os.Parcel;
import android.os.Parcelable;

import com.seisw.util.geom.Point2D;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Author by HEKE
 *
 * @time 2018/6/22 17:18
 * TODO: 点列表处理对象
 */
public class PointList implements Parcelable {

    /**
     * 相连的围点列表
     */
    private ArrayList<Point> pointsList;

    /**
     * 顺时针方向
     */
    private boolean clockwise;

    public PointList(ArrayList<Point> pointsList) {
        this.pointsList = pointsList;
    }

    protected PointList(Parcel in) {
        pointsList = in.createTypedArrayList(Point.CREATOR);
        clockwise = in.readByte() != 0;
    }

    /**
     * 判断数据内容是否有效
     */
    public boolean invalid() {
        return pointsList == null || pointsList.size() == 0;
    }

    /**
     * 获取具体数据列表
     */
    public ArrayList<Point> getPointsList() {
        return pointsList;
    }

    /**
     * 设置具体数据列表
     *
     * @param pointsList
     */
    public void setPointsList(ArrayList<Point> pointsList) {
        this.pointsList = pointsList;
    }

    /**
     * 设置具体数据列表
     *
     * @param array
     */
    public void setPointsList(Point[] array) {
        this.pointsList = toList(array);
    }

    /**
     * 判断围点组合是否是顺时针方向
     */
    public boolean isClockwise() {
        area();
        return clockwise;
    }

    /**
     * 转化为gpc点列表
     */
    public ArrayList<Point2D> toPoint2DList() {
        if (invalid())
            return null;
        ArrayList<Point2D> point2DSList = new ArrayList<>();
        for (Point point : pointsList) {
            point2DSList.add(point.toPoint2D());
        }
        return point2DSList;
    }

    /**
     * 复制列表
     */
    public ArrayList<Point> copy() {
        if (invalid())
            return null;
        ArrayList<Point> copyList = new ArrayList<>();
        for (Point point : pointsList) {
            copyList.add(point.copy());
        }
        return copyList;
    }

    /**
     * 按照 X 轴坐标大小进行排序
     */
    public ArrayList<Point> sortX() {
        if (invalid())
            return null;
        ArrayList<Point> copyList = copy();
        if (copyList == null)
            return null;
        Collections.sort(copyList, new Comparator<Point>() {
            @Override
            public int compare(Point point, Point t1) {
                int x = Integer.compare((int) point.x, (int) t1.x);
                if (x != 0)
                    return x;
                return 0;
            }
        });
        return copyList;
    }

    /**
     * 按照 Y 轴坐标大小进行排序
     */
    public ArrayList<Point> sortY() {
        if (invalid())
            return null;
        ArrayList<Point> copyList = copy();
        if (copyList == null)
            return null;
        Collections.sort(copyList, new Comparator<Point>() {
            @Override
            public int compare(Point point, Point t1) {
                int y = Integer.compare((int) point.y, (int) t1.y);
                if (y != 0)
                    return y;
                return 0;
            }
        });
        return copyList;
    }

    /**
     * 获取外部矩形
     *
     * @return
     */
    public RectD getRectBox() {
        if (invalid())
            return null;
        int size = pointsList.size();
        if (size == 0)
            return null;
        RectD rectD = new RectD();
        double minX = Float.MAX_VALUE;
        double maxX = Float.MIN_VALUE;
        double minY = Float.MAX_VALUE;
        double maxY = Float.MIN_VALUE;
        try {
            for (Point point : pointsList) {
                if (point.x < minX) {
                    minX = point.x;
                }
                if (point.y < minY) {
                    minY = point.y;
                }
                if (point.x > maxX) {
                    maxX = point.x;
                }
                if (point.y > maxY) {
                    maxY = point.y;
                }
            }
            rectD.left = minX;
            rectD.top = minY;
            rectD.right = maxX;
            rectD.bottom = maxY;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rectD;
    }

    /**
     * 区域面积，单位平方米
     *
     * @return
     */
    public double area() {
        double area = 0f;
        try {
            if (invalid()) {
                return 0f;
            }
            int size = pointsList.size();
            if (size == 0 || size < 3) {
                return 0f;
            }
            // 计算面积
            int i = 1;
            for (; i < size; ++i) {
                Point before = pointsList.get(i - 1);
                Point now = pointsList.get(i);
                area += (new BigDecimal(before.x * now.y - now.x * before.y).doubleValue());
            }
            // 起始点与终点
            Point last = pointsList.get(size - 1);
            Point start = pointsList.get(0);
            area += (new BigDecimal(last.x * start.y - start.x * last.y).doubleValue());
            // 顺逆时针判断
            double dirArea = area * 0.1d;
            clockwise = dirArea > 0;
            // 面积数值处理,100px等于1米，这里除以10000
            area = new BigDecimal(0.5d * Math.abs(area)).doubleValue();
            area = Point.percision(new BigDecimal((area / 10000d)).doubleValue(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return area;
    }

    /**
     * 转换成数组
     *
     * @return
     */
    public Point[] toArray() {
        if (invalid())
            return null;
        Point[] array = new Point[pointsList.size()];
        int count = 0;
        for (Point point : pointsList) {
            array[count] = point;
            count++;
        }
        return array;
    }

    /**
     * 转换成列表
     *
     * @param array
     * @return
     */
    public ArrayList<Point> toList(Point[] array) {
        if (array == null || array.length == 0)
            return null;
        ArrayList<Point> list = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            list.add(array[i]);
        }
        return list;
    }

    /**
     * 获取组合路径
     *
     * @param close
     * @return
     */
    public Path getPath(boolean close) {
        if (invalid())
            return null;
        int size = pointsList.size();
        if (size == 0)
            return null;
        Path path = new Path();
        path.reset();
        for (int i = 0; i < size; i++) {
            Point p = pointsList.get(i);
            if (i == 0) {
                path.moveTo((float) p.x, (float) p.y);
            } else {
                path.lineTo((float) p.x, (float) p.y);
            }
        }
        if (close)
            path.close();
        return path;
    }

    /**
     * 围点矫正顺序为顺时针左起第一个点
     */
    public ArrayList<Point> fixToLeftTopPointsList() {
        if (invalid())
            return null;
        ArrayList<Point> fixedList = new ArrayList<>();
        try {
            // 计算最左边的点集合
            ArrayList<IndexPoint> leftList = new ArrayList<>();
            double minX = Double.MAX_VALUE;
            for (Point p : pointsList) {
                if (p.x <= minX)
                    minX = p.x;
            }
            for (int i = 0; i < pointsList.size(); i++) {
                Point p = pointsList.get(i);
                if (p.x == minX)
                    leftList.add(new IndexPoint(i, p));
            }
            // 左上角点的索引位置
            int leftTopIndex = -1;
            double minY = Double.MAX_VALUE;
            for (IndexPoint ip : leftList) {
                if (ip.point.y <= minY) {
                    leftTopIndex = ip.index;
                    minY = ip.point.y;
                }
            }
            boolean clockwise = isClockwise();
            if (leftTopIndex == 0) {
                // 判断是否是逆时针
                if (!clockwise) {
                    fixedList.add(pointsList.get(0));
                    // 倒序转化为顺时针
                    for (int i = pointsList.size() - 1; i > 0; i--) {
                        fixedList.add(pointsList.get(i));
                    }
                } else {
                    return pointsList;
                }
            } else {
                // 顺时针
                if (clockwise) {
                    for (int i = leftTopIndex; i < pointsList.size(); i++) {
                        fixedList.add(pointsList.get(i));
                    }
                    for (int i = 0; i < leftTopIndex; i++) {
                        fixedList.add(pointsList.get(i));
                    }
                }
                // 逆时针
                else {
                    for (int i = leftTopIndex; i > -1; i--) {
                        fixedList.add(pointsList.get(i));
                    }
                    for (int i = pointsList.size() - 1; i > leftTopIndex; i--) {
                        fixedList.add(pointsList.get(i));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fixedList;
    }

    /**
     * 盒子转列表
     *
     * @param box
     * @return
     */
    public static ArrayList<Point> boxToList(RectD box) {
        if (box == null)
            return null;
        ArrayList<Point> pointsList = new ArrayList<>();
        pointsList.add(new Point(box.left, box.top));
        pointsList.add(new Point(box.right, box.top));
        pointsList.add(new Point(box.right, box.bottom));
        pointsList.add(new Point(box.left, box.bottom));
        return pointsList;
    }

    /**
     * 静态匹配两个区域围点数据是否相同
     *
     * @param area1
     * @param area2
     * @return
     */
    public static boolean isPointListMatched(ArrayList<Point> area1, ArrayList<Point> area2) {
        if (area1 == null || area1.size() == 0 || area2 == null || area2.size() == 0)
            return false;
        PointList pointList1 = new PointList(area1);
        PointList pointList2 = new PointList(area2);
        return pointList1.equals(pointList2);
    }

    /**
     * 判断点是否在不规则多边形内(不支持自身多条线相交的情况)
     *
     * @param points
     * @param point
     * @return -1外部，0边界，1内部
     */
    public static int pointRelationToPolygon(ArrayList<Point> points, Point point) {
        try {
            // 无效点列表
            PointList pointList = new PointList(points);
            if (pointList.invalid()) {
                return -1;
            }
            RectD rect = pointList.getRectBox();
            // 判断点是否在外部
            if (point.x < rect.left || point.x > rect.right || point.y < rect.top || point.y > rect.bottom) {
                return -1;
            }
            // 新建数据列表
            ArrayList<Point> tempList = new ArrayList<Point>();
            int size = points.size();
            for (int i = 0; i < size; i++) {
                tempList.add(new Point(points.get(i).x, points.get(i).y));
            }
            // 换算变量
            int sum = 0, t1, t2, sz;
            boolean b;
            double f;
            sz = tempList.size();
            // 换算向量之差
            for (int i = 0; i < sz; ++i) {
                Point tp = tempList.get(i);
                tp.x = (tp.x - point.x);
                tp.y = (tp.y - point.y);
            }
            // 末尾添加起始点
            tempList.add(tempList.get(0));
            b = tempList.get(0).y >= 0;
            t1 = (tempList.get(0).x >= 0) ? (b ? 0 : 3) : (b ? 1 : 2);
            for (int i = 1; i <= sz; i++) {
                Point current = tempList.get(i);
                Point before = tempList.get(i - 1);
                // 顶点
                if (current.x == 0 && current.y == 0)
                    return 0;
                f = current.y * before.x - current.x * before.y;
                if (f == 0 && 0 >= current.x * before.x && 0 >= current.y * before.y) {
                    return 0; // 边上
                }
                b = current.y >= 0;
                t2 = (current.x >= 0) ? (b ? 0 : 3) : (b ? 1 : 2);
                if (t2 == (t1 + 1) % 4)
                    sum += 1;
                else if (t2 == (t1 + 3) % 4)
                    sum -= 1;
                else if (t2 == (t1 + 2) % 4) {
                    if (f > 0)
                        sum += 2;
                    else
                        sum -= 2;
                }
                t1 = t2;
            }
            // 内部
            if (sum != 0) {
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 根据模型中心点换算模型外包立方体的所有顶点
     *
     * @param adsorbAngle
     * @param thickness
     * @param xlong
     * @param point
     * @return
     */
    public static ArrayList<Point> getRotateVertexs(double adsorbAngle, double thickness, double xlong, Point point) {
        ArrayList<Point> result = new ArrayList<>();
        try {
            // 一半宽与半墙厚、角度
            double halfDoorWidth = xlong * 0.5f;
            double halfThickness = thickness * 0.5f;
            // 根据与水平线形成的夹角，计算在同线上的两个线上点
            double x = Math.cos(Math.toRadians(adsorbAngle)) * halfDoorWidth;
            double y = Math.sin(Math.toRadians(adsorbAngle)) * halfDoorWidth;
            Point p1 = new Point(point.x + x, point.y + y);
            Point p2 = new Point(point.x - x, point.y - y);
            // 计算四个顶点
            x = Math.sin(Math.toRadians(adsorbAngle)) * halfThickness;
            y = Math.cos(Math.toRadians(adsorbAngle)) * halfThickness;
            result.add(new Point(p1.x + x, p1.y - y));
            result.add(new Point(p2.x + x, p2.y - y));
            result.add(new Point(p2.x - x, p2.y + y));
            result.add(new Point(p1.x - x, p1.y + y));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (result.size() == 0)
                result = null;
        }
        return result;
    }

    /**
     * 根据点、所在线段角度，获取所在线段的指定长度的两个端点
     *
     * @param adsorbAngle
     * @param adsorbPoint
     * @return
     */
    public static ArrayList<Point> getRotateLEPS(double adsorbAngle, double length, Point adsorbPoint) {
        ArrayList<Point> result = new ArrayList<>();
        try {
            // 一半宽与半墙厚、角度
            double halfLength = length * 0.5d;
            // 根据与水平线形成的夹角，计算在同线上的两个线上点
            double x = Math.cos(Math.toRadians(adsorbAngle)) * halfLength;
            double y = Math.sin(Math.toRadians(adsorbAngle)) * halfLength;
            Point p1 = new Point(adsorbPoint.x + x, adsorbPoint.y + y);
            Point p2 = new Point(adsorbPoint.x - x, adsorbPoint.y - y);
            result.add(p1);
            result.add(p2);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (result.size() == 0)
                result = null;
        }
        return result;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(pointsList);
        dest.writeByte((byte) (clockwise ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PointList> CREATOR = new Creator<PointList>() {
        @Override
        public PointList createFromParcel(Parcel in) {
            return new PointList(in);
        }

        @Override
        public PointList[] newArray(int size) {
            return new PointList[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof PointList)) {
            return false;
        }
        PointList other = (PointList) obj;
        if (invalid() || other.invalid())
            return false;
        ArrayList<Point> otherList = other.fixToLeftTopPointsList();
        ArrayList<Point> mineList = fixToLeftTopPointsList();
        int otherSize = otherList.size();
        int mineSize = mineList.size();
        if (otherSize != mineSize)
            return false;
        boolean match = true;
        for (int i = 0; i < mineSize; i++) {
            Point mine = mineList.get(i);
            Point oth = otherList.get(i);
            match = match && (mine.equals(oth));
            if (!match) {
                break;
            }
        }
        if (match)
            return true;
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return clockwise + "," + pointsList;
    }

}
