package com.lejia.mobile.orderking.hk3d.datas_3d.classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES30;

import com.lejia.mobile.orderking.hk3d.datas_2d.RendererObject;
import com.lejia.mobile.orderking.hk3d.datas_3d.ShadowViewingShader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class CourtyardGround extends RendererObject {

    private final FloatBuffer planePosition;
    private final FloatBuffer planeNormal;
    private final FloatBuffer planeColor;

    private FloatBuffer texcoordBuffer;

    private Context context;

    int half_size = 5000;

    float[] planePositionData = {
            // X, Y, Z,
            -half_size, -5.0f, -half_size,
            -half_size, -5.0f, half_size,
            half_size, -5.0f, -half_size,
            -half_size, -5.0f, half_size,
            half_size, -5.0f, half_size,
            half_size, -5.0f, -half_size
    };

    float[] planeNormalData = {
            // nX, nY, nZ
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f
    };

    float[] planeColorData = {
            // R, G, B, A
            0.5f, 0.5f, 0.5f, 1.0f,
            0.5f, 0.5f, 0.5f, 1.0f,
            0.5f, 0.5f, 0.5f, 1.0f,
            0.5f, 0.5f, 0.5f, 1.0f,
            0.5f, 0.5f, 0.5f, 1.0f,
            0.5f, 0.5f, 0.5f, 1.0f
    };

    float[] texcoords = {
            0.0f, 0.0f,
            20.0f, 0.0f,
            20.0f, 20.0f,
            0.0f, 0.0f,
            20.0f, 20.0f,
            0.0f, 20.0f
    };

    boolean needBindTexture;
    int textureId = -1;
    private Bitmap bitmap;

    public CourtyardGround(Context context) {
        this.context = context;
        // Buffer initialization
        ByteBuffer bPos = ByteBuffer.allocateDirect(planePositionData.length * 4);
        bPos.order(ByteOrder.nativeOrder());
        planePosition = bPos.asFloatBuffer();

        ByteBuffer bNormal = ByteBuffer.allocateDirect(planeNormalData.length * 4);
        bNormal.order(ByteOrder.nativeOrder());
        planeNormal = bNormal.asFloatBuffer();

        ByteBuffer bColor = ByteBuffer.allocateDirect(planeColorData.length * 4);
        bColor.order(ByteOrder.nativeOrder());
        planeColor = bColor.asFloatBuffer();

        ByteBuffer bTex = ByteBuffer.allocateDirect(texcoords.length * 4);
        bTex.order(ByteOrder.nativeOrder());
        texcoordBuffer = bTex.asFloatBuffer();

        planePosition.put(planePositionData).position(0);
        planeNormal.put(planeNormalData).position(0);
        planeColor.put(planeColorData).position(0);
        texcoordBuffer.put(texcoords).position(0);

        // 贴图
        uuid = "normal_plot";
        if (bitmap == null) {
            bitmap = createTextureWithAssets("textures/" + uuid + ".jpg");
            needBindTexture = true;
        }
        needBindTexture = true;
    }

    /**
     * 外院地面一半宽高，作为平行灯光渲染的半径
     */
    public int getHalf_size() {
        return half_size;
    }

    public void render(int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition) {
        if (needBindTexture) {
            needBindTexture = false;
            // 绑定纹理
            textureId = createTexture3DIdAndCache(uuid, bitmap, false);
            refreshShadowRender();
        } else {
            if (textureId == -1)
                return;
            // 关闭混色
            GLES30.glDisable(GLES30.GL_BLEND);
            // Pass position information to shader
            GLES30.glVertexAttribPointer(positionAttribute, 3, GLES30.GL_FLOAT, false,
                    0, planePosition);
            GLES30.glEnableVertexAttribArray(positionAttribute);
            if (!onlyPosition) {
                // uv
                GLES30.glVertexAttribPointer(ShadowViewingShader.scene_uv0, 2, GLES30.GL_FLOAT, false, 8, texcoordBuffer);
                GLES30.glEnableVertexAttribArray(ShadowViewingShader.scene_uv0);
                // Pass normal information to shader
                GLES30.glVertexAttribPointer(normalAttribute, 3, GLES30.GL_FLOAT, false,
                        0, planeNormal);
                GLES30.glEnableVertexAttribArray(normalAttribute);
                // Pass color information to shader
                GLES30.glVertexAttribPointer(colorAttribute, 4, GLES30.GL_FLOAT, false,
                        0, planeColor);
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
            // Draw the plane
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 6);
        }
    }
}