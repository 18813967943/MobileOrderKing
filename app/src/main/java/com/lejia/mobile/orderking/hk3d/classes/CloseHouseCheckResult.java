package com.lejia.mobile.orderking.hk3d.classes;

import com.lejia.mobile.orderking.hk3d.datas.House;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/8/1 14:20
 * TODO: 闭合房间切割结果对象
 */
public class CloseHouseCheckResult {

    /**
     * 需要加载地面的房间
     */
    public House needLoadGroundHouse;


    /**
     * 需要新建的所有房间面围点列表集合对象
     */
    public ArrayList<PointList> pointListsList;

    public CloseHouseCheckResult() {
        pointListsList = new ArrayList<>();
    }

    public CloseHouseCheckResult(House needLoadGroundHouse, ArrayList<PointList> pointListsList) {
        this.needLoadGroundHouse = needLoadGroundHouse;
        this.pointListsList = pointListsList;
    }

    /**
     * 新增区域
     *
     * @param pointList
     */
    public void add(PointList pointList) {
        if (pointList == null || pointList.invalid())
            return;
        this.pointListsList.add(pointList);
    }

}
