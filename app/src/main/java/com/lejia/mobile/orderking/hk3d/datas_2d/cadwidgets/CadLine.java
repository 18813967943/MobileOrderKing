package com.lejia.mobile.orderking.hk3d.datas_2d.cadwidgets;

import android.opengl.GLES30;

import com.lejia.mobile.orderking.hk3d.ViewingShader;
import com.lejia.mobile.orderking.hk3d.classes.Point;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/8/22 17:40
 * TODO: cad 线条
 */
public class CadLine {

    // 点列表
    public ArrayList<Point> pointsList;
    private boolean invalid;
    // 顶点
    public float[] vertexs;
    public FloatBuffer vertexsBuffer;

    // 线条颜色
    public float[] lineColor = new float[]{0.1f, 0.1f, 0.1f, 0.8f};
    public float[] lineColors;
    public FloatBuffer lineColorsBuffer;

    public CadLine() {
        super();
    }

    public CadLine(ArrayList<Point> pointsList) {
        this.pointsList = pointsList;
        this.invalid = (pointsList == null || pointsList.size() < 2);
        initDatas();
    }

    // 是否无效
    public boolean isInvalid() {
        return invalid;
    }

    // 大小
    public int size() {
        if (invalid)
            return 0;
        return pointsList.size();
    }

    // 初始化数据
    private void initDatas() {
        if (invalid)
            return;
        int size = size();
        vertexs = new float[3 * size];
        lineColors = new float[4 * size];
        for (int i = 0; i < size; i++) {
            Point point = pointsList.get(i);
            int index = 3 * i;
            vertexs[index] = (float) point.x;
            vertexs[index + 1] = (float) point.y;
            vertexs[index + 2] = 0.0f;
            int colorIndex = 4 * i;
            lineColors[colorIndex] = lineColor[0];
            lineColors[colorIndex + 1] = lineColor[1];
            lineColors[colorIndex + 2] = lineColor[2];
            lineColors[colorIndex + 3] = lineColor[3];
        }
        vertexsBuffer = ByteBuffer.allocateDirect(4 * vertexs.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexsBuffer.put(vertexs).position(0);
        lineColorsBuffer = ByteBuffer.allocateDirect(4 * lineColors.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        lineColorsBuffer.put(lineColors).position(0);
    }

    /**
     * 渲染
     */
    public void render(int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition) {
        GLES30.glVertexAttribPointer(positionAttribute, 3, GLES30.GL_FLOAT, false, 12, vertexsBuffer);
        GLES30.glEnableVertexAttribArray(positionAttribute);
        if (!onlyPosition) {
            GLES30.glVertexAttribPointer(colorAttribute, 4, GLES30.GL_FLOAT, false,
                    16, lineColorsBuffer);
            GLES30.glEnableVertexAttribArray(colorAttribute);
            GLES30.glUniform1f(ViewingShader.scene_only_color, 1);
            GLES30.glUniform1f(ViewingShader.scene_use_light, 0.0f);
        }
        GLES30.glLineWidth(2.0f);
        GLES30.glDrawArrays(GLES30.GL_LINE_STRIP, 0, size());
    }

}
