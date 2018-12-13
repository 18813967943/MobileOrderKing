package com.lejia.mobile.orderking.hk3d.datas_3d.classes;

import android.graphics.Bitmap;
import android.opengl.GLES30;

import com.lejia.mobile.orderking.hk3d.classes.LJ3DPoint;
import com.lejia.mobile.orderking.hk3d.classes.Texture;
import com.lejia.mobile.orderking.hk3d.datas_2d.Ground;
import com.lejia.mobile.orderking.hk3d.datas_2d.TexturesCache;
import com.lejia.mobile.orderking.hk3d.datas_3d.common.RenderConstants;
import com.lejia.mobile.orderking.hk3d.datas_3d.tools.Scaling;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/11/8 15:27
 * TODO: 三维房间地面对象
 */
public class BuildingGround extends Render3DObject {

    private Ground ground;
    private Bitmap textureBitmap;

    private boolean needBindTexture;

    public BuildingGround(Ground ground) {
        this.ground = ground;
        initDatas();
    }

    private void initDatas() {
        if (ground == null)
            return;
        indices = ground.getIndices();
        texcoord = ground.getUV();
        float[] vs = ground.getVertexs();
        vertexs = new float[vs.length];
        normals = new float[vs.length];
        int size = vs.length / 3;
        lj3DPointsList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            int index = 3 * i;
            vertexs[index] = Scaling.scaleSimpleValue(vs[index]);
            vertexs[index + 1] = Scaling.scaleSimpleValue(vs[index + 2]);
            vertexs[index + 2] = Scaling.scaleSimpleValue(vs[index + 1]);
            lj3DPointsList.add(new LJ3DPoint(vertexs[index], vertexs[index + 1], vertexs[index + 2]));
            normals[index] = 0;
            normals[index + 1] = 1;
            normals[index + 2] = 0;
        }
        vertexsBuffer = ByteBuffer.allocateDirect(RenderConstants.FLOAT_SIZE_IN_BYTES * vertexs.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexsBuffer.put(vertexs).position(0);
        texcoordBuffer = ByteBuffer.allocateDirect(RenderConstants.FLOAT_SIZE_IN_BYTES * texcoord.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        texcoordBuffer.put(texcoord).position(0);
        normalsBuffer = ByteBuffer.allocateDirect(RenderConstants.FLOAT_SIZE_IN_BYTES * normals.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        normalsBuffer.put(normals).position(0);
        uuid = ground.getUUID() + "_BD";
    }

    public void setNeedBindTexture(boolean needBindTexture) {
        this.needBindTexture = needBindTexture;
        if (needBindTexture)
            textureId = -1;
        refreshRender();
    }

    /**
     * 加载材质
     */
    public void loadTexture() {
        if (ground == null)
            return;
        Texture texture = TexturesCache.get(uuid);
        if (texture != null && !needBindTexture) {
            textureId = texture.textureId;
            textureBitmap = texture.bitmap;
        } else {
            textureBitmap = ground.getBitmap();
            textureId = createTextureIdAndCache(uuid, textureBitmap, true);
            needBindTexture = false;
        }
        refreshRender();
    }

    @Override
    public void render(int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition) {
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
            if (textureId != -1) {
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
                GLES30.glUniform1f(mRenderer.scene_uSpecular, -0.45f);
            } else {
                loadTexture();
            }
        }
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, indices.length);
    }

}
