package com.lejia.mobile.orderking.hk3d.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author by HEKE
 *
 * @time 2018/7/16 17:13
 * TODO: 射线
 */
public class Ray implements Parcelable {

    public LJ3DPoint pos;
    public LJ3DPoint dir;

    public Ray() {
        super();
    }

    public Ray(LJ3DPoint pos, LJ3DPoint dir) {
        this.pos = pos;
        this.dir = dir;
    }

    protected Ray(Parcel in) {
        pos = in.readParcelable(LJ3DPoint.class.getClassLoader());
        dir = in.readParcelable(LJ3DPoint.class.getClassLoader());
    }

    public LJ3DPoint getPos() {
        return pos;
    }

    public void setPos(LJ3DPoint pos) {
        this.pos = pos;
    }

    public LJ3DPoint getDir() {
        return dir;
    }

    public void setDir(LJ3DPoint dir) {
        this.dir = dir;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(pos, flags);
        dest.writeParcelable(dir, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Ray> CREATOR = new Creator<Ray>() {
        @Override
        public Ray createFromParcel(Parcel in) {
            return new Ray(in);
        }

        @Override
        public Ray[] newArray(int size) {
            return new Ray[size];
        }
    };

}
