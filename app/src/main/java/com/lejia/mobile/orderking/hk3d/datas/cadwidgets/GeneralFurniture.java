package com.lejia.mobile.orderking.hk3d.datas.cadwidgets;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.opengl.GLES30;
import android.os.AsyncTask;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.lejia.mobile.orderking.bases.OrderKingApplication;
import com.lejia.mobile.orderking.hk3d.ViewingShader;
import com.lejia.mobile.orderking.hk3d.classes.LJ3DPoint;
import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.classes.PointList;
import com.lejia.mobile.orderking.hk3d.classes.Trianglulate;
import com.lejia.mobile.orderking.hk3d.datas.Furniture;
import com.lejia.mobile.orderking.hk3d.datas.Selector;
import com.lejia.mobile.orderking.utils.BitmapUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Author by HEKE
 *
 * @time 2018/8/23 19:25
 * TODO: 常态三维模型
 */
public class GeneralFurniture extends BaseCad {

    private Bitmap bitmap;
    private boolean isLoadingBitmap;
    private boolean needBindTextureId;

    public GeneralFurniture(FurTypes furTypes) {
        super(furTypes);
    }

    public GeneralFurniture(double angle, double thickness, double xlong, Point point, FurTypes furTypes) {
        super(angle, thickness, xlong, point, furTypes);
    }

    public GeneralFurniture(double angle, double thickness, double xlong, Point point, FurTypes furTypes, Furniture furniture) {
        super(angle, thickness, xlong, point, furTypes, furniture);
    }

    @Override
    public void initDatas() {
        if (furniture == null)
            return;
        // 根据吸附点、厚度变化刷新数据
        thicknessPointsList = PointList.getRotateVertexs(angle, thickness, xlong, point);
        thicknessPointsList = new PointList(thicknessPointsList).antiClockwise();
        lj3DPointsList = new PointList(thicknessPointsList).to3dList();
        indices = new Trianglulate().getTristrip(new PointList(thicknessPointsList).toArray());
        LJ3DPoint normal = LJ3DPoint.spaceNormal(lj3DPointsList.get(indices[0]), lj3DPointsList.get(indices[1])
                , lj3DPointsList.get(indices[2]));
        vertexs = createVertexsBuffer(thicknessPointsList, indices);
        texcoord = createUvBuffer(thicknessPointsList, indices);
        normals = createNormalsBuffer(thicknessPointsList, indices, normal);
        vertexsBuffer = ByteBuffer.allocateDirect(4 * vertexs.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexsBuffer.put(vertexs).position(0);
        texcoordBuffer = ByteBuffer.allocateDirect(4 * texcoord.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        texcoordBuffer.put(texcoord).position(0);
        normalsBuffer = ByteBuffer.allocateDirect(4 * normals.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        normalsBuffer.put(normals).position(0);
        // 选中对象
        selector = new Selector(new PointList(thicknessPointsList));
        // 加载贴图
        if (bitmap == null) {
            isLoadingBitmap = true;
            Glide.with(OrderKingApplication.getInstant()).asBitmap().load(furniture.topView).into(new SimpleTarget<Bitmap>() {
                @SuppressLint("StaticFieldLeak")
                @Override
                public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                    // 异步线程执行
                    new AsyncTask<Bitmap, Integer, Bitmap>() {
                        @Override
                        protected Bitmap doInBackground(Bitmap... bitmaps) {
                            return BitmapUtils.toSize(bitmaps[0], furniture.xLong / 10, furniture.width / 10);
                        }

                        @Override
                        protected void onPostExecute(Bitmap bitmap) {
                            super.onPostExecute(bitmap);
                            // 赋值位图
                            GeneralFurniture.this.bitmap = bitmap;
                            // 关闭加载
                            isLoadingBitmap = false;
                            // 开启加载贴图
                            needBindTextureId = true;
                            // 刷新请求
                            refreshRender();
                        }
                    }.execute(bitmap);
                }
            });
        }
    }

    @Override
    public void render(int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition) {
        if (vertexsBuffer != null) {
            // 加载材质贴图
            if (needBindTextureId) {
                needBindTextureId = false;
                textureId = createTextureIdAndCache(furniture.materialCode, bitmap, false);
                refreshRender();
            }
            // 渲染
            if (textureId != -1) {
                // 开启混色
                GLES30.glEnable(GLES30.GL_BLEND);
                GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA);
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
                    GLES30.glUniform1i(ViewingShader.scene_s_baseMap, 0);
                    // 着色器使用标志
                    GLES30.glUniform1f(ViewingShader.scene_only_color, 0.0f);
                    GLES30.glUniform1f(ViewingShader.scene_use_light, 0.0f);
                }
                GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, indices.length);
                // 选中绘制
                if (isSelected()) {
                    if (selector != null) {
                        selector.render(positionAttribute, normalAttribute, colorAttribute, onlyPosition);
                    }
                }
                // 关闭混色
                GLES30.glDisable(GLES30.GL_BLEND);
            }
        }
    }

}
