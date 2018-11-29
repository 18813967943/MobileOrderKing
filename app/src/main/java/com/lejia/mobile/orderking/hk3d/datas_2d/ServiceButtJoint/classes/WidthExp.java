package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author by HEKE
 *
 * @time 2018/11/20 15:18
 * TODO: 逻辑砖宽度包裹标记对象
 */
public class WidthExp implements Parcelable {

    /**
     * 实际宽度标记对象
     */
    public SymbolExp symbolExp;

    public WidthExp() {
        super();
    }

    public WidthExp(SymbolExp symbolExp) {
        this.symbolExp = symbolExp;
    }

    protected WidthExp(Parcel in) {
        symbolExp = in.readParcelable(SymbolExp.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(symbolExp, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WidthExp> CREATOR = new Creator<WidthExp>() {
        @Override
        public WidthExp createFromParcel(Parcel in) {
            return new WidthExp(in);
        }

        @Override
        public WidthExp[] newArray(int size) {
            return new WidthExp[size];
        }
    };

    public String toXml() {
        return "<widthExp>\n" + symbolExp.toXml() + "\n</widthExp>";
    }

    @Override
    public String toString() {
        return "" + symbolExp;
    }

}
