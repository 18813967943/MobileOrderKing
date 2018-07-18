package com.lejia.mobile.orderking.hk3d.datas;

import android.content.Context;

import com.lejia.mobile.orderking.hk3d.classes.PointList;

/**
 * Author by HEKE
 *
 * @time 2018/7/18 16:11
 * TODO: 普通房间
 */
public class NormalHouse extends House {

    public NormalHouse(Context context) {
        super(context);
    }

    public NormalHouse(Context context, PointList center_pointList, int thickness) {
        super(context, center_pointList, thickness);
    }

    @Override
    public void createRenderer() {
        super.createRenderer();

    }

}
