package com.lejia.mobile.orderking.hk3d.classes;

import java.util.ArrayList;
import java.util.List;

/**
 * 模型操作矩阵
 *
 * @author HEKE
 * @2016年11月25日
 * @上午11:19:29
 */
public class L3DMatrix {

    /**
     * 顶点缩放比例
     */
    public static final double m = 0.1d;

    /**
     * 平移矩阵
     *
     * @param vertexs
     * @param transX
     * @param transY
     * @param transZ  为1时表示不变
     * @return
     */
    public static double[] translate(float[] vertexs, double transX, double transY, double transZ) {
        if (vertexs == null)
            return null;
        double[] result = null;
        int size = vertexs.length / 3;
        try {
            // 初始化矩阵
            double[] matrix = new double[]{1, 0, transX, 0, 1, transY, 0, 0, transZ};
            result = new double[vertexs.length];
            // 运算所有顶点
            for (int i = 0; i < size; i++) {
                int index = i * 3;
                float x = vertexs[index];
                float y = vertexs[index + 1];
                float z = vertexs[index + 2];
                // 存入新的顶点数组
                result[index] = x * matrix[0] + y * matrix[1] + matrix[2];
                result[index + 1] = x * matrix[3] + y * matrix[4] + matrix[5];
                result[index + 2] = x * matrix[6] + y * matrix[7] + z + (transZ == 1.0f ? 0 : matrix[8]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 旋转矩阵
     *
     * @param vertexs
     * @param angle
     * @return
     */
    public static float[] rotate(float[] vertexs, float angle) {
        if (vertexs == null)
            return null;
        float[] result = null;
        try {
            int size = vertexs.length / 3;
            result = new float[vertexs.length];
            float useAngle = angle;
            // 初始化矩阵
            float cosa = (float) Math.cos(Math.toRadians(useAngle));
            float sina = (float) Math.sin(Math.toRadians(useAngle));
            float[] matrix = new float[]{cosa, -sina, 0, sina, cosa, 0, 0, 0, 1};
            // 预算所有顶点
            for (int i = 0; i < size; i++) {
                int index = i * 3;
                float x = vertexs[index];
                float y = vertexs[index + 1];
                float z = vertexs[index + 2];
                // 存入新的顶点数组
                result[index] = x * matrix[0] + y * matrix[1] + matrix[2];
                result[index + 1] = x * matrix[3] + y * matrix[4] + matrix[5];
                result[index + 2] = x * matrix[6] + y * matrix[7] + z * matrix[8];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 旋转单个点
     *
     * @param point
     * @param angle
     * @param forceAbs 强制正整数
     * @return 返回旋转后的点
     */
    public static Point rotateSingle(Point point, float angle, boolean forceAbs) {
        if (point == null)
            return null;
        ArrayList<Point> pointsList = new ArrayList<>();
        pointsList.add(point.copy());
        ArrayList<Point> rotateList = rotate(pointsList, angle);
        Point p = rotateList.get(0);
        if (forceAbs) {
            return new Point(Math.abs(p.x), Math.abs(p.y));
        } else {
            return p;
        }
    }

    /**
     * 旋转顶点列表
     *
     * @param vertexList
     * @param angle
     */
    public static ArrayList<Point> rotate(ArrayList<Point> vertexList, float angle) {
        if (vertexList == null)
            return null;
        ArrayList<Point> rotateList = new ArrayList<>();
        try {
            // 初始化矩阵
            float cosa = (float) Math.cos(Math.toRadians(angle));
            float sina = (float) Math.sin(Math.toRadians(angle));
            float[] matrix = new float[]{cosa, -sina, 0, sina, cosa, 0, 0, 0, 1};
            // 运算
            int size = vertexList.size();
            for (int i = 0; i < size; i++) {
                Point p = vertexList.get(i);
                double x = p.x;
                double y = p.y;
                double nx = x * matrix[0] + y * matrix[1] + matrix[2];
                double ny = x * matrix[3] + y * matrix[4] + matrix[5];
                rotateList.add(new Point(nx, ny));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotateList;
    }

    /**
     * 旋转顶点列表
     *
     * @param vertexList
     * @param angle
     * @param toCenter
     */
    public static ArrayList<Point> rotate(ArrayList<Point> vertexList, double angle, Point toCenter) {
        if (vertexList == null)
            return null;
        ArrayList<Point> rotateList = new ArrayList<>();
        try {
            // 初始化矩阵
            double cosa = (float) Math.cos(Math.toRadians(angle));
            double sina = (float) Math.sin(Math.toRadians(angle));
            double[] matrix = new double[]{cosa, -sina, 0, sina, cosa, 0, 0, 0, 1};
            // 运算
            int size = vertexList.size();
            for (int i = 0; i < size; i++) {
                Point p = vertexList.get(i);
                double x = p.x;
                double y = p.y;
                double nx = x * matrix[0] + y * matrix[1] + matrix[2];
                double ny = x * matrix[3] + y * matrix[4] + matrix[5];
                rotateList.add(new Point(nx + toCenter.x, ny + toCenter.y));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotateList;
    }

    /**
     * 平移矩阵(三维点)
     *
     * @param pointsList
     * @param transX
     * @param transY
     * @param transZ     为1时表示不变
     * @return
     */
    public static ArrayList<LJ3DPoint> translate3DList(List<LJ3DPoint> pointsList, double transX, double transY, double transZ) {
        if (pointsList == null)
            return null;
        ArrayList<LJ3DPoint> result = new ArrayList<LJ3DPoint>();
        int size = pointsList.size();
        try {
            // 初始化矩阵
            double[] matrix = new double[]{1, 0, transX, 0, 1, transY, 0, 0, transZ};
            // 运算所有顶点
            for (int i = 0; i < size; i++) {
                double[] val = pointsList.get(i).toValues();
                // 存入新的顶点数组
                double x = val[0] + matrix[2];
                double y = val[1] + matrix[5];
                double z = val[2] + matrix[8];
                result.add(new LJ3DPoint(x, y, z));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (result.size() == 0)
                result = null;
        }
        return result;
    }

    /**
     * 平移矩阵
     *
     * @param pointList
     * @param transX
     * @param transY
     * @param transZ    为1时表示不变
     * @return
     */
    public static ArrayList<Point> translate(ArrayList<Point> pointList, double transX, double transY, double transZ) {
        if (pointList == null)
            return null;
        ArrayList<Point> result = null;
        int size = pointList.size();
        try {
            // 初始化矩阵
            double[] matrix = new double[]{1, 0, transX, 0, 1, transY, 0, 0, transZ};
            result = new ArrayList<>();
            // 运算所有顶点
            for (int i = 0; i < size; i++) {
                Point p = pointList.get(i);
                // 存入新的顶点数组
                Point np = new Point();
                np.x = p.x * matrix[0] + p.y * matrix[1] + matrix[2];
                np.y = p.x * matrix[3] + p.y * matrix[4] + matrix[5];
                result.add(np);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 模型Z轴方向平移
     *
     * @param vertexs
     * @param transZ
     * @return
     */
    public static double[] translate(double[] vertexs, double transZ) {
        if (vertexs == null)
            return null;
        double[] result = null;
        try {
            int len = vertexs.length;
            result = new double[len];
            int size = len / 3;
            for (int i = 0; i < size; i++) {
                int index = i * 3;
                result[index] = vertexs[index];
                result[index + 1] = vertexs[index + 1];
                result[index + 2] = vertexs[index + 2] + transZ;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 旋转矩阵(三维点)
     *
     * @param pointsList
     * @param angle
     * @return 对应每个顶点的旋转后的顶点
     */
    public static ArrayList<LJ3DPoint> rotate3DList(ArrayList<LJ3DPoint> pointsList, double angle) {
        if (pointsList == null)
            return null;
        ArrayList<LJ3DPoint> result = new ArrayList<LJ3DPoint>();
        try {
            int size = pointsList.size();
            double useAngle = angle;
            // 初始化矩阵
            double cosa = (float) Math.cos(Math.toRadians(useAngle));
            double sina = (float) Math.sin(Math.toRadians(useAngle));
            double[] matrix = new double[]{cosa, -sina, 0, sina, cosa, 0, 0, 0, 1};
            // 预算所有顶点
            for (int i = 0; i < size; i++) {
                double[] val = pointsList.get(i).toValues();
                // 存入新的顶点数组
                double x = val[0] * matrix[0] + val[1] * matrix[1] + matrix[2];
                double y = val[0] * matrix[3] + val[1] * matrix[4] + matrix[5];
                double z = val[2] * matrix[8];
                result.add(new LJ3DPoint(x, y, z));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (result.size() == 0)
                result = null;
        }
        return result;
    }

    /**
     * 缩放
     *
     * @param vertexs
     * @param scaleX
     * @param scaleY
     * @return
     */
    public static double[] scale(double[] vertexs, double scaleX, double scaleY, double scaleZ) {
        if (vertexs == null)
            return null;
        double[] result = null;
        try {
            int size = vertexs.length / 3;
            result = new double[vertexs.length];
            // 初始化矩阵
            double[] matrix = new double[]{scaleX, 0, 0, 0, scaleY, 0, 0, 0, scaleZ};
            for (int i = 0; i < size; i++) {
                int index = i * 3;
                double x = vertexs[index];
                double y = vertexs[index + 1];
                double z = vertexs[index + 2];
                result[index] = x * matrix[0] + y * matrix[1] + matrix[2];
                result[index + 1] = x * matrix[3] + y * matrix[4] + matrix[5];
                result[index + 2] = x * matrix[6] + y * matrix[7] + z * matrix[8];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 缩放
     *
     * @param pointList
     * @param scaleX
     * @param scaleY
     * @return
     */
    public static ArrayList<Point> scale(ArrayList<Point> pointList, double scaleX, double scaleY, double scaleZ) {
        if (pointList == null)
            return null;
        ArrayList<Point> result = null;
        try {
            int size = pointList.size();
            result = new ArrayList<>();
            // 初始化矩阵
            double[] matrix = new double[]{scaleX, 0, 0, 0, scaleY, 0, 0, 0, scaleZ};
            for (int i = 0; i < size; i++) {
                Point p = pointList.get(i);
                Point np = new Point();
                np.x = p.x * matrix[0] + p.y * matrix[1] + matrix[2];
                np.y = p.x * matrix[3] + p.y * matrix[4] + matrix[5];
                result.add(np);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 缩放
     *
     * @param pointList
     * @param scaleX
     * @param scaleY
     * @return
     */
    public static ArrayList<Point> scale(ArrayList<Point> pointList, double scaleX, double scaleY, Point toCenter) {
        if (pointList == null)
            return null;
        ArrayList<Point> result = new ArrayList<>();
        try {
            int size = pointList.size();
            // 初始化矩阵
            for (int i = 0; i < size; i++) {
                Point p = pointList.get(i);
                double x1 = p.x * scaleX;
                double y1 = p.y * scaleY;
                if (toCenter == null)
                    result.add(new Point(x1, y1));
                else
                    result.add(new Point(x1 + toCenter.x, y1 + toCenter.y));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 缩放
     *
     * @param pointList 相对于某点的向量列变
     * @param scaleList 缩放比例列表
     * @param toCenter  中心点(可为某点或其他点，为其他点时所有点与原点偏移)，
     *                  如果为null，则返回对应某点的向量缩进列表
     * @return 返回缩放点数据
     */
    public static ArrayList<Point> scale(ArrayList<Point> pointList, ArrayList<Double> scaleList, Point toCenter) {
        if (pointList == null)
            return null;
        ArrayList<Point> result = new ArrayList<>();
        try {
            int size = pointList.size();
            // 初始化矩阵
            for (int i = 0; i < size; i++) {
                Point p = pointList.get(i);
                int index = 2 * i;
                Double x1 = p.x * scaleList.get(index);
                Double y1 = p.y * scaleList.get(index + 1);
                if (toCenter == null)
                    result.add(new Point(x1, y1));
                else
                    result.add(new Point(x1 + toCenter.x, y1 + toCenter.y));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 复制矩阵
     *
     * @param matrix
     */
    public static float[] copy(float[] matrix) {
        if (matrix == null)
            return null;
        float[] copyMatrix = new float[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            copyMatrix[i] = matrix[i];
        }
        return copyMatrix;
    }

    /**
     * 镜像
     *
     * @param nowVertexs
     * @param scale
     */
    public static double[] mirror(double[] nowVertexs, double scale) {
        if (nowVertexs == null || nowVertexs.length == 0)
            return null;
        double[] vertexs = new double[nowVertexs.length];
        try {
            int size = nowVertexs.length / 3;
            for (int i = 0; i < size; i++) {
                int index = 3 * i;
                vertexs[index] = -nowVertexs[index] * scale;
                vertexs[index + 1] = nowVertexs[index + 1] * scale;
                vertexs[index + 2] = nowVertexs[index + 2] * scale;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vertexs;
    }
}
