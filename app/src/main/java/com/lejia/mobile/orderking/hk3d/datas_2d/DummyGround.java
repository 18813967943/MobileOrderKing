package com.lejia.mobile.orderking.hk3d.datas_2d;

import android.content.Context;

import com.lejia.mobile.orderking.hk3d.classes.LJ3DPoint;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/7/16 17:56
 * TODO: 虚拟地面，用于触摸映射交点操作
 */
public class DummyGround extends RendererObject {

    public DummyGround(Context context) {
        indices = new short[]{0, 1, 2, 0, 2, 3};
        lj3DPointsList = new ArrayList<>();
        int maxValue = Integer.MAX_VALUE / 10000;
        lj3DPointsList.add(new LJ3DPoint(maxValue, maxValue, 0.0d));
        lj3DPointsList.add(new LJ3DPoint(maxValue, -maxValue, 0.0d));
        lj3DPointsList.add(new LJ3DPoint(-maxValue, -maxValue, 0.0d));
        lj3DPointsList.add(new LJ3DPoint(-maxValue, maxValue, 0.0d));
    }

    @Override
    public void render(int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition) {
    }

}
