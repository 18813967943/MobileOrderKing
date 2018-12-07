package com.lejia.mobile.orderking.hk3d.datas_2d.cadwidgets;

import com.lejia.mobile.orderking.hk3d.classes.L3DMatrix;
import com.lejia.mobile.orderking.hk3d.classes.LJ3DPoint;
import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.classes.PointList;
import com.lejia.mobile.orderking.hk3d.classes.RectD;
import com.lejia.mobile.orderking.hk3d.datas_2d.HouseDatasManager;
import com.lejia.mobile.orderking.hk3d.datas_2d.RendererObject;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.models.TopView;

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

    // 类型
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
     * 镜像
     */
    public boolean mirror;

    /**
     * 对应的数据集对象
     */
    public TopView topView;

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
        if (topView != null)
            topView.adi.point = this.point;
        initDatas();
    }

    // 设置围点平移数据
    public void translate(double tx, double ty) {
        if (point != null) {
            point.x += tx;
            point.y += ty;
            initDatas();
            refreshRender();
        }
    }

    /**
     * 设置拖拽结果
     *
     * @param dragResult
     */
    public void setDragResult(HouseDatasManager.DragAdsorbRet dragResult) {
        if (dragResult == null)
            return;
        if (dragResult.adsorbLine != null) {
            angle = dragResult.adsorbLine.getAngle();
        }
        this.point = dragResult.point;
        if (topView != null) {
            topView.adi.point = this.point;
            topView.adi.angle = (float) angle;
        }
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
        if (topView != null)
            topView.adi.angle = (float) angle;
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

    // 判断是否是镜像
    public boolean isMirror() {
        return mirror;
    }

    // 获取顶视数据对象
    public TopView getTopView() {
        return topView;
    }

    // 设置顶视数据对象
    public void setTopView(TopView topView) {
        this.topView = topView.copy();
        this.xlong = this.topView.xInfo.X / 10;
        if (this.topView.adi.thickness != -1) {
            this.thickness = this.topView.adi.thickness;
        } else {
            this.thickness = this.topView.xInfo.Y / 10;
        }
        this.angle = this.topView.adi.angle;
        this.point = this.topView.adi.point;
        initDatas();
        bindTexture();
    }

    /**
     * 初始化加载数据
     */
    public abstract void initDatas();

    /**
     * 设置家具信息后刷新贴图
     */
    public abstract void bindTexture();

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
     * 镜像
     */
    public void mirror() {
        mirror = !mirror;
        if (topView != null)
            topView.mirror = mirror;
        initDatas();
    }

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
     * 家具模型顶视图UV纹理转换生成
     *
     * @param angle
     * @return
     */
    public float[] createUvBufferByAngleOnlyRectangle(double angle) {
        if (indices == null)
            return null;
        float[] array = new float[]{0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f};
        if (angle > 0 && angle <= 90) {
            array = L3DMatrix.rotateUVArray(array, 90);
        } else if (angle > 180 && angle <= 270) {
            array = L3DMatrix.rotateUVArray(array, -90);
        } else if (angle == 0 || (angle > 270 && angle <= 360)) {
            array = L3DMatrix.rotateUVArray(array, -180);
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
