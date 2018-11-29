package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author by HEKE
 *
 * @time 2018/11/28 10:34
 * TODO: 铺砖区域数据对象
 */
public class TileRegion implements Parcelable{

    public boolean isAddArea;
    public boolean isWallTile;
    public boolean isEmportTile;
    public long area;
    public int opType;
    public Shape shape; // 区域形状围点
    public Plan plan; // 中心转铺砖对象
    // TS >> TilingSolution 用于混铺数据，暂不考虑

    public TileRegion() {
    }

    protected TileRegion(Parcel in) {
        isAddArea = in.readByte() != 0;
        isWallTile = in.readByte() != 0;
        isEmportTile = in.readByte() != 0;
        area = in.readLong();
        opType = in.readInt();
        shape = in.readParcelable(Shape.class.getClassLoader());
        plan = in.readParcelable(Plan.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isAddArea ? 1 : 0));
        dest.writeByte((byte) (isWallTile ? 1 : 0));
        dest.writeByte((byte) (isEmportTile ? 1 : 0));
        dest.writeLong(area);
        dest.writeInt(opType);
        dest.writeParcelable(shape, flags);
        dest.writeParcelable(plan, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TileRegion> CREATOR = new Creator<TileRegion>() {
        @Override
        public TileRegion createFromParcel(Parcel in) {
            return new TileRegion(in);
        }

        @Override
        public TileRegion[] newArray(int size) {
            return new TileRegion[size];
        }
    };

    public String toXml() {
        String v = "<TileRegion isAddArea=\"" + isAddArea + "\" isWallTile=\"" + isWallTile + "\" isEmportTile=\"" + isEmportTile + "\" area=\"" + area + "\" opType=\"" + opType + "\">";
        if (shape != null) {
            v += "\n" + shape.toXml();
        }
        if (plan != null) {
            v += "\n" + plan.toXml();
        }
        v += "\n</TileRegion>";
        return v;
    }

    @Override
    public String toString() {
        return isAddArea + "," + isWallTile + "," + isEmportTile + "," + area + "," + opType + ",[" + shape + "]," + "[" + plan + "]";
    }

}
