package com.lejia.mobile.orderking.hk3d.datas.cadwidgets;

import com.lejia.mobile.orderking.hk3d.classes.LJ3DPoint;
import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.classes.PointList;
import com.lejia.mobile.orderking.hk3d.classes.RectD;
import com.lejia.mobile.orderking.hk3d.datas.Furniture;
import com.lejia.mobile.orderking.hk3d.datas.RendererObject;

import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/8/22 16:00
 * TODO: Cad控件基类
 */
public abstract class BaseCad extends RendererObject {

    // 通用颜色
    public float[] color = new float[]{1.0f, 1.0f, 1.0f, 0.6f};
    public float[] colors;
    public FloatBuffer colorsBuffer;

    // 角度
    public double angle;

    // 所在墙体厚度，默认厚度240mm(模型宽度)
    public double thickness = 24.0d;

    // 模型长度，默认1000mm
    public double xlong = 100.0d;

    // 吸附围点
    public Point point;

    // 门窗类型
    public FurTypes furTypes;

    /**
     * 厚度围点列表
     */
    public ArrayList<Point> thicknessPointsList;

    /**
     * 线段列表
     */
    public ArrayList<CadLine> cadLinesList;

    /**
     * 对应家具
     */
    public Furniture furniture;

    public BaseCad(FurTypes furTypes) {
        this.furTypes = furTypes;
    }

    public BaseCad(double angle, double thickness, double xlong, Point point, FurTypes furTypes) {
        this.angle = angle;
        this.thickness = thickness;
        this.xlong = xlong;
        this.point = point;
        this.furTypes = furTypes;
        initDatas();
    }

    public BaseCad(double angle, double thickness, double xlong, Point point, FurTypes furTypes, Furniture furniture) {
        this.angle = angle;
        this.thickness = thickness;
        this.xlong = xlong;
        this.point = point;
        this.furTypes = furTypes;
        this.furniture = furniture;
        initDatas();
    }

    // 获取当前厚度
    public double getThickness() {
        return thickness;
    }

    // 设置厚度(模型宽度)
    public void setThickness(double thickness) {
        this.thickness = thickness;
        initDatas();
    }

    // 获取吸附点
    public Point getPoint() {
        return point;
    }

    // 设置吸附点
    public void setPoint(Point point) {
        this.point = point;
        initDatas();
    }

    // 获取门窗类型
    public FurTypes getFurTypes() {
        return furTypes;
    }

    // 获取现在所在的角度，范围-360°至360°
    public double getAngle() {
        return angle;
    }

    // 设置角度
    public void setAngle(double angle) {
        this.angle = angle;
        initDatas();
    }

    // 获取长度
    public double getXlong() {
        return xlong;
    }

    // 设置长度
    public void setXlong(double xlong) {
        this.xlong = xlong;
        initDatas();
    }

    /**
     * 获取当前对应的模型
     */
    public Furniture getFurniture() {
        return furniture;
    }

    /**
     * 设置模型数据
     *
     * @param furniture 家具模型
     */
    public void setFurniture(Furniture furniture) {
        this.furniture = furniture;
        if (this.furniture != null) {
            thickness = this.furniture.width / 10;
            xlong = this.furniture.xLong / 10;
            initDatas();
        }
    }

    /**
     * 初始化加载数据
     */
    public abstract void initDatas();

    /**
     * 基础渲染
     *
     * @param positionAttribute 位置编号
     * @param normalAttribute   法线编号
     * @param colorAttribute    颜色编号
     * @param onlyPosition      是否阴影仅顶点着色
     */
    public abstract void render(int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition);

    /**
     * 围点数据转换
     *
     * @param pointsList
     * @param indices
     * @return
     */
    public float[] createVertexsBuffer(ArrayList<Point> pointsList, short[] indices) {
        if (pointsList == null || pointsList.size() == 0)
            return null;
        int size = indices.length;
        float[] array = new float[3 * size];
        for (int i = 0; i < size; i++) {
            Point point = pointsList.get(indices[i]);
            int index = 3 * i;
            array[index] = (float) point.x;
            array[index + 1] = (float) point.y;
            array[index + 2] = 0.0f;
        }
        return array;
    }

    /**
     * 围点数据转换
     *
     * @param pointsList
     * @param indices
     * @return
     */
    public float[] createVertexsColorBuffer(ArrayList<Point> pointsList, short[] indices) {
        if (pointsList == null || pointsList.size() == 0)
            return null;
        int size = indices.length;
        float[] array = new float[4 * size];
        for (int i = 0; i < size; i++) {
            Point point = pointsList.get(indices[i]);
            int index = 4 * i;
            array[index] = color[0];
            array[index + 1] = color[1];
            array[index + 2] = color[2];
            array[index + 3] = color[3];
        }
        return array;
    }

    /**
     * 围点数据转换
     *
     * @param pointsList
     * @param indices
     * @return
     */
    public float[] createUvBuffer(ArrayList<Point> pointsList, short[] indices) {
        if (pointsList == null || pointsList.size() == 0)
            return null;
        RectD box = new PointList(pointsList).getRectBox();
        int size = indices.length;
        float[] array = new float[2 * size];
        for (int i = 0; i < size; i++) {
            Point point = pointsList.get(indices[i]);
            int index = 2 * i;
            array[index] = (float) (Math.abs(point.x - box.left) / box.width());
            array[index + 1] = (float) (Math.abs(point.y - box.top) / box.height());
        }
        return array;
    }

    /**
     * 创建法线
     *
     * @param pointsList
     * @param indices
     * @param normal
     * @return
     */
    public float[] createNormalsBuffer(ArrayList<Point> pointsList, short[] indices, LJ3DPoint normal) {
        if (pointsList == null || pointsList.size() == 0)
            return null;
        int size = indices.length;
        float[] array = new float[3 * size];
        for (int i = 0; i < size; i++) {
            Point point = pointsList.get(indices[i]);
            int index = 3 * i;
            array[index] = (float) normal.x;
            array[index + 1] = (float) normal.y;
            array[index + 2] = (float) normal.z;
        }
        return array;
    }

}
