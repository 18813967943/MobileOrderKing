package com.lejia.mobile.orderking.hk3d.datas_3d.classes;

import android.opengl.GLES30;

import com.lejia.mobile.orderking.hk3d.classes.LJ3DPoint;
import com.lejia.mobile.orderking.hk3d.datas_2d.Ground;
import com.lejia.mobile.orderking.hk3d.datas_2d.RendererObject;
import com.lejia.mobile.orderking.hk3d.datas_3d.ShadowViewingShader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/9/27 14:06
 * TODO: 三维地面，兼容平面数据改动对象
 */
public class Ground3D extends RendererObject {

    private Ground ground;
    private boolean needBindTexture;

    private void initBuffers() {
        indices = ground.getIndices();
        texcoord = ground.getUV();
        float[] gvs = ground.getVertexs();
        int size = indices.length;
        vertexs = new float[3 * size];
        colors = new float[4 * size];
        normals = new float[3 * size];
        lj3DPointsList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            int index = 3 * i;
            vertexs[index] = gvs[index];
            vertexs[index + 1] = gvs[index + 2];
            vertexs[index + 2] = gvs[index + 1];
            lj3DPointsList.add(new LJ3DPoint(vertexs[index], vertexs[index + 1], vertexs[index + 2]));
            normals[index] = 0.0f;
            normals[index + 1] = 1.0f;
            normals[index + 2] = 0.0f;
            int colorIndex = 4 * i;
            colors[colorIndex] = 0.5f;
            colors[colorIndex + 1] = 0.5f;
            colors[colorIndex + 2] = 0.5f;
            colors[colorIndex + 3] = 1.0f;
        }
        vertexsBuffer = ByteBuffer.allocateDirect(4 * vertexs.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexsBuffer.put(vertexs).position(0);
        normalsBuffer = ByteBuffer.allocateDirect(4 * normals.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        normalsBuffer.put(normals).position(0);
        texcoordBuffer = ByteBuffer.allocateDirect(4 * texcoord.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        texcoordBuffer.put(texcoord).position(0);
        colorsBuffer = ByteBuffer.allocateDirect(4 * colors.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        colorsBuffer.put(colors).position(0);
        uuid = ground.uuid + "_3D";
        needBindTexture = true;
    }

    public Ground3D(Ground ground) {
        this.ground = ground;
        initBuffers();
    }

    public void setNeedBindTexture(boolean needBindTexture) {
        this.needBindTexture = needBindTexture;
    }

    @Override
    public void render(int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition) {
        if (needBindTexture) {
            needBindTexture = false;
            // 绑定纹理
            textureId = createTexture3DIdAndCache(uuid, ground.getBitmap(), true);
            refreshShadowRender();
        } else {
            // 无效返回
            if (textureId == -1)
                return;
            // 关闭混色
            GLES30.glDisable(GLES30.GL_BLEND);
            // Pass position information to shader
            GLES30.glVertexAttribPointer(positionAttribute, 3, GLES30.GL_FLOAT, false,
                    0, vertexsBuffer);
            GLES30.glEnableVertexAttribArray(positionAttribute);
            if (!onlyPosition) {
                // uv
                GLES30.glVertexAttribPointer(ShadowViewingShader.scene_uv0, 2, GLES30.GL_FLOAT, false, 8, texcoordBuffer);
                GLES30.glEnableVertexAttribArray(ShadowViewingShader.scene_uv0);
                // Pass normal information to shader
                GLES30.glVertexAttribPointer(normalAttribute, 3, GLES30.GL_FLOAT, false,
                        0, normalsBuffer);
                GLES30.glEnableVertexAttribArray(normalAttribute);
                // Pass color information to shader
                GLES30.glVertexAttribPointer(colorAttribute, 4, GLES30.GL_FLOAT, false,
                        0, colorsBuffer);
                GLES30.glEnableVertexAttribArray(colorAttribute);
                // 贴图
                GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT);
                GLES30.glUniform1i(ShadowViewingShader.scene_baseMap, 0);
                GLES30.glUniform1f(ShadowViewingShader.scene_texture_flags, 1.0f);
            }
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, indices.length);
        }
    }

}
