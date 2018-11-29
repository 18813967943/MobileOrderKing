package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.classes.schemes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author by HEKE
 *
 * @time 2018/11/28 14:34
 * TODO: 家具数据对象
 */
public class FurnitureData implements Parcelable {

    public String name;
    public String code;
    public String ERPCode;
    public String ID;
    public String catalog;
    public String moduleCode;
    public String curtainModuleCode;
    public String curtainMainClothCode;
    public String URL;
    public float PositionX;
    public float PositionY;
    public float PositionZ;
    public float OffGround;
    public float Rotation;
    public float Length;
    public float Width;
    public float Height;
    public String MeshName;
    public String LinkWallID;
    public String LinkWall2ID;
    public int Mirror;
    public int CeilingLayerHeight;
    public int CeilingLightDeep;
    public int CeilingTipHeight;
    public int BuildCeilingLight;
    public int CeilingLightColor;
    public int CeilingLightWattage;
    public String LinkedFloorID;
    public int PickCornerPointX;
    public int PickCornerPointY;
    public int PickCornerPointZ;
    public int PickDir1X;
    public int PickDir1Y;
    public int PickDir1Z;
    public int PickDir2X;
    public int PickDir2Y;
    public int PickDir2Z;
    public int ParamLength;
    public int ParamLength2;
    public String WindowName;
    public int WindowLine0X;
    public int WindowLine0Y;
    public int WindowLine0Z;
    public int WindowLine1X;
    public int WindowLine1Y;
    public int WindowLine1Z;
    public int WindowLine2X;
    public int WindowLine2Y;
    public int WindowLine2Z;
    public int WindowOffGround;
    public int WindowHeight;
    public int WindowFloatDistance;
    public int WindowFloatSide;
    public int WindowWallThickness;
    public int BeamThickness;
    public int BeamRotation;
    public int HadCurveRailingPoints;
    public int BuildCurtainLength;
    public int BuildCurtainPositionX;
    public int BuildCurtainPositionY;
    public int BuildCurtainPositionZ;
    public int BuildCurtainOffGround;
    public int VirtualLightColor;
    public int VirtualLightWattage;
    public int OffDistance;
    public String LinkedThresholdMaterialURL;
    public int LightRatio;
    public int LightColor;
    public String LightDoubleSide;
    public String comonID;
    public String IsPolyMode;

    public FurnitureData() {
        super();
    }

