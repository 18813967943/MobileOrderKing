package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author by HEKE
 *
 * @time 2018/11/28 10:31
 * TODO: 铺砖层数据对象
 */
public class TileLayer implements Parcelable{

    public boolean main;
    public boolean isMainArea;
    public boolean hasOffset;
    public TileRegion tileRegion; //中心砖数据对象
    public WaveLine waveLine; // 波打线数据对象

    public TileLayer() {
        main = true;
    }

    protected TileLayer(Parcel in) {
        main = in.readByte() != 0;
        isMainArea = in.readByte() != 0;
        hasOffset = in.readByte() != 0;
        tileRegion = in.readParcelable(TileRegion.class.getClassLoader());
        waveLine = in.readParcelable(WaveLine.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (main ? 1 : 0));
        dest.writeByte((byte) (isMainArea ? 1 : 0));
        dest.writeByte((byte) (hasOffset ? 1 : 0));
        dest.writeParcelable(tileRegion, flags);
        dest.writeParcelable(waveLine, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TileLayer> CREATOR = new Creator<TileLayer>() {
        @Override
        public TileLayer createFromParcel(Parcel in) {
            return new TileLayer(in);
        }

        @Override
        public TileLayer[] newArray(int size) {
            return new TileLayer[size];
        }
    };

    public String toXml() {
        String v = "<TileLayer main=\"" + main + "\" isMainArea=\"" + isMainArea + "\" hasOffset=\"" + hasOffset + "\">";
        if (tileRegion != null) {
            v += "\n" + tileRegion.toXml();
        }
        if (waveLine != null) {
            v += "\n" + waveLine.toXml();
        }
        v += "\n</TileLayer>";
        return v;
    }

    @Override
    public String toString() {
        return main + "," + isMainArea + "," + hasOffset + ",[" + tileRegion + "]";
    }

}
