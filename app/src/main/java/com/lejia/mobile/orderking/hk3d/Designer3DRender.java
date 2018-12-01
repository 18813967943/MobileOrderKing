package com.lejia.mobile.orderking.hk3d;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES30;
import android.opengl.GLException;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.support.annotation.NonNull;
import android.util.Log;

import com.lejia.mobile.orderking.bases.OrderKingApplication;
import com.lejia.mobile.orderking.hk3d.classes.LJ3DPoint;
import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.classes.Ray;
import com.lejia.mobile.orderking.hk3d.datas_2d.DummyGround;
import com.lejia.mobile.orderking.hk3d.datas_2d.Ground;
import com.lejia.mobile.orderking.hk3d.datas_2d.House;
import com.lejia.mobile.orderking.hk3d.datas_2d.HouseDatasManager;
import com.lejia.mobile.orderking.hk3d.datas_2d.RendererObject;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.bridge.OnReadPixsListener;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.models.FurnitureController;
import com.lejia.mobile.orderking.hk3d.datas_2d.cadwidgets.BaseCad;
import com.lejia.mobile.orderking.hk3d.datas_3d.classes.BuildingGround;

import java.nio.IntBuffer;
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

    /**
     * 家具管理对象
     */
    private FurnitureController furnitureController;

    // 用于求出交点的虚拟地面
    private DummyGround dummyGround;

    /**
     * 视图宽高
     */
    private int mDisplayWidth;
    private int mDisplayHeight;

    // 深度
    private float near = 1.0f;
    private float far = 20000.0f;

    /**
     * 摄像机等信息
     */
    public float eyesX;
    public float eyesY;
    public float eyesZ = far / 25;

    /**
     * 看向位置
     */
    public float lookX;
    public float lookY;
    public float lookZ = -3000;

    /***
     * 模型矩阵平移、旋转数值
     * **/
    private float maxScale = 3.0f;
    private float minScale = 0.4f;
    private float scale = 1.0f; // 当前缩放数值
    public float transX; // X轴平移总量
    public float transY; // Y轴平移总量
    public float transZ; // Z轴平移总量

    public float rotateX; // X轴旋转数值
    public float rotateZ; // Z轴旋转数值

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
        this.furnitureController = new FurnitureController(mContext, this.houseDatasManager);
        this.onRenderStatesListener = onRenderStatesListener;
    }

    // 获取所有数据管理对象
    public HouseDatasManager getHouseDatasManager() {
        return houseDatasManager;
    }

    // 新旧版对接家具管理对象
    public FurnitureController getFurnitureController() {
        return furnitureController;
    }

    // 获取触摸选中管理对象
    public TouchSelectedManager getTouchSelectedManager() {
        return touchSelectedManager;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        Matrix.setLookAtM(ViewingMatrixs.mViewMatrix, 0, eyesX, eyesY, eyesZ,
                lookX, lookY, lookZ, 0, 1, 0);
        ViewingShader.loadShader(mContext);
        ViewingShader.loadShadowShader(mContext);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mDisplayWidth = width;
        mDisplayHeight = height;
        GLES30.glViewport(0, 0, mDisplayWidth, mDisplayHeight);
        GLES30.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
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
        // set model view scale、translate、rotate，set the init matrixs
        setMineModelViews();
        // 改变摄像机位置及状态
        //Set view matrix from light source position
        Matrix.setLookAtM(ViewingMatrixs.mViewMatrix, 0, eyesX, eyesY, eyesZ,
                lookX, lookY, lookZ, 0, 1, 0);
        //------------------------- refreshRender scene ------------------------------
        // normal refreshRender
        renderScene();
        // 读取像素区域
        if (requestReadPixs) {
            requestReadPixs = false;
            Bitmap bitmap = createBitmapFromGLSurface(0, 0, mDisplayWidth, mDisplayHeight);
            onReadPixsListener.complelted(bitmap);
        }
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

    // 实景常态渲染矩阵
    private void sencesMatrixs() {
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

        //MVP matrix that was used during depth map refreshRender
        GLES30.glUniformMatrix4fv(ViewingShader.scene_schadowProjMatrixUniform, 1, false,
                LightMatrixs.mLightMvpMatrix_staticShapes, 0);
    }

    /**
     * TODO 实景渲染
     */
    private void renderScene() {
        // bind default framebuffer
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        GLES30.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES30.glUseProgram(ViewingShader.mProgram);
        GLES30.glViewport(0, 0, mDisplayWidth, mDisplayHeight);
        sencesMatrixs();
        // draw all views
        if (houseDatasManager != null) {
            try {
                boolean is3D = !RendererState.isNot3D();
                // 绘制房间数据
                if (!is3D) {
                    GLES30.glDisable(GLES30.GL_DEPTH_TEST);
                } else {
                    GLES30.glEnable(GLES30.GL_DEPTH_TEST);
                }
                boolean isNot2D = RendererState.isNot2D();
                ArrayList<House> housesList = houseDatasManager.getHousesList();
                if (housesList != null && housesList.size() > 0) {
                    for (int i = 0; i < housesList.size(); i++) {
                        House house = housesList.get(i);
                        house.render(ViewingShader.scene_positionAttribute, ViewingShader.scene_normalAttribute
                                , ViewingShader.scene_colorAttribute, false);
                    }
                }
                // 绘制家具数据
                if (!is3D) {
                    GLES30.glEnable(GLES30.GL_DEPTH_TEST);
                }
                if (!isNot2D) {
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
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        Matrix.translateM(ViewingMatrixs.mModelMatrix, 0, transX, transY, transZ);
        // set the matrix to the phone or tablet align to left and top directions
        Matrix.rotateM(ViewingMatrixs.mModelMatrix, 0, -180, 0.0f, 0.0f, 1.0f);
        // rotate animation
        Matrix.rotateM(ViewingMatrixs.mModelMatrix, 0, rotateX, 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(ViewingMatrixs.mModelMatrix, 0, rotateZ, 0.0f, 0.0f, 1.0f);
    }

    /**
     * 轴侧
     */
    public void toAxisSideViews() {
        if (RendererState.isNot25D()) {
            RendererState.setRenderState(RendererState.STATE_25D);
            rotateX = 45;
            rotateZ = 40;
            eyesZ = far / 25 + 300f;
            lookZ = -3000;
        } else {
            RendererState.setRenderState(RendererState.STATE_2D);
            rotateX = 0;
            rotateZ = 0;
            transY = 0;
            eyesX = 0;
            eyesZ = far / 25;
            lookX = 0;
            lookY = 0;
            lookZ = 0;
        }
        refreshRenderer();
    }

    /**
     * 进入房间
     */
    public void enterInner() {
        if (RendererState.isNot3D()) {
            RendererState.setRenderState(RendererState.STATE_3D);
            rotateX = 90;
            rotateZ = 0;
            Point point = houseDatasManager.getEnterHouse3DInnerPosition();
            eyesX = (float) point.x;
            eyesZ = (float) point.y;
            transY = -120f;
            lookZ = -3000;
        } else {
            RendererState.setRenderState(RendererState.STATE_2D);
            rotateX = 0;
            rotateZ = 0;
            transY = 0;
            eyesX = 0;
            eyesZ = far / 25;
            lookX = 0;
            lookY = 0;
            lookZ = 0;
        }
        refreshRenderer();
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
                    rendererObjectsList.addAll(house.getCurrentTotalRendererObjectList());
                }
            }
            ArrayList<BaseCad> baseCadsList = houseDatasManager.getFurnituresList();
            if (baseCadsList != null && baseCadsList.size() > 0) { // 家具
                rendererObjectsList.addAll(baseCadsList);
            }
            object = LJ3DPoint.checkRayIntersectedObject(ray, rendererObjectsList, new LJ3DPoint(eyesX, eyesY, eyesZ));
            if (object != null) {
                touchSelectedManager.setRendererObjectsList(rendererObjectsList);
                touchSelectedManager.setSelector(object);
                return true;
            } else {
                // 未选中任何数据，如果当前已有选中数据为家具模型，则取消家具模型的选中，其他照旧
                RendererObject currentSelector = touchSelectedManager.getSelector();
                if (currentSelector != null) {
                    if (currentSelector instanceof BaseCad) {
                        touchSelectedManager.setSelector(null);
                    }
                }
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
            LJ3DPoint intersectedPoint = LJ3DPoint.checkRayIntersectedPoint(ray, rendererObjectsList, new LJ3DPoint(eyesX, eyesY, eyesZ));
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

    // 截屏操作
    private boolean requestReadPixs;
    private OnReadPixsListener onReadPixsListener;

    /**
     * 在此控件中执行截取绘制内容
     *
     * @param onReadPixsListener
     */
    public void readPixs(@NonNull OnReadPixsListener onReadPixsListener) {
        this.onReadPixsListener = onReadPixsListener;
        requestReadPixs = true;
        refreshRenderer();
    }

    /**
     * 读取像素
     *
     * @param x
     * @param y
     * @param w
     * @param h
     * @return
     */
    public Bitmap createBitmapFromGLSurface(int x, int y, int w, int h) {
        int bitmapBuffer[] = new int[w * h];
        int bitmapSource[] = new int[w * h];
        IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
        intBuffer.position(0);
        Bitmap ret = null;
        try {
            GLES30.glReadPixels(x, y, w, h, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, intBuffer);
            int offset1, offset2;
            for (int i = 0; i < h; i++) {
                offset1 = i * w;
                offset2 = (h - i - 1) * w;
                for (int j = 0; j < w; j++) {
                    int texturePixel = bitmapBuffer[offset1 + j];
                    int blue = (texturePixel >> 16) & 0xff;
                    int red = (texturePixel << 16) & 0x00ff0000;
                    int pixel = (texturePixel & 0xff00ff00) | red | blue;
                    bitmapSource[offset2 + j] = pixel;
                }
            }
            ret = Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888);
            GLES30.glDeleteTextures(1, intBuffer);
            intBuffer = null;
            System.gc();
        } catch (GLException e) {
            return null;
        }
        return ret;
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
                // 2D数据
                ArrayList<RendererObject> rendererObjectsList = house.getTotalRendererObjectList();
                if (rendererObjectsList != null && rendererObjectsList.size() > 0) {
                    for (RendererObject rendererObject : rendererObjectsList) {
                        rendererObject.release();
                    }
                }
                // 3D数据
                Ground ground = house.ground;
                if (ground != null) {
                    final BuildingGround buildingGround = ground.getBuildingGround();
                    if (buildingGround != null) {
                        buildingGround.run(new Runnable() {
                            @Override
                            public void run() {
                                buildingGround.release();
                            }
                        });
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
