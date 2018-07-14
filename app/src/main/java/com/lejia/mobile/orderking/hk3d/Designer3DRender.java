package com.lejia.mobile.orderking.hk3d;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Author by HEKE
 *
 * @time 2018/7/9 17:01
 * TODO: 渲染管理对象
 */
public class Designer3DRender implements GLSurfaceView.Renderer {

    private Context mContext;
    private OnRenderStatesListener onRenderStatesListener;

    /**
     * 视图宽高
     */
    private int mDisplayWidth;
    private int mDisplayHeight;

    /**
     * 阴影视图宽高
     */
    private int mShadowMapWidth;
    private int mShadowMapHeight;

    /**
     * 摄像机等信息
     */
    private float eyeX;
    private float eyeY;
    private float eyeZ;

    /**
     * FBO
     */
    int[] fboId;
    int[] depthTextureId;
    int[] renderTextureId;

    public Designer3DRender(Context context, OnRenderStatesListener onRenderStatesListener) {
        this.mContext = context;
        this.onRenderStatesListener = onRenderStatesListener;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES30.glEnable(GLES20.GL_DEPTH_TEST);
        GLES30.glEnable(GLES20.GL_CULL_FACE);
        Matrix.setLookAtM(ViewingMatrixs.mViewMatrix, 0, eyeX, eyeY, eyeZ,
                0, 0, 0, 0, 1, 0);
        // 加载着色器
        ViewingShader.loadShader(mContext);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {

    }


}
