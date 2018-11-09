package com.lejia.mobile.orderking.hk3d.classes;

import android.graphics.Path;
import android.os.Parcel;
import android.os.Parcelable;

import com.lejia.mobile.orderking.utils.TextUtils;
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
        if (pointsList == null || pointsList.size() == 0)
            throw new NullPointerException("pointsList can`t be null !");
        ArrayList<Point> checkThenWipeRepeatPointList = new ArrayList<>();
        checkThenWipeRepeatPointList.add(pointsList.get(0).copy());
        if (pointsList.size() > 1) {
            Point check = checkThenWipeRepeatPointList.get(0);
            for (int i = 1; i < pointsList.size(); i++) {
                Point point = pointsList.get(i);
                if (!((check.x == point.x) && (check.y == point.y))) {
                    check = point;
                    checkThenWipeRepeatPointList.add(point.copy());
                }
            }
        }
        this.pointsList = checkThenWipeRepeatPointList;
    }

    public PointList(String pointsString) {
        if (TextUtils.isTextEmpity(pointsString))
            throw new NullPointerException("pointsString can`t be null !");
        String[] params = pointsString.split("[,]");
        if (params != null) {
            ArrayList<Point> pointArrayList = new ArrayList<>();
            int size = params.length / 2;
            for (int i = 0; i < size; i++) {
                int index = 2 * i;
                Point point = new Point(Double.parseDouble(params[index]), Double.parseDouble(params[index + 1]));
                pointArrayList.add(point);
            }
            this.pointsList = pointArrayList;
        }
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
     * 获取指定位置的数据
     *
     * @param position
     * @return
     */
    public Point get(int position) {
        if (invalid())
            return null;
        if (position < 0 || position >= size())
            return null;
        return pointsList.get(position);
    }

    /**
     * 获取具体数据列表
     */
    public ArrayList<Point> getPointsList() {
        return pointsList;
    }

    /**
     * 转为三维点列表
     */
    public ArrayList<LJ3DPoint> to3dList() {
        if (invalid())
            return null;
        ArrayList<LJ3DPoint> point3dList = new ArrayList<>();
        for (Point point : pointsList) {
            point3dList.add(point.toLJ3DPoint());
        }
        return point3dList;
    }

    /**
     * 转为切割铺砖围点列表
     *
     * @return
     */
    public ArrayList<geom.Point> toGeomPointList() {
        if (invalid())
            return null;
        ArrayList<geom.Point> geomList = new ArrayList<>();
        for (Point point : pointsList) {
            geomList.add(new geom.Point((float) point.x, (float) point.y));
        }
        return geomList;
    }

    /**
     * 将铺砖切割围点转换成本列表围点
     */
    public static ArrayList<Point> staticExchangeGemoListToThisList(ArrayList<geom.Point> geomList) {
        if (geomList == null || geomList.size() == 0)
            return null;
        ArrayList<Point> pointsList = new ArrayList<>();
        for (geom.Point point : geomList) {
            Point point1 = new Point(point.x, point.y);
            pointsList.add(point1);
        }
        return pointsList;
    }

    /**
     * 将铺砖切割围点转换成本列表围点
     *
     * @param geomList
     * @return
     */
    public ArrayList<Point> exchangeGemoListToThisList(ArrayList<geom.Point> geomList) {
        if (geomList == null || geomList.size() == 0)
            return null;
        ArrayList<Point> pointsList = new ArrayList<>();
        for (geom.Point point : geomList) {
            Point point1 = new Point();
            point1.x = point.x;
            point1.y = point.y;
            pointsList.add(point1);
        }
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
     * 内容长度大小
     */
    public int size() {
        if (invalid())
            return 0;
        return pointsList.size();
    }

    /**
     * 获取指定下标的点
     */
    public Point getIndexAt(int index) {
        if (invalid())
            return null;
        if (index < 0 || index >= size())
            return null;
        return pointsList.get(index);
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
        double minX = Integer.MAX_VALUE; // 使用整形最大最小值，double不支持负数
        double maxX = Integer.MIN_VALUE;
        double minY = Integer.MAX_VALUE;
        double maxY = Integer.MIN_VALUE;
        try {
            for (int i = 0; i < pointsList.size(); i++) {
                Point point = pointsList.get(i);
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
     * 获取外包盒子的围点列表
     */
    public ArrayList<Point> getBoxList() {
        if (invalid())
            return null;
        RectD box = getRectBox();
        ArrayList<Point> pointsList = new ArrayList<>();
        Point p1 = new Point();
        p1.x = box.left;
        p1.y = box.top;
        pointsList.add(p1);
        Point p2 = new Point();
        p2.x = box.right;
        p2.y = box.top;
        pointsList.add(p2);
        Point p3 = new Point();
        p3.x = box.right;
        p3.y = box.bottom;
        pointsList.add(p3);
        Point p4 = new Point();
        p4.x = box.left;
        p4.y = box.bottom;
        pointsList.add(p4);
        return pointsList;
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
            array[count] = point.copy();
            count++;
        }
        return array;
    }

    /**
     * 转换成数值数组
     *
     * @return
     */
    public float[] toFloatArray() {
        if (invalid())
            return null;
        float[] array = new float[2 * pointsList.size()];
        for (int i = 0; i < pointsList.size(); i++) {
            Point point = pointsList.get(i);
            int index = 2 * i;
            array[index] = (float) point.x;
            array[index + 1] = (float) point.y;
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
     * 转化为线段组合
     */
    public ArrayList<Line> toLineList() {
        if (invalid())
            return null;
        ArrayList<Line> linesList = new ArrayList<>();
        int size = size();
        for (int i = 0; i < size; i++) {
            Point now = pointsList.get(i);
            Point next = null;
            if (i == size - 1) {
                next = pointsList.get(0);
            } else {
                next = pointsList.get(i + 1);
            }
            linesList.add(new Line(now.copy(), next.copy()));
        }
        return linesList;
    }

    /**
     * 转化为线段组合
     */
    public ArrayList<Line> toNotClosedLineList() {
        if (invalid())
            return null;
        ArrayList<Line> linesList = new ArrayList<>();
        int size = size();
        for (int i = 0; i < size; i++) {
            Point now = pointsList.get(i);
            Point next = null;
            if (i != size - 1) {
                next = pointsList.get(i + 1);
            }
            if (next != null)
                linesList.add(new Line(now.copy(), next.copy()));
        }
        return linesList;
    }

    /**
     * 转化为对应的三维围点列表
     */
    public ArrayList<LJ3DPoint> to3DPointsList() {
        if (invalid())
            return null;
        ArrayList<LJ3DPoint> lj3dList = new ArrayList<>();
        int size = size();
        for (int i = 0; i < size; i++) {
            Point now = pointsList.get(i);
            lj3dList.add(now.toLJ3DPoint());
        }
        return lj3dList;
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
     * 逆时针围点列表
     */
    public ArrayList<Point> antiClockwise() {
        if (invalid())
            return null;
        ArrayList<Point> antiClockwiseList = new ArrayList<>();
        ArrayList<Point> clockwiseList = fixToLeftTopPointsList();
        for (int i = clockwiseList.size() - 1; i > -1; i--) {
            Point point = clockwiseList.get(i);
            antiClockwiseList.add(point.copy());
        }
        return antiClockwiseList;
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
            double halfDoorWidth = xlong * 0.5d;
            double halfThickness = thickness * 0.5d;
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

    /**
     * 内外点偏置
     *
     * @param outer      内外偏置标志
     * @param offset     偏置距离
     * @param pointsList
     * @return 返回内外偏置结果集合列表
     */
    public static ArrayList<Point> offsetList(boolean outer, double offset, ArrayList<Point> pointsList) {
        PointList pointList = new PointList(pointsList);
        if (pointList.invalid())
            return null;
        return pointList.offsetList(outer, offset);
    }

    /**
     * 内外点偏置
     *
     * @param outer  内外偏置标志
     * @param offset 偏置距离
     * @return 返回内外偏置结果集合列表
     */
    public ArrayList<Point> offsetList(boolean outer, double offset) {
        if (invalid())
            return null;
        ArrayList<Point> offsetPointsList = new ArrayList<>();
        try {
            // 组成线段
            ArrayList<Line> linesList = toLineList();
            if (linesList == null)
                return null;
            // 遍历获取线段垂直线段偏置距离两点
            double xlong = 2 * offset;
            ArrayList<ArrayList<Point>> linesElpsList = new ArrayList<>();
            for (int i = 0; i < linesList.size(); i++) {
                Line line = linesList.get(i);
                double angle = line.getAngle() + 90d;
                Point center = line.getCenter();
                ArrayList<Point> elpsList = PointList.getRotateLEPS(angle, xlong, center);
                linesElpsList.add(elpsList);
            }
            // 根据里外获取点
            ArrayList<Point> usePointsList = new ArrayList<>();
            for (int i = 0; i < linesElpsList.size(); i++) {
                ArrayList<Point> elpsList = linesElpsList.get(i);
                Point p0 = elpsList.get(0);
                Point p1 = elpsList.get(1);
                boolean p0Inner = pointRelationToPolygon(pointsList, p0) == 1;
                if (outer) {
                    if (p0Inner) {
                        usePointsList.add(p1);
                    } else {
                        usePointsList.add(p0);
                    }
                } else {
                    if (p0Inner) {
                        usePointsList.add(p0);
                    } else {
                        usePointsList.add(p1);
                    }
                }
            }
            // 根据点做线段
            ArrayList<Line> useLinesList = new ArrayList<>();
            for (int i = 0; i < usePointsList.size(); i++) {
                Point p = usePointsList.get(i);
                Line line = linesList.get(i);
                ArrayList<Point> elpsList = PointList.getRotateLEPS(line.getAngle(), (6 * line.getLength()), p);
                useLinesList.add(new Line(elpsList.get(0), elpsList.get(1)));
            }
            // 依次计算相交点
            Point lasterIntersectedPoint = null;
            ArrayList<Point> newPointsList = new ArrayList<>();
            for (int i = 0; i < useLinesList.size(); i++) {
                Line now = useLinesList.get(i);
                Line next = null;
                if (i == useLinesList.size() - 1) {
                    next = useLinesList.get(0);
                    lasterIntersectedPoint = now.getLineIntersectedPoint(next);
                } else {
                    next = useLinesList.get(i + 1);
                    Point intersected = now.getLineIntersectedPoint(next);
                    newPointsList.add(intersected);
                }
            }
            // 组合围点
            offsetPointsList.add(lasterIntersectedPoint.copy());
            for (int i = 0; i < newPointsList.size(); i++) {
                offsetPointsList.add(newPointsList.get(i).copy());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 无效
            if (offsetPointsList.size() == 0) {
                offsetPointsList = null;
            }
        }
        return offsetPointsList;
    }

    /**
     * 获取内部一点，优先中心点
     *
     * @param forceWithMaxLength 强制将房间名称放置在最长墙体附近显示
     * @return
     */
    public Point getInnerValidPoint(boolean forceWithMaxLength) {
        if (invalid())
            return null;
        Point point = null;
        try {
            RectD box = getRectBox();
            point = new Point(box.centerX(), box.centerY());
            // 不在内部
            if (pointRelationToPolygon(pointsList, point) != 1 || forceWithMaxLength) {
                // 取当前区域的最长水平线段
                ArrayList<Line> linesList = toLineList();
                int size = linesList.size();
                Line maxLengthLine = null;
                Line nextLine = null;
                Line beforeLine = null;
                double maxLength = Integer.MIN_VALUE;
                double offset = 200;
                for (int i = 0; i < size; i++) {
                    Line line = linesList.get(i);
                    double length = line.getLength();
                    double angle = line.getAngle();
                    if (Math.abs(angle % 180.0d) <= 0.5d && length >= maxLength) {
                        maxLength = length;
                        maxLengthLine = line;
                        // 上下一条线段，计算偏置距离
                        if (i == 0) {
                            beforeLine = linesList.get(size - 1);
                            nextLine = linesList.get(i + 1);
                        } else if (i == size - 1) {
                            beforeLine = linesList.get(i - 1);
                            nextLine = linesList.get(0);
                        } else {
                            beforeLine = linesList.get(i - 1);
                            nextLine = linesList.get(i + 1);
                        }
                        double beforeLength = beforeLine.getLength();
                        double nextLength = nextLine.getLength();
                        double minLength = nextLength < beforeLength ? nextLength : beforeLength;
                        offset = minLength < 200 ? 100 : 200;
                    }
                }
                // 根据水平线段获取垂直于内部一点
                if (maxLengthLine != null) {
                    ArrayList<Point> lepsList = getRotateLEPS(maxLengthLine.getAngle() + 90.0d, offset, maxLengthLine.getCenter());
                    for (Point point1 : lepsList) {
                        if (pointRelationToPolygon(pointsList, point1) == 1) {
                            point = point1;
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return point;
    }

    /**
     * 围点进行重复点、相近点、共线点去除、浮点型数据偏差等处理
     *
     * @param pointsList
     * @return
     */
    public static ArrayList<Point> filtrationList(ArrayList<Point> pointsList) {
        if (pointsList == null || pointsList.size() == 0)
            return null;
        ArrayList<Point> filtrationList = new ArrayList<>();
        try {
            // 去除近点、重复点、浮点精度修复
            ArrayList<Point> wipeRepeatAndNearlyList = new ArrayList<>();
            Point valid = pointsList.get(0);
            wipeRepeatAndNearlyList.add(valid.copy());
            for (int i = 1; i < pointsList.size(); i++) {
                Point point = pointsList.get(i);
                if (!point.equals(valid)) { // 近点、重复点
                    // 浮点精度修复
                    Point deviation = deviation(valid, point);
                    valid = (deviation != null) ? deviation : point;
                    wipeRepeatAndNearlyList.add(valid.copy());
                }
            }
            ArrayList<Point> tempWRANList = new ArrayList<>();
            for (int i = 0; i < wipeRepeatAndNearlyList.size() - 1; i++) {
                tempWRANList.add(wipeRepeatAndNearlyList.get(i));
            }
            Point deviationLast = deviation(wipeRepeatAndNearlyList.get(0), wipeRepeatAndNearlyList.get(wipeRepeatAndNearlyList.size() - 1));
            tempWRANList.add(deviationLast);
            // 去除共线点
            for (int i = 0; i < tempWRANList.size(); i++) {
                Point now = tempWRANList.get(i);
                Point before = null;
                Point next = null;
                if (i == 0) {
                    before = tempWRANList.get(tempWRANList.size() - 1);
                    next = tempWRANList.get(i + 1);
                } else if (i == tempWRANList.size() - 1) {
                    next = tempWRANList.get(0);
                    before = tempWRANList.get(i - 1);
                } else {
                    next = tempWRANList.get(i + 1);
                    before = tempWRANList.get(i - 1);
                }
                double nb = now.dist(before); // now、before
                double nn = now.dist(next); // now、next
                double bn = before.dist(next); // before、next
                if (!(Math.abs(bn - (nb + nn)) <= 1.0d)) {
                    filtrationList.add(now.copy());
                }
            }
            // 去除共线特殊情况
            ArrayList<Line> linesList = new PointList(filtrationList).toLineList();
            ArrayList<Point> specialList = new ArrayList<>();
            if (linesList != null && linesList.size() > 0) {
                for (Point point : filtrationList) {
                    boolean specialPoint = false;
                    for (Line line : linesList) {
                        if (!point.equals(line.down) && !point.equals(line.up) && line.isTouchOnLine(point.x, point.y)) {
                            specialPoint = true;
                            break;
                        }
                    }
                    if (!specialPoint) {
                        specialList.add(point);
                    }
                }
            }
            if (specialList.size() > 0) {
                filtrationList = specialList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filtrationList;
    }

    /**
     * 修复两点之间的浮点偏差
     *
     * @param target 对应目标点
     * @param check  被检测的点
     * @return 返回矫正后的点
     */
    public static Point deviation(Point target, Point check) {
        if (target == null || check == null)
            return null;
        Point ret = check.copy();
        double poorX = Math.abs(check.x - target.x);
        double poorY = Math.abs(check.y - target.y);
        if (poorX <= 0.0002d) {
            ret.x = target.x;
        }
        if (poorY <= 0.0002d) {
            ret.y = target.y;
        }
        return ret;
    }

    /**
     * 修复两点之间的浮点偏差
     *
     * @param target     对应目标点
     * @param check      被检测的点
     * @param targetDist 指定矫正差值
     * @param changeSelf 直接改变check点数值
     * @return 返回矫正后的点
     */
    public static Point deviation(Point target, Point check, double targetDist, boolean changeSelf) {
        if (target == null || check == null)
            return null;
        Point ret = check.copy();
        double poorX = Math.abs(check.x - target.x);
        double poorY = Math.abs(check.y - target.y);
        if (poorX <= targetDist) {
            if (changeSelf)
                check.x = target.x;
            else
                ret.x = target.x;
        }
        if (poorY <= targetDist) {
            if (changeSelf)
                check.y = target.y;
            else
                ret.y = target.y;
        }
        return ret;
    }

    /**
     * 用于矫正列表中点与点的x/y的指定最小差值对齐的功能
     *
     * @param pointArrayList 需要被矫正的点列表
     * @param targetMinDist  指定最小偏差
     * @return 返回对齐后的结果列表
     */
    public static ArrayList<Point> deviationList(ArrayList<Point> pointArrayList, double targetMinDist) {
        if (pointArrayList == null || pointArrayList.size() == 0)
            return null;
        try {
            int size = pointArrayList.size();
            for (int i = 0; i < size; i++) {
                Point now = pointArrayList.get(i);
                Point next = null;
                if (i != size - 1) {
                    next = pointArrayList.get(i + 1);
                    deviation(now, next, targetMinDist, true);
                } else {
                    next = pointArrayList.get(0);
                    deviation(next, now, targetMinDist, true);
                }
            }
            return pointArrayList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断端点吸附，矫正位置
     *
     * @param check
     * @param distV 吸附最大距离
     * @return
     */
    public Point correctAdsorbPoint(Point check, double distV) {
        if (check == null || invalid())
            return null;
        Point point = null;
        for (Point point1 : pointsList) {
            double dist = check.dist(point1);
            if (dist <= distV) {
                point = point1.copy();
                break;
            }
        }
        return point;
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
