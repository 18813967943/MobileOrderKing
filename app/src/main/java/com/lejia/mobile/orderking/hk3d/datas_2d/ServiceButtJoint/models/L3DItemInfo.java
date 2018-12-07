package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.models;

import android.graphics.Bitmap;
import android.opengl.GLES30;

import com.lejia.mobile.orderking.hk3d.classes.LJ3DPoint;
import com.lejia.mobile.orderking.hk3d.datas_3d.classes.Render3DObject;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Author by HEKE
 *
 * @time 2018/12/6 9:37
 * TODO: 三维模型子件
 */
public class L3DItemInfo extends Render3DObject {

    public Bitmap diffuseBitmap; // 光照贴图
    private boolean needBindTexture;

    public void loadBuffer() {
        vertexsBuffer = ByteBuffer.allocateDirect(4 * vertexs.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexsBuffer.put(vertexs).position(0);
        if (normals == null) {
            normals = new float[vertexs.length];
            int size = vertexs.length / 9;
            for (int i = 0; i < size; i++) {
                int index = 9 * i;
                LJ3DPoint normal = LJ3DPoint.spaceNormal(vertexs[index], vertexs[index + 1], vertexs[index + 2], vertexs[index + 3],
                        vertexs[index + 4], vertexs[index + 5], vertexs[index + 6], vertexs[index + 7], vertexs[index + 8]);
                normals[index] = (float) normal.x;
                normals[index + 1] = (float) normal.y;
                normals[index + 2] = (float) normal.z;
                normals[index + 3] = (float) normal.x;
                normals[index + 4] = (float) normal.y;
                normals[index + 5] = (float) normal.z;
                normals[index + 6] = (float) normal.x;
                normals[index + 7] = (float) normal.y;
                normals[index + 8] = (float) normal.z;
            }
        }
        normalsBuffer = ByteBuffer.allocateDirect(4 * normals.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        normalsBuffer.put(normals).position(0);
        texcoordBuffer = ByteBuffer.allocateDirect(4 * texcoord.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        texcoordBuffer.put(texcoord).position(0);
        indicesBuffer = ByteBuffer.allocateDirect(2 * indices.length).order(ByteOrder.nativeOrder()).asShortBuffer();
        indicesBuffer.put(indices).position(0);
        needBindTexture = true;
    }

    @Override
    public void render(int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition) {
        if (textureId == -1) {
            if (needBindTexture) {
                needBindTexture = false;
                textureId = createTextureIdAndCache(textureName, diffuseBitmap, false);
                refreshRender();
            }
        } else {
            // Pass position information to shader
            vertexsBuffer.position(0);
            GLES30.glVertexAttribPointer(positionAttribute, 3, GLES30.GL_FLOAT, false,
                    0, vertexsBuffer);
            GLES30.glEnableVertexAttribArray(positionAttribute);
            if (!onlyPosition) {
                // Pass normal information to shader
                if (normalsBuffer != null) {
                    normalsBuffer.position(0);
                    GLES30.glVertexAttribPointer(normalAttribute, 3, GLES30.GL_FLOAT, false,
                            0, normalsBuffer);
                    GLES30.glEnableVertexAttribArray(normalAttribute);
                }
                // texcoord
                texcoordBuffer.position(0);
                GLES30.glVertexAttribPointer(mRenderer.scene_texcoordAttribute, 2, GLES30.GL_FLOAT, false,
                        0, texcoordBuffer);
                GLES30.glEnableVertexAttribArray(mRenderer.scene_texcoordAttribute);
                // map
                GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT);
                GLES30.glUniform1i(mRenderer.scene_SbaseMapUniform, 0);
                GLES30.glUniform1f(mRenderer.scene_useSkinTexcoord_flag, 1.0f);
                GLES30.glUniform1f(mRenderer.scene_uSpecular, 0.3f);
            }
            GLES30.glDrawElements(GLES30.GL_TRIANGLES, indices.length, GLES30.GL_UNSIGNED_SHORT,
                    indicesBuffer);
        }
    }

}
