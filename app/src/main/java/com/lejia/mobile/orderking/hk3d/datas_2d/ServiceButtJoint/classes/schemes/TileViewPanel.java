package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author by HEKE
 *
 * @time 2018/11/28 10:14
 * TODO: 铺砖控件面板对象
 */
public class TileViewPanel implements Parcelable {

    public float scale;
    public int origx;
    public int origy;
    public boolean canMoveTileLayer;
    public boolean saveTiles;

    public TileLayers tileLayers; // 铺砖细化层数据对象
    public RoomLayer roomLayer; // 房间围点细化数据对象

    public TileViewPanel() {
        scale = 10;
        canMoveTileLayer = true;
        saveTiles = false;
    }

    protected TileViewPanel(Parcel in) {
        scale = in.readFloat();
        origx = in.readInt();
        origy = in.readInt();
        canMoveTileLayer = in.readByte() != 0;
        saveTiles = in.readByte() != 0;
        tileLayers = in.readParcelable(TileLayers.class.getClassLoader());
        roomLayer = in.readParcelable(RoomLayer.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(scale);
        dest.writeInt(origx);
        dest.writeInt(origy);
        dest.writeByte((byte) (canMoveTileLayer ? 1 : 0));
        dest.writeByte((byte) (saveTiles ? 1 : 0));
        dest.writeParcelable(tileLayers, flags);
        dest.writeParcelable(roomLayer, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TileViewPanel> CREATOR = new Creator<TileViewPanel>() {
        @Override
        public TileViewPanel createFromParcel(Parcel in) {
            return new TileViewPanel(in);
        }

        @Override
        public TileViewPanel[] newArray(int size) {
            return new TileViewPanel[size];
        }
    };

    public String toXml() {
        String v = "<TileViewPanel scale=\"" + scale + "\" origx=\"" + origx + "\" origy=\"" + origy + "\" canMoveTileLayer=\"" + canMoveTileLayer + "\" saveTiles=\"" + saveTiles + "\">";
        if (tileLayers != null) {
            v += "\n" + tileLayers.toXml();
        }
        if (roomLayer != null) {
            v += "\n" + roomLayer.toXml();
        }
        v += "\n</TileViewPanel>";
        return v;
    }

    @Override
    public String toString() {
        return scale + "," + origx + "," + origy + "," + canMoveTileLayer + "," + saveTiles + ",[" + tileLayers + "]" + ",[" + roomLayer + "]";
    }
}
