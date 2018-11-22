package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.lejia.mobile.orderking.bases.OrderKingApplication;
import com.lejia.mobile.orderking.classes.ResUrlNodeXml;
import com.lejia.mobile.orderking.classes.XInfo;
import com.lejia.mobile.orderking.hk3d.classes.PointList;
import com.lejia.mobile.orderking.hk3d.datas_2d.Area3D;
import com.lejia.mobile.orderking.hk3d.datas_2d.Ground;
import com.lejia.mobile.orderking.hk3d.gpc.OnTilesResultListener;
import com.lejia.mobile.orderking.utils.BitmapUtils;

import java.util.ArrayList;

import geom.Point;
import geom.gpc.GPCConfig;
import geom.gpc.GPCManager;
import geom.gpc.TileInfoList;

/**
 * Author by HEKE
 *
 * @time 2018/11/19 18:03
 * TODO: 普通砖铺砖
 */
public class NormalPave {

    private Context mContext;
    private PointList pointList;
    private ResUrlNodeXml.ResPath resPath;
    private OnTilesResultListener onTilesResultListener;
    private Ground ground; // 所在的房间地面

    private XInfo normalXInfo; // 使用的普通砖信息对象
    private Bitmap originBitmap; // 使用铺砖位图

    /**
     * 铺砖起始方向
     */
    private int direction = GPCConfig.FROM_RIGHT_TOP;

    /**
     * 是否45°斜铺
     */
    private boolean skewTile;

    /**
     * 砖缝颜色
     */
    private int gapsColor = 0xFF333333;

    /**
     * 砖缝厚度
     */
    private float brickGap = 0.2f;

    /**
     * 铺砖结果对象
     */
    private NTTileResult NTTileResult;

    /**
     * 基础构造函数一
     *
     * @param pointList
     * @param resPath
     * @param ground
     * @param onTilesResultListener
     */
    public NormalPave(PointList pointList, ResUrlNodeXml.ResPath resPath, Ground ground, OnTilesResultListener onTilesResultListener) {
        this.mContext = OrderKingApplication.getInstant();
        this.pointList = pointList;
        this.resPath = resPath;
        this.ground = ground;
        this.onTilesResultListener = onTilesResultListener;
        new DefaultTile().getTilesXInfo(resPath.name, resPath.nodeName, new DefaultTile.OnDefaultTilesListener() {
            @Override
            public void compelet(XInfo xInfo, Bitmap bitmap) {
                normalXInfo = xInfo;
                tile();
            }
        });
    }

    /**
     * 基础构造函数二
     *
     * @param pointList
     * @param resPath
     * @param xInfo
     * @param ground
     * @param onTilesResultListener
     */
    public NormalPave(PointList pointList, ResUrlNodeXml.ResPath resPath, XInfo xInfo, Ground ground, OnTilesResultListener onTilesResultListener) {
        this.mContext = OrderKingApplication.getInstant();
        this.pointList = pointList;
        this.resPath = resPath;
        this.normalXInfo = xInfo;
        this.ground = ground;
        this.onTilesResultListener = onTilesResultListener;
        tile();
    }

    /**
     * 构造函数三，基于无需进行铺砖操作的前提下，创建NormalPave对象
     *
     * @param resPath
     * @param onNoNeedTileListener
     */
    @SuppressLint("StaticFieldLeak")
    public NormalPave(ResUrlNodeXml.ResPath resPath, OnNoNeedTileListener onNoNeedTileListener) {
        this.resPath = resPath;
        final OnNoNeedTileListener myOnNoNeedTileListener = onNoNeedTileListener;
        new DefaultTile().getTilesXInfo(resPath.name, resPath.nodeName, new DefaultTile.OnDefaultTilesListener() {
            @Override
            public void compelet(final XInfo xInfo, Bitmap bitmap) {
                normalXInfo = xInfo;
                new AsyncTask<String, Integer, String>() {
                    @Override
                    protected String doInBackground(String... strings) {
                        originBitmap = BitmapUtils.createBitmapByXInfo(xInfo);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        if (myOnNoNeedTileListener != null) {
                            myOnNoNeedTileListener.created();
                        }
                    }
                }.execute();
            }
        });
    }

    /**
     * 设置铺砖方向
     *
     * @param direction
     */
    public void setDirection(int direction) {
        if (this.direction == direction)
            return;
        this.direction = direction;
        tile();
    }

    /**
     * 设置斜铺
     *
     * @param skewTile
     */
    public void setSkewTile(boolean skewTile) {
        this.skewTile = skewTile;
        tile();
    }

