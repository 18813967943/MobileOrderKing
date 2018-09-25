package com.lejia.mobile.orderking.hk3d;

/**
 * Author by HEKE
 *
 * @time 2018/7/14 16:00
 * TODO: 灯光矩阵对象
 */
public class LightMatrixs {
    /**
     * MVP matrix used at rendering shadow map for stationary objects
     */
    public static final float[] mLightMvpMatrix_staticShapes = new float[16];

    /**
     * MVP matrix used at rendering shadow map for the big cube in the center
     */
    public static final float[] mLightMvpMatrix_dynamicShapes = new float[16];

    /**
     * Projection matrix from point of light source
     */
    public static final float[] mLightProjectionMatrix = new float[16];

    /**
     * View matrix of light source
     */
    public static final float[] mLightViewMatrix = new float[16];

    /**
     * Position of light source in eye space
     */
    public static final float[] mLightPosInEyeSpace = new float[16];

    /**
     * Light source position in model space
     */
    public static final float[] mLightPosModel = new float[]
            {-10000.0f, 10000.0f, 10000.0f, 1.0f};

    /**
     * 灯光调整后的最终位置
     */
    public static float[] mActualLightPosition = new float[4];

}
