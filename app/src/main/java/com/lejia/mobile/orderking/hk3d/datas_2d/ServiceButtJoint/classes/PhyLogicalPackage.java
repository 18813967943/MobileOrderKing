package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.lejia.mobile.orderking.classes.XInfo;

/**
 * Author by HEKE
 *
 * @time 2018/11/21 14:41
 * TODO: 物理砖数据与逻辑砖数据捆绑对象
 */
public class PhyLogicalPackage implements Parcelable {

    /**
     * 物理砖数据对象
     */
    public Tile tile;

    /**
     * 逻辑砖数据对象
     */
    public LogicalTile logicalTile;

    /**
     * 资源数据
     */
    public XInfo xInfo;

    public PhyLogicalPackage(Tile tile, LogicalTile logicalTile) {
        this.tile = tile;
        this.logicalTile = logicalTile;
    }

    protected PhyLogicalPackage(Parcel in) {
        tile = in.readParcelable(Tile.class.getClassLoader());
        logicalTile = in.readParcelable(LogicalTile.class.getClassLoader());
        xInfo = in.readParcelable(XInfo.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(tile, flags);
        dest.writeParcelable(logicalTile, flags);
        dest.writeParcelable(xInfo, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PhyLogicalPackage> CREATOR = new Creator<PhyLogicalPackage>() {
        @Override
        public PhyLogicalPackage createFromParcel(Parcel in) {
            return new PhyLogicalPackage(in);
        }

        @Override
        public PhyLogicalPackage[] newArray(int size) {
            return new PhyLogicalPackage[size];
        }
    };

    /**
     * 数据释放
     */
    public void release() {
        if (xInfo != null) {
            xInfo.previewBuffer = null;
            xInfo.topViewBuffer = null;
            xInfo = null;
        }
    }

    @Override
    public String toString() {
        return tile + "|" + logicalTile;
    }

}
