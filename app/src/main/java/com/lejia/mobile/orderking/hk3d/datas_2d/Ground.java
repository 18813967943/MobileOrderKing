package com.lejia.mobile.orderking.hk3d.datas_2d;

import android.graphics.Bitmap;
import android.opengl.GLES30;

import com.lejia.mobile.orderking.bases.OrderKingApplication;
import com.lejia.mobile.orderking.classes.ResUrlNodeXml;
import com.lejia.mobile.orderking.classes.XInfo;
import com.lejia.mobile.orderking.hk3d.ViewingShader;
import com.lejia.mobile.orderking.hk3d.classes.LJ3DPoint;
import com.lejia.mobile.orderking.hk3d.classes.PointList;
import com.lejia.mobile.orderking.hk3d.classes.RectD;
import com.lejia.mobile.orderking.hk3d.classes.Trianglulate;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.DefaultTile;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.NormalPave;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.WaveLinesPave;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.WaveMutliPlan;
import com.lejia.mobile.orderking.hk3d.datas_3d.classes.BuildingGround;
import com.lejia.mobile.orderking.hk3d.gpc.OnTilesResultListener;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

/**
 * Author by HEKE
 *
 * @time 2018/7/16 16:50
 * TODO: 地面
 */
public class Ground extends RendererObject {

    private House house; // 所属房间
    private boolean canDraw; // 是否可渲染
    private boolean needBindTextureId; // 是否需要打开绑定材质贴图标签
    private boolean fromReplaceTiles; // 来自于瓷砖替换操作
    private PointList pointList; // 地面围点列表

    /**
     * 瓷砖列表
     */
    private Bitmap bitmap; // 组成位图

    /**
     * 普通砖铺砖
     */
    private NormalPave normalPave;

    /**
     * 波打线铺砖
     */
    private WaveLinesPave waveLinesPave;
    private WaveMutliPlan waveLinesPaveRes;

    /**
     * 对应三维的地面
     */
    private BuildingGround buildingGround;

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
        buildingGround = new BuildingGround(this);
    }

    public Ground(PointList pointList, House house) {
        this.pointList = pointList;
        this.house = house;
        initDatas();
        defaultLoadGroundTile();
    }

    // 默认加载地砖
    private void defaultLoadGroundTile() {
        OrderKingApplication orderKingApplication = (OrderKingApplication) getContext().getApplicationContext();
        String defaultCode = orderKingApplication.getDefaultTileCode();
        new DefaultTile(getContext(), defaultCode, new DefaultTile.OnDefaultTilesListener() {
            @Override
            public void compelet(XInfo xInfo, Bitmap bitmap) {
                // 执行铺砖切割操作
                normalPave = new NormalPave(pointList, null, xInfo, Ground.this, new OnTilesResultListener() {
                    @Override
                    public void textureJointCompleted(Bitmap bitmap) {
                        // 刷新绑定操作
                        Ground.this.bitmap = bitmap;
                        needBindTextureId = true;
                        refreshRender();
                    }
                });
            }
        });
    }

    /**
     * 设置普通换砖
     *
     * @param resPath
     */
    public void setNormalPaveRes(final ResUrlNodeXml.ResPath resPath) {
        if (resPath == null)
            return;
        // 释放上一次铺贴数据
        if (normalPave != null) {
            normalPave.release();
            normalPave = null;
        }
        // 已有波打线铺砖方案，则修改中心砖
        if (waveLinesPave != null) {
            normalPave = new NormalPave(resPath, new NormalPave.OnNoNeedTileListener() {
                @Override
                public void created() {
                    setWaveLinesPaveRes(waveLinesPaveRes);
                }
            });
        } else {
            new DefaultTile(getContext(), resPath.name, new DefaultTile.OnDefaultTilesListener() {
                @Override
                public void compelet(XInfo xInfo, Bitmap bitmap) {
                    // 执行铺砖切割操作
                    normalPave = new NormalPave(pointList, resPath, xInfo, Ground.this, new OnTilesResultListener() {
                        @Override
                        public void textureJointCompleted(Bitmap bitmap) {
                            refreshTileResult(bitmap);
                        }
                    });
                }
            });
        }
    }

    /**
     * 设置波打线铺砖
     *
     * @param waveLinesPaveRes 波打线铺砖数据
     */
    public void setWaveLinesPaveRes(WaveMutliPlan waveLinesPaveRes) {
        if (waveLinesPaveRes == null)
            return;
        this.waveLinesPaveRes = waveLinesPaveRes;
        // 释放上一次铺贴数据
        if (waveLinesPave != null) {
            waveLinesPave.release();
            waveLinesPave = null;
        }
        // 加载波打线
        waveLinesPave = new WaveLinesPave(pointList, waveLinesPaveRes, normalPave, new OnTilesResultListener() {
            @Override
            public void textureJointCompleted(Bitmap bitmap) {
                refreshTileResult(bitmap);
            }
        });
    }

    /**
     * 刷新铺砖显示
     *
     * @param bitmap
     */
    private void refreshTileResult(Bitmap bitmap) {
        Ground.this.bitmap = bitmap;
        fromReplaceTiles = true;
        needBindTextureId = true;
        buildingGround.setNeedBindTexture(true);
        refreshRender();
    }

    // 获取所属的房间
    public House getHouse() {
        return house;
    }

    /**
     * 获取对应的三维地面
     */
    public BuildingGround getBuildingGround() {
        return buildingGround;
    }

    /**
     * 获取贴图
     */
    public Bitmap getBitmap() {
        return bitmap;
    }

    /**
     * 获取围点
     */
    public float[] getVertexs() {
        return vertexs;
    }

    /**
     * 获取UV
     */
    public float[] getUV() {
        return texcoord;
    }

    /**
     * 获取地面编号
     */
    public String getUUID() {
        return uuid;
    }

    /**
     * 获取索引
     */
    public short[] getIndices() {
        return indices;
    }

    @Override
    public void render(int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition) {
        // 加载材质贴图
        if (needBindTextureId) {
            needBindTextureId = false;
            textureId = createTextureIdAndCache(uuid, bitmap, fromReplaceTiles);
            canDraw = true;
            fromReplaceTiles = false;
            refreshRender();
        }
        // 材质贴图不为空
        if (textureId != -1 && canDraw) {
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
        }
    }

}
