package com.lejia.mobile.orderking.hk3d.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author by HEKE
 *
 * @time 2018/6/23 11:03
 * TODO: 带索引的点对象
 */
public class IndexPoint implements Parcelable {

    public int index; // 下标位置
    public Point point; // 点

    public IndexPoint(int index, Point point) {
        this.index = index;
        this.point = point;
    }

    protected IndexPoint(Parcel in) {
        index = in.readInt();
        point = in.readParcelable(Point.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(index);
        dest.writeParcelable(point, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<IndexPoint> CREATOR = new Creator<IndexPoint>() {
        @Override
        public IndexPoint createFromParcel(Parcel in) {
            return new IndexPoint(in);
        }

        @Override
        public IndexPoint[] newArray(int size) {
            return new IndexPoint[size];
        }
    };

    @Override
    public String toString() {
        return index + "," + point;
    }

}
