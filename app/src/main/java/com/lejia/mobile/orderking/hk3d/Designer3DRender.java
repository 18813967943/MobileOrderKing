package com.lejia.mobile.orderking.hk3d;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.util.Log;

import com.lejia.mobile.orderking.bases.OrderKingApplication;
import com.lejia.mobile.orderking.hk3d.classes.LJ3DPoint;
import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.classes.Ray;
import com.lejia.mobile.orderking.hk3d.datas.DummyGround;
import com.lejia.mobile.orderking.hk3d.datas.House;
import com.lejia.mobile.orderking.hk3d.datas.HouseDatasManager;
import com.lejia.mobile.orderking.hk3d.datas.RendererObject;
import com.lejia.mobile.orderking.hk3d.datas.cadwidgets.BaseCad;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.support.constraint.Constraints.TAG;

/**
 * Author by HEKE
 *
 * @time 2018/7/9 17:01
 * TODO: 渲染管理对象
 */
public class Designer3DRender implements GLSurfaceView.Renderer {

    private Context mContext;
    private OnRenderStatesListener onRenderStatesListener;
    private boolean release; // 释放数据

    /**
     * 数据管理对象
     */
    private HouseDatasManager houseDatasManager;

    // 用于求出交点的虚拟地面
    private DummyGround dummyGround;

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

    // 深度
    private float near = 1.0f;
    private float far = 20000.0f;

    /**
     * 摄像机等信息
     */
    private float eyeX;
    private float eyeY;
    private float eyeZ = -far / 25;

    /***
     * 模型矩阵平移、旋转数值
     * **/
    private float maxScale = 2.0f;
    private float minScale = 0.4f;
    private float scale = 1.0f; // 当前缩放数值
    private float transX; // X轴平移总量
    private float transY; // Y轴平移总量

    /**
     * FBO
     */
    int[] fboId;
    int[] depthTextureId;
    int[] renderTextureId;

    // 触摸管理对象
    private TouchSelectedManager touchSelectedManager;

    // 当前渲染状态
    private int rendererState = RendererState.STATE_2D;

    public Designer3DRender(Context context, OnRenderStatesListener onRenderStatesListener) {
        this.mContext = context;
        this.houseDatasManager = new HouseDatasManager(mContext);
        this.dummyGround = new DummyGround(mContext);
        this.touchSelectedManager = new TouchSelectedManager(mContext, new ArrayList<RendererObject>());
        this.onRenderStatesListener = onRenderStatesListener;
    }

    // 获取所有数据管理对象
    public HouseDatasManager getHouseDatasManager() {
        return houseDatasManager;
    }

    // 获取触摸选中管理对象
    public TouchSelectedManager getTouchSelectedManager() {
        return touchSelectedManager;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        Matrix.setLookAtM(ViewingMatrixs.mViewMatrix, 0, eyeX, eyeY, eyeZ,
                0, 0, 0, 0, 1, 0);
        ViewingShader.loadShader(mContext);
        ViewingShader.loadShadowShader(mContext);
    }

