package com.lejia.mobile.orderking.hk3d.classes;

import android.graphics.Bitmap;

/**
 * Author by HEKE
 *
 * @time 2018/8/1 20:02
 * TODO: 房间名称数据
 */
public class NameData {

    public String name; // 房间名称
    public String area; // 面积,单位平方米
    public Point centerPoint; // 吸附中心点
    public Bitmap bitmap; // 房间名称位图
    public PointList pointList; // 房间名称所在围点列表

    public NameData() {
        super();
    }

    public NameData(String name, String area, Point centerPoint, Bitmap bitmap, PointList pointList) {
        this.name = name;
        this.area = area;
        this.centerPoint = centerPoint;
        this.bitmap = bitmap;
        this.pointList = pointList;
    }

    @Override
    public String toString() {
        return name + "," + area + "," + centerPoint + "," + pointList;
    }

}
