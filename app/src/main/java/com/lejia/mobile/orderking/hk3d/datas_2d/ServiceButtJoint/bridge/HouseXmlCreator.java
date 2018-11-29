package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.bridge;

import android.content.Context;

import com.lejia.mobile.orderking.hk3d.datas_2d.House;
import com.lejia.mobile.orderking.hk3d.datas_2d.HouseDatasManager;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.LogicalTile;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.Logtile;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.Phy;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.TilePlan;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes.FloorData;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes.TileLayer;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes.TileLayers;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes.TileViewPanel;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes.WaveLine;

import java.util.ArrayList;

import geom.gpc.GPCConfig;

/**
 * Author by HEKE
 *
 * @time 2018/11/29 15:46
 * TODO: 获取服务器保存的数据内容，进行房间瓷砖、名称等设置操作
 */
public class HouseXmlCreator {

    private Context mContext;
    private House house;
    private FloorData floorData;
    private HouseDatasManager houseDatasManager;

    public HouseXmlCreator(Context context, House house, FloorData floorData, HouseDatasManager houseDatasManager) {
        this.mContext = context;
        this.house = house;
        this.floorData = floorData;
        this.houseDatasManager = houseDatasManager;
        initAttrs();
    }

    private void initAttrs() {
        // 设置房间名称
        house.houseName.setHouseName(floorData.roomName);
        // 铺砖数据整理
        TileViewPanel tileViewPanel = floorData.tileViewPanel;
        TileLayers tileLayers = tileViewPanel.tileLayers;
        ArrayList<TileLayer> tileLayerArrayList = tileLayers.tileLayersList;
        TileLayer tileLayer = tileLayerArrayList.get(0); // 主铺砖数据
        // 无区域铺砖
        if (tileLayerArrayList.size() == 1) {
            WaveLine waveLine = tileLayer.waveLine;
            TilePlan tilePlan = tileLayer.tileRegion.plan.tilePlanArrayList.get(0);
            Phy phy = tilePlan.phy;
            Logtile logtile = tilePlan.logtile;
            LogicalTile logicalTile = logtile.logicalTileArrayList.get(0);
            String url = phy.tileArrayList.get(0).url;
            boolean randRotate = logicalTile.randRotate;
            int direction = tileDirectionChange(tilePlan.locate);
            float gap = tilePlan.gap * 0.1f;
            boolean isSkewTile = (logicalTile.rotate != 0.0f);
            if (waveLine == null) {
                house.ground.setTiles(url, null, randRotate, isSkewTile, direction, gap);
            } else {
                house.ground.setTiles(url, waveLine.tilePlanArrayList, randRotate, isSkewTile, direction, gap);
            }
        }
        // 包含多区域铺砖
        else {

        }
    }

    /**
     * 转换接单王的起铺方向
     *
     * @param dir
     */
    private int tileDirectionChange(int dir) {
        int ret = GPCConfig.FROM_MIDDLE;
        switch (dir) {
            case 0:
                ret = GPCConfig.FROM_RIGHT_TOP;
                break;
            case 1:
                ret = GPCConfig.FROM_MIDDLE_TOP;
                break;
            case 2:
                ret = GPCConfig.FROM_LEFT_TOP;
                break;
            case 3:
                ret = GPCConfig.FROM_MIDDLE_RIGHT;
                break;
            case 4:
                ret = GPCConfig.FROM_MIDDLE;
                break;
            case 5:
                ret = GPCConfig.FROM_MIDDLE_LEFT;
                break;
            case 6:
                ret = GPCConfig.FROM_RIGHT_BOTTOM;
                break;
            case 7:
                ret = GPCConfig.FROM_MIDDLE_BOTTOM;
                break;
            case 8:
                ret = GPCConfig.FROM_LEFT_BOTTOM;
                break;
        }
        return ret;
    }

}
