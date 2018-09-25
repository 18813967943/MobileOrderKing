package com.lejia.mobile.orderking.hk3d.datas_2d.cadwidgets;

import android.opengl.GLES30;

import com.lejia.mobile.orderking.hk3d.ViewingShader;
import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.classes.PointList;
import com.lejia.mobile.orderking.hk3d.classes.Trianglulate;
import com.lejia.mobile.orderking.hk3d.datas_2d.Furniture;
import com.lejia.mobile.orderking.hk3d.datas_2d.Selector;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/8/22 17:00
 * TODO: 简窗
 */
public class SimpleWindow extends BaseCad {

    public SimpleWindow(FurTypes furTypes) {
        super(furTypes);
    }

    public SimpleWindow(double angle, double thickness, double xlong, Point point, FurTypes furTypes) {
        super(angle, thickness, xlong, point, furTypes);
    }

    public SimpleWindow(double angle, double thickness, double xlong, Point point, FurTypes furTypes, Furniture furniture) {
        super(angle, thickness, xlong, point, furTypes, furniture);
    }

    @Override
    public void initDatas() {
        // 根据吸附点、厚度变化刷新数据
        thicknessPointsList = PointList.getRotateVertexs(angle, thickness, xlong, point);
        thicknessPointsList = new PointList(thicknessPointsList).antiClockwise();
        lj3DPointsList = new PointList(thicknessPointsList).to3dList();
        indices = new Trianglulate().getTristrip(new PointList(thicknessPointsList).toArray());
        vertexs = createVertexsBuffer(thicknessPointsList, indices);
        colors = createVertexsColorBuffer(thicknessPointsList, indices);
        vertexsBuffer = ByteBuffer.allocateDirect(4 * vertexs.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexsBuffer.put(vertexs).position(0);
        colorsBuffer = ByteBuffer.allocateDirect(4 * colors.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        colorsBuffer.put(colors).position(0);
        // 选中对象
        selector = new Selector(new PointList(thicknessPointsList));
        // 根据围点求出两条线段
        ArrayList<Point> lepsVerticalList = PointList.getRotateLEPS(angle + 90.0d, thickness / 3, point);
        ArrayList<Point> line1List = PointList.getRotateLEPS(angle, xlong, lepsVerticalList.get(0));
        ArrayList<Point> line2List = PointList.getRotateLEPS(angle, xlong, lepsVerticalList.get(1));
        // 根据以上围点，初始化渲染缓存数据
        cadLinesList = new ArrayList<>();
        cadLinesList.add(new CadLine(line1List));
        cadLinesList.add(new CadLine(line2List));
        // 刷新显示
        refreshRender();
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
            // 绘制选中
            if (isSelected()) {
                if (selector != null) {
                    selector.render(positionAttribute, normalAttribute, colorAttribute, onlyPosition);
                }
            }
            // 关闭混色
            GLES30.glDisable(GLES30.GL_BLEND);
        }
    }

}
