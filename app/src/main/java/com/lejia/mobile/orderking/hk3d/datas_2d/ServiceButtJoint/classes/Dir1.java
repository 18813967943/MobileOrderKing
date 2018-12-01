package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author by HEKE
 *
 * @time 2018/11/30 19:35
 * TODO: 铺砖方向1
 */
public class Dir1 implements Parcelable {

    public float x;
    public float y;
    public float z;

    public Dir1() {
        super();
    }

    protected Dir1(Parcel in) {
        x = in.readFloat();
        y = in.readFloat();
        z = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(x);
        dest.writeFloat(y);
        dest.writeFloat(z);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Dir1> CREATOR = new Creator<Dir1>() {
        @Override
        public Dir1 createFromParcel(Parcel in) {
            return new Dir1(in);
        }

        @Override
        public Dir1[] newArray(int size) {
            return new Dir1[size];
        }
    };

    public String toXml() {
        return "<dir1 x=\"" + x + "\" y=\"" + y + "\" z=\"" + z + "\"/>";
    }

    @Override
    public String toString() {
        return x + "," + y + "," + z;
    }
}
