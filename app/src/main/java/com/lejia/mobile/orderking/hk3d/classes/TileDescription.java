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

    protected TileDescription(Parcel in) {
        styleType = in.readInt();
        materialList = in.createTypedArrayList(Tile.CREATOR);
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
        if (materialList == null || materialList.size() == 0)
            return;
        this.onTileDescriptionLoadListener = onTileDescriptionLoadListener;
        this.size = materialList.size();
        this.count = 0;
        for (Tile tile : materialList) {
            tile.getBitmap(onTileBitmapListener);
        }
    }

    /**
     * 根据材质编码获取位图
     *
     * @param materialCode 编码
     * @return 对应位图
     */
    public Bitmap getTileBitmap(String materialCode) {
        if (TextUtils.isTextEmpity(materialCode) || materialList == null || materialList.size() == 0)
            return null;
        for (Tile tile : materialList) {
            if (tile.materialCode.equals(materialCode)) {
                return tile.bitmap;
            }
        }
        return null;
    }

    /**
     * 获取瓷砖的宽度
     *
     * @param position
     */
    public int getTileWidth(int position) {
        if (materialList == null || materialList.size() == 0 || position < 0 || position >= materialList.size())
            return 0;
        return materialList.get(position).materialWidth / 10;
    }

    /**
     * 获取瓷砖的高度
     *
     * @param position
     */
    public int getTileHeight(int position) {
        if (materialList == null || materialList.size() == 0 || position < 0 || position >= materialList.size())
            return 0;
        return materialList.get(position).materialHeight / 10;
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

}
