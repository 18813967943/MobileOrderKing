package com.lejia.mobile.orderking.hk3d.datas_2d;

import android.opengl.GLES30;

import com.lejia.mobile.orderking.hk3d.RendererState;
import com.lejia.mobile.orderking.hk3d.ViewingShader;
import com.lejia.mobile.orderking.hk3d.classes.LJ3DPoint;
import com.lejia.mobile.orderking.hk3d.classes.Line;
import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.classes.PointList;
import com.lejia.mobile.orderking.hk3d.classes.RectD;
import com.lejia.mobile.orderking.hk3d.classes.Trianglulate;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/9/5 14:41
 * TODO: 切割三维墙立面的碎片面
 */
public class PunchFragmentFacade extends RendererObject {

    private ArrayList<Point> selfReleativeList; // 切割后映射的围点列表
    private ArrayList<Point> originReleativeList; // 原立面墙映射的围点列表
    private ArrayList<LJ3DPoint> lj3DPointArrayList; // 切割后对应的三维围点列表
    private int cell; // 所属楼层
    private int cellHeight; // 楼层层高
    private float[] normal; // 面对应的法线
    private Line punchLine; // 切割墙体对应的区域线段

    private void initBuffer() {
        PointList pointList = new PointList(selfReleativeList);
        indices = new Trianglulate().getTristrip(pointList.toArray());
        RectD box = new PointList(originReleativeList).getRectBox();
        vertexs = new float[3 * indices.length];
        texcoord = new float[2 * indices.length];
        normals = new float[3 * indices.length];
        for (int i = 0; i < indices.length; i++) {
            LJ3DPoint lj3DPoint = lj3DPointArrayList.get(indices[i]);
            Point point = selfReleativeList.get(indices[i]);
            int index = 3 * i;
            int uvIndex = 2 * i;
            vertexs[index] = (float) lj3DPoint.x;
            vertexs[index + 1] = (float) lj3DPoint.y;
            vertexs[index + 2] = (float) lj3DPoint.z;
            normals[index] = normal[0];
            normals[index + 1] = normal[1];
            normals[index + 2] = normal[2];
            texcoord[uvIndex] = (float) (Math.abs(point.x - box.left) / box.width());
            texcoord[uvIndex + 1] = (float) (Math.abs(point.y - box.bottom) / box.height());
        }
        vertexsBuffer = ByteBuffer.allocateDirect(4 * vertexs.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexsBuffer.put(vertexs).position(0);
        normalsBuffer = ByteBuffer.allocateDirect(4 * normals.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        normalsBuffer.put(normals).position(0);
        texcoordBuffer = ByteBuffer.allocateDirect(4 * texcoord.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        texcoordBuffer.put(texcoord).position(0);
    }

    public PunchFragmentFacade(Line punchLine, ArrayList<Point> selfReleativeList, ArrayList<Point> originReleativeList
            , ArrayList<LJ3DPoint> lj3DPointArrayList, int cell, int cellHeight, float[] normal) {
        this.punchLine = punchLine;
        this.selfReleativeList = selfReleativeList;
        this.originReleativeList = originReleativeList;
        this.lj3DPointArrayList = lj3DPointArrayList;
        this.cell = cell;
        this.cellHeight = cellHeight;
        this.normal = normal;
        initBuffer();
    }

    /**
     * 获取切割面对应的三维线段
     */
    public Line getPunchLine() {
        return punchLine;
    }

    /**
     * 获取自身映射至平面的围点列表
     */
    public ArrayList<Point> getSelfReleativeList() {
        return selfReleativeList;
    }

    /**
     * 获取原立面墙映射至平面的围点列表
     */
    public ArrayList<Point> getOriginReleativeList() {
        return originReleativeList;
    }

    /**
     * 切割面渲染
     */
    public void render(int textureId, int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition) {
        if (texcoordBuffer != null) {
            // 顶点
            GLES30.glVertexAttribPointer(positionAttribute, 3, GLES30.GL_FLOAT, false, 12, vertexsBuffer);
            GLES30.glEnableVertexAttribArray(positionAttribute);
            if (!onlyPosition) {
                // 法线
                GLES30.glVertexAttribPointer(normalAttribute, 3, GLES30.GL_FLOAT, false, 12, normalsBuffer);
                GLES30.glEnableVertexAttribArray(normalAttribute);
                // 纹理
                GLES30.glVertexAttribPointer(ViewingShader.scene_uv0, 2, GLES30.GL_FLOAT, false, 8, texcoordBuffer);
                GLES30.glEnableVertexAttribArray(ViewingShader.scene_uv0);
                // 贴图
                GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT);
                GLES30.glUniform1i(ViewingShader.scene_s_baseMap, 0);
                // 着色器使用标志
                GLES30.glUniform1f(ViewingShader.scene_only_color, 0.0f);
            }
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, indices.length);
        }
    }

    @Override
    @Deprecated
    public void render(int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition) {
    }

}
