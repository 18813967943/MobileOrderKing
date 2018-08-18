package com.lejia.mobile.orderking.hk3d.datas;

import android.graphics.Bitmap;
import android.opengl.GLES30;

import com.lejia.mobile.orderking.bases.OrderKingApplication;
import com.lejia.mobile.orderking.hk3d.RendererState;
import com.lejia.mobile.orderking.hk3d.ViewingShader;
import com.lejia.mobile.orderking.hk3d.classes.LJ3DPoint;
import com.lejia.mobile.orderking.hk3d.classes.PointList;
import com.lejia.mobile.orderking.hk3d.classes.RectD;
import com.lejia.mobile.orderking.hk3d.classes.TileDescription;
import com.lejia.mobile.orderking.hk3d.classes.Trianglulate;
import com.lejia.mobile.orderking.hk3d.gpc.NSGPCManager;
import com.lejia.mobile.orderking.hk3d.gpc.OnTilesResultListener;
import com.lejia.mobile.orderking.utils.TextUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Author by HEKE
 *
 * @time 2018/7/16 16:50
 * TODO: 地面
 */
public class Ground extends RendererObject {

    private House house; // 所属房间
    private boolean needLoadBitmap; // 是否需要加载瓷砖贴图
    private boolean canDraw; // 是否可渲染
    private boolean needBindTextureId; // 是否需要打开绑定材质贴图标签
    private boolean needRefreshNameTexture; // 是否需要刷新房间名称显示
    private boolean fromReplaceTiles; // 来自于瓷砖替换操作
    private PointList pointList; // 地面围点列表
    private NSGPCManager gpcManager; // 切割管理对象

    /**
     * 瓷砖列表
     */
    private ArrayList<TileDescription> tileDescriptionsList;
    private int cellSize; // 层数
    private int cellCount; // 层加载计数
    private Bitmap bitmap; // 组成位图

