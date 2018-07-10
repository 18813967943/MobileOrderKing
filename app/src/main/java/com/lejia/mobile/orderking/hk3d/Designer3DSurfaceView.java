package com.lejia.mobile.orderking.hk3d;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;

import com.lejia.mobile.orderking.bases.OrderKingApplication;

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
            setEGLConfigChooser(8, 8, 8, 8, 16, 0);
            setZOrderMediaOverlay(true);
            getHolder().setFormat(PixelFormat.TRANSPARENT);
            designer3DRender = new Designer3DRender(getContext(), onRenderStatesListener);
            setRenderer(designer3DRender);
            setRenderMode(RENDERMODE_WHEN_DIRTY);
            // 绑定刷新机制
            OrderKingApplication application = (OrderKingApplication) getContext().getApplicationContext();
            if (application != null) {
                application.setDesigner3DSurfaceView(this);
            }
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
