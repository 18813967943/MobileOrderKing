package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/11/20 16:55
 * TODO: 方案中的波打线信息对象
 */
public class Wave implements Parcelable {

    /**
     * 每层波打线详细铺贴数据对象列表
     */
    public ArrayList<TilePlan> tilePlanArrayList = new ArrayList<>();

    public Wave() {
        super();
    }

    public Wave(ArrayList<TilePlan> tilePlanArrayList) {
        this.tilePlanArrayList = tilePlanArrayList;
    }

    protected Wave(Parcel in) {
        tilePlanArrayList = in.createTypedArrayList(TilePlan.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(tilePlanArrayList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Wave> CREATOR = new Creator<Wave>() {
        @Override
        public Wave createFromParcel(Parcel in) {
            return new Wave(in);
        }

        @Override
        public Wave[] newArray(int size) {
            return new Wave[size];
        }
    };

    @Override
    public String toString() {
        return "Wave: " + tilePlanArrayList;
    }

}
