package com.lejia.mobile.orderking.hk3d.datas;

import com.lejia.mobile.orderking.hk3d.classes.Point;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/7/16 16:20
 * TODO: 墙体
 */
public class Wall extends RendererObject {

    private ArrayList<Point> pointsList; // 围点

    public void initDatas() {

    }

    public Wall(ArrayList<Point> pointsList) {
        this.pointsList = pointsList;
        initDatas();
    }

    @Override
    public void render(int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition) {

    }

}
