package com.lejia.mobile.orderking.hk3d.datas_3d;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import com.lejia.mobile.orderking.R;
import com.lejia.mobile.orderking.bases.OrderKingApplication;
import com.lejia.mobile.orderking.hk3d.Designer3DSurfaceView;
import com.lejia.mobile.orderking.hk3d.RendererState;
import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.classes.PolyM;
import com.lejia.mobile.orderking.hk3d.datas_2d.House;
import com.lejia.mobile.orderking.hk3d.datas_2d.HouseDatasManager;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.models.InterObserver;
import com.lejia.mobile.orderking.hk3d.datas_3d.classes.BuildingGround;
import com.lejia.mobile.orderking.hk3d.datas_3d.classes.BuildingWall;
import com.lejia.mobile.orderking.hk3d.datas_3d.common.FPSCounter;
import com.lejia.mobile.orderking.hk3d.datas_3d.common.RenderConstants;
import com.lejia.mobile.orderking.hk3d.datas_3d.common.RenderProgram;
import com.lejia.mobile.orderking.hk3d.datas_3d.tools.Scaling;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ShadowsRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "ShadowsRenderer";

    private Context mContext;
    private FPSCounter mFPSCounter;

    /**
     * Handles to vertex and fragment shader programs
     */
    private RenderProgram mPCFShadowDynamicBiasProgram;

    /**
     * The vertex and fragment shader to refreshRender depth map
     */
    public RenderProgram mDepthMapProgram;

    private int mActiveProgram;

    public final float[] mMVPMatrix = new float[16];
    public final float[] mMVMatrix = new float[16];
    public final float[] mNormalMatrix = new float[16];
    public final float[] mProjectionMatrix = new float[16];
    public final float[] mViewMatrix = new float[16];
    public final float[] mModelMatrix = new float[16];

    /**
     * MVP matrix used at rendering shadow map for stationary objects
     */
    public final float[] mLightMvpMatrix_staticShapes = new float[16];

    /**
     * MVP matrix used at rendering shadow map for the big cube in the center
     */
    public final float[] mLightMvpMatrix_dynamicShapes = new float[16];

    /**
     * Projection matrix from point of light source
     */
    public final float[] mLightProjectionMatrix = new float[16];

    /**
     * View matrix of light source
     */
    public final float[] mLightViewMatrix = new float[16];

    /**
     * Position of light source in eye space
     */
    public final float[] mLightPosInEyeSpace = new float[16];

    /**
     * Light source position in model space
     */
    private final float[] mLightPosModel = new float[]
            {0.0f, 27.0f, -3.0f, 1.0f};

    public float[] mActualLightPosition = new float[4];

    /**
     * Current display sizes
     */
    private int mDisplayWidth;
    private int mDisplayHeight;

    /**
     * Current shadow map sizes
     */
    public int mShadowMapWidth;
    public int mShadowMapHeight;

    public boolean mHasDepthTextureExtension = false;

    public int[] fboId;
    public int[] depthTextureId;
    public int[] renderTextureId;

    // Uniform locations for scene refreshRender program
    public int scene_mvpMatrixUniform;
    public int scene_mvMatrixUniform;
    public int scene_normalMatrixUniform;
    public int scene_lightPosUniform;
    public int scene_schadowProjMatrixUniform;
    public int scene_textureUniform;
    public int scene_mapStepXUniform;
    public int scene_mapStepYUniform;

    // Shader program attribute locations
    public int scene_positionAttribute;
    public int scene_normalAttribute;
    public int scene_colorAttribute;

    public int scene_SbaseMapUniform;
    public int scene_texcoordAttribute;
    public int scene_useSkinTexcoord_flag;
    public int scene_uSpecular;

    // Uniform locations for shadow refreshRender program
    public int shadow_mvpMatrixUniform;
    public int shadow_positionAttribute;

    // 展示地面
    private Plane mPlane;

    /**
     * 阴影画布大小比例
     */
    private float mShadowMapRatio = 2.0f;

    /**
     * 旋转设置
     */
    public float rotateY;

    /**
     * 平移设置
     */
    public float transX;
    public float transY;
    public float transZ;

    /**
     * 摄像机位置
     */
    public float eyesX;
    public float eyesY;
    public float eyesZ = -12;

    /**
     * 视线
     */
    public float lookX;
    public float lookY;
    public float lookZ = 3000;

    /**
     * 平面数据管理对象
     */
    private HouseDatasManager houseDatasManager;

    // 加载阴影FBO缓存区域标志
    private boolean hadGenerateShadowFBO;

    /**
     * 模型数据管理对象
     */
    private InterObserver interObserver;
    private boolean releaseRequest;

    public ShadowsRenderer(Context context) {
        this.mContext = context;
        if (mContext != null) {
            OrderKingApplication orderKingApplication = (OrderKingApplication) mContext.getApplicationContext();
            Designer3DSurfaceView designer3DSurfaceView = orderKingApplication.getDesigner3DSurfaceView();
            houseDatasManager = designer3DSurfaceView.getDesigner3DRender().getHouseDatasManager();
        }
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        mFPSCounter = new FPSCounter();
        // Test OES_depth_texture extension
        String extensions = GLES30.glGetString(GLES20.GL_EXTENSIONS);
        if (extensions.contains("OES_depth_texture"))
            mHasDepthTextureExtension = true;
        //Enable depth testing
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        //GLES20.glEnable(GLES20.GL_CULL_FACE);
        //ground
        mPlane = new Plane(mContext);
        //Set view matrix from eye position
        Matrix.setLookAtM(mViewMatrix, 0,
                //eyeX, eyeY, eyeZ,
                eyesX, eyesY, eyesZ,
                //lookX, lookY, lookZ,
                0, 0, 0,
                //upX, upY, upZ
                0, 1, 0);
        //Load shaders and create program used by OpenGL for rendering
        if (!mHasDepthTextureExtension) {
            // If there is no OES_depth_texture extension depth values must be coded in rgba texture and later decoded at calculation of shadow
            mPCFShadowDynamicBiasProgram = new RenderProgram(R.raw.v_with_shadow,
                    R.raw.f_with_pcf_shadow_dynamic_bias, mContext);
            mDepthMapProgram = new RenderProgram(R.raw.v_depth_map,
                    R.raw.f_depth_map, mContext);
        } else {
            // OES_depth_texture is available -> shaders are simplier
            mPCFShadowDynamicBiasProgram = new RenderProgram(R.raw.depth_tex_v_with_shadow,
                    R.raw.depth_tex_f_with_pcf_shadow_dynamic_bias, mContext);
            mDepthMapProgram = new RenderProgram(R.raw.depth_tex_v_depth_map,
                    R.raw.depth_tex_f_depth_map, mContext);
        }
        mActiveProgram = mPCFShadowDynamicBiasProgram.getProgram();
        loadShaders();
    }

    /**
     * Sets up the framebuffer and renderbuffer to refreshRender to texture
     */
    public void generateShadowFBO() {
        hadGenerateShadowFBO = true;
        mShadowMapWidth = Math.round(mDisplayWidth * mShadowMapRatio);
        mShadowMapHeight = Math.round(mDisplayHeight * mShadowMapRatio);
        fboId = new int[1];
        depthTextureId = new int[1];
        renderTextureId = new int[1];
        // create a framebuffer object
        GLES20.glGenFramebuffers(1, fboId, 0);
        // create refreshRender buffer and bind 16-bit depth buffer
        GLES20.glGenRenderbuffers(1, depthTextureId, 0);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, depthTextureId[0]);
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, mShadowMapWidth, mShadowMapHeight);
        // Try to use a texture depth component
        GLES20.glGenTextures(1, renderTextureId, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, renderTextureId[0]);
        // GL_LINEAR does not make sense for depth texture. However, next tutorial shows usage of GL_LINEAR and PCF. Using GL_NEAREST
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        // Remove artifact on the edges of the shadowmap
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId[0]);
        if (!mHasDepthTextureExtension) {
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mShadowMapWidth, mShadowMapHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
            // specify texture as color attachment
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, renderTextureId[0], 0);
            // attach the texture to FBO depth attachment point
            // (not supported with gl_texture_2d)
            GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, depthTextureId[0]);
        } else {
            // Use a depth texture
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_DEPTH_COMPONENT, mShadowMapWidth, mShadowMapHeight, 0, GLES20.GL_DEPTH_COMPONENT, GLES20.GL_UNSIGNED_INT, null);
            // Attach the depth texture to FBO depth attachment point
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_TEXTURE_2D, renderTextureId[0], 0);
        }
        // check FBO status
        int FBOstatus = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if (FBOstatus != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            Log.e(TAG, "GL_FRAMEBUFFER_COMPLETE failed, CANNOT use FBO");
            throw new RuntimeException("GL_FRAMEBUFFER_COMPLETE failed, CANNOT use FBO");
        }
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        mDisplayWidth = width;
        mDisplayHeight = height;

        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, mDisplayWidth, mDisplayHeight);

        // Generate buffer where depth values are saved for shadow calculation
        if (!hadGenerateShadowFBO)
            generateShadowFBO();

        float ratio = (float) mDisplayWidth / mDisplayHeight;

        // this projection matrix is applied at rendering scene
        // in the onDrawFrame() method
        float bottom = -1.0f;
        float top = 1.0f;
        float near = 1.0f;
        float far = 1000.0f;

        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, bottom, top, near, far);

        // this projection matrix is used at rendering shadow map
        Matrix.frustumM(mLightProjectionMatrix, 0, -1.1f * ratio, 1.1f * ratio, 1.1f * bottom, 1.1f * top, near, far);
        //Matrix.frustumM(mLightProjectionMatrix, 0, -ratio, ratio, bottom, top, near, far);
    }

    private void loadShaders() {
        setRenderProgram();
        // Set program handles for cube drawing.
        scene_mvpMatrixUniform = GLES20.glGetUniformLocation(mActiveProgram, RenderConstants.MVP_MATRIX_UNIFORM);
        scene_mvMatrixUniform = GLES20.glGetUniformLocation(mActiveProgram, RenderConstants.MV_MATRIX_UNIFORM);
        scene_normalMatrixUniform = GLES20.glGetUniformLocation(mActiveProgram, RenderConstants.NORMAL_MATRIX_UNIFORM);
        scene_lightPosUniform = GLES20.glGetUniformLocation(mActiveProgram, RenderConstants.LIGHT_POSITION_UNIFORM);
        scene_schadowProjMatrixUniform = GLES20.glGetUniformLocation(mActiveProgram, RenderConstants.SHADOW_PROJ_MATRIX);
        scene_textureUniform = GLES20.glGetUniformLocation(mActiveProgram, RenderConstants.SHADOW_TEXTURE);
        scene_SbaseMapUniform = GLES20.glGetUniformLocation(mActiveProgram, RenderConstants.SKIN_TEXTURE);
        scene_positionAttribute = GLES20.glGetAttribLocation(mActiveProgram, RenderConstants.POSITION_ATTRIBUTE);
        scene_normalAttribute = GLES20.glGetAttribLocation(mActiveProgram, RenderConstants.NORMAL_ATTRIBUTE);
        scene_colorAttribute = GLES20.glGetAttribLocation(mActiveProgram, RenderConstants.COLOR_ATTRIBUTE);
        scene_texcoordAttribute = GLES20.glGetAttribLocation(mActiveProgram, RenderConstants.TEX_COORDINATE);
        scene_useSkinTexcoord_flag = GLES20.glGetUniformLocation(mActiveProgram, RenderConstants.HAS_SKIN_FLAG);
        scene_uSpecular = GLES20.glGetUniformLocation(mActiveProgram, RenderConstants.USE_SPECULAR);
        scene_mapStepXUniform = GLES20.glGetUniformLocation(mActiveProgram, RenderConstants.SHADOW_X_PIXEL_OFFSET);
        scene_mapStepYUniform = GLES20.glGetUniformLocation(mActiveProgram, RenderConstants.SHADOW_Y_PIXEL_OFFSET);
        //shadow handles
        int shadowMapProgram = mDepthMapProgram.getProgram();
        shadow_mvpMatrixUniform = GLES20.glGetUniformLocation(shadowMapProgram, RenderConstants.MVP_MATRIX_UNIFORM);
        shadow_positionAttribute = GLES20.glGetAttribLocation(shadowMapProgram, RenderConstants.SHADOW_POSITION_ATTRIBUTE);
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        // Write FPS information to console
        mFPSCounter.logFrame();
        //--------------- calc values common for both renderers
        // light rotates around Y axis in every 12 seconds
        long elapsedMilliSec = SystemClock.elapsedRealtime();
        long rotationCounter = elapsedMilliSec % 12000L;
        float lightRotationDegree = (360.0f / 12000.0f) * ((int) rotationCounter);
        float[] rotationMatrix = new float[16];
        Matrix.setIdentityM(rotationMatrix, 0);
        Matrix.rotateM(rotationMatrix, 0, lightRotationDegree, 0.0f, 1.0f, 0.0f);
        Matrix.multiplyMV(mActualLightPosition, 0, rotationMatrix, 0, mLightPosModel, 0);
        //System.out.println("####### mActualLightPosition : " + mActualLightPosition[0] + " , " + mActualLightPosition[1] + " , " + mActualLightPosition[2]);
        //mActualLightPosition = new float[]{0, 27, -5, 1.0f};
        // set models Matrixs
        setModelMatrix();
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
        // set real time person at
        Matrix.setLookAtM(mViewMatrix, 0,
                //eyeX, eyeY, eyeZ,
                eyesX, eyesY, eyesZ,
                //lookX, lookY, lookZ,
                lookX, lookY, lookZ,
                //upX, upY, upZ
                0, 1, 0);
        //------------------------- refreshRender depth map --------------------------
        renderShadowMap();

        //------------------------- refreshRender scene ------------------------------
        renderScene();

        // Print openGL errors to console
        int debugInfo = GLES20.glGetError();
        if (debugInfo != GLES20.GL_NO_ERROR) {
            String msg = "OpenGL error: " + debugInfo;
            Log.w(TAG, msg);
        }

        // release
        if (releaseRequest) {
            releaseRequest = false;
            release();
        }
    }

    /**
     * 设置变换矩阵
     */
    private void setModelMatrix() {
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, transX, transY, transZ);
        Matrix.scaleM(mModelMatrix, 0, -1.0f, 1.0f, 1.0f);
        Matrix.rotateM(mModelMatrix, 0, -180, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(mModelMatrix, 0, rotateY, 0.0f, 1.0f, 0.0f);
    }

    private void renderShadowMap() {
        // bind the generated framebuffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId[0]);
        GLES20.glViewport(0, 0, mShadowMapWidth,
                mShadowMapHeight);
        // Clear color and buffers
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        // Start using the shader
        GLES20.glUseProgram(mDepthMapProgram.getProgram());
        float[] tempResultMatrix = new float[16];
        // View matrix * Model matrix value is stored
        Matrix.multiplyMM(mLightMvpMatrix_staticShapes, 0, mLightViewMatrix, 0, mModelMatrix, 0);
        // Model * view * projection matrix stored and copied for use at rendering from camera point of view
        Matrix.multiplyMM(tempResultMatrix, 0, mLightProjectionMatrix, 0, mLightMvpMatrix_staticShapes, 0);
        System.arraycopy(tempResultMatrix, 0, mLightMvpMatrix_staticShapes, 0, 16);
        // Pass in the combined matrix.
        GLES20.glUniformMatrix4fv(shadow_mvpMatrixUniform, 1, false, mLightMvpMatrix_staticShapes, 0);
        try {
            // Render all stationary shapes on scene
            mPlane.render(shadow_positionAttribute, 0, 0, true);
            // 仅绘制外墙面阴影
            ArrayList<BuildingWall> insideBuildingWallsList = houseDatasManager.getBuildingWallsMapsWithType(BuildingWall.Type.OUTSIDE);
            if (insideBuildingWallsList != null && insideBuildingWallsList.size() > 0) {
                for (BuildingWall buildingWall : insideBuildingWallsList) {
                    buildingWall.render(shadow_positionAttribute, 0, 0, true);
                }
            }
            // 闭合房间外墙面
            ArrayList<BuildingWall> closeHouseOutsideBuildingWallsList = PolyM.getCloseHousesOutsideBuildingWallsList();
            if (closeHouseOutsideBuildingWallsList != null && closeHouseOutsideBuildingWallsList.size() > 0) {
                for (BuildingWall buildingWall : closeHouseOutsideBuildingWallsList) {
                    buildingWall.render(shadow_positionAttribute, 0, 0, true);
                }
            }
            // 绘制地面
            ArrayList<House> houseArrayList = houseDatasManager.getHousesList();
            if (houseArrayList != null && houseArrayList.size() > 0) {
                for (House house : houseArrayList) {
                    BuildingGround buildingGround = house.ground.getBuildingGround();
                    if (buildingGround != null) {
                        buildingGround.render(shadow_positionAttribute, 0, 0, true);
                    }
                }
            }
            // 绘制模型
            if (interObserver == null) {
                interObserver = ((OrderKingApplication) mContext.getApplicationContext()).getDesigner3DSurfaceView().getInterObserver();
            }
            interObserver.render(shadow_positionAttribute, 0, 0, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void renderScene() {
        // bind default framebuffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUseProgram(mActiveProgram);
        GLES20.glViewport(0, 0, mDisplayWidth, mDisplayHeight);
        //pass stepsize to map nearby points properly to depth map texture - used in PCF algorithm
        GLES20.glUniform1f(scene_mapStepXUniform, (float) (1.0 / mShadowMapWidth));
        GLES20.glUniform1f(scene_mapStepYUniform, (float) (1.0 / mShadowMapHeight));
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
        GLES20.glUniformMatrix4fv(scene_mvMatrixUniform, 1, false, mMVMatrix, 0);
        //calculate Normal Matrix as uniform (invert transpose MV)
        Matrix.invertM(tempResultMatrix, 0, mMVMatrix, 0);
        Matrix.transposeM(mNormalMatrix, 0, tempResultMatrix, 0);
        //pass in Normal Matrix as uniform
        GLES20.glUniformMatrix4fv(scene_normalMatrixUniform, 1, false, mNormalMatrix, 0);
        //calculate MVP matrix
        Matrix.multiplyMM(tempResultMatrix, 0, mProjectionMatrix, 0, mMVMatrix, 0);
        System.arraycopy(tempResultMatrix, 0, mMVPMatrix, 0, 16);
        //pass in MVP Matrix as uniform
        GLES20.glUniformMatrix4fv(scene_mvpMatrixUniform, 1, false, mMVPMatrix, 0);
        Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mActualLightPosition, 0);
        //pass in light source position
        GLES20.glUniform3f(scene_lightPosUniform, mLightPosInEyeSpace[0], mLightPosInEyeSpace[1], mLightPosInEyeSpace[2]);
        if (mHasDepthTextureExtension) {
            Matrix.multiplyMM(depthBiasMVP, 0, bias, 0, mLightMvpMatrix_staticShapes, 0);
            System.arraycopy(depthBiasMVP, 0, mLightMvpMatrix_staticShapes, 0, 16);
        }
        //MVP matrix that was used during depth map refreshRender
        GLES20.glUniformMatrix4fv(scene_schadowProjMatrixUniform, 1, false, mLightMvpMatrix_staticShapes, 0);
        //pass in texture where depth map is stored
        GLES20.glActiveTexture(GLES20.GL_TEXTURE10);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, renderTextureId[0]);
        GLES20.glUniform1i(scene_textureUniform, 10);
        try {
            // 墙内面
            ArrayList<BuildingWall> insideBuildingWallsList = houseDatasManager.getBuildingWallsMapsWithType(BuildingWall.Type.INSIDE);
            if (insideBuildingWallsList != null && insideBuildingWallsList.size() > 0) {
                for (BuildingWall buildingWall : insideBuildingWallsList) {
                    buildingWall.render(scene_positionAttribute, scene_normalAttribute, scene_colorAttribute, false);
                }
            }
            // 闭合房间外墙面
            ArrayList<BuildingWall> closeHouseOutsideBuildingWallsList = PolyM.getCloseHousesOutsideBuildingWallsList();
            if (closeHouseOutsideBuildingWallsList != null && closeHouseOutsideBuildingWallsList.size() > 0) {
                for (BuildingWall buildingWall : closeHouseOutsideBuildingWallsList) {
                    buildingWall.render(scene_positionAttribute, scene_normalAttribute, scene_colorAttribute, false);
                }
            }
            // 未闭合房间墙外面
            ArrayList<BuildingWall> outsideBuildingWallsList = houseDatasManager.getBuildingWallsMapsWithType(BuildingWall.Type.OUTSIDE);
            if (outsideBuildingWallsList != null && outsideBuildingWallsList.size() > 0) {
                for (BuildingWall buildingWall : outsideBuildingWallsList) {
                    buildingWall.render(scene_positionAttribute, scene_normalAttribute, scene_colorAttribute, false);
                }
            }
            // 绘制地面
            ArrayList<House> houseArrayList = houseDatasManager.getHousesList();
            if (houseArrayList != null && houseArrayList.size() > 0) {
                for (House house : houseArrayList) {
                    BuildingGround buildingGround = house.ground.getBuildingGround();
                    if (buildingGround != null) {
                        buildingGround.render(scene_positionAttribute, scene_normalAttribute, scene_colorAttribute, false);
                    }
                }
            }
            // 顶部厚度面
            ArrayList<BuildingWall> topsideBuildingWallsList = houseDatasManager.getBuildingWallsMapsWithType(BuildingWall.Type.TOPSIDE);
            if (topsideBuildingWallsList != null && topsideBuildingWallsList.size() > 0) {
                for (BuildingWall buildingWall : topsideBuildingWallsList) {
                    buildingWall.render(scene_positionAttribute, scene_normalAttribute, scene_colorAttribute, false);
                }
            }
            topsideBuildingWallsList = PolyM.getCloseHousesTopsideBuildingWallsList();
            if (topsideBuildingWallsList != null && topsideBuildingWallsList.size() > 0) {
                for (BuildingWall buildingWall : topsideBuildingWallsList) {
                    buildingWall.render(scene_positionAttribute, scene_normalAttribute, scene_colorAttribute, false);
                }
            }
            // 闭合房间顶面
            if (!RendererState.isNot3D()) {
                ArrayList<BuildingWall> roofBuildingWallsList = houseDatasManager.getBuildingWallsMapsWithType(BuildingWall.Type.ROOF);
                if (roofBuildingWallsList != null && roofBuildingWallsList.size() > 0) {
                    for (BuildingWall buildingWall : roofBuildingWallsList) {
                        buildingWall.render(scene_positionAttribute, scene_normalAttribute, scene_colorAttribute, false);
                    }
                }
            }
            mPlane.render(scene_positionAttribute, scene_normalAttribute, scene_colorAttribute, false);
            // 绘制模型
            if (interObserver != null)
                interObserver.render(scene_positionAttribute, scene_normalAttribute, scene_colorAttribute, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Changes refreshRender program after changes in menu
     */
    private void setRenderProgram() {
        mActiveProgram = mPCFShadowDynamicBiasProgram.getProgram();
    }

    /**
     * 获取所有内外墙面
     */
    public ArrayList<BuildingWall> getTotalBePirecedWallsList() {
        ArrayList<BuildingWall> buildingWallArrayList = new ArrayList<>();
        ArrayList<BuildingWall> insideBuildingWallsList = houseDatasManager.getBuildingWallsMapsWithType(BuildingWall.Type.INSIDE);
        if (insideBuildingWallsList != null && insideBuildingWallsList.size() > 0) {
            buildingWallArrayList.addAll(insideBuildingWallsList);
        }
        ArrayList<BuildingWall> closeHouseOutsideBuildingWallsList = PolyM.getCloseHousesOutsideBuildingWallsList();
        if (closeHouseOutsideBuildingWallsList != null && closeHouseOutsideBuildingWallsList.size() > 0) {
            buildingWallArrayList.addAll(closeHouseOutsideBuildingWallsList);
        }
        ArrayList<BuildingWall> outsideBuildingWallsList = houseDatasManager.getBuildingWallsMapsWithType(BuildingWall.Type.OUTSIDE);
        if (outsideBuildingWallsList != null && outsideBuildingWallsList.size() > 0) {
            buildingWallArrayList.addAll(outsideBuildingWallsList);
        }
        return buildingWallArrayList;
    }

    /**
     * 轴侧展示
     */
    public void axisSide() {
        rotateY = -40;
        eyesX = 0;
        eyesY = 4;
        eyesZ = -12;
        transX = 0;
        transY = -5;
        transZ = 0;
        lookX = 0;
        lookY = 0;
        lookZ = 0;
        refreshRender();
    }

    /**
     * 进入房间
     */
    public void enterHouse() {
        Point point = houseDatasManager.getEnterHouse3DInnerPosition();
        rotateY = 0;
        eyesX = Scaling.scaleSimpleValue((float) point.x);
        eyesY = 0.8f;
        eyesZ = -Scaling.scaleSimpleValue((float) point.y);
        transX = 0;
        transY = -0.4f;
        transZ = 0;
        lookX = 0;
        lookY = 0;
        lookZ = 3000;
        refreshRender();
    }

    /**
     * 移动
     *
     * @param flag
     * @param speed
     */
    public void move(boolean flag, float speed) {
        float v[] = {lookX - eyesX, lookY - eyesY, lookZ - eyesZ};
        if (flag) {
            eyesX += v[0] * speed;
            eyesZ += v[2] * speed;
            lookX += v[0] * speed;
            lookZ += v[2] * speed;
        } else {
            eyesX -= v[0] * speed;
            eyesZ -= v[2] * speed;
            lookX -= v[0] * speed;
            lookZ -= v[2] * speed;
        }
        refreshRender();
    }

    /**
     * 转向
     *
     * @param flag
     * @param speed
     */
    public void turn(boolean flag, float speed) {
        float v[] = {lookX - eyesX, lookY - eyesY, lookZ - eyesZ};
        float radians = speed * 0.5f;
        if (flag) {
            lookX = (float) (eyesX + ((Math.cos(radians) * v[0]) - (Math.sin(radians) * v[2])));
            lookZ = (float) (eyesZ + ((Math.sin(radians) * v[0]) + (Math.cos(radians) * v[2])));
        } else {
            lookX = (float) (eyesX + ((Math.cos(-radians) * v[0]) - (Math.sin(-radians) * v[2])));
            lookZ = (float) (eyesZ + ((Math.sin(-radians) * v[0]) + (Math.cos(-radians) * v[2])));
        }
        refreshRender();
    }

    /**
     * 平移
     *
     * @param tx
     * @param tz
     */
    public void trans(float tx, float tz) {
        transX += Scaling.scaleSimpleValue(tx);
        transY += Scaling.scaleSimpleValue(tz);
        refreshRender();
    }

    /**
     * 旋转Y轴
     *
     * @param turnLeft
     */
    public void rotateY(boolean turnLeft) {
        if (turnLeft) {
            rotateY -= 45f;
        } else {
            rotateY += 45f;
        }
        rotateY = rotateY % 360;
        refreshRender();
    }

    /**
     * 摄像头远近设置
     *
     * @param nearly
     * @param v
     */
    public void axsideMove(boolean nearly, float v) {
        transZ += Scaling.scaleSimpleValue(v);
        refreshRender();
    }

    /**
     * 刷新显示
     */
    public void refreshRender() {
        OrderKingApplication orderKingApplication = (OrderKingApplication) mContext.getApplicationContext();
        ShadowsGLSurfaceView shadowsGLSurfaceView = orderKingApplication.getShadowsGLSurfaceView();
        shadowsGLSurfaceView.requestRender();
    }

    // 释放请求
    public void setReleaseRequest(boolean releaseRequest) {
        this.releaseRequest = releaseRequest;
        refreshRender();
    }

    /**
     * 释放数据
     */
    private void release() {
        if (interObserver != null) {
            interObserver.release();
        }
    }

}