    /**
     * 设置砖缝颜色
     *
     * @param color
     */
    public void setGapsColor(int color) {
        this.gapsColor = color;
        tile();
    }

    public int getGapsColor() {
        return gapsColor;
    }

    /**
     * 设置砖缝厚度
     *
     * @param brickGap
     */
    public void setBrickGap(float brickGap) {
        this.brickGap = brickGap;
        tile();
    }

    public float getBrickGap() {
        return brickGap;
    }

    // 获取当前铺贴方向
    public int getDirection() {
        return direction;
    }

    // 获取当前是否斜铺
    public boolean isSkewTile() {
        return skewTile;
    }

    // 获取所在的房间地面
    public Ground getGround() {
        return ground;
    }

    // 获取铺砖结果对象
    public NTTileResult getTilesResult() {
        return NTTileResult;
    }

    // 基础瓷砖信息对象
    public XInfo getNormalXInfo() {
        return normalXInfo;
    }

    /**
     * 原始贴图的位图
     */
    public Bitmap getOriginBitmap() {
        return originBitmap;
    }

    /**
     * TODO 进行切割铺贴
     */
    @SuppressLint("StaticFieldLeak")
    private void tile() {
        new AsyncTask<String, Integer, NTTileResult>() {
            @Override
            protected NTTileResult doInBackground(String... strings) {
                if (NTTileResult != null) {
                    NTTileResult.release();
                    NTTileResult = null;
                }
                NTTileResult = new NTTileResult(pointList.getRectBox(), NormalPave.this);
                try {
                    // 加载贴图
                    if (originBitmap == null) {
                        originBitmap = BitmapUtils.createBitmapByXInfo(normalXInfo);
                    }
                    int tileWidth = originBitmap.getWidth();
                    int tileHeight = originBitmap.getHeight();
                    ArrayList<Point> geomList = pointList.toGeomPointList();
                    GPCManager gpcManager = new GPCManager(geomList, tileWidth, tileHeight, brickGap, brickGap,
                            direction, skewTile ? GPCConfig.TILT : GPCConfig.STRAIGHT);
                    // 实质砖
                    TileInfoList mTileInfoList = gpcManager.getTileInfoList();
                    createArea3D(mTileInfoList, false);
                    // 砖缝
                    TileInfoList xGapTileInfoList = gpcManager.getXGapInfoList();
                    TileInfoList yGapTileInfoList = gpcManager.getYGapInfoList();
                    createArea3D(xGapTileInfoList, true);
                    createArea3D(yGapTileInfoList, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return NTTileResult;
            }

            @Override
            protected void onPostExecute(NTTileResult tilesResult) {
                super.onPostExecute(tilesResult);
                if (onTilesResultListener != null)
                    onTilesResultListener.textureJointCompleted(tilesResult.getHoleBitmap());
            }
        }.execute();
    }

    /**
     * 创建区域
     *
     * @param tileInfoList
     * @param isGap
     */
    private void createArea3D(TileInfoList tileInfoList, boolean isGap) {
        String materialCode = normalXInfo.materialCode;
        ArrayList<ArrayList<geom.Point>> geomIntersectList = tileInfoList.intersect_point;
        ArrayList<ArrayList<geom.Point>> geomOriginList = tileInfoList.original_point;
        for (int i = 0; i < geomIntersectList.size(); i++) {
            ArrayList<geom.Point> pointsList = geomIntersectList.get(i);
            ArrayList<geom.Point> originList = geomOriginList.get(i);
            ArrayList<com.lejia.mobile.orderking.hk3d.classes.Point> changePointsList = PointList.staticExchangeGemoListToThisList(pointsList);
            ArrayList<com.lejia.mobile.orderking.hk3d.classes.Point> changeOriginList = PointList.staticExchangeGemoListToThisList(originList);
            Area3D area3D = new Area3D(isGap, materialCode, changePointsList, changeOriginList);
            area3D.setSkewTile(skewTile);
            NTTileResult.putArea3D(area3D);
        }
    }

    /**
     * 释放数据
     */
    public void release() {
        if (NTTileResult != null) {
            NTTileResult.release();
            NTTileResult = null;
        }
        normalXInfo = null;
        if (originBitmap != null) {
            originBitmap.recycle();
            originBitmap = null;
        }
    }

    /**
     * Author by HEKE
     *
     * @time 2018/11/22 11:53
     * TODO: 回调创建无需切割铺砖的次对象的接口
     */
    public interface OnNoNeedTileListener {
        void created();
    }
}
