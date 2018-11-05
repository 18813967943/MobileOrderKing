package com.lejia.mobile.orderking.hk3d.datas_2d;

import android.opengl.GLES30;

import com.lejia.mobile.orderking.hk3d.ViewingShader;
import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.classes.PointList;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Author by HEKE
 *
 * @time 2018/8/18 15:44
 * TODO:
 */
public class LineSeg extends RendererObject {

    private float[] colorValue = new float[]{1.0f, 0.0f, 0.0f, 1.0f}; // 颜色值
    private float[] colors; // 墙体颜色
    private FloatBuffer colorsBuffer; // 颜色字节缓存
    private ArrayList<Point> pointsList; // 围点

    public LineSeg(ArrayList<Point> pointsList) {
        this.pointsList = pointsList;
        initDatas();
    }

    /**
     * 初始化数据
     */
    private void initDatas() {
        if (pointsList == null || pointsList.size() == 0)
            return;
        uuid = UUID.randomUUID().toString();
        PointList pointList = new PointList(pointsList);
        pointsList = pointList.getPointsList();
        lj3DPointsList = pointList.to3dList();
        indices = new short[pointsList.size()];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = (short) i;
        }
        vertexs = new float[3 * indices.length];
        colors = new float[4 * indices.length];
        for (int i = 0; i < indices.length; i++) {
            Point point = pointsList.get(indices[i]);
            int index = 3 * i;
            vertexs[index] = (float) point.x;
            vertexs[index + 1] = (float) point.y;
            vertexs[index + 2] = 0;
            int colorIndex = 4 * i;
            colors[colorIndex] = colorValue[0];
            colors[colorIndex + 1] = colorValue[1];
            colors[colorIndex + 2] = colorValue[2];
            colors[colorIndex + 3] = colorValue[3];
        }
        vertexsBuffer = ByteBuffer.allocateDirect(4 * vertexs.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexsBuffer.put(vertexs).position(0);
        colorsBuffer = ByteBuffer.allocateDirect(4 * colors.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        colorsBuffer.put(colors).position(0);
    }

    public void setPointsList(ArrayList<Point> pointsList) {
        this.pointsList = pointsList;
        initDatas();
    }

    @Override
    public void render(int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition) {
        if (vertexsBuffer != null) {
            GLES30.glVertexAttribPointer(positionAttribute, 3, GLES30.GL_FLOAT, false, 12, vertexsBuffer);
            GLES30.glEnableVertexAttribArray(positionAttribute);
            if (!onlyPosition) {
                GLES30.glVertexAttribPointer(colorAttribute, 4, GLES30.GL_FLOAT, false,
                        16, colorsBuffer);
                GLES30.glEnableVertexAttribArray(colorAttribute);
                GLES30.glUniform1f(ViewingShader.scene_only_color, 1);
            }
            GLES30.glLineWidth(6.0f);
            GLES30.glDrawArrays(GLES30.GL_LINE_STRIP, 0, indices.length);
        }
    }

}