    /**
     * Sets up the framebuffer and renderbuffer to render to texture
     */
    public void generateShadowFBO() {
        mShadowMapWidth = Math.round(mDisplayWidth);
        mShadowMapHeight = Math.round(mDisplayHeight);

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
            Log.e(TAG, "GL_FRAMEBUFFER_COMPLETE failed, CANNOT use FBO");
            throw new RuntimeException("GL_FRAMEBUFFER_COMPLETE failed, CANNOT use FBO");
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mDisplayWidth = width;
        mDisplayHeight = height;
        GLES30.glViewport(0, 0, mDisplayWidth, mDisplayHeight);
        generateShadowFBO();
        if (mDisplayHeight == 0)
            mDisplayHeight = 1;
        float ratio = (float) mDisplayWidth / mDisplayHeight;
        // this projection matrix is applied at rendering scene
        // in the onDrawFrame() method
        float bottom = -1.0f;
        float top = 1.0f;
        Matrix.frustumM(ViewingMatrixs.mProjectionMatrix, 0, -ratio, ratio, bottom, top, near, far);
        // this projection matrix is used at rendering shadow map
        Matrix.frustumM(LightMatrixs.mLightProjectionMatrix, 0, -1.1f * ratio, 1.1f * ratio,
                1.1f * bottom, 1.1f * top, near, far);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        LightMatrixs.mActualLightPosition = LightMatrixs.mLightPosModel.clone();
        // set model view scale、translate、rotate，set the init matrixs
        setMineModelViews();
        // scale 、translate 、rotate ModelMatrix
        animationModelViews();
        //Set view matrix from light source position
        Matrix.setLookAtM(LightMatrixs.mLightViewMatrix, 0,
                //lightX, lightY, lightZ,
                LightMatrixs.mActualLightPosition[0], LightMatrixs.mActualLightPosition[1], LightMatrixs.mActualLightPosition[2],
                //lookX, lookY, lookZ,
                //look in direction -y
                LightMatrixs.mActualLightPosition[0], -LightMatrixs.mActualLightPosition[1], LightMatrixs.mActualLightPosition[2],
                //upX, upY, upZ
                //up vector in the direction of axisY
                -LightMatrixs.mActualLightPosition[0], 0, -LightMatrixs.mActualLightPosition[2]);

        // 关闭剔除3.0不支持
        GLES30.glDisable(GLES20.GL_CULL_FACE);
        //------------------------- render depth map --------------------------
        // shadow generation to avoid self shadowing
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        renderShadowMap();

        //------------------------- render scene ------------------------------
        // normal render
        renderScene();

        // release datas
        if (release) {
            release = false;
            release();
        }

        // Print openGL errors to console
        int debugInfo = GLES30.glGetError();
        if (debugInfo != GLES30.GL_NO_ERROR) {
            String msg = "OpenGL error: " + debugInfo;
            Log.w(TAG, msg);
        }
    }

    /**
     * 缩放、平移、旋转矩阵
     */
    private void setMineModelViews() {
        // init matrix
        Matrix.setIdentityM(ViewingMatrixs.mModelMatrix, 0);
        // scale
        Matrix.scaleM(ViewingMatrixs.mModelMatrix, 0, scale, scale, 1.0f);
        // translate
        Matrix.translateM(ViewingMatrixs.mModelMatrix, 0, transX, transY, 0.0f);
        // set the matrix to the phone or tablet align to left and top directions
        Matrix.rotateM(ViewingMatrixs.mModelMatrix, 0, -180, 0.0f, 0.0f, 1.0f);
    }

    /**
     * TODO 执行2D/25D/3D切换动画
     */
    private void animationModelViews() {
        int setRendererState = RendererState.getRenderState();

    }

    /**
     * TODO 阴影渲染
     */
    private void renderShadowMap() {
        // bind the generated framebuffer
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, fboId[0]);
        GLES30.glViewport(0, 0, mShadowMapWidth, mShadowMapHeight);
        // Clear color and buffers
        GLES30.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
        // Start using the shader
        GLES30.glUseProgram(ViewingShader.shadowProgram);

        float[] tempResultMatrix = new float[16];
        // Calculate matrices for standing objects

        // View matrix * Model matrix value is stored
        Matrix.multiplyMM(LightMatrixs.mLightMvpMatrix_staticShapes, 0, LightMatrixs.mLightViewMatrix
                , 0, ViewingMatrixs.mModelMatrix, 0);

        // Model * view * projection matrix stored and copied for use at rendering from camera point of view
        Matrix.multiplyMM(tempResultMatrix, 0, LightMatrixs.mLightProjectionMatrix, 0,
                LightMatrixs.mLightMvpMatrix_staticShapes, 0);
        System.arraycopy(tempResultMatrix, 0, LightMatrixs.mLightMvpMatrix_staticShapes, 0, 16);
        // Pass in the combined matrix.
        GLES30.glUniformMatrix4fv(ViewingShader.shadow_mvpMatrixUniform, 1, false, LightMatrixs.mLightMvpMatrix_staticShapes, 0);

