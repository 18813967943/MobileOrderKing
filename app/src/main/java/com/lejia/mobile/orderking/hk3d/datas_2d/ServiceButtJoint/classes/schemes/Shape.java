package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes;

import android.os.Parcel;
import android.os.Parcelable;

import com.lejia.mobile.orderking.hk3d.classes.Point;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/11/28 10:42
 * TODO: 区域形状围点对象
 */
public class Shape implements Parcelable {

    public ArrayList<Point> pointsList;

    public Shape() {
        pointsList = new ArrayList<>();
    }

    protected Shape(Parcel in) {
        pointsList = in.createTypedArrayList(Point.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(pointsList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Shape> CREATOR = new Creator<Shape>() {
        @Override
        public Shape createFromParcel(Parcel in) {
            return new Shape(in);
        }

        @Override
        public Shape[] newArray(int size) {
            return new Shape[size];
        }
    };

    public void add(Point point) {
        if (point == null)
            return;
        pointsList.add(point);
    }

    public String toXml() {
        String v = "<shape>";
        if (pointsList != null && pointsList.size() > 0) {
            for (Point point : pointsList) {
                v += "\n<point x=\"" + (int) (point.x * -10 + 30000) + "\" y=\"" + (int) (point.y * 10 + 30000) + "\"/>"; // x/y加30000，接单王坐标系原点(3000,3000)并放大10倍进行运算
            }
        }
        v += "\n</shape>";
        return v;
    }

    @Override
    public String toString() {
        return "" + pointsList;
    }
}
