package com.lejia.mobile.orderking.hk3d.datas;

import android.graphics.Bitmap;
import android.opengl.GLES30;

import com.lejia.mobile.orderking.hk3d.ViewingShader;
import com.lejia.mobile.orderking.hk3d.classes.LJ3DPoint;
import com.lejia.mobile.orderking.hk3d.classes.NameData;
import com.lejia.mobile.orderking.hk3d.classes.PointList;
import com.lejia.mobile.orderking.hk3d.classes.RectD;
import com.lejia.mobile.orderking.hk3d.classes.Trianglulate;
import com.lejia.mobile.orderking.hk3d.factory.RoomNameBitmapFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

/**
 * Author by HEKE
 *
 * @time 2018/8/1 19:50
 * TODO: 房间名称对象
 */
public class HouseName extends RendererObject {

    private PointList innerPointList;

    /**
     * 房间名称信息对象
     */
    private NameData nameData;
    private boolean needBindTextureId;

    private void loadDatas() {
        if (nameData == null)
            return;
        uuid = UUID.randomUUID().toString();
        nameData.pointList.setPointsList(nameData.pointList.antiClockwise());
        lj3DPointsList = nameData.pointList.to3dList();
        RectD box = nameData.pointList.getRectBox();
        indices = new Trianglulate().getTristrip(nameData.pointList.toArray());
        int size = indices.length;
        vertexs = new float[3 * size];
        texcoord = new float[2 * size];
        normals = new float[3 * size];
        for (int i = 0; i < size; i++) {
            LJ3DPoint point = lj3DPointsList.get(indices[i]);
            int index = 3 * i;
            vertexs[index] = (float) point.x;
            vertexs[index + 1] = (float) point.y;
            vertexs[index + 2] = (float) point.z;
            int uvIndex = 2 * i;
            texcoord[uvIndex] = (float) (Math.abs(point.x - box.left) / box.width());
            texcoord[uvIndex + 1] = 1.0f - (float) (Math.abs(point.y - box.bottom) / box.height());
        }
        vertexsBuffer = ByteBuffer.allocateDirect(4 * vertexs.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexsBuffer.put(vertexs).position(0);
        texcoordBuffer = ByteBuffer.allocateDirect(4 * texcoord.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        texcoordBuffer.put(texcoord).position(0);
        indicesBuffer = ByteBuffer.allocateDirect(2 * indices.length).order(ByteOrder.nativeOrder()).asShortBuffer();
        indicesBuffer.put(indices).position(0);
    }

    public HouseName(PointList innerPointList) {
        this.innerPointList = innerPointList;
    }

    /**
     * 根据地面铺砖贴图创建名称贴图材质
     *
     * @param bitmap
     */
    public void createNameTextureWithGroundBitmap(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled())
            return;
        nameData = RoomNameBitmapFactory.createRoomNameBitmap(null, innerPointList, bitmap);
        // 初次创建
        if (indices == null) {
            loadDatas();
        }
        if (nameData != null)
            needBindTextureId = true;
        refreshRender();
    }

    /**
     * 房间名称数据对象
     */
    public NameData getNameData() {
        return nameData;
    }

    @Override
    public void render(int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition) {
        if (needBindTextureId) {
            needBindTextureId = false;
            textureId = createTextureIdAndCache(uuid, nameData.bitmap, true);
        } else {
            if (textureId != -1) {
                // 开启混色
                GLES30.glEnable(GLES30.GL_BLEND);
                GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA);
                // 顶点
                GLES30.glVertexAttribPointer(positionAttribute, 3, GLES30.GL_FLOAT, false, 12, vertexsBuffer);
                GLES30.glEnableVertexAttribArray(positionAttribute);
                if (!onlyPosition) {
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
                // 关闭混色
                GLES30.glDisable(GLES30.GL_BLEND);
            }
        }
    }

}
