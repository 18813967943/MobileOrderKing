package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.models;

import com.lejia.mobile.orderking.hk3d.classes.Point;

/**
 * Author by HEKE
 *
 * @time 2018/12/1 17:04
 * TODO: 吸附返回主要信息数据对象
 */
public class ADI {
    public Point point; // 吸附点
    public float angle; // 角度
    public float thickness = -1; // 门窗模型厚度

    public ADI copy() {
        ADI adi = new ADI();
        adi.point = point.copy();
        adi.angle = angle;
        adi.thickness = thickness;
        return adi;
    }
}
