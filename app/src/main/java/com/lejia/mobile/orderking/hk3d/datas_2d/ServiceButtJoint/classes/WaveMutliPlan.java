package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/11/20 16:36
 * TODO: 多层波打线数据对象
 */
public class WaveMutliPlan implements Parcelable {

    public int planType; // 铺砖计划类型
    public int type; // 类型
    public String guid; // 唯一编码

    /**
     * 每层波打线详细铺贴数据对象列表
     */
    public ArrayList<TilePlan> tilePlanArrayList = new ArrayList<>();

    public WaveMutliPlan() {
        super();
    }

    public WaveMutliPlan(int planType, int type, String guid, ArrayList<TilePlan> tilePlanArrayList) {
        this.planType = planType;
        this.type = type;
        this.guid = guid;
        this.tilePlanArrayList = tilePlanArrayList;
    }


    protected WaveMutliPlan(Parcel in) {
        planType = in.readInt();
        type = in.readInt();
        guid = in.readString();
        tilePlanArrayList = in.createTypedArrayList(TilePlan.CREATOR);
    }

    /**
     * 判断是否单层波打线
     */
    public boolean isSingleWaveLine() {
        return tilePlanArrayList.size() == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(planType);
        dest.writeInt(type);
        dest.writeString(guid);
        dest.writeTypedList(tilePlanArrayList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WaveMutliPlan> CREATOR = new Creator<WaveMutliPlan>() {
        @Override
        public WaveMutliPlan createFromParcel(Parcel in) {
            return new WaveMutliPlan(in);
        }

        @Override
        public WaveMutliPlan[] newArray(int size) {
            return new WaveMutliPlan[size];
        }
    };

    /**
     * 释放数据
     */
    public void release() {
        if (tilePlanArrayList != null && tilePlanArrayList.size() > 0) {
            for (TilePlan tilePlan : tilePlanArrayList) {
                tilePlan.release();
            }
            tilePlanArrayList.clear();
            tilePlanArrayList = null;
        }
    }

    @Override
    public String toString() {
        return "WaveMutliPlan: " + planType + "," + type + "," + guid + "," + tilePlanArrayList;
    }
}
