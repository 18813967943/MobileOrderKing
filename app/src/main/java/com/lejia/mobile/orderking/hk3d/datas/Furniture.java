package com.lejia.mobile.orderking.hk3d.datas;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/8/20 15:11
 * TODO: 家具数据对象
 */
public class Furniture implements Parcelable {

    public int materialID; // 模型数据库编号
    public String materialGID; // 材质唯一身份证
    public int modelMaterialRoomTypeID; // 模型所处空间类型
    public int modelMaterialTypeID; // 模型大类类型
    public String materialCode; // 模型编码
    public String materialName; // 模型名称
    public String preview; // 预览图链接
    public String topView; // 顶视图链接
    public int xLong; // 长
    public int width; // 宽
    public int height; // 高
    public int groundHeight; // 离地高
    public String renderingPath; // 对应渲染服务器路径
    public String materialURL; // 模型源文件路径
    public String materialSubsetsJsonURL; // 模型所有子件数据文件路径
    public int adsorptionType; // 吸附类型
    public String creatorID; // 创建人编号
    public String enterpriseID; // 所属企业编号
    public boolean isPublic; // 是否公用模型
    public boolean isEnable; // 是否开启使用
    public String createTime; // 创建时间

    /**
     * 子件数据对象
     */
    private Subset subset;

    /**
     * 所有同模型矩阵及位置数据对象列表(共享同一个模型数据)
     */
    private ArrayList<FurnitureMatrixs> furnitureMatrixsList;

    public Furniture() {
        super();
        furnitureMatrixsList = new ArrayList<>();
    }

    protected Furniture(Parcel in) {
        materialID = in.readInt();
        materialGID = in.readString();
        modelMaterialRoomTypeID = in.readInt();
        modelMaterialTypeID = in.readInt();
        materialCode = in.readString();
        materialName = in.readString();
        preview = in.readString();
        topView = in.readString();
        xLong = in.readInt();
        width = in.readInt();
        height = in.readInt();
        groundHeight = in.readInt();
        renderingPath = in.readString();
        materialURL = in.readString();
        materialSubsetsJsonURL = in.readString();
        adsorptionType = in.readInt();
        creatorID = in.readString();
        enterpriseID = in.readString();
        isPublic = in.readByte() != 0;
        isEnable = in.readByte() != 0;
        createTime = in.readString();
        furnitureMatrixsList = in.createTypedArrayList(FurnitureMatrixs.CREATOR);
    }

    /**
     * 加载子件数据
     */
    public void loadSubsets() {
        if (subset != null)
            return;
        subset = new Subset(materialCode, materialSubsetsJsonURL);
    }

    /**
     * 获取模型子件数据对象
     */
    public Subset getSubset() {
        return subset;
    }

    /**
     * 存入模型矩阵数据对象
     *
     * @param furnitureMatrixs
     */
    public void put(FurnitureMatrixs furnitureMatrixs) {
        if (furnitureMatrixs == null)
            return;
        furnitureMatrixsList.add(furnitureMatrixs);
    }

    /**
     * 存入模型矩阵数据对象
     *
     * @param furnitureMatrixsList
     */
    public void putAll(ArrayList<FurnitureMatrixs> furnitureMatrixsList) {
        if (furnitureMatrixsList == null || furnitureMatrixsList.size() == 0)
            return;
        for (FurnitureMatrixs furnitureMatrixs : furnitureMatrixsList) {
            put(furnitureMatrixs);
        }
    }

    /**
     * 获取当前此模型所有使用对象列表
     */
    public ArrayList<FurnitureMatrixs> getFurnitureMatrixsList() {
        return furnitureMatrixsList;
    }

    public int getMaterialID() {
        return materialID;
    }

    public void setMaterialID(int materialID) {
        this.materialID = materialID;
    }

    public String getMaterialGID() {
        return materialGID;
    }

    public void setMaterialGID(String materialGID) {
        this.materialGID = materialGID;
    }

    public int getModelMaterialRoomTypeID() {
        return modelMaterialRoomTypeID;
    }

    public void setModelMaterialRoomTypeID(int modelMaterialRoomTypeID) {
        this.modelMaterialRoomTypeID = modelMaterialRoomTypeID;
    }

    public int getModelMaterialTypeID() {
        return modelMaterialTypeID;
    }

    public void setModelMaterialTypeID(int modelMaterialTypeID) {
        this.modelMaterialTypeID = modelMaterialTypeID;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getTopView() {
        return topView;
    }

    public void setTopView(String topView) {
        this.topView = topView;
    }

    public int getxLong() {
        return xLong;
    }

    public void setxLong(int xLong) {
        this.xLong = xLong;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getGroundHeight() {
        return groundHeight;
    }

    public void setGroundHeight(int groundHeight) {
        this.groundHeight = groundHeight;
    }

    public String getRenderingPath() {
        return renderingPath;
    }

    public void setRenderingPath(String renderingPath) {
        this.renderingPath = renderingPath;
    }

    public String getMaterialURL() {
        return materialURL;
    }

    public void setMaterialURL(String materialURL) {
        this.materialURL = materialURL;
    }

    public String getMaterialSubsetsJsonURL() {
        return materialSubsetsJsonURL;
    }

    public void setMaterialSubsetsJsonURL(String materialSubsetsJsonURL) {
        this.materialSubsetsJsonURL = materialSubsetsJsonURL;
    }

    public int getAdsorptionType() {
        return adsorptionType;
    }

    public void setAdsorptionType(int adsorptionType) {
        this.adsorptionType = adsorptionType;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public String getEnterpriseID() {
        return enterpriseID;
    }

    public void setEnterpriseID(String enterpriseID) {
        this.enterpriseID = enterpriseID;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(materialID);
        dest.writeString(materialGID);
        dest.writeInt(modelMaterialRoomTypeID);
        dest.writeInt(modelMaterialTypeID);
        dest.writeString(materialCode);
        dest.writeString(materialName);
        dest.writeString(preview);
        dest.writeString(topView);
        dest.writeInt(xLong);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeInt(groundHeight);
        dest.writeString(renderingPath);
        dest.writeString(materialURL);
        dest.writeString(materialSubsetsJsonURL);
        dest.writeInt(adsorptionType);
        dest.writeString(creatorID);
        dest.writeString(enterpriseID);
        dest.writeByte((byte) (isPublic ? 1 : 0));
        dest.writeByte((byte) (isEnable ? 1 : 0));
        dest.writeString(createTime);
        dest.writeTypedList(furnitureMatrixsList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Furniture> CREATOR = new Creator<Furniture>() {
        @Override
        public Furniture createFromParcel(Parcel in) {
            return new Furniture(in);
        }

        @Override
        public Furniture[] newArray(int size) {
            return new Furniture[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof Furniture)) {
            return false;
        }
        Furniture furniture = (Furniture) obj;
        return furniture.materialCode.equals(materialCode);
    }

    @Override
    public String toString() {
        return materialCode + "," + xLong + "," + width + "," + height + "," + materialSubsetsJsonURL;
    }

}
