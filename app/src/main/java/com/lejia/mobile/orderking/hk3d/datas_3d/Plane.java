package com.lejia.mobile.orderking.hk3d.datas_3d;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLES30;

import com.lejia.mobile.orderking.R;
import com.lejia.mobile.orderking.hk3d.datas_3d.classes.Render3DObject;
import com.lejia.mobile.orderking.hk3d.datas_3d.common.RenderConstants;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Author by HEKE
 *
 * @time 2018/11/5 17:56
 * TODO: 用于即时光影展示的地面空间对象。
 * 用于动态将平面数据缩放至此平面内部进行展示。
 */
public class Plane extends Render3DObject {

    private final FloatBuffer planePosition;
    private final FloatBuffer planeNormal;
    private final FloatBuffer planeTexcoord;

    // 半径
    private float radiaus = 50f;

    // bais
    private float bais = -0.05f;

    float[] planePositionData = {
            // X, Y, Z,
            -radiaus, bais, -radiaus,
            -radiaus, bais, radiaus,
            radiaus, bais, -radiaus,
            -radiaus, bais, radiaus,
            radiaus, bais, radiaus,
            radiaus, bais, -radiaus
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

    float[] planUvData = {
            0.0f, 20.0f,
            20.0f, 20.0f,
            20.0f, 0.0f,
            0.0f, 20.0f,
            20.0f, 0.0f,
            0.0f, 0.0f
    };

    public Plane(Context context) {
        indices = new short[]{0, 1, 2, 0, 2, 3};
        // Buffer initialization
        ByteBuffer bPos = ByteBuffer.allocateDirect(planePositionData.length * RenderConstants.FLOAT_SIZE_IN_BYTES);
        bPos.order(ByteOrder.nativeOrder());
        planePosition = bPos.asFloatBuffer();

        ByteBuffer bNormal = ByteBuffer.allocateDirect(planeNormalData.length * RenderConstants.FLOAT_SIZE_IN_BYTES);
        bNormal.order(ByteOrder.nativeOrder());
        planeNormal = bNormal.asFloatBuffer();

        ByteBuffer bTexcoord = ByteBuffer.allocateDirect(planUvData.length * RenderConstants.FLOAT_SIZE_IN_BYTES);
        bTexcoord.order(ByteOrder.nativeOrder());
        planeTexcoord = bTexcoord.asFloatBuffer();

        planePosition.put(planePositionData).position(0);
        planeNormal.put(planeNormalData).position(0);
        planeTexcoord.put(planUvData).position(0);

        run(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = createTextureWithRaw(R.raw.normal_plot);
                textureId = createTextureIdAndCache("Plane", bitmap, true);
            }
        });
    }

    /**
     * 获取展示地面宽高大小半径
     */
    public float getRadiaus() {
        return radiaus;
    }

    /**
     * 获取实际用于显示的区域
     */
    public float getShowContentArea() {
        return 2 * radiaus - 10f;
    }

    public void render(int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition) {
        // Pass position information to shader
        planePosition.position(0);
        GLES20.glVertexAttribPointer(positionAttribute, 3, GLES20.GL_FLOAT, false,
                0, planePosition);
        GLES20.glEnableVertexAttribArray(positionAttribute);
        if (!onlyPosition) {
            // Pass normal information to shader
            planeNormal.position(0);
            GLES20.glVertexAttribPointer(normalAttribute, 3, GLES20.GL_FLOAT, false,
                    0, planeNormal);
            GLES20.glEnableVertexAttribArray(normalAttribute);
            // texcoord
            if (textureId != -1) {
                // texcoord
                planeTexcoord.position(0);
                GLES20.glVertexAttribPointer(mRenderer.scene_texcoordAttribute, 2, GLES20.GL_FLOAT, false,
                        0, planeTexcoord);
                GLES20.glEnableVertexAttribArray(mRenderer.scene_texcoordAttribute);
                // map
                GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT);
                GLES30.glUniform1i(mRenderer.scene_SbaseMapUniform, 0);
                GLES30.glUniform1f(mRenderer.scene_useSkinTexcoord_flag, 1.0f);
                GLES30.glUniform1f(mRenderer.scene_uSpecular, -0.1f);
            }
        }
        // Draw the plane
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
    }
}