package com.lejia.mobile.orderking.hk3d;

import android.content.Context;
import android.opengl.GLES30;

import com.lejia.mobile.orderking.hk3d.common.ESShader;

/**
 * Author by HEKE
 *
 * @time 2018/7/14 16:13
 * TODO: 可见数据的着色器
 */
public class ViewingShader {

    /**
     * 可是三维空间使用着色器
     */
    public static int mProgram; // 着色器程序编号
    public static int scene_mvpMatrixUniform; // 总矩阵
    public static int scene_mvMatrixUniform; // 模型与视图矩阵
    public static int scene_normalMatrixUniform; // 法线矩阵
    public static int scene_lightPosUniform; // 灯光位置
    public static int scene_schadowProjMatrixUniform; // 引用投影矩阵
    public static int scene_textureUniform; // 阴影纹理
    public static int scene_positionAttribute; // 顶点
    public static int scene_normalAttribute; // 法线
    public static int scene_colorAttribute; // 颜色
    public static int scene_uv0; // 光照贴图
    public static int scene_s_baseMap; // 材质贴图通道
    public static int scene_only_color; // 仅仅使用颜色渲染标志

    // 加载着色器
    public static void loadShader(Context context) {
        mProgram = ESShader.loadProgramFromAsset(context, "shaders/vertex.glsl",
                "shaders/fragment.glsl");
        scene_mvpMatrixUniform = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        scene_mvMatrixUniform = GLES30.glGetUniformLocation(mProgram, "uMVMatrix");
        scene_normalMatrixUniform = GLES30.glGetUniformLocation(mProgram, "uNormalMatrix");
        scene_schadowProjMatrixUniform = GLES30.glGetUniformLocation(mProgram, "uShadowProjMatrix");
        scene_textureUniform = GLES30.glGetUniformLocation(mProgram, "uShadowTexture");
        scene_s_baseMap = GLES30.glGetUniformLocation(mProgram, "s_baseMap");
        scene_lightPosUniform = GLES30.glGetUniformLocation(mProgram, "uLightPos");
        scene_positionAttribute = GLES30.glGetAttribLocation(mProgram, "aPosition");
        scene_normalAttribute = GLES30.glGetAttribLocation(mProgram, "aNormal");
        scene_colorAttribute = GLES30.glGetAttribLocation(mProgram, "aColor");
        scene_uv0 = GLES30.glGetAttribLocation(mProgram, "a_texCoord");
        scene_only_color = GLES30.glGetUniformLocation(mProgram, "s_flags");
    }

    /**
     * FBO 深度着色器
     */
    public static int shadowProgram; // 阴影着色器编号
    public static int shadow_mvpMatrixUniform; // 阴影总矩阵
    public static int shadow_positionAttribute; // 阴影顶点

    public static void loadShadowShader(Context context) {
        shadowProgram = ESShader.loadProgramFromAsset(context, "shaders/shadow_vertex.glsl",
                "shaders/shadow_frag.glsl");
        shadow_mvpMatrixUniform = GLES30.glGetUniformLocation(shadowProgram, "uMVPMatrix");
        shadow_positionAttribute = GLES30.glGetAttribLocation(shadowProgram, "aShadowPosition");
    }

}
