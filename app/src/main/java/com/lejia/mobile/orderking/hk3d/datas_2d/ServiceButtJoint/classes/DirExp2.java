package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author by HEKE
 *
 * @time 2018/11/20 16:17
 * TODO:起铺方向一的偏移数值包裹对象
 */
public class DirExp2 implements Parcelable {

    /**
     * 实际对应偏移数值
     */
    public SymbolVector3D symbolVector3D;

    public DirExp2() {
        super();
    }

    public DirExp2(SymbolVector3D symbolVector3D) {
        this.symbolVector3D = symbolVector3D;
    }

    protected DirExp2(Parcel in) {
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

    public static final Creator<DirExp2> CREATOR = new Creator<DirExp2>() {
        @Override
        public DirExp2 createFromParcel(Parcel in) {
            return new DirExp2(in);
        }

        @Override
        public DirExp2[] newArray(int size) {
            return new DirExp2[size];
        }
    };

    @Override
    public String toString() {
        return "DirExp2: " + symbolVector3D;
    }
}
