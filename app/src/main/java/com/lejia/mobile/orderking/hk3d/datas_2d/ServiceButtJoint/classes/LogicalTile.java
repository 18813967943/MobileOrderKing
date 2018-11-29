package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

/**
 * Author by HEKE
 *
 * @time 2018/11/20 15:01
 * TODO: 逻辑铺砖详细数据对象
 */
public class LogicalTile implements Parcelable {

    public String code; // 编号
    public boolean isMain; // 是否主砖
    public float rotate; // 旋转角度
    public float length; // 长度
    public float width; // 宽度
    public float dirx;
    public float diry;
    public float dirz;
    public int notchStyle; // 倒角类型
    public boolean randRotate; // 砖纹随机

    /**
     * 逻辑砖长度标记对象
     */
    public LengthExp lengthExp;

    /**
     * 逻辑砖宽度标记对象
     */
    public WidthExp widthExp;

    /**
     * 自身砖偏置数据对象
     */
    public AttachDirectionExp attachDirectionExp;

    /**
     * 对应三角化信息数据对象
     */
    public StyleData styleData;

    public LogicalTile() {
        super();
    }

    public LogicalTile(String code, boolean isMain, float rotate, float length, float width, float dirx, float diry, float dirz, int notchStyle) {
        this.code = code;
        this.isMain = isMain;
        this.rotate = rotate;
        this.length = length;
        this.width = width;
        this.dirx = dirx;
        this.diry = diry;
        this.dirz = dirz;
        this.notchStyle = notchStyle;
    }

    protected LogicalTile(Parcel in) {
        code = in.readString();
        isMain = in.readByte() != 0;
        rotate = in.readFloat();
        length = in.readFloat();
        width = in.readFloat();
        dirx = in.readFloat();
        diry = in.readFloat();
        dirz = in.readFloat();
        notchStyle = in.readInt();
        lengthExp = in.readParcelable(LengthExp.class.getClassLoader());
        widthExp = in.readParcelable(WidthExp.class.getClassLoader());
        attachDirectionExp = in.readParcelable(AttachDirectionExp.class.getClassLoader());
        styleData = in.readParcelable(StyleData.class.getClassLoader());
    }

    /**
     * 设置自身偏移数值并运算具体数值
     *
     * @param u
     * @param v
     * @param w
     * @param symbolsMap
     */
    public void setAttachDirectionExp(String u, String v, String w, HashMap<String, Integer> symbolsMap) {
        this.attachDirectionExp = new AttachDirectionExp();
        this.attachDirectionExp.setSymbolVector3DValues(u, v, symbolsMap);
    }

    /**
     * 设置长度描述数据标记
     *
     * @param sym
     */
    public void setLengthExp(String sym) {
        this.lengthExp = new LengthExp();
        this.lengthExp.symbolExp = new SymbolExp(sym);
    }

    /**
     * 设置宽度描述数据标记
     *
     * @param sym
     */
    public void setWidthExp(String sym) {
        this.widthExp = new WidthExp();
        this.widthExp.symbolExp = new SymbolExp(sym);
    }

    /**
     * 设置三角化对应的运算数值
     */
    public void setStyleData(String type, int defType, String yxExp0, String yxExp1, String zxExp0, String zxExp1) {
        this.styleData = new StyleData();
        this.styleData.style = new Style(type, defType, yxExp0, yxExp1, zxExp0, zxExp1);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeByte((byte) (isMain ? 1 : 0));
        dest.writeFloat(rotate);
        dest.writeFloat(length);
        dest.writeFloat(width);
        dest.writeFloat(dirx);
        dest.writeFloat(diry);
        dest.writeFloat(dirz);
        dest.writeInt(notchStyle);
        dest.writeParcelable(lengthExp, flags);
        dest.writeParcelable(widthExp, flags);
        dest.writeParcelable(attachDirectionExp, flags);
        dest.writeParcelable(styleData, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LogicalTile> CREATOR = new Creator<LogicalTile>() {
        @Override
        public LogicalTile createFromParcel(Parcel in) {
            return new LogicalTile(in);
        }

        @Override
        public LogicalTile[] newArray(int size) {
            return new LogicalTile[size];
        }
    };

    public String toXml() {
        boolean single = (lengthExp == null && widthExp == null && attachDirectionExp == null && styleData == null);
        if (single) {
            return "<LogicalTile code=\"" + code + "\" isMain=\"" + isMain + "\" " +
                    "rotate=\"" + rotate + "\" length=\"" + length + "\" width=\"" + width + "\" dirx=\"" + dirx + "\" diry=\"" + diry + "\"" +
                    "dirz=\"" + dirz + "\" notchStyle=\"" + notchStyle + "\"/>";
        } else {
            String v = "<LogicalTile code=\"" + code + "\" isMain=\"" + isMain + "\" " +
                    "rotate=\"" + rotate + "\" length=\"" + length + "\" width=\"" + width + "\" dirx=\"" + dirx + "\" diry=\"" + diry + "\"" +
                    "dirz=\"" + dirz + "\" notchStyle=\"" + notchStyle + "\">";
            if (lengthExp != null) {
                v += "\n" + lengthExp.toXml();
            }
            if (widthExp != null) {
                v += "\n" + widthExp.toXml();
            }
            if (attachDirectionExp != null) {
                v += "\n" + attachDirectionExp.toXml();
            }
            if (styleData != null) {
                v += "\n" + styleData.toXml();
            }
            v += "\n</LogicalTile>";
            return v;
        }
    }

    @Override
    public String toString() {
        return code + "," + isMain + "," + rotate + "," + length + "," + width + "," + dirx + "," + diry + "," + dirz + "," + notchStyle + ","
                + "[" + lengthExp + "]," + "[" + widthExp + "]," + "[" + attachDirectionExp + "]," + "[" + styleData + "]";
    }

}
