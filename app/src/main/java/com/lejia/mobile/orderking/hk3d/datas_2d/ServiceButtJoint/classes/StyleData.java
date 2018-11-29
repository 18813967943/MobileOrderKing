package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author by HEKE
 *
 * @time 2018/11/20 15:48
 * TODO: 逻辑砖三角化对应运算数值包裹对象
 */
public class StyleData implements Parcelable {

    /**
     * 实际运算数值对象
     */
    public Style style;

    public StyleData() {
        super();
    }

    public StyleData(Style style) {
        this.style = style;
    }

    protected StyleData(Parcel in) {
        style = in.readParcelable(Style.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(style, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<StyleData> CREATOR = new Creator<StyleData>() {
        @Override
        public StyleData createFromParcel(Parcel in) {
            return new StyleData(in);
        }

        @Override
        public StyleData[] newArray(int size) {
            return new StyleData[size];
        }
    };

    public String toXml() {
        return "<styleData>\n" + style.toXml() + "\n</styleData>";
    }

    @Override
    public String toString() {
        return "" + style;
    }

}
