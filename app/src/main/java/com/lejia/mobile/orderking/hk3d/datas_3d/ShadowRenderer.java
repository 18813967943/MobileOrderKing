package com.lejia.mobile.orderking.hk3d.datas_3d;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.lejia.mobile.orderking.R;
import com.lejia.mobile.orderking.bases.OrderKingApplication;
import com.lejia.mobile.orderking.hk3d.RendererState;
import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.datas_2d.House;
import com.lejia.mobile.orderking.hk3d.datas_2d.HouseDatasManager;
import com.lejia.mobile.orderking.hk3d.datas_3d.classes.CourtyardGround;
import com.lejia.mobile.orderking.hk3d.datas_3d.classes.Ground3D;
import com.lejia.mobile.orderking.hk3d.datas_3d.classes.WallSpace;

import java.util.ArrayList;

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
    private float[] mLightPosModel = new float[] // 光线初始位置
            {5000.0f, 270.0f, 0.0f, 1.0f};

    private float[] mActualLightPosition = new float[4]; // 实时光线位置

    /**
     * The vertex and fragment shader to render depth map
     */
    private RenderProgram mDepthMapProgram;

    /**
     * Handles to vertex and fragment shader programs
     */
    private RenderProgram mSimpleShadowProgram;

    /**
     * 实景视图、模型矩阵参数
     */
    private float eyesX;
    private float eyesY = 4;
    private float eyesZ = -12;

    private float lookX;
    private float lookY;
    private float lookZ;

    private float transX;
    private float transY;
    private float transZ;

    private float rotateX;
    private float rotateY;

    /**
     * 房间数据管理对象
     */
    private HouseDatasManager houseDatasManager;

    /**
     * 实景外院地面
     **/
    private CourtyardGround courtyardGround;

    /***
     * 楼层信息
     * 默认1楼，默认层高280(2米8)
     * **/
    private int cell = 1;
    private int cellHeight = 280;

    public ShadowRenderer(Context context) {
        mContext = context;
        houseDatasManager = ((OrderKingApplication) mContext.getApplicationContext()).getDesigner3DSurfaceView().getDesigner3DRender().getHouseDatasManager();
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        //Set the background frame color
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        //Enable depth testing
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        //Set view matrix from eye position
        Matrix.setLookAtM(mViewMatrix, 0,
                //eyeX, eyeY, eyeZ,
                eyesX, eyesY, eyesZ,
                //lookX, lookY, lookZ,
                lookX, lookY, lookZ,
                //upX, upY, upZ
                0, 1, 0);

        // OES_depth_texture is available -> shaders are simplier
        mSimpleShadowProgram = new RenderProgram(R.raw.depth_tex_v_with_shadow,
                R.raw.depth_tex_f_with_simple_shadow, mContext);
        mDepthMapProgram = new RenderProgram(R.raw.depth_tex_v_depth_map,
                R.raw.depth_tex_f_depth_map, mContext);
        ShadowViewingShader.loadShader(mSimpleShadowProgram.getProgram());
        ShadowViewingShader.loadShadowShader(mDepthMapProgram.getProgram());

        courtyardGround = new CourtyardGround(mContext);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        mDisplayWidth = width;
        mDisplayHeight = height;
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES30.glViewport(0, 0, mDisplayWidth, mDisplayHeight);

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
        Matrix.frustumM(mLightProjectionMatrix, 0, -ratio, ratio, bottom, top, near, far);
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

    private void setModelMatrixs() {
        // init matrix
        Matrix.setIdentityM(mModelMatrix, 0);
        // scale
        Matrix.scaleM(mModelMatrix, 0, -1.0f, 1.0f, 1.0f);
        // translate
        Matrix.translateM(mModelMatrix, 0, transX, transY, transZ);
        // rotate animation
        Matrix.rotateM(mModelMatrix, 0, -180, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(mModelMatrix, 0, rotateX, 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(mModelMatrix, 0, rotateY, 0.0f, 1.0f, 0.0f);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        /** 旋转灯光位置 **/
        int renderState = RendererState.getRenderState();
        // 轴侧
        if (renderState == RendererState.STATE_25D) {
            /*mLightPosModel[0] = courtyardGround.getHalf_size() - 200f;
            mLightPosModel[1] = 270;
            mLightPosModel[2] = 0;
            long elapsedMilliSec = SystemClock.elapsedRealtime();
            long rotationCounter = elapsedMilliSec % 12000L;
            float lightRotationDegree = (360.0f / 12000.0f) * ((int) rotationCounter);
            float[] rotationMatrix = new float[16];
            Matrix.setIdentityM(rotationMatrix, 0);
            Matrix.rotateM(rotationMatrix, 0, lightRotationDegree, 0.0f, 1.0f, 0.0f);
            Matrix.multiplyMV(mActualLightPosition, 0, rotationMatrix, 0, mLightPosModel, 0);
            System.out.println("###### mActualLightPosition : " + mActualLightPosition[0] + "  " + mActualLightPosition[1] + "  " + mActualLightPosition[2]);*/
            mLightPosModel[0] = 4612.9f;
            mLightPosModel[1] = 270f;
            mLightPosModel[2] = -1327.0852f;
            mActualLightPosition = mLightPosModel.clone();
            GLES30.glUniform1f(ShadowViewingShader.scene_room_light, 0.0f); // 关闭室内灯光
        }
        // 进入房间
        else {
            mLightPosModel[0] = eyesX;
            mLightPosModel[1] = cell * cellHeight - 10f;
            mLightPosModel[2] = eyesZ;
            mActualLightPosition = mLightPosModel.clone();
            GLES30.glUniform1f(ShadowViewingShader.scene_room_light, 1.0f); // 开启室内灯光
        }
        //System.out.println("#### mActualLightPosition : " + mActualLightPosition[0] + "  " + mActualLightPosition[1] + "  " + mActualLightPosition[2]);
        // mActualLightPosition = mLightPosModel.clone();
        /** 调整模型矩阵 **/
        setModelMatrixs();
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
        //Set view matrix from eye position
        Matrix.setLookAtM(mViewMatrix, 0,
                //eyeX, eyeY, eyeZ,
                eyesX, eyesY, eyesZ,
                //lookX, lookY, lookZ,
                lookX, lookY, lookZ,
                //upX, upY, upZ
                0, 1, 0);

        /** 渲染阴影 **/
        renderShadowMap();
        /** 渲染实景 **/
        renderScene();

        // Print openGL errors to console
        int debugInfo = GLES20.glGetError();
        if (debugInfo != GLES20.GL_NO_ERROR) {
            String msg = "OpenGL error: " + debugInfo;
            Log.w("Render", msg);
        }
    }

    // 实际渲染
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
        GLES30.glUniformMatrix4fv(ShadowViewingShader.shadow_mvpMatrixUniform, 1, false, mLightMvpMatrix_staticShapes, 0);

        // Render all stationary shapes on scene
        courtyardGround.render(ShadowViewingShader.shadow_positionAttribute, 0, 0, true);
        // 房间立面数据
        if (houseDatasManager != null) {
            ArrayList<House> housesList = houseDatasManager.getHousesList();
            if (housesList != null && housesList.size() > 0) {
                // 渲染地面
                for (House house : housesList) {
                    Ground3D ground3D = house.ground3D;
                    if (ground3D != null) {
                        ground3D.render(ShadowViewingShader.shadow_positionAttribute, 0, 0, true);
                    }
                }
            }
            // 外墙面
            ArrayList<WallSpace> outerWallSpacesList = houseDatasManager.getWallOuterSpacesList();
            if (outerWallSpacesList != null && outerWallSpacesList.size() > 0) {
                for (WallSpace wallSpace : outerWallSpacesList) {
                    int flag = wallSpace.getFlag();
                    if (flag == WallSpace.FLAG_OUTER)
                        wallSpace.render(ShadowViewingShader.shadow_positionAttribute, 0, 0, true);
                }
            }
        }
    }

    private void renderScene() {
        // bind default framebuffer
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        GLES30.glUseProgram(mSimpleShadowProgram.getProgram());

        GLES30.glViewport(0, 0, mDisplayWidth, mDisplayHeight);
        GLES30.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        //pass stepsize to map nearby points properly to depth map texture - used in PCF algorithm
        GLES30.glUniform1f(ShadowViewingShader.scene_mapStepXUniform, (float) (1.0 / mShadowMapWidth));
        GLES30.glUniform1f(ShadowViewingShader.scene_mapStepYUniform, (float) (1.0 / mShadowMapHeight));

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
        GLES30.glUniformMatrix4fv(ShadowViewingShader.scene_mvMatrixUniform, 1, false, mMVMatrix, 0);

        //calculate Normal Matrix as uniform (invert transpose MV)
        Matrix.invertM(tempResultMatrix, 0, mMVMatrix, 0);
        Matrix.transposeM(mNormalMatrix, 0, tempResultMatrix, 0);

        //pass in Normal Matrix as uniform
        GLES30.glUniformMatrix4fv(ShadowViewingShader.scene_normalMatrixUniform, 1, false, mNormalMatrix, 0);

        //calculate MVP matrix
        Matrix.multiplyMM(tempResultMatrix, 0, mProjectionMatrix, 0, mMVMatrix, 0);
        System.arraycopy(tempResultMatrix, 0, mMVPMatrix, 0, 16);

        //pass in MVP Matrix as uniform
        GLES30.glUniformMatrix4fv(ShadowViewingShader.scene_mvpMatrixUniform, 1, false, mMVPMatrix, 0);

        Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mActualLightPosition, 0);
        //pass in light source position
        GLES30.glUniform3f(ShadowViewingShader.scene_lightPosUniform, mLightPosInEyeSpace[0], mLightPosInEyeSpace[1], mLightPosInEyeSpace[2]);

        Matrix.multiplyMM(depthBiasMVP, 0, bias, 0, mLightMvpMatrix_staticShapes, 0);
        System.arraycopy(depthBiasMVP, 0, mLightMvpMatrix_staticShapes, 0, 16);

        //MVP matrix that was used during depth map render
        GLES30.glUniformMatrix4fv(ShadowViewingShader.scene_schadowProjMatrixUniform, 1, false, mLightMvpMatrix_staticShapes, 0);

        //pass in texture where depth map is stored
        GLES30.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES30.glBindTexture(GLES20.GL_TEXTURE_2D, renderTextureId[0]);
        GLES30.glUniform1i(ShadowViewingShader.scene_textureUniform, 1);

        // render sences
        courtyardGround.render(ShadowViewingShader.scene_positionAttribute, ShadowViewingShader.scene_normalAttribute,
                ShadowViewingShader.scene_colorAttribute, false);
        // 房间立面数据
        if (houseDatasManager != null) {
            ArrayList<House> housesList = houseDatasManager.getHousesList();
            if (housesList != null && housesList.size() > 0) {
                // 渲染地面
                for (House house : housesList) {
                    Ground3D ground3D = house.ground3D;
                    if (ground3D != null) {
                        ground3D.render(ShadowViewingShader.scene_positionAttribute, ShadowViewingShader.scene_normalAttribute,
                                ShadowViewingShader.scene_colorAttribute, false);
                    }
                }
                // 渲染墙面
                for (House house : housesList) {
                    ArrayList<WallSpace> wallSpacesList = house.wallSpacesList;
                    if (wallSpacesList != null && wallSpacesList.size() > 0) {
                        for (int j = 0; j < wallSpacesList.size(); j++) {
                            WallSpace wallSpace = wallSpacesList.get(j);
                            wallSpace.render(ShadowViewingShader.scene_positionAttribute, ShadowViewingShader.scene_normalAttribute,
                                    ShadowViewingShader.scene_colorAttribute, false);
                        }
                    }
                }
            }
            // 外墙面
            ArrayList<WallSpace> outerWallSpacesList = houseDatasManager.getWallOuterSpacesList();
            if (outerWallSpacesList != null && outerWallSpacesList.size() > 0) {
                for (WallSpace wallSpace : outerWallSpacesList) {
                    wallSpace.render(ShadowViewingShader.scene_positionAttribute, ShadowViewingShader.scene_normalAttribute,
                            ShadowViewingShader.scene_colorAttribute, false);
                }
            }
        }
    }

    /*********  参数设置  ***********/

    // 轴侧
    public void toAxisViews() {
        rotateY = -45;
        rotateX = 10f;
        transY = -1000f;
        eyesZ = -2000f;
        transX = 0;
        transZ = 0;
        lookX = 0;
        lookY = 0;
        lookZ = 0;
        refreshRender();
    }

    // 进入房间
    public void gotoHouse() {
        if (houseDatasManager == null)
            return;
        Point point = houseDatasManager.getEnterHouse3DInnerPosition();
        rotateY = 0;
        rotateX = 0;
        transX = 0;
        transZ = 0;
        lookX = 0;
        lookY = 0;
        lookZ = 0;
        transY = -100f;
        eyesX = (float) point.x;
        eyesZ = -(float) point.y;
        refreshRender();
    }

    // 恢复数据
    public void resetParams() {
        rotateX = 0;
        rotateY = 0;
        transX = 0;
        transY = 0;
        transZ = 0;
        eyesX = 0;
        eyesY = 4;
        eyesZ = -12;
        lookX = 0;
        lookY = 0;
        lookZ = 0;
        refreshRender();
    }

    /**
     * 设置平移数值
     *
     * @param transXVal
     * @param transYVal
     */
    public void setTransLate(float transXVal, float transYVal) {
        transX += transXVal;
        transY += transYVal;
        refreshRender();
    }

    /**
     * 走动函数
     *
     * @param flag
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
     * 旋转
     *
     * @param turnLeft
     */
    public void turnAround(boolean turnLeft) {
        float v[] = {lookX - eyesX, lookY - eyesY, lookZ - eyesZ};
        float radians = 0.05f;
        if (turnLeft) {
            lookX = (float) (eyesX + ((Math.cos(radians) * v[0]) - (Math.sin(radians) * v[2])));
            lookZ = (float) (eyesZ + ((Math.sin(radians) * v[0]) + (Math.cos(radians) * v[2])));
        } else {
            lookX = (float) (eyesX + ((Math.cos(-radians) * v[0]) - (Math.sin(-radians) * v[2])));
            lookZ = (float) (eyesZ + ((Math.sin(-radians) * v[0]) + (Math.cos(-radians) * v[2])));
        }
        refreshRender();
    }

    /**
     * 获取当前所在楼层
     */
    public int getCell() {
        return cell;
    }

    /**
     * 设置当前所在楼层
     *
     * @param cell
     */
    public void setCell(int cell) {
        this.cell = cell;
        refreshRender();
    }

    /**
     * 获取当前所在的层高
     */
    public int getCellHeight() {
        return cellHeight;
    }

    /**
     * 设置当前所在的层高
     */
    public void setCellHeight(int cellHeight) {
        this.cellHeight = cellHeight;
        refreshRender();
    }

    // 刷新操作
    public void refreshRender() {
        ((OrderKingApplication) OrderKingApplication.getInstant()).render3D();
    }

}
