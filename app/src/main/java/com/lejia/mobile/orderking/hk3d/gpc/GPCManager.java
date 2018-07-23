package com.lejia.mobile.orderking.hk3d.gpc;

import android.content.Context;

import com.lejia.mobile.orderking.bases.OrderKingApplication;
import com.lejia.mobile.orderking.hk3d.classes.PointList;
import com.lejia.mobile.orderking.hk3d.classes.TileDescription;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/7/10 9:51
 * TODO: 切割管理对象
 */
public class GPCManager {

    private Context mContext;
    private PointList pointList; // 围点
    private ArrayList<TileDescription> tileDescriptionsList; // 多层数据列表

    /**
     * 铺砖结果对象
     */
    private TilesResult tilesResult;

    /**
     * 铺砖组合完毕接口
     */
    private OnTilesResultListener onTilesResultListener;

    public GPCManager(PointList pointList, ArrayList<TileDescription> tileDescriptionsList) {
        this.mContext = OrderKingApplication.getInstant();
        this.pointList = pointList;
        this.tileDescriptionsList = tileDescriptionsList;
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
     * 进行铺砖
     */
    public void tile() {
        if (tileDescriptionsList == null || tileDescriptionsList.size() == 0)
            return;

    }

}