    private void initDatas() {
        if (pointList == null || pointList.invalid())
            return;
        uuid = UUID.randomUUID().toString();
        // 字节缓存数据
        pointList.setPointsList(pointList.antiClockwise());
        lj3DPointsList = pointList.to3dList();
        RectD box = pointList.getRectBox();
        indices = new Trianglulate().getTristrip(pointList.toArray());
        int size = indices.length;
        vertexs = new float[3 * size];
        texcoord = new float[2 * size];
        normals = new float[3 * size];
        LJ3DPoint normal = LJ3DPoint.spaceNormal(lj3DPointsList.get(indices[0]), lj3DPointsList.get(indices[1])
                , lj3DPointsList.get(indices[2]));
        for (int i = 0; i < size; i++) {
            LJ3DPoint point = lj3DPointsList.get(indices[i]);
            int index = 3 * i;
            vertexs[index] = (float) point.x;
            vertexs[index + 1] = (float) point.y;
            vertexs[index + 2] = (float) point.z;
            normals[index] = (float) normal.x;
            normals[index + 1] = (float) normal.y;
            normals[index + 2] = (float) normal.z;
            int uvIndex = 2 * i;
            texcoord[uvIndex] = (float) (Math.abs(point.x - box.left) / box.width());
            texcoord[uvIndex + 1] = 1.0f - (float) (Math.abs(point.y - box.bottom) / box.height());
        }
        vertexsBuffer = ByteBuffer.allocateDirect(4 * vertexs.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexsBuffer.put(vertexs).position(0);
        normalsBuffer = ByteBuffer.allocateDirect(4 * normals.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        normalsBuffer.put(normals).position(0);
        texcoordBuffer = ByteBuffer.allocateDirect(4 * texcoord.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        texcoordBuffer.put(texcoord).position(0);
        indicesBuffer = ByteBuffer.allocateDirect(2 * indices.length).order(ByteOrder.nativeOrder()).asShortBuffer();
        indicesBuffer.put(indices).position(0);
    }

    public Ground(PointList pointList, House house) {
        this.pointList = pointList;
        this.house = house;
        initDatas();
        defaultLoadGroundTile();
    }

    // 默认加载地砖
    private void defaultLoadGroundTile() {
        TileDescription tileDescription = ((OrderKingApplication) house.getContext().getApplicationContext()).getRandomTileDescription();
        if (tileDescription != null) {
            ArrayList<TileDescription> tileDescriptionsList = new ArrayList<>();
            tileDescriptionsList.add(tileDescription);
            setTileDescriptionsList(tileDescriptionsList);
        }
    }

    // 获取所属的房间
    public House getHouse() {
        return house;
    }

    /**
     * 设置铺砖数据内容
     *
     * @param tileDescriptionsList
     */
    public void setTileDescriptionsList(ArrayList<TileDescription> tileDescriptionsList) {
        if (tileDescriptionsList == null || tileDescriptionsList.size() == 0)
            return;
        fromReplaceTiles = !TileDescription.isTileDescriptionListEquals(this.tileDescriptionsList, tileDescriptionsList);
        this.tileDescriptionsList = tileDescriptionsList;
        // 有效加载数据
        if (this.tileDescriptionsList != null && this.tileDescriptionsList.size() > 0) {
            cellSize = this.tileDescriptionsList.size();
            cellCount = 0;
            canDraw = false;
            for (TileDescription tileDescription : this.tileDescriptionsList) {
                tileDescription.loadBitmaps(onTileDescriptionLoadListener);
            }
        }
    }

    /**
     * 获取地面内部的铺砖管理对象
     */
    public NSGPCManager getGpcManager() {
        return gpcManager;
    }

    /**
     * 根据材质编码获取材质位图
     *
     * @param materialCode 编码
     * @return 返回对应贴图
     */
    public Bitmap getTileBitmap(String materialCode, int styleType) {
        if (TextUtils.isTextEmpity(materialCode) || tileDescriptionsList == null)
            return null;
        Bitmap bmp = null;
        // 普通铺砖、波打线
        if (styleType <= 2) {
            for (TileDescription tileDescription : tileDescriptionsList) {
                bmp = tileDescription.getTileBitmap(materialCode);
                if (bmp != null)
                    break;
            }
        }
        // 样式砖
        else {

        }
        // 方案砖
        return bmp;
    }

    /**
     * 每层材质加载回调监听接口
     */
    private TileDescription.OnTileDescriptionLoadListener onTileDescriptionLoadListener = new TileDescription.OnTileDescriptionLoadListener() {
        @Override
        public void onLoaded() {
            cellCount++;
            if (cellCount == cellSize) {
                // 刷新绑定材质
                needLoadBitmap = true;
                refreshRender();
            }
        }
    };

    /**
     * 铺砖完成结果监听对象
     */
    private OnTilesResultListener onTilesResultListener = new OnTilesResultListener() {
        @Override
        public void textureJointCompleted(Bitmap bitmap) {
            if (bitmap != null) {
                Ground.this.bitmap = bitmap;
                needBindTextureId = true;
                refreshRender();
            }
        }
    };

    @Override
    public void render(int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition) {
        // 进行切割
        if (needLoadBitmap) {
            needLoadBitmap = false;
            // 进行铺砖对象切割
            if (gpcManager == null)
                gpcManager = new NSGPCManager(pointList, tileDescriptionsList, this, onTilesResultListener);
            gpcManager.setTileDescriptionsList(tileDescriptionsList);
        }
        // 渲染内容
        else {
            // 加载材质贴图
            if (needBindTextureId) {
                needBindTextureId = false;
                textureId = createTextureIdAndCache(uuid, bitmap, fromReplaceTiles);
                canDraw = true;
                needRefreshNameTexture = true;
                refreshRender();
            }
            // 材质贴图不为空
            if (textureId != -1 && canDraw) {
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
                    GLES30.glUniform1i(ViewingShader.scene_s_baseMap, 0);
                    // 着色器使用标志
                    GLES30.glUniform1f(ViewingShader.scene_only_color, 0.0f);
                    if (RendererState.isNot2D()) {
                        GLES30.glUniform1f(ViewingShader.scene_use_light, 1.0f);
                    } else {
                        GLES30.glUniform1f(ViewingShader.scene_use_light, 0.0f);
                    }
                }
                GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, indices.length);
                // TODO 刷新房间名称显示
                if (needRefreshNameTexture) {
                    needRefreshNameTexture = false;
                    if (house.houseName != null) {
                        house.houseName.createNameTextureWithGroundBitmap(bitmap);
                    }
                }
            }
        }
    }

}
