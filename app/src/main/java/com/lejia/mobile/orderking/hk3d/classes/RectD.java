package com.lejia.mobile.orderking.hk3d.classes;

import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author by HEKE
 *
 * @time 2018/6/22 19:48
 * TODO: 双精度浮点型外包矩形对象
 */
public class RectD implements Parcelable {

    public double left;
    public double top;
    public double right;
    public double bottom;

    public RectD() {
        super();
    }

    public RectD(Rect rect) {
        this.left = rect.left;
        this.top = rect.top;
        this.right = rect.right;
        this.bottom = rect.bottom;
    }

    public RectD(RectF rectF) {
        this.left = rectF.left;
        this.top = rectF.top;
        this.right = rectF.right;
        this.bottom = rectF.bottom;
    }

    public RectD(RectD rectD) {
        this.left = rectD.left;
        this.top = rectD.top;
        this.right = rectD.right;
        this.bottom = rectD.bottom;
    }

    public RectD(double left, double top, double right, double bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    protected RectD(Parcel in) {
        left = in.readDouble();
        top = in.readDouble();
        right = in.readDouble();
        bottom = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(left);
        dest.writeDouble(top);
        dest.writeDouble(right);
        dest.writeDouble(bottom);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RectD> CREATOR = new Creator<RectD>() {
        @Override
        public RectD createFromParcel(Parcel in) {
            return new RectD(in);
        }

        @Override
        public RectD[] newArray(int size) {
            return new RectD[size];
        }
    };

    public double width() {
        return Math.abs(right - left);
    }

    public double height() {
        return Math.abs(bottom - top);
    }

    public double centerX() {
        return (left + right) / 2;
    }

    public double centerY() {
        return (top + bottom) / 2;
    }

    public RectF toRectF() {
        return new RectF((float) left, (float) top, (float) right, (float) bottom);
    }

    public Rect toRect() {
        return new Rect((int) left, (int) top, (int) right, (int) bottom);
    }

    @Override
    public String toString() {
        return left + "," + top + "," + right + "," + bottom;
    }

}
