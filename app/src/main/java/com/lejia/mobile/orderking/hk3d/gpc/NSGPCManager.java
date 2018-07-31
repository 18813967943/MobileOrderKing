package com.lejia.mobile.orderking.hk3d.gpc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.lejia.mobile.orderking.bases.OrderKingApplication;
import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.classes.PointList;
import com.lejia.mobile.orderking.hk3d.classes.TileDescription;
import com.lejia.mobile.orderking.hk3d.datas.Ground;

import java.util.ArrayList;

import geom.gpc.GPCConfig;

/**
 * Author by HEKE
 *
 * @time 2018/7/10 9:51
 * TODO: 切割管理对象
 */
public class NSGPCManager {

    private Context mContext;
    private PointList pointList; // 围点
    private ArrayList<TileDescription> tileDescriptionsList; // 多层数据列表
    private Ground ground; // 所在的房间地面

    /**
     * 铺砖起始方向
     */
    private int direction = GPCConfig.FROM_LEFT_TOP;

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
    private int brickGap = 2;

    /**
     * 铺砖结果对象
     */
    private TilesResult tilesResult;

    /**
     * 铺砖组合完毕接口
     */
    private OnTilesResultListener onTilesResultListener;

    public NSGPCManager(PointList pointList, ArrayList<TileDescription> tileDescriptionsList,
                        Ground ground, OnTilesResultListener onTilesResultListener) {
        this.mContext = OrderKingApplication.getInstant();
        this.pointList = pointList;
        this.tileDescriptionsList = tileDescriptionsList;
        this.ground = ground;
        this.onTilesResultListener = onTilesResultListener;
    }

    // 绑定铺贴结果接口
    public void setOnTilesResultListener(OnTilesResultListener onTilesResultListener) {
        this.onTilesResultListener = onTilesResultListener;
    }

    /**
     * 设置新的铺砖数据对象进行铺贴
     *
     * @param tileDescriptionsList
     */
    public void setTileDescriptionsList(ArrayList<TileDescription> tileDescriptionsList) {
        this.tileDescriptionsList = tileDescriptionsList;
        tile();
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
    public void setBrickGap(int brickGap) {
        this.brickGap = brickGap;
        tile();
    }

    public int getBrickGap() {
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

    /**
     * 进行铺砖
     */
    @SuppressLint("StaticFieldLeak")
    public void tile() {
        if (tileDescriptionsList == null || tileDescriptionsList.size() == 0)
            return;
        new AsyncTask<String, Integer, TilesResult>() {
            @Override
            protected TilesResult doInBackground(String... strings) {
                // 进行新的切割操作
                if (tilesResult == null)
                    tilesResult = new TilesResult(pointList.getRectBox(), NSGPCManager.this);
                else
                    tilesResult.clearDatas();
                try {
                    ArrayList<Point> pointsList = pointList.getPointsList();
                    for (TileDescription tileDescription : tileDescriptionsList) {
                        switch (tileDescription.styleType) {
                            case 1:
                                // 普通砖
                                new SingleTilePave(mContext, pointsList, tileDescription, skewTile,
                                        direction, brickGap, NSGPCManager.this, tilesResult);
                                break;
                            case 2:
                                // 波打线
                                ArrayList<Point> innerPointsList = PointList.offsetList(false
                                        , tileDescription.getTileHeight(0), pointsList);
                                // 切割波打线处理
                                int size = tileDescription.size();
                                if (size == 1) { // 斜切波打线

                                } else if (size == 2) { // 转角波打线

                                }
                                pointsList = innerPointsList;
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return tilesResult;
            }

            @Override
            protected void onPostExecute(TilesResult tilesResult) {
                super.onPostExecute(tilesResult);
                if (onTilesResultListener != null)
                    onTilesResultListener.textureJointCompleted(tilesResult.getHoleBitmap());
            }
        }.execute();
    }

    // 获取铺砖结果对象
    public TilesResult getTilesResult() {
        return tilesResult;
    }

}
