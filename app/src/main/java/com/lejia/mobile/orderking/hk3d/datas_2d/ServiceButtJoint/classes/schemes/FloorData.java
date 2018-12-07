package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/11/28 10:03
 * TODO: 房间信息对象
 */
public class FloorData implements Parcelable {

    public String roomName; // 房间名称
    public float centerX; // 中心点x坐标
    public float centerY; // 中心点y坐标
    public float centerZ; // 中心点z坐标
    public float area; // 面积
    public int materialType;
    public String materialCode;
    public String url;
    public String floorID; // 地面唯一编码
    public int roomHeight;
    public boolean buildAngularLines;
    public boolean buildCeilingLines;
    public int angularLineType;
    public String ceilingLinesMatCode;
    public String ceilingLinesMatUrl;
    public String angularLinesMatCode;
    public String angularLinesMatUrl;
    public boolean isSpecialMode;
    public int materialMode;

    public TileViewPanel tileViewPanel; // 房间对应的所有铺砖数据对象
    public ArrayList<RoundPointData> roundPointDataList; // 房间墙中点列表

    public FloorData() {
        roundPointDataList = new ArrayList<>();
    }

    protected FloorData(Parcel in) {
        roomName = in.readString();
        centerX = in.readFloat();
        centerY = in.readFloat();
        centerZ = in.readFloat();
        area = in.readFloat();
        materialType = in.readInt();
        materialCode = in.readString();
        url = in.readString();
        floorID = in.readString();
        roomHeight = in.readInt();
        buildAngularLines = in.readByte() != 0;
        buildCeilingLines = in.readByte() != 0;
        angularLineType = in.readInt();
        ceilingLinesMatCode = in.readString();
        ceilingLinesMatUrl = in.readString();
        angularLinesMatCode = in.readString();
        angularLinesMatUrl = in.readString();
        isSpecialMode = in.readByte() != 0;
        materialMode = in.readInt();
        tileViewPanel = in.readParcelable(TileViewPanel.class.getClassLoader());
        roundPointDataList = in.createTypedArrayList(RoundPointData.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(roomName);
        dest.writeFloat(centerX);
        dest.writeFloat(centerY);
        dest.writeFloat(centerZ);
        dest.writeFloat(area);
        dest.writeInt(materialType);
        dest.writeString(materialCode);
        dest.writeString(url);
        dest.writeString(floorID);
        dest.writeInt(roomHeight);
        dest.writeByte((byte) (buildAngularLines ? 1 : 0));
        dest.writeByte((byte) (buildCeilingLines ? 1 : 0));
        dest.writeInt(angularLineType);
        dest.writeString(ceilingLinesMatCode);
        dest.writeString(ceilingLinesMatUrl);
        dest.writeString(angularLinesMatCode);
        dest.writeString(angularLinesMatUrl);
        dest.writeByte((byte) (isSpecialMode ? 1 : 0));
        dest.writeInt(materialMode);
        dest.writeParcelable(tileViewPanel, flags);
        dest.writeTypedList(roundPointDataList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FloorData> CREATOR = new Creator<FloorData>() {
        @Override
        public FloorData createFromParcel(Parcel in) {
            return new FloorData(in);
        }

        @Override
        public FloorData[] newArray(int size) {
            return new FloorData[size];
        }
    };

    public String toXml() {
        String v = "<FloorData RoomName=\"" + roomName + "\" CenterX=\"" + centerX + "\" CenterY=\"" + centerY + "\" CenterZ=\"" + centerZ + "\" Area=\"" + area + "\" MaterialType=\"" + materialType + "\"" + " MaterialCode=\"" + materialCode + "\" URL=\"" + url + "\" FloorID=\"" + floorID + "\" RoomHeight=\"" + roomHeight + "\" " + "BuildAngularLines=\"" + buildAngularLines + "\" BuildCeilingLines=\"" + buildCeilingLines + "\" AngularLineType=\"" + angularLineType + "\" CeilingLinesMatCode=\"" + ceilingLinesMatCode + "\" " + "CeilingLinesMatUrl=\"" + ceilingLinesMatUrl + "\" AngularLinesMatCode=\"" + angularLinesMatCode + "\" " + "AngularLinesMatUrl=\"" + angularLinesMatUrl + "\" IsSpecialMode=\"" + isSpecialMode + "\" MaterialMode=\"" + materialMode + "\">";
        if (tileViewPanel != null) {
            v += "\n" + tileViewPanel.toXml();
        }
        if (roundPointDataList != null && roundPointDataList.size() > 0) {
            for (RoundPointData roundPointData : roundPointDataList) {
                v += "\n" + roundPointData.toXml();
            }
        }
        v += "\n</FloorData>";
        return v;
    }

    @Override
    public String toString() {
        return roomName + "," + centerX + "," + centerY + "," + centerZ + "," + area + "," + materialType + "," + materialCode + "," + url + "," + floorID + ","
                + roomHeight + "," + buildAngularLines + "," + buildCeilingLines + "," + angularLineType + "," + ceilingLinesMatCode + ","
                + ceilingLinesMatUrl + "," + angularLinesMatCode + "," + angularLinesMatUrl + "," + isSpecialMode + "," + materialMode
                + ",[" + tileViewPanel + "]" + ",[" + roundPointDataList + "]";
    }
}
