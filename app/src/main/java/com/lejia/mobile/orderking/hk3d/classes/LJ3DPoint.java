package com.lejia.mobile.orderking.hk3d.classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.lejia.mobile.orderking.hk3d.datas.RendererObject;

import java.util.ArrayList;

/**
 * @author HEKE
 * @2016年10月14日
 * @下午4:58:33
 * @TODO 三维空间坐标
 */
public class LJ3DPoint implements Parcelable {

    public double x;

    public double y;

    public double z;

    public LJ3DPoint() {
        super();
    }

    public LJ3DPoint(double x, double y, double z) {
        this.x = Point.percision(x, Point.defaultDecimalPlaces);
        this.y = Point.percision(y, Point.defaultDecimalPlaces);
        this.z = Point.percision(z, Point.defaultDecimalPlaces);
    }

    protected LJ3DPoint(Parcel in) {
        x = in.readDouble();
        y = in.readDouble();
        z = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(x);
        dest.writeDouble(y);
        dest.writeDouble(z);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LJ3DPoint> CREATOR = new Creator<LJ3DPoint>() {
        @Override
        public LJ3DPoint createFromParcel(Parcel in) {
            return new LJ3DPoint(in);
        }

        @Override
        public LJ3DPoint[] newArray(int size) {
            return new LJ3DPoint[size];
        }
    };

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    /**
     * 点积
     *
     * @param point
     * @return
     */
    public double dotProduct(LJ3DPoint point) {
        return x * point.x + y * point.y + z * point.z;
    }

    /**
     * 向量点相差
     *
     * @param point
     * @return
     */
    public LJ3DPoint subtract(LJ3DPoint point) {
        return new LJ3DPoint(x - point.x, y - point.y, z - point.z);
    }

    /**
     * 向量点相加
     *
     * @param point
     * @return
     */
    public LJ3DPoint add(LJ3DPoint point) {
        return new LJ3DPoint(x + point.x, y + point.y, z + point.z);
    }

    /**
     * 缩放
     *
     * @param scale
     * @return
     */
    public LJ3DPoint mul(double scale) {
        return new LJ3DPoint(x * scale, y * scale, z * scale);
    }

    /**
     * 向量叉积
     *
     * @param point
     * @return
     */
    public LJ3DPoint crossProduct(LJ3DPoint point) {
        return new LJ3DPoint(y * point.z - z * point.y, z * point.x - x * point.z, x * point.y - y * point.x);
    }

    /**
     * 投射到平面
     *
     * @return
     */
    public Point off() {
        Point point = new Point();
        point.x = x;
        point.y = y;
        return point;
    }

    public LJ3DPoint copy() {
        LJ3DPoint lj3DPoint = new LJ3DPoint();
        lj3DPoint.x = x;
        lj3DPoint.y = y;
        lj3DPoint.z = z;
        return lj3DPoint;
    }

    /**
     * 缩放至指定倍数
     *
     * @param scale
     * @return
     */
    public Point offScale(double scale) {
        return new Point(x * scale, y * scale);
    }

    /**
     * 数值
     */
    public double[] toValues() {
        return new double[]{x, y, z};
    }

    /**
     * 转换为1个单位
     *
     * @param a
     * @return
     */
    public static LJ3DPoint normalize(LJ3DPoint a) {
        double length = Math.sqrt(a.dotProduct(a));
        return a.mul(1 / length);
    }

    /**
     * 判断射线是否与平面相交
     *
     * @param ray
     * @param planePos
     * @param planeNormal
     * @return 不为null时表示相交, null未相交
     */
    private static LJ3DPoint rayCrossPlane(Ray ray, LJ3DPoint planePos, LJ3DPoint planeNormal) {
        LJ3DPoint Nab = ray.dir;
        double d_n_nab = Nab.dotProduct(planeNormal);
        if (d_n_nab == 0)
            return null;
        double xLen = (planePos.subtract(ray.pos).dotProduct(planeNormal)) / d_n_nab;
        if (xLen < 0)
            return null;
        return ray.pos.add(Nab.mul(xLen));
    }

    /**
     * 判断射线是否与三角面相交
     *
     * @param ray
     * @param a
     * @param b
     * @param c
     * @return
     */
    private static boolean checkRayInTriangle(Ray ray, LJ3DPoint a, LJ3DPoint b, LJ3DPoint c) {
        if (ray == null || a == null || b == null || c == null)
            return false;
        // 向量ab,ac
        LJ3DPoint E1 = b.subtract(a);
        LJ3DPoint E2 = c.subtract(a);
        // P
        LJ3DPoint P = ray.dir.crossProduct(E2);
        double det = E1.dotProduct(P);
        // T
        LJ3DPoint T = null;
        if (det > 0) {
            T = ray.pos.subtract(a);
        } else {
            T = a.subtract(ray.pos);
            det = -det;
        }
        if (det < 0.0001d)
            return false;
        // u
        double u = T.dotProduct(P);
        if (u < 0.0d || u > det)
            return false;
        // Q
        LJ3DPoint Q = T.crossProduct(E1);
        // v
        double v = ray.dir.dotProduct(Q);
        if (v < 0.0f || (u + v) > det)
            return false;
        return true;
    }

    /**
     * 获取当前点击屏幕相交点
     *
     * @param ray
     * @param spaceList
     * @param eyes
     * @return
     */
    public static LJ3DPoint checkRayIntersectedPoint(Ray ray, ArrayList<RendererObject> spaceList, LJ3DPoint eyes) {
        if (ray == null || spaceList == null)
            return null;
        // 记录相交点和面
        ArrayList<LJ3DPoint> intersectedPointList = new ArrayList<>();
        ArrayList<RendererObject> intersectedSpaceList = new ArrayList<>();
        // 循环遍历面
        int ssz = spaceList.size();
        for (int i = 0; i < ssz; i++) {
            RendererObject space = spaceList.get(i);
            short[] indices = space.indices;
            ArrayList<LJ3DPoint> plist = space.lj3DPointsList;
            int size = indices.length / 3;
            for (int j = 0; j < size; j++) {
                // 三角面顶点
                int index = j * 3;
                LJ3DPoint a = plist.get(indices[index]);
                LJ3DPoint b = plist.get(indices[index + 1]);
                LJ3DPoint c = plist.get(indices[index + 2]);
                boolean flag = checkRayInTriangle(ray, a, b, c);
                if (flag) {
                    // 三角面法向量
                    LJ3DPoint normal = LJ3DPoint.normalize((b.subtract(a)).crossProduct(c.subtract(a)));
                    // 三角面中心点
                    LJ3DPoint center = new LJ3DPoint((a.x + b.x + c.x) / 3, (a.y + b.y + c.y) / 3,
                            (a.z + b.z + c.z) / 3);
                    // 判断是否相交
                    LJ3DPoint intersected = rayCrossPlane(ray, center, normal);
                    if (intersected != null) {
                        intersectedPointList.add(intersected);
                        intersectedSpaceList.add(space);
                    }
                }
            }
        }
        // 返回面
        LJ3DPoint result = null;
        // 对比距离远近
        int interSize = intersectedPointList.size();
        if (interSize == 0) {
            return null;
        } else {
            double minDistance = Double.MAX_VALUE;
            for (int i = 0; i < interSize; i++) {
                double tempDist = getToWCSPointDistance(intersectedPointList.get(i), eyes);
                if (tempDist <= minDistance) {
                    minDistance = tempDist;
                    result = intersectedPointList.get(i);
                }
            }
        }
        return result;
    }

    /**
     * 获取两点之间的距离
     *
     * @param p1
     * @param p2
     * @return
     */
    private static double getToWCSPointDistance(LJ3DPoint p1, LJ3DPoint p2) {
        if (p1 == null || p2 == null)
            return -1;
        LJ3DPoint sub = p1.subtract(p2);
        return Math.sqrt(Math.abs(sub.dotProduct(sub)));
    }

    /**
     * 获取三角面法向量
     */
    public static LJ3DPoint spaceNormal(double p1x, double p1y, double p1z, double p2x, double p2y
            , double p2z, double p3x, double p3y, double p3z) {
        return spaceNormal(new LJ3DPoint(p1x, p1y, p1z), new LJ3DPoint(p2x, p2y, p2z), new LJ3DPoint(p3x, p3y, p3z));
    }

    /**
     * 获取三角面法向量
     *
     * @param p1
     * @param p2
     * @param p3
     * @return
     */
    public static LJ3DPoint spaceNormal(LJ3DPoint p1, LJ3DPoint p2, LJ3DPoint p3) {
        if (p1 == null || p2 == null || p3 == null)
            return null;
        LJ3DPoint normal = null;
        try {
            double x = (p2.y - p1.y) * (p3.z - p1.z) - (p2.z - p1.z) * (p3.y - p1.y);
            double y = ((p2.z - p1.z) * (p3.x - p1.x) - (p2.x - p1.x) * (p3.z - p1.z));
            double z = ((p2.x - p1.x) * (p3.y - p1.y) - (p2.y - p1.y) * (p3.x - p1.x));
            normal = LJ3DPoint.normalize(new LJ3DPoint(x, y, z));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return normal;
    }

    /**
     * 获取当前点击屏幕相交面
     *
     * @param ray
     * @param objectList
     * @param eyes
     * @return
     */
    public static RendererObject checkRayIntersectedObject(Ray ray, ArrayList<RendererObject> objectList, LJ3DPoint eyes) {
        if (ray == null || objectList == null)
            return null;
        // 记录相交点和面
        ArrayList<LJ3DPoint> intersectedPointList = new ArrayList<>();
        ArrayList<RendererObject> intersectedSpaceList = new ArrayList<>();
        // 循环遍历面
        for (int i = 0; i < objectList.size(); i++) {
            RendererObject space = objectList.get(i);
            short[] indices = space.indices;
            ArrayList<LJ3DPoint> plist = space.lj3DPointsList;
            for (int j = 0; j < indices.length / 3; j++) {
                // 三角面顶点
                int index = j * 3;
                LJ3DPoint a = plist.get(indices[index]);
                LJ3DPoint b = plist.get(indices[index + 1]);
                LJ3DPoint c = plist.get(indices[index + 2]);
                boolean rayIn = checkRayInTriangle(ray, a, b, c);
                if (rayIn) {
                    // 三角面法向量
                    LJ3DPoint normal = normalize((b.subtract(a)).crossProduct(c.subtract(a)));
                    // 三角面内部一个点
                    LJ3DPoint center = getTriangleInnerPoint(a, b, c);
                    // 判断是否相交
                    LJ3DPoint intersected = rayCrossPlane(ray, center, normal);
                    if (intersected != null) {
                        intersectedPointList.add(intersected);
                        intersectedSpaceList.add(space);
                    }
                }
            }
        }
        // 返回面
        RendererObject result = null;
        // 对比距离远近
        int interSize = intersectedPointList.size();
        if (interSize == 0) {
            return null;
        } else {
            float minDistance = Float.MAX_VALUE;
            for (int i = 0; i < interSize; i++) {
                float tempDist = getTwoWCSPointDistance(intersectedPointList.get(i), eyes);
                if (tempDist <= minDistance) {
                    minDistance = tempDist;
                    result = intersectedSpaceList.get(i);
                }
            }
        }
        return result;
    }

    /**
     * 获取两点之间的距离
     *
     * @param p1
     * @param p2
     * @return
     */
    public static float getTwoWCSPointDistance(LJ3DPoint p1, LJ3DPoint p2) {
        if (p1 == null || p2 == null)
            return -1;
        LJ3DPoint sub = p1.subtract(p2);
        return (float) Math.sqrt(Math.abs(sub.dotProduct(sub)));
    }

    /**
     * 根据三角形三个顶点，获取三角形内部一点
     *
     * @param a
     * @param b
     * @param c
     * @return
     */
    private static LJ3DPoint getTriangleInnerPoint(LJ3DPoint a, LJ3DPoint b, LJ3DPoint c) {
        return new LJ3DPoint((a.x + b.x + c.x) / 3, (a.y + b.y + c.y) / 3, (a.z + b.z + c.z) / 3);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LJ3DPoint))
            return false;
        LJ3DPoint p = (LJ3DPoint) obj;
        return p.x == x && p.y == y && p.z == z;
    }

    @Override
    public String toString() {
        return x + "," + y + "," + z;
    }

}
