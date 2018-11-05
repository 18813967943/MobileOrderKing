package com.lejia.mobile.orderking.hk3d.datas_2d;

import android.graphics.Bitmap;

import com.lejia.mobile.orderking.utils.TextUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Author by HEKE
 *
 * @time 2018/9/1 14:16
 * TODO: 模型子件详细数据
 */
public class SubsetView extends RendererObject {

    // 子件编号
    int id;
    // 材质编号类型
    int type;
    // 顶点
    String vertexsStr;
    // UV0纹理
    String uv0Str;
    // UV1纹理
    String uv1Str;
    // 光照贴图
    String texture0Str;
    // 法线贴图
    String texture1Str;
    // 法线
    String normalsStr;
    // 索引
    String indicesStr;

    // 材质贴图
    private Bitmap textureBitmap;
    private boolean needLoadTexture;

    private void initBuffers() {
        String[] vertexArray = vertexsStr.split("[,]");
        vertexs = new float[vertexArray.length];
        int vsize = vertexArray.length / 3;
        for (int i = 0; i < vsize; i++) {
            int index = 3 * i;
            vertexs[index] = Float.parseFloat(vertexArray[index]) * 0.1f;
            vertexs[index + 1] = Float.parseFloat(vertexArray[index + 2]) * 0.1f;
            vertexs[index + 2] = Float.parseFloat(vertexArray[index + 1]) * 0.1f;
        }
        String[] uvArray = uv0Str.split("[,]");
        texcoord = new float[uvArray.length];
        for (int i = 0; i < uvArray.length; i++) {
            if (i % 2 == 1) {
                texcoord[i] = 1.0f - Float.parseFloat(uvArray[i]);
            } else {
                texcoord[i] = Float.parseFloat(uvArray[i]);
            }
        }
        if (!TextUtils.isTextEmpity(normalsStr)) {
            String[] normalsArray = normalsStr.split("[,]");
            normals = new float[normalsArray.length];
            for (int i = 0; i < normalsArray.length; i++) {
                normals[i] = Float.parseFloat(normalsArray[i]);
            }
            normalsBuffer = ByteBuffer.allocateDirect(4 * normals.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
            normalsBuffer.put(normals).position(0);
        }
        String[] indicesArray = indicesStr.split("[,]");
        indices = new short[indicesArray.length];
        for (int i = 0; i < indicesArray.length; i++) {
            indices[i] = Short.parseShort(indicesArray[i]);
        }
        vertexsBuffer = ByteBuffer.allocateDirect(4 * vertexs.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexsBuffer.put(vertexs).position(0);
        texcoordBuffer = ByteBuffer.allocateDirect(4 * texcoord.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        texcoordBuffer.put(texcoord).position(0);
        indicesBuffer = ByteBuffer.allocateDirect(2 * indices.length).order(ByteOrder.nativeOrder()).asShortBuffer();
        indicesBuffer.put(indices).position(0);
    }

    public SubsetView(int id, int type, String vertexsStr, String uv0Str, String uv1Str,
                      String texture0Str, String texture1Str, String normalsStr, String indicesStr,
                      String uuid) {
        this.id = id;
        this.type = type;
        this.vertexsStr = vertexsStr;
        this.uv0Str = uv0Str;
        this.uv1Str = uv1Str;
        this.texture0Str = texture0Str;
        this.texture1Str = texture1Str;
        this.normalsStr = normalsStr;
        this.indicesStr = indicesStr;
        this.uuid = uuid;
        initBuffers();
    }

    /**
     * 加载材质贴图
     */
    public void loadTexture() {
    }

    /**
     * 渲染
     */
    public void render(FurnitureMatrixs furnitureMatrixs, int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition) {
    }

    @Override
    @Deprecated
    public void render(int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition) {
    }

}
