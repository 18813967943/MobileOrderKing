package com.lejia.mobile.orderking.hk3d.classes;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/7/23 14:15
 * TODO: 每层铺砖材质对象信息对象
 */
public class TileDescription implements Parcelable {

    /**
     * 铺砖类型，普通砖1，波打线2，切割四块为3，转角切2块为4,等等
     */
    public int type;

    /**
     * 当前瓷砖信息存储列表
     */
    public ArrayList<Tile> tilesList;

    private int size; // 本层材质数量
    private int count; // 加载计数
    private OnTileDescriptionLoadListener onTileDescriptionLoadListener; // 回调接口

    public TileDescription() {
        super();
    }

    public TileDescription(int type, ArrayList<Tile> tilesList) {
        this.type = type;
        this.tilesList = tilesList;
    }

    protected TileDescription(Parcel in) {
        type = in.readInt();
        tilesList = in.createTypedArrayList(Tile.CREATOR);
    }

    /**
     * 加载所有贴图
     */
    public void loadBitmaps(OnTileDescriptionLoadListener onTileDescriptionLoadListener) {
        if (tilesList == null || tilesList.size() == 0)
            return;
        this.onTileDescriptionLoadListener = onTileDescriptionLoadListener;
        this.size = tilesList.size();
        this.count = 0;
        for (Tile tile : tilesList) {
            tile.getBitmap(onTileBitmapListener);
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
        dest.writeInt(type);
        dest.writeTypedList(tilesList);
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
