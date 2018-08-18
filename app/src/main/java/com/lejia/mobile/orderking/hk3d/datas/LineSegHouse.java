package com.lejia.mobile.orderking.hk3d.datas;

import android.content.Context;

import com.lejia.mobile.orderking.hk3d.classes.PointList;

/**
 * Author by HEKE
 *
 * @time 2018/8/18 15:36
 * TODO: 线建墙数据，与普通画墙方式类似
 */
public class LineSegHouse extends NormalHouse {

    public LineSegHouse(Context context) {
        super(context, true);
    }

    public LineSegHouse(Context context, PointList center_pointList, int thickness) {
        super(context, center_pointList, thickness);
    }

    public LineSegHouse(Context context, PointList center_pointList, int thickness, boolean isLineSeg) {
        super(context, center_pointList, thickness, isLineSeg);
    }

    @Override
    public void render(int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition) {
        super.render(positionAttribute, normalAttribute, colorAttribute, onlyPosition);
    }

}
