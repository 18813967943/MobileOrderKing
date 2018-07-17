package com.lejia.mobile.orderking.hk3d.datas;

import android.content.Context;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/7/16 15:39
 * TODO: 总房间数据管理
 */
public class HouseDatasManager {

    private Context mContext;
    private ArrayList<House> rectDHousesList; // 矩形房间

    public HouseDatasManager(Context context) {
        mContext = context;
        this.rectDHousesList = new ArrayList<>();
    }

    /**
     * 新增房间
     */
    public void checkThenAdd(House house) {
        if (house == null)
            return;
        // 与其他房间或墙体相交处理

        // 加入
        rectDHousesList.add(house);
    }

    /**
     * 获取房间列表
     */
    public ArrayList<House> getHousesList() {
        return rectDHousesList;
    }

}
