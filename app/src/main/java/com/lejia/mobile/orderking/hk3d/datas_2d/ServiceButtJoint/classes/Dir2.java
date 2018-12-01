package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author by HEKE
 *
 * @time 2018/11/30 19:35
 * TODO: 铺砖方向1
 */
public class Dir2 implements Parcelable {

    public float x;
    public float y;
    public float z;

    public Dir2() {
        super();
    }

    protected Dir2(Parcel in) {
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

    public static final Creator<Dir2> CREATOR = new Creator<Dir2>() {
        @Override
        public Dir2 createFromParcel(Parcel in) {
            return new Dir2(in);
        }

        @Override
        public Dir2[] newArray(int size) {
            return new Dir2[size];
        }
    };

    public String toXml() {
        return "<dir2 x=\"" + x + "\" y=\"" + y + "\" z=\"" + z + "\"/>";
    }

    @Override
    public String toString() {
        return x + "," + y + "," + z;
    }
}