        // Render all stationary shapes on scene
        if (houseDatasManager != null) {
            try {
                // 开启深度测试
                GLES30.glEnable(GLES30.GL_DEPTH_TEST);
                // 绘制房间数据
                ArrayList<House> housesList = houseDatasManager.getHousesList();
                if (housesList != null && housesList.size() > 0) {
                    for (int i = housesList.size() - 1; i > -1; i--) {
                        House house = housesList.get(i);
                        house.render(ViewingShader.shadow_positionAttribute, 0, 0, true);
                    }
                }
                // 绘制家具数据
                ArrayList<BaseCad> baseCadsList = houseDatasManager.getFurnituresList();
                if (baseCadsList != null && baseCadsList.size() > 0) {
                    for (int i = baseCadsList.size() - 1; i > -1; i--) {
                        BaseCad baseCad = baseCadsList.get(i);
                        baseCad.render(ViewingShader.shadow_positionAttribute, 0, 0, true);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * TODO 实景渲染
     */
    private void renderScene() {
        // bind default framebuffer
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        GLES30.glUseProgram(ViewingShader.mProgram);
        GLES30.glViewport(0, 0, mDisplayWidth, mDisplayHeight);
        float[] tempResultMatrix = new float[16];
        float bias[] = new float[]{
                0.5f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.5f, 0.0f,
                0.5f, 0.5f, 0.5f, 1.0f};

        float[] depthBiasMVP = new float[16];

        //calculate MV matrix
        Matrix.multiplyMM(tempResultMatrix, 0, ViewingMatrixs.mViewMatrix, 0, ViewingMatrixs.mModelMatrix, 0);
        System.arraycopy(tempResultMatrix, 0, ViewingMatrixs.mMVMatrix, 0, 16);

        //pass in MV Matrix as uniform
        GLES30.glUniformMatrix4fv(ViewingShader.scene_mvMatrixUniform, 1, false, ViewingMatrixs.mMVMatrix, 0);

        //calculate Normal Matrix as uniform (invert transpose MV)
        Matrix.invertM(tempResultMatrix, 0, ViewingMatrixs.mMVMatrix, 0);
        Matrix.transposeM(ViewingMatrixs.mNormalMatrix, 0, tempResultMatrix, 0);

        //pass in Normal Matrix as uniform
        GLES30.glUniformMatrix4fv(ViewingShader.scene_normalMatrixUniform, 1, false, ViewingMatrixs.mNormalMatrix, 0);

        //calculate MVP matrix
        Matrix.multiplyMM(tempResultMatrix, 0, ViewingMatrixs.mProjectionMatrix, 0, ViewingMatrixs.mMVMatrix, 0);
        System.arraycopy(tempResultMatrix, 0, ViewingMatrixs.mMVPMatrix, 0, 16);
        //pass in MVP Matrix as uniform
        GLES30.glUniformMatrix4fv(ViewingShader.scene_mvpMatrixUniform, 1, false, ViewingMatrixs.mMVPMatrix, 0);

        Matrix.multiplyMV(LightMatrixs.mLightPosInEyeSpace, 0, ViewingMatrixs.mViewMatrix,
                0, LightMatrixs.mActualLightPosition, 0);

        //pass in light source position
        GLES30.glUniform3f(ViewingShader.scene_lightPosUniform, LightMatrixs.mLightPosInEyeSpace[0],
                LightMatrixs.mLightPosInEyeSpace[1], LightMatrixs.mLightPosInEyeSpace[2]);
        Matrix.multiplyMM(depthBiasMVP, 0, bias, 0, LightMatrixs.mLightMvpMatrix_staticShapes, 0);
        System.arraycopy(depthBiasMVP, 0, LightMatrixs.mLightMvpMatrix_staticShapes, 0, 16);

        //MVP matrix that was used during depth map render
        GLES30.glUniformMatrix4fv(ViewingShader.scene_schadowProjMatrixUniform, 1, false,
                LightMatrixs.mLightMvpMatrix_staticShapes, 0);

        //pass in texture where depth map is stored
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, renderTextureId[0]);
        GLES30.glUniform1i(ViewingShader.scene_textureUniform, 0);

        // draw all views
        if (houseDatasManager != null) {
            try {
                // 绘制房间数据
                GLES30.glDisable(GLES30.GL_DEPTH_TEST);
                ArrayList<House> housesList = houseDatasManager.getHousesList();
                if (housesList != null && housesList.size() > 0) {
                    for (int i = 0; i < housesList.size(); i++) {
                        House house = housesList.get(i);
                        house.render(ViewingShader.scene_positionAttribute, ViewingShader.scene_normalAttribute
                                , ViewingShader.scene_colorAttribute, false);
                    }
                }
                // 绘制家具数据
                GLES30.glEnable(GLES30.GL_DEPTH_TEST);
                ArrayList<BaseCad> baseCadsList = houseDatasManager.getFurnituresList();
                if (baseCadsList != null && baseCadsList.size() > 0) {
                    for (int i = baseCadsList.size() - 1; i > -1; i--) {
                        BaseCad baseCad = baseCadsList.get(i);
                        baseCad.render(ViewingShader.scene_positionAttribute, ViewingShader.scene_normalAttribute
                                , ViewingShader.scene_colorAttribute, false);
                    }
                }
                // 绘制房间名称
                if (housesList != null && housesList.size() > 0) {
                    for (int i = housesList.size() - 1; i > -1; i--) {
                        House house = housesList.get(i);
                        house.renderName(ViewingShader.scene_positionAttribute, ViewingShader.scene_normalAttribute
                                , ViewingShader.scene_colorAttribute, false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 缩放增减
     *
     * @param bigger
     */
    public void setScale(boolean bigger) {
        if (bigger) {
            scale += 0.1f;
            if (scale >= maxScale)
                scale = maxScale;
        } else {
            scale -= 0.1f;
            if (scale <= minScale)
                scale = minScale;
        }
        refreshRenderer();
    }

    /**
     * 恢复缩放
     */
    public void resetScale() {
        scale = 1.0f;
        refreshRenderer();
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
        refreshRenderer();
    }

    /**
     * 恢复平移数据
     */
    public void resetTranslate() {
        transX = 0.0f;
        transY = 0.0f;
        refreshRenderer();
    }

    /**
     * 触摸选中处理
     *
     * @param x
     * @param y
     */
    public boolean checkClickAtViews(float x, float y) {
        if (houseDatasManager == null)
            return false;
        RendererObject object = null;
        int[] view = new int[]{0, 0, mDisplayWidth, mDisplayHeight};
        y = view[3] - y - 1;
        float[] r1 = new float[4];
        float[] r2 = new float[4];
        float[] mv = new float[16];
        Matrix.multiplyMM(mv, 0, ViewingMatrixs.mViewMatrix, 0, ViewingMatrixs.mModelMatrix, 0);
        int near = GLU.gluUnProject(x, y, 0.0f, mv, 0, ViewingMatrixs.mProjectionMatrix, 0,
                view, 0, r1, 0);
        int far = GLU.gluUnProject(x, y, 1.0f, mv, 0, ViewingMatrixs.mProjectionMatrix, 0,
                view, 0, r2, 0);
        // 返回正确值
        if (near == 1 && far == 1) {
            for (int i = 0; i < 3; i++) {
                r1[i] /= r1[3];
                r2[i] /= r2[3];
            }
            // 初始化射线
            LJ3DPoint a = new LJ3DPoint(r1[0], r1[1], r1[2]);
            LJ3DPoint b = new LJ3DPoint(r2[0], r2[1], r2[2]);
            Ray ray = new Ray(a, LJ3DPoint.normalize(b.subtract(a)));
            // 求出相交对象
            ArrayList<RendererObject> rendererObjectsList = new ArrayList<>();
            ArrayList<House> housesList = houseDatasManager.getHousesList();
            if (housesList != null && housesList.size() > 0) { // 房间
                for (House house : housesList) {
                    rendererObjectsList.addAll(house.getTotalRendererObjectList());
                }
            }
            ArrayList<BaseCad> baseCadsList = houseDatasManager.getFurnituresList();
            if (baseCadsList != null && baseCadsList.size() > 0) { // 家具
                rendererObjectsList.addAll(baseCadsList);
            }
            object = LJ3DPoint.checkRayIntersectedObject(ray, rendererObjectsList, new LJ3DPoint(eyeX, eyeY, eyeZ));
            if (object != null) {
                touchSelectedManager.setRendererObjectsList(rendererObjectsList);
                touchSelectedManager.setSelector(object);
                return true;
            } else {
                touchSelectedManager.setSelector(null);
            }
        }
        return false;
    }

    /**
     * 平面拖动转三维点
     *
     * @param x          平面x坐标
     * @param y          平面y坐标
     * @param needAdsorb
     * @return 返回触摸点
     */
    public LJ3DPoint touchPlanTo3D(float x, float y, boolean needAdsorb) {
        int[] view = new int[]{0, 0, mDisplayWidth, mDisplayHeight};
        y = view[3] - y - 1;
        float[] r1 = new float[4];
        float[] r2 = new float[4];
        float[] mv = new float[16];
        Matrix.multiplyMM(mv, 0, ViewingMatrixs.mViewMatrix, 0, ViewingMatrixs.mModelMatrix, 0);
        int near = GLU.gluUnProject(x, y, 0.0f, mv, 0, ViewingMatrixs.mProjectionMatrix, 0,
                view, 0, r1, 0);
        int far = GLU.gluUnProject(x, y, 1.0f, mv, 0, ViewingMatrixs.mProjectionMatrix, 0,
                view, 0, r2, 0);
        // 返回正确值
        if (near == 1 && far == 1) {
            for (int i = 0; i < 3; i++) {
                r1[i] /= r1[3];
                r2[i] /= r2[3];
            }
            // 初始化射线
            LJ3DPoint a = new LJ3DPoint(r1[0], r1[1], r1[2]);
            LJ3DPoint b = new LJ3DPoint(r2[0], r2[1], r2[2]);
            Ray ray = new Ray(a, LJ3DPoint.normalize(b.subtract(a)));
            // 求出交点
            ArrayList<RendererObject> rendererObjectsList = new ArrayList<>();
            rendererObjectsList.add(dummyGround);
            LJ3DPoint intersectedPoint = LJ3DPoint.checkRayIntersectedPoint(ray, rendererObjectsList, new LJ3DPoint(eyeX, eyeY, eyeZ));
            if (intersectedPoint != null) {
                if (needAdsorb) {
                    Point adsorb = houseDatasManager.checkAdsorb(intersectedPoint.off());
                    if (adsorb != null) {
                        intersectedPoint = adsorb.toLJ3DPoint();
                    }
                }
                return intersectedPoint;
            }
        }
        return null;
    }

    /**
     * 请求释放数据
     */
    public void requestRelease() {
        this.release = true;
        refreshRenderer();
    }

    /**
     * 刷新内容
     */
    public void refreshRenderer() {
        ((OrderKingApplication) mContext.getApplicationContext()).render();
    }

    /**
     * 释放具体数据
     */
    private void release() {
        ArrayList<House> housesList = houseDatasManager.getHousesList();
        if (housesList != null && housesList.size() > 0) {
            for (House house : housesList) {
                ArrayList<RendererObject> rendererObjectsList = house.getTotalRendererObjectList();
                if (rendererObjectsList != null && rendererObjectsList.size() > 0) {
                    for (RendererObject rendererObject : rendererObjectsList) {
                        rendererObject.release();
                    }
                }
            }
            houseDatasManager.laterClearWhen3DViewsClearFinished();
            if (touchSelectedManager != null)
                touchSelectedManager.clear();
        }
        resetScale();
        resetTranslate();
    }

}
