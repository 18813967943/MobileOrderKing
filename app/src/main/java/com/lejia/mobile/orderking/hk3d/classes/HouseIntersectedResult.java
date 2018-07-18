package com.lejia.mobile.orderking.hk3d.classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.lejia.mobile.orderking.hk3d.datas.House;
import com.seisw.util.geom.Poly;

import java.util.ArrayList;
import java.util.Map;

/**
 * Author by HEKE
 *
 * @time 2018/7/17 17:18
 * TODO: 房间相交结果
 */
public class HouseIntersectedResult implements Parcelable {

    public int type; // 两区域类型 : -1为没有相交，0为重合,1为相交
    public ArrayList<PointList> pointListsList; // 两区域相交结果集合列表
    public House house; // 相交的房间

    /**
     * 被相交房间亦或相交结果，重新拆分后的房间围点列表
     */
    public PointList differencePointList;

    /**
     * 所在组合区域实体对象
     */
    public Map.Entry<Integer, Poly> atPolyEntry;

    public HouseIntersectedResult() {
        super();
    }

    public HouseIntersectedResult(int type, ArrayList<PointList> pointListsList, House house) {
        this.type = type;
        this.pointListsList = pointListsList;
        this.house = house;
        initIntersectedHouse();
    }

    protected HouseIntersectedResult(Parcel in) {
        type = in.readInt();
        differencePointList = in.readParcelable(PointList.class.getClassLoader());
        pointListsList = in.createTypedArrayList(PointList.CREATOR);
    }

    /**
     * 重新计算被相交的房间切割后的结果列表
     */
    private void initIntersectedHouse() {
        if (house == null || pointListsList == null || pointListsList.size() == 0 || type != 1)
            return;
        Poly poly = PolyE.toPolyDefault(house.centerPointList);
        Poly difference = null;
        for (PointList pointList : pointListsList) {
            if (difference == null) {
                difference = poly.difference(PolyE.toPolyDefault(pointList));
            } else {
                difference = difference.difference(PolyE.toPolyDefault(pointList));
            }
        }
        differencePointList = PolyE.toPointList(difference);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeParcelable(differencePointList, flags);
        dest.writeTypedList(pointListsList);
    }

    /**
     * 释放数据引用，避免Map中数据无法被回收
     */
    public void release() {
        pointListsList = null;
        atPolyEntry = null;
        differencePointList = null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<HouseIntersectedResult> CREATOR = new Creator<HouseIntersectedResult>() {
        @Override
        public HouseIntersectedResult createFromParcel(Parcel in) {
            return new HouseIntersectedResult(in);
        }

        @Override
        public HouseIntersectedResult[] newArray(int size) {
            return new HouseIntersectedResult[size];
        }
    };

}
