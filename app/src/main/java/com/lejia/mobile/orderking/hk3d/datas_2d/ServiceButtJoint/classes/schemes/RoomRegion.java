package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/11/28 11:42
 * TODO: 房间形状围点列表对象
 */
public class RoomRegion implements Parcelable{

    public ArrayList<TPoint> tPointsList;

    public RoomRegion() {
        tPointsList = new ArrayList<>();
    }

    protected RoomRegion(Parcel in) {
        tPointsList = in.createTypedArrayList(TPoint.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(tPointsList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RoomRegion> CREATOR = new Creator<RoomRegion>() {
        @Override
        public RoomRegion createFromParcel(Parcel in) {
            return new RoomRegion(in);
        }

        @Override
        public RoomRegion[] newArray(int size) {
            return new RoomRegion[size];
        }
    };

    public String toXml() {
        String v = "<RoomRegion>";
        if (tPointsList != null && tPointsList.size() > 0) {
            for (TPoint tPoint : tPointsList) {
                v += "\n" + tPoint.toXml();
            }
        }
        v += "\n</RoomRegion>";
        return v;
    }

    @Override
    public String toString() {
        return "RoomRegion : " + tPointsList;
    }
}
