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
import java.util.UUID;

/**
 * Author by HEKE
 *
 * @time 2018/7/16 16:20
 * TODO: 墙体
 */
public class Wall extends RendererObject {

    private float[] colorValue = new float[]{0.1f, 0.1f, 0.1f, 1.0f}; // 颜色值
    private float[] colors; // 墙体颜色
    private FloatBuffer colorsBuffer; // 颜色字节缓存
    private ArrayList<Point> pointsList; // 围点
    private boolean invalid; // 无效的

    public void initDatas() {
        invalid = (pointsList == null || pointsList.size() == 0);
        if (invalid)
            return;
        uuid = UUID.randomUUID().toString();
        PointList pointList = new PointList(pointsList);
        pointList.setPointsList(pointList.antiClockwise());
        pointsList = pointList.getPointsList();
        lj3DPointsList = pointList.to3dList();
        indices = new short[]{0, 1, 2, 0, 2, 3};
        vertexs = new float[3 * indices.length];
        colors = new float[4 * indices.length];
        normals = new float[3 * indices.length];
        LJ3DPoint normal = LJ3DPoint.spaceNormal(pointsList.get(indices[0]).toLJ3DPoint()
                , pointsList.get(indices[1]).toLJ3DPoint(), pointsList.get(indices[2]).toLJ3DPoint());
        for (int i = 0; i < indices.length; i++) {
            Point point = pointsList.get(indices[i]);
            int index = 3 * i;
            vertexs[index] = (float) point.x;
            vertexs[index + 1] = (float) point.y;
            vertexs[index + 2] = 0;
            normals[index] = (float) normal.x;
            normals[index + 1] = (float) normal.y;
            normals[index + 2] = (float) normal.z;
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
        normalsBuffer = ByteBuffer.allocateDirect(4 * normals.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        normalsBuffer.put(normals).position(0);
    }

    public Wall(ArrayList<Point> pointsList) {
        this.pointsList = pointsList;
        initDatas();
    }

    @Override
    public void render(int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition) {
        if (invalid)
            return;
        GLES30.glVertexAttribPointer(positionAttribute, 3, GLES30.GL_FLOAT, false, 12, vertexsBuffer);
        GLES30.glEnableVertexAttribArray(positionAttribute);
        if (!onlyPosition) {
            GLES30.glVertexAttribPointer(normalAttribute, 3, GLES30.GL_FLOAT, false,
                    12, normalsBuffer);
            GLES30.glEnableVertexAttribArray(normalAttribute);
            GLES30.glVertexAttribPointer(colorAttribute, 4, GLES30.GL_FLOAT, false,
                    16, colorsBuffer);
            GLES30.glEnableVertexAttribArray(colorAttribute);
            GLES30.glUniform1f(ViewingShader.scene_only_color, 1);
        }
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, indices.length);
    }

}
