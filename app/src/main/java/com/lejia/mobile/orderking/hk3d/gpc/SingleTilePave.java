package com.lejia.mobile.orderking.hk3d.gpc;

import android.content.Context;

import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.classes.PointList;
import com.lejia.mobile.orderking.hk3d.classes.TileDescription;
import com.lejia.mobile.orderking.hk3d.datas_2d.Area3D;

import java.util.ArrayList;

import geom.gpc.GPCConfig;
import geom.gpc.GPCManager;
import geom.gpc.TileInfoList;

/**
 * Author by HEKE
 *
 * @time 2018/7/27 10:12
 * TODO: 单张材质铺贴
 */
@Deprecated
public class SingleTilePave {

    private Context mContext;
    private ArrayList<Point> pointsList; // 铺砖区域
    private TileDescription tileDescription; // 铺砖材质，必须为单张
    private boolean skewTile; // 是否斜铺
    private int direction; // 铺砖起始方向
    private float gap; // 砖缝厚度
    private TilesResult tilesResult; // 保存结果对象
    private NSGPCManager nsGpcManager; // 铺砖管理对象

    public SingleTilePave(Context context, ArrayList<Point> pointsList, TileDescription tileDescription
            , boolean skewTile, int direction, float gap, NSGPCManager nsGpcManager, TilesResult tilesResult) {
        this.mContext = context;
        this.pointsList = pointsList;
        this.tileDescription = tileDescription;
        this.skewTile = skewTile;
        this.direction = direction;
        this.gap = gap;
        this.nsGpcManager = nsGpcManager;
        this.tilesResult = tilesResult;
        tile();
    }

    /**
     * 开始铺贴
     */
    private void tile() {
        PointList pointList = new PointList(pointsList);
        if (pointList.invalid())
            return;
        int tileWidth = tileDescription.getTileWidth(0);
        int tileHeight = tileDescription.getTileHeight(0);
        ArrayList<geom.Point> geomList = pointList.toGeomPointList();
        GPCManager gpcManager = new GPCManager(geomList, tileWidth, tileHeight, gap, gap,
                direction, skewTile ? GPCConfig.TILT : GPCConfig.STRAIGHT);
        // 实质砖
        TileInfoList mTileInfoList = gpcManager.getTileInfoList();
        createArea3D(mTileInfoList, false);
        // 砖缝
        TileInfoList xGapTileInfoList = gpcManager.getXGapInfoList();
        TileInfoList yGapTileInfoList = gpcManager.getYGapInfoList();
        createArea3D(xGapTileInfoList, true);
        createArea3D(yGapTileInfoList, true);
    }

    /**
     * 创建区域
     *
     * @param tileInfoList
     * @param isGap
     */
    private void createArea3D(TileInfoList tileInfoList, boolean isGap) {
        String materialCode = tileDescription.getMaterialCode(0, 0);
        ArrayList<ArrayList<geom.Point>> geomIntersectList = tileInfoList.intersect_point;
        ArrayList<ArrayList<geom.Point>> geomOriginList = tileInfoList.original_point;
        for (int i = 0; i < geomIntersectList.size(); i++) {
            ArrayList<geom.Point> pointsList = geomIntersectList.get(i);
            ArrayList<geom.Point> originList = geomOriginList.get(i);
            ArrayList<Point> changePointsList = PointList.staticExchangeGemoListToThisList(pointsList);
            ArrayList<Point> changeOriginList = PointList.staticExchangeGemoListToThisList(originList);
            Area3D area3D = new Area3D(isGap, materialCode, changePointsList, changeOriginList);
            area3D.setSkewTile(skewTile);
            tilesResult.putArea3D(area3D);
        }
    }

}
