package com.lejia.mobile.orderking.hk3d.gpc;

import com.lejia.mobile.orderking.hk3d.classes.Point;

/**
 * Author by HEKE
 *
 * @time 2018/7/26 15:28
 * TODO: 砖缝中心点描述对象
 */
public class GapPoint {

    public double width; // 宽度(长度)
    public double height; // 高度(厚度)
    public Point point; // 中心点

    public GapPoint() {
        super();
    }

    public GapPoint(double width, double height, Point point) {
        this.width = width;
        this.height = height;
        this.point = point;
    }

}
