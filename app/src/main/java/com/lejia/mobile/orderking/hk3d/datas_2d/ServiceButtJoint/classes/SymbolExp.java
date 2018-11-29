package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author by HEKE
 *
 * @time 2018/11/20 15:13
 * TODO: 逻辑砖长宽标记符号数据对象
 */
public class SymbolExp implements Parcelable {

    public String sym; // 标记

    public SymbolExp() {
        super();
    }

    public SymbolExp(String sym) {
        this.sym = sym;
    }

    protected SymbolExp(Parcel in) {
        sym = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sym);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SymbolExp> CREATOR = new Creator<SymbolExp>() {
        @Override
        public SymbolExp createFromParcel(Parcel in) {
            return new SymbolExp(in);
        }

        @Override
        public SymbolExp[] newArray(int size) {
            return new SymbolExp[size];
        }
    };

    public String toXml() {
        return "<SymbolExp sym=\"" + sym + "\"/>";
    }

    @Override
    public String toString() {
        return "" + sym;
    }
}
