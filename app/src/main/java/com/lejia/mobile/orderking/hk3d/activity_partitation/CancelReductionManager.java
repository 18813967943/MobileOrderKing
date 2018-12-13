package com.lejia.mobile.orderking.hk3d.activity_partitation;

import com.lejia.mobile.orderking.hk3d.RendererState;
import com.lejia.mobile.orderking.hk3d.datas_3d.ShadowsRenderer;

/**
 * Author by HEKE
 *
 * @time 2018/12/13 16:09
 * TODO: 撤销还原按钮处理对象
 */
public class CancelReductionManager {

    private Designer3DManager designer3DManager;
    private ShadowsRenderer shadowsRenderer;

    public CancelReductionManager(Designer3DManager designer3DManager) {
        this.designer3DManager = designer3DManager;
        this.shadowsRenderer = this.designer3DManager.getShadowsGLSurfaceView().getRenderer();
    }

    /**
     * 撤销按钮点击
     */
    public void cancel() {
        // 撤销
        if (!RendererState.isNot2D()) {

        }
        // 左转
        if (!RendererState.isNot25D()) {
            shadowsRenderer.rotateY(true);
        }
    }

    /**
     * 还原按钮点击
     */
    public void reduction() {
        // 还原
        if (!RendererState.isNot2D()) {

        }
        // 右转
        if (!RendererState.isNot25D()) {
            shadowsRenderer.rotateY(false);
        }
    }

}
