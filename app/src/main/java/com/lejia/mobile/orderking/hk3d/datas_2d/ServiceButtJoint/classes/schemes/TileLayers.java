package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/11/28 10:25
 * TODO: 房间内的所有铺砖层数据对象
 */
public class TileLayers implements Parcelable {

    public ArrayList<TileLayer> tileLayersList;

    public TileLayers() {
        tileLayersList = new ArrayList<>();
    }

    protected TileLayers(Parcel in) {
        tileLayersList = in.createTypedArrayList(TileLayer.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(tileLayersList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TileLayers> CREATOR = new Creator<TileLayers>() {
        @Override
        public TileLayers createFromParcel(Parcel in) {
            return new TileLayers(in);
        }

        @Override
        public TileLayers[] newArray(int size) {
            return new TileLayers[size];
        }
    };

    public void add(TileLayer tileLayer) {
        if (tileLayer == null)
            return;
        tileLayersList.add(tileLayer);
    }

    public String toXml() {
        String v = "<TileLayers>";
        if (tileLayersList != null && tileLayersList.size() > 0) {
            for (TileLayer tileLayer : tileLayersList) {
                v += "\n" + tileLayer.toXml();
            }
        }
        v += "\n</TileLayers>";
        return v;
    }

    @Override
    public String toString() {
        return "TileLayers : " + tileLayersList;
    }

}
