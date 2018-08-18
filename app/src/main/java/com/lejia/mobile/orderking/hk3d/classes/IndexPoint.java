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
    public boolean isSidePoint; // 是否是端点
    public boolean isBegain; // 是否是起始点

    public IndexPoint(int index, Point point) {
        this.index = index;
        this.point = point;
    }

    public IndexPoint(int index, Point point, boolean isSidePoint, boolean isBegain) {
        this.index = index;
        this.point = point;
        this.isSidePoint = isSidePoint;
        this.isBegain = isBegain;
    }

    protected IndexPoint(Parcel in) {
        index = in.readInt();
        point = in.readParcelable(Point.class.getClassLoader());
        isSidePoint = in.readInt() == 1;
        isBegain = in.readInt() == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(index);
        dest.writeParcelable(point, flags);
        dest.writeInt(isSidePoint ? 1 : 0);
        dest.writeInt(isBegain ? 1 : 0);
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
        return index + "," + point + "," + isSidePoint + "," + isBegain;
    }

}
