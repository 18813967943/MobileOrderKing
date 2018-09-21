package com.lejia.mobile.orderking.hk3d.datas;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.opengl.GLES30;
import android.os.AsyncTask;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.lejia.mobile.orderking.bases.OrderKingApplication;
import com.lejia.mobile.orderking.hk3d.ViewingShader;
import com.lejia.mobile.orderking.hk3d.classes.Texture;
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
        for (int i = 0; i < vertexArray.length; i++) {
            vertexs[i] = Float.parseFloat(vertexArray[i]);
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
        // 加载材质贴图
        Texture texture = getTextureCache(uuid);
        if (texture != null) {
            textureBitmap = texture.bitmap;
            textureId = texture.textureId;
            refreshRender();
        } else {
            if (!TextUtils.isTextEmpity(texture0Str)) {
                Glide.with(OrderKingApplication.getInstant()).asBitmap().load(texture0Str).into(new SimpleTarget<Bitmap>() {
                    @SuppressLint("StaticFieldLeak")
                    @Override
                    public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                        // 异步线程执行
                        new AsyncTask<Bitmap, Integer, Bitmap>() {
                            @Override
                            protected Bitmap doInBackground(Bitmap... bitmaps) {
                                return bitmaps[0];
                            }

                            @Override
                            protected void onPostExecute(Bitmap bitmap) {
                                super.onPostExecute(bitmap);
                                // 赋值位图
                                textureBitmap = bitmap;
                                // 开启加载贴图
                                needLoadTexture = true;
                                refreshRender();
                            }
                        }.execute(bitmap);
                    }
                });
            }
        }
    }

    /**
     * 渲染
     */
    public void render(FurnitureMatrixs furnitureMatrixs, int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition) {
        if (needLoadTexture) {
            needLoadTexture = false;
            textureId = createTextureIdAndCache(uuid, textureBitmap, false);
            refreshRender();
        }
        if (textureId != -1 && (!TextUtils.isTextEmpity(normalsStr))) {
            // 关闭混色
            GLES30.glDisable(GLES30.GL_BLEND);
            // 顶点
            GLES30.glVertexAttribPointer(positionAttribute, 3, GLES30.GL_FLOAT, false, 12, vertexsBuffer);
            GLES30.glEnableVertexAttribArray(positionAttribute);
            if (!onlyPosition) {
                // 法线
                GLES30.glVertexAttribPointer(normalAttribute, 3, GLES30.GL_FLOAT, false, 12, normalsBuffer);
                GLES30.glEnableVertexAttribArray(normalAttribute);
                // 纹理
                GLES30.glVertexAttribPointer(ViewingShader.scene_uv0, 2, GLES30.GL_FLOAT, false, 8, texcoordBuffer);
                GLES30.glEnableVertexAttribArray(ViewingShader.scene_uv0);
                // 贴图
                GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT);
                GLES30.glUniform1i(ViewingShader.scene_s_baseMap, 0);
                // 着色器使用标志
                GLES30.glUniform1f(ViewingShader.scene_only_color, 0.0f);
                GLES30.glUniform1f(ViewingShader.scene_use_light, 1.0f);
            }
            GLES30.glDrawElements(GLES30.GL_TRIANGLES, indices.length, GLES30.GL_UNSIGNED_SHORT, indicesBuffer);
        }
    }

    @Override
    @Deprecated
    public void render(int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition) {
    }

}
