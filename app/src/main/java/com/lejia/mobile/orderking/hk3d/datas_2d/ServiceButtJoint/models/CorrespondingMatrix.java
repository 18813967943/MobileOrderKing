package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.models;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.lejia.mobile.orderking.hk3d.datas_3d.ShadowsRenderer;
import com.lejia.mobile.orderking.hk3d.datas_3d.tools.Scaling;

/**
 * Author by HEKE
 *
 * @time 2018/12/4 17:31
 * TODO: 对应的矩阵数据对象
 */
public class CorrespondingMatrix {

    private String hashCode; // 模型hashcode
    private TopView topView; // 模型对应的数据对象

    public CorrespondingMatrix(String hashCode, TopView topView) {
        this.hashCode = hashCode;
        this.topView = topView;
    }

    /**
     * 设置家具对应的模型矩阵
     *
     * @param shadowsRenderer
     * @param shadow
     */
    public void renderSetMatrixs(ShadowsRenderer shadowsRenderer, boolean shadow) {
        if (shadowsRenderer == null || topView == null)
            return;
        // 矩阵
        float[] correspondingMatrix = new float[16];
        Matrix.setIdentityM(correspondingMatrix, 0);
        float transX = Scaling.scaleSimpleValue((float) (topView.adi.point.x));
        float transY = Scaling.scaleSimpleValue((float) (topView.adi.point.y));
        Matrix.translateM(correspondingMatrix, 0, transX,
                Scaling.scaleSimpleValue(topView.xInfo.offGround * 0.1f), transY);
        if (topView.mirror) {
            Matrix.scaleM(correspondingMatrix, 0, -1.0f, 1.0f, 1.0f);
        } else {
            Matrix.scaleM(correspondingMatrix, 0, 1.0f, 1.0f, 1.0f);
        }
        Matrix.rotateM(correspondingMatrix, 0, -topView.adi.angle, 0.0f, 1.0f, 0.0f);
        float[] tempResultMatrix = new float[16];
        // 阴影
        if (shadow) {
            // Rotate the model matrix with current rotation matrix
            Matrix.multiplyMM(tempResultMatrix, 0, shadowsRenderer.mModelMatrix, 0, correspondingMatrix, 0);
            // View matrix * Model matrix value is stored
            Matrix.multiplyMM(shadowsRenderer.mLightMvpMatrix_dynamicShapes, 0, shadowsRenderer.mLightViewMatrix, 0, tempResultMatrix, 0);
            // Model * view * projection matrix stored and copied for use at rendering from camera point of view
            Matrix.multiplyMM(tempResultMatrix, 0, shadowsRenderer.mLightProjectionMatrix, 0, shadowsRenderer.mLightMvpMatrix_dynamicShapes, 0);
            System.arraycopy(tempResultMatrix, 0, shadowsRenderer.mLightMvpMatrix_dynamicShapes, 0, 16);
            // Pass in the combined matrix.
            GLES20.glUniformMatrix4fv(shadowsRenderer.shadow_mvpMatrixUniform, 1, false, shadowsRenderer.mLightMvpMatrix_dynamicShapes, 0);
        }
        // 实体
        else {
            float[] depthBiasMVP = new float[16];
            float bias[] = new float[]{
                    0.5f, 0.0f, 0.0f, 0.0f,
                    0.0f, 0.5f, 0.0f, 0.0f,
                    0.0f, 0.0f, 0.5f, 0.0f,
                    0.5f, 0.5f, 0.5f, 1.0f};
            // Rotate the model matrix with current rotation matrix
            Matrix.multiplyMM(tempResultMatrix, 0, shadowsRenderer.mModelMatrix, 0, correspondingMatrix, 0);
            //calculate MV matrix
            Matrix.multiplyMM(tempResultMatrix, 0, shadowsRenderer.mViewMatrix, 0, tempResultMatrix, 0);
            System.arraycopy(tempResultMatrix, 0, shadowsRenderer.mMVMatrix, 0, 16);
            //pass in MV Matrix as uniform
            GLES20.glUniformMatrix4fv(shadowsRenderer.scene_mvMatrixUniform, 1, false, shadowsRenderer.mMVMatrix, 0);
            //calculate Normal Matrix as uniform (invert transpose MV)
            Matrix.invertM(tempResultMatrix, 0, shadowsRenderer.mMVMatrix, 0);
            Matrix.transposeM(shadowsRenderer.mNormalMatrix, 0, tempResultMatrix, 0);
            //pass in Normal Matrix as uniform
            GLES20.glUniformMatrix4fv(shadowsRenderer.scene_normalMatrixUniform, 1, false, shadowsRenderer.mNormalMatrix, 0);
            //calculate MVP matrix
            Matrix.multiplyMM(tempResultMatrix, 0, shadowsRenderer.mProjectionMatrix, 0, shadowsRenderer.mMVMatrix, 0);
            System.arraycopy(tempResultMatrix, 0, shadowsRenderer.mMVPMatrix, 0, 16);
            //pass in MVP Matrix as uniform
            GLES20.glUniformMatrix4fv(shadowsRenderer.scene_mvpMatrixUniform, 1, false, shadowsRenderer.mMVPMatrix, 0);
            if (shadowsRenderer.mHasDepthTextureExtension) {
                Matrix.multiplyMM(depthBiasMVP, 0, bias, 0, shadowsRenderer.mLightMvpMatrix_dynamicShapes, 0);
                System.arraycopy(depthBiasMVP, 0, shadowsRenderer.mLightMvpMatrix_dynamicShapes, 0, 16);
            }
            //MVP matrix that was used during depth map render
            GLES20.glUniformMatrix4fv(shadowsRenderer.scene_schadowProjMatrixUniform, 1, false, shadowsRenderer.mLightMvpMatrix_dynamicShapes, 0);
        }
    }

}
