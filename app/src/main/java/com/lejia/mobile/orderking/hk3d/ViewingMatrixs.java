package com.lejia.mobile.orderking.hk3d;

/**
 * Author by HEKE
 *
 * @time 2018/7/14 15:57
 * TODO:  可见的绘制内容使用矩阵
 */
public class ViewingMatrixs {
    public static final float[] mMVPMatrix = new float[16]; // 总矩阵
    public static final float[] mMVMatrix = new float[16]; // 模型与视图矩阵
    public static final float[] mNormalMatrix = new float[16]; // 法线矩阵
    public static final float[] mProjectionMatrix = new float[16]; // 投影矩阵
    public static final float[] mViewMatrix = new float[16]; // 视图矩阵
    public static final float[] mModelMatrix = new float[16]; // 缩放、平移、旋转矩阵
}
