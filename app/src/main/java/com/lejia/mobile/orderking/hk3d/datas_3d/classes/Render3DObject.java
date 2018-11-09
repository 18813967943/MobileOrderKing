package com.lejia.mobile.orderking.hk3d.datas_3d.classes;

import com.lejia.mobile.orderking.bases.OrderKingApplication;
import com.lejia.mobile.orderking.hk3d.datas_2d.RendererObject;
import com.lejia.mobile.orderking.hk3d.datas_3d.ShadowsGLSurfaceView;
import com.lejia.mobile.orderking.hk3d.datas_3d.ShadowsRenderer;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/11/5 17:49
 * TODO: 三维即时光影渲染基础对象
 */
public abstract class Render3DObject extends RendererObject {

    /**
     * 渲染对象
     */
    public ShadowsRenderer mRenderer = getShadowsGLSurfaceView().getRenderer();

    /**
     * 在GLThread线程中执行操作
     *
     * @param r
     */
    public void run(Runnable r) {
        if (r == null)
            return;
        try {
            ShadowsGLSurfaceView shadowsGLSurfaceView = getShadowsGLSurfaceView();
            if (shadowsGLSurfaceView == null)
                return;
            shadowsGLSurfaceView.queueEvent(r);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取即时光影渲染控件
     */
    public ShadowsGLSurfaceView getShadowsGLSurfaceView() {
        return ((OrderKingApplication) getContext().getApplicationContext()).getShadowsGLSurfaceView();
    }

    @Override
    public void refreshRender() {
        ShadowsGLSurfaceView shadowsGLSurfaceView = getShadowsGLSurfaceView();
        if (shadowsGLSurfaceView != null)
            shadowsGLSurfaceView.requestRender();
    }

}