    protected FurnitureData(Parcel in) {
        name = in.readString();
        code = in.readString();
        ERPCode = in.readString();
        ID = in.readString();
        catalog = in.readString();
        moduleCode = in.readString();
        curtainModuleCode = in.readString();
        curtainMainClothCode = in.readString();
        URL = in.readString();
        PositionX = in.readFloat();
        PositionY = in.readFloat();
        PositionZ = in.readFloat();
        OffGround = in.readFloat();
        Rotation = in.readFloat();
        Length = in.readFloat();
        Width = in.readFloat();
        Height = in.readFloat();
        MeshName = in.readString();
        LinkWallID = in.readString();
        LinkWall2ID = in.readString();
        Mirror = in.readInt();
        CeilingLayerHeight = in.readInt();
        CeilingLightDeep = in.readInt();
        CeilingTipHeight = in.readInt();
        BuildCeilingLight = in.readInt();
        CeilingLightColor = in.readInt();
        CeilingLightWattage = in.readInt();
        LinkedFloorID = in.readString();
        PickCornerPointX = in.readInt();
        PickCornerPointY = in.readInt();
        PickCornerPointZ = in.readInt();
        PickDir1X = in.readInt();
        PickDir1Y = in.readInt();
        PickDir1Z = in.readInt();
        PickDir2X = in.readInt();
        PickDir2Y = in.readInt();
        PickDir2Z = in.readInt();
        ParamLength = in.readInt();
        ParamLength2 = in.readInt();
        WindowName = in.readString();
        WindowLine0X = in.readInt();
        WindowLine0Y = in.readInt();
        WindowLine0Z = in.readInt();
        WindowLine1X = in.readInt();
        WindowLine1Y = in.readInt();
        WindowLine1Z = in.readInt();
        WindowLine2X = in.readInt();
        WindowLine2Y = in.readInt();
        WindowLine2Z = in.readInt();
        WindowOffGround = in.readInt();
        WindowHeight = in.readInt();
        WindowFloatDistance = in.readInt();
        WindowFloatSide = in.readInt();
        WindowWallThickness = in.readInt();
        BeamThickness = in.readInt();
        BeamRotation = in.readInt();
        HadCurveRailingPoints = in.readInt();
        BuildCurtainLength = in.readInt();
        BuildCurtainPositionX = in.readInt();
        BuildCurtainPositionY = in.readInt();
        BuildCurtainPositionZ = in.readInt();
        BuildCurtainOffGround = in.readInt();
        VirtualLightColor = in.readInt();
        VirtualLightWattage = in.readInt();
        OffDistance = in.readInt();
        LinkedThresholdMaterialURL = in.readString();
        LightRatio = in.readInt();
        LightColor = in.readInt();
        LightDoubleSide = in.readString();
        comonID = in.readString();
        IsPolyMode = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(code);
        dest.writeString(ERPCode);
        dest.writeString(ID);
        dest.writeString(catalog);
        dest.writeString(moduleCode);
        dest.writeString(curtainModuleCode);
        dest.writeString(curtainMainClothCode);
        dest.writeString(URL);
        dest.writeFloat(PositionX);
        dest.writeFloat(PositionY);
        dest.writeFloat(PositionZ);
        dest.writeFloat(OffGround);
        dest.writeFloat(Rotation);
        dest.writeFloat(Length);
        dest.writeFloat(Width);
        dest.writeFloat(Height);
        dest.writeString(MeshName);
        dest.writeString(LinkWallID);
        dest.writeString(LinkWall2ID);
        dest.writeInt(Mirror);
        dest.writeInt(CeilingLayerHeight);
        dest.writeInt(CeilingLightDeep);
        dest.writeInt(CeilingTipHeight);
        dest.writeInt(BuildCeilingLight);
        dest.writeInt(CeilingLightColor);
        dest.writeInt(CeilingLightWattage);
        dest.writeString(LinkedFloorID);
        dest.writeInt(PickCornerPointX);
        dest.writeInt(PickCornerPointY);
        dest.writeInt(PickCornerPointZ);
        dest.writeInt(PickDir1X);
        dest.writeInt(PickDir1Y);
        dest.writeInt(PickDir1Z);
        dest.writeInt(PickDir2X);
        dest.writeInt(PickDir2Y);
        dest.writeInt(PickDir2Z);
        dest.writeInt(ParamLength);
        dest.writeInt(ParamLength2);
        dest.writeString(WindowName);
        dest.writeInt(WindowLine0X);
        dest.writeInt(WindowLine0Y);
        dest.writeInt(WindowLine0Z);
        dest.writeInt(WindowLine1X);
        dest.writeInt(WindowLine1Y);
        dest.writeInt(WindowLine1Z);
        dest.writeInt(WindowLine2X);
        dest.writeInt(WindowLine2Y);
        dest.writeInt(WindowLine2Z);
        dest.writeInt(WindowOffGround);
        dest.writeInt(WindowHeight);
        dest.writeInt(WindowFloatDistance);
        dest.writeInt(WindowFloatSide);
        dest.writeInt(WindowWallThickness);
        dest.writeInt(BeamThickness);
        dest.writeInt(BeamRotation);
        dest.writeInt(HadCurveRailingPoints);
        dest.writeInt(BuildCurtainLength);
        dest.writeInt(BuildCurtainPositionX);
        dest.writeInt(BuildCurtainPositionY);
        dest.writeInt(BuildCurtainPositionZ);
        dest.writeInt(BuildCurtainOffGround);
        dest.writeInt(VirtualLightColor);
        dest.writeInt(VirtualLightWattage);
        dest.writeInt(OffDistance);
        dest.writeString(LinkedThresholdMaterialURL);
        dest.writeInt(LightRatio);
        dest.writeInt(LightColor);
        dest.writeString(LightDoubleSide);
        dest.writeString(comonID);
        dest.writeString(IsPolyMode);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FurnitureData> CREATOR = new Creator<FurnitureData>() {
        @Override
        public FurnitureData createFromParcel(Parcel in) {
            return new FurnitureData(in);
        }

        @Override
        public FurnitureData[] newArray(int size) {
            return new FurnitureData[size];
        }
    };

    public String toXml() {
        String v = "<FurnitureData Name=\"" + name + "\" Code=\"" + code + "\" ERPCode=\"" + ERPCode + "\" ID=\"" + ID + "\" " +
                "Catalog=\"" + catalog + "\" ModuleCode=\"" + moduleCode + "\" CurtainModuleCode=\"" + curtainModuleCode + "\" CurtainMainClothCode=\"" + curtainMainClothCode + "\" URL=\"" + URL + "\" \n" +
                "PositionX=\"" + PositionX + "\" PositionY=\"" + PositionY + "\" PositionZ=\"" + PositionZ + "\" OffGround=\"" + OffGround + "\" Rotation=\"" + Rotation + "\" Length=\"" + Length + "\" Width=\"" + Width + "\" " +
                "Height=\"" + Height + "\" MeshName=\"" + MeshName + "\" LinkWallID=\"" + LinkWallID + "\" LinkWall2ID=\"" + LinkWall2ID + "\" Mirror=\"" + Mirror + "\" \n" +
                "CeilingLayerHeight=\"" + CeilingLayerHeight + "\" CeilingLightDeep=\"" + CeilingLightDeep + "\" CeilingTipHeight=\"" + CeilingTipHeight + "\" BuildCeilingLight=\"" + BuildCeilingLight + "\" CeilingLightColor=\"" + CeilingLightColor + "\"" +
                "CeilingLightWattage=\"" + CeilingLightWattage + "\" LinkedFloorID=\"" + LinkedFloorID + "\" PickCornerPointX=\"" + PickCornerPointX + "\" PickCornerPointY=\"" + PickCornerPointY + "\"\n" +
                "PickCornerPointZ=\"" + PickCornerPointZ + "\" PickDir1X=\"" + PickDir1X + "\" PickDir1Y=\"" + PickDir1Y + "\" PickDir1Z=\"" + PickDir1Z + "\" PickDir2X=\"" + PickDir2X + "\" PickDir2Y=\"" + PickDir2Y + "\" PickDir2Z=\"" + PickDir2Z + "\"" +
                "WindowName=\"" + WindowName + "\" WindowLine0X=\"" + WindowLine0X + "\" WindowLine0Y=\"" + WindowLine0Y + "\" WindowLine0Z=\"" + WindowLine0Z + "\" WindowLine1X=\"" + WindowLine1X + "\" \n" +
                "WindowLine1Y=\"" + WindowLine1Y + "\" WindowLine1Z=\"" + WindowLine1Z + "\" WindowLine2X=\"" + WindowLine2X + "\" WindowLine2Y=\"" + WindowLine2X + "\" WindowLine2Z=\"" + WindowLine2Z + "\" WindowOffGround=\"" + WindowOffGround + "\" WindowHeight=\"" + WindowHeight + "\" " +
                "WindowFloatDistance=\"" + WindowFloatDistance + "\" WindowFloatSide=\"" + WindowFloatSide + "\" WindowWallThickness=\"" + WindowWallThickness + "\" BeamThickness=\"" + BeamThickness + "\" BeamRotation=\"" + BeamRotation + "\" \n" +
                "HadCurveRailingPoints=\"" + HadCurveRailingPoints + "\" BuildCurtainLength=\"" + BuildCurtainLength + "\" BuildCurtainPositionX=\"" + BuildCurtainPositionX + "\" BuildCurtainPositionY=\"" + BuildCurtainPositionY + "\" BuildCurtainPositionZ=\"" + BuildCurtainPositionZ + "\" " +
                "BuildCurtainOffGround=\"" + BuildCurtainOffGround + "\" VirtualLightColor=\"" + VirtualLightColor + "\" VirtualLightWattage=\"" + VirtualLightColor + "\" OffDistance=\"" + OffDistance + "\" \n" +
                "LinkedThresholdMaterialURL=\"" + LinkedThresholdMaterialURL + "\" LightRatio=\"" + LightRatio + "\" LightColor=\"" + LightColor + "\" LightDoubleSide=\"" + LightDoubleSide + "\" comonID=\"" + comonID + "\" IsPolyMode=\"" + IsPolyMode + "\">";
        v += "\n<RoundPoint num=\"0\"/>\n" +
                "    <RailingPoint num=\"0\"/>\n" +
                "    <RailingControlPoint num=\"0\"/>\n" +
                "    <Material num=\"0\"/>\n" +
                "    <SwitchLinksData>\n" +
                "      <linkPanelID num=\"0\"/>\n" +
                "    </SwitchLinksData>";
        v += "\n</FurnitureData>";
        return v;
    }

    @Override
    public String toString() {
        return toXml();
    }
}
