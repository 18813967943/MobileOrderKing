package com.lejia.mobile.orderking.hk3d.datas;

import com.lejia.mobile.orderking.hk3d.classes.PointList;

/**
 * Author by HEKE
 *
 * @time 2018/7/16 16:50
 * TODO: 地面
 */
public class Ground extends RendererObject {

    private PointList pointList; // 地面围点列表

    private void initDatas() {

    }

    public Ground(PointList pointList) {
        this.pointList = pointList;
        initDatas();
    }

    @Override
    public void render(int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition) {

    }

}
