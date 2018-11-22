package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/11/20 16:57
 * TODO:  方案铺砖数据对象
 */
public class CombinePlan implements Parcelable {

    /**
     * 波打线数据对象
     */
    public Wave wave;

    /**
     * 内部铺砖计划数据列表
     */
    public ArrayList<TilePlan> tilePlanArrayList = new ArrayList<>();

    public CombinePlan() {
        super();
    }

    public CombinePlan(Wave wave, ArrayList<TilePlan> tilePlanArrayList) {
        this.wave = wave;
        this.tilePlanArrayList = tilePlanArrayList;
    }

    protected CombinePlan(Parcel in) {
        wave = in.readParcelable(Wave.class.getClassLoader());
        tilePlanArrayList = in.createTypedArrayList(TilePlan.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(wave, flags);
        dest.writeTypedList(tilePlanArrayList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CombinePlan> CREATOR = new Creator<CombinePlan>() {
        @Override
        public CombinePlan createFromParcel(Parcel in) {
            return new CombinePlan(in);
        }

        @Override
        public CombinePlan[] newArray(int size) {
            return new CombinePlan[size];
        }
    };

    @Override
    public String toString() {
        return "CombinePlan: " + wave + "," + tilePlanArrayList;
    }

}
