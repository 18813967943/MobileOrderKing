package com.lejia.mobile.orderking.hk3d.classes;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.lejia.mobile.orderking.utils.TextUtils;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/7/23 14:15
 * TODO: 每层铺砖材质对象信息对象
 */
public class TileDescription implements Parcelable {

    public int id; // 在数据库中的编号

    /**
     * 铺砖类型，普通砖1，波打线2，切割四块为3，转角切2块为4,等等
     */
    public int styleType;

    /**
     * 预览图
     */
    public String previewImg;

    /**
     * 当前瓷砖信息存储列表
     */
    public ArrayList<Tile> materialList;

    /**
     * 样式砖，多层
     */
    public ArrayList<ArrayList<Tile>> styliesMaterialList;

    private int size; // 本层材质数量
    private int count; // 加载计数
    private OnTileDescriptionLoadListener onTileDescriptionLoadListener; // 回调接口

    public TileDescription() {
        super();
    }

    public TileDescription(int type, ArrayList<Tile> tilesList) {
        this.styleType = type;
        this.materialList = tilesList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStyleType() {
        return styleType;
    }

    public void setStyleType(int styleType) {
        this.styleType = styleType;
    }

    public String getPreviewImg() {
        return previewImg;
    }

    public void setPreviewImg(String previewImg) {
        this.previewImg = previewImg;
    }

    public ArrayList<Tile> getMaterialList() {
        return materialList;
    }

    public void setMaterialList(ArrayList<Tile> materialList) {
        this.materialList = materialList;
    }

    public ArrayList<ArrayList<Tile>> getStyliesMaterialList() {
        return styliesMaterialList;
    }

    public void setStyliesMaterialList(ArrayList<ArrayList<Tile>> styliesMaterialList) {
        this.styliesMaterialList = styliesMaterialList;
    }

    protected TileDescription(Parcel in) {
        styleType = in.readInt();
        materialList = in.createTypedArrayList(Tile.CREATOR);
        int styleSize = in.readInt();
        if (styleSize > 0) {
            styliesMaterialList = new ArrayList<>();
            for (int i = 0; i < styleSize; i++) {
                ArrayList<Tile> tilesList = in.createTypedArrayList(Tile.CREATOR);
                styliesMaterialList.add(tilesList);
            }
        }
    }

    /**
     * 此层有用的瓷砖数量
     *
     * @return
     */
    public int size() {
        if (materialList == null)
            return 0;
        return materialList.size();
    }

    /**
     * 加载所有贴图
     */
    public void loadBitmaps(OnTileDescriptionLoadListener onTileDescriptionLoadListener) {
        // 样式、方案铺砖
        if (styliesMaterialList != null && styliesMaterialList.size() > 0) {
            this.onTileDescriptionLoadListener = onTileDescriptionLoadListener;
            ArrayList<Tile> allTilesArrayList = new ArrayList<>();
            for (int i = 0; i < styliesMaterialList.size(); i++) {
                ArrayList<Tile> tileArrayList = styliesMaterialList.get(i);
                if (tileArrayList != null && tileArrayList.size() > 0) {
                    this.size += tileArrayList.size();
                    allTilesArrayList.addAll(tileArrayList);
                }
            }
            this.count = 0;
            for (Tile tile : allTilesArrayList) {
                tile.getBitmap(onTileBitmapListener);
            }
        }
        // 常态铺砖
        else {
            if (materialList == null || materialList.size() == 0)
                return;
            this.onTileDescriptionLoadListener = onTileDescriptionLoadListener;
            this.size = materialList.size();
            this.count = 0;
            for (Tile tile : materialList) {
                tile.getBitmap(onTileBitmapListener);
            }
        }
    }

    /**
     * 根据材质编码获取位图
     *
     * @param materialCode 编码
     * @return 对应位图
     */
    public Bitmap getTileBitmap(String materialCode) {
        if (TextUtils.isTextEmpty(materialCode))
            return null;
        // 波打线、样式
        if (styliesMaterialList != null && styliesMaterialList.size() > 0) {
            ArrayList<Tile> allTilesArrayList = new ArrayList<>();
            for (int i = 0; i < styliesMaterialList.size(); i++) {
                ArrayList<Tile> tileArrayList = styliesMaterialList.get(i);
                if (tileArrayList != null && tileArrayList.size() > 0) {
                    allTilesArrayList.addAll(tileArrayList);
                }
            }
            for (Tile tile : allTilesArrayList) {
                if (tile.materialCode.equals(materialCode)) {
                    return tile.bitmap;
                }
            }
            return null;
        }
        // 常态砖
        else {
            if (materialList == null || materialList.size() == 0)
                return null;
            for (Tile tile : materialList) {
                if (tile.materialCode.equals(materialCode)) {
                    return tile.bitmap;
                }
            }
            return null;
        }
    }

    /**
     * 获取瓷砖的宽度
     *
     * @param position
     */
    public int getTileWidth(int position) {
        if (styliesMaterialList != null && styliesMaterialList.size() > 0) {
            return styliesMaterialList.get(position).get(0).materialWidth / 10;
        } else {
            if (materialList == null || materialList.size() == 0 || position < 0 || position >= materialList.size())
                return 0;
            return materialList.get(position).materialWidth / 10;
        }
    }

    /**
     * 获取瓷砖的高度
     *
     * @param position
     */
    public int getTileHeight(int position) {
        if (styliesMaterialList != null && styliesMaterialList.size() > 0) {
            return styliesMaterialList.get(position).get(0).materialHeight / 10;
        } else {
            if (materialList == null || materialList.size() == 0 || position < 0 || position >= materialList.size())
                return 0;
            return materialList.get(position).materialHeight / 10;
        }
    }

    /**
     * 获取材质编码
     *
     * @param cell
     * @param position
     */
    public String getMaterialCode(int cell, int position) {
        if (styliesMaterialList != null && styliesMaterialList.size() > 0) {
            return styliesMaterialList.get(cell).get(position).materialCode;
        } else {
            if (materialList == null || materialList.size() == 0 || position < 0 || position >= materialList.size())
                return null;
            return materialList.get(position).materialCode;
        }
    }

    /**
     * 材质贴图加载完毕回调监听接口
     */
    private Tile.OnTileBitmapListener onTileBitmapListener = new Tile.OnTileBitmapListener() {
        @Override
        public void onTileBitmapThenDoSomething(Bitmap bitmap) {
            count++;
            // 加载完成
            if (count == size) {
                if (onTileDescriptionLoadListener != null)
                    onTileDescriptionLoadListener.onLoaded();
            }
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(styleType);
        dest.writeTypedList(materialList);
        if (styliesMaterialList == null)
            dest.writeInt(0);
        else {
            dest.writeInt(styliesMaterialList.size());
            for (int i = 0; i < styliesMaterialList.size(); i++) {
                dest.writeTypedList(styliesMaterialList.get(i));
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TileDescription> CREATOR = new Creator<TileDescription>() {
        @Override
        public TileDescription createFromParcel(Parcel in) {
            return new TileDescription(in);
        }

        @Override
        public TileDescription[] newArray(int size) {
            return new TileDescription[size];
        }
    };

    /**
     * Author by HEKE
     *
     * @time 2018/7/23 14:25
     * TODO: 每层瓷砖加载完毕监听接口
     */
    public interface OnTileDescriptionLoadListener {
        void onLoaded();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof TileDescription)) {
            return false;
        }
        TileDescription other = (TileDescription) obj;
        if (other.styleType == styleType && other.previewImg.equals(previewImg) && other.size() == size()) {
            return true;
        }
        return super.equals(obj);
    }

    /**
     * 判断两组列表是否相同
     *
     * @param list1
     * @param list2
     * @return true表示选择铺砖方案相同
     */
    public static boolean isTileDescriptionListEquals(ArrayList<TileDescription> list1, ArrayList<TileDescription> list2) {
        if (list1 == null || list2 == null || list1.size() == 0 || list2.size() == 0)
            return false;
        if (list1.size() != list2.size())
            return false;
        boolean equals = true;
        for (int i = 0; i < list1.size(); i++) {
            TileDescription t1 = list1.get(i);
            TileDescription t2 = list2.get(i);
            equals = equals && t1.equals(t2);
            if (!equals)
                break;
        }
        return equals;
    }

}
