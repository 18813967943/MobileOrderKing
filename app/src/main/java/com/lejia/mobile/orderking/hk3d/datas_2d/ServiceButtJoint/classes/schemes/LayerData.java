package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author by HEKE
 *
 * @time 2018/11/28 9:45
 * TODO: 铺砖层各种区分标记对象
 */
public class LayerData implements Parcelable {

    public boolean groundLayerVisible;
    public boolean wallLayerVisible;
    public boolean furnitureLayerVisible;
    public boolean ceilingLayerVisible;
    public boolean dimensionLayerVisible;
    public boolean virtualLightLayerVisible;
    public boolean rectLightLayerVisible;

    public LayerData() {
        groundLayerVisible = true;
        wallLayerVisible = true;
        furnitureLayerVisible = true;
        ceilingLayerVisible = true;
        dimensionLayerVisible = true;
        virtualLightLayerVisible = false;
        rectLightLayerVisible = true;
    }

    protected LayerData(Parcel in) {
        groundLayerVisible = in.readByte() != 0;
        wallLayerVisible = in.readByte() != 0;
        furnitureLayerVisible = in.readByte() != 0;
        ceilingLayerVisible = in.readByte() != 0;
        dimensionLayerVisible = in.readByte() != 0;
        virtualLightLayerVisible = in.readByte() != 0;
        rectLightLayerVisible = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (groundLayerVisible ? 1 : 0));
        dest.writeByte((byte) (wallLayerVisible ? 1 : 0));
        dest.writeByte((byte) (furnitureLayerVisible ? 1 : 0));
        dest.writeByte((byte) (ceilingLayerVisible ? 1 : 0));
        dest.writeByte((byte) (dimensionLayerVisible ? 1 : 0));
        dest.writeByte((byte) (virtualLightLayerVisible ? 1 : 0));
        dest.writeByte((byte) (rectLightLayerVisible ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LayerData> CREATOR = new Creator<LayerData>() {
        @Override
        public LayerData createFromParcel(Parcel in) {
            return new LayerData(in);
        }

        @Override
        public LayerData[] newArray(int size) {
            return new LayerData[size];
        }
    };

    public String toXml() {
        return "<LayerData GroundLayerVisible=\"" + groundLayerVisible + "\" WallLayerVisible=\"" + wallLayerVisible + "\" FurnitureLayerVisible=\"" + furnitureLayerVisible + "\"" +
                " CeilingLayerVisible=\"" + ceilingLayerVisible + "\" DimensionLayerVisible=\"" + dimensionLayerVisible + "\" VirtualLightLayerVisible=\"" + virtualLightLayerVisible + "\" RectLightLayerVisible=\"" + rectLightLayerVisible + "\"/>";
    }

    @Override
    public String toString() {
        return groundLayerVisible + "," + wallLayerVisible + "," + furnitureLayerVisible + "," + ceilingLayerVisible + "," + dimensionLayerVisible + ","
                + "," + virtualLightLayerVisible + "," + rectLightLayerVisible;
    }

}
