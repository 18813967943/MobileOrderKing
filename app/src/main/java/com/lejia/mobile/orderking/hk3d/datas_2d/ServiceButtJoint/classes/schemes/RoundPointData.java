package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes;

import android.os.Parcel;
import android.os.Parcelable;

import com.lejia.mobile.orderking.hk3d.classes.Point;

/**
 * Author by HEKE
 *
 * @time 2018/11/28 11:56
 * TODO: 接单王墙中点对象
 */
public class RoundPointData implements Parcelable {

    public int x;
    public int y;

    public RoundPointData() {
    }

    public RoundPointData(int x, int y) {
        this.x = x;
        this.y = y;
    }

    protected RoundPointData(Parcel in) {
        x = in.readInt();
        y = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(x);
        dest.writeInt(y);
    }

    public Point toPoint() {
        return new Point(x * 0.1d, y * 0.1d);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RoundPointData> CREATOR = new Creator<RoundPointData>() {
        @Override
        public RoundPointData createFromParcel(Parcel in) {
            return new RoundPointData(in);
        }

        @Override
        public RoundPointData[] newArray(int size) {
            return new RoundPointData[size];
        }
    };

    public String toXml() {
        return "<RoundPointData X=\"" + x + "\" Y=\"" + y + "\"/>";
    }

    @Override
    public String toString() {
        return x + "," + y;
    }

}
