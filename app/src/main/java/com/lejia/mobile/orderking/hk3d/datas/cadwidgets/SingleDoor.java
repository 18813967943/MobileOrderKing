package com.lejia.mobile.orderking.hk3d.datas.cadwidgets;

import android.opengl.GLES30;

import com.lejia.mobile.orderking.hk3d.ViewingShader;
import com.lejia.mobile.orderking.hk3d.classes.Line;
import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.classes.PointList;
import com.lejia.mobile.orderking.hk3d.classes.Trianglulate;
import com.lejia.mobile.orderking.hk3d.datas.Furniture;
import com.lejia.mobile.orderking.hk3d.datas.Selector;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/8/24 11:08
 * TODO: 单开门
 */
public class SingleDoor extends BaseCad {

    // 扇形区域数据
    private ArrayList<Point> radianList; // 扇形区域围点列表
    private float[] radianVertexs;
    private FloatBuffer radianVertexsBuffer;
    private float[] radianColors;
    private FloatBuffer radianColorsBuffer;
    private short[] radianIndices;

    private void initBuffers() {
        // 由于组合模式过滤同线段及近距离点问题，这里不采用组合一个方式，直接绘制两个区域
        // 扇形区域
        radianIndices = new Trianglulate().createRadiansIndices(new PointList(radianList).toArray());
        radianVertexs = createVertexsBuffer(radianList, radianIndices);
        radianColors = createVertexsColorBuffer(radianList, radianIndices);
        radianVertexsBuffer = ByteBuffer.allocateDirect(4 * radianVertexs.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        radianVertexsBuffer.put(radianVertexs).position(0);
        radianColorsBuffer = ByteBuffer.allocateDirect(4 * radianColors.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        radianColorsBuffer.put(radianColors).position(0);
        // 墙体厚度区域
        indices = new Trianglulate().getTristrip(new PointList(thicknessPointsList).toArray());
        vertexs = createVertexsBuffer(thicknessPointsList, indices);
        colors = createVertexsColorBuffer(thicknessPointsList, indices);
        vertexsBuffer = ByteBuffer.allocateDirect(4 * vertexs.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexsBuffer.put(vertexs).position(0);
        colorsBuffer = ByteBuffer.allocateDirect(4 * colors.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        colorsBuffer.put(colors).position(0);
        // 刷新
        refreshRender();
    }

    public SingleDoor(FurTypes furTypes) {
        super(furTypes);
    }

    public SingleDoor(double angle, double thickness, double xlong, Point point, FurTypes furTypes) {
        super(angle, thickness, xlong, point, furTypes);
    }

    public SingleDoor(double angle, double thickness, double xlong, Point point, FurTypes furTypes, Furniture furniture) {
        super(angle, thickness, xlong, point, furTypes, furniture);
    }

    @Override
    public void initDatas() {
        try {
            // 厚度围点
            thicknessPointsList = PointList.getRotateVertexs(angle, thickness, xlong, point);
            thicknessPointsList = new PointList(thicknessPointsList).antiClockwise();
            lj3DPointsList = new PointList(thicknessPointsList).to3dList();
            // 选中对象
            selector = new Selector(new PointList(thicknessPointsList));
            // 取第一个点作为扇区起始点
            Point begain = thicknessPointsList.get(thicknessPointsList.size() - 1);
            // 获取与自身长边的点
            Point next = thicknessPointsList.get(0);
            Point before = thicknessPointsList.get(thicknessPointsList.size() - 2);
            Point longsidePoint = null;
            double distN = begain.dist(next);
            double distB = begain.dist(before);
            longsidePoint = distN >= distB ? next : before;
            // 获取有效方向的点
            Line line = new Line(begain.copy(), longsidePoint.copy());
            double dist = line.getLength();
            ArrayList<Point> lepsList = PointList.getRotateLEPS(line.getAngle() + 90.0d, 2 * dist, begain);
            Line line1 = new Line(begain.copy(), lepsList.get(0));
            Line line2 = new Line(begain.copy(), lepsList.get(1));
            int count1 = 0;
            int count2 = 0;
            for (Point point : thicknessPointsList) {
                if (line1.getAdsorbPoint(point.x, point.y, 2.0d) != null) {
                    count1++;
                }
                if (line2.getAdsorbPoint(point.x, point.y, 2.0d) != null) {
                    count2++;
                }
            }
            Point validSidePoint = (count1 < count2) ? lepsList.get(0) : lepsList.get(1);
            ArrayList<Point> segLineList = new ArrayList<>();
            segLineList.add(validSidePoint.copy());
            segLineList.add(begain.copy());
            // 根据线段长度、起始角度、每条半径之间的夹角，遍历循环求出四分之一圆的弧线点集合
            Line checkLine = new Line(validSidePoint, longsidePoint);
            double angle = line.getAngle();
            double radianAngle = 18;
            double radian = dist;
            // 主体区域
            radianList = new ArrayList<>();
            radianList.add(longsidePoint.copy());
            radianList.add(begain.copy());
            radianList.add(validSidePoint.copy());
            for (int i = 1; i < 21; i++) {
                double rAngle = angle + i * radianAngle;
                ArrayList<Point> rlepsList = PointList.getRotateLEPS(rAngle, 2 * radian, begain);
                Line rline = new Line(rlepsList.get(1), rlepsList.get(0));
                Point interPoint = rline.getLineIntersectedPoint(checkLine);
                if (interPoint != null) {
                    double distI1 = interPoint.dist(rline.down);
                    double distI2 = interPoint.dist(rline.up);
                    Point valid = (distI1 < distI2) ? rline.down.copy() : rline.up.copy();
                    // 检测点是否存在
                    boolean existed = false;
                    for (int j = 0; j < radianList.size(); j++) {
                        Point p = radianList.get(j);
                        if (p.equals(valid)) {
                            existed = true;
                            break;
                        }
                    }
                    if (!existed)
                        radianList.add(valid);
                }
            }
            // 检测扇区连续性
            radianList = checkFanContinuity(radianList);
            // 扇区边线点
            ArrayList<Point> fanSideLineList = new ArrayList<>();
            for (int i = 2; i < radianList.size(); i++) {
                fanSideLineList.add(radianList.get(i).copy());
            }
            fanSideLineList.add(radianList.get(0).copy());
            // 创建边线绘制
            cadLinesList = new ArrayList<>();
            cadLinesList.add(new CadLine(segLineList));
            cadLinesList.add(new CadLine(fanSideLineList));
            // 加载字节缓存
            initBuffers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 获取扇形点
    public ArrayList<Point> getRadianList() {
        return radianList;
    }

    // 获取扇形索引
    public short[] getRadianIndices() {
        return radianIndices;
    }

    /**
     * 检测扇区连续性(前提:前三个点必须为扇区端点)
     *
     * @param checkList
     * @return
     */
    private ArrayList<Point> checkFanContinuity(ArrayList<Point> checkList) {
        if (checkList == null || checkList.size() == 0)
            return null;
        ArrayList<Point> continuityList = new ArrayList<>();
        try {
            // 切分
            ArrayList<Point> sideList = new ArrayList<>();
            ArrayList<Point> onfanList = new ArrayList<>();
            for (int i = 0; i < checkList.size(); i++) {
                if (i < 3) {
                    sideList.add(checkList.get(i));
                } else {
                    onfanList.add(checkList.get(i));
                }
            }
            // 检测末端端点与扇区端点的连接方向
            Point sideEnd = sideList.get(sideList.size() - 1);
            Point onfanBegain = onfanList.get(0);
            Point onfanEnd = onfanList.get(onfanList.size() - 1);
            double distsb = sideEnd.dist(onfanBegain);
            double distse = sideEnd.dist(onfanEnd);
            // 组合
            continuityList.addAll(sideList);
            if (distsb < distse) {
                continuityList.addAll(onfanList);
            } else {
                for (int i = onfanList.size() - 1; i > -1; i--) {
                    continuityList.add(onfanList.get(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return continuityList;
    }

    @Override
    public void render(int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition) {
        if (vertexsBuffer != null) {
            // 开启混色
            GLES30.glEnable(GLES30.GL_BLEND);
            GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA);
            // 绘制线条
            if (cadLinesList != null) {
                for (CadLine cadLine : cadLinesList) {
                    cadLine.render(positionAttribute, normalAttribute, colorAttribute, onlyPosition);
                }
            }
            // 绘制选中
            if (isSelected()) {
                if (selector != null) {
                    selector.render(positionAttribute, normalAttribute, colorAttribute, onlyPosition);
                }
            }
            // 绘制厚度面
            GLES30.glVertexAttribPointer(positionAttribute, 3, GLES30.GL_FLOAT, false, 12, vertexsBuffer);
            GLES30.glEnableVertexAttribArray(positionAttribute);
            if (!onlyPosition) {
                GLES30.glVertexAttribPointer(colorAttribute, 4, GLES30.GL_FLOAT, false,
                        16, colorsBuffer);
                GLES30.glEnableVertexAttribArray(colorAttribute);
                GLES30.glUniform1f(ViewingShader.scene_only_color, 1);
            }
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, indices.length);
            // 绘制扇形区域
            if (radianVertexsBuffer != null) {
                GLES30.glVertexAttribPointer(positionAttribute, 3, GLES30.GL_FLOAT, false, 12, radianVertexsBuffer);
                GLES30.glEnableVertexAttribArray(positionAttribute);
                if (!onlyPosition) {
                    GLES30.glVertexAttribPointer(colorAttribute, 4, GLES30.GL_FLOAT, false,
                            16, radianColorsBuffer);
                    GLES30.glEnableVertexAttribArray(colorAttribute);
                    GLES30.glUniform1f(ViewingShader.scene_only_color, 1);
                }
                GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, radianIndices.length);
            }
            // 关闭混色
            GLES30.glDisable(GLES30.GL_BLEND);
        }
    }

}
