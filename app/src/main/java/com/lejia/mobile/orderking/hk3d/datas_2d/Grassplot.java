package com.lejia.mobile.orderking.hk3d.datas_2d;

import android.graphics.Bitmap;
import android.opengl.GLES30;

import com.lejia.mobile.orderking.hk3d.RendererState;
import com.lejia.mobile.orderking.hk3d.ViewingShader;
import com.lejia.mobile.orderking.hk3d.classes.LJ3DPoint;
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
 * @time 2018/8/29 18:04
 * TODO:
 */
@Deprecated
public class Grassplot extends RendererObject {

    private ArrayList<Point> pointsList; // 原始围点
    private Bitmap textureBitmap; // 贴图
    private boolean needBindTexture;

    private void initBuffers() {
        int canvasSize = 15000;
        pointsList = PointList.getRotateVertexs(0, canvasSize, canvasSize, new Point(0, 0));
        PointList pointList = new PointList(pointsList);
        if (pointList.invalid())
            return;
        pointList.setPointsList(pointList.antiClockwise());
        pointsList = pointList.getPointsList();
        lj3DPointsList = pointList.to3dList();
        RectD box = pointList.getRectBox();
        indices = new Trianglulate().getTristrip(pointList.toArray());
        int size = indices.length;
        vertexs = new float[3 * size];
        texcoord = new float[2 * size];
        normals = new float[3 * size];
        LJ3DPoint normal = LJ3DPoint.spaceNormal(lj3DPointsList.get(indices[0]), lj3DPointsList.get(indices[1])
                , lj3DPointsList.get(indices[2]));
        int repeat = 40;
        for (int i = 0; i < size; i++) {
            LJ3DPoint point = lj3DPointsList.get(indices[i]);
            int index = 3 * i;
            vertexs[index] = (float) point.x;
            vertexs[index + 1] = (float) point.y;
            vertexs[index + 2] = (float) point.z - 5f;
            normals[index] = (float) normal.x;
            normals[index + 1] = (float) normal.y;
            normals[index + 2] = (float) normal.z;
            int uvIndex = 2 * i;
            texcoord[uvIndex] = repeat * (float) (Math.abs(point.x - box.left) / box.width());
            texcoord[uvIndex + 1] = repeat * (1.0f - (float) (Math.abs(point.y - box.bottom) / box.height()));
        }
        vertexsBuffer = ByteBuffer.allocateDirect(4 * vertexs.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexsBuffer.put(vertexs).position(0);
        normalsBuffer = ByteBuffer.allocateDirect(4 * normals.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        normalsBuffer.put(normals).position(0);
        texcoordBuffer = ByteBuffer.allocateDirect(4 * texcoord.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        texcoordBuffer.put(texcoord).position(0);
        // 贴图
        String[] rdUuid = new String[]{"normal_plot", "normal_plot", "normal_plot", "normal_plot"};
        uuid = rdUuid[(int) (Math.random() * rdUuid.length)];
        if (textureBitmap == null) {
            textureBitmap = createTextureWithAssets("textures/" + uuid + ".jpg");
            needBindTexture = true;
        }
    }

    public Grassplot() {
        initBuffers();
    }

    // 刷新
    public void refresh() {
        initBuffers();
    }

    @Override
    public void render(int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition) {
        if (needBindTexture) {
            needBindTexture = false;
            textureId = createTextureIdAndCache(uuid, textureBitmap, false);
        }
        if (textureId != -1) {
            // 关闭混色
            GLES30.glDisable(GLES30.GL_BLEND);
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
                if (RendererState.isNot2D()) {
                    GLES30.glUniform1f(ViewingShader.scene_use_light, 1.0f);
                } else {
                    GLES30.glUniform1f(ViewingShader.scene_use_light, 0.0f);
                }
            }
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, indices.length);
        }
    }

}
