package com.lejia.mobile.orderking.hk3d.classes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/7/2 15:55
 * TODO: 辅助线段(墙体内外线段)，主要用于处理线段相交的交点带厚度的内外点处理
 */
public class AuxiliaryLine implements Parcelable {

    public Point down; // 起始点
    public Point up; // 结束点

    public AuxiliaryLine(Point down, Point up) {
        this.down = down;
        this.up = up;
    }

    protected AuxiliaryLine(Parcel in) {
        down = in.readParcelable(Point.class.getClassLoader());
        up = in.readParcelable(Point.class.getClassLoader());
    }

    /**
     * 判断是否有效
     */
    public boolean invalid() {
        return down == null || up == null || down.equals(up);
    }

    /**
     * 转化为线段
     */
    public Line toLine() {
        if (invalid())
            return null;
        return new Line(down.copy(), up.copy());
    }

    /**
     * 获取边线的交点
     *
     * @param other
     */
    public Point getAuxiliaryIntersectePoint(AuxiliaryLine other) {
        if (other == null || other.invalid() || invalid())
            return null;
        Point ret = null;
        try {
            Line nowLine0 = toLine();
            Line nextLine0 = other.toLine();
            ret = nowLine0.getLineIntersectedPoint(nextLine0);
            if (ret == null) {
                ArrayList<Point> nowLepsList = PointList.getRotateLEPS(nowLine0.getAngle(), 5 * nowLine0.getLength(), nowLine0.getCenter());
                ArrayList<Point> nextLepsList = PointList.getRotateLEPS(nextLine0.getAngle(), 5 * nextLine0.getLength(), nextLine0.getCenter());
                nowLine0 = new Line(nowLepsList.get(1), nowLepsList.get(0));
                nextLine0 = new Line(nextLepsList.get(1), nextLepsList.get(0));
                ret = nowLine0.getLineIntersectedPoint(nextLine0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public String toString() {
        return down + "," + up;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(down, flags);
        dest.writeParcelable(up, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AuxiliaryLine> CREATOR = new Creator<AuxiliaryLine>() {
        @Override
        public AuxiliaryLine createFromParcel(Parcel in) {
            return new AuxiliaryLine(in);
        }

        @Override
        public AuxiliaryLine[] newArray(int size) {
            return new AuxiliaryLine[size];
        }
    };

}
