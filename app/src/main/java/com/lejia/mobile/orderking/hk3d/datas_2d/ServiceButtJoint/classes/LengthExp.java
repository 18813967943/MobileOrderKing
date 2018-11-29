package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author by HEKE
 *
 * @time 2018/11/20 15:13
 * TODO: 逻辑转长度包裹对象
 */
public class LengthExp implements Parcelable {

    /**
     * 详细标记数据对象
     */
    public SymbolExp symbolExp;

    public LengthExp() {
        super();
    }

    public LengthExp(SymbolExp symbolExp) {
        this.symbolExp = symbolExp;
    }

    protected LengthExp(Parcel in) {
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

    public static final Creator<LengthExp> CREATOR = new Creator<LengthExp>() {
        @Override
        public LengthExp createFromParcel(Parcel in) {
            return new LengthExp(in);
        }

        @Override
        public LengthExp[] newArray(int size) {
            return new LengthExp[size];
        }
    };

    public String toXml() {
        return "<lengthExp>\n" + symbolExp.toXml() + "\n</lengthExp>";
    }

    @Override
    public String toString() {
        return "" + symbolExp;
    }

}
