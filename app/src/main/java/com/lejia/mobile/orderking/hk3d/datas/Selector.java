package com.lejia.mobile.orderking.hk3d.datas;

import android.opengl.GLES30;

import com.lejia.mobile.orderking.hk3d.ViewingShader;
import com.lejia.mobile.orderking.hk3d.classes.LJ3DPoint;
import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.classes.PointList;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/7/20 16:53
 * TODO: 选中对象
 */
public class Selector extends RendererObject {

    //private float[] color = new float[]{0.53f, 0.808f, 0.98f, 1.0f};
    private float[] color = new float[]{0.0f, 0.7f, 1.0f, 1.0f};
    private float[] colors;
    private FloatBuffer colorsBuffer;

    private int size;

    /**
     * 适用于正对面选中的构造函数
     *
     * @param pointList 选中区域
     */
    public Selector(PointList pointList) {
        init2DSelector(pointList);
    }

    // 加载平面选中数据
    private void init2DSelector(PointList pointList) {
        if (pointList == null || pointList.invalid())
            return;
        size = pointList.size() + 1;
        vertexs = new float[3 * size];
        colors = new float[4 * size];
        for (int i = 0; i < size; i++) {
            Point point = null;
            if (i == size - 1) {
                point = pointList.get(0);
            } else {
                point = pointList.get(i);
            }
            int index = 3 * i;
            vertexs[index] = (float) point.x;
            vertexs[index + 1] = (float) point.y;
            vertexs[index + 2] = 0;
            int colorIndex = 4 * i;
            colors[colorIndex] = color[0];
            colors[colorIndex + 1] = color[1];
            colors[colorIndex + 2] = color[2];
            colors[colorIndex + 3] = color[3];
        }
        // 存入字节缓存
        vertexsBuffer = ByteBuffer.allocateDirect(4 * vertexs.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexsBuffer.put(vertexs).position(0);
        colorsBuffer = ByteBuffer.allocateDirect(4 * colors.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        colorsBuffer.put(colors).position(0);
    }

    /**
     * 适用于三维模型的构造函数
     *
     * @param lj3DPointArrayList 选中区域
     */
    public Selector(ArrayList<LJ3DPoint> lj3DPointArrayList) {
        init3DSelector(lj3DPointArrayList);
    }

    // 加载三维选中数据
    private void init3DSelector(ArrayList<LJ3DPoint> lj3DPointArrayList) {
        indices = new short[]{0, 1, 2, 0, 2, 3};
        size = lj3DPointArrayList.size();
        vertexs = new float[3 * size];
        colors = new float[4 * size];
        for (int i = 0; i < size; i++) {
            LJ3DPoint point = null;
            if (i == size - 1) {
                point = lj3DPointArrayList.get(0);
            } else {
                point = lj3DPointArrayList.get(i);
            }
            int index = 3 * i;
            vertexs[index] = (float) point.x;
            vertexs[index + 1] = (float) point.y;
            vertexs[index + 2] = (float) point.z;
            int colorIndex = 4 * i;
            colors[colorIndex] = color[0];
            colors[colorIndex + 1] = color[1];
            colors[colorIndex + 2] = color[2];
            colors[colorIndex + 3] = color[3];
        }
        // 存入字节缓存
        vertexsBuffer = ByteBuffer.allocateDirect(4 * vertexs.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexsBuffer.put(vertexs).position(0);
        colorsBuffer = ByteBuffer.allocateDirect(4 * colors.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        colorsBuffer.put(colors).position(0);
    }

    /**
     * 刷新围点
     */
    public void refresh(PointList pointList) {
        init2DSelector(pointList);
    }

    /**
     * 刷新围点
     */
    public void refresh3D(ArrayList<LJ3DPoint> lj3DPointArrayList) {
        init3DSelector(lj3DPointArrayList);
    }

    @Override
    public void render(int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition) {
        // Pass in the position information
        GLES30.glVertexAttribPointer(positionAttribute, 3, GLES30.GL_FLOAT, false, 12, vertexsBuffer);
        GLES30.glEnableVertexAttribArray(positionAttribute);
        if (!onlyPosition) {
            // Pass in the color information
            GLES30.glVertexAttribPointer(colorAttribute, 4, GLES30.GL_FLOAT, false,
                    16, colorsBuffer);
            GLES30.glEnableVertexAttribArray(colorAttribute);
            // use color only render
            GLES30.glUniform1f(ViewingShader.scene_only_color, 1.0f);
            GLES30.glUniform1f(ViewingShader.scene_use_light, 0.0f);
        }
        // draw selector lines
        GLES30.glLineWidth(4.0f);
        GLES30.glDrawArrays(GLES30.GL_LINE_STRIP, 0, size);
    }

    /**
     * 释放数据
     */
    public void releaseDatas() {
        vertexs = null;
        vertexsBuffer = null;
        colors = null;
        colorsBuffer = null;
    }

}
