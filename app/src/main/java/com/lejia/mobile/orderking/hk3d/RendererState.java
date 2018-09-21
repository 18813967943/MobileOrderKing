package com.lejia.mobile.orderking.hk3d;

import com.lejia.mobile.orderking.bases.OrderKingApplication;

/**
 * Author by HEKE
 *
 * @time 2018/7/25 10:04
 * TODO: 渲染所属状态
 */
public class RendererState {

    // 2D正视
    public static final int STATE_2D = 0x00;

    // 2.5D轴侧
    public static final int STATE_25D = 0x01;

    // 3D
    public static final int STATE_3D = 0x02;

    /**
     * 平面、轴侧、三维效果状态
     */
    private static int renderState = STATE_2D;

    public static int getRenderState() {
        return renderState;
    }

    public static void setRenderState(int renderState) {
        RendererState.renderState = renderState;
        ((OrderKingApplication) OrderKingApplication.getInstant()).render();
    }

    public static void setRenderStateWithNotRefresh(int renderState) {
        RendererState.renderState = renderState;
    }

    public static boolean isNot2D() {
        return renderState != STATE_2D;
    }

    public static boolean isNot25D() {
        return renderState != STATE_25D;
    }

    public static boolean isNot3D() {
        return renderState != STATE_3D;
    }

    /**
     * 精准画墙模式标志
     */
    public static boolean isAccurate = false;

    public static boolean isIsAccurate() {
        return isAccurate;
    }

    public static void setAccurate(boolean isAccurate) {
        RendererState.isAccurate = isAccurate;
    }

}
