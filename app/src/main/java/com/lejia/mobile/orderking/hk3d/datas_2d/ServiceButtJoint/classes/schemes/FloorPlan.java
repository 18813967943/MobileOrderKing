package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/11/28 9:37
 * TODO: 户型图，保存方案最大数据对象
 */
public class FloorPlan implements Parcelable {

    public String version; // 版本
    public SceneID sceneID; // 场景对应编号
    public String authorCode; // 用户唯一识别码
    public String previewUrl; // 预览图链接
    public int ceilingHeight; // 层高
    public LayerData layerData; // 显示层标志属性对象
    public int wallNum; // 墙体数量
    public ArrayList<WallData> wallDataArrayList; // 接单王内墙体数据对象列表
    public int floorNum; // 房间地面对象(房间数量)
    public ArrayList<FloorData> floorDataArrayList; // 所有地面数据对象列表
    public int furnitureNum; // 家具数量
    public ArrayList<FurnitureData> furnitureDataArrayList; // 家具数据列表

    public FloorPlan() {
        wallDataArrayList = new ArrayList<>();
        floorDataArrayList = new ArrayList<>();
        furnitureDataArrayList = new ArrayList<>();
    }

    protected FloorPlan(Parcel in) {
        version = in.readString();
        sceneID = in.readParcelable(SceneID.class.getClassLoader());
        authorCode = in.readString();
        previewUrl = in.readString();
        ceilingHeight = in.readInt();
        layerData = in.readParcelable(LayerData.class.getClassLoader());
        wallNum = in.readInt();
        wallDataArrayList = in.createTypedArrayList(WallData.CREATOR);
        floorNum = in.readInt();
        floorDataArrayList = in.createTypedArrayList(FloorData.CREATOR);
        furnitureNum = in.readInt();
        furnitureDataArrayList = in.createTypedArrayList(FurnitureData.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(version);
        dest.writeParcelable(sceneID, flags);
        dest.writeString(authorCode);
        dest.writeString(previewUrl);
        dest.writeInt(ceilingHeight);
        dest.writeParcelable(layerData, flags);
        dest.writeInt(wallNum);
        dest.writeTypedList(wallDataArrayList);
        dest.writeInt(floorNum);
        dest.writeTypedList(floorDataArrayList);
        dest.writeInt(furnitureNum);
        dest.writeTypedList(furnitureDataArrayList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FloorPlan> CREATOR = new Creator<FloorPlan>() {
        @Override
        public FloorPlan createFromParcel(Parcel in) {
            return new FloorPlan(in);
        }

        @Override
        public FloorPlan[] newArray(int size) {
            return new FloorPlan[size];
        }
    };

    public String toXml() {
        String v = "<root>";
        v += "\n<Version code=\"" + ((version == null) ? "LJ003" : version) + "\"/>";
        if (sceneID != null) {
            v += "\n" + sceneID.toXml();
        }
        v += "\n<Author code=\"" + authorCode + "\"/>";
        v += "\n<Preview url=\"" + previewUrl + "\"/>";
        v += "\n<CeilingHeight value=\"" + ceilingHeight + "\"/>";
        if (layerData != null) {
            v += "\n" + layerData.toXml();
        }
        v += "\n<Wall num=\"" + wallNum + "\"/>";
        if (wallNum > 0) {
            if (wallDataArrayList != null && wallDataArrayList.size() > 0) {
                for (WallData wallData : wallDataArrayList) {
                    v += "\n" + wallData.toXml();
                }
            }
        }
        v += "\n<Floor num=\"" + floorNum + "\"/>";
        if (floorDataArrayList != null && floorDataArrayList.size() > 0) {
            for (FloorData floorData : floorDataArrayList) {
                v += "\n" + floorData.toXml();
            }
        }
        v += "\n<Furniture num=\"" + furnitureNum + "\"/>";
        if (furnitureNum > 0) {
            if (furnitureDataArrayList != null && furnitureDataArrayList.size() > 0) {
                for (FurnitureData furnitureData : furnitureDataArrayList) {
                    v += "\n" + furnitureData.toXml();
                }
            }
        }
        // 其他暂不对接
        v += "\n</root>";
        return v;
    }

    @Override
    public String toString() {
        return version + "," + sceneID + "," + authorCode + "," + previewUrl + "," + ceilingHeight + "," + layerData + "," + wallNum + "," + wallDataArrayList + "," + floorNum + ","
                + floorDataArrayList + "," + furnitureNum + "," + furnitureDataArrayList;
    }

}
