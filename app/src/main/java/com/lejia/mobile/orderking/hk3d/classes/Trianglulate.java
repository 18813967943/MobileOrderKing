package com.lejia.mobile.orderking.hk3d.classes;

import java.util.ArrayList;

/**
 * @author HEKE 不规则多边形三角化对象
 */
public class Trianglulate {

    private double Number = 0.0000000001d;

    /**
     * 缓存索引数据结果
     */
    private ArrayList<Integer> triangleIndices = null;

    public Trianglulate() {
        super();
    }

    /**
     * 获取不规则多边形的索引数据
     *
     * @param points
     * @return
     */
    public short[] getTristrip(Point[] points) {
        triangleIndices = new ArrayList<>();
        process(points, true);
        if (triangleIndices.size() == 0)
            return null;
        int size = triangleIndices.size();
        short[] indices = new short[size];
        for (int i = 0; i < size; i++) {
            indices[i] = triangleIndices.get(i).shortValue();
        }
        triangleIndices.clear();
        triangleIndices = null;
        return indices;
    }

    /**
     * 多边形三角化
     *
     * @param contour  多边形的点序列
     * @param bIndices 返回所有的三角形，result需要调用者自己new出来，不能传null,
     *                 result的元素为Array存储三角形的逆时针三点
     * @return 三角化是否成功
     */
    public boolean process(Point[] contour, boolean bIndices) {
        int n = contour.length;
        if (n < 3)
            return false;
        int[] V = new int[n];
        int v = 0;
        if (0.0 < area(contour))
            for (v = 0; v < n; ++v)
                V[v] = v;
        else
            for (v = 0; v < n; ++v)
                V[v] = n - 1 - v;
        int nv = n;
        int m = 0;
        v = nv - 1;
        int count = 2 * nv;
        for (; nv > 2; ) {
            if (0 >= (count--)) {
                return (m >= 1);
            }
            int u = v;
            if (nv <= u)
                u = 0;
            v = u + 1;
            if (nv <= v)
                v = 0;
            int w = v + 1;
            if (nv <= w)
                w = 0;
            if (snip(contour, u, v, w, nv, V)) {
                int a = V[u];
                int b = V[v];
                int c = V[w];
                ArrayList<Integer> triangle = new ArrayList<Integer>();
                if (isAntiClockwise(contour[a].x, contour[a].y, contour[b].x, contour[b].y, contour[c].x,
                        contour[c].y)) {
                    if (bIndices) {
                        triangle.add(a);
                        triangle.add(b);
                        triangle.add(c);
                    }
                } else {
                    if (bIndices) {
                        triangle.add(a);
                        triangle.add(c);
                        triangle.add(b);
                    }
                }
                // 存入结果
                triangleIndices.addAll(triangle);
                m++;
                int s = v;
                int t = v + 1;
                for (; t < nv; t++, s++) {
                    V[s] = V[t];
                }
                nv--;
                count = 2 * nv;
            }
        }
        return true;
    }

    public double area(Point[] contour) {
        int n = contour.length;
        double A = 0.0;
        int p = n - 1;
        int q = 0;
        for (; q < n; ) {
            A += contour[p].x * contour[q].y - contour[q].x * contour[p].y;
            p = q;
            q++;
        }
        return A * 0.5;
    }

    private boolean snip(Point[] contour, int u, int v, int w, int n, int[] V) {
        int p = 0;
        double Ax = contour[V[u]].x;
        double Ay = contour[V[u]].y;
        double Bx = contour[V[v]].x;
        double By = contour[V[v]].y;
        double Cx = contour[V[w]].x;
        double Cy = contour[V[w]].y;
        double Px;
        double Py;

        if (Number > (((Bx - Ax) * (Cy - Ay)) - ((By - Ay) * (Cx - Ax))))
            return false;

        for (p = 0; p < n; ++p) {
            if (p == u || p == v || p == w)
                continue;
            Px = contour[V[p]].x;
            Py = contour[V[p]].y;
            if (insideTriangle(Ax, Ay, Bx, By, Cx, Cy, Px, Py))
                return false;
        }
        return true;
    }

    public boolean insideTriangle(double ax, double ay, double bx, double by, double cx, double cy, double px,
                                  double py) {
        double dx = cx - bx;// ax
        double dy = cy - by;// ay
        double ex = ax - cx;// bx
        double ey = ay - cy;// by
        double fx = bx - ax;// cx
        double fy = by - ay;// cy

        double apx = px - ax;
        double apy = py - ay;
        double bpx = px - bx;
        double bpy = py - by;
        double cpx = px - cx;
        double cpy = py - cy;

        double aCrossbp = dx * bpy - dy * bpx;// ax
        double cCrossap = fx * apy - fy * apx;// cx
        double bCrosscp = ex * cpy - ey * cpx;// bx

        return ((aCrossbp >= 0.0) && (bCrosscp >= 0.0) && (cCrossap >= 0.0));
    }

    public boolean isAntiClockwise(double Ax, double Ay, double Bx, double By, double Cx, double Cy) {
        double abx = Bx - Ax;
        double aby = By - Ay;
        double acx = Cx - Ax;
        double acy = Cy - Ay;
        double crossP = abx * acy - aby * acx;
        return (crossP > 0 || Math.abs(crossP) < 1e-6);
    }

    /**
     * 创建四分之一弧形索引数据
     * 声明: 扇形(弧形)点必须以非弧形线上的点为起始点的组合区域
     *
     * @return 返回索引
     */
    public short[] createRadiansIndices(Point[] points) {
        if (points == null)
            return null;
        short[] V = new short[points.length];
        for (short i = 0; i < V.length; i++) {
            V[i] = i;
        }
        int size = V.length - 2;
        short[] ret = new short[3 * size];
        for (int i = size - 1; i > -1; i--) {
            short b = (short) (i + 1);
            short c = (short) i;
            if (i == 0) {
                b = (short) (V.length - 1);
                c = (short) size;
            }
            int index = 3 * i;
            ret[index] = 0;
            ret[index + 1] = b;
            ret[index + 2] = c;
        }
        return ret;
    }

}
