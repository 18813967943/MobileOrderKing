package com.lejia.mobile.orderking.hk3d;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;

import com.lejia.mobile.orderking.bases.OrderKingApplication;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

/**
 * Author by HEKE
 *
 * @time 2018/7/9 16:43
 * TODO: 三维控件
 */
public class Designer3DSurfaceView extends GLSurfaceView {

    private OnRenderStatesListener onRenderStatesListener;
    private Designer3DRender designer3DRender; // 渲染管理对象

    // 初始化配置
    private void init() {
        try {
            ActivityManager am = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
            ConfigurationInfo info = am.getDeviceConfigurationInfo();
            int v = info.reqGlEsVersion;
            if (v >= 0x30000) {
                setEGLContextClientVersion(3);
            } else {
                setEGLContextClientVersion(2);
            }
            setEGLConfigChooser(new MyEGLConfigChooser());
            setZOrderMediaOverlay(true);
            getHolder().setFormat(PixelFormat.TRANSLUCENT);
            designer3DRender = new Designer3DRender(getContext(), onRenderStatesListener);
            setRenderer(designer3DRender);
            setRenderMode(RENDERMODE_WHEN_DIRTY);
            ((OrderKingApplication) getContext().getApplicationContext()).setDesigner3DSurfaceView(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Designer3DSurfaceView(Context context, OnRenderStatesListener onRenderStatesListener) {
        super(context);
        this.onRenderStatesListener = onRenderStatesListener;
        init();
    }

    /**
     * Author by HEKE
     *
     * @time 2018/8/7 11:39
     * TODO: 自定义画布属性配置
     */
    private class MyEGLConfigChooser implements GLSurfaceView.EGLConfigChooser {

        @Override
        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
            int attribs[] = {
                    EGL10.EGL_LEVEL, 0,
                    EGL10.EGL_RENDERABLE_TYPE, 4,  // EGL_OPENGL_ES2_BIT
                    EGL10.EGL_COLOR_BUFFER_TYPE, EGL10.EGL_RGB_BUFFER,
                    EGL10.EGL_RED_SIZE, 8,
                    EGL10.EGL_GREEN_SIZE, 8,
                    EGL10.EGL_BLUE_SIZE, 8,
                    EGL10.EGL_ALPHA_SIZE, 8,
                    EGL10.EGL_DEPTH_SIZE, 16,
                    EGL10.EGL_STENCIL_SIZE, 0,
                    EGL10.EGL_SAMPLE_BUFFERS, 1,
                    EGL10.EGL_SAMPLES, 3,  // 在这里修改MSAA的倍数，采用3层数据采样抗锯齿
                    EGL10.EGL_NONE
            };
            EGLConfig[] configs = new EGLConfig[1];
            int[] configCounts = new int[1];
            egl.eglChooseConfig(display, attribs, configs, 1, configCounts);
            if (configCounts[0] == 0) {
                // Failed! Error handling.
                return null;
            } else {
                return configs[0];
            }
        }
    }

    /**
     * 获取渲染数据管理对象
     */
    public Designer3DRender getDesigner3DRender() {
        return designer3DRender;
    }

    /**
     * 释放控件及数据
     */
    public void release() {
        // 置空
        OrderKingApplication application = (OrderKingApplication) getContext().getApplicationContext();
        if (application != null) {
            application.setDesigner3DSurfaceView(null);
        }
        // 数据释放
    }

}
