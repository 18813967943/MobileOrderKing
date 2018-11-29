package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

/**
 * Author by HEKE
 *
 * @time 2018/11/20 15:20
 * TODO: 逻辑砖对应的偏置函数包裹对象
 */
public class AttachDirectionExp implements Parcelable {

    /**
     * 实际偏置数据对象
     */
    public SymbolVector3D symbolVector3D;

    public AttachDirectionExp() {
        super();
        symbolVector3D = new SymbolVector3D();
    }

    public AttachDirectionExp(SymbolVector3D symbolVector3D) {
        this.symbolVector3D = symbolVector3D;
    }

    /**
     * 设置偏置数据运算
     *
     * @param ustring
     * @param vstring
     * @param symbolsMap
     */
    public void setSymbolVector3DValues(String ustring, String vstring, HashMap<String, Integer> symbolsMap) {
        if (symbolVector3D != null)
            symbolVector3D.calculateValues(ustring, vstring, symbolsMap);
    }

    protected AttachDirectionExp(Parcel in) {
        symbolVector3D = in.readParcelable(SymbolVector3D.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(symbolVector3D, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AttachDirectionExp> CREATOR = new Creator<AttachDirectionExp>() {
        @Override
        public AttachDirectionExp createFromParcel(Parcel in) {
            return new AttachDirectionExp(in);
        }

        @Override
        public AttachDirectionExp[] newArray(int size) {
            return new AttachDirectionExp[size];
        }
    };

    public String toXml() {
        return "<attachDirectionExp>\n" + symbolVector3D.toXml() + "\n</attachDirectionExp>";
    }

    @Override
    public String toString() {
        return "" + symbolVector3D;
    }

}
