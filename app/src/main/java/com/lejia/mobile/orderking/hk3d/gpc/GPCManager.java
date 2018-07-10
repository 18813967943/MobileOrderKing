package com.lejia.mobile.orderking.hk3d.gpc;

import android.content.Context;

import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.hk3d.classes.Tile;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/7/10 9:51
 * TODO: 切割管理对象
 */
public class GPCManager {

    private Context mContext;

    public GPCManager(Context context) {
        this.mContext = context;
    }

    /**
     * 常态铺砖
     *
     * @param direction，详细信息请查看Direction对象
     * @param pointsList                   铺砖区域
     * @param tile                         铺砖对象
     */
    public void asyncDoNormalTile(int direction, ArrayList<Point> pointsList, Tile tile) {
        if (pointsList == null || pointsList.size() < 3 || tile == null)
            return;

    }

}
