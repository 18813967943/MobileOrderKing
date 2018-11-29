package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.lejia.mobile.orderking.utils.TextUtils;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/11/20 14:47
 * TODO: 物理瓷砖信息包裹对象
 */
public class Phy implements Parcelable {

    public ArrayList<Tile> tileArrayList = new ArrayList<>();

    public Phy() {
        super();
    }

    public Phy(ArrayList<Tile> tileArrayList) {
        this.tileArrayList = tileArrayList;
    }

    protected Phy(Parcel in) {
        tileArrayList = in.createTypedArrayList(Tile.CREATOR);
    }

    /**
     * 增加物理瓷砖详细描述对象数据
     *
     * @param tile
     */
    public void add(Tile tile) {
        if (tileArrayList.contains(tile))
            return;
        tileArrayList.add(tile);
    }

    /**
     * 根据瓷砖编号获取对应的物理瓷砖详细信息描述对象
     *
     * @param code
     */
    public Tile getTile(String code) {
        if (tileArrayList.size() == 0 || TextUtils.isTextEmpty(code))
            return null;
        Tile tile = null;
        for (Tile t : tileArrayList) {
            if (code.equals(t.code)) {
                tile = t;
                break;
            }
        }
        return tile;
    }

    /**
     * 根据瓷砖位置获取对应的物理瓷砖详细信息描述对象
     *
     * @param position
     */
    public Tile getTile(int position) {
        if (size() == 0 || position < 0 || position >= size())
            return null;
        return tileArrayList.get(position);
    }

    /**
     * 获取数据大小
     */
    public int size() {
        return tileArrayList.size();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(tileArrayList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Phy> CREATOR = new Creator<Phy>() {
        @Override
        public Phy createFromParcel(Parcel in) {
            return new Phy(in);
        }

        @Override
        public Phy[] newArray(int size) {
            return new Phy[size];
        }
    };

    public String toXml() {
        String value = "<phy>";
        if (tileArrayList != null && tileArrayList.size() > 0) {
            for (Tile tile : tileArrayList) {
                value += "\n" + tile.toXml();
            }
        }
        value += "\n</phy>";
        return value;
    }

    @Override
    public String toString() {
        return "Phy{" + tileArrayList + "}";
    }

}
