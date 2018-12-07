package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/11/20 14:59
 * TODO: 逻辑瓷砖包裹数据对象
 */
public class Logtile implements Parcelable {

    /**
     * 所有逻辑砖数据列表
     */
    public ArrayList<LogicalTile> logicalTileArrayList = new ArrayList<>();

    public Logtile() {
        super();
    }

    public Logtile(ArrayList<LogicalTile> logicalTileArrayList) {
        this.logicalTileArrayList = logicalTileArrayList;
    }

    protected Logtile(Parcel in) {
        logicalTileArrayList = in.createTypedArrayList(LogicalTile.CREATOR);
    }

    /**
     * 增加逻辑砖数据
     *
     * @param logicalTile
     */
    public void add(LogicalTile logicalTile) {
        if (logicalTileArrayList.contains(logicalTile))
            return;
        logicalTileArrayList.add(logicalTile);
    }

    /**
     * 根据物理砖列表对应位置获取对应的逻辑砖对象
     *
     * @param position
     * @return
     */
    public LogicalTile getLogicalTile(int position) {
        if (logicalTileArrayList.size() == 0)
            return null;
        if (position < 0 || position >= logicalTileArrayList.size())
            return null;
        return logicalTileArrayList.get(position);
    }

    /**
     * 数据大小
     */
    public int size() {
        return logicalTileArrayList.size();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(logicalTileArrayList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Logtile> CREATOR = new Creator<Logtile>() {
        @Override
        public Logtile createFromParcel(Parcel in) {
            return new Logtile(in);
        }

        @Override
        public Logtile[] newArray(int size) {
            return new Logtile[size];
        }
    };

    public String toXml() {
        String v = "<logTile>";
        if (logicalTileArrayList != null && logicalTileArrayList.size() > 0) {
            for (LogicalTile logicalTile : logicalTileArrayList) {
                v += ("\n" + logicalTile.toXml());
            }
        }
        v += "\n</logTile>";
        return v;
    }

    @Override
    public String toString() {
        return "Logtile: " + logicalTileArrayList;
    }

}
