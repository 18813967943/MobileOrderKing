package com.lejia.mobile.orderking.hk3d.datas_3d;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import com.lejia.mobile.orderking.R;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ShadowRenderer implements GLSurfaceView.Renderer {

    private Context mContext;

    private final float[] mMVPMatrix = new float[16]; // 渲染总矩阵
    private final float[] mMVMatrix = new float[16]; // 视图模型矩阵
    private final float[] mNormalMatrix = new float[16]; // 法线矩阵
    private final float[] mProjectionMatrix = new float[16]; // 投影矩阵
    private final float[] mViewMatrix = new float[16]; // 视图矩阵
    private final float[] mModelMatrix = new float[16]; // 模型矩阵

    /**
     * MVP matrix used at rendering shadow map for stationary objects
     */
    private final float[] mLightMvpMatrix_staticShapes = new float[16]; // 阴影着色器总矩阵

    /**
     * Projection matrix from point of light source
     */
    private final float[] mLightProjectionMatrix = new float[16]; // 光线投影矩阵

    /**
     * View matrix of light source
     */
    private final float[] mLightViewMatrix = new float[16]; // 光线视图矩阵

    /**
     * Position of light source in eye space
     */
    private final float[] mLightPosInEyeSpace = new float[16]; // 实时光线位置

    /**
     * Light source position in model space
     */
    private final float[] mLightPosModel = new float[] // 光线初始位置
            {-5.0f, 9.0f, 0.0f, 1.0f};

    private float[] mActualLightPosition = new float[4]; // 实时光线位置


    public ShadowRenderer(Context context) {
        mContext = context;
    }

    /**
     * The vertex and fragment shader to render depth map
     */
    private RenderProgram mDepthMapProgram;

    /**
     * Handles to vertex and fragment shader programs
     */
    private RenderProgram mSimpleShadowProgram;

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        //Set the background frame color
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        //Enable depth testing
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        GLES30.glEnable(GLES30.GL_CULL_FACE);

        //Set view matrix from eye position
        Matrix.setLookAtM(mViewMatrix, 0,
                //eyeX, eyeY, eyeZ,
                0, 4, -12,
                //lookX, lookY, lookZ,
                0, 0, 0,
                //upX, upY, upZ
                0, 1, 0);

        // OES_depth_texture is available -> shaders are simplier
        mSimpleShadowProgram = new RenderProgram(R.raw.depth_tex_v_with_shadow,
                R.raw.depth_tex_f_with_simple_shadow, mContext);  // TODO 着色器
        mDepthMapProgram = new RenderProgram(R.raw.depth_tex_v_depth_map,
                R.raw.depth_tex_f_depth_map, mContext); // TODO 阴影着色器
        ViewingShader.loadShader(mSimpleShadowProgram.getProgram());
        ViewingShader.loadShadowShader(mDepthMapProgram.getProgram());
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        mDisplayWidth = width;
        mDisplayHeight = height;
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, mDisplayWidth, mDisplayHeight);

        // Generate buffer where depth values are saved for shadow calculation
        generateShadowFBO();

        float ratio = (float) mDisplayWidth / mDisplayHeight;

        // this projection matrix is applied at rendering scene
        // in the onDrawFrame() method
        float bottom = -1.0f;
        float top = 1.0f;
        float near = 1.0f;
        float far = 20000.0f;

        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, bottom, top, near, far);

        // this projection matrix is used at rendering shadow map
        Matrix.frustumM(mLightProjectionMatrix, 0, -1.1f * ratio, 1.1f * ratio, 1.1f * bottom, 1.1f * top, near, far);
    }

    /**
     * Current display sizes
     */
    private int mDisplayWidth;
    private int mDisplayHeight;

    /**
     * Current shadow map sizes
     */
    private int mShadowMapWidth;
    private int mShadowMapHeight;

    int[] fboId;
    int[] depthTextureId;
    int[] renderTextureId;

    /**
     * Sets up the framebuffer and renderbuffer to render to texture
     */
    public void generateShadowFBO() {
        mShadowMapWidth = mDisplayWidth;
        mShadowMapHeight = mDisplayHeight;

        fboId = new int[1];
        depthTextureId = new int[1];
        renderTextureId = new int[1];

        // create a framebuffer object
        GLES30.glGenFramebuffers(1, fboId, 0);

        // create render buffer and bind 16-bit depth buffer
        GLES30.glGenRenderbuffers(1, depthTextureId, 0);
        GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, depthTextureId[0]);
        GLES30.glRenderbufferStorage(GLES30.GL_RENDERBUFFER, GLES30.GL_DEPTH_COMPONENT16, mShadowMapWidth, mShadowMapHeight);

        // Try to use a texture depth component
        GLES30.glGenTextures(1, renderTextureId, 0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, renderTextureId[0]);

        // GL_LINEAR does not make sense for depth texture. However, next tutorial shows usage of GL_LINEAR and PCF. Using GL_NEAREST
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_NEAREST);

        // Remove artifact on the edges of the shadowmap
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);

        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, fboId[0]);
        // Use a depth texture
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_DEPTH_COMPONENT, mShadowMapWidth, mShadowMapHeight,
                0, GLES30.GL_DEPTH_COMPONENT, GLES30.GL_UNSIGNED_INT, null);
        // Attach the depth texture to FBO depth attachment point
        GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_DEPTH_ATTACHMENT, GLES30.GL_TEXTURE_2D, renderTextureId[0], 0);
        // check FBO status
        int FBOstatus = GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER);
        if (FBOstatus != GLES30.GL_FRAMEBUFFER_COMPLETE) {
            Log.e("MyRender", "GL_FRAMEBUFFER_COMPLETE failed, CANNOT use FBO");
            throw new RuntimeException("GL_FRAMEBUFFER_COMPLETE failed, CANNOT use FBO");
        }
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        /** 旋转灯光位置 **/
        long elapsedMilliSec = SystemClock.elapsedRealtime();
        long rotationCounter = elapsedMilliSec % 12000L;
        float lightRotationDegree = (360.0f / 12000.0f) * ((int) rotationCounter);
        float[] rotationMatrix = new float[16];
        Matrix.setIdentityM(rotationMatrix, 0);
        Matrix.rotateM(rotationMatrix, 0, lightRotationDegree, 0.0f, 1.0f, 0.0f);
        Matrix.multiplyMV(mActualLightPosition, 0, rotationMatrix, 0, mLightPosModel, 0);
        //mActualLightPosition = mLightPosModel.clone();
        Matrix.setIdentityM(mModelMatrix, 0);
        /** 设置镜头 **/
        //Set view matrix from light source position
        Matrix.setLookAtM(mLightViewMatrix, 0,
                //lightX, lightY, lightZ,
                mActualLightPosition[0], mActualLightPosition[1], mActualLightPosition[2],
                //lookX, lookY, lookZ,
                //look in direction -y
                mActualLightPosition[0], -mActualLightPosition[1], mActualLightPosition[2],
                //upX, upY, upZ
                //up vector in the direction of axisY
                -mActualLightPosition[0], 0, -mActualLightPosition[2]);
        /** 渲染阴影 **/
        GLES30.glCullFace(GLES30.GL_FRONT);
        renderShadowMap();
        /** 渲染实景 **/
        GLES30.glCullFace(GLES30.GL_BACK);
        renderScene();

        // Print openGL errors to console
        int debugInfo = GLES20.glGetError();
        if (debugInfo != GLES20.GL_NO_ERROR) {
            String msg = "OpenGL error: " + debugInfo;
            Log.w("Render", msg);
        }
    }

    private void renderShadowMap() {
        // bind the generated framebuffer
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, fboId[0]);

        GLES30.glViewport(0, 0, mShadowMapWidth,
                mShadowMapHeight);

        // Clear color and buffers
        GLES30.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);

        // Start using the shader
        GLES30.glUseProgram(mDepthMapProgram.getProgram());

        float[] tempResultMatrix = new float[16];

        // Calculate matrices for standing objects

        // View matrix * Model matrix value is stored
        Matrix.multiplyMM(mLightMvpMatrix_staticShapes, 0, mLightViewMatrix, 0, mModelMatrix, 0);

        // Model * view * projection matrix stored and copied for use at rendering from camera point of view
        Matrix.multiplyMM(tempResultMatrix, 0, mLightProjectionMatrix, 0, mLightMvpMatrix_staticShapes, 0);
        System.arraycopy(tempResultMatrix, 0, mLightMvpMatrix_staticShapes, 0, 16);

        // Pass in the combined matrix.
        GLES30.glUniformMatrix4fv(ViewingShader.shadow_mvpMatrixUniform, 1, false, mLightMvpMatrix_staticShapes, 0);

        // Render all stationary shapes on scene
        //mPlane.render(ViewingShader.shadow_positionAttribute, 0, 0, true);
    }

    private void renderScene() {
        // bind default framebuffer
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        GLES30.glUseProgram(mSimpleShadowProgram.getProgram());

        GLES30.glViewport(0, 0, mDisplayWidth, mDisplayHeight);

        //pass stepsize to map nearby points properly to depth map texture - used in PCF algorithm
        GLES30.glUniform1f(ViewingShader.scene_mapStepXUniform, (float) (1.0 / mShadowMapWidth));
        GLES30.glUniform1f(ViewingShader.scene_mapStepYUniform, (float) (1.0 / mShadowMapHeight));

        float[] tempResultMatrix = new float[16];

        float bias[] = new float[]{
                0.5f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.5f, 0.0f,
                0.5f, 0.5f, 0.5f, 1.0f};

        float[] depthBiasMVP = new float[16];

        //calculate MV matrix
        Matrix.multiplyMM(tempResultMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        System.arraycopy(tempResultMatrix, 0, mMVMatrix, 0, 16);

        //pass in MV Matrix as uniform
        GLES30.glUniformMatrix4fv(ViewingShader.scene_mvMatrixUniform, 1, false, mMVMatrix, 0);

        //calculate Normal Matrix as uniform (invert transpose MV)
        Matrix.invertM(tempResultMatrix, 0, mMVMatrix, 0);
        Matrix.transposeM(mNormalMatrix, 0, tempResultMatrix, 0);

        //pass in Normal Matrix as uniform
        GLES30.glUniformMatrix4fv(ViewingShader.scene_normalMatrixUniform, 1, false, mNormalMatrix, 0);

        //calculate MVP matrix
        Matrix.multiplyMM(tempResultMatrix, 0, mProjectionMatrix, 0, mMVMatrix, 0);
        System.arraycopy(tempResultMatrix, 0, mMVPMatrix, 0, 16);

        //pass in MVP Matrix as uniform
        GLES30.glUniformMatrix4fv(ViewingShader.scene_mvpMatrixUniform, 1, false, mMVPMatrix, 0);

        Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mActualLightPosition, 0);
        //pass in light source position
        GLES30.glUniform3f(ViewingShader.scene_lightPosUniform, mLightPosInEyeSpace[0], mLightPosInEyeSpace[1], mLightPosInEyeSpace[2]);

        Matrix.multiplyMM(depthBiasMVP, 0, bias, 0, mLightMvpMatrix_staticShapes, 0);
        System.arraycopy(depthBiasMVP, 0, mLightMvpMatrix_staticShapes, 0, 16);

        //MVP matrix that was used during depth map render
        GLES30.glUniformMatrix4fv(ViewingShader.scene_schadowProjMatrixUniform, 1, false, mLightMvpMatrix_staticShapes, 0);

        //pass in texture where depth map is stored
        GLES30.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES30.glBindTexture(GLES20.GL_TEXTURE_2D, renderTextureId[0]);
        GLES30.glUniform1i(ViewingShader.scene_textureUniform, 1);

        // mSmallCube0.render(ViewingShader.scene_positionAttribute, ViewingShader.scene_normalAttribute, ViewingShader.scene_colorAttribute, false);
    }


}
