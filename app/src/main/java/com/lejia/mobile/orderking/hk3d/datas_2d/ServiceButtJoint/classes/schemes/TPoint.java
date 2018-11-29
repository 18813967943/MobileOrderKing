package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author by HEKE
 *
 * @time 2018/11/28 11:46
 * TODO: 接单王房间形状点对象
 */
public class TPoint implements Parcelable {

    public int type;
    public float xpt1;
    public float ypt1;
    public float xpt2;
    public float ypt2;
    public float xpt3;
    public float ypt3;

    public TPoint() {
    }

    protected TPoint(Parcel in) {
        type = in.readInt();
        xpt1 = in.readFloat();
        ypt1 = in.readFloat();
        xpt2 = in.readFloat();
        ypt2 = in.readFloat();
        xpt3 = in.readFloat();
        ypt3 = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeFloat(xpt1);
        dest.writeFloat(ypt1);
        dest.writeFloat(xpt2);
        dest.writeFloat(ypt2);
        dest.writeFloat(xpt3);
        dest.writeFloat(ypt3);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TPoint> CREATOR = new Creator<TPoint>() {
        @Override
        public TPoint createFromParcel(Parcel in) {
            return new TPoint(in);
        }

        @Override
        public TPoint[] newArray(int size) {
            return new TPoint[size];
        }
    };

    public String toXml() {
        return "<TPoint type=\"" + type + "\" xpt1=\"" + xpt1 + "\" ypt1=\"" + ypt1 + "\" xpt2=\"" + xpt2 + "\" ypt2=\"" + ypt2 + "\" xpt3=\"" + xpt3 + "\" ypt3=\"" + ypt3 + "\"/>";
    }

    @Override
    public String toString() {
        return type + "," + xpt1 + "," + ypt1 + "," + xpt2 + "," + ypt2 + "," + xpt3 + "," + ypt3;
    }
}
