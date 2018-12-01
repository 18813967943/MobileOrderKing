package com.lejia.mobile.orderking.hk3d.datas_2d.cadwidgets;

import android.graphics.Bitmap;
import android.opengl.GLES30;

import com.lejia.mobile.orderking.hk3d.RendererState;
import com.lejia.mobile.orderking.hk3d.ViewingShader;
import com.lejia.mobile.orderking.hk3d.classes.LJ3DPoint;
import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.classes.PointList;
import com.lejia.mobile.orderking.hk3d.classes.Trianglulate;
import com.lejia.mobile.orderking.hk3d.datas_2d.Selector;

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

    @Override
    public void initDatas() {
        // 根据吸附点、厚度变化刷新数据
        thicknessPointsList = PointList.getRotateVertexs(angle, thickness, xlong, point);
        thicknessPointsList = new PointList(thicknessPointsList).antiClockwise();
        lj3DPointsList = new PointList(thicknessPointsList).to3dList();
        indices = new Trianglulate().getTristrip(new PointList(thicknessPointsList).toArray());
        LJ3DPoint normal = LJ3DPoint.spaceNormal(lj3DPointsList.get(indices[0]), lj3DPointsList.get(indices[1])
                , lj3DPointsList.get(indices[2]));
        vertexs = createVertexsBuffer(thicknessPointsList, indices);
        texcoord = createUvBufferByAngleOnlyRectangle(angle);
        if (mirror) {
            for (int i = 0; i < texcoord.length; i++) {
                if (i % 2 == 0) {
                    texcoord[i] = 1.0f - texcoord[i];
                }
            }
        }
        normals = createNormalsBuffer(thicknessPointsList, indices, normal);
        vertexsBuffer = ByteBuffer.allocateDirect(4 * vertexs.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexsBuffer.put(vertexs).position(0);
        texcoordBuffer = ByteBuffer.allocateDirect(4 * texcoord.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        texcoordBuffer.put(texcoord).position(0);
        normalsBuffer = ByteBuffer.allocateDirect(4 * normals.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        normalsBuffer.put(normals).position(0);
        // 选中对象
        selector = new Selector(new PointList(thicknessPointsList));
    }

    @Override
    public void bindTexture() {
        
    }

    @Override
    public void render(int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition) {
        // 非平面不进行渲染
        if (RendererState.isNot2D()) {
            return;
        }
        if (vertexsBuffer != null) {
            // 加载材质贴图
            if (needBindTextureId) {
                needBindTextureId = false;
                textureId = createTextureIdAndCache(uuid, bitmap, false);
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
