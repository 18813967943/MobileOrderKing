package com.lejia.mobile.orderking.hk3d.datas_3d.classes;

import android.opengl.GLES30;

import com.lejia.mobile.orderking.hk3d.RendererState;
import com.lejia.mobile.orderking.hk3d.classes.LJ3DPoint;
import com.lejia.mobile.orderking.hk3d.datas_3d.tools.Scaling;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Author by HEKE
 *
 * @time 2018/12/8 11:45
 * TODO: 墙体切割对象
 */
public class BuildingFragmentWall extends Render3DObject {

    private ArrayList<LJ3DPoint> lj3DPointArrayList; // 三维原始围点

    public BuildingFragmentWall(ArrayList<LJ3DPoint> lj3DPointArrayList, float[] texcoords) {
        this.lj3DPointArrayList = lj3DPointArrayList;
        this.texcoord = texcoords;
        this.uuid = UUID.randomUUID().toString();
        initBuffers();
    }

    private void initBuffers() {
        indices = new short[]{0, 1, 2, 0, 2, 3};
        vertexs = new float[3 * indices.length];
        for (int i = 0; i < indices.length; i++) {
            LJ3DPoint point = lj3DPointArrayList.get(indices[i]);
            float x = Scaling.scaleSimpleValue((float) point.x);
            float y = Scaling.scaleSimpleValue((float) point.z);
            float z = Scaling.scaleSimpleValue((float) point.y);
            int index = 3 * i;
            vertexs[index] = x;
            vertexs[index + 1] = y;
            vertexs[index + 2] = z;
        }
        vertexsBuffer = ByteBuffer.allocateDirect(4 * vertexs.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexsBuffer.put(vertexs).position(0);
        texcoordBuffer = ByteBuffer.allocateDirect(4 * texcoord.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        texcoordBuffer.put(texcoord).position(0);
    }

    /**
     * 碎片渲染
     *
     * @param positionAttribute
     * @param normalAttribute
     * @param colorAttribute
     * @param onlyPosition
     * @param textureId
     * @param normalsBuffer
     */
    public void render(int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition, int textureId, FloatBuffer normalsBuffer) {
        if (textureId != -1) {
            this.textureId = textureId;
            // Pass position information to shader
            vertexsBuffer.position(0);
            GLES30.glVertexAttribPointer(positionAttribute, 3, GLES30.GL_FLOAT, false,
                    0, vertexsBuffer);
            GLES30.glEnableVertexAttribArray(positionAttribute);
            if (!onlyPosition) {
                // Pass normal information to shader
                normalsBuffer.position(0);
                GLES30.glVertexAttribPointer(normalAttribute, 3, GLES30.GL_FLOAT, false,
                        0, normalsBuffer);
                GLES30.glEnableVertexAttribArray(normalAttribute);
                if (this.textureId != -1) {
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
                    if (RendererState.isNot3D()) {
                        GLES30.glUniform1f(mRenderer.scene_uSpecular, 0.5f);
                    } else {
                        GLES30.glUniform1f(mRenderer.scene_uSpecular, 0.65f);
                    }
                }
            }
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, indices.length);
        }
    }

    @Override
    @Deprecated
    public void render(int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition) {
    }

}
