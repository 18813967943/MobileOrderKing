package com.lejia.mobile.orderking.hk3d;

import android.content.Context;
import android.opengl.GLSurfaceView;

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

    public Designer3DRender(Context context, OnRenderStatesListener onRenderStatesListener) {
        this.mContext = context;
        this.onRenderStatesListener = onRenderStatesListener;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {

    }


}
