package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author by HEKE
 *
 * @time 2018/11/28 9:47
 * TODO: 墙体数据对象(接单王房间内墙体)
 */
public class WallData implements Parcelable {

    public String id;
    public float startX;
    public float startY;
    public float startZ;
    public float endX;
    public float endY;
    public float endZ;
    public float thickness;
    public float offSide;

    public WallData() {
    }

    protected WallData(Parcel in) {
        id = in.readString();
        startX = in.readFloat();
        startY = in.readFloat();
        startZ = in.readFloat();
        endX = in.readFloat();
        endY = in.readFloat();
        endZ = in.readFloat();
        thickness = in.readFloat();
        offSide = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeFloat(startX);
        dest.writeFloat(startY);
        dest.writeFloat(startZ);
        dest.writeFloat(endX);
        dest.writeFloat(endY);
        dest.writeFloat(endZ);
        dest.writeFloat(thickness);
        dest.writeFloat(offSide);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WallData> CREATOR = new Creator<WallData>() {
        @Override
        public WallData createFromParcel(Parcel in) {
            return new WallData(in);
        }

        @Override
        public WallData[] newArray(int size) {
            return new WallData[size];
        }
    };

    public String toXml() {
        return "<WallData ID=\"" + id + "\" StartX=\"" + startX + "\" StartY=\"" + startY + "\" StartZ=\"" + startZ + "\" " +
                "EndX=\"" + endX + "\" EndY=\"" + endY + "\" EndZ=\"" + endZ + "\" Thickness=\"" + thickness + "\" OffSide=\"" + offSide + "\"/>";
    }

    @Override
    public String toString() {
        return id + "," + startX + "," + startY + "," + startZ + "," + endX + "," + endY + "," + endZ + "," + thickness + "," + offSide;
    }
}
