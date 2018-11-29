package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author by HEKE
 *
 * @time 2018/11/28 11:41
 * TODO: 房间围点层对象
 */
public class RoomLayer implements Parcelable{

    public RoomRegion roomRegion;

    public RoomLayer() {
    }

    protected RoomLayer(Parcel in) {
        roomRegion = in.readParcelable(RoomRegion.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(roomRegion, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RoomLayer> CREATOR = new Creator<RoomLayer>() {
        @Override
        public RoomLayer createFromParcel(Parcel in) {
            return new RoomLayer(in);
        }

        @Override
        public RoomLayer[] newArray(int size) {
            return new RoomLayer[size];
        }
    };

    public String toXml() {
        String v = "<RoomLayer>";
        if (roomRegion != null) {
            v += "\n" + roomRegion.toXml();
        }
        v += "\n</RoomLayer>";
        return v;
    }

    @Override
    public String toString() {
        return "RoomLayer : " + roomRegion;
    }

}
