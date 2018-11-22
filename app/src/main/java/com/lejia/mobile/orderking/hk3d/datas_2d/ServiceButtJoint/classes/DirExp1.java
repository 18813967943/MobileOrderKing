package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author by HEKE
 *
 * @time 2018/11/20 16:17
 * TODO:起铺方向一的偏移数值包裹对象
 */
public class DirExp1 implements Parcelable {

    /**
     * 实际对应偏移数值
     */
    public SymbolVector3D symbolVector3D;

    public DirExp1() {
        super();
    }

    public DirExp1(SymbolVector3D symbolVector3D) {
        this.symbolVector3D = symbolVector3D;
    }

    protected DirExp1(Parcel in) {
        symbolVector3D = in.readParcelable(SymbolVector3D.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(symbolVector3D, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DirExp1> CREATOR = new Creator<DirExp1>() {
        @Override
        public DirExp1 createFromParcel(Parcel in) {
            return new DirExp1(in);
        }

        @Override
        public DirExp1[] newArray(int size) {
            return new DirExp1[size];
        }
    };

    @Override
    public String toString() {
        return "DirExp1: " + symbolVector3D;
    }
}
