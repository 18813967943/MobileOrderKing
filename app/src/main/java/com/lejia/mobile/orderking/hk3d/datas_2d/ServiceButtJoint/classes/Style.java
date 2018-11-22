package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author by HEKE
 *
 * @time 2018/11/20 15:53
 * TODO: 逻辑砖三角化对应的实际数值运算对象
 */
public class Style implements Parcelable {

    public String type; // 三角化类型
    public int defType; // 定义类型
    public String yxExp0;
    public String yxExp1;
    public String zxExp0;
    public String zxExp1;

    public Style() {
        super();
    }

    public Style(String type, int defType, String yxExp0, String yxExp1, String zxExp0, String zxExp1) {
        this.type = type;
        this.defType = defType;
        this.yxExp0 = yxExp0;
        this.yxExp1 = yxExp1;
        this.zxExp0 = zxExp0;
        this.zxExp1 = zxExp1;
    }

    protected Style(Parcel in) {
        type = in.readString();
        defType = in.readInt();
        yxExp0 = in.readString();
        yxExp1 = in.readString();
        zxExp0 = in.readString();
        zxExp1 = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeInt(defType);
        dest.writeString(yxExp0);
        dest.writeString(yxExp1);
        dest.writeString(zxExp0);
        dest.writeString(zxExp1);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Style> CREATOR = new Creator<Style>() {
        @Override
        public Style createFromParcel(Parcel in) {
            return new Style(in);
        }

        @Override
        public Style[] newArray(int size) {
            return new Style[size];
        }
    };

    @Override
    public String toString() {
        return type + "," + defType + "," + yxExp0 + "," + yxExp1 + "," + zxExp0 + "," + zxExp1;
    }
}
